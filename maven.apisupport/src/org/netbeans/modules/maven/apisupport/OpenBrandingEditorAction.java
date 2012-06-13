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

package org.netbeans.modules.maven.apisupport;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.api.BrandingUtils;
import org.netbeans.modules.apisupport.project.api.ManifestManager;
import org.netbeans.modules.apisupport.project.spi.BrandingModel;
import org.netbeans.modules.apisupport.project.spi.BrandingSupport;
import org.netbeans.modules.apisupport.project.spi.PlatformJarProvider;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import static org.netbeans.modules.maven.apisupport.Bundle.*;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Opens branding editor window for 'branding' sub-project of a Maven app suite.
 * 
 * @author S. Aubrecht
 */
@ActionID(id = "org.netbeans.modules.maven.apisupport.OpenBrandingEditorAction", category = "Project")
@ActionRegistration(displayName = "#LBL_OpenBrandingEditor", lazy=false)
@ActionReference(position = 3150, path = "Projects/org-netbeans-modules-maven/Actions")
@Messages("LBL_OpenBrandingEditor=Branding...")
public class OpenBrandingEditorAction extends AbstractAction implements ContextAwareAction {

    private final Lookup context;
    private static final RequestProcessor RP = new RequestProcessor(OpenBrandingEditorAction.class);

    public OpenBrandingEditorAction() {
        this( Lookup.EMPTY );
    }

    private OpenBrandingEditorAction( Lookup context ) {
        super(LBL_OpenBrandingEditor());
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RP.post(new Runnable() {
            @Override public void run() {
                final Project project = context.lookup(Project.class);
                final MavenProject mavenProject = project.getLookup().lookup(NbMavenProject.class).getMavenProject();
                final BrandingModel model = createBrandingModel(project, brandingPath(mavenProject));
                EventQueue.invokeLater(new Runnable() {
                    @Override public void run() {
                        BrandingUtils.openBrandingEditor(mavenProject.getName(), project, model);
                    }
                });
            }
        });
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new OpenBrandingEditorAction(actionContext);
    }

    public @Override boolean isEnabled() {
        Project project = context.lookup(Project.class);
        if (project == null) {
            return false;
        }
        NbMavenProject mproject = project.getLookup().lookup(NbMavenProject.class);
        if (mproject == null) {
            return false;
        }
        return project.getProjectDirectory().getFileObject(brandingPath(mproject.getMavenProject())) != null;
    }

    private String brandingPath(MavenProject mavenProject) {
        String brandingPath = PluginPropertyUtils.getPluginProperty(mavenProject, MavenNbModuleImpl.GROUPID_MOJO, MavenNbModuleImpl.NBM_PLUGIN, "brandingSources", "branding"); //NOI18N
        return brandingPath != null ? brandingPath : "src/main/nbm-branding"; //NOI18N
    }

    /**
     * Creates a basic branding model.
     * @param p Project to be branded.
     * @param brandingPath Path relative to project's directory where branded are stored in.
     */
    private static BrandingModel createBrandingModel(final Project p, final String brandingPath) {
        BrandingModel model = new BrandingModel() {
            @Override public Project getProject() {
                return p;
            }
            @Override protected File getProjectDirectoryFile() {
                return FileUtil.toFile(p.getProjectDirectory());
            }
            @Override protected BrandingSupport createBranding() throws IOException {
                return new GenericBrandingSupport(p, brandingPath);
            }
            @Override protected boolean isBrandingEnabledRefresh() {
                return true;
            }
            @Override protected String loadName() {
                return null;
            }
            @Override protected String loadTitle() {
                return null;
            }
        };
        model.init();
        return model;
    }

    private static class GenericBrandingSupport extends BrandingSupport {

        private final Project p;
        private Map<String,BrandableModuleImpl> modules;

        GenericBrandingSupport(Project p, String brandingPath) throws IOException {
            super(p, brandingPath);
            this.p = p;
        }

        @Override protected BrandableModule findBrandableModule(String moduleCodeNameBase) {
            return modules.get(moduleCodeNameBase);
        }

        @Override public Set<File> getBrandableJars() {
            Set<File> jars = new HashSet<File>();
            for (BrandableModuleImpl m : modules.values()) {
                jars.add(m.jar);
            }
            return jars;
        }

        @Override protected Set<BrandableModule> loadModules() throws IOException {
            if (modules != null) {
                // XXX is there some way to tell that the effective platform has changed for e.g. a Maven module?
                return null;
            }
            modules = new HashMap<String,BrandableModuleImpl>();
            PlatformJarProvider pjp = p.getLookup().lookup(PlatformJarProvider.class);
            if (pjp != null) {
                for (File jar : pjp.getPlatformJars()) {
                    ManifestManager mfm = ManifestManager.getInstanceFromJAR(jar);
                    String cnb = mfm.getCodeNameBase();
                    if (cnb != null) {
                        modules.put(cnb, new BrandableModuleImpl(cnb, jar));
                    }
                }
            }
            return new HashSet<BrandableModule>(modules.values());
        }

        @Override protected Map<String,String> localizingBundle(BrandableModule moduleEntry) {
            File jar = ((BrandableModuleImpl) moduleEntry).jar;
            ManifestManager mfm = ManifestManager.getInstanceFromJAR(jar);
            String localizingBundle = mfm.getLocalizingBundle();
            if (localizingBundle == null) {
                return Collections.emptyMap();
            }
            EditableProperties props = new EditableProperties(false);
            try {
                InputStream is = new URL("jar:" + Utilities.toURI(jar) + "!/" + localizingBundle).openStream();
                try {
                    props.load(is);
                } finally {
                    is.close();
                }
            } catch (IOException x) {
                Exceptions.printStackTrace(x);
            }
            return props;
        }

        private static class BrandableModuleImpl implements BrandableModule {

            private final String cnb;
            private final File jar;

            BrandableModuleImpl(String cnb, File jar) {
                this.cnb = cnb;
                this.jar = jar;
            }

            @Override public String getCodeNameBase() {
                return cnb;
            }

            @Override public File getJarLocation() {
                return jar;
            }

            @Override public boolean equals(Object obj) {
                if (!(obj instanceof BrandableModuleImpl)) {
                    return false;
                }
                BrandableModuleImpl o = (BrandableModuleImpl) obj;
                return cnb.equals(o.cnb) && jar.equals(o.jar);
            }

            @Override public int hashCode() {
                return cnb.hashCode();
            }

            @Override public String toString() {
                return cnb;
            }

            @Override public String getRelativePath() {
                String base;
                if (cnb.equals("org.netbeans.bootstrap")) {
                    base = "boot";
                } else if (cnb.equals("org.netbeans.core.startup")) {
                    base = "core";
                } else {
                    base = cnb.replace('.', '-');
                }
                String dir;
                if (cnb.matches("org[.](netbeans[.]bootstrap|openide[.](modules|util(|[.]lookup)))")) {
                    dir = "lib";
                } else if (cnb.matches("org[.](netbeans[.]core[.]startup|openide[.]filesystems)")) {
                    dir = "core";
                } else {
                    dir = "modules";
                }
                return dir + '/' + base + ".jar";
            }

        }

    }

}
