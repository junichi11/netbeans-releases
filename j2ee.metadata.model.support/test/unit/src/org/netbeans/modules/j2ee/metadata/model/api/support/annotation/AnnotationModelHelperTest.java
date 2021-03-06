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

package org.netbeans.modules.j2ee.metadata.model.api.support.annotation;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.TypesEvent;
import org.netbeans.modules.j2ee.metadata.model.support.TestUtilities;
import org.netbeans.modules.j2ee.metadata.model.support.PersistenceTestCase;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.netbeans.modules.parsing.impl.indexing.RepositoryUpdater;

/**
 *
 * @author Andrei Badea
 */
public class AnnotationModelHelperTest extends PersistenceTestCase {

    public AnnotationModelHelperTest(String testName) {
        super(testName);
    }

    public void testUserActionTask() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        final String expected = "foo";
        String returned = helper.runJavaSourceTask(new Callable<String>() {
            public String call() throws Exception {
                assertNotNull(helper.getCompilationController());
                return expected;
            }
        });
        assertEquals(expected, returned);
    }

    public void testGetSuperclasses() throws Exception {
        TestUtilities.copyStringToFileObject(srcFO, "Person.java",
                "public interface Person {" +
                "   String getName();" +
                "}");
        TestUtilities.copyStringToFileObject(srcFO, "Employee.java",
                "@javax.persistence.MappedSuperclass()" +
                "public class Employee implements Person {" +
                "   public String getName() {" +
                "       return null;" +
                "   }" +
                "}");
        TestUtilities.copyStringToFileObject(srcFO, "PartTimeEmployee.java",
                "@javax.persistence.Entity()" +
                "public class PartTimeEmployee extends Employee {" +
                "   public int getHoursPerDay() {" +
                "       return 0;" +
                "   }" +
                "}");
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        helper.runJavaSourceTask(new Runnable() {
            public void run() {
                TypeElement pte = helper.getCompilationController().getElements().getTypeElement("PartTimeEmployee");
                List<? extends TypeElement> superclasses = helper.getSuperclasses(pte);
                assertEquals(1, superclasses.size());
                assertTrue(superclasses.get(0).getQualifiedName().contentEquals("Employee"));
            }
        });
    }

    public void testJavaContextListener() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        final boolean[] contextLeft = { false };
        JavaContextListener listener = new JavaContextListener() {
            public void javaContextLeft() {
                contextLeft[0] = true;
            }
        };
        helper.addJavaContextListener(listener);
        Callable<Void> empty = new Callable<Void>() {
            public Void call() {
                return null;
            }
        };
        helper.runJavaSourceTask(empty);
        assertTrue(contextLeft[0]);
        contextLeft[0] = false;
        helper.runJavaSourceTask(empty, false);
        assertFalse(contextLeft[0]);
        Future<Void> future = helper.runJavaSourceTaskWhenScanFinished(empty);
        if (!future.isDone()) {
            SourceUtils.waitScanFinished();
        }
        assertTrue(contextLeft[0]);
        WeakReference<JavaContextListener> listenerRef = new WeakReference<JavaContextListener>(listener);
        listener = null;
        assertGC("Should be possible to GC listener", listenerRef);
    }

    public void testRecursiveUserActionTask() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        helper.runJavaSourceTask(new Callable<Void>() {
            public Void call() throws IOException {
                final JavaSource js1 = helper.javaSource;
                helper.runJavaSourceTask(new Callable<Void>() {
                    public Void call() {
                        JavaSource js2 = helper.javaSource;
                        assertSame(js1, js2);
                        return null;
                    }
                });
                return  null;
            }
        });
    }

    public void testGetCompilationControllerFromAnotherThread() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    latch1.await();
                } catch (InterruptedException e) {}
                try {
                    helper.getCompilationController();
                    fail();
                } catch (IllegalStateException e) {}
                latch2.countDown();
            }
        });
        t.start();
        helper.runJavaSourceTask(new Callable<Void>() {
            public Void call() throws Exception {
                latch1.countDown();
                try {
                    latch2.await();
                } catch (InterruptedException e) {}
                return null;
            }
        });
        t.join();
    }

    public void testUserActionTaskSingleThread() throws Exception {
        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean longTaskFinished = new AtomicBoolean();
        final Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {}
                try {
                    helper.runJavaSourceTask(new Callable<Void>() {
                        public Void call() throws Exception {
                            if (!longTaskFinished.get()) {
                                fail();
                            }
                            return null;
                        }
                    });
                } catch (IOException e) {}
            }
        });
        t.start();
        helper.runJavaSourceTask(new Callable<Void>() {
            public Void call() throws Exception {
                latch.countDown();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
                longTaskFinished.set(true);
                return null;
            }
        });
        t.join();
    }

    public void testWhenScanFinished() throws Exception {
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, new ClassPath[] { ClassPath.getClassPath(srcFO, ClassPath.SOURCE) });
        ClasspathInfo cpi = ClasspathInfo.create(srcFO);
        final AnnotationModelHelper helper = AnnotationModelHelper.create(cpi);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch scanBlockingLatch = new CountDownLatch(1);
        ClassIndex index = cpi.getClassIndex();
        index.addClassIndexListener(new ClassIndexAdapter() {
            public void typesAdded(TypesEvent event) {
                // called as a result of creating Person.java below
                startLatch.countDown();
                try {
                    scanBlockingLatch.await();
                } catch (InterruptedException e) {}
            }
        });
        final String result = "result";
        final AtomicReference<Future<String>> futureRef = new AtomicReference<Future<String>>();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {}
                try {
                    futureRef.set(helper.runJavaSourceTaskWhenScanFinished(new Callable<String>() {
                        public String call() throws Exception {
                            return result;
                        }
                    }));
                } catch (IOException e) {
                    throw new Error(e);
                }
                assertFalse(futureRef.get().isDone());
                assertFalse(futureRef.get().isCancelled());
                scanBlockingLatch.countDown();
            }
        });
        t.start();
        RepositoryUpdater.getDefault().refreshAll(true, true, false, null);
//        IndexingManager.getDefault().refreshIndexAndWait(srcFO.getURL(), null);
        TestUtilities.copyStringToFileObject(srcFO, "Person.java",
                "public interface Person {" +
                "   String getName();" +
                "}");
        assertTrue("operation timed out", scanBlockingLatch.await(25, TimeUnit.SECONDS));
        assertSame(result, futureRef.get().get());
        t.join();
    }
}
