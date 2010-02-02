/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.makefile.navigator;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import org.netbeans.modules.cnd.makefile.model.MakefileElement;
import org.netbeans.modules.cnd.makefile.model.MakefileRule;
import org.netbeans.modules.cnd.makefile.parser.MakefileModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * @author Alexey Vladykin
 */
public class MakefileNavigatorPanelUI extends JPanel implements ExplorerManager.Provider {

    private final ExplorerManager manager;
    private final ListView view;
    private final Lookup lookup;

    public MakefileNavigatorPanelUI() {
        super(new BorderLayout());

        manager = new ExplorerManager();
        lookup = ExplorerUtils.createLookup(manager, getActionMap());

        view = new ListView();
        add(view, BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public void setWaiting() {
        // TODO: manager.setRootContext(new AbstractNode(new WaitingChildren()));
    }

    public void setModel(MakefileModel model) {
        manager.setRootContext(new AbstractNode(new MakefileElementChildren(model)));
    }

    private static class MakefileElementChildren extends Children.Keys<MakefileElement> {

        public MakefileElementChildren(MakefileModel model) {
            setKeys(model == null ? Collections.<MakefileElement>emptyList() : model.getElements());
        }

        @Override
        protected Node[] createNodes(MakefileElement key) {
            if (key.getKind() == MakefileElement.Kind.RULE) {
                List<Node> list = new ArrayList<Node>();
                MakefileRule rule = (MakefileRule) key;
                for (String target : rule.getTargets()) {
                    if (0 < target.length()) {
                        list.add(new MakefileTargetNode(rule, target));
                    }
                }
                return list.toArray(new Node[list.size()]);
            } else {
                return new Node[] {};
            }
        }
    }
}
