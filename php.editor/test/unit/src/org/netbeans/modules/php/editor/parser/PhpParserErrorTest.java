/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.parser;

import org.netbeans.modules.csl.api.test.CslTestBase;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class PhpParserErrorTest extends CslTestBase {

    public PhpParserErrorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIssue189630() throws Exception {
        checkErrors("testfiles/parser/issue189630.php");
    }

    public void testFieldModificators_01() throws Exception {
        checkErrors("testfiles/parser/fieldModificators_01.php");
    }

    public void testFieldModificators_02() throws Exception {
        checkErrors("testfiles/parser/fieldModificators_02.php");
    }

    public void testFieldModificators_03() throws Exception {
        checkErrors("testfiles/parser/fieldModificators_03.php");
    }

    public void testFieldModificators_04() throws Exception {
        checkErrors("testfiles/parser/fieldModificators_04.php");
    }

    public void testFieldModificators_05() throws Exception {
        checkErrors("testfiles/parser/fieldModificators_05.php");
    }

    public void testMethodModificators_01() throws Exception {
        checkErrors("testfiles/parser/methodModificators_01.php");
    }

    public void testMethodModificators_02() throws Exception {
        checkErrors("testfiles/parser/methodModificators_02.php");
    }

    public void testMethodModificators_03() throws Exception {
        checkErrors("testfiles/parser/methodModificators_03.php");
    }

    public void testMethodModificators_04() throws Exception {
        checkErrors("testfiles/parser/methodModificators_04.php");
    }

    public void testMethodModificators_05() throws Exception {
        checkErrors("testfiles/parser/methodModificators_05.php");
    }

    public void testTraits_01() throws Exception {
        checkErrors("testfiles/traits_01.php");
    }

    public void testTraits_02() throws Exception {
        checkErrors("testfiles/traits_02.php");
    }

    public void testTraits_03() throws Exception {
        checkErrors("testfiles/traits_03.php");
    }

    public void testTraits_04() throws Exception {
        checkErrors("testfiles/traits_04.php");
    }

    public void testTraits_05() throws Exception {
        checkErrors("testfiles/traits_05.php");
    }

    public void testTraits_06() throws Exception {
        checkErrors("testfiles/traits_06.php");
    }

    public void testShortArray_01() throws Exception {
        checkErrors("testfiles/parser/shortArrays_01.php");
    }

    public void testShortArray_02() throws Exception {
        checkErrors("testfiles/parser/shortArrays_02.php");
    }

    public void testShortArraysStaticScalar_01() throws Exception {
        checkErrors("testfiles/parser/shortArraysStaticScalar_01.php");
    }

    public void testShortArraysStaticScalar_02() throws Exception {
        checkErrors("testfiles/parser/shortArraysStaticScalar_02.php");
    }

    public void testAnonymousObjectVariable() throws Exception {
        checkErrors("testfiles/parser/anonymousObjectVariable.php");
    }

    public void testArrayDereferencing_01() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_01.php");
    }

    public void testArrayDereferencing_02() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_02.php");
    }

    public void testArrayDereferencing_03() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_03.php");
    }

    public void testArrayDereferencing_04() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_04.php");
    }

    public void testArrayDereferencing_05() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_05.php");
    }

    public void testArrayDereferencing_06() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_06.php");
    }

    public void testArrayDereferencing_07() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_07.php");
    }

    public void testArrayDereferencing_08() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_08.php");
    }

    public void testArrayDereferencing_09() throws Exception {
        checkErrors("testfiles/parser/arrayDereferencing_09.php");
    }

    public void testBinaryNotation_01() throws Exception {
        checkErrors("testfiles/parser/binaryNotation_01.php");
    }

    public void testBinaryNotation_02() throws Exception {
        checkErrors("testfiles/parser/binaryNotation_02.php");
    }

    public void testBinaryNotation_03() throws Exception {
        checkErrors("testfiles/parser/binaryNotation_03.php");
    }

    public void testBinaryNotation_04() throws Exception {
        checkErrors("testfiles/parser/binaryNotation_04.php");
    }
}
