/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.makeproject.packaging;

import java.util.List;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.PackagingConfiguration;
import org.openide.util.NbBundle;

/**
 *
 * @author thp
 */
public class ZipPackager implements PackagerDescriptor {
    public static String PACKAGER_NAME = "Zip"; // NOI18N

    public String getName() {
        return PACKAGER_NAME;
    }

    public String getDisplayName() {
        return getString("Zip");
    }
    
    public boolean hasInfoList() {
        return false;
    }
    
    public List<PackagerInfoElement> getDefaultInfoList(MakeConfiguration makeConfiguration) {
        return null;
    }

    public String getDefaultOptions() {
        return ""; // NOI18N
    }

    public String getDefaultTool() {
        return "zip"; // NOI18N
    }

    public boolean isOutputAFolder() {
        return false;
    }
    
    public String getOutputFileName(MakeConfiguration makeConfiguration) {
        return makeConfiguration.getPackagingConfiguration().getOutputName();
    }
    
    public String getOutputFileSuffix() {
        return "zip";  // NOI18N
    }

    public String getTopDir(MakeConfiguration makeConfiguration) {
        String topDir = IpeUtils.getBaseName(makeConfiguration.getPackagingConfiguration().getOutputValue());
        
        int i = topDir.lastIndexOf("."); // NOI18N
        if (i > 0) {
            topDir = topDir.substring(0, i);
        }
        return topDir;
    }

    
    /** Look up i18n strings here */
    private static String getString(String s) {
        return NbBundle.getMessage(PackagingConfiguration.class, s); // FIXUP: Using Bundl in .../api.configurations. Too latet to move bundles around
    }
}
