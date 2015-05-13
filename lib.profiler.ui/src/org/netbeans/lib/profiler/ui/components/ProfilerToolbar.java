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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.lib.profiler.ui.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.lib.profiler.ui.swing.GenericToolbar;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.openide.util.Lookup;

/**
 *
 * @author Jiri Sedlacek
 */
public abstract class ProfilerToolbar {
    
    public static ProfilerToolbar create(boolean showSeparator) {
        Provider provider = Lookup.getDefault().lookup(Provider.class);
        return provider != null ? provider.create(showSeparator) :
                                  new Impl(showSeparator);
    }
    
    
    public abstract JComponent getComponent();
    
    
    public Component add(ProfilerToolbar toolbar) { return toolbar.getComponent(); }
    
    public Component add(ProfilerToolbar toolbar, int index) { return toolbar.getComponent(); }
    
    public void remove(ProfilerToolbar toolbar) {}
    
    
    public abstract Component add(Action action);
    
    public abstract Component add(Component component);
    
    public abstract Component add(Component component, int index);
    
    public abstract void addSeparator();
    
    public abstract void addSpace(int width);
    
    public abstract void addFiller();
    
    public abstract void remove(Component component);
    
    public abstract void remove(int index);
    
    public abstract int getComponentCount();
    
    
    protected ProfilerToolbar() {}
    
    
    public static abstract class Provider {
        
        public abstract ProfilerToolbar create(boolean showSeparator);
        
    }
    
    public static class Impl extends ProfilerToolbar {
        
        protected static int PREFERRED_HEIGHT = -1;
        
        protected final JComponent component;
        protected final JToolBar toolbar;
        
        protected Impl(boolean showSeparator) {
            toolbar = new GenericToolbar() {
                protected void addImpl(Component comp, Object constraints, int index) {
                    if (comp instanceof JButton)
                        UIUtils.fixButtonUI((JButton) comp);
                    super.addImpl(comp, constraints, index);
                }
                public Dimension getPreferredSize() {
                    Dimension dim = super.getPreferredSize();
                    if (PREFERRED_HEIGHT == -1) {
                        JToolBar tb = new GenericToolbar();
                        tb.setBorder(toolbar.getBorder());
                        tb.setBorderPainted(toolbar.isBorderPainted());
                        tb.setRollover(toolbar.isRollover());
                        tb.setFloatable(toolbar.isFloatable());
                        Icon icon = Icons.getIcon(GeneralIcons.SAVE);
                        tb.add(new JButton("Button", icon)); // NOI18N
                        tb.add(new JToggleButton("Button", icon)); // NOI18N
                        JComboBox c = new JComboBox();
                        c.setEditor(new BasicComboBoxEditor());
                        c.setRenderer(new BasicComboBoxRenderer());
                        tb.add(c);
                        tb.addSeparator();
                        PREFERRED_HEIGHT = tb.getPreferredSize().height;
                    }
                    dim.height = getParent() instanceof JToolBar ? 1 :
                                 Math.max(dim.height, PREFERRED_HEIGHT);
                    return dim;
                }
                public void doLayout() {
                    // #216443 - disabled/invisible/JLabel toolbar components
                    //           break left/right arrow focus traversal
                    for (Component component : getComponents())
                        component.setFocusable(isFocusableComponent(component));
                    super.doLayout();
                }
            };
            if (UIUtils.isGTKLookAndFeel() || UIUtils.isNimbusLookAndFeel())
                toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
            toolbar.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
            toolbar.setBorderPainted(false);
            toolbar.setRollover(true);
            toolbar.setFloatable(false);
            
            if (showSeparator) {
                component = new JPanel(new BorderLayout(0, 0));
                component.setOpaque(false);
                component.add(toolbar, BorderLayout.CENTER);
                component.add(UIUtils.createHorizontalLine(toolbar.getBackground()),
                        BorderLayout.SOUTH);
            } else {
                component = toolbar;
            }
        }
        
        protected boolean isFocusableComponent(Component component) {
            if (!component.isVisible()) return false;
//            if (!component.isEnabled()) return false;
            if (component instanceof JLabel) return false;
            if (component instanceof JPanel) return false;
            if (component instanceof JSeparator) return false;
            if (component instanceof JToolBar) return false;
            if (component instanceof Box.Filler) return false;
            return true;
        }
        
        @Override
        public JComponent getComponent() {
            return component;
        }
        
        @Override
        public Component add(ProfilerToolbar toolbar) {
            return add(toolbar, getComponentCount());
        }
    
        @Override
        public Component add(ProfilerToolbar toolbar, int index) {
            JToolBar implToolbar = ((Impl)toolbar).toolbar;
            implToolbar.setFocusTraversalPolicyProvider(true);
            implToolbar.setBorder(BorderFactory.createEmptyBorder());
            implToolbar.setOpaque(false);
            return add(implToolbar, index);
        }
        
        @Override
        public void remove(ProfilerToolbar toolbar) {
            remove(((Impl)toolbar).toolbar);
        }

        @Override
        public Component add(Action action) {
            Component c = toolbar.add(action);
            toolbar.repaint();
            return c;
        }

        @Override
        public Component add(Component component) {
            Component c = toolbar.add(component);
            toolbar.repaint();
            return c;
        }
        
        @Override
        public Component add(Component component, int index) {
            Component c = toolbar.add(component, index);
            toolbar.repaint();
            return c;
        }

        @Override
        public void addSeparator() {
            toolbar.addSeparator();
            toolbar.repaint();
        }
        
        @Override
        public void addSpace(int width) {
            toolbar.addSeparator(new Dimension(width, 0));
            toolbar.repaint();
        }
        
        @Override
        public void addFiller() {
            Dimension minDim = new Dimension(0, 0);
            Dimension maxDim = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
            toolbar.add(new Box.Filler(minDim, minDim, maxDim));
            toolbar.repaint();
        }
        
        @Override
        public void remove(Component component) {
            toolbar.remove(component);
            toolbar.repaint();
        }
        
        @Override
        public void remove(int index) {
            toolbar.remove(index);
            toolbar.repaint();
        }
        
        @Override
        public int getComponentCount() {
            return toolbar.getComponentCount();
        }
        
    }
    
}
