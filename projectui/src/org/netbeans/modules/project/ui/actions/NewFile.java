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

package org.netbeans.modules.project.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.modules.project.ui.NewFileWizard;
import org.netbeans.modules.project.ui.OpenProjectList;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/** Action for invoking the project sensitive NewFile Wizard
 */
public class NewFile extends ProjectAction implements PropertyChangeListener {

    private static final Icon ICON = new ImageIcon( Utilities.loadImage( "org/netbeans/modules/project/ui/resources/newFile.gif" ) ); //NOI18N        
    private static final String name = NbBundle.getMessage(NewFile.class, "LBL_NewFileAction_Name"); // NI18N
    
    public NewFile() {
        this( null );
    }
    
    public NewFile( Lookup context ) {
        super( (String)null, name, ICON, context ); //NOI18N        
        OpenProjectList.getDefault().addPropertyChangeListener( this );
        refresh( getLookup() );
    }

    protected void refresh( Lookup context ) {
        setEnabled( OpenProjectList.getDefault().getOpenProjects().length > 0 );
        setDisplayName( name );
    }

    //private NewFileWizard wizardIterator;  

    protected void actionPerformed( Lookup context ) {

        TemplateWizard wd = new NewFileWizard( preselectedProject( context ), null ); //wizardIterator );

        DataFolder preselectedFolder = preselectedFolder( context );
        if ( preselectedFolder != null ) {
            wd.setTargetFolder( preselectedFolder );
        }

        try { 
            wd.instantiate();
        }
        catch ( IOException e ) {
            // XXX
            e.printStackTrace();
        }

    }
    
    public Action createContextAwareInstance( Lookup actionContext ) {
        return new NewFile( actionContext );
    }

    private Project preselectedProject( Lookup context ) {
        Project preselectedProject = null;

        // if ( activatedNodes != null && activatedNodes.length != 0 ) {

        Project[] projects = ActionsUtil.getProjectsFromLookup( context, null );
        if ( projects.length > 0 ) {
            preselectedProject = projects[0];
        }

        
        if ( preselectedProject == null ) {
            // No project context => use main project
            preselectedProject = OpenProjectList.getDefault().getMainProject();
            if ( preselectedProject == null ) {
                // No main project => use the first one
                preselectedProject = OpenProjectList.getDefault().getOpenProjects()[0];
            }
        }

        if ( preselectedProject == null ) {
            assert false : "Action should be disabled"; // NOI18N
        }

        return preselectedProject;    
    }

    private DataFolder preselectedFolder( Lookup context ) {
        
        DataFolder preselectedFolder = null;
        
        // Try to find selected folder
        preselectedFolder = (DataFolder)context.lookup( DataFolder.class );
        if ( preselectedFolder == null ) {
            // No folder selectd try with DataObject
            DataObject dobj = (DataObject)context.lookup( DataObject.class );
            if ( dobj != null) {
                // DataObject found => we'll use the parent folder
                preselectedFolder = dobj.getFolder();
            }
        }
        
        return preselectedFolder;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        refresh( Lookup.EMPTY );
    }
    
}
    
    
    