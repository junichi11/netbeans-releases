/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.bpel.nodes.actions;

import javax.swing.KeyStroke;
import javax.swing.UIManager;
import org.netbeans.modules.bpel.model.api.BpelContainer;
import org.netbeans.modules.bpel.model.api.BpelEntity;
import org.netbeans.modules.bpel.model.api.Else;
import org.netbeans.modules.bpel.model.api.Process;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Vitaly Bychkov
 * @version 23 March 2006
 *
 */
public class DeleteAction extends BpelNodeAction {
    private static final long serialVersionUID = 1L;
    private static final String DELETE_KEYSTROKE = "DELETE"; // NOI18N
    
    public DeleteAction() {
        super();
        putValue(DeleteAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(DELETE_KEYSTROKE));
    }
    
    protected String getBundleName() {
        return NbBundle.getMessage(DeleteAction.class, "CTL_DeleteAction"); // NOI18N
    }

    /**
     * Used just to declare public scope instead protected
     */
    public boolean enable(Node[] nodes) {
        return super.enable(nodes);
    }

    /**
     * Used just to declare public scope instead protected
     */
    public void performAction(Node[] nodes) {
        super.performAction(nodes);
    }
    
    protected void performAction(BpelEntity[] bpelEntities) {
        if (!enable(bpelEntities)) {
            return;
        }

        for (BpelEntity entity : bpelEntities) {
            assert entity != null;
            BpelContainer parent = entity.getParent();
            assert parent != null;
            parent.remove(entity);
            
            if (parent instanceof Else){
                BpelContainer if_elem = parent .getParent();
                if (if_elem != null){
                    if_elem.remove(parent);
                }
            }
        }
    }
    
    protected boolean enable(BpelEntity[] bpelEntities) {
        return super.enable(bpelEntities)
        && !(bpelEntities[0] instanceof Process);
    }
    
    public ActionType getType() {
        return ActionType.REMOVE;
    }
}
