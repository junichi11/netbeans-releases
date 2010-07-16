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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.nodes;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 *
 * @author  mkleint
 */
public class InstallDocSourcePanel extends javax.swing.JPanel {

    private static File lastFolder = new File(System.getProperty("user.home")); //NOI18N

    private boolean docs;
    
    /** Creates new form InstallPanel */
    private InstallDocSourcePanel(boolean javadoc) {
        initComponents();
        docs = javadoc;
        if (javadoc) {
            lblFile.setText(org.openide.util.NbBundle.getMessage(InstallDocSourcePanel.class, "TXT_Javadoc_Loc"));
        } else {
            lblFile.setText(org.openide.util.NbBundle.getMessage(InstallDocSourcePanel.class, "TXT_Sources_Loc"));
        }
    }
    
    boolean isJavadoc() {
        return docs;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFile = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(lblFile, "Javadoc Location :");

        org.openide.awt.Mnemonics.setLocalizedText(btnFile, org.openide.util.NbBundle.getMessage(InstallDocSourcePanel.class, "BTN_Browse")); // NOI18N
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtFile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnFile)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFile)
                    .add(btnFile)
                    .add(txtFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        JFileChooser chooser = new JFileChooser(lastFolder);
        chooser.setDialogTitle(isJavadoc() ? NbBundle.getMessage(InstallDocSourcePanel.class, "TIT_Select_javadoc_zip")
                                           : NbBundle.getMessage(InstallDocSourcePanel.class, "TIT_Select_source_zip"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory() || f.getName().toLowerCase().endsWith(".jar") || f.getName().toLowerCase().endsWith(".zip")); //NOI18N
            }
            public String getDescription() {
                
                return isJavadoc() ? NbBundle.getMessage(InstallDocSourcePanel.class, "LBL_Select_javadoc_zip")
                                   : NbBundle.getMessage(InstallDocSourcePanel.class, "LBL_Select_source_zip");
            }
        });
        chooser.setMultiSelectionEnabled(false);
        if (txtFile.getText().trim().length() > 0) {
            File fil = new File(txtFile.getText().trim());
            if (fil.exists()) {
                chooser.setSelectedFile(fil);
            }
        }
        int ret = chooser.showDialog(SwingUtilities.getWindowAncestor(this), org.openide.util.NbBundle.getMessage(InstallDocSourcePanel.class, "BTN_Select"));
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtFile.setText(chooser.getSelectedFile().getAbsolutePath());
            txtFile.requestFocusInWindow();
        }

    }//GEN-LAST:event_btnFileActionPerformed

    File getFile() {
        File fil = txtFile.getText().trim().length() > 0 ? new File(txtFile.getText().trim()) : null;
        return fil != null && fil.exists() ? fil : null;
    }
    
    public static File showInstallDialog(boolean javadoc) {
        final InstallDocSourcePanel panel = new InstallDocSourcePanel(javadoc);
        final JButton btnSelect  = new JButton(org.openide.util.NbBundle.getMessage(InstallDocSourcePanel.class, "BTN_Select"));
        btnSelect.setEnabled(panel.getFile() != null);
        panel.addDocListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                btnSelect.setEnabled(panel.getFile() != null);
            }
            public void insertUpdate(DocumentEvent e) {
                btnSelect.setEnabled(panel.getFile() != null);
            }
            public void removeUpdate(DocumentEvent e) {
                btnSelect.setEnabled(panel.getFile() != null);
            }
        });
        Object[] options =  new Object[] {
            btnSelect,
            NotifyDescriptor.CANCEL_OPTION
        };
        String tit = panel.isJavadoc() ? NbBundle.getMessage(InstallDocSourcePanel.class, "TIT_Use_local_docs")
                                 : NbBundle.getMessage(InstallDocSourcePanel.class, "TIT_Use_local_source");
        DialogDescriptor dd = new DialogDescriptor(panel, tit,
                true,
                options,
                btnSelect, 0, HelpCtx.DEFAULT_HELP, null);
        dd.setClosingOptions(options);
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == btnSelect) {
            lastFolder = panel.getFile().getParentFile();
            return panel.getFile();
        }
        return null;
    }

    private void addDocListener(DocumentListener documentListener) {
        txtFile.getDocument().addDocumentListener(documentListener);
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFile;
    private javax.swing.JLabel lblFile;
    private javax.swing.JTextField txtFile;
    // End of variables declaration//GEN-END:variables
    
}
