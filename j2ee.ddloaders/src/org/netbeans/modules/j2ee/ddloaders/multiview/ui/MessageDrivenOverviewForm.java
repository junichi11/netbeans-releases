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

package org.netbeans.modules.j2ee.ddloaders.multiview.ui;

import org.netbeans.modules.xml.multiview.Refreshable;
import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

import javax.swing.*;

/**
 * @author pfiala
 */
public class MessageDrivenOverviewForm extends SectionNodeInnerPanel {

    private static final String ACKNOWLEDGE_MODE_AUTO = "Auto-acknowledge"; //NOI18N
    private static final String ACKNOWLEDGE_MODE_DUPS_OK = "Dups-ok-acknowledge"; //NOI18N
    /**
     * Creates new form MessageDrivenOverviewForm
     *
     * @param sectionNodeView enclosing SectionNodeView object
     */
    public MessageDrivenOverviewForm(SectionNodeView sectionNodeView) {
        super(sectionNodeView);
        initComponents();
        beanTransactionTypeRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, SessionOverviewForm.TRANSACTION_TYPE_BEAN);
        containerTransactionTypeRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, SessionOverviewForm.TRANSACTION_TYPE_CONTAINER);
        autoAckModeRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, ACKNOWLEDGE_MODE_AUTO);
        dupsOkAckModeRadioButton.putClientProperty(Refreshable.PROPERTY_FIXED_VALUE, ACKNOWLEDGE_MODE_DUPS_OK);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        transactionTypeButtonGroup = new javax.swing.ButtonGroup();
        acknowledgeModeButtonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spacerLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        beanTransactionTypeRadioButton = new javax.swing.JRadioButton();
        containerTransactionTypeRadioButton = new javax.swing.JRadioButton();
        messageSelectorTextField = new javax.swing.JTextField();
        autoAckModeRadioButton = new javax.swing.JRadioButton();
        dupsOkAckModeRadioButton = new javax.swing.JRadioButton();
        destinationTypeComboBox = new javax.swing.JComboBox();
        durabilityComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(nameTextField);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_EjbName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_TransactionType")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel2, gridBagConstraints);

        jLabel3.setLabelFor(messageSelectorTextField);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_MessageSelector")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_AcknowledgeMode")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel4, gridBagConstraints);

        jLabel5.setLabelFor(destinationTypeComboBox);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_DestinationType")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel5, gridBagConstraints);

        jLabel6.setLabelFor(durabilityComboBox);
        jLabel6.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_Durability")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 6);
        add(jLabel6, gridBagConstraints);

        spacerLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(spacerLabel, gridBagConstraints);

        nameTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(nameTextField, gridBagConstraints);
        nameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_EJB_Name")); // NOI18N

        transactionTypeButtonGroup.add(beanTransactionTypeRadioButton);
        beanTransactionTypeRadioButton.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_Bean")); // NOI18N
        beanTransactionTypeRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(beanTransactionTypeRadioButton, gridBagConstraints);
        beanTransactionTypeRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_TX_Bean")); // NOI18N

        transactionTypeButtonGroup.add(containerTransactionTypeRadioButton);
        containerTransactionTypeRadioButton.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_Container")); // NOI18N
        containerTransactionTypeRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(containerTransactionTypeRadioButton, gridBagConstraints);
        containerTransactionTypeRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_TX_Container")); // NOI18N

        messageSelectorTextField.setColumns(25);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(messageSelectorTextField, gridBagConstraints);
        messageSelectorTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_Msg_Selector")); // NOI18N

        acknowledgeModeButtonGroup.add(autoAckModeRadioButton);
        autoAckModeRadioButton.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_Auto")); // NOI18N
        autoAckModeRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(autoAckModeRadioButton, gridBagConstraints);
        autoAckModeRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_ACK_Auto")); // NOI18N

        acknowledgeModeButtonGroup.add(dupsOkAckModeRadioButton);
        dupsOkAckModeRadioButton.setText(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "LBL_DupsOk")); // NOI18N
        dupsOkAckModeRadioButton.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(dupsOkAckModeRadioButton, gridBagConstraints);
        dupsOkAckModeRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_ACK_DupsOK")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(destinationTypeComboBox, gridBagConstraints);
        destinationTypeComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_Destination_Type")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(durabilityComboBox, gridBagConstraints);
        durabilityComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MessageDrivenOverviewForm.class, "ACSD_Durability")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup acknowledgeModeButtonGroup;
    private javax.swing.JRadioButton autoAckModeRadioButton;
    private javax.swing.JRadioButton beanTransactionTypeRadioButton;
    private javax.swing.JRadioButton containerTransactionTypeRadioButton;
    private javax.swing.JComboBox destinationTypeComboBox;
    private javax.swing.JRadioButton dupsOkAckModeRadioButton;
    private javax.swing.JComboBox durabilityComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField messageSelectorTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel spacerLabel;
    private javax.swing.ButtonGroup transactionTypeButtonGroup;
    // End of variables declaration//GEN-END:variables

    public void setValue(JComponent source, Object value) {
    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {
    }

    public JComponent getErrorComponent(String errorId) {
        return null;
    }

    public ButtonGroup getAcknowledgeModeButtonGroup() {
        return acknowledgeModeButtonGroup;
    }

    public JRadioButton getAutoAckModeRadioButton() {
        return autoAckModeRadioButton;
    }

    public JRadioButton getBeanTransactionTypeRadioButton() {
        return beanTransactionTypeRadioButton;
    }

    public JRadioButton getContainerTransactionTypeRadioButton() {
        return containerTransactionTypeRadioButton;
    }

    public JComboBox getDestinationTypeComboBox() {
        return destinationTypeComboBox;
    }

    public JRadioButton getDupsOkAckModeRadioButton() {
        return dupsOkAckModeRadioButton;
    }

    public JComboBox getDurabilityComboBox() {
        return durabilityComboBox;
    }

    public JTextField getMessageSelectorTextField() {
        return messageSelectorTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public ButtonGroup getTransactionTypeButtonGroup() {
        return transactionTypeButtonGroup;
    }
}
