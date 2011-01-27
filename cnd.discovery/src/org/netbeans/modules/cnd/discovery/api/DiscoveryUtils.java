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

package org.netbeans.modules.cnd.discovery.api;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.api.toolchain.CompilerFlavor;
import org.netbeans.modules.cnd.discovery.wizard.bridge.ProjectBridge;
import org.netbeans.modules.cnd.spi.utils.CndFileSystemProvider;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author Alexander Simon
 */
public class DiscoveryUtils {

    private DiscoveryUtils() {
    }
    public static List<String> getSystemIncludePaths(ProjectProxy project, boolean isCPP) {
        Project p = project.getProject();
        if (p != null){
            ProjectBridge bridge = new ProjectBridge(p);
            if (bridge.isValid()) {
                return bridge.getSystemIncludePaths(isCPP);
            }
        }
        return new ArrayList<String>();
    }
    
    public static CompilerFlavor getCompilerFlavor(ProjectProxy project){
        Project p = project.getProject();
        if (p != null){
            ProjectBridge bridge = new ProjectBridge(p);
            if (bridge.isValid()) {
                return bridge.getCompilerFlavor();
            }
        }
        return null;
    }

    public static String getCygwinDrive(ProjectProxy project){
        Project p = project.getProject();
        if (p != null){
            ProjectBridge bridge = new ProjectBridge(p);
            if (bridge.isValid()) {
                return bridge.getCygwinDrive();
            }
        }
        return null;
    }

    public static Map<String,String> getSystemMacroDefinitions(ProjectProxy project, boolean isCPP) {
        Project p = project.getProject();
        if (p != null){
            ProjectBridge bridge = new ProjectBridge(p);
            if (bridge.isValid()) {
                return bridge.getSystemMacroDefinitions(isCPP);
            }
        }
        return new HashMap<String,String>();
    }
    
    public static boolean ignoreFolder(File file){
        if (file.isDirectory()) {
            String name = file.getName();
            return name.equals("SCCS") || name.equals("CVS") || name.equals(".hg") || name.equals("SunWS_cache") || name.equals(".svn"); // NOI18N
        }
        return false;
    }

    public static List<String> scanCommandLine(String line){
        List<String> res = new ArrayList<String>();
        int i = 0;
        StringBuilder current = new StringBuilder();
        boolean isSingleQuoteMode = false;
        boolean isDoubleQuoteMode = false;
        while (i < line.length()) {
            char c = line.charAt(i);
            i++;
            switch (c){
                case '\'': // NOI18N
                    if (isSingleQuoteMode) {
                        isSingleQuoteMode = false;
                    } else if (!isDoubleQuoteMode) {
                        isSingleQuoteMode = true;
                    }
                    current.append(c);
                    break;
                case '\"': // NOI18N
                    if (isDoubleQuoteMode) {
                        isDoubleQuoteMode = false;
                    } else if (!isSingleQuoteMode) {
                        isDoubleQuoteMode = true;
                    }
                    current.append(c);
                    break;
                case ' ': // NOI18N
                case '\t': // NOI18N
                case '\n': // NOI18N
                case '\r': // NOI18N
                    if (isSingleQuoteMode || isDoubleQuoteMode) {
                        current.append(c);
                        break;
                    } else {
                        if (current.length()>0) {
                            res.add(current.toString());
                            current.setLength(0);
                        }
                    }
                    break;
                default:
                    current.append(c);
                    break;
            }
        }
        if (current.length()>0) {
            res.add(current.toString());
        }
        return res;
    }

    public static String getRelativePath(String base, String path) {
        if (path.equals(base)) {
            return path;
        } else if (path.startsWith(base + '/')) { // NOI18N
            return path.substring(base.length()+1);
        } else if (path.startsWith(base + '\\')) { // NOI18N
            return path.substring(base.length() + 1);
        } else if (!(path.startsWith("/") || path.startsWith("\\") || // NOI18N
                     path.length() > 2 && path.charAt(2)==':')) { // NOI18N
            return path;
        } else {
            StringTokenizer stb = new StringTokenizer(base, "\\/"); // NOI18N
            StringTokenizer stp = new StringTokenizer(path, "\\/"); // NOI18N
            int match = 0;
            String pstring = null;
            while(stb.hasMoreTokens() && stp.hasMoreTokens()) {
                String bstring = stb.nextToken();
                pstring = stp.nextToken();
                if (bstring.equals(pstring)) {
                    match++;
                } else {
                    break;
                }
            }
            if (match <= 1){
                return path;
            }
            StringBuilder s = new StringBuilder();
            while(stb.hasMoreTokens()) {
                String bstring = stb.nextToken();
                s.append("..").append(File.separator); // NOI18N
            }
            s.append("..").append(File.separator).append(pstring); // NOI18N
            while(stp.hasMoreTokens()) {
                s.append(File.separator).append(stp.nextToken()); // NOI18N
            }
            return s.toString();
        }
    }

    public static String normalizeAbsolutePath(String path) {
        boolean caseSensitive = CndFileUtils.isSystemCaseSensitive();
        if (!caseSensitive) {
            // with case sensitive "path"s returned by remote compilers
            path = CndFileSystemProvider.getCaseInsensitivePath(path);
        }
        String normalized;
        // small optimization for true case sensitive OSs
        if (!caseSensitive || (path.endsWith("/.") || path.endsWith("\\.") || path.contains("..") || path.contains("./") || path.contains(".\\"))) { // NOI18N
            normalized = FileUtil.normalizeFile(new File(path)).getAbsolutePath();
        } else {
            normalized = path;
        }
        return normalized;
    }

    /**
     * Path is include path like:
     * .
     * ../
     * include
     * Returns path in Unix style
     */
    public static String convertRelativePathToAbsolute(SourceFileProperties source, String path){
        if ( !( path.startsWith("/") || (path.length()>1 && path.charAt(1)==':') ) ) { // NOI18N
            if (path.equals(".")) { // NOI18N
                path = source.getCompilePath();
            } else {
                path = source.getCompilePath()+File.separator+path;
            }
            File file = new File(path);
            path = CndFileUtils.normalizeFile(file).getAbsolutePath();
        }
        if (Utilities.isWindows()) {
            path = path.replace('\\', '/'); // NOI18N
        }
        return path;
    }
    
    /**
     * parse compile line
     */
    public static String gatherCompilerLine(String line, boolean isScriptOutput,
            List<String> userIncludes, Map<String, String> userMacros, Set<String> libraries, List<String> languageArtifacts){
        boolean TRACE = false;
        List<String> list = DiscoveryUtils.scanCommandLine(line);
        boolean hasQuotes = false;
        for(String s : list){
            if (s.startsWith("\"")){  //NOI18N
                hasQuotes = true;
                break;
            }
        }
        if (hasQuotes) {
            List<String> newList = new ArrayList<String>();
            for(int i = 0; i < list.size();) {
                String s = list.get(i); 
                if (s.startsWith("-D") && s.endsWith("=") && i+1 < list.size() && list.get(i+1).startsWith("\"")){ // NOI18N
                    String longString = null;
                    for(int j = i+1; j < list.size() && list.get(j).startsWith("\""); j++){  //NOI18N
                        if (longString != null) {
                            longString += " " + list.get(j);  //NOI18N
                        } else {
                            longString = list.get(j);
                        }
                        i = j;
                    }
                    newList.add(s+"`"+longString+"`");  //NOI18N
                } else {
                    newList.add(s);
                }
                i++;
            }
            list = newList;
        }
        String what = null;
        Iterator<String> st = list.iterator();
        String option = null; 
        if (st.hasNext()) {
            option = st.next();
            if (option.equals("+") && st.hasNext()) { // NOI18N
                option = st.next();
            }
        }
        while(st.hasNext()){
            option = st.next();
            boolean isQuote = false;
            if (isScriptOutput) {
                if (option.startsWith("'") && option.endsWith("'") || // NOI18N
                    option.startsWith("\"") && option.endsWith("\"")){ // NOI18N
                    option = option.substring(1,option.length()-1);
                    isQuote = true;
                }
            }
            if (option.startsWith("-D")){ // NOI18N
                String macro;
                if (option.equals("-D") && st.hasNext()){  //NOI18N
                    macro = st.next();
                } else {
                    macro = option.substring(2);
                }
                macro = removeQuotes(macro);
                int i = macro.indexOf('=');
                if (i>0){
                    String value = macro.substring(i+1).trim();
                    if (value.length() >= 2 && value.charAt(0) == '`' && value.charAt(value.length()-1) == '`'){ // NOI18N
                        value = value.substring(1,value.length()-1);  // NOI18N
                    } else {
                        if (!isQuote && value.length() >= 6 &&
                           (value.charAt(0) == '"' && value.charAt(1) == '\\' && value.charAt(2) == '"' &&  // NOI18N
                            value.charAt(value.length()-3) == '\\' && value.charAt(value.length()-2) == '"' && value.charAt(value.length()-1) == '"')) { // NOI18N
                            value = value.substring(2,value.length()-3)+"\"";  // NOI18N
                        } else if (value.length() >= 4 &&
                           (value.charAt(0) == '\\' && value.charAt(1) == '"' &&  // NOI18N
                            value.charAt(value.length()-2) == '\\' && value.charAt(value.length()-1) == '"' )) { // NOI18N
                            value = value.substring(1,value.length()-2)+"\"";  // NOI18N
                        }
                    }
                    userMacros.put(macro.substring(0,i), value);
                } else {
                    userMacros.put(macro, null);
                }
            } else if (option.startsWith("-I")){ // NOI18N
                String path = option.substring(2);
                if (path.length()==0 && st.hasNext()){
                    path = st.next();
                }
                path = removeQuotes(path);
                userIncludes.add(path);
            } else if (option.startsWith("-isystem")){ // NOI18N
                String path = option.substring(8);
                if (path.length()==0 && st.hasNext()){
                    path = st.next();
                }
                path = removeQuotes(path);
                userIncludes.add(path);
            } else if (option.startsWith("-include")){ // NOI18N
                String path = option.substring(8);
                if (path.length()==0 && st.hasNext()){
                    path = st.next();
                }
                path = removeQuotes(path);
                userIncludes.add(path);
            } else if (option.startsWith("-imacros")){ // NOI18N
                String path = option.substring(8);
                if (path.length()==0 && st.hasNext()){
                    path = st.next();
                }
                path = removeQuotes(path);
                userIncludes.add(path);
            } else if (option.startsWith("-Y")){ // NOI18N
                String defaultSearchPath = option.substring(2);
                if (defaultSearchPath.length()==0 && st.hasNext()){
                    defaultSearchPath = st.next();
                }
                if (defaultSearchPath.startsWith("I,")){ // NOI18N
                    defaultSearchPath = defaultSearchPath.substring(2);
                    defaultSearchPath = removeQuotes(defaultSearchPath);
                    userIncludes.add(defaultSearchPath);
                }
            } else if (option.startsWith("-idirafter")){ // NOI18N
                //Search dir for header files, but do it after all directories specified with -I and the standard system directories have been exhausted.
                if (option.equals("-idirafter") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-iprefix")){ // NOI18N
                //Specify prefix as the prefix for subsequent -iwithprefix options.
                if (option.equals("-iprefix") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-iwithprefix")){ // NOI18N
                //Append dir to the prefix specified previously with -iprefix, and add the resulting directory to the include search path.
                if (option.equals("-iwithprefix") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-iwithprefixbefore")){ // NOI18N
                //Append dir to the prefix specified previously with -iprefix, and add the resulting directory to the include search path.
                if (option.equals("-iwithprefixbefore") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-isysroot")){ // NOI18N
                //This option is like the --sysroot option, but applies only to header files.
                if (option.equals("-isysroot") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-iquote")){ // NOI18N
                //Search dir only for header files requested with "#include " file ""
                if (option.equals("-iquote") && st.hasNext()) { // NOI18N
                    st.next();
                }
            } else if (option.startsWith("-U")) { // NOI18N
                if (option.equals("-U") && st.hasNext()){  //NOI18N
                    st.next();
                }
            } else if (option.equals("-K")){ // NOI18N
                // Skip pic
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-R")){ // NOI18N
                // Skip runtime search path 
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.startsWith("-l")){ // NOI18N
                String lib = option.substring(2);
                if (lib.length() == 0 && st.hasNext()){
                    lib = st.next();
                }
                // library
                if (lib.length()>0 && libraries != null){
                    libraries.add(lib);
                }
            } else if (option.equals("-L")){ // NOI18N
                // Skip library search path
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-M")){ // NOI18N
                // Skip library search path
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-h")){ // NOI18N
                // Skip generated dynamic shared library
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-o")){ // NOI18N
                // Skip result
                if (st.hasNext()){
                    st.next();
                }
            // generation 2 of params
            } else if (option.equals("-z")){ // NOI18N
                // XXXX: what's this? All I know it has param
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-x")){ // NOI18N
                // Specify explicitly the language for the following input files (rather than letting the compiler choose a default based on the file name suffix).
                if (st.hasNext()){
                    String lang = st.next();
                    if (languageArtifacts != null) {
                        languageArtifacts.add(lang);
                    }
                }
            } else if (option.equals("-xMF")){ // NOI18N
                // ignore dependency output file
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-MF")){ // NOI18N
                // ignore dependency output file
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-MT")){ // NOI18N
                // once more fancy preprocessor option with parameter. Ignore.
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-MQ")){ // NOI18N
                // once more fancy preprocessor option with parameter. Ignore.
                if (st.hasNext()){
                    st.next();
                }
            } else if (option.equals("-aux-info")){ // NOI18N
                // Output to the given filename prototyped declarations for all functions declared and/or defined in a translation unit, including those in header files. Ignore.
                if (st.hasNext()){
                    st.next();
                }
            // end of generation 2    
            } else if (option.equals("-fopenmp")){ // NOI18N
                userMacros.put("_OPENMP", "200505"); // NOI18N
            } else if (option.equals("-xopenmp") || option.equals("-xopenmp=parallel") || option.equals("-xopenmp=noopt")){ // NOI18N
                userMacros.put("_OPENMP", null); // NOI18N
            } else if (option.startsWith("-")){ // NOI18N
                // Skip option
            } else if (option.startsWith("ccfe")){ // NOI18N
                // Skip option
            } else if (option.startsWith(">")){ // NOI18N
                // Skip redurect
                break;
            } else {
                if (option.endsWith(".il") || option.endsWith(".o") || option.endsWith(".a") ||  //NOI18N
                    option.endsWith(".so") || option.endsWith(".so.1")) {  //NOI18N
                    continue;
                }
                if (what == null) {
                    what = option;
                } else {
                    if (TRACE) {
                        System.out.println("**** What is this ["+option + "] if previous was ["+ what + "]?"); //NOI18N
                        System.out.println("*> "+line); //NOI18N
                    }
                    if ((option.endsWith(".c") || option.endsWith(".cc") || option.endsWith(".cpp") || //NOI18N
                        option.endsWith(".cxx") ||option.endsWith(".c++") || option.endsWith(".C")) && //NOI18N
                        !(what.endsWith(".c") || what.endsWith(".cc") || what.endsWith(".cpp") || //NOI18N
                        what.endsWith(".cxx") ||what.endsWith(".c++") || what.endsWith(".C"))) { //NOI18N
                        what = option;
                    }
                }
            }
        }
        return what;
    }

    private static String removeQuotes(String path) {
        if (path.length() >= 2 && (path.charAt(0) == '\'' && path.charAt(path.length() - 1) == '\'' || // NOI18N
            path.charAt(0) == '"' && path.charAt(path.length() - 1) == '"')) {// NOI18N

            path = path.substring(1, path.length() - 1); // NOI18N
        }
        return path;
    }

}
