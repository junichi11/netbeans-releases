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

package org.netbeans.modules.web.jsf.dialogs;

import java.util.Hashtable;
import java.util.Iterator;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.web.jsf.JSFConfigDataObject;
import org.netbeans.modules.web.jsf.JSFConfigUtilities;
import org.netbeans.modules.web.jsf.config.model.NavigationRule;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author  radko
 */
public class AddNavigationRuleDialog extends javax.swing.JPanel implements ValidatingPanel{
    private JSFConfigDataObject config;
    private Hashtable existingRules = null;
    /** Creates new form AddNavigationRuleDialog */
    public AddNavigationRuleDialog(JSFConfigDataObject config) {
        initComponents();
        this.config = config;
    }

    public javax.swing.text.JTextComponent[] getDocumentChangeComponents() {
        return new javax.swing.text.JTextComponent[]{jTextFieldFromView};
    }

    public javax.swing.AbstractButton[] getStateChangeComponents() {
        return new javax.swing.AbstractButton[]{  };
    }

    public String validatePanel() {
        if (getFromView().length()==0)
            return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationRule_EmptyFromView");
        if(existingRules == null){
            existingRules = new Hashtable();
            NavigationRule rule;
            Iterator iter = JSFConfigUtilities.getAllNavigationRules(config).iterator();
            while (iter.hasNext()){
                rule = (NavigationRule) iter.next();
                existingRules.put(rule.getFromViewId(), "");
            }
        }
        if (existingRules.get(getFromView())!=null)
            return NbBundle.getMessage(AddManagedBeanDialog.class,"MSG_AddNavigationRule_RuleExist");
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelFromView = new javax.swing.JLabel();
        jTextFieldFromView = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jLabelDesc = new javax.swing.JLabel();
        jScrollPaneDesc = new javax.swing.JScrollPane();
        jTextAreaDesc = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jLabelFromView.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "MNE_FromView").charAt(0));
        jLabelFromView.setLabelFor(jTextFieldFromView);
        jLabelFromView.setText(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "LBL_FromView"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 12);
        add(jLabelFromView, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 5, 0);
        add(jTextFieldFromView, gridBagConstraints);

        jButtonBrowse.setMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "MNE_Browse").charAt(0));
        jButtonBrowse.setText(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "LBL_Browse"));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 11);
        add(jButtonBrowse, gridBagConstraints);

        jLabelDesc.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "MNE_BeanDescription").charAt(0));
        jLabelDesc.setLabelFor(jTextAreaDesc);
        jLabelDesc.setText(org.openide.util.NbBundle.getMessage(AddNavigationRuleDialog.class, "LBL_RuleDescription"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jLabelDesc, gridBagConstraints);

        jTextAreaDesc.setColumns(20);
        jTextAreaDesc.setRows(5);
        jScrollPaneDesc.setViewportView(jTextAreaDesc);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(jScrollPaneDesc, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        try{
            org.netbeans.api.project.SourceGroup[] groups = JSFConfigUtilities.getDocBaseGroups(config.getPrimaryFile());
            org.openide.filesystems.FileObject fo = BrowseFolders.showDialog(groups);
            if (fo!=null) {
                String res = "/"+JSFConfigUtilities.getResourcePath(groups,fo,'/',true);
                jTextFieldFromView.setText(res);
            }
        } catch (java.io.IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }//GEN-LAST:event_jButtonBrowseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JLabel jLabelDesc;
    private javax.swing.JLabel jLabelFromView;
    private javax.swing.JScrollPane jScrollPaneDesc;
    private javax.swing.JTextArea jTextAreaDesc;
    private javax.swing.JTextField jTextFieldFromView;
    // End of variables declaration//GEN-END:variables
    
    public String getFromView(){
        return jTextFieldFromView.getText();
    }
    
    public String getDescription(){
        return jTextAreaDesc.getText();
    }
}
