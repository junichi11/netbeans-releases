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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.bpel.properties.editors;

import static org.netbeans.modules.bpel.properties.PropertyType.NAME;
import org.netbeans.modules.soa.ui.form.CustomNodeEditor;
import org.netbeans.modules.soa.ui.form.EditorLifeCycleAdapter;
import org.netbeans.modules.bpel.properties.editors.controls.MessageConfigurationController;
import org.netbeans.modules.bpel.properties.editors.controls.MessageExchangeController;
import org.netbeans.modules.soa.ui.form.valid.ValidStateManager;
import org.netbeans.modules.soa.ui.form.valid.ValidStateManager.ValidStateListener;

/**
 *
 * @author  nk160297
 */
public class OnEventMainPanel extends EditorLifeCycleAdapter {

    static final long serialVersionUID = 1L;

    private CustomNodeEditor myEditor;
    private MessageConfigurationController mcc;
    private MessageExchangeController mec;

    public OnEventMainPanel(CustomNodeEditor anEditor) {
        this.myEditor = anEditor;
        createContent();
    }
    
    public void createContent() {
        mcc = new MessageConfigurationController(myEditor);
        mcc.createContent();
        mcc.setVisibleVariables(false, false, true);
        mcc.useMyRole();
        //
        mec = new MessageExchangeController(myEditor);
        mec.createContent();
        //
        initComponents();
        //
        
        // Issue 85553 start
        lblMessageExchange.setVisible(false);
        btnChooseMessEx.setVisible(false);
        fldMessageExchange.setVisible(false);
        // Issue 85553 end        
        
        myEditor.getValidStateManager(true).addValidStateListener(
                new ValidStateListener() {
            public void stateChanged(ValidStateManager source, boolean isValid) {
                if (source.isValid()) {
                    lblErrorMessage.setText("");
                } else {
                    lblErrorMessage.setText(source.getHtmlReasons());
                }
            }
        });
    }
    
    public boolean initControls() {
        mcc.initControls();
        mec.initControls();
        return true;
    }
    
    public boolean subscribeListeners() {
        mcc.subscribeListeners();
        mec.subscribeListeners();
        return true;
    }
    
    public boolean unsubscribeListeners() {
        mcc.unsubscribeListeners();
        mec.unsubscribeListeners();
        return true;
    }
    
    public boolean applyNewValues() {
        mcc.applyNewValues();
        mec.applyNewValues();
        return true;
    }
    
    public boolean afterClose() {
        mcc.afterClose();
        mec.afterClose();
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblPartnerLink = new javax.swing.JLabel();
        cbxPartnerLink = mcc.getCbxPartnerLink();
        lblOperation = new javax.swing.JLabel();
        cbxOperation = mcc.getCbxOperation();
        lblErrorMessage = new javax.swing.JLabel();
        lblVariableName = new javax.swing.JLabel();
        fldVariableName = mcc.getFldVariableName();
        lblMessageExchange = new javax.swing.JLabel();
        fldMessageExchange = mec.getFldMessageExchange();
        btnChooseMessEx = mec.getBtnChooseMsgEx();

        lblPartnerLink.setLabelFor(cbxPartnerLink);
        lblPartnerLink.setText(org.openide.util.NbBundle.getMessage(FormBundle.class, "LBL_PartnerLink"));
        lblPartnerLink.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_PartnerLink"));
        lblPartnerLink.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_PartnerLink"));

        lblOperation.setLabelFor(cbxOperation);
        lblOperation.setText(org.openide.util.NbBundle.getMessage(FormBundle.class, "LBL_Operation"));
        lblOperation.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_Operation"));
        lblOperation.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_Operation"));

        lblErrorMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorMessage.setAlignmentX(0.5F);

        lblVariableName.setLabelFor(fldVariableName);
        lblVariableName.setText(org.openide.util.NbBundle.getMessage(FormBundle.class, "LBL_EventVariable"));
        lblVariableName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_EventVariable"));
        lblVariableName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_EventVariable"));

        lblMessageExchange.setLabelFor(fldMessageExchange);
        lblMessageExchange.setText(org.openide.util.NbBundle.getMessage(FormBundle.class,"LBL_MessageExchange"));
        lblMessageExchange.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_MessageExchange"));
        lblMessageExchange.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_MessageExchange"));

        btnChooseMessEx.setText(org.openide.util.NbBundle.getMessage(FormBundle.class,"BTN_ChooseMessageExchange"));
        btnChooseMessEx.setMargin(new java.awt.Insets(0, 4, 0, 4));
        btnChooseMessEx.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_BTN_ChooseMessageExchange"));
        btnChooseMessEx.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_BTN_ChooseMessageExchange"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblErrorMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblPartnerLink)
                            .add(lblOperation)
                            .add(lblVariableName))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbxOperation, 0, 351, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cbxPartnerLink, 0, 351, Short.MAX_VALUE))
                            .add(fldVariableName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(lblMessageExchange)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fldMessageExchange, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnChooseMessEx)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPartnerLink)
                    .add(cbxPartnerLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOperation)
                    .add(cbxOperation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fldVariableName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblVariableName))
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblMessageExchange)
                    .add(fldMessageExchange, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnChooseMessEx))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblErrorMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseMessEx;
    private javax.swing.JComboBox cbxOperation;
    private javax.swing.JComboBox cbxPartnerLink;
    private javax.swing.JTextField fldMessageExchange;
    private javax.swing.JTextField fldVariableName;
    private javax.swing.JLabel lblErrorMessage;
    private javax.swing.JLabel lblMessageExchange;
    private javax.swing.JLabel lblOperation;
    private javax.swing.JLabel lblPartnerLink;
    private javax.swing.JLabel lblVariableName;
    // End of variables declaration//GEN-END:variables
}
