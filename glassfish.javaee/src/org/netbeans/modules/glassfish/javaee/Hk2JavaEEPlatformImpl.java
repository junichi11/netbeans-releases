/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.glassfish.javaee;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.tools.ide.data.GlassFishVersion;
import org.glassfish.tools.ide.server.config.JavaEEProfile;
import org.glassfish.tools.ide.server.config.JavaSEPlatform;
import org.glassfish.tools.ide.server.config.ModuleType;
import static org.glassfish.tools.ide.server.config.ModuleType.EJB;
import static org.glassfish.tools.ide.server.config.ModuleType.RAR;
import org.glassfish.tools.ide.utils.ServerUtils;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.JavaClassPathConstants;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.glassfish.spi.GlassfishModule;
import org.netbeans.modules.glassfish.spi.ServerUtilities;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule.Type;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl2;
import org.netbeans.modules.j2ee.deployment.plugins.spi.support.LookupProviderSupport;
import org.netbeans.modules.javaee.specs.support.api.JaxRpc;
import org.netbeans.modules.javaee.specs.support.api.JaxWs;
import org.netbeans.modules.javaee.specs.support.spi.JaxRsStackSupportImplementation;
import org.netbeans.modules.websvc.wsstack.api.WSStack;
import org.netbeans.modules.websvc.wsstack.spi.WSStackFactory;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.filesystems.*;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
    
/**
 *
 * @author Ludo, Tomas Kraus
 */
public class Hk2JavaEEPlatformImpl extends J2eePlatformImpl2 {
    
    private Hk2DeploymentManager dm;
    private final LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
    private final LibraryImplementation[] libraries = { lib };

    /** NetBeans JavaSE platforms. */
    private String[] platforms;

    /** NetBeans JavaEE profiles. */
    private Profile[] profiles;

    /** NetBeans JavaEE module types. */
    private J2eeModule.Type[] types;

    /** NetBeans JavaEE platform display name. */
    private final String displayName;

    /** NetBeans JavaEE library name. */
    private final String libraryName;

    /** GlassFish JavaEE platform lookup key. */
    private final String lookupKey;

    private FileChangeListener fcl;

    /** Keep local Lookup instance to be returned by getLookup method. */
    private volatile Lookup lkp;

    /** Jersey Library support. */
    private Hk2LibraryProvider libraryProvider;

    ////////////////////////////////////////////////////////////////////////////
    // Static methods                                                         //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Map GlassFish tooling SDK JavaSE platforms to NetBeans JavaSE platforms.
     * <p/>
     * @param sdkPlatforms GlassFish tooling SDK JavaSE platforms.
     * @return Array of NetBeans JavaSE platforms containing known GlassFish
     *         tooling SDK JavaSE platforms.
     */
    public static String[] nbJavaSEProfiles(
            final Set<JavaSEPlatform> sdkPlatforms) {
        int size = sdkPlatforms != null ? sdkPlatforms.size() : 0;
        String[] platforms = new String[size];
        if (size > 0) {
            int index = 0;
            for (JavaSEPlatform platform : sdkPlatforms) {
                platforms[index++] = platform.toString();
            }
        }
        return platforms;
    }
            
    
    /**
     * Map GlassFish tooling SDK JavaEE profiles to NetBeans JavaEE profiles.
     * <p/>
     * @param sdkProfiles GlassFish tooling SDK JavaEE profiles.
     * @return Array of NetBeans JavaEE profiles containing known GlassFish
     *         tooling SDK JavaEE profiles.
     */
    public static Profile[] nbJavaEEProfiles(
            final Set<JavaEEProfile> sdkProfiles) {
        int sdkSize = sdkProfiles != null ? sdkProfiles.size() : 0;
        int size = sdkSize;
        // Shrink output array size for unsupported JavaEE profiles.
        for (JavaEEProfile sdkProfile : sdkProfiles) switch(sdkProfile) {
            case v1_2: size--;
        }
        Profile[] profiles;
        if (sdkSize > 0) {
            profiles = new Profile[size];
            int index = 0;
            // JavaEE 1.2 should be ignored.
            for (JavaEEProfile sdkProfile : sdkProfiles) switch(sdkProfile) {
                case v1_3:     profiles[index++] = Profile.J2EE_13;
                               break;
                case v1_4:     profiles[index++] = Profile.J2EE_14;
                               break;
                case v1_5:     profiles[index++] = Profile.JAVA_EE_5;
                               break;
                case v1_6_web: profiles[index++] = Profile.JAVA_EE_6_WEB;
                               break;
                case v1_6:     profiles[index++] = Profile.JAVA_EE_6_FULL;
                               break;
                case v1_7_web: profiles[index++] = Profile.JAVA_EE_7_WEB;
                               break;
                case v1_7:     profiles[index++] = Profile.JAVA_EE_7_FULL;
                               break;
            }
        } else {
            profiles = new Profile[0];
        }
        return profiles;
    }

    /**
     * Map GlassFish tooling SDK JavaEE module types to NetBeans JavaEE
     * module types.
     * <p/>
     * @param sdkModule GlassFish tooling SDK JavaEE module types.
     * @return Array of NetBeans JavaEE module types containing known GlassFish
     *         tooling SDK JavaEE module types.
     */
    public static J2eeModule.Type[] nbModuleTypes(
            final Set<ModuleType> sdkModuleTypes) {
        int size = sdkModuleTypes != null ? sdkModuleTypes.size() : 0;
        J2eeModule.Type[] types = new J2eeModule.Type[size];
        if (size > 0) {
            int index = 0;
            for (ModuleType sdkType : sdkModuleTypes) switch(sdkType) {
                case EAR: types[index++] = J2eeModule.Type.EAR;
                          break;
                case EJB: types[index++] = J2eeModule.Type.EJB;
                          break;
                case CAR: types[index++] = J2eeModule.Type.CAR;
                          break;
                case RAR: types[index++] = J2eeModule.Type.RAR;
                          break;
                case WAR: types[index++] = J2eeModule.Type.WAR;
                          break;
            }
        }
        return types;
    }

    /**
     * Create {@see Set} of {@see String} objects from array of supported
     * JavaSE platform objects.
     * <p>
     * @param platforms Array of supported JavaSE platforms.
     * @return Newly created set of supported JavaSE platforms.
     */
    Set<String> platformsSetFromArray(String[] platforms) {
        int size = platforms != null ? platforms.length : 0;
        Set<String> platformSet = new HashSet<String>(size);
        for (int i = 0; i < size; i++) {
            platformSet.add(platforms[i]);
        }
        return platformSet;
    }

    /**
     * Create {@see Set} of {@see Profile} objects from array of those objects.
     * <p>
     * @param profiles Array of NetBeans JavaEE profiles.
     * @return Newly created set of NetBeans JavaEE profiles from array.
     */
    Set<Profile> profilesSetFromArray(Profile[] profiles) {
        int size = profiles != null ? profiles.length : 0;
        Set<Profile> profileSet = new HashSet<Profile>(size);
        for (int i = 0; i < size; i++) {
            profileSet.add(profiles[i]);
        }
        return profileSet;
    }

    /**
     * Create {@see Set} of {@see J2eeModule.Type} objects from array
     * of those objects.
     * <p>
     * @param moduleTypes Array of NetBeans JavaEE module types.
     * @return Newly created set of NetBeans JavaEE module types from array.
     */
    Set<J2eeModule.Type> moduleTypesSetFromArray(
            J2eeModule.Type[] moduleTypes) {
        int size = moduleTypes != null ? moduleTypes.length : 0;
        Set<J2eeModule.Type> moduleTypeSet = new HashSet<J2eeModule.Type>(size);
        for (int i = 0; i < size; i++) {
            moduleTypeSet.add(moduleTypes[i]);
        }
        return moduleTypeSet;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates an instance of GlassFish JavaEE platform.
     * <p/>
     * @param dm {@see Hk2DeploymentManager} instance containing valid reference
     *           to GlassFish server instance.
     * @param platforms   NetBeans JavaSE platforms supported.
     * @param profiles    NetBeans JavaEE profiles supported.
     * @param types       NetBeans JavaEE module types supported.
     * @param displayName NetBeans JavaEE platform display name.
     * @param libraryName NetBeans JavaEE library name.
     * @param lookupKey   GlassFish JavaEE platform lookup key.
     */
    public Hk2JavaEEPlatformImpl(final Hk2DeploymentManager dm,
            final String[] platforms,
            final Profile[] profiles, final J2eeModule.Type[] types,
            final String displayName, final String libraryName,
            final String lookupKey) {
        this.dm = dm;
        this.platforms = platforms;
        this.profiles = profiles;
        this.types = types;
        this.displayName = displayName;
        this.libraryName = libraryName;
        this.lookupKey = lookupKey;
        this.libraryProvider = Hk2LibraryProvider.getProvider(
                dm.getCommonServerSupport().getInstance());
        addFcl();
        initLibraries();
    }

    private void addFcl() {
        if (null == fcl) {
            String path = dm.getCommonServerSupport().getInstanceProperties().get(GlassfishModule.GLASSFISH_FOLDER_ATTR);
            File f = new File(path, "modules"); // NOI18N
            FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(f));
            if (null == fo) {
                Logger.getLogger("glassfish-javaee").log(Level.WARNING, "{0} did not exist but should", f.getAbsolutePath());
                return;
            }
            fcl = new FileChangeListener() {

                @Override
                public void fileFolderCreated(FileEvent fe) {
                    notifyLibrariesChanged();
                }

                @Override
                public void fileDataCreated(FileEvent fe) {
                    notifyLibrariesChanged();
                }

                @Override
                public void fileChanged(FileEvent fe) {
                    notifyLibrariesChanged();
                }

                @Override
                public void fileDeleted(FileEvent fe) {
                    notifyLibrariesChanged();
                }

                @Override
                public void fileRenamed(FileRenameEvent fe) {
                    notifyLibrariesChanged();
                }

                @Override
                public void fileAttributeChanged(FileAttributeEvent fe) {
                    // we can ignore this type of change
                }
            };
            fo.addFileChangeListener(fcl);
        }
    }

    // Persistence provider strings
    private static final String PERSISTENCE_PROV_ECLIPSELINK = "org.eclipse.persistence.jpa.PersistenceProvider"; //NOI18N

    // WEB SERVICES PROPERTIES 
    // TODO - shall be removed and usages replaced by values from j2eeserver or websvc apis after api redesign
    private static final String TOOL_WSCOMPILE = "wscompile";
    private static final String TOOL_JSR109 = "jsr109";
    private static final String TOOL_WSIMPORT = "wsimport";
    private static final String TOOL_WSGEN = "wsgen";
    private static final String TOOL_KEYSTORE = "keystore";
    private static final String TOOL_KEYSTORECLIENT = "keystoreClient";
    private static final String TOOL_TRUSTSTORE = "truststore";
    private static final String TOOL_TRUSTSTORECLIENT = "truststoreClient";
    private static final String TOOL_WSIT = "wsit";
    private static final String TOOL_JAXWSTESTER = "jaxws-tester";
    private static final String TOOL_APPCLIENTRUNTIME = "appClientRuntime";
    private static final String KEYSTORE_LOCATION = "config/keystore.jks";
    private static final String TRUSTSTORE_LOCATION = "config/cacerts.jks";

    private static final String EMBEDDED_EJB_CONTAINER_PATH = "lib/embedded/glassfish-embedded-static-shell.jar";

    /** Application client cContainer configuration file for GlassFish v3. */
    private static final String GFv3_ACC_XML = "sun-acc.xml";

    /** Application client container configuration file for GlassFish v4. */
    private static final String GFv4_ACC_XML = "glassfish-acc.xml";

    /**
     * 
     * @param toolName 
     * @return 
     */
    @Override
    public boolean isToolSupported(String toolName) {
        // Persistence Providers
        if(PERSISTENCE_PROV_ECLIPSELINK.equals(toolName)) {
            return true;
        }

        if("org.hibernate.ejb.HibernatePersistence".equals(toolName) || //NOI18N
                "oracle.toplink.essentials.PersistenceProvider".equals(toolName) || // NOI18N
                "kodo.persistence.PersistenceProviderImpl".equals(toolName) || // NOI18N
                "org.apache.openjpa.persistence.PersistenceProviderImpl".equals(toolName)) { //NOI18N
            return true;
        }
        
        if("defaultPersistenceProviderJavaEE5".equals(toolName)) {  //NOI18N
            return true;
        }
        if("eclipseLinkPersistenceProviderIsDefault2.0".equals(toolName)) {
            return true;
        }
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        if (J2eePlatform.TOOL_EMBEDDABLE_EJB.equals(toolName)) {
            File jar = new File(gfRootStr, EMBEDDED_EJB_CONTAINER_PATH);
            return jar.exists() && jar.isFile() && jar.canRead();
        }

        File wsLib = null;
        
        if (gfRootStr != null) {
            wsLib = ServerUtilities.getJarName(gfRootStr, "webservices(|-osgi).jar");
        }

        // WEB SERVICES SUPPORT
        if ((wsLib != null) && (wsLib.exists())) {      // existence of webservice libraries
            if (TOOL_WSGEN.equals(toolName)) {         //NOI18N
                return true;
            }
            if (TOOL_WSIMPORT.equals(toolName)) {      //NOI18N
                return true;
            }
            if (TOOL_WSIT.equals(toolName)) {          //NOI18N
                return true;
            }
            if (TOOL_JAXWSTESTER.equals(toolName)) {  //NOI18N
                return true;
            }
            if (TOOL_JSR109.equals(toolName)) {        //NOI18N
                return true;
            }
            if (TOOL_KEYSTORE.equals(toolName)) {      //NOI18N
                return true;
            }
            if (TOOL_KEYSTORECLIENT.equals(toolName)) {//NOI18N
                return true;
            }
            if (TOOL_TRUSTSTORE.equals(toolName)) {    //NOI18N
                return true;
            }
            if (TOOL_TRUSTSTORECLIENT.equals(toolName)) {  //NOI18N
                return true;
            }
            if (TOOL_WSCOMPILE.equals(toolName)) {  //NOI18N
                return true;
            }
            if (TOOL_APPCLIENTRUNTIME.equals(toolName)) { //NOI18N
                return true;
            }
        }
        
        return false;     
    }
    
    /**
     * 
     * @param toolName 
     * @return 
     */
    @Override
    public File[] getToolClasspathEntries(String toolName) {
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        if (null != gfRootStr) {
            if (J2eePlatform.TOOL_EMBEDDABLE_EJB.equals(toolName)) {
                return new File[]{new File(gfRootStr, EMBEDDED_EJB_CONTAINER_PATH)};
            }
            if (TOOL_WSGEN.equals(toolName) || TOOL_WSIMPORT.equals(toolName)) {
                String[] entries = new String[]{"webservices(|-osgi).jar", //NOI18N
                    "webservices-api(|-osgi).jar", //NOI18N
                    "jaxb(|-osgi).jar", //NOI18N
                    "jaxb-api(|-osgi).jar", //NOI18N
                    "javax.activation.jar"}; //NOI18N
                List<File> cPath = new ArrayList<File>();

                for (String entry : entries) {
                    File f = ServerUtilities.getWsJarName(gfRootStr, entry);
                    if ((f != null) && (f.exists())) {
                        cPath.add(f);
                    }
                }
                return cPath.toArray(new File[cPath.size()]);
            }

            if (TOOL_WSCOMPILE.equals(toolName)) {
                String[] entries = new String[] {"webservices(|-osgi).jar"}; //NOI18N
                List<File> cPath = new ArrayList<File>();

                for (String entry : entries) {
                    File f = ServerUtilities.getWsJarName(gfRootStr, entry);
                    if ((f != null) && (f.exists())) {
                        cPath.add(f);
                    }
                }
                return cPath.toArray(new File[cPath.size()]);
            }

            File domainDir;
            File gfRoot = new File(gfRootStr);
            if (gfRoot.exists()) {
                String domainDirName = dm.getProperties().getDomainDir();
                if (domainDirName != null) {
                    domainDir = new File(domainDirName);

                    if (TOOL_KEYSTORE.equals(toolName) || TOOL_KEYSTORECLIENT.equals(toolName)) {
                        return new File[]{
                                    new File(domainDir, KEYSTORE_LOCATION) //NOI18N
                                };
                    }

                    if (TOOL_TRUSTSTORE.equals(toolName) || TOOL_TRUSTSTORECLIENT.equals(toolName)) {
                        return new File[]{
                                    new File(domainDir, TRUSTSTORE_LOCATION) //NOI18N
                                };
                    }
                }
            }
        } else {
            Logger.getLogger("glassfish-javaee").log(Level.INFO, "dm has no root???", new Exception());
        }
        
        return new File[0];
    }

    /**
     * Get supported JavaEE profiles.
     * <p/>
     * @return Supported JavaEE profiles.
     */
    @Override
    public Set<Profile> getSupportedProfiles() {
        return profilesSetFromArray(profiles);
    }
    
    /**
     * Get supported JavaEE profiles.
     * <p/>
     * @return Supported JavaEE profiles.
     */
    @Override
    public Set<Profile> getSupportedProfiles(J2eeModule.Type type) {
        return profilesSetFromArray(profiles);
    }

    /**
     * Get supported JavaEE module types.
     * <p/>
     * @return Supported JavaEE module types.
     */
    @Override
    public Set<Type> getSupportedTypes() {
        return moduleTypesSetFromArray(types);
    }

    
    /**
     * 
     * @return 
     */
    @Override
    public java.io.File[] getPlatformRoots() {
        File server = getServerHome();
        if (server != null) {
            return new File[] {server};
        }
        return new File[]{};
    }

    @Override
    public File getServerHome() {
        return getExistingFolder(dm.getProperties().getGlassfishRoot());
    }

    @Override
    public File getDomainHome() {
        return getExistingFolder(dm.getProperties().getDomainDir());
    }

    @Override
    public File getMiddlewareHome() {
        return getExistingFolder(dm.getProperties().getInstallRoot());
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public LibraryImplementation[] getLibraries() {
        addFcl();
        return libraries.clone();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public java.awt.Image getIcon() {
        return ImageUtilities.loadImage("org/netbeans/modules/j2ee/hk2/resources/server.gif"); // NOI18N
    }
    
    /**
     * Get GlassFish JavaEE platform display name.
     * <p/>
     * @return GlassFish JavaEE platform display name.
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get supported JavaSE platforms.
     * <p/>
     * @return Supported JavaSE platforms.
     */
    @Override
    public Set getSupportedJavaPlatformVersions() {
        return platformsSetFromArray(platforms);
    }
    
    /**
     * Get default GlassFish JavaSE platform.
     * <p/>
     * Returns <code>null</code>.
     * <p/>
     * @return Default GlassFish JavaSE platform.
     */
    @Override
    public JavaPlatform getJavaPlatform() {
        return null;
    }
    
    /**
     * 
     */
    public void notifyLibrariesChanged() {
        initLibraries();
    }

    private static RequestProcessor libInitThread =
            new RequestProcessor("init libs -- Hk2JavaEEPlatformImpl");
    
     private void initLibraries() {
        libInitThread.post(new Runnable() {

            @Override
            public void run() {
                libraryProvider.setJavaEELibraryImplementation(
                        lib, libraryName);
                firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
            }
        });
    }

    /**
     * Return Java EE platform lookup instance or create a new one if no such instance exists.
     *
     * @return Platform lookup instance.
     */
    @Override
    public Lookup getLookup() {
        // Avoid locking when instance already exists.
        if (lkp != null)
            return lkp;
        // Create only one for the first time.
        else {
            synchronized (this) {
                if (lkp == null) {
                    String gfRootStr = dm.getProperties().getGlassfishRoot();
                    WSStack<JaxWs> wsStack = WSStackFactory.createWSStack(JaxWs.class,
                            new Hk2JaxWsStack(gfRootStr, this), WSStack.Source.SERVER);
                    WSStack<JaxRpc> rpcStack = WSStackFactory.createWSStack(JaxRpc.class,
                            new Hk2JaxRpcStack(gfRootStr), WSStack.Source.SERVER);
                    Lookup baseLookup = Lookups.fixed(gfRootStr,
                            new JaxRsStackSupportImpl(), wsStack, rpcStack,
                            new Hk2JpaSupportImpl(
                            dm.getCommonServerSupport().getInstance()));
                    lkp = LookupProviderSupport
                            .createCompositeLookup(baseLookup, lookupKey);
                }
            }
        }
        return lkp;
    }

    private File getExistingFolder(String path) {
        if (path != null) {
            File returnedElement = new File(path);
            if (returnedElement.exists()) {
                return returnedElement;
            }
        }
        return null;
    }

    /* return the string within quotes
     **/
    private String quotedString(String s){
        return "\""+s+"\"";
    }

    /**
     * Get GlassFish version.
     * <p/>
     * Returns {
     *
     * @see GlassFishVersion} for current GlassFish server instance.
     * <p/>
     * @return GlassFish version.
     */
    private GlassFishVersion getGFVersion() {
        GlassFishVersion version = null;
        try {
            version = dm
                    .getCommonServerSupport().getInstance().getVersion();
        } catch (NullPointerException npe) {
            Logger.getLogger("glassfish-javaee").log(Level.INFO,
                    "Caught NullPointerException in Hk2JavaEEPlatformImpl "
                    + "while checking GlassFish version", npe);
        }
        return version;
    }

    /**
     * Get proper application client container configuration file name
     * for GlassFish.
     * <p/>
     * @return Application client container configuration file name
     *         for GlassFish.
     */
    private String getAccConfigFile() {
        GlassFishVersion version = getGFVersion();
        String accConfigFile;
        if (version != null
                && version.ordinal() >= GlassFishVersion.GF_4.ordinal()) {
            accConfigFile = GFv4_ACC_XML;
        } else {
            accConfigFile = GFv3_ACC_XML;
        }
        StringBuilder sb = new StringBuilder(
                ServerUtils.GF_DOMAIN_CONFIG_DIR_NAME.length()
                + 1 + accConfigFile.length());
        sb.append(ServerUtils.GF_DOMAIN_CONFIG_DIR_NAME);
        sb.append(File.separatorChar);
        sb.append(accConfigFile);
        return sb.toString();
    }
    
    @Override
    public String getToolProperty(String toolName, String propertyName) {
        if (J2eePlatform.TOOL_APP_CLIENT_RUNTIME.equals(toolName)) {
            File root = new File(dm.getProperties().getGlassfishRoot());
            String domainPath = dm.getProperties().getDomainDir();
            if (J2eePlatform.TOOL_PROP_MAIN_CLASS.equals(propertyName)) {
                return "org.glassfish.appclient.client.AppClientFacade"; // NOI18N
            }
            if (J2eePlatform.TOOL_PROP_MAIN_CLASS_ARGS.equals(propertyName)) {
                return "${j2ee.appclient.tool.args}"; // NOI18N
            }
            if (J2eePlatform.TOOL_PROP_JVM_OPTS.equals(propertyName)) {
                if(domainPath != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("-Djava.endorsed.dirs=");
                     sb.append(quotedString(new File(root,"lib/endorsed").getAbsolutePath()));
                    sb.append(File.pathSeparator);
                     sb.append(quotedString(new File(root,"modules/endorsed").getAbsolutePath()));
                     sb.append(" -javaagent:");
                     String url = dm.getCommonServerSupport().getInstanceProperties().get(GlassfishModule.URL_ATTR);
                     File f = new File(root,"lib/gf-client.jar"); // NOI18N
                     if (f.exists()) {
                        sb.append(quotedString(f.getAbsolutePath()));
                     } else {
                        sb.append(quotedString(new File(root,"modules/gf-client.jar").getAbsolutePath()));
                    }
                      sb.append("=mode=acscript,arg=-configxml,arg=");
                      sb.append(quotedString(new File(domainPath, getAccConfigFile()).getAbsolutePath()));
                      sb.append(",client=jar=");
//                  sb.append(""); // path to /tmp/test/ApplicationClient1Client.jar
//                   sb.append(" -jar ");
//                    sb.append(""); // path to /tmp/test/ApplicationClient1Client.jar
                    return sb.toString();
                } else {
                    return null;
                }
            }
            if (J2eePlatform.TOOL_PROP_CLIENT_JAR_LOCATION.equals(propertyName)) {
                if(domainPath != null) {
                    FileObject location = FileUtil.toFileObject(FileUtil.normalizeFile(new File(domainPath)));
                    if (location == null) {
                        return null;
                    }
                    return (FileUtil.toFile(location).getAbsolutePath())+File.separator+"generated"+File.separator+"xml"; // NOI18N
                } else {
                    return null;
                }
            }
            if ("j2ee.appclient.args".equals(propertyName)) { // NOI18N
                return null; // "-configxml " + quotedString(new File(dmProps.getLocation(), dmProps.getDomainName() +
                // "/config/sun-acc.xml").getAbsolutePath()); // NOI18N
            }
        }
        return null;
    }

    /**
     * Get GlassFish bundled libraries provider.
     * <p/>
     * @return GlassFish bundled libraries provider.
     */
    public Hk2LibraryProvider getLibraryProvider() {
        return libraryProvider;
    }

    private class JaxRsStackSupportImpl implements JaxRsStackSupportImplementation {
        
        private static final String VERSION_30X = "v3";     // NOI18N
        private static final String VERSION_31X = "3.1";    // NOI18N

        /* (non-Javadoc)
         * @see org.netbeans.modules.javaee.specs.support.spi.JaxRsStackSupportImplementation#addJsr311Api(org.netbeans.api.project.Project)
         */
        @Override
        public boolean addJsr311Api( Project project ) {
            Library library = libraryProvider.getJaxRsLibrary();
            if ( library!= null ){
                try {
                    String classPathType;
                    if (hasJee6Profile()) {
                        classPathType = JavaClassPathConstants.COMPILE_ONLY;
                    } else {
                        classPathType = ClassPath.COMPILE;
                    }
                    return ProjectClassPathModifier.addLibraries(
                            new Library[] { library }, getSourceRoot(project), 
                            classPathType);
                }
                catch (UnsupportedOperationException ex) {
                    return false;
                }
                catch (IOException e) {
                    return false;
                }
            }
            String version = getGFVersion();
            try {
                if (version == null) {
                    return false;
                }
                else if (version.startsWith(VERSION_30X)) {
                    File jsr311 = ServerUtilities.getJarName(dm.getProperties()
                            .getGlassfishRoot(), "jsr311-api.jar"); // NOI18N
                    if (jsr311 == null || !jsr311.exists()) {
                        return false;
                    }
                    return addJars(project, Collections.singletonList(Utilities
                            .toURI(jsr311).toURL()));
                }
                else if (version.startsWith(VERSION_31X)) {
                    File jerseyCore = ServerUtilities.getJarName(dm.getProperties().
                            getGlassfishRoot(), "jersey-core.jar");          // NOI18N
                    if ( jerseyCore== null || !jerseyCore.exists()){
                        return false;
                    }
                    return addJars(project, Collections.singletonList(
                            Utilities.toURI(jerseyCore).toURL()));
                }
            } catch (MalformedURLException ex) {
                return false;
            }
            return false;
        }

        /* (non-Javadoc)
         * @see org.netbeans.modules.javaee.specs.support.spi.JaxRsStackSupportImplementation#extendsJerseyProjectClasspath(org.netbeans.api.project.Project)
         */
        @Override
        public boolean extendsJerseyProjectClasspath( Project project ) {
            Library library = libraryProvider.getJerseyLibrary();
            FileObject sourceRoot = getSourceRoot(project);
            if (sourceRoot == null) {
                return false;
            }
            try {
                String classPathType;
                if (hasJee6Profile()) {
                    classPathType = JavaClassPathConstants.COMPILE_ONLY;
                }
                else {
                    classPathType = ClassPath.COMPILE;
                }
                return ProjectClassPathModifier.addLibraries(
                        new Library[] { library }, sourceRoot, classPathType);
            }
            catch (UnsupportedOperationException ex) {
                return false;
            }
            catch (IOException e) {
                return false;
            }
            /*List<URL> urls = getJerseyLibraryURLs();
            if ( urls.sdkSize() >0 ){
                return addJars( project , urls );
            }*/
        }
        
        @Override
        public void removeJaxRsLibraries(Project project) {
            Library library = libraryProvider.getJerseyLibrary();
            FileObject sourceRoot = getSourceRoot(project);
            if ( sourceRoot != null){
                String[] classPathTypes = new String[]{ ClassPath.COMPILE , 
                        JavaClassPathConstants.COMPILE_ONLY };
                for (String type : classPathTypes) {
                    try {
                        ProjectClassPathModifier.removeLibraries( new Library[]{
                                library} ,sourceRoot, type);
                    }    
                    catch(UnsupportedOperationException ex) {
                        Logger.getLogger( JaxRsStackSupportImpl.class.getName() ).
                                log (Level.INFO, null , ex );
                    }
                    catch( IOException e ){
                        Logger.getLogger( JaxRsStackSupportImpl.class.getName() ).
                                log(Level.INFO, null , e );
                    }
                }     
            }
            /*List<URL> urls = getJerseyLibraryURLs();
            if ( urls.sdkSize() >0 ){
                SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(
                    JavaProjectConstants.SOURCES_TYPE_JAVA);
                if (sourceGroups == null || sourceGroups.length < 1) {
                    return;
                }
                FileObject sourceRoot = sourceGroups[0].getRootFolder();
                String[] classPathTypes = new String[]{ ClassPath.COMPILE , ClassPath.EXECUTE };
                for (String type : classPathTypes) {
                    try {
                        ProjectClassPathModifier.removeRoots(urls.toArray( 
                            new URL[ urls.sdkSize()]), sourceRoot, type);
                    }    
                    catch(UnsupportedOperationException ex) {
                        Logger.getLogger( JaxRsStackSupportImpl.class.getName() ).
                                log (Level.INFO, null , ex );
                    }
                    catch( IOException e ){
                        Logger.getLogger( JaxRsStackSupportImpl.class.getName() ).
                                log(Level.INFO, null , e );
                    }
                }     
            }*/
        }
        
        @Override
        public void configureCustomJersey( Project project ){
        }
        
        /* (non-Javadoc)
         * @see org.netbeans.modules.javaee.specs.support.spi.JaxRsStackSupportImplementation#isBundled(java.lang.String)
         */
        @Override
        public boolean isBundled( String classFqn ) {
            List<URL> urls = libraryProvider.getJerseyClassPathURLs();
            for( URL url : urls ){
                FileObject root = URLMapper.findFileObject(url);
                if ( FileUtil.isArchiveFile(root)){
                    root = FileUtil.getArchiveRoot(root);
                }
                String path = classFqn.replace('.', '/')+".class";
                if ( root.getFileObject(path )!= null ){
                    return true;
                }
            }
            return false;
        }

        private FileObject getSourceRoot(Project project) {
            SourceGroup[] sourceGroups = ProjectUtils.getSources(project)
                    .getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (sourceGroups == null || sourceGroups.length < 1) {
                return null;
            }
            return sourceGroups[0].getRootFolder();
        }
        
        private boolean hasJee6Profile(){
            Set<Profile> profiles = getSupportedProfiles();
            return profiles.contains(Profile.JAVA_EE_6_FULL) || 
                profiles.contains(Profile.JAVA_EE_6_WEB) ;
        }
        
        private void addURL( Collection<URL> urls, File file ){
            if ( file == null || !file.exists()) {
                return;
            }
            try {
                urls.add(Utilities.toURI(file).toURL());
            } catch (MalformedURLException ex) {
                // ignore the file
            }
        }
        
        private boolean addJars( Project project, Collection<URL> jars ){
            List<URL> urls = new ArrayList<URL>();
            for (URL url : jars) {
                if ( FileUtil.isArchiveFile( url)){
                    urls.add(FileUtil.getArchiveRoot(url));
                }
            }
            SourceGroup[] sourceGroups = ProjectUtils.getSources(project).getSourceGroups(
                JavaProjectConstants.SOURCES_TYPE_JAVA);
            if (sourceGroups == null || sourceGroups.length < 1) {
               return false;
            }
            FileObject sourceRoot = sourceGroups[0].getRootFolder();
            try {
                String classPathType;
                if ( hasJee6Profile() ){
                    classPathType = JavaClassPathConstants.COMPILE_ONLY;
                }
                else {
                    classPathType = ClassPath.COMPILE;
                }
                ProjectClassPathModifier.addRoots(urls.toArray( new URL[ urls.size()]), 
                        sourceRoot, classPathType );
            } 
            catch(UnsupportedOperationException ex) {
                return false;
            }
            catch ( IOException e ){
                return false;
            }
            return true;
        }
        
        /**
         * Get GlassFish version as string.
         * <p/>
         * Returns {@see GlassFishVersion#toString()} method output for current
         * GlassFish server instance.
         * <p/>
         * @return GlassFish version as string.
         */
        private String getGFVersion() {
            GlassFishVersion version = null;
            try {
                version = dm
                        .getCommonServerSupport().getInstance().getVersion();
            } catch (NullPointerException npe) {
                Logger.getLogger("glassfish-javaee").log(Level.INFO,
                        "Caught NullPointerException in Hk2JavaEEPlatformImpl "
                        + "while checking GlassFish version", npe);
            }
            return version != null ? version.toString() : "";
        }
    }

    private static class RegistrationHandler extends DefaultHandler {
        
        private static final String VERSION_TAG = "product_version";    // NOI18N

        @Override
        public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException 
        {
            super.startElement(uri, localName, qName, attributes);
            if (VERSION_TAG.equals( localName )|| VERSION_TAG.equals( qName )){
                versionTag = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) 
                throws SAXException 
        {
            super.endElement(uri, localName, qName);
            if (VERSION_TAG.equals( localName )|| VERSION_TAG.equals( qName )){
                versionTag = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if ( versionTag ){
                version.append(ch, start, length);
            }
        }
        
        String getVersion() {
            return version.toString();
        }
        
        private boolean versionTag;
        private StringBuilder version = new StringBuilder();
    }
}
