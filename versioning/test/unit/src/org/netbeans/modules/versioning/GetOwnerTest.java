/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.modules.versioning;

import java.io.IOException;
import java.io.File;
import java.security.Permission;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.versioning.spi.VersioningSystem;
import org.netbeans.modules.versioning.spi.testvcs.TestVCS;

public class GetOwnerTest extends NbTestCase {
    
    protected File dataRootDir;
    private StatFiles accessMonitor;
    private SecurityManager defaultSecurityManager;
    protected File versionedFolder;
    protected File unversionedFolder;

    public GetOwnerTest(String testName) {
        super(testName);
        accessMonitor = new StatFiles();
    }

    protected File getVersionedFolder() {
        if (versionedFolder == null) {
            versionedFolder = new File(dataRootDir, "workdir/root-test-versioned/");
            versionedFolder.mkdirs();
        }
        return versionedFolder;
    }
    
    protected File getUnversionedFolder() {
        if (unversionedFolder == null) {
            unversionedFolder = new File(dataRootDir, "workdir/unversioned/");
            unversionedFolder.mkdirs();
        }
        return unversionedFolder;
    }

    protected void setUp() throws Exception {
        super.setUp();
        dataRootDir = getWorkDir();
        File userdir = new File(getWorkDir(), "userdir");
        userdir.mkdirs();
        System.setProperty("netbeans.user", userdir.getAbsolutePath());
        if(accessMonitor != null) {
            if(defaultSecurityManager == null) {
                defaultSecurityManager = System.getSecurityManager();
            }
            System.setSecurityManager(accessMonitor);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(accessMonitor != null) {
            System.setSecurityManager(defaultSecurityManager);
        }
    }
     
    public void testGetOwnerKnowFileType() throws IOException {
        assertTrue(VersioningManager.getInstance().getOwner(getVersionedFolder()).getClass() == getVCS());
        File f = new File(getVersionedFolder(), "file");
        f.createNewFile();
        
        testGetOwnerKnowFileType(f, true);
        
        f = new File(getVersionedFolder(), "folder");
        f.mkdirs();
        testGetOwnerKnowFileType(f, false);                         
    }

    protected Class getVCS() {
        return TestVCS.class;
    }
    
    private void testGetOwnerKnowFileType(File f, boolean isFile) throws IOException {                
        accessMonitor.files.clear();
        VersioningSystem vs = VersioningManager.getInstance().getOwner(f, isFile); // true => its a file, no io.file.isFile() call needed
        assertNotNull(vs);
        
        // file wasn't accessed even on first shot
        assertFalse(accessMonitor.files.contains(f.getAbsolutePath()));
        
        accessMonitor.files.clear();
        vs = VersioningManager.getInstance().getOwner(f, isFile);
        assertNotNull(vs);
        
        // file wasn't accessed
        assertFalse(accessMonitor.files.contains(f.getAbsolutePath()));               
    }
    
    public void testGetOwnerVersioned() throws IOException {
        assertTrue(VersioningManager.getInstance().getOwner(getVersionedFolder()).getClass() == getVCS());
        File aRoot = new File(getVersionedFolder(), "a.txt");
        assertTrue(VersioningManager.getInstance().getOwner(aRoot).getClass() ==  getVCS());
        
        aRoot = new File(getVersionedFolder(), "b-folder");
        aRoot.mkdirs();
        assertTrue(VersioningManager.getInstance().getOwner(aRoot).getClass() ==  getVCS());
        aRoot = new File(aRoot, "deep-file");
        aRoot.createNewFile();
        assertTrue(VersioningManager.getInstance().getOwner(aRoot).getClass() ==  getVCS());
        
        aRoot = new File(getVersionedFolder(), "nonexistent-file");
        assertTrue(VersioningManager.getInstance().getOwner(aRoot).getClass() ==  getVCS());
    }
    
    public void testGetOwnerUnversioned() throws IOException {
        File aRoot = File.listRoots()[0];
        assertNull(VersioningManager.getInstance().getOwner(aRoot));
        aRoot = dataRootDir;
        assertNull(VersioningManager.getInstance().getOwner(aRoot));
        aRoot = new File(dataRootDir, "workdir");
        assertNull(VersioningManager.getInstance().getOwner(aRoot));               
        
        assertNull(VersioningManager.getInstance().getOwner(getUnversionedFolder()));        

        File f = new File(getUnversionedFolder(), "a.txt");
        f.createNewFile();
        assertNull(VersioningManager.getInstance().getOwner(f));
        
        f = new File(getUnversionedFolder(), "notexistent.txt");
        assertNull(VersioningManager.getInstance().getOwner(f));        
    }
    
    public void testFileOwnerCache() throws IOException {
        testFileOwnerCache(true /* versioned */ , false /* file */);
        testFileOwnerCache(false/* versioned */ , false /* file */);
    }
        
    public void testFolderOwnerCache() throws IOException {
        testFileOwnerCache(true /* unversioned */ , true /* folder */);
        testFileOwnerCache(false/* unversioned */ , true /* folder */);
    }
        
    private void testFileOwnerCache(boolean isVersioned, boolean isFolder) throws IOException {
        File folder = isVersioned ? getVersionedFolder() : getUnversionedFolder();
        File child = new File(folder, "file");
        File child2 = new File(folder, "file2");
        if(isFolder) {
            child.mkdirs();
            child2.mkdirs();
        } else {
            child.createNewFile();
            child2.createNewFile();
        }
        
        assertFileAccess(child, isVersioned, true /* access */);
        
        // try again - shouldn't be accessed anymore
        assertFileAccess(child, isVersioned, false /* no access */);        
        
        // try few more times some other file no file access expected
        assertFileAccess(child2, isVersioned, true /* access */);
        for (int i = 0; i < 100; i++) {
            // try some other file
            assertFileAccess(child2, isVersioned, false /* no access */);
        }        
        
        // try the first file again
        assertFileAccess(child, isVersioned, false /* no access */);        
    }
    
    private void assertFileAccess(File f, boolean versioned, boolean access) {
        accessMonitor.files.clear();
        VersioningSystem vs = VersioningManager.getInstance().getOwner(f);
        if(versioned && vs == null) {
            fail("no VersioningSystem returned for versioned file " + f);
        } else if(!versioned && vs != null) {
            fail("VersioningSystem returned for unversioned file " + f);
        }
        // file was accessed
        boolean accessed = accessMonitor.files.contains(f.getAbsolutePath());
        if(access && !accessed) {
            fail(f + " was not but should be accessed");
        } else if (!access && accessed) {
            fail(f + " was accessed but shouldn't");            
        }
    }    

    private class StatFiles extends SecurityManager {
        private List<String> files = new LinkedList<String>();        
        @Override
        public void checkRead(String file) {
            files.add(file);
        }       
        @Override
        public void checkPermission(Permission perm) {
        }
    }    
    
}
