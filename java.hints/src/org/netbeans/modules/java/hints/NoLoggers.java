/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.jackpot.code.spi.Hint;
import org.netbeans.modules.java.hints.jackpot.code.spi.TriggerTreeKind;
import org.netbeans.modules.java.hints.jackpot.spi.HintContext;
import org.netbeans.modules.java.hints.jackpot.spi.support.ErrorDescriptionFactory;
import org.netbeans.modules.java.hints.spi.support.FixFactory;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.NbBundle;

/**
 *
 * @author vita
 */
@Hint(category="logging", suppressWarnings={"ClassWithoutLogger"}, enabled=false) //NOI18N
public final class NoLoggers {

    public NoLoggers() {
    }

    @TriggerTreeKind({Tree.Kind.ANNOTATION_TYPE, Tree.Kind.CLASS, Tree.Kind.ENUM, Tree.Kind.INTERFACE})
    public static Iterable<ErrorDescription> checkNoLoggers(HintContext ctx) {
        Element cls = ctx.getInfo().getTrees().getElement(ctx.getPath());
        if (cls == null || cls.getKind() != ElementKind.CLASS || cls.getModifiers().contains(Modifier.ABSTRACT) ||
            (cls.getEnclosingElement() != null && cls.getEnclosingElement().getKind() != ElementKind.PACKAGE)
        ) {
            return null;
        }

        TypeElement loggerTypeElement = ctx.getInfo().getElements().getTypeElement("java.util.logging.Logger"); // NOI18N
        if (loggerTypeElement == null) {
            return null;
        }
        TypeMirror loggerTypeElementAsType = loggerTypeElement.asType();
        if (loggerTypeElementAsType == null || loggerTypeElementAsType.getKind() != TypeKind.DECLARED) {
            return null;
        }

        List<VariableElement> loggerFields = new LinkedList<VariableElement>();
        List<VariableElement> fields = ElementFilter.fieldsIn(cls.getEnclosedElements());
        for(VariableElement f : fields) {
            if (f.getKind() != ElementKind.FIELD) {
                continue;
            }

            if (f.asType().equals(loggerTypeElementAsType)) {
                loggerFields.add(f);
            }
        }

        if (loggerFields.size() == 0) {
            return Collections.singleton(ErrorDescriptionFactory.forName(
                    ctx,
                    ctx.getPath(),
                    NbBundle.getMessage(NoLoggers.class, "MSG_NoLoggers_checkNoLoggers", cls), //NOI18N
                    new NoLoggersFix(NbBundle.getMessage(NoLoggers.class, "MSG_NoLoggers_checkNoLoggers_Fix", cls), TreePathHandle.create(cls, ctx.getInfo())), //NOI18N
                    FixFactory.createSuppressWarningsFix(ctx.getInfo(), ctx.getPath(), "ClassWithoutLogger") //NOI18N
            ));
        } else {
            return null;
        }
    }

    private static final class NoLoggersFix implements Fix {

        private final String description;
        private final TreePathHandle loggerFieldHandle;

        public NoLoggersFix(String description, TreePathHandle loggerFieldHandle) {
            this.description = description;
            this.loggerFieldHandle = loggerFieldHandle;
        }

        public String getText() {
            return description;
        }

        public ChangeInfo implement() throws Exception {
            JavaSource.forFileObject(loggerFieldHandle.getFileObject()).runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.RESOLVED);
                    TreePath tp = loggerFieldHandle.resolve(wc);

                    if (tp == null) {
                        return;
                    }

                    TreeMaker m = wc.getTreeMaker();
                    ClassTree classTree = (ClassTree) tp.getLeaf();
                    Element cls = wc.getTrees().getElement(tp);

                    // find free field name
                    String loggerFieldName = null;
                    List<VariableElement> fields = ElementFilter.fieldsIn(cls.getEnclosedElements());
                    if (!contains(fields, "LOG")) { //NOI18N
                        loggerFieldName = "LOG"; //NOI18N
                    } else {
                        if (!contains(fields, "LOGGER")) { //NOI18N
                            loggerFieldName = "LOGGER"; //NOI18N
                        } else {
                            for(int i = 1; i < Integer.MAX_VALUE; i++) {
                                String n = "LOG" + i; //NOI18N
                                if (!contains(fields, n)) {
                                    loggerFieldName = n;
                                    break;
                                }
                            }
                        }
                    }

                    if (loggerFieldName == null) {
                        return;
                    }
                    
                    // modifiers
                    Set<Modifier> mods = new HashSet<Modifier>();
                    mods.add(Modifier.PRIVATE);
                    mods.add(Modifier.STATIC);
                    mods.add(Modifier.FINAL);
                    ModifiersTree mt = m.Modifiers(mods);

                    // logger type
                    TypeElement loggerTypeElement = wc.getElements().getTypeElement("java.util.logging.Logger"); // NOI18N
                    ExpressionTree loggerClassQualIdent = m.QualIdent(loggerTypeElement);

                    // initializer
                    MemberSelectTree getLogger = m.MemberSelect(loggerClassQualIdent, "getLogger"); //NOI18N
                    ExpressionTree initializer = m.MethodInvocation(
                        Collections.<ExpressionTree>emptyList(),
                        getLogger,
                        Collections.singletonList(m.MethodInvocation(
                            Collections.<ExpressionTree>emptyList(),
                            m.MemberSelect(m.QualIdent(cls), "class.getName"), //NOI18N
                            Collections.<ExpressionTree>emptyList())
                    ));

                    // new logger field
                    VariableTree nueLogger = m.Variable(mt, loggerFieldName, loggerClassQualIdent, initializer); //NOI18N
                    ClassTree nueClassTree = m.addClassMember(classTree, nueLogger);
                    wc.rewrite(classTree, nueClassTree);
                }
            }).commit();
            return null;
        }

        private static boolean contains(Collection<VariableElement> fields, String name) {
            for(VariableElement f : fields) {
                if (f.getSimpleName().contentEquals(name)) {
                    return true;
                }
            }
            return false;
        }
    } // End of FixImpl class
    
}
