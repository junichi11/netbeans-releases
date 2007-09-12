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
package org.netbeans.modules.bpel.design.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.bpel.design.model.patterns.CompositePattern;
import org.netbeans.modules.bpel.design.model.patterns.Pattern;
import org.netbeans.modules.bpel.design.selection.EntitySelectionModel;

/**
 *
 * @author Vitaly Bychkov
 * @version 1.0
 */
public class DiagramModelHierarchyIterator implements DiagramModelIterator {
    
    private DiagramModel model;
    private EntitySelectionModel selectionModel;
    
    public DiagramModelHierarchyIterator(DiagramModel model,
            EntitySelectionModel selectionModel) {
        assert model != null && selectionModel != null;
        
        this.model = model;
        this.selectionModel = selectionModel;
    }
    
    public Pattern next() {
        Pattern p = selectionModel.getSelectedPattern();
        
        if (p == null) {
            return null;
        }
        
        Pattern nextPattern = null;
        
        if (p instanceof CompositePattern
                && ! model.isCollapsed(p.getOMReference())) {
            nextPattern = getNextNestedPattern((CompositePattern)p);
        }
        
        if (nextPattern == null) {
            nextPattern = getNextParentPattern(p);
        }
        
        return nextPattern;
    }
    
    public Pattern previous() {
        Pattern p = selectionModel.getSelectedPattern();
        
        if (p == null) {
            return null;
        }
        return getPrevParentPattern(p);
    }
    
    private Pattern getPrevNestedPattern(CompositePattern pattern) {
        if (pattern == null) {
            return null;
        }
        Pattern prevPattern = null;
        
        List<Pattern> children = pattern.getNestedPatterns();
        if (children == null) {
            return pattern;
        }
        
        int childSize = children.size();
        for (int i = childSize-1; i >= 0; i--) {
            Pattern tmpPattern = children.get(i);
            if (tmpPattern.isSelectable()) {
                prevPattern = tmpPattern;
            }
            
            if (tmpPattern instanceof CompositePattern) {
                tmpPattern = getPrevNestedPattern((CompositePattern)tmpPattern);
                if (tmpPattern != null && tmpPattern.isSelectable()) {
                    prevPattern = tmpPattern;
                }
            }
            
            if (prevPattern != null) {
                break;
            }
        }
        
        return prevPattern == null ? pattern : prevPattern;
    }

    private Pattern getNextNestedPattern(CompositePattern pattern) {
        if (pattern == null) {
            return null;
        }
        Pattern nextPattern = null;
        
        List<Pattern> children = pattern.getNestedPatterns();
        for (Pattern elem : children) {
            if (elem.isSelectable()) {
                nextPattern = elem;
                break;
            }
        }
        
        return nextPattern;
    }
    
    private Pattern getPrevParentPattern(Pattern curPattern) {
        if (curPattern == null) {
            return curPattern;
        }
        
        Pattern prevParent = null;
        CompositePattern parent = curPattern.getParent();
        if (parent == null) {
            // hardcoded trick - loop shift tab way
            Pattern rootPattern = curPattern.getModel().getRootPattern();
            return rootPattern instanceof CompositePattern 
                    ? getPrevNestedPattern((CompositePattern)rootPattern) 
                    : rootPattern;
        }
        
        List<Pattern> children = parent.getNestedPatterns();
        int curPatternIndex = children.indexOf(curPattern);
        assert curPatternIndex > -1;   
        
        for (int i = curPatternIndex-1; i >= 0; i--) {
            Pattern tmpPattern = children.get(i);
            if (tmpPattern.isSelectable()) {
                prevParent = tmpPattern;
            }
            
            if (tmpPattern instanceof CompositePattern) {
                tmpPattern = getPrevNestedPattern((CompositePattern)tmpPattern);
                if (tmpPattern != null && tmpPattern.isSelectable()) {
                    prevParent = tmpPattern;
                }
            }
            
            if (prevParent != null) {
                break;
            }
        }
        
        if (prevParent == null && ! parent.isSelectable()) {
            prevParent = getPrevParentPattern(parent);
        }
        
        return prevParent == null ? parent : prevParent;
    }
    
    private Pattern getNextParentPattern(Pattern pattern) {
        if (pattern == null) {
            return pattern;
        }
        
        Pattern nextParent = null;
        CompositePattern curParent = pattern.getParent();
        if (curParent == null) {
            // hardcoded trick - loop tab way
            return pattern.getModel().getRootPattern();
        }
        
        List<Pattern> nestedPatterns = curParent.getNestedPatterns();
        int curPatternIndex = nestedPatterns.indexOf(pattern);
        assert curPatternIndex > -1;
        
        for (int i = curPatternIndex+1; i < nestedPatterns.size(); i++) {
            Pattern tmpPattern = nestedPatterns.get(i);

            if (tmpPattern.isSelectable()) {
                nextParent = tmpPattern;
            } else if (!tmpPattern.isSelectable()
                    && tmpPattern instanceof CompositePattern) 
            {
                nextParent = getNextNestedPattern((CompositePattern) tmpPattern);
            }
            
            if (nextParent != null) {
                break;
            }        
        }
        
        return nextParent == null ? getNextParentPattern(curParent) : nextParent;
    }
}
