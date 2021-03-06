/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.model.impl;

import java.util.List;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.TypeScope;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class SuperAndSubTypeTest extends ModelTestBase {

    public SuperAndSubTypeTest(String testName) {
        super(testName);
    }

    private Model getModel() throws Exception {
        return getModel(getTestSource("testfiles/model/superandsubtype/" + getName() + ".php"));
    }

    private TypeScope getSuperType(final Model model) {
        List<? extends TypeScope> findSuperTypes = model.getIndexScope().findTypes(QualifiedName.create("\\TestNameSpace\\Super"));
        assertFalse(findSuperTypes.isEmpty());
        TypeScope possibleSuperType = ModelUtils.getFirst(findSuperTypes);
        assertNotNull(possibleSuperType);
        return possibleSuperType;
    }

    private TypeScope getSubType(final Model model) {
        List<? extends TypeScope> findSubTypes = model.getIndexScope().findTypes(QualifiedName.create("\\TestNameSpace\\Sub"));
        assertFalse(findSubTypes.isEmpty());
        TypeScope possibleSubType = ModelUtils.getFirst(findSubTypes);
        assertNotNull(possibleSubType);
        return possibleSubType;
    }

    public void testSuperType_01() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_02() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_03() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_04() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_05() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_06() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_07() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_08() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_09() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_10() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_11() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_12() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_13() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_14() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_15() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSuperType_16() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

    public void testSubType_01() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_02() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_03() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_04() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_05() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_06() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_07() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_08() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_09() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_10() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_11() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_12() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_13() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_14() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_15() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testSubType_16() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertTrue(possibleSubType.isSubTypeOf(possibleSuperType));
    }

    public void testIssue217175() throws Exception {
        Model model = getModel();
        TypeScope possibleSuperType = getSuperType(model);
        TypeScope possibleSubType = getSubType(model);

        assertFalse(possibleSuperType.isSuperTypeOf(possibleSubType));
    }

}
