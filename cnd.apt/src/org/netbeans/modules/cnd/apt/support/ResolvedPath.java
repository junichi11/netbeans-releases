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

package org.netbeans.modules.cnd.apt.support;

import java.io.File;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.netbeans.modules.cnd.utils.cache.FilePathCache;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Alexander Simon
 */
public final class ResolvedPath {
    private final CharSequence folder;
    private final FileSystem fileSystem;
    private final CharSequence path;
    private final boolean isDefaultSearchPath;
    private final int index;
    
    public ResolvedPath(FileSystem fileSystem, CharSequence folder, CharSequence path, boolean isDefaultSearchPath, int index) {
        this.folder = folder;// should be already shared
        this.fileSystem = fileSystem;
        this.path = FilePathCache.getManager().getString(path);
        this.isDefaultSearchPath = isDefaultSearchPath;
        this.index = index;
        assert CndFileUtils.isExistingFile(fileSystem, this.path.toString()) : "isExistingFile failed in " + fileSystem + " for " + path;
        assert !CndFileUtils.isLocalFileSystem(fileSystem) || new File(this.path.toString()).isFile() : "not a file " + this.path;
        assert CndFileUtils.toFileObject(fileSystem, path) != null : "no FileObject in " + fileSystem + " for " + path + 
                " FileUtil.toFileObject = " + FileUtil.toFileObject(new File(FileUtil.normalizePath(path.toString()))) + // NOI18N
                " second check = " + fileSystem.findResource(path.toString()); // NOI18N
        CndUtils.assertNormalized(fileSystem, folder);
        CndUtils.assertNormalized(fileSystem, path);
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public FileObject getFileObject() {
        // using fileSystem.findResource is not safe, see #196425 -  AssertionError: no FileObject 
        return CndFileUtils.toFileObject(fileSystem, path);
    }
    
    /**
     * Resolved file path (normalized version)
     */
    public CharSequence getPath(){
        return path;
    }

    /**
     * Include path used for resolving file path
     */
    public CharSequence getFolder(){
        return folder;
    }

    /**
     * Returns true if path resolved from default path
     */
    public boolean isDefaultSearchPath(){
        return isDefaultSearchPath;
    }

    /**
     * Returns index of resolved path in user and system include paths
     */
    public int getIndex(){
        return index;
    }
    
    @Override
    public String toString(){
        return path + " in " + folder + " at " + fileSystem; // NOI18N
    }
}
