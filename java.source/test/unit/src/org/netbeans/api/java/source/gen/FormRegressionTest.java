/**
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
package org.netbeans.api.java.source.gen;

import java.io.File;
import java.io.IOException;
import com.sun.source.tree.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.java.source.*;
import org.netbeans.junit.NbTestSuite;
import static org.netbeans.api.java.source.JavaSource.*;

/**
 *
 * @author Pavel Flaska, Jan Becicka
 */
public class FormRegressionTest extends GeneratorTestMDRCompat {

    public FormRegressionTest(String aName) {
        super(aName);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTestSuite(FormRegressionTest.class);
//        suite.addTest(new FormRegressionTest("testImportFQNs"));
        return suite;
    }

    public void testImportFQNs() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        String originalCode =
            "package issue_212248;\n" +
            "\n" +
            "public class NB72_Error2 extends javax.swing.JDialog {\n" +
            "    public NB72_Error2(java.awt.Frame parent, boolean modal) {\n" +
            "        super(parent, modal);\n" +
            "        initComponents();\n" +
            "    }\n" +
            "\n" +
            "    // <editor-fold defaultstate=\"collapsed\" desc=\"Generated Code\">\n" +
            "    private void initComponents() {\n" +
            "\n" +
            "        jLabel1 = new javax.swing.JLabel();\n" +
            "        jLabel2 = new javax.swing.JLabel();\n" +
            "        jLabel3 = new javax.swing.JLabel();\n" +
            "        jLabel4 = new javax.swing.JLabel();\n" +
            "        jTextField1 = new javax.swing.JTextField();\n" +
            "        jTextField2 = new javax.swing.JTextField();\n" +
            "        jTextField3 = new javax.swing.JTextField();\n" +
            "        jTextField4 = new javax.swing.JTextField();\n" +
            "\n" +
            "        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);\n" +
            "\n" +
            "        jLabel1.setText(\"jLabel1\");\n" +
            "        jLabel1.setName(\"jLabel1\"); // NOI18N\n" +
            "\n" +
            "        jLabel2.setText(\"jLabel2\");\n" +
            "        jLabel2.setName(\"jLabel2\"); // NOI18N\n" +
            "\n" +
            "        jLabel3.setText(\"jLabel3\");\n" +
            "        jLabel3.setName(\"jLabel3\"); // NOI18N\n" +
            "\n" +
            "        jLabel4.setText(\"jLabel4\");\n" +
            "        jLabel4.setName(\"jLabel4\"); // NOI18N\n" +
            "\n" +
            "        jTextField1.setText(\"jTextField1\");\n" +
            "        jTextField1.setName(\"jTextField1\"); // NOI18N\n" +
            "\n" +
            "        jTextField2.setText(\"jTextField2\");\n" +
            "        jTextField2.setName(\"jTextField2\"); // NOI18N\n" +
            "\n" +
            "        jTextField3.setText(\"jTextField3\");\n" +
            "        jTextField3.setName(\"jTextField3\"); // NOI18N\n" +
            "\n" +
            "        jTextField4.setText(\"jTextField4\");\n" +
            "        jTextField4.setName(\"jTextField4\"); // NOI18N\n" +
            "\n" +
            "        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());\n" +
            "        getContentPane().setLayout(layout);\n" +
            "        layout.setHorizontalGroup(\n" +
            "            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)\n" +
            "            .addGroup(layout.createSequentialGroup()\n" +
            "                .addContainerGap()\n" +
            "                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)\n" +
            "                    .addGroup(layout.createSequentialGroup()\n" +
            "                        .addComponent(jLabel1)\n" +
            "                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                    .addGroup(layout.createSequentialGroup()\n" +
            "                        .addComponent(jLabel2)\n" +
            "                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                    .addGroup(layout.createSequentialGroup()\n" +
            "                        .addComponent(jLabel3)\n" +
            "                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                    .addGroup(layout.createSequentialGroup()\n" +
            "                        .addComponent(jLabel4)\n" +
            "                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))\n" +
            "                .addContainerGap(16, Short.MAX_VALUE))\n" +
            "        );\n" +
            "        layout.setVerticalGroup(\n" +
            "            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)\n" +
            "            .addGroup(layout.createSequentialGroup()\n" +
            "                .addContainerGap()\n" +
            "                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)\n" +
            "                    .addComponent(jLabel1)\n" +
            "                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)\n" +
            "                    .addComponent(jLabel2)\n" +
            "                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)\n" +
            "                    .addComponent(jLabel3)\n" +
            "                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)\n" +
            "                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)\n" +
            "                    .addComponent(jLabel4)\n" +
            "                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))\n" +
            "                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))\n" +
            "        );\n" +
            "\n" +
            "        pack();\n" +
            "    }// </editor-fold>\n" +
            "    // Variables declaration - do not modify\n" +
            "    private javax.swing.JLabel jLabel1;\n" +
            "    private javax.swing.JLabel jLabel2;\n" +
            "    private javax.swing.JLabel jLabel3;\n" +
            "    private javax.swing.JLabel jLabel4;\n" +
            "    private javax.swing.JTextField jTextField1;\n" +
            "    private javax.swing.JTextField jTextField2;\n" +
            "    private javax.swing.JTextField jTextField3;\n" +
            "    private javax.swing.JTextField jTextField4;\n" +
            "    // End of variables declaration\n" +
            "}\n";
        TestUtilities.copyStringToFile(testFile, originalCode);
        String imports =
                "\n\nimport java.awt.Frame;\n" +
                "import javax.swing.GroupLayout;\n" +
                "import javax.swing.GroupLayout.Alignment;\n" +
                "import javax.swing.JDialog;\n" +
                "import javax.swing.JLabel;\n" +
                "import javax.swing.JTextField;\n" +
                "import javax.swing.LayoutStyle.ComponentPlacement;\n" +
                "import javax.swing.WindowConstants;\n\n";
        
        String golden = originalCode
                .replace("java.awt.Frame", "Frame")
                .replace("javax.swing.GroupLayout.Alignment", "Alignment")
                .replace("javax.swing.GroupLayout", "GroupLayout")
                .replace("javax.swing.JDialog", "JDialog")
                .replace("javax.swing.JLabel", "JLabel")
                .replace("javax.swing.JTextField", "JTextField")
                .replace("javax.swing.LayoutStyle.ComponentPlacement", "ComponentPlacement")
                .replace("javax.swing.WindowConstants", "WindowConstants")
                .replaceFirst(Pattern.quote("\n\n"), Matcher.quoteReplacement(imports));

        JavaSource src = getJavaSource(testFile);
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                CompilationUnitTree cut = workingCopy.getCompilationUnit();
                GeneratorUtilities gu = GeneratorUtilities.get(workingCopy);
                
                workingCopy.rewrite(cut, gu.importFQNs(cut));
            }
            
        };
        src.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    String getGoldenPckg() {
        return "";
    }

    String getSourcePckg() {
        return "";
    }
}
