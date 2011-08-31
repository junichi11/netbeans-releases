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
package org.netbeans.modules.editor.lib2.document;

import org.netbeans.lib.editor.util.ArrayUtilities;

/**
 * Container for {@link Mark} for the document.
 * The structure is array with a gap together with an offset gap
 * for the abstract storage of the document text.
 * <br/>
 * {@link #isBackwardBias()} determines whether regular (or backward-bias) marks
 * are handled by the mark vector.
 * <br/>
 * Certain methods require an explicit outer synchronization done by {@link EditorDocumentContent}.
 *
 * @author Miloslav Metelka
 * @since 1.46
 */
final class MarkVector {
    
    /**
     * Default size of the offset gap. Since 
     */
    private static final int INITIAL_OFFSET_GAP_SIZE = (Integer.MAX_VALUE >> 2);

    /** Sorted array of marks. Contains a gap to speed up adding of new marks. */
    private Mark[] markArray;
    
    /** Whether this vector manages marks with backward bias. */
    private final boolean backwardBiasMarks;
    
    /** Starting index of the gap in the markArray. */
    private int gapStart;

    /** Length of the gap in the markArray. */
    private int gapLength;

    /** Starting offset of the offset gap */
    private int offsetGapStart;

    /** Length of the offset gap */
    private int offsetGapLength;
    
    private final Object lock;

    /**
     * Number of mark instances that are still in the markArray but 
     * that are no longer valid (their position objects were disposed).
     */
    private int disposedMarkCount;

    /**
     * Position at offset == 0 that is first in array and never moves.
     * <br/>
     * Hard reference to it here ensures that its mark will never be GCed.
     */
    private static final EditorPosition zeroPos = new EditorPosition();
    private static final Mark zeroMark = new Mark(null, 0, zeroPos);
    
    MarkVector(Object lock, boolean backwardBias) {
        this.lock = lock;
        this.backwardBiasMarks = backwardBias;
        // Create mark array with two free slots
        markArray = new Mark[gapLength = 2]; // gapStart == 0
        offsetGapStart = 1; // Above offset == 0
        offsetGapLength = INITIAL_OFFSET_GAP_SIZE;
        
    }
    
    /**
     * Whether this mark vector serves regular marks or backward-bias marks.
     * 
     * @return true for BB marks or false otherwise.
     */
    boolean isBackwardBiasMarks() {
        return backwardBiasMarks;
    }

    /**
     * Create position (or get an existing one) for the given offset.
     * Needs explicit synchronization.
     *
     * @param offset 
     */
    public EditorPosition position(int offset) {
        if (offset == 0) { // Both regular and backward-bias marks use zeroPos
            return zeroPos;
        }
        EditorPosition pos;
        // Find existing position by binary-search or create a new position
        // For better consistency (see EDC javadoc) the last mark
        // (in a series of marks with same offset) is returned.
        int low = 0;
        int markCount = markCount();
        int high = markCount - 1;
        int rawOffset = rawOffset(offset);
        if (!backwardBiasMarks) { // Regular marks
            while (low <= high) {
                int mid = (low + high) >>> 1;
                // Search for index that follows marks with "rawOffset"
                if (getMark(mid).rawOffset() <= rawOffset) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            // "low" points to mark insertion point and high == low - 1
            Mark mark;
            if (high >= 0 && (mark = getMark(high)).rawOffset() == rawOffset && (pos = mark.get()) != null) {
                return pos; // Reuse
            }

        } else { // Backward bias marks => return first mark with the index
            while (low <= high) {
                int mid = (low + high) >>> 1;
                // Search for index that points to first mark with "rawOffset"
                if (getMark(mid).rawOffset() < rawOffset) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            // "low" >= 0 points to mark with rawOffset or a higher offset
            Mark mark;
            if (low < markCount && (mark = getMark(low)).rawOffset() == rawOffset && (pos = mark.get()) != null) {
                return pos; // Reuse
            }
        }
        return createPosition(low, offset);
    }
    
    private EditorPosition createPosition(int index, int offset) {
        if (index != gapStart) {
            moveGap(index);
        }
        if (gapLength == 0) {
            // Although gapLength == 0 so single arraycopy() could be used
            // the gap should be present at gapStart index for insertion of the new mark.
            reallocate(Math.max(4, markArray.length >>> 1));
        }
        // offset is now raw-offset
        return newPosition(offset); // Create position; updates gapStart and gapLength
    }

    /**
     * Create Mark object (together with EditorPosition) and assign it to gapStart index
     * in the mark array (decrement gapLength).
     *
     * @param offset &gt;=0
     * @return EditorPosition instance associated with the mark.
     */
    private EditorPosition newPosition(int offset) {
        if (offset >= offsetGapStart) {
            offset += offsetGapLength;
        }
        EditorPosition pos = new EditorPosition();
        markArray[gapStart++] = new Mark(this, offset, pos);
        gapLength--;
        return pos;
    }
    
    /**
     * Update mark vector by insertion. Needs explicit synchronization.
     *
     * @param offset offset >= 0
     * @param length length > 0 
     * @param markUpdates possible mark updates in case this is redo of a removal.
     * @return position at offset or null (if offsetPos was false).
     */
    void insertUpdate(int offset, int length, MarkUpdate[] markUpdates) {
        // According to AbstractDocument's implementation positions at offset=0 should stay
        // so treat them like backward-bias marks
        boolean backwardBiasHandling = isBackwardBiasMarks() || offset == 0;
        int index; // index in mark array to which the offset gap gets moved
        if (!backwardBiasHandling) { // Regular marks
            index = findFirstIndex(offset);
            if (offsetGapStart != offset) {
                moveOffsetGap(offset, index);
            }
            // Shift the offset gap right to the end of insertion.
            // This is necessary since marks from markUpdates assume it and so they update to below-gap offsets.
            offsetGapStart += length;

        } else { // Backward-bias marks or marks at offset == 0
            int newGapOffset = offset + 1;
            // Find first index with offset+1 or higher so offsetGapStart will be at offset+1
            index = findFirstIndex(newGapOffset);
            if (offsetGapStart != newGapOffset) {
                moveOffsetGap(newGapOffset, index);
            }
            // Shift the offset gap right to the end of insertion + 1.
            // This corresponds to how the removal gets handled.
            // This is necessary since marks from markUpdates assume it and so they update to below-gap offsets.
            offsetGapStart += length;

            if (markUpdates != null) { // Possibly restore marks' original offsets
                int activeMarkUpdatesCount = 0;
                for (int i = 0; i < markUpdates.length; i++) {
                    MarkUpdate update = markUpdates[i];
                    if (update.mark.isActive()) {
                        update.restoreRawOffset();
                        markUpdates[activeMarkUpdatesCount++] = update; // Filter markUpdates to valid ones
                    }
                }
            }
        }
        offsetGapLength -= length; // Shrink the offset gap to process insertion

        // If markUpdates != null (retain original offsets) then marks could become "unsorted":
        // 1. Position pos = doc.createPosition(2);
        // 2. doc.remove(2, 1); // ensures "marking" of pos to return to offset==2
        // 3. doc.insert(context, 2, "x"); // pos moves to offset==3
        // 4. Position pos2 = doc.createPosition(2); // creates new position at offset==2
        // 5. undoManager.undo(); // both pos and pos2 at offset==2; pos2 before pos
        // 6. undoManager.undo(); // pos2 at offset==3; pos2 returned to offset==2 but pos2 before pos!
        //
        // For remove(offset, n) the markUpdates content is created by taking a consecutive array of marks
        // with <offset, offset+n) and resetting their offset to "offset" and remembering their
        // original offsets in MarkUpdate[].
        // Since these marks share the same offset after the removal they should retain
        // their order with no "intruder" mark inserted in between them.
        // When doing undo of remove(offset, n) additional marks with offset may be present.
        // Those must be moved to end of <offset,offset+n> marks interval.
        // The interval must be scanned (since markUpdates has no info about mark's current index).
        // Some marks in markUpdates array might already be removed from markArray since their position
        // was GCed so this must be taken into consideration too.
        if (markUpdates != null) { // Possibly restore marks' original offsets
            int activeMarkUpdatesCount = 0;
            for (int i = 0; i < markUpdates.length; i++) {
                MarkUpdate update = markUpdates[i];
                if (update.mark.isActive()) {
                    update.restoreRawOffset();
                    markUpdates[activeMarkUpdatesCount++] = update; // Filter markUpdates to valid ones
                }
            }
            if (activeMarkUpdatesCount > 0) {
                if (!backwardBiasHandling) {
                    Mark firstUpdatedMark = markUpdates[0].mark;
                    for (int i = index;; i++) { // The updated mark must be there
                        if (getMark(i) == firstUpdatedMark) {
                            if (i > index) {
                                // Must move initial marks forward right beyond those from filtered markUpdates
                                // Cannot use System.arraycopy() due to gap-storage
                                for (int j = i - index - 1; j >= 0; j--) { // Copy backward
                                    setMark(index + activeMarkUpdatesCount + j, getMark(index + j));
                                }
                                for (int j = activeMarkUpdatesCount - 1; j >= 0; j--) {
                                    setMark(index + j, markUpdates[j].mark);
                                }
                            }
                            break;
                        }
                    }
                } else { // backward-bias marks or marks at offset == 0
                    // For backward bias the active mark updates will be above the non-updated marks.
                    // "index" corresponds to offset+1 (or offset+1+length)
                    Mark lastUpdatedMark = markUpdates[activeMarkUpdatesCount - 1].mark;
                    for (int i = index - 1;; i--) { // The updated mark must be there
                        if (getMark(i) == lastUpdatedMark) {
                            i++; // First mark to move
                            if (i < index) {
                                // Must move initial marks backward right beyond those from filtered markUpdates
                                int count = index - i;
                                for (int j = 0; j < count; j++) {
                                    setMark(i - activeMarkUpdatesCount + j, getMark(i + j));
                                }
                                i = index - activeMarkUpdatesCount;
                                for (int j = activeMarkUpdatesCount - 1; j >= 0; j--) {
                                    setMark(i + j, markUpdates[j].mark);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * Update mark vector by removal. Needs explicit synchronization.
     * 
     * @param offset offset >= 0.
     * @param length length >= 0.
     * @return mark updates or null if none needed.
     */
    MarkUpdate[] removeUpdate(int offset, int length) {
        int index; // Index of first mark to restore its offset upon undo of removal
        int endIndex; // End index of marks to restore their offset upon undo of removal
        int biasOffset = offset;
        if (isBackwardBiasMarks()) {
            biasOffset++;
        }
        int newGapOffset = biasOffset + length;
        endIndex = findFirstIndex(newGapOffset);
        // Move offset gap to endIndex to have natural offsets in RemoveMarkUpdate
        if (newGapOffset != offsetGapStart) {
            moveOffsetGap(newGapOffset, endIndex);
        }
        
        offsetGapStart -= length;
        offsetGapLength += length;
        // Move all positions inside the removed area to its begining.
        // First scan which marks need to be moved to create array of proper size.
        // Affected mark count is small for typical removals so linear iteration
        // is faster than another binary search.
        for (index = endIndex - 1; index >= 0; index--) {
            Mark mark = getMark(index);
            if (mark.rawOffset() < biasOffset) {
                break;
            }
        }
        index++;

        // Remember original marks' offsets for possible undo
        int updateCount = endIndex - index;
        MarkUpdate[] updates;
        if (updateCount > 0) {
            updates = new MarkUpdate[updateCount];
            // Regular marks:
            // Offset gap was moved to (offset+length) i.e. to offset (after actual removal)
            // so all the marks "inside" removed area need to be at offset but above offset gap.
            // Backward-bias marks:
            // Offset gap will be at offset + 1 so all marks will be below gap.
            int newRawOffset = offset;
            if (!isBackwardBiasMarks()) {
                newRawOffset += offsetGapLength;
            }
            for (int i = updateCount - 1; i >= 0; i--) {
                Mark mark = getMark(index + i);
                updates[i] = new MarkUpdate(mark);
                // Fix the mark (it's below gap)
                mark.rawOffset = newRawOffset;
            }
        } else {
            updates = null;
        }
        return updates;
    }
    
    /**
     * Move offset gap so that its start is at particular offset and a corresponding (pre-computed) index.
     *
     * @param newOffsetGapStart new gap start offset.
     * @param index pre-computed index corresponding to new gap start offset.
     *  When offset &gt;= newOffsetGapStart it's above gap.
     */
    private void moveOffsetGap(int newOffsetGapStart, int index) {
        int rawIndex = rawIndex(index);
        int markArrayLength = markArray.length;
        int origOffsetGapStart = offsetGapStart;
        offsetGapStart = newOffsetGapStart;

        if (rawIndex == markArrayLength || markArray[rawIndex].rawOffset() > origOffsetGapStart) {
            // Mark at rawIndex is above the gap
            // Go down and move marks below gap
            if (rawIndex >= gapStart) {
                int gapEnd = gapStart + gapLength;
                while (--rawIndex >= gapEnd) {
                    Mark mark = markArray[rawIndex];
                    if (mark.rawOffset() > origOffsetGapStart) {
                        mark.rawOffset -= offsetGapLength;
                    } else {
                        return;
                    }
                }
                rawIndex = gapStart; // Continue on gapStart-1
            }
            while (--rawIndex >= 0) {
                Mark mark = markArray[rawIndex];
                if (mark.rawOffset() > origOffsetGapStart) {
                    mark.rawOffset -= offsetGapLength;
                } else {
                    return;
                }
            }
            
        } else { // go up to check and move the marks to be beyond the offset gap
            if (rawIndex < gapStart) {
                while (rawIndex < gapStart) {
                    Mark mark = markArray[rawIndex++];
                    if (mark.rawOffset() <= origOffsetGapStart) {
                        mark.rawOffset += offsetGapLength;
                    } else {
                        return;
                    }
                }
                rawIndex += gapLength;
            }
            while (rawIndex < markArrayLength) {
                Mark mark = markArray[rawIndex++];
                if (mark.rawOffset() <= origOffsetGapStart) {
                    mark.rawOffset += offsetGapLength;
                } else {
                    return;
                }
            }
        }
    }

    private void moveGap(int index) {
        // No need to clear the stale space in mark array after arraycopy() since removeDisposedMarks() will do it
        if (index <= gapStart) { // move gap down
            int moveSize = gapStart - index;
            System.arraycopy(markArray, index, markArray, gapStart + gapLength - moveSize, moveSize);
        } else { // above gap
            int moveSize = index - gapStart;
            System.arraycopy(markArray, gapStart + gapLength, markArray, gapStart, moveSize);
        }
        gapStart = index;
    }

    /**
     * Compact array of marks. Needs explicit synchronization.
     */
    void compact() {
        if (gapLength > 4) { // Possibly leave 4 free mark slots for little expansion
            reallocate(4);
        }
    }

    private void reallocate(int newGapLength) {
        int gapEnd = gapStart + gapLength;
        int aboveGapLength = markArray.length - gapEnd;
        int newLength = gapStart + aboveGapLength + newGapLength;
        Mark[] newMarkArray = new Mark[newLength];
        System.arraycopy(markArray, 0, newMarkArray, 0, gapStart);
        System.arraycopy(markArray, gapEnd, newMarkArray, newLength - aboveGapLength, aboveGapLength);
        // gapStart is same
        gapLength = newGapLength;
        markArray = newMarkArray;
    }

    /**
     * Find an index of the first mark with the given offset (if there would be multiple marks with the same offset).
     *
     * @param offset &gt;=0
     * @return index &gt;=0 where a first mark with the given offset (or a higher offset) resides.
     */
    private int findFirstIndex(int offset) {
        // Find existing position or create a new position
        int low = 0;
        int markCount = markCount();
        int high = markCount - 1;
        int rawOffset = rawOffset(offset);
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (getMark(mid).rawOffset() < rawOffset) { // Search for first with "rawOffset"
                low = mid + 1;
            } else { // markRawOffset >= rawOffset
                high = mid - 1;
            }
        }
        return low; // "low" points to first mark with the given offset
    }

    int offset(int rawOffset) {
        return (rawOffset < offsetGapStart) ? rawOffset : (rawOffset - offsetGapLength);
    }
    
    int rawOffset(int offset) {
        return (offset < offsetGapStart) ? offset : offset + offsetGapLength;
    }

    private int rawIndex(int index) {
        return (index < gapStart)
                ? index
                : index + gapLength;
    }

    private int markCount() {
        return markArray.length - gapLength;
    }

    private Mark getMark(int index) {
        return markArray[rawIndex(index)];
    }
    
    private void setMark(int index, Mark mark) {
        markArray[rawIndex(index)] = mark;
    }

    void notifyMarkDisposed() {
        synchronized (lock) {
            disposedMarkCount++;
            if (disposedMarkCount > Math.max(5, markCount() >> 3)) {
                removeDisposedMarks();
            }
        }
    }

    private void removeDisposedMarks() {
        int rawIndex = 0;
        int validIndex = 0;
        int gapEnd = gapStart + gapLength;
        // Only retain marks with valid position
        while (rawIndex < gapStart) {
            Mark mark = markArray[rawIndex];
            if (mark.get() != null) { // valid mark
                if (rawIndex != validIndex) {
                    markArray[validIndex] = mark;
                }
                validIndex++;
            } else {
                mark.clearMarkVector();
            }
            rawIndex++;
        }
        gapStart = validIndex;
        // Go back from end till gap end
        rawIndex = markArray.length;
        int topValidIndex = rawIndex; // validIndex points to first valid mark above gap
        while (--rawIndex >= gapEnd) {
            Mark mark = markArray[rawIndex];
            if (mark.get() != null) { // valid mark
                if (rawIndex != --topValidIndex) {
                    markArray[topValidIndex] = mark;
                }
            } else {
                mark.clearMarkVector();
            }
        }
        int newGapLength = topValidIndex - gapStart;
        gapLength = newGapLength;
        // Clear the area between valid indices (also because moveGap() does not clear the stale areas)
        while (validIndex < topValidIndex) {
            markArray[validIndex++] = null;
        }
        // Set disposedMarkCount to zero. Since the "markVector" field was cleared in the marks
        // removed from markArray and so those marks pending in the queue
        // will no longer notify markVector.notifyMarkDisposed().
        disposedMarkCount = 0;
    }
    
    String consistencyError(int maxOffset) {
        int markCount = markCount();
        if (!isBackwardBiasMarks() && markCount < 1) {
            return "markCount=" + markCount + " < 1"; // NOI18N
        }
        int lastOffset = 0;
        for (int i = 0; i < markCount; i++) {
            Mark mark = getMark(i);
            int offset = mark.getOffset();
            int rawOffset = mark.rawOffset();
            String err = null;
            if (offset < lastOffset) {
                err = "offset=" + offset + " < lastOffset=" + lastOffset; // NOI18N
            } else if (rawOffset < 0) {
                err = "rawOffset=" + rawOffset + " < 0";
            } else if (offset > maxOffset) {
                err = "offset=" + offset + " > maxOffset=" + maxOffset; // NOI18N
            } else if (offset < offsetGapStart && rawOffset >= offsetGapStart) {
                err = "offset=" + offset + " but rawOffset=" + rawOffset + // NOI18N
                        " >= offsetGapStart=" + offsetGapStart; // NOI18N
            } else if (offset >= offsetGapStart && rawOffset < offsetGapStart + offsetGapLength) {
                err = "offset=" + offset + " but rawOffset=" + rawOffset + // NOI18N
                        " < offsetGapStart=" + offsetGapStart + " + offsetGapLength=" + offsetGapLength; // NOI18N
            }
            if (err != null) {
                return (isBackwardBiasMarks() ? "BB-" : "") + "markArray[" + i + "]: " + err; // NOI18N
            }
            lastOffset = offset;
        }
        if (zeroPos.getOffset() != 0) {
            return "zeroPos.getOffset()=" + zeroPos.getOffset() + " != 0"; // NOI18N
        }
        return null;
    }

    /** Get info about this mark vector. */
    @Override
    public String toString() {
        return (isBackwardBiasMarks() ? "BB:" : "") + "markCount=" + markCount() + // NOI18N
                ", gap:" + CharContent.gapToString(markArray.length, gapStart, gapLength) + // NOI18N
                ", OGap:<" + offsetGapStart + '+' + offsetGapLength + // NOI18N
                ',' + (offsetGapStart+offsetGapLength) + '>'; // NOI18N
    }
    
    public String toStringDetail(Mark accentMark) {
        StringBuilder sb = new StringBuilder(200);
        sb.append(toString()).append(", IHC=").append(System.identityHashCode(this)).append('\n');
        int markCount = markCount();
        int digitCount = ArrayUtilities.digitCount(markCount);
        for (int i = 0; i < markCount; i++) {
            Mark mark = getMark(i);
            sb.append((mark == accentMark) ? "**" : "  ");
            ArrayUtilities.appendBracketedIndex(sb, i, digitCount);
            sb.append(mark.toStringDetail()).append('\n');
        }
        return sb.toString();
    }


    /**
     * Class used for holding the offset to which the mark
     * will be restored in case it was inside the area 
     * that was removed.
     */
    static final class MarkUpdate {

        final Mark mark;

        /**
         * Original offset that should be used for restoring of the mark's offset upon undo.
         */
        final int origRawOffset;
        
        MarkUpdate(Mark mark) {
            this.mark = mark;
            this.origRawOffset = mark.rawOffset();
        }
        
        void restoreRawOffset() {
            mark.rawOffset = origRawOffset;
        }

        @Override
        public String toString() {
            return mark.toStringDetail() + " <= OrigRaw:" + origRawOffset; // NOI18N
        }

    }

}
