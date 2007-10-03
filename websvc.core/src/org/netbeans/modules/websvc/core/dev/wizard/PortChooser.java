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

package org.netbeans.modules.websvc.core.dev.wizard;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.border.EtchedBorder;
import org.netbeans.modules.websvc.core.dev.wizard.nodes.PortNode;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.util.NbBundle;

public class PortChooser extends javax.swing.JPanel {
    
    public static final String IS_VALID = "portChooser_isValid"; //NOI18N
    
    private NodeAcceptor nodeAcceptor;
    private NodeDisplayPanel nodeDisplayPanel;
    
    /** Creates new form PortChooser */
    public PortChooser(Node root) {
        initComponents();

        this.nodeAcceptor = new NodeAcceptorImpl();

        nodeDisplayPanel = new NodeDisplayPanel(root);
        nodeDisplayPanel.setBorder(new EtchedBorder());
        jPanelBeanTree.add(nodeDisplayPanel);
        nodeDisplayPanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                Node[] nodes = nodeDisplayPanel.getSelectedNodes();
                validateNodes();
            }
        });
        
        BeanTreeView btw = (BeanTreeView)nodeDisplayPanel.getComponent(0);
        jLabelDesc.setLabelFor(btw.getViewport().getView());
        
        validateNodes();
    }
    
    private void validateNodes() {
        boolean nodeAccepted = nodeAcceptor.acceptNodes(nodeDisplayPanel.getSelectedNodes());
        if (!nodeAccepted)
            firePropertyChange(IS_VALID, Boolean.TRUE, Boolean.FALSE);
        else
            firePropertyChange(IS_VALID, Boolean.FALSE, Boolean.TRUE);
    }

    private void setErrorMessage(String message) {
        if (message == null)
            message = " ";
        jLabelError.setText(message);
    }
    
    public Node[] getSelectedNodes() {
        return nodeDisplayPanel.getSelectedNodes();
    }

    public String getSelectedPortOwnerName() {
        return nodeDisplayPanel.getSelectedNodes()[0].getParentNode().getDisplayName();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelDesc = new javax.swing.JLabel();
        jPanelBeanTree = new javax.swing.JPanel();
        jLabelError = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabelDesc, org.openide.util.NbBundle.getMessage(PortChooser.class, "LBL_SelectPort")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
        add(jLabelDesc, gridBagConstraints);

        jPanelBeanTree.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 11, 11, 11);
        add(jPanelBeanTree, gridBagConstraints);

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setLabelFor(jPanelBeanTree);
        jLabelError.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 11);
        add(jLabelError, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PortChooser.class, "TTL_SelectPort")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelDesc;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JPanel jPanelBeanTree;
    // End of variables declaration//GEN-END:variables
    
    private class NodeAcceptorImpl implements NodeAcceptor {
        public boolean acceptNodes(Node[] nodes) {
            setErrorMessage(" "); //NOI18N

            // no node selected
            if (nodes.length == 0) {
                setErrorMessage(NbBundle.getMessage(PortChooser.class, "LBL_SelectOnePort")); //NOI18N
                return false;
            }
            
            PortNode port = (PortNode) nodes[0].getLookup().lookup(PortNode.class);
            // non-port node is selected
            if (port == null) {
                setErrorMessage(NbBundle.getMessage(PortChooser.class, "LBL_NodeIsNotPort")); //NOI18N
                return false;
            }
            
            return true;
        }
    }

}
