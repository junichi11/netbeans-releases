/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.subversion.ui.update;

/**
 *
 * @author  Tomas Stupka
 */
public class RevertModificationsPanel extends javax.swing.JPanel {
    
    /** Creates new form ReverModificationsPanel */
    public RevertModificationsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText("Revert Modifications from:");

        buttonGroup.add(lcoalChangesRadioButton);
        lcoalChangesRadioButton.setSelected(true);
        lcoalChangesRadioButton.setText("Local Changes");
        lcoalChangesRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lcoalChangesRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lcoalChangesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lcoalChangesRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup.add(commitsRadioButton);
        commitsRadioButton.setText("Previous Commit(s)");
        commitsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        commitsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        commitsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commitsRadioButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Start with Revision:");

        startRevisionTextField.setEnabled(false);

        startSearchButton.setText("Search...");
        startSearchButton.setEnabled(false);

        jLabel9.setText("(empty means repository HEAD)");

        endRevisionTextField.setEnabled(false);

        endSearchButton.setText("Search...");
        endSearchButton.setEnabled(false);

        inclusiveCheckBox.setText("inclusive");
        inclusiveCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        inclusiveCheckBox.setEnabled(false);
        inclusiveCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel3.setText("End with Revision:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(startRevisionTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                            .add(endRevisionTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                            .add(jLabel9))
                        .add(6, 6, 6)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(endSearchButton)
                            .add(startSearchButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(inclusiveCheckBox))
                    .add(jLabel1)
                    .add(commitsRadioButton)
                    .add(lcoalChangesRadioButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(14, 14, 14)
                .add(lcoalChangesRadioButton)
                .add(27, 27, 27)
                .add(commitsRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startRevisionTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(startSearchButton)
                    .add(inclusiveCheckBox)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(endRevisionTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(endSearchButton)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9)
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void commitsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commitsRadioButtonActionPerformed
        enableCommitsFields(true);
    }//GEN-LAST:event_commitsRadioButtonActionPerformed

    private void lcoalChangesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lcoalChangesRadioButtonActionPerformed
        enableCommitsFields(false);
    }//GEN-LAST:event_lcoalChangesRadioButtonActionPerformed

    private void enableCommitsFields(boolean b) {
        startRevisionTextField.setEnabled(b);
        endRevisionTextField.setEnabled(b);
        startSearchButton.setEnabled(b);
        endSearchButton.setEnabled(b);
        inclusiveCheckBox.setEnabled(b);
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
    final javax.swing.JRadioButton commitsRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JTextField endRevisionTextField = new javax.swing.JTextField();
    final javax.swing.JButton endSearchButton = new javax.swing.JButton();
    final javax.swing.JCheckBox inclusiveCheckBox = new javax.swing.JCheckBox();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    final javax.swing.JRadioButton lcoalChangesRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JTextField startRevisionTextField = new javax.swing.JTextField();
    final javax.swing.JButton startSearchButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables
    
}
