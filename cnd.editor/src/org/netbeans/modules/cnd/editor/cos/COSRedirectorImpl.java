/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.cnd.editor.cos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableOpenSupportRedirector;

/**
 *
 * @author inikiforov
 */
@ServiceProvider(service = CloneableOpenSupportRedirector.class, position = 1000)
public class COSRedirectorImpl extends CloneableOpenSupportRedirector {

    private static final Logger LOG = Logger.getLogger(COSRedirectorImpl.class.getName());
    private static final boolean ENABLED;
    private static final int L1_CACHE_SIZE = 10;
    private final static long INVALID_INODE = -1L;

    private static final Method getDataObjectMethod;

    static {
        Method m = null;
        try {
           m = OpenSupport.Env.class.getDeclaredMethod("getDataObject", new Class[0]); //NOI18N
           m.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            // ignoring
        } catch (SecurityException ex) {
            // ignoring
        } finally {
            getDataObjectMethod = m;
        }
    }

    static {
        String prop = System.getProperty("nb.cosredirector", "true");
        boolean enabled = true;
        try {
            enabled = Boolean.parseBoolean(prop);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
        ENABLED = enabled;
    }
    private final Map<Long, COSRedirectorImpl.Storage> imap = new HashMap<Long, COSRedirectorImpl.Storage>();
    private final LinkedList<Long> cache = new LinkedList<Long>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Override
    protected CloneableOpenSupport redirect(CloneableOpenSupport.Env env) {
        DataObject dobj = getDataObjectIfApplicable(env);
        if (dobj == null) {
            return null;
        }
        Lookup dobjLookup = dobj.getLookup();
        if (dobjLookup == null) {
            return null;
        }
        CloneableOpenSupport cos = env.findCloneableOpenSupport();
        lock.readLock().lock();
        try {
            for (Long n : cache) {
                COSRedirectorImpl.Storage storage = imap.get(n);
                if (storage != null) {
                    if (storage.hasDataObject(dobj)) {
                        CloneableOpenSupport aCes = storage.getCloneableOpenSupport(dobj, cos);
                        if (aCes != null) {
                            return aCes;
                        }
                        break;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        long inode = getINode(dobj);
        if (inode == INVALID_INODE) {
            return null;
        } 
        { // update L1 cache
            lock.writeLock().lock();
            try {
                cache.remove(inode);
                cache.addFirst(inode);
                if (cache.size() > L1_CACHE_SIZE) {
                    cache.removeLast();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        Storage list = findOrCreateINodeList(inode);
        if (list.addDataObject(inode, dobj, cos)) {
            return null;
        }
        return list.getCloneableOpenSupport(dobj, cos);
    }

    private Storage findOrCreateINodeList(long inode) {
        assert inode != INVALID_INODE;
        COSRedirectorImpl.Storage list;
        lock.writeLock().lock();
        try {
            list = imap.get(inode);
            if (list == null) {
                list = new COSRedirectorImpl.Storage();
                imap.put(inode, list);
            }
        } finally {
            lock.writeLock().unlock();
        }
        return list;
    }

    @Override
    protected void opened(CloneableOpenSupport.Env env) {
        redirect(env);
    }

    @Override
    protected void closed(CloneableOpenSupport.Env env) {
        DataObject dobj = getDataObjectIfApplicable(env);
        if (dobj == null) {
            return;
        }
        lock.writeLock().lock();
        try {
            for (long n : cache) {
                COSRedirectorImpl.Storage storage = imap.get(n);
                if (storage != null) {
                    if (storage.hasDataObject(dobj)) {
                        CloneableOpenSupport aCes = storage.getCloneableOpenSupport(dobj, env.findCloneableOpenSupport());
                        if (aCes != null) {
                            storage.removeDataObject(dobj);
                            cache.remove((Long) n);
                        }
                        break;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private DataObject getDataObjectIfApplicable(CloneableOpenSupport.Env env) {
        if (!ENABLED) {
            return null;
        }
        // disable on windows for now
        if (Utilities.isWindows()) {
            return null;
        }
        if (!(env instanceof OpenSupport.Env)) {
            return null;
        }
        DataObject dobj = null;
        if (getDataObjectMethod != null) {
            try {
                dobj = (DataObject) getDataObjectMethod.invoke(env);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (dobj == null) { // See CR#7117235
            return null;
        }
        if (!dobj.isValid()) {
            return null;
        }
        FileObject primaryFile = dobj.getPrimaryFile();
        if (primaryFile == null) {
            return null;
        }
        try {
            if (!CndFileUtils.isLocalFileSystem(primaryFile.getFileSystem())) {
                return null;
            }
        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        return dobj;
    }

    private static final class Storage {

        private final List<COSRedirectorImpl.StorageItem> list = new LinkedList<COSRedirectorImpl.StorageItem>();
        private WeakReference<CloneableOpenSupport> cosRef;

        private Storage() {
        }

        private synchronized boolean addDataObject(long origINode, DataObject dao, CloneableOpenSupport cos) {
            Iterator<COSRedirectorImpl.StorageItem> iterator = list.iterator();
            boolean found = false;
            while (iterator.hasNext()) {
                COSRedirectorImpl.StorageItem next = iterator.next();
                DataObject aDao = next.getValidDataObject();
                if (aDao == null) {
                    iterator.remove();
                } else if (aDao.equals(dao)) {
                    found = true;
                }
            }
            if (list.isEmpty()) {
                cosRef = null;
            }
            if (!found) {
                list.add(createItem(origINode, dao, cos));
            }
            if (cosRef == null || cosRef.get() == null) {
                cosRef = new WeakReference<CloneableOpenSupport>(cos);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.log(Level.FINE, "Store SES for {0}", dao.getPrimaryFile().getPath());
                }
                return true;
            }
            return false;
        }

        private synchronized void removeDataObject(DataObject dao) {
            Iterator<COSRedirectorImpl.StorageItem> iterator = list.iterator();
            while (iterator.hasNext()) {
                COSRedirectorImpl.StorageItem next = iterator.next();
                DataObject aDao = next.getValidDataObject();
                if (aDao.equals(dao)) {
                    iterator.remove();
                    return;
                }
            }
        }

        private synchronized boolean hasDataObject(DataObject dao) {
            Iterator<COSRedirectorImpl.StorageItem> iterator = list.iterator();
            while (iterator.hasNext()) {
                COSRedirectorImpl.StorageItem next = iterator.next();
                DataObject aDao = next.getValidDataObject();
                if (aDao == null) {
                    iterator.remove();
                } else if (aDao.equals(dao)) {
                    return true;
                }
            }
            return false;
        }

        private synchronized CloneableOpenSupport getCloneableOpenSupport(DataObject dao, CloneableOpenSupport cos) {
            CloneableOpenSupport aCos = null;
            if (cosRef != null) {
                aCos = cosRef.get();
                if (aCos == null) {
                    list.clear();
                    cosRef = null;
                } else {
                    return aCos;
                }
            }
            Iterator<COSRedirectorImpl.StorageItem> iterator = list.iterator();
            while (iterator.hasNext()) {
                COSRedirectorImpl.StorageItem next = iterator.next();
                DataObject aDao = next.getValidDataObject();
                if (aDao == null) {
                    iterator.remove();
                } else if (aDao.equals(dao)) {
                    cosRef = new WeakReference<CloneableOpenSupport>(cos);
                    aCos = cos;
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "Store SES for {0}", dao.getPrimaryFile().getPath());
                    }
                    break;
                }
            }
            return aCos;
        }
    }

    private static StorageItem createItem(long origINode, DataObject dao, CloneableOpenSupport origCOS) {
        StorageItem out = new COSRedirectorImpl.StorageItem(origINode, dao, origCOS);
        dao.addPropertyChangeListener(out);
        FileObject primaryFile = dao.getPrimaryFile();
        primaryFile.addFileChangeListener(out);
        return out;
    }
    
    private static long getINode(DataObject dao) {
        if (!dao.isValid()) {
            return INVALID_INODE;
        }
        BasicFileAttributes attrs = null;
        try {
            Path path = FileSystems.getDefault().getPath(FileUtil.getFileDisplayName(dao.getPrimaryFile()));
            attrs = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (FileNotFoundException ex) {
            // it is OK for file to be deleted
            LOG.log(Level.FINE, "can not get inode for {0}:\n{1}", new Object[] {dao, ex.getMessage()});
        } catch (NoSuchFileException ex) {
            // it is OK for file to be deleted
            LOG.log(Level.FINE, "can not get inode for {0}:\n{1}", new Object[] {dao, ex.getMessage()});
        } catch (IOException ex) {
            LOG.log(Level.INFO, "can not get inode for {0}:\n{1}", new Object[] {dao, ex.getMessage()});
        }
        Object key = null;
        if (attrs != null) {
            key = attrs.fileKey();
        }
        long inode = INVALID_INODE;
        if (key != null) {
            inode = key.hashCode();
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "getInode {0}[{1}], {2}", new Object[] {key, dao, inode});
            }
        }
        return inode;
    }
    
    private static final class StorageItem implements PropertyChangeListener, FileChangeListener {

        private final DataObject dao;
        private final long origINode;
        private final AtomicBoolean removed = new AtomicBoolean(false);
        private final CloneableOpenSupport origCOS;

        private StorageItem(long origINode, DataObject dao, CloneableOpenSupport cos) {
            this.dao = dao;
            this.origINode = origINode;
            this.origCOS = cos;
        }

        private DataObject getValidDataObject() {
            if (!removed.get() && dao.isValid()) {
                return dao;
            }
            return null;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!removed.get()) {
                if (evt.getPropertyName().equals(DataObject.PROP_NAME) ||
                        evt.getPropertyName().equals(DataObject.PROP_VALID) ||
                        evt.getPropertyName().equals(DataObject.PROP_PRIMARY_FILE)) {
                    if (!(evt.getSource() instanceof DataObject)) {
                        return;
                    }
                    DataObject toBeRemoved = (DataObject) evt.getSource();
                    if (dao.equals(toBeRemoved)) {
                        checkAndUpdateIfNeeded();
                    }
                }
            }
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
        }

        @Override
        public void fileChanged(FileEvent fe) {
            checkAndUpdateIfNeeded();
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            removed.set(true);
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            checkAndUpdateIfNeeded();
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
        }

        private void checkAndUpdateIfNeeded() {
            long curINode = getINode(dao);
            // track file remove followed by create with the same name
            // also handles file removes where curInode is invalid
            if (origINode != curINode) {
                LOG.log(Level.INFO, "inode file Changed {0} {1}->{2}", new Object[] {dao, origINode, curINode});
                if (removed.compareAndSet(false, true)) {
                    if (curINode != INVALID_INODE) {
                        // register orig COS under new INode 
                        COSRedirectorImpl instance = Lookup.getDefault().lookup(COSRedirectorImpl.class);
                        Storage list = instance.findOrCreateINodeList(curINode);
                        list.addDataObject(curINode, dao, origCOS);
                    }
                }
            }
        }
    }
}
