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
package org.netbeans.modules.css.prep.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.css.lib.api.CssParserResult;
import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.css.lib.api.NodeType;
import org.netbeans.modules.css.lib.api.NodeUtil;
import org.netbeans.modules.css.lib.api.NodeVisitor;
import org.netbeans.modules.css.prep.CPType;

/**
 * naive temporary impl.
 *
 * @author marekfukala
 */
public class CPModel {

    public static String topLevelSnapshotMimetype; //set by unit tests
    private static final Collection<String> SASS_DIRECTIVES = Arrays.asList(new String[]{
        "@debug",
        "@each",
        "@extend",
        "@if",
        "@include",
        "@for",
        "@function",
        "@mixin",
        "@return",
        "@warn",
        "@while"
    });
    private CssParserResult result;
//    private Set<Variable> vars;
    private CPType cpType;

    public static CPModel getModel(CssParserResult result) {
        CPModel curr = result.getProperty(CPModel.class);
        if (curr == null) {
            curr = new CPModel(result);
            result.setProperty(CPModel.class, curr);
        }
        return curr;
    }

    private CPModel(CssParserResult result) {
        this.result = result;
    }

    public CPType getPreprocessorType() {
        if (cpType == null) {
            String fileMimetype = topLevelSnapshotMimetype != null
                    ? topLevelSnapshotMimetype //unit tests - fileless snapshots
                    : result.getSnapshot().getSource().getFileObject().getMIMEType();
            if (fileMimetype == null) {
                cpType = CPType.NONE;
            } else {
                if ("text/less".equals(fileMimetype)) { //NOI18N
                    cpType = CPType.LESS;
                } else if ("text/scss".equals(fileMimetype)) { //NOI18N
                    cpType = CPType.SCSS;
                } else {
                    cpType = CPType.NONE;
                }
            }
        }
        return cpType;
    }

    //xxx just per current file
    public Collection<Variable> getVariables() {
//        if (vars == null) {
        final Collection<Variable> vars = new HashSet<Variable>();
        NodeVisitor visitor = new NodeVisitor() {
            @Override
            public boolean visit(Node node) {
                switch (node.type()) {
                    case cp_variable:
                        vars.add(new Variable(node.image().toString(), new OffsetRange(node.from(), node.to())));
                        break;

//                        case error:
//                        case recovery:
//                            //skip errorneous content, do not visit
//                            break;

                    default:
                        //visit children
                        List<Node> children = node.children();
                        if (children != null) {
                            for (Node child : children) {
                                visit(child);
                            }
                        }
                }

                return false;
            }
        };
        visitor.visit(result.getParseTree());
//        }

        return vars;
    }

    //xxx just per current file
    //xxx caching
    public Collection<Element> getMixins() {
        final Collection<Element> mixins = new HashSet<Element>();
        NodeVisitor visitor = new NodeVisitor() {
            @Override
            public boolean visit(Node node) {
                switch (node.type()) {
                    case cp_mixin_declaration:
                        Node mixin_name = NodeUtil.getChildByType(node, NodeType.cp_mixin_name);
                        if (mixin_name != null) {
                            mixins.add(new Element(mixin_name.image().toString(), new OffsetRange(mixin_name.from(), mixin_name.to())));
                        }
                        break;
                    default:
                        //visit children
                        List<Node> children = node.children();
                        if (children != null) {
                            for (Node child : children) {
                                visit(child);
                            }
                        }
                        break;
                }
                return false;
            }
        };
        visitor.visit(result.getParseTree());
        return mixins;
    }
    
    

    public Collection<String> getMixinNames() {
        return getElementNames(getMixins());
    }
    
    public Collection<String> getVarNames() {
        return getElementNames(getVariables());
    }
    
    private static Collection<String> getElementNames(Collection<? extends Element> elements) {
        Collection<String> names = new HashSet<String>();
        for (Element e : elements) {
            names.add(e.getName().toString());
        }
        return names;
    }
    
    public Collection<String> getDirectives() {
        switch (getPreprocessorType()) {
            case SCSS:
                return SASS_DIRECTIVES;
            default:
                //nothing
                return Collections.emptyList();
        }
    }
}
