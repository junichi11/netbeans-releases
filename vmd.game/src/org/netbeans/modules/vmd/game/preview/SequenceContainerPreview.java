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

package org.netbeans.modules.vmd.game.preview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.vmd.game.model.Sequence;
import org.netbeans.modules.vmd.game.model.SequenceContainer;

/**
 *
 * @author  kherink
 */
public class SequenceContainerPreview extends javax.swing.JPanel implements ActionListener {
    
    private SequenceContainer sequenceContainer;
    private SequencePreviewPanel sequencePreviewPanel;
	private String labelTypeName;
	
    /**
     * Creates new form SequenceContainerPreview
     */
    public SequenceContainerPreview(String labelTypeName, SequenceContainer sequenceContainer) {
		this.labelTypeName = labelTypeName;
        this.sequenceContainer = sequenceContainer;
		this.sequencePreviewPanel = (SequencePreviewPanel) this.sequenceContainer.getDefaultSequence().getPreview();
        initComponents();
    }
    
    private ComboBoxModel getSequenceComboModel() {
        List sequences = this.sequenceContainer.getSequences();
        return new DefaultComboBoxModel(sequences.toArray());
    }
	
    public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.comboBoxSequence) {
			Sequence currentSequence = (Sequence) this.comboBoxSequence.getSelectedItem();
			this.sequencePreviewPanel.setSequence(currentSequence);
		}
    }

	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        labelSprite = new javax.swing.JLabel();
        textFieldSprite = new javax.swing.JTextField();
        comboBoxSequence = new javax.swing.JComboBox();
        sequencePanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(10, 180));
        labelSprite.setText(this.labelTypeName);

        textFieldSprite.setBackground(new java.awt.Color(255, 255, 255));
        textFieldSprite.setEditable(false);
        textFieldSprite.setText(this.sequenceContainer.getName());
        textFieldSprite.setBorder(null);

        comboBoxSequence.setBackground(new java.awt.Color(255, 255, 255));
        comboBoxSequence.setModel(getSequenceComboModel());
        this.comboBoxSequence.addActionListener(this);

        sequencePanel.setLayout(new java.awt.BorderLayout());

        sequencePanel.setBackground(new java.awt.Color(255, 255, 255));
        this.sequencePanel.add(this.sequencePreviewPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(labelSprite)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textFieldSprite, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, comboBoxSequence, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                .addContainerGap())
            .add(sequencePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelSprite)
                    .add(textFieldSprite, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboBoxSequence, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sequencePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox comboBoxSequence;
    public javax.swing.JLabel labelSprite;
    private javax.swing.JPanel sequencePanel;
    public javax.swing.JTextField textFieldSprite;
    // End of variables declaration//GEN-END:variables
    
}
