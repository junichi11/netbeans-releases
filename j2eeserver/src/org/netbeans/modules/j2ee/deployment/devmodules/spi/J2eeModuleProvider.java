/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.deployment.devmodules.spi;

import org.netbeans.modules.j2ee.deployment.devmodules.api.*;
import org.netbeans.modules.j2ee.deployment.impl.projects.J2eeDeploymentLookup;
import org.netbeans.modules.j2ee.deployment.impl.projects.ExecutionProfileEditor;
import org.openide.loaders.*;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

import org.openide.util.actions.SystemAction;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Sheet;

import org.netbeans.modules.j2ee.deployment.execution.actions.ConfigureAction;
import org.netbeans.modules.j2ee.deployment.execution.ServerExecSupport;

/**
 *
 * @author  Pavel Buzek
 */
public abstract class J2eeModuleProvider implements Node.Cookie {
    
    public abstract J2eeModule getJ2eeModule ();
// settings will be stored in filesystems attributes of {@link getModuleFolder}
//    public abstract Context getContext ();
    public abstract ModuleChangeReporter getModuleChangeReporter ();
    
    /** A folder that can be used for storig confugration data , etc.
     * Should be part of VCSed files tree but adding files/folders must not 
     * clutter the module sources.
     */
    public abstract FileObject getModuleFolder ();
    
    /** This cookie must be added by modules */
    public static final Node.Cookie cookieToAdd (DataObject obj) {
        return new J2eeDeploymentLookup (obj);
    }
    public static final java.util.Set cookieClasses () {
        java.util.Set set = new java.util.HashSet ();
        set.add (J2eeDeploymentLookup.class);
        return set;
    }
    
    static SystemAction configureAction;
    public static final SystemAction configureAction() {
        if (configureAction == null) {
            configureAction = NodeAction.get (ConfigureAction.class);
        }
        return configureAction;
    }

    public static org.openide.loaders.ExecutionSupport getExecutionSupport (DataObject dobj) {
        return new ServerExecSupport (((MultiDataObject) dobj).getPrimaryEntry ());
    }
} 