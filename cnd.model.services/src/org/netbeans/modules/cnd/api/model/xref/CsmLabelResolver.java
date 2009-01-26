/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.api.model.xref;

import java.util.Collection;
import java.util.Set;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.openide.util.Lookup;

/**
 *
 * @author Alexander Simon
 */
public abstract class CsmLabelResolver {
    private static CsmLabelResolver DEFAULT = new Default();

    /**
     * Search for usage of referenced label.
     * Return collection of labels in the function.
     * Label name can be null. Service finds all labels in the function.
     * If label name not null then service searches exact label references.
     */
    public abstract Collection<CsmReference> getLabels(CsmFunctionDefinition referencedFunction,
            CharSequence label, Set<LabelKind> kinds);
    
    protected CsmLabelResolver() {
    }
    
    /**
     * Static method to obtain the CsmLabelResolver implementation.
     * @return the selector
     */
    public static synchronized CsmLabelResolver getDefault() {
        return DEFAULT;
    }
    
    public static enum LabelKind {
        Definiton,
        Reference,
    }
    /**
     * Implementation of the default selector
     */  
    private static final class Default extends CsmLabelResolver {
        private final Lookup.Result<CsmLabelResolver> res;
        Default() {
            res = Lookup.getDefault().lookupResult(CsmLabelResolver.class);
        }

        @Override
        public Collection<CsmReference> getLabels(CsmFunctionDefinition referencedFunction, CharSequence label, Set<LabelKind> kinds) {
            for (CsmLabelResolver selector : res.allInstances()) {
                return selector.getLabels(referencedFunction, label, kinds);
            }
            return null;
        }
    }
}
