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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.api.model;

import java.util.List;

/**
 * Represents function or class template.
 *
 * Class template should be represesented by an instance,
 * which implements both CsmClass and CsmTemplate interfaces;
 *
 * Function template should be implemented by an instance,
 * which implements both CsmFunction and CsmTemplate interfaces;
 *
 * @author Vladimir Kvashin
 */
public interface CsmTemplate {

    List<CsmTemplateParameter> getTemplateParameters();

// This method is never used.
//    /**
//     * Gets a string that acts like a signature for function:
//     * a name followed by comma-separater list of parameter types
//     */
//    String getTemplateSignature(); 
    
    /*
     * Returns the name including template specialization part
     */
    String getDisplayName();
}
