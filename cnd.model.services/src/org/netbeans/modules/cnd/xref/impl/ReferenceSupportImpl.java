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

package org.netbeans.modules.cnd.xref.impl;

import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmIdentifiable;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmOffsetable;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceKind;

/**
 *
 * @author Vladimir Voskresensky
 */
public class ReferenceSupportImpl {
    public CsmReference createObjectReference(CsmOffsetable obj) {
        int start = getStartRefenceOffset(obj);
        int end = getEndReferenceOffset(obj);
        CsmUID<CsmObject> targetUID = getUID((CsmObject)obj);
        CsmUID<CsmFile> targetFileUID = getUID(obj.getContainingFile());
        CsmReferenceKind kind = getObjectKind(obj);
        return new ObjectReferenceImpl(targetUID, targetFileUID, kind, start, end);
    }

    private CsmReferenceKind getObjectKind(CsmObject obj) {
        CsmReferenceKind kind = CsmReferenceKind.UNKNOWN;
        if (CsmKindUtilities.isDeclaration(obj)) {
            kind = CsmReferenceKind.DECLARATION;
            if (CsmKindUtilities.isFunctionDefinition(obj) ||
                    CsmKindUtilities.isVariableDefinition(obj)) {
                kind = CsmReferenceKind.DEFINITION;
            }
        }
        return kind;
    }
    
    private int getStartRefenceOffset(CsmOffsetable obj) {
        return obj.getStartOffset();
    }
    
    private int getEndReferenceOffset(CsmOffsetable obj) {
        int end = obj.getEndOffset();
        if (CsmKindUtilities.isClass(obj)) {
            end = ((CsmClass) obj).getLeftBracketOffset();
        } else if (CsmKindUtilities.isFunctionDefinition(obj)) {
            if (((CsmFunctionDefinition) obj).getBody() != null) {
                end = ((CsmFunctionDefinition) obj).getBody().getStartOffset();
            }
        }
        return end;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends CsmObject> CsmUID<T> getUID(T element) {
        CsmUID<T> uid = null;
        if (CsmKindUtilities.isIdentifiable(element)) {
            uid = ((CsmIdentifiable<T>)element).getUID();
            boolean checkAssert = false;
            assert checkAssert = true;
            if (checkAssert && (uid.getObject() == null)) {
                System.err.println("UID " + uid + "can't return object " + element);
                uid = null;
            }
        } 
        if (uid == null) {
            uid = new SelfUID(element);
        }
        return uid;
    }
    
    private static final class SelfUID<T> implements CsmUID<T> {
        private final T element;
        SelfUID(T element) {
            this.element = element;
        }
        public T getObject() {
            return this.element;
        }
    }    
}
