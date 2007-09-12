/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.compapp.test.ui.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.compapp.projects.jbi.JbiProject;
import org.netbeans.modules.compapp.test.ui.TestcaseNode;
import org.netbeans.modules.compapp.test.ui.wizards.NewTestcaseConstants;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.*;
import org.openide.execution.ExecutorTask;
import org.openide.nodes.Node;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.actions.NodeAction;

/**
 * An abstract action for test case execution.
 *
 * @author jqian
 */
public abstract class AbstractTestCaseExecutionAction extends NodeAction 
        implements NewTestcaseConstants {
    
    protected boolean enable(Node[] activatedNodes) {
         for (Node activatedNode : activatedNodes) {
            TestcaseCookie cookie = activatedNode.getCookie(TestcaseCookie.class);
            if (cookie != null) {
                TestcaseNode node = cookie.getTestcaseNode();
                if (node.isTestCaseRunning()) {
                    return false;
                }
            }
         }
        return true;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(final Node[] activatedNodes) {
        TestcaseCookie tc = activatedNodes[0].getCookie(TestcaseCookie.class);
        if (tc == null) {
            return;
        }
        
        /*tc.getTestcaseNode().showDiffTopComponentVisible();*/        
        JbiProject project = tc.getTestcaseNode().getProject();
        FileObject projectDir = project.getProjectDirectory();
        FileObject buildXMLFile = projectDir.getFileObject(project.getBuildXmlName());
        
        FileObject testDir = project.getTestDirectory();
        
        String selectedTestCasesCSV = ""; // NOI18N
        for (Node activatedNode : activatedNodes) {
            TestcaseCookie cookie = activatedNode.getCookie(TestcaseCookie.class);
            TestcaseNode node = cookie.getTestcaseNode();
            String testFolderName = node.getTestCaseDir().getName();
            selectedTestCasesCSV +=  testFolderName + ","; // NOI18N
        }
        selectedTestCasesCSV = selectedTestCasesCSV.substring(0, selectedTestCasesCSV.length() - 1);
        
        try {
            String testDirPath = FileUtil.toFile(testDir).getAbsolutePath();            
            String fileName = testDirPath + "/selected-tests.properties"; // NOI18N
            
            // EditableProperties takes care of encoding.
            EditableProperties props = new EditableProperties();
            props.setProperty("testcases", selectedTestCasesCSV);    // NOI18N
            FileOutputStream outStream = new FileOutputStream(fileName);
            props.store(outStream);
            outStream.close();
            
            for (Node activatedNode : activatedNodes) {
                TestcaseCookie cookie = activatedNode.getCookie(TestcaseCookie.class);
                TestcaseNode node = cookie.getTestcaseNode();
                node.setTestCaseRunning(true);
            }
            
            String[] targetNames = { getAntTargetName() };
            ExecutorTask task = ActionUtils.runTarget(buildXMLFile, targetNames, null);
            
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {                            
                            for (Node activatedNode : activatedNodes) {
                                TestcaseCookie cookie = 
                                        activatedNode.getCookie(TestcaseCookie.class);
                                TestcaseNode node = cookie.getTestcaseNode();
                                node.setTestCaseRunning(false);
                                if (node.isDiffTopComponentVisible()) {
                                    node.refreshDiffTopComponent();
                                }
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
          
    /**
     * Gets the ant script target name.
     * 
     * @return ant target name
     */
    protected abstract String getAntTargetName();
}
