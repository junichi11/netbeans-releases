/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ReferenceCustomizer.java
 *
 * Created on January 17, 2006, 10:26 PM
 */

package org.netbeans.modules.xml.schema.ui.nodes.categorized.customizer;

import java.io.IOException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.xml.schema.model.AttributeGroupReference;
import org.netbeans.modules.xml.schema.model.AttributeReference;
import org.netbeans.modules.xml.schema.model.ElementReference;
import org.netbeans.modules.xml.schema.model.GlobalAttribute;
import org.netbeans.modules.xml.schema.model.GlobalAttributeGroup;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.GlobalGroup;
import org.netbeans.modules.xml.schema.model.GroupReference;
import org.netbeans.modules.xml.schema.model.ReferenceableSchemaComponent;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.SchemaComponentReference;
import org.netbeans.modules.xml.schema.model.visitor.DefaultSchemaVisitor;
import org.netbeans.modules.xml.schema.ui.basic.editors.SchemaComponentSelectionPanel;
import org.netbeans.modules.xml.xam.ui.customizer.MessageDisplayer;
import org.openide.util.HelpCtx;

/**
 * Attribute customizer
 *
 * @author  Ajit Bhate
 */
public class ReferenceCustomizer<T extends SchemaComponent>
        extends AbstractSchemaComponentCustomizer<T>
        implements PropertyChangeListener {
    
    static final long serialVersionUID = 1L;
    
    /**
     * Creates new form ReferenceCustomizer
     */
    public ReferenceCustomizer(SchemaComponentReference<T> reference,
            SchemaComponent parent) {
        super(reference, parent);
        this.refVisitor = new ReferenceVisitor(reference.get());
        initComponents();
        reset();
    }
    
    public void applyChanges() throws IOException {
        if(canApply()) {
            setRef();
        }
    }
    
    public void reset() {
        initializeModel();
        initializeUISelection();
        if(hasParent()||getUIRef()==null) {
            setSaveEnabled(false);
        } else {
            setSaveEnabled(true);
        }
        setResetEnabled(false);
    }
    
    /**
     * Returns current type of the element
     */
    protected ReferenceableSchemaComponent getRef() {
        return refVisitor.getRef();
    }
    
    /**
     * initializes non ui elements
     */
    protected void initializeModel(){};
    
    /**
     * Changes the type of element
     *
     */
    protected void setRef(){
        refVisitor.setRef(getUIRef());
    };
    
    /**
     * selects model node on ui
     */
    private void selectModelNode() {
        refVisitor.getComponentSelectionPanel().removePropertyChangeListener(this);
        refVisitor.reset();
        refVisitor.getComponentSelectionPanel().addPropertyChangeListener(this);
    }
    
    /**
     *
     *
     */
    private void initializeTypeView() {
        refVisitor.getComponentSelectionPanel().addPropertyChangeListener(this);
        typePanel.add(refVisitor.getComponentSelectionPanel().getTypeSelectionPanel(),
                java.awt.BorderLayout.CENTER);
        refVisitor.getComponentSelectionPanel().getTypeSelectionPanel().
                getAccessibleContext().setAccessibleParent(typePanel);
    }
    
    private void initializeUISelection() {
        getMessageDisplayer().clear();
        selectModelNode();
    }
    
    /**
     * Retrieve the selected ref from the UI.
     *
     * @return  global reference from UI.
     */
    protected ReferenceableSchemaComponent getUIRef() {
        return refVisitor.getComponentSelectionPanel().getCurrentSelection();
    }
    
    /**
     * This method is called from within the constructor to
     * initializeTypeView the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        typePanel = new javax.swing.JPanel();
        mPanel = new javax.swing.JPanel();

        typePanel.setLayout(new java.awt.BorderLayout());

        initializeTypeView();

        mPanel.setLayout(new java.awt.BorderLayout());

        mPanel.add(getMessageDisplayer().getComponent(),java.awt.BorderLayout.CENTER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(typePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(typePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(mPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    ////////////////////////
    // event handling
    ////////////////////////
    /**
     * Since it implements PCL.
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(SchemaComponentSelectionPanel.PROPERTY_SELECTION)) {
            determineValidity();
        }
    }
    
    /**
     * Based on the current radio button status and node selections, decide
     * if we are in a valid state for accepting the user's input.
     */
    private void determineValidity() {
        getMessageDisplayer().clear();
        ReferenceableSchemaComponent ref = getUIRef();
        if(ref!=null) {
            if(getRef()==ref) {
                setResetEnabled(false);
                setSaveEnabled(!hasParent());
            } else {
                setResetEnabled(true);
                setSaveEnabled(true);
            }
        } else {
            setResetEnabled(getRef()!=null);
            setSaveEnabled(false);
            getMessageDisplayer().annotate(org.openide.util.NbBundle.
                    getMessage(ReferenceCustomizer.class,
                    "MSG_Reference_Error"),
                    MessageDisplayer.Type.ERROR);
        }
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ReferenceCustomizer.class);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel mPanel;
    public javax.swing.JPanel typePanel;
    // End of variables declaration//GEN-END:variables
    
    private transient ReferenceVisitor refVisitor;
    
    private static class ReferenceVisitor extends DefaultSchemaVisitor {
        private SchemaComponent component;
        private OPERATION operation;
        private static enum OPERATION {GET,SET};
        private ReferenceableSchemaComponent ref;
        private SchemaComponentSelectionPanel<? extends ReferenceableSchemaComponent>
                componentSelectionPanel;
        public ReferenceVisitor(SchemaComponent component) {
            this.component = component;
            this.operation = null;
            component.accept(this);
        }
        public void visit(ElementReference reference) {
            if(operation==null) {
                if(componentSelectionPanel==null) {
                    componentSelectionPanel = new SchemaComponentSelectionPanel
                            <GlobalElement>(reference.getModel(), GlobalElement.class,
                            (GlobalElement)getRef(), null, false);
                }
            } else if (operation==OPERATION.GET) {
                if(reference.getRef()!=null) {
                    ref = reference.getRef().get();
                }
            } else if (operation==OPERATION.SET) {
                reference.setRef(reference.getModel().getFactory().
                        createGlobalReference((GlobalElement)ref,
                        GlobalElement.class,reference));
            }
        }
        
        public void visit(AttributeReference reference) {
            if(operation==null) {
                componentSelectionPanel = new SchemaComponentSelectionPanel<GlobalAttribute>(
                        reference.getModel(), GlobalAttribute.class,
                        (GlobalAttribute)getRef(), null, false);
            } else if (operation==OPERATION.GET) {
                if(reference.getRef()!=null) {
                    ref = reference.getRef().get();
                }
            } else if (operation==OPERATION.SET) {
                reference.setRef(reference.getModel().getFactory().
                        createGlobalReference((GlobalAttribute)ref,
                        GlobalAttribute.class,reference));
            }
        }
        
        public void visit(GroupReference reference) {
            if(operation==null) {
                componentSelectionPanel = new SchemaComponentSelectionPanel<GlobalGroup>(
                        reference.getModel(), GlobalGroup.class,
                        (GlobalGroup)getRef(), null, false);
            } else if (operation==OPERATION.GET) {
                if(reference.getRef()!=null) {
                    ref = reference.getRef().get();
                }
            } else if (operation==OPERATION.SET) {
                reference.setRef(reference.getModel().getFactory().
                        createGlobalReference((GlobalGroup)ref,
                        GlobalGroup.class,reference));
            }
        }
        
        public void visit(AttributeGroupReference reference) {
            if(operation==null) {
                componentSelectionPanel = new SchemaComponentSelectionPanel<GlobalAttributeGroup>(
                        reference.getModel(), GlobalAttributeGroup.class,
                        (GlobalAttributeGroup)getRef(), null, false);
            } else if (operation==OPERATION.GET) {
                if(reference.getGroup()!=null) {
                    ref = reference.getGroup().get();
                }
            } else if (operation==OPERATION.SET) {
                reference.setGroup(reference.getModel().getFactory().
                        createGlobalReference((GlobalAttributeGroup)ref,
                        GlobalAttributeGroup.class,reference));
            }
        }
        
        public SchemaComponentSelectionPanel<? extends ReferenceableSchemaComponent>
                getComponentSelectionPanel() {
            return componentSelectionPanel;
        }
        
        public ReferenceableSchemaComponent getRef() {
            operation=OPERATION.GET;
            ref = null;
            component.accept(this);
            return ref;
        }
        
        public void setRef(ReferenceableSchemaComponent ref) {
            operation=OPERATION.SET;
            this.ref = ref;
            component.accept(this);
        }
        
        public void reset() {
        }
    }
}
