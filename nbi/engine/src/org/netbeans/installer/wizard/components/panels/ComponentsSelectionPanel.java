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
 *
 * $Id$
 */
package org.netbeans.installer.wizard.components.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.installer.product.ProductComponent;
import org.netbeans.installer.product.ProductComponent.Status;
import org.netbeans.installer.product.ProductRegistry;
import org.netbeans.installer.product.ProductTreeNode;
import org.netbeans.installer.utils.ErrorLevel;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.StringUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.swing.treetable.TreeColumnCellRenderer;
import org.netbeans.installer.utils.helper.swing.treetable.TreeTable;
import org.netbeans.installer.utils.helper.swing.treetable.TreeTableModel;

/**
 *
 * @author ks152834
 */
public class ComponentsSelectionPanel extends DefaultWizardPanel {
    private JTextPane   messagePane;
    private TreeTable   componentsTreeTable;
    private JScrollPane treeTableScrollPane;
    
    private JLabel      displayNameLabel;
    private JTextPane   descriptionPane;
    private JLabel      requirementsLabel;
    private JLabel      conflictsLabel;
    
    private JLabel      totalDownloadSizeLabel;
    private JLabel      totalDiskSpaceLabel;
    
    private JLabel      errorLabel;
    
    public ComponentsSelectionPanel() {
        setProperty(MESSAGE_TEXT_PROPERTY, DEFAULT_MESSAGE_TEXT);
        setProperty(MESSAGE_CONTENT_TYPE_PROPERTY, DEFAULT_MESSAGE_CONTENT_TYPE);
        setProperty(DISPLAY_NAME_LABEL_TEXT_PROPERTY, DEFAULT_DISPLAY_NAME_LABEL_TEXT);
        setProperty(DESCRIPTION_TEXT_PROPERTY, DEFAULT_DESCRIPTION_TEXT);
        setProperty(DESCRIPTION_CONTENT_TYPE_PROPERTY, DEFAULT_DESCRIPTION_CONTENT_TYPE);
        setProperty(REQUIREMENTS_LABEL_TEXT_PROPERTY, DEFAULT_REQUIREMENTS_LABEL_TEXT);
        setProperty(CONFLICTS_LABEL_TEXT_PROPERTY, DEFAULT_CONFLICTS_LABEL_TEXT);
        setProperty(TOTAL_DOWNLOAD_SIZE_LABEL_TEXT_PROPERTY, DEFAULT_TOTAL_DOWNLOAD_SIZE_LABEL_TEXT);
        setProperty(TOTAL_DISK_SPACE_LABEL_TEXT_PROPERTY, DEFAULT_TOTAL_DISK_SPACE_LABEL_TEXT);
        
        setProperty(ERROR_NO_CHANGES_PROPERTY, DEFAULT_ERROR_NO_CHANGES);
        setProperty(ERROR_REQUIREMENT_INSTALL_PROPERTY, DEFAULT_ERROR_REQUIREMENT_INSTALL);
        setProperty(ERROR_CONFLICT_INSTALL_PROPERTY, DEFAULT_ERROR_CONFLICT_INSTALL);
        setProperty(ERROR_REQUIREMENT_UNINSTALL_PROPERTY, DEFAULT_ERROR_REQUIREMENT_UNINSTALL);
    }
    
    public void initialize() {
        final String messageContentType = systemUtils.parseString(getProperty(MESSAGE_CONTENT_TYPE_PROPERTY), getClassLoader());
        messagePane.setContentType(messageContentType);
        
        final String messageText = systemUtils.parseString(getProperty(MESSAGE_TEXT_PROPERTY), getClassLoader());
        messagePane.setText(messageText);
        
        final String descriptionContentType = systemUtils.parseString(getProperty(DESCRIPTION_CONTENT_TYPE_PROPERTY), getClassLoader());
        descriptionPane.setContentType(descriptionContentType);
        
        updateDescription();
        updateTotalSizes();
        
        evaluateErrors();
    }
    
    public void initComponents() {
        setLayout(new GridBagLayout());
        
        messagePane = new JTextPane();
        messagePane.setOpaque(false);
        
        componentsTreeTable = new TreeTable(new ComponentsTreeTableModel());
        componentsTreeTable.setShowVerticalLines(false);
        componentsTreeTable.setOpaque(false);
        componentsTreeTable.setTableHeader(null);
        componentsTreeTable.setRowHeight(componentsTreeTable.getRowHeight() + 4);
        componentsTreeTable.setIntercellSpacing(new Dimension(0, 0));
        componentsTreeTable.setTreeColumnCellRenderer(new ComponentsTreeColumnCellRenderer(componentsTreeTable));
        componentsTreeTable.getColumnModel().getColumn(0).setPreferredWidth(400);
        componentsTreeTable.getColumnModel().getColumn(1).setCellRenderer(new ComponentsStatusCellRenderer());
        componentsTreeTable.getColumnModel().getColumn(1).setCellEditor(new ComponentsStatusCellEditor());
        componentsTreeTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent event) {
                updateTotalSizes();
                evaluateErrors();
            }
        });
        componentsTreeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                updateDescription();
            }
        });
        
        treeTableScrollPane = new JScrollPane(componentsTreeTable);
        treeTableScrollPane.setOpaque(false);
        treeTableScrollPane.getViewport().setOpaque(false);
        treeTableScrollPane.setViewportBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        treeTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        displayNameLabel = new JLabel();
        displayNameLabel.setFont(displayNameLabel.getFont().deriveFont(Font.BOLD));
        
        descriptionPane = new JTextPane();
        descriptionPane.setOpaque(false);
        
        requirementsLabel = new JLabel();
        
        conflictsLabel = new JLabel();
        
        totalDownloadSizeLabel = new JLabel();
        
        totalDiskSpaceLabel = new JLabel();
        
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setIcon(emptyIcon);
        errorLabel.setText(" ");
        
        add(messagePane, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(11, 11, 0, 11), 0, 0));
        add(treeTableScrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 11, 0, 11), 0, 0));
        
        add(displayNameLabel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 11, 0, 11), 0, 0));
        add(descriptionPane, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 11, 0, 11), 0, 0));
        add(requirementsLabel, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 11, 0, 11), 0, 0));
        add(conflictsLabel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 11, 0, 11), 0, 0));
        
        add(totalDownloadSizeLabel, new GridBagConstraints(0, 6, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 11, 0, 0), 0, 0));
        add(totalDiskSpaceLabel, new GridBagConstraints(1, 6, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 6, 0, 11), 0, 0));
        
        add(errorLabel, new GridBagConstraints(0, 99, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(7, 11, 11, 11), 0, 0));
    }
    
    public void evaluateNextButtonClick() {
        String errorMessage = validateChanges();
        
        if (errorMessage == null) {
            super.evaluateNextButtonClick();
        } else {
            ErrorManager.getInstance().notify(ErrorLevel.ERROR, errorMessage);
        }
    }
    
    private void evaluateErrors() {
        String errorMessage = validateChanges();
        if (errorMessage != null) {
            errorLabel.setIcon(errorIcon);
            errorLabel.setText(errorMessage);
            getNextButton().setEnabled(false);
        } else {
            errorLabel.setIcon(emptyIcon);
            errorLabel.setText(" ");
            getNextButton().setEnabled(true);
        }
    }
    
    private String validateChanges() {
        List<ProductComponent> componentsToInstall = new ArrayList<ProductComponent>();
        List<ProductComponent> componentsToUninstall = new ArrayList<ProductComponent>();
        
        for (ProductComponent component: ProductRegistry.getInstance().getProductComponentsAsList()) {
            if (component.getStatus() == Status.TO_BE_INSTALLED) {
                componentsToInstall.add(component);
            }
            if (component.getStatus() == Status.TO_BE_UNINSTALLED) {
                componentsToUninstall.add(component);
            }
        }
        
        if ((componentsToInstall.size() == 0) && (componentsToUninstall.size() == 0)) {
            return systemUtils.parseString(getProperty(ERROR_NO_CHANGES_PROPERTY), getClassLoader());
        }
        
        for (ProductComponent component: componentsToInstall) {
            for (ProductComponent requirement: component.getRequirements()) {
                if ((requirement.getStatus() != Status.TO_BE_INSTALLED) && (requirement.getStatus() != Status.INSTALLED)) {
                    return stringUtils.formatMessage(systemUtils.parseString(getProperty(ERROR_REQUIREMENT_INSTALL_PROPERTY), getClassLoader()), component.getDisplayName(), requirement.getDisplayName());
                }
            }
            
            for (ProductComponent conflict: component.getConflicts()) {
                if ((conflict.getStatus() == Status.TO_BE_INSTALLED) || (conflict.getStatus() == Status.INSTALLED)) {
                    return stringUtils.formatMessage(systemUtils.parseString(getProperty(ERROR_CONFLICT_INSTALL_PROPERTY), getClassLoader()), component.getDisplayName(), conflict.getDisplayName());
                }
            }
        }
        
        for (ProductComponent component: componentsToUninstall) {
            for (ProductComponent dependent: ProductRegistry.getInstance().getProductComponentsAsList()) {
                if (dependent.requires(component) && ((dependent.getStatus() == Status.INSTALLED) || (dependent.getStatus() == Status.TO_BE_INSTALLED))) {
                    return stringUtils.formatMessage(systemUtils.parseString(getProperty(ERROR_REQUIREMENT_UNINSTALL_PROPERTY), getClassLoader()), component.getDisplayName(), dependent.getDisplayName());
                }
            }
        }
        
        return null;
    }
    
    private void updateDescription() {
        int selectedRow = componentsTreeTable.getSelectedRow();
        if (selectedRow == -1) {
            displayNameLabel.setText(systemUtils.parseString(EMPTY_DISPLAY_NAME_LABEL_TEXT, getClassLoader()) + " ");
            displayNameLabel.setEnabled(false);
            
            descriptionPane.setText(systemUtils.parseString(EMPTY_DESCRIPTION_TEXT, getClassLoader()) + " ");
            descriptionPane.setEnabled(false);
            
            requirementsLabel.setText(systemUtils.parseString(EMPTY_REQUIREMENTS_LABEL_TEXT, getClassLoader()) + " ");
            requirementsLabel.setEnabled(false);
            
            conflictsLabel.setText(systemUtils.parseString(EMPTY_CONFLICTS_LABEL_TEXT, getClassLoader()) + " ");
            conflictsLabel.setEnabled(false);
        } else {
            ProductTreeNode node = (ProductTreeNode) componentsTreeTable.getModel().getValueAt(selectedRow, 0);
            
            displayNameLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(DISPLAY_NAME_LABEL_TEXT_PROPERTY), getClassLoader()), node.getDisplayName()) + " ");
            displayNameLabel.setEnabled(true);
            
            descriptionPane.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(DESCRIPTION_TEXT_PROPERTY), getClassLoader()), node.getDescription()) + " ");
            descriptionPane.setEnabled(true);
            
            if ((node instanceof ProductComponent) && (((ProductComponent) node).getRequirements().size() > 0)) {
                requirementsLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(REQUIREMENTS_LABEL_TEXT_PROPERTY), getClassLoader()), stringUtils.asString(((ProductComponent) node).getRequirements())) + " ");
                requirementsLabel.setEnabled(true);
            } else {
                requirementsLabel.setText(systemUtils.parseString(EMPTY_REQUIREMENTS_LABEL_TEXT, getClassLoader()) + " ");
                requirementsLabel.setEnabled(false);
            }
            
            if ((node instanceof ProductComponent) && (((ProductComponent) node).getConflicts().size() > 0)) {
                conflictsLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(CONFLICTS_LABEL_TEXT_PROPERTY), getClassLoader()), stringUtils.asString(((ProductComponent) node).getConflicts())) + " ");
                conflictsLabel.setEnabled(true);
            } else {
                conflictsLabel.setText(systemUtils.parseString(EMPTY_CONFLICTS_LABEL_TEXT, getClassLoader()) + " ");
                conflictsLabel.setEnabled(false);
            }
        }
    }
    
    private void updateTotalSizes() {
        List<ProductComponent> components = new ArrayList<ProductComponent>();
        
        for (ProductComponent component: ProductRegistry.getInstance().getProductComponentsAsList()) {
            if (component.getStatus() == Status.TO_BE_INSTALLED) {
                components.add(component);
            }
        }
        
        if (components.size() == 0) {
            totalDownloadSizeLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(TOTAL_DOWNLOAD_SIZE_LABEL_TEXT_PROPERTY), getClassLoader()), systemUtils.parseString(DEFAULT_TOTAL_DOWNLOAD_SIZE, getClassLoader())) + " ");
            totalDiskSpaceLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(TOTAL_DISK_SPACE_LABEL_TEXT_PROPERTY), getClassLoader()), systemUtils.parseString(DEFAULT_TOTAL_DISK_SPACE, getClassLoader())) + " ");
        } else {
            long totalDownloadSize = 0;
            long totalDiskSpace = 0;
            for (ProductComponent component: components) {
                totalDownloadSize += component.getDownloadSize();
                totalDiskSpace += component.getRequiredDiskSpace();
            }
            
            totalDownloadSizeLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(TOTAL_DOWNLOAD_SIZE_LABEL_TEXT_PROPERTY), getClassLoader()), stringUtils.formatSize(totalDownloadSize)) + " ");
            totalDiskSpaceLabel.setText(stringUtils.formatMessage(systemUtils.parseString(getProperty(TOTAL_DISK_SPACE_LABEL_TEXT_PROPERTY), getClassLoader()), stringUtils.formatSize(totalDiskSpace)) + " ");
        }
    }
    
    public static class ComponentsTreeModel implements TreeModel {
        private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
        
        public Object getRoot() {
            return ProductRegistry.getInstance().getProductTreeRoot();
        }
        
        public Object getChild(Object parent, int index) {
            return ((ProductTreeNode) parent).getVisibleChildren().get(index);
        }
        
        public int getChildCount(Object parent) {
            return ((ProductTreeNode) parent).getVisibleChildren().size();
        }
        
        public boolean isLeaf(Object node) {
            return ((ProductTreeNode) node).getVisibleChildren().size() == 0;
        }
        
        public void valueForPathChanged(TreePath path, Object newValue) {
            // do nothing, we're read-only
        }
        
        public int getIndexOfChild(Object parent, Object child) {
            return ((ProductTreeNode) parent).getVisibleChildren().indexOf(child);
        }
        
        public void addTreeModelListener(TreeModelListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }
        
        public void removeTreeModelListener(TreeModelListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    }
    
    public static class ComponentsTreeTableModel extends TreeTableModel {
        public ComponentsTreeTableModel() {
            super(new ComponentsTreeModel());
        }
        
        public int getTreeColumnIndex() {
            return 0;
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public String getColumnName(int column) {
            return "";
        }
        
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return ProductTreeNode.class;
                case 1:
                    return Status.class;
                default:
                    return null;
            }
        }
        
        public boolean isCellEditable(int row, int column) {
            switch (column) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    return false;
            }
        }
        
        public Object getValueAt(int row, int column) {
            ProductTreeNode node = (ProductTreeNode) getTree().getPathForRow(row).getLastPathComponent();
            
            switch (column) {
                case 0:
                    return node;
                case 1:
                    if (node instanceof ProductComponent) {
                        return ((ProductComponent) node).getStatus();
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        }
        
        public void setValueAt(Object value, int row, int column) {
            if (column == 1) {
                ProductTreeNode node = (ProductTreeNode) getTree().getPathForRow(row).getLastPathComponent();
                if (node instanceof ProductComponent) {
                    ((ProductComponent) node).setStatus((Status) value);
                }
                fireTableRowsUpdated(row, row);
            }
        }
    }
    
    public static class ComponentsTreeColumnCellRenderer extends TreeColumnCellRenderer {
        public ComponentsTreeColumnCellRenderer(final TreeTable treeTable) {
            super(treeTable);
        }
        
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            ProductTreeNode node = (ProductTreeNode) value;
            
            setIcon(node.getIcon());
            setText(node.getDisplayName());
            
            return this;
        }
    }
    
    public static class ComponentsStatusCellRenderer extends JCheckBox implements TableCellRenderer {
        private static final JLabel EMPTY = new JLabel();
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
            if (selected) {
                setOpaque(true);
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
                EMPTY.setOpaque(true);
                EMPTY.setBackground(table.getSelectionBackground());
            } else {
                setOpaque(false);
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                EMPTY.setOpaque(false);
                EMPTY.setBackground(table.getBackground());
            }
            
            if (value != null) {
                Status status = (Status) value;
                
                if ((status == Status.INSTALLED) || (status == Status.TO_BE_INSTALLED)) {
                    setSelected(true);
                } else {
                    setSelected(false);
                }
                
                setText(status.getDisplayName());
                
                return this;
            } else {
                return EMPTY;
            }
        }
    }
    
    public static class ComponentsStatusCellEditor extends JCheckBox implements TableCellEditor {
        private Status status;
        
        Vector<CellEditorListener> listeners = new Vector<CellEditorListener>();
        
        public ComponentsStatusCellEditor() {
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
            if (selected) {
                setOpaque(true);
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setOpaque(false);
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            
            if (value != null) {
                status = (Status) value;
                
                if ((status == Status.INSTALLED) || (status == Status.TO_BE_INSTALLED)) {
                    setSelected(true);
                } else {
                    setSelected(false);
                }
                
                setText(status.getDisplayName());
                
                return this;
            } else {
                return null;
            }
        }
        
        public Object getCellEditorValue() {
            if (isSelected()) {
                switch (status) {
                    case NOT_INSTALLED:
                        return Status.TO_BE_INSTALLED;
                    case TO_BE_UNINSTALLED:
                        return Status.INSTALLED;
                    default:
                        return status;
                }
            } else {
                switch (status) {
                    case INSTALLED:
                        return Status.TO_BE_UNINSTALLED;
                    case TO_BE_INSTALLED:
                        return Status.NOT_INSTALLED;
                    default:
                        return status;
                }
            }
        }
        
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
        
        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }
        
        public boolean stopCellEditing() {
            return true;
        }
        
        public void cancelCellEditing() {
            // do nothing
        }
        
        public void addCellEditorListener(CellEditorListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }
        
        public void removeCellEditorListener(CellEditorListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
        
        private void fireEditingCanceled() {
            synchronized (listeners) {
                CellEditorListener[] clone = listeners.toArray(new CellEditorListener[0]);
                ChangeEvent event = new ChangeEvent(this);
                
                for (CellEditorListener listener: clone) {
                    listener.editingCanceled(event);
                }
            }
        }
        
        private void fireEditingStopped() {
            synchronized (listeners) {
                CellEditorListener[] clone = listeners.toArray(new CellEditorListener[0]);
                ChangeEvent event = new ChangeEvent(this);
                
                for (CellEditorListener listener: clone) {
                    listener.editingStopped(event);
                }
            }
        }
    }
    
    private static StringUtils   stringUtils   = StringUtils.getInstance();
    private static SystemUtils   systemUtils   = SystemUtils.getInstance();
    private static ResourceUtils resourceUtils = ResourceUtils.getInstance();
    
    private static final String MESSAGE_TEXT_PROPERTY = "message.text";
    private static final String MESSAGE_CONTENT_TYPE_PROPERTY = "message.content.type";
    private static final String DISPLAY_NAME_LABEL_TEXT_PROPERTY = "display.name.label.text";
    private static final String DESCRIPTION_TEXT_PROPERTY = "description.text";
    private static final String DESCRIPTION_CONTENT_TYPE_PROPERTY = "description.content.type";
    private static final String REQUIREMENTS_LABEL_TEXT_PROPERTY = "requirements.label.text";
    private static final String CONFLICTS_LABEL_TEXT_PROPERTY = "conflicts.label.text";
    private static final String TOTAL_DOWNLOAD_SIZE_LABEL_TEXT_PROPERTY = "total.download.size.label.text";
    private static final String TOTAL_DISK_SPACE_LABEL_TEXT_PROPERTY = "total.disk.space.label.text";
    
    private static final String DEFAULT_MESSAGE_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.message.text");
    private static final String DEFAULT_MESSAGE_CONTENT_TYPE = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.message.content.type");
    private static final String DEFAULT_DISPLAY_NAME_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.display.name.label.text");
    private static final String DEFAULT_DESCRIPTION_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.description.text");
    private static final String DEFAULT_DESCRIPTION_CONTENT_TYPE = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.description.content.type");
    private static final String DEFAULT_REQUIREMENTS_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.requirements.label.text");
    private static final String DEFAULT_CONFLICTS_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.conflicts.label.text");
    private static final String DEFAULT_TOTAL_DOWNLOAD_SIZE_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.total.download.size.label.text");
    private static final String DEFAULT_TOTAL_DISK_SPACE_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.total.disk.space.label.text");
    
    private static final String EMPTY_DISPLAY_NAME_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.empty.display.name.label.text");
    private static final String EMPTY_DESCRIPTION_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.empty.description.text");
    private static final String EMPTY_REQUIREMENTS_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.empty.requirements.label.text");
    private static final String EMPTY_CONFLICTS_LABEL_TEXT = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.empty.conflicts.label.text");
    
    private static final String DEFAULT_TOTAL_DOWNLOAD_SIZE = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.total.download.size");
    private static final String DEFAULT_TOTAL_DISK_SPACE = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.total.disk.space");
    
    private static final String ERROR_NO_CHANGES_PROPERTY = "error.no.changes";
    private static final String ERROR_REQUIREMENT_INSTALL_PROPERTY = "error.requirement.install";
    private static final String ERROR_CONFLICT_INSTALL_PROPERTY = "error.conflict.install";
    private static final String ERROR_REQUIREMENT_UNINSTALL_PROPERTY = "error.requirement.uninstall";
    
    private static final String DEFAULT_ERROR_NO_CHANGES = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.error.no.changes");
    private static final String DEFAULT_ERROR_REQUIREMENT_INSTALL = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.error.requirement.install");
    private static final String DEFAULT_ERROR_CONFLICT_INSTALL = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.error.conflict.install");
    private static final String DEFAULT_ERROR_REQUIREMENT_UNINSTALL = resourceUtils.getString(ComponentsSelectionPanel.class, "ComponentsSelectionPanel.default.error.requirement.uninstall");
}
