/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.websvc.rest.codegen;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;
import junit.framework.Assert;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.websvc.rest.support.JavaSourceHelper;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author nam
 */
public abstract class TestBase extends NbTestCase {

    static Repository defaultRepository;
    static ClassPathProviderImpl defaultCPP = new ClassPathProviderImpl();

    public static final String TESTSRC = "testsrc";
    public static final String TESTSRC_ACME = TESTSRC+"/com/acme";
    public static final String PACKAGE_ACME = "com.acme";
    public static final String USER_DIR = "netbeans.user";
    
    protected List<FileObject> entityFOs = new ArrayList<FileObject>();
    protected FileObject entityClassDirFO;
    protected FileObject genSourceRoot;
    protected ClassPath compileClassPath = null;
    protected ClassPath bootClassPath = null;
    
    public TestBase(String name) {
        super(name);
        setLookups(Lookups.singleton(getDefaultRepository()));
    }

    static {
        System.setProperty("org.openide.util.Lookup",TestBase.Lkp.class.getName());
        //Main.initializeURLFactory();
    }   
    
    public static void setLookups(Object... lookups) {
        ((Lkp)Lookup.getDefault()).setProxyLookups(Lookups.fixed(lookups));
    }
    
    public static void setSourceClassPath(FileObject[] sources) {
        defaultCPP.setSourceClassPath(sources);
    }
    
    public static final class Lkp extends ProxyLookup {
        
        public Lkp() {
            setProxyLookups(new Lookup[0]);
        }
        
        public void setProxyLookups(Lookup... lookups) {
            Lookup[] allLookups = new Lookup[lookups.length+3];
            ClassLoader classLoader = TestBase.class.getClassLoader();
            allLookups[0] = Lookups.singleton(classLoader);
            allLookups[1] = Lookups.singleton(defaultCPP);
            System.arraycopy(lookups, 0, allLookups, 2, lookups.length);
            allLookups[allLookups.length - 1] = Lookups.metaInfServices(classLoader);
            setLookups(allLookups);
        }

    }
    
    public static Repository getDefaultRepository() {
        if (defaultRepository == null) {
            XMLFileSystem xmlFS = new XMLFileSystem();
            URL url = Thread.currentThread().getContextClassLoader().getResource(
                    "org/netbeans/modules/websvc/rest/resources/layer.xml");
            URL url2 = TestBase.class.getResource("layer.xml"); 
            Assert.assertNotNull(url);
            Assert.assertNotNull(url2);
            try {
                xmlFS.setXmlUrls(new URL[] { url, url2 });
            } catch(Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
            FileSystem system = new MultiFileSystem(new FileSystem[] {FileUtil.createMemoryFileSystem(), xmlFS});
            defaultRepository = new Repository(system);
        }
        return defaultRepository;
    }

    public static class ClassPathProviderImpl implements ClassPathProvider {

        private ClassPath bootClassPath;
        private ClassPath compileClassPath;
        private ClassPath sourceClassPath;

        public ClassPath findClassPath(FileObject file, String type) {
            if (type == ClassPath.SOURCE) {
                return getSourceClassPath();
            } else if (type == ClassPath.COMPILE) {
                return getCompileClassPath();
            } else if (type == ClassPath.BOOT) {
                return getBootClassPath();
            }
            return null;
        }

        public void setSourceClassPath(FileObject[] sources) {
            sourceClassPath = ClassPathSupport.createClassPath(sources);
        }

        public ClassPath getSourceClassPath() {
            return sourceClassPath;
        }
        
        public ClassPath getCompileClassPath() {
            if (compileClassPath == null) {
                URL statelessAnnotationURL = Entity.class.getProtectionDomain().getCodeSource().getLocation();
                compileClassPath = ClassPathSupport.createClassPath(new URL[] { FileUtil.getArchiveRoot(statelessAnnotationURL) });
            }
            return compileClassPath;
        }
        
        public ClassPath getBootClassPath() {
            if (bootClassPath == null) {
                String bootPath = System.getProperty ("sun.boot.class.path");
                String[] paths = bootPath.split(File.pathSeparator);
                List<URL>roots = new ArrayList<URL> (paths.length);
                for (String path : paths) {
                    File f = new File (path);            
                    if (!f.exists()) {
                        continue;
                    }
                    try {
                        URL url = f.toURI().toURL();
                        if (FileUtil.isArchiveFile(url)) {
                            url = FileUtil.getArchiveRoot(url);
                        }
                        roots.add (url);
                    } catch(Exception ex) {
                        
                    }
                }
                bootClassPath = ClassPathSupport.createClassPath(roots.toArray(new URL[roots.size()]));
            }
            return bootClassPath;
        }
    }
    
    protected void setUpSrcDir() throws Exception {
        
        if (System.getProperty(USER_DIR) == null) {
            System.setProperty(USER_DIR, getWorkDir()+"/.netbeans");
        }
        File userdir = new File(System.getProperty(USER_DIR));
        if (! userdir.isDirectory()) {
            userdir.mkdirs();
        }
        File genSourceDir = new File(getWorkDir(), TESTSRC);
        if (genSourceDir.isDirectory()) {
            FileUtil.toFileObject(genSourceDir).delete();
        }
        File entityClassDir = new File(getWorkDir(), TESTSRC_ACME);
        entityClassDir.mkdirs();
        entityClassDirFO = FileUtil.toFileObject(new File(getWorkDir(), TESTSRC_ACME));
        genSourceRoot = FileUtil.toFileObject(genSourceDir);
        setSourceClassPath(new FileObject[] { genSourceRoot });
    }
    
    protected void setUpEntities() throws Exception {
        setUpSrcDir();
        copyEntityClasses();
    }
    
    private void copyEntityClasses() throws Exception {
        File entityClassDataDir = new File(getDataDir(), TESTSRC_ACME);
        FileObject entityClassDataDirFO = FileUtil.toFileObject(entityClassDataDir);
        for (FileObject fo : entityClassDataDirFO.getChildren()) {
            if (fo.isFolder()) continue;
            FileUtil.copyFile(fo, entityClassDirFO, fo.getName());
        }
    }
    
    
    static Collection<JavaSource> entities = null;
    public Collection<JavaSource> getEntities() throws Exception {
        if (entities != null) return entities;
        
        setUpEntities();
        
        entities = new ArrayList<JavaSource>();
        for (FileObject child : entityClassDirFO.getChildren()) {
            if (! child.isData() || ! "java".equals(child.getExt())) {
                continue;
            }
            FileObject entityFO =  entityClassDirFO.getFileObject(child.getNameExt());
            JavaSource js = JavaSource.create(ClasspathInfo.create(
                    defaultCPP.getBootClassPath(), defaultCPP.getCompileClassPath(), defaultCPP.getSourceClassPath()), 
                    Collections.singleton(entityFO));
            assertNotNull("can't get javasource for "+entityFO.getPath(), js);
            if (js != null && JavaSourceHelper.isEntity(js)) {
                entities.add(js);
            }
        }
        return entities;
    }
    
}
