/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.autoupdate.ui.wizards;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.netbeans.api.autoupdate.UpdateElement;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jiri Rechtacek
 */
public class LicenseApprovalPanel extends javax.swing.JPanel {
    public static final String LICENSE_APPROVED = "license-approved";
    private Map<String, String> licenses;
    
    /** Creates new form LicenseApprovalPanel */
    public LicenseApprovalPanel (InstallUnitWizardModel model) {
        initComponents ();
        postInitComponents (model);
    }
    
    Collection<String> getLicenses () {
        assert licenses != null : "Licenses must found.";
        if (licenses == null && licenses.isEmpty ()) {
            return Collections.emptyList ();
        }
        return licenses.values ();
    }
    
    private void postInitComponents (InstallUnitWizardModel model) {
        // XXX: Hack to set as same background as JLabel.background
        Color labelBackground = UIManager.getColor ("Label.background");
        if (labelBackground == null) {
            labelBackground = new Color (new JLabel ().getBackground ().getRGB ());
        } else {
            labelBackground = new Color (labelBackground.getRGB ());
        }
        taTitle.setBackground (labelBackground);
        Color textAreaBackground = UIManager.getColor ("TextArea.background");
        if (textAreaBackground == null) {
            textAreaBackground = new Color (new JTextArea ().getBackground ().getRGB ());
        } else {
            textAreaBackground = new Color (textAreaBackground.getRGB ());
        }
        tpLicense.setBackground (textAreaBackground);
        rbDismis.setSelected (true);
        cbLicenseFor.setModel (new DefaultComboBoxModel (getItems (model)));
        cbLicenseFor.setSelectedIndex (0);
        cbLicenseForItemStateChanged (null);
    }
    
    private String [] getItems (InstallUnitWizardModel model) {
        for (UpdateElement el : InstallUnitWizardModel.getVisibleUpdateElements (model.getAllUpdateElements (), false)) {
            if (el.getLicence () != null) {
                if (licenses == null) {
                    licenses = new HashMap<String, String> ();
                }
                licenses.put (el.getDisplayName (), el.getLicence ());
            }
        }
        return licenses.keySet ().toArray (new String [0]);
    }
    
    public boolean isApproved () {
        return rbAccept.isSelected ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgApproveButtons = new javax.swing.ButtonGroup();
        taTitle = new javax.swing.JTextArea();
        lLicenseFor = new javax.swing.JLabel();
        cbLicenseFor = new javax.swing.JComboBox();
        rbAccept = new javax.swing.JRadioButton();
        rbDismis = new javax.swing.JRadioButton();
        spLicense = new javax.swing.JScrollPane();
        tpLicense = new javax.swing.JTextPane();

        taTitle.setEditable(false);
        taTitle.setLineWrap(true);
        taTitle.setText(org.openide.util.NbBundle.getMessage(LicenseApprovalPanel.class, "LicenseApprovalPanel_taTitle_Text")); // NOI18N
        taTitle.setWrapStyleWord(true);
        taTitle.setOpaque(false);

        lLicenseFor.setLabelFor(cbLicenseFor);
        org.openide.awt.Mnemonics.setLocalizedText(lLicenseFor, org.openide.util.NbBundle.getMessage(LicenseApprovalPanel.class, "LicenseApprovalPanel_lLicenseFor_Text")); // NOI18N

        cbLicenseFor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbLicenseForItemStateChanged(evt);
            }
        });

        bgApproveButtons.add(rbAccept);
        org.openide.awt.Mnemonics.setLocalizedText(rbAccept, org.openide.util.NbBundle.getMessage(LicenseApprovalPanel.class, "LicenseApprovalPanel_rbAccept_Text")); // NOI18N
        rbAccept.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAcceptActionPerformed(evt);
            }
        });

        bgApproveButtons.add(rbDismis);
        org.openide.awt.Mnemonics.setLocalizedText(rbDismis, org.openide.util.NbBundle.getMessage(LicenseApprovalPanel.class, "LicenseApprovalPanel_rbDismis_Text")); // NOI18N
        rbDismis.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbDismis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDismisActionPerformed(evt);
            }
        });

        tpLicense.setContentType("text/html");
        tpLicense.setEditable(false);
        spLicense.setViewportView(tpLicense);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rbAccept, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, rbDismis, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, taTitle)
                    .add(layout.createSequentialGroup()
                        .add(111, 111, 111)
                        .add(lLicenseFor)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbLicenseFor, 0, 233, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, spLicense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(taTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbLicenseFor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lLicenseFor))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(spLicense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAccept, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbDismis)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void rbDismisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDismisActionPerformed
    firePropertyChange (LICENSE_APPROVED, null, rbAccept.isSelected ());
}//GEN-LAST:event_rbDismisActionPerformed

private void rbAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAcceptActionPerformed
    firePropertyChange (LICENSE_APPROVED, null, rbAccept.isSelected ());
}//GEN-LAST:event_rbAcceptActionPerformed

private void cbLicenseForItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbLicenseForItemStateChanged
    if (licenses != null) {
        writeLicense ((String) cbLicenseFor.getSelectedItem (), licenses.get ((String) cbLicenseFor.getSelectedItem ()));
    }
}//GEN-LAST:event_cbLicenseForItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgApproveButtons;
    private javax.swing.JComboBox cbLicenseFor;
    private javax.swing.JLabel lLicenseFor;
    private javax.swing.JRadioButton rbAccept;
    private javax.swing.JRadioButton rbDismis;
    private javax.swing.JScrollPane spLicense;
    private javax.swing.JTextArea taTitle;
    private javax.swing.JTextPane tpLicense;
    // End of variables declaration//GEN-END:variables
    
    private void writeLicense (String plugin, String license) {
        license = license.replaceAll ("\\n", "<br>"); // NOI18N
        tpLicense.setText ("<h1>" + // NOI18N
                NbBundle.getMessage (LicenseApprovalPanel.class, "LicenseApprovalPanel_tpLicense_Head", plugin) +
                "</h1>" + license); // NOI18N
        tpLicense.setCaretPosition (0);
    }
}
