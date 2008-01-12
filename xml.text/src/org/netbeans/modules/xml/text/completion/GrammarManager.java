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

package org.netbeans.modules.xml.text.completion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.api.xml.parsers.DocumentInputSource;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.xml.api.model.ExtendedGrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarEnvironment;
import org.netbeans.modules.xml.api.model.GrammarQuery;
import org.netbeans.modules.xml.api.model.GrammarQueryManager;
import org.netbeans.modules.xml.text.syntax.SyntaxElement;
import org.netbeans.modules.xml.text.syntax.XMLSyntaxSupport;
import org.netbeans.modules.xml.text.syntax.dom.SyntaxNode;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.WeakListeners;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;

/**
 * Manages grammar to text editor association. It is able to
 * dynamically switch among providers.
 *
 * @author  Petr Kuzel, mfukala@netbeans.org
 */
class GrammarManager extends FileChangeAdapter implements DocumentListener {

    private static final String FILE_PROTOCOL_URI_PREFIX = "file:/"; //NOI18N
    
    private ArrayList<FileObject> externalDTDs = new ArrayList();
    
    // current cache state
    private int state = INVALID;
    
    static final int VALID = 1;
    static final int INVALID = 3;
    
    // cache entry
    private GrammarQuery grammar;
    
    // grammar is provided for this document
    private final XMLSyntaxSupport syntax;
    private final Document doc;
    
    // guarded positions pairs
    private Position[] guarded;
    
    // maximal gurded offset
    private Position maxGuarded;
    
    private int environmentElementsCount = -1;
    
    /**
     * Create new manager.
     */
    public GrammarManager(Document doc, XMLSyntaxSupport syntax) {
        this.doc = doc;
        this.syntax = syntax;
    }
    
    /**
     * Return any suitable grammar that you can get
     * till expires given timeout.
     */
    public synchronized GrammarQuery getGrammar() {
        
        switch (state) {
            case VALID:
                return grammar;
                
            case INVALID:
                loadGrammar();
                return grammar;
                
            default:
                throw new IllegalStateException();
        }
    }
    
    
    /**
     * Notification from invalidator thread, the grammar need to be reloaded.
     */
    public synchronized void invalidateGrammar() {
        
        // make current loader a zombie
        if (state == VALID) {
            String msg = NbBundle.getMessage(GrammarManager.class, "MSG_loading_cancel");
            StatusDisplayer.getDefault().setStatusText(msg);
        }
        
        doc.removeDocumentListener(this);
        
        //remove FileChangeListeners from the external DTD files
        for(FileObject fo : externalDTDs) {
//            System.out.println("[GrammarManager] removed FileObjectListener from " + fo.getPath());
            fo.removeFileChangeListener(this);
        }
        externalDTDs.clear();
        
        guarded = new Position[0];
        state = INVALID;
    }
    
    public void fileChanged(FileEvent fe) {
        //one of the external DTD files has changed => invalidate grammar
        invalidateGrammar();
    }
    
    public void fileDeleted(FileEvent fe) {
        invalidateGrammar();
    }
    
    public void insertUpdate(DocumentEvent e) {
        //test whether there is a change in the grammar environment - e.g. is a grammar
        //declaration was added and so.
        checkDocumentEnvironment(e);
        
        if (isGuarded(e.getOffset(), e.getLength())) {
            invalidateGrammar();
        }
    }
    
    public void removeUpdate(DocumentEvent e) {
        //test whether there is a change in the grammar environment - e.g. is a grammar
        //declaration was removed and so.
        checkDocumentEnvironment(e);
        
        if (isGuarded(e.getOffset(), e.getLength())) {
            invalidateGrammar();
        }
    }
    
    private void checkDocumentEnvironment(DocumentEvent e) {
        long current = System.currentTimeMillis();
        
        try {
            LinkedList ll = getEnvironmentElements();
            if(ll.size() != environmentElementsCount) {
                invalidateGrammar();
                environmentElementsCount = ll.size();
            }
        }catch(BadLocationException ble) {}
        
    }
    
    public void changedUpdate(DocumentEvent e) {
        // not interested
    }
    
    private boolean isGuarded(int offset, int length) {
        
        // optimalization for common case
        if ((maxGuarded != null) && (offset > maxGuarded.getOffset())) {
            return false;
        }
        
        if (guarded == null) {
            return false;
        }
        
        // slow loop matchibng range overlaps
        for (int i = 0; i<guarded.length; i+=2) {
            int start = guarded[i].getOffset();
            int end = guarded[i+1].getOffset();
            if (start < offset && offset < end) {
                return true;
            }
            int changeEnd = offset + length;
            if (offset < start && start < changeEnd) {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * Nofification from grammar loader thread, new valid grammar.
     * @param grammar grammar or <code>null</code> if cannot load.
     */
    private synchronized void grammarLoaded(GrammarQuery grammar) {        
        //Issue: http://www.netbeans.org/issues/show_bug.cgi?id=108610
        //do not show error message since there can be many more completion providers.
        String status = (grammar != null) ? NbBundle.getMessage(GrammarManager.class, "MSG_loading_done")
        : null;
        
        this.grammar = grammar == null ? EmptyQuery.INSTANCE : grammar;
        state = VALID;
        
        StatusDisplayer.getDefault().setStatusText(status);
    }
    
    
    /**
     * Async grammar fetching
     */
    private void loadGrammar() {
        
        
        GrammarQuery loaded = null;
        try {
            
            String status = NbBundle.getMessage(GrammarManager.class, "MSG_loading");
            StatusDisplayer.getDefault().setStatusText(status);
            
            // prepare grammar environment
            
            try {
                
                LinkedList ctx = getEnvironmentElements();
                InputSource inputSource = new DocumentInputSource(doc);
                FileObject fileObject = null;
                Object obj = doc.getProperty(Document.StreamDescriptionProperty);
                if (obj instanceof DataObject) {
                    DataObject dobj = (DataObject) obj;
                    fileObject = dobj.getPrimaryFile();
                }
                GrammarEnvironment env = new GrammarEnvironment(Collections.enumeration(ctx), inputSource, fileObject);
                
                // lookup for grammar
                
                GrammarQueryManager g = GrammarQueryManager.getDefault();
                Enumeration en = g.enabled(env);
                if (en == null) return;
                
                // set guarded regions
                
                List positions = new ArrayList(10);
                int max = 0;
                
                while (en.hasMoreElements()) {
                    Node next = (Node) en.nextElement();
                    if (next instanceof SyntaxNode) {
                        SyntaxNode node = (SyntaxNode) next;
                        int start = node.getElementOffset();
                        int end = start + node.getElementLength();
                        if (end > max) max = end;
                        Position startPosition =
                                NbDocument.createPosition(doc, start, Position.Bias.Forward);
                        positions.add(startPosition);
                        Position endPosition =
                                NbDocument.createPosition(doc, end, Position.Bias.Backward);
                        positions.add(endPosition);
                    }
                }
                
                guarded = (Position[]) positions.toArray(new Position[positions.size()]);
                maxGuarded = NbDocument.createPosition(doc, max, Position.Bias.Backward);
                
                
                // retrieve the grammar and start invalidation listener
                
                loaded = g.getGrammar(env);
                
                if(loaded instanceof ExtendedGrammarQuery) {
                    //attach listeners to external files and if any of them changes then reload this grammar
                    for(String resolvedEntity : (List<String>)((ExtendedGrammarQuery)loaded).getResolvedEntities()) {
                        //filter non-files resolved entities
                        if(!resolvedEntity.startsWith(FILE_PROTOCOL_URI_PREFIX)) continue;
                        
                        DataObject docDo = NbEditorUtilities.getDataObject(doc);
                        if(docDo != null) {
                            FileObject docFo = docDo.getPrimaryFile();
                            if(docFo != null) {
                                try {
                                    FileSystem fs = docFo.getFileSystem();
                                    FileObject fo = fs.findResource(resolvedEntity.substring(FILE_PROTOCOL_URI_PREFIX.length())); //NOI18N
                                    if(fo != null) {
                                        externalDTDs.add(fo);
                                        //add a week listener to the fileobject - detach when document is being disposed
                                        fo.addFileChangeListener((FileChangeListener)WeakListeners.create(FileChangeListener.class, this, doc));
//                                        System.out.println("[GrammarManager] added FileObjectListener to " + fo.getPath());
                                    }
                                }catch(IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                
            } catch (BadLocationException ex) {
                loaded = null;
            }
            
        } finally {
            
            doc.addDocumentListener(GrammarManager.this);
            
            grammarLoaded(loaded);
        }
    }
    
    private LinkedList getEnvironmentElements() throws BadLocationException {
        LinkedList ctx = new LinkedList();
        SyntaxElement first = syntax.getElementChain(1);
        while (true) {
            if (first == null) break;
            if (first instanceof SyntaxNode) {
                SyntaxNode node = (SyntaxNode) first;
                ctx.add(node);
                if (node.ELEMENT_NODE == node.getNodeType()) {
                    break;
                }
            }
            first = first.getNext();
        }
        return ctx;
    }
}

