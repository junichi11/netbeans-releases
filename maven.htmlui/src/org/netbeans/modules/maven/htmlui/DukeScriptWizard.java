/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.maven.htmlui;

import java.awt.EventQueue;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.netbeans.api.templates.TemplateRegistration;
import org.openide.util.NbBundle.Messages;
import net.java.html.json.Model;
import net.java.html.json.Property;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.ModelOperation;
import net.java.html.json.OnPropertyChange;
import net.java.html.json.OnReceive;
import org.openide.filesystems.FileChooserBuilder;

@Model(className = "WizardData", properties = {
    @Property(name = "current", type = String.class),
    @Property(name = "ok", type = boolean.class),
    @Property(name = "msg", type = String.class),
    @Property(name = "archetype", type = ArchetypeData.class),
    @Property(name = "archetypes", type = ArchetypeData.class, array = true),
    @Property(name = "android", type = boolean.class),
    @Property(name = "ios", type = boolean.class),
    @Property(name = "web", type = boolean.class),
    @Property(name = "netbeans", type = boolean.class),
    @Property(name = "installExample", type = boolean.class),
    @Property(name = "androidSdkPath", type = String.class),
    @Property(name = "nbhome", type = String.class),
    @Property(name = "nbInstallationDefined", type = boolean.class),
    @Property(name = "laf", type = String.class)
})
public class DukeScriptWizard {

    @TemplateRegistration(
            position = 133,
            page = "dukeScriptWizard.html",
            content = "dukescript.archetype",
            folder = "Project/JavaFX",
            displayName = "#DukeScriptWizard_displayName",
            iconBase = "org/netbeans/modules/maven/htmlui/DukeHTML.png",
            description = "description.html"
    )
    @Messages("DukeScriptWizard_displayName=Java HTML5 Application")
    public static WizardData javafxWebViewAppWizard() {
        WizardData data = new WizardData();
        data.init(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        return data;
    }

    private static void setUIDefaults(final WizardData data) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
                    String name = lookAndFeel.getName();
                    if (name.equals("Mac OS X")) name="mac";
                    else if (name.equals("Metal"))name="metal";
                    else if (name.equals("GTK look and feel"))name="gtk";
                    else if (name.equals("Nimbus"))name="nimbus";
                    else if (name.startsWith("Windows"))name="win";
                    final String laf = name;
                    data.setLaf("wizard-"+laf+".css");
                }
            });
       

    }
    
//    private static String toHex(Color c){  
//        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()); 
//    }

    @Model(className = "ArchetypeData", properties = {
        @Property(name = "artifactId", type = String.class),
        @Property(name = "groupId", type = String.class),
        @Property(name = "example", type = boolean.class),
        @Property(name = "version", type = String.class),
        @Property(name = "name", type = String.class),
        @Property(name = "description", type = String.class),
        @Property(name = "url", type = String.class),
        @Property(name = "open", array = true, type = String.class),})
    static class ArchetypeViewModel {
    }

    @ModelOperation
    static void init(WizardData data,
            Boolean android, Boolean ios, Boolean web, Boolean netbeans
    ) {
        setUIDefaults(data);
        File nbHome = new File(System.getProperty("netbeans.home"));
        data.setNbhome(nbHome.getParent());

        final ArchetypeData crudArch = new ArchetypeData(
                "crud4j-archetype",
                "com.dukescript.archetype",
                false,
                "0.17",
                "DukeScript CRUD Template", "Client-Server Application demonstrating communication and reuse of DataModels",
                null
        );
        data.getArchetypes().add(crudArch);
        final ArchetypeData koArch = new ArchetypeData(
                "knockout4j-archetype",
                "com.dukescript.archetype",
                true,
                "0.17",
                "Basic DukeScript Template", "Default skeletal application",
                null
        );
        data.setArchetype(koArch);
        data.getArchetypes().add(koArch);
        String srvPath = Boolean.getBoolean("staging.archetypes") ? "stage" : "archetypes";
        data.loadArchetypes(srvPath);
        data.setAndroidSdkPath(MavenUtilities.getDefault().readAndroidSdkPath());
    }

    @ComputedProperty
    static String archetypeGroupId(ArchetypeData archetype) {
        return archetype == null ? null : archetype.getGroupId();
    }

    @ComputedProperty
    static String archetypeArtifactId(ArchetypeData archetype) {
        return archetype == null ? null : archetype.getArtifactId();
    }

    @ComputedProperty
    static String archetypeVersion(ArchetypeData archetype) {
        return archetype == null ? null : archetype.getVersion();
    }

    @ComputedProperty
    static String archetypeOpen(ArchetypeData archetype) {
        StringBuilder sb = new StringBuilder();
        if (archetype != null) {
            for (String item : archetype.getOpen()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(item);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    @ComputedProperty
    static String webpath(boolean web) {
        return web ? "client-web" : null;
    }

    @ComputedProperty
    static String androidpath(boolean android) {
        return android ? "client-android" : null;
    }

    @ComputedProperty
    static String iospath(boolean ios) {
        return ios ? "client-ios" : null;
    }

    @ComputedProperty
    static String netbeanspath(boolean netbeans) {
        return netbeans ? "client-netbeans" : null;
    }

    @ComputedProperty
    static String example(boolean installExample) {
        return Boolean.valueOf(installExample).toString();
    }

    @ComputedProperty
    static int errorCode(
            String current,
            boolean android,
            String androidSdkPath,
            boolean netbeans,
            boolean nbInstallationDefined
    ) {
        if (android && "platforms".equals(current)) { // NOI18N
            if (androidSdkPath == null) {
                return 7;
            }
            if (!isValidAndroidSdk(new File(androidSdkPath))) {
                return 7;
            }
        }
        if (netbeans && "platforms".equals(current)) { // NOI18N
            if (!nbInstallationDefined) {
                return 8;
            }
        }
        return 0;
    }

    @Function
    static void chooseAndroidSDK(final WizardData data) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FileChooserBuilder b = new FileChooserBuilder(WizardData.class).
                        setSelectionApprover(new FileChooserBuilder.SelectionApprover() {
                            @Override
                            public boolean approve(File[] files) {
                                if (files.length != 1) {
                                    return false;
                                }
                                return isValidAndroidSdk(files[0]);
                            }
                        }).
                        setDirectoriesOnly(true);
                if (data.getAndroidSdkPath() != null) {
                    b.setDefaultWorkingDirectory(new File(data.getAndroidSdkPath()));
                }
                JFileChooser fc = b.createFileChooser();
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    data.setAndroidSdkPath(fc.getSelectedFile().getPath());
                    MavenUtilities.getDefault().writeAndroidSdkPath(fc.getSelectedFile().getPath());
                }
            }
        });
    }

    private static boolean isValidAndroidSdk(File dir) {
        return new File(new File(dir, "platform-tools"), "adb.exe").exists()
                || new File(new File(dir, "platform-tools"), "adb").exists();
    }

    @OnPropertyChange(value = "netbeans")
    static void verifyNbInstallationDefined(WizardData data) {
        boolean ok = !data.isNetbeans() || MavenUtilities.getDefault().readNetBeansInstallation() != null;
        data.setNbInstallationDefined(ok);
    }

    @Function
    static void defineNbInstallation(final WizardData data) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FileChooserBuilder b = new FileChooserBuilder(WizardData.class).
                        setSelectionApprover(new FileChooserBuilder.SelectionApprover() {
                            @Override
                            public boolean approve(File[] files) {
                                if (files.length != 1) {
                                    return false;
                                }
                                File platform = new File(files[0], "platform");
                                File lib = new File(platform, "lib");
                                File bootJar = new File(lib, "boot.jar");
                                return bootJar.exists();
                            }
                        }).
                        setDirectoriesOnly(true);
                JFileChooser fc = b.createFileChooser();
                File f = new File(System.getProperty("netbeans.home"));
                fc.setCurrentDirectory(f);
                int res = fc.showOpenDialog(null);
                if (res == JFileChooser.APPROVE_OPTION) {
                    MavenUtilities.getDefault().writeNetBeansInstallation(f.getParent());
                    if (MavenUtilities.getDefault().readNetBeansInstallation() != null) {
                        data.setNbInstallationDefined(true);
                    }
                }
            }
        });
    }

    @OnReceive(url = "http://dukescript.com/presenters/{path}", onError = "loadError")
    static void loadArchetypes(WizardData model, List<ArchetypeData> found) {
        if (!found.isEmpty()) {
            final ArchetypeData first = found.get(0);
            if (first == null || first.getName() == null) {
                model.setMsg("Loaded data are corrupted");
                return;
            }
            model.getArchetypes().clear();
            model.getArchetypes().addAll(found);
            model.setArchetype(first);
        }
    }

    static void loadError(WizardData model, Throwable t) {
        model.setMsg(t.getLocalizedMessage());
    }

}
