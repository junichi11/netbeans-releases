/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx2.editor.parser;

import org.netbeans.modules.javafx2.editor.completion.model.FxInstance;
import org.netbeans.modules.javafx2.editor.completion.model.FxInstanceCopy;
import org.netbeans.modules.javafx2.editor.completion.model.FxTreeUtilities;
import org.netbeans.modules.javafx2.editor.completion.model.FxNewInstance;
import org.netbeans.modules.javafx2.editor.completion.model.FxNode;
import org.netbeans.modules.javafx2.editor.completion.model.FxNodeVisitor;
import org.netbeans.modules.javafx2.editor.completion.model.FxReference;
import org.netbeans.modules.javafx2.editor.completion.model.FxScriptFragment;
import org.netbeans.modules.javafx2.editor.completion.model.ImportDecl;
import org.netbeans.modules.javafx2.editor.completion.model.LanguageDecl;
import org.netbeans.modules.javafx2.editor.completion.model.MapProperty;
import org.netbeans.modules.javafx2.editor.completion.model.PropertySetter;
import org.netbeans.modules.javafx2.editor.completion.model.StaticProperty;
import org.netbeans.modules.javafx2.editor.completion.model.TextPositions;

/**
 *
 * @author sdedic
 */
public class PrintVisitor extends FxNodeVisitor.ModelTraversal {
    public StringBuilder out = new StringBuilder();
    private int indent = 0;
    private FxTreeUtilities trees;

    public PrintVisitor(FxTreeUtilities nodes) {
        this.trees = nodes;
    }

    @Override
    protected void scan(FxNode node) {
        indent += 2;
        if (node != null) {
            TextPositions pos = trees.positions(node);
            String s = String.format("[%d - %d]  ", pos.getStart(), pos.getEnd());
            out.append(s).append(PADDING, 0, 16 - s.length());
        }
        super.scan(node);
        indent -= 2;
    }

    private StringBuilder i() {
        for (int i = 0; i < indent; i++) {
            out.append(" ");
        }
        return out;
    }

    @Override
    public void visitCopy(FxInstanceCopy decl) {
        i().append(String.format("copy: source=%s, id=%s\n", decl.getBlueprintId(), decl.getId()));
        super.visitCopy(decl); 
    }

    @Override
    public void visitReference(FxReference decl) {
        i().append(String.format("reference: source=%s\n", decl.getTargetId()));
        super.visitReference(decl); 
    }

    @Override
    public void visitInstance(FxNewInstance decl) {
        i().append(String.format("instance: id=%s, className=%s\n", decl.getId(), decl.getTypeName()));
        super.visitInstance(decl);
    }

    @Override
    public void visitPropertySetter(PropertySetter p) {
        i().append(String.format("setter: name=%s, content=%s\n", p.getPropertyName(), p.getContent()));
        super.visitPropertySetter(p);
    }

    @Override
    public void visitLanguage(LanguageDecl decl) {
        i().append(String.format("language: %s\n", decl.getLanguage()));
        super.visitLanguage(decl);
    }

    @Override
    public void visitImport(ImportDecl decl) {
        i().append(String.format("import: name=%s, wildcard=%b\n", decl.getImportedName(), decl.isWildcard()));
        super.visitImport(decl);
    }

    @Override
    public void visitMapProperty(MapProperty p) {
        i().append(String.format("map: name=%s, content=%s\n", p.getPropertyName(), p.getValueMap()));
        super.visitMapProperty(p);
    }

    @Override
    public void visitStaticProperty(StaticProperty p) {
        i().append(String.format("attached: name=%s, source=%s, content=%s\n", p.getPropertyName(), p.getSourceClassName(), p.getContent()));
        super.visitStaticProperty(p);
    }
    
    public void visitScript(FxScriptFragment f) {
        i().append(String.format("script: src=%s, len=%d", f.getSourcePath(), (f.hasContent() ? f.getContent().length() : -1)));
    }
    
    private static final String PADDING = "                ";
}
