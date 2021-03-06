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

package org.netbeans.modules.web.project.ui.customizer;


import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.netbeans.modules.javaee.project.api.ui.utils.J2eePlatformUiSupport;
import org.netbeans.modules.javaee.project.api.ui.utils.MessageUtils;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.InstanceRemovedException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerInstance;
import org.netbeans.modules.web.browser.api.BrowserUISupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

public class CustomizerRun extends JPanel implements HelpCtx.Provider {

    private final ProjectCustomizer.Category category;
    private final WebProjectProperties uiProperties;
    private final String oldServerInstanceId;
    
    /** Creates new form CustomizerRun */
    public CustomizerRun(ProjectCustomizer.Category category, WebProjectProperties uiProperties) {
        this.uiProperties = uiProperties;
        
        initComponents();

        this.category = category;
        
        this.oldServerInstanceId = uiProperties.J2EE_SERVER_INSTANCE_MODEL.getSelectedItem() != null
                ? J2eePlatformUiSupport.getServerInstanceID(uiProperties.J2EE_SERVER_INSTANCE_MODEL.getSelectedItem())
                : null;
        
        uiProperties.JAVAC_CLASSPATH_MODEL.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                setMessages();
            }
        });
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACS_CustomizeRun_A11YDesc")); //NOI18N

        jTextFieldContextPath.setDocument( uiProperties.CONTEXT_PATH_MODEL );
        jTextFieldRelativeURL.setDocument( uiProperties.LAUNCH_URL_RELATIVE_MODEL );
        vmOptions.setDocument(uiProperties.RUNMAIN_JVM_MODEL);
        uiProperties.DISPLAY_BROWSER_MODEL.setMnemonic( jCheckBoxDisplayBrowser.getMnemonic() );
        jCheckBoxDisplayBrowser.setModel( uiProperties.DISPLAY_BROWSER_MODEL ); 
        jCheckBoxDeployOnSave.setModel(uiProperties.DEPLOY_ON_SAVE_MODEL);
        jComboBoxServer.setModel( uiProperties.J2EE_SERVER_INSTANCE_MODEL );
        
        JTextField temp = new JTextField();
        temp.setDocument( uiProperties.J2EE_PLATFORM_MODEL );
        Profile j2eeProfile = Profile.fromPropertiesString(temp.getText().trim());
        if (j2eeProfile != null) {
            jTextFieldJ2EE_Display.setText(j2eeProfile.getDisplayName());
        }
        setDeployOnSaveState();
    }

    private JComboBox createBrowserComboBox() {
        JComboBox combo = BrowserUISupport.createBrowserPickerComboBox(
                uiProperties.BROWSERS_MODEL.getSelectedBrowserId(), true, false,
                uiProperties.BROWSERS_MODEL);
        return combo;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelServer = new javax.swing.JLabel();
        jComboBoxServer = new javax.swing.JComboBox();
        jLabelJ2EE = new javax.swing.JLabel();
        jTextFieldJ2EE_Display = new javax.swing.JTextField();
        jLabelContextPath = new javax.swing.JLabel();
        jTextFieldContextPath = new javax.swing.JTextField();
        jCheckBoxDisplayBrowser = new javax.swing.JCheckBox();
        errorLabel = new javax.swing.JLabel();
        jCheckBoxDeployOnSave = new javax.swing.JCheckBox();
        dosDescription = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        vmOptions = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabelURLExample = new javax.swing.JLabel();
        jBrowserCombo = createBrowserComboBox();
        jTextFieldRelativeURL = new javax.swing.JTextField();
        jLabelRelativeURL = new javax.swing.JLabel();
        jBrowserLabel = new javax.swing.JLabel();

        jLabelServer.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeRun_Server_LabelMnemonic").charAt(0));
        jLabelServer.setLabelFor(jComboBoxServer);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelServer, NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_Server_JLabel")); // NOI18N

        jComboBoxServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxServerActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabelJ2EE, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_J2EE_JLabel")); // NOI18N

        jTextFieldJ2EE_Display.setEditable(false);

        jLabelContextPath.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeRun_ContextPath_LabelMnemonic").charAt(0));
        jLabelContextPath.setLabelFor(jTextFieldContextPath);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelContextPath, NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_ContextPath_JLabel")); // NOI18N

        jTextFieldContextPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldContextPathKeyReleased(evt);
            }
        });

        jCheckBoxDisplayBrowser.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDisplayBrowser, NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_DisplayBrowser_JCheckBox")); // NOI18N
        jCheckBoxDisplayBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDisplayBrowserActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, " ");

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDeployOnSave, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_DeployOnSave_JCheckBox")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dosDescription, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_DeployOnSave_Description")); // NOI18N

        jLabel1.setLabelFor(vmOptions);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "Label_JVM_Argument")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "Label_VM_Hint")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(vmOptions)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(vmOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "ACSN_CustomizerRun_NA")); // NOI18N
        vmOptions.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "ACSN_CustomizerRun_NA")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "ACSN_CustomizerRun_NA")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabelURLExample, NbBundle.getMessage(CustomizerRun.class, "LBL_RelativeURLExample")); // NOI18N

        jLabelRelativeURL.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle").getString("LBL_CustomizeRun_RelativeURL_LabelMnemonic").charAt(0));
        jLabelRelativeURL.setLabelFor(jTextFieldRelativeURL);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelRelativeURL, NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_RelativeURL_JLabel")); // NOI18N

        jBrowserLabel.setLabelFor(jBrowserCombo);
        org.openide.awt.Mnemonics.setLocalizedText(jBrowserLabel, org.openide.util.NbBundle.getMessage(CustomizerRun.class, "LBL_CustomizeRun_Browser_Label")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jBrowserLabel)
                .addGap(35, 35, 35)
                .addComponent(jBrowserCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabelRelativeURL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelURLExample, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTextFieldRelativeURL)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRelativeURL)
                    .addComponent(jTextFieldRelativeURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(jLabelURLExample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBrowserLabel)
                    .addComponent(jBrowserCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabelURLExample.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CustomizerRun.class, "ACS_CustomizeRun_RelativeURLExample_A11YDesc")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/web/project/ui/customizer/Bundle"); // NOI18N
        jTextFieldRelativeURL.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_CustomizeRun_RelativeURL_A11YDesc")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelServer)
                            .addComponent(jLabelJ2EE)
                            .addComponent(jLabelContextPath))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldContextPath)
                            .addComponent(jTextFieldJ2EE_Display)
                            .addComponent(jComboBoxServer, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCheckBoxDisplayBrowser, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxDeployOnSave, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dosDescription)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelServer))
                    .addComponent(jComboBoxServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelJ2EE))
                    .addComponent(jTextFieldJ2EE_Display, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelContextPath))
                    .addComponent(jTextFieldContextPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxDisplayBrowser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDeployOnSave)
                .addGap(0, 0, 0)
                .addComponent(dosDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel))
        );

        jComboBoxServer.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_CustomizeRun_Server_A11YDesc")); // NOI18N
        jLabelJ2EE.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerRun.class, "ACSD_CustomizerRun_jLabelJ2EE")); // NOI18N
        jTextFieldContextPath.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_CustomizeRun_ContextPath_A11YDesc")); // NOI18N
        jCheckBoxDisplayBrowser.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_CustomizeRun_DisplayBrowser_A11YDesc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldContextPathKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldContextPathKeyReleased
        setMessages();
    }//GEN-LAST:event_jTextFieldContextPathKeyReleased

    private void jCheckBoxDisplayBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDisplayBrowserActionPerformed
        boolean editable = jCheckBoxDisplayBrowser.isSelected();
        
        jLabelRelativeURL.setEnabled(editable);
        jTextFieldRelativeURL.setEditable(editable);
        jTextFieldRelativeURL.setEnabled(editable);
        jLabelURLExample.setEnabled(editable);
        jBrowserLabel.setEnabled(editable);
        jBrowserCombo.setEnabled(editable);
    }//GEN-LAST:event_jCheckBoxDisplayBrowserActionPerformed

    private void jComboBoxServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxServerActionPerformed
        setDeployOnSaveState();
        setMessages();
    }//GEN-LAST:event_jComboBoxServerActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dosDescription;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JComboBox jBrowserCombo;
    private javax.swing.JLabel jBrowserLabel;
    private javax.swing.JCheckBox jCheckBoxDeployOnSave;
    private javax.swing.JCheckBox jCheckBoxDisplayBrowser;
    private javax.swing.JComboBox jComboBoxServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelContextPath;
    private javax.swing.JLabel jLabelJ2EE;
    private javax.swing.JLabel jLabelRelativeURL;
    private javax.swing.JLabel jLabelServer;
    private javax.swing.JLabel jLabelURLExample;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldContextPath;
    private javax.swing.JTextField jTextFieldJ2EE_Display;
    private javax.swing.JTextField jTextFieldRelativeURL;
    private javax.swing.JTextField vmOptions;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerRun.class);
    }

    private void setDeployOnSaveState() {
        if (uiProperties.J2EE_SERVER_INSTANCE_MODEL.getSelectedItem() != null) {
            String serverInstanceID = J2eePlatformUiSupport.getServerInstanceID(
                    uiProperties.J2EE_SERVER_INSTANCE_MODEL.getSelectedItem());

            J2eeModule module = uiProperties.getProject().getWebModule().getJ2eeModule();
            ServerInstance instance = Deployment.getDefault().getServerInstance(serverInstanceID);

            try {
                jCheckBoxDeployOnSave.setEnabled(instance.isDeployOnSaveSupported(module));
            } catch (InstanceRemovedException ex) {
                jCheckBoxDeployOnSave.setEnabled(false);
            }
        } else {
            jCheckBoxDeployOnSave.setEnabled(false);
        }
    }

    private void setMessages() {
        MessageUtils.clear(errorLabel);
        category.setValid(true);

        String message = contextPathValidation();
        if (message != null) {
            MessageUtils.setMessage(errorLabel, MessageUtils.MessageType.ERROR,
                    "<html>"+message+"</html>"); // NOI18N
            category.setValid(false);
            return;
        }

    }

    private String contextPathValidation() {
        String contextPath = jTextFieldContextPath.getText();
        String message = null;
        if (contextPath.length() > 0) {
            if (!contextPath.startsWith("/")) { // NOI18N
                message = NbBundle.getMessage (CustomizerRun.class, "MSG_INVALID_CP_DOES_NOT_START_WITH_SLASH"); //NOI18N
            } else if (contextPath.indexOf("//") >= 0) { // NOI18N
                message = NbBundle.getMessage (CustomizerRun.class, "MSG_INVALID_CP_CONTAINS_DOUBLE_SLASH"); //NOI18N
            } else if (contextPath.endsWith("/")) { // NOI18N
                message = NbBundle.getMessage (CustomizerRun.class, "MSG_INVALID_CP_ENDS_WITH_SLASH"); //NOI18N
            }
        }
        return message;
    }

}
