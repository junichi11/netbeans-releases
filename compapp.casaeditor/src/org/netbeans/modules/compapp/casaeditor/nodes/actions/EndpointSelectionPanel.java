/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.compapp.casaeditor.nodes.actions;

import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.compapp.casaeditor.model.casa.CasaEndpointRef;
import org.openide.util.NbBundle;

/**
 * 
 * A panel for selecting an endpoint from a list.
 *
 * @author  jqian
 */
public class EndpointSelectionPanel extends javax.swing.JPanel {
             
    private List<CasaEndpointRef> endpoints;
    
    /** Creates new form EndpointSelectionPanel */
    public EndpointSelectionPanel(final List<CasaEndpointRef> endpoints) {
        
        this.endpoints = endpoints;
        
        initComponents();
        
        TableModel tableModel = new AbstractTableModel() {
            public int getRowCount() {
                return endpoints.size();
            }
            public int getColumnCount() {
                return 2;
            }
            public String getColumnName(int columnIndex) {
                if (columnIndex == 0) {
                    return NbBundle.getMessage(EndpointSelectionPanel.class, 
                            "TITLE_EndpointName"); // NOI18N
                } else {
                    return NbBundle.getMessage(EndpointSelectionPanel.class, 
                            "TITLE_ServiceQName"); // NOI18N
                }
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                CasaEndpointRef endpoint = endpoints.get(rowIndex);
                if (columnIndex == 0) {
                    return endpoint.getEndpointName();
                } else {
                    return endpoint.getServiceQName();
                }
            }            
        };
        endpointTable.setModel(tableModel);
        endpointTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectableEndpointsLabel.setLabelFor(endpointTable);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectableEndpointsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        endpointTable = new javax.swing.JTable();

        connectableEndpointsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        org.openide.awt.Mnemonics.setLocalizedText(connectableEndpointsLabel, org.openide.util.NbBundle.getMessage(EndpointSelectionPanel.class, "LBL_AvailableEndpoints")); // NOI18N

        endpointTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Endpoint Name", "Service QName"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(endpointTable);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                    .add(connectableEndpointsLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(connectableEndpointsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 253, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        connectableEndpointsLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(EndpointSelectionPanel.class, "ACS_Endpoint_Selection_Panel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    public CasaEndpointRef getSelectedItem() {
        int row = endpointTable.getSelectedRow();
        return row == -1 ? null : endpoints.get(row);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel connectableEndpointsLabel;
    private javax.swing.JTable endpointTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
