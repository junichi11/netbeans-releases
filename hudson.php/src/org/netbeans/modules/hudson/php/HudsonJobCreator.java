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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudson.php;

import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.hudson.spi.HudsonSCM;
import org.netbeans.modules.hudson.spi.ProjectHudsonJobCreatorFactory;
import org.netbeans.modules.hudson.spi.ProjectHudsonJobCreatorFactory.ConfigurationStatus;
import org.netbeans.modules.hudson.spi.ProjectHudsonJobCreatorFactory.Helper;
import org.netbeans.modules.hudson.spi.ProjectHudsonJobCreatorFactory.ProjectHudsonJobCreator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;

public class HudsonJobCreator extends JPanel implements ProjectHudsonJobCreator {

    private static final long serialVersionUID = -668435132135465L;

    private final PhpModule phpModule;
    private final HudsonSCM.Configuration scm;


    public HudsonJobCreator(PhpModule phpModule) {
        this.phpModule = phpModule;
        scm = Helper.prepareSCM(FileUtil.toFile(phpModule.getProjectDirectory()));
    }

    @Override
    public String jobName() {
        return phpModule.getDisplayName();
    }

    @Override
    public JComponent customizer() {
        return this;
    }

    @NbBundle.Messages({
        "MsgNoTests=The project does not have any tests.",
        "MsgBuildXmlExists=The project already has build.xml file.",
        "MsgPhpUnitConfigExists=The project already has phpunit.xml.dist file."
    })
    @Override
    public ConfigurationStatus status() {
        if (phpModule.getTestDirectory() == null) {
            return ConfigurationStatus.withError(Bundle.MsgNoTests());
        }
        if (scm == null) {
            return Helper.noSCMError();
        }
        // XXX check ppw setup and if error - add extra button to open IDE options
        FileObject buildXml = phpModule.getProjectDirectory().getFileObject("build.xml");
        if (buildXml != null && buildXml.isData()) {
            return ConfigurationStatus.withError(Bundle.MsgBuildXmlExists());
        }
        FileObject phpUnitConfig = phpModule.getProjectDirectory().getFileObject("phpunit.xml.dist");
        if (phpUnitConfig != null && phpUnitConfig.isData()) {
            return ConfigurationStatus.withError(Bundle.MsgPhpUnitConfigExists());
        }
        ConfigurationStatus scmStatus = scm.problems();
        if (scmStatus != null) {
            return scmStatus;
        }
        return ConfigurationStatus.valid();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }

    @Override
    public Document configure() throws IOException {
        setupProject();
        return createJobXml();
    }

    private void setupProject() {
        // XXX call ppw and process generated build.xml and phpunit.xml.dist according to checkboxes
    }

    private Document createJobXml() {
        Document doc = XMLUtil.createDocument("project", null, null, null); // NOI18N
        // XXX copy & process jenkins-php template (<description> - url)
        scm.configure(doc);
        Helper.addLogRotator(doc);
        return doc;
    }

    //~ Inner classes

    @ServiceProvider(service=ProjectHudsonJobCreatorFactory.class, position=300)
    public static class Factory implements ProjectHudsonJobCreatorFactory {
        @Override
        public ProjectHudsonJobCreator forProject(Project project) {
            PhpModule phpModule = project.getLookup().lookup(PhpModule.class);
            if (phpModule == null) {
                // not a php project
                return null;
            }
            return new HudsonJobCreator(phpModule);
        }
    }

}
