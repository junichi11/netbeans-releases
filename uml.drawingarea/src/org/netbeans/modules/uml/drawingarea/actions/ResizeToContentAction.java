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
package org.netbeans.modules.uml.drawingarea.actions;

import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.uml.drawingarea.util.Util;
import org.netbeans.modules.uml.drawingarea.view.DesignerScene;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IPresentationElement;
import org.netbeans.modules.uml.drawingarea.palette.context.ContextPaletteManager;
import org.netbeans.modules.uml.drawingarea.view.UMLNodeWidget;
import org.netbeans.modules.uml.drawingarea.widgets.Container;
import org.netbeans.modules.uml.drawingarea.widgets.ResizeToContentMarker;

/**
 *
 * 
 */
public class ResizeToContentAction extends SceneNodeAction
{
    private DesignerScene scene;
    
    @Override
    public Action createContextAwareInstance(Lookup actionContext)
    {
        scene = actionContext.lookup(DesignerScene.class);
        return this;
    }
    
    protected void performAction(Node[] activatedNodes)
    {

        if (scene == null) 
        {
            return;
        }
        //separate from event dispatch thread
            new Thread()
            {
            @Override
                public void run() {
                    Set selectedObjs = scene.getSelectedObjects();

                    ContextPaletteManager manager = scene.getContextPaletteManager();
                    if(manager != null)
                    {
                        manager.cancelPalette();
                    }

                    for(Object selected: selectedObjs) 
                    {
                        if (selected instanceof IPresentationElement)
                        {
                            IPresentationElement selectedPE = (IPresentationElement) selected;
                            Widget w = scene.findWidget(selectedPE);
                            Util.resizeNodeToContents(w);
                        }
                    }
                    scene.validate();

                    if(manager != null)
                    {
                        manager.selectionChanged(null);
                    }
                }
            }.start();
    }

    @Override
    protected boolean enable(Node[] activatedNodes)
    {
        boolean retVal = false;
        
        if(super.enable(activatedNodes) == true)
        {
            Set selectedObjs = scene.getSelectedObjects();
            if (selectedObjs.size() > 0) 
            {
                for (Object object: selectedObjs)
                {
                    Widget widget = scene.findWidget(object);
                    if (!(widget instanceof UMLNodeWidget))
                    {
                        return false;
                    }
                    if (widget instanceof Container || !((UMLNodeWidget)widget).isResizable())
                    {
                        return false;
                    }
                    if (widget instanceof ResizeToContentMarker)
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        return retVal;
    }

    public String getName()
    {
        return NbBundle.getMessage(ResizeToContentAction.class, "LBL_RESIZE_TO_CONTENT");
    }

    public HelpCtx getHelpCtx()
    {
        return null;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
