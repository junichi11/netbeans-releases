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

package org.netbeans.modules.project.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.project.ui.OpenProjectListSettings;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author  phrebejk
 */
public class ProjectChooserAccessory extends javax.swing.JPanel 
    implements ActionListener, PropertyChangeListener {
    
    private RequestProcessor.Task updateSubprojectsTask;    
    private ModelUpdater modelUpdater;
    
    /** Creates new form ProjectChooserAccessory */
    public ProjectChooserAccessory( JFileChooser chooser, boolean isOpenSubprojects, boolean isOpenAsMain ) {
        initComponents();
        
        modelUpdater = new ModelUpdater();
        updateSubprojectsTask = new RequestProcessor( "ProjectChooserAccesoryModelUpdater" ).create( modelUpdater );
        updateSubprojectsTask.setPriority( Thread.MIN_PRIORITY );
        
        // Listen on the subproject checkbox to change the option accordingly
        jCheckBoxSubprojects.setSelected( isOpenSubprojects );
        jCheckBoxSubprojects.addActionListener( this );
        
        // Listen on the main checkbox to change the option accordingly
        jCheckBoxMain.setSelected( isOpenAsMain );
        jCheckBoxMain.addActionListener( this );
        
        // Listen on the chooser to update the Accessory
        chooser.addPropertyChangeListener( this );
        
        // Set default list model for the subprojects list
        jListSubprojects.setModel( new DefaultListModel() );
        
        // Disable the Accessory. JFileChooser does not select a file
        // by default
        setAccessoryEnablement( false );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelProjectName = new javax.swing.JLabel();
        jTextFieldProjectName = new javax.swing.JTextField();
        jCheckBoxMain = new javax.swing.JCheckBox();
        jCheckBoxSubprojects = new javax.swing.JCheckBox();
        jScrollPaneSubprojects = new javax.swing.JScrollPane();
        jListSubprojects = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 12, 0, 0)));
        jLabelProjectName.setLabelFor(jTextFieldProjectName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelProjectName, org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_ProjectName_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jLabelProjectName, gridBagConstraints);

        jTextFieldProjectName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jTextFieldProjectName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxMain, org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_Main_CheckBox"));
        jCheckBoxMain.setMargin(new java.awt.Insets(2, 0, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxMain, gridBagConstraints);
        jCheckBoxMain.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSD_ProjectChooserAccessory_jCheckBoxMain"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxSubprojects, org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "LBL_PrjChooser_Subprojects_CheckBox"));
        jCheckBoxSubprojects.setMargin(new java.awt.Insets(2, 0, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(jCheckBoxSubprojects, gridBagConstraints);
        jCheckBoxSubprojects.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSD_ProjectChooserAccessory_jCheckBoxSubprojects"));

        jListSubprojects.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListSubprojects.setEnabled(false);
        jScrollPaneSubprojects.setViewportView(jListSubprojects);
        jListSubprojects.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSN_ProjectChooserAccessory_jListSubprojects"));
        jListSubprojects.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectChooserAccessory.class, "ACSD_ProjectChooserAccessory_jListSubprojects"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPaneSubprojects, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxMain;
    private javax.swing.JCheckBox jCheckBoxSubprojects;
    private javax.swing.JLabel jLabelProjectName;
    private javax.swing.JList jListSubprojects;
    private javax.swing.JScrollPane jScrollPaneSubprojects;
    private javax.swing.JTextField jTextFieldProjectName;
    // End of variables declaration//GEN-END:variables
    
    // Implementation of action listener ---------------------------------------
    
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == jCheckBoxSubprojects ) {
            OpenProjectListSettings.getInstance().setOpenSubprojects( jCheckBoxSubprojects.isSelected() );
        }
        else if ( e.getSource() == jCheckBoxMain ) {
            OpenProjectListSettings.getInstance().setOpenAsMain( jCheckBoxMain.isSelected() );
        }
    }

    // Implementayion of PropertyChange listener
    
    public void propertyChange( PropertyChangeEvent e ) {
        if ( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals( e.getPropertyName() ) ) {             
            // We have to update the Accessory
            JFileChooser chooser = (JFileChooser)e.getSource();
            File rawdir = chooser.getSelectedFile();
            File dir = rawdir != null ? FileUtil.normalizeFile(rawdir) : null;
            DefaultListModel spListModel = (DefaultListModel)jListSubprojects.getModel();
            
            Project project = null;            
            if ( isProjectDir( dir ) ) {
                project = getProject( dir );
                // may still be null
            }
            
            if (project != null) {
                // Enable all components acessory
                setAccessoryEnablement( true );
                
                jTextFieldProjectName.setText(ProjectUtils.getInformation(project).getDisplayName());
                
                spListModel.clear();                
                
                modelUpdater.project = project;
                updateSubprojectsTask.schedule( 100 );                
            }
            else {            
                // Clear the accessory data if the dir is not project dir
                jTextFieldProjectName.setText( "" ); // NOI18N
                modelUpdater.project = null;
                spListModel.clear();
                
                // Disable all components in accessory
                setAccessoryEnablement( false );
            }
                        
        }
        else if ( JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals( e.getPropertyName() ) ) {
            // Selection lost => disable accessory
            setAccessoryEnablement( false );
        }
    }    
    
    
    // Private methods ---------------------------------------------------------
    
    private static boolean isProjectDir( File dir ) {
        boolean retVal = false;
        if (dir != null) {
            FileObject fo = convertToValidDir(dir);
            if (fo != null) {
                if ( Utilities.isUnix() && fo.getParent() != null && fo.getParent().getParent() == null  ) {
                    retVal = false; // Ignore all subfolders of / on unixes (e.g. /net, /proc)
                }
                else {
                    retVal = ProjectManager.getDefault().isProject( fo );    
                }
            }            
        }
        return retVal;
    }
    
    private static FileObject convertToValidDir(File f) {
        FileObject fo;
        File testFile = new File( f.getPath() );
        if ( testFile == null || testFile.getParent() == null ) {
            // BTW this means that roots of file systems can't be project
            // directories.
            return null;
        }
        
        /**ATTENTION: on Windows may occure dir.isDirectory () == dir.isFile () == true then
         * its used testFile instead of dir. 
        */    
        if ( !testFile.isDirectory() ) {
            return null;
        }
            
        fo =  FileUtil.toFileObject(FileUtil.normalizeFile(f));
        return fo;
    }
    
    private static Project getProject( File dir ) {
        return OpenProjectList.fileToProject( dir );
    }
    
    private void setAccessoryEnablement( boolean enable ) {
        jLabelProjectName.setEnabled( enable );
        jTextFieldProjectName.setEnabled( enable );
        jCheckBoxMain.setEnabled( enable );
        jCheckBoxSubprojects.setEnabled( enable );
        jScrollPaneSubprojects.setEnabled( enable );
    }
        
    /** Gets all subprojects recursively
     */
    private void addSubprojects( Project p, List result ) {
        
        SubprojectProvider spp = (SubprojectProvider)p.getLookup().lookup( SubprojectProvider.class );
        
        if ( spp == null ) {
            return;
        }
        
        for( Iterator/*<Project>*/ it = spp.getSubprojects().iterator(); it.hasNext(); ) {
            Project sp = (Project)it.next(); 
            if ( !result.contains( sp ) ) {
                result.add( sp );
            }
            addSubprojects( sp, result );            
        }
        
    }
    
    /**
     * Get a slash-separated relative path from f1 to f2, if they are collocated
     * and this is possible.
     * May return null.
     */
    private static String relativizePath(File f1, File f2) {
        if (f1 == null || f2 == null) {
            return null;
        }
        if (!CollocationQuery.areCollocated(f1, f2)) {
            return null;
        }
        // Copied from PropertyUtils.relativizeFile, more or less:
        StringBuffer b = new StringBuffer();
        File base = f1;
        String filepath = f2.getAbsolutePath();
        while (!filepath.startsWith(slashify(base.getAbsolutePath()))) {
            base = base.getParentFile();
            if (base == null) {
                return null;
            }
            b.append("../"); // NOI18N
        }
        URI u = base.toURI().relativize(f2.toURI());
        assert !u.isAbsolute() : u + " from " + f1 + " and " + f2 + " with common root " + base;
        b.append(u.getPath());
        if (b.charAt(b.length() - 1) == '/') {
            // file is an existing directory and file.toURI ends in /
            // we do not want the trailing slash
            b.setLength(b.length() - 1);
        }
        return b.toString();
    }
    private static String slashify(String path) {
        if (path.endsWith(File.separator)) {
            return path;
        } else {
            return path + File.separatorChar;
        }
    }
    
    
    // Other methods -----------------------------------------------------------
    
    /** Factory method for project chooser
     */    
    public static JFileChooser createProjectChooser( boolean defaultAccessory ) {
        
        OpenProjectListSettings opls = OpenProjectListSettings.getInstance();
        JFileChooser chooser = new ProjectFileChooser();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

        if ("GTK".equals(javax.swing.UIManager.getLookAndFeel().getID())) { // NOI18N
            // see BugTraq #5027268
            chooser.putClientProperty("GTKFileChooser.showDirectoryIcons", Boolean.TRUE); // NOI18N
            //chooser.putClientProperty("GTKFileChooser.showFileIcons", Boolean.TRUE); // NOI18N
        }
        
        chooser.setApproveButtonText( NbBundle.getMessage( ProjectChooserAccessory.class, "BTN_PrjChooser_ApproveButtonText" ) ); // NOI18N
        chooser.setApproveButtonMnemonic( NbBundle.getMessage( ProjectChooserAccessory.class, "MNM_PrjChooser_ApproveButtonText" ).charAt (0) ); // NOI18N
        chooser.setApproveButtonToolTipText (NbBundle.getMessage( ProjectChooserAccessory.class, "BTN_PrjChooser_ApproveButtonTooltipText")); // NOI18N
        chooser.setMultiSelectionEnabled( false );        
        chooser.setDialogTitle( NbBundle.getMessage( ProjectChooserAccessory.class, "LBL_PrjChooser_Title" ) ); // NOI18N
        chooser.setFileFilter( ProjectDirFilter.INSTANCE );        
        chooser.setAcceptAllFileFilterUsed( false );
        if ( defaultAccessory ) {
            chooser.setAccessory( new ProjectChooserAccessory( chooser, opls.isOpenSubprojects(), opls.isOpenAsMain() ) );
        }
        
        File currDir = null;
        String dir = opls.getLastOpenProjectDir();
        if ( dir == null ) {
            currDir = opls.getProjectsFolder();
        } else {
            File d = new File( dir );
            if ( d.exists() && d.isDirectory() ) {
                currDir = d;
            }
        }
        
        FileUtil.preventFileChooserSymlinkTraversal(chooser, currDir);
        chooser.setFileView( new ProjectFileView( chooser.getFileSystemView() ) );                
        
        return chooser;    
        
    }
    
    
    // Aditional innerclasses for the file chooser -----------------------------
    
    private static class ProjectFileChooser extends JFileChooser {
        
        public void approveSelection() {
            File dir = FileUtil.normalizeFile(getSelectedFile());
            
            if ( isProjectDir( dir ) && getProject( dir ) != null ) {
                super.approveSelection();
            }
            else {
                setCurrentDirectory( dir );
            }
            
        }
        
        
    }
    
    private static class ProjectDirFilter extends FileFilter {
        
        private static final FileFilter INSTANCE = new ProjectDirFilter( );
                       
        public boolean accept( File f ) {
            
            if ( f.isDirectory() ) {                
                return true; // Directory selected
            }
            
            return false;
        }        
        
        public String getDescription() {
            return NbBundle.getMessage( ProjectDirFilter.class, "LBL_PrjChooser_ProjectDirectoryFilter_Name" ); // NOI18N
        }        
        
    }
    
    private static class ProjectFileView extends FileView {
        
        private static final Icon BADGE = new ImageIcon(Utilities.loadImage("org/netbeans/modules/project/ui/resources/projectBadge.gif")); // NOI18N
        private static final Icon EMPTY = new ImageIcon(Utilities.loadImage("org/netbeans/modules/project/ui/resources/empty.gif")); // NOI18N
        
        private FileSystemView fsv;
        private Icon lastOriginal;
        private Icon lastMerged;
        
        public ProjectFileView( FileSystemView fsv ) {
            this.fsv = fsv;            
        }
                
        public Icon getIcon(File _f) {
            if (!_f.exists()) {
                // Can happen when a file was deleted on disk while project
                // dialog was still open. In that case, throws an exception
                // repeatedly from FSV.gSI during repaint.
                return null;
            }
            File f = FileUtil.normalizeFile(_f);
            Icon original = fsv.getSystemIcon(f);
            if (original == null) {
                // L&F (e.g. GTK) did not specify any icon.
                original = EMPTY;
            }
            if ( isProjectDir( f ) ) {
                if ( original.equals( lastOriginal ) ) {
                    return lastMerged;
                }
                lastOriginal = original;
                lastMerged = new MergedIcon(original, BADGE, -1, -1);                
                return lastMerged;
            }
            else {
                return original;
            }
        }
        
                
    }
    
    private static class MergedIcon implements Icon {
        
        private Icon icon1;
        private Icon icon2;
        private int xMerge;
        private int yMerge;
        
        MergedIcon( Icon icon1, Icon icon2, int xMerge, int yMerge ) {
            
            this.icon1 = icon1;
            this.icon2 = icon2;
            
            if ( xMerge == -1 ) {
                xMerge = icon1.getIconWidth() - icon2.getIconWidth();
            }
            
            if ( yMerge == -1 ) {
                yMerge = icon1.getIconHeight() - icon2.getIconHeight();
            }
            
            this.xMerge = xMerge;
            this.yMerge = yMerge;
        }
        
        public int getIconHeight() {
            return Math.max( icon1.getIconHeight(), yMerge + icon2.getIconHeight() );
        }
        
        public int getIconWidth() {
            return Math.max( icon1.getIconWidth(), yMerge + icon2.getIconWidth() );
        }
        
        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            icon1.paintIcon( c, g, x, y );
            icon2.paintIcon( c, g, x + xMerge, y + yMerge );
        }
        
    }
    
    private class ModelUpdater implements Runnable {

        volatile Project project;
        private DefaultListModel subprojectsToSet;
        
        public void run() {
            
            if ( !SwingUtilities.isEventDispatchThread() ) {
                Project currentProject = project;
                if ( currentProject == null ) {
                    return;
                }
                ArrayList subprojects = new ArrayList( 5 );
                addSubprojects( currentProject, subprojects ); // Find the projects recursively
                if ( !subprojects.isEmpty() ) {
                    String pattern = NbBundle.getMessage( ProjectChooserAccessory.class, "LBL_PrjChooser_SubprojectName_Format" ); // NOI18N
                    File pDir = FileUtil.toFile( currentProject.getProjectDirectory() );

                    // Replace projects in the list with formated names
                    for ( int i = 0; i < subprojects.size(); i++ ) {
                        Project p = (Project)subprojects.get( i );

                        // Try to compute relative path            
                        FileObject spDir = p.getProjectDirectory();
                        String relPath = relativizePath(pDir, FileUtil.toFile( spDir ));

                        if (relPath == null) {
                            // Cannot get a relative path; display it as absolute.
                            relPath = FileUtil.getFileDisplayName(spDir);
                        }
                        String displayName = MessageFormat.format( 
                            pattern, 
                            new Object[] { ProjectUtils.getInformation(p).getDisplayName(), relPath } );
                        subprojects.set( i, displayName );
                    }

                    // Sort the list
                    Collections.sort( subprojects, Collator.getInstance() );
                }
                if ( currentProject != project ) {
                    return;
                }
                DefaultListModel listModel = new DefaultListModel();
                // Put all the strings into the list model
                for( Iterator it = subprojects.iterator(); it.hasNext(); ) {
                    listModel.addElement( it.next() );
                }
                subprojectsToSet = listModel;
                SwingUtilities.invokeLater( this );
                return;
            }
            else {
                if ( project == null ) {
                    ((DefaultListModel)jListSubprojects.getModel()).clear();
                    jCheckBoxSubprojects.setEnabled( false );
                }
                else {
                    jListSubprojects.setModel(subprojectsToSet);
                    // If no soubprojects checkbox should be disabled                
                    jCheckBoxSubprojects.setEnabled( !subprojectsToSet.isEmpty() );
                    project = null;
                }                
            }
                        
        }
    
    
    }
    
    
}
