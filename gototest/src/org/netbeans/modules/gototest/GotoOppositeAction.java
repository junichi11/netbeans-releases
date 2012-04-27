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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.gototest;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.gototest.TestLocator;
import org.netbeans.spi.gototest.TestLocator.FileType;
import org.netbeans.spi.gototest.TestLocator.LocationListener;
import org.netbeans.spi.gototest.TestLocator.LocationResult;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.*;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;

/**
 * Action which jumps to the opposite test file given a current file.
 * This action delegates to specific framework implementations (JUnit, Ruby etc.)
 * which perform logic appropriate for the file type being opened.
 * <p>
 * Much of this is based on the original JUnit action by Marian Petras.
 * 
 * @author  Marian Petras
 * @author Tor Norbye
 */
public class GotoOppositeAction extends CallableSystemAction {
    private TestLocator cachedLocator;
    private FileObject cachedLocatorFo;
    private FileObject cachedFileTypeFo;
    private FileObject cachedLocationResultsFo;
    private FileType cachedFileType;
    private HashMap<LocationResult, String> locationResults = new HashMap<LocationResult, String>();
    private Semaphore lock;

    public GotoOppositeAction() {
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N

        // Not sure what the following is used for - a grep for trimmed-text
        // doesn't reveal any clients. Obsolete code perhaps?
        String trimmedName = NbBundle.getMessage(
                GotoOppositeAction.class,
                "LBL_Action_GoToTest_trimmed"); //NOI18N
        putValue("trimmed-text", trimmedName); //NOI18N
    }
    
    public String getName() {
        return NbBundle.getMessage(getClass(),
                                   getCurrentFileType() == FileType.TEST
                                        ? "LBL_Action_GoToSource" //NOI18N
                                        : "LBL_Action_GoToTest"); //NOI18N
    }
    
    @Override
    public boolean isEnabled() {
        assert EventQueue.isDispatchThread();
        EditorCookie ec =  Utilities.actionsGlobalContext().lookup(EditorCookie.class);
        if (ec == null || ec.getDocument() == null) {
            return false;
        }
        return getCurrentFileType() != FileType.NEITHER;
    }

    public HelpCtx getHelpCtx() {
        // TODO - delegate to file locators!
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void initialize () {
	super.initialize ();
        putProperty(Action.SHORT_DESCRIPTION,
                    NbBundle.getMessage(getClass(),
                                        "HINT_Action_GoToTest"));       //NOI18N
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    @Override
    public void performAction() {
        int caretOffsetHolder[] = { -1 };
        final FileObject fo = getApplicableFileObject(caretOffsetHolder);
        final int caretOffset = caretOffsetHolder[0];

        if (fo != null) {
            RequestProcessor RP = new RequestProcessor(GotoOppositeAction.class.getName());

            RP.post(new Runnable() {

                @Override
                public void run() {
                    populateLocationResults(fo, caretOffset);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (locationResults.size() == 1) {
                                handleResult(locationResults.keySet().iterator().next());
                            } else if (locationResults.size() > 1) {
                                showPopup(fo);
                            }
                        }
                    });
                }
            });
        }
    }

    private void populateLocationResults(FileObject fo, int caretOffset) {
        if (cachedLocationResultsFo == fo) {
            return;
        }
        cachedLocationResultsFo = fo;
        locationResults.clear();

        Collection<? extends TestLocator> locators = Lookup.getDefault().lookupAll(TestLocator.class);

        int permits = 0;
        for (TestLocator locator : locators) {
            if (locator.appliesTo(fo)) {
                permits++;
            }
        }

        lock = new Semaphore(permits);
        try {
            lock.acquire(permits);
        } catch (InterruptedException e) {
        }

        for (TestLocator locator : locators) {
            if (locator.appliesTo(fo)) {
                doPopulateLocationResults(fo, caretOffset, locator);
            }
        }
        try {
            lock.acquire(permits);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void doPopulateLocationResults(FileObject fo, int caretOffset, TestLocator locator) {
        if (locator != null) {
            if (locator.appliesTo(fo)) {
                if (locator.asynchronous()) {
                    locator.findOpposite(fo, caretOffset, new LocationListener() {

                        @Override
                        public void foundLocation(FileObject fo, LocationResult location) {
                            if (location != null) {
                                locationResults.put(location, location.getFileObject().getName());
                            }
                            lock.release();
                        }
                    });
                } else {
                    LocationResult opposite = locator.findOpposite(fo, caretOffset);

                    if (opposite != null) {
                        locationResults.put(opposite, opposite.getFileObject().getName());
                    }
                    lock.release();
                }
            }
        }
    }

    @NbBundle.Messages("LBL_PickExpression=Go to Test")
    private void showPopup(FileObject fo) {
        JTextComponent pane;
        Point l = new Point(-1, -1);

        DataObject dobj = null;
        try {
            dobj = DataObject.find(fo);
            EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);
            if (ec != null) {
                pane = NbDocument.findRecentEditorPane(ec);
                Rectangle pos = pane.modelToView(pane.getCaretPosition());
                l = new Point(pos.x + pos.width, pos.y + pos.height);
                SwingUtilities.convertPointToScreen(l, pane);

                String label = Bundle.LBL_PickExpression();
                PopupUtil.showPopup(new OppositeCandidateChooser(this, label, locationResults), label, l.x, l.y, true, -1);
            }
        } catch (DataObjectNotFoundException ex) {
            Logger.getLogger(GotoOppositeAction.class.getName()).log(Level.WARNING, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(GotoOppositeAction.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public void handleResult(LocationResult opposite) {
        FileObject fileObject = opposite.getFileObject();
        if (fileObject != null) {
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                Logger.getLogger(GotoOppositeAction.class.getName()).log(Level.WARNING, null, ex);
            }
            NbDocument.openDocument(dobj, opposite.getOffset(), Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS);
        } else if (opposite.getErrorMessage() != null) {
            String msg = opposite.getErrorMessage();
            NotifyDescriptor descr = new NotifyDescriptor.Message(msg, 
                    NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(descr);
        }
    }
    
    private TestLocator getLocatorFor(FileObject fo) {
        if (fo == cachedLocatorFo) {
            return cachedLocator;
        }
        cachedLocatorFo = fo;
        cachedLocator = null;

        Collection<? extends TestLocator> locators = Lookup.getDefault().lookupAll(TestLocator.class);
        for (TestLocator locator : locators) {
            if (locator.appliesTo(fo)) {
                cachedLocator = locator;
                
                break;
            }
        }
        
        return cachedLocator;
    }
    
    private FileType getFileType(FileObject fo) {
        if (fo == cachedFileTypeFo) {
            return cachedFileType;
        }
        
        cachedFileTypeFo = fo;
        cachedFileType = FileType.NEITHER;
        
        TestLocator locator = getLocatorFor(fo);
        if (locator != null) {
            cachedFileType = locator.getFileType(fo);
        }
        
        return cachedFileType;
    }
    
    private FileType getCurrentFileType() {
        FileObject fo = getApplicableFileObject(new int[1]);
        
        return (fo != null) ? getFileType(fo) : FileType.NEITHER;
    }

    private FileObject getApplicableFileObject(int[] caretPosHolder) {
        if (!EventQueue.isDispatchThread()) {
            // Unsafe to ask for an editor pane from a random thread.
            // E.g. org.netbeans.lib.uihandler.LogRecords.write asking for getName().
            Collection<? extends DataObject> dobs = Utilities.actionsGlobalContext().lookupAll(DataObject.class);
            return dobs.size() == 1 ? dobs.iterator().next().getPrimaryFile() : null;
        }

        // TODO: Use the new editor library to compute this:
        // JTextComponent pane = EditorRegistry.lastFocusedComponent();

        TopComponent comp = TopComponent.getRegistry().getActivated();
        if(comp == null) {
            return null;
        }
        Node[] nodes = comp.getActivatedNodes();
        if (nodes != null && nodes.length == 1) {
            if (comp instanceof CloneableEditorSupport.Pane) { //OK. We have an editor
                EditorCookie ec = nodes[0].getLookup().lookup(EditorCookie.class);
                if (ec != null) {
                    JEditorPane editorPane = NbDocument.findRecentEditorPane(ec);
                    if (editorPane != null) {
                        if (editorPane.getCaret() != null) {
                                caretPosHolder[0] = editorPane.getCaret().getDot();
                        }
                        Document document = editorPane.getDocument();
                        Object sdp = document.getProperty(Document.StreamDescriptionProperty);
                        if (sdp instanceof FileObject) {
                                return (FileObject)sdp;
                        } else if (sdp instanceof DataObject) {
                                return ((DataObject) sdp).getPrimaryFile();
                        }
                    }
                }
            }else {
                DataObject dataObj = nodes[0].getLookup().lookup(DataObject.class);
                if (dataObj != null) {
                    return dataObj.getPrimaryFile();
                }
            }
        }
        
        return null;
    }
}
