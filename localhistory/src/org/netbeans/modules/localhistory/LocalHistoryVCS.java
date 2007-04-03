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
 */
package org.netbeans.modules.localhistory;

import java.io.File;
import org.netbeans.modules.versioning.spi.OriginalContent;
import org.netbeans.modules.versioning.spi.VCSAnnotator;
import org.netbeans.modules.versioning.spi.VCSInterceptor;
import org.netbeans.modules.versioning.spi.VersioningSystem;
import org.openide.util.NbBundle;

/**
 *
 * Provides the VersioningSystem functionality to the IDE
 * 
 * @author Tomas Stupka
 */
public class LocalHistoryVCS extends VersioningSystem {
        
    public LocalHistoryVCS() {
        putProperty(PROP_DISPLAY_NAME, NbBundle.getMessage(LocalHistoryVCS.class, "CTL_DisplayName"));
        putProperty(PROP_MENU_LABEL, NbBundle.getMessage(LocalHistoryVCS.class, "CTL_MainMenuItem"));
        putProperty(PROP_LOCALHISTORY_VCS, Boolean.TRUE);
    }
    
    public File getTopmostManagedParent(File file) {    
        if(file == null) {
            return null;
        }                
        return LocalHistory.getInstance().isManagedByParent(file);                             
    }
    
    public VCSAnnotator getVCSAnnotator() {
        return LocalHistory.getInstance().getVCSAnnotator();
    }
    
    public VCSInterceptor getVCSInterceptor() {
        return LocalHistory.getInstance().getVCSInterceptor();
    }
    
    public OriginalContent getVCSOriginalContent(File workingCopy) {
        // not supported yet
        return super.getVCSOriginalContent(workingCopy);
    }     
    
}
