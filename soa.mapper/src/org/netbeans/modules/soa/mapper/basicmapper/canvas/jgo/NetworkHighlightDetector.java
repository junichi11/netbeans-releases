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

package org.netbeans.modules.soa.mapper.basicmapper.canvas.jgo;

import org.netbeans.modules.soa.mapper.common.basicmapper.canvas.gtk.ICanvasFieldNode;
import org.netbeans.modules.soa.mapper.common.basicmapper.canvas.gtk.ICanvasMapperLink;
import org.netbeans.modules.soa.mapper.common.basicmapper.canvas.gtk.ICanvasMethoidNode;


/**
 * Attempts to determine if a given path has been highlighted.
 *
 * @author Josh Sandusky
 */
public class NetworkHighlightDetector implements NetworkHighlightConstants {

    private boolean mIsHighlightDetected;


    public NetworkHighlightDetector(ICanvasMapperLink link) {
        mIsHighlightDetected = false;
        visit(link);
    }
    
    
    public boolean isHighlightDetected() {
        return mIsHighlightDetected;
    }
    
    private void visit(ICanvasMapperLink link) {
        if (link instanceof BasicCanvasNodeToNodeLink) {
            visit((BasicCanvasNodeToNodeLink) link);
        } else if (link instanceof BasicCanvasNodeToTreeLink) {
            visit((BasicCanvasNodeToTreeLink) link);
        } else if (link instanceof BasicCanvasTreeToNodeLink) {
            visit((BasicCanvasTreeToNodeLink) link);
        } else if (link instanceof BasicCanvasTreeToTreeLink) {
            visit((BasicCanvasTreeToTreeLink) link);
        }
    }
    
    private void visit(BasicCanvasNodeToNodeLink link) {
        if (link.getSourceFieldNode() != null) {
            visit(link.getSourceFieldNode());
        }
        if (link.getDestFieldNode() != null) {
            visit(link.getDestFieldNode());
        }
    }
    
    private void visit(BasicCanvasNodeToTreeLink link) {
        if (link.getSourceFieldNode() != null) {
            visit(link.getSourceFieldNode());
        }
    }
    
    private void visit(BasicCanvasTreeToNodeLink link) {
        if (link.getDestFieldNode() != null) {
            visit(link.getDestFieldNode());
        }
    }
    
    private void visit(ICanvasFieldNode fieldNode) {
        ICanvasMethoidNode methoidNode = (ICanvasMethoidNode) fieldNode.getContainer();
        if (methoidNode instanceof BasicCanvasMethoidNode) {
            BasicCanvasMethoidNode basicMethoidNode = (BasicCanvasMethoidNode) methoidNode;
            if (COLOR_NODE_SELECTED.equals(basicMethoidNode.getTitleBarColor())) {
                mIsHighlightDetected = true;
            }
        }
    }
}
