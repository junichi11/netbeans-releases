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
/*
 * ChapterTreePanel.java
 *
 * Created on May 26, 2005, 6:00 PM
 */

package org.netbeans.modules.xml.multiview.test;

import org.netbeans.modules.xml.multiview.test.bookmodel.Chapter;
import org.netbeans.modules.xml.multiview.ui.TreePanel;
import org.netbeans.modules.xml.multiview.ui.TreeNode;
/**
 *
 * @author  mkuchtiak
 */
public class ChapterTreePanel extends javax.swing.JPanel implements TreePanel {
    private javax.swing.JTextArea[] paragraphTA;
    
    /** Creates new form ChapterTreePanel */
    public ChapterTreePanel() {
        initComponents();
    }
    
    public void setModel(TreeNode node) {
        Chapter chapter = ((BookTreePanelMVElement.ChapterNode)node).getChapter();
        titleTF.setText(chapter.getTitle());;
        summaryTA.setBorder(titleTF.getBorder());
        summaryTA.setText(chapter.getSummary());
        String[] paragraphs = chapter.getParagraph();
        paragraphTA = new javax.swing.JTextArea[paragraphs.length];
        paragraphsPanel.removeAll();
        for (int i=0;i<paragraphs.length;i++) {
            paragraphTA[i] = new javax.swing.JTextArea();
            paragraphTA[i].setText(paragraphs[i]);
            paragraphTA[i].setRows(10);
            paragraphsPanel.add(new javax.swing.JScrollPane(paragraphTA[i]),String.valueOf(i+1));
        }
        lengthTF.setText(chapter.getAttributeValue("length"));
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        titleLabel = new javax.swing.JLabel();
        titleTF = new javax.swing.JTextField();
        summaryLabel = new javax.swing.JLabel();
        summaryTA = new javax.swing.JTextArea();
        paragraphLabel = new javax.swing.JLabel();
        paragraphsPanel = new javax.swing.JTabbedPane();
        filler = new javax.swing.JPanel();
        lengthLabel = new javax.swing.JLabel();
        lengthTF = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        titleLabel.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(titleLabel, gridBagConstraints);

        titleTF.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(titleTF, gridBagConstraints);

        summaryLabel.setText("Summary:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(summaryLabel, gridBagConstraints);

        summaryTA.setRows(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        add(summaryTA, gridBagConstraints);

        paragraphLabel.setText("Paragraphs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        add(paragraphLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        add(paragraphsPanel, gridBagConstraints);

        filler.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(filler, gridBagConstraints);

        lengthLabel.setText("Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
        add(lengthLabel, gridBagConstraints);

        lengthTF.setColumns(5);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
        add(lengthTF, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel filler;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JTextField lengthTF;
    private javax.swing.JLabel paragraphLabel;
    private javax.swing.JTabbedPane paragraphsPanel;
    private javax.swing.JLabel summaryLabel;
    private javax.swing.JTextArea summaryTA;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTF;
    // End of variables declaration//GEN-END:variables
    
}
