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

package org.netbeans.modules.parsing.lucene.support;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.masterfs.providers.ProvidedExtensions;
import org.netbeans.modules.parsing.lucene.DocumentIndexImpl;
import org.netbeans.modules.parsing.lucene.IndexDocumentImpl;
import org.netbeans.modules.parsing.lucene.IndexFactory;
import org.netbeans.modules.parsing.lucene.LuceneIndexFactory;
import org.netbeans.modules.parsing.lucene.SimpleDocumentIndexCache;
import org.netbeans.modules.parsing.lucene.SupportAccessor;
import org.netbeans.modules.parsing.lucene.spi.ScanSuspendImplementation;
import org.netbeans.modules.parsing.lucene.support.Index.WithTermFrequencies.TermFreq;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Parameters;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 * The {@link IndexManager} controls access to {@link Index} instances and acts
 * as an {@link Index} factory.
 * 
 * @author Tomas Zezula
 */
public final class IndexManager {

    private static final ReentrantReadWriteLock lock  = new ReentrantReadWriteLock();
    private static final Lookup.Result<? extends ScanSuspendImplementation> res = Lookup.getDefault().lookupResult(ScanSuspendImplementation.class);
    private static final LookupListener lookupListener = new LookupListener() {
        @Override
        public void resultChanged(LookupEvent ev) {
            scanSuspendImpls = null;
        }
    };
    private static volatile Collection<? extends ScanSuspendImplementation> scanSuspendImpls;

    static IndexFactory factory = new LuceneIndexFactory();    //Unit tests overrides the factory
    
    static {
        SupportAccessor.setInstance(new SupportAccessorImpl());
        res.addLookupListener(WeakListeners.create(LookupListener.class, lookupListener, res));
    }
    
    private IndexManager() {}
    
    
    /**
     * The action to be performed under the {@link IndexManager}'s lock
     */
    public static interface Action<R> {
        
        /**
         * The action
         * @return the action result
         * @throws IOException
         * @throws InterruptedException
         */
        public R run () throws IOException, InterruptedException;
    }
        
    
    /**
     * Runs the given action under {@link IndexManager}'s write lock.
     * @param action the action to be performed.
     * @return the result of the action
     * @throws IOException when the action throws {@link IOException}
     * @throws InterruptedException when the action throws {@link InterruptedException}
     * @deprecated The {@link Index} is self guarded and global lock acquired by
     * {@link IndexManager#writeAccess} is not needed for correct synchronization.
     * To suspend the scan and external changes check during the action use {@link IndexManager#priorityAccess}.
     */
    @Deprecated
    public static <R> R writeAccess (final Action<R> action) throws IOException, InterruptedException {
        assert action != null;
        lock.writeLock().lock();                    
        try {
            return ProvidedExtensions.priorityIO(new Callable<R>() {
                @Override
                public R call() throws Exception {
                    return action.run();
                }
            });
        } catch (IOException ioe) {
            //rethrow ioe
            throw ioe;
        } catch (InterruptedException ie) {
            //rethrow ioe
            throw ie;
        } catch (RuntimeException re) {
            //rethrow ioe
            throw re;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Runs the given action under {@link IndexManager}'s read lock.
     * @param action the action to be performed.
     * @return the result of the action
     * @throws IOException when the action throws {@link IOException}
     * @throws InterruptedException when the action throws {@link InterruptedException}
     * @deprecated The {@link Index} is self guarded and global lock acquired by
     * {@link IndexManager#readAccess} is not needed for correct synchronization.
     * To suspend the scan and external changes check during the action use {@link IndexManager#priorityAccess}.
     */
    @Deprecated
    public static <R> R readAccess (final Action<R> action) throws IOException, InterruptedException {
        assert action != null;
        suspend();
        try {
            lock.readLock().lock();
            try {
                return ProvidedExtensions.priorityIO(new Callable<R>() {
                    @Override
                    public R call() throws Exception {
                        return action.run();
                    }
                });
            } catch (IOException ioe) {
                //rethrow ioe
                throw ioe;
            } catch (InterruptedException ie) {
                //rethrow ioe
                throw ie;
            } catch (RuntimeException re) {
                //rethrow ioe
                throw re;
            } catch (Exception e) {
                throw new IOException(e);
            } finally {
                lock.readLock().unlock();
            }
        } finally {
            resume();
        }
    }
    
    /**
     * Runs the given action as a priority action.
     * During the priority action scan and checking for external changes are
     * suspended.
     * @param action the action to be performed.
     * @return the result of the action
     * @throws IOException when the action throws {@link IOException}
     * @throws InterruptedException when the action throws {@link InterruptedException}
     * @since 2.9
     */
    public static <R> R priorityAccess(final Action<R> action) throws IOException, InterruptedException {
        assert action != null;
        suspend();
        try {
            return ProvidedExtensions.priorityIO(new Callable<R>() {
                @Override
                public R call() throws Exception {
                    return action.run();
                }
            });
        } catch (IOException ioe) {
            //rethrow ioe
            throw ioe;
        } catch (InterruptedException ie) {
            //rethrow ioe
            throw ie;
        } catch (RuntimeException re) {
            //rethrow ioe
            throw re;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            resume();
        }
    }

    /**
     * Checks if the caller thread holds the {@link IndexManager}'s write lock
     * @return true when the caller holds the lock
     */
    public static boolean holdsWriteLock () {
        return lock.isWriteLockedByCurrentThread();
    }
        
    /**
     * Creates a new {@link Index} for given folder with given lucene Analyzer.
     * The returned {@link Index} is not cached, next call with the same arguments returns a different instance
     * of {@link Index}. The caller is responsible to cache the returned {@link Index}.
     * @param cacheFolder the folder in which the index is stored
     * @param analyzer the lucene Analyzer used to split fields into tokens.
     * @return the created {@link Index}
     * @throws IOException in case of IO problem.
     */
    @NonNull
    public static Index createIndex(final @NonNull File cacheFolder, final @NonNull Analyzer analyzer) throws IOException {
        return createTransactionalIndex(cacheFolder, analyzer);
    }

    /**
     * Creates a new {@link Index.Transactional} for given folder with given lucene Analyzer.
     * The returned {@link Index} is not cached, next call with the same arguments returns a different instance
     * of {@link Index}. The caller is responsible to cache the returned {@link Index}.
     * @param cacheFolder the folder in which the index is stored
     * @param analyzer the lucene Analyzer used to split fields into tokens.
     * @return the created {@link Index.Transactional}
     * @throws IOException in case of IO problem.
     * @since 2.19
     */
    @NonNull
    public static Index.Transactional createTransactionalIndex(final @NonNull File cacheFolder, final @NonNull Analyzer analyzer) throws IOException {
        Parameters.notNull("cacheFolder", cacheFolder); //NOI18N
        Parameters.notNull("analyzer", analyzer);       //NOI18N
        if (!cacheFolder.canRead()) {
            throw new IOException(String.format("Cannot read cache folder: %s.", cacheFolder.getAbsolutePath()));   //NOI18N
        }
        if (!cacheFolder.canWrite()) {
            throw new IOException(String.format("Cannot write to cache folder: %s.", cacheFolder.getAbsolutePath()));   //NOI18N
        }
        final Index.Transactional index = factory.createIndex(cacheFolder, analyzer);
        assert index != null;
        indexes.put(cacheFolder, new Ref(cacheFolder,index));
        return index;
    }
    
    /**
     * Creates a transient {@link Index} stored in the RAM.
     * @param analyzer the lucene Analyzer used to split fields into tokens.
     * @return the created {@link Index}
     * @throws IOException in case of IO problem.
     * @since 2.8
     */
    public static Index createMemoryIndex(final @NonNull Analyzer analyzer) throws IOException {        
        Parameters.notNull("analyzer", analyzer);       //NOI18N
        final Index index = factory.createMemoryIndex(analyzer);
        assert index != null;
        return index;
    }

    /**
     * Returns existing {@link Index}es.
     * @return the mapping of cache folder to opened index.
     * @since 2.4
     */
    @NonNull
    public static Map<File,Index> getOpenIndexes() {
        final Map<File,Index> result = new HashMap<File, Index>();
        synchronized (indexes) {
            for (Map.Entry<File,Reference<Index>> e : indexes.entrySet()) {
                final File folder = e.getKey();
                final Index index = e.getValue().get();
                if (index != null) {
                    result.put(folder, index);
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * Creates a document based index
     * The returned {@link Index} is not cached, next call with the same arguments returns a different instance
     * of {@link Index}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param index the low level index to which the document based index delegates
     * @return the document based index
     * @since 1.1
     */
    public static DocumentIndex createDocumentIndex (final @NonNull Index index) {
        Parameters.notNull("index", index);
        return createDocumentIndex(index, new SimpleDocumentIndexCache());
    }

    /**
     * Creates a document based index.
     * The returned {@link DocumentIndex} is not cached, next call with the same arguments returns a different instance
     * of {@link DocumentIndex}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param index the low level index to which the document based index delegates
     * @param cache the document caching provider
     * @return the document based index
     * @since 2.18.0
     */
    public static DocumentIndex createDocumentIndex (
            final @NonNull Index index,
            final @NonNull DocumentIndexCache cache) {
        Parameters.notNull("index", index);     //NOI18N
        Parameters.notNull("cache", cache);     //NOI18N
        return DocumentIndexImpl.create(index, cache);
    }
    
    /**
     * Creates a document based index
     * The returned {@link Index} is not cached, next call with the same arguments returns a different instance
     * of {@link Index}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param cacheFolder the folder in which the index should be stored
     * @return the document based index
     * @since 1.1
     */
    public static DocumentIndex createDocumentIndex (final @NonNull File cacheFolder) throws IOException {
        Parameters.notNull("cacheFolder", cacheFolder);
        return createDocumentIndex(createIndex(cacheFolder, new KeywordAnalyzer()));
    }

    /**
     * Creates a document based index
     * The returned {@link Index} is not cached, next call with the same arguments returns a different instance
     * of {@link Index}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param cacheFolder the folder in which the index should be stored
     * @param cache the document caching provider
     * @return the document based index
     * @since 2.18.0
     */
    public static DocumentIndex createDocumentIndex (
            final @NonNull File cacheFolder,
            final @NonNull DocumentIndexCache cache) throws IOException {
        Parameters.notNull("cacheFolder", cacheFolder);     //NOI18N
        Parameters.notNull("cache", cache);                 //NOI18N
        return createDocumentIndex(createIndex(cacheFolder, new KeywordAnalyzer()), cache);
    }

    /**
     * Creates a transactional document based index.
     * The returned {@link DocumentIndex} is not cached, next call with the same arguments returns a different instance
     * of {@link DocumentIndex}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param index the low level index to which the document based index delegates
     * @param cache the document caching provider
     * @return the document based index
     * @since 2.19
     */
    @NonNull
    public static DocumentIndex.Transactional createTransactionalDocumentIndex (
            final @NonNull Index.Transactional index) {
        return createTransactionalDocumentIndex(index, new SimpleDocumentIndexCache());
    }

    /**
     * Creates a transactional document based index.
     * The returned {@link DocumentIndex} is not cached, next call with the same arguments returns a different instance
     * of {@link DocumentIndex}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param index the low level index to which the document based index delegates
     * @return the document based index
     * @since 2.19
     */
    @NonNull
    public static DocumentIndex.Transactional createTransactionalDocumentIndex (
            final @NonNull Index.Transactional index,
            final @NonNull DocumentIndexCache cache) {
        Parameters.notNull("index", index);     //NOI18N
        Parameters.notNull("cache", cache);     //NOI18N
        return DocumentIndexImpl.createTransactional(index, cache);
    }

    /**
     * Creates a transactional document based index.
     * The returned {@link DocumentIndex} is not cached, next call with the same arguments returns a different instance
     * of {@link DocumentIndex}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param cacheFolder the folder in which the index should be stored
     * @return the document based index
     * @since 2.19
     */
    @NonNull
    public static DocumentIndex.Transactional createTransactionalDocumentIndex (
            final @NonNull File cacheFolder) throws IOException {
        Parameters.notNull("cacheFolder", cacheFolder);     //NOI18N                 //NOI18N
        return createTransactionalDocumentIndex(cacheFolder, new SimpleDocumentIndexCache());
    }

    /**
     * Creates a transactional document based index.
     * The returned {@link DocumentIndex} is not cached, next call with the same arguments returns a different instance
     * of {@link DocumentIndex}. The caller is responsible to cache the returned {@link DocumentIndex}.
     * @param cacheFolder the folder in which the index should be stored
     * @param cache the document caching provider
     * @return the document based index
     * @since 2.19
     */
    @NonNull
    public static DocumentIndex.Transactional createTransactionalDocumentIndex (
            final @NonNull File cacheFolder,
            final @NonNull DocumentIndexCache cache) throws IOException {
        Parameters.notNull("cacheFolder", cacheFolder);     //NOI18N
        Parameters.notNull("cache", cache);                 //NOI18N
        return createTransactionalDocumentIndex(
                createTransactionalIndex(cacheFolder, new KeywordAnalyzer()),
                cache);
    }

    /**
     * Creates a new instance of {@link IndexDocument} to store in {@link DocumentIndex}
     * @param primaryKey the primary key of the document, for example relative path to the file.
     * The primary key is used in {@link DocumentIndex#removeDocument(java.lang.String)}
     * @return the document
     * @since 1.1
     */
    public static IndexDocument createDocument (final @NonNull String primaryKey) {
        Parameters.notNull("primaryKey", primaryKey);
        return new IndexDocumentImpl(primaryKey);
    }

    private static final Map<File,Reference<Index>> indexes =
            Collections.synchronizedMap(new HashMap<File, Reference<Index>>());

    private static class Ref extends WeakReference<Index> implements Runnable {

        private final File folder;

        Ref(@NonNull final File folder, @NonNull final Index index) {
            super(index, Utilities.activeReferenceQueue());
            this.folder = folder;
        }

        @Override
        public void run() {
            synchronized (indexes) {
                if (indexes.get(folder) == this) {
                    indexes.remove(folder);
                }
            }
        }
    }

    private static void suspend() {
        for (ScanSuspendImplementation impl : getScanSuspendImpls()) {
            impl.suspend();
        }
    }

    private static void resume() {
        for (ScanSuspendImplementation impl : getScanSuspendImpls()) {
            impl.resume();
        }
    }

    @NonNull
    private static Collection<? extends ScanSuspendImplementation> getScanSuspendImpls() {
        Collection<? extends ScanSuspendImplementation> result = scanSuspendImpls;
        if (result == null) {
            scanSuspendImpls = result = new ArrayList<>(res.allInstances());
        }
        return result;
    }
    
    private static class SupportAccessorImpl extends SupportAccessor {

        @Override
        @NonNull
        public TermFreq newTermFreq() {
            return new Index.WithTermFrequencies.TermFreq();
        }

        @Override
        public Index.WithTermFrequencies.TermFreq setTermFreq(
            @NonNull final TermFreq into,
            @NonNull final Term term,
            final int freq) {
            into.setTerm(term);
            into.setFreq(freq);
            return into;
        }
        
    }

}
