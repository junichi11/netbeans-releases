package org.netbeans.modules.web.beans.model;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.support.TestUtilities;
import org.netbeans.modules.web.beans.api.model.WebBeansModel;


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

/**
 * @author ads
 *
 */
public class QualifiersTest extends CommonTestCase {

    public QualifiersTest( String testName ) {
        super(testName);
    }

    public void testSimpleQualifiers() throws IOException {
        createQualifier("Binding1");
        createQualifier("Binding2");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "@Binding1 @Binding2 @Default "+
                "public class One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "public class Two extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.inject.*; "+
                "@Named "+
                "public class Three extends Two {}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            @Override
            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.One" );
                Element clazz = ((DeclaredType)mirror).asElement();
                
                List<AnnotationMirror> qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.One should have exactly 3 qualifiers", 3, qualifiers.size());
                assertFalse( "foo.One shoudn't have implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) ); 
                
                mirror = model.resolveType( "foo.Two" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true );
                assertEquals( "foo.Two shouldn't have qualifiers", 0, qualifiers.size());
                assertTrue( "foo.Two has implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                
                mirror = model.resolveType( "foo.Three" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true );
                assertEquals( "foo.Three has exactly one qualifier",  1, 
                        qualifiers.size());
                assertTrue( "foo.Three has implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                return null;
            }
        });
    }
    
    public void testDefaultInheritance() throws IOException {
        createQualifier("Binding1");
        createQualifier("Binding2");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding3.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Inherited "+
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding3 {} ");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "@Binding1 @Default "+
                "public class One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "public class Two extends One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.inject.*; "+
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding2 "+
                "public class Three extends Two {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One1.java",
                "package foo; " +
                "public class One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding1 "+
                "public class Two1 extends One1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three1.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "public class Three1 extends Two1 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One2.java",
                "package foo; " +
                "@Binding3 "+
                "public class One2 {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two2.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "public class Two2 extends One2 {}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            @Override
            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.Two" );
                Element clazz = ((DeclaredType)mirror).asElement();
                
                List<AnnotationMirror> qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Two should have exactly 2 qualifiers", 2, 
                        qualifiers.size());
                assertTrue( "foo.Two shouldn't have implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                
                mirror = model.resolveType( "foo.Three" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Three has exactly three qualifier", 
                        3, qualifiers.size());
                assertFalse( "foo.Three shouldn't have implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                
                mirror = model.resolveType( "foo.Two1" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Two1 has exactly one qualifier", 1,
                        qualifiers.size());
                assertFalse( "foo.Two1 shouldn't have implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                
                mirror = model.resolveType( "foo.Three1" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Three1 has exactly one qualifier", 1,
                        qualifiers.size());
                assertTrue( "foo.Three1 has implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                
                mirror = model.resolveType( "foo.Two2" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true );
                assertEquals( "foo.Three1 has exactly one qualifier", 1,
                        qualifiers.size());
                assertFalse( "foo.Three1 has implicitely defined @Default qualifier",  
                        model.hasImplicitDefaultQualifier(clazz) );
                return null;
            }
        });
    }
    
    public void testInheritance() throws IOException {
        createQualifier("Binding1");
        createQualifier("Binding2");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Binding3.java",
                "package foo; " +
                "import static java.lang.annotation.ElementType.METHOD; "+
                "import static java.lang.annotation.ElementType.FIELD; "+
                "import static java.lang.annotation.ElementType.PARAMETER; "+
                "import static java.lang.annotation.ElementType.TYPE; "+
                "import static java.lang.annotation.RetentionPolicy.RUNTIME; "+
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "import java.lang.annotation.*; "+
                "@Qualifier " +
                "@Inherited "+
                "@Retention(RUNTIME) "+
                "@Target({METHOD, FIELD, PARAMETER, TYPE}) "+
                "public @interface Binding3 {} ");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "@Binding1 "+
                "public class One {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding3 "+
                "public class Two extends One  {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "@Binding2 "+
                "public class Three extends Two {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Four.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "@Binding2 "+
                "public class Four extends Two {}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Five.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "@Specializes "+
                "public class Five extends Three {}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            @Override
            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.Two" );
                Element clazz = ((DeclaredType)mirror).asElement();
                
                List<AnnotationMirror> qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Two should have exactly 2 qualifiers", 2, 
                        qualifiers.size());
                
                mirror = model.resolveType( "foo.Three" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Three has exactly two qualifier", 
                        2, qualifiers.size());
                
                mirror = model.resolveType( "foo.Four" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Four has exactly three qualifier", 3,
                        qualifiers.size());
                
                mirror = model.resolveType( "foo.Five" );
                clazz = ((DeclaredType)mirror).asElement();
                
                qualifiers = model.getQualifiers(clazz, true);
                assertEquals( "foo.Three1 has exactly two qualifier", 2,
                        qualifiers.size());
                
                return null;
            }
        });
    }
    
    public void testMethodInheritance() throws IOException {
        createQualifier("Binding1");
        createQualifier("Binding2");
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/One.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class One {" +
                " @Produces @Binding1 public int method() {return 0;} "+
                " @Produces @Binding1 public String operation() {return \"\";} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Two.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class Two extends One  {" +
                " @Produces @Specializes  @Binding2 public int method() {return 0;} "+
                " @Produces public @Binding2 String operation() {return \"\";} "+
                "}" );
        
        TestUtilities.copyStringToFileObject(srcFO, "foo/Three.java",
                "package foo; " +
                "import javax.enterprise.inject.*; "+
                "import javax.inject.*; "+
                "public class Three extends Two  {" +
                " @Produces @Specializes  public int method() {return 0;} "+
                " @Produces @Specializes public String operation() {return \"\";} "+
                "}" );
        
        TestWebBeansModelImpl modelImpl = createModelImpl(true );
        MetadataModel<WebBeansModel> testModel = modelImpl.createTestModel();
        testModel.runReadAction( new MetadataModelAction<WebBeansModel,Void>(){

            @Override
            public Void run( WebBeansModel model ) throws Exception {
                TypeMirror mirror = model.resolveType( "foo.Three" );
                Element clazz = ((DeclaredType)mirror).asElement();
                
                Set<String> names = new HashSet<String>();
                List<? extends Element> children = clazz.getEnclosedElements();
                for (Element element : children) {
                    String name = element.getSimpleName().toString() ;
                    names.add(name );
                    assertTrue ( element instanceof ExecutableElement );
                    
                    List<AnnotationMirror> qualifiers = model.getQualifiers(element, true);
                    if ( name.equals("method")){
                        assertEquals( "Method 'method' should have exactly 2 qualifiers", 
                                2, qualifiers.size());
                    }
                    else if ( name.equals("operation")){
                        assertEquals( "Method 'method' should have exactly 1 qualifiers", 
                                1, qualifiers.size());
                    }
                }
                
                assertTrue( names.contains( "method"));
                assertTrue( names.contains( "operation"));

                return null;
            }
        });
    }
}
