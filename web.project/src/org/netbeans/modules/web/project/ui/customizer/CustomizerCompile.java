/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.project.ui.customizer;

import org.openide.util.NbBundle;

public class CustomizerCompile extends javax.swing.JPanel implements WebCustomizer.Panel {

    private VisualPropertySupport vps;
    private VisualClasspathSupport vcs;
    
    /** Creates new form CustomizerCompile */
    public CustomizerCompile(WebProjectProperties webProperties) {
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerCompile.class, "ACS_CustomizeCompile_A11YDesc")); //NOI18N
        
        vps = new VisualPropertySupport(webProperties);
        vcs = new VisualClasspathSupport(
            webProperties.getProject(),
            jTableClasspath,
            jButtonAddJar,
            jButtonAddLibrary,
            jButtonAddProject,
            jButtonEdit,
            jButtonRemove,
            jButtonMoveUp,
            jButtonMoveDown);
    }

    public void initValues() {
        vps.register(jCheckBoxDebugInfo, WebProjectProperties.JAVAC_DEBUG);
        vps.register(jCheckBoxDeprecation, WebProjectProperties.JAVAC_DEPRECATION);
        vps.register(vcs, WebProjectProperties.JAVAC_CLASSPATH);
        vps.register(jCheckBoxCompileJSP, WebProjectProperties.COMPILE_JSPS);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jCheckBoxDebugInfo = new javax.swing.JCheckBox();
        jCheckBoxDeprecation = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabelClasspath = new javax.swing.JLabel();
        jScrollClasspath = new javax.swing.JScrollPane();
        jTableClasspath = new javax.swing.JTable();
        jButtonAddJar = new javax.swing.JButton();
        jButtonAddLibrary = new javax.swing.JButton();
        jButtonAddProject = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonMoveUp = new javax.swing.JButton();
        jButtonMoveDown = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jCheckBoxCompileJSP = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EtchedBorder(), new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12))));
        jCheckBoxDebugInfo.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Debugging_LabelMnemonic").charAt(0));
        jCheckBoxDebugInfo.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_DebugInfo_JCheckBox"));
        jCheckBoxDebugInfo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jCheckBoxDebugInfo, gridBagConstraints);
        jCheckBoxDebugInfo.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Debugging__A11YDesc"));

        jCheckBoxDeprecation.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Deprecated_LabelMnemonic").charAt(0));
        jCheckBoxDeprecation.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Deprecation_JCheckBox"));
        jCheckBoxDeprecation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jCheckBoxDeprecation, gridBagConstraints);
        jCheckBoxDeprecation.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Deprecated_A11YDesc"));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelClasspath.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_CompilationClasspath_LabelMnemonic").charAt(0));
        jLabelClasspath.setLabelFor(jTableClasspath);
        jLabelClasspath.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jLabelClasspath, gridBagConstraints);

        jTableClasspath.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollClasspath.setViewportView(jTableClasspath);
        jTableClasspath.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_CompilationClasspath_A11YDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel2.add(jScrollClasspath, gridBagConstraints);

        jButtonAddJar.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_AddJAR_LabelMnemonic").charAt(0));
        jButtonAddJar.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddJar_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonAddJar, gridBagConstraints);
        jButtonAddJar.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_AddJAR_A11YDesc"));

        jButtonAddLibrary.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_AddLibrary_LabelMnemonic").charAt(0));
        jButtonAddLibrary.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddLibrary_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddLibrary, gridBagConstraints);
        jButtonAddLibrary.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_AddLibrary_A11YDesc"));

        jButtonAddProject.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_AddProject_LabelMnemonic").charAt(0));
        jButtonAddProject.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddProject_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddProject, gridBagConstraints);
        jButtonAddProject.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_AddProject_A11YDesc"));

        jButtonRemove.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Remove_LabelMnemonic").charAt(0));
        jButtonRemove.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Remove_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonRemove, gridBagConstraints);
        jButtonRemove.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Remove_A11YDesc"));

        jButtonMoveUp.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Up_LabelMnemonic").charAt(0));
        jButtonMoveUp.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveUp_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonMoveUp, gridBagConstraints);
        jButtonMoveUp.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Up_A11YDesc"));

        jButtonMoveDown.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_Down_LabelMnemonic").charAt(0));
        jButtonMoveDown.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveDown_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonMoveDown, gridBagConstraints);
        jButtonMoveDown.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_Down_A11YDesc"));

        jButtonEdit.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_EditLibrary_LabelMnemonic").charAt(0));
        jButtonEdit.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Edit_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel2.add(jButtonEdit, gridBagConstraints);
        jButtonEdit.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_EditLibrary_A11YDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jPanel2, gridBagConstraints);

        jCheckBoxCompileJSP.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeCompile_TestCompile_LabelMnemonic").charAt(0));
        jCheckBoxCompileJSP.setText(NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_CompileJSP_JCheckBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(jCheckBoxCompileJSP, gridBagConstraints);
        jCheckBoxCompileJSP.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("ACS_CustomizeCompile_TestCompile_A11YDesc"));

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddJar;
    private javax.swing.JButton jButtonAddLibrary;
    private javax.swing.JButton jButtonAddProject;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonMoveDown;
    private javax.swing.JButton jButtonMoveUp;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JCheckBox jCheckBoxCompileJSP;
    private javax.swing.JCheckBox jCheckBoxDebugInfo;
    private javax.swing.JCheckBox jCheckBoxDeprecation;
    private javax.swing.JLabel jLabelClasspath;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollClasspath;
    private javax.swing.JTable jTableClasspath;
    // End of variables declaration//GEN-END:variables

}
