/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.editor.options;

import java.awt.Dimension;
import java.util.ResourceBundle;
import org.openide.util.NbBundle;

/**
 * Input panel for pair of strings, one inline and one in editor
 *
 * @author  Petr Nejedly
 */

public class AbbrevInputPanel extends javax.swing.JPanel {

    private static ResourceBundle bundle = NbBundle.getBundle( AbbrevInputPanel.class );

    /** Creates new form AbbrevsInputPanel */
    public AbbrevInputPanel() {
        initComponents ();
        
        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AIP")); // NOI18N
        abbrevLabel.setDisplayedMnemonic(bundle.getString("AIP_Abbrev_Mnemonic").charAt (0)); // NOI18N
        expandLabel.setDisplayedMnemonic(bundle.getString("AIP_Expand_Mnemonic").charAt (0)); // NOI18N
        abbrevField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AIP_Abbrev")); // NOI18N
        expandTextArea.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AIP_Expand")); // NOI18N
        
        Dimension dim = getPreferredSize();
        dim.width = 4*dim.width;
        dim.height = 4*dim.height;
        setPreferredSize( dim );
    }

    public void setAbbrev( String[] abbrev ) {
        abbrevField.setText( abbrev[0] );
        expandTextArea.setText( abbrev[1] );
    }

    public String[] getAbbrev() {
        String[] retVal = { abbrevField.getText(), expandTextArea.getText() };
        return retVal;
    }


    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        abbrevLabel = new javax.swing.JLabel();
        abbrevField = new javax.swing.JTextField();
        expandLabel = new javax.swing.JLabel();
        expandScrollPane = new javax.swing.JScrollPane();
        expandTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        abbrevLabel.setText(bundle.getString( "AIP_Abbrev" ));
        abbrevLabel.setLabelFor(abbrevField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(abbrevLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(abbrevField, gridBagConstraints);

        expandLabel.setText(bundle.getString( "AIP_Expand" ));
        expandLabel.setLabelFor(expandTextArea);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 12);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(expandLabel, gridBagConstraints);

        expandScrollPane.setViewportView(expandTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(expandScrollPane, gridBagConstraints);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea expandTextArea;
    private javax.swing.JLabel abbrevLabel;
    private javax.swing.JTextField abbrevField;
    private javax.swing.JScrollPane expandScrollPane;
    private javax.swing.JLabel expandLabel;
    // End of variables declaration//GEN-END:variables

}
