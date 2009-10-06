/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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



package org.netbeans.test.umllib.customelements;

import java.util.List;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.test.umllib.DiagramElementChooser;
import org.netbeans.test.umllib.DiagramElementOperator;
import org.netbeans.test.umllib.DiagramOperator;
import org.netbeans.test.umllib.ElementTypes;
import org.netbeans.test.umllib.UMLWidgetOperator;
import org.netbeans.test.umllib.exceptions.NotFoundException;

public class CombinedFragmentOperator extends DiagramElementOperator{
    
    public CombinedFragmentOperator(DiagramOperator diagramOperator, String interactionOperatorName ) throws NotFoundException {
        this(diagramOperator, interactionOperatorName, 0);
    }
    
    public CombinedFragmentOperator(DiagramOperator diagramOperator, String interactionOperatorName, int index) throws NotFoundException {
        super(diagramOperator, new CombinedFragmentByNameChooser(interactionOperatorName), 0);
    }
    
    
    
    public static class CombinedFragmentByNameChooser implements DiagramElementChooser {
        
        String name;
        
        public CombinedFragmentByNameChooser(String name){
            this.name = name;
        }
        
        
        public boolean checkElement(Widget widget) {
            UMLWidgetOperator widgetOpr = new UMLWidgetOperator(widget);
            String name = widgetOpr.getName();
            if (!widgetOpr.getElementType().equals(ElementTypes.COMBINED_FRAGMENT.toString())) {
                return false;
            }

            List<Widget> widList = widget.getChildren();
            UMLWidgetOperator childWidgetOpr;
            
            for (Widget child : widList) {
                 childWidgetOpr = new UMLWidgetOperator(child);
                if (name.equals(childWidgetOpr.getName())) {
                    return true;
                }
            }
            return false;
        }
        
        public String getDescription() {
            return "Chooser for CombinedFragment Element: " + name ;
        }  
    }
}
