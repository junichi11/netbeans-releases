/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.debugger.jpda.js.frames.models;

import org.netbeans.modules.debugger.jpda.js.frames.JSStackFrame;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.modules.debugger.jpda.js.JSUtils;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.ExtendedNodeModel;
import org.netbeans.spi.viewmodel.ExtendedNodeModelFilter;
import org.netbeans.spi.viewmodel.ModelEvent;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author Martin
 */
@DebuggerServiceRegistration(path="netbeans-JPDASession/DebuggingView",
                             types=ExtendedNodeModelFilter.class,
                             position=22000)
public class DebuggingJSNodeModel implements ExtendedNodeModelFilter {
    
    private static final String SCRIPT_CLASS_PREFIX = "Script$";
    private static final String SCRIPT_CLASS_IN_HTML = ">Script$";

    private final List<ModelListener> listeners = new ArrayList<>();
    private ModelListener listenerToOriginal;
    private final Object listenerToOriginalLock = new Object();
    
    @Override
    public boolean canRename(ExtendedNodeModel original, Object node) throws UnknownTypeException {
        return original.canRename(node);
    }

    @Override
    public boolean canCopy(ExtendedNodeModel original, Object node) throws UnknownTypeException {
        return original.canCopy(node);
    }

    @Override
    public boolean canCut(ExtendedNodeModel original, Object node) throws UnknownTypeException {
        return original.canCut(node);
    }

    @Override
    public Transferable clipboardCopy(ExtendedNodeModel original, Object node) throws IOException, UnknownTypeException {
        return original.clipboardCopy(node);
    }

    @Override
    public Transferable clipboardCut(ExtendedNodeModel original, Object node) throws IOException, UnknownTypeException {
        return original.clipboardCut(node);
    }

    @Override
    public PasteType[] getPasteTypes(ExtendedNodeModel original, Object node, Transferable t) throws UnknownTypeException {
        return original.getPasteTypes(node, t);
    }

    @Override
    public void setName(ExtendedNodeModel original, Object node, String name) throws UnknownTypeException {
        original.setName(node, name);
    }

    @Override
    public String getIconBaseWithExtension(ExtendedNodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof JSStackFrame) {
            node = ((JSStackFrame) node).getJavaFrame();
        }
        return original.getIconBaseWithExtension(node);
    }

    @Override
    public String getDisplayName(NodeModel original, Object node) throws UnknownTypeException {
        String descr;
        if (node instanceof JSStackFrame) {
            synchronized (listenerToOriginalLock) {
                if (listenerToOriginal == null) {
                    listenerToOriginal = new OriginalModelListener();
                    original.addModelListener(listenerToOriginal);
                }
            }
            CallStackFrame javaFrame = ((JSStackFrame) node).getJavaFrame();
            descr = original.getDisplayName(javaFrame);
            //System.out.println("ORIG descr = '"+descr+"'");
            int i = descr.indexOf(JSUtils.NASHORN_SCRIPT);
            int i2 = 0;
            if (i < 0) {
                if (descr.startsWith(SCRIPT_CLASS_PREFIX)) {
                    i = 0;
                    i2 = SCRIPT_CLASS_PREFIX.length();
                } else {
                    i = descr.indexOf(SCRIPT_CLASS_IN_HTML);
                    if (i > 0) {
                        i2 = i + SCRIPT_CLASS_IN_HTML.length();
                        i++;
                    }
                }
            } else {
                i2 = i + JSUtils.NASHORN_SCRIPT.length();
            }
            if (i >= 0) {
                descr = descr.substring(0, i) + descr.substring(i2);
            }
            //System.out.println(" => descr = '"+descr+"'");
        } else {
            descr = original.getDisplayName(node);
        }
        return descr;
    }

    @Override
    public String getIconBase(NodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof JSStackFrame) {
            return original.getIconBase(((JSStackFrame) node).getJavaFrame());
        } else {
            return original.getIconBase(node);
        }
    }

    @Override
    public String getShortDescription(NodeModel original, Object node) throws UnknownTypeException {
        if (node instanceof JSStackFrame) {
            return original.getShortDescription(((JSStackFrame) node).getJavaFrame());
        } else {
            return original.getShortDescription(node);
        }
    }

    @Override
    public void addModelListener(ModelListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public void removeModelListener(ModelListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    private void fireModelEvent(ModelEvent event) {
        List<ModelListener> ls;
        synchronized (listeners) {
            ls = new ArrayList<>(listeners);
        }
        for (ModelListener ml : ls) {
            ml.modelChanged (event);
        }
    }
    
    private class OriginalModelListener implements ModelListener {

        @Override
        public void modelChanged(ModelEvent event) {
            if (event instanceof ModelEvent.NodeChanged) {
                ModelEvent.NodeChanged nch = (ModelEvent.NodeChanged) event;
                Object node = nch.getNode();
                if (node instanceof CallStackFrame) {
                    JSStackFrame jsFrame = JSStackFrame.getExisting((CallStackFrame) node);
                    if (jsFrame != null) {
                        event = new ModelEvent.NodeChanged(DebuggingJSNodeModel.this, jsFrame, nch.getChange());
                        fireModelEvent(event);
                    }
                }
            } else if (event instanceof ModelEvent.TableValueChanged) {
                ModelEvent.TableValueChanged tch = (ModelEvent.TableValueChanged) event;
                Object node = tch.getNode();
                if (node instanceof CallStackFrame) {
                    JSStackFrame jsFrame = JSStackFrame.getExisting((CallStackFrame) node);
                    if (jsFrame != null) {
                        event = new ModelEvent.TableValueChanged(DebuggingJSNodeModel.this, jsFrame, tch.getColumnID(), tch.getChange());
                        fireModelEvent(event);
                    }
                }
            }
            
        }
        
    }
    
}