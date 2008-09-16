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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ant.freeform.ui;

import org.netbeans.modules.ant.freeform.FreeformProjectGenerator;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milan Kubec
 */
public class GeneralInformationPanel extends javax.swing.JPanel {
    
    /** Creates new form GeneralInformationPanel */
    public GeneralInformationPanel(AntProjectHelper helper, PropertyEvaluator evaluator) {
        initComponents();
        projectFolderTextField.setText(FileUtil.getFileDisplayName(helper.getProjectDirectory()));
        FileObject baseFolderFO = FileUtil.toFileObject(Util.getProjectLocation(helper, evaluator));
        contentsFolderTextField.setText(FileUtil.getFileDisplayName(baseFolderFO));
        FileObject antScriptFO = FreeformProjectGenerator.getAntScript(helper, evaluator);
        if (antScriptFO != null) {
            buildScriptTextField.setText(FileUtil.getFileDisplayName(antScriptFO));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelLabel = new javax.swing.JLabel();
        projectFolderLabel = new javax.swing.JLabel();
        contentsFolderLabel = new javax.swing.JLabel();
        buildScriptLabel = new javax.swing.JLabel();
        buildScriptTextField = new javax.swing.JTextField();
        contentsFolderTextField = new javax.swing.JTextField();
        projectFolderTextField = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(600, 500));
        setLayout(new java.awt.GridBagLayout());

        panelLabel.setText(org.openide.util.NbBundle.getMessage(GeneralInformationPanel.class, "GeneralInformationPanel.panelLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(panelLabel, gridBagConstraints);

        projectFolderLabel.setText(org.openide.util.NbBundle.getMessage(GeneralInformationPanel.class, "GeneralInformationPanel.projectFolderLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(projectFolderLabel, gridBagConstraints);

        contentsFolderLabel.setText(org.openide.util.NbBundle.getMessage(GeneralInformationPanel.class, "GeneralInformationPanel.contentsFolderLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(contentsFolderLabel, gridBagConstraints);

        buildScriptLabel.setText(org.openide.util.NbBundle.getMessage(GeneralInformationPanel.class, "GeneralInformationPanel.buildScriptLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(buildScriptLabel, gridBagConstraints);

        buildScriptTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(buildScriptTextField, gridBagConstraints);

        contentsFolderTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(contentsFolderTextField, gridBagConstraints);

        projectFolderTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 0);
        add(projectFolderTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel buildScriptLabel;
    private javax.swing.JTextField buildScriptTextField;
    private javax.swing.JLabel contentsFolderLabel;
    private javax.swing.JTextField contentsFolderTextField;
    private javax.swing.JLabel panelLabel;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JTextField projectFolderTextField;
    // End of variables declaration//GEN-END:variables
    
}
