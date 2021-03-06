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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.csl.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 * A list of edits to be made to a document.  This should probably be combined with the many
 * other similar abstractions in other classes; ModificationResult, Diff, etc.
 * 
 * @todo Take out the offsetOrdinal number, and manage that on the edit list side
 *  (order of entry for duplicates should insert a new ordinal)
 * @todo Make formatting more explicit; allow to add a "format" region edit. These must
 *   be sorted such that they don't overlap after edits and are all applied last.
 * 
 * @author Tor Norbye
 */
public class EditList {
    private static final Logger LOG = Logger.getLogger(EditList.class.getName());
    
    private BaseDocument doc;
    private List<Edit> edits;
    private boolean formatAll;
    private List<DelegatedPosition> positions = new ArrayList<DelegatedPosition>();;
    
    public EditList(BaseDocument doc) {
        this.doc = doc;
        edits = new ArrayList<Edit>();
    }
  
    @Override
    public String toString() {
        return "EditList(" + edits + ")"; //NOI18N
    }

    /**
     * Create a position using an original offset that after applying the fixes
     * will return the corresponding offset in the edited document
     */
    public Position createPosition(int offset) {
        return createPosition(offset, Position.Bias.Forward);
    }

    public Position createPosition(int offset, Position.Bias bias) {
        DelegatedPosition pos = new DelegatedPosition(offset, bias);
        positions.add(pos);

        return pos;
    }

    public EditList replace(int offset, int removeLen, String insertText, boolean format, int offsetOrdinal) {
        edits.add(new Edit(offset, removeLen, insertText, format, offsetOrdinal));
        
        return this;
    }
    
    public void applyToDocument(BaseDocument otherDoc/*, boolean narrow*/) {
        EditList newList = new EditList(otherDoc);
        newList.formatAll = formatAll;
        /*
        if (narrow) {
            OffsetRange range = getRange();
            int start = range.getStart();
            int lineno = NbDocument.findLineNumber((StyledDocument) otherDoc,start);
            lineno = Math.max(0, lineno-3);
            start = NbDocument.findLineOffset((StyledDocument) otherDoc,lineno);

            List newEdits = new ArrayList<Edit>(edits.size());
            newList.edits = newEdits;
            for (Edit edit : edits) {
                newEdits.add(new Edit(edit.offset-start, edit.removeLen, edit.insertText, edit.format, edit.offsetOrdinal));
            }
        } else {
         */
            newList.edits = edits;
        //}
        newList.apply();
    }

    public void setFormatAll(boolean formatAll) {
        this.formatAll = formatAll;
    }
    
    /** Apply the given list of edits in the current document. If positionOffset is a position
     * within one of the regions, return a document Position that corresponds to it.
     */
    public void apply() {
        if (edits.size() == 0) {
            return;
        }

        Collections.sort(edits);
        Collections.reverse(edits);

        final Position [] lastPos = new Position [] { null };
        
        // Apply edits in reverse order (to keep offsets accurate)
        final Reformat r = Reformat.get(doc);
        r.lock();
        try {
            doc.runAtomic(() -> {
                for (Edit edit : edits) {
                    final Edit fEdit = edit;
                    final int [] fEnd = new int [] { -1 };
                    try {
                        if (lastPos[0] == null) {
                            lastPos[0] = doc.createPosition(edits.get(0).offset, Position.Bias.Forward);
                        }

                        if (fEdit.removeLen > 0) {
                            doc.remove(fEdit.offset, fEdit.removeLen);
                            LOG.log(
                                    Level.FINE,
                                    "Remove text: <{0}, {1}>",   //NOI18N
                                    new Object[]{
                                        fEdit.offset,
                                        fEdit.offset + fEdit.removeLen
                                    });
                        }
                        if (fEdit.getInsertText() != null) {
                            doc.insertString(fEdit.offset, fEdit.insertText, null);
                            LOG.log(
                                    Level.FINE,
                                    "Insert text: offset={0}, text=''{1}''\n",   //NOI18N
                                    new Object[]{
                                        fEdit.offset,
                                        fEdit.insertText
                                    });

                            fEnd[0] = fEdit.offset + fEdit.insertText.length();
                            for (int i = 0; i < positions.size(); i++) {
                                DelegatedPosition pos = positions.get(i);
                                int positionOffset = pos.originalOffset;
                                if (fEdit.getOffset() <= positionOffset && fEnd[0] >= positionOffset) {
                                    pos.delegate = doc.createPosition(positionOffset, pos.bias); // Position of the comment
                                }
                            }
                        }
                    } catch (BadLocationException ble) {
                        Exceptions.printStackTrace(ble);
                    }

                    if (edit.format && edit.offset <= fEnd[0]) {
                        try {
                            r.reformat(fEdit.offset, fEnd[0]);
                            LOG.log(
                                    Level.FINE,
                                    "Formatting: <{0}, {1}>",    //NOI18N
                                    new Object[]{
                                        fEdit.offset,
                                        fEnd[0]
                                    });
                        } catch (BadLocationException ble) {
                            Exceptions.printStackTrace(ble);
                        }
                    }
                }
                if (formatAll) {
                    final int firstOffset = edits.get(edits.size() - 1).offset;
                    final int lastOffset = lastPos[0].getOffset();
                    if (firstOffset <= lastOffset) {
                        try {
                            r.reformat(firstOffset, lastOffset);
                            LOG.log(
                                    Level.FINE,
                                    "Formatting all: <{0}, {1}>",    //NOI18N
                                    new Object[]{
                                        firstOffset,
                                        lastOffset
                                    });
                        } catch (BadLocationException ble) {
                            Exceptions.printStackTrace(ble);
                        }
                    }
                }
            });
        } finally {
            r.unlock();
        }
    }
    
    public OffsetRange getRange() {
        int minOffset = edits.get(0).offset;
        int maxOffset = minOffset;
        for (Edit edit : edits) {
            if (edit.offset < minOffset) {
                minOffset = edit.offset;
            }
            if (edit.offset > maxOffset) {
                maxOffset = edit.offset;
            }
        }
        
        return new OffsetRange(minOffset, maxOffset);
    }
    
    public int firstLine(BaseDocument doc) {
        OffsetRange range = getRange();
        
        return NbDocument.findLineNumber((StyledDocument)doc, range.getStart());
    }
    
    /**
     * A class which records a set of edits to a document, and then can apply these edits.
     * The edit regions are sorted in reverse order and applied from back to front such that
     * all the document offsets are correct at the time they are used.
     * 
     * @author Tor Norbye
     */
    private static class Edit implements Comparable<Edit> {

        int offset;
        int removeLen;
        String insertText;
        boolean format;
        int offsetOrdinal;

        private Edit(int offset, int removeLen, String insertText, boolean format) {
            super();
            this.offset = offset;
            this.removeLen = removeLen;
            this.insertText = insertText;
            this.format = format;
        }

        /** The offsetOrdinal is used to choose among multiple edits at the same offset */
        private Edit(int offset, int removeLen, String insertText, boolean format, int offsetOrdinal) {
            this(offset, removeLen, insertText, format);
            this.offsetOrdinal = offsetOrdinal;
        }

        public int compareTo(Edit other) {
            if (offset == other.offset) {
                return other.offsetOrdinal - offsetOrdinal;
            }
            return offset - other.offset;
        }

        public int getOffset() {
            return offset;
        }

        public int getRemoveLen() {
            return removeLen;
        }

        public String getInsertText() {
            return insertText;
        }

        @Override
        public String toString() {
            return "Edit(pos=" + offset + ",delete=" + removeLen + ",insert="+insertText+")"; //NOI18N
        }
    }
    
    private class DelegatedPosition implements Position {
        private int originalOffset;
        private Position delegate;
        private Position.Bias bias;

        private DelegatedPosition(int offset, Position.Bias bias) {
            this.originalOffset = offset;
            this.bias = bias;
        }

        public int getOffset() {
            if (delegate != null) {
                return delegate.getOffset();
            }

            return -1;
        }
    }
}
