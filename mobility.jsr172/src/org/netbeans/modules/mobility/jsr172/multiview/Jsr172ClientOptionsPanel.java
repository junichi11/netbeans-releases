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
 * ClientOptionsPanel.java
 * Created on July 25, 2005, 10:40 AM
 */
package org.netbeans.modules.mobility.jsr172.multiview;


import java.util.Properties;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import javax.swing.JComponent;
import org.netbeans.modules.mobility.end2end.E2EDataObject;
import org.netbeans.modules.mobility.end2end.client.config.ClientConfiguration;
import org.netbeans.modules.mobility.end2end.client.config.Configuration;



/**
 *
 *
 *
 * @author  Michal Skvor,Sigal Duek
 *
 */

public class Jsr172ClientOptionsPanel extends SectionInnerPanel {
    
    final private transient E2EDataObject dataObject;
    private transient Properties properties;
    
    /** Creates new form ClientOptionsPanel */
    public Jsr172ClientOptionsPanel( SectionView sectionView, E2EDataObject dataObject ) {        
        super( sectionView );
        
        this.dataObject = dataObject;
        dataObject.addSaveCallback( new SaveCallbackImpl());
        initComponents();
        initValues();
    }
    
    
    
    private void initValues() {        
        final Configuration config = dataObject.getConfiguration();
        final ClientConfiguration cc = config.getClientConfiguration();
        properties = cc.getProperties();
        if( "true".equals( properties.getProperty( "cldc11" ))) {
            cldc10.setSelected( false );
        } else {
            cldc10.setSelected( true );
        }
        if( "true".equals( properties.getProperty( "DataBinding" ))) {
            dataBindingCheckBox.setSelected( true );
        } else {
            dataBindingCheckBox.setSelected( false );
        }
    }
    
    
    
    public JComponent getErrorComponent( @SuppressWarnings("unused")
	final String errorId ) {
        return null;
    }
    
    
    
    public void linkButtonPressed( @SuppressWarnings("unused")
	final Object ddBean, @SuppressWarnings("unused")
	final String ddProperty ) {
    }
    
    
    
    public void setValue( @SuppressWarnings("unused")
	final JComponent source, @SuppressWarnings("unused")
	final Object value ) {
    }
    
    
    
    private class SaveCallbackImpl implements E2EDataObject.SaveCallback {
        
        SaveCallbackImpl() {
            //To avoid creation of accessor class
        }
        
        private final static String ID = "Jsr172ClientOptionsPanelSaveCallback";
        
        @SuppressWarnings("synthetic-access")
		public void save() {
            System.err.println(" - Saving Option Panel");
            
            if( cldc10.getSelectedObjects() == null ) {
                properties.setProperty( "cldc11", "true" );
            } else {
                properties.setProperty( "cldc11", "false" );
            }
            if( dataBindingCheckBox.isSelected()) {
                properties.setProperty( "DataBinding", "true" );
            } else {
                properties.setProperty( "DataBinding", "false" );
            }
        }
        
        public int hashCode() {
            return ID.hashCode();
        }
        
        public boolean equals( final Object obj ) {
            if( obj instanceof SaveCallbackImpl ) {
                return true;
            }
            return false;
        }
        
    }
    
    
    
    /** This method is called from within the constructor to
     *
     * initialize the form.
     *
     * WARNING: Do NOT modify this code. The content of this method is
     *
     * always regenerated by the Form Editor.
     *
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cldc10 = new javax.swing.JCheckBox();
        dataBindingCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        cldc10.setBackground(new java.awt.Color(255, 255, 255));
        cldc10.setText(org.openide.util.NbBundle.getMessage(Jsr172ClientOptionsPanel.class, "LABEL_Floating_Point_To_String")); // NOI18N
        cldc10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cldc10.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cldc10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cldc10ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        add(cldc10, gridBagConstraints);

        dataBindingCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        dataBindingCheckBox.setText(org.openide.util.NbBundle.getMessage(Jsr172ClientOptionsPanel.class, "LABLE_DataBinding")); // NOI18N
        dataBindingCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dataBindingCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        dataBindingCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataBindingCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        add(dataBindingCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void dataBindingCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataBindingCheckBoxActionPerformed
    dataObject.setModified( true );
}//GEN-LAST:event_dataBindingCheckBoxActionPerformed
    
    
    
    private void cldc10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cldc10ActionPerformed
        dataObject.setModified( true );
    }//GEN-LAST:event_cldc10ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cldc10;
    private javax.swing.JCheckBox dataBindingCheckBox;
    // End of variables declaration//GEN-END:variables
    
    
    
}

