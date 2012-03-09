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

package org.netbeans.core.startup.preferences;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.util.EditableProperties;
import org.openide.util.Exceptions;

/**
 * No synchronization - must be called just from NbPreferences which
 *  ensures proper synchronization
 * @author Radek Matous
 */
class PropertiesStorage implements NbPreferences.FileStorage {
    private static final String USERROOT_PREFIX = "/Preferences";//NOI18N
    private static final String SYSTEMROOT_PREFIX = "/SystemPreferences";//NOI18N
    private final static FileObject SFS_ROOT = FileUtil.getConfigRoot();
    
    private final String folderPath;
    private String filePath;
    private boolean isModified;
    private FileChangeAdapter fileChangeAdapter;
    
    
    static NbPreferences.FileStorage instance(final String absolutePath) {
        return new PropertiesStorage(absolutePath, true);
    }
    
    FileObject preferencesRoot() throws IOException {
        return FileUtil.createFolder(SFS_ROOT, USERROOT_PREFIX);
    }
    
    static NbPreferences.FileStorage instanceReadOnly(final String absolutePath) {
        return new PropertiesStorage(absolutePath, false) {
            public @Override boolean isReadOnly() {
                return true;
            }
            
            public @Override final String[] childrenNames() {
                return new String[0];
            }
            
            public @Override final EditableProperties load() throws IOException {
                return new EditableProperties(true);
            }
            
            protected @Override FileObject toPropertiesFile(boolean create) throws IOException {
                if (create) {
                    throw new IOException();
                }
                return null;
            }
            
            protected @Override FileObject toFolder(boolean create) throws IOException {
                if (create) {
                    throw new IOException();
                }
                return null;
            }
            
            protected @Override FileObject toPropertiesFile() {
                return null;
            }
            
            protected @Override FileObject toFolder() {
                return null;
            }
            
            @Override FileObject preferencesRoot() throws IOException {
                return FileUtil.createFolder(SFS_ROOT, SYSTEMROOT_PREFIX);
            }
            
        };
    }
    
    /** Creates a new instance */
    private PropertiesStorage(final String absolutePath, boolean userRoot) {
        StringBuilder sb = new StringBuilder();
        String prefix = (userRoot) ? USERROOT_PREFIX : SYSTEMROOT_PREFIX;
        sb.append(prefix).append(absolutePath);
        folderPath = sb.toString();
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
    public void markModified() {
        isModified = true;
    }
    
    public final boolean existsNode() {
        return (toPropertiesFile() != null) || (toFolder() != null);
    }
    
    public String[] childrenNames() {
        Statistics.StopWatch sw = Statistics.getStopWatch(Statistics.CHILDREN_NAMES, true);
        try {
            FileObject folder = toFolder();
            List<String> folderNames = new ArrayList<String>();
            
            if (folder != null) {
                for (FileObject fo : Collections.list(folder.getFolders(false))) {
                    Enumeration<? extends FileObject> en = fo.getChildren(true);
                    while (en.hasMoreElements()) {
                        FileObject ffo = en.nextElement();
                        if (ffo.hasExt("properties")) { // NOI18N
                            folderNames.add(fo.getNameExt());
                            break;
                        }
                    }
                }
                for (FileObject fo : Collections.list(folder.getData(false))) {
                    if (fo.hasExt("properties")) { // NOI18N
                        folderNames.add(fo.getName());
                    }
                }
            }
            
            return folderNames.toArray(new String[folderNames.size()]);
        } finally {
            sw.stop();
        }
    }
    
    public final void removeNode() throws IOException {
        Statistics.StopWatch sw = Statistics.getStopWatch(Statistics.REMOVE_NODE, true);
        try {
            FileObject propertiesFile = toPropertiesFile();
            if (propertiesFile != null && propertiesFile.isValid()) {
                propertiesFile.delete();
                FileObject folder = propertiesFile.getParent();
                while (folder != null && folder != preferencesRoot() && folder.getChildren().length == 0) {
                    folder.delete();
                    folder = folder.getParent();
                }
            }
        } finally {
            sw.stop();
        }
    }
    
    public EditableProperties load() throws IOException {
        Statistics.StopWatch sw = Statistics.getStopWatch(Statistics.LOAD, true);
        try {
            EditableProperties retval = new EditableProperties(true);
            FileObject file = toPropertiesFile(false);
            if (file != null) {
                try {
                    InputStream is = file.getInputStream();
                    try {
                        retval.load(is);
                    } finally {
                        is.close();
                    }
                } catch (IllegalArgumentException x) { // #167745
                    Logger.getLogger(PropertiesStorage.class.getName()).log(Level.INFO, "While loading " + file, x);
                    file.delete();
                }
            }
            return retval;
        } finally {
            sw.stop();
        }
    }
    
    public void save(final EditableProperties properties) throws IOException {
        if (isModified) {
            Statistics.StopWatch sw = Statistics.getStopWatch(Statistics.FLUSH, true);
            try {
                isModified = false;
                if (!properties.isEmpty()) {
                    OutputStream os = null;
                    try {
                        os = outputStream();
                        properties.store(os);
                    } finally {
                        if (os != null) os.close();
                    }
                } else {
                    FileObject file = toPropertiesFile();
                    if (file != null) {
                        file.delete();
                    }
                    FileObject folder = toFolder();
                    while (folder != null && folder != preferencesRoot() && folder.getChildren().length == 0) {
                        folder.delete();
                        folder = folder.getParent();
                    }
                }
            } finally {
                sw.stop();
            }
        }
    }
    
    private OutputStream outputStream() throws IOException {
        FileObject fo = toPropertiesFile(true);
        final FileLock lock = fo.lock();
        OutputStream os = null;
        try {
            os = fo.getOutputStream(lock);
        } finally {
            if(os == null && lock != null) {
                // release lock if getOutputStream failed
                lock.releaseLock();
            }
        }
        return new FilterOutputStream(os) {
            public @Override void close() throws IOException {
                super.close();
                lock.releaseLock();
            }
        };
    }
    
    private String folderPath() {
        return folderPath;
    }

    private String filePath() {
        if (filePath == null) {
            String[] all = folderPath().split("/");//NOI18N
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < all.length-1; i++) {
                sb.append(all[i]).append("/");//NOI18N
            }
            if (all.length > 0) {
                sb.append(all[all.length-1]).append(".properties");//NOI18N
            } else {
                sb.append("root.properties");//NOI18N
            }
            filePath = sb.toString();
        }
        return filePath;
    }        

    protected FileObject toFolder()  {
        return SFS_ROOT.getFileObject(folderPath());
    }

    protected  FileObject toPropertiesFile() {
        return SFS_ROOT.getFileObject(filePath());
    }

    protected FileObject toFolder(boolean create) throws IOException {
        FileObject retval = toFolder();
        if (retval == null && create) {
            retval = FileUtil.createFolder(SFS_ROOT, folderPath);
        }
        assert (retval == null && !create) || (retval != null && retval.isFolder());
        return retval;
    }
    
    protected FileObject toPropertiesFile(boolean create) throws IOException {
        FileObject retval = toPropertiesFile();
        if (retval == null && create) {
            retval = FileUtil.createData(SFS_ROOT,filePath());//NOI18N
        }
        assert (retval == null && !create) || (retval != null && retval.isData());
        return retval;
    }

    public void runAtomic(final Runnable run) {
        try {
            SFS_ROOT.getFileSystem().runAtomicAction(new AtomicAction() {
                public void run() throws IOException {
                    run.run();
                }
            });
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }


     @Override
     public void attachChangeListener(final ChangeListener changeListener) {
         try {            
             fileChangeAdapter = new FileChangeAdapter(){

              @Override
              public void fileDataCreated(FileEvent fe) {                  
                  if(fe.getFile().equals(toPropertiesFile())){
                      changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                  }
              }

              @Override
              public void fileFolderCreated(FileEvent fe) {                  
                  if(fe.getFile().equals(toPropertiesFile())){
                      changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                  }
              }

              @Override
              public void fileChanged(FileEvent fe) {                  
                  if(fe.getFile().equals(toPropertiesFile())){
                      changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                  }
              }

              @Override
              public void fileDeleted(FileEvent fe) {                 
                  if(fe.getFile().equals(toPropertiesFile())){
                      changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                  }
              }

              @Override
              public void fileRenamed(FileRenameEvent fe) {                 
                  if(fe.getFile().equals(toPropertiesFile())){
                      changeListener.stateChanged(new ChangeEvent(PropertiesStorage.this));
                  }
              }

          };
             SFS_ROOT.getFileSystem().addFileChangeListener(FileUtil.weakFileChangeListener(fileChangeAdapter, SFS_ROOT.getFileSystem()));
         } catch (FileStateInvalidException ex) {
             Exceptions.printStackTrace(ex);
         }
     }
}
