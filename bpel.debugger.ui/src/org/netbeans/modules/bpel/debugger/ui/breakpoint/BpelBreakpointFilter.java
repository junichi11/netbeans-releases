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

package org.netbeans.modules.bpel.debugger.ui.breakpoint;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JComponent;
import org.netbeans.modules.bpel.debugger.api.breakpoints.BpelFaultBreakpoint;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.netbeans.spi.viewmodel.Models;

import org.netbeans.modules.bpel.debugger.api.EditorContextBridge;
import org.netbeans.modules.bpel.debugger.api.breakpoints.LineBreakpoint;

/**
 * @author Vladimir Yaroslavskiy
 * @version 2005.10.27
 */
public class BpelBreakpointFilter implements NodeActionsProviderFilter {
    
    /**{@inheritDoc}*/
    public Action[] getActions (NodeActionsProvider original, Object object)
        throws UnknownTypeException
    {
        if (object instanceof LineBreakpoint) {
            Action[] actions = original.getActions (object);
            Action[] results = new Action [actions.length + 2];
            results[0] = GO_TO_SOURCE_ACTION;
            results[1] = null;

            for (int i=0; i < actions.length; i++) {
                results[i+2] = actions[i];
            }
            return results;
        } else if (object instanceof BpelFaultBreakpoint) {
            Action[] actions = original.getActions (object);
            Action[] results = new Action [actions.length + 2];
            results[0] = CUSTOMIZE_ACTION;
            results[1] = null;

            for (int i=0; i < actions.length; i++) {
                results[i+2] = actions[i];
            }
            return results;
        } else {
            return original.getActions (object);
        }
    }
    
    /**{@inheritDoc}*/
    public void performDefaultAction (NodeActionsProvider original, Object object)
        throws UnknownTypeException
    {
        if (object instanceof LineBreakpoint) {
            goToSource ((LineBreakpoint) object);
        } else if (object instanceof BpelFaultBreakpoint) {
            customize(object);
        } else {
            original.performDefaultAction(object);
        }
    }

    private static void goToSource (LineBreakpoint breakpoint) {
        EditorContextBridge.showSource(
            breakpoint.getURL(),
            breakpoint.getXpath());
    }

    private static final Action GO_TO_SOURCE_ACTION = Models.createAction (
        NbBundle.getMessage(
            BpelBreakpointFilter.class,
            "CTL_Breakpoint_Action_Go_to_Source"), // NOI18N
        new Models.ActionPerformer () {
            public boolean isEnabled (Object object) {
                return true;
            }
            public void perform (Object[] objects) {
                goToSource ((LineBreakpoint) objects [0]);
            }
        },
        Models.MULTISELECTION_TYPE_EXACTLY_ONE
    );
        
    private static final Action CUSTOMIZE_ACTION = Models.createAction (
        NbBundle.getMessage(
            BpelBreakpointFilter.class,
            "CTL_Breakpoint_Customize_Label"), // NOI18N
        new Models.ActionPerformer() {
            public boolean isEnabled(Object object) {
                return (object instanceof BpelFaultBreakpoint);
            }
            public void perform (Object[] nodes) {
                customize(nodes[0]);
            }
        },
        Models.MULTISELECTION_TYPE_EXACTLY_ONE
    );
        
    private static void customize (Object object) {
        if (!(object instanceof BpelFaultBreakpoint)) {
            return;
        }
        
        BpelFaultBreakpoint faultBp = (BpelFaultBreakpoint)object;
        JComponent c = new BpelFaultBreakpointPanel(faultBp);
        c.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(BpelBreakpointFilter.class,
                "ACSD_Breakpoint_Customizer_Dialog")); // NOI18N
        HelpCtx helpCtx = HelpCtx.findHelp (c);
        if (helpCtx == null) {
            helpCtx = new HelpCtx ("debug.add.breakpoint");  // NOI18N
        }
        final Controller[] cPtr = new Controller[] { (Controller) c };
        final DialogDescriptor[] descriptorPtr = new DialogDescriptor[1];
        final Dialog[] dialogPtr = new Dialog[1];
        ActionListener buttonsActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (descriptorPtr[0].getValue() == DialogDescriptor.OK_OPTION) {
                    boolean ok = cPtr[0].ok();
                    if (ok) {
                        dialogPtr[0].setVisible(false);
                    }
                } else {
                    dialogPtr[0].setVisible(false);
                }
            }
        };
        DialogDescriptor descriptor = new DialogDescriptor (
            c,
            NbBundle.getMessage (
                BpelBreakpointFilter.class,
                "CTL_Breakpoint_Customizer_Title" // NOI18N
            ),
            true,
            DialogDescriptor.OK_CANCEL_OPTION,
            DialogDescriptor.OK_OPTION,
            DialogDescriptor.DEFAULT_ALIGN,
            helpCtx,
            buttonsActionListener
        );
        descriptor.setClosingOptions(new Object[] {});
        Dialog d = DialogDisplayer.getDefault ().createDialog (descriptor);
        d.pack ();
        descriptorPtr[0] = descriptor;
        dialogPtr[0] = d;
        d.setVisible (true);
    }
}
