/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cnd.makeproject.api.MakeArtifact;
import org.netbeans.modules.cnd.makeproject.ui.utils.PathPanel;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class MakeArtifactChooser extends JPanel implements PropertyChangeListener {
    
    // XXX to become an array later
    private String artifactType;
    
    /** Creates new form JarArtifactChooser */
    public MakeArtifactChooser( String artifactType, JFileChooser chooser ) {
        this.artifactType = artifactType;
        
        initComponents();
        listArtifacts.setModel( new DefaultListModel() );
        chooser.addPropertyChangeListener( this );

	PathPanel pathPanel = new PathPanel();
	leftPanel.add(pathPanel);
        
        // Accessibility
        listArtifacts.getAccessibleContext().setAccessibleDescription(getString("PROJECT_LIBRARY_FILES_AD"));
        libFilesLabel.setDisplayedMnemonic(getString("PROJECT_LIBRARY_FILES_MN").charAt(0));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        libFilesLabel = new javax.swing.JLabel();
        scrollPane1 = new javax.swing.JScrollPane();
        listArtifacts = new javax.swing.JList();
        leftPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        projectLabel.setLabelFor(projectTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle").getString("PROJECT_NAME_TXT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        add(projectLabel, gridBagConstraints);

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 6, 0);
        add(projectTextField, gridBagConstraints);
        projectTextField.getAccessibleContext().setAccessibleDescription(null);

        libFilesLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle").getString("PROJECT_LIBRARY_FILES_MN").charAt(0));
        libFilesLabel.setLabelFor(listArtifacts);
        org.openide.awt.Mnemonics.setLocalizedText(libFilesLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle").getString("PROJECT_LIBRARY_FILES_TXT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        add(libFilesLabel, gridBagConstraints);

        scrollPane1.setViewportView(listArtifacts);
        listArtifacts.getAccessibleContext().setAccessibleDescription(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(scrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(leftPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    public void propertyChange(PropertyChangeEvent e) {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())) {
            // We have to update the Accessory
            JFileChooser chooser = (JFileChooser) e.getSource();
            File dir = chooser.getSelectedFile(); // may be null (#46744)
            Project project = getProject(dir); // may be null
            populateAccessory(project);
        }
    }
    
    private Project getProject( File projectDir ) {
        
        if (projectDir == null) { // #46744
            return null;
        }
        
        try {            
            File normProjectDir = FileUtil.normalizeFile(projectDir);
            FileObject fo = FileUtil.toFileObject(normProjectDir);
            if (fo != null) {
                return ProjectManager.getDefault().findProject(fo);
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            // Return null
        }
        
        return null;
    }    
    
    /**
     * Set up GUI fields according to the requested project.
     * @param project a subproject, or null
     */
    private void populateAccessory( Project project ) {
        
        DefaultListModel model = (DefaultListModel)listArtifacts.getModel();
        model.clear();
        projectTextField.setText(project == null ? "" : ProjectUtils.getInformation(project).getDisplayName()); //NOI18N
        
        if ( project != null ) {
	    MakeArtifact[] artifacts = MakeArtifact.getMakeArtifacts(project);
	    int def = 0;
            for (int i = 0; i < artifacts.length; i++) {
		if (artifacts[i].getConfigurationType() == MakeArtifact.TYPE_APPLICATION)
		    continue;
		if (artifacts[i].getConfigurationType() == MakeArtifact.TYPE_UNKNOWN &&
		    (artifacts[i].getOutput().endsWith(".a") || artifacts[i].getOutput().endsWith(".so") || artifacts[i].getOutput().endsWith(".dll")) || // NOI18N
		    artifacts[i].getConfigurationType() == MakeArtifact.TYPE_DYNAMIC_LIB ||
		    artifacts[i].getConfigurationType() == MakeArtifact.TYPE_STATIC_LIB)
                    model.addElement(artifacts[i]);
		    if (artifacts[i].getActive())
			def = i;
            }
            listArtifacts.setSelectionInterval(def, def);
        }
    }
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel leftPanel;
    private javax.swing.JLabel libFilesLabel;
    private javax.swing.JList listArtifacts;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables

    
    /** Shows dialog with the artifact chooser 
     * @return null if canceled selected jars if some jars selected
     */
    public static MakeArtifact[] showDialog( String artifactType, Project master, Component parent ) {
        
        JFileChooser chooser = ProjectChooser.projectChooser();
        chooser.getAccessibleContext().setAccessibleDescription(getString("ADD_PROJECT_DIALOG_AD"));
        chooser.setDialogTitle(getString("ADD_PROJECT_DIALOG_TITLE"));
        chooser.setApproveButtonText(getString("ADD_BUTTON_TXT"));
        //chooser.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage (MakeArtifactChooser.class,"AD_AACH_SelectProject"));
        MakeArtifactChooser accessory = new MakeArtifactChooser( artifactType, chooser );
        chooser.setAccessory( accessory );
        chooser.setPreferredSize( new Dimension( 650, 380 ) );
        //chooser.setCurrentDirectory (FoldersListSettings.getDefault().getLastUsedArtifactFolder());

        int option = chooser.showOpenDialog( parent ); // Show the chooser
              
        if ( option == JFileChooser.APPROVE_OPTION ) {

            File dir = chooser.getSelectedFile();
            dir = FileUtil.normalizeFile (dir);
            Project selectedProject = accessory.getProject( dir );

            if ( selectedProject == null ) {
                return null;
            }
            
            if ( selectedProject.getProjectDirectory().equals( master.getProjectDirectory() ) ) {
                DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message( 
                    getString("ADD_ITSELF_ERROR"),
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
                return null;
            }
            
	    // FIXUP: need to check for this
            if ( ProjectUtils.hasSubprojectCycles( master, selectedProject ) ) {
                DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message( 
                    getString("ADD_CYCLIC_ERROR"), 
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
                return null;
            }
            
            //FoldersListSettings.getDefault().setLastUsedArtifactFolder (FileUtil.normalizeFile(chooser.getCurrentDirectory()));
            
            Object[] tmp = new Object[accessory.listArtifacts.getModel().getSize()];
            int count = 0;
            for(int i = 0; i < tmp.length; i++) {
                if (accessory.listArtifacts.isSelectedIndex(i)) {
                    tmp[count] = accessory.listArtifacts.getModel().getElementAt(i);
                    count++;
                }
            }
            MakeArtifact artifactItems[] = new MakeArtifact[count];
            System.arraycopy(tmp, 0, artifactItems, 0, count);
            return artifactItems;
        }
        else {
            return null; 
        }
                
    }
       
    /**
     * Pair of MakeArtifact and one of jars it produces.
     */
    public static class ArtifactItem {
        
        private MakeArtifact artifact;
        private URI artifactURI;
        
        public ArtifactItem(MakeArtifact artifact, URI artifactURI) {
            this.artifact = artifact;
            this.artifactURI = artifactURI;
        }
        
        public MakeArtifact getArtifact() {
            return artifact;
        }
        
        public URI getArtifactURI() {
            return artifactURI;
        }
        
        public String toString() {
            return artifactURI.toString();
        }
        
    }
    
    private static String getString(String s) {
        return NbBundle.getBundle(MakeArtifactChooser.class).getString(s);
    }
}
