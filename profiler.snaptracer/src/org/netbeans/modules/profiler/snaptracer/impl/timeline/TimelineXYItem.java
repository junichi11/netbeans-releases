/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.profiler.snaptracer.impl.timeline;

import org.netbeans.lib.profiler.charts.swing.LongRect;
import org.netbeans.lib.profiler.charts.xy.XYItemChange;
import org.netbeans.lib.profiler.charts.xy.synchronous.SynchronousXYItem;

/**
 *
 * @author Jiri Sedlacek
 */
abstract class TimelineXYItem extends SynchronousXYItem {

    private int lastIndex;
    private int lastValuesCount;
    
    private final LongRect bounds;
    private long initialMinY;
    private long initialMaxY;

    private long minY;
    private long maxY;

    private int itemIndex;


    // --- Constructor ---------------------------------------------------------

    TimelineXYItem(String name, int itemIndex) {
        this(name, Long.MAX_VALUE, itemIndex);
    }

    TimelineXYItem(String name, long initialMinY, int itemIndex) {
        this(name, initialMinY, Long.MIN_VALUE, itemIndex);
    }

    TimelineXYItem(String name, long initialMinY, long initialMaxY, int itemIndex) {
        super(name, initialMinY, initialMaxY);
        this.initialMinY = initialMinY;
        this.initialMaxY = initialMaxY;
        minY = Long.MAX_VALUE;
        maxY = Long.MIN_VALUE;
        bounds = new LongRect();
        lastIndex = -1;
        setIndex(itemIndex);
    }


    // --- Internal interface --------------------------------------------------

    final void setIndex(int itemIndex) { this.itemIndex = itemIndex; }

    final int getIndex() { return itemIndex; }


    // --- Item telemetry ------------------------------------------------------

    public XYItemChange valuesChanged() {

        int valuesCount = getValuesCount();
        int index = valuesCount - 1;
        XYItemChange change = null;

        if (index > -1) { // New item(s)

            // Save oldBounds, setup dirtyBounds
            LongRect oldBounds = new LongRect(bounds);
            LongRect dirtyBounds = new LongRect();

            boolean initBounds = lastIndex == -1;
            int dirtyIndex = lastIndex == -1 ? 0 : lastIndex;

            // Process other values
            for (int i = dirtyIndex; i <= index; i++) {

                long timestamp = getXValue(i);
                long value = getYValue(i);

                // Update item minY/maxY
                minY = Math.min(value, minY);
                maxY = Math.max(value, maxY);

                // Process item bounds
                if (initBounds) {
                    // Initialize item bounds
                    bounds.x = timestamp;
                    bounds.y = Math.min(value, initialMinY);
                    bounds.width = 0;
                    bounds.height = Math.max(value, initialMaxY) - bounds.y;
                    initBounds = false;
                } else {
                    // Update item bounds
                    LongRect.add(bounds, timestamp, value);
                    if (valuesCount == lastValuesCount) {
                        bounds.x = getXValue(0);
                        bounds.width = getXValue(valuesCount - 1) - bounds.x;
                    }
                }

                // Process dirty bounds
                if (i == dirtyIndex) {
                    // Setup dirty bounds
                    dirtyBounds.x = timestamp;
                    dirtyBounds.y = value;
                    dirtyBounds.width = getXValue(index) - dirtyBounds.x;
                } else {
                    // Update dirty y/height
                    long dirtyY = dirtyBounds.y;
                    dirtyBounds.y = Math.min(dirtyY, value);
                    dirtyBounds.height = Math.max(dirtyY, value) - dirtyBounds.y;
                }

            }

            // Return ItemChange
            int indexesCount = index - lastIndex;
            int[] indexes = new int[indexesCount];
            for (int i = 0; i < indexesCount; i++) indexes[i] = lastIndex + 1 + i;
            change = new XYItemChange.Default(this, indexes, oldBounds,
                                              new LongRect(bounds), dirtyBounds);

        } else { // Reset

            minY = Long.MAX_VALUE;
            maxY = Long.MIN_VALUE;

            // Save oldBounds
            LongRect oldBounds = new LongRect(bounds);
            LongRect.set(bounds, 0, 0, 0, 0);

            // Return ItemChange
            change = new XYItemChange.Default(this, new int[] { -1 }, oldBounds,
                                            new LongRect(bounds), oldBounds);

        }
        
        lastIndex = index;
        lastValuesCount = valuesCount;
        return change;
        
    }

    public long getMinYValue() { return minY; }

    public long getMaxYValue() { return maxY; }
    
    public LongRect getBounds() {
        if (getValuesCount() > 0) return bounds;
        else return getInitialBounds();
    }

}
