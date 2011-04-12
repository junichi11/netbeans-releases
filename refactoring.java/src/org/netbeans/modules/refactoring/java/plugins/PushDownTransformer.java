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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.refactoring.java.plugins;

import java.io.IOException;
import javax.lang.model.util.Types;
import org.netbeans.modules.refactoring.java.spi.RefactoringVisitor;
import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.java.RetoucheUtils;
import org.netbeans.modules.refactoring.java.api.MemberInfo;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Becicka
 */
public class PushDownTransformer extends RefactoringVisitor {

    private MemberInfo<ElementHandle<? extends Element>>[] members;
    private Problem problem;
 
    public Problem getProblem() {
        return problem;
    }

    public PushDownTransformer(MemberInfo<ElementHandle<? extends Element>> members[]) {
        this.members = members;
    }

    @Override
    public Tree visitClass(ClassTree tree, Element p) {
        Element el = workingCopy.getTrees().getElement(getCurrentPath());
        GeneratorUtilities genUtils = GeneratorUtilities.get(workingCopy);
        ClassTree njuClass = tree;
        if (el.equals(p)) {
            //source type
            boolean classIsAbstract = el.getKind().isInterface();

            for (Tree t:njuClass.getImplementsClause()) {
                Element currentInterface = workingCopy.getTrees().getElement(TreePath.getPath(getCurrentPath(), t));
                for (int i=0; i<members.length; i++) {
                    if (members[i].getGroup()==MemberInfo.Group.IMPLEMENTS && currentInterface.equals(members[i].getElementHandle().resolve(workingCopy))) {
                        njuClass = make.removeClassImplementsClause(njuClass, t);
                        rewrite(tree, njuClass);
                    }
                }
            }
            
            for (Tree t: njuClass.getMembers()) {
                for (int i=0; i<members.length; i++) {
                    Element current = workingCopy.getTrees().getElement(TreePath.getPath(workingCopy.getCompilationUnit(), t));
                    if (members[i].getGroup()!=MemberInfo.Group.IMPLEMENTS && current!=null && current.equals(members[i].getElementHandle().resolve(workingCopy))) {
                        if (members[i].isMakeAbstract()) {
                            if (el.getKind().isClass()) {
                                if (!classIsAbstract) {
                                    classIsAbstract = true;
                                    Set<Modifier> mod = new HashSet<Modifier>(njuClass.getModifiers().getFlags());
                                    mod.add(Modifier.ABSTRACT);
                                    ModifiersTree modifiers = make.Modifiers(mod);
                                    rewrite(njuClass.getModifiers(), modifiers);
                                }


                                MethodTree method = (MethodTree) t;
                                Set<Modifier> mod = new HashSet<Modifier>(method.getModifiers().getFlags());
                                mod.add(Modifier.ABSTRACT);
                                if(mod.contains(Modifier.PRIVATE)) {
                                    mod.remove(Modifier.PRIVATE);
                                    mod.add(Modifier.PROTECTED);
                                }
                                MethodTree nju = make.Method(
                                        make.Modifiers(mod),
                                        method.getName(),
                                        method.getReturnType(),
                                        method.getTypeParameters(),
                                        method.getParameters(),
                                        method.getThrows(),
                                        (BlockTree) null,
                                        (ExpressionTree)method.getDefaultValue());
                                rewrite(method, nju);
                            }
                        } else {
                            njuClass = make.removeClassMember(njuClass, t);
                            rewrite(tree, njuClass);
                        }
                        fixVisibility(current);
                    }
                }
                
            }
        } else {
            //target type
            TypeMirror tm = el.asType();
            Types types = workingCopy.getTypes();
            if (types.isSubtype(types.erasure(tm), types.erasure(p.asType()))) {
                List<String> imports = new ArrayList<String>();
                boolean makeClassAbstract = false;
                for (int i = 0; i<members.length; i++) {
                    Element member = members[i].getElementHandle().resolve(workingCopy);
                    if (members[i].getGroup()==MemberInfo.Group.IMPLEMENTS) {
                        if (((TypeElement) el).getInterfaces().contains(member.asType())) {
                            problem = MoveTransformer.createProblem(problem, false, org.openide.util.NbBundle.getMessage(PushDownTransformer.class, "ERR_PushDown_AlreadyExists", member.getSimpleName(), el.getSimpleName()));
                        }
                        imports.add(member.asType().toString()); //add may-be necessary import
                        njuClass = make.addClassImplementsClause(njuClass, make.Identifier(member));
                    } else if (members[i].getGroup()==MemberInfo.Group.METHOD
                            && member.getModifiers().contains(Modifier.ABSTRACT) && el.getKind().isClass() && p.getKind().isInterface()) {
                        // moving abstract method from interface to class
                        if (RetoucheUtils.elementExistsIn((TypeElement) el, member, workingCopy)) {
                            problem = MoveTransformer.createProblem(problem, false, org.openide.util.NbBundle.getMessage(PushDownTransformer.class, "ERR_PushDown_AlreadyExists", member.getSimpleName(), el.getSimpleName()));
                        }
                        MethodTree methodTree = workingCopy.getTrees().getTree((ExecutableElement) member);
                        ModifiersTree mods = RetoucheUtils.makeAbstract(make, methodTree.getModifiers());
                        mods = make.addModifiersModifier(mods, Modifier.PUBLIC);
                        MethodTree njuMethod = make.Method(
                                mods,
                                methodTree.getName(),
                                methodTree.getReturnType(),
                                methodTree.getTypeParameters(),
                                methodTree.getParameters(),
                                methodTree.getThrows(),
                                (BlockTree) null,
                                null);
                        RetoucheUtils.copyJavadoc(member, njuMethod, workingCopy);
                        njuClass = genUtils.insertClassMember(njuClass, njuMethod);
                        makeClassAbstract = true;
                    } else {
                        if (RetoucheUtils.elementExistsIn((TypeElement) el, member, workingCopy)) {
                            problem = MoveTransformer.createProblem(problem, false, org.openide.util.NbBundle.getMessage(PushDownTransformer.class, "ERR_PushDown_AlreadyExists", member.getSimpleName(), el.getSimpleName()));
                        }
                        TreePath path = workingCopy.getTrees().getPath(member);
                        Tree memberTree = genUtils.importComments(path.getLeaf(), path.getCompilationUnit());
                        memberTree = genUtils.importFQNs(memberTree);
                        if (members[i].isMakeAbstract() && memberTree.getKind() == Tree.Kind.METHOD && member.getModifiers().contains((Modifier.PRIVATE))) {
                            MethodTree oldOne = (MethodTree) memberTree;
                            MethodTree m = make.Method(
                                    make.addModifiersModifier(make.removeModifiersModifier(oldOne.getModifiers(), Modifier.PRIVATE), Modifier.PROTECTED),
                                    oldOne.getName(),
                                    oldOne.getReturnType(),
                                    oldOne.getTypeParameters(),
                                    oldOne.getParameters(),
                                    oldOne.getThrows(),
                                    oldOne.getBody(),
                                    (ExpressionTree) oldOne.getDefaultValue());
                            RetoucheUtils.copyJavadoc(member, m, workingCopy);
                            njuClass = genUtils.insertClassMember(njuClass, m);
                        } else {
                            njuClass = genUtils.insertClassMember(njuClass, memberTree);
                        }
                        makeClassAbstract |= member.getModifiers().contains(Modifier.ABSTRACT);
                    }
                }

                if (makeClassAbstract && !njuClass.getModifiers().getFlags().contains(Modifier.ABSTRACT) && (njuClass.getKind() != Tree.Kind.INTERFACE)) {
                    // make enclosing class abstract if necessary
                    njuClass = make.Class(RetoucheUtils.makeAbstract(make,
                            njuClass.getModifiers()), njuClass.getSimpleName(),
                            njuClass.getTypeParameters(), njuClass.getExtendsClause(),
                            njuClass.getImplementsClause(), njuClass.getMembers());
                }

                try {
                    if (imports.size() > 0) {
                        CompilationUnitTree newCut = RetoucheUtils.addImports(workingCopy.getCompilationUnit(), imports, make);
                        rewrite(workingCopy.getCompilationUnit(), newCut);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                rewrite(tree, njuClass);
            }
        }
        return super.visitClass(tree, p);
    }
    
    void fixVisibility(final Element el) {
        if (el.getKind() != ElementKind.METHOD) {
            return;
        }
        
        new TreePathScanner() {

            @Override
            public Object visitIdentifier(IdentifierTree node, Object p) {
                check();
                return super.visitIdentifier(node, p);
            }

            @Override
            public Object visitMemberSelect(MemberSelectTree node, Object p) {
                check();
                return super.visitMemberSelect(node, p);
            }

            private void check() throws IllegalArgumentException {
                Element thisElement = workingCopy.getTrees().getElement(getCurrentPath());
                if (workingCopy.getElementUtilities().enclosingTypeElement(thisElement) == el.getEnclosingElement()) {
                    Tree tree = workingCopy.getTrees().getTree(thisElement);
                    if (thisElement.getKind().isField() && tree!=null) {
                        makeProtectedIfPrivate(((VariableTree) tree).getModifiers());
                    } else if (thisElement.getKind() == ElementKind.METHOD) {
                        makeProtectedIfPrivate(((MethodTree) tree).getModifiers());
                    } else if (thisElement.getKind().isClass() || thisElement.getKind().isInterface()) {
                        makeProtectedIfPrivate(((ClassTree) tree).getModifiers());
                    }
                }
            }
            
            private void makeProtectedIfPrivate(ModifiersTree modTree) {
                if (modTree.getFlags().contains(Modifier.PRIVATE)) {
                    ModifiersTree newMods = workingCopy.getTreeMaker().removeModifiersModifier(modTree, Modifier.PRIVATE);
                    newMods = workingCopy.getTreeMaker().addModifiersModifier(newMods, Modifier.PROTECTED);
                    rewrite(modTree, newMods);
                }
            }
            
        }.scan(workingCopy.getTrees().getPath(el), null);
    }
}

