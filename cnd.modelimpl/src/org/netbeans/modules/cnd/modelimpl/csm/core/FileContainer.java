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

package org.netbeans.modules.cnd.modelimpl.csm.core;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.apt.debug.DebugUtils;
import org.netbeans.modules.cnd.apt.support.APTPreprocHandler;
import org.netbeans.modules.cnd.apt.support.APTHandlersSupport;
import org.netbeans.modules.cnd.modelimpl.debug.DiagnosticExceptoins;
import org.netbeans.modules.cnd.utils.cache.APTStringManager;
import org.netbeans.modules.cnd.utils.cache.FilePathCache;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelimpl.repository.FileContainerKey;
import org.netbeans.modules.cnd.modelimpl.repository.PersistentUtils;
import org.netbeans.modules.cnd.modelimpl.repository.RepositoryUtils;
import org.netbeans.modules.cnd.modelimpl.uid.LazyCsmCollection;
import org.netbeans.modules.cnd.modelimpl.uid.UIDCsmConverter;
import org.netbeans.modules.cnd.modelimpl.uid.UIDObjectFactory;
import org.netbeans.modules.cnd.repository.spi.Persistent;
import org.netbeans.modules.cnd.repository.support.SelfPersistent;

/**
 * Storage for files and states. Class was extracted from ProjectBase.
 * @author Alexander Simon
 */
/*package-local*/ class FileContainer extends ProjectComponent implements Persistent, SelfPersistent {
    private static final boolean TRACE_PP_STATE_OUT = DebugUtils.getBoolean("cnd.dump.preproc.state", false);
    private final Object lock = new Object();
    private Map<CharSequence, MyFile> myFiles = new ConcurrentHashMap<CharSequence, MyFile>();
    private Map<CharSequence, Object/*String or String[]*/> canonicFiles = new ConcurrentHashMap<CharSequence, Object/*String or String[]*/>();
    
    /** Creates a new instance of FileContainer */
    public FileContainer(ProjectBase project) {
	super(new FileContainerKey(project.getUniqueName().toString()));
	put();
    }
    
    public FileContainer (DataInput input) throws IOException {
	super(input);
        readStringToMyFileMap(input, myFiles);
        readStringToStringsArrMap(input, canonicFiles);
	//trace(canonicFiles, "Read in ctor:");
    }
    
    private void trace(Map<String, Object/*String or String[]*/> map, String title) {
	System.err.printf("%s\n", title);
	for( Map.Entry<String, Object> entry : map.entrySet() ) {
	    System.err.printf("%s ->\n%s\n\n", entry.getKey(), entry.getValue());
	}
    }
    
    public void putFile(File file, FileImpl impl, APTPreprocHandler.State state) {
        String path = getFileKey(file, true);
        MyFile newEntry;
        @SuppressWarnings("unchecked")
        CsmUID<CsmFile> uid = RepositoryUtils.put(impl);
        newEntry = new MyFile(uid, state, path);
        MyFile old;

        old = myFiles.put(path, newEntry);
        addAlternativeFileKey(path, newEntry.canonical);

        if (old != null){
            System.err.println("Replace file "+file.getAbsoluteFile());
        }
	put();
    }
    
    public void removeFile(File file) {
        String path = getFileKey(file, false);
        MyFile f;

        f = myFiles.remove(path);
        if (f != null) {
            removeAlternativeFileKey(f.canonical, path);
        }
        
        if (f != null) {
            if (f.fileNew != null){
                // clean repository
                if (false) RepositoryUtils.remove(f.fileNew);
            }
        }
	put();
    }
    
    public FileImpl getFile(File file) {
        MyFile f = getMyFile(file, false);
        if (f == null) {
            return null;
        }
        CsmUID<CsmFile> fileUID = f.fileNew;
        FileImpl impl = (FileImpl) UIDCsmConverter.UIDtoFile(f.fileNew);
        if( impl == null ) {
            DiagnosticExceptoins.register(new IllegalStateException("no file for UID " + fileUID)); // NOI18N
        }
        return impl;
    }

    public void putPreprocConditionState(Entry entry, FilePreprocessorConditionState pcState) {
        if (entry != null) {
            MyFile f = ((MyFile) entry);
//            if (pcState == null) {
//                System.err.printf("Null pcState for %s\n", f.canonical);
//            }
            f.setPCState(pcState);
        }
    }

    public void putPreprocState(File file, APTPreprocHandler.State state) {
        MyFile f = getMyFile(file, true);
        putPreprocState(f, state);
    }

    public void putPreprocState(Entry entry, APTPreprocHandler.State state) {
        if (entry == null) {
            return;
        }
        MyFile f = (MyFile) entry;
	if (f.getState() == null || !f.getState().isValid()) {
	    f.setState(state);
	} else {
	    if (f.getState().isCompileContext()) {
		if (state.isCompileContext()) {
		    f.setState(state);
		} else {
		    if (TRACE_PP_STATE_OUT) {
			System.err.println("Do not reset correct state to incorrect " + f.canonical);
		    }
		}
	    } else {
		f.setState(state);
	    }
	}
	if (TRACE_PP_STATE_OUT) {
	    System.err.println("\nPut state for file" + f.canonical + "\n");
	    System.err.println(state);
	}
    }
    
    public void invalidatePreprocState(File file) {
        MyFile f = getMyFile(file, false);
        if (f == null){
            return;
        }
        synchronized (f) {
            if (f.getState() != null){
                f.setState(APTHandlersSupport.createInvalidPreprocState(f.getState()));
            }
        }
        if (TRACE_PP_STATE_OUT) {
            String path = getFileKey(file, false);
            System.err.println("\nInvalidated state for file" + path + "\n");
        }
    }
    
    public APTPreprocHandler.State getPreprocState(File file) {
        MyFile f = getMyFile(file, false);
        if (f == null){
            return null;
        }
        return f.getState();
    }

    public FilePreprocessorConditionState getPreprocessorConditionState(File file) {
        MyFile f = getMyFile(file, false);
        return (f == null) ? null : f.getPCState();
    }

    public Entry getEntry(File file) {
        return getMyFile(file, false);
    }

    public Object getLock(Entry entry) {
        return (entry == null) ? lock : entry;
    }

    public Object getLock(File file) {
        MyFile f = getMyFile(file, false);
        return getLock(f);
    }
    
    public void clearState(){
        List<MyFile> files;
        files = new ArrayList<MyFile>(myFiles.values());
        for (MyFile file : files){
            synchronized (getLock(file)) {
                file.setState(null);
            }
        }
	put();
    }
    
    public Collection<CsmFile> getFiles() {
	List<CsmUID<CsmFile>> uids = new ArrayList<CsmUID<CsmFile>>(myFiles.values().size());
	getFiles2(uids);
	return new LazyCsmCollection<CsmFile, CsmFile>(uids, TraceFlags.SAFE_UID_ACCESS);
    }
    
    public Collection<FileImpl> getFileImpls() {
	List<CsmUID<CsmFile>> uids = new ArrayList<CsmUID<CsmFile>>(myFiles.values().size());
	getFiles2(uids);
	return new LazyCsmCollection<CsmFile, FileImpl>(uids, TraceFlags.SAFE_UID_ACCESS);
    }
    
    private void getFiles2(List<CsmUID<CsmFile>> res) {
        List<MyFile> files;
        files = new ArrayList<MyFile>(myFiles.values());
        for(MyFile f : files){
            res.add(f.fileNew);
        }
    }
    
    public void clear(){
        myFiles.clear();
	put();
    }
    
    public int getSize(){
        return myFiles.size();
    }
    
    @Override
    public void write(DataOutput aStream) throws IOException {
	super.write(aStream);
	// maps are concurrent, so we don't need synchronization here
        writeStringToMyFileMap(aStream, myFiles);
        writeStringToStringsArrMap(aStream, canonicFiles);
	//trace(canonicFiles, "Wrote in write()");
    }
    
    public static String getFileKey(File file, boolean sharedText) {
        String key = null;
        if (TraceFlags.USE_CANONICAL_PATH) {
            try {
                key = file.getCanonicalPath();
            } catch (IOException ex) {
                key = file.getAbsolutePath();
            }
        } else {
            key = file.getAbsolutePath();
        }
        return sharedText ? FilePathCache.getString(key).toString() : key;
    }
    

    private String getAlternativeFileKey(String primaryKey) {
        Object out = canonicFiles.get(primaryKey);
        if (out instanceof String) {
            return (String)out;
        } else if (out != null) {
            assert ((String[])out).length >= 2;
            return ((String[])out)[0];
        }
        return null;
    }
    
    private MyFile getMyFile(File file, boolean sharedText) {
        String path = getFileKey(file, sharedText);
        MyFile f = myFiles.get(path);
        if (f == null) {
            // check alternative expecting that 'path' is canonical path
            String path2 = getAlternativeFileKey(path);
            f = path2 == null ? null : myFiles.get(path2);
            if (f != null) {
                if (TraceFlags.TRACE_CANONICAL_FIND_FILE) {
                    System.err.println("alternative for " + path + " is " + path2);
                }
            }
        }
        return f;
    }
    
    private void addAlternativeFileKey(String primaryKey, CharSequence canonicKey) {
        Object out = canonicFiles.get(canonicKey);
        Object newVal;
        if (out == null) {
            newVal = primaryKey;
        } else {
            if (out instanceof String) {
                if (out.equals(primaryKey)) {
                    return;
                }
                newVal = new String[] {(String)out, primaryKey};
            } else {
                String[] oldAr = (String[])out;
                for(String what:oldAr){
                    if (what.equals(primaryKey)){
                        return;
                    }
                }
                String[] newAr = new String[oldAr.length + 1];
                System.arraycopy(oldAr, 0, newAr, 0, oldAr.length);
                newAr[oldAr.length] = primaryKey;
                newVal = newAr;
            }
        }
        canonicFiles.put(canonicKey, newVal);
        if (TraceFlags.TRACE_CANONICAL_FIND_FILE) {
            if (newVal instanceof String[]) {
                System.err.println("entry for " + canonicKey + " while adding " + primaryKey + " is " + Arrays.asList((String[])newVal).toString());
            } else {
                System.err.println("entry for " + canonicKey + " while adding " + primaryKey + " is " + newVal);
            }
        }                
    }
    
    private void removeAlternativeFileKey(CharSequence canonicKey, String primaryKey) {
        Object out = canonicFiles.get(canonicKey);
        assert out != null : "no entry for " + canonicKey + " of " + primaryKey;
        Object newVal;
        if (out instanceof String) {
            newVal = null;
        } else {
            String[] oldAr = (String[])out;
            assert oldAr.length >= 2;
            if (oldAr.length == 2) {
                newVal = oldAr[0].equals(primaryKey) ? oldAr[1] : oldAr[0];
            } else {
                String[] newAr = new String[oldAr.length - 1];
                int k = 0;
                for(String cur : oldAr){
                    if (!cur.equals(primaryKey)){
                        newAr[k++]=cur;
                    }
                }
                newVal = newAr;
            }
        }
        if (newVal == null) {
            canonicFiles.remove(primaryKey);
            if (TraceFlags.TRACE_CANONICAL_FIND_FILE) {
                System.err.println("removed entry for " + canonicKey + " while removing " + primaryKey);
            }
        } else {
            canonicFiles.put(canonicKey, newVal);
            if (TraceFlags.TRACE_CANONICAL_FIND_FILE) {
                System.err.println("change entry for " + canonicKey + " while removing " + primaryKey + " to " + newVal);
            }
        }
    }
    
    private static void writeStringToMyFileMap (
            final DataOutput output, Map<CharSequence, MyFile> aMap) throws IOException {
        assert output != null;
        assert aMap != null;
        int size = aMap.size();
        
        //write size
        output.writeInt(size);
        
        // write the map
        final Set<Map.Entry<CharSequence, MyFile>> entrySet = aMap.entrySet();
        final Iterator <Map.Entry<CharSequence, MyFile>> setIterator = entrySet.iterator();
        while (setIterator.hasNext()) {
            final Map.Entry<CharSequence, MyFile> anEntry = setIterator.next();

            output.writeUTF(anEntry.getKey().toString());
            assert anEntry.getValue() != null;
            anEntry.getValue().write(output);
        }
    }
    
    private static void  readStringToMyFileMap(
            final DataInput input, Map<CharSequence, MyFile> aMap) throws IOException {
        
        assert input != null; 
        assert aMap != null;
        
        final APTStringManager pathManager = FilePathCache.getManager();
        
        aMap.clear();
        final int size = input.readInt();
        
        for (int i = 0; i < size; i++) {
            String key = input.readUTF();
            key = pathManager == null? key: pathManager.getString(key).toString();
            MyFile value = new MyFile(input);
            
            assert key != null;
            assert value != null;
            
            aMap.put(key, value);
        }
    }
    
    private static void writeStringToStringsArrMap (
            final DataOutput output, final Map<CharSequence, Object/*String or String[]*/> aMap) throws IOException {
        
        assert output != null;
        assert aMap != null;
        
        final int size = aMap.size();
        output.writeInt(size);
        
        final Set<Map.Entry<CharSequence, Object>> entrySet = aMap.entrySet();
        final Iterator<Map.Entry<CharSequence, Object>> setIterator = entrySet.iterator();
        
        while (setIterator.hasNext()) {
            final Map.Entry<CharSequence, Object> anEntry = setIterator.next();
            assert anEntry != null;
            
            final CharSequence key = anEntry.getKey();
            final Object value = anEntry.getValue();
            assert key != null;
            assert value != null;
            assert ((value instanceof CharSequence) || (value instanceof CharSequence[]));
            
            output.writeUTF(key.toString());
            
            if (value instanceof String ) {
                output.writeInt(1);
                output.writeUTF((String)value);
            } else if (value instanceof String[]) {
                
                final String[] array = (String[]) value;
                
                output.writeInt(array.length);
                for (int j = 0; j < array.length; j++) {
                    output.writeUTF(array[j]);
                }
            }
        }
    }
    
    private static void readStringToStringsArrMap (
            final DataInput input, Map<CharSequence, Object/*String or String[]*/> aMap) throws IOException {
        assert input != null;
        assert aMap != null;
        
        final APTStringManager pathManager = FilePathCache.getManager();
        
        aMap.clear();
        
        final int size = input.readInt();
        
        for (int i = 0; i < size; i++) {
            String key = input.readUTF();
            key = pathManager == null ? key : pathManager.getString(key).toString();
            assert key != null;
            
            final int arraySize = input.readInt();
            assert arraySize != 0;
            
	    if( arraySize == 1 ) {
		aMap.put(key, input.readUTF());
	    }
	    else {
		final String[] value = new String[arraySize];
		for (int j = 0; j < arraySize; j++) {
		    String path = input.readUTF();
		    path = pathManager == null ? path : pathManager.getString(path).toString();
		    assert path != null;

		    value[j] = path;
		}
		aMap.put(key, value);
	    }
        }
    }

    public static interface Entry {
        APTPreprocHandler.State getState();
        FilePreprocessorConditionState getPCState();
        int getModCount();
    }

    private static final class MyFile implements Persistent, SelfPersistent, Entry {

        private final CsmUID<CsmFile> fileNew;
        private final CharSequence canonical;
        private APTPreprocHandler.State state;
        private FilePreprocessorConditionState pcState;
        private int modCount;
        
        private MyFile (final DataInput input) throws IOException {
            fileNew = UIDObjectFactory.getDefaultFactory().readUID(input);
            canonical = input.readUTF();
            modCount = input.readInt();
            if (input.readBoolean()){
                state = PersistentUtils.readPreprocState(input);
            }
            if (input.readBoolean()){
                pcState = new FilePreprocessorConditionState(input);
            }
        }
        
        private MyFile(CsmUID<CsmFile> fileNew, APTPreprocHandler.State state, CharSequence fileKey) {
            this.fileNew = fileNew;
            this.state = state;
            this.canonical = getCanonicalKey(fileKey);
            this.modCount = 0;
        }
        
        public void write(final DataOutput output) throws IOException {
            UIDObjectFactory.getDefaultFactory().writeUID(fileNew, output);
            output.writeUTF(canonical.toString());
            output.writeInt(modCount);
            output.writeBoolean(state != null);
            if (state != null) {
                PersistentUtils.writePreprocState(state, output);
            }
            output.writeBoolean(pcState != null);
            if (pcState != null) {
                pcState.write(output);
            }
        }

        public final int getModCount() {
            return modCount;
        }

        public final FilePreprocessorConditionState getPCState() {
            return pcState;
        }

        public final void setPCState(FilePreprocessorConditionState newPCState) {
            incrementModCount();
            pcState = newPCState;
        }

        public final APTPreprocHandler.State getState() {
            return state;
        }

        //package
        final void setState(APTPreprocHandler.State state) {
            incrementModCount();
            this.state = state;
        }

        private void incrementModCount() {
            modCount = (modCount == Integer.MAX_VALUE) ? 0 : modCount+1;
        }
    }
    
    private static final CharSequence getCanonicalKey(CharSequence fileKey) {
        try {
            String res = new File(fileKey.toString()).getCanonicalPath();
            if (fileKey.equals(res)) {
                return fileKey;
            }
            return FilePathCache.getString(res);
        } catch (IOException e) {
            // skip exception
            return fileKey;
        }
    }
}
