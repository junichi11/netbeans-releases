/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.source.parsing;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.java.preprocessorbridge.spi.JavaFileFilterImplementation;
import org.openide.util.Exceptions;

public class CachingArchive implements Archive {
    private final File archiveFile;
    private final boolean keepOpened;
    private ZipFile zipFile;
        
    byte[] names;// = new byte[16384];
    private int nameOffset = 0;
    final static int[] EMPTY = new int[0];
    private Map<String, Folder> folders; // = new HashMap<String, Folder>();

        // Constructors ------------------------------------------------------------    
    
    /** Creates a new instance of archive from zip file */
    public CachingArchive( File archiveFile, boolean keepOpened) {
        this.archiveFile = archiveFile;
        this.keepOpened = keepOpened;
    }
        
    // Archive implementation --------------------------------------------------
   
    /** Gets all files in given folder
     */
    public Iterable<JavaFileObject> getFiles( String folderName, ClassPath.Entry entry, JavaFileFilterImplementation filter ) throws IOException {
        doInit();        
        Folder files = folders.get( folderName );        
        if (files == null) {
            return Collections.<JavaFileObject>emptyList();
        }
        else {
            assert !keepOpened || zipFile != null;
            List<JavaFileObject> l = new ArrayList<JavaFileObject>(files.idx / 4);
            for (int i = 0; i < files.idx; i += 4){
                l.add(create(folderName, files, i));
            }
            return l;
        }
    }          
    

    private String getString(int off, int len) {
        byte[] name = new byte[len];
        System.arraycopy(names, off, name, 0, len);
        try {
            return new String(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("No UTF-8");
        }
    }
    
    private JavaFileObject create(String pkg, Folder f, int off) {
        String baseName = getString(f.indices[off], f.indices[off+1]);
        long mtime = (((long)f.indices[off+3]) << 32) | f.indices[off+2];
        if (zipFile == null) {
            return FileObjects.zipFileObject(archiveFile, pkg, baseName, mtime);
        } else {
            return FileObjects.zipFileObject( zipFile, pkg, baseName, mtime);
        }
    }
    
    public synchronized void clear () {
        folders = null;
        names = null;
    }
                      
    // ILazzy implementation ---------------------------------------------------
    
    public synchronized boolean isInitialized() {
        return folders != null;
    }
    
    public synchronized void initialize() {
        names = new byte[16384];
        folders = createMap( archiveFile );
        trunc();
    }
    
    // Private methods ---------------------------------------------------------
    
    private synchronized void doInit() {
        if ( !isInitialized() ) {
            initialize();
        }
    }

    private void trunc() {
        // strip the name array:
        byte[] newNames = new byte[nameOffset];
        System.arraycopy(names, 0, newNames, 0, nameOffset);
        names = newNames;

        // strip all the indices arrays:
        for (Iterator it = folders.values().iterator(); it.hasNext();) {
            ((Folder) it.next()).trunc();
        }
    }

    private Map<String,Folder> createMap(File file ) {        
        if (file.canRead()) {
            try {
                ZipFile zip = new ZipFile (file);
                try {
                    final Map<String,Folder> map = new HashMap<String,Folder>();

                    for ( Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                        ZipEntry entry = e.nextElement();
                        String name = entry.getName();
                        int i = name.lastIndexOf('/');
                        String dirname = i == -1 ? "" : name.substring(0, i /* +1 */);
                        String basename = name.substring(i+1);
                        if (basename.length() == 0) {
                            basename = null;
                        }
                        Folder fld = map.get(dirname);
                        if (fld == null) {
                            fld = new Folder();                
                            map.put(new String(dirname).intern(), fld);
                        }

                        if ( basename != null ) {
                            fld.appendEntry(this, basename, entry.getTime());
                        }
                    }
                    return map;
                } finally {
                    if (keepOpened) {
                        this.zipFile = zip;
                    }
                    else {
                        try {
                            zip.close();
                        } catch (IOException ioe) {
                            Exceptions.printStackTrace(ioe);
                        }
                    }
                }
            } catch (IOException ioe) {
                
            }
        }
        return Collections.<String,Folder>emptyMap();
    }
    
    // Innerclasses ------------------------------------------------------------
    
    int putName(byte[] name) {
        int start = nameOffset;

        if ((start + name.length) > names.length) {
            byte[] newNames = new byte[(names.length * 2) + name.length];
            System.arraycopy(names, 0, newNames, 0, start);
            names = newNames;
        }

        System.arraycopy(name, 0, names, start, name.length);
        nameOffset += name.length;

        return start;
    }

    
    private static class Folder {
        int[] indices = EMPTY; // off, len, mtimeL, mtimeH
        int idx = 0;

        public Folder() {
        }

        void appendEntry(CachingArchive outer, String name, long mtime) {
            // ensure enough space
            if ((idx + 4) > indices.length) {
                int[] newInd = new int[(2 * indices.length) + 4];
                System.arraycopy(indices, 0, newInd, 0, idx);
                indices = newInd;
            }

            try {
                byte[] bytes = name.getBytes("UTF-8");
                indices[idx++] = outer.putName(bytes);
                indices[idx++] = bytes.length;
                indices[idx++] = (int)(mtime & 0xFFFFFFFF);
                indices[idx++] = (int)(mtime >> 32);
            } catch (UnsupportedEncodingException e) {
                throw new InternalError("No UTF-8");
            }
        }

        void trunc() {
            if (indices.length > idx) {
                int[] newInd = new int[idx];
                System.arraycopy(indices, 0, newInd, 0, idx);
                indices = newInd;
            }
        }
    }

        
}
