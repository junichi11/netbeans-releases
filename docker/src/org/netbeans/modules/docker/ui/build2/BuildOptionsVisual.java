/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docker.ui.build2;

import javax.swing.JPanel;
import org.netbeans.modules.docker.ui.UiUtils;
import org.openide.util.NbBundle;

public final class BuildOptionsVisual extends JPanel {

    public BuildOptionsVisual() {
        initComponents();

        dockerfileTextField.setText("Dockerfile"); // NOI18N
    }

    public String getDockerfile() {
        return UiUtils.getValue(dockerfileTextField);
    }
    
    public void setDockerfile(String dockerfile) {
        dockerfileTextField.setText(dockerfile);
    }
    
    @NbBundle.Messages("LBL_BuildOptions=Build Options")
    @Override
    public String getName() {
        return Bundle.LBL_BuildOptions();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dockerfileLabel = new javax.swing.JLabel();
        dockerfileTextField = new javax.swing.JTextField();
        dockerfileButton = new javax.swing.JButton();

        dockerfileLabel.setLabelFor(dockerfileTextField);
        org.openide.awt.Mnemonics.setLocalizedText(dockerfileLabel, org.openide.util.NbBundle.getMessage(BuildOptionsVisual.class, "BuildOptionsVisual.dockerfileLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dockerfileButton, org.openide.util.NbBundle.getMessage(BuildOptionsVisual.class, "BuildOptionsVisual.dockerfileButton.text")); // NOI18N
        dockerfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dockerfileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dockerfileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dockerfileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dockerfileButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(dockerfileLabel)
                .addComponent(dockerfileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(dockerfileButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dockerfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dockerfileButtonActionPerformed
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        String buildText = UiUtils.getValue(buildContextTextField);
//        String dockerText = UiUtils.getValue(dockerfileTextField);
//
//        File file = null;
//        if (buildText != null || dockerText != null) {
//            if (dockerText == null) {
//                file = new File(buildText);
//                chooser.setSelectedFile(file);
//                chooser.setCurrentDirectory(file);
//            } else if (buildText == null) {
//                file = new File(dockerText);
//                chooser.setSelectedFile(file);
//                chooser.setCurrentDirectory(file);
//            } else {
//                // XXX
//                file = new File(buildText);
//                if (!file.isDirectory()) {
//                    file = file.getParentFile();
//                }
//                file = new File(file, dockerText);
//            }
//        }
//        if (file != null) {
//            chooser.setSelectedFile(file);
//            chooser.setCurrentDirectory(file);
//        }
//
//        if (chooser.showOpenDialog(SwingUtilities.getWindowAncestor(this)) == JFileChooser.APPROVE_OPTION) {
//            if (buildText != null) {
//                File build = FileUtil.normalizeFile(new File(buildText));
//                File selected = FileUtil.normalizeFile(chooser.getSelectedFile());
//                if (selected.getAbsolutePath().startsWith(build.getAbsolutePath())) {
//                    String text = selected.getAbsolutePath().substring(build.getAbsolutePath().length());
//                    if (text.startsWith(File.separator) && !text.startsWith("//")) { // NOI18N
//                        text = text.substring(1);
//                    }
//                    dockerfileTextField.setText(text);
//                } else {
//                    dockerfileTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//                }
//            } else {
//                dockerfileTextField.setText(chooser.getSelectedFile().getAbsolutePath());
//            }
//        }
    }//GEN-LAST:event_dockerfileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dockerfileButton;
    private javax.swing.JLabel dockerfileLabel;
    private javax.swing.JTextField dockerfileTextField;
    // End of variables declaration//GEN-END:variables
}
