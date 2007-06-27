/*
 * TemplateEntry.java
 *
 * Created on June 19, 2007, 10:18 AM
 */

package org.netbeans.modules.uml.codegen.ui.customizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import org.netbeans.modules.uml.codegen.dataaccess.xmlbeans.DomainObject;

/**
 *
 * @author  treyspiva
 */
public class TemplateEntry extends javax.swing.JPanel
{
    private DomainObject domainObject = null;
    private DescriptionTextArea templateDescription = new DescriptionTextArea();
    
    public TemplateEntry(
        String familyName, DomainObject domain, boolean isChecked)
    {
        initComponents();
        descContainer.add(templateDescription, BorderLayout.CENTER);
        domainObject = domain;
        setTemplateName(domainObject.getName());
        setTemplateDescription(domainObject.getDescription());
        setModelElement(domainObject.getModelElement());
        setStereroType(domainObject.getStereotype());
        templateNameCheckBox.setSelected(isChecked);
        templateNameCheckBox.putClientProperty("familyName", familyName); // NOI18N
    }

    public void setTemplateName(String name)
    {
        templateNameCheckBox.setText(name);
    }

    public void setTemplateDescription(String desc)
    {
        templateDescription.setText(desc);
        Dimension preferredSize = templateDescription.getPreferredSize();
        templateDescription.setSize(preferredSize);
    }

    public void setStereroType(String value)
    {
        if ((value == null) || (value.length() == 0))
            value = org.openide.util.NbBundle.getMessage(TemplateEntry.class, "LBL_NA"); // NOI18N
        
        stereotypeValue.setText(value);
    }

    public void setModelElement(String value)
    {
        elementTypeValue.setText(value);
    }

    
    public javax.swing.JCheckBox getTemplateNameField()
    {
        return templateNameCheckBox;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        templateNameCheckBox = new javax.swing.JCheckBox();
        elementTypeLabel = new javax.swing.JLabel();
        elementTypeValue = new javax.swing.JLabel();
        stereotypeLabel = new javax.swing.JLabel();
        stereotypeValue = new javax.swing.JLabel();
        descContainer = new javax.swing.JPanel();

        setOpaque(false);

        templateNameCheckBox.setText("<domainName>");
        templateNameCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        templateNameCheckBox.setOpaque(false);

        elementTypeLabel.setFont(new java.awt.Font("Tahoma", 2, 10));
        elementTypeLabel.setText(org.openide.util.NbBundle.getMessage(TemplateEntry.class, "LBL_ElementType")); // NOI18N

        elementTypeValue.setFont(new java.awt.Font("Tahoma", 2, 10));
        elementTypeValue.setText("<elementType>");

        stereotypeLabel.setFont(new java.awt.Font("Tahoma", 2, 10));
        stereotypeLabel.setText(org.openide.util.NbBundle.getMessage(TemplateEntry.class, "LBL_Stereotype")); // NOI18N

        stereotypeValue.setFont(new java.awt.Font("Tahoma", 2, 10));
        stereotypeValue.setText(org.openide.util.NbBundle.getMessage(TemplateEntry.class, "LBL_NA")); // NOI18N

        descContainer.setBackground(new java.awt.Color(255, 255, 0));
        descContainer.setOpaque(false);
        descContainer.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(templateNameCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(39, 39, 39)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(descContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(elementTypeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(elementTypeValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(stereotypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(stereotypeValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(templateNameCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(descContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(elementTypeLabel)
                    .add(elementTypeValue)
                    .add(stereotypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(stereotypeValue))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>stereotypeValuestereotypeValuestereotypeValuestereotypeValuestereotypeValue//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel descContainer;
    private javax.swing.JLabel elementTypeLabel;
    private javax.swing.JLabel elementTypeValue;
    private javax.swing.JLabel stereotypeLabel;
    private javax.swing.JLabel stereotypeValue;
    private javax.swing.JCheckBox templateNameCheckBox;
    // End of variables declarationstereotypeValue//GEN-END:variables
    
    /**
     * This JTextArea is used to make sure that all rows are visible.  If I use 
     * the JTextArea in the group layout, then the text area will never grow to 
     * fit all of the rows of text.  If I use something like a Gridbag layout 
     * then the text area is too big by default.  So, I have created a text area
     * that makes sure the bounds of the text area will fit all of the rows of
     * text when word wrapping is turned on.
     */
    public class DescriptionTextArea extends JTextArea
    {

        public DescriptionTextArea()
        {
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
        }
        
        @Override
        public void setBounds(int x, int y, int w, int h)
        {
            super.setBounds(x, y, w, h);
            
            int rows = getRows();
            int height = getRowHeight();
            
            if(h == rows * height)
            {
                setBounds(x, y, w, rows * height);
            }
        }

        @Override
        public void setBounds(Rectangle bounds)
        {
            super.setBounds(bounds);
            
            int rows = getRows();
            int height = getRowHeight();
            
            if(bounds.height == rows * height)
            {
                setBounds(bounds.x, bounds.y, bounds.width, rows * height);
            }
        }
        
    }
    
}
