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
package org.netbeans.modules.cnd.api.compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.cnd.api.utils.RemoteUtils;

/**
 * CompilerSetManagerEvents handles tasks which depends on CompilerSetManager activity and
 * have to survive CompilerSetManager.deepCopy()
 * 
 * There is only one (and I hope there would be only one) such event -- Code Model Readiness
 *
 * @author Sergey Grinev
 */
public class CompilerSetManagerEvents {

    private static final Map<String, CompilerSetManagerEvents> map = new HashMap<String, CompilerSetManagerEvents>();

    public static synchronized CompilerSetManagerEvents get(String hkey) {
        CompilerSetManagerEvents instance = map.get(hkey);
        if (instance == null) {
            instance = new CompilerSetManagerEvents(hkey);
            map.put(hkey, instance);
        }
        return instance;
    }

    public CompilerSetManagerEvents(String hkey) {
        this.hkey = hkey;
    }

    private final String hkey;

    private boolean isCodeModelInfoReady = false;
    private List<Runnable> tasks = new ArrayList<Runnable>();

    public void runOnCodeModelReadiness(Runnable task) {
        if (RemoteUtils.isLocalhost(hkey) || isCodeModelInfoReady) {
            task.run();
        } else {
            tasks.add(task);
        }
    }

    /* package */ void runTasks() {
        isCodeModelInfoReady = true;
        for (Runnable task : tasks) {
            task.run();
        }
        tasks = null;
    }
}
