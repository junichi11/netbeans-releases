/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tomcat5.customizer;

import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.JTextField;


/**
 * Customizer general (connection) tab.
 * 
 * @author  sherold
 */
public class CustomizerGeneral extends javax.swing.JPanel {
    
    private CustomizerDataSupport custData;
    
    /** Creates new form CustomizerGeneral */
    public CustomizerGeneral(CustomizerDataSupport custData) {
        this.custData = custData;
        initComponents();
        
        JTextField jSpinner1TextField = ((JSpinner.NumberEditor)jSpinner1.getEditor()).getTextField();
        JTextField jSpinner2TextField = ((JSpinner.NumberEditor)jSpinner2.getEditor()).getTextField();
        
        // work-around for jspinner incorrect fonts
        Font font = usernameTextField.getFont();
        jSpinner1TextField.setFont(font);
        jSpinner2TextField.setFont(font);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        homeTextField = new javax.swing.JTextField();
        baseTextField = new javax.swing.JTextField();
        homeLabel = new javax.swing.JLabel();
        baseLabel = new javax.swing.JLabel();
        usernameTextField = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        monitorCheckBox = new javax.swing.JCheckBox();
        passwordField = new javax.swing.JPasswordField();
        roleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        homeTextField.setColumns(30);
        homeTextField.setDocument(custData.getCatalinaHomeModel());
        homeTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 0, 12);
        add(homeTextField, gridBagConstraints);

        baseTextField.setColumns(30);
        baseTextField.setDocument(custData.getCatalinaBaseModel());
        baseTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 12);
        add(baseTextField, gridBagConstraints);

        homeLabel.setLabelFor(homeTextField);
        org.openide.awt.Mnemonics.setLocalizedText(homeLabel, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_Catalina_home"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(homeLabel, gridBagConstraints);

        baseLabel.setLabelFor(baseTextField);
        org.openide.awt.Mnemonics.setLocalizedText(baseLabel, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_Catalina_Base"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(baseLabel, gridBagConstraints);

        usernameTextField.setColumns(15);
        usernameTextField.setDocument(custData.getUsernameModel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 0);
        add(usernameTextField, gridBagConstraints);

        usernameLabel.setLabelFor(usernameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(usernameLabel, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_Username"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(usernameLabel, gridBagConstraints);

        passwordLabel.setLabelFor(passwordField);
        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_Password"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(passwordLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(monitorCheckBox, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_Monitor"));
        monitorCheckBox.setModel(custData.getMonitorModel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(monitorCheckBox, gridBagConstraints);

        passwordField.setColumns(15);
        passwordField.setDocument(custData.getPasswordModel());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(passwordField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(roleLabel, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_ManagerRole"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 12);
        add(roleLabel, gridBagConstraints);

        jLabel1.setLabelFor(jSpinner1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_ServerPort"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(jLabel1, gridBagConstraints);

        jLabel2.setLabelFor(jSpinner2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_ShutdownPort"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CustomizerGeneral.class, "TXT_NoteChangesTakeAffect"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 12, 0);
        add(jLabel3, gridBagConstraints);

        jSpinner1.setFont(new java.awt.Font("Dialog", 0, 12));
        jSpinner1.setModel(custData.getServerPortModel());
        jSpinner1.setEditor(new JSpinner.NumberEditor(jSpinner1, "#"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 0);
        add(jSpinner1, gridBagConstraints);

        jSpinner2.setFont(new java.awt.Font("Dialog", 0, 12));
        jSpinner2.setModel(custData.getShutdownPortModel());
        jSpinner2.setEditor(new JSpinner.NumberEditor(jSpinner2, "#"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jSpinner2, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel baseLabel;
    private javax.swing.JTextField baseTextField;
    private javax.swing.JLabel homeLabel;
    private javax.swing.JTextField homeTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JCheckBox monitorCheckBox;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel roleLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTextField;
    // End of variables declaration//GEN-END:variables
    
}
