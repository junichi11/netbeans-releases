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
package org.netbeans.modules.php.editor.verification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.astnodes.*;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class PHP54UnhandledError extends DefaultVisitor {

    private FileObject fileObject;
    private List<PHPVerificationError> errors = new ArrayList<PHPVerificationError>();
    private static final String BINARY_PREFIX = "0b"; //NOI18N
    private boolean checkAnonymousObjectVariable;

    public PHP54UnhandledError(FileObject fobj) {
        this.fileObject = fobj;
    }

    public static  boolean appliesTo(FileObject fobj) {
        return !CodeUtils.isPhp_54(fobj);
    }

    public Collection<PHPVerificationError> getErrors() {
        return Collections.unmodifiableCollection(errors);
    }

    @Override
    public void visit(TraitDeclaration node) {
        Identifier name = node.getName();
        if (name != null) {
            createError(name);
        } else {
            createError(node);
        }
    }

    @Override
    public void visit(UseTraitStatement node) {
        createError(node);
    }

    @Override
    public void visit(MethodInvocation node) {
        checkAnonymousObjectVariable = true;
        super.visit(node);
        checkAnonymousObjectVariable = false;
    }

    @Override
    public void visit(FieldAccess node) {
        checkAnonymousObjectVariable = true;
        super.visit(node);
        checkAnonymousObjectVariable = false;
    }

    @Override
    public void visit(AnonymousObjectVariable node) {
        if (checkAnonymousObjectVariable) {
            createError(node);
        }
    }

    @Override
    public void visit(DereferencedArrayAccess node) {
        createError(node);
    }

    @Override
    public void visit(Scalar node) {
        if (node.getScalarType().equals(Scalar.Type.REAL) && node.getStringValue().startsWith(BINARY_PREFIX)) {
            createError(node);
        }
    }

    @Override
    public void visit(StaticMethodInvocation node) {
        Expression name = node.getMethod().getFunctionName().getName();
        if (name instanceof ReflectionVariable) {
            createError(name);
        }
    }

    private  void createError(int startOffset, int endOffset){
        PHPVerificationError error = new PHP54VersionError(fileObject, startOffset, endOffset);
        errors.add(error);
    }

    private void createError(ASTNode node){
        createError(node.getStartOffset(), node.getEndOffset());
        super.visit(node);
    }

    private class PHP54VersionError extends PHPVerificationError {

        private static final String KEY = "Php.Version.54"; //NOI18N

        private PHP54VersionError(FileObject fileObject, int startOffset, int endOffset) {
            super(fileObject, startOffset, endOffset);
        }

        @Override
        @Messages("CheckPHP54VerDisp=Language feature not compatible with PHP version indicated in project settings")
        public String getDisplayName() {
            return Bundle.CheckPHP54VerDisp();
        }

        @Override
        @Messages("CheckPHP54VerDesc=Detect language features not compatible with PHP version indicated in project settings")
        public String getDescription() {
            return Bundle.CheckPHP54VerDesc();
        }

        @Override
        public String getKey() {
            return KEY;
        }

    }

}
