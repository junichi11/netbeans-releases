/*
 * PackagingInfo2Panel.java
 *
 * Created on July 28, 2008, 12:42 PM
 */

package org.netbeans.modules.cnd.makeproject.configurations.ui;

import org.netbeans.modules.cnd.makeproject.MakeOptions;
import org.netbeans.modules.cnd.makeproject.api.configurations.PackagingConfiguration;

/**
 *
 * @author  thp
 */
public class PackagingFilesOuterPanel extends javax.swing.JPanel {

    /** Creates new form PackagingInfo2Panel */
    public PackagingFilesOuterPanel(PackagingFilesPanel innerPanel, PackagingConfiguration conf) {
        java.awt.GridBagConstraints gridBagConstraints;
        
        initComponents();
        
        remove(tmpPanel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        
        // Set default values
        exePermTextField.setText(MakeOptions.getInstance().getDefExePerm()); // NOI18N
        filePermTextField.setText(MakeOptions.getInstance().getDefFilePerm()); // NOI18N
        groupTextField.setText(MakeOptions.getInstance().getDefOwner()); // NOI18N
        ownerTextField.setText(MakeOptions.getInstance().getDefGroup()); // NOI18N
        
        // Hide some fields:
        if (conf.getType().getValue() == PackagingConfiguration.TYPE_TAR || conf.getType().getValue() == PackagingConfiguration.TYPE_ZIP) {
            groupLabel.setEnabled(false);
            groupTextField.setEnabled(false);
            ownerLabel.setEnabled(false);
            ownerTextField.setEnabled(false);
//            groupTextField.setText(""); // NOI18N
//            ownerTextField.setText(""); // NOI18N
        }
        
        innerPanel.setOuterPanel(this);
        
        add(innerPanel, gridBagConstraints);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tmpPanel = new javax.swing.JPanel();
        defaultsPanel = new javax.swing.JPanel();
        topDirectoryLabel = new javax.swing.JLabel();
        topDirectoryTextField = new javax.swing.JTextField();
        exePermLabel = new javax.swing.JLabel();
        exePermTextField = new javax.swing.JTextField();
        filePermLabel = new javax.swing.JLabel();
        filePermTextField = new javax.swing.JTextField();
        ownerLabel = new javax.swing.JLabel();
        ownerTextField = new javax.swing.JTextField();
        groupLabel = new javax.swing.JLabel();
        groupTextField = new javax.swing.JTextField();
        defaultValues = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.jdesktop.layout.GroupLayout tmpPanelLayout = new org.jdesktop.layout.GroupLayout(tmpPanel);
        tmpPanel.setLayout(tmpPanelLayout);
        tmpPanelLayout.setHorizontalGroup(
            tmpPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 547, Short.MAX_VALUE)
        );
        tmpPanelLayout.setVerticalGroup(
            tmpPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 240, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(tmpPanel, gridBagConstraints);

        defaultsPanel.setLayout(new java.awt.GridBagLayout());

        topDirectoryLabel.setDisplayedMnemonic('o');
        topDirectoryLabel.setLabelFor(topDirectoryTextField);
        topDirectoryLabel.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.topDirectoryLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        defaultsPanel.add(topDirectoryLabel, gridBagConstraints);

        topDirectoryTextField.setColumns(8);
        topDirectoryTextField.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.topDirectoryTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        defaultsPanel.add(topDirectoryTextField, gridBagConstraints);

        exePermLabel.setDisplayedMnemonic('x');
        exePermLabel.setLabelFor(exePermTextField);
        exePermLabel.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.exePermLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        defaultsPanel.add(exePermLabel, gridBagConstraints);

        exePermTextField.setColumns(5);
        exePermTextField.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.exePermTextField.text")); // NOI18N
        exePermTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exePermTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        defaultsPanel.add(exePermTextField, gridBagConstraints);

        filePermLabel.setDisplayedMnemonic('i');
        filePermLabel.setLabelFor(filePermTextField);
        filePermLabel.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.filePermLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        defaultsPanel.add(filePermLabel, gridBagConstraints);

        filePermTextField.setColumns(5);
        filePermTextField.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.filePermTextField.text")); // NOI18N
        filePermTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filePermTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        defaultsPanel.add(filePermTextField, gridBagConstraints);

        ownerLabel.setDisplayedMnemonic('w');
        ownerLabel.setLabelFor(ownerTextField);
        ownerLabel.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.ownerLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        defaultsPanel.add(ownerLabel, gridBagConstraints);

        ownerTextField.setColumns(5);
        ownerTextField.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.ownerTextField.text")); // NOI18N
        ownerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        defaultsPanel.add(ownerTextField, gridBagConstraints);

        groupLabel.setDisplayedMnemonic('p');
        groupLabel.setLabelFor(groupTextField);
        groupLabel.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.groupLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        defaultsPanel.add(groupLabel, gridBagConstraints);

        groupTextField.setColumns(5);
        groupTextField.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.groupTextField.text")); // NOI18N
        groupTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        defaultsPanel.add(groupTextField, gridBagConstraints);

        defaultValues.setText(org.openide.util.NbBundle.getMessage(PackagingFilesOuterPanel.class, "PackagingFilesOuterPanel.defaultValues.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        defaultsPanel.add(defaultValues, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        add(defaultsPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void exePermTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exePermTextFieldActionPerformed
    MakeOptions.getInstance().setDefExePerm(exePermTextField.getText());
}//GEN-LAST:event_exePermTextFieldActionPerformed

private void filePermTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePermTextFieldActionPerformed
    MakeOptions.getInstance().setDefFilePerm(filePermTextField.getText());
}//GEN-LAST:event_filePermTextFieldActionPerformed

private void ownerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerTextFieldActionPerformed
    MakeOptions.getInstance().setDefOwner(ownerTextField.getText());
}//GEN-LAST:event_ownerTextFieldActionPerformed

private void groupTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupTextFieldActionPerformed
    MakeOptions.getInstance().setDefGroup(groupTextField.getText());
}//GEN-LAST:event_groupTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel defaultValues;
    private javax.swing.JPanel defaultsPanel;
    private javax.swing.JLabel exePermLabel;
    private javax.swing.JTextField exePermTextField;
    private javax.swing.JLabel filePermLabel;
    private javax.swing.JTextField filePermTextField;
    private javax.swing.JLabel groupLabel;
    private javax.swing.JTextField groupTextField;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JTextField ownerTextField;
    private javax.swing.JPanel tmpPanel;
    private javax.swing.JLabel topDirectoryLabel;
    private javax.swing.JTextField topDirectoryTextField;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JTextField getDirPermTextField() {
        return exePermTextField;
    }

    public javax.swing.JTextField getFilePermTextField() {
        return filePermTextField;
    }

    public javax.swing.JTextField getGroupTextField() {
        return groupTextField;
    }

    public javax.swing.JTextField getOwnerTextField() {
        return ownerTextField;
    }
    
    public javax.swing.JTextField getTopDirectoryTextField() {
        return topDirectoryTextField;
    }
}
