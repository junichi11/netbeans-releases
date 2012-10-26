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
package org.netbeans.modules.css.visual.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import org.netbeans.api.actions.Openable;
import org.netbeans.modules.css.visual.Location;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Action that opens a location in the editor.
 *
 * @author mfukala@netbeans.org.
 */
public class OpenLocationAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            final Location location = activatedNodes[i].getLookup().lookup(Location.class);
            FileObject fob = location.getFile();
            if (fob != null) {
                try {
                    DataObject dob = DataObject.find(fob);
                    if (location.getOffset() == -1) {
                        //just open file
                        Openable openable = dob.getLookup().lookup(Openable.class);
                        if (openable != null) {
                            openable.open();
                        }
                    } else {
                        //open and set caret to the location
                        final EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
                        if (ec != null) {
                            Mutex.EVENT.readAccess(new Runnable() {

                                @Override
                                public void run() {
                                    JEditorPane[] openedPanes = ec.getOpenedPanes();
                                    if (openedPanes != null && openedPanes.length > 0) {
                                        //already opened
                                        ec.open(); //give it a focus 
                                        JEditorPane pane = openedPanes[0];
                                        pane.setCaretPosition(location.getOffset());
                                    } else {
                                        //not opened, open it
                                        try {
                                            ec.openDocument();
                                            ec.open();
                                            openedPanes = ec.getOpenedPanes();
                                            if (openedPanes != null && openedPanes.length > 0) {
                                                //now opened
                                                JEditorPane pane = openedPanes[0];
                                                pane.setCaretPosition(location.getOffset());
                                            }
                                        } catch (IOException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }
                                    }
                                }
                                
                            });
                        }

                    }
                } catch (DataObjectNotFoundException ex) {
                    Logger.getLogger(OpenLocationAction.class.getName()).log(Level.INFO, null, ex);
                }
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length == 0) {
            return false;
        }
        for (int i = 0; i < activatedNodes.length; i++) {
            Location resource = activatedNodes[i].getLookup().lookup(Location.class);
            if ((resource != null) && (resource.getFile() == null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(OpenLocationAction.class, "OpenResourceAction.displayName"); // NOI18N
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
