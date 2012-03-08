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

package org.openide.text;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.netbeans.junit.NbTestCase;
import org.openide.awt.UndoRedo;
import org.openide.text.CloneableEditorSupport.Env;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 * Deadlock of a thread simulating reloading of document and another thread trying to close the file.
 * 
 * @author Miloslav Metelka
 */
public class Deadlock207571Test extends NbTestCase
implements CloneableEditorSupport.Env {
    static {
        System.setProperty("org.openide.windows.DummyWindowManager.VISIBLE", "false");
    }
    /** the support to work with */
    private transient CES support;

    // Env variables
    private transient String content = "";
    private transient boolean valid = true;
    private transient boolean modified = false;
    /** if not null contains message why this document cannot be modified */
    private transient String cannotBeModified;
    private transient Date date = new Date ();
    private transient final PropertyChangeSupport pcl;
    private transient VetoableChangeListener vetoL;
    
    private transient volatile boolean inReloadBeforeSupportLock;
    private transient volatile boolean closing;
    
    private static Deadlock207571Test RUNNING;
    
    public Deadlock207571Test(String s) {
        super(s);
        pcl = new PropertyChangeSupport(this);
    }
    
    protected void setUp () {
        support = new CES (this, Lookup.EMPTY);
        RUNNING = this;
    }
    
    protected boolean runInEQ() {
        return false;
    }

    @Override
    protected int timeOut() {
        return 15000;
    }
    
    private Object writeReplace () {
        return new Replace ();
    }
    
    public void testCloseDocumentWhenCheckReload() throws Exception {
        final StyledDocument doc = support.openDocument();
        // Create position ref so that it gets processed by support.close()
        PositionRef posRef = support.createPositionRef(0, Position.Bias.Forward);
        // Reload first does runAtomic() and inside it it syncs on support.getLock()
        Runnable reloadSimulationRunnable = new Runnable() {
            private boolean inRunAtomic;

            @Override
            public void run() {
                if (!inRunAtomic) {
                    inRunAtomic = true;
                    NbDocument.runAtomic(doc, this);
                    return;
                }
                inReloadBeforeSupportLock = true;
                while (!closing) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                synchronized (support.getLock()) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                inReloadBeforeSupportLock = true;
                try {
                    pcl.firePropertyChange(Env.PROP_TIME, null, null);
                } finally {
                    inReloadBeforeSupportLock = false;
                }
            }
        };
        Task reloadSimulationTask = RequestProcessor.getDefault().post(reloadSimulationRunnable);

        while (!inReloadBeforeSupportLock) {
            Thread.sleep(1);
        }
        closing = true;
        support.close();
        
        reloadSimulationTask.waitFinished(1000);        
    }
    
    public void testUndoThrowsException() throws Exception {
        Document doc = support.openDocument();
        doc.insertString(0, "a", null);
        UndoRedo.Manager ur = support.getUndoRedo();
        MyEdit edit = new MyEdit();
        ur.undoableEditHappened(new UndoableEditEvent(this, edit));
        ur.canUndo();
        assertFalse("Expecting not undone", edit.undone);
        ur.undo();
        assertTrue("Expecting undone", edit.undone);
        ur.redo();
        assertFalse("Expecting redone", edit.undone);

        edit.undoFail = true;
        assertEquals(0, edit.undoFailedCount);
        try {
            ur.undo();
            fail("Exception expected");
        } catch (CannotUndoException ex) {
            // Expected
        }
        assertEquals(1, edit.undoFailedCount);
        try {
            ur.undo();
            fail("Exception expected");
        } catch (CannotUndoException ex) {
            // Expected
        }
        assertEquals(2, edit.undoFailedCount);
    }
    
    //
    // Implementation of the CloneableEditorSupport.Env
    //
    
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        pcl.addPropertyChangeListener(l);
    }    
    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        pcl.removePropertyChangeListener(l);
    }
    
    public synchronized void addVetoableChangeListener(VetoableChangeListener l) {
        assertNull ("This is the first veto listener", vetoL);
        vetoL = l;
    }
    public void removeVetoableChangeListener(VetoableChangeListener l) {
        assertEquals ("Removing the right veto one", vetoL, l);
        vetoL = null;
    }
    
    public CloneableOpenSupport findCloneableOpenSupport() {
        return RUNNING.support;
    }
    
    public String getMimeType() {
        return "text/plain";
    }
    
    public Date getTime() {
        return date;
    }
    
    public InputStream inputStream() throws IOException {
        return new ByteArrayInputStream (content.getBytes ());
    }
    public OutputStream outputStream() throws IOException {
        class ContentStream extends ByteArrayOutputStream {
            public void close () throws IOException {
                super.close ();
                content = new String (toByteArray ());
            }
        }
        
        return new ContentStream ();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean isModified() {
        return modified;
    }

    public void markModified() throws IOException {
        if (cannotBeModified != null) {
            final String notify = cannotBeModified;
            IOException e = new IOException () {
                public String getLocalizedMessage () {
                    return notify;
                }
            };
            Exceptions.attachLocalizedMessage(e, cannotBeModified);
            throw e;
        }
        
        modified = true;
    }
    
    public void unmarkModified() {
        modified = false;
    }
    
    /** Implementation of the CES */
    private static final class CES extends CloneableEditorSupport {
        public CES (Env env, Lookup l) {
            super (env, l);
        }
        
        @Override
        protected EditorKit createEditorKit () {
            // Important to use NbLikeEditorKit since otherwise FilterDocument
            // would be created with improper runAtomic()
            return new MyKit ();
        }
        public CloneableTopComponent.Ref getRef () {
            return allEditors;
        }
        
        protected String messageName() {
            return "Name";
        }
        
        protected String messageOpened() {
            return "Opened";
        }
        
        protected String messageOpening() {
            return "Opening";
        }
        
        protected String messageSave() {
            return "Save";
        }
        
        protected String messageToolTip() {
            return "ToolTip";
        }
        
    }

    private static final class Replace implements Serializable {
        public Object readResolve () {
            return RUNNING;
        }
    }

    private static final class MyEdit extends AbstractUndoableEdit {
        
        boolean undone;
        
        boolean undoFail;
        
        boolean redoFail;
        
        int undoFailedCount;
        
        public void undo() throws CannotUndoException {
            assert (!undone) : "Already undone";
            if (undoFail) {
                undoFailedCount++;
                throw new CannotUndoException();
            }
            undone = true;
        }
        
        public void redo() throws CannotRedoException {
            assert (undone) : "Already redone";
            if (redoFail) {
                throw new CannotRedoException();
            }
            undone = false;
        }

    } // end of UndoableEdit
    
    private static final class MyKit extends NbLikeEditorKit {

        @Override
        public Document createDefaultDocument() {
            return new Doc() {

                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    super.insertString(offs, str, a);
                }
                
            };
        }
        
        
    }
}
