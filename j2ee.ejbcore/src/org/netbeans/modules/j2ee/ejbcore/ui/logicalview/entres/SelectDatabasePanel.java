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

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.entres;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.modules.j2ee.ejbcore.ui.FoldersListSettings;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;


/**
 * Provide an interface to support datasource selection.
 * @author  Chris Webster
 */
public class SelectDatabasePanel extends javax.swing.JPanel {
    private DefaultComboBoxModel model;
    private Node driverNode;
    private static String PROTOTYPE_VALUE = "jdbc:pointbase://localhost/sample [pbpublic on PBPUBLIC] "; //NOI18N
    private ServiceLocatorStrategyPanel slPanel;
    
    /** Creates new form SelectDatabasePanel */
    public SelectDatabasePanel(String dbName, String lastLocator) {
        model = new DefaultComboBoxModel();
        initComponents();
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_ChooseDatabase"));
        getConnections();
        connectionCombo.setPrototypeDisplayValue(PROTOTYPE_VALUE);
        connectionCombo.setModel(model);
        setAddConnectionStatus();
        databaseName.setText(dbName);
        slPanel = new ServiceLocatorStrategyPanel(lastLocator);
        serviceLocatorPanel.add(slPanel, BorderLayout.CENTER);
        createResourcesCheckBox.setSelected(FoldersListSettings.getDefault().isAgreedCreateServerResources());
    }
    
    private void getConnections() {
        model.removeAllElements();
        DatabaseConnection[] dbconns = ConnectionManager.getDefault().getConnections();
        for (int i = 0; i < dbconns.length; i++) {
            model.addElement(new ConnectionWrapper(dbconns[i]));
        }
    }
    
    private static class ConnectionWrapper {
        private DatabaseConnection dbconn;
        
        public ConnectionWrapper(DatabaseConnection dbconn) {
            this.dbconn = dbconn;
        }
        
        public DatabaseConnection getConnection() {
            return dbconn;
        }
        
        public String toString() {
            return dbconn.getName();
        }
    }
    
    public String getDatabaseName() {
        return databaseName.getText();
    }
    
    public DatabaseConnection getConnection() {
        return ((ConnectionWrapper)connectionCombo.getSelectedItem()).getConnection();
    }
    
    public String getServiceLocator() {
        return slPanel.classSelected();
    }
    
    public ServiceLocatorStrategyPanel getServiceLocatorPanel() {
        return slPanel;
    }
    
    public boolean createServerResources() {
        return createResourcesCheckBox.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        connectionCombo = new javax.swing.JComboBox();
        addDriverButton = new javax.swing.JButton();
        addConnectionButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        databaseName = new javax.swing.JTextField();
        serviceLocatorPanel = new javax.swing.JPanel();
        createResourcesCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(connectionCombo, gridBagConstraints);
        connectionCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_connectionCombo"));

        addDriverButton.setMnemonic(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_AddDriverMnemonic").charAt(0));
        org.openide.awt.Mnemonics.setLocalizedText(addDriverButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle").getString("LBL_AddDriver"));
        addDriverButton.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle").getString("MSG_AddDriver"));
        addDriverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDriverButtonPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(addDriverButton, gridBagConstraints);
        addDriverButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_AddDriver"));

        addConnectionButton.setMnemonic(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_AddConnectionMnemonic").charAt(0));
        org.openide.awt.Mnemonics.setLocalizedText(addConnectionButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle").getString("LBL_AddConnection"));
        addConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addConnectionPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(addConnectionButton, gridBagConstraints);
        addConnectionButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_AddConnection"));

        jLabel1.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_ConnectionMnemonic").charAt(0));
        jLabel1.setLabelFor(connectionCombo);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle").getString("LBL_Connection"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_Connection"));

        jLabel2.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_JNDIMnemonic").charAt(0));
        jLabel2.setLabelFor(databaseName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle").getString("LBL_jndiName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 11, 11);
        add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_jndiName"));

        databaseName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseNameActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 11, 11);
        add(databaseName, gridBagConstraints);

        serviceLocatorPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(serviceLocatorPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(createResourcesCheckBox, org.openide.util.NbBundle.getBundle(SelectDatabasePanel.class).getString("LBL_CreateServerResources"));
        createResourcesCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(SelectDatabasePanel.class).getString("ToolTip_CreateServerResources"));
        createResourcesCheckBox.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
        createResourcesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        createResourcesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createResourcesCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(createResourcesCheckBox, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void createResourcesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createResourcesCheckBoxActionPerformed
        FoldersListSettings.getDefault().setAgreedCreateServerResources(createResourcesCheckBox.isSelected());
    }//GEN-LAST:event_createResourcesCheckBoxActionPerformed

    private void databaseNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_databaseNameActionPerformed

    private void addConnectionPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addConnectionPressed
        ConnectionManager.getDefault().showAddConnectionDialog(null);
        getConnections();
    }//GEN-LAST:event_addConnectionPressed

    private void addDriverButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDriverButtonPressed
        JDBCDriverManager.getDefault().showAddDriverDialog();
        setAddConnectionStatus();
    }//GEN-LAST:event_addDriverButtonPressed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addConnectionButton;
    private javax.swing.JButton addDriverButton;
    private javax.swing.JComboBox connectionCombo;
    private javax.swing.JCheckBox createResourcesCheckBox;
    private javax.swing.JTextField databaseName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel serviceLocatorPanel;
    // End of variables declaration//GEN-END:variables
    
    private void setAddConnectionStatus() {
        int driverCount = JDBCDriverManager.getDefault().getDrivers().length;
        addConnectionButton.setEnabled(driverCount > 0);
    }
}
