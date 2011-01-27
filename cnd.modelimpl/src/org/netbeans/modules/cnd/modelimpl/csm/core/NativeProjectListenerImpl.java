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

package org.netbeans.modules.cnd.modelimpl.csm.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.cnd.api.project.NativeFileItem;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.api.project.NativeProjectItemsListener;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelimpl.repository.RepositoryUtils;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Implementation of the NativeProjectItemsListener interface
 * @author Vladimir Kvashin
 */
// package-local
class NativeProjectListenerImpl implements NativeProjectItemsListener {
    private static final boolean TRACE = false;

    private final ModelImpl model;
    private final NativeProject nativeProject;
    // TODO: we have so much conversion to ProjectBase, while each ProjectBase have own listener associated with
    // own NativeProject, may be it's worth to reuse it and simplify all logic in this class?
    private final ProjectBase projectBase;
    private volatile boolean enabledEventsHandling = true;

    public NativeProjectListenerImpl(ModelImpl model, NativeProject nativeProject, ProjectBase project) {
	this.model = model;
        this.nativeProject = nativeProject;
        this.projectBase = project;
    }
    
    @Override
    public void fileAdded(NativeFileItem fileItem) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event fileAdded:"); // NOI18N
            System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
        }
	onProjectItemAdded(fileItem);
    }

    @Override
    public void filesAdded(List<NativeFileItem> fileItems) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event filesAdded:"); // NOI18N
            for(NativeFileItem fileItem: fileItems){
                System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
            }
        }
	for (List<NativeFileItem> list : divideByProjects(fileItems)){
	    onProjectItemAdded(list);
	}
    }

    @Override
    public void fileRemoved(NativeFileItem fileItem) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event fileRemoved:"); // NOI18N
            System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
        }
	onProjectItemRemoved(fileItem);
    }

    @Override
    public void filesRemoved(List<NativeFileItem> fileItems) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event filesRemoved:"); // NOI18N
            for(NativeFileItem fileItem: fileItems){
                System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
            }
        }
	for (List<NativeFileItem> list : divideByProjects(fileItems)){
	    onProjectItemRemoved(list);
	}
    }

    @Override
    public void fileRenamed(String oldPath, NativeFileItem newFileIetm){
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event fileRenamed:"); // NOI18N
            System.err.println("\tOld Name:"+oldPath); // NOI18N
            System.err.println("\tNew Name:"+newFileIetm.getAbsolutePath()); // NOI18N
        }
	onProjectItemRenamed(oldPath, newFileIetm);
    }

    @Override
    public void filePropertiesChanged(NativeFileItem fileItem) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event filePropertiesChanged:"); // NOI18N
            System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
        }
	onProjectItemChanged(fileItem);
    }

    @Override
    public void filesPropertiesChanged(final List<NativeFileItem> fileItems) {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event filesPropertiesChanged:"); // NOI18N
            for(NativeFileItem fileItem: fileItems){
                System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
            }
        }
        if (enabledEventsHandling) {
            // FIXUP for #109425
            ModelImpl.instance().enqueueModelTask(new Runnable() {
                @Override
                public void run() {
                    filesPropertiesChangedImpl(fileItems);
                }
            }, "Applying property changes"); // NOI18N
        } else {
            if (TraceFlags.TIMING) {
                System.err.printf("\nskipped filesPropertiesChanged(list) %s...\n",
                        nativeProject.getProjectDisplayName());
            }
        }
    }

    /*package*/final void enableListening(boolean enable) {
        if (TraceFlags.TIMING) {
            System.err.printf("\n%s ProjectListeners %s...\n", enable?"enable":"disable",
                    nativeProject.getProjectDisplayName());
        }
        enabledEventsHandling = enable;
    }

    private void filesPropertiesChangedImpl(List<NativeFileItem> fileItems) {
        for (List<NativeFileItem> list : divideByProjects(fileItems)){
            onProjectItemChanged(list);
        }
   }

    @Override
    public void filesPropertiesChanged() {
        if (TRACE) {
            new Exception().printStackTrace(System.err);
            System.err.println("Native event projectPropertiesChanged:"); // NOI18N
            for(NativeFileItem fileItem : nativeProject.getAllFiles()){
                System.err.println("\t"+fileItem.getAbsolutePath()); // NOI18N
            }
        }
        if (enabledEventsHandling) {
            // FIXUP for #109425
            ModelImpl.instance().enqueueModelTask(new Runnable() {
                @Override
                public void run() {
                    ArrayList<NativeFileItem> list = new ArrayList<NativeFileItem>();
                    for(NativeFileItem item : nativeProject.getAllFiles()){
                        if (!item.isExcluded()) {
                            switch(item.getLanguage()){
                                case C:
                                case CPP:
                                case FORTRAN:
                                    list.add(item);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    filesPropertiesChangedImpl(list);
                }
            }, "Applying property changes"); // NOI18N
        } else {
            if (TraceFlags.TIMING) {
                System.err.printf("\nskipped filesPropertiesChanged %s...\n", nativeProject.getProjectDisplayName());
            }
        }
    }

    @Override
    public void projectDeleted(NativeProject nativeProject) {
	RepositoryUtils.onProjectDeleted(nativeProject);
    }

    private Collection<List<NativeFileItem>> divideByProjects(List<NativeFileItem> fileItems){
	Map<NativeProject,List<NativeFileItem>> res = new HashMap<NativeProject,List<NativeFileItem>>();
	for(NativeFileItem item : fileItems){
	    NativeProject aNativeProject = item.getNativeProject();
	    if (aNativeProject != null){
		List<NativeFileItem> list = res.get(aNativeProject);
		if (list == null){
		    list =new ArrayList<NativeFileItem>();
		    res.put(aNativeProject,list);
		}
		list.add(item);
	    }
	}
	return res.values();
    }

    private void onProjectItemAdded(final NativeFileItem item) {
        try {
            final ProjectBase project = getProject(item, true);
            if( project != null ) {
                project.onFileAdded(item);
            }
        } catch( Exception e ) {
            e.printStackTrace(System.err);
        }
    }
    
    private void onProjectItemAdded(final List<NativeFileItem> items) {
        if (items.size()>0){
            try {
                final ProjectBase project = getProject(items.get(0), true);
                if( project != null ) {
                    project.onFileAdded(items);
                }
            } catch( Exception e ) {
                e.printStackTrace(System.err);
            }
        }
    }
    
    private void onProjectItemRemoved(final NativeFileItem item) {
        try {
            final ProjectBase project = getProject(item, false);
            if( project != null ) {
                FileObject fo = item.getFileObject();
                if (fo != null) {
                    fo.addFileChangeListener(FileUtil.weakFileChangeListener(new FileDeleteListener(project), fo));
                }
                project.onFileRemoved(item.getAbsolutePath());
            }
        } catch( Exception e ) {
            //TODO: FIX (most likely in Makeproject: path == null in this situation,
            //this cause NPE
            e.printStackTrace(System.err);
        }
    }
    
    private void onProjectItemRemoved(final List<NativeFileItem> items) {
        if (!items.isEmpty()) {
            try {
                final ProjectBase project = getProject(items.get(0), false);
                if( project != null ) {
                    project.onFileRemoved(items);
                }
            } catch( Exception e ) {
                e.printStackTrace(System.err);
            }
        }
    }

    private void onProjectItemRenamed(String oldPath, NativeFileItem newFileIetm) {
        try {
            final ProjectBase project = getProject(newFileIetm, false);
            if( project != null ) {
                project.onFileRemoved(oldPath);
                project.onFileAdded(newFileIetm);
            }
        } catch( Exception e ) {
            //TODO: FIX (most likely in Makeproject: path == null in this situation,
            //this cause NPE
            e.printStackTrace(System.err);
        }
    }
    
    private void onProjectItemChanged(final NativeFileItem item) {
        try {
            final ProjectBase project = getProject(item, false);
            if( project != null ) {
                project.onFilePropertyChanged(item);
            }
        } catch( Exception e ) {
            //TODO: FIX (most likely in Makeproject: path == null in this situation,
            //this cause NPE
            e.printStackTrace(System.err);
        }
    }
    
    private void onProjectItemChanged(final List<NativeFileItem> items) {
        if (items.size()>0){
            try {
                final ProjectBase project = getProject(items.get(0), true);
                if( project != null && project.isValid()) {
                    if (project instanceof ProjectImpl) {
                        LibraryManager.getInstance().onProjectPropertyChanged(project.getUID());
                    }
                    project.onFilePropertyChanged(items);
                }
            } catch( Exception e ) {
                e.printStackTrace(System.err);
            }
        }
    }
    
    private ProjectBase getProject(NativeFileItem nativeFile, boolean createIfNeeded) {
        assert nativeFile != null : "must not be null";
        ProjectBase csmProject = null;
        try {
            NativeProject aNnativeProject = nativeFile.getNativeProject();
            assert (aNnativeProject == null) || this.nativeProject.equals(aNnativeProject) : "listener was attached to " + this.nativeProject + "\n was notified from " + aNnativeProject;
            // removed file can have null project
            if (aNnativeProject == null) {
                aNnativeProject = this.nativeProject;
            }
            if (aNnativeProject != null) {
                csmProject = createIfNeeded ? (ProjectBase) model._getProject(aNnativeProject) :
                    (ProjectBase) model.findProject(aNnativeProject);
            }
        } catch(NullPointerException ex) {
            ex.printStackTrace(System.err);
        }
        return csmProject;
    }

    private static class FileDeleteListener extends FileChangeAdapter {
        private final ProjectBase project;

        public FileDeleteListener(ProjectBase project) {
            this.project = project;
        }
        
        @Override
        public void fileDeleted(FileEvent fe) {
            FileObject fo = fe.getFile();
            project.onFileRemoved(CndFileUtils.getNormalizedPath(fo));
            fo.removeFileChangeListener(this);
        }
    };
    
}
