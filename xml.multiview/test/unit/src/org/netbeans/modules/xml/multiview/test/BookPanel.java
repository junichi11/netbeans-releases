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
package org.netbeans.modules.xml.multiview.test;

import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.netbeans.modules.xml.multiview.test.bookmodel.Book;
import org.netbeans.modules.xml.multiview.Error;

/**
 *
 * @author  mkuchtiak
 */
public class BookPanel extends SectionInnerPanel {
    Book book;
    BookDataObject dObj;
    javax.swing.JTextArea[] paragraphTA;
    /** Creates new form ChapterPanel */
    public BookPanel(SectionView view, BookDataObject dObj,  Book book) {
        super(view);
        this.dObj=dObj;
        this.book=book;
        initComponents();
        titleTF.setText(book.getTitle());
        addValidatee(titleTF);
        priceTF.setText(book.getPrice());
        addModifier(priceTF);
        paperbackBox.setSelected(book.isPaperback());
        String instock = book.getAttributeValue("instock");
        instockBox.setSelected("yes".equals(instock));
    }

    public void setValue(javax.swing.JComponent source, Object value) {
        if (source==titleTF) {
            book.setTitle((String)value);
        } else if (source==priceTF) {
            book.setPrice((String)value);
        }
    }
    
    public void documentChanged(javax.swing.text.JTextComponent comp, String value) {
        if (comp==titleTF) {
            String val = (String)value;
            if (val.length()==0) {
                getSectionView().getErrorPanel().setError(new Error(Error.MISSING_VALUE_MESSAGE, "title", comp));
                return;
            }
            getSectionView().getErrorPanel().clearError();
        }
    }

    public void rollbackValue(javax.swing.text.JTextComponent source) {
        if (titleTF==source) {
            titleTF.setText(book.getTitle());
        }
    }
    
    protected void endUIChange() {
        dObj.modelUpdatedFromUI();
    }

    public void linkButtonPressed(Object ddBean, String ddProperty) {
    }

    public javax.swing.JComponent getErrorComponent(String errorId) {
        if ("title".equals(errorId)) return titleTF;
        return null;
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
        priceLabel = new javax.swing.JLabel();
        priceTF = new javax.swing.JTextField();
        paperbackBox = new javax.swing.JCheckBox();
        instockBox = new javax.swing.JCheckBox();
        filler = new javax.swing.JPanel();

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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(titleTF, gridBagConstraints);

        priceLabel.setText("Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        add(priceLabel, gridBagConstraints);

        priceTF.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(priceTF, gridBagConstraints);

        paperbackBox.setText("Paperback");
        paperbackBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        paperbackBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        add(paperbackBox, gridBagConstraints);

        instockBox.setText("In Stock");
        instockBox.setActionCommand("instock");
        instockBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        instockBox.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        add(instockBox, gridBagConstraints);

        filler.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(filler, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel filler;
    private javax.swing.JCheckBox instockBox;
    private javax.swing.JCheckBox paperbackBox;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField priceTF;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTF;
    // End of variables declaration//GEN-END:variables
    
}
