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
package org.netbeans.modules.web.beans.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.web.beans.analysis.analizer.AnnotationAnalyzer;
import org.netbeans.modules.web.beans.analysis.analizer.ClassElementAnalyzer;
import org.netbeans.modules.web.beans.analysis.analizer.CtorAnalyzer;
import org.netbeans.modules.web.beans.analysis.analizer.ElementAnalyzer;
import org.netbeans.modules.web.beans.analysis.analizer.FieldElementAnalyzer;
import org.netbeans.modules.web.beans.analysis.analizer.MethodElementAnalyzer;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.openide.filesystems.FileObject;


/**
 * @author ads
 *
 */
class CdiEditorAnalysisTask implements CancellableTask<CompilationInfo> {
    
    CdiEditorAnalysisTask(FileObject javaFile){
        myFileObject = javaFile;
        cancel = new AtomicBoolean( false );
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.java.source.Task#run(java.lang.Object)
     */
    @Override
    public void run( CompilationInfo compInfo ) throws Exception {
        List<ErrorDescription> problems = new LinkedList<ErrorDescription>();
        
        List<? extends TypeElement> types = compInfo.getTopLevelElements();
        for (TypeElement typeElement : types) {
            if ( isCancelled() ){
                break;
            }
            analyzeType(typeElement, null,  compInfo, problems);
        }
        HintsController.setErrors(myFileObject, "CDI Analyser", problems); //NOI18N
    }
    
    private void analyzeType(TypeElement typeElement , TypeElement parent ,
            CompilationInfo compInfo, List<ErrorDescription> descriptions)
    {
        ElementKind kind = typeElement.getKind();
        ElementAnalyzer analyzer = ANALIZERS.get( kind );
        if ( analyzer != null ){
            analyzer.analyze(typeElement, parent, compInfo, descriptions, cancel);
        }
        if ( isCancelled() ){
            return;
        }
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<TypeElement> types = ElementFilter.typesIn(enclosedElements);
        for (TypeElement innerType : types) {
            analyzeType(innerType, typeElement , compInfo, descriptions);
        }
        Set<Element> enclosedSet = new HashSet<Element>( enclosedElements );
        enclosedSet.removeAll( types );
        for(Element element : enclosedSet ){
            if ( isCancelled() ){
                return;
            }
            analyzer = ANALIZERS.get( element.getKind() );
            if ( analyzer == null ){
                continue;
            }
            analyzer.analyze(element, typeElement, compInfo, descriptions,
                    cancel);
        }
    }

    /* (non-Javadoc)
     * @see org.netbeans.api.java.source.CancellableTask#cancel()
     */
    @Override
    public void cancel() {
        cancel.set( true );
    }
    
    private boolean isCancelled(){
        return cancel.get();
    }
    
    private AtomicBoolean cancel;
    private FileObject myFileObject;
    private static final Map<ElementKind,ElementAnalyzer> ANALIZERS = 
        new HashMap<ElementKind, ElementAnalyzer>();

    static {
        ANALIZERS.put(ElementKind.CLASS, new ClassElementAnalyzer());
        ANALIZERS.put(ElementKind.FIELD, new FieldElementAnalyzer());
        ANALIZERS.put(ElementKind.METHOD, new MethodElementAnalyzer());
        ANALIZERS.put(ElementKind.CONSTRUCTOR, new CtorAnalyzer());
        ANALIZERS.put(ElementKind.ANNOTATION_TYPE, new AnnotationAnalyzer());
    }
}
