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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.0-06/22/2005 01:29 PM(ryans)-EA2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2005.09.05 at 07:05:33 PM MSD
//
package org.netbeans.modules.bpel.model.ext.editor.api;

import org.netbeans.modules.bpel.model.api.BpelContainer;
import org.netbeans.modules.bpel.model.api.ExtensionEntity;
import org.netbeans.modules.bpel.model.api.events.VetoException;
import org.netbeans.modules.bpel.model.api.references.SchemaReference;
import org.netbeans.modules.xml.schema.model.GlobalType;

/**
 * @author Vitaly Bychkov
 * @version 1.0
 */
public interface Cast extends ExtensionEntity, BpelContainer {
    String SOURCE = "source"; //NOI18N
    String PATH = "path"; //NOI18N
    String TYPE = "type"; //NOI18N

    Source getSource();
    void setSource(Source source);

    String getPath();
    void setPath(String path) throws VetoException;
    void removePath();

    SchemaReference<GlobalType> getType();
    void setType(SchemaReference<GlobalType> type);
    void removeType();
}
