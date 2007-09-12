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

import org.netbeans.modules.bpel.model.api.BpelEntity;
import org.netbeans.modules.bpel.model.api.ElseIf;
import org.netbeans.modules.bpel.model.api.If;
import org.openide.util.NbBundle;

public class MoveElseIfRightAction extends BpelNodeAction {

    private static final long serialVersionUID = 1L;

    protected String getBundleName() {
        return NbBundle.getMessage(getClass(), "CTL_MoveElseIfRightAction"); // NOI18N
    }

    public ActionType getType() {
        return ActionType.MOVE_ELSE_IF_RIGHT;
    }
    
    protected void performAction(BpelEntity[] bpelEntities) {
        
        ElseIf curElseIf = (ElseIf) bpelEntities[0];
        
        If parentIf = (If) curElseIf.getParent();
        
        ElseIf[] elseIfs = parentIf.getElseIfs();

        for (int i = 0; i < elseIfs.length - 1; i++) {
            if (elseIfs[i] == curElseIf) {
                ElseIf newElseIf = (ElseIf) curElseIf.cut();
                parentIf.insertElseIf(newElseIf, i + 1);
                return;
            }
        }
    }
    
    protected boolean enable(BpelEntity[] bpelEntities) {
        if (!super.enable(bpelEntities)) {
            return false;
        }
        if (bpelEntities[0] instanceof ElseIf) {
            ElseIf curElseIf = (ElseIf) bpelEntities[0];
            If parentIf = (If) curElseIf.getParent();
            ElseIf[] elseIfs = parentIf.getElseIfs();
            
            return elseIfs[elseIfs.length - 1] != curElseIf;
        }
        return false;
    }
}
