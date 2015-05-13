/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.cnd.debugger.common2.debugger.options;

import org.netbeans.modules.cnd.debugger.common2.debugger.api.EngineType;
import org.netbeans.modules.cnd.debugger.common2.debugger.api.EngineTypeManager;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Collection;

import org.openide.nodes.PropertySupport;

public class EngineNodeProp extends PropertySupport<EngineType> {
    private EngineProfile profile;
    private EngineEditor engineEditor;

    public EngineNodeProp(EngineProfile profile) {
	super("EngineType", // NOI18N
	      EngineType.class,
	      Catalog.get("EnginePropDisplayName"), // NOI18N
	      Catalog.get("EnginePropTT"), // NOI18N
	      true,
	      true);
	this.profile = profile;
    }

    @Override
    public EngineType getValue() {
	return profile.getEngineType();
    }

    @Override
    public void setValue(EngineType v) {
	profile.setEngineType(v);
    } 

    @Override
    public PropertyEditor getPropertyEditor() {
	// NB provides a default EnumPropertyEditor but it only works with 
	// enum names and won't work if we override toString().
	if (engineEditor == null)
	    engineEditor = new EngineEditor();
	return engineEditor;
    }

    @Override
    public Object getValue(String attributeName) {
	return super.getValue(attributeName);
    }

    private class EngineEditor extends PropertyEditorSupport {

	public EngineEditor() {
	}

        @Override
	public void setAsText(String text) {
            if (IpeUtils.isEmpty(text)) {
                EngineEditor.super.setValue(null);
            } else {
                EngineEditor.super.setValue(EngineTypeManager.getEngineTypeByDisplayName(text));
            }
	}

        @Override
	public String getAsText() {
	    return profile.getEngineType().getDisplayName();
	}

        @Override
	public String[] getTags() {
            Collection<EngineType> engineTypes = EngineTypeManager.getEngineTypes(true);

	    String[] tags = new String[engineTypes.size()];
            int i=0;
            for (EngineType engine : engineTypes) {
                tags[i++] = engine.getDisplayName();
            }
	    return tags;
	}
    }
}
