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

package org.netbeans.modules.php.editor.nav;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.php.editor.nav.SemiAttribute.AttributedElement;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.ClassInstanceCreation;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;

/**
 *
 * @author Jan Lahoda
 */
public class NavUtils {

    public static List<ASTNode> underCaret(CompilationInfo info, final int offset) {
        class Result extends Error {
            private Stack<ASTNode> result;
            public Result(Stack<ASTNode> result) {
                this.result = result;
            }
            @Override
            public Throwable fillInStackTrace() {
                return this;
            }
        }
        try {
            new DefaultVisitor() {
                private Stack<ASTNode> s = new Stack<ASTNode>();
                @Override
                public void scan(ASTNode node) {
                    if (node == null) {
                        return ;
                    }
                    
                    if (node.getStartOffset() <= offset && offset <= node.getEndOffset()) {
                        s.push(node);
                        super.scan(node);
                        throw new Result(s);
                    }
                }
            }.scan(Utils.getRoot(info));
        } catch (Result r) {
            return new LinkedList<ASTNode>(r.result);
        }
        
        return Collections.emptyList();
    }
    
    public static AttributedElement findElement(CompilationInfo info, List<ASTNode> path, int offset, SemiAttribute a) {
        if (path.size() == 0) {
            return null;
        }

        path = new LinkedList<ASTNode>(path);

        Collections.reverse(path);

        AttributedElement result = null;

        for (final ASTNode leaf : path) {
            if (leaf instanceof Variable) {
                result = a.getElement(leaf);
                continue;
            }

            if (leaf instanceof FunctionInvocation) {
                FunctionInvocation i = (FunctionInvocation) leaf;

                if (i.getFunctionName().getStartOffset() <= offset && offset <= i.getFunctionName().getEndOffset()) {
                    return a.getElement(leaf);
                }
            }

            if (leaf instanceof ClassInstanceCreation) {
                return a.getElement(leaf);
            }

            if (result != null) {
                return result;
            }
        }

        return null;
    }
    
//    public static AttributedElement findElement(CompilationInfo info, final int offset, SemiAttribute[] out) {
//        List<ASTNode> path = NavUtils.underCaret(info, offset);
//        
//        if (path.size() == 0) {
//            return null;
//        }
//        
//        path = new LinkedList<ASTNode>(path);//very defensive, for case underCaret becomes API
//        
//        Collections.reverse(path);
//        
//        AttributedElement result = null;
//        
//        for (final ASTNode leaf : path) {
//            if (leaf instanceof Variable) {
//                SemiAttribute semiAttribute = SemiAttribute.semiAttribute(info, out == null ? offset : -1);
//                
//                if (out != null) {
//                    out[0] = semiAttribute;
//                }
//
//                result = semiAttribute.getElement(leaf);
//                continue;
//            }
//
//            if (leaf instanceof FunctionInvocation) {
//                FunctionInvocation i = (FunctionInvocation) leaf;
//                
//                if (i.getFunctionName().getStartOffset() <= offset && offset <= i.getFunctionName().getEndOffset()) {
//                    SemiAttribute semiAttribute = SemiAttribute.semiAttribute(info, out == null ? offset : -1);
//
//                    if (out != null) {
//                        out[0] = semiAttribute;
//                    }
//
//                    return semiAttribute.getElement(leaf);
//                }
//            }
//            
//            if (leaf instanceof ClassInstanceCreation) {
//                SemiAttribute semiAttribute = SemiAttribute.semiAttribute(info, out == null ? offset : -1);
//
//                if (out != null) {
//                    out[0] = semiAttribute;
//                }
//
//                return semiAttribute.getElement(leaf);
//            }
//            
//            if (result != null) {
//                return result;
//            }
//        }
//        
//        return null;
//    }
}
