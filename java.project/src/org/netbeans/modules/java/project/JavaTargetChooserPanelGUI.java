/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.project;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class JavaTargetChooserPanelGUI extends javax.swing.JPanel implements ActionListener, DocumentListener {
  
    /** prefered dimmension of the panel */
    private static final java.awt.Dimension PREF_DIM = new java.awt.Dimension (560, 350);
    
    private static final ListCellRenderer CELL_RENDERER = new NodeCellRenderer();
    
    private Project project;
    private ModelItem[] groupItems;
    private String expectedExtension;
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    
    /** Creates new form SimpleTargetChooserGUI */
    public JavaTargetChooserPanelGUI( Component bottomPanel ) {
        initComponents();
        if ( bottomPanel != null ) {
            bottomPanelContainer.add( bottomPanel, java.awt.BorderLayout.CENTER );
        }
        
        
        //initValues( project, null, null );
        documentNameTextField.getDocument().addDocumentListener( this );
        
        rootComboBox.setRenderer( CELL_RENDERER );
        packageComboBox.setRenderer( CELL_RENDERER );
                
        rootComboBox.addActionListener( this );
        packageComboBox.addActionListener( this );
        setName( NbBundle.getBundle (JavaTargetChooserPanelGUI.class).getString ("LBL_JavaTargetChooserPanelGUI_Name") ); // NOI18N
    }
    
    public void initValues( Project p, SourceGroup[] groups, FileObject template, String preselectedFolder ) {
        this.project = p;
        
        this.groupItems = new ModelItem[ groups.length ]; 
        for( int i = 0; i < groups.length; i++ ) {
            this.groupItems[i] = new ModelItem( groups[i] ); 
        }
                
        // Show name of the project
        projectTextField.setText( ProjectUtils.getInformation(p).getDisplayName() );
        assert template != null;
        String displayName = null;
        try {
            DataObject templateDo = DataObject.find (template);
            displayName = templateDo.getNodeDelegate ().getDisplayName ();
        } catch (DataObjectNotFoundException ex) {
            displayName = template.getName ();
        }
        putClientProperty ("NewFileWizard_Title", displayName);// NOI18N
        
        // Setup comboboxes 
        rootComboBox.setModel( new DefaultComboBoxModel( this.groupItems ) );
        updatePackages();        
        // Determine the extension
        String ext = template == null ? "" : template.getExt(); // NOI18N
        expectedExtension = ext.length() == 0 ? "" : "." + ext; // NOI18N
    }
        
    public FileObject getRootFolder() {
        return ((ModelItem)rootComboBox.getSelectedItem()).group.getRootFolder();        
    }
    
    public String getPackageFileName() {
        String packageName = packageComboBox.getEditor().getItem().toString();        
        return  packageName.replace( '.', '/' ); // NOI18N        
    }
    
    public java.awt.Dimension getPreferredSize() {
        return PREF_DIM;
    }
    
    
    /*
    public String getTargetFolder() {
        File fo = getFolder();
        
        if ( fo == null ) {
            return null;
        }
        else {
            return fo.getPath();
        }
    }
    */
    
    public String getTargetName() {
        String text = documentNameTextField.getText().trim();
        
        if ( text.length() == 0 ) {
            return null;
        }
        else {
            return text;
        }

    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(e);
        }
    }
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        documentNameTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        rootComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        packageComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        targetSeparator = new javax.swing.JSeparator();
        bottomPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel3"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(documentNameTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 24, 0);
        add(jPanel1, gridBagConstraints);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel5"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel5, gridBagConstraints);

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        add(projectTextField, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 0);
        add(rootComboBox, gridBagConstraints);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel2"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jLabel2, gridBagConstraints);

        packageComboBox.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 0);
        add(packageComboBox, gridBagConstraints);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(JavaTargetChooserPanelGUI.class, "LBL_JavaTargetChooserPanelGUI_jLabel4"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jLabel4, gridBagConstraints);

        fileTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 0);
        add(fileTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(targetSeparator, gridBagConstraints);

        bottomPanelContainer.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(bottomPanelContainer, gridBagConstraints);

    }//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanelContainer;
    private javax.swing.JTextField documentNameTextField;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JComboBox rootComboBox;
    private javax.swing.JSeparator targetSeparator;
    // End of variables declaration//GEN-END:variables

    // ActionListener implementation -------------------------------------------
        
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if ( rootComboBox == e.getSource() ) {            
            updatePackages();
            updateText();
        }
        else if ( packageComboBox == e.getSource() ) {
            updateText();
        }
    }    
    
    // DocumentListener implementation -----------------------------------------
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {                
        updateText();
        fireChange();        
    }    
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    // Private methods ---------------------------------------------------------
        
    private void updatePackages() {
        packageComboBox.setModel( new DefaultComboBoxModel( ((ModelItem)rootComboBox.getSelectedItem()).getChildren() ) );        
    }
    
    private File getFolder() {
        FileObject rootFo = getRootFolder();
        File rootFile = FileUtil.toFile( rootFo );
        if ( rootFile == null ) {
            return null;
        }        
        String packageFileName = getPackageFileName();        
        File folder = new File( rootFile, packageFileName );
        return folder;
    }
    
    private void updateText() {
        File projdirFile = FileUtil.toFile(project.getProjectDirectory());
        if (projdirFile != null) {
            String documentName = documentNameTextField.getText().trim();
            if (documentName.length() == 0) {
                fileTextField.setText(""); // NOI18N
            } else {
                File newFile = new File( getFolder(), documentName + expectedExtension);
                fileTextField.setText(newFile.getAbsolutePath());
            }
        } else {
            // Not on disk.
            fileTextField.setText(""); // NOI18N
        }
    }
    
    // Private innerclasses ----------------------------------------------------
    
    private static class ModelItem {
        
        private Node node;        
        private SourceGroup group;
        private Icon icon;
        private ModelItem[] children;
	
        // For source groups
        public ModelItem( SourceGroup group ) {            
            this.group = group;
            this.icon = null; // XXX Should be group getIcon() 
        }
        
        // For packages
        public ModelItem( Node node ) {
            this.node = node;
            this.icon = new ImageIcon( node.getIcon( java.beans.BeanInfo.ICON_COLOR_16x16 ) );
        }
        
	public String getDisplayName() {
            if ( group != null ) {
                return group.getDisplayName();
            }
            else {
                return node.getDisplayName();
            }
        }
	
        public Icon getIcon() {
            return icon;
        }
        
        public String toString() {
            return getDisplayName();
        }
        
        public ModelItem[] getChildren() {
            if ( group == null ) {
                return null;
            }
            else {
                if ( children == null ) {
                    Children ch = PackageView.createPackageView( group.getRootFolder() );
                    Node nodes[] = ch.getNodes( true );
                    children = new ModelItem[ nodes.length ];
                    for( int i = 0; i < nodes.length; i++ ) {
                        children[i] = new ModelItem( nodes[i] );
                    }
                }
                return children;
            }
        }
        
    }
    
    private static class NodeCellRenderer extends JLabel implements ListCellRenderer {
    
        public NodeCellRenderer() {
            setOpaque( true );
        }
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            if (value instanceof ModelItem) {
                ModelItem item = (ModelItem)value;
                setText( item.getDisplayName() );
                setIcon( item.getIcon() );
            } 
            else {
                setText( value.toString () );
                setIcon( null );
            }
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());             
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
             
            }
            return this;        
        }
                
    }
    
}
