/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.compapp.projects.jbi.ui.customizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.DialogDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class JbiJarCustomizer extends javax.swing.JPanel {
    
    private Component currentCustomizer;

    private GridBagConstraints fillConstraints;
    
    private JbiProjectProperties webProperties;

    private DialogDescriptor dialogDescriptor;    
    
    /** Creates new form WebCustomizer */
    public JbiJarCustomizer(JbiProjectProperties webProperties) {
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(JbiJarCustomizer.class, "ACS_Customize_A11YDesc")); // NOI18N

        this.webProperties = webProperties;
        //this.wm = wm;
        
        fillConstraints = new GridBagConstraints();
        fillConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        fillConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        fillConstraints.fill = java.awt.GridBagConstraints.BOTH;
        fillConstraints.weightx = 1.0;
        fillConstraints.weighty = 1.0;
        
        categoryPanel.add( new CategoryView( createRootNode(webProperties) ), fillConstraints );       
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        categoryPanel = new javax.swing.JPanel();
        customizerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(750, 450));
        categoryPanel.setLayout(new java.awt.GridBagLayout());

        categoryPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        categoryPanel.setMinimumSize(new java.awt.Dimension(220, 4));
        categoryPanel.setPreferredSize(new java.awt.Dimension(220, 4));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        add(categoryPanel, gridBagConstraints);

        customizerPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 8);
        add(customizerPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JPanel customizerPanel;
    // End of variables declaration//GEN-END:variables
    
    // Private innerclasses ----------------------------------------------------

    private class CategoryView extends JPanel implements ExplorerManager.Provider {
        
        private ExplorerManager manager;
        private BeanTreeView btv;
        
        CategoryView( Node rootNode ) {
        
            // See #36315
            manager = new ExplorerManager();

            setLayout( new BorderLayout() );
            
            Dimension size = new Dimension( 220, 4 );
            btv = new BeanTreeView();    // Add the BeanTreeView
            btv.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
            btv.setPopupAllowed( false );
            btv.setRootVisible( false );
            btv.setDefaultActionAllowed( false );            
            btv.setMinimumSize( size );
            btv.setPreferredSize( size );
            btv.setMaximumSize( size );
            this.add( btv, BorderLayout.CENTER );                        
            manager.setRootContext( rootNode );
            manager.addPropertyChangeListener( new ManagerChangeListener() );
            selectFirstNode();
            btv.expandAll();
                   
            btv.getAccessibleContext().setAccessibleName(NbBundle.getMessage(JbiJarCustomizer.class, "ACS_CustomizeTree_A11YName")); // NOI18N
            btv.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(JbiJarCustomizer.class, "ACS_CustomizeTree_A11YDesc")); // NOI18N
        }
        
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        
        public void addNotify() {
            super.addNotify();
            btv.expandAll();
        }
        
        private void selectFirstNode() {
            
            Children ch = manager.getRootContext().getChildren();
            if ( ch != null ) {
                Node nodes[] = ch.getNodes();
                
                if ( nodes != null && nodes.length > 0 ) {
                    try {                    
                        manager.setSelectedNodes( new Node[] { nodes[0] } );
                    }
                    catch ( PropertyVetoException e ) {
                        // No node will be selected
                    }
                }
            }
            
        }
        
        
        /** Listens to selection change and shows the customizers as
         *  panels
         */
        
        private class ManagerChangeListener implements PropertyChangeListener {

            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getSource() != manager) {
                    return;
                }

                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node nodes[] = manager.getSelectedNodes(); 
                    if ( nodes == null || nodes.length <= 0 ) {
                        return;
                    }
                    Node node = nodes[0];

                    if ( currentCustomizer != null ) {
                        customizerPanel.remove( currentCustomizer );
                    }
                    if ( node.hasCustomizer() ) {
                        currentCustomizer = node.getCustomizer();
                        
                        if ( currentCustomizer instanceof Panel ) {
                            ((Panel)currentCustomizer).initValues();
                        }
                        
                        /*
                        if ( currentCustomizer instanceof javax.swing.JComponent ) {
                            ((javax.swing.JComponent)currentCustomizer).setPreferredSize( new java.awt.Dimension( 600, 0 ) );
                        }
                        */
                        customizerPanel.add( currentCustomizer, fillConstraints );
                        customizerPanel.validate();
                        customizerPanel.repaint();
                        if (JbiJarCustomizer.this.dialogDescriptor != null ) {
                            JbiJarCustomizer.this.dialogDescriptor.setHelpCtx(JbiJarCustomizer.this.getHelpCtx());
                        }                        
                    }
                    else {
                        currentCustomizer = null;
                        if (JbiJarCustomizer.this.dialogDescriptor != null ) {
                            JbiJarCustomizer.this.dialogDescriptor.setHelpCtx(null);
                        }                        
                    }

                    return;
                }
            }
        }
    }
             
    // Private methods ---------------------------------------------------------
    
    private static Node createRootNode(JbiProjectProperties webProperties) {
        String ICON_FOLDER = "org/netbeans/modules/compapp/projects/jbi/ui/resources/";
        ResourceBundle bundle = NbBundle.getBundle( JbiJarCustomizer.class );
        
        
        ConfigurationDescription buildDescriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "Build",
                bundle.getString( "LBL_Config_Jar" ), // NOI18N
                ICON_FOLDER + "defaultCategory", //  "build", // NOI18N
                new CustomizerJarContent(webProperties),
                null ),
            /*
            new ConfigurationDescription(
                "Build",
                bundle.getString( "LBL_Config_Build" ), // NOI18N
                ICON_FOLDER + "build", // NOI18N
                new CustomizerCompile(jbiProperties),
                null ),
            new ConfigurationDescription(
                "Jar",
                bundle.getString( "LBL_Config_Jar" ), // NOI18N
                ICON_FOLDER + "jar", // NOI18N
                new CustomizerWar(jbiProperties),
                null ),
            */
        };
        //=======Start of JBI ==============================================//
        ConfigurationDescription runDescriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "Run",
                bundle.getString( "LBL_Config_Run" ), // NOI18N
                ICON_FOLDER + "defaultCategory", // "run", // NOI18N
                new CustomizerRun(webProperties),
                null ),
        };
        //=======End of JBI ================================================//

        ConfigurationDescription descriptions[] = new ConfigurationDescription[] {
            new ConfigurationDescription(
                "General",
                bundle.getString( "LBL_Config_General" ), // NOI18N
                ICON_FOLDER + "defaultCategory", // "jbiProjectIcon", // NOI18N
                new CustomizerGeneral(webProperties),
                null ),
            new ConfigurationDescription(
                "BuildCategoty",
                "Build",
                ICON_FOLDER + "defaultCategory", // "general", // NOI18N
                createEmptyLabel( null ),
                buildDescriptions ),
            //=======Start of JBI ==========================================//
            new ConfigurationDescription(
                "RunCategoty",
                "Run",
                ICON_FOLDER + "defaultCategory", // "run", // NOI18N
                createEmptyLabel( null ),
                runDescriptions )
            //=======End of JBI ============================================//
        };

        ConfigurationDescription rootDescription = new ConfigurationDescription(
        "InvisibleRoot", "InvisibleRoot", null, null, descriptions );  // NOI18N

        return new ConfigurationNode( rootDescription );


    }

    // Private meyhods ---------------------------------------------------------

    // XXX Remove when all panels have some options

    private static javax.swing.JLabel createEmptyLabel( String text ) {

        JLabel label;
        if ( text == null ) {
            label = new JLabel();
        }
        else {
            label = new JLabel( text );
            label.setHorizontalAlignment( JLabel.CENTER );
        }

        return label;
    }

    // Private innerclasses ----------------------------------------------------

    /** Class describing the configuration node. Prototype of the
     *  configuration node.
     */
    private static class ConfigurationDescription {
        
        
        private String name;
        private String displayName;
        private String iconBase;
        private Component customizer;
        private ConfigurationDescription[] children;
        // XXX Add Node.Properties
        
        ConfigurationDescription( String name,
        String displayName,
        String iconBase,
        Component customizer,
        ConfigurationDescription[] children ) {
            
            this.name = name;
            this.displayName = displayName;
            this.iconBase = iconBase;
            this.customizer = customizer;
            this.children = children;
        }
        
    }
    
    
    /** Node to be used for configuration
     */
    private static class ConfigurationNode extends AbstractNode {
        
        private Component customizer;
        
        public ConfigurationNode( ConfigurationDescription description ) {
            super( description.children == null ? Children.LEAF : new ConfigurationChildren( description.children ) );
            setName( description.name );
            setDisplayName( description.displayName );
            if ( description.iconBase != null ) {
                setIconBase( description.iconBase );
            }
            this.customizer = description.customizer;
        }
        
        public boolean hasCustomizer() {
            return true;
        }
        
        public Component getCustomizer() {
            return customizer;
        }
        
    }
    
    /** Children used for configuration
     */
    private static class ConfigurationChildren extends Children.Keys {
        
        private Collection descriptions;
        
        public ConfigurationChildren( ConfigurationDescription[] descriptions ) {
            this.descriptions = Arrays.asList( descriptions );
        }
        
        // Children.Keys impl --------------------------------------------------
        
        public void addNotify() {
            setKeys( descriptions );
        }
        
        public void removeNotify() {
            setKeys( Collections.EMPTY_LIST );
        }
        
        protected Node[] createNodes( Object key ) {
            return new Node[] { new ConfigurationNode( (ConfigurationDescription)key ) };
        }
    }
    
    static interface Panel {
        
        public void initValues();
        
    }
    
    public HelpCtx getHelpCtx() {
        return currentCustomizer instanceof JLabel
                ? null : HelpCtx.findHelp(currentCustomizer);
    }
    
    public void setDialogDescriptor( DialogDescriptor dialogDescriptor ) {
        this.dialogDescriptor = dialogDescriptor;
        if (null != dialogDescriptor) {
            dialogDescriptor.setHelpCtx(getHelpCtx());
        }
    }    
}
