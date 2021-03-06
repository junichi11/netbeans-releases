/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium2.webclient.mocha;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.selenium2.webclient.api.Utilities;
import org.netbeans.modules.selenium2.webclient.mocha.preferences.MochaJSPreferences;
import org.netbeans.modules.web.clientproject.api.jstesting.JsTestingProviders;
import org.netbeans.modules.web.clientproject.api.jstesting.TestRunInfo;
import org.netbeans.modules.web.clientproject.spi.CustomizerPanelImplementation;
import org.netbeans.modules.web.clientproject.spi.jstesting.JsTestingProviderImplementation;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Theofanis Oikonomou
 */
@ServiceProvider(service = JsTestingProviderImplementation.class, path = JsTestingProviders.JS_TESTING_PATH, position = 300)
public class MochaJSTestingProvider implements JsTestingProviderImplementation {

    private static final Logger LOGGER = Logger.getLogger(MochaJSTestingProvider.class.getName());


    @Override
    public String getIdentifier() {
        return CustomizerMochaPanel.IDENTIFIER;
    }

    @Override
    public String getDisplayName() {
        return Bundle.CustomizerMochaPanel_displayName();
    }

    @Override
    public boolean isEnabled(Project project) {
        return MochaJSPreferences.isEnabled(project);
    }

    @Override
    public boolean isCoverageSupported(Project project) {
        return false;
    }

    @Override
    public void runTests(Project project, TestRunInfo runInfo) {
        String testFile = runInfo.getTestFile();
        FileObject[] activatedFOs = new FileObject[1];
        if(testFile == null) {
            activatedFOs[0] = project.getProjectDirectory();
        } else {
            activatedFOs[0] = FileUtil.toFileObject(new File(testFile));
        }
        MochaRunner.runTests(activatedFOs, false);
    }

    @Override
    public FileObject fromServer(Project project, URL serverUrl) {
        return null;
    }

    @Override
    public URL toServer(Project project, FileObject projectFile) {
        return null;
    }

    @Override
    public CustomizerPanelImplementation createCustomizerPanel(Project project) {
        return new CustomizerMochaPanel(project, false);
    }

    @Override
    public void notifyEnabled(Project project, boolean enabled) {
        MochaJSPreferences.setEnabled(project, enabled);
    }

    @Override
    public void projectOpened(Project project) {
        // noop
    }

    @Override
    public void projectClosed(Project project) {
        // noop
    }

    @Override
    public NodeList<Node> createNodeList(Project project) {
        return null;
    }

}
