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
 * NamespacePanel.java
 *
 * Created on December 20, 2005, 5:49 PM
 */

package org.netbeans.modules.xml.schema.ui.basic.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
public class NamespacePanel extends javax.swing.JPanel
        implements ActionListener, ListSelectionListener, DocumentListener {

    static final long serialVersionUID = 1L;
    public static final String PROP_VALID_SELECTION="validSelection";
    private boolean isValid=true;
    /** Creates new form NotationSystemForm */
    public NamespacePanel(String currentTns, Collection<String> uris, Collection<NamespaceEditor.Option> options) {
        initComponents();
        initialize(currentTns,uris,options);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        noNamespaceRadioButton = new javax.swing.JRadioButton();
        schemaNamespaceRadioButton = new javax.swing.JRadioButton();
        otherNamespaceRadioButton = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        uriList = new javax.swing.JList();
        otherNamespaceTextField = new javax.swing.JTextField();
        dummyLabel1 = new javax.swing.JLabel();
        dummyLabel2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(noNamespaceRadioButton, org.openide.util.NbBundle.getMessage(NamespacePanel.class, "LBL_NoNamespace"));
        noNamespaceRadioButton.setToolTipText(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "HINT_NoNamespace"));
        noNamespaceRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        noNamespaceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonGroup1.add(noNamespaceRadioButton);
        noNamespaceRadioButton.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(schemaNamespaceRadioButton, org.openide.util.NbBundle.getMessage(NamespacePanel.class, "LBL_SchemaNamespace"));
        schemaNamespaceRadioButton.setToolTipText(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "HINT_SchemaNamespace"));
        schemaNamespaceRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        schemaNamespaceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonGroup1.add(schemaNamespaceRadioButton);
        schemaNamespaceRadioButton.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(otherNamespaceRadioButton, org.openide.util.NbBundle.getMessage(NamespacePanel.class, "LBL_OtherNamespace"));
        otherNamespaceRadioButton.setToolTipText(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "HINT_OtherNamespace"));
        otherNamespaceRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        otherNamespaceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonGroup1.add(otherNamespaceRadioButton);
        otherNamespaceRadioButton.addActionListener(this);

        uriList.addListSelectionListener(this);
        jScrollPane1.setViewportView(uriList);

        otherNamespaceTextField.setText(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "TXT_defaultTNS"));
        otherNamespaceTextField.getDocument().addDocumentListener(this);
        otherNamespaceTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "HINT_OtherNamespace"));
        otherNamespaceTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(NamespacePanel.class, "HINT_OtherNamespace"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(otherNamespaceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(schemaNamespaceRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dummyLabel2))
                    .add(otherNamespaceRadioButton)
                    .add(layout.createSequentialGroup()
                        .add(noNamespaceRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dummyLabel1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(noNamespaceRadioButton)
                    .add(dummyLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(schemaNamespaceRadioButton)
                    .add(dummyLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(otherNamespaceRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(otherNamespaceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void initialize(String currentTns, final Collection<String> uris, Collection<NamespaceEditor.Option> options) {
        // hide radio button for uri list and uri list
        if (uris==null || uris.isEmpty()) {
            schemaNamespaceRadioButton.setVisible(false);
            uriList.setVisible(false);
            jScrollPane1.setVisible(false);
        } else {
            uriList.setListData(uris.toArray(new String[uris.size()]));
        }
        // hide other radio buttons if needed
        if(!options.contains(NamespaceEditor.Option.None)) {
            noNamespaceRadioButton.setSelected(false);
            noNamespaceRadioButton.setVisible(false);
        }
        if(currentTns==null || currentTns.trim().length()==0) {
            uriList.setEnabled(false);
            if(noNamespaceRadioButton.isVisible()) {
                noNamespaceRadioButton.setSelected(true);
                otherNamespaceTextField.setEnabled(false);
            } else if (otherNamespaceRadioButton.isVisible()) {
                otherNamespaceRadioButton.setSelected(true);
                otherNamespaceTextField.setEnabled(true);
            }
        } else if(uris!=null && uris.contains(currentTns)) {
            schemaNamespaceRadioButton.setSelected(true);
            uriList.setSelectedValue(currentTns,true);
            uriList.setEnabled(true);
            otherNamespaceTextField.setEnabled(false);
        } else {
            uriList.setEnabled(false);
            if(otherNamespaceRadioButton.isVisible()) {
                otherNamespaceRadioButton.setSelected(true);
                otherNamespaceTextField.setText(currentTns);
                otherNamespaceTextField.setEnabled(true);
            }
        }
    }
    
    public void checkValidity() {
        boolean newValue = false;
        if(noNamespaceRadioButton.isSelected()) {
            newValue = true;
        } else if(schemaNamespaceRadioButton.isSelected()) {
            newValue = uriList.getSelectedValue()!=null;
        } else if(otherNamespaceRadioButton.isSelected()) {
            newValue = !otherNamespaceTextField.getText().equals("");
        }
        firePropertyChange(PROP_VALID_SELECTION,isValid,newValue);
        isValid = newValue;
    }

    public String getCurrentSelection() {
        if(schemaNamespaceRadioButton.isSelected()) {
            return (String) uriList.getSelectedValue();
        } else if (otherNamespaceRadioButton.isSelected()){
            String otherUri = otherNamespaceTextField.getText().trim();
            if (otherUri.length()>0) return otherUri;
        }
        return null;
    }

    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if(source.equals(noNamespaceRadioButton)) {
            uriList.setEnabled(false);
            otherNamespaceTextField.setEnabled(false);
        } else if(source.equals(otherNamespaceRadioButton)){
            uriList.setEnabled(false);
            otherNamespaceTextField.setEnabled(true);
        } else if(source.equals(schemaNamespaceRadioButton)) {
            otherNamespaceTextField.setEnabled(false);
            uriList.setEnabled(true);
        }
        checkValidity();
    }

    public void valueChanged(ListSelectionEvent e) {
        checkValidity();
    }

    public void insertUpdate(DocumentEvent e) {
        checkValidity();
    }

    public void removeUpdate(DocumentEvent e) {
        checkValidity();
    }

    public void changedUpdate(DocumentEvent e) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JLabel dummyLabel1;
    public javax.swing.JLabel dummyLabel2;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JRadioButton noNamespaceRadioButton;
    public javax.swing.JRadioButton otherNamespaceRadioButton;
    public javax.swing.JTextField otherNamespaceTextField;
    public javax.swing.JRadioButton schemaNamespaceRadioButton;
    public javax.swing.JList uriList;
    // End of variables declaration//GEN-END:variables

}
