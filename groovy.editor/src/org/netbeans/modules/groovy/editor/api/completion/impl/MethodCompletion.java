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

package org.netbeans.modules.groovy.editor.api.completion.impl;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.Parameter;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.groovy.editor.api.GroovyUtils;
import org.netbeans.modules.groovy.editor.api.NbUtilities;
import org.netbeans.modules.groovy.editor.api.completion.CaretLocation;
import org.netbeans.modules.groovy.editor.api.completion.CompletionItem;
import org.netbeans.modules.groovy.editor.api.completion.CompletionItem.ConstructorItem;
import org.netbeans.modules.groovy.editor.api.completion.CompletionItem.ParameterDescriptor;
import org.netbeans.modules.groovy.editor.api.completion.MethodSignature;
import org.netbeans.modules.groovy.editor.api.completion.util.CompletionRequest;
import org.netbeans.modules.groovy.editor.api.completion.util.RequestHelper;
import org.netbeans.modules.groovy.editor.completion.CompleteElementHandler;

/**
 * Complete the methods invokable on a class.
 *
 * @author Martin Janicek
 */
public class MethodCompletion extends BaseCompletion {

    private static List<String> defaultImports;

    // There attributes should be initiated after each complete() method call
    private List<CompletionProposal> proposals;
    private CompletionRequest request;
    private int anchor;


    public MethodCompletion() {
        defaultImports = new ArrayList<String>();
        defaultImports.addAll(GroovyUtils.DEFAULT_IMPORT_PACKAGES);
    }


    @Override
    public boolean complete(final List<CompletionProposal> proposals, final CompletionRequest request, final int anchor) {
        LOG.log(Level.FINEST, "-> completeMethods"); // NOI18N

        this.proposals = proposals;
        this.request = request;
        this.anchor = anchor;

        if (request.location == CaretLocation.INSIDE_PARAMETERS) {
            LOG.log(Level.FINEST, "no method completion inside of parameters"); // NOI18N
            return false;
        }

        if (request == null || request.ctx == null/* || request.ctx.before1 == null*/) {
            return false;
        }

        // check whether we are either:
        //
        // 1.) This is a constructor-call like: String s = new String|
        // 2.) right behind a dot. Then we look for:
        //     2.1  method on collection type: List, Map or Range
        //     2.2  static/instance method on class or object
        //     2.3  Get apropriate groovy-methods from index.
        //     2.4  dynamic, mixin method on Groovy-object like getXbyY()


        // 1.) Test if this is a Constructor-call?
        if (RequestHelper.isConstructorCall(request)) {
            return completeConstructor();
        }

        // 2.2  static/instance method on class or object
        if (!request.isBehindDot() && request.ctx.before1 != null) {
            LOG.log(Level.FINEST, "I'm not invoked behind a dot."); // NOI18N
            return false;
        }

        ClassNode declaringClass = RequestHelper.getBeforeDotDeclaringClass(request);

        if (declaringClass == null) {
            LOG.log(Level.FINEST, "No declaring class found"); // NOI18N
            return false;
        }

        /*
            Here we need to figure out, whether we want to complete a variable:

            s.|

            where we want to complete fields and methodes *OR* a package prefix like:

            java.|

            To achive this we only complete methods if there is no basePackage, which is a valid
            package.
         */

        PackageCompletionRequest packageRequest = getPackageRequest(request);

        if (packageRequest.basePackage.length() > 0) {
            ClasspathInfo pathInfo = getClasspathInfoFromRequest(request);

            if (isValidPackage(pathInfo, packageRequest.basePackage)) {
                LOG.log(Level.FINEST, "The string before the dot seems to be a valid package"); // NOI18N
                return false;
            }
        }

        Map<MethodSignature, ? extends CompletionItem> result = CompleteElementHandler
                .forCompilationInfo(request.info)
                    .getMethods(RequestHelper.getSurroundingClassNode(request), declaringClass, request.prefix, anchor,
                    request.dotContext != null && request.dotContext.isMethodsOnly());
        proposals.addAll(result.values());

        return true;
    }

    private boolean completeConstructor() {
        LOG.log(Level.FINEST, "This looks like a constructor ...");

        // look for all imported types starting with prefix, which have public constructors
        final JavaSource javaSource = getJavaSourceFromRequest();

        if (javaSource != null) {
            try {
                javaSource.runUserActionTask(new Task<CompilationController>() {
                    @Override
                    public void run(CompilationController info) {

                        List<Element> typelist = new ArrayList<Element>();
                        for (String singlePackage : defaultImports) {
                            typelist.addAll(getElementListForPackage(info.getElements(), singlePackage));
                        }
                        LOG.log(Level.FINEST, "Number of types found:  {0}", typelist.size());

                        if (exactConstructorExists(typelist, request.prefix)) {
                            // if we are in situation like "String s = new String|" we want to
                            // show only String constructors (not StringBuffer constructors etc.)
                            addExactProposals(typelist);
                        }
                        addProposalsForDeclaredClasses();
                    }
                }, true);
            } catch (IOException ex) {
                LOG.log(Level.FINEST, "IOException : {0}", ex.getMessage());
            }
        }

        return !proposals.isEmpty();
    }

    /**
     * Finds out if the given prefix has an exact match to a one of given types.
     *
     * @param typelist list of types for comparison
     * @param prefix prefix we are looking for
     * @return true if there is an exact match, false otherwise
     */
    private boolean exactConstructorExists(List<? extends Element> typelist, String prefix) {
        for (Element element : typelist) {
            if (prefix.toUpperCase().equals(element.getSimpleName().toString().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private void addExactProposals(List<? extends Element> typelist) {
        for (Element element : typelist) {
            // only look for classes rather than enums or interfaces
            if (element.getKind() == ElementKind.CLASS) {
                for (Element encl : element.getEnclosedElements()) {
                    if (encl.getKind() == ElementKind.CONSTRUCTOR) {
                        // we gotta get the constructors name from the type itself, since
                        // all the constructors are named <init>.
                        String constructorName = element.getSimpleName().toString();

                        if (constructorName.toUpperCase().equals(request.prefix.toUpperCase())) {
                            addProposal(constructorName, (ExecutableElement) encl);
                        }
                    }
                }
            }
        }
    }

    private void addProposal(String constructorName, ExecutableElement encl) {
        LOG.log(Level.FINEST, "Constructor call candidate added : {0}", constructorName);

        String paramListString = getParameterListForMethod(encl);
        List<ParameterDescriptor> paramList = getParameterList(encl);

        proposals.add(new ConstructorItem(constructorName, paramListString, paramList, anchor, false));
    }

    // FIXME: These should have higher priority and thus be on top of completion list
    private void addProposalsForDeclaredClasses() {
        List<ClassNode> declaredClasses = RequestHelper.getDeclaredClasses(request);

        for (ClassNode declaredClass : declaredClasses) {
            String constructorName = declaredClass.getNameWithoutPackage();

            if (isPrefixed(request, constructorName)) {
                for (ConstructorNode constructor : declaredClass.getDeclaredConstructors()) {
                    Parameter[] parameters = constructor.getParameters();
                    String paramListString = getParameterListStringForMethod(parameters);
                    List<ParameterDescriptor> paramList = getParameterListForMethod(parameters);

                    proposals.add(new ConstructorItem(constructorName, paramListString, paramList, anchor, false));
                }
            }
        }
    }

    private JavaSource getJavaSourceFromRequest() {
        ClasspathInfo pathInfo = getClasspathInfoFromRequest(request);
        assert pathInfo != null;

        JavaSource javaSource = JavaSource.create(pathInfo);

        if (javaSource == null) {
            LOG.log(Level.FINEST, "Problem retrieving JavaSource from ClassPathInfo, exiting.");
            return null;
        }

        return javaSource;
    }

    @NonNull
    private List<? extends Element> getElementListForPackage(Elements elements, final String pkg) {
        LOG.log(Level.FINEST, "getElementListForPackage(), Package :  {0}", pkg);

        if (elements != null && pkg != null) {
            LOG.log(Level.FINEST, "TypeSearcherHelper.run(), elements retrieved");
            PackageElement packageElement = elements.getPackageElement(pkg);

            if (packageElement == null) {
                LOG.log(Level.FINEST, "packageElement is null");
            } else {
                return packageElement.getEnclosedElements();
            }
        }

        LOG.log(Level.FINEST, "Returning Typlist");
        return Collections.emptyList();
    }

    /**
     * Get the list of parameters of this executable as a List of <code>ParameterDescriptor</code>'s
     * To be used in insert templates and pretty-printers.
     * 
     * @param exe executable element
     * @return list of <code>ParameterDescriptor</code>'s
     */
    private List<CompletionItem.ParameterDescriptor> getParameterList(ExecutableElement exe) {
        List<CompletionItem.ParameterDescriptor> paramList = new ArrayList<CompletionItem.ParameterDescriptor>();

        if (exe != null) {
            try {
                // generate a list of parameters
                // unfortunately, we have to work around # 139695 in an ugly fashion
                List<? extends VariableElement> params = exe.getParameters(); // this can cause NPE's
                int i = 1;

                for (VariableElement variableElement : params) {
                    TypeMirror tm = variableElement.asType();

                    String fullName = tm.toString();
                    String name = fullName;

                    if (tm.getKind() == javax.lang.model.type.TypeKind.DECLARED) {
                        name = NbUtilities.stripPackage(fullName);
                    }

                    // todo: this needs to be replaced with values from the JavaDoc
                    String varName = "param" + String.valueOf(i);

                    paramList.add(new CompletionItem.ParameterDescriptor(fullName, name, varName));

                    i++;
                }
            } catch (NullPointerException e) {
                // simply do nothing.
            }
        }

        return paramList;
    }

    /**
     * Get the parameter-list of this executable as String.
     *
     * @param exe
     * @return
     */
    private String getParameterListForMethod(ExecutableElement exe) {
        StringBuilder sb = new StringBuilder();

        if (exe != null) {
            try {
                // generate a list of parameters
                // unfortunately, we have to work around # 139695 in an ugly fashion
                List<? extends VariableElement> params = exe.getParameters(); // this can cause NPE's

                for (VariableElement variableElement : params) {
                    TypeMirror tm = variableElement.asType();

                    if (sb.length() > 0) {
                        sb.append(", ");
                    }

                    if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY) {
                        sb.append(NbUtilities.stripPackage(tm.toString()));
                    } else {
                        sb.append(tm.toString());
                    }
                }
            } catch (NullPointerException e) {
                // simply do nothing.
            }
        }
        return sb.toString();
    }

    /**
     * Convert given parameter array into one String.
     * For example if we send two parameters (int and String), this method will
     * return "int, String"
     *
     * @param parameters array of parameters
     * @return parameter string
     */
    private String getParameterListStringForMethod(Parameter[] parameters) {
        if (parameters.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Parameter param : parameters) {
            sb.append(param.getType().getNameWithoutPackage()).append(", ");
        }

        // remove last comma and return
        return sb.substring(0, sb.lastIndexOf(","));
    }

    /**
     * Convert given parameter array into the list of <code>ParameterDescription</code>'s
     *
     * @param parameters array of parameters
     * @return list of <code>ParameterDescription</code>'s
     */
    private List<ParameterDescriptor> getParameterListForMethod(Parameter[] parameters) {
        if (parameters.length == 0) {
            return Collections.EMPTY_LIST;
        }

        List<ParameterDescriptor> paramDescriptors = new ArrayList<ParameterDescriptor>();
        for (Parameter param : parameters) {
            String fullTypeName = param.getType().getName();
            String typeName = param.getType().getNameWithoutPackage();
            String name = param.getName();

            paramDescriptors.add(new ParameterDescriptor(fullTypeName, typeName, name));
        }
        return paramDescriptors;
    }
}
