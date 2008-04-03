/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.nav;

import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.DeclarationFinder.DeclarationLocation;

/**
 *
 * @author Jan Lahoda
 */
public class DeclarationFinderImplTest extends TestBase {
    
    public DeclarationFinderImplTest(String testName) {
        super(testName);
    }            

    public void testSimpleFindDeclaration1() throws Exception {
        performTestSimpleFindDeclaration("<?php\n^$name = \"test\";\n echo \"$na|me\";\n?>");
    }
    
    public void testSimpleFindDeclaration2() throws Exception {
        performTestSimpleFindDeclaration("<?php\n$name = \"test\";\n^$name = \"test\";\n echo \"$na|me\";\n?>");
    }
    
    public void testSimpleFindDeclaration3() throws Exception {
        performTestSimpleFindDeclaration("<?php\n$name = \"test\";\n^$name = \"test\";\n echo \"$na|me\";\n$name = \"test\";\n?>");
    }
    
    public void testSimpleFindDeclaration4() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "$name = \"test\";\n" +
                                         "function foo(^$name) {\n" +
                                         "    echo \"$na|me\";\n" +
                                         "}\n" + 
                                         "?>");
    }
    
    public void testSimpleFindDeclaration5() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "^$name = \"test\";\n" +
                                         "function foo($name) {\n" +
                                         "}\n" + 
                                         "echo \"$na|me\";\n" +
                                         "?>");
    }
    
    public void testSimpleFindDeclaration6() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "$name = \"test\";\n" +
                                         "^function foo($name) {\n" +
                                         "}\n" + 
                                         "fo|o($name);\n" +
                                         "?>");
    }
    
    public void testSimpleFindDeclaration7() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "^class name {\n" +
                                         "}\n" + 
                                         "$r = new na|me();\n" +
                                         "?>");
    }
    
    public void testSimpleFindDeclaration8() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "class name {\n" +
                                         "    ^function test() {" +
                                         "    }" +
                                         "}\n" + 
                                         "$r = new name();\n" +
                                         "$r->te|st();" +
                                         "?>");
    }
    
    public void testSimpleFindDeclaration9() throws Exception {
        performTestSimpleFindDeclaration("<?php\n" +
                                         "^$name = \"test\";\n" +
                                         "function foo($name) {\n" +
                                         "}\n" + 
                                         "foo($na|me);\n" +
                                         "?>");
    }
    
    public void DISABLEtestFindDeclarationInOtherFile1() throws Exception {
        performTestSimpleFindDeclaration(1,
                                         "<?php\n" +
                                         "include \"testa.php\";\n" +
                                         "fo|o();\n" +
                                         "?>",
                                         "<?php\n" +
                                         "^function foo() {}\n" +
                                         "?>");
    }
    
    public void DISABLEtestFindDeclarationInOtherFile2() throws Exception {
        performTestSimpleFindDeclaration(2,
                                         "<?php\n" +
                                         "include \"testa.php\";\n" +
                                         "fo|o();\n" +
                                         "?>",
                                         "<?php\n" +
                                         "include \"testb.php\";\n" +
                                         "?>",
                                         "<?php\n" +
                                         "^function foo() {}\n" +
                                         "?>");
    }
    
    public void testFunctionsInGlobalScope1() throws Exception {
        performTestSimpleFindDeclaration(0,
                                         "<?php\n" +
                                         "^function foo() {}\n" +
                                         "function bar() {\n" +
                                         "    fo|o();\n" +
                                         "}\n" +
                                         "?>");
    }
    
    public void testClassInGlobalScope1() throws Exception {
        performTestSimpleFindDeclaration(0,
                                         "<?php\n" +
                                         "function foo() {" +
                                         "    ^class bar {}\n" +
                                         "}\n" +
                                         "$r = new b|ar();\n" +
                                         "?>");
    }
    
    private void performTestSimpleFindDeclaration(int declarationFile, String... code) throws Exception {
        assertTrue(code.length > 0);
        
        int declOffset = -1;
        
        for (int cntr = 0; cntr < code.length; cntr++) {
            int i = code[cntr].replaceAll("\\|", "").indexOf('^');
            
            if (i != (-1)) {
                declOffset = i;
                code[cntr] = code[cntr].replaceAll("\\^", "");
                break;
            }
        }
        
        int caretOffset = code[0].replaceAll("\\^", "").indexOf('|');
        
        code[0] = code[0].replaceAll("\\|", "");
        
        assertTrue(caretOffset != (-1));
        assertTrue(declOffset != (-1));
        
        performTestSimpleFindDeclaration(code, caretOffset, declarationFile, declOffset);
    }
    
    private void performTestSimpleFindDeclaration(String code) throws Exception {
        int caretOffset = code.replaceAll("\\^", "").indexOf('|');
        int declOffset = code.replaceAll("\\|", "").indexOf('^');
        
        assertTrue(caretOffset != (-1));
        assertTrue(declOffset != (-1));
        
        performTestSimpleFindDeclaration(code.replaceAll("\\^", "").replaceAll("\\|", ""), caretOffset, declOffset);
    }
    
    private void performTestSimpleFindDeclaration(String code, final int caretOffset, final int declarationOffset) throws Exception {
        performTestSimpleFindDeclaration(new String[] {code}, caretOffset, 0, declarationOffset);
    }
    
    private void performTestSimpleFindDeclaration(String[] code, final int caretOffset, final int declarationFile, final int declarationOffset) throws Exception {
        performTest(code, new CancellableTask<CompilationInfo>() {
            public void cancel() {}
            public void run(CompilationInfo parameter) throws Exception {
                DeclarationLocation found = DeclarationFinderImpl.findDeclarationImpl(parameter, caretOffset);

                assertNotNull(found.getFileObject());
                assertEquals(computeFileName(declarationFile - 1), found.getFileObject().getNameExt());
                assertEquals(declarationOffset, found.getOffset());
            }
        });
    }

}
