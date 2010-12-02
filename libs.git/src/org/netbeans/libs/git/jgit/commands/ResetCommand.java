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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.libs.git.jgit.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuildIterator;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.netbeans.libs.git.GitClient.ResetType;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.jgit.Utils;
import org.netbeans.libs.git.progress.FileListener;
import org.netbeans.libs.git.progress.ProgressMonitor;
import org.openide.util.NbBundle;

/**
 *
 * @author ondra
 */
public class ResetCommand extends GitCommand {

    private final File[] roots;
    private final FileListener listener;
    private final ProgressMonitor monitor;
    private final String revisionStr;
    private final ResetType resetType;
    private final boolean moveHead;

    public ResetCommand (Repository repository, String revision, File[] roots, ProgressMonitor monitor, FileListener listener) {
        super(repository, monitor);
        this.roots = roots;
        this.listener = listener;
        this.monitor = monitor;
        this.revisionStr = revision;
        this.resetType = ResetType.MIXED;
        moveHead = false;
    }

    public ResetCommand (Repository repository, String revision, ResetType resetType, ProgressMonitor monitor, FileListener listener) {
        super(repository, monitor);
        this.roots = new File[0];
        this.listener = listener;
        this.monitor = monitor;
        this.revisionStr = revision;
        this.resetType = resetType;
        moveHead = true;
    }

    @Override
    protected String getCommandDescription () {
        StringBuilder sb = new StringBuilder("git reset "); //NOI18N
        if (moveHead) {
            sb.append(resetType.toString()).append(" ").append(revisionStr); //NOI18N
        } else {
            sb.append(revisionStr);
            for (File root : roots) {
                sb.append(" ").append(root.getAbsolutePath()); //NOI18N
            }
        }
        return sb.toString();
    }

    @Override
    protected void run() throws GitException {
        Repository repository = getRepository();
        RevCommit commit = Utils.findCommit(repository, revisionStr);
        try {
            boolean finished = false;
            DirCache backup = repository.readDirCache();
            try {
                try {
                    DirCache cache = repository.lockDirCache();
                    try {
                        if (!resetType.equals(ResetType.SOFT)) {
                            TreeWalk treeWalk = new TreeWalk(repository);
                            DirCacheBuilder builder = cache.builder();
                            if (!moveHead) {
                                Collection<String> relativePaths = Utils.getRelativePaths(repository.getWorkTree(), roots);
                                if (!relativePaths.isEmpty()) {
                                    treeWalk.setFilter(PathFilterGroup.createFromStrings(relativePaths));
                                }
                            }
                            treeWalk.setRecursive(true);
                            treeWalk.reset();
                            treeWalk.addTree(new DirCacheBuildIterator(builder));
                            treeWalk.addTree(commit.getTree());
                            List<File> toDelete = new LinkedList<File>();
                            Map<File, DirCacheEntry> toCheckout = new LinkedHashMap<File, DirCacheEntry>();
                            String lastAddedPath = null;
                            while (treeWalk.next() && !monitor.isCanceled()) {
                                File path = new File(repository.getWorkTree(), treeWalk.getPathString());
                                int modeCache = treeWalk.getFileMode(0).getBits();
                                final int modeRev = treeWalk.getFileMode(1).getBits();
                                final ObjectId objIdRev = treeWalk.getObjectId(1);
                                final ObjectId objIdCache = treeWalk.getObjectId(0);
                                if (treeWalk.getPathString().equals(lastAddedPath)) {
                                    // skip conflicts
                                    continue;
                                } else {
                                    lastAddedPath = treeWalk.getPathString();
                                }
                                if (modeRev == FileMode.MISSING.getBits()) {
                                    // remove from index
                                    listener.notifyFile(path, treeWalk.getPathString());
                                    toDelete.add(path);
                                } else if (modeRev != FileMode.MISSING.getBits() && modeCache != FileMode.MISSING.getBits() && !objIdCache.equals(objIdRev)
                                        || modeCache == FileMode.MISSING.getBits()) {
                                    // add entry
                                    listener.notifyFile(path, treeWalk.getPathString());
                                    DirCacheEntry e = new DirCacheEntry(treeWalk.getPathString());
                                    AbstractTreeIterator it = treeWalk.getTree(1, AbstractTreeIterator.class);
                                    e.setFileMode(it.getEntryFileMode());
                                    e.setLastModified(System.currentTimeMillis());
                                    e.setObjectId(it.getEntryObjectId());
                                    e.smudgeRacilyClean();
                                    builder.add(e);
                                    toCheckout.put(path, e);
                                } else {
                                    DirCacheEntry e = treeWalk.getTree(0, DirCacheBuildIterator.class).getDirCacheEntry();
                                    builder.add(e);
                                    toCheckout.put(path, e);
                                }
                            }
                            if (!monitor.isCanceled()) {
                                builder.commit();
                                finished = true;
                            }
                            if (resetType.equals(ResetType.HARD) && !monitor.isCanceled()) {
                                for (File file : toDelete) {
                                    deleteFile(file, roots.length > 0 ? roots : new File[] { repository.getWorkTree() });
                                }

                                for (Map.Entry<File, DirCacheEntry> e : toCheckout.entrySet()) {
                                    // ... create/overwrite this file ...
                                    File file = e.getKey();
                                    if (!ensureParentFolderExists(file.getParentFile())) {
                                        continue;
                                    }
                                    if (file.isDirectory()) {
                                        monitor.notifyWarning(NbBundle.getMessage(ResetCommand.class, "MSG_Warning_ReplacingDirectory", file.getAbsolutePath())); //NOI18N
                                        Utils.deleteRecursively(file);
                                    }
                                    file.createNewFile();
                                    if (file.isFile()) {
                                        DirCacheCheckout.checkoutEntry(repository, file, e.getValue(), getFileMode(repository));
                                    } else {
                                        monitor.notifyWarning(NbBundle.getMessage(ResetCommand.class, "MSG_Warning_CannotCreateFile", file.getAbsolutePath())); //NOI18N
                                    }
                                }
                            }
                        }
                        if (moveHead && !monitor.isCanceled()) {
                            RefUpdate u = repository.updateRef(Constants.HEAD);
                            u.setNewObjectId(commit);
                            if (u.forceUpdate() == RefUpdate.Result.LOCK_FAILURE) {
                                throw new GitException(NbBundle.getMessage(ResetCommand.class, "MSG_Exception_CannotUpdateHead", revisionStr)); //NOI18N
                            }
                        }
                    } finally {
                        cache.unlock();
                    }
                } catch (IOException ex) {
                    throw new GitException(ex);
                }
            } finally {
                if (!finished) {
                    backup.lock();
                    try {
                        backup.write();
                    } finally {
                        backup.unlock();
                    }
                }
            }
        } catch (NoWorkTreeException ex) {
            throw new GitException(ex);
        } catch (CorruptObjectException ex) {
            throw new GitException(ex);
        } catch (IOException ex) {
            throw new GitException(ex);
        }
    }

    private Boolean filemode;

    private boolean getFileMode (Repository repository) {
        if (filemode == null) {
            filemode = Utils.checkExecutable(repository);
        }
        return filemode.booleanValue();
    }

    private void deleteFile (File file, File[] roots) {
        Set<File> rootFiles = new HashSet<File>(Arrays.asList(roots));
        File[] children;
        while (file != null && !rootFiles.contains(file) && ((children = file.listFiles()) == null || children.length == 0)) {
            // file is an empty folder
            if (!file.delete()) {
                monitor.notifyWarning("Cannot delete " + file.getAbsolutePath());
            }
            file = file.getParentFile();
        }
    }

    private boolean ensureParentFolderExists (File parentFolder) {
        File predecessor = parentFolder;
        while (!predecessor.exists()) {
            predecessor = predecessor.getParentFile();
        }
        if (predecessor.isFile()) {
            if (!predecessor.delete()) {
                monitor.notifyError("Cannot replace file " + predecessor.getAbsolutePath());
                return false;
            }
            monitor.notifyWarning("Replacing file " + predecessor.getAbsolutePath());
        }
        return parentFolder.mkdirs() || parentFolder.exists();
    }
}
