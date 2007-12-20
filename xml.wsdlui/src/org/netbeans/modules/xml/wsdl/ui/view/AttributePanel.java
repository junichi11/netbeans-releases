/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * AttributePanel.java
 *
 * Created on June 9, 2006, 4:03 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.namespace.QName;

import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.ui.view.common.CommonMessagePanel;
import org.openide.util.NbBundle;

/**
 *
 * @author  skini
 */
public class AttributePanel extends javax.swing.JPanel {
    
    /**
     *
     */
    private static final long serialVersionUID = 1680305470367882467L;
    /**
     * Creates new form AttributePanel
     */
    public AttributePanel(boolean isNamespaceRequired, Vector namespaces, WSDLComponent component) {
        this.isNamespaceRequired =isNamespaceRequired;
        this.mNamespaces = namespaces;
        this.mComponent = component;
        initComponents();
        initCommonMessagePanel();
    }
    
    private void initCommonMessagePanel() {
    	mErrorPanel = new CommonMessagePanel();
        mCommonMsgPanel.add(mErrorPanel, BorderLayout.CENTER);
        DocumentListener listener = new AttributeDocumentListener();
        mAttrNameTextField.getDocument().addDocumentListener(listener);
        ((JTextField)mAttrNamespaceComboBox.getEditor().getEditorComponent()).getDocument().addDocumentListener(listener);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mAttrNameLabel = new javax.swing.JLabel();
        mAttrNamespaceLabel = new javax.swing.JLabel();
        mAttrNameTextField = new javax.swing.JTextField();
        mAttrNamespaceComboBox = new javax.swing.JComboBox();
        mCommonMsgPanel = new javax.swing.JPanel();

        setName("Form"); // NOI18N

        mAttrNameLabel.setLabelFor(mAttrNameTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/xml/wsdl/ui/view/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(mAttrNameLabel, bundle.getString("LBL_AttributePanel_NAME")); // NOI18N
        mAttrNameLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mAttrNameLabel.toolTipText")); // NOI18N
        mAttrNameLabel.setName("mAttrNameLabel"); // NOI18N

        mAttrNamespaceLabel.setLabelFor(mAttrNamespaceComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(mAttrNamespaceLabel, bundle.getString("LBL_AttributePanel_NAMESPACE")); // NOI18N
        mAttrNamespaceLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mAttrNamespaceLabel.toolTipText")); // NOI18N
        mAttrNamespaceLabel.setName("mAttrNamespaceLabel"); // NOI18N

        mAttrNameTextField.setToolTipText(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mAttrNameTextField.toolTipText")); // NOI18N
        mAttrNameTextField.setName("mAttrNameTextField"); // NOI18N

        mAttrNamespaceComboBox.setEditable(true);
        mAttrNamespaceComboBox.setModel(new DefaultComboBoxModel(mNamespaces));
        mAttrNamespaceComboBox.setToolTipText(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mAttrNamespaceComboBox.toolTipText")); // NOI18N
        mAttrNamespaceComboBox.setName("mAttrNamespaceComboBox"); // NOI18N

        mCommonMsgPanel.setToolTipText(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mCommonMsgPanel.toolTipText")); // NOI18N
        mCommonMsgPanel.setName("mCommonMsgPanel"); // NOI18N
        mCommonMsgPanel.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mCommonMsgPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mAttrNameLabel)
                            .add(mAttrNamespaceLabel))
                        .add(16, 16, 16)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mAttrNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .add(mAttrNamespaceComboBox, 0, 305, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mAttrNameLabel)
                    .add(mAttrNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mAttrNamespaceLabel)
                    .add(mAttrNamespaceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(mCommonMsgPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mCommonMsgPanel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AttributePanel.class, "AttributePanel.mCommonMsgPanel.toolTipText")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    public String getAttributeNamespace() {
        JTextField editor = (JTextField) this.mAttrNamespaceComboBox.getEditor().getEditorComponent();
        return editor.getText();
    }
    
    public String getAttributeNameFromTextField() {
        return this.mAttrNameTextField.getText();
    }
    
    public JTextField getAttributeNameTextField() {
        return this.mAttrNameTextField;
    }
    
    public JComboBox getNamespaceComboBox() {
        return this.mAttrNamespaceComboBox;
    }
    
    public boolean isStateValid() {
    	return mErrorPanel.isStateValid();
    }
    
    private boolean validateAttributeNameAndNamespace() {
        boolean valid = true;
        
        String attrName = getAttributeNameFromTextField();
        String namespace = getAttributeNamespace();
        if(attrName == null || attrName.trim().equals("")) {
            valid = false;
            
            mErrorPanel.setErrorMessage(NbBundle.getMessage(getClass(),
                    "AttributeView_COMMON_MSG_ERROR_ATTRIBUTE_NAME_NOT_VALID", attrName));
            
            this.firePropertyChange(STATE_CHANGED,
                    true, false);
            
        } else if(isNamespaceRequired && (namespace == null || namespace.trim().equals(""))) {
            valid = false;
            
            mErrorPanel.setErrorMessage(NbBundle.getMessage(getClass(),
                    "AttributeView_COMMON_MSG_ERROR_NAMESPACE_IS_NOT_VALID", namespace));
            
            this.firePropertyChange(STATE_CHANGED,
                    true, false);
            
        } else {
            mNamespace = namespace;
            
            
            QName attributeQName = new QName(namespace, attrName);
            if(mComponent.getAttributeMap().get(attributeQName) != null) {
                valid = false;
                
                mErrorPanel.setErrorMessage(NbBundle.getMessage(getClass(),
                        "AttributeView_COMMON_MSG_ERROR_ATTRIBUTE_ALREADY_EXIST", attributeQName.toString()));
                
                this.firePropertyChange(STATE_CHANGED,
                        true, false);
                
            } else {
                mAttributeName = attrName;
            }
        }
        
        if(valid) {
            mErrorPanel.setMessage("");
            this.firePropertyChange(STATE_CHANGED,
                    false, true);
        }
        
        return valid;
    }
    
    class AttributeDocumentListener implements DocumentListener {
        
        
        
        public void insertUpdate(DocumentEvent e) {
            validateAttributeNameAndNamespace();
        }
        
        
        public void removeUpdate(DocumentEvent e) {
            validateAttributeNameAndNamespace();
        }
        
        
        public void changedUpdate(DocumentEvent e) {
            validateAttributeNameAndNamespace();
        }
        
    }
    public String getNamespace() {
    	return mNamespace;
    }
    
    public String getAttributeName() {
    	return mAttributeName;
    }
    public static String STATE_CHANGED = "STATE_CHANGED";//NOI18N
    private CommonMessagePanel mErrorPanel;
    private String mNamespace;
    private String mAttributeName;
    private Vector mNamespaces;
    private boolean isNamespaceRequired = false;
    private WSDLComponent mComponent;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel mAttrNameLabel;
    private javax.swing.JTextField mAttrNameTextField;
    private javax.swing.JComboBox mAttrNamespaceComboBox;
    private javax.swing.JLabel mAttrNamespaceLabel;
    private javax.swing.JPanel mCommonMsgPanel;
    // End of variables declaration//GEN-END:variables
    
}
