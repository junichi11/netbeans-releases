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

package org.netbeans.modules.httpserver;

import java.awt.event.*;

import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  Gabriel Tichy
 */
public class HostPropertyCustomEditor extends javax.swing.JPanel implements HelpCtx.Provider, ActionListener, EnhancedCustomPropertyEditor {
    private HostPropertyEditor editor;

    /** Creates new form HostEditorPanel */
    public HostPropertyCustomEditor (HostPropertyEditor ed) {
        editor = ed;
        initComponents ();
        initAccessibility();
        anyRadioButton.addActionListener (this);
        selectedRadioButton.addActionListener (this);
        anyRadioButton.setMnemonic (NbBundle.getBundle(HostPropertyCustomEditor.class).getString ("CTL_AnyRadioButton_Mnemonic").charAt (0));
        selectedRadioButton.setMnemonic (NbBundle.getBundle(HostPropertyCustomEditor.class).getString ("CTL_SelectedRadioButton_Mnemonic").charAt (0));
        grantLabel.setDisplayedMnemonic (NbBundle.getBundle(HostPropertyCustomEditor.class).getString ("CTL_GrantLabel_Mnemonic").charAt (0));
        setPreferredSize (new java.awt.Dimension (300, 200));
        
        // set values from PropertyEditor
        HttpServerSettings.HostProperty hp = (HttpServerSettings.HostProperty)editor.getValue ();
        if (HttpServerSettings.ANYHOST.equals (hp.getHost ())) {
            anyRadioButton.setSelected (true);
            grantTextArea.setText (""); // NOI18N
        }
        else if (HttpServerSettings.LOCALHOST.equals (hp.getHost ())) {
            selectedRadioButton.setSelected (true);
            grantTextArea.setText (hp.getGrantedAddresses ());
        }
    }

    public void actionPerformed (ActionEvent event) {
        try {
            if (event.getSource() == anyRadioButton) {
                grantLabel.setEnabled (false);
                grantTextArea.setEnabled (false);
            }
            else if (event.getSource() == selectedRadioButton) {
                grantLabel.setEnabled (true);
                grantTextArea.setEnabled (true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initAccessibility()
    {
        hostLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_HostLabelA11yDesc"));
        grantLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_GrantLabelA11yDesc"));
        grantTextArea.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_GrantTextAreaA11yName"));
        getAccessibleContext().setAccessibleDescription (org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_HostPropertyPanelA11yDesc"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        hostLabel = new javax.swing.JLabel();
        anyRadioButton = new javax.swing.JRadioButton();
        selectedRadioButton = new javax.swing.JRadioButton();
        grantLabel = new javax.swing.JLabel();
        grantScrollPane = new javax.swing.JScrollPane();
        grantTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        hostLabel.setText(org.openide.util.NbBundle.getBundle("org/netbeans/modules/httpserver/Bundle").getString("CTL_HostLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(hostLabel, gridBagConstraints);

        anyRadioButton.setToolTipText(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_AnyRadioButtonA11yDesc"));
        anyRadioButton.setText(org.openide.util.NbBundle.getBundle("org/netbeans/modules/httpserver/Bundle").getString("CTL_AnyRadioButton"));
        buttonGroup.add(anyRadioButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(anyRadioButton, gridBagConstraints);

        selectedRadioButton.setToolTipText(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_SelectedRadioButtonA11yDesc"));
        selectedRadioButton.setSelected(true);
        selectedRadioButton.setText(org.openide.util.NbBundle.getBundle("org/netbeans/modules/httpserver/Bundle").getString("CTL_SelectedRadioButton"));
        buttonGroup.add(selectedRadioButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(selectedRadioButton, gridBagConstraints);

        grantLabel.setText(org.openide.util.NbBundle.getBundle("org/netbeans/modules/httpserver/Bundle").getString("CTL_GrantLabel"));
        grantLabel.setLabelFor(grantTextArea);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 2, 0);
        add(grantLabel, gridBagConstraints);

        grantTextArea.setToolTipText(org.openide.util.NbBundle.getBundle(HostPropertyCustomEditor.class).getString("ACS_GrantTextAreaA11yDesc"));
        grantScrollPane.setViewportView(grantTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(grantScrollPane, gridBagConstraints);

    }//GEN-END:initComponents

    public java.lang.Object getPropertyValue () throws java.lang.IllegalStateException {
        if (anyRadioButton.isSelected ())
            return new HttpServerSettings.HostProperty ("", HttpServerSettings.ANYHOST);    // NOI18N
        else if (selectedRadioButton.isSelected ())
            return new HttpServerSettings.HostProperty (grantTextArea.getText (), HttpServerSettings.LOCALHOST);
        
        throw new IllegalStateException ();
    }    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane grantScrollPane;
    private javax.swing.JRadioButton anyRadioButton;
    private javax.swing.JRadioButton selectedRadioButton;
    private javax.swing.JTextArea grantTextArea;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JLabel grantLabel;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        String helpid = HttpServerNode.class.getName()+"_properties"; //NOI18N
        return new HelpCtx(helpid);
    }        
}
