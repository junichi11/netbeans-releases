/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.schema.model.impl;

import org.netbeans.modules.xml.schema.model.Notation;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.visitor.SchemaVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public class NotationImpl extends NamedImpl implements Notation {

    /** Creates a new instance of NotationImpl */
    public NotationImpl(SchemaModelImpl model) {
        this(model,createNewComponent(SchemaElements.DOCUMENTATION,model));
    }
    
    /**
     * Creates a new instance of DocumentationImpl
     */
    public NotationImpl(SchemaModelImpl model, Element el) {
        super(model, el);
    }

    public void setSystemIdentifier(String systemID) {
        setAttribute(SYSTEM_PROPERTY, SchemaAttributes.SYSTEM, systemID);
    }

    public String getSystemIdentifier() {
        return getAttribute(SchemaAttributes.SYSTEM);
    }

    public void setPublicIdentifier(String publicID) {
        setAttribute(SYSTEM_PROPERTY, SchemaAttributes.PUBLIC, publicID);
    }

    public String getPublicIdentifier() {
        return getAttribute(SchemaAttributes.PUBLIC);
    }

    public void accept(SchemaVisitor v) {
        v.visit(this);
    }

    public Class<? extends SchemaComponent> getComponentType() {
        return Notation.class;
    }
    
}
