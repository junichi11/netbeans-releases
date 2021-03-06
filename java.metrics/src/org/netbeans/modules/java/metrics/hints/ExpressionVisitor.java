/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.metrics.hints;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationInfo;

/**
 * Visitor that collects subexpressions with more than X operands. The trick here is flag passing
 * between the visit* methods and their subtrees. A visit method for numeric/boolean computation sets up a flag {@link #currentMatches},
 * which marks the (part of) subtree as matching the type query (numeric, logical). The generic {@link #scan} method 
 * maintains that flag on the level, but flips it to false before diving deeper. If the visit* method for the
 * concrete subexpression type does not raise the flag, the subexpression's subtree will not be counted in.
 * <p/>
 * Inside subtrees that define numeric/logical expression, operators are counted and the root of a subexpression that
 * exceeds the limit is put into the {@link #errorNodes} list.
 * <p/>
 * All subexpressions (none is part of the other) in the scanned tree that exceeds the limit of operators will be
 * reported.
 * 
 * @author sdedic
 */
class ExpressionVisitor extends TreePathScanner<Object, Object> {
    private static final EnumSet<Tree.Kind> NUMERIC_OPERATORS = EnumSet.of(
            Tree.Kind.BITWISE_COMPLEMENT, Tree.Kind.LEFT_SHIFT, Tree.Kind.RIGHT_SHIFT, Tree.Kind.UNSIGNED_RIGHT_SHIFT, 
            Tree.Kind.DIVIDE, Tree.Kind.MINUS, Tree.Kind.MULTIPLY, Tree.Kind.PLUS, Tree.Kind.REMAINDER, 
            Tree.Kind.UNARY_MINUS, Tree.Kind.UNARY_PLUS, Tree.Kind.AND, Tree.Kind.OR, 
            Tree.Kind.PREFIX_INCREMENT, Tree.Kind.PREFIX_DECREMENT, Tree.Kind.POSTFIX_INCREMENT, 
            Tree.Kind.POSTFIX_DECREMENT, Tree.Kind.XOR);
    private static final EnumSet<Tree.Kind> LOGICAL_OPERATORS = EnumSet.of(Tree.Kind.LOGICAL_COMPLEMENT, 
            Tree.Kind.OR, Tree.Kind.AND, Tree.Kind.XOR);
    
    /**
     * This flag is set to true by visit* methods and is read/reset by scan() method. It is a tunnel through
     * super.visit() methods.
     */
    private boolean currentMatches = false;

    /**
     * The count of operators in the current expression subtree. Raised after the subtree is processed.
     */
    private int count;
    
    /**
     * The operator limit
     */
    private final int threshold;
    
    /**
     * The current root of the erroneous subexpression. Will be updated as the processing goes up the tree 
     * up to the non-expression Tree node.
     */
    private TreePath errorNode;
    
    /**
     * Final result - subtree that breach the operator limit.
     */
    private List<TreePath> errorNodes = Collections.emptyList();
    
    /**
     * For each TreePath from {@link #errorNodes}, records the actual number of operators in the subexpression.
     */
    private List<Integer> nodeOperands = Collections.emptyList();
    
    private final CompilationInfo info;
    
    /**
     * Eligible operators
     */
    private final EnumSet<Tree.Kind> watchOperators;
    
    /**
     * If true, logical expressions are scanned. If false, numerical.
     */
    private final boolean logical;

    public ExpressionVisitor(CompilationInfo info, boolean logical, int threshold) {
        this.info = info;
        this.threshold = threshold;
        if (logical) {
            this.logical = true;
            this.watchOperators = LOGICAL_OPERATORS;
        } else {
            this.logical = false;
            this.watchOperators = NUMERIC_OPERATORS;
        }
    }
    
    public List<TreePath> getErrorPaths() {
        return this.errorNodes;
    }
    
    public int getNodeOperands(TreePath n) {
        int index = errorNodes.indexOf(n);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return nodeOperands.get(index);
    }
    
    private void addError(TreePath n) {
        if (errorNodes.isEmpty()) {
            errorNodes = new ArrayList<>(3);
            nodeOperands = new ArrayList<>(3);
        }
        errorNodes.add(n);
        nodeOperands.add(count);
    }

    @Override
    public Object scan(Tree tree, Object p) {
        if (currentMatches) {
            currentMatches = false;
            Object o = super.scan(tree, p);
            this.currentMatches = true;
            return o;
        } else {
            int saveCount = count;
            TreePath node = errorNode;
            count = 0;
            errorNode = null;
            Object o = super.scan(tree, p);
            if (errorNode != null) {
                addError(errorNode);
            }
            this.currentMatches = false;
            this.errorNode = node;
            this.count = saveCount;
            return o;
        }
    }

    @Override
    public Object visitParenthesized(ParenthesizedTree node, Object p) {
        boolean b = isCorrectType(node);
        currentMatches = b;
        Object o = super.visitParenthesized(node, p);
        increment(node, b);
        return o;
    }

    @Override
    public Object visitUnary(UnaryTree node, Object p) {
        boolean b = isCorrectType(node);
        currentMatches = b;
        Object o = super.visitUnary(node, p);
        increment(node, b);
        return o;
    }

    @Override
    public Object visitBinary(BinaryTree node, Object p) {
        boolean b = isCorrectType(node);
        currentMatches = b;
        Object o = super.visitBinary(node, p);
        increment(node, b);
        return o;
    }

    private void increment(Tree node, boolean b) {
        if (!b) {
            return;
        }
        if (watchOperators.contains(node.getKind())) {
            if (++count > threshold) {
                errorNode = getCurrentPath();
            }
        }
    }

    /**
     * In the numeric case, accept both primitives and boxed types. Each boxed type is
     * convertible to the appropriate primitive, and all primitive types are assignable to double.
     */
    private boolean isCorrectType(ExpressionTree t) {
        if (currentMatches) {
            return true;
        }
        TypeMirror tm = info.getTrees().getTypeMirror(getCurrentPath());
        switch (tm.getKind()) {
            case BOOLEAN:
                return logical;
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                return !logical;
            case DECLARED:
                // can be converted to primitive
                return info.getTypes().isAssignable(tm, info.getTypes().getPrimitiveType(TypeKind.DOUBLE));
            default:
                return false;
        }
    }
    
}
