/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserBasedEmbeddingProvider;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.Task;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.FolderLookup;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;


/**
 *
 * @author Jan Jancura
 */
public class ParserManagerImpl {
    
    
    /**
     * Returns {@link Parser} for given {@link Source}
     * @param source for which the {@link Parser} should
     * be returned
     * @return a parser
     */
    static Parser getParser (final Source source) {
        assert source != null;
        Parser parser;
        synchronized (source) {
            parser = SourceAccessor.getINSTANCE().getParser(source);            
        }
        if (parser == null) {
            ParserFactory pf = null;    //Get parser factory for givem MIME Type
            parser = pf.createParser(source);
            assert parser != null : "Factory: " + pf.getClass().getName()+" returned null parser for: " + source;   //NOI18N
        }
        synchronized (source) {
            if (SourceAccessor.getINSTANCE().getParser(source)==null) {
                SourceAccessor.getINSTANCE().setParser(source, parser);
            }
        }
        return parser;
    }
    
//    
//    static void setDocument (Document document) {
//    }
//    
//    
//    private class DocumentManager {
//
//        private Document        document;
//        private List<Parser>    parsers;
//        private List<ATask>     tasks = new ArrayList<ATask> ();
//        
//        
//        public DocumentManager (final Document document) {
//            this.document = document;
//            String mimeType = (String) document.getProperty ("mimeType");
//            lookupInstances (document, mimeType);
//            parse ();
//        }
//        
//        private void lookupInstances (Document document, String mimeType) {
//            FileSystem fileSystem = Repository.getDefault ().getDefaultFileSystem ();
//            FileObject fileObject1 = fileSystem.findResource ("Editors");
//            DataFolder dataFolder1 = fileObject1 == null ? null : DataFolder.findFolder (fileObject1);
//            FileObject fileObject2 = fileSystem.findResource ("Editors/" + mimeType);
//            DataFolder dataFolder2 = fileObject2 == null ? null : DataFolder.findFolder (fileObject2);
//            Lookup lookup = dataFolder2 == null ? 
//                (Lookup) (dataFolder1 == null ? 
//                    Lookup.EMPTY :
//                    new FolderLookup (dataFolder1)) :
//                new ProxyLookup (
//                    new FolderLookup (dataFolder1).getLookup (),
//                    new FolderLookup (dataFolder2).getLookup ()
//                );
//            final Lookup.Result<ParserFactory> parsersResult = lookup.lookupResult (ParserFactory.class);
//            setParsers (parsersResult.allInstances ());
//            parsersResult.addLookupListener (new LookupListener () {
//                public void resultChanged (LookupEvent ev) {
//                    setParsers (parsersResult.allInstances ());
//                }
//            });
//            final Lookup.Result<TaskFactory> tasksResult = lookup.lookupResult (TaskFactory.class);
//            setTaskFactories (tasksResult.allInstances ());
//            parsersResult.addLookupListener (new LookupListener () {
//                public void resultChanged (LookupEvent ev) {
//                    setTaskFactories (tasksResult.allInstances ());
//                }
//            });
//            final Lookup.Result<EmbeddingProviderFactory> embeddingsResult = lookup.lookupResult (EmbeddingProviderFactory.class);
//            setEmbeddingProviderFactories (embeddingsResult.allInstances ());
//            parsersResult.addLookupListener (new LookupListener () {
//                public void resultChanged (LookupEvent ev) {
//                    setEmbeddingProviderFactories (embeddingsResult.allInstances ());
//                }
//            });
//            final Lookup.Result<ParserBasedEmbeddingProviderFactory> parserEmbeddingsResult = lookup.lookupResult (ParserBasedEmbeddingProviderFactory.class);
//            setParserbasedEmbeddingProviderFactories (parserEmbeddingsResult.allInstances ());
//            parsersResult.addLookupListener (new LookupListener () {
//                public void resultChanged (LookupEvent ev) {
//                    setParserbasedEmbeddingProviderFactories (parserEmbeddingsResult.allInstances ());
//                }
//            });
//        }
//        
//        private void parse () {
//            
//        }
//        
//        private void setParsers (Collection<? extends ParserFactory> parserFactories) {
//            parsers = new ArrayList<Parser> ();
//            for (ParserFactory parserFactory : parserFactories) {
//                Parser parser = parserFactory.createParser (document);
//                if (parser != null)
//                    parsers.add (parser);
//            }
//        }
//        
//        private void setTaskFactories (Collection<? extends TaskFactory> taskFactories) {
//            for (TaskFactory taskFactory : taskFactories) {
//                for (Parser parser : parsers) {
//                    Collection<Task> tasks = taskFactory.create (parser, document);
//                    for (Task task : tasks) {
//                        this.tasks.add (new TaskTask (parser, task));
//                    }
//                }
//            }
//        }
//        
//        private void setEmbeddingProviderFactories (Collection<? extends EmbeddingProviderFactory> embeddingFactories) {
//            for (EmbeddingProviderFactory taskFactory : embeddingFactories) {
//                EmbeddingProvider tasks = taskFactory.create (document);
//                this.tasks.add (new EmbeddingProviderTask (parser, task));
//            }
//        }
//    }
//    
//    private static abstract class ATask {
//        abstract int getPriority ();
//        abstract boolean needsParserResult ();
//        abstract void run (Source source, Map<Parser,Result> results);
//    }
//    
//    private static class TaskTask extends ATask {
//
//        private Parser      parser;
//        private Task        task;
//        
//        public TaskTask (Parser parser, Task task) {
//            this.parser = parser;
//            this.task = task;
//        }
//        
//        
//        int getPriority () {
//            return task.getPriority ();
//        }
//
//        boolean needsParserResult () {
//            return true;
//        }
//
//        void run (Source source, Map<Parser, Result> results) {
//            task.run (results.get (parser), source);
//        }
//    }
//    
//    private static class EmbeddingProviderTask extends ATask {
//
//        private EmbeddingProvider      embeddingProvider;
//        
//        public EmbeddingProviderTask (EmbeddingProvider embeddingProvider) {
//            this.embeddingProvider = embeddingProvider;
//        }
//        
//        
//        int getPriority () {
//            return embeddingProvider.getPriority ();
//        }
//
//        boolean needsParserResult () {
//            return false;
//        }
//
//        void run (Source source, Map<Parser, Result> results) {
//            embeddingProvider.getEmbeddings (source);
//        }
//    }
//    
//    private static class ParserBasedEmbeddingProviderTask extends ATask {
//
//        private Parser                            parser;
//        private ParserBasedEmbeddingProvider      embeddingProvider;
//        
//        public ParserBasedEmbeddingProviderTask (
//            Parser                          parser,
//            ParserBasedEmbeddingProvider    embeddingProvider
//        ) {
//            this.parser = parser;
//            this.embeddingProvider = embeddingProvider;
//        }
//        
//        
//        int getPriority () {
//            return embeddingProvider.getPriority ();
//        }
//
//        boolean needsParserResult () {
//            return false;
//        }
//
//        void run (Source source, Map<Parser, Result> results) {
//            embeddingProvider.getSources (results.get (parser), source);
//        }
//    }
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    private static Document                         currentDocument;
////    private static Map<Parser,List<PriorityParserListener>> parserToParserListeners;
//    private static Listener                         listener;
//    
//    /**
//     * Called from ParserManager.parseUserTask.
//     */
//    public static void parseUserTask (
//        List<Source>        sources, 
//        UserTask            parserListener
//    ) {
//        for (Source source:sources) {
//            Iterator<? extends Parser> it = getParsers ().iterator ();
//            while (it.hasNext ()) {
//                Parser parser = it.next ();
//                if (cache == null) continue;
//                Parser.Result result = cache.get (parser);
//                //Parser.Result result = parser.parseUserTask (document);
//                if (result != null)
//                    parserListener.parsed (result, source);
//            }
//        }
//    }
//    
//    /**
//     * Called from ParserManager.parseEmbedded.
//     */
//    public static void parseEmbedded (
//        Result              result, 
//        String              mimeType, 
//        Task      parserListener
//    ) {
//        
//    }
//    
//    
//    
//    static void setDocument (
//        Document            document
//    ) {
//        if (document == currentDocument) return;
//        System.out.println("\nParserManager.setDocument " + document.hashCode () + " (" + document.getProperty("title") + ")");
//        if (currentDocument != null) {
////            dispose (parserToParserListeners);
//            currentDocument.removeDocumentListener (listener);
//        }
//        currentDocument = document;
//        parserToParserListeners = new HashMap<Parser,List<PriorityParserListener>> ();
//        if (document != null) {
//            Iterator<? extends Parser> it = getParsers ().iterator ();
//            while (it.hasNext ()) {
//                Parser parser = it.next ();
//                List<PriorityParserListener> listeners = new ArrayList<PriorityParserListener> ();
//                Iterator<? extends TaskFactory> it2 = getParserListeresFactories ().iterator ();
//                while (it2.hasNext ()) {
//                    TaskFactory parserListenerFactory = it2.next ();
//                    PriorityParserListener parserListener = parserListenerFactory.create (parser, document);
//                    if (parserListener != null) {
//                        listeners.add (parserListener);
//                        System.out.println("  listener created " + parserListener + " (" + parser + ")");
//                    }
//                }
//                Collections.sort (listeners, parserListenerComparator);
//                parserToParserListeners.put (parser, listeners);
//            }
//            parse ();
//            if (listener == null) listener = new Listener ();
//            document.addDocumentListener (listener);
//        }
//    }
//    
//    private static RequestProcessor         rp = new RequestProcessor ("org.netbeans.modules.parsing.impl.ParserManager");
//    private static RequestProcessor.Task    parsingTask;
//    
//    private static void parseLater () {
//        if (parsingTask != null) {
//            parsingTask.cancel ();
//        }
//        parsingTask = rp.post (new Runnable () {
//            public void run () {
//                parse ();
//            }
//        }, 1000);
//    }
//    
//    private static void parse () {
//        ((AbstractDocument) currentDocument).readLock ();
//        try {
//            Source source = null;
//            try {
//                source = new Source (
//                    currentDocument.getText (0, currentDocument.getLength ()),
//                    (String) currentDocument.getProperty ("mimeType"),
//                    currentDocument
//                );
//            } finally {
//                ((AbstractDocument) currentDocument).readUnlock ();
//            }
//            parse (source);
//        } catch (BadLocationException ex) {
//            ex.printStackTrace ();
//        }
//    }
//        
//    private static Map<Parser,Parser.Result> cache;
//    
//    public static void parse (
//        Source              source
//    ) {
//        cache = new HashMap<Parser,Parser.Result> ();
//        Iterator<? extends Parser> it = parserToParserListeners.keySet ().iterator ();
//        while (it.hasNext ()) {
//            Parser parser = it.next ();
//            Parser.Result result = parser.parse (source);
//            cache.put (parser, result);
//            if (result == null) continue;
//            List<PriorityParserListener> listeners = parserToParserListeners.get (parser);
//            if (listeners.isEmpty ()) continue;
//            Iterator<PriorityParserListener> it2 = listeners.iterator ();
//            while (it2.hasNext ()) {
//                Task parserListener = it2.next ();
//                parserListener.parsed (result, source);
//            }
//        }
//    }
//    
//    private static Lookup.Result<Parser>    parserLookupResult;
//    private static ParsersListener          parsersListener;
//    private static Set<Parser>              parsers;
//    
//    private static Lookup.Result getParsers (
//        String              mimeType, 
//        Class               cls,
//        ParsersListener     parsersListener
//    ) {
//        FileSystem fileSystem = Repository.getDefault ().getDefaultFileSystem ();
//        FileObject fileObject1 = fileSystem.findResource ("Editors");
//        DataFolder dataFolder1 = fileObject1 == null ? null : DataFolder.findFolder (fileObject1);
//        FileObject fileObject2 = fileSystem.findResource ("Editors/" + mimeType);
//        DataFolder dataFolder2 = fileObject2 == null ? null : DataFolder.findFolder (fileObject2);
//        Lookup lookup = dataFolder2 == null ? 
//            (Lookup) (dataFolder1 == null ? 
//                Lookup.EMPTY :
//                new FolderLookup (dataFolder1)) :
//            new ProxyLookup (
//                new FolderLookup (dataFolder1).getLookup (),
//                new FolderLookup (dataFolder2).getLookup ()
//            );
//
//        return parserLookupResult = Lookup.getDefault ().lookupResult (cls);
//    }
//
//    private static Lookup.Result<TaskFactory>   parserListenerFactoryLookupResult;
//    
//    private static Collection<? extends TaskFactory> getParserListeresFactories () {
//        if (parserListenerFactoryLookupResult == null) {
//            parserListenerFactoryLookupResult = Lookup.getDefault ().lookupResult (TaskFactory.class);
//        }
//        return parserListenerFactoryLookupResult.allInstances ();
//    }
//    
//    private static class ParsersListener implements ChangeListener, LookupListener {
//
//        public void stateChanged (
//            ChangeEvent     e
//        ) {
//            parse ();
//        }
//
//        public void resultChanged (
//            LookupEvent     ev
//        ) {
//            Set<Parser> remove = new HashSet<Parser> (parsers);
//            Set<Parser> newParsers = new HashSet<Parser> (parserLookupResult.allInstances ());
//            Iterator<Parser> it = newParsers.iterator ();
//            while (it.hasNext ()) {
//                Parser parser = it.next ();
//                if (!remove.remove (parser))
//                    parser.addChangeListener (parsersListener);
//            }
//            it = remove.iterator ();
//            while (it.hasNext ()) {
//                Parser parser = it.next ();
//                parser.removeChangeListener (parsersListener);
//            }
//            parsers = newParsers;
//            parse ();
//        }
//    }
//    
//    private static class Listener implements DocumentListener {
//
//        public void insertUpdate (
//            DocumentEvent   e
//        ) {
//            parseLater ();
//        }
//
//        public void removeUpdate (
//            DocumentEvent   e
//        ) {
//            parseLater ();
//        }
//
//        public void changedUpdate (
//            DocumentEvent   e
//        ) {
//            //parseUserTask ();
//        }
//    }
//    
//    private static final Comparator<PriorityParserListener> parserListenerComparator = new Comparator<PriorityParserListener> () {
//
//        public int compare (
//            PriorityParserListener o1, 
//            PriorityParserListener o2
//        ) {
//            return o1.getPriority () - o2.getPriority ();
//        }
//    };







