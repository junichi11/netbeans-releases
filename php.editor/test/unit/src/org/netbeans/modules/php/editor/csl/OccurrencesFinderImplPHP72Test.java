/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.php.editor.csl;


public class OccurrencesFinderImplPHP72Test extends OccurrencesFinderImplTestBase {

    public OccurrencesFinderImplPHP72Test(String testName) {
        super(testName);
    }

    public void testGroupUseTrailingCommas_01() throws Exception {
        checkOccurrences(getTestPath(), "class ^Foo {", true);
    }

    public void testGroupUseTrailingCommas_02() throws Exception {
        checkOccurrences(getTestPath(), "    Fo^o,", true);
    }

    public void testGroupUseTrailingCommas_03() throws Exception {
        checkOccurrences(getTestPath(), "$foo = new Fo^o();", true);
    }

    public void testGroupUseTrailingCommas_04() throws Exception {
        checkOccurrences(getTestPath(), "class B^ar {", true);
    }

    public void testGroupUseTrailingCommas_05() throws Exception {
        checkOccurrences(getTestPath(), "    Ba^r,", true);
    }

    public void testGroupUseTrailingCommas_06() throws Exception {
        checkOccurrences(getTestPath(), "$bar = new Ba^r();", true);
    }

    public void testGroupUseTrailingCommas_07() throws Exception {
        checkOccurrences(getTestPath(), "class Ba^z {", true);
    }

    public void testGroupUseTrailingCommas_08() throws Exception {
        checkOccurrences(getTestPath(), "    B\\Ba^z,", true);
    }

    public void testGroupUseTrailingCommas_09() throws Exception {
        checkOccurrences(getTestPath(), "$baz = new Ba^z();", true);
    }

}
