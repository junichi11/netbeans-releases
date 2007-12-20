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
package org.netbeans.modules.xml.wsdl.ui.api.property;

import javax.xml.namespace.QName;

import org.netbeans.modules.xml.wsdl.model.ExtensibilityElement;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.ui.actions.ActionHelper;
import org.netbeans.modules.xml.wsdl.ui.netbeans.module.Utility;

public class DefaultElementOrTypeProvider implements ElementOrTypeProvider {

    private final ExtensibilityElement extensibilityElement;
    private final String typeAttributeName;
    private final String elementAttributeName;

    public DefaultElementOrTypeProvider(ExtensibilityElement extensibilityElement, String elementAttributeName, String typeAttributeName) {
        this.extensibilityElement = extensibilityElement;
        this.elementAttributeName = elementAttributeName;
        this.typeAttributeName = typeAttributeName;
    }

    public void setElementOrType(ElementOrType newValue) {
        if (newValue == null) {
            return;
        }
        
        ElementOrType oldValue = getElementOrType();
        
        if (oldValue != null && newValue.toString().equals(oldValue.toString())) return;
        
        if (newValue.getType() != null || newValue.getElement() != null) { 
            getModel().startTransaction();
            if (newValue.isElement()) {
                Utility.addSchemaImport(newValue.getElement(), getModel());
                Utility.addNamespacePrefix(newValue.getElement().getModel().getSchema(), extensibilityElement.getModel(), null);
                extensibilityElement.setAttribute(elementAttributeName, newValue.toString());
                extensibilityElement.setAttribute(typeAttributeName, null);
            } else {
                Utility.addSchemaImport(newValue.getType(), getModel());
                Utility.addNamespacePrefix(newValue.getType().getModel().getSchema(), extensibilityElement.getModel(), null);
                extensibilityElement.setAttribute(typeAttributeName, newValue.toString());
                extensibilityElement.setAttribute(elementAttributeName, null);
            }
            getModel().endTransaction();
            ActionHelper.selectNode(extensibilityElement);
        }
    }

    public ElementOrType getElementOrType() {
        boolean isElement = false;
        String value = extensibilityElement.getAttribute(typeAttributeName);
        if (value == null) {
            value = extensibilityElement.getAttribute(elementAttributeName);
            if (value != null) {
                isElement = true;
            }
        } else {
            isElement = false;
        }
        
        
        if (extensibilityElement.getModel() == null) { //this seems to happen during deletion.
            return null;
        }
        
        if (value != null && value.trim().length() > 0) {
            String[] parts = value.split(":");
            if (parts != null && parts.length == 2) {
                String prefix = parts[0];
                String localPart = parts[1];
                String namespace = Utility.getNamespaceURI(prefix, extensibilityElement);
                return new ElementOrType(new QName(namespace, localPart, prefix), extensibilityElement.getModel(), isElement);
            }
            return new ElementOrType(new QName(value), extensibilityElement.getModel(), false);
        }
        
        return new ElementOrType(new QName(""), extensibilityElement.getModel(), false);
    }

    public WSDLModel getModel() {
        return extensibilityElement.getModel();
    }

}
