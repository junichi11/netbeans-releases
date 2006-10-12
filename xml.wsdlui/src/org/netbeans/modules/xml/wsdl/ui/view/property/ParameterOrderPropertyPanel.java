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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.wsdl.ui.view.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListModel;

import org.netbeans.modules.xml.wsdl.model.Input;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.Output;
import org.netbeans.modules.xml.wsdl.model.Part;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.openide.awt.Mnemonics;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.NbBundle;

/**
 *
 * @author  skini
 */
public class ParameterOrderPropertyPanel extends javax.swing.JPanel {
    
    /**
     * Creates new form ParameterOrderPropertyPanel
     */
    public ParameterOrderPropertyPanel(Operation operation, PropertyEnv env) {
        mOperation = operation;
        mSelectedParts = new Vector<PartDelegate>();
        mParts = getAllMessageParts(mSelectedParts);
        mEnv = env;
        mEnv.setState(PropertyEnv.STATE_INVALID);
        initComponents();
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.Form.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.Form.AccessibleContext.accessibleDescription")); // NOI18N
        getAccessibleContext().setAccessibleParent(this);
        jLabel1.setLabelFor(jList2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jLabel1.toolTipText")); // NOI18N

        jLabel2.setLabelFor(jList1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jLabel2.toolTipText")); // NOI18N

        jList1.setModel(new DefaultComboBoxModel(mParts));
        jList1.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList1.toolTipText")); // NOI18N
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(jList1);
        jList1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList1.AccessibleContext.accessibleName")); // NOI18N
        jList1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList1.toolTipText")); // NOI18N

        jList2.setModel(new DefaultComboBoxModel(mSelectedParts));
        jList2.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList2.toolTipText")); // NOI18N
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });

        jScrollPane2.setViewportView(jList2);
        jList2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList2.AccessibleContext.accessibleName")); // NOI18N
        jList2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jList2.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton3.text")); // NOI18N
        jButton3.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton3.toolTipText")); // NOI18N
        jButton3.setDefaultCapable(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton4.text")); // NOI18N
        jButton4.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton4.toolTipText")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton1.text")); // NOI18N
        jButton1.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton1.toolTipText")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton2.text")); // NOI18N
        jButton2.setToolTipText(NbBundle.getMessage(ParameterOrderPropertyPanel.class, "ParameterOrderPropertyPanel.jButton2.toolTipText")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                    .add(jLabel2))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2))
                    .add(layout.createSequentialGroup()
                        .add(58, 58, 58)
                        .add(jButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton4)
                        .add(20, 20, 20)
                        .add(jButton2)))
                .add(11, 11, 11)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1)
                    .add(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        enableOKButton();
        int[] selectedIndices = jList2.getSelectedIndices();
        for (int i = 0; i < selectedIndices.length; i++) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) jList2.getModel();
            Object removedObj = model.getElementAt(selectedIndices[i]);
            model.removeElementAt(selectedIndices[i]);
            selectedIndices[i]++;
            model.insertElementAt(removedObj, selectedIndices[i]);
        }
        jList2.setSelectedIndices(selectedIndices);
    }//GEN-LAST:event_jButton4ActionPerformed
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        enableOKButton();
        int[] selectedIndices = jList2.getSelectedIndices();
        for (int i = 0; i < selectedIndices.length; i++) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) jList2.getModel();
            Object removedObj = model.getElementAt(selectedIndices[i]);
            model.removeElementAt(selectedIndices[i]);
            selectedIndices[i]--;
            model.insertElementAt(removedObj, selectedIndices[i]);
        }
        jList2.setSelectedIndices(selectedIndices);
    }//GEN-LAST:event_jButton3ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        enableOKButton();
        Object[] selectedObjects = jList2.getSelectedValues();
        int[] selectedIndices = new int[selectedObjects.length];
        int count = 0;
        for (Object selectedObject : selectedObjects) {
            ((DefaultComboBoxModel) jList2.getModel()).removeElement(selectedObject);
            ((DefaultComboBoxModel) jList1.getModel()).addElement(selectedObject);
            selectedIndices[count++] = jList1.getModel().getSize() - 1;
        }
        jList1.setSelectedIndices(selectedIndices);
        
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        enableOKButton();
        Object[] selectedObjects = jList1.getSelectedValues();
        int[] selectedIndices = new int[selectedObjects.length];
        int count = 0;
        for (Object selectedObject : selectedObjects) {
            ((DefaultComboBoxModel) jList1.getModel()).removeElement(selectedObject);
            ((DefaultComboBoxModel) jList2.getModel()).addElement(selectedObject);
            selectedIndices[count++] = jList2.getModel().getSize() - 1;
        }
        jList2.setSelectedIndices(selectedIndices);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        JList list = (JList) evt.getSource();
        if (!list.getValueIsAdjusting()) {
            int[] si = list.getSelectedIndices();
            if (si != null && si.length > 0) {
                jButton2.setEnabled(true);
                jButton3.setEnabled(true);
                jButton4.setEnabled(true);
            } else {
                jButton2.setEnabled(false);
                jButton3.setEnabled(false);
                jButton4.setEnabled(false);
            }
            
            if (si.length > 0) {
                
                if (si[si.length - 1] < list.getModel().getSize() - 1) {
                    jButton4.setEnabled(true);
                } else {
                    jButton4.setEnabled(false);
                }
                
                if (si[0] > 0) {
                    jButton3.setEnabled(true);
                } else {
                    jButton3.setEnabled(false);
                }
                
            }
        }
    }//GEN-LAST:event_jList2ValueChanged
    
    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        JList list = (JList) evt.getSource();
        if (!list.getValueIsAdjusting()) {
            Object[] sv = list.getSelectedValues();
            if (sv != null && sv.length > 0) {
                jButton1.setEnabled(true);
            } else {
                jButton1.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jList1ValueChanged
    
    
    
    private void enableOKButton() {
        mEnv.setState(PropertyEnv.STATE_VALID);
    }
    
    /** Override method to detect the OK button */
    @Override
            public void removeNotify() {
        if (mEnv.getState().equals(PropertyEnv.STATE_VALID)) {
            ListModel model = jList2.getModel();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < model.getSize(); i++) {
                PartDelegate delegate = (PartDelegate) model.getElementAt(i);
                strBuf.append(delegate.toString()).append(" ");
            }
            this.firePropertyChange(ParameterOrderPropertyEditor.PROP_NAME, null, strBuf.toString().trim());
        }
        super.removeNotify();
    }
    
    
    private Vector<PartDelegate> getAllMessageParts(Vector<PartDelegate> selectedParameters) {
        Set<PartDelegate> allParts = new HashSet<PartDelegate>();
        
        Input input = mOperation.getInput();
        Output output = this.mOperation.getOutput();
        if(input != null) {
            NamedComponentReference<Message> messageRef = input.getMessage();
            if(messageRef != null && messageRef.get() != null) {
                getAllMessageParts(messageRef.get(), allParts);
            }
        }
        
        if(output != null) {
            NamedComponentReference<Message> messageRef = output.getMessage();
            if(messageRef != null && messageRef.get() != null) {
                getAllMessageParts(messageRef.get(), allParts);
            }
        }
        
        //REmove the ones that are already selected
        selectedParameters.clear();
        List<String> parameterOrder = mOperation.getParameterOrder();
        if (parameterOrder != null) {
            Set<String> set = new HashSet<String>(parameterOrder);
            for (PartDelegate part : allParts) {
                if (set.contains(part.toString())) {
                    selectedParameters.add(part);
                }
            }
            allParts.removeAll(selectedParameters);
        }
        
        return new Vector<PartDelegate>(allParts);
    }
    
    private void getAllMessageParts(Message message, Set<PartDelegate> allParts) {
        
        Collection<Part> parts = message.getParts();
        if (parts != null) {
            for (Part part : parts) {
                PartDelegate pd = new PartDelegate(part);
                allParts.add(pd);
            }
        }
    }
    
    class PartDelegate {
        Part mPart;
       
        PartDelegate(Part part) {
            this.mPart = part;
        }
        
        public Part getPart() {
            return this.mPart;
        }
        
        @Override
        public String toString() {
            return this.mPart.getName();
        }

        @Override
        public int hashCode() {
            return mPart.getName().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            
            return mPart.getName().equals(((PartDelegate)obj).toString());
        }
        
        
    }
    
    private Operation mOperation;
    private Vector<PartDelegate> mParts;
    private Vector<PartDelegate> mSelectedParts;
    private PropertyEnv mEnv;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
    
}
