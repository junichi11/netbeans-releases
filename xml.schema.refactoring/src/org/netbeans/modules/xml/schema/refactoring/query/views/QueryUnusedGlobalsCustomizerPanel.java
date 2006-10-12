/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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
/*
 * QueryUnusedGlobalsCustomizerPanel.java
 *
 * Created on April 14, 2006, 8:49 PM
 */

package org.netbeans.modules.xml.schema.refactoring.query.views;

import javax.swing.UIManager;

/**
 *
 * @author  Jeri Lockhart
 */
public class QueryUnusedGlobalsCustomizerPanel extends javax.swing.JPanel {
    public static final long serialVersionUID = 1L;
    public static final String PROP_RUN_UNUSED_GLOBALS_QUERY =
            "prop-run-unused-globals-query";    // NOI18N
    /**
     * Creates new form QueryUnusedGlobalsCustomizerPanel
     */
    public QueryUnusedGlobalsCustomizerPanel() {
        initComponents();
        
    }
    
    /**
     * programmatically set the Exclude Global Elements
     * check box
     *
     */
    public void setExcludeElements(boolean exclude){
        excludeGEsCheckBox.setSelected(exclude);
    }
    /**
     * programmatically set the Exclude Global Elements
     * check box
     *
     */
    public boolean getExcludeElements(){
        return excludeGEsCheckBox.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        excludeGEsCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(excludeGEsCheckBox, java.util.ResourceBundle.getBundle("org/netbeans/modules/xml/schema/refactoring/query/views/Bundle").getString("LBL_Exclude_Global_Elements"));
        excludeGEsCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(QueryUnusedGlobalsCustomizerPanel.class).getString("LBL_Exclude_Global_Elements"));
        excludeGEsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        excludeGEsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        excludeGEsCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, java.util.ResourceBundle.getBundle("org/netbeans/modules/xml/schema/refactoring/query/views/Bundle").getString("LBL_Find_Unused_Description"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 292, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(excludeGEsCheckBox))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24)
                .add(excludeGEsCheckBox)
                .addContainerGap(64, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox excludeGEsCheckBox;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
}
