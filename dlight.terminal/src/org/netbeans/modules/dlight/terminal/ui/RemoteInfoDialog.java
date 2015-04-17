/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

/*
 * RemoteInfoDialog.java
 *
 * Created on 09.03.2010, 20:39:17
 */
package org.netbeans.modules.dlight.terminal.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.prefs.Preferences;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.openide.util.NbPreferences;

/**
 *
 * @author Vladimir Voskresensky
 */
public final class RemoteInfoDialog extends javax.swing.JPanel {
    private static final String LAST_SELECTED_HOST = "lastSelectedHost"; // NOI18N

    /** Creates new form RemoteInfoDialog */
    public RemoteInfoDialog(String user) {
        initComponents();
        userField.setText(user);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        cbKnownHosts = new javax.swing.JComboBox();
        btnKnownHosts = new javax.swing.JRadioButton();
        btnNewHost = new javax.swing.JRadioButton();
        pnlConnectionInfo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        hostField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();

        buttonGroup1.add(btnKnownHosts);
        btnKnownHosts.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(btnKnownHosts, org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.btnKnownHosts.text")); // NOI18N
        btnKnownHosts.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnKnownHostsItemStateChanged(evt);
            }
        });

        buttonGroup1.add(btnNewHost);
        org.openide.awt.Mnemonics.setLocalizedText(btnNewHost, org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.btnNewHost.text")); // NOI18N
        btnNewHost.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnNewHostItemStateChanged(evt);
            }
        });

        pnlConnectionInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "NewHostInfoTitle"))); // NOI18N

        jLabel1.setLabelFor(userField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.jLabel1.text")); // NOI18N

        userField.setText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.userField.text")); // NOI18N
        userField.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.userField.toolTipText")); // NOI18N
        userField.setInputVerifier(new NonEmpty());

        jLabel2.setLabelFor(hostField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.jLabel2.text")); // NOI18N

        hostField.setText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.hostField.text")); // NOI18N
        hostField.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.hostField.toolTipText")); // NOI18N
        hostField.setInputVerifier(new NonEmpty());

        jLabel3.setLabelFor(portField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.jLabel3.text")); // NOI18N

        portField.setColumns(4);
        portField.setText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.portField.text")); // NOI18N
        portField.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.portField.toolTipText")); // NOI18N
        portField.setInputVerifier(new IntVerifier(portField));

        javax.swing.GroupLayout pnlConnectionInfoLayout = new javax.swing.GroupLayout(pnlConnectionInfo);
        pnlConnectionInfo.setLayout(pnlConnectionInfoLayout);
        pnlConnectionInfoLayout.setHorizontalGroup(
            pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
            .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlConnectionInfoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(userField, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                        .addComponent(hostField, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()))
        );
        pnlConnectionInfoLayout.setVerticalGroup(
            pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
            .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pnlConnectionInfoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        userField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.userField.AccessibleContext.accessibleName")); // NOI18N
        hostField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.hostField.AccessibleContext.accessibleName")); // NOI18N
        portField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RemoteInfoDialog.class, "RemoteInfoDialog.portField.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlConnectionInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnKnownHosts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbKnownHosts, 0, 224, Short.MAX_VALUE))
                    .addComponent(btnNewHost))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbKnownHosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKnownHosts))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNewHost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlConnectionInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnKnownHostsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnKnownHostsItemStateChanged
        if (java.awt.event.ItemEvent.SELECTED == evt.getStateChange()) {
            selectMode(true);
        }
    }//GEN-LAST:event_btnKnownHostsItemStateChanged

    private void btnNewHostItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnNewHostItemStateChanged
        if (java.awt.event.ItemEvent.SELECTED == evt.getStateChange()) {
            selectMode(false);
        }
    }//GEN-LAST:event_btnNewHostItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnKnownHosts;
    private javax.swing.JRadioButton btnNewHost;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cbKnownHosts;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel pnlConnectionInfo;
    private javax.swing.JTextField portField;
    private javax.swing.JTextField userField;
    // End of variables declaration//GEN-END:variables

    private ExecutionEnvironment last;
    public ExecutionEnvironment getExecutionEnvironment() {
        if (btnKnownHosts.isSelected()) {
            last = (ExecutionEnvironment) cbKnownHosts.getSelectedItem();
        } else {
            if (userField.getText().isEmpty() || hostField.getText().isEmpty()) {
                return null;
            }

            int port = 22;

            if (!portField.getText().isEmpty()) {
                try {
                    port = Integer.parseInt(portField.getText());
                } catch (NumberFormatException ex) {
                }
            }

            last = ExecutionEnvironmentFactory.createNew(userField.getText(), hostField.getText(), port);
        }
        Preferences prefs = NbPreferences.forModule(RemoteInfoDialog.class);
        prefs.put(LAST_SELECTED_HOST, ExecutionEnvironmentFactory.toUniqueID(last));
        return last;
    }

    private void fillHosts() {
        cbKnownHosts.removeAllItems();
        for (ExecutionEnvironment ee : ConnectionManager.getInstance().getRecentConnections()) {
            cbKnownHosts.addItem(ee);
        }
        boolean hasKnown = cbKnownHosts.getItemCount() > 0;
        btnKnownHosts.setEnabled(hasKnown);
        if (hasKnown) {
            if (last != null) {
                cbKnownHosts.setSelectedItem(last);
            }
            btnKnownHosts.setSelected(true);
        } else {
            btnNewHost.setSelected(true);
        }
        selectMode(hasKnown);
    }

    private void selectMode(boolean knownHosts) {
        cbKnownHosts.setEnabled(knownHosts);
        Component[] components = pnlConnectionInfo.getComponents();
        for (Component component : components) {
            component.setEnabled(!knownHosts);
        }
    }

    public void init() {
        if (last == null) {
            Preferences prefs = NbPreferences.forModule(RemoteInfoDialog.class);
            String eeID = prefs.get(LAST_SELECTED_HOST, "");
            if (!eeID.isEmpty()) {
                last = ExecutionEnvironmentFactory.fromUniqueID(eeID);
            }
        }
        fillHosts();
    }

    private static final class IntVerifier extends InputVerifier {

        private final JTextField tf;

        public IntVerifier(JTextField tf) {
            this.tf = tf;
        }

        @Override
        public boolean verify(JComponent input) {
            try {
                Integer.valueOf(tf.getText());
                return true;
            } catch (NumberFormatException numberFormatException) {
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }
    }

    private static final class NonEmpty extends InputVerifier {

        public NonEmpty() {
        }

        @Override
        public boolean verify(JComponent input) {
            if (input instanceof JTextComponent) {
                JTextComponent tf = (JTextComponent) input;
                return !tf.getText().isEmpty();
            }

            return true;
        }
    }
}
