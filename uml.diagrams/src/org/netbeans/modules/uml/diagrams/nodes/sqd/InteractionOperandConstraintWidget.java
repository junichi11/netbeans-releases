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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.uml.diagrams.nodes.sqd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.modules.uml.core.metamodel.dynamics.IInteractionConstraint;
import org.netbeans.modules.uml.diagrams.nodes.FeatureWidget;
import org.netbeans.modules.uml.diagrams.nodes.MovableLabelWidget;
import org.netbeans.modules.uml.drawingarea.persistence.NodeWriter;
import org.netbeans.modules.uml.drawingarea.persistence.PersistenceUtil;

/**
 *
 * @author sp153251
 */
public class InteractionOperandConstraintWidget extends FeatureWidget implements PropertyChangeListener {
    private IInteractionConstraint constraint;
    private ExpressionWidget expression;
    public InteractionOperandConstraintWidget(Scene scene,IInteractionConstraint constraint)
    {
        super(scene);
        this.constraint=constraint;
    }

    @Override
    protected void updateUI() {
        if(expression==null)
        {
            removeChildren();
            expression=new ExpressionWidget(getScene(), constraint.getSpecification());
            addChild(expression);
            expression.initialize(constraint.getSpecification());
        }
        getScene().validate();
        expression.propertyChange(null);
        getScene().validate();
    }
    
    
    
    @Override
    public void save(NodeWriter nodeWriter) {
        nodeWriter = PersistenceUtil.populateNodeWriter(nodeWriter, this);
        nodeWriter.setTypeInfo("InteractionOperandConstraintWidget");
        nodeWriter.setHasPositionSize(true);        
        PersistenceUtil.populateProperties(nodeWriter, this);
        nodeWriter.beginGraphNode();
        nodeWriter.endGraphNode();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateUI();
    }

    public MovableLabelWidget getLabel() {
        return expression.getLable();
    }

    void hideLabel() {
        expression.hideLabel();
    }

    boolean isLabelVisible() {
        return expression.isLabelVisible();
    }

    void setLabel(String expression) {
        setText(expression);
    }

    void showLabel() {
        expression.showLabel();
    }

    void switchToEditMode() {
        
    }

    public String getWidgetID() {
        return UMLWidgetIDString.INTERACTIONOPERANDCONSTRAINTWIDGET.toString();
    }

}
