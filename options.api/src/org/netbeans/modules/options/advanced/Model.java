/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.options.advanced;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.options.OptionsPanelControllerAccessor;
import org.netbeans.modules.options.ui.TabbedPanelModel;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Jan Jancura
 */
public final class Model extends TabbedPanelModel {
    
    private Map<String,String> idToCategory = new HashMap<String,String>();
    private Map<String,AdvancedOption> categoryToOption = new LinkedHashMap<String,AdvancedOption>();
    private Map<String, JComponent> categoryToPanel = new HashMap<String, JComponent> ();
    private Map<String, OptionsPanelController> categoryToController = new HashMap<String, OptionsPanelController>();
    private Lookup masterLookup;
    private LookupListener lkpListener;
    private Result<AdvancedOption> lkpResult;
    private String subpath;
    private PropertyChangeListener propertyChangeListener;

    /**
     * @param subpath path to folder under OptionsDialog folder containing 
     * instances of AdvancedOption class. Path is composed from registration 
     * names divided by slash.
     */
    public Model(String subpath, LookupListener listener) {
        this.subpath = subpath;
        this.lkpListener = listener;
    }
    
    @Override
    public List<String> getCategories () {
        init ();
        List<String> l = new ArrayList<String>(categoryToOption.keySet ());
        // Sort Miscellaneous (aka Advanced) subcategories. Order of other categories
        // can be defined in layer by position attribute.
        if(OptionsDisplayer.ADVANCED.equals(subpath)) {
            Collections.sort(l, Collator.getInstance());
        }
        return l;
    }
    
    /** Returns list of IDs in this model. 
     * @return list of IDs in this model
     */
    public List<String> getIDs() {
        init();
        return new ArrayList<String>(idToCategory.keySet());
    }

    /** Returns ID in this model for given category.
     * @return ID in this model for given category or null if the category is not present in this model
     */
    public String getID(String category) {
        init();
	for(Entry<String, String> entrySet : idToCategory.entrySet()) {
	    if(entrySet.getValue().equals(category)) {
		return entrySet.getKey();
	    }
	}
        return null;
    }

    @Override
    public String getToolTip (String category) {
        AdvancedOption option = categoryToOption.get (category);
        return option.getTooltip ();
    }
    
    /** Returns display name for given categoryID.
     * @param categoryID ID of category as defined in layer xml
     * @return display name of given category
     */
    public String getDisplayName(String categoryID) {
        AdvancedOption option = categoryToOption.get(idToCategory.get(categoryID));
        if (option == null) {
            Logger.getLogger(Model.class.getName()).info("No category found for ID: " + categoryID); // NOI18N
            return "";
        }
        return option.getDisplayName();
    }

    /** Returns controller for given categoryID.
     * @param categoryID ID of category as defined in layer xml
     * @return controller of given category
     */
    public OptionsPanelController getController(String categoryID) {
        return categoryToController.get(getDisplayName(categoryID));
    }

    @Override
    public JComponent getPanel (String category) {
        init ();
        JComponent panel = categoryToPanel.get (category);        
        if (panel != null) return panel;
        AdvancedOption option = categoryToOption.get (category);
        OptionsPanelController controller = categoryToController.get(category);
        if (controller==null) {
            controller = new DelegatingController(option.create ());
            categoryToController.put (category, controller);
        }
        controller.addPropertyChangeListener(propertyChangeListener);
        panel = controller.getComponent (masterLookup);
        categoryToPanel.put (category, panel);
        Border b = panel.getBorder ();
        if (b != null)
            b = new CompoundBorder (
                new EmptyBorder (6, 16, 6, 6),
                b
            );
        else
            b = new EmptyBorder (6, 16, 6, 6);
        panel.setBorder (b);
        //panel.setBackground (Color.white);
        panel.setMaximumSize (panel.getPreferredSize ());
        return panel;
    }
    
    
    // implementation ..........................................................
    void update (String category) {
        OptionsPanelController controller = categoryToController.get(category);
        if (controller != null) {
            controller.update();
        }
    }
    
    void applyChanges () {
        Iterator<OptionsPanelController> it = categoryToController.values ().iterator ();
        while (it.hasNext ())
            it.next().applyChanges ();
    }
    
    void cancel () {
        Iterator<OptionsPanelController> it = categoryToController.values ().iterator ();
        while (it.hasNext ())
            it.next().cancel ();
    }
    
    boolean isValid () {
        for (OptionsPanelController controller : categoryToController.values()) {
            // if changed (#145569) and not valid
            if (!controller.isValid() && controller.isChanged()) {
                return false;
            }
        }
        return true;
    }
    
    boolean isChanged () {
        Iterator<OptionsPanelController> it = categoryToController.values ().iterator ();
        while (it.hasNext ())
            if (it.next().isChanged ())
                return true;
        return false;
    }

    void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListener = listener;
        for(OptionsPanelController controller : categoryToController.values()) {
            controller.addPropertyChangeListener(listener);
        }
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        for(OptionsPanelController controller : categoryToController.values()) {
            controller.removePropertyChangeListener(listener);
        }
    }

    Lookup getLookup () {
        List<Lookup> lookups = new ArrayList<Lookup> ();
        Iterator<OptionsPanelController> it = categoryToController.values ().iterator ();
        while (it.hasNext ())
            lookups.add (it.next ().getLookup ());
        return new ProxyLookup 
            (lookups.toArray (new Lookup [lookups.size ()]));
    }
    
    HelpCtx getHelpCtx (JComponent panel) {
        if (panel instanceof JScrollPane) {
            // #158755, #165240 - get panel from scroll pane if needed
            Component view = ((JScrollPane) panel).getViewport().getView();
            if (view instanceof JComponent) {
                panel = (JComponent) view;
            }
        }
        Iterator<String> it = categoryToPanel.keySet ().iterator ();
        while (it.hasNext ()) {
            String category = it.next ();
            if (panel == null || panel == categoryToPanel.get (category)) {
                OptionsPanelController controller = categoryToController.get (category);
                if (controller != null) {
                    return controller.getHelpCtx ();
                }
            }
        }
        return new HelpCtx ("netbeans.optionsDialog.advanced");
    }
    
    private boolean initialized = false;
    
    private void init () {
        if (initialized) return;
        initialized = true;
        
        String path = "OptionsDialog/"+subpath; // NOI18N
        Lookup lookup = Lookups.forPath(path);
        lkpResult = lookup.lookup(new Lookup.Template<AdvancedOption>(AdvancedOption.class));
        for(Item<AdvancedOption> item : lkpResult.allItems()) {
            // don't lookup in subfolders
            if(item.getId().substring(0, item.getId().lastIndexOf('/')).equals(path)) {  // NOI18N
                AdvancedOption option = item.getInstance();
                String displayName = option.getDisplayName();
                if (displayName != null) {
                    categoryToOption.put(option.getDisplayName(), option);
                    idToCategory.put(item.getId().substring(path.length()+1), item.getInstance().getDisplayName());
                } else {
                    assert false : "Display name not defined: " + item.toString();  //NOI18N
                }
            }
        }
        lkpResult.addLookupListener(lkpListener);
        lkpListener = null;
    }
    
    void setLoookup (Lookup masterLookup) {
        this.masterLookup = masterLookup;
    }
    
    private static final class DelegatingController extends OptionsPanelController {
        private OptionsPanelController delegate;
        private boolean isUpdated;
        private DelegatingController(OptionsPanelController delegate) {
            this.delegate = delegate;
        }
        @Override
        public void update() {
            if (!isUpdated) {
                isUpdated = true;
                delegate.update();
            }
        }

        @Override
        public void applyChanges() {
            isUpdated = false;
            delegate.applyChanges();
        }

        @Override
        public void cancel() {
            isUpdated = false;
            delegate.cancel();
        }

        @Override
        public boolean isValid() {
            return delegate.isValid();
        }

        @Override
        public boolean isChanged() {
            return delegate.isChanged();
        }

        @Override
        public JComponent getComponent(Lookup masterLookup) {
            return delegate.getComponent(masterLookup);
        }

        @Override
        public void setCurrentSubcategory(String subpath) {
            OptionsPanelControllerAccessor.getDefault().setCurrentSubcategory(delegate, subpath);
        }
        
        @Override
        public HelpCtx getHelpCtx() {
            return delegate.getHelpCtx();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            delegate.addPropertyChangeListener(l);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            delegate.removePropertyChangeListener(l);
        }

        @Override
        public void handleSuccessfulSearch(String searchText, List<String> matchedKeywords) {
            delegate.handleSuccessfulSearch(searchText, matchedKeywords);
        }
    }            
}


