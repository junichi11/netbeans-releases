/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx2.samples;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.*;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.PlatformsCustomizer;
import org.netbeans.modules.javafx2.platform.api.JavaFXPlatformUtils;
import org.netbeans.modules.javafx2.project.api.JavaFXProjectUtils;
import org.openide.WizardDescriptor;
import org.openide.util.*;


/**
 * @author Anton Chechel 
 */
// TODO mnemonics
public class PanelOptionsVisual extends JPanel {

    private static final Logger LOGGER = Logger.getLogger("javafx"); // NOI18N

    private PanelConfigureProject panel;

    private ComboBoxModel platformsModel;
    private ListCellRenderer platformsCellRenderer;
    private JavaPlatformChangeListener jpcl;


    /** Creates new form PanelOptionsVisual */
    PanelOptionsVisual(PanelConfigureProject panel) {
        this.panel = panel;

        preInitComponents();
        initComponents();
        postInitComponents();
    }

    private void preInitComponents() {
        platformsModel = JavaFXProjectUtils.createPlatformComboBoxModel();
        platformsCellRenderer = JavaFXProjectUtils.createPlatformListCellRenderer();
    }
    
    private void postInitComponents() {
        // copied from CustomizerLibraries
        if (!UIManager.getLookAndFeel().getClass().getName().toUpperCase().contains("AQUA")) {  //NOI18N
            platformComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE); // NOI18N
        }
        jpcl = new JavaPlatformChangeListener();
        JavaPlatformManager.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(jpcl, JavaPlatformManager.getDefault()));
        
        selectJavaFXEnabledPlatform();
        progressLabel.setVisible(false);
        progressPanel.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPlatform = new javax.swing.JLabel();
        platformComboBox = new javax.swing.JComboBox();
        btnManagePlatforms = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 100), new java.awt.Dimension(0, 0));
        progressLabel = new javax.swing.JLabel();
        progressPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        lblPlatform.setLabelFor(platformComboBox);
        lblPlatform.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_Platform_ComboBox")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 12);
        add(lblPlatform, gridBagConstraints);
        lblPlatform.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_labelPlatform")); // NOI18N
        lblPlatform.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_labelPlatform")); // NOI18N

        platformComboBox.setModel(platformsModel);
        platformComboBox.setRenderer(platformsCellRenderer);
        platformComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                platformComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_LEADING;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(platformComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnManagePlatforms, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_PanelOptions_Manage_Button")); // NOI18N
        btnManagePlatforms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManagePlatformsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE_TRAILING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        add(btnManagePlatforms, gridBagConstraints);
        btnManagePlatforms.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_buttonManagePlatforms")); // NOI18N
        btnManagePlatforms.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_buttonManagePlatforms")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.1;
        add(filler1, gridBagConstraints);

        progressLabel.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_Platform_Progress")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        add(progressLabel, gridBagConstraints);
        progressLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_platformProgress")); // NOI18N
        progressLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_platformProgress")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        add(progressPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnManagePlatformsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManagePlatformsActionPerformed
        PlatformsCustomizer.showCustomizer(getSelectedPlatform());
    }//GEN-LAST:event_btnManagePlatformsActionPerformed

    private void platformComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_platformComboBoxItemStateChanged
        this.panel.fireChangeEvent();
    }//GEN-LAST:event_platformComboBoxItemStateChanged
    
    private JavaPlatform getSelectedPlatform() {
        Object selectedItem = this.platformComboBox.getSelectedItem();
        JavaPlatform platform = (selectedItem == null ? null : JavaFXProjectUtils.getPlatform(selectedItem));
        return platform;
    }

    private void selectJavaFXEnabledPlatform() {
        for (int i = 0; i < platformsModel.getSize(); i++) {
            JavaPlatform platform = JavaFXProjectUtils.getPlatform(platformsModel.getElementAt(i));
            if (JavaFXPlatformUtils.isJavaFXEnabled(platform)) {
                platformComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    boolean valid(WizardDescriptor wizardDescriptor) {
        if (!JavaFXPlatformUtils.isJavaFXEnabled(getSelectedPlatform())) {
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(PanelOptionsVisual.class, "WARN_PanelOptionsVisual.notFXPlatform")); // NOI18N
            return false;
        }
        return true;
    }

    void store(WizardDescriptor d) {
        String platformName = getSelectedPlatform().getProperties().get(JavaFXPlatformUtils.PLATFORM_ANT_NAME);
        d.putProperty(JavaFXProjectUtils.PROP_JAVA_PLATFORM_NAME, platformName);
    }

    void read(WizardDescriptor d) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnManagePlatforms;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblPlatform;
    private javax.swing.JComboBox platformComboBox;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JPanel progressPanel;
    // End of variables declaration//GEN-END:variables
    
    private class JavaPlatformChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            PanelOptionsVisual.this.panel.fireChangeEvent();
        }
    }
    
}

