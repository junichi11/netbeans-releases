/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.performance.languages;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.languages.menus.*;

/**
 *
 * @author mkhramov@netbeans.org
 */
public class ScriptingMeasureMenusTest {
    public static NbTestSuite suite() throws URISyntaxException {
        PerformanceTestCase.prepareForMeasurements();

        NbTestSuite suite = new NbTestSuite("Scripting UI Responsiveness Menus suite");
        System.setProperty("suitename", ScriptingMeasureMenusTest.class.getCanonicalName());
        System.setProperty("suite", "UI Responsiveness Scripting Menus suite");

        URL u = ScriptingMeasureMenusTest.class.getProtectionDomain().getCodeSource().getLocation();
        File f = new File(u.toURI());
        while (f != null) {
            File hg = new File(f, ".hg");
            if (hg.isDirectory()) {
                System.setProperty("versioning.unversionedFolders", f.getPath());
                System.err.println("ignoring Hg folder: " + f);
                break;
            }
            f = f.getParentFile();
        }

        suite.addTest(NbModuleSuite.create(NbModuleSuite.emptyConfiguration().honorAutoloadEager(true)
        .addTest(ScriptingProjectNodePopupTest.class)
        .addTest(ScriptingNodePopupTest.class)
        .addTest(EditorMenuPopupTest.class)
        .enableModules(".*").clusters(".*").reuseUserDir(true)));
        
        return suite;
    }

}
