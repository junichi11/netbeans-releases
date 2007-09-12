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
package org.netbeans.modules.bpel.properties.editors;

import org.netbeans.modules.bpel.model.api.BaseScope;
import org.netbeans.modules.bpel.properties.ImportRegistrationHelper;
import static org.netbeans.modules.bpel.properties.PropertyType.NAME;
import org.netbeans.modules.bpel.model.api.Variable;
import org.netbeans.modules.bpel.model.api.VariableContainer;
import org.netbeans.modules.bpel.model.api.references.SchemaReference;
import org.netbeans.modules.bpel.model.api.references.WSDLReference;
import org.netbeans.modules.bpel.properties.TypeContainer;
import org.netbeans.modules.bpel.properties.choosers.TypeChooserPanel;
import org.netbeans.modules.soa.ui.form.CustomNodeEditor;
import org.netbeans.modules.soa.ui.form.EditorLifeCycle;
import org.netbeans.modules.soa.ui.form.EditorLifeCycleAdapter;
import org.netbeans.modules.soa.ui.form.valid.DefaultValidator;
import org.netbeans.modules.soa.ui.form.valid.ValidStateManager;
import org.netbeans.modules.soa.ui.form.valid.ValidStateManager.ValidStateListener;
import org.netbeans.modules.soa.ui.form.valid.Validator;
import org.netbeans.modules.bpel.nodes.VariableNode;
import org.netbeans.modules.bpel.properties.Constants.StereotypeFilter;
import org.netbeans.modules.bpel.properties.Util;
import org.netbeans.modules.soa.ui.form.CustomNodeEditor.EditingMode;
import org.netbeans.modules.bpel.editors.api.ui.valid.ErrorMessagesBundle;
import org.netbeans.modules.bpel.properties.editors.controls.filter.VisibilityScope;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.GlobalType;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.Reference;
import org.openide.ErrorManager;

/**
 * @author nk160297
 */
public class VariableMainPanel extends EditorLifeCycleAdapter
        implements Validator.Provider {
    
    static final long serialVersionUID = 1L;
    
    private CustomNodeEditor<Variable> myEditor;
    private DefaultValidator myValidator;
    
    public VariableMainPanel(CustomNodeEditor<Variable> editor) {
        this.myEditor = editor;
        createContent();
    }
    
    private void bindControls2PropertyNames() {
        fldVariableName.putClientProperty(CustomNodeEditor.PROPERTY_BINDER, NAME);
    }
    
    public boolean initControls() {
        try {
            //
            TypeChooserPanel typeChooser = (TypeChooserPanel)pnlTypeChooser;
            typeChooser.init(StereotypeFilter.ALL, myEditor.getLookup());
            //
            Variable var = myEditor.getEditedObject();
            Reference typeRef = VariableNode.getVariableType(var);
            //
            if (typeRef == null) {
                typeChooser.setSelectedValue(null);
            } else {
                TypeContainer tc = new TypeContainer(typeRef);
                typeChooser.setSelectedValue(tc);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return true;
    }
    
    public void createContent() {
        initComponents();
        bindControls2PropertyNames();
        //
        ((EditorLifeCycle)pnlTypeChooser).createContent();
        TypeChooserPanel chooserPanel = (TypeChooserPanel)pnlTypeChooser;
        Util.attachDefaultDblClickAction(chooserPanel, chooserPanel);
        //
        myEditor.getValidStateManager(true).addValidStateListener(
                new ValidStateListener() {
            public void stateChanged(ValidStateManager source, boolean isValid) {
                if (source.isValid()) {
                    lblErrorMessage.setText("");
                } else {
                    lblErrorMessage.setText(source.getHtmlReasons());
                }
            }
        });
    }
    
    public boolean applyNewValues() {
        try {
            Variable var = myEditor.getEditedObject();
            Model model = null;
            //
            TypeContainer tc = ((TypeChooserPanel)pnlTypeChooser).getSelectedValue();
            if (tc != null) {
                switch (tc.getStereotype()) {
                    case PRIMITIVE_TYPE:
                    case GLOBAL_SIMPLE_TYPE:
                    case GLOBAL_COMPLEX_TYPE:
                    case GLOBAL_TYPE:
                        GlobalType gType = tc.getGlobalType();
                        SchemaReference<GlobalType> gTypeRef =
                                var.createSchemaReference(
                                gType, GlobalType.class);
                        var.setType(gTypeRef);
                        var.removeElement();
                        var.removeMessageType();
                        model = gType.getModel();
                        
                        break;
                    case MESSAGE:
                        Message message = tc.getMessage();
                        WSDLReference<Message> messageRef =
                                var.createWSDLReference(message, Message.class);
                        var.setMessageType(messageRef);
                        var.removeElement();
                        var.removeType();
                        model = message.getModel();
                        
                        break;
                    case GLOBAL_ELEMENT:
                        GlobalElement gElement = tc.getGlobalElement();
                        SchemaReference<GlobalElement> elementRef =
                                var.createSchemaReference(
                                gElement, GlobalElement.class);
                        var.setElement(elementRef);
                        var.removeType();
                        var.removeMessageType();
                        model = gElement.getModel();
                        break;
                }
            }
            //
            if (model != null){
                new ImportRegistrationHelper(var.getBpelModel()).addImport(model);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return true;
    }
    
    public DefaultValidator getValidator() {
        if (myValidator == null) {
            myValidator = new DefaultValidator(myEditor, ErrorMessagesBundle.class) {
                
                public boolean doFastValidation() {
                    //
                    String varName = fldVariableName.getText();
                    if (varName == null || varName.length() == 0) {
                        addReasonKey("ERR_NAME_EMPTY"); //NOI18N
                    }
                    //
                    return isReasonsListEmpty();
                }
                
                public boolean doDetailedValidation() {
                    super.doDetailedValidation();
                    //
                    // Check that the variable name is unique
                    VariableContainer vc = null;
                    if (myEditor.getEditingMode() ==
                            EditingMode.CREATE_NEW_INSTANCE) {
                        VisibilityScope visScope = (VisibilityScope)myEditor.
                                getLookup().lookup(VisibilityScope.class);
                        if (visScope != null) {
                            BaseScope scope = visScope.getClosestScope();
                            vc = scope.getVariableContainer();
                        }
//                    } else {
// A VetoException will be thorown if the name isn't unique.                        
//                        Variable var = myEditor.getModelNode().getReference();
//                        if (var != null) {
//                            BpelContainer container = var.getParent();
//                            if (container instanceof VariableContainer) {
//                                vc = (VariableContainer)container;
//                            }
//                        }
//                    }
                        //
                        if (vc != null) {
                            String varName = fldVariableName.getText();
                            Variable[] variables = vc.getVariables();
                            for (Variable variable : variables) {
                                if (varName.equals( variable.getName())){
                                    addReasonKey("ERR_NOT_UNIQUE_VARIABLE_NAME"); //NOI18N
                                }
                            }
                        }
                    }
                    //
                    return isReasonsListEmpty();
                }
                
            };
        }
        return myValidator;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        bngVariableMetaType = new javax.swing.ButtonGroup();
        lblVariableName = new javax.swing.JLabel();
        fldVariableName = new javax.swing.JTextField();
        pnlTypeChooser = new TypeChooserPanel();
        lblErrorMessage = new javax.swing.JLabel();

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_CreateNewVariableTitle"));
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_CreateNewVariableTitle"));
        lblVariableName.setLabelFor(fldVariableName);
        lblVariableName.setText(org.openide.util.NbBundle.getMessage(FormBundle.class, "LBL_VariableName"));
        lblVariableName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_LBL_VariableName"));
        lblVariableName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_LBL_VariableName"));

        pnlTypeChooser.setFocusable(false);
        org.jdesktop.layout.GroupLayout pnlTypeChooserLayout = new org.jdesktop.layout.GroupLayout(pnlTypeChooser);
        pnlTypeChooser.setLayout(pnlTypeChooserLayout);
        pnlTypeChooserLayout.setHorizontalGroup(
            pnlTypeChooserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 394, Short.MAX_VALUE)
        );
        pnlTypeChooserLayout.setVerticalGroup(
            pnlTypeChooserLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 308, Short.MAX_VALUE)
        );

        lblErrorMessage.setForeground(new java.awt.Color(255, 0, 0));
        lblErrorMessage.setFocusable(false);
        lblErrorMessage.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSN_ErrorLabel"));
        lblErrorMessage.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FormBundle.class,"ACSD_ErrorLabel"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlTypeChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblErrorMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(lblVariableName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fldVariableName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblVariableName)
                    .add(fldVariableName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlTypeChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblErrorMessage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bngVariableMetaType;
    private javax.swing.JTextField fldVariableName;
    private javax.swing.JLabel lblErrorMessage;
    private javax.swing.JLabel lblVariableName;
    private javax.swing.JPanel pnlTypeChooser;
    // End of variables declaration//GEN-END:variables
}
