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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.wsdl.model.spi;

import javax.xml.namespace.QName;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.impl.*;
import org.netbeans.modules.xml.xam.Nameable;
import org.w3c.dom.Element;

/**
 *
 * @author Nam Nguyen
 */
public abstract class NamedExtensibilityElementBase extends GenericExtensibilityElement implements Nameable<WSDLComponent>{
    
    /** Creates a new instance of NamedExtensibilityElementImpl */
    public NamedExtensibilityElementBase(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public void setName(String name) {
        setAttribute(NAME_PROPERTY, WSDLAttribute.NAME, name);
    }

    public String getName() {
        return getAttribute(WSDLAttribute.NAME);
    }
}
