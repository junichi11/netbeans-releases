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
 * Software is Sun Microsystems, Inc. Portions Copyright 2002 Sun
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

package org.netbeans.modules.settings.convertors;

import org.netbeans.junit.NbTestCase;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInfo;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author  Jan Pokorsky
 */
public class SerialDataNodeTest extends NbTestCase {

    /** Creates a new instance of SerialDataNodeTest */
    public SerialDataNodeTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        Lookup.getDefault().lookup(ModuleInfo.class);
    }
    
    public void testDisplayName() throws Exception {
        String res = "Settings/org-netbeans-modules-settings-convertors-testDisplayName.settings";
        FileObject fo = FileUtil.getConfigFile(res);
        assertNotNull(res, fo);
        assertNull("name", fo.getAttribute("name"));
        
        DataObject dobj = DataObject.find (fo);
        Node n = dobj.getNodeDelegate();
        assertNotNull(n);
        assertEquals("I18N", n.getDisplayName());
        
        // property sets have to be initialized otherwise the change name would be
        // propagated to the node after some delay (~2s)
        Object garbage = n.getPropertySets();
        
        InstanceCookie ic = (InstanceCookie) dobj.getCookie(InstanceCookie.class);
        assertNotNull (dobj + " does not contain instance cookie", ic);
        
        FooSetting foo = (FooSetting) ic.instanceCreate();
        String newName = "newName";
        foo.setName(newName);
        assertEquals(n.toString(), newName, n.getDisplayName());
        
        newName = "newNameViaNode";
        n.setName(newName);
        assertEquals(n.toString(), newName, n.getDisplayName());
        assertEquals(n.toString(), newName, foo.getName());
    }
}
