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

package org.netbeans.modules.cnd.api.utils;

import org.netbeans.modules.cnd.loaders.CDataLoader;
import org.openide.util.NbBundle;

public class CSourceFileFilter extends SourceFileFilter{
    
    private static CSourceFileFilter instance = null;
    
    private String[] suffixList = null;
    
    public static SourceFileFilter getInstance() {
        if (instance == null)
            instance = new CSourceFileFilter();
        return instance;
    }
    
    public String getDescription() {
        return NbBundle.getMessage(SourceFileFilter.class, "FILECHOOSER_C_SOURCES_FILEFILTER", getSuffixesAsString()); // NOI18N
    }
    
    public String[] getSuffixes() {
        if (suffixList == null) {
            suffixList = getSuffixList(CDataLoader.getInstance().getExtensions());
        }
        return suffixList;
    }
}
