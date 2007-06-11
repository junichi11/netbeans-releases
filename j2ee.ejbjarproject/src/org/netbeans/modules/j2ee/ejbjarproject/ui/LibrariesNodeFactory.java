/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbjarproject.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.ejbjarproject.EjbJarProject;
import org.netbeans.modules.j2ee.ejbjarproject.SourceRoots;
import org.netbeans.modules.j2ee.ejbjarproject.UpdateHelper;
import org.netbeans.modules.j2ee.ejbjarproject.classpath.ClassPathSupport;
import org.netbeans.modules.j2ee.ejbjarproject.ui.SourceNodeFactory.PreselectPropertiesAction;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.CustomizerLibraries;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.EjbJarProjectProperties;
import org.netbeans.modules.j2ee.ejbjarproject.ui.logicalview.libraries.LibrariesNode;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * NodeFactory to create source and test library nodes.
 * 
 * @author gpatil
 */
public final class LibrariesNodeFactory implements NodeFactory {
    
    /** Creates a new instance of LibrariesNodeFactory */
    public LibrariesNodeFactory() {
    }

    public NodeList createNodes(Project p) {
        EjbJarProject project = p.getLookup().lookup(EjbJarProject.class);
        assert project != null;
        return new LibrariesNodeList(project);
    }

    private static class LibrariesNodeList implements NodeList<String>, PropertyChangeListener {
        private static final String LIBRARIES = "Libs"; //NOI18N
        private static final String TEST_LIBRARIES = "TestLibs"; //NOI18N

        private final SourceRoots testSources;
        private final EjbJarProject project;
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        private final PropertyEvaluator evaluator;
        private final UpdateHelper updateHelper;
        private final ReferenceHelper refHelper;
        
        LibrariesNodeList(EjbJarProject proj) {
            project = proj;
            testSources = project.getTestSourceRoots();
            evaluator = project.evaluator();
            updateHelper = project.getUpdateHelper();
            refHelper = project.getReferenceHelper();
        }
        
        public List<String> keys() {
            List<String> result = new ArrayList<String>();
            result.add(LIBRARIES);
            URL[] testRoots = testSources.getRootURLs();
            boolean addTestSources = false;
            for (int i = 0; i < testRoots.length; i++) {
                File f = new File(URI.create(testRoots[i].toExternalForm()));
                if (f.exists()) {
                    addTestSources = true;
                    break;
                }
            }
            if (addTestSources) {
                result.add(TEST_LIBRARIES);
            }
            return result;
        }

        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        public Node node(String key) {
            if (key == LIBRARIES) {
                //Libraries Node
                return  new LibrariesNode(
                    NbBundle.getMessage(LibrariesNodeFactory.class,"CTL_LibrariesNode"), // NOI18N
                    project,
                    evaluator,
                    updateHelper,
                    refHelper,
                    EjbJarProjectProperties.JAVAC_CLASSPATH,
                    new String[] { EjbJarProjectProperties.BUILD_CLASSES_DIR },
                    "platform.active", //NOI18N
                    EjbJarProjectProperties.J2EE_SERVER_INSTANCE,
                    new Action[] {
                        LibrariesNode.createAddProjectAction(project, EjbJarProjectProperties.JAVAC_CLASSPATH,
                                                             ClassPathSupport.ELEMENT_INCLUDED_LIBRARIES),
                        LibrariesNode.createAddLibraryAction(project, updateHelper.getAntProjectHelper(),
                                                             EjbJarProjectProperties.JAVAC_CLASSPATH, ClassPathSupport.ELEMENT_INCLUDED_LIBRARIES),
                        LibrariesNode.createAddFolderAction(project, EjbJarProjectProperties.JAVAC_CLASSPATH,
                                                            ClassPathSupport.ELEMENT_INCLUDED_LIBRARIES),
                        null,
                        new PreselectPropertiesAction(project, "Libraries", CustomizerLibraries.COMPILE), //NOI18N
                    },
                    ClassPathSupport.ELEMENT_INCLUDED_LIBRARIES
                );                        
            } else if (key == TEST_LIBRARIES) {
                return  new LibrariesNode(
                    NbBundle.getMessage(LibrariesNodeFactory.class,"CTL_TestLibrariesNode"), // NOI18N
                    project,
                    evaluator,
                    updateHelper,
                    refHelper,
                    EjbJarProjectProperties.JAVAC_TEST_CLASSPATH,
                    new String[] {
                        EjbJarProjectProperties.BUILD_TEST_CLASSES_DIR,
                        EjbJarProjectProperties.JAVAC_CLASSPATH,
                        EjbJarProjectProperties.BUILD_CLASSES_DIR,
                    },
                    null,
                    null,
                    new Action[] {
                        LibrariesNode.createAddProjectAction(project, EjbJarProjectProperties.JAVAC_TEST_CLASSPATH, null),
                        LibrariesNode.createAddLibraryAction(project, updateHelper.getAntProjectHelper(), EjbJarProjectProperties.JAVAC_TEST_CLASSPATH, null),
                        LibrariesNode.createAddFolderAction(project, EjbJarProjectProperties.JAVAC_TEST_CLASSPATH, null),
                        null,
                        new PreselectPropertiesAction(project, "Libraries", CustomizerLibraries.COMPILE_TESTS), //NOI18N
                    },
                    null
                    );
            }
            assert false: "No node for key: " + key; // NOI18N
            return null;
            
        }

        public void addNotify() {
            testSources.addPropertyChangeListener(this);
        }

        public void removeNotify() {
            testSources.removePropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            // The caller holds ProjectManager.mutex() read lock
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    changeSupport.fireChange();
                }
            });
        }
        
    }
    
}
