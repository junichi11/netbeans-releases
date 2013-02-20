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
package org.netbeans.modules.javadoc.hints;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocTreePath;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.java.source.DocTreePathHandle;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.java.hints.JavaFix;
import org.openide.util.NbBundle;
import static org.netbeans.modules.javadoc.hints.Bundle.*;

/**
 *
 * @author Jan Pokorsky
 * @author Ralph Benjamin Ruijs
 */
@NbBundle.Messages({"# {0} - @param name", "MISSING_PARAM_HINT=Add @param {0} tag",
    "# {0} - @param name", "MISSING_TYPEPARAM_HINT=Add @param {0} tag",
    "MISSING_RETURN_HINT=Add @return tag",
    "# {0} - Throwable name", "MISSING_THROWS_HINT=Add @throws {0} tag",
    "MISSING_DEPRECATED_HINT=Add @deprecated tag"})
abstract class AddTagFix extends JavaFix {

    private String message;
    private final DocTreePathHandle dtph;
    private final int index;

    private AddTagFix(DocTreePathHandle dtph, String message, int index) {
        super(dtph.getTreePathHandle());
        this.dtph = dtph;
        this.message = message;
        this.index = index;
    }
    
    @Override
    protected void performRewrite(TransformationContext ctx) throws Exception {
        WorkingCopy javac = ctx.getWorkingCopy();
        DocTreePath path = dtph.resolve(javac);
        DocCommentTree docComment = path.getDocComment();
        TreeMaker make = javac.getTreeMaker();
        TagComparator comparator = new TagComparator();
        final List<DocTree> blockTags = new LinkedList<DocTree>();
        DocTree newTree = getNewTag(make);
        
        boolean added = false;
        int count = 0;
        for (DocTree docTree : docComment.getBlockTags()) {
            if (!added && comparator.compare(newTree, docTree) == TagComparator.HIGHER) {
                blockTags.add(newTree);
                added = true;
            }
            if (!added && comparator.compare(newTree, docTree) == TagComparator.EQUAL &&
                    index == count++) {
                blockTags.add(newTree);
                added = true;
            }
            blockTags.add(docTree);
        }
        if (!added) {
            blockTags.add(newTree);
        }
        
        DocCommentTree newDoc = make.DocComment(docComment, docComment.getFirstSentence(), docComment.getBody(), blockTags);
        Tree tree = ctx.getPath().getLeaf();
        javac.rewrite(tree, docComment, newDoc);
    }
    
    protected abstract DocTree getNewTag(TreeMaker make);

    public static JavaFix createAddParamTagFix(DocTreePathHandle dtph, final String name, final boolean isTypeParam, int index) {
        return new AddTagFix(dtph, isTypeParam? MISSING_TYPEPARAM_HINT("<" + name + ">"):MISSING_PARAM_HINT(name), index) {
            @Override
            protected DocTree getNewTag(TreeMaker make) {
                return make.Param(isTypeParam, make.DocIdentifier(name), Collections.EMPTY_LIST);
            }
        };
    }

    public static JavaFix createAddReturnTagFix(DocTreePathHandle dtph) {
        return new AddTagFix(dtph, MISSING_RETURN_HINT(), -1) {
            @Override
            protected DocTree getNewTag(TreeMaker make) {
                return make.DocReturn(Collections.EMPTY_LIST);
            }
        };
    }

    public static JavaFix createAddThrowsTagFix(DocTreePathHandle dtph, final String fqn, int throwIndex) {
        return new AddTagFix(dtph, MISSING_THROWS_HINT(fqn), throwIndex) {
            @Override
            protected DocTree getNewTag(TreeMaker make) {
                return make.Throws(make.Reference(fqn), Collections.EMPTY_LIST);
            }
        };
    }

    public static JavaFix createAddDeprecatedTagFix(DocTreePathHandle dtph) {
        return new AddTagFix(dtph, MISSING_DEPRECATED_HINT(), -1) {
            @Override
            protected DocTree getNewTag(TreeMaker make) {
                return make.Deprecated(Collections.EMPTY_LIST);
            }
        };
    }
    
    
    /**
     * Orders tags as follows
     * <ul>
     * <li>@author (classes and interfaces only, required)</li>
     * <li>@version (classes and interfaces only, required. See footnote 1)</li>
     * <li>@param (methods and constructors only)</li>
     * <li>@return (methods only)</li>
     * <li>@exception (</li>
     * <li>@throws is a synonym added in Javadoc 1.2)</li>
     * <li>@see</li>
     * <li>@since</li>
     * <li>@serial (or @serialField or @serialData)</li>
     * <li>@deprecated (see How and When To Deprecate APIs)</li>
     * </ul>
     */
    private static class TagComparator implements Comparator<DocTree> {
        
        private final static int HIGHER = -1;
        private final static int EQUAL = 0;
        private final static int LOWER = 1;

        @Override
        public int compare(DocTree t, DocTree t1) {
            if(t.getKind() == t1.getKind()) {
                if(t.getKind() == DocTree.Kind.PARAM) {
                    ParamTree p = (ParamTree) t;
                    ParamTree p1 = (ParamTree) t1;
                    if(p.isTypeParameter() && !p1.isTypeParameter()) {
                        return HIGHER;
                    } else if(!p.isTypeParameter() && p1.isTypeParameter()) {
                        return LOWER;
                    }
                }
                return EQUAL;
            }
            switch(t.getKind()) {
                case AUTHOR:
                    return HIGHER;
                case VERSION:
                    if(t1.getKind() == DocTree.Kind.AUTHOR) {
                        return LOWER;
                    }
                    return HIGHER;
                case PARAM:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION) {
                        return LOWER;
                    }
                    return HIGHER;
                case RETURN:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM) {
                        return LOWER;
                    }
                    return HIGHER;
                case EXCEPTION:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM
                            || t1.getKind() == DocTree.Kind.RETURN) {
                        return LOWER;
                    }
                    return HIGHER;
                case THROWS:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM
                            || t1.getKind() == DocTree.Kind.RETURN
                            || t1.getKind() == DocTree.Kind.EXCEPTION) {
                        return LOWER;
                    }
                    return HIGHER;
                case SEE:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM
                            || t1.getKind() == DocTree.Kind.RETURN
                            || t1.getKind() == DocTree.Kind.EXCEPTION
                            || t1.getKind() == DocTree.Kind.THROWS) {
                        return LOWER;
                    }
                    return HIGHER;
                case SINCE:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM
                            || t1.getKind() == DocTree.Kind.RETURN
                            || t1.getKind() == DocTree.Kind.EXCEPTION
                            || t1.getKind() == DocTree.Kind.THROWS
                            || t1.getKind() == DocTree.Kind.SEE) {
                        return LOWER;
                    }
                    return HIGHER;
                case SERIAL:
                case SERIAL_DATA:
                case SERIAL_FIELD:
                    if(t1.getKind() == DocTree.Kind.AUTHOR
                            || t1.getKind() == DocTree.Kind.VERSION
                            || t1.getKind() == DocTree.Kind.PARAM
                            || t1.getKind() == DocTree.Kind.RETURN
                            || t1.getKind() == DocTree.Kind.EXCEPTION
                            || t1.getKind() == DocTree.Kind.THROWS
                            || t1.getKind() == DocTree.Kind.SEE
                            || t1.getKind() == DocTree.Kind.SINCE) {
                        return LOWER;
                    }
                    return HIGHER;
                case DEPRECATED:
                    if(t1.getKind() == DocTree.Kind.UNKNOWN_BLOCK_TAG) {
                        return HIGHER;
                    }
                    return LOWER;
                case UNKNOWN_BLOCK_TAG:
                    return LOWER;
            }
            return LOWER;
        }
        
    }
    
    @Override
    public String getText() {
        return message;
    }
}
