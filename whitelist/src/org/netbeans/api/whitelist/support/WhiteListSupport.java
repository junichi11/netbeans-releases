/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.api.whitelist.support;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.whitelist.WhiteListQuery;
import org.netbeans.api.whitelist.WhiteListQuery.Result;
import org.netbeans.api.whitelist.WhiteListQuery.WhiteList;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 * White list supporting methods.
 * @author Tomas Zezula
 * @since 1.2
 */
public final class WhiteListSupport {

    private static final Logger LOG = Logger.getLogger(WhiteListSupport.class.getName());

    private WhiteListSupport() {}

    /**
     * Utility method to check the given {@link CompilationUnitTree} for white list violations.
     * @param unit the {@link CompilationUnitTree} to be analyzed
     * @param whitelist the {@link WhiteList} to use to check the violations
     * @param trees the {@link Trees} service
     * @param cancel the cancel request. If the {@link Callable} returns true the check is canceled
     * and null is returned.
     * @return a {@link Map} of {@link Tree}s with attached white list violations or null when the
     * scan was canceled.
     */
    @CheckForNull
    public static Map<? extends Tree, ? extends WhiteListQuery.Result> getWhiteListViolations(
            @NonNull final CompilationUnitTree unit,
            @NonNull final WhiteListQuery.WhiteList whitelist,
            @NonNull final Trees trees,
            @NullAllowed final Callable<Boolean> cancel) {
        Parameters.notNull("tree", unit);           //NOI18N
        Parameters.notNull("whitelist", whitelist); //NOI18N
        Parameters.notNull("trees", trees);         //NOI18N
        final Map<Tree,WhiteListQuery.Result> result = new HashMap<Tree, Result>();
        final WhiteListScanner scanner = new WhiteListScanner(trees, whitelist, cancel);
        try {
            scanner.scan(unit, result);
            return result;
        } catch (WhiteListScanner.Cancel ce) {
            return null;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Private implementation">
    private static class WhiteListScanner extends TreePathScanner<Void, Map<Tree,WhiteListQuery.Result>> {

        private final Trees trees;
        private final Callable<Boolean> cancel;
        private final WhiteList whiteList;
        private final ArrayDeque<MethodInvocationTree> methodInvocation;

        WhiteListScanner(
            final Trees trees,
            final WhiteList whiteList,
            final Callable<Boolean> cancel) {
            this.trees = trees;
            this.whiteList = whiteList;
            this.cancel = cancel;
            methodInvocation = new ArrayDeque<MethodInvocationTree>();
        }

        @Override
        public Void visitMethod(MethodTree node, Map<Tree,WhiteListQuery.Result> p) {
            LOG.log(Level.FINEST, "Visiting {0}", node);    //NOI18N
            checkCancel();
            return super.visitMethod(node, p);
        }

        @Override
        public Void visitClass(ClassTree node, Map<Tree,WhiteListQuery.Result> p) {
            LOG.log(Level.FINEST, "Visiting {0}", node);    //NOI18N
            checkCancel();
            return super.visitClass(node, p);
        }

        @Override
        public Void visitIdentifier(IdentifierTree node, Map<Tree,WhiteListQuery.Result> p) {
            handleNode(node,p);
            return super.visitIdentifier(node, p);
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree node, Map<Tree,WhiteListQuery.Result> p) {
            handleNode(node,p);
            return super.visitMemberSelect(node, p);
        }

        @Override
        public Void visitNewClass(NewClassTree node, Map<Tree,WhiteListQuery.Result> p) {
            final Element e = trees.getElement(getCurrentPath());
            final WhiteListQuery.Result res;
            if (e != null && !(res=whiteList.check(ElementHandle.create(e),WhiteListQuery.Operation.USAGE)).isAllowed()) {
                p.put(node,res);
            }
            scan(node.getTypeArguments(), p);
            scan(node.getArguments(), p);
            scan(node.getClassBody(), p);
            return null;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree node, Map<Tree,WhiteListQuery.Result> p) {
            methodInvocation.offerFirst(node);
            super.visitMethodInvocation(node, p);
            methodInvocation.removeFirst();
            return null;
        }

        private void handleNode(
                final Tree node,
                final Map<Tree,WhiteListQuery.Result> p) {
            final Element e = trees.getElement(getCurrentPath());
            if (e == null) {
                return;
            }
            final ElementKind k = e.getKind();
            ElementHandle<?> eh = null;
            Tree toReport =  null;
            if (k.isClass() || k.isInterface()) {
                TypeMirror type = e.asType();
                if (type != null) {
                    type = findComponentType(type);
                    if (type.getKind() == TypeKind.DECLARED) {
                        eh = ElementHandle.create(((DeclaredType)type).asElement());
                        toReport=node;
                    }
                }
            } else if ((k == ElementKind.METHOD || k == ElementKind.CONSTRUCTOR) &&
                    !methodInvocation.isEmpty()) {
                toReport=methodInvocation.peekFirst();
                eh = ElementHandle.create(e);
            }
            final WhiteListQuery.Result res;
            if (toReport != null &&
                !(res=whiteList.check(eh,WhiteListQuery.Operation.USAGE)).isAllowed()) {
                    p.put(toReport,res);
            }
        }

        @NonNull
        private TypeMirror findComponentType(@NonNull TypeMirror type) {
            if (type.getKind() != TypeKind.ARRAY) {
                return type;
            }
            return findComponentType(((ArrayType)type).getComponentType());
        }

        private void checkCancel() {
            if (cancel != null) {
                Boolean vote = null;
                try {
                    vote = cancel.call();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
                if (vote  == Boolean.TRUE) {
                    throw new Cancel();
                }
            }
        }

        static final class Cancel extends RuntimeException {
        }
    }
    //</editor-fold>

}
