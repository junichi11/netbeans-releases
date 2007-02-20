/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.freeform;

import org.netbeans.api.project.Project;
import org.netbeans.modules.ant.freeform.spi.HelpIDFragmentProvider;
import org.netbeans.modules.ant.freeform.spi.ProjectAccessor;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author mkleint
 */
public class LookupProviderImpl implements LookupProvider {
     private static final String HELP_ID_FRAGMENT = "java"; // NOI18N
  
    /** Creates a new instance of LookupProviderImpl */
    public LookupProviderImpl() {
    }
    
    public Lookup createAdditionalLookup(Lookup baseContext) {
        Project prj = baseContext.lookup(Project.class);
        ProjectAccessor acc = baseContext.lookup(ProjectAccessor.class);
        AuxiliaryConfiguration aux = baseContext.lookup(AuxiliaryConfiguration.class);
        assert aux != null;
        assert prj != null;
        assert acc != null;
        return new ProjectLookup(prj, acc.getHelper(), acc.getEvaluator(), aux);
    }
    
    private static Lookup initLookup(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        Classpaths cp = new Classpaths(projectHelper, projectEvaluator, aux);
        return Lookups.fixed(
            cp, // ClassPathProvider
            new SourceLevelQueryImpl(projectHelper, projectEvaluator, aux), // SourceLevelQueryImplementation
            new SourceForBinaryQueryImpl(projectHelper, projectEvaluator, aux), // SourceForBinaryQueryImplementation
            new OpenHook(cp), // ProjectOpenedHook
            new TestQuery(projectHelper, projectEvaluator, aux), // MultipleRootsUnitTestForSourceQueryImplementation
            new JavadocQuery(projectHelper, projectEvaluator, aux), // JavadocForBinaryQueryImplementation
            new PrivilegedTemplatesImpl(), // PrivilegedTemplates
            new JavaActions(project, projectHelper, projectEvaluator, aux), // ActionProvider
            new LookupMergerImpl(), // LookupMerger
            new JavaFreeformFileBuiltQuery(project, projectHelper, projectEvaluator, aux), // FileBuiltQueryImplementation
            new HelpIDFragmentProviderImpl());
    }
    
    public static boolean isMyProject(AuxiliaryConfiguration aux) {
        return aux.getConfigurationFragment(JavaProjectNature.EL_JAVA, JavaProjectNature.NS_JAVA_1, true) != null ||
               aux.getConfigurationFragment(JavaProjectNature.EL_JAVA, JavaProjectNature.NS_JAVA_2, true) != null;
    }
    
    private static final class HelpIDFragmentProviderImpl implements HelpIDFragmentProvider {
        public String getHelpIDFragment() {
            return HELP_ID_FRAGMENT;
        }
    }
    
    private static class OpenHook extends ProjectOpenedHook {
        
        private final Classpaths cp;
        
        public OpenHook(Classpaths cp) {
            this.cp = cp;
        }
        
        protected void projectOpened() {
            cp.opened();
        }
        
        protected void projectClosed() {
            cp.closed();
        }
        
    }
    
    /**
     * Transparently handles /1 -> /2 schema upgrade (on read only, not write!).
     */
    static final class UpgradingAuxiliaryConfiguration implements AuxiliaryConfiguration {
        
        private final AuxiliaryConfiguration delegate;
        
        public UpgradingAuxiliaryConfiguration(AuxiliaryConfiguration delegate) {
            this.delegate = delegate;
        }

        public Element getConfigurationFragment(String elementName, String namespace, boolean shared) {
            if (elementName.equals(JavaProjectNature.EL_JAVA) && namespace.equals(JavaProjectNature.NS_JAVA_2) && shared) {
                Element nue = delegate.getConfigurationFragment(JavaProjectNature.EL_JAVA, JavaProjectNature.NS_JAVA_2, true);
                if (nue == null) {
                    Element old = delegate.getConfigurationFragment(JavaProjectNature.EL_JAVA, JavaProjectNature.NS_JAVA_1, true);
                    if (old != null) {
                        nue = upgradeSchema(old);
                    }
                }
                return nue;
            } else {
                return delegate.getConfigurationFragment(elementName, namespace, shared);
            }
        }

        public void putConfigurationFragment(Element fragment, boolean shared) throws IllegalArgumentException {
            delegate.putConfigurationFragment(fragment, shared);
        }
        
        public boolean removeConfigurationFragment(String elementName, String namespace, boolean shared) throws IllegalArgumentException {
            return delegate.removeConfigurationFragment(elementName, namespace, shared);
        }
        
    }
    
    static Element upgradeSchema(Element old) {
        Document doc = old.getOwnerDocument();
        Element nue = doc.createElementNS(JavaProjectNature.NS_JAVA_2, JavaProjectNature.EL_JAVA);
        copyXMLTree(doc, old, nue, JavaProjectNature.NS_JAVA_2);
        return nue;
    }  
    // Copied from org.netbeans.modules.java.j2seproject.UpdateHelper with changes; could be an API eventually:
    private static void copyXMLTree(Document doc, Element from, Element to, String newNamespace) {
        NodeList nl = from.getChildNodes();
        int length = nl.getLength();
        for (int i = 0; i < length; i++) {
            org.w3c.dom.Node node = nl.item(i);
            org.w3c.dom.Node newNode;
            switch (node.getNodeType()) {
                case org.w3c.dom.Node.ELEMENT_NODE:
                    Element oldElement = (Element) node;
                    newNode = doc.createElementNS(newNamespace, oldElement.getTagName());
                    NamedNodeMap attrs = oldElement.getAttributes();
                    int alength = attrs.getLength();
                    for (int j = 0; j < alength; j++) {
                        org.w3c.dom.Attr oldAttr = (org.w3c.dom.Attr) attrs.item(j);
                        ((Element)newNode).setAttributeNS(oldAttr.getNamespaceURI(), oldAttr.getName(), oldAttr.getValue());
                    }
                    copyXMLTree(doc, oldElement, (Element) newNode, newNamespace);
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    newNode = doc.createTextNode(((Text) node).getData());
                    break;
                case org.w3c.dom.Node.COMMENT_NODE:
                    newNode = doc.createComment(((Comment) node).getData());
                    break;
                default:
                    // Other types (e.g. CDATA) not yet handled.
                    throw new AssertionError(node);
            }
            to.appendChild(newNode);
        }
    }    
    
    
    
   private static final class ProjectLookup extends ProxyLookup implements AntProjectListener {

        private AntProjectHelper helper;
        private PropertyEvaluator evaluator;
        private Project project;
        private AuxiliaryConfiguration aux;
        private boolean isMyProject;
        
        public ProjectLookup(Project project, AntProjectHelper helper, PropertyEvaluator evaluator, AuxiliaryConfiguration aux) {
            super(new Lookup[0]);
            this.project = project;
            this.helper = helper;
            this.evaluator = evaluator;
            this.aux = new UpgradingAuxiliaryConfiguration(aux);
            this.isMyProject = isMyProject(aux);
            updateLookup();
            helper.addAntProjectListener(this);
        }
        
        private void updateLookup() {
            Lookup l = Lookup.EMPTY;
            if (isMyProject) {
                l = initLookup(project, helper, evaluator, aux);
            }
            setLookups(new Lookup[]{l});
        }
        
        public void configurationXmlChanged(AntProjectEvent ev) {
            if (isMyProject(aux) != isMyProject) {
                isMyProject = !isMyProject;
                updateLookup();
            }
        }
        
        public void propertiesChanged(AntProjectEvent ev) {
            // ignore
        }
        
    }
   private static final class PrivilegedTemplatesImpl implements PrivilegedTemplates, RecommendedTemplates {
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/Classes/Class.java", // NOI18N
            "Templates/Classes/Package", // NOI18N
            "Templates/Classes/Interface.java", // NOI18N
        };
        
        // List of primarily supported templates = J2SEProject.LIBRARY_TYPES + J2SEProject.APPLICATION_TYPES
        private static final String[] RECOMENDED_TYPES = new String[] { 
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-forms",           // NOI18N
            "gui-java-application", // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "servlet-types",        // NOI18N
            "wsdl",                 // NOI18N
            "junit",                // NOI18N
            // "MIDP",              // NOI18N
            "simple-files"          // NOI18N
        };
        
        public String[] getRecommendedTypes() {            
            return RECOMENDED_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }
        
    }   
}
