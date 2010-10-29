/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.modelimpl.trace;

import java.io.PrintWriter;
import java.util.Collection;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.services.CsmStandaloneFileProvider;
import org.netbeans.modules.cnd.debug.CndDiagnosticProvider;
import org.netbeans.modules.cnd.modelimpl.csm.core.CsmStandaloneFileProviderImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileSnapshot;
import org.netbeans.modules.cnd.modelimpl.csm.core.LibraryManager;
import org.netbeans.modules.cnd.modelimpl.csm.core.ModelImpl;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv159170
 */
@ServiceProvider(service=CndDiagnosticProvider.class, position=1000)
public class CodeModelDiagnostic implements CndDiagnosticProvider {

    @Override
    public String getDisplayName() {
        return "Code Model Impl"; // NOI18N
    }

    @Override
    public void dumpInfo(Lookup context, PrintWriter printOut) {
        printOut.printf("====CsmStandaloneFileProviders info:\n");// NOI18N
        for (CsmStandaloneFileProvider sap : Lookup.getDefault().lookupAll(CsmStandaloneFileProvider.class)) {
            if (sap instanceof CsmStandaloneFileProviderImpl) {
                ((CsmStandaloneFileProviderImpl)sap).dumpInfo(printOut);
            } else {
                printOut.printf("UKNOWN FOR ME [%s] %s\n", sap.getClass().getName(), sap.toString());// NOI18N 
            }
        }
        printOut.printf("====ModelImpl:\n");// NOI18N
        ModelImpl.instance().dumpInfo(printOut);
        printOut.printf("====Libraries:\n"); //NOI18N
        LibraryManager.getInstance().dumpInfo(printOut);
        printOut.printf("====Files info:\nGlobal ParseCount=%d\n", FileImpl.getParseCount());// NOI18N 
        Collection<? extends CsmFile> allFiles = context.lookupAll(CsmFile.class);
        for (CsmFile csmFile : allFiles) {
            if (csmFile instanceof FileImpl) {
                ((FileImpl) csmFile).dumpInfo(printOut);
            } else if (csmFile instanceof FileSnapshot) {
                ((FileSnapshot) csmFile).dumpInfo(printOut);
            } else {
                printOut.printf("UKNOWN FOR ME [%s] %s\n", csmFile.getClass().getName(), csmFile.toString());// NOI18N 
            }
        }
    }

}
