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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.test.cvsmodule;

/**
 *
 * @author novakm
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.modules.javacvs.BranchOperator;
import org.netbeans.jellytools.modules.javacvs.CVSRootStepOperator;
import org.netbeans.jellytools.modules.javacvs.CheckoutWizardOperator;
import org.netbeans.jellytools.modules.javacvs.ModuleToCheckoutStepOperator;
import org.netbeans.jellytools.modules.javacvs.SwitchToBranchOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.Operator.DefaultStringComparator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.ide.ProjectSupport;


/**
 *
 * @author novakm
 */
public class BranchCreationAndSwitchTest extends JellyTestCase {
    
    /** Creates a new instance of BranchCreationAndSwitchningTest */
    static String os_name;
    static String sessionCVSroot;
    final String projectName = "ForImport";
    final String pathToMain = "forimport|Main.java";
    final String PROTOCOL_FOLDER = "protocol";
    static File cacheFolder;
    Operator.DefaultStringComparator comOperator;
    Operator.DefaultStringComparator oldOperator;
    static Logger log;
    
    /** Creates a new instance of testIgnoreUnignoreFile */
    public BranchCreationAndSwitchTest(String name) {
        super(name);
        if (os_name == null) {
            os_name = System.getProperty("os.name");
        }
        try {
            TestKit.extractProtocol(getDataDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    @Override
    protected void setUp() throws Exception {
        System.out.println("### " + getName() + " ###");
        if (log == null) {
            log = Logger.getLogger("org.netbeans.modules.versioning.system.cvss.t9y");
            log.setLevel(Level.ALL);
            TestKit.removeHandlers(log);
        } else {
            TestKit.removeHandlers(log);
        }
    }
    
    public static Test suite() {
        return NbModuleSuite.create(
                 NbModuleSuite.createConfiguration(BranchCreationAndSwitchTest.class).addTest(
                    "testCheckOutProject", "testCreateBranchForProject", "testSwitchProjectToBranch", "removeAllData"
                 )
                .enableModules(".*")
                .clusters(".*")
        );
    }

    public void testCheckOutProject() throws Exception {
        //JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 18000);
        //JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 18000);
        
        MessageHandler mh = new MessageHandler("Checking out");
        log.addHandler(mh);

        TestKit.closeProject(projectName);
        TestKit.showStatusLabels();
        if ((os_name !=null) && (os_name.indexOf("Mac") > -1))
            NewProjectWizardOperator.invoke().close();

        comOperator = new Operator.DefaultStringComparator(true, true);
        oldOperator = (DefaultStringComparator) Operator.getDefaultStringComparator();
        Operator.setDefaultStringComparator(comOperator);
        CheckoutWizardOperator cwo = CheckoutWizardOperator.invoke();
        Operator.setDefaultStringComparator(oldOperator);
        CVSRootStepOperator crso = new CVSRootStepOperator();
        crso.setCVSRoot(":pserver:anoncvs@localhost:/cvs");
        
        //prepare stream for successful authentification and run PseudoCVSServer
        InputStream in = TestKit.getStream(getDataDir().getCanonicalFile().toString() + File.separator + PROTOCOL_FOLDER, "authorized.in");
        PseudoCvsServer cvss = new PseudoCvsServer(in);
        new Thread(cvss).start();
        cvss.ignoreProbe();
        String CVSroot = cvss.getCvsRoot();
        sessionCVSroot = CVSroot;
        crso.setCVSRoot(CVSroot);
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", CVSroot);
        crso.next();
        
        //2nd step of CheckOutWizard
        File tmp = new File("/tmp"); // NOI18N
        File work = new File(tmp, "" + File.separator + System.currentTimeMillis());
        cacheFolder = new File(work, projectName + File.separator + "src" + File.separator + "forimport" + File.separator + "CVS" + File.separator + "RevisionCache");
        tmp.mkdirs();
        work.mkdirs();
        tmp.deleteOnExit();
        ModuleToCheckoutStepOperator moduleCheck = new ModuleToCheckoutStepOperator();
        cvss.stop();
        try {
            new EventTool().waitNoEvent(1000);
            in.close();
        } catch (IOException e) {
            //
        }
        moduleCheck.setModule("ForImport");
        moduleCheck.setLocalFolder(work.getAbsolutePath()); // NOI18N
        //Pseudo CVS server for finishing check out wizard
        in = TestKit.getStream(getDataDir().getCanonicalFile().toString() + File.separator + PROTOCOL_FOLDER, "checkout_finish_2.in");
        cvss = new PseudoCvsServer(in);
        new Thread(cvss).start();
        CVSroot = cvss.getCvsRoot();
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", CVSroot);
        cwo.finish();
        TestKit.waitText(mh);
        cvss.stop();
        try {
            new EventTool().waitNoEvent(1000);
            in.close();
        } catch (IOException e) {
            //
        }
        NbDialogOperator nbdialog = new NbDialogOperator("Checkout Completed");
        JButtonOperator open = new JButtonOperator(nbdialog, "Open Project");
        open.push();
        
        ProjectSupport.waitScanFinished();
//        TestKit.waitForQueueEmpty();
//        ProjectSupport.waitScanFinished();
        
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", "");
    }
    
    public void testCreateBranchForProject() throws Exception {
        //JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 18000);
        PseudoCvsServer cvss;
        InputStream in;

        MessageHandler mh = new MessageHandler("Branching");
        log.addHandler(mh);

        String CVSroot;
        Node rootNode = new ProjectsTabOperator().getProjectRootNode(projectName);
        in = TestKit.getStream(getDataDir().getCanonicalFile().toString() + File.separator + PROTOCOL_FOLDER, "cvs_60_create_branch.in");
        cvss = new PseudoCvsServer(in);
        new Thread(cvss).start();
        CVSroot = cvss.getCvsRoot();
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", CVSroot);
        BranchOperator bo = BranchOperator.invoke(rootNode);
        bo.setBranchName("MyNewBranch");
        bo.checkSwitchToThisBranchAftewards(false);
        bo.checkTagBeforeBranching(false);
        
        bo.branch();
        new EventTool().waitNoEvent(1000);
        TestKit.waitText(mh);
        cvss.stop();
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", "");
    }
    public void testSwitchProjectToBranch() throws Exception {
        //JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 18000);
        PseudoCvsServer cvss;
        InputStream in;

        MessageHandler mh = new MessageHandler("Switching");
        log.addHandler(mh);

        org.openide.nodes.Node nodeIDE;
        String cvsRoot;
        
        Node rootNode = new ProjectsTabOperator().getProjectRootNode(projectName);
        in = TestKit.getStream(getDataDir().getCanonicalFile().toString() + File.separator + PROTOCOL_FOLDER, "cvs_60_switch_branch.in");
        cvss = new PseudoCvsServer(in);
        new Thread(cvss).start();
        cvsRoot = cvss.getCvsRoot();
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", cvsRoot);
        SwitchToBranchOperator sbo = SwitchToBranchOperator.invoke(rootNode);
        sbo.switchToBranch();
        sbo.setBranch("MyNewBranch");
        sbo.pushSwitch();
        new EventTool().waitNoEvent(1000);
        TestKit.waitText(mh);
        cvss.stop();
        //        test branch annotation
        
        Node nodeFile = new Node(new SourcePackagesNode(projectName), "ForImport" + "|Main.java");
        nodeIDE = (org.openide.nodes.Node) nodeFile.getOpenideNode();
        String status = TestKit.getStatus(nodeIDE.getHtmlDisplayName());
        boolean comparison = status.equals("[; MyNewBranch]");
        assertEquals("Wrong annotation of node!!!", true, comparison);
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", "");
    }
    
    
    public void removeAllData() throws Exception {
        TestKit.closeProject(projectName);
        System.setProperty("netbeans.t9y.cvs.connection.CVSROOT", "");
    }
}
