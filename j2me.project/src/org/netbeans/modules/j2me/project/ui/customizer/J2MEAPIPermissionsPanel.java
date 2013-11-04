/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2me.project.ui.customizer;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Theofanis Oikonomou
 */
public class J2MEAPIPermissionsPanel extends javax.swing.JPanel {
    
    protected JTable table;
    private final StorableTableModel tableModel;

    private final J2MEProjectProperties uiProperties;
    private final ListSelectionListener listSelectionListener;

    /**
     * Creates new form J2MEAPIPermissionsPanel
     */
    public J2MEAPIPermissionsPanel(J2MEProjectProperties uiProperties) {
        this.uiProperties = uiProperties;
        initComponents();
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "ACSN_Perm"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "ACSD_Perm"));
        tableModel = this.uiProperties.API_PERMISSIONS_TABLE_MODEL;
        table = new JTable(tableModel);
        scrollPane.setViewportView(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                bRemove.setEnabled(table.isEnabled()  &&  table.getSelectedRow() >= 0);
            }
        };
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        TableColumn col0 = table.getColumnModel().getColumn(0);
        TableColumn col1 = table.getColumnModel().getColumn(1);
        col0.setResizable(true);
        col0.setPreferredWidth(300);
        col1.setResizable(true);
        col1.setPreferredWidth(80);
        postInitComponents();
    }
    
    private void postInitComponents() {
        String platformProfile = uiProperties.getProject().evaluator().getProperty(J2MEProjectProperties.PLATFORM_PROFILE);
        final boolean notMIDP10 = platformProfile != null && !platformProfile.equals("MIDP-1.0"); //NOI18N
        String[] propertyNames = uiProperties.API_PERMISSIONS_PROPERTY_NAMES;
        String values[] = new String[propertyNames.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = uiProperties.getEvaluator().getProperty(propertyNames[i]);
        }
        tableModel.setDataDelegates(values);
        table.setBackground(UIManager.getDefaults().getColor("Table.background")); //NOI18N
        listSelectionListener.valueChanged(null);
        lError.setVisible(! notMIDP10);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lTable = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        bAdd = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();
        lError = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lTable, org.openide.util.NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "J2MEAPIPermissionsPanel.lTable.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(lTable, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(scrollPane, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(bAdd, org.openide.util.NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "J2MEAPIPermissionsPanel.bAdd.text")); // NOI18N
        bAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 5, 0);
        add(bAdd, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(bRemove, org.openide.util.NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "J2MEAPIPermissionsPanel.bRemove.text")); // NOI18N
        bRemove.setEnabled(false);
        bRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 5, 0);
        add(bRemove, gridBagConstraints);

        lError.setForeground(new java.awt.Color(89, 79, 191));
        org.openide.awt.Mnemonics.setLocalizedText(lError, org.openide.util.NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "J2MEAPIPermissionsPanel.lError.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(lError, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void bAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddActionPerformed
        final AddAPIPanel add = new AddAPIPanel(tableModel.getKeys());
        final DialogDescriptor dd = new DialogDescriptor(
            add, NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "TITLE_AddAPI"), //NOI18N
            true, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (NotifyDescriptor.OK_OPTION.equals(e.getSource())) {
                        int row = tableModel.addRow(add.getAPIName());
                        table.getSelectionModel().setSelectionInterval(row, row);
                    }
                }
            }
        );
        add.setDialogDescriptor(dd);
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);
    }//GEN-LAST:event_bAddActionPerformed

    private void bRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveActionPerformed
        final int i = table.getSelectedRow();
        if (i < 0)
        return;
        tableModel.removeRow(i);
        final int max = tableModel.getRowCount();
        if (max <= 0)
        table.getSelectionModel().clearSelection();
        else if (i < max)
        table.getSelectionModel().setSelectionInterval(i, i);
        else
        table.getSelectionModel().setSelectionInterval(max - 1, max - 1);
    }//GEN-LAST:event_bRemoveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bRemove;
    private javax.swing.JLabel lError;
    private javax.swing.JLabel lTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    static class StorableTableModel extends AbstractTableModel {
        
        static class Item {
            
            final private String name;
            private boolean required;
            
            public Item(String name, boolean required) {
                this.name = name;
                this.required = required;
            }
            
            public String getName() {
                return name;
            }
            
            public boolean isRequired() {
                return required;
            }
            
            public void setRequired(final boolean required) {
                this.required = required;
            }
            
            public String toString() {
                return name;
            }
        }
        
        private HashMap<String,String> map = new HashMap<String,String>();
        final private ArrayList<Item> items = new ArrayList<Item>();
        
        private static final long serialVersionUID = -6523408202243150812L;
        private final J2MEProjectProperties uiProperties;
        private boolean dataDelegatesWereSet = false;

        public StorableTableModel(J2MEProjectProperties uiProperties) {
            this.uiProperties = uiProperties;
        }
        
        public HashSet<String> getKeys() {
            final HashSet<String> set = new HashSet<String>();
            for (int a = 0; a < items.size(); a ++)
                set.add(items.get(a).getName());
            return set;
        }
        
        public int getRowCount() {
            return items.size();
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public boolean isCellEditable(@SuppressWarnings("unused")
		final int rowIndex, final int columnIndex) {
            return columnIndex == 1;
        }
        
        public String getColumnName(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "LBL_Perm_Column_API"); //NOI18N
                case 1:
                    return NbBundle.getMessage(J2MEAPIPermissionsPanel.class, "LBL_Perm_Column_Required"); //NOI18N
                default:
                    return null;
            }
        }
        
		public Class<?> getColumnClass(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Boolean.class;
                default:
                    return null;
            }
        }
        
        public synchronized Object[] getDataDelegates() {
            if (!dataDelegatesWereSet) {
                String[] propertyNames = uiProperties.API_PERMISSIONS_PROPERTY_NAMES;
                String values[] = new String[propertyNames.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = uiProperties.getEvaluator().getProperty(propertyNames[i]);
                }
                setDataDelegates(values);
            }
            updateMapFromItems();
            return new Object[]{map};
        }
        
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            assert rowIndex < items.size();
            switch (columnIndex) {
                case 0:
                    return items.get(rowIndex);
                case 1:
                    return Boolean.valueOf(items.get(rowIndex).isRequired());
                default:
                    return null;
            }
        }
        
        public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
            assert columnIndex == 1  &&  value instanceof Boolean;
            items.get(rowIndex).setRequired(((Boolean) value).booleanValue());
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
        
	public synchronized void setDataDelegates(final String data[]) {
            assert data != null;
            map = data[0] == null ? new HashMap<String,String>() : (HashMap<String,String>) uiProperties.decode(data[0]);
            updateItemsFromMap();
            fireTableDataChanged();
            dataDelegatesWereSet = true;
        }
        
        public void updateItemsFromMap() {
            items.clear();
            String perms;
            StringTokenizer tokens;
            
            perms = map.get("MIDlet-Permissions"); //NOI18N
            if (perms != null) {
                tokens = new StringTokenizer(perms, ","); //NOI18N
                while (tokens.hasMoreTokens())
                    items.add(new Item(tokens.nextToken().trim(), true));
            }
            perms = map.get("MIDlet-Permissions-Opt"); //NOI18N
            if (perms != null) {
                tokens = new StringTokenizer(perms, ","); //NOI18N
                while (tokens.hasMoreTokens())
                    items.add(new Item(tokens.nextToken().trim(), false));
            }
        }
        
        public void updateMapFromItems() {
            final ArrayList<String> req = new ArrayList<String>();
            final ArrayList<String> opt = new ArrayList<String>();
            for (int a = 0; a < items.size(); a ++) {
                final Item i = items.get(a);
                if (i.isRequired())
                    req.add(i.getName());
                else
                    opt.add(i.getName());
            }
            map = new HashMap<String,String>();
            if (req.size() > 0)
                map.put("MIDlet-Permissions", commaSeparatedList(req)); //NOI18N
            if (opt.size() > 0)
                map.put("MIDlet-Permissions-Opt", commaSeparatedList(opt)); //NOI18N
        }
        
        public String commaSeparatedList(final ArrayList<String> list) {
            final StringBuffer sb = new StringBuffer();
            boolean first = true;
            if (list != null) for (int a = 0; a < list.size(); a ++) {
                if (first)
                    first = false;
                else
                    sb.append(", "); //NOI18N
                sb.append(list.get(a));
            }
            return sb.toString();
        }
        
        public int addRow(final String name) {
            final int row = items.size();
            items.add(new Item(name, true));
            fireTableRowsInserted(row, row);
            return row;
        }
        
        public void removeRow(final int row) {
            assert row < items.size();
            items.remove(row);
            fireTableRowsDeleted(row, items.size() + 1);
        }
        
    }

}
