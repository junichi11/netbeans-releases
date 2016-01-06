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
package org.netbeans.modules.derby.test;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.derby.api.DerbyDatabasesTest;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Common ancestor for all test classes.
 *
 * @author Andrei Badea
 */
public class TestBase extends NbTestCase {
    protected Lookup sampleDBLookup;
    
    public TestBase(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
       
        clearWorkDir();
        
        DerbyDatabasesTest.SampleDatabaseLocator sdl = new DerbyDatabasesTest.SampleDatabaseLocator();
        sampleDBLookup = new ProxyLookup(Lookup.getDefault(), Lookups.singleton(sdl));
        
        Lookups.executeWith(sampleDBLookup, new Runnable() {
            @Override
            public void run() {
                // Force initialization of JDBCDrivers
                JDBCDriverManager jdm = JDBCDriverManager.getDefault();
                JDBCDriver[] registeredDrivers = jdm.getDrivers();
            }
        });
    }

    public static void createFakeDerbyInstallation(File location) throws IOException {
        if (!location.mkdirs()) {
            throw new IOException("Could not create "
                    + location.getAbsolutePath());
        }
        File lib = new File(location, "lib");
        if (!lib.mkdir()) {
            throw new IOException("Could not create " + lib.getAbsolutePath());
        }
        new File(lib, "derby.jar").createNewFile();
        new File(lib, "derbyclient.jar").createNewFile();
    }
}
