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

package org.netbeans.modules.maven.j2ee.web;

import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.common.spi.ProjectWebRootProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author marekfukala
 */
@ProjectServiceProvider(
    service = {
        ProjectWebRootProvider.class
    },
    projectType = {
        "org-netbeans-modules-maven/" + NbMavenProject.TYPE_WAR,
        "org-netbeans-modules-maven/" + NbMavenProject.TYPE_JAR // #233476
    }
)
public class MavenWebProjectWebRootProvider implements ProjectWebRootProvider {

    private WebModuleProvider webModuleProvider;
    private Project project;

    public MavenWebProjectWebRootProvider(Project project) {
        this.project = project;
    }

    private synchronized WebModuleProvider getProvider() {
        if(webModuleProvider == null) {
            webModuleProvider = project.getLookup().lookup(WebModuleProvider.class);
        }
        return webModuleProvider;
    }

    @Override
    public FileObject getWebRoot(FileObject file) {
        WebModuleProvider provider = getProvider();
        WebModule wm = provider != null ? provider.findWebModule(file) : null;
        return wm != null ? wm.getDocumentBase() : null;
    }

    @Override
    public Collection<FileObject> getWebRoots() {
        WebModuleProvider provider = getProvider();
        if (provider == null) {
            return Collections.emptyList();
        }
        WebModule webModule = provider.findWebModule(project.getProjectDirectory());
        if (webModule == null) {
            return Collections.emptyList();
        }
        FileObject documentBase = webModule.getDocumentBase();
        if (documentBase == null) {
            return Collections.emptyList();
        }
        return Collections.singleton(documentBase);
    }

}
