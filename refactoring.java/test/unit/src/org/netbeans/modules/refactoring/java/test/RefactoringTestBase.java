/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009-2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.refactoring.java.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.SourceUtilsTestUtil;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.core.startup.Main;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.source.indexing.JavaCustomIndexer;
import org.netbeans.modules.parsing.impl.indexing.CacheFolder;
import org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater;
import org.netbeans.modules.parsing.impl.indexing.Util;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.spi.editor.mimelookup.MimeDataProvider;
import org.netbeans.spi.gototest.TestLocator;
import org.netbeans.spi.gototest.TestLocator.FileType;
import org.netbeans.spi.gototest.TestLocator.LocationListener;
import org.netbeans.spi.gototest.TestLocator.LocationResult;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.support.GenericSources;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

public class RefactoringTestBase extends NbTestCase {

    public RefactoringTestBase(String name) {
        super(name);
    }

    protected static void writeFilesAndWaitForScan(FileObject sourceRoot, File... files) throws Exception {
        for (FileObject c : sourceRoot.getChildren()) {
            c.delete();
        }

        for (File f : files) {
            FileObject fo = FileUtil.createData(sourceRoot, f.filename);
            TestUtilities.copyStringToFile(fo, f.content);
        }

        SourceUtils.waitScanFinished();
    }

    protected void verifyContent(FileObject sourceRoot, File... files) throws Exception {
        List<FileObject> todo = new LinkedList<FileObject>();

        todo.add(sourceRoot);

        Map<String, String> content = new HashMap<String, String>();

        while (!todo.isEmpty()) {
            FileObject file = todo.remove(0);

            if (file.isData()) {
                content.put(FileUtil.getRelativePath(sourceRoot, file), TestUtilities.copyFileToString(FileUtil.toFile(file)));
            } else {
                todo.addAll(Arrays.asList(file.getChildren()));
            }
        }

        for (File f : files) {
            String fileContent = content.remove(f.filename);

            assertEquals(getName() ,f.content.replaceAll("[ \t\n]+", " "), fileContent.replaceAll("[ \t\n]+", " "));
        }

        assertTrue(content.toString(), content.isEmpty());
    }

    protected static void addAllProblems(List<Problem> problems, Problem head) {
        while (head != null) {
            problems.add(head);
            head = head.getNext();
        }
    }

    protected static void assertProblems(Iterable<? extends Problem> golden, Iterable<? extends Problem> real) {
        Iterator<? extends Problem> g = golden.iterator();
        Iterator<? extends Problem> r = real.iterator();

        while (g.hasNext() && r.hasNext()) {
            Problem gp = g.next();
            Problem rp = r.next();

            assertEquals(gp.isFatal(), rp.isFatal());
            assertEquals(gp.getMessage(), rp.getMessage());
        }
        boolean goldenHasNext = g.hasNext();
        boolean realHasNext = r.hasNext();

        assertFalse(goldenHasNext?"Expected: " + g.next().getMessage():"", goldenHasNext);
        assertFalse(realHasNext?"Unexpected: " + r.next().getMessage():"", realHasNext);
    }

    static {
        NbBundle.setBranding("test");
    }

    protected static final class File {
        public final String filename;
        public final String content;

        public File(String filename, String content) {
            this.filename = filename;
            this.content = content;
        }
    }

    protected FileObject src;
    protected FileObject test;
    protected Project prj;

    @Override
    protected void setUp() throws Exception {
        Util.allMimeTypes = new HashSet<String>();
        SourceUtilsTestUtil.prepareTest(new String[] {"org/netbeans/modules/openide/loaders/layer.xml",
            "org/netbeans/modules/java/source/resources/layer.xml",
            "org/netbeans/modules/java/editor/resources/layer.xml",
            "org/netbeans/libs/freemarker/layer.xml",
            "org/netbeans/modules/refactoring/java/test/resources/layer.xml", "META-INF/generated-layer.xml"}, new Object[] {
            new ClassPathProvider() {
                public ClassPath findClassPath(FileObject file, String type) {
                    if ((src != null && (file == src || FileUtil.isParentOf(src, file)))
                            || (test != null && (file == test || FileUtil.isParentOf(test, file)))){
                        if (ClassPath.BOOT.equals(type)) {
                            return ClassPathSupport.createClassPath(System.getProperty("sun.boot.class.path"));
                        }
                        if (ClassPath.COMPILE.equals(type)) {
                            return ClassPathSupport.createClassPath(new FileObject[0]);
                        }
                        if (ClassPath.SOURCE.equals(type)) {
                            return ClassPathSupport.createClassPath(src, test);
                        }
                    }

                    return null;
                }
            },
            new ProjectFactory() {
            public boolean isProject(FileObject projectDirectory) {
                return src.getParent() == projectDirectory;
            }
            public Project loadProject(final FileObject projectDirectory, ProjectState state) throws IOException {
                if (!isProject(projectDirectory)) return null;
                return new Project() {
                    public FileObject getProjectDirectory() {
                        return projectDirectory;
                    }
                    public Lookup getLookup() {
                        final Project p = this;
                        return Lookups.singleton(new Sources() {

                            @Override
                            public SourceGroup[] getSourceGroups(String type) {
                                return new SourceGroup[] {GenericSources.group(p, src.getParent(), "", "", null, null)};//,
//                                                          GenericSources.group(p, test, "testsources", "Test Sources", null, null)};
                            }

                            @Override
                            public void addChangeListener(ChangeListener listener) {
                            }

                            @Override
                            public void removeChangeListener(ChangeListener listener) {
                            }
                        });
                    }
                };
            }
            public void saveProject(Project project) throws IOException, ClassCastException {}
            },
            new TestLocator() {

            @Override
            public boolean appliesTo(FileObject fo) {
                return true;
            }

            @Override
            public boolean asynchronous() {
                return false;
            }

            @Override
            public LocationResult findOpposite(FileObject fo, int caretOffset) {
                ClassPath srcCp;
        
                if ((srcCp = ClassPath.getClassPath(fo, ClassPath.SOURCE)) == null) {
                    return new LocationResult("File not found"); //NOI18N
                }

                String baseResName = srcCp.getResourceName(fo, '/', false);
                String testResName = getTestResName(baseResName, fo.getExt());
                assert testResName != null;
                FileObject fileObject = test.getFileObject(testResName);
                if(fileObject != null) {
                    return new LocationResult(fileObject, -1);
                }
                
                return new LocationResult("File not found"); //NOI18N
            }

            @Override
            public void findOpposite(FileObject fo, int caretOffset, LocationListener callback) {
                throw new UnsupportedOperationException("This should not be called on synchronous locators.");
            }

            @Override
            public FileType getFileType(FileObject fo) {
                if(FileUtil.isParentOf(test, fo)) {
                    return FileType.TEST;
                } else if(FileUtil.isParentOf(src, fo)) {
                    return FileType.TESTED;
                }
                return FileType.NEITHER;
            }
            
            private String getTestResName(String baseResName, String ext) {
                StringBuilder buf
                        = new StringBuilder(baseResName.length() + ext.length() + 10);
                buf.append(baseResName).append("Test");                         //NOI18N
                if (ext.length() != 0) {
                    buf.append('.').append(ext);
                }
                return buf.toString();
            }
        }});
        Main.initializeURLFactory();
        org.netbeans.api.project.ui.OpenProjects.getDefault().getOpenProjects();
        prepareTest();
        org.netbeans.api.project.ui.OpenProjects.getDefault().open(new Project[] {prj = ProjectManager.getDefault().findProject(src.getParent())}, false);
        Util.allMimeTypes = Collections.singleton("text/x-java");
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, new ClassPath[] {ClassPathSupport.createClassPath(src),
                                                                                    ClassPathSupport.createClassPath(test)});
        RepositoryUpdater.getDefault().start(true);
        super.setUp();
        FileUtil.createData(FileUtil.getConfigRoot(), "Templates/Classes/Empty.java");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        org.netbeans.api.project.ui.OpenProjects.getDefault().open(new Project[] {prj}, false);
        prj = null;
    }

    private void prepareTest() throws Exception {
        FileObject workdir = SourceUtilsTestUtil.makeScratchDir(this);
        
        FileObject projectFolder = FileUtil.createFolder(workdir, "testProject");
        src = FileUtil.createFolder(projectFolder, "src");
        test = FileUtil.createFolder(projectFolder, "test");

        FileObject cache = FileUtil.createFolder(workdir, "cache");

        CacheFolder.setCacheFolder(cache);
    }

    @ServiceProvider(service=MimeDataProvider.class)
    public static final class MimeDataProviderImpl implements MimeDataProvider {

        private static final Lookup L = Lookups.singleton(new JavaCustomIndexer.Factory());

        public Lookup getLookup(MimePath mimePath) {
            if ("text/x-java".equals(mimePath.getPath())) {
                return L;
            }

            return null;
        }
        
    }
    
    protected static boolean problemIsFatal(List<Problem> problems) {
        for (Problem problem : problems) {
            if (problem.isFatal()) {
                return true;
            }
        }
        return false;
    }
}