/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.cnd.api.compilers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.netbeans.modules.cnd.api.compilers.CompilerSet.CompilerFlavor;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.api.utils.Path;
import org.netbeans.modules.cnd.settings.CppSettings;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 * Manage a set of CompilerSets. The CompilerSets are dynamically created based on which compilers
 * are found in the user's $PATH variable.
 */
public class CompilerSetManager {
    
    private static final String gcc_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*gcc(([-.]\\d){2,4})?(\\.exe)?"; // NOI18N
    private static final String gpp_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*g\\+\\+(([-.]\\d){2,4})?(\\.exe)?$"; // NOI18N
    private static final String cc_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*cc(([-.]\\d){2,4})?(\\.exe)?$"; // NOI18N
    private static final String CC_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*CC(([-.]\\d){2,4})?$"; // NOI18N
    private static final String fortran_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*[fg](77|90|95|fortran)(([-.]\\d){2,4})?(\\.exe)?"; // NOI18N
    private static final String make_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*([dg]make|make)(([-.]\\d){2,4})?(\\.exe)?$"; // NOI18N
    private static final String debugger_pattern = "([a-zA-z][a-zA-Z0-9_]*-)*(gdb|dbx)(([-.]\\d){2,4})?(\\.exe)?$"; // NOI18N
    
    /* Legacy defines for CND 5.5 compiler set definitions */
    public static final int SUN_COMPILER_SET = 0;
    public static final int GNU_COMPILER_SET = 1;
    
    public static final String Sun12 = "Sun12"; // NOI18N
    public static final String Sun11 = "Sun11"; // NOI18N
    public static final String Sun10 = "Sun10"; // NOI18N
    public static final String Sun = "Sun"; // NOI18N
    public static final String GNU = "GNU"; // NOI18N
    
    private CompilerFilenameFilter gcc_filter;
    private CompilerFilenameFilter gpp_filter;
    private CompilerFilenameFilter cc_filter;
    private CompilerFilenameFilter CC_filter;
    private CompilerFilenameFilter fortran_filter;
    private CompilerFilenameFilter make_filter;
    private CompilerFilenameFilter debugger_filter;
    
    private ArrayList<CompilerSet> sets = new ArrayList();
    private ArrayList<String> dirlist;
    
    private static CompilerSetManager instance = null;
    private static Set<CompilerSetChangeListener> listeners = new HashSet();
    
    public CompilerSetManager() {
        dirlist = Path.getPath();
        initCompilerFilters();
        initCompilerSets();
    }
    
    public CompilerSetManager(ArrayList dirlist) {
        this.dirlist = dirlist;
        initCompilerFilters();
        initCompilerSets();
    }
    
    public static CompilerSetManager getDefault() {
	return getDefault(true);
    }
    
    public static synchronized CompilerSetManager getDefault(boolean doCreate) {
        if (instance == null && doCreate) {
            instance = new CompilerSetManager();
        }
        return instance;
    }
    
    /**
     * Replace the default CompilerSetManager. Let registered listeners know its been updated.
     */
    public static synchronized void setDefault(CompilerSetManager csm) {
        instance = csm;
        fireCompilerSetChangeNotification(csm);
    }
    
    /** Search $PATH for all desired compiler sets and initialize cbCompilerSet and spCompilerSets */
    private void initCompilerSets() {
        
        for (String path : dirlist) {
            if (path.equals("/usr/ccs/bin")) { // NOI18N
                // contains only softlinks
                continue;
            }
            File dir = new File(path);
            if (dir.isDirectory()) {
                initCompiler(gcc_filter, "gcc", Tool.CCompiler, path); // NOI18N
                initCompiler(gpp_filter, "g++", Tool.CCCompiler, path); // NOI18N
                initCompiler(cc_filter, "cc", Tool.CCompiler, path); // NOI18N
                initFortranCompiler(fortran_filter, Tool.FortranCompiler, path); // NOI18N
                if (Utilities.isUnix()) {  // CC and cc are the same on Windows, so skip this step on Windows
                    initCompiler(CC_filter, "CC", Tool.CCCompiler, path); // NOI18N
                }
                initMakeTool(make_filter, Tool.MakeTool, path); // NOI18N
                initDebuggerTool(debugger_filter, Tool.DebuggerTool, path); // NOI18N
            }
        }
        completeCompilerSets();
    }
    
    private void initCompiler(CompilerFilenameFilter filter, String best, int kind, String path) {
        File dir = new File(path);
        String[] list = dir.list(filter);

        if (list != null && list.length > 0) {
            CompilerSet cs = CompilerSet.getCompilerSet(dir.getAbsolutePath(), list);
            add(cs);
            for (String name : list) {
                File file = new File(dir, name);
                if (file.exists() && (name.equals(best) || name.equals(best + ".exe"))) { // NOI18N
                    cs.addTool(name, path, kind);
                }
            }
        }
    }
    
    private void initFortranCompiler(CompilerFilenameFilter filter, int kind, String path) {
        File dir = new File(path);
        String[] list = dir.list(filter);
        String[] best = {
            "gfortran", // NOI18N
            "g77", // NOI18N
            "f95", // NOI18N
            "f90", // NOI18N
            "f77", // NOI18N
        };

        if (list != null && list.length > 0) {
            CompilerSet cs = CompilerSet.getCompilerSet(dir.getAbsolutePath(), list);
            add(cs);
            for (String name : list) {
                File file = new File(dir, name);
                if (file.exists()) {
                    for (int i = 0; i < best.length; i++) {
                        if (name.equals(best[i]) || name.equals(best[i] + ".exe")) { // NOI18N
                            cs.addTool(name, path, kind);
                        }
                    }
                }
            }
        }
    }
    
    private void initMakeTool(CompilerFilenameFilter filter, int kind, String path) {
        File dir = new File(path);
        String[] list = dir.list(filter);
        String[] best = {
            "dmake", // NOI18N
            "gmake", // NOI18N
            "make", // NOI18N
            "make.exe", // NOI18N
        };

        if (list != null && list.length > 0) {
            CompilerSet cs = null;
            if (path.contains("msys")) { // NOI18N
                // use minGW cs
                cs = getCompilerSet(CompilerFlavor.MinGW);
            }
            if (cs == null) {
                cs = CompilerSet.getCompilerSet(dir.getAbsolutePath(), list);
                add(cs);
            }
            for (int i = 0; i < best.length; i++) {
                File file = new File(dir, best[i]);
                if (file.exists() && !file.isDirectory()) {
                    cs.addTool(best[i], path, kind);
                    return;
                }
            }
        }
    }
    
    private void initDebuggerTool(CompilerFilenameFilter filter, int kind, String path) {
        File dir = new File(path);
        String[] list = dir.list(filter);
        String[] best;
        if (IpeUtils.isGdbEnabled()) {
            best = new String[] {"gdb", "gdb.exe"}; // NOI18N
        }
        else {
            // Assume dbxgui
            best = new String[] {"dbx"}; // NOI18N
        }

        if (list != null && list.length > 0) {
            CompilerSet cs = CompilerSet.getCompilerSet(dir.getAbsolutePath(), list);
            add(cs);
            for (int i = 0; i < best.length; i++) {
                File file = new File(dir, best[i]);
                if (file.exists() && !file.isDirectory()) {
                    cs.addTool(best[i], path, kind);
                    return;
                }
            }
        }
    }
    
    /**
     * If a compiler set doesn't have one of each compiler types, add a "No compiler"
     * tool. If selected, this will tell the build validation things are OK.
     */
    private void completeCompilerSets() {
        for (CompilerSet cs : sets) {
            if (cs.getTool(Tool.CCompiler) == null) {
                cs.addTool("", "", Tool.CCompiler); // NOI18N
            }
            if (cs.getTool(Tool.CCCompiler) == null) {
                cs.addTool("", "", Tool.CCCompiler); // NOI18N
            }
            if (cs.getTool(Tool.FortranCompiler) == null) {
                cs.addTool("", "", Tool.FortranCompiler); // NOI18N
            }
            if (cs.getTool(Tool.CustomTool) == null) {
                cs.addTool("", "", Tool.CustomTool); // NOI18N
            }
            if (cs.findTool(Tool.MakeTool) == null) {
                String path = Path.findCommand("make"); // NOI18N
                if (path != null)
                    cs.addNewTool(IpeUtils.getBaseName(path), IpeUtils.getDirName(path), Tool.MakeTool); // NOI18N
            }
            if (cs.getTool(Tool.MakeTool) == null) {
                    cs.addTool("", "", Tool.MakeTool); // NOI18N
            }
            if (cs.findTool(Tool.DebuggerTool) == null) {
                String path;
                if (IpeUtils.isGdbEnabled()) {
                    path = Path.findCommand("gdb"); // NOI18N
                }
                else {
                    path = Path.findCommand("dbx"); // NOI18N
                }
                if (path != null)
                    cs.addNewTool(IpeUtils.getBaseName(path), IpeUtils.getDirName(path), Tool.DebuggerTool); // NOI18N
            }
            if (cs.getTool(Tool.DebuggerTool) == null) {
                    cs.addTool("", "", Tool.DebuggerTool); // NOI18N
            }
        }
        
        if (sets.size() == 0) { // No compilers found
            add(CompilerSet.createEmptyCompilerSet());
        }
    }
    
    private void initCompilerFilters() {
        gcc_filter = new CompilerFilenameFilter(gcc_pattern);
        gpp_filter = new CompilerFilenameFilter(gpp_pattern);
        cc_filter = new CompilerFilenameFilter(cc_pattern);
        fortran_filter = new CompilerFilenameFilter(fortran_pattern);
        if (Utilities.isUnix()) {
            CC_filter = new CompilerFilenameFilter(CC_pattern);
        }
        make_filter = new CompilerFilenameFilter(make_pattern);
        debugger_filter = new CompilerFilenameFilter(debugger_pattern);
    }
    
    /**
     * Add a CompilerSet to this CompilerSetManager. Make sure it doesn't get added multiple times.
     *
     * @param cs The CompilerSet to (possibly) add
     */
    public void add(CompilerSet cs) {
        String csdir = cs.getDirectory();
        
        if (sets.size() == 1 && sets.get(0).getName() == CompilerSet.None) {
            sets.remove(0);
        }
        for (CompilerSet cs2 : sets) {
            if (cs2.getDirectory().equals(csdir)) {
                return;
            }
        }
        sets.add(cs);
    }
    
    /**
     * Remove a CompilerSet from this CompilerSetManager. Use caution with this method. Its primary
     * use is to remove temporary CompilerSets which were added to represent missing compiler sets. In
     * that context, they're removed immediately after showing the ToolsPanel after project open.
     *
     * @param cs The CompilerSet to (possibly) remove
     */
    public void remove(CompilerSet cs) {
        if (sets.contains(cs)) {
            sets.remove(cs);
            if (CppSettings.getDefault().getCompilerSetName().equals(cs.getName())) {
                CppSettings.getDefault().setCompilerSetName("");
            }
            if (this == instance) {
                fireCompilerSetChangeNotification(instance);
            }
        }
        if (sets.size() == 0) { // No compilers found
            add(CompilerSet.createEmptyCompilerSet());
        }
    }
    
    public CompilerSet getCompilerSet(CompilerFlavor flavor) {
        return getCompilerSet(flavor.toString());
    }
    
    public CompilerSet getCompilerSet(String name) {
        for (CompilerSet cs : sets) {
            if (cs.getName().equals(name)) {
                return cs;
            }
        }
        return null;
    }
    
    public CompilerSet getCompilerSetByDisplayName(String name) {
        for (CompilerSet cs : sets) {
            if (cs.getDisplayName().equals(name)) {
                return cs;
            }
        }
        return null;
    }
        
    public CompilerSet getCompilerSet(String name, String dname) {
        for (CompilerSet cs : sets) {
            if (cs.getName().equals(name) && cs.getDisplayName().equals(dname)) {
                return cs;
            }
        }
        return null;
    }

    public CompilerSet getCompilerSet(int idx) {
        assert idx >= 0 && idx < sets.size();
        return sets.get(idx);
    }
    
    public List<CompilerSet> getCompilerSets() {
        return sets;
    }
    
    public List<String> getCompilerSetDisplayNames() {
        ArrayList<String> names = new ArrayList();
        for (CompilerSet cs : getCompilerSets()) {
            names.add(cs.getDisplayName());
        }
        return names;
    }
    
    public List<String> getCompilerSetNames() {
        ArrayList<String> names = new ArrayList();
        for (CompilerSet cs : getCompilerSets()) {
            names.add(cs.getName());
        }
        return names;
    }
    
    /**
     * Check if the gdb module is enabled. Don't show the gdb line if it isn't.
     *
     * @return true if the gdb module is enabled, false if missing or disabled
     */
    protected boolean isGdbEnabled() {
        Iterator iter = Lookup.getDefault().lookup(new Lookup.Template(ModuleInfo.class)).allInstances().iterator();
        while (iter.hasNext()) {
            ModuleInfo info = (ModuleInfo) iter.next();
            if (info.getCodeNameBase().equals("org.netbeans.modules.cnd.debugger.gdb") && info.isEnabled()) { // NOI18N
                return true;
            }
        }
        return false;
    }
    
    public static void addCompilerSetChangeListener(CompilerSetChangeListener l) {
        listeners.add(l);
    }
    
    public static void removeCompilerSetChangeListener(CompilerSetChangeListener l) {
        listeners.remove(l);
    }
    
    private static void fireCompilerSetChangeNotification(CompilerSetManager csm) {
        for (CompilerSetChangeListener l : listeners) {
            l.compilerSetChange(new CompilerSetEvent(csm));
        }
    }
    
    /** Special FilenameFilter which should recognize different variations of supported compilers */
    private class CompilerFilenameFilter implements FilenameFilter {
        
        Pattern pc = null;
        
        public CompilerFilenameFilter(String pattern) {
            try {
                pc = Pattern.compile(pattern);
            } catch (PatternSyntaxException ex) {
            }
        }
        
        public boolean accept(File dir, String name) {
            return pc != null && pc.matcher(name).matches();
        }
    }
}
