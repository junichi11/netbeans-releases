/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.mercurial.ui.wizards;

import javax.swing.JPanel;

import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.versioning.util.AccessibleJFileChooser;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.util.NbBundle;

public final class CloneDestinationDirectoryPanel extends JPanel implements ActionListener {
    private String message;
    
    /** Creates new form CloneVisualPanel2 */
    public CloneDestinationDirectoryPanel() {
        initComponents();
        directoryBrowseButton.addActionListener(this);    
        scanForProjectsCheckBox.addActionListener(this);    
        directoryField.setText(defaultWorkingDirectory().getPath());
        scanForProjectsCheckBox.setSelected(HgModuleConfig.getDefault().getShowCloneCompleted());
    }
    
    public String getName() {
        if (destinationDirectoryPanel == null) {
            return null;
        }
        return destinationDirectoryPanel.getName();
    }
    
    public String getDirectory() {
        return directoryField.getText();
    }

    public String getCloneName() {
        return nameField.getText();
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        String dir = directoryField.getText();
        String name = nameField.getText();
        File file = new File (dir, name);
        if (file.exists()) {
            message = NbBundle.getMessage(CloneRepositoryPanel.class, "MSG_TARGET_EXISTS"); // NOI18N
            return false;
        } else {
            message = "";
            return true;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        destinationDirectoryPanel = new javax.swing.JPanel();
        directoryLabel = new javax.swing.JLabel();
        directoryBrowseButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        destinationDirectoryPanel.setName(org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "destinationDirectoryPanel.Name")); // NOI18N

        directoryLabel.setLabelFor(directoryField);
        org.openide.awt.Mnemonics.setLocalizedText(directoryLabel, org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "directoryLabel.Name")); // NOI18N

        directoryField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(directoryBrowseButton, org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "directoryBrowseButton.Name")); // NOI18N

        nameLabel.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "nameLabel.Name")); // NOI18N

        scanForProjectsCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(scanForProjectsCheckBox, org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "CTL_Scan_After_Clone")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "directoryTitleLabel.Name")); // NOI18N

        org.jdesktop.layout.GroupLayout destinationDirectoryPanelLayout = new org.jdesktop.layout.GroupLayout(destinationDirectoryPanel);
        destinationDirectoryPanel.setLayout(destinationDirectoryPanelLayout);
        destinationDirectoryPanelLayout.setHorizontalGroup(
            destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(destinationDirectoryPanelLayout.createSequentialGroup()
                .add(destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(destinationDirectoryPanelLayout.createSequentialGroup()
                        .add(destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(nameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(directoryLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                            .add(directoryField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(directoryBrowseButton))
                    .add(scanForProjectsCheckBox)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 597, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        destinationDirectoryPanelLayout.setVerticalGroup(
            destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(destinationDirectoryPanelLayout.createSequentialGroup()
                .add(jLabel1)
                .add(20, 20, 20)
                .add(destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(directoryLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(directoryBrowseButton)
                    .add(directoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(28, 28, 28)
                .add(destinationDirectoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(nameLabel))
                .add(18, 18, 18)
                .add(scanForProjectsCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(75, 75, 75))
        );

        scanForProjectsCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "ACSD_Scan_After_Clone")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(destinationDirectoryPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(destinationDirectoryPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void directoryFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_directoryFieldActionPerformed
    
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == directoryBrowseButton) {
            onBrowseClick();
        } else if (evt.getSource() == scanForProjectsCheckBox) {
            HgModuleConfig.getDefault().setShowCloneCompleted(scanForProjectsCheckBox.isSelected());
        }
    }

    private void onBrowseClick() {
        File oldFile = defaultWorkingDirectory();
        JFileChooser fileChooser = new AccessibleJFileChooser(NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "ACSD_BrowseFolder"), oldFile);   // NO I18N
        fileChooser.setDialogTitle(NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "Browse_title"));                                            // NO I18N
        fileChooser.setMultiSelectionEnabled(false);
        FileFilter[] old = fileChooser.getChoosableFileFilters();
        for (int i = 0; i < old.length; i++) {
            FileFilter fileFilter = old[i];
            fileChooser.removeChoosableFileFilter(fileFilter);
        }
        fileChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
            public String getDescription() {
                return NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "Folders"); // NOI18N
            }
        });
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showDialog(this, NbBundle.getMessage(CloneDestinationDirectoryPanel.class, "OK_Button"));                                            // NO I18N
        File f = fileChooser.getSelectedFile();
        if (f != null) {
            directoryField.setText(f.getAbsolutePath());
        }
    }
    
    /**
     * Returns file to be initally used.
     * <ul>
     * <li>first is takes text in workTextField
     * <li>then recent project folder
     * <li>finally <tt>user.home</tt>
     * <ul>
     */
    private File defaultWorkingDirectory() {
        File defaultDir = null;
        String current = directoryField.getText();
        if (current != null && !(current.trim().equals(""))) {  // NOI18N
            File currentFile = new File(current);
            while (currentFile != null && currentFile.exists() == false) {
                currentFile = currentFile.getParentFile();
            }
            if (currentFile != null) {
                if (currentFile.isFile()) {
                    defaultDir = currentFile.getParentFile();
                } else {
                    defaultDir = currentFile;
                }
            }
        }

        if (defaultDir == null) {
            File projectFolder = ProjectChooser.getProjectsFolder();
            if (projectFolder.exists() && projectFolder.isDirectory()) {
                defaultDir = projectFolder;
            }

        }

        if (defaultDir == null) {
            defaultDir = new File(System.getProperty("user.home"));  // NOI18N
        }

        return defaultDir;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel destinationDirectoryPanel;
    private javax.swing.JButton directoryBrowseButton;
    final javax.swing.JTextField directoryField = new javax.swing.JTextField();
    private javax.swing.JLabel directoryLabel;
    private javax.swing.JLabel jLabel1;
    final javax.swing.JTextField nameField = new javax.swing.JTextField();
    private javax.swing.JLabel nameLabel;
    final javax.swing.JCheckBox scanForProjectsCheckBox = new javax.swing.JCheckBox();
    // End of variables declaration//GEN-END:variables
    
}

