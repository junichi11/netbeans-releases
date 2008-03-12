/*
 * HibernateConfigurationPanel.java
 *
 * Created on 11 March, 2008, 3:00 PM
 */

package org.netbeans.modules.hibernate.framework;

/**
 *
 * @author  isvuser
 */
public class HibernateConfigurationPanel extends javax.swing.JPanel {
    
    /** Creates new form HibernateConfigurationPanel */
    public HibernateConfigurationPanel() {
        initComponents();
    }

    public boolean isPanelValid() {
      return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hibernateSessionNameLabel = new javax.swing.JLabel();
        hibernateSessionNameTextField = new javax.swing.JTextField();
        databaseDialectNameLabel = new javax.swing.JLabel();
        databaseDialectNameTextField = new javax.swing.JTextField();

        hibernateSessionNameLabel.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationPanel.class, "HibernateConfigurationPanel.hibernateSessionNameLabel.text")); // NOI18N

        hibernateSessionNameTextField.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationPanel.class, "HibernateConfigurationPanel.hibernateSessionNameTextField.text")); // NOI18N

        databaseDialectNameLabel.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationPanel.class, "HibernateConfigurationPanel.databaseDialectNameLabel.text")); // NOI18N

        databaseDialectNameTextField.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationPanel.class, "HibernateConfigurationPanel.databaseDialectNameTextField.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(hibernateSessionNameLabel)
                    .add(databaseDialectNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(databaseDialectNameTextField)
                    .add(hibernateSessionNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hibernateSessionNameLabel)
                    .add(hibernateSessionNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(databaseDialectNameLabel)
                    .add(databaseDialectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel databaseDialectNameLabel;
    private javax.swing.JTextField databaseDialectNameTextField;
    private javax.swing.JLabel hibernateSessionNameLabel;
    private javax.swing.JTextField hibernateSessionNameTextField;
    // End of variables declaration//GEN-END:variables
    
}
