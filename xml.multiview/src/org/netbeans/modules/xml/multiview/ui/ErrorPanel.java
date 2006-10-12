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

package org.netbeans.modules.xml.multiview.ui;

import javax.swing.UIManager;
import org.netbeans.modules.xml.multiview.Error;
//import org.netbeans.modules.xml.multiview.cookies.ErrorComponentContainer;

/** 
 * A panel for error messages.
 *
 * Created on November 19, 2004, 10:44 AM
 * @author  mkuchtiak
 */
public class ErrorPanel extends javax.swing.JPanel {

    private Error error;
    private ErrorLabel errorLabel;
    private String errorMessage;


    /** Creates new form ErrorPanel */
    public ErrorPanel(final ToolBarDesignEditor editor) {
        initComponents();
        
        errorLabel = new ErrorLabel();
        errorLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Error error = getError();
                if (error!=null) {
                    Error.ErrorLocation errorLocation = error.getErrorLocation();
                    if (errorLocation!=null) {
                        SectionPanel sectPanel = ((SectionView)editor.getContentView()).findSectionPanel(errorLocation.getKey());
                        if (sectPanel.getInnerPanel()==null) sectPanel.open();
                        sectPanel.scroll();
                        javax.swing.JComponent errorComp = sectPanel.getErrorComponent(errorLocation.getComponentId());
                        if (errorComp!=null) errorComp.requestFocus();
                    }
                }
            }
        });
        add(errorLabel,java.awt.BorderLayout.CENTER);
        
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    /*
    public ErrorComponentContainer getErrorComponentContainer() {
        return errorContainer;
    }
    */
    public Error getError() {
        return error;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
 
    
    public void setError(Error error) {
        switch (error.getErrorType()) {
            case Error.ERROR_MESSAGE : {
                errorMessage="Error: "+error.getErrorMessage();
                break;
            }
            case Error.WARNING_MESSAGE : {
                errorMessage="Warning: "+error.getErrorMessage();
                break;
            }
            case Error.MISSING_VALUE_MESSAGE : {
                errorMessage="Missing Value: "+error.getErrorMessage();
                break;
            }            
            case Error.DUPLICATE_VALUE_MESSAGE : {
                errorMessage="Duplicate Value: "+error.getErrorMessage();
                break;
            }
        }
        this.error=error;
        errorLabel.setText(errorMessage);
        errorLabel.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/org/netbeans/modules/xml/multiview/resources/error-glyph.gif"))); //NOI18N
    }
    
    public void clearError() {
        error=null;
        errorLabel.setIcon(null);
        errorLabel.setText("");
        errorMessage="";
    }
    
    private class ErrorLabel extends javax.swing.JLabel {
        ErrorLabel() {
            super();
            //setForeground(SectionVisualTheme.hyperlinkColor);
            setForeground(UIManager.getDefaults().getColor("ToolBar.dockingForeground")); //NOI18N
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            setText(""); //NOI18N
        }

        public void setText(String text) {
            if (text.length()==0) super.setText(" "); //NOI18N
            else super.setText("<html><u>"+text+"</u></html>"); //NOI18N
        }
    }
    

}
