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
package org.netbeans.qa.form;

import java.io.File;
import java.io.IOException;
import junit.framework.Test;
import org.netbeans.jellytools.DocumentsDialogOperator;
import org.netbeans.jellytools.NewFileWizardOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.modules.form.ComponentInspectorOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewJavaProjectNameLocationStepOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.form.FormDesignerOperator;
import org.netbeans.jellytools.nodes.ProjectRootNode;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.junit.NbModuleSuite;

/**
 * A Test based on JellyTestCase. JellyTestCase redirects Jemmy output
 * to a log file provided by NbTestCase. It can be inspected in results.
 * It also sets timeouts necessary for NetBeans GUI testing.
 *
 * Any JemmyException (which is normally thrown as a result of an unsuccessful
 * operation in Jemmy) going from a test is treated by JellyTestCase as a test
 * failure; any other exception - as a test error.
 *
 * Additionally it:
 *    - closes all modal dialogs at the end of the test case (property jemmy.close.modal - default true)
 *    - generates component dump (XML file containing components information) in case of test failure (property jemmy.screen.xmldump - default false)
 *    - captures screen into a PNG file in case of test failure (property jemmy.screen.capture - default true)
 *    - waits at least 1000 ms between test cases (property jelly.wait.no.event - default true)
 *
 * @author Jana Maleckova
 * Created on 29 January 2007, 15:59
 * Test is only for java 1.6 for now
 * 
 * <b>Adam Senk</b>
 * 20 April 2011 WORKS
 */
public class OpenTempl_defaultPackTest extends ExtJellyTestCase {

    public String DATA_PROJECT_NAME = "SampleProject";
    public String PACKAGE_NAME = "Source Package";
    public String PROJECT_NAME = "Java";
    public String workdirpath;
    public String jdkVersion = ExtJellyTestCase.getJDKVersionCode();
    MainWindowOperator mainWindow;
    ProjectsTabOperator pto;
    ComponentInspectorOperator cio;

    /** Constructor required by JUnit */
    public OpenTempl_defaultPackTest(String name) {
        super(name);

    }

    /** Creates suite from particular test cases. You can define order of testcases here. */
    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(OpenTempl_defaultPackTest.class).addTest(
                //SWING
                "testApplet",
                "testDialog",
                "testFrame", 
                "testInter", 
                "testMidi", 
                "testPanel",
                "testBean", 
                "testAppl",
                "testOkCancel",
                //AWT
                "testAWTApplet",
                "testAWTDialog",
                "testAWTFrame",
                "testAWTPanel").gui(true).enableModules(".*").clusters(".*"));

    }

    /** Called before every test case. */
    @Override
    public void setUp() throws IOException {
        openDataProjects(DATA_PROJECT_NAME);
        workdirpath = getWorkDir().getParentFile().getAbsolutePath();
        System.out.println("########  " + getName() + "  #######");
    }

    /** Called after every test case. */
    @Override
    public void tearDown() {
    }

    // Add test methods here, they have to start with 'test' name.
    //method create new project in parent dir to workdir
    public void begin() throws InterruptedException {
        DeleteDir.delDir(workdirpath + System.getProperty("file.separator") + DATA_PROJECT_NAME);
        Thread.sleep(10000);
        mainWindow = MainWindowOperator.getDefault();
        NewProjectWizardOperator npwo = NewProjectWizardOperator.invoke();
        npwo.selectCategory(PROJECT_NAME);
        npwo.selectProject("Java Application");
        npwo.next();

        NewJavaProjectNameLocationStepOperator tfo_name = new NewJavaProjectNameLocationStepOperator();
        tfo_name.txtProjectName().setText(DATA_PROJECT_NAME);

        NewJavaProjectNameLocationStepOperator tfo1_location = new NewJavaProjectNameLocationStepOperator();
        tfo_name.txtLocation().setText(workdirpath);
        JButtonOperator bo = new JButtonOperator(npwo, "Finish");
        //bo.getSource().requestFocus();
        bo.push();

        log("Project " + DATA_PROJECT_NAME + " was created");
        Thread.sleep(10000);

    }

    public void deleteProject() throws InterruptedException {
        //Project Deleting
        pto = new ProjectsTabOperator();
        ProjectRootNode prn = pto.getProjectRootNode(DATA_PROJECT_NAME);
        prn.select();

        DeleteAction delProject = new DeleteAction();
        delProject.perform();

        NbDialogOperator ndo = new NbDialogOperator("Delete Project");
        JCheckBoxOperator cbo = new JCheckBoxOperator(ndo);
        cbo.changeSelection(true);
        ndo.yes();

        Thread.sleep(10000);
        //check if project was really deleted from disc
        File f = new File(workdirpath + System.getProperty("file.separator") + DATA_PROJECT_NAME);
        System.out.println("adresar:" + f);
        if (f.exists()) {
            log("File " + DATA_PROJECT_NAME + " was not deleted correctly");
            System.exit(1);
        } else {
            log("File " + DATA_PROJECT_NAME + " was deleted correctly");
        }
    }
    /*
     * Close document given in parametr.
     *Is HIGHLY RECOMMENDED close document, after test is finished.
     */

    public void closeDocument(String documentName) throws InterruptedException {
        FormDesignerOperator fdo = new FormDesignerOperator(documentName);
        fdo.editor();
        Thread.sleep(500);
        DocumentsDialogOperator ddo = DocumentsDialogOperator.invoke();
        Thread.sleep(500);
        ddo.selectDocument(documentName);
        Thread.sleep(500);
        ddo.btCloseDocuments().doClick();


    }

    public void openTemplate(String templateName, String category) throws InterruptedException {

        NewFileWizardOperator nfwo = NewFileWizardOperator.invoke();
        nfwo.selectProject(DATA_PROJECT_NAME);
        Thread.sleep(10000);
        nfwo.selectCategory(category);
        nfwo.selectFileType(templateName);
        nfwo.next();
        JComboBoxOperator jcb_package = new JComboBoxOperator(nfwo, 1);
        jcb_package.clearText();
        Thread.sleep(5000);

        if (templateName.equals("Bean Form")) {
            nfwo.next();
            JTextFieldOperator class_name = new JTextFieldOperator(nfwo);
            class_name.setText("javax.swing.JButton");
            nfwo.finish();
            log(templateName + " is created correctly");
        } else {
            nfwo.finish();
            log(templateName + " is created correctly");
            Thread.sleep(3000);
        }
    }

    public void testTemplateMethod(String templateName, String category, String name) throws InterruptedException, IOException {

        openTemplate(templateName, category);

        System.out.println(getWorkDir());
        testFormFile(name);
        Thread.sleep(1000);
        testJavaFile(name);
        Thread.sleep(1000);
        closeDocument(name + ".java");

    }

    /** Test case 1.
     *Create new JApplet template in default package
     */
    public void testApplet() throws InterruptedException, IOException, Exception {


//        begin();
        testTemplateMethod("JApplet Form", "Swing GUI Forms", "NewJApplet");

    }

    /** Test case 2.
     * Create new JDialog template in default package
     */
    public void testDialog() throws InterruptedException, IOException {

        testTemplateMethod("JDialog Form", "Swing GUI Forms", "NewJDialog");


    }

    /** Test case 3.
     * Create new JFrame template in default package
     */
    public void testFrame() throws InterruptedException, IOException {

        testTemplateMethod("JFrame Form", "Swing GUI Forms", "NewJFrame");

    }

    /** Test case 4.
     * Create new JInternalFrame template in default package
     */
    public void testInter() throws InterruptedException, IOException {

        testTemplateMethod("JInternalFrame Form", "Swing GUI Forms", "NewJInternalFrame");

    }

    public void testAppl() throws InterruptedException, IOException, Exception {

        testTemplateMethod("Application Sample Form", "Swing GUI Forms", "NewApplication");
    }

    public void testMidi() throws InterruptedException, IOException, Exception {

        testTemplateMethod("MDI Application Sample Form", "Swing GUI Forms", "NewMDIApplication");
    }

    /** Test case 5.
     * Create new JPanel template in default package
     */
    public void testPanel() throws InterruptedException, IOException {

        testTemplateMethod("JPanel Form", "Swing GUI Forms", "NewJPanel");

    }

    /** Test case 6. oa
     * Create new Bean template in default package
     */
    public void testBean() throws InterruptedException, IOException {

        testTemplateMethod("Bean Form", "Swing GUI Forms", "NewBeanForm");

    }

    public void testOkCancel() throws InterruptedException, IOException {

        testTemplateMethod("OK / Cancel Dialog Sample Form", "Swing GUI Forms", "NewOkCancelDialog");

    }
    
    /**AWT Test case 1.
     * Create new Panel template in default package
     */
    public void testAWTPanel() throws InterruptedException, IOException {

        testTemplateMethod("Panel Form", "AWT GUI Forms", "NewPanel");

    }
    
    /**AWT Test case 2.
     * Create new Dialog template in default package
     */
    
    public void testAWTApplet() throws InterruptedException, IOException, Exception {

        testTemplateMethod("Applet Form", "AWT GUI Forms", "NewApplet");

    }

    /**AWT Test case 3.
     * Create new Dialog template in default package
     */
    public void testAWTDialog() throws InterruptedException, IOException {

        testTemplateMethod("Dialog Form", "AWT GUI Forms", "NewDialog");


    }

    /**AWT Test case 4.
     * Create new Frame template in default package
     */
    public void testAWTFrame() throws InterruptedException, IOException {

        testTemplateMethod("Frame Form", "AWT GUI Forms", "NewFrame");

    }

    public void testFormFile(String formfile) throws IOException {
        try {
            String refFile = VisualDevelopmentUtil.readFromFile(getDataDir().getAbsolutePath()
                    + File.separatorChar + DATA_PROJECT_NAME + File.separatorChar + "src" + File.separatorChar + formfile + ".form");

            getLog(formfile + "Form.ref").print(refFile);


        } catch (Exception e) {
            fail("Fail during create reffile: " + e.getMessage());
        }

        assertFile(new File(getWorkDir() + File.separator + formfile + "Form.ref"), getGoldenFile(formfile + "FormFile.pass"), new File(getWorkDir(), formfile + ".diff"));

    }

    public void testJavaFile(String javafile) throws IOException {

        try {
            String refFile = VisualDevelopmentUtil.readFromFile(getDataDir().getAbsolutePath()
                    + File.separatorChar + DATA_PROJECT_NAME + File.separatorChar + "src" + File.separatorChar + javafile + ".java");

            getRef().print(createRefFile(refFile));

            // golden files are in ${xtest.data}/goldenfiles/${classname}/...

            log("Java reference file was created");

        } catch (Exception e) {
            fail("Fail during create reffile: " + e.getMessage());
        }


        assertFile(new File(getWorkDir() + File.separator + this.getName() + ".ref"), getGoldenFile(javafile + "JavaFile" + jdkVersion.replaceAll("jdk", "") + ".pass"), new File(getWorkDir(), javafile + "java.diff"));


    }

    public String createRefFile(String test) {
        int start = test.indexOf("/*");
        int end = test.indexOf("*/");
        test = test.substring(0, start) + test.substring(end + 2);

        start = test.indexOf("/*");
        end = test.indexOf("*/");
        test = test.substring(0, start) + test.substring(end + 2);

        start = test.indexOf("/**");
        end = test.indexOf("*/");
        test = test.substring(0, start) + test.substring(end + 2);
        return test;
    }
}