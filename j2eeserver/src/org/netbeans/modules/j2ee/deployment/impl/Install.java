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

package org.netbeans.modules.j2ee.deployment.impl;

import java.util.Collection;
import java.util.Iterator;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.deployment.impl.ui.DeployProgressMonitor;

/**
 *
 * Install.java
 *
 * Created on January 7, 2004, 7:34 AM
 * @author  nn136682
 */
public class Install extends org.openide.modules.ModuleInstall {
    
    /** Creates a new instance of Install */
    public Install() {
    }
    
    public void close() {
        Collection instances = ServerRegistry.getInstance().getInstances();
        String title = NbBundle.getMessage(Install.class, "LBL_ShutDownServers");
        
        DeployProgressMonitor ui = new DeployProgressMonitor(title, true, false);
        for (Iterator i=instances.iterator(); i.hasNext();) {
            ServerInstance inst = (ServerInstance) i.next();
            java.util.Set targets = inst.getTargetsStartedByIde();
            for (Iterator j=targets.iterator(); j.hasNext();) {
                ServerTarget target = (ServerTarget) j.next();
                target.stop(ui);
            }
            if (inst.startedByIde()) {
                inst.stop(ui);
            }
        }
    }
}
