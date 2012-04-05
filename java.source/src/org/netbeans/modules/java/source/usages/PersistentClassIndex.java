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

package org.netbeans.modules.java.source.usages;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.ElementKind;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.source.*;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.netbeans.modules.java.source.indexing.TransactionContext;
import org.netbeans.modules.java.source.parsing.FileObjects;
import org.netbeans.modules.parsing.lucene.support.Convertor;
import org.netbeans.modules.parsing.lucene.support.Index;
import org.netbeans.modules.parsing.lucene.support.IndexManager;
import org.netbeans.modules.parsing.lucene.support.IndexReaderInjection;
import org.netbeans.modules.parsing.lucene.support.Queries;
import org.netbeans.modules.parsing.lucene.support.StoppableConvertor;
import org.netbeans.modules.parsing.lucene.support.StoppableConvertor.Stop;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 *
 * @author Petr Hrebejk, Tomas Zezula
 */
public final class PersistentClassIndex extends ClassIndexImpl {

    private final Index index;
    private final URL root;
    private final File cacheRoot;
    private final Type beforeInitType;
    private final Type finalType;
    private final IndexPatch indexPath;
    //@GuardedBy("this")
    private Set<String> rootPkgCache;
    private static final Logger LOGGER = Logger.getLogger(PersistentClassIndex.class.getName());
    private static final String REFERENCES = "refs";    // NOI18N
    
    /** Creates a new instance of ClassesAndMembersUQ */
    private PersistentClassIndex(
            final URL root,
            final File cacheRoot,
            final Type beforeInitType,
            final Type finalType)
	    throws IOException, IllegalArgumentException {
        assert root != null;
        this.root = root;
        this.cacheRoot = cacheRoot;
        this.beforeInitType = beforeInitType;
        this.finalType = finalType;
        this.index = IndexManager.createIndex(getReferencesCacheFolder(cacheRoot), DocumentUtil.createAnalyzer());
        this.indexPath = new IndexPatch(root,index);
    }
    
    @Override
    public BinaryAnalyser getBinaryAnalyser () {
        // TODO - run in tx ?
        return new BinaryAnalyser (new PIWriter(), this.cacheRoot);
    }
    
    @Override
    public SourceAnalyzerFactory.StorableAnalyzer getSourceAnalyser () {        
        Writer writer = new PIWriter();
        final TransactionContext txCtx = TransactionContext.get();
        assert  txCtx != null;
        
        PersistentIndexTransaction pit = txCtx.get(PersistentIndexTransaction.class);
        pit.addIndexWriter(writer);
        return SourceAnalyzerFactory.createStorableAnalyzer(writer);        
    }

    @Override
    public Type getType () {
        return getState() == State.INITIALIZED ? finalType : beforeInitType;
    }

    
    @Override
    public boolean isValid() {
        try {
            return index.getStatus(true) != Index.Status.INVALID;
        } catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public FileObject[] getSourceRoots () {
        FileObject[] rootFos;
        if (getType() == Type.SOURCE) {
            FileObject rootFo = URLMapper.findFileObject (this.root);
            rootFos = rootFo == null ? new FileObject[0]  : new FileObject[] {rootFo};
        }
        else {
            rootFos = SourceForBinaryQuery.findSourceRoots(this.root).getRoots();
        }
        return rootFos;
    }
    
    @Override
    public String getSourceName (final String binaryName) throws IOException, InterruptedException {
        try {
            final Query q = DocumentUtil.binaryNameQuery(binaryName);        
            Set<String> names = new HashSet<String>();
            index.query(names, DocumentUtil.sourceNameConvertor(), DocumentUtil.sourceNameFieldSelector(), cancel.get(), q);
            return names.isEmpty() ? null : names.iterator().next();
        } catch (IOException e) {
            return this.<String,IOException>handleException(null,e);
        }
    }
    

    // Factory method
    
    public static ClassIndexImpl create(
            final URL root,
            final File cacheRoot,
            final Type beforeInitType,
            final Type finalType)
	    throws IOException, IllegalArgumentException {        
        return new PersistentClassIndex(root, cacheRoot, beforeInitType, finalType);
    }
    
    // Implementation of UsagesQueryImpl ---------------------------------------    
    @Override
    public <T> void search (
            @NonNull final ElementHandle<?> element,
            @NonNull final Set<? extends UsageType> usageType,
            @NonNull final Set<? extends ClassIndex.SearchScopeType> scope,
            @NonNull final Convertor<? super Document, T> convertor,
            @NonNull final Set<? super T> result) throws InterruptedException, IOException {
        Parameters.notNull("element", element); //NOI18N
        Parameters.notNull("usageType", usageType); //NOI18N
        Parameters.notNull("scope", scope); //NOI18N
        Parameters.notNull("convertor", convertor); //NOI18N
        Parameters.notNull("result", result);   //NOI18N
        final Pair<Convertor<? super Document, T>,Index> ctu = indexPath.getPatch(convertor);
        try {
            final String binaryName = SourceUtils.getJVMSignature(element)[0];
            final ElementKind kind = element.getKind();
            if (kind == ElementKind.PACKAGE) {
                IndexManager.priorityAccess(new IndexManager.Action<Void> () {
                    @Override
                    public Void run () throws IOException, InterruptedException {
                        final Query q = QueryUtil.scopeFilter(
                                QueryUtil.createPackageUsagesQuery(binaryName,usageType,Occur.SHOULD),
                                scope);
                        if (q!=null) {
                            index.query(result, ctu.first, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), q);
                            if (ctu.second != null) {
                                ctu.second.query(result, convertor, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), q);
                            }
                        }
                        return null;
                    }
                });
            } else if (kind.isClass() ||
                       kind.isInterface() ||
                       kind == ElementKind.OTHER) {
                if (BinaryAnalyser.OBJECT.equals(binaryName)) {
                    getDeclaredTypes(
                        "", //NOI18N
                        ClassIndex.NameKind.PREFIX,
                        scope,
                        convertor,
                        result);
                } else {
                    IndexManager.priorityAccess(new IndexManager.Action<Void> () {
                        @Override
                        public Void run () throws IOException, InterruptedException {
                            final Query usagesQuery = QueryUtil.scopeFilter(
                                    QueryUtil.createUsagesQuery(binaryName, usageType, Occur.SHOULD),
                                    scope);
                            if (usagesQuery != null) {
                                index.query(result, ctu.first, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), usagesQuery);
                                if (ctu.second != null) {
                                    ctu.second.query(result, convertor, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), usagesQuery);
                                }
                            }
                            return null;
                        }
                    });
                }
            } else {
                throw new IllegalArgumentException(element.toString());
            }
        } catch (IOException ioe) {
            this.<Void,IOException>handleException(null, ioe);
        }
    }
    
                       
    @Override
    public <T> void getDeclaredTypes (
            @NonNull final String simpleName,
            @NonNull final ClassIndex.NameKind kind,
            @NonNull final Set<? extends ClassIndex.SearchScopeType> scope,
            @NonNull final Convertor<? super Document, T> convertor,
            @NonNull final Set<? super T> result) throws InterruptedException, IOException {
        final Pair<Convertor<? super Document, T>,Index> ctu = indexPath.getPatch(convertor);
        try {
            IndexManager.priorityAccess(new IndexManager.Action<Void> () {
                @Override
                public Void run () throws IOException, InterruptedException {
                    final Query query =  QueryUtil.scopeFilter(
                        Queries.createQuery(
                            DocumentUtil.FIELD_SIMPLE_NAME,
                            DocumentUtil.FIELD_CASE_INSENSITIVE_NAME,
                            simpleName,
                            DocumentUtil.translateQueryKind(kind)),
                        scope);
                    if (query != null) {
                        index.query(result, ctu.first, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), query);
                        if (ctu.second != null) {
                            ctu.second.query(result, convertor, DocumentUtil.declaredTypesFieldSelector(), cancel.get(), query);
                        }
                    }
                    return null;
                }
            });
        } catch (IOException ioe) {
            this.<Void,IOException>handleException(null,ioe);
        }
    }
    
    @Override
    public <T> void getDeclaredElements (
            final String ident,
            final ClassIndex.NameKind kind,
            final Convertor<? super Document, T> convertor,
            final Map<T,Set<String>> result) throws InterruptedException, IOException {
        final Pair<Convertor<? super Document, T>,Index> ctu = indexPath.getPatch(convertor);
        try {
            IndexManager.priorityAccess(new IndexManager.Action<Void>() {
                @Override
                public Void run () throws IOException, InterruptedException {
                    final Query query = Queries.createTermCollectingQuery(
                            DocumentUtil.FIELD_FEATURE_IDENTS,
                            DocumentUtil.FIELD_CASE_INSENSITIVE_FEATURE_IDENTS,
                            ident,
                            DocumentUtil.translateQueryKind(kind));
                    final Convertor<Term, String> t2s =
                        new Convertor<Term, String>(){
                            @Override
                            public String convert(Term p) {
                                return p.text();
                            }
                        };
                    index.queryDocTerms(
                            result,
                            ctu.first,
                            t2s,
                            DocumentUtil.declaredTypesFieldSelector(),
                            cancel.get(),
                            query);
                    if (ctu.second != null) {
                        ctu.second.queryDocTerms(
                            result,
                            convertor,
                            t2s,
                            DocumentUtil.declaredTypesFieldSelector(),
                            cancel.get(),
                            query);
                    }
                    return null;
                }
            });
        } catch (IOException ioe) {
            this.<Void,IOException>handleException(null, ioe);
        }
    }
    
    
    @Override
    public void getPackageNames (final String prefix, final boolean directOnly, final Set<String> result) throws InterruptedException, IOException {
        try {
            IndexManager.priorityAccess(new IndexManager.Action<Void>() {
                @Override
                public Void run () throws IOException, InterruptedException {
                    final boolean cacheOp = directOnly && prefix.length() == 0;
                    Set<String> myPkgs = null;
                    Collection<String> collectInto;
                    if (cacheOp) {
                        synchronized (PersistentClassIndex.this) {
                            if (rootPkgCache != null) {
                                result.addAll(rootPkgCache);
                                return null;
                            }
                        }
                        myPkgs = new HashSet<String>();
                        collectInto = new TeeCollection(myPkgs,result);
                    } else {
                        collectInto = result;
                    }
                    final Pair<StoppableConvertor<Term,String>,Term> filter = QueryUtil.createPackageFilter(prefix,directOnly);
                    index.queryTerms(collectInto, filter.second, filter.first, cancel.get());
                    if (cacheOp) {
                        synchronized (PersistentClassIndex.this) {
                            if (rootPkgCache == null) {
                                assert myPkgs != null;
                                rootPkgCache = myPkgs;
                            }
                        }
                    }
                    return null;
                }
            });
        } catch (IOException ioe) {
            this.<Void,IOException>handleException(null, ioe);
        }
    }
    
    @Override
    public void getReferencesFrequences (
            @NonNull final Map<String,Integer> typeFreq,
            @NonNull final Map<String,Integer> pkgFreq) throws IOException, InterruptedException {
        assert typeFreq != null;
        assert pkgFreq != null;
        try {
            IndexManager.priorityAccess(new IndexManager.Action<Void>() {
                @Override
                public Void run() throws IOException, InterruptedException {
                    final Term startTerm = DocumentUtil.referencesTerm("", null, true);    //NOI18N
                    final StoppableConvertor<Term,Void> convertor = new FreqCollector(
                            startTerm, typeFreq, pkgFreq);
                    index.queryTerms(
                        Collections.<Void>emptyList(),
                        startTerm,
                        convertor,
                        cancel.get());
                    return null;
                }
            });
        } catch (IOException ioe) {
            this.<Void,IOException>handleException(null, ioe);
        }
    }
        
    @Override
    public void setDirty (final URL url) {
        try {
            indexPath.setDirtyFile(url);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public @Override String toString () {
        return "PersistentClassIndex["+this.root.toExternalForm()+"]";     // NOI18N
    }
            
    //Protected methods --------------------------------------------------------
    @Override
    protected final void close () throws IOException {
        this.index.close();
    }


    // Private methods ---------------------------------------------------------                          
    
    private static File getReferencesCacheFolder (final File cacheRoot) throws IOException {
        File refRoot = new File (cacheRoot,REFERENCES);
        if (!refRoot.exists()) {
            refRoot.mkdir();
        }
        return refRoot;
    }
    
    

    private synchronized void resetPkgCache() {        
        rootPkgCache = null;
    }
    
    private class PIWriter implements Writer {
        
        PIWriter() {
            if (index instanceof Runnable) {
                ((Runnable)index).run();
            }
        }
        
        @Override
        public void clear() throws IOException {
            resetPkgCache();
            index.clear();
        }

        @Override
        public void deleteAndFlush(List<Pair<Pair<String, String>, Object[]>> refs, Set<Pair<String, String>> toDelete) throws IOException {
            resetPkgCache();
            if (index instanceof Index.Transactional) {
                ((Index.Transactional)index).txStore(refs, toDelete, DocumentUtil.documentConvertor(), DocumentUtil.queryClassConvertor());
            } else {
                // fallback to the old behaviour
                deleteAndStore(refs, toDelete);
            }
        }

        @Override
        public void commit() throws IOException {
            if (index instanceof Index.Transactional) {
                ((Index.Transactional)index).commit();
            }
        }
        
        @Override
        public void rollback() throws IOException {
            if (index instanceof Index.Transactional) {
                ((Index.Transactional)index).rollback();
            }
        }
        
        
        @Override
        public void deleteAndStore(List<Pair<Pair<String,String>, Object[]>> refs, Set<Pair<String, String>> toDelete) throws IOException {
            resetPkgCache();
            index.store(refs, toDelete, DocumentUtil.documentConvertor(), DocumentUtil.queryClassConvertor(), true);
        }
    }
    
    private static class TeeCollection<T> extends AbstractCollection<T> {
        
        private Collection<T> primary;
        private Collection<T> secondary;
              
        
        TeeCollection(final @NonNull Collection<T> primary, @NonNull Collection<T> secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        @Override
        public Iterator<T> iterator() {
            throw new UnsupportedOperationException("Not supported operation.");    //NOI18N
        }

        @Override
        public int size() {
            return primary.size();
        }

        @Override
        public boolean add(T e) {
            final boolean result = primary.add(e);
            secondary.add(e);
            return result;
        }
    }
    
    private static final class IndexPatch {
        
        private final URL root;
        private final Index baseIndex;
        //@GuardedBy("this")
        private Index indexPatch;
        //@GuardedBy("this")
        private URL dirty;
        //@GuardedBy("this")
        private Set<String> typeFilter;
        
        IndexPatch(
            @NonNull final URL root,
            @NonNull final Index baseIndex) {
            this.root = root;
            this.baseIndex = baseIndex;
        }
        
        <T> Pair<Convertor<? super Document, T>,Index> getPatch (
                @NonNull final Convertor<? super Document, T> delegate) {
            assert delegate != null;
            try {
                final Pair<Index,Set<String>> data = updateDirty();
                if (data != null) {
                    return Pair.<Convertor<? super Document, T>,Index>of(
                            new FilterConvertor<T>(data.second, delegate),
                            data.first);
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
            return Pair.<Convertor<? super Document, T>,Index>of(delegate,null);
        }
        
        synchronized void setDirtyFile(@NullAllowed final URL url) throws IOException {
            this.dirty = url;
            this.indexPatch = null;
            if (url == null) {
                typeFilter = null;
            }
        }
        
        @CheckForNull
        private Pair<Index,Set<String>> updateDirty () throws IOException {
            final URL url;
            Set<String> filter;
            Index result;
            synchronized (this) {
                url = this.dirty;
                filter = typeFilter;
                result = indexPatch;
            }
            if (url != null) {
                final FileObject file = url != null ? URLMapper.findFileObject(url) : null;
                final JavaSource js = file != null ? JavaSource.forFileObject(file) : null;
                final long startTime = System.currentTimeMillis();
                final List[] dataHolder = new List[1];
                if (js != null) {
                    final ClassPath scp = js.getClasspathInfo().getClassPath(PathKind.SOURCE);
                    if (scp != null && scp.contains(file)) {                    
                        js.runUserActionTask(new Task<CompilationController>() {
                            @Override
                            public void run (final CompilationController controller) {
                                try {                            
                                    if (controller.toPhase(Phase.RESOLVED).compareTo(Phase.RESOLVED)<0) {
                                        return;
                                    }
                                    try {
                                        final SourceAnalyzerFactory.SimpleAnalyzer sa = SourceAnalyzerFactory.createSimpleAnalyzer();
                                        dataHolder[0] = sa.analyseUnit(
                                            controller.getCompilationUnit(),
                                            JavaSourceAccessor.getINSTANCE().getJavacTask(controller),
                                            ClasspathInfoAccessor.getINSTANCE().getFileManager(controller.getClasspathInfo()));
                                    } catch (IllegalArgumentException ia) {
                                        //Debug info for issue #187344
                                        //seems that invalid dirty class index is used
                                        final ClassPath scp = controller.getClasspathInfo().getClassPath(PathKind.SOURCE);
                                        throw new IllegalArgumentException(
                                                String.format("Provided source path: %s root: %s",    //NOI18N
                                                    scp == null ? "<null>" : scp.toString(),    //NOI18N
                                                    root.toExternalForm()),
                                                ia);
                                    }
                                } catch (IOException ioe) {
                                    Exceptions.printStackTrace(ioe);
                                }
                            }
                        }, true);
                    } else {
                        LOGGER.log(
                                Level.INFO,
                                "Not updating cache for file {0}, does not belong to classpath {1}",    //NOI18N
                                new Object[] {
                                    FileUtil.getFileDisplayName(file),
                                    scp
                                });
                    }
                }
                final List<Pair<Pair<String, String>, Object[]>> data =
                        (List<Pair<Pair<String, String>, Object[]>>) dataHolder[0];
                if (data != null) {
                    if (filter == null) {
                        try {
                            filter = new HashSet<String>();
                            final String relPath = FileObjects.getRelativePath(root, url);
                            final String clsName = FileObjects.convertFolder2Package(
                                    FileObjects.stripExtension(relPath));
                            baseIndex.query(
                                    filter,
                                    DocumentUtil.binaryNameConvertor(),
                                    DocumentUtil.declaredTypesFieldSelector(),
                                    null,
                                    DocumentUtil.queryClassWithEncConvertor(true).convert(Pair.<String,String>of(clsName,relPath)));
                        } catch (URISyntaxException se) {
                            throw new IOException(se);
                        } catch (InterruptedException ie) {
                            //Never thrown, but throw as IOE for sure
                            throw new IOException(ie);
                        }
                    }
                    result = IndexManager.createMemoryIndex(DocumentUtil.createAnalyzer());
                    result.store(
                            data,
                            Collections.<Object>emptySet(),
                            DocumentUtil.documentConvertor(),
                            new NoCallConvertor(),
                            false);
                } else {
                    filter = null;
                    result = null;
                }
                synchronized (this) {
                    this.dirty = null;
                    this.typeFilter = filter;
                    this.indexPatch = result;
                }
                final long endTime = System.currentTimeMillis();
                LOGGER.log(Level.FINE, "PersistentClassIndex.updateDirty took: {0} ms", (endTime-startTime));     //NOI18N
            }
            if (result != null) {
                assert filter != null;
                return Pair.<Index,Set<String>>of(result,filter);
            } else {
                assert filter == null;
                return null;
            }
        }
        
    
        private static class FilterConvertor<T> implements Convertor<Document, T> {

            private final Set<String> toExclude;
            private final Convertor<? super Document, T> delegate;

            private FilterConvertor(
                    @NonNull final Set<String> toExclude,
                    @NonNull final Convertor<? super Document,T> delegate) {
                assert toExclude != null;
                assert delegate != null;
                this.toExclude = toExclude;
                this.delegate = delegate;
            }

            @Override
            @CheckForNull
            public T convert(@NonNull final Document doc) {
                final String rawName = DocumentUtil.getBinaryName(doc);
                return toExclude.contains(rawName) ? null : delegate.convert(doc);
            }
        }
        
        private static class NoCallConvertor<F,T> implements Convertor<F,T> {
            @Override
            public T convert(F p) {
                throw new IllegalStateException();
            }
        }
    }
    
    private static final class FreqCollector implements StoppableConvertor<Term, Void>, IndexReaderInjection {
        
        private final String fieldName;
        private final Map<String,Integer> typeFreq;
        private final Map<String,Integer> pkgFreq;
        private IndexReader in;
        
        
        FreqCollector(
                @NonNull final Term startTerm,
                @NonNull final Map<String,Integer> typeFreqs,
                @NonNull final Map<String,Integer> pkgFreq) {
            this.fieldName = startTerm.field();
            this.typeFreq = typeFreqs;
            this.pkgFreq = pkgFreq;
        }

        @CheckForNull
        @Override
        public Void convert(@NonNull final Term param) throws Stop {
            if (fieldName != param.field()) {
                throw new Stop();
            }
            try {
                final String encBinName = param.text();
                final String binName = encBinName.substring(
                    0,
                    encBinName.length() - ClassIndexImpl.UsageType.values().length);
                final int dotIndex = binName.lastIndexOf('.');  //NOI18N
                final String pkgName = dotIndex == -1 ? "" : binName.substring(0, dotIndex);    //NOI18N
                final int docCount = in.docFreq(param);
                final Integer typeCount = typeFreq.get(binName);
                final Integer pkgCount = pkgFreq.get(pkgName);
                typeFreq.put(binName, typeCount == null ? docCount : docCount + typeCount);
                pkgFreq.put(pkgName, pkgCount == null ? docCount : docCount + pkgCount);                
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                throw new Stop();
            }
            return null;
        }

        @Override
        public void setIndexReader(IndexReader indexReader) {
            in = indexReader;
        }
        
    }
}
