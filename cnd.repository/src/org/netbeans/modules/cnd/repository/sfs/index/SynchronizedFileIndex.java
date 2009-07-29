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

package org.netbeans.modules.cnd.repository.sfs.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.netbeans.modules.cnd.repository.spi.Key;


/**
 * Synchronized index wrapper:
 * Delegate work to the given index, is responsible for synchronization
 * @author Vladimir Kvashin
 */
class SynchronizedFileIndex implements FileIndex {
    
    private final FileIndex delegate;
    private final ReadWriteLock indexLock = new ReentrantReadWriteLock();
    
    public SynchronizedFileIndex(FileIndex delegate) {
	this.delegate = delegate;
    }
    
    public int size() {
	indexLock.readLock().lock();
	try {
	    return delegate.size();
	} finally {
	    indexLock.readLock().unlock();
	}
    }
    
    private class SyncRemoveIterator<K> implements Iterator<K> {
	
	Iterator<K> delegate;
	
	SyncRemoveIterator(Iterator<K> delegate) {
	    this.delegate = delegate;
	}

	public void remove() {
	    indexLock.writeLock().lock();
	    try {
		delegate.remove();
	    } finally {
		indexLock.writeLock().unlock();
	    }
	}
	
	public K next() {
	    return delegate.next();
	}
	
	public boolean hasNext() {
	    return delegate.hasNext();
	}
    }
    
    public Iterator<Key> getKeySetIterator() {
        return new SyncRemoveIterator<Key>(delegate.getKeySetIterator());
    }
    
    public Collection<Key> keySet() {
        indexLock.readLock().lock();
        try {
            return new ArrayList<Key>(delegate.keySet());
        }  finally {
            indexLock.readLock().unlock();
        }
    }
    
    public int put(Key key, long offset, int size) {
        indexLock.writeLock().lock();
        try {
            return delegate.put(key, offset, size);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    public int remove(Key key) {
        indexLock.writeLock().lock();
        try {
            return delegate.remove(key);
        } finally {
            indexLock.writeLock().unlock();
        }
    }
    
    public ChunkInfo get(Key key) {
        indexLock.readLock().lock();
        try {
            return delegate.get(key);
        } finally {
            indexLock.readLock().unlock();
        }
    }    
}
