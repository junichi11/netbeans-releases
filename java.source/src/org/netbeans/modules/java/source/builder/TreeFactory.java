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

package org.netbeans.modules.java.source.builder;

import com.sun.source.doctree.AttributeTree;
import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.InheritDocTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SerialTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.doctree.VersionTree;
import com.sun.source.tree.MemberReferenceTree.ReferenceMode;
import com.sun.tools.javac.code.Type.ErrorType;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.util.Names;
import javax.lang.model.util.Elements;
import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.WildcardType;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Context;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.type.ArrayType;
import javax.lang.model.util.Types;
import org.netbeans.api.annotations.common.NonNull;
import static org.netbeans.modules.java.source.save.PositionEstimator.*;
import static com.sun.tools.javac.code.Flags.*;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.DCTree;
import com.sun.tools.javac.tree.DCTree.DCIdentifier;
import com.sun.tools.javac.tree.DCTree.DCReference;
import com.sun.tools.javac.tree.DCTree.DCText;
import com.sun.tools.javac.tree.DocTreeMaker;
import com.sun.tools.javac.util.DiagnosticSource;

/**
 * Factory for creating new com.sun.source.tree instances.
 * 
 * @author Rastislav Komara (<a href="mailto:moonko@netbeans.org">RKo</a>)
 * @since 0.44.0
 */
public class TreeFactory {
    Names names;
    ClassReader classReader;
    com.sun.tools.javac.tree.TreeMaker make;
    ASTService model;
    Elements elements;
    Types types;
    private final CommentHandlerService chs;
    
    private static final Context.Key<TreeFactory> contextKey = new Context.Key<TreeFactory>();

    public static synchronized TreeFactory instance(Context context) {
	TreeFactory instance = context.get(contextKey);
	if (instance == null) {
	    instance = new TreeFactory(context);
        }
	return instance;
    }

    protected TreeFactory(Context context) {
        context.put(contextKey, this);
        model = ASTService.instance(context);
        names = Names.instance(context);
        classReader = ClassReader.instance(context);
        make = com.sun.tools.javac.tree.TreeMaker.instance(context);
        docMake = com.sun.tools.javac.tree.DocTreeMaker.instance(context);
        elements = JavacElements.instance(context);
        types = JavacTypes.instance(context);
        chs = CommentHandlerService.instance(context);
        make.toplevel = null;
    }
    
    public AnnotationTree Annotation(Tree type, List<? extends ExpressionTree> arguments) {
        ListBuffer<JCExpression> lb = new ListBuffer<JCExpression>();
        for (ExpressionTree t : arguments)
            lb.append((JCExpression)t);
        return make.at(NOPOS).Annotation((JCTree)type, lb.toList());
    }

    public AnnotationTree TypeAnnotation(Tree type, List<? extends ExpressionTree> arguments) {
        ListBuffer<JCExpression> lb = new ListBuffer<JCExpression>();
        for (ExpressionTree t : arguments)
            lb.append((JCExpression)t);
        return make.at(NOPOS).TypeAnnotation((JCTree)type, lb.toList());
    }
    
    public AnnotatedTypeTree AnnotatedType(List<? extends AnnotationTree> annotations, Tree underlyingType) {
        ListBuffer<JCAnnotation> lb = new ListBuffer<JCAnnotation>();
        for (AnnotationTree t : annotations)
            lb.append((JCAnnotation)t);
        return make.at(NOPOS).AnnotatedType(lb.toList(), (JCExpression)underlyingType);
    }

    public ArrayAccessTree ArrayAccess(ExpressionTree array, ExpressionTree index) {
        return make.at(NOPOS).Indexed((JCExpression)array, (JCExpression)index);
    }
    
    public ArrayTypeTree ArrayType(Tree type) {
        return make.at(NOPOS).TypeArray((JCExpression)type);
    }
    
    public AssertTree Assert(ExpressionTree condition, ExpressionTree detail) {
        return make.at(NOPOS).Assert((JCExpression)condition, (JCExpression)detail);
    }
    
    public AssignmentTree Assignment(ExpressionTree variable, ExpressionTree expression) {
        return make.at(NOPOS).Assign((JCExpression)variable, (JCExpression)expression);
    }
    
    public BinaryTree Binary(Kind operator, ExpressionTree left, ExpressionTree right) {
        final Tag op;
        switch (operator) {
            case MULTIPLY: op = JCTree.Tag.MUL; break;
            case DIVIDE: op = JCTree.Tag.DIV; break;
            case REMAINDER: op = JCTree.Tag.MOD; break;
            case PLUS: op = JCTree.Tag.PLUS; break;
            case MINUS: op = JCTree.Tag.MINUS; break;
            case LEFT_SHIFT: op = JCTree.Tag.SL; break;
            case RIGHT_SHIFT: op = JCTree.Tag.SR; break;
            case UNSIGNED_RIGHT_SHIFT: op = JCTree.Tag.USR; break;
            case LESS_THAN: op = JCTree.Tag.LT; break;
            case GREATER_THAN: op = JCTree.Tag.GT; break;
            case LESS_THAN_EQUAL: op = JCTree.Tag.LE; break;
            case GREATER_THAN_EQUAL: op = JCTree.Tag.GE; break;
            case EQUAL_TO: op = JCTree.Tag.EQ; break;
            case NOT_EQUAL_TO: op = JCTree.Tag.NE; break;
            case AND: op = JCTree.Tag.BITAND; break;
            case XOR: op = JCTree.Tag.BITXOR; break;
            case OR: op = JCTree.Tag.BITOR; break;
            case CONDITIONAL_AND: op = JCTree.Tag.AND; break;
            case CONDITIONAL_OR: op = JCTree.Tag.OR; break;
            default:
                throw new IllegalArgumentException("Illegal binary operator: " + operator);
        }
        return make.at(NOPOS).Binary(op, (JCExpression)left, (JCExpression)right);
    }
    
    public BlockTree Block(List<? extends StatementTree> statements, boolean isStatic) {
        ListBuffer<JCStatement> lb = new ListBuffer<JCStatement>();
        for (StatementTree t : statements)
            lb.append((JCStatement)t);
        return make.at(NOPOS).Block(isStatic ? Flags.STATIC : 0L, lb.toList());
    }
    
    public BreakTree Break(CharSequence label) {
        Name n = label != null ? names.fromString(label.toString()) : null;
        return make.at(NOPOS).Break(n);
    }
    
    public CaseTree Case(ExpressionTree expression, List<? extends StatementTree> statements) {
        ListBuffer<JCStatement> lb = new ListBuffer<JCStatement>();
        for (StatementTree t : statements)
            lb.append((JCStatement)t);
        return make.at(NOPOS).Case((JCExpression)expression, lb.toList());
    }
    
    public CatchTree Catch(VariableTree parameter, BlockTree block) {
        return make.at(NOPOS).Catch((JCVariableDecl)parameter, (JCBlock)block);
    }
    
    public ClassTree Class(ModifiersTree modifiers, 
                     CharSequence simpleName,
                     List<? extends TypeParameterTree> typeParameters,
                     Tree extendsClause,
                     List<? extends Tree> implementsClauses,
                     List<? extends Tree> memberDecls) 
    {
        ListBuffer<JCTypeParameter> typarams = new ListBuffer<JCTypeParameter>();
        for (TypeParameterTree t : typeParameters)
            typarams.append((JCTypeParameter)t);
        ListBuffer<JCExpression> impls = new ListBuffer<JCExpression>();
        for (Tree t : implementsClauses)
            impls.append((JCExpression)t);
        ListBuffer<JCTree> defs = new ListBuffer<JCTree>();
        for (Tree t : memberDecls)
            defs.append((JCTree)t);
        return make.at(NOPOS).ClassDef((JCModifiers)modifiers,
                             names.fromString(simpleName.toString()),
                             typarams.toList(),
                             (JCExpression)extendsClause,
                             impls.toList(),
                             defs.toList());
        
    }
    
    public ClassTree Interface(ModifiersTree modifiers, 
                     CharSequence simpleName,
                     List<? extends TypeParameterTree> typeParameters,
                     List<? extends Tree> extendsClauses,
                     List<? extends Tree> memberDecls) 
    {
        long flags = getBitFlags(modifiers.getFlags()) | Flags.INTERFACE;
        return Class(flags, (com.sun.tools.javac.util.List<JCAnnotation>) modifiers.getAnnotations(), simpleName, typeParameters, null, extendsClauses, memberDecls);
    }

    public ClassTree AnnotationType(ModifiersTree modifiers, 
             CharSequence simpleName,
             List<? extends Tree> memberDecls) {
        long flags = getBitFlags(modifiers.getFlags()) | Flags.ANNOTATION;
        return Class(flags, (com.sun.tools.javac.util.List<JCAnnotation>) modifiers.getAnnotations(), simpleName, Collections.<TypeParameterTree>emptyList(), null, Collections.<ExpressionTree>emptyList(), memberDecls);
    }
    
    public ClassTree Enum(ModifiersTree modifiers, 
             CharSequence simpleName,
             List<? extends Tree> implementsClauses,
             List<? extends Tree> memberDecls) {
        long flags = getBitFlags(modifiers.getFlags()) | Flags.ENUM;
        return Class(flags, (com.sun.tools.javac.util.List<JCAnnotation>) modifiers.getAnnotations(), simpleName, Collections.<TypeParameterTree>emptyList(), null, implementsClauses, memberDecls);
    }
    
    public CompilationUnitTree CompilationUnit(@NonNull List<? extends AnnotationTree> packageAnnotations,
                                               ExpressionTree packageDecl,
                                               List<? extends ImportTree> importDecls, 
                                               List<? extends Tree> typeDecls, 
                                               JavaFileObject sourceFile) {

        ListBuffer<JCAnnotation> annotations = new ListBuffer<JCAnnotation>();
        for (AnnotationTree at : packageAnnotations) {
            annotations.add((JCAnnotation) at);
        }
        ListBuffer<JCTree> defs = new ListBuffer<JCTree>();
        if (importDecls != null)
            for (Tree t : importDecls)
                defs.append((JCTree)t);
        if (typeDecls != null) 
            for (Tree t : typeDecls)
                defs.append((JCTree)t);
        JCCompilationUnit unit = make.at(NOPOS).TopLevel(annotations.toList(),
                                                          (JCExpression)packageDecl, defs.toList());
        unit.sourcefile = sourceFile;
        return unit;
    }
    
    public CompoundAssignmentTree CompoundAssignment(Kind operator, 
                                                     ExpressionTree variable, 
                                                     ExpressionTree expression) {
        final Tag op;
        switch (operator) {
            case MULTIPLY_ASSIGNMENT: op = JCTree.Tag.MUL_ASG; break;
            case DIVIDE_ASSIGNMENT: op = JCTree.Tag.DIV_ASG; break;
            case REMAINDER_ASSIGNMENT: op = JCTree.Tag.MOD_ASG; break;
            case PLUS_ASSIGNMENT: op = JCTree.Tag.PLUS_ASG; break;
            case MINUS_ASSIGNMENT: op = JCTree.Tag.MINUS_ASG; break;
            case LEFT_SHIFT_ASSIGNMENT: op = JCTree.Tag.SL_ASG; break;
            case RIGHT_SHIFT_ASSIGNMENT: op = JCTree.Tag.SR_ASG; break;
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT: op = JCTree.Tag.USR_ASG; break;
            case AND_ASSIGNMENT: op = JCTree.Tag.BITAND_ASG; break;
            case XOR_ASSIGNMENT: op = JCTree.Tag.BITXOR_ASG; break;
            case OR_ASSIGNMENT: op = JCTree.Tag.BITOR_ASG; break;
            default:
                throw new IllegalArgumentException("Illegal binary operator: " + operator);
        }
        return make.at(NOPOS).Assignop(op, (JCExpression)variable, (JCExpression)expression);
    }
    
    public ConditionalExpressionTree ConditionalExpression(ExpressionTree condition,
                                                           ExpressionTree trueExpression,
                                                           ExpressionTree falseExpression) {
        return make.at(NOPOS).Conditional((JCExpression)condition,
                                (JCExpression)trueExpression,
                                (JCExpression)falseExpression);
    }
    
    public ContinueTree Continue(CharSequence label) {
        Name n = label != null ? names.fromString(label.toString()) : null;
        return make.at(NOPOS).Continue(n);
    }
    
    public UnionTypeTree UnionType(List<? extends Tree> typeComponents) {
        ListBuffer<JCExpression> components = new ListBuffer<JCExpression>();
        for (Tree t : typeComponents)
            components.append((JCExpression)t);
        return make.at(NOPOS).TypeUnion(components.toList());
    }

    public DoWhileLoopTree DoWhileLoop(ExpressionTree condition, StatementTree statement) {
        return make.at(NOPOS).DoLoop((JCStatement)statement, (JCExpression)condition);
    }
    
    public EmptyStatementTree EmptyStatement() {
        return make.at(NOPOS).Skip();
    }
    
    public EnhancedForLoopTree EnhancedForLoop(VariableTree variable, 
                                               ExpressionTree expression,
                                               StatementTree statement) {
        return make.at(NOPOS).ForeachLoop((JCVariableDecl)variable,
                                (JCExpression)expression,
                                (JCStatement)statement);
    }
    
    public ErroneousTree Erroneous(List<? extends Tree> errorTrees) {
        ListBuffer<JCTree> errors = new ListBuffer<JCTree>();
        for (Tree t : errorTrees)
           errors.append((JCTree)t);
        return make.at(NOPOS).Erroneous(errors.toList());
    }
    
    public ExpressionStatementTree ExpressionStatement(ExpressionTree expression) {
        return make.at(NOPOS).Exec((JCExpression)expression);
    }
    
    public ForLoopTree ForLoop(List<? extends StatementTree> initializer, 
                               ExpressionTree condition,
                               List<? extends ExpressionStatementTree> update,
                               StatementTree statement) {
        ListBuffer<JCStatement> init = new ListBuffer<JCStatement>();
        for (StatementTree t : initializer)
            init.append((JCStatement)t);
        ListBuffer<JCExpressionStatement> step = new ListBuffer<JCExpressionStatement>();
        for (ExpressionStatementTree t : update)
            step.append((JCExpressionStatement)t);
        return make.at(NOPOS).ForLoop(init.toList(), (JCExpression)condition,
                            step.toList(), (JCStatement)statement);
    }
    
    public IdentifierTree Identifier(CharSequence name) {
        return make.at(NOPOS).Ident(names.fromString(name.toString()));
    }
    
    public IdentifierTree Identifier(Element element) {
        return make.at(NOPOS).Ident((Symbol)element);
    }
    
    public IfTree If(ExpressionTree condition, StatementTree thenStatement, StatementTree elseStatement) {
        return make.at(NOPOS).If((JCExpression)condition, (JCStatement)thenStatement, (JCStatement)elseStatement);
    }
    
    public ImportTree Import(Tree qualid, boolean importStatic) {
        return make.at(NOPOS).Import((JCTree)qualid, importStatic);
    }
    
    public InstanceOfTree InstanceOf(ExpressionTree expression, Tree type) {
        return make.at(NOPOS).TypeTest((JCExpression)expression, (JCTree)type);
    }
    
    public IntersectionTypeTree IntersectionType(List<? extends Tree> bounds) {
        ListBuffer<JCExpression> jcbounds = new ListBuffer<JCExpression>();
        for (Tree t : bounds)
            jcbounds.append((JCExpression)t);
        return make.at(NOPOS).TypeIntersection(jcbounds.toList());
    }
    
    public LabeledStatementTree LabeledStatement(CharSequence label, StatementTree statement) {
        return make.at(NOPOS).Labelled(names.fromString(label.toString()), (JCStatement)statement);
    }
    
    public LambdaExpressionTree LambdaExpression(List<? extends VariableTree> parameters, Tree body) {
        ListBuffer<JCVariableDecl> params = new ListBuffer<JCVariableDecl>();
        for (Tree t : parameters)
            params.append((JCVariableDecl)t);
        return make.at(NOPOS).Lambda(params.toList(), (JCTree) body);
    }

    public LiteralTree Literal(Object value) {
        try {
            if (value instanceof Boolean)  // workaround for javac issue 6504896
                return make.at(NOPOS).Literal(TypeTag.BOOLEAN, value == Boolean.FALSE ? 0 : 1);
            if (value instanceof Character) // looks like world championship in workarounds here ;-)
                return make.at(NOPOS).Literal(TypeTag.CHAR, Integer.valueOf((Character) value));
            if (value instanceof Byte) // #119143: Crystal ball no. 4
                return make.at(NOPOS).Literal(TypeTag.INT, ((Byte) value).intValue());
            if (value instanceof Short)
                return make.at(NOPOS).Literal(TypeTag.INT, ((Short) value).intValue());
            // workaround for making NULL_LITERAL kind.
            if (value == null) {
                return make.at(NOPOS).Literal(TypeTag.BOT, value);
            }
            return make.at(NOPOS).Literal(value);
        } catch (AssertionError e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public MemberSelectTree MemberSelect(ExpressionTree expression, CharSequence identifier) {
        return make.at(NOPOS).Select((JCExpression)expression, names.fromString(identifier.toString()));
    }
    
    public MemberSelectTree MemberSelect(ExpressionTree expression, Element element) {
        return (MemberSelectTree)make.at(NOPOS).Select((JCExpression)expression, (Symbol)element);
    }
    
    public MethodInvocationTree MethodInvocation(List<? extends ExpressionTree> typeArguments, 
                                                 ExpressionTree method, 
                                                 List<? extends ExpressionTree> arguments) {
        ListBuffer<JCExpression> typeargs = new ListBuffer<JCExpression>();
        for (ExpressionTree t : typeArguments)
            typeargs.append((JCExpression)t);
        ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
        for (ExpressionTree t : arguments)
            args.append((JCExpression)t);
        return make.at(NOPOS).Apply(typeargs.toList(), (JCExpression)method, args.toList());
    }
    
    public MethodTree Method(ModifiersTree modifiers,
                             CharSequence name,
                             Tree returnType,
                             List<? extends TypeParameterTree> typeParameters,
                             List<? extends VariableTree> parameters,
                             List<? extends ExpressionTree> throwsList,
                             BlockTree body,
                             ExpressionTree defaultValue) {
        return Method(modifiers, name, returnType, typeParameters, parameters, throwsList, body, defaultValue, false);
    }
    
    public MethodTree Method(ModifiersTree modifiers,
                             CharSequence name,
                             Tree returnType,
                             List<? extends TypeParameterTree> typeParameters,
                             List<? extends VariableTree> parameters,
                             List<? extends ExpressionTree> throwsList,
                             BlockTree body,
                             ExpressionTree defaultValue,
                             boolean isVarArgs) {
        ListBuffer<JCTypeParameter> typarams = new ListBuffer<JCTypeParameter>();
        for (TypeParameterTree t : typeParameters)
            typarams.append((JCTypeParameter)t);
        ListBuffer<JCVariableDecl> params = new ListBuffer<JCVariableDecl>();        
        if (!parameters.isEmpty() && isVarArgs) {
            JCVariableDecl variableDecl = (JCVariableDecl) parameters.get(parameters.size()-1);
            if (variableDecl.getKind() != Kind.ARRAY_TYPE) {                
                variableDecl.mods = make.at(NOPOS).Modifiers(variableDecl.mods.flags | Flags.VARARGS);
            } else {
                throw new IllegalArgumentException("Last parameter isn't array. Can't set varargs flag.");
            }
        } else if (parameters.isEmpty() && isVarArgs) {
            throw new IllegalArgumentException("Can't set varargs flag on empty parameter list.");
        }
        for (VariableTree t : parameters) {
            params.append((JCVariableDecl) t);
        }
        ListBuffer<JCExpression> throwz = new ListBuffer<JCExpression>();
        for (ExpressionTree t : throwsList)
            throwz.append((JCExpression)t);
        return make.at(NOPOS).MethodDef((JCModifiers)modifiers, names.fromString(name.toString()),
                              (JCExpression)returnType, typarams.toList(),
                              params.toList(), throwz.toList(),
                              (JCBlock)body, (JCExpression)defaultValue);
    }
    
    public MethodTree Method(ExecutableElement element, BlockTree body) {
        return make.at(NOPOS).MethodDef((Symbol.MethodSymbol)element, (JCBlock)body);
    }

    public MemberReferenceTree MemberReference(ReferenceMode refMode, CharSequence name, ExpressionTree expression, List<? extends ExpressionTree> typeArguments) {
        ListBuffer<JCExpression> targs;
        
        if (typeArguments != null) {
            targs = new ListBuffer<JCExpression>();
            for (ExpressionTree t : typeArguments)
                targs.append((JCExpression)t);
        } else {
            targs = null;
        }
        
        return make.at(NOPOS).Reference(refMode, names.fromString(name.toString()), (JCExpression) expression, targs != null ? targs.toList() : null);
    }
    
    public ModifiersTree Modifiers(Set<Modifier> flagset, List<? extends AnnotationTree> annotations) {
        return Modifiers(modifiersToFlags(flagset), annotations);
    }
    
    public ModifiersTree Modifiers(long mods, List<? extends AnnotationTree> annotations) {
        ListBuffer<JCAnnotation> anns = new ListBuffer<JCAnnotation>();
        for (AnnotationTree t : annotations)
            anns.append((JCAnnotation)t);
        return make.at(NOPOS).Modifiers(mods, anns.toList());
    }
    
    public static long modifiersToFlags(Set<Modifier> flagset) {
        long flags = 0L;
        for (Modifier mod : flagset)
            switch (mod) {
                case PUBLIC: flags |= Flags.PUBLIC; break;
                case PROTECTED: flags |= Flags.PROTECTED; break;
                case PRIVATE: flags |= Flags.PRIVATE; break;
                case ABSTRACT: flags |= Flags.ABSTRACT; break;
                case STATIC: flags |= Flags.STATIC; break;
                case FINAL: flags |= Flags.FINAL; break;
                case TRANSIENT: flags |= Flags.TRANSIENT; break;
                case VOLATILE: flags |= Flags.VOLATILE; break;
                case SYNCHRONIZED: flags |= Flags.SYNCHRONIZED; break;
                case NATIVE: flags |= Flags.NATIVE; break;
                case STRICTFP: flags |= Flags.STRICTFP; break;
                case DEFAULT: flags |= Flags.DEFAULT; break;
                default:
                    throw new AssertionError("Unknown Modifier: " + mod); //NOI18N
            }
        return flags;
    }
    
    public ModifiersTree Modifiers(Set<Modifier> flagset) {
        return Modifiers(flagset, com.sun.tools.javac.util.List.<AnnotationTree>nil());
    }
    
    public ModifiersTree Modifiers(ModifiersTree oldMods, List<? extends AnnotationTree> annotations) {
        ListBuffer<JCAnnotation> anns = new ListBuffer<JCAnnotation>();
        for (AnnotationTree t : annotations)
            anns.append((JCAnnotation)t);
        return make.at(NOPOS).Modifiers(((JCModifiers)oldMods).flags, anns.toList());
    }
    
    public NewArrayTree NewArray(Tree elemtype, 
                                 List<? extends ExpressionTree> dimensions,
                                 List<? extends ExpressionTree> initializers) {
        ListBuffer<JCExpression> dims = new ListBuffer<JCExpression>();
        for (ExpressionTree t : dimensions)
            dims.append((JCExpression)t);
        ListBuffer<JCExpression> elems = null;
        if (initializers != null) {
            elems = new ListBuffer<JCExpression>();
            for (ExpressionTree t : initializers)
                elems.append((JCExpression)t);
        }
        return make.at(NOPOS).NewArray((JCExpression)elemtype, dims.toList(), elems != null ? elems.toList() : null);
    }
    
    public NewClassTree NewClass(ExpressionTree enclosingExpression, 
                          List<? extends ExpressionTree> typeArguments,
                          ExpressionTree identifier,
                          List<? extends ExpressionTree> arguments,
                          ClassTree classBody) {
        ListBuffer<JCExpression> typeargs = new ListBuffer<JCExpression>();
        for (ExpressionTree t : typeArguments)
            typeargs.append((JCExpression)t);
        ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
        for (ExpressionTree t : arguments)
            args.append((JCExpression)t);
        return make.at(NOPOS).NewClass((JCExpression)enclosingExpression, typeargs.toList(),
                             (JCExpression)identifier, args.toList(),
                             (JCClassDecl)classBody);
    }
    
    public ParameterizedTypeTree ParameterizedType(Tree type,
                                                   List<? extends Tree> typeArguments) {
        ListBuffer<JCExpression> typeargs = new ListBuffer<JCExpression>();
        for (Tree t : typeArguments)
            typeargs.append((JCExpression)t);
        return make.at(NOPOS).TypeApply((JCExpression)type, typeargs.toList());
    }
    
    public ParenthesizedTree Parenthesized(ExpressionTree expression) {
        return make.at(NOPOS).Parens((JCExpression)expression);
    }
    
    public PrimitiveTypeTree PrimitiveType(TypeKind typekind) {
        final TypeTag typetag;
        switch (typekind) {
            case BOOLEAN:
                typetag = TypeTag.BOOLEAN;
                break;
            case BYTE:
                typetag = TypeTag.BYTE;
                break;
            case SHORT:
                typetag = TypeTag.SHORT;
                break;
            case INT:
                typetag = TypeTag.INT;
                break;
            case LONG:
                typetag = TypeTag.LONG;
                break;
            case CHAR:
                typetag = TypeTag.CHAR;
                break;
            case FLOAT:
                typetag = TypeTag.FLOAT;
                break;
            case DOUBLE:
                typetag = TypeTag.DOUBLE;
                break;
            case VOID:
                typetag = TypeTag.VOID;
                break;
            default:
                throw new AssertionError("unknown primitive type " + typekind);
        }
        return make.at(NOPOS).TypeIdent(typetag);
    }
    
    public ExpressionTree QualIdentImpl(Element element) {
        return make.at(NOPOS).QualIdent((Symbol) element);
    }
    
    public ExpressionTree QualIdent(Element element) {
        Symbol s = (Symbol) element;

        if (s.owner != null && (s.owner.kind == Kinds.MTH || s.owner.name.isEmpty())) {
            JCIdent result = make.at(NOPOS).Ident(s);
            result.setType(s.type);
            return result;
        }
        
        QualIdentTree result = new QualIdentTree(make.at(NOPOS).QualIdent(s.owner), s.name, s);
        
        result.setPos(make.pos).setType(s.type);
        
        return result;
    }

    public ExpressionTree QualIdent(String name) {
        int lastDot = name.lastIndexOf('.');

        if (lastDot == (-1)) {
            return Identifier(name);
        }

        QualIdentTree result = new QualIdentTree(((JCExpression) QualIdent(name.substring(0, lastDot))).setPos(NOPOS), (Name) elements.getName(name.substring(lastDot + 1)), name);

        result.setPos(NOPOS);

        return result;
    }
    
    public ReturnTree Return(ExpressionTree expression) {
        return make.at(NOPOS).Return((JCExpression)expression);
    }
    
    public SwitchTree Switch(ExpressionTree expression, List<? extends CaseTree> caseList) {
        ListBuffer<JCCase> cases = new ListBuffer<JCCase>();
        for (CaseTree t : caseList)
            cases.append((JCCase)t);
        return make.at(NOPOS).Switch((JCExpression)expression, cases.toList());
    }
    
    public SynchronizedTree Synchronized(ExpressionTree expression, BlockTree block) {
        return make.at(NOPOS).Synchronized((JCExpression)expression, (JCBlock)block);
    }
    
    public ThrowTree Throw(ExpressionTree expression) {
        return make.at(NOPOS).Throw((JCExpression)expression);
    }
    
    public TryTree Try(List<? extends Tree> resources,
                       BlockTree tryBlock,
                       List<? extends CatchTree> catchList, 
                       BlockTree finallyBlock) {
        ListBuffer<JCTree> res = new ListBuffer<JCTree>();
        for (Tree t : resources)
            res.append((JCTree)t);
        ListBuffer<JCCatch> catches = new ListBuffer<JCCatch>();
        for (CatchTree t : catchList)
            catches.append((JCCatch)t);
        return make.at(NOPOS).Try(res.toList(), (JCBlock)tryBlock, catches.toList(), (JCBlock)finallyBlock);
    }
    
    public com.sun.tools.javac.util.List<JCExpression> Types(List<Type> ts) {
        ListBuffer<JCExpression> types = new ListBuffer<JCExpression>();
        for (Type t : ts)
            types.append((JCExpression) Type(t));
        return types.toList();
    }
    
    public ExpressionTree Type(TypeMirror type) {
        Type t = (Type) type;
        JCExpression tp;
        switch (type.getKind()) {
            case WILDCARD: {
                WildcardType a = ((WildcardType) type);
                tp = make.at(NOPOS).Wildcard(make.at(NOPOS).TypeBoundKind(a.kind), (JCExpression) Type(a.type));
                break;
            }
            case DECLARED:
                JCExpression clazz = (JCExpression) QualIdent(t.tsym);
                tp = t.getTypeArguments().isEmpty()
                ? clazz
                        : make.at(NOPOS).TypeApply(clazz, Types(t.getTypeArguments()));
                break;
            case ARRAY:
                
                tp = make.at(NOPOS).TypeArray((JCExpression) Type(((ArrayType) type).getComponentType()));
                break;
            case NULL:
                tp = make.at(NOPOS).Literal(TypeTag.BOT, null);
                break;
            case ERROR:
                tp = make.at(NOPOS).Ident(((ErrorType) type).tsym.name);
                break;
            default:
                return make.at(NOPOS).Type((Type)type);
        }
    
        return tp;
    }
    
    public TypeCastTree TypeCast(Tree type, ExpressionTree expression) {
        return make.at(NOPOS).TypeCast((JCTree)type, (JCExpression)expression);
    }
    
    public TypeParameterTree TypeParameter(CharSequence name, List<? extends ExpressionTree> boundsList) {
        ListBuffer<JCExpression> bounds = new ListBuffer<JCExpression>();
        for (Tree t : boundsList)
            bounds.append((JCExpression)t);
        return make.at(NOPOS).TypeParameter(names.fromString(name.toString()), bounds.toList());
    }
    
    public UnaryTree Unary(Kind operator, ExpressionTree arg) {
        final Tag op;
        switch (operator) {
            case POSTFIX_INCREMENT: op = JCTree.Tag.POSTINC; break;
            case POSTFIX_DECREMENT: op = JCTree.Tag.POSTDEC; break;
            case PREFIX_INCREMENT: op = JCTree.Tag.PREINC; break;
            case PREFIX_DECREMENT: op = JCTree.Tag.PREDEC; break;
            case UNARY_PLUS: op = JCTree.Tag.POS; break;
            case UNARY_MINUS: op = JCTree.Tag.NEG; break;
            case BITWISE_COMPLEMENT: op = JCTree.Tag.COMPL; break;
            case LOGICAL_COMPLEMENT: op = JCTree.Tag.NOT; break;
            default:
                throw new IllegalArgumentException("Illegal unary operator: " + operator);
        }
        return make.at(NOPOS).Unary(op, (JCExpression)arg);
    }
    
    public VariableTree Variable(ModifiersTree modifiers,
                                 CharSequence name,
                                 Tree type,
                                 ExpressionTree initializer) {
        return make.at(NOPOS).VarDef((JCModifiers)modifiers, names.fromString(name.toString()),
                           (JCExpression)type, (JCExpression)initializer);
    }
    
    public VariableTree Variable(VariableElement variable, ExpressionTree initializer) {
        return make.at(NOPOS).VarDef((Symbol.VarSymbol)variable, (JCExpression)initializer);
    }
    
    public WhileLoopTree WhileLoop(ExpressionTree condition, StatementTree statement) {
        return make.at(NOPOS).WhileLoop((JCExpression)condition, (JCStatement)statement);
    }
    
    public WildcardTree Wildcard(Kind kind, Tree type) {
        final BoundKind boundKind;
        switch (kind) {
            case UNBOUNDED_WILDCARD:
                boundKind = BoundKind.UNBOUND;
                break;
            case EXTENDS_WILDCARD:
                boundKind = BoundKind.EXTENDS;
                break;
            case SUPER_WILDCARD:
                boundKind = BoundKind.SUPER;
                break;
            default:
                throw new IllegalArgumentException("Unknown wildcard bound " + kind);
        }
        TypeBoundKind tbk = make.at(NOPOS).TypeBoundKind(boundKind);
        return make.at(NOPOS).Wildcard(tbk, (JCExpression)type);
    }
    
    ////////////////////////////////////// makers modification suggested by Tom
    
    // AnnotationTree
    public AnnotationTree addAnnotationAttrValue(AnnotationTree annotation, ExpressionTree attrValue) {
        return modifyAnnotationAttrValue(annotation, -1, attrValue, Operation.ADD);
    }
    
    public AnnotationTree insertAnnotationAttrValue(AnnotationTree annotation, int index, ExpressionTree attrValue) {
        return modifyAnnotationAttrValue(annotation, index, attrValue, Operation.ADD);
    }
    
    public AnnotationTree removeAnnotationAttrValue(AnnotationTree annotation, ExpressionTree attrValue) {
        return modifyAnnotationAttrValue(annotation, -1, attrValue, Operation.REMOVE);
    }
    
    public AnnotationTree removeAnnotationAttrValue(AnnotationTree annotation, int index) {
        return modifyAnnotationAttrValue(annotation, index, null, Operation.REMOVE);
    }

    private AnnotationTree modifyAnnotationAttrValue(AnnotationTree annotation, int index, ExpressionTree attrValue, Operation op) {
        AnnotationTree copy = annotation.getKind() == Kind.ANNOTATION
                ? Annotation(annotation.getAnnotationType(), c(annotation.getArguments(), index, attrValue, op))
                : TypeAnnotation(annotation.getAnnotationType(), c(annotation.getArguments(), index, attrValue, op));
        return copy;
    }
    
    // BlockTree
    public BlockTree addBlockStatement(BlockTree block, StatementTree statement) {
        return modifyBlockStatement(block, -1, statement, Operation.ADD);
    }
    
    public BlockTree insertBlockStatement(BlockTree block, int index, StatementTree statement) {
        return modifyBlockStatement(block, index, statement, Operation.ADD);
    }
    
    public BlockTree removeBlockStatement(BlockTree block, StatementTree statement) {
        return modifyBlockStatement(block, -1, statement, Operation.REMOVE);
    }
    
    public BlockTree removeBlockStatement(BlockTree block, int index) {
        return modifyBlockStatement(block, index, null, Operation.REMOVE);
    }
    
    private BlockTree modifyBlockStatement(BlockTree block, int index, StatementTree statement, Operation op) {
        BlockTree copy = Block(
            c(block.getStatements(), index, statement, op),
            block.isStatic()
        );
        return copy;
    }
    
    // CaseTree
    public CaseTree addCaseStatement(CaseTree kejs, StatementTree statement) {
        return modifyCaseStatement(kejs, -1, statement, Operation.ADD);
    }

    public CaseTree insertCaseStatement(CaseTree kejs, int index, StatementTree statement) {
        return modifyCaseStatement(kejs, index, statement, Operation.ADD);
    }
    
    public CaseTree removeCaseStatement(CaseTree kejs, StatementTree statement) {
        return modifyCaseStatement(kejs, -1, statement, Operation.REMOVE);
    }

    public CaseTree removeCaseStatement(CaseTree kejs, int index) {
        return modifyCaseStatement(kejs, index, null, Operation.REMOVE);
    }
    
    private CaseTree modifyCaseStatement(CaseTree kejs, int index, StatementTree statement, Operation op) {
        CaseTree copy = Case(
                kejs.getExpression(),
                c(kejs.getStatements(), index, statement, op)
        );
        return copy;
    }

    // ClassTree
    public ClassTree addClassMember(ClassTree clazz, Tree member) {
        return modifyClassMember(clazz, -1, member, Operation.ADD);
    }
    
    public ClassTree insertClassMember(ClassTree clazz, int index, Tree member) {
        return modifyClassMember(clazz, index, member, Operation.ADD);
    }
    
    public ClassTree removeClassMember(ClassTree clazz, Tree member) {
        return modifyClassMember(clazz, -1, member, Operation.REMOVE);
    }
    
    public ClassTree removeClassMember(ClassTree clazz, int index) {
        return modifyClassMember(clazz, index, null, Operation.REMOVE);
    }
    
    private ClassTree modifyClassMember(ClassTree clazz, int index, Tree member, Operation op) {
        ClassTree copy = Class(
            clazz.getModifiers(),
            clazz.getSimpleName(),
            clazz.getTypeParameters(),
            clazz.getExtendsClause(),
            (List<ExpressionTree>) clazz.getImplementsClause(),
            c(clazz.getMembers(), index, member, op)
        );
        return copy;
    }
    
    public ClassTree addClassTypeParameter(ClassTree clazz, TypeParameterTree typeParameter) {
        return modifyClassTypeParameter(clazz, -1, typeParameter, Operation.ADD);
    }
    
    public ClassTree insertClassTypeParameter(ClassTree clazz, int index, TypeParameterTree typeParameter) {
        return modifyClassTypeParameter(clazz, index, typeParameter, Operation.ADD);
    }

    public ClassTree removeClassTypeParameter(ClassTree clazz, TypeParameterTree typeParameter) {
        return modifyClassTypeParameter(clazz, -1, typeParameter, Operation.REMOVE);
    }
    
    public ClassTree removeClassTypeParameter(ClassTree clazz, int index) {
        return modifyClassTypeParameter(clazz, index, null, Operation.REMOVE);
    }

    private ClassTree modifyClassTypeParameter(ClassTree clazz, int index, TypeParameterTree typeParameter, Operation op) {
        ClassTree copy = Class(
            clazz.getModifiers(),
            clazz.getSimpleName(),
            c(clazz.getTypeParameters(), index, typeParameter, op),
            clazz.getExtendsClause(),
            (List<ExpressionTree>) clazz.getImplementsClause(),
            clazz.getMembers()
        );
        return copy;
    }
    
    public ClassTree addClassImplementsClause(ClassTree clazz, Tree implementsClause) {
        return modifyClassImplementsClause(clazz, -1, implementsClause, Operation.ADD);
    }

    public ClassTree insertClassImplementsClause(ClassTree clazz, int index, Tree implementsClause) {
        return modifyClassImplementsClause(clazz, index, implementsClause, Operation.ADD);
    }
    
    public ClassTree removeClassImplementsClause(ClassTree clazz, Tree implementsClause) {
        return modifyClassImplementsClause(clazz, -1, implementsClause, Operation.REMOVE);
    }

    public ClassTree removeClassImplementsClause(ClassTree clazz, int index) {
        return modifyClassImplementsClause(clazz, index, null, Operation.REMOVE);
    }
    
    private ClassTree modifyClassImplementsClause(ClassTree clazz, int index, Tree implementsClause, Operation op) {
        ClassTree copy = Class(
            clazz.getModifiers(),
            clazz.getSimpleName(),
            clazz.getTypeParameters(),
            clazz.getExtendsClause(),
            c((List<ExpressionTree>) clazz.getImplementsClause(), index, implementsClause, op), // todo: cast!
            clazz.getMembers()
        );
        return copy;
    }
    
    // CompilationUnit
    public CompilationUnitTree addCompUnitTypeDecl(CompilationUnitTree compilationUnit, Tree typeDeclaration) {
        return modifyCompUnitTypeDecl(compilationUnit, -1, typeDeclaration, Operation.ADD);
    }
    
    public CompilationUnitTree insertCompUnitTypeDecl(CompilationUnitTree compilationUnit, int index, Tree typeDeclaration) {
        return modifyCompUnitTypeDecl(compilationUnit, index, typeDeclaration, Operation.ADD);
    }
    
    public CompilationUnitTree removeCompUnitTypeDecl(CompilationUnitTree compilationUnit, Tree typeDeclaration) {
        return modifyCompUnitTypeDecl(compilationUnit, -1, typeDeclaration, Operation.REMOVE);
    }
    
    public CompilationUnitTree removeCompUnitTypeDecl(CompilationUnitTree compilationUnit, int index) {
        return modifyCompUnitTypeDecl(compilationUnit, index, null, Operation.REMOVE);
    }
    
    private CompilationUnitTree modifyCompUnitTypeDecl(CompilationUnitTree compilationUnit, int index, Tree typeDeclaration, Operation op) {
        CompilationUnitTree copy = CompilationUnit(
            compilationUnit.getPackageAnnotations(),
            compilationUnit.getPackageName(),
            compilationUnit.getImports(),
            c(compilationUnit.getTypeDecls(), index, typeDeclaration, op),
            compilationUnit.getSourceFile()
        );
        return copy;
    }
    
    // CompilationUnit
    public CompilationUnitTree addCompUnitImport(CompilationUnitTree compilationUnit, ImportTree importt) {
        return modifyCompUnitImport(compilationUnit, -1, importt, Operation.ADD);
    }
    
    public CompilationUnitTree insertCompUnitImport(CompilationUnitTree compilationUnit, int index, ImportTree importt) {
        return modifyCompUnitImport(compilationUnit, index, importt, Operation.ADD);
    }
    
    public CompilationUnitTree removeCompUnitImport(CompilationUnitTree compilationUnit, ImportTree importt) {
        return modifyCompUnitImport(compilationUnit, -1, importt, Operation.REMOVE);
    }
    
    public CompilationUnitTree removeCompUnitImport(CompilationUnitTree compilationUnit, int index) {
        return modifyCompUnitImport(compilationUnit, index, null, Operation.REMOVE);
    }
    
    private CompilationUnitTree modifyCompUnitImport(CompilationUnitTree compilationUnit, int index, ImportTree importt, Operation op) {
        CompilationUnitTree copy = CompilationUnit(
            compilationUnit.getPackageAnnotations(),
            compilationUnit.getPackageName(),
            c(compilationUnit.getImports(), index, importt, op),
            compilationUnit.getTypeDecls(),
            compilationUnit.getSourceFile()
        );
        return copy;
    }
    
    public CompilationUnitTree addPackageAnnotation(CompilationUnitTree cut, AnnotationTree annotation) {
        return modifyPackageAnnotation(cut, -1, annotation, Operation.ADD);
    }

    public CompilationUnitTree insertPackageAnnotation(CompilationUnitTree cut, int index, AnnotationTree annotation) {
        return modifyPackageAnnotation(cut, index, annotation, Operation.ADD);
    }

    public CompilationUnitTree removePackageAnnotation(CompilationUnitTree cut, AnnotationTree annotation) {
        return modifyPackageAnnotation(cut, -1, annotation, Operation.REMOVE);
    }

    public CompilationUnitTree removePackageAnnotation(CompilationUnitTree cut, int index) {
        return modifyPackageAnnotation(cut, index, null, Operation.REMOVE);
    }

    private CompilationUnitTree modifyPackageAnnotation(CompilationUnitTree cut, int index, AnnotationTree annotation, Operation op) {
        CompilationUnitTree copy = CompilationUnit(
                c(cut.getPackageAnnotations(), index, annotation, op),
                cut.getPackageName(),
                cut.getImports(),
                cut.getTypeDecls(),
                cut.getSourceFile()
        );
        return copy;
    }

    /** ErroneousTree */
    
    // ForLoop
    public ForLoopTree addForLoopInitializer(ForLoopTree forLoop, StatementTree statement) {
        return modifyForLoopInitializer(forLoop, -1, statement, Operation.ADD);
    }
    
    public ForLoopTree insertForLoopInitializer(ForLoopTree forLoop, int index, StatementTree statement) {
        return modifyForLoopInitializer(forLoop, index, statement, Operation.ADD);
    }

    public ForLoopTree removeForLoopInitializer(ForLoopTree forLoop, StatementTree statement) {
        return modifyForLoopInitializer(forLoop, -1, statement, Operation.REMOVE);
    }
    
    public ForLoopTree removeForLoopInitializer(ForLoopTree forLoop, int index) {
        return modifyForLoopInitializer(forLoop, index, null, Operation.REMOVE);
    }
    
    private ForLoopTree modifyForLoopInitializer(ForLoopTree forLoop, int index, StatementTree statement, Operation op) {
        ForLoopTree copy = ForLoop(
            c(forLoop.getInitializer(), index, statement, op),
            forLoop.getCondition(),
            forLoop.getUpdate(),
            forLoop.getStatement()
        );
        return copy;
    }
    
    public ForLoopTree addForLoopUpdate(ForLoopTree forLoop, ExpressionStatementTree update) {
        return modifyForLoopUpdate(forLoop, -1, update, Operation.ADD);
    }
    
    public ForLoopTree insertForLoopUpdate(ForLoopTree forLoop, int index, ExpressionStatementTree update) {
        return modifyForLoopUpdate(forLoop, index, update, Operation.ADD);
    }
    
    public ForLoopTree removeForLoopUpdate(ForLoopTree forLoop, ExpressionStatementTree update) {
        return modifyForLoopUpdate(forLoop, -1, update, Operation.REMOVE);
    }
    
    public ForLoopTree removeForLoopUpdate(ForLoopTree forLoop, int index) {
        return modifyForLoopUpdate(forLoop, index, null, Operation.REMOVE);
    }

    private ForLoopTree modifyForLoopUpdate(ForLoopTree forLoop, int index, ExpressionStatementTree update, Operation op) {
        ForLoopTree copy = ForLoop(
            forLoop.getInitializer(),
            forLoop.getCondition(),
            c(forLoop.getUpdate(), index, update, op),
            forLoop.getStatement()
        );
        return copy;
    }
    
    // MethodInvocation
    public MethodInvocationTree addMethodInvocationArgument(MethodInvocationTree methodInvocation, ExpressionTree argument) {
        return modifyMethodInvocationArgument(methodInvocation, -1, argument, Operation.ADD);
    }
    
    public MethodInvocationTree insertMethodInvocationArgument(MethodInvocationTree methodInvocation, int index, ExpressionTree argument) {
        return modifyMethodInvocationArgument(methodInvocation, index, argument, Operation.ADD);
    }

    public MethodInvocationTree removeMethodInvocationArgument(MethodInvocationTree methodInvocation, ExpressionTree argument) {
        return modifyMethodInvocationArgument(methodInvocation, -1, argument, Operation.REMOVE);
    }
    
    public MethodInvocationTree removeMethodInvocationArgument(MethodInvocationTree methodInvocation, int index) {
        return modifyMethodInvocationArgument(methodInvocation, index, null, Operation.REMOVE);
    }
    
    private MethodInvocationTree modifyMethodInvocationArgument(MethodInvocationTree methodInvocation, int index, ExpressionTree argument, Operation op) {
        MethodInvocationTree copy = MethodInvocation(
            (List<? extends ExpressionTree>) methodInvocation.getTypeArguments(),
            methodInvocation.getMethodSelect(),
            c(methodInvocation.getArguments(), index, argument, op)
        );
        return copy;
    }
    
    public MethodInvocationTree addMethodInvocationTypeArgument(MethodInvocationTree methodInvocation, ExpressionTree typeArgument) {
        return modifyMethodInvocationTypeArgument(methodInvocation, -1, typeArgument, Operation.ADD);
    }
    
    public MethodInvocationTree insertMethodInvocationTypeArgument(MethodInvocationTree methodInvocation, int index, ExpressionTree typeArgument) {
        return modifyMethodInvocationTypeArgument(methodInvocation, index, typeArgument, Operation.ADD);
    }

    public MethodInvocationTree removeMethodInvocationTypeArgument(MethodInvocationTree methodInvocation, ExpressionTree typeArgument) {
        return modifyMethodInvocationTypeArgument(methodInvocation, -1, typeArgument, Operation.REMOVE);
    }
    
    public MethodInvocationTree removeMethodInvocationTypeArgument(MethodInvocationTree methodInvocation, int index) {
        return modifyMethodInvocationArgument(methodInvocation, index, null, Operation.REMOVE);
    }
    
    private MethodInvocationTree modifyMethodInvocationTypeArgument(MethodInvocationTree methodInvocation, int index, ExpressionTree typeArgument, Operation op) {
        MethodInvocationTree copy = MethodInvocation(
            c((List<? extends ExpressionTree>) methodInvocation.getTypeArguments(), index, typeArgument, op),
            methodInvocation.getMethodSelect(),
            methodInvocation.getArguments()
        );
        return copy;
    }
    
    // Method
    public MethodTree addMethodTypeParameter(MethodTree method, TypeParameterTree typeParameter) {
        return modifyMethodTypeParameter(method, -1, typeParameter, Operation.ADD);
    }

    public MethodTree insertMethodTypeParameter(MethodTree method, int index, TypeParameterTree typeParameter) {
        return modifyMethodTypeParameter(method, index, typeParameter, Operation.ADD);
    }

    public MethodTree removeMethodTypeParameter(MethodTree method, TypeParameterTree typeParameter) {
        return modifyMethodTypeParameter(method, -1, typeParameter, Operation.REMOVE);
    }

    public MethodTree removeMethodTypeParameter(MethodTree method, int index) {
        return modifyMethodTypeParameter(method, index, null, Operation.REMOVE);
    }
    
    private MethodTree modifyMethodTypeParameter(MethodTree method, int index, TypeParameterTree typeParameter, Operation op) {
        MethodTree copy = Method(
                method.getModifiers(),
                method.getName(),
                method.getReturnType(),
                c(method.getTypeParameters(), index, typeParameter, op),
                method.getParameters(),
                method.getThrows(),
                method.getBody(),
                (ExpressionTree) method.getDefaultValue()
        );
        return copy;
    }
    
    public MethodTree addMethodParameter(MethodTree method, VariableTree parameter) {
        return modifyMethodParameter(method, -1, parameter, Operation.ADD);
    }

    public MethodTree insertMethodParameter(MethodTree method, int index, VariableTree parameter) {
        return modifyMethodParameter(method, index, parameter, Operation.ADD);
    }
    
    public MethodTree removeMethodParameter(MethodTree method, VariableTree parameter) {
        return modifyMethodParameter(method, -1, parameter, Operation.REMOVE);
    }

    public MethodTree removeMethodParameter(MethodTree method, int index) {
        return modifyMethodParameter(method, index, null, Operation.REMOVE);
    }
    
    private MethodTree modifyMethodParameter(MethodTree method, int index, VariableTree parameter, Operation op) {
        MethodTree copy = Method(
                method.getModifiers(),
                method.getName(),
                method.getReturnType(),
                method.getTypeParameters(),
                c(method.getParameters(), index, parameter, op),
                method.getThrows(),
                method.getBody(),
                (ExpressionTree) method.getDefaultValue()
        );
        return copy;
    }
    
    public MethodTree addMethodThrows(MethodTree method, ExpressionTree throwz) {
        return modifyMethodThrows(method, -1, throwz, Operation.ADD);
    }
    
    public MethodTree insertMethodThrows(MethodTree method, int index, ExpressionTree throwz) {
        return modifyMethodThrows(method, index, throwz, Operation.ADD);
    }
    
    public MethodTree removeMethodThrows(MethodTree method, ExpressionTree throwz) {
        return modifyMethodThrows(method, -1, throwz, Operation.REMOVE);
    }
    
    public MethodTree removeMethodThrows(MethodTree method, int index) {
        return modifyMethodThrows(method, index, null, Operation.REMOVE);
    }
    
    private MethodTree modifyMethodThrows(MethodTree method, int index, ExpressionTree throwz, Operation op) {
        MethodTree copy = Method(
                method.getModifiers(),
                method.getName(),
                method.getReturnType(),
                method.getTypeParameters(),
                method.getParameters(),
                c(method.getThrows(), index, throwz, op),
                method.getBody(),
                (ExpressionTree) method.getDefaultValue()
        );
        return copy;
    }
    
    // Modifiers
    public ModifiersTree addModifiersAnnotation(ModifiersTree modifiers, AnnotationTree annotation) {
        return modifyModifiersAnnotation(modifiers, -1, annotation, Operation.ADD);
    }

    public ModifiersTree insertModifiersAnnotation(ModifiersTree modifiers, int index, AnnotationTree annotation) {
        return modifyModifiersAnnotation(modifiers, index, annotation, Operation.ADD);
    }
    
    public ModifiersTree removeModifiersAnnotation(ModifiersTree modifiers, AnnotationTree annotation) {
        return modifyModifiersAnnotation(modifiers, -1, annotation, Operation.REMOVE);
    }

    public ModifiersTree removeModifiersAnnotation(ModifiersTree modifiers, int index) {
        return modifyModifiersAnnotation(modifiers, index, null, Operation.REMOVE);
    }
    
    private ModifiersTree modifyModifiersAnnotation(ModifiersTree modifiers, int index, AnnotationTree annotation, Operation op) {
        ModifiersTree copy = Modifiers(
            ((JCModifiers) modifiers).flags,
            c(modifiers.getAnnotations(), index, annotation, op)
        );
        return copy;
    }
    
    // NewArray
    public NewArrayTree addNewArrayDimension(NewArrayTree newArray, ExpressionTree dimension) {
        return modifyNewArrayDimension(newArray, -1, dimension, Operation.ADD);
    }

    public NewArrayTree insertNewArrayDimension(NewArrayTree newArray, int index, ExpressionTree dimension) {
        return modifyNewArrayDimension(newArray, index, dimension, Operation.ADD);
    }
    
    public NewArrayTree removeNewArrayDimension(NewArrayTree newArray, ExpressionTree dimension) {
        return modifyNewArrayDimension(newArray, -1, dimension, Operation.REMOVE);
    }

    public NewArrayTree removeNewArrayDimension(NewArrayTree newArray, int index) {
        return modifyNewArrayDimension(newArray, index, null, Operation.REMOVE);
    }
    
    private NewArrayTree modifyNewArrayDimension(NewArrayTree newArray, int index, ExpressionTree dimension, Operation op) {
        NewArrayTree copy = NewArray(
            newArray.getType(),
            c(newArray.getDimensions(), index, dimension, op),
            newArray.getInitializers()
        );
        return copy;
    }
    
    public NewArrayTree addNewArrayInitializer(NewArrayTree newArray, ExpressionTree initializer) {
        return modifyNewArrayInitializer(newArray, -1, initializer, Operation.ADD);
    }

    public NewArrayTree insertNewArrayInitializer(NewArrayTree newArray, int index, ExpressionTree initializer) {
        return modifyNewArrayInitializer(newArray, index, initializer, Operation.ADD);
    }
    
    public NewArrayTree removeNewArrayInitializer(NewArrayTree newArray, ExpressionTree initializer) {
        return modifyNewArrayInitializer(newArray, -1, initializer, Operation.REMOVE);
    }

    public NewArrayTree removeNewArrayInitializer(NewArrayTree newArray, int index) {
        return modifyNewArrayInitializer(newArray, index, null, Operation.REMOVE);
    }
    
    private NewArrayTree modifyNewArrayInitializer(NewArrayTree newArray, int index, ExpressionTree initializer, Operation op) {
        NewArrayTree copy = NewArray(
            newArray.getType(),
            newArray.getDimensions(),
            c(newArray.getInitializers(), index, initializer, op)
        );
        return copy;
    }
    
    // NewClass
    public NewClassTree addNewClassArgument(NewClassTree newClass, ExpressionTree argument) {
        return modifyNewClassArgument(newClass, -1, argument, Operation.ADD);
    }

    public NewClassTree insertNewClassArgument(NewClassTree newClass, int index, ExpressionTree argument) {
        return modifyNewClassArgument(newClass, index, argument, Operation.ADD);
    }

    public NewClassTree removeNewClassArgument(NewClassTree newClass, ExpressionTree argument) {
        return modifyNewClassArgument(newClass, -1, argument, Operation.REMOVE);
    }

    public NewClassTree removeNewClassArgument(NewClassTree newClass, int index) {
        return modifyNewClassArgument(newClass, index, null, Operation.REMOVE);
    }
    
    private NewClassTree modifyNewClassArgument(NewClassTree newClass, int index, ExpressionTree argument, Operation op) {
        NewClassTree copy = NewClass(
            newClass.getEnclosingExpression(),
            (List<ExpressionTree>) newClass.getTypeArguments(),
            newClass.getIdentifier(),
            c(newClass.getArguments(), index, argument, op),
            newClass.getClassBody()
        );
        return copy;
    }

    public NewClassTree addNewClassTypeArgument(NewClassTree newClass, ExpressionTree typeArgument) {
        return modifyNewClassTypeArgument(newClass, -1, typeArgument, Operation.ADD);
    }

    public NewClassTree insertNewClassTypeArgument(NewClassTree newClass, int index, ExpressionTree typeArgument) {
        return modifyNewClassTypeArgument(newClass, index, typeArgument, Operation.ADD);
    }

    public NewClassTree removeNewClassTypeArgument(NewClassTree newClass, ExpressionTree typeArgument) {
        return modifyNewClassTypeArgument(newClass, -1, typeArgument, Operation.REMOVE);
    }

    public NewClassTree removeNewClassTypeArgument(NewClassTree newClass, int index) {
        return modifyNewClassTypeArgument(newClass, index, null, Operation.REMOVE);
    }
    
    private NewClassTree modifyNewClassTypeArgument(NewClassTree newClass, int index, ExpressionTree typeArgument, Operation op) {
        NewClassTree copy = NewClass(
            newClass.getEnclosingExpression(),
            c((List<ExpressionTree>) newClass.getTypeArguments(), index, typeArgument, op),
            newClass.getIdentifier(),
            newClass.getArguments(),
            newClass.getClassBody()
        );
        return copy;
    }
    
    // ParameterizedType
    public ParameterizedTypeTree addParameterizedTypeTypeArgument(ParameterizedTypeTree parameterizedType, ExpressionTree argument) {
        return modifyParameterizedTypeTypeArgument(parameterizedType, -1, argument, Operation.ADD);
    }

    public ParameterizedTypeTree insertParameterizedTypeTypeArgument(ParameterizedTypeTree parameterizedType, int index, ExpressionTree argument) {
        return modifyParameterizedTypeTypeArgument(parameterizedType, index, argument, Operation.ADD);
    }
    
    public ParameterizedTypeTree removeParameterizedTypeTypeArgument(ParameterizedTypeTree parameterizedType, ExpressionTree argument) {
        return modifyParameterizedTypeTypeArgument(parameterizedType, -1, argument, Operation.REMOVE);
    }

    public ParameterizedTypeTree removeParameterizedTypeTypeArgument(ParameterizedTypeTree parameterizedType, int index) {
        return modifyParameterizedTypeTypeArgument(parameterizedType, index, null, Operation.REMOVE);
    }
    
    private ParameterizedTypeTree modifyParameterizedTypeTypeArgument(ParameterizedTypeTree parameterizedType, int index, ExpressionTree argument, Operation op) {
        ParameterizedTypeTree copy = ParameterizedType(
            parameterizedType.getType(),
            c((List<ExpressionTree>) parameterizedType.getTypeArguments(), index, argument, op)
        );
        return copy;
    }

    // Switch
    public SwitchTree addSwitchCase(SwitchTree swic, CaseTree kejs) {
        return modifySwitchCase(swic, -1, kejs, Operation.ADD);
    }

    public SwitchTree insertSwitchCase(SwitchTree swic, int index, CaseTree kejs) {
        return modifySwitchCase(swic, index, kejs, Operation.ADD);
    }

    public SwitchTree removeSwitchCase(SwitchTree swic, CaseTree kejs) {
        return modifySwitchCase(swic, -1, kejs, Operation.REMOVE);
    }

    public SwitchTree removeSwitchCase(SwitchTree swic, int index) {
        return modifySwitchCase(swic, index, null, Operation.REMOVE);
    }

    private SwitchTree modifySwitchCase(SwitchTree swic, int index, CaseTree kejs, Operation op) {
        SwitchTree copy = Switch(
            swic.getExpression(),
            c(swic.getCases(), index, kejs, op)
        );
        return copy;
    }
    
    // Try
    public TryTree addTryCatch(TryTree traj, CatchTree kec) {
        return modifyTryCatch(traj, -1, kec, Operation.ADD);
    }
    
    public TryTree insertTryCatch(TryTree traj, int index, CatchTree kec) {
        return modifyTryCatch(traj, index, kec, Operation.ADD);
    }
    
    public TryTree removeTryCatch(TryTree traj, CatchTree kec) {
        return modifyTryCatch(traj, -1, kec, Operation.REMOVE);
    }
    
    public TryTree removeTryCatch(TryTree traj, int index) {
        return modifyTryCatch(traj, index, null, Operation.REMOVE);
    }

    private TryTree modifyTryCatch(TryTree traj, int index, CatchTree kec, Operation op) {
        TryTree copy = Try(
            traj.getResources(),
            traj.getBlock(),
            c(traj.getCatches(), index, kec, op),
            traj.getFinallyBlock()
        );
        return copy;
    }
            
    public TypeParameterTree addTypeParameterBound(TypeParameterTree typeParameter, ExpressionTree bound) {
        return modifyTypeParameterBound(typeParameter, -1, bound, Operation.ADD);
    }

    public TypeParameterTree insertTypeParameterBound(TypeParameterTree typeParameter, int index, ExpressionTree bound) {
        return modifyTypeParameterBound(typeParameter, index, bound, Operation.ADD);
    }
    
    public TypeParameterTree removeTypeParameterBound(TypeParameterTree typeParameter, ExpressionTree bound) {
        return modifyTypeParameterBound(typeParameter, -1, bound, Operation.REMOVE);
    }

    public TypeParameterTree removeTypeParameterBound(TypeParameterTree typeParameter, int index) {
        return modifyTypeParameterBound(typeParameter, index, null, Operation.REMOVE);
    }
            
    private TypeParameterTree modifyTypeParameterBound(TypeParameterTree typeParameter, int index, ExpressionTree bound, Operation op) {
        TypeParameterTree copy = TypeParameter(
            typeParameter.getName(),
            c((List<ExpressionTree>) typeParameter.getBounds(), index, bound, op)
        );
        return copy;
    }
    
    public LambdaExpressionTree addLambdaParameter(LambdaExpressionTree method, VariableTree parameter) {
        return modifyLambdaParameter(method, -1, parameter, Operation.ADD);
    }

    public LambdaExpressionTree insertLambdaParameter(LambdaExpressionTree method, int index, VariableTree parameter) {
        return modifyLambdaParameter(method, index, parameter, Operation.ADD);
    }
    
    public LambdaExpressionTree removeLambdaParameter(LambdaExpressionTree method, VariableTree parameter) {
        return modifyLambdaParameter(method, -1, parameter, Operation.REMOVE);
    }

    public LambdaExpressionTree removeLambdaParameter(LambdaExpressionTree method, int index) {
        return modifyLambdaParameter(method, index, null, Operation.REMOVE);
    }
    
    private LambdaExpressionTree modifyLambdaParameter(LambdaExpressionTree method, int index, VariableTree parameter, Operation op) {
        LambdaExpressionTree copy = LambdaExpression(
                c(method.getParameters(), index, parameter, op),
                method.getBody()
        );
        return copy;
    }
    
    public LambdaExpressionTree setLambdaBody(LambdaExpressionTree method, Tree newBody) {
        return LambdaExpression(method.getParameters(),newBody);
    }
    
    private <E extends Tree> List<E> c(List<? extends E> originalList, int index, E item, Operation operation) {
        List<E> copy = new ArrayList<E>(originalList);
        switch (operation) {
            case ADD:
                if (index > -1) {
                    copy.add(index, item);
                } else {
                    copy.add(item);
                }
                break;
            case REMOVE:
                if (index > -1) {
                    copy.remove(index);
                } else {
                    copy.remove(item);
                }
                break;
        }
        return copy;
    }
    
    /**
     * Represents operation on list
     */
    private static enum Operation {
        /** list's add operation */
        ADD,
        
        /** list's remove operation */
        REMOVE
    }
    
    private List<TypeMirror> typesFromTrees(List<? extends Tree> trees) {
        List<TypeMirror> types = new ArrayList<TypeMirror>();
        for (Tree t : trees)
            types.add(model.getType(t));
        return types;
    }
    
    private ClassTree Class(long modifiers, 
                     com.sun.tools.javac.util.List<JCAnnotation> annotations,
                     CharSequence simpleName,
                     List<? extends TypeParameterTree> typeParameters,
                     Tree extendsClause,
                     List<? extends Tree> implementsClauses,
                     List<? extends Tree> memberDecls) {
        ListBuffer<JCTypeParameter> typarams = new ListBuffer<JCTypeParameter>();
        for (TypeParameterTree t : typeParameters)
            typarams.append((JCTypeParameter)t);
        ListBuffer<JCExpression> impls = new ListBuffer<JCExpression>();
        for (Tree t : implementsClauses)
            impls.append((JCExpression)t);
        ListBuffer<JCTree> defs = new ListBuffer<JCTree>();
        for (Tree t : memberDecls)
            defs.append((JCTree)t);
        return make.at(NOPOS).ClassDef(make.at(NOPOS).Modifiers(modifiers, annotations),
                             names.fromString(simpleName.toString()),
                             typarams.toList(),
                             (JCExpression)extendsClause,
                             impls.toList(),
                             defs.toList());
        
    }
    
    private long getBitFlags(Set<Modifier> modifiers) {
        int flags  = 0;
        for (Modifier modifier : modifiers) {
            switch (modifier) {
                case PUBLIC:       flags |= PUBLIC; break;
                case PROTECTED:    flags |= PROTECTED; break;
                case PRIVATE:      flags |= PRIVATE; break;   
                case ABSTRACT:     flags |= ABSTRACT; break;  
                case STATIC:       flags |= STATIC; break;    
                case FINAL:        flags |= FINAL; break;     
                case TRANSIENT:    flags |= TRANSIENT; break; 
                case VOLATILE:     flags |= VOLATILE; break;  
                case SYNCHRONIZED: flags |= SYNCHRONIZED; break;
                case NATIVE:       flags |= NATIVE; break;
                case STRICTFP:     flags |= STRICTFP; break;
                default:
                    break;
            }
        }
        return flags;
    }
    
    
    private DocTreeMaker docMake;

    public AttributeTree Attribute(CharSequence name, AttributeTree.ValueKind vkind, List<? extends DocTree> value) {
        ListBuffer<DCTree> lb = null;
        if(value != null) {
            lb = new ListBuffer<DCTree>();
            for (DocTree t : value) {
                lb.append((DCTree) t);
            }
        }
        return docMake.at(NOPOS).Attribute((Name) names.fromString(name.toString()), vkind, lb != null ? lb.toList() : null);
    }

    public AuthorTree Author(List<? extends DocTree> name) {
        ListBuffer<DCTree> lb = new ListBuffer<DCTree>();
        for (DocTree t : name) {
            lb.append((DCTree) t);
        }
        return docMake.at(NOPOS).Author(lb.toList());
    }

    public DeprecatedTree Deprecated(List<? extends DocTree> text) {
        ListBuffer<DCTree> txt = new ListBuffer<DCTree>();
        for (DocTree t : text) {
            txt.append((DCTree) t);
        }
        return docMake.at(NOPOS).Deprecated(txt.toList());
    }
    
    public DocCommentTree DocComment(List<? extends DocTree> firstSentence, List<? extends DocTree> body, List<? extends DocTree> tags) {
        ListBuffer<DCTree> fs = new ListBuffer<DCTree>();
        for (DocTree t : firstSentence) {
            fs.append((DCTree) t);
        }
        ListBuffer<DCTree> bd = new ListBuffer<DCTree>();
        for (DocTree t : body) {
            bd.append((DCTree) t);
        }
        ListBuffer<DCTree> tg = new ListBuffer<DCTree>();
        for (DocTree t : tags) {
            tg.append((DCTree) t);
        }
        return docMake.at(NOPOS).DocComment(null, fs.toList(), bd.toList(), tg.toList());
    }
    
    public com.sun.source.doctree.ErroneousTree Erroneous(String text, DiagnosticSource diagSource, String code, Object... args) {
        String msg = "Erroneous tree implemented: "
                + text
                + " " + code;
        throw new AssertionError(msg);
    }

    public ParamTree Param(boolean isTypeParameter, com.sun.source.doctree.IdentifierTree name, List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).Param(isTypeParameter, (DCTree.DCIdentifier)name, desc.toList());
    }
    
    public LinkTree Link(ReferenceTree ref, List<? extends DocTree> label) {
        ListBuffer<DCTree> lbl = new ListBuffer<DCTree>();
        for (DocTree t : label) {
            lbl.append((DCTree) t);
        }
        return docMake.at(NOPOS).Link((DCTree.DCReference)ref, lbl.toList());
    }
    
    public com.sun.source.doctree.LiteralTree Literal(com.sun.source.doctree.TextTree text) {
        return docMake.at(NOPOS).Literal((DCText)text);
    }
    
    public com.sun.source.doctree.ReturnTree Return(List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).Return(desc.toList());
    }
    
    public SeeTree See(List<? extends DocTree> reference) {
        ListBuffer<DCTree> ref = new ListBuffer<DCTree>();
        for (DocTree t : reference) {
            ref.append((DCTree) t);
        }
        return docMake.at(NOPOS).See(ref.toList());
    }
    
    public SerialTree Serial(List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).Serial(desc.toList());
    }
    
    public SerialDataTree SerialData(List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).SerialData(desc.toList());
    }
    
    public SerialFieldTree SerialField(com.sun.source.doctree.IdentifierTree name, ReferenceTree type, List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).SerialField((DCIdentifier)name, (DCReference)type, desc.toList());
    }
    
    public SinceTree Since(List<? extends DocTree> text) {
        ListBuffer<DCTree> txt = new ListBuffer<DCTree>();
        for (DocTree t : text) {
            txt.append((DCTree) t);
        }
        return docMake.at(NOPOS).Since(txt.toList());
    }
    
    public StartElementTree StartElement(CharSequence name, List<? extends DocTree> attrs, boolean selfClosing) {
        ListBuffer<DCTree> atr = new ListBuffer<DCTree>();
        for (DocTree t : attrs) {
            atr.append((DCTree) t);
        }
        return docMake.at(NOPOS).StartElement(names.fromString(name.toString()), atr.toList(), selfClosing);
    }
    
    public TextTree Text(String text) {
        return docMake.at(NOPOS).Text(text);
    }
    
    public ThrowsTree Throws(ReferenceTree name, List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).Throws((DCReference)name, desc.toList());
    }
    
    public UnknownBlockTagTree UnknownBlockTag(CharSequence name, List<? extends DocTree> content) {
        ListBuffer<DCTree> cont = new ListBuffer<DCTree>();
        for (DocTree t : content) {
            cont.append((DCTree) t);
        }
        return docMake.at(NOPOS).UnknownBlockTag(names.fromString(name.toString()), cont.toList());
    }

    public UnknownInlineTagTree UnknownInlineTag(CharSequence name, List<? extends DocTree> content) {
        ListBuffer<DCTree> cont = new ListBuffer<DCTree>();
        for (DocTree t : content) {
            cont.append((DCTree) t);
        }
        return docMake.at(NOPOS).UnknownInlineTag(names.fromString(name.toString()), cont.toList());
    }
    
    public ValueTree Value(ReferenceTree ref) {
        return docMake.at(NOPOS).Value((DCReference)ref);
    }

    public VersionTree Version(List<? extends DocTree> text) {
        ListBuffer<DCTree> txt = new ListBuffer<DCTree>();
        for (DocTree t : text) {
            txt.append((DCTree) t);
        }
        return docMake.at(NOPOS).Version(txt.toList());
    }
    
    public com.sun.source.doctree.LiteralTree Code(TextTree text) {
        return docMake.at(NOPOS).Code((DCText)text);
    }

    public com.sun.source.doctree.CommentTree Comment(String text) {
        return docMake.at(NOPOS).Comment(text);
    }
    public DocRootTree DocRoot() {
        return docMake.at(NOPOS).DocRoot();
    }

    public EndElementTree EndElement(CharSequence name) {
        return docMake.at(NOPOS).EndElement(names.fromString(name.toString()));
    }

    public EntityTree Entity(CharSequence name) {
        return docMake.at(NOPOS).Entity(names.fromString(name.toString()));
    }
    
    public ThrowsTree Exception(ReferenceTree name, List<? extends DocTree> description) {
        ListBuffer<DCTree> desc = new ListBuffer<DCTree>();
        for (DocTree t : description) {
            desc.append((DCTree) t);
        }
        return docMake.at(NOPOS).Exception((DCReference)name, desc.toList());
    }

    public com.sun.source.doctree.IdentifierTree DocIdentifier(CharSequence name) {
        return docMake.at(NOPOS).Identifier((Name) names.fromString(name.toString()));
    }

    public InheritDocTree InheritDoc() {
        return docMake.at(NOPOS).InheritDoc();
    }

    public LinkTree LinkPlain(ReferenceTree ref, List<? extends DocTree> label) {
        ListBuffer<DCTree> lbl = new ListBuffer<DCTree>();
        for (DocTree t : label) {
            lbl.append((DCTree) t);
        }
        return docMake.at(NOPOS).LinkPlain((DCReference)ref, lbl.toList());
    }

    public ReferenceTree Reference(ExpressionTree qualExpr, CharSequence member, List<? extends Tree> paramTypes) {
        com.sun.tools.javac.util.List<JCTree> paramTypesList = null;
        if (paramTypes != null) {
            ListBuffer<JCTree> lbl = new ListBuffer<JCTree>();
            for (Tree t : paramTypes) {
                lbl.append((JCTree) t);
            }
            paramTypesList = lbl.toList();
        }
        return docMake.at(NOPOS).Reference("", (JCExpression) qualExpr, member != null ? (Name) names.fromString(member.toString()) : null, paramTypesList);
    }
}
