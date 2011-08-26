/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.javafx2.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.Document;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.java.api.common.project.ProjectProperties;
import org.netbeans.modules.java.j2seproject.api.J2SEPropertyEvaluator;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ui.StoreGroup;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.Utilities;

public final class JFXProjectProperties {

    public static final String JAVAFX_ENABLED = "javafx.enabled"; // NOI18N
    
    public static final String JAVA_PLATFORM_NAME = "java.platform.name"; // NOI18N
    public static final String SOURCE_ENCODING = "source.encoding"; // NOI18N
    public static final String JAVADOC_PRIVATE = "javadoc.private"; // NOI18N
    public static final String JAVADOC_NO_TREE = "javadoc.notree"; // NOI18N
    public static final String JAVADOC_USE = "javadoc.use"; // NOI18N
    public static final String JAVADOC_NO_NAVBAR = "javadoc.nonavbar"; // NOI18N
    public static final String JAVADOC_NO_INDEX = "javadoc.noindex"; // NOI18N
    public static final String JAVADOC_SPLIT_INDEX = "javadoc.splitindex"; // NOI18N
    public static final String JAVADOC_AUTHOR = "javadoc.author"; // NOI18N
    public static final String JAVADOC_VERSION = "javadoc.version"; // NOI18N
    public static final String JAVADOC_WINDOW_TITLE = "javadoc.windowtitle"; // NOI18N
    public static final String JAVADOC_ENCODING = "javadoc.encoding"; // NOI18N
    public static final String JAVADOC_ADDITIONALPARAM = "javadoc.additionalparam"; // NOI18N
    public static final String BUILD_SCRIPT = "buildfile"; //NOI18N

    // Packaging properties
    public static final String JAVAFX_BINARY_ENCODE_CSS = "javafx.binarycss"; // NOI18N
    
    // FX config properties (Run panel), replicated from ProjectProperties
    public static final String MAIN_CLASS = "javafx.main.class"; // NOI18N
    //public static final String APPLICATION_ARGS = "javafx.application.args"; // NOI18N
    public static final String APP_PARAM_PREFIX = "javafx.param."; // NOI18N
    public static final String APP_PARAM_SUFFIXES[] = new String[] { "name", "value" }; // NOI18N
    public static final String RUN_JVM_ARGS = ProjectProperties.RUN_JVM_ARGS; // NOI18N
    public static final String PRELOADER = "javafx.preloader.enabled"; // NOI18N
    public static final String PRELOADER_PROJECT = "javafx.preloader.project"; // NOI18N
    public static final String PRELOADER_JAR = "javafx.preloader.jar"; // NOI18N
    public static final String PRELOADER_CLASS = "javafx.preloader.class"; // NOI18N
    public static final String RUN_WORK_DIR = ProjectProperties.RUN_WORK_DIR; // NOI18N
    public static final String RUN_APP_WIDTH = "javafx.run.width"; // NOI18N
    public static final String RUN_APP_HEIGHT = "javafx.run.height"; // NOI18N
    public static final String RUN_IN_HTMLPAGE = "javafx.run.inhtmlpage"; // NOI18N
    public static final String RUN_IN_BROWSER = "javafx.run.inbrowser"; // NOI18N
    public static final String RUN_AS = "javafx.run.as"; // NOI18N

    public static final String DEFAULT_APP_WIDTH = "800"; // NOI18N
    public static final String DEFAULT_APP_HEIGHT = "600"; // NOI18N

    // Deployment properties
    public static final String BACKGROUND_UPDATE_CHECK = "javafx.deploy.backgroundupdate"; // NOI18N
    public static final String ALLOW_OFFLINE = "javafx.deploy.allowoffline"; // NOI18N
    public static final String INSTALL_PERMANENTLY = "javafx.deploy.installpermanently"; // NOI18N
    public static final String ADD_DESKTOP_SHORTCUT = "javafx.deploy.adddesktopshortcut"; // NOI18N
    public static final String ADD_STARTMENU_SHORTCUT = "javafx.deploy.addstartmenushortcut"; // NOI18N
    public static final String ICON_FILE = "javafx.deploy.icon"; // NOI18N

    public static final String SIGNING_GENERATED = "generated"; //NOI18N
    public static final String SIGNING_KEY = "key"; //NOI18N

    public static final String JAVAFX_SIGNED = "javafx.signed"; //NOI18N
    public static final String JAVAFX_SIGNING = "javafx.signing"; //NOI18N
    public static final String JAVAFX_SIGNING_KEYSTORE = "javafx.signing.keystore"; //NOI18N
    public static final String JAVAFX_SIGNING_KEY = "javafx.signing.alias"; //NOI18N
    public static final String JAVAFX_SIGNING_KEYSTORE_PASSWORD = "javafx.signing.storepass"; //NOI18N
    public static final String JAVAFX_SIGNING_KEY_PASSWORD = "javafx.signing.keypass"; //NOI18N
    public static final String RUN_CP = "run.classpath";    //NOI18N
    public static final String BUILD_CLASSES = "build.classes.dir"; //NOI18N
    public static final String DOWNLOAD_MODE_LAZY_JARS = "download.mode.lazy.jars";   //NOI18N
    private static final String DOWNLOAD_MODE_LAZY_JAR = "download.mode.lazy.jar."; //NOI18N
    private static final String DOWNLOAD_MODE_LAZY_FORMAT = DOWNLOAD_MODE_LAZY_JAR +"%s"; //NOI18N
    
    private StoreGroup fxPropGroup = new StoreGroup();
    
    // Packaging
    JToggleButton.ToggleButtonModel binaryEncodeCSS;
    public JToggleButton.ToggleButtonModel getBinaryEncodeCSSModel() {
        return binaryEncodeCSS;
    }

    // CustomizerRun
    private Map<String/*|null*/,Map<String,String/*|null*/>/*|null*/> RUN_CONFIGS;
    private Map<String/*|null*/,List<Map<String,String/*|null*/>>/*|null*/> APP_PARAMS;
    private String activeConfig;
    
    public Map<String/*|null*/,Map<String,String/*|null*/>/*|null*/> getRunConfigs() {
        return RUN_CONFIGS;
    }    
    public Map<String/*|null*/,List<Map<String,String/*|null*/>>/*|null*/> getAppParameters() {
        return APP_PARAMS;
    }   
    public List<Map<String,String/*|null*/>> getActiveAppParameters() {
        return APP_PARAMS.get(activeConfig);
    }   
    public void setActiveAppParameters(List<Map<String,String/*|null*/>>/*|null*/ params) {
        APP_PARAMS.put(activeConfig, params);
    }
    public String getActiveConfig() {
        return activeConfig;
    }
    public void setActiveConfig(String config) {
        activeConfig = config;
    }

    // CustomizerRun - Run type
    public enum RunAsType {
        STANDALONE("standalone"), // NOI18N
        ASWEBSTART("webstart"), // NOI18N
        INBROWSER("embedded"); // NOI18N
        private final String propertyValue;
        RunAsType(String propertyValue) {
            this.propertyValue = propertyValue;
        }
        public String getString() {
            return propertyValue;
        }
    }
//    RunAsType runModel;
//    public RunAsType getRunModel() {
//        return runModel;
//    }
    JToggleButton.ToggleButtonModel runStandalone;
    JToggleButton.ToggleButtonModel runAsWebStart;
    JToggleButton.ToggleButtonModel runInBrowser;
    
    // Deployment
    JToggleButton.ToggleButtonModel allowOfflineModel;
    public JToggleButton.ToggleButtonModel getAllowOfflineModel() {
        return allowOfflineModel;
    }
    JToggleButton.ToggleButtonModel backgroundUpdateCheck;
    public JToggleButton.ToggleButtonModel getBackgroundUpdateCheckModel() {
        return backgroundUpdateCheck;
    }
    JToggleButton.ToggleButtonModel installPermanently;
    public JToggleButton.ToggleButtonModel getInstallPermanentlyModel() {
        return installPermanently;
    }
    JToggleButton.ToggleButtonModel addDesktopShortcut;
    public JToggleButton.ToggleButtonModel getAddDesktopShortcutModel() {
        return addDesktopShortcut;
    }
    JToggleButton.ToggleButtonModel addStartMenuShortcut;
    public JToggleButton.ToggleButtonModel getAddStartMenuShortcutModel() {
        return addStartMenuShortcut;
    }
    Document iconDocument;
    public Document getIconDocumentModel() {
        return iconDocument;
    }

    // Deployment - Signing
    String signing;
    String signingKeyStore;
    String signingKeyAlias;
    char [] signingKeyStorePassword;
    char [] signingKeyPassword;
    public String getSigning() {
        return signing;
    }
    public String getSigningKeyAlias() {
        return signingKeyAlias;
    }
    
    // Deployment - Libraries Download Mode
    List<? extends File> runtimeCP;
    List<? extends File> lazyJars;
    boolean lazyJarsChanged;
    public List<? extends File> getRuntimeCP() {
        return runtimeCP;
    }
    public List<? extends File> getLazyJars() {
        return lazyJars;
    }
    public void setLazyJars(List<? extends File> newLazyJars) {
        lazyJars = newLazyJars;
    }
    public boolean getLazyJarsChanged() {
        return lazyJarsChanged;
    }
    public void setLazyJarsChanged(boolean changed) {
        lazyJarsChanged = changed;
    }
    
    // Project related references
    private J2SEPropertyEvaluator j2sePropEval;
    private PropertyEvaluator evaluator;
    private Project project;

    public Project getProject() {
        return project;
    }
    public PropertyEvaluator getEvaluator() {
        return evaluator;
    }
    
    /** Keeps singleton instance for any fx project for which property customizer is opened at once */
    private static Map<String, JFXProjectProperties> propInstance = new TreeMap<String, JFXProjectProperties>();
    
    /** Factory method */
    public static JFXProjectProperties getInstance(Lookup context) {
        Project proj = context.lookup(Project.class);
        String projDir = proj.getProjectDirectory().getPath();
        JFXProjectProperties prop = propInstance.get(projDir);
        if(prop == null) {
            prop = new JFXProjectProperties(context);
            propInstance.put(projDir, prop);
        }
        return prop;
    }

    /** Getter method */
    public static JFXProjectProperties getInstanceIfExists(Lookup context) {
        Project proj = context.lookup(Project.class);
        String projDir = proj.getProjectDirectory().getPath();
        JFXProjectProperties prop = propInstance.get(projDir);
        if(prop != null) {
            return prop;
        }
        return null;
    }

    public static void cleanup(Lookup context) {
        Project proj = context.lookup(Project.class);
        String projDir = proj.getProjectDirectory().getPath();
        propInstance.remove(projDir);
    }

    /** Creates a new instance of JFXProjectProperties */
    private JFXProjectProperties(Lookup context) {
        
        //defaultInstance = provider.getJFXProjectProperties();
        project = context.lookup(Project.class);
        
        if (project != null) {
            j2sePropEval = project.getLookup().lookup(J2SEPropertyEvaluator.class);
            evaluator = j2sePropEval.evaluator();
            
            // Packaging
            binaryEncodeCSS = fxPropGroup.createToggleButtonModel(evaluator, JAVAFX_BINARY_ENCODE_CSS);

            // Deployment
            allowOfflineModel = fxPropGroup.createToggleButtonModel(evaluator, ALLOW_OFFLINE);
            if(evaluator.getProperty(ALLOW_OFFLINE) == null) {
                allowOfflineModel.setSelected(true); // default is true
            }
            backgroundUpdateCheck = fxPropGroup.createToggleButtonModel(evaluator, BACKGROUND_UPDATE_CHECK);
            if(evaluator.getProperty(BACKGROUND_UPDATE_CHECK) == null) {
                backgroundUpdateCheck.setSelected(true); // default is true
            }
            installPermanently = fxPropGroup.createToggleButtonModel(evaluator, INSTALL_PERMANENTLY);
            addDesktopShortcut = fxPropGroup.createToggleButtonModel(evaluator, ADD_DESKTOP_SHORTCUT);
            addStartMenuShortcut = fxPropGroup.createToggleButtonModel(evaluator, ADD_STARTMENU_SHORTCUT);
            iconDocument = fxPropGroup.createStringDocument(evaluator, ICON_FILE);

            // CustomizerRun
            RUN_CONFIGS = readRunConfigs();
            APP_PARAMS = readAppParams();
            activeConfig = evaluator.getProperty(ProjectProperties.PROP_PROJECT_CONFIGURATION_CONFIG); // NOI18N
            
            initSigning(evaluator);
            initResources(evaluator, project);             
        }
    }
    
    public static boolean isTrue(final String value) {
        return value != null &&
                (value.equalsIgnoreCase("true") ||  //NOI18N
                 value.equalsIgnoreCase("yes") ||   //NOI18N
                 value.equalsIgnoreCase("on"));     //NOI18N
    }

    public static class PropertiesTableModel extends AbstractTableModel {
        
        private List<Map<String,String>> properties;
        private String propSuffixes[];
        private String columnNames[];
        
        public PropertiesTableModel(List<Map<String,String>> props, String sfxs[], String clmns[]) {
            if (sfxs.length != clmns.length) {
                throw new IllegalArgumentException();
            }
            properties = props;
            propSuffixes = sfxs;
            columnNames = clmns;
        }
        
        @Override
        public int getRowCount() {
            return properties.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
        
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            properties.get(rowIndex).put(propSuffixes[columnIndex], (String) aValue);
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return properties.get(rowIndex).get(propSuffixes[columnIndex]);
        }
        
        public void addRow() {
            Map<String,String> emptyMap = new HashMap<String,String>();
            for (String  suffix : propSuffixes) {
                emptyMap.put(suffix, "");
            }
            properties.add(emptyMap);
        }
        
        public void removeRow(int index) {
            properties.remove(index);
        }

    }
    
    /**
     * A mess. (modified from J2SEProjectProperties)
     */
    Map<String/*|null*/,Map<String,String>> readRunConfigs() {
        Map<String,Map<String,String>> m = new TreeMap<String,Map<String,String>>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1 != null ? (s2 != null ? s1.compareTo(s2) : 1) : (s2 != null ? -1 : 0);
            }
        });
        Map<String,String> def = new TreeMap<String,String>();
        EditableProperties ep = null;
        try {
            ep = readFromFile(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
        } catch (IOException ex) {
            // can be ignored
        }
        EditableProperties pep = null;
        try {
            pep = readFromFile(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        } catch (IOException ex) {
            // can be ignored
        }
        for (String prop : new String[] {MAIN_CLASS, /*APPLICATION_ARGS,*/ RUN_JVM_ARGS, PRELOADER, PRELOADER_PROJECT, PRELOADER_JAR, PRELOADER_CLASS, 
                                        RUN_WORK_DIR, RUN_APP_WIDTH, RUN_APP_HEIGHT, RUN_IN_HTMLPAGE, RUN_IN_BROWSER, RUN_AS}) {
            String v = ep.getProperty(prop);
            if (v == null) {
                v = pep.getProperty(prop);
            }
            if (v != null) {
                def.put(prop, v);
            }
        }
        if(def.get(RUN_APP_WIDTH) == null) {
            def.put(RUN_APP_WIDTH, DEFAULT_APP_WIDTH);
        }
        if(def.get(RUN_APP_HEIGHT) == null) {
            def.put(RUN_APP_HEIGHT, DEFAULT_APP_HEIGHT);
        }
        m.put(null, def);
        FileObject configs = project.getProjectDirectory().getFileObject("nbproject/configs"); // NOI18N
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) { // NOI18N
                    continue;
                }
                EditableProperties cep = null;
                try {
                    cep = readFromFile( FileUtil.getRelativePath(project.getProjectDirectory(), kid) );
                } catch (IOException ex) {
                    // can be ignored
                }
                m.put(kid.getName(), new TreeMap<String,String>(cep) );
            }
        }
        configs = project.getProjectDirectory().getFileObject("nbproject/private/configs"); // NOI18N
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) { // NOI18N
                    continue;
                }
                Map<String,String> c = m.get(kid.getName());
                if (c == null) {
                    continue;
                }
                EditableProperties cep = null;
                try {
                    cep = readFromFile( FileUtil.getRelativePath(project.getProjectDirectory(), kid) );
                } catch (IOException ex) {
                    // can be ignored
                }
                c.putAll(new HashMap<String,String>(cep));
            }
        }
        //System.err.println("readRunConfigs: " + p);
        return m;
    }

    /**
     * Another mess.
     */
    Map<String/*|null*/,List<Map<String,String/*|null*/>>/*|null*/> readAppParams() {
        Map<String/*|null*/,List<Map<String,String/*|null*/>>/*|null*/> p = new TreeMap<String,List<Map<String,String/*|null*/>>/*|null*/>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1 != null ? (s2 != null ? s1.compareTo(s2) : 1) : (s2 != null ? -1 : 0);
            }
        });
        List<Map<String,String/*|null*/>>/*|null*/ def = new ArrayList<Map<String,String/*|null*/>>(); //TreeMap<String,String>();
        EditableProperties ep = null;
        try {
            ep = readFromFile(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
        } catch (IOException ex) {
            // can be ignored
        }
        EditableProperties pep = null;
        try {
            pep = readFromFile(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        } catch (IOException ex) {
            // can be ignored
        }
        
        int index = 0;
        while (true) {
            Map<String,String> map = new HashMap<String,String>();
            int numProps = 0;
            for (String propSuffix : APP_PARAM_SUFFIXES) {
                String propValue = ep.getProperty(APP_PARAM_PREFIX + index + "." + propSuffix);
                if(propValue == null) {
                    propValue = pep.getProperty(APP_PARAM_PREFIX + index + "." + propSuffix);
                }
                if (propValue != null) {
                    map.put(propSuffix, propValue);
                    numProps++;
                }
            }
            if (numProps == 0) {
                break;
            }
            def.add(map);
            index++;
        }       
        p.put(null, def);

        FileObject configs = project.getProjectDirectory().getFileObject("nbproject/configs"); // NOI18N
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) { // NOI18N
                    continue;
                }
                EditableProperties cep = null;
                try {
                    cep = readFromFile( FileUtil.getRelativePath(project.getProjectDirectory(), kid) );
                } catch (IOException ex) {
                    // can be ignored
                }
                List<Map<String,String/*|null*/>>/*|null*/ params = new ArrayList<Map<String,String/*|null*/>>();
                if(cep != null) {
                    index = 0;
                    while (true) {
                        Map<String,String> map = new HashMap<String,String>();
                        int numProps = 0;
                        for (String propSuffix : APP_PARAM_SUFFIXES) {
                            String propValue = cep.getProperty(APP_PARAM_PREFIX + index + "." + propSuffix);
                            if (propValue != null) {
                                map.put(propSuffix, propValue);
                                numProps++;
                            }
                        }
                        if (numProps == 0) {
                            break;
                        }
                        params.add(map);
                        index++;
                    }
                }
                p.put(kid.getName(), params );
            }
        }
        configs = project.getProjectDirectory().getFileObject("nbproject/private/configs"); // NOI18N
        if (configs != null) {
            for (FileObject kid : configs.getChildren()) {
                if (!kid.hasExt("properties")) { // NOI18N
                    continue;
                }
                //Map<String,String> c = p.get(kid.getName());
                List<Map<String,String/*|null*/>>/*|null*/ params = p.get(kid.getName());
                if (params == null) {
                    params = new ArrayList<Map<String,String/*|null*/>>();
                    p.put(kid.getName(), params);
                }
                EditableProperties cep = null;
                try {
                    cep = readFromFile( FileUtil.getRelativePath(project.getProjectDirectory(), kid) );
                } catch (IOException ex) {
                    // can be ignored
                }
                if(cep != null) {
                    index = 0;
                    while (true) {
                        Map<String,String> map = new HashMap<String,String>();
                        int numProps = 0;
                        for (String propSuffix : APP_PARAM_SUFFIXES) {
                            String propValue = cep.getProperty(APP_PARAM_PREFIX + index + "." + propSuffix);
                            if (propValue != null) {
                                map.put(propSuffix, propValue);
                                numProps++;
                            }
                        }
                        if (numProps == 0) {
                            break;
                        }
                        params.add(map);
                        index++;
                    }
                }
                //c.putAll(new HashMap<String,String>(cep));
            }
        }
        //System.err.println("readAppParams: " + p);
        return p;
    }

    /**
     * A royal mess. (modified from J2SEProjectProperties)
     */
    void storeRunConfigs(Map<String/*|null*/,Map<String,String/*|null*/>/*|null*/> configs,
            Map<String/*|null*/,List<Map<String,String/*|null*/>>/*|null*/> params,
            EditableProperties projectProperties, EditableProperties privateProperties) throws IOException {
        //System.err.println("storeRunConfigs: " + configs);
        Map<String,String> def = configs.get(null);
        for (String prop : new String[] {MAIN_CLASS, /*APPLICATION_ARGS,*/ RUN_JVM_ARGS, PRELOADER, PRELOADER_PROJECT, PRELOADER_JAR, PRELOADER_CLASS, 
                                        RUN_WORK_DIR, RUN_APP_WIDTH, RUN_APP_HEIGHT, RUN_IN_HTMLPAGE, RUN_IN_BROWSER, RUN_AS}) {
            String v = def.get(prop);
            EditableProperties ep =
                    (//prop.equals(APPLICATION_ARGS) ||
                    prop.equals(RUN_WORK_DIR)  ||
                    prop.equals(RUN_IN_HTMLPAGE)  ||
                    prop.equals(RUN_IN_BROWSER)  ||
                    prop.equals(RUN_AS)  ||
                    privateProperties.containsKey(prop)) ?
                privateProperties : projectProperties;
            if (!Utilities.compareObjects(v, ep.getProperty(prop))) {
                if (v != null && v.length() > 0) {
                    ep.setProperty(prop, v);
                } else {
                    ep.remove(prop);
                }
            }
        }
        int index = 0;
        for(Map<String,String> m : params.get(null)) {
            for (Map.Entry<String,String> propSuffix : m.entrySet()) {
                String prop = APP_PARAM_PREFIX + index + "." + propSuffix.getKey();
                String v = propSuffix.getValue();
                if (!Utilities.compareObjects(v, projectProperties.getProperty(prop))) {
                    if (v != null && v.length() > 0) {
                        projectProperties.setProperty(prop, v);
                    } else {
                        projectProperties.remove(prop);
                    }
                }
            }
            index++;
        }
        for (Map.Entry<String,Map<String,String>> entry : configs.entrySet()) {
            String config = entry.getKey();
            if (config == null) {
                continue;
            }
            String sharedPath = "nbproject/configs/" + config + ".properties"; // NOI18N
            String privatePath = "nbproject/private/configs/" + config + ".properties"; // NOI18N
            Map<String,String> c = entry.getValue();
            if (c == null) {
                try {
                    deleteFile(sharedPath);
                } catch (IOException ex) {
                    // TO DO
                }
                try {
                    deleteFile(privatePath);
                } catch (IOException ex) {
                    // TO DO
                }
                continue;
            }
            final EditableProperties sharedCfgProps = readFromFile(sharedPath);
            final EditableProperties privateCfgProps = readFromFile(privatePath);
            boolean privatePropsChanged = false;
            for (Map.Entry<String,String> entry2 : c.entrySet()) {
                String prop = entry2.getKey();
                String v = entry2.getValue();
                EditableProperties ep =
                        (//prop.equals(APPLICATION_ARGS) ||
                         prop.equals(RUN_WORK_DIR) ||
                         prop.equals(RUN_IN_HTMLPAGE)  ||
                         prop.equals(RUN_IN_BROWSER)  ||
                         prop.equals(RUN_AS)  ||
                         privateCfgProps.containsKey(prop)) ?
                    privateCfgProps : sharedCfgProps;
                if (!Utilities.compareObjects(v, ep.getProperty(prop))) {
                    if (v != null && (v.length() > 0 || (def.get(prop) != null && def.get(prop).length() > 0))) {
                        ep.setProperty(prop, v);
                    } else {
                        ep.remove(prop);
                    }
                    privatePropsChanged |= ep == privateCfgProps;
                }
            }
            index = 0;
            for(Map<String,String> m : params.get(config)) {
                for (Map.Entry<String,String> propSuffix : m.entrySet()) {
                    String prop = APP_PARAM_PREFIX + index + "." + propSuffix.getKey();
                    String v = propSuffix.getValue();
                    if (!Utilities.compareObjects(v, sharedCfgProps.getProperty(prop))) {
                        if (v != null && v.length() > 0) {
                            sharedCfgProps.setProperty(prop, v);
                        } else {
                            sharedCfgProps.remove(prop);
                        }
                    }
                }
                index++;
            }
            saveToFile(sharedPath, sharedCfgProps);    //Make sure the definition file is always created, even if it is empty.
            if (privatePropsChanged) {                              //Definition file is written, only when changed
                saveToFile(privatePath, privateCfgProps);
            }
        }
    }
    
    private void storeRest(EditableProperties editableProps, EditableProperties privProps) {
//        // store descriptor type
//        DescType descType = getSelectedDescType();
//        if (descType != null) {
//            editableProps.setProperty(JNLP_DESCRIPTOR, descType.toString());
//        }
//        //Store Mixed Code
//        final MixedCodeOptions option = (MixedCodeOptions) mixedCodeModel.getSelectedItem();
//        editableProps.setProperty(JNLP_MIXED_CODE, option.getPropertyValue());
//        //Store jar indexing
//        if (editableProps.getProperty(JAR_INDEX) == null) {
//            editableProps.setProperty(JAR_INDEX, String.format("${%s}", JNLP_ENABLED));   //NOI18N
//        }
//        if (editableProps.getProperty(JAR_ARCHIVE_DISABLED) == null) {
//            editableProps.setProperty(JAR_ARCHIVE_DISABLED, String.format("${%s}", JNLP_ENABLED));  //NOI18N
//        }
        // store signing info
        editableProps.setProperty(JAVAFX_SIGNING, signing);
        editableProps.setProperty(JAVAFX_SIGNED, "".equals(signing) ? "false" : "true"); //NOI18N
        setOrRemove(editableProps, JAVAFX_SIGNING_KEY, signingKeyAlias);
        setOrRemove(editableProps, JAVAFX_SIGNING_KEYSTORE, signingKeyStore);
        setOrRemove(privProps, JAVAFX_SIGNING_KEYSTORE_PASSWORD, signingKeyStorePassword);
        setOrRemove(privProps, JAVAFX_SIGNING_KEY_PASSWORD, signingKeyPassword);
        
        // store resources
        storeResources(editableProps);
    }

    private void setOrRemove(EditableProperties props, String name, char [] value) {
        setOrRemove(props, name, value != null ? new String(value) : null);
    }

    private void setOrRemove(EditableProperties props, String name, String value) {
        if (value != null) {
            props.setProperty(name, value);
        } else {
            props.remove(name);
        }
    }
        
    public EditableProperties readFromFile(String relativePath) throws IOException {
        final EditableProperties ep = new EditableProperties(true);
        final FileObject propsFO = project.getProjectDirectory().getFileObject(relativePath);
        if(propsFO != null) {
            try {
                final InputStream is = propsFO.getInputStream();
                ProjectManager.mutex().readAccess(new Mutex.ExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        try {
                            ep.load(is);
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                        return null;
                    }
                });
            } catch (MutexException mux) {
                throw (IOException) mux.getException();
            }
        }
        return ep;
    }

    public void deleteFile(String relativePath) throws IOException {
        final FileObject propsFO = project.getProjectDirectory().getFileObject(relativePath);
        if(propsFO != null) {
            try {
                final Mutex.ExceptionAction<Void> action = new Mutex.ExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        FileLock lock = null;
                        try {
                            lock = propsFO.lock();
                            propsFO.delete(lock);
                        } finally {
                            if (lock != null) {
                                lock.releaseLock();
                            }
                        }
                        return null;
                    }
                };
                ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                    @Override
                    public Void run() throws Exception {
                        ProjectManager.mutex().readAccess(action);
                        return null;
                    }
                });
            } catch (MutexException mux) {
                throw (IOException) mux.getException();
            }       
        }
    }

    public void saveToFile(String relativePath, final EditableProperties ep) throws IOException {
        FileObject f = project.getProjectDirectory().getFileObject(relativePath);
        final FileObject propsFO;
        if(f == null) {
            propsFO = FileUtil.createData(project.getProjectDirectory(), relativePath);
            assert propsFO != null : "FU.cD must not return null; called on " + project.getProjectDirectory() + " + " + relativePath; // #50802  // NOI18N
        } else {
            propsFO = f;
        }
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    OutputStream os = null;
                    FileLock lock = null;
                    try {
                        lock = propsFO.lock();
                        os = propsFO.getOutputStream(lock);
                        ep.store(os);
                    } finally {
                        if (lock != null) {
                            lock.releaseLock();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                    return null;
                }
            });
        } catch (MutexException mux) {
            throw (IOException) mux.getException();
        }
    }

    public void store() throws IOException {
        
        final EditableProperties ep = new EditableProperties(true);
        final FileObject projPropsFO = project.getProjectDirectory().getFileObject(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        final EditableProperties pep = new EditableProperties(true);
        final FileObject privPropsFO = project.getProjectDirectory().getFileObject(AntProjectHelper.PRIVATE_PROPERTIES_PATH);
        
        try {
            final InputStream is = projPropsFO.getInputStream();
            final InputStream pis = privPropsFO.getInputStream();
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    try {
                        ep.load(is);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    try {
                        pep.load(pis);
                    } finally {
                        if (pis != null) {
                            pis.close();
                        }
                    }
                    fxPropGroup.store(ep);
                    storeRest(ep, pep);
                    storeRunConfigs(RUN_CONFIGS, APP_PARAMS, ep, pep);
                    OutputStream os = null;
                    FileLock lock = null;
                    try {
                        lock = projPropsFO.lock();
                        os = projPropsFO.getOutputStream(lock);
                        ep.store(os);
                    } finally {
                        if (lock != null) {
                            lock.releaseLock();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                    try {
                        lock = privPropsFO.lock();
                        os = privPropsFO.getOutputStream(lock);
                        pep.store(os);
                    } finally {
                        if (lock != null) {
                            lock.releaseLock();
                        }
                        if (os != null) {
                            os.close();
                        }
                    }
                    return null;
                }
            });
        } catch (MutexException mux) {
            throw (IOException) mux.getException();
        }       
    }

    private void initSigning(PropertyEvaluator eval) {
        signing = eval.getProperty(JAVAFX_SIGNING);
        if (signing == null) signing = "";
        signingKeyStore = eval.getProperty(JAVAFX_SIGNING_KEYSTORE);
        if (signingKeyStore == null) signingKeyStore = "";
        signingKeyAlias = eval.getProperty(JAVAFX_SIGNING_KEY);
        if (signingKeyAlias == null) signingKeyAlias = "";
        if (eval.getProperty(JAVAFX_SIGNING_KEYSTORE_PASSWORD) != null) {
            signingKeyStorePassword = eval.getProperty(JAVAFX_SIGNING_KEYSTORE_PASSWORD).toCharArray();
        }
        if (eval.getProperty(JAVAFX_SIGNING_KEY_PASSWORD) != null) {
            signingKeyPassword = eval.getProperty(JAVAFX_SIGNING_KEY_PASSWORD).toCharArray();
        }
        // compatibility
        if ("".equals(signing) && "true".equals(eval.getProperty(JAVAFX_SIGNED))) { //NOI18N
            signing = SIGNING_GENERATED;
        }
    }
    
    private void initResources (final PropertyEvaluator eval, final Project prj) {
        final String lz = eval.getProperty(DOWNLOAD_MODE_LAZY_JARS); //old way, when changed rewritten to new
        final String rcp = eval.getProperty(RUN_CP);        
        final String bc = eval.getProperty(BUILD_CLASSES);        
        final File prjDir = FileUtil.toFile(prj.getProjectDirectory());
        final File bcDir = bc == null ? null : PropertyUtils.resolveFile(prjDir, bc);
        final List<File> lazyFileList = new ArrayList<File>();
        String[] paths;
        if (lz != null) {
            paths = PropertyUtils.tokenizePath(lz);            
            for (String p : paths) {
                lazyFileList.add(PropertyUtils.resolveFile(prjDir, p));
            }
        }
        paths = PropertyUtils.tokenizePath(rcp);
        final List<File> resFileList = new ArrayList<File>(paths.length);
        for (String p : paths) {
            if (p.startsWith("${") && p.endsWith("}")) {    //NOI18N
                continue;
            }
            final File f = PropertyUtils.resolveFile(prjDir, p);
            if (bc == null || !bcDir.equals(f)) {
                resFileList.add(f);
                if (isTrue(eval.getProperty(String.format(DOWNLOAD_MODE_LAZY_FORMAT, f.getName())))) {
                    lazyFileList.add(f);
                }
            }
        }
        lazyJars = lazyFileList;
        runtimeCP = resFileList;
        lazyJarsChanged = false;
    }
    
    private void storeResources(final EditableProperties props) {
        if (lazyJarsChanged) {
            //Remove old way if exists
            props.remove(DOWNLOAD_MODE_LAZY_JARS);
            final Iterator<Map.Entry<String,String>> it = props.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next().getKey().startsWith(DOWNLOAD_MODE_LAZY_JAR)) {
                    it.remove();
                }
            }
            for (File lazyJar : lazyJars) {
                props.setProperty(String.format(DOWNLOAD_MODE_LAZY_FORMAT, lazyJar.getName()), "true");  //NOI18N
            }
        }
    }
    
}
