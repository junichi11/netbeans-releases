/*
 * HibernateConfigurationWizardPanel.java
 *
 * Created on January 9, 2008, 4:26 PM
 */
package org.netbeans.modules.hibernate.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author  gowri
 */
public class HibernateConfigurationWizardPanel extends javax.swing.JPanel  {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private Project project;

    /** Creates new form HibernateConfigurationWizardPanel */
    public HibernateConfigurationWizardPanel(HibernateConfigurationWizardDescriptor descriptor) {
        initComponents();
        project = descriptor.getProject();
        cmbDialect.setModel(new javax.swing.DefaultComboBoxModel(Util.getDialectCodes()));
        cmbDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[0]));
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "LBL_HibernateConfigurationPanel_Name");
    }

    private void fillCombos() {
        String strIndex = cmbDialect.getSelectedItem().toString();
        String drivers = Util.getSelectedDriver(strIndex);
        String urlConnection = Util.getSelectedURLConnection(strIndex);
        cmbDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[]{drivers}));
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[]{urlConnection}));

    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        cmbDialect = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        cmbDriver = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cmbURL = new javax.swing.JComboBox();

        setName(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "LBL_HibernateConfigurationPanel_Name")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel4.text")); // NOI18N

        cmbDialect.setEditable(true);
        cmbDialect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbDialect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDialectActionPerformed(evt);
            }
        });

        jLabel5.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel5.text")); // NOI18N

        cmbDriver.setEditable(true);
        cmbDriver.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText(org.openide.util.NbBundle.getMessage(HibernateConfigurationWizardPanel.class, "HibernateConfigurationWizardPanel.jLabel6.text")); // NOI18N

        cmbURL.setEditable(true);
        cmbURL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel6)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cmbDriver, 0, 488, Short.MAX_VALUE)
                    .add(cmbDialect, 0, 488, Short.MAX_VALUE)
                    .add(cmbURL, 0, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmbDialect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cmbDriver, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cmbURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void cmbDialectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDialectActionPerformed
        // TODO add your handling code here:
        fillCombos();
    }//GEN-LAST:event_cmbDialectActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbDialect;
    private javax.swing.JComboBox cmbDriver;
    private javax.swing.JComboBox cmbURL;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables


    public void actionPerformed(ActionEvent e) {
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    public String getSelectedDialect() {
        if (cmbDialect.getSelectedItem() != null) {
            return Util.getSelectedDialect(cmbDialect.getSelectedItem().toString());
        }
        return null;
    }

    public String getSelectedDriver() {
        if (cmbDriver.getSelectedItem() != null) {
            return cmbDriver.getSelectedItem().toString();
        }
        return null;
    }

    public String getSelectedURL() {
        if (cmbURL.getSelectedItem() != null) {
            return cmbURL.getSelectedItem().toString();
        }
        return null;

    }
}
