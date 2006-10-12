/*
 * CommonMessageConfigurationPanel.java
 *
 * Created on August 25, 2006, 1:18 PM
 */

package org.netbeans.modules.xml.wsdl.ui.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import org.netbeans.api.project.Project;

/**
 *
 * @author  radval
 */
public class CommonMessageConfigurationPanel extends javax.swing.JPanel {
    
    private Map<String, String> namespaceToPrefixMap;
    private Project mProject;
    
    private ElementOrTypeTableCellRenderer elementOrTypeRenderer;
    
    /** Creates new form CommonMessageConfigurationPanel */
    public CommonMessageConfigurationPanel(Project project, Map<String, String> namespaceToPrefixMap) {
        mProject = project;
        this.namespaceToPrefixMap = namespaceToPrefixMap;
        initComponents();
        initGUI();
    }
    
    
    /** Mattise require default constructor otherwise will not load in design view of mattise
     **/
    public CommonMessageConfigurationPanel() {
        namespaceToPrefixMap = new HashMap<String, String>();
        if (!namespaceToPrefixMap.containsKey("xsd")) {
            namespaceToPrefixMap.put("http://www.w3.org/2001/XMLSchema", "xsd");
        }
        initComponents();
        initGUI();
    }
    
    public void addNewRow() {
        PartAndElementOrTypeTableModel model = (PartAndElementOrTypeTableModel) partsTable.getModel();
        model.addNewRow();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        partsScrollPane = new javax.swing.JScrollPane();
        partsTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        partsScrollPane.setAutoscrolls(true);
        partsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "Message Part Name", "Element Or Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        partsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        partsTable.setSurrendersFocusOnKeystroke(true);
        partsScrollPane.setViewportView(partsTable);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.addButton.textNoMnemonic")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.removeButton.textNoMnemonic")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, partsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .add(jPanel1Layout.createSequentialGroup()
                .add(addButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton)
                .add(272, 272, 272))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(partsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addButton)
                    .add(removeButton)))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        PartAndElementOrTypeTableModel model = (PartAndElementOrTypeTableModel) partsTable.getModel();
        int[] rows = partsTable.getSelectedRows();
        if(rows != null) {
            for(int i = rows.length; i > 0; i--) {
                if (rows[i-1] < partsTable.getRowCount()) {
                    model.removeSelectedRow(rows[i-1]);
                }
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        PartAndElementOrTypeTableModel model = (PartAndElementOrTypeTableModel) partsTable.getModel();
        model.addNewRow();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void initGUI() {
        PartAndElementOrTypeTableModel model =  new PartAndElementOrTypeTableModel(namespaceToPrefixMap);
        partsTable.setModel(model);
        partsTable.getColumnModel().getColumn(0).setCellRenderer(new ElementOrTypeTableCellRenderer());
        setUpElementOrTypeColumn(partsTable.getColumnModel().getColumn(1));
        model.addTableModelListener(new TableModelChangeListener());
        FocusListener fl = new PanelFocusListener();
        partsTable.addFocusListener(fl);
        addButton.addFocusListener(fl);
        removeButton.addFocusListener(fl);
        this.addFocusListener(fl);
        partsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (partsTable.getSelectedRowCount() > 0) {
                        removeButton.setEnabled(true);
                    } else {
                        removeButton.setEnabled(false);
                    }
                }
            }
        });
        
        partsTable.getTableHeader().setReorderingAllowed(false);
    }
    
    
    public void setUpElementOrTypeColumn(TableColumn elementOrTypeColumn) {
        elementOrTypeColumn.setCellEditor(new ElementOrTypeTableCellEditor(partsTable, namespaceToPrefixMap, mProject));
        
        //Set up tool tips for the sport cells.
        elementOrTypeRenderer =
                new ElementOrTypeTableCellRenderer();
        elementOrTypeRenderer.setToolTipText("Click to select Element Or Type");
        elementOrTypeColumn.setCellRenderer(elementOrTypeRenderer);
    }
    
    public List<PartAndElementOrTypeTableModel.PartAndElementOrType> getPartAndElementOrType() {
        PartAndElementOrTypeTableModel  model = (PartAndElementOrTypeTableModel) partsTable.getModel();
        return model.getPartAndElementOrType();
    }
    
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        addButton.setEnabled(enable);
        removeButton.setEnabled(enable);
        partsTable.setEnabled(enable);
        partsScrollPane.setEnabled(enable);
        
    }
    
    public void scrollToVisible(int rowIndex, int vColIndex) {
        if (!(partsTable.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport)partsTable.getParent();
    
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = partsTable.getCellRect(rowIndex, vColIndex, true);
    
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
    
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }
    
    
    class TableModelChangeListener implements TableModelListener {
        
        public void tableChanged(TableModelEvent e) {
            PartAndElementOrTypeTableModel model = (PartAndElementOrTypeTableModel) e.getSource();
            if (e.getType() == TableModelEvent.DELETE) {
                int firstRow = e.getFirstRow();
                int lastRow = e.getLastRow();
                int newRow = firstRow >= lastRow ? firstRow -1 : lastRow -1;
                if (newRow < 0) {
                    newRow = 0;
                }
                if (newRow < model.getRowCount()) {
                    partsTable.setRowSelectionInterval(newRow, newRow);
                    scrollToVisible(newRow, 0);
                }
            } else if (e.getType() == TableModelEvent.INSERT) {
                int rowCount = model.getRowCount();
                int newRow = rowCount - 1;
                if (newRow > -1) {
                    partsTable.setRowSelectionInterval(newRow, newRow);
                    scrollToVisible(newRow, 0);
                }
            } else if (e.getType() == TableModelEvent.UPDATE) {
                int lastRow = e.getLastRow();
                
                boolean allColumns = e.getColumn() == TableModelEvent.ALL_COLUMNS;
                
                if (lastRow > -1 && lastRow < model.getRowCount()) {
                    partsTable.setRowSelectionInterval(lastRow, lastRow);
                    if (!allColumns) {
                        partsTable.setColumnSelectionInterval(e.getColumn(), e.getColumn());
                        scrollToVisible(lastRow, e.getColumn());
                    }
                }
            }
            partsTable.requestFocus();
        }
    }
    
    
    class PanelFocusListener implements FocusListener {
        public void focusGained(FocusEvent e) {
            org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.addButton.text"));
            org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.removeButton.text"));
        }
        
        public void focusLost(FocusEvent e) {
            org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.addButton.textNoMnemonic"));
            org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(CommonMessageConfigurationPanel.class, "CommonMessageConfigurationPanel.removeButton.textNoMnemonic"));
        }
        
    }
    
    public static void main(String[] args) {
        
//        JFrame frame = new JFrame();
//        frame.getContentPane().setLayout(new BorderLayout());
//        CommonMessageConfigurationPanel p = new CommonMessageConfigurationPanel();
//        frame.getContentPane().add(p, BorderLayout.CENTER);
//        frame.setSize(200, 200);
//        frame.setVisible(true);
        
        
    }

    public void clearSelection() {
        partsTable.clearSelection();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane partsScrollPane;
    private javax.swing.JTable partsTable;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
}
