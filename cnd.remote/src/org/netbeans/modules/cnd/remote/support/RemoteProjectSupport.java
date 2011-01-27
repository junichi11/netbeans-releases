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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.remote.support;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.utils.CndPathUtilitities;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationSupport;
import org.netbeans.modules.cnd.makeproject.api.configurations.Item;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.netbeans.modules.cnd.remote.sync.SharabilityFilter;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;

/**
 * Misc projct related utility functions
 * @author Vladimir Kvashin
 */
public class RemoteProjectSupport {

    private RemoteProjectSupport() {
    }

    public static ExecutionEnvironment getExecutionEnvironment(Project project) {
        MakeConfiguration mk = ConfigurationSupport.getProjectActiveConfiguration(project);
        if (mk != null) {
            return mk.getDevelopmentHost().getExecutionEnvironment();
        }
        return null;
    }

    public static boolean projectExists(Project project) {
        File baseDir = CndFileUtils.toFile(project.getProjectDirectory()).getAbsoluteFile();
        File nbproject = CndFileUtils.createLocalFile(baseDir, "nbproject"); //NOI18N
        return nbproject.exists();
    }

    public static File getPrivateStorage(Project project) {
        File baseDir = CndFileUtils.toFile(project.getProjectDirectory()).getAbsoluteFile();
        final File privProjectStorage = CndFileUtils.createLocalFile(new File(baseDir, "nbproject"), "private"); //NOI18N
        return privProjectStorage;
    }

    public static File[] getProjectSourceDirs(Project project) {
        MakeConfiguration conf = ConfigurationSupport.getProjectActiveConfiguration(project);
        if (conf == null) {
            File baseDir = CndFileUtils.toFile(project.getProjectDirectory()).getAbsoluteFile();
            return new File[] { baseDir };
        } else {
            return getProjectSourceDirs(project, conf);
        }
    }

    public static File[] getProjectSourceDirs(Project project, MakeConfiguration conf) {
        File baseDir = CndFileUtils.toFile(project.getProjectDirectory()).getAbsoluteFile();
        if (conf == null) {
            return new File[] { baseDir };
        }
        // the project itself
        Set<File> sourceFilesAndDirs = new HashSet<File>();
        if (!conf.isMakefileConfiguration()) {
            sourceFilesAndDirs.add(baseDir);
        }

        MakeConfigurationDescriptor mcs = MakeConfigurationDescriptor.getMakeConfigurationDescriptor(project);
        if (mcs == null) {
            return new File[0];
        }
        for(String soorceRoot : mcs.getSourceRoots()) {
            String path = CndPathUtilitities.toAbsolutePath(baseDir.getAbsolutePath(), soorceRoot);
            File file = CndFileUtils.createLocalFile(path); // or canonical?
            sourceFilesAndDirs.add(file);
        }
        addExtraFiles(mcs, sourceFilesAndDirs);
        // Make sure 1st level subprojects are visible remotely
        // First, remembr all subproject locations
        for (String subprojectDir : conf.getSubProjectLocations()) {
            subprojectDir = CndPathUtilitities.toAbsolutePath(baseDir.getAbsolutePath(), subprojectDir);
            sourceFilesAndDirs.add(CndFileUtils.createLocalFile(subprojectDir));
        }
        // Then go trough open subprojects and add their external source roots
        for (Project subProject : conf.getSubProjects()) {
            MakeConfigurationDescriptor subMcs =
                    MakeConfigurationDescriptor.getMakeConfigurationDescriptor(subProject);
            for(String soorceRoot : mcs.getSourceRoots()) {
                File file = CndFileUtils.createLocalFile(soorceRoot).getAbsoluteFile(); // or canonical?
                sourceFilesAndDirs.add(file);
            }
            addExtraFiles(subMcs, sourceFilesAndDirs);
        }
        return sourceFilesAndDirs.toArray(new File[sourceFilesAndDirs.size()]);
    }

    private static void addExtraFiles(MakeConfigurationDescriptor subMcs, Set<File> filesToSync) {
        FileFilter filter = new SharabilityFilter();
        for (Item item : subMcs.getProjectItems()) {
            File normFile = item.getNormalizedFile();
            if (!filter.accept(normFile)) {
                // user explicitely added file -> copy it even
                filesToSync.add(normFile);
            } else if (!isContained(normFile, filesToSync)) {
                // directory containing file is not yet added => copy it
                filesToSync.add(normFile);
            } else if (!normFile.exists()) {
                // it won't be added while recursin into directories
                filesToSync.add(normFile);
            }
        }
    }

    private static boolean isContained(File normFile, Set<File> files) {
        String itemAbsPath = normFile.getAbsolutePath();
        for (File dir : files) {
            if (dir.isDirectory()) {
                String alreadyAddedPath = dir.getAbsolutePath() + File.separatorChar;
                if (itemAbsPath.startsWith(alreadyAddedPath)) {
                    return true;
                }
            }
        }
        return false;
    }
}
