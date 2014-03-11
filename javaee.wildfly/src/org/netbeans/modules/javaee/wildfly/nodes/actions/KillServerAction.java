/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javaee.wildfly.nodes.actions;

import org.netbeans.modules.j2ee.deployment.impl.ServerInstance;
import org.netbeans.modules.javaee.wildfly.ide.WildflyKiller;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import static org.openide.util.actions.CookieAction.MODE_EXACTLY_ONE;

/**
 *
 * @author Emmanuel Hugonnet (ehsavoie) <emmanuel.hugonnet@gmail.com>
 */
public class KillServerAction extends CookieAction {

    private final WildflyKiller killer = new WildflyKiller();

    @Override
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    @Override
    protected void performAction(Node[] nodes) {
        if ((nodes == null) || (nodes.length < 1)) {
            return;
        }
        if (killer.killServers()) {
            for (Node node : nodes) {
                ServerInstance si = (ServerInstance) node.getCookie(ServerInstance.class);
                if (si != null) {
                    si.refresh();
                }
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(KillServerAction.class, "LBL_KillServerGUIAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return true;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class<?>[]{};
    }
}
