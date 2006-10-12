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

import org.netbeans.modules.xml.schema.model.Pattern;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.visitor.SchemaVisitor;
import org.w3c.dom.Element;

/**
 * This implements interface represents xs:pattern used to define regular expressions over
 * the lexical space.
 *
 * @author nn136682
 */
public class PatternImpl extends SchemaComponentImpl implements Pattern {

    public PatternImpl(SchemaModelImpl model) {
        this(model,createNewComponent(SchemaElements.PATTERN, model));
    }
    
    /** Creates a new instance of PatternImpl */
    public PatternImpl(SchemaModelImpl model, Element e) {
        super(model, e);
    
    }

	/**
	 *
	 *
	 */
	public Class<? extends SchemaComponent> getComponentType() {
		return Pattern.class;
	}
    
    public void accept(SchemaVisitor visitor) {
        visitor.visit(this);
    }
    

    public String getValue() {
        return getAttribute(SchemaAttributes.VALUE);
    }
	
    public void setValue(String value) {
        setAttribute(VALUE_PROPERTY, SchemaAttributes.VALUE, value);
    }
}

