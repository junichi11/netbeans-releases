/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.java.classpath;

import java.beans.PropertyChangeListener;
import java.util.*;

import org.openide.execution.NbClassLoader;
import org.openide.filesystems.*;
import org.openide.util.WeakListener;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/** Classloader for the filesystem pool. Attaches itself as a listener to
 * each file a class has been loaded from. If such a file is deleted, modified
 * or renamed clears the global variable that holds "current" classloader, so
 * on next request for current one new is created.
 *
 * @author Jaroslav Tulach
 */
class ClassLoaderSupport extends NbClassLoader
    implements FileChangeListener, PropertyChangeListener {

    /** change listener */
    private org.openide.filesystems.FileChangeListener listener;

    /** PropertyChangeListener */
    private java.beans.PropertyChangeListener propListener;

    /** holds current classloader (or null if not created yet) */
    private static ClassLoaderSupport current;

    /** contains AllPermission */
    private static java.security.PermissionCollection allPermission;

    private static boolean firstTime = true;

    /**
     * The ClassPath to load classes from.
     */
    private ClassPath   classPath;

    /** @return the current classloader for the system */
    synchronized static ClassLoader currentClassLoader () {
        if (current == null) {
            current = new ClassLoaderSupport (ClassPath.getClassPath(null, ClassPath.EXECUTE));
            if (firstTime) {
                firstTime = false;
                Lookup.getDefault().lookup(new Lookup.Template(ClassLoader.class)).addLookupListener(new LookupListener() {
                    public void resultChanged(LookupEvent e) {
                        if (current != null) {
                            current.reset();
                        }
                    }
                });
            }
        }
        return current;
    }

    private static FileSystem[] getFileSystems(FileObject[] roots) {
        Collection fss = new ArrayList(roots.length);
        for (int i = 0; i < roots.length; i++) {
            try {
                fss.add(roots[i].getFileSystem());
            } catch (FileStateInvalidException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
        }
        return (FileSystem[])fss.toArray(new FileSystem[fss.size()]);
    }

    /** Constructor that attaches itself to the filesystem pool.
    */
    ClassLoaderSupport (ClassPath cp) {
        super(getFileSystems(cp.getRoots()));
        this.classPath = cp;

        setDefaultPermissions(getAllPermissions());
        listener = WeakListener.fileChange (this, null);
        propListener = WeakListener.propertyChange (this, null);
        cp.addPropertyChangeListener(propListener);
    }

    protected void finalize () {
        org.openide.ErrorManager.getDefault().getInstance("org.netbeans.api.java").log ("Collected currentClassLoader");
    }

    /**
     * Tries to locate the .class file on the ClassPath
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    protected Class findClass (String name) throws ClassNotFoundException {
        Class c = super.findClass (name);
        if (c != null) {
            org.openide.filesystems.FileObject fo;
            String resName = name.replace('.', '/') + ".class"; // NOI18N
            fo = classPath.findResource(resName);
            if (fo != null) {
                // if the file is from the file system pool,
                // register to catch its changes
                fo.addFileChangeListener (listener);
            }
        }
        return c;
    }

    /** Tests whether this object is current loader and if so,
    * clears the loader.
    * @param fo file object that initiated the action
    */
    private void test (org.openide.filesystems.FileObject fo) {
        classPath.resetClassLoader(this);
        fo.removeFileChangeListener (listener);
    }

    /** Resets the loader, removes it from listneing on all known objects.
    */
    private void reset () {
        classPath.resetClassLoader(this);
    }

    /** If this object is not current classloader, removes it from
    * listening on given file object.
    */
    private void testRemove (org.openide.filesystems.FileObject fo) {
        if (current != this) {
            fo.removeFileChangeListener (listener);
        }
    }

    /** Fired when a new folder has been created. This action can only be
    * listened in folders containing the created file up to the root of
    * file system.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileFolderCreated (org.openide.filesystems.FileEvent fe) {
        testRemove (fe.getFile ());
    }

    /** Fired when a new file has been created. This action can only be
    * listened in folders containing the created file up to the root of
    * file system.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileDataCreated (org.openide.filesystems.FileEvent fe) {
        testRemove (fe.getFile ());
    }

    /** Fired when a file has been changed.
    * @param fe the event describing context where action has taken place
    */
    public void fileChanged (org.openide.filesystems.FileEvent fe) {
        test (fe.getFile ());
    }

    /** Fired when a file has been deleted.
    * @param fe the event describing context where action has taken place
    */
    public void fileDeleted (org.openide.filesystems.FileEvent fe) {
        test (fe.getFile ());
    }

    /** Fired when a file has been renamed.
    * @param fe the event describing context where action has taken place
    *           and the original name and extension.
    */
    public void fileRenamed (org.openide.filesystems.FileRenameEvent fe) {
        test (fe.getFile ());
    }

    /** Fired when a file attribute has been changed.
    * @param fe the event describing context where action has taken place,
    *           the name of attribute and old and new value.
    */
    public void fileAttributeChanged (org.openide.filesystems.FileAttributeEvent fe) {
        testRemove (fe.getFile ());
    }
    
    /** Getter for allPermissions */
    static synchronized java.security.PermissionCollection getAllPermissions() {
        if (allPermission == null) {
            allPermission = new java.security.Permissions();
            allPermission.add(new java.security.AllPermission());
        }
        return allPermission;
    }

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *  	and the property that has changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (ClassPath.PROP_ROOTS.equals(evt.getPropertyName()))
            reset();
    }
}
