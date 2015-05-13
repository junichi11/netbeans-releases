/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.git.remote.ui.repository.remote;

/**
 *
 * @author Tomas Stupka
 */
public class RemoteRepositoryPanel extends javax.swing.JPanel {

    /** Creates new form RepositoryPanel */
    public RemoteRepositoryPanel() {
        initComponents();
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/git/remote/ui/clone/Bundle"); // NOI18N
        setName(bundle.getString("BK2018")); // NOI18N
        setVerifyInputWhenFocusTarget(false);

        org.openide.awt.Mnemonics.setLocalizedText(tipLabel, "-"); // NOI18N
        tipLabel.setMaximumSize(new java.awt.Dimension(32767, 32767));

        org.openide.awt.Mnemonics.setLocalizedText(proxySettingsButton, org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "BK0006")); // NOI18N
        proxySettingsButton.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "ACSD_ProxyDialog")); // NOI18N
        proxySettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proxySettingsButtonActionPerformed(evt);
            }
        });

        repositoryLabel.setLabelFor(urlComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(repositoryLabel, org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "BK0002")); // NOI18N
        repositoryLabel.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "TT_RepositoryUrl")); // NOI18N

        urlComboBox.setEditable(true);
        java.awt.Component editorComp = urlComboBox.getEditor().getEditorComponent();
        if (editorComp instanceof javax.swing.JTextField) {
            ((javax.swing.JTextField) editorComp).setColumns(35);
        }

        org.openide.awt.Mnemonics.setLocalizedText(directoryBrowseButton, org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "directoryBrowseButton.Name")); // NOI18N
        directoryBrowseButton.setToolTipText(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "TT_Browse")); // NOI18N

        connectionSettings.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(repositoryLabel)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tipLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(urlComboBox, 0, 275, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directoryBrowseButton))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(connectionSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(proxySettingsButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(repositoryLabel)
                    .addComponent(urlComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(directoryBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connectionSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(proxySettingsButton)
                .addContainerGap())
        );

        proxySettingsButton.getAccessibleContext().setAccessibleParent(this);
        repositoryLabel.getAccessibleContext().setAccessibleParent(this);
        urlComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "ACSN_RepositoryURL")); // NOI18N
        urlComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "ACSD_RepositoryURL")); // NOI18N
        urlComboBox.getAccessibleContext().setAccessibleParent(this);

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(RemoteRepositoryPanel.class, "ACSD_RepositoryPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void proxySettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proxySettingsButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_proxySettingsButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JPanel connectionSettings = new javax.swing.JPanel();
    final javax.swing.JButton directoryBrowseButton = new javax.swing.JButton();
    final javax.swing.JButton proxySettingsButton = new javax.swing.JButton();
    final javax.swing.JLabel repositoryLabel = new javax.swing.JLabel();
    final javax.swing.JLabel tipLabel = new javax.swing.JLabel();
    final javax.swing.JComboBox urlComboBox = new javax.swing.JComboBox();
    // End of variables declaration//GEN-END:variables
        
}
