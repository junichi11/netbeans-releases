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

package org.netbeans.modules.java.project;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractListModel;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import static org.netbeans.modules.java.project.Bundle.*;
import org.openide.util.NbBundle.Messages;

public final class BrokenReferencesModel extends AbstractListModel {

    private String[] props;
    private String[] platformsProps;
    private AntProjectHelper helper;
    private ReferenceHelper resolver;
    private List<OneReference> references;

    public BrokenReferencesModel(AntProjectHelper helper, 
            ReferenceHelper resolver, String[] props, String[] platformsProps) {
        this.props = props.clone();
        this.platformsProps = platformsProps.clone();
        this.resolver = resolver;
        this.helper = helper;
        references = new ArrayList<OneReference>();
        refresh();
    }

    public void refresh() {
        Set<OneReference> all = new LinkedHashSet<OneReference>();
        Set<OneReference> s = getReferences(helper, resolver, helper.getStandardPropertyEvaluator(), props, false);
        all.addAll(s);
        s = getPlatforms(helper.getStandardPropertyEvaluator(), platformsProps, false);
        all.addAll(s);
        updateReferencesList(references, all);
        this.fireContentsChanged(this, 0, getSize());
    }

    @Messages({
        "LBL_BrokenLinksCustomizer_BrokenLibrary=\"{0}\" library could not be found",
        "LBL_BrokenLinksCustomizer_BrokenLibraryContent=\"{0}\" library has missing items",
        "LBL_BrokenLinksCustomizer_BrokenProjectReference=\"{0}\" project could not be found",
        "LBL_BrokenLinksCustomizer_BrokenFileReference=\"{0}\" file/folder could not be found",
        "LBL_BrokenLinksCustomizer_BrokenVariable=\"{0}\" variable could not be found",
        "LBL_BrokenLinksCustomizer_BrokenVariableContent=\"{0}\" variable based file/folder could not be found",
        "LBL_BrokenLinksCustomizer_BrokenPlatform=\"{0}\" platform could not be found"
    })
    public @Override Object getElementAt(int index) {
        OneReference or = getOneReference(index);
        switch (or.type) {
            case LIBRARY:
                return LBL_BrokenLinksCustomizer_BrokenLibrary(or.getDisplayID());
            case LIBRARY_CONTENT:
                return LBL_BrokenLinksCustomizer_BrokenLibraryContent(or.getDisplayID());
            case PROJECT:
                return LBL_BrokenLinksCustomizer_BrokenProjectReference(or.getDisplayID());
            case FILE:
                return LBL_BrokenLinksCustomizer_BrokenFileReference(or.getDisplayID());
            case VARIABLE:
                return LBL_BrokenLinksCustomizer_BrokenVariable(or.getDisplayID());
            case VARIABLE_CONTENT:
                return LBL_BrokenLinksCustomizer_BrokenVariableContent(or.getDisplayID());
            case PLATFORM:
                return LBL_BrokenLinksCustomizer_BrokenPlatform(or.getDisplayID());
            default:
                assert false;
                return null;
        }
    }

    public OneReference getOneReference(int index) {
        assert index>=0 && index<references.size();
        return references.get(index);
    }
    
    public boolean isBroken(int index) {
        return references.get(index).broken;
    }
    
    public @Override int getSize() {
        return references.size();
    }

    public static boolean isBroken(AntProjectHelper helper, ReferenceHelper refHelper, PropertyEvaluator evaluator, String[] props, String[] platformsProps) {
        Set<OneReference> s = getReferences(helper, refHelper, evaluator, props, true);
        if (s.size() > 0) {
            return true;
        }
        s = getPlatforms(evaluator, platformsProps, true);
        return s.size() > 0;
    }

    private static Set<OneReference> getReferences(AntProjectHelper helper, ReferenceHelper refHelper, PropertyEvaluator evaluator, String[] ps, boolean abortAfterFirstProblem) {
        Set<OneReference> set = new LinkedHashSet<OneReference>();
        StringBuilder all = new StringBuilder();
        // this call waits for list of libraries to be refreshhed
        LibraryManager.getDefault().getLibraries();
        EditableProperties ep = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        for (String p : ps) {
            // evaluate given property and tokenize it
            
            String prop = evaluator.getProperty(p);
            if (prop == null) {
                continue;
            }
            String[] vals = PropertyUtils.tokenizePath(prop);
                        
            // no check whether after evaluating there are still some 
            // references which could not be evaluated
            for (String v : vals) {
                // we are checking only: project reference, file reference, library reference
                if (!(v.startsWith("${file.reference.") || v.startsWith("${project.") || v.startsWith("${libs.") || v.startsWith("${var."))) { // NOI18N
                    all.append(v);
                    continue;
                }
                if (v.startsWith("${project.")) { // NOI18N
                    // something in the form: "${project.<projID>}/dist/foo.jar"
                    String val = v.substring(2, v.indexOf('}')); // NOI18N
                    set.add(new OneReference(RefType.PROJECT, val, true));
                } else {
                    RefType type = RefType.LIBRARY;
                    if (v.startsWith("${file.reference")) { // NOI18N
                        type = RefType.FILE;
                    } else if (v.startsWith("${var")) { // NOI18N
                        type = RefType.VARIABLE;
                    }
                    String val = v.substring(2, v.length() - 1);
                    set.add(new OneReference(type, val, true));
                }
                if (abortAfterFirstProblem) {
                    break;
                }
            }
            if (set.size() > 0 && abortAfterFirstProblem) {
                break;
            }
            
            // test that resolved variable based property points to an existing file
            String path = ep.getProperty(p);
            if (path != null) {
                for (String v : PropertyUtils.tokenizePath(path)) {
                    if (v.startsWith("${file.reference.")) {    //NOI18N
                        v = ep.getProperty(v.substring(2, v.length() - 1));
                    }
                    if (v != null && v.startsWith("${var.")) {    //NOI18N
                        String value = evaluator.evaluate(v);
                        if (value.startsWith("${var.")) { // NOI18N
                            // this problem was already reported
                            continue;
                        }
                        File f = getFile(helper, evaluator, value);
                        if (f.exists()) {
                            continue;
                        }
                        set.add(new OneReference(RefType.VARIABLE_CONTENT, v, true));
                    }
                }
            }
            
        }
        
        // Check also that all referenced project really exist and are reachable.
        // If they are not report them as broken reference.
        // XXX: there will be API in PropertyUtils for listing of Ant 
        // prop names in String. Consider using it here.
        final Map<String, String> entries = evaluator.getProperties();
        if (entries == null) {
            throw new IllegalArgumentException("Properies mapping could not be computed (e.g. due to a circular definition). Evaluator: "+evaluator.toString());  //NOI18N
        }        
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.startsWith("project.")) { // NOI18N
                if ("project.license".equals(key)) {    //NOI18N
                    continue;
                }
                File f = getFile(helper, evaluator, value);
                if (f.exists()) {
                    continue;
                }
                // Check that the value is really used by some property.
                // If it is not then ignore such a project.
                if (all.indexOf(value) == -1) {
                    continue;
                }
                set.add(new OneReference(RefType.PROJECT, key, true));
            }
            else if (key.startsWith("file.reference")) {    //NOI18N
                File f = getFile(helper, evaluator, value);
                String unevaluatedValue = ep.getProperty(key);
                boolean alreadyChecked = unevaluatedValue != null ? unevaluatedValue.startsWith("${var.") : false; // NOI18N
                if (f.exists() || all.indexOf(value) == -1 || alreadyChecked) { // NOI18N
                    continue;
                }
                set.add(new OneReference(RefType.FILE, key, true));
            }
        }
        
        //Check for libbraries with broken classpath content
        Set<String> usedLibraries = new HashSet<String>();
        Pattern libPattern = Pattern.compile("\\$\\{(libs\\.[-._a-zA-Z0-9]+\\.classpath)\\}"); //NOI18N
        for (String p : ps) {
            String propertyValue = ep.getProperty(p);
            if (propertyValue != null) {
                for (String v : PropertyUtils.tokenizePath(propertyValue)) {
                    Matcher m = libPattern.matcher(v);
                    if (m.matches()) {
                        usedLibraries.add (m.group(1));
                    }
                }
            }
        }
        for (String libraryRef : usedLibraries) {
            String libraryName = libraryRef.substring(5,libraryRef.length()-10);
            Library lib = refHelper.findLibrary(libraryName);
            if (lib == null) {
                set.add(new OneReference(RefType.LIBRARY, libraryRef, true));
            }
            else {
                //XXX: Should check all the volumes (sources, javadoc, ...)?
                for (URI uri : lib.getURIContent("classpath")) { // NOI18N
                    URI uri2 = LibrariesSupport.getArchiveFile(uri);
                    if (uri2 == null) {
                        uri2 = uri;
                    }
                    FileObject fo = LibrariesSupport.resolveLibraryEntryFileObject(lib.getManager().getLocation(), uri2);
                    if (null == fo && !canResolveEvaluatedUri(helper.getStandardPropertyEvaluator(), lib.getManager().getLocation(), uri2)) {
                        set.add(new OneReference(RefType.LIBRARY_CONTENT, libraryRef, true));
                        break;
                    }
                }
            }
        }
        
        return set;
    }
    
    /** Tests whether evaluated URI can be resolved. To support library entry 
     * like "${MAVEN_REPO}/struts/struts.jar".
     */
    private static boolean canResolveEvaluatedUri(PropertyEvaluator eval, URL libBase, URI libUri) {
        if (libUri.isAbsolute()) {
            return false;
        }
        String path = LibrariesSupport.convertURIToFilePath(libUri);
        String newPath = eval.evaluate(path);
        if (newPath.equals(path)) {
            return false;
        }
        URI newUri = LibrariesSupport.convertFilePathToURI(newPath);
        return null != LibrariesSupport.resolveLibraryEntryFileObject(libBase, newUri);
    }

    private static File getFile (AntProjectHelper helper, PropertyEvaluator evaluator, String name) {
        if (helper != null) {
            return new File(helper.resolvePath(name));
        } else {
            File f = new File(name);
            if (!f.exists()) {
                // perhaps the file is relative?
                String basedir = evaluator.getProperty("basedir"); // NOI18N
                assert basedir != null;
                f = new File(new File(basedir), name);
            }
            return f;
        }
    }

    private static Set<OneReference> getPlatforms(PropertyEvaluator evaluator, String[] platformsProps, boolean abortAfterFirstProblem) {
        Set<OneReference> set = new LinkedHashSet<OneReference>();
        for (String pprop : platformsProps) {
            String prop = evaluator.getProperty(pprop);
            if (prop == null) {
                continue;
            }
            if (!existPlatform(prop)) {
                
                // XXX: the J2ME stores in project.properties also platform 
                // display name and so show this display name instead of just
                // prop ID if available.
                if (evaluator.getProperty(pprop + ".description") != null) { // NOI18N
                    prop = evaluator.getProperty(pprop + ".description"); // NOI18N
                }
                
                set.add(new OneReference(RefType.PLATFORM, prop, true));
            }
            if (set.size() > 0 && abortAfterFirstProblem) {
                break;
            }
        }
        return set;
    }
    
    private static void updateReferencesList(List<OneReference> oldBroken, Set<OneReference> newBroken) {
        for (OneReference or : oldBroken) {
            if (newBroken.contains(or)) {
                or.broken = true;
            } else {
                or.broken = false;
            }
        }
        for (OneReference or : newBroken) {
            if (!oldBroken.contains(or)) {
                oldBroken.add(or);
            }
        }
    }
    
    private static boolean existPlatform(String platform) {
        if (platform.equals("default_platform")) { // NOI18N
            return true;
        }
        for (JavaPlatform plat : JavaPlatformManager.getDefault().getInstalledPlatforms()) {
            // XXX: this should be defined as PROPERTY somewhere
            if (platform.equals(plat.getProperties().get("platform.ant.name")) && // NOI18N
                    plat.getInstallFolders().size() > 0) {
                return true;
            }
        }
        return false;
    }

    // XXX: perhaps could be moved to ReferenceResolver. 
    // But nobody should need it so it is here for now.
    void updateReference(int index, File file) {
        updateReference0(index, file);
        // #48210 - check whether the folder does not contain other jars
        // which could auto resolve some broken links:
        OneReference or = getOneReference(index);
        if (or.getType() != RefType.FILE) {
            return;
        }
        for (int i=0; i<getSize(); i++) {
            if (!isBroken(i) || i == index) {
                continue;
            }
            or = getOneReference(i);
            if (or.getType() != RefType.FILE) {
                continue;
            }
            File f = new File(file.getParentFile(), or.getDisplayID());
            if (f.exists()) {
                updateReference0(i, f);
            }
        }
    }
    
    private void updateReference0(int index, File file) {
        final String reference = getOneReference(index).ID;
        FileObject myProjDirFO = helper.getProjectDirectory();
        final String propertiesFile = AntProjectHelper.PRIVATE_PROPERTIES_PATH;
        final String path = file.getAbsolutePath();
        Project p;
        try {
            p = ProjectManager.getDefault().findProject(myProjDirFO);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            p = null;
        }
        final Project proj = p;
        ProjectManager.mutex().postWriteRequest(new Runnable() {
                public @Override void run() {
                    EditableProperties props = helper.getProperties(propertiesFile);
                    if (!path.equals(props.getProperty(reference))) {
                        props.setProperty(reference, path);
                        helper.putProperties(propertiesFile, props);
                    }
                                        
                    if (proj != null) {
                        try {
                            ProjectManager.getDefault().saveProject(proj);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
    }
    
    /** @return non-null library manager */
    LibraryManager getProjectLibraryManager() {
        return resolver.getProjectLibraryManager() != null ? 
            resolver.getProjectLibraryManager() : LibraryManager.getDefault();
    }

    enum RefType {
        PROJECT,
        FILE,
        PLATFORM,
        LIBRARY,
        LIBRARY_CONTENT,
        VARIABLE,
        VARIABLE_CONTENT,
    }
    
    public static class OneReference {
        
        private final RefType type;
        private boolean broken;
        private final String ID;

        public OneReference(RefType type, String ID, boolean broken) {
            this.type = type;
            this.ID = ID;
            this.broken = broken;
        }
        
        public RefType getType() {
            return type;
        }
        
        public String getDisplayID() {
            switch (type) {
                
                case LIBRARY:
                case LIBRARY_CONTENT:
                    // libs.<name>.classpath
                    return ID.substring(5, ID.length()-10);
                    
                case PROJECT:
                    // project.<name>
                    return ID.substring(8);
                    
                case FILE:
                    // file.reference.<name>
                    return ID.substring(15);
                    
                case PLATFORM:
                    return ID;
                    
                case VARIABLE:
                    return ID.substring(4, ID.indexOf('}')); // NOI18N
                    
                case VARIABLE_CONTENT:
                    return ID.substring(6, ID.indexOf('}')) + ID.substring(ID.indexOf('}')+1); // NOI18N
                    
                default:
                    assert false;
                    return ID;
            }
        }

        public @Override boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof OneReference)) {
                return false;
            }
            OneReference or = (OneReference)o;
            return (this.type == or.type && this.ID.equals(or.ID));
        }
        
        public @Override int hashCode() {
            int result = 7 * type.hashCode();
            result = 31*result + ID.hashCode();
            return result;
        }
        
    }
    
}
