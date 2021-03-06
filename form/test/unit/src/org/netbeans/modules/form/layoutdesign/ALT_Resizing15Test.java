/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of the
 * License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing the
 * software, include this License Header Notice in each file and include the
 * License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by Oracle
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only
 * the GPL Version 2, indicate your decision by adding "[Contributor] elects to
 * include this software in this distribution under the [CDDL or GPL Version 2]
 * license." If you do not indicate a single choice of license, a recipient has
 * the option to distribute your version of this file under either the CDDL, the
 * GPL Version 2 or to extend the choice of license to its licensees as provided
 * above. However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is made
 * subject to such option by the copyright holder.
 */
package org.netbeans.modules.form.layoutdesign;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileUtil;

public class ALT_Resizing15Test extends LayoutTestCase {

    public ALT_Resizing15Test(String name) {
        super(name);
        try {
            className = this.getClass().getName();
            className = className.substring(className.lastIndexOf('.') + 1, className.length());
            startingFormFile = FileUtil.toFileObject(new File(url.getFile() + goldenFilesPath + className + "-StartingForm.form").getCanonicalFile());
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    /**
     * Resize jTextField3 by right edge to align with jTextField2.
     * This tries to create S-layout. To avoid that the resizing snap should be
     * ignored, preserving alignment of the original fixed edge.
     */
    public void doChanges0() {
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jLabel4", new Rectangle(30, 66, 34, 14));
        baselinePosition.put("jLabel4-34-14", new Integer(11));
        compBounds.put("jLabel3", new Rectangle(21, 40, 43, 14));
        baselinePosition.put("jLabel3-43-14", new Integer(11));
        compBounds.put("jLabel1", new Rectangle(10, 14, 54, 14));
        baselinePosition.put("jLabel1-54-14", new Integer(11));
        compBounds.put("jTextField3", new Rectangle(68, 37, 322, 20));
        baselinePosition.put("jTextField3-322-20", new Integer(14));
        compBounds.put("jTextField4", new Rectangle(68, 63, 59, 20));
        baselinePosition.put("jTextField4-59-20", new Integer(14));
        compBounds.put("jLabel5", new Rectangle(158, 66, 28, 14));
        baselinePosition.put("jLabel5-28-14", new Integer(11));
        compBounds.put("jTextField1", new Rectangle(68, 11, 59, 20));
        baselinePosition.put("jTextField1-59-20", new Integer(14));
        compBounds.put("jLabel2", new Rectangle(131, 14, 55, 14));
        baselinePosition.put("jLabel2-55-14", new Integer(11));
        compBounds.put("jTextField5", new Rectangle(190, 63, 200, 20));
        baselinePosition.put("jTextField5-200-20", new Integer(14));
        compBounds.put("jTextField2", new Rectangle(190, 11, 59, 20));
        baselinePosition.put("jTextField2-59-20", new Integer(14));
        compMinSize.put("Form", new Dimension(259, 94));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        compPrefSize.put("jTextField3", new Dimension(59, 20));
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
// > START RESIZING
        baselinePosition.put("jTextField3-322-20", new Integer(14));
        compPrefSize.put("jTextField3", new Dimension(59, 20));
        {
            String[] compIds = new String[]{
                "jTextField3"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(68, 37, 322, 20)
            };
            Point hotspot = new Point(391, 49);
            int[] resizeEdges = new int[]{
                1,
                -1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jTextField3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(251, 61);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(68, 37, 181, 20)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jTextField3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(250, 61);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(68, 37, 181, 20)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jLabel4", new Rectangle(30, 66, 34, 14));
        baselinePosition.put("jLabel4-34-14", new Integer(11));
        compBounds.put("jLabel3", new Rectangle(21, 40, 43, 14));
        baselinePosition.put("jLabel3-43-14", new Integer(11));
        compBounds.put("jLabel1", new Rectangle(10, 14, 54, 14));
        baselinePosition.put("jLabel1-54-14", new Integer(11));
        compBounds.put("jTextField3", new Rectangle(68, 37, 181, 20));
        baselinePosition.put("jTextField3-181-20", new Integer(14));
        compBounds.put("jTextField4", new Rectangle(68, 63, 59, 20));
        baselinePosition.put("jTextField4-59-20", new Integer(14));
        compBounds.put("jLabel5", new Rectangle(158, 66, 28, 14));
        baselinePosition.put("jLabel5-28-14", new Integer(11));
        compBounds.put("jTextField1", new Rectangle(68, 11, 59, 20));
        baselinePosition.put("jTextField1-59-20", new Integer(14));
        compBounds.put("jLabel2", new Rectangle(131, 14, 55, 14));
        baselinePosition.put("jLabel2-55-14", new Integer(11));
        compBounds.put("jTextField5", new Rectangle(190, 63, 200, 20));
        baselinePosition.put("jTextField5-200-20", new Integer(14));
        compBounds.put("jTextField2", new Rectangle(190, 11, 59, 20));
        baselinePosition.put("jTextField2-59-20", new Integer(14));
        compMinSize.put("Form", new Dimension(259, 94));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jLabel4", new Rectangle(30, 66, 34, 14));
        baselinePosition.put("jLabel4-34-14", new Integer(11));
        compBounds.put("jLabel3", new Rectangle(21, 40, 43, 14));
        baselinePosition.put("jLabel3-43-14", new Integer(11));
        compBounds.put("jLabel1", new Rectangle(10, 14, 54, 14));
        baselinePosition.put("jLabel1-54-14", new Integer(11));
        compBounds.put("jTextField3", new Rectangle(68, 37, 181, 20));
        baselinePosition.put("jTextField3-181-20", new Integer(14));
        compBounds.put("jTextField4", new Rectangle(68, 63, 59, 20));
        baselinePosition.put("jTextField4-59-20", new Integer(14));
        compBounds.put("jLabel5", new Rectangle(158, 66, 28, 14));
        baselinePosition.put("jLabel5-28-14", new Integer(11));
        compBounds.put("jTextField1", new Rectangle(68, 11, 59, 20));
        baselinePosition.put("jTextField1-59-20", new Integer(14));
        compBounds.put("jLabel2", new Rectangle(131, 14, 55, 14));
        baselinePosition.put("jLabel2-55-14", new Integer(11));
        compBounds.put("jTextField5", new Rectangle(190, 63, 200, 20));
        baselinePosition.put("jTextField5-200-20", new Integer(14));
        compBounds.put("jTextField2", new Rectangle(190, 11, 59, 20));
        baselinePosition.put("jTextField2-59-20", new Integer(14));
        compMinSize.put("Form", new Dimension(259, 94));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }

    /**
     * Resize jTextField5 by its right edge to align with jTextField2 and 3.
     */
    public void doChanges1() {
// > START RESIZING
        baselinePosition.put("jTextField5-200-20", new Integer(14));
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        {
            String[] compIds = new String[]{
                "jTextField5"
            };
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(190, 63, 200, 20)
            };
            Point hotspot = new Point(388, 70);
            int[] resizeEdges = new int[]{
                1,
                -1
            };
            boolean inLayout = true;
            ld.startResizing(compIds, bounds, hotspot, resizeEdges, inLayout);
        }
// < START RESIZING
        prefPaddingInParent.put("Form-jTextField5-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(254, 79);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(190, 63, 59, 20)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
        prefPaddingInParent.put("Form-jTextField5-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
// > MOVE
        {
            Point p = new Point(253, 79);
            String containerId = "Form";
            boolean autoPositioning = true;
            boolean lockDimension = false;
            Rectangle[] bounds = new Rectangle[]{
                new Rectangle(190, 63, 59, 20)
            };
            ld.move(p, containerId, autoPositioning, lockDimension, bounds);
        }
// < MOVE
// > END MOVING
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPaddingInParent.put("Form-jTextField2-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.endMoving(true);
// < END MOVING
        ld.externalSizeChangeHappened();
// > UPDATE CURRENT STATE
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jLabel4", new Rectangle(30, 66, 34, 14));
        baselinePosition.put("jLabel4-34-14", new Integer(11));
        compBounds.put("jLabel3", new Rectangle(21, 40, 43, 14));
        baselinePosition.put("jLabel3-43-14", new Integer(11));
        compBounds.put("jLabel1", new Rectangle(10, 14, 54, 14));
        baselinePosition.put("jLabel1-54-14", new Integer(11));
        compBounds.put("jTextField3", new Rectangle(68, 37, 181, 20));
        baselinePosition.put("jTextField3-181-20", new Integer(14));
        compBounds.put("jTextField4", new Rectangle(68, 63, 59, 20));
        baselinePosition.put("jTextField4-59-20", new Integer(14));
        compBounds.put("jLabel5", new Rectangle(158, 66, 28, 14));
        baselinePosition.put("jLabel5-28-14", new Integer(11));
        compBounds.put("jTextField1", new Rectangle(68, 11, 59, 20));
        baselinePosition.put("jTextField1-59-20", new Integer(14));
        compBounds.put("jLabel2", new Rectangle(131, 14, 55, 14));
        baselinePosition.put("jLabel2-55-14", new Integer(11));
        compBounds.put("jTextField5", new Rectangle(190, 63, 59, 20));
        baselinePosition.put("jTextField5-59-20", new Integer(14));
        compBounds.put("jTextField2", new Rectangle(190, 11, 59, 20));
        baselinePosition.put("jTextField2-59-20", new Integer(14));
        compMinSize.put("Form", new Dimension(259, 94));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        compPrefSize.put("jTextField2", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jTextField3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField2-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        contInterior.put("Form", new Rectangle(0, 0, 400, 300));
        compBounds.put("jLabel4", new Rectangle(30, 66, 34, 14));
        baselinePosition.put("jLabel4-34-14", new Integer(11));
        compBounds.put("jLabel3", new Rectangle(21, 40, 43, 14));
        baselinePosition.put("jLabel3-43-14", new Integer(11));
        compBounds.put("jLabel1", new Rectangle(10, 14, 54, 14));
        baselinePosition.put("jLabel1-54-14", new Integer(11));
        compBounds.put("jTextField3", new Rectangle(68, 37, 181, 20));
        baselinePosition.put("jTextField3-181-20", new Integer(14));
        compBounds.put("jTextField4", new Rectangle(68, 63, 59, 20));
        baselinePosition.put("jTextField4-59-20", new Integer(14));
        compBounds.put("jLabel5", new Rectangle(158, 66, 28, 14));
        baselinePosition.put("jLabel5-28-14", new Integer(11));
        compBounds.put("jTextField1", new Rectangle(68, 11, 59, 20));
        baselinePosition.put("jTextField1-59-20", new Integer(14));
        compBounds.put("jLabel2", new Rectangle(131, 14, 55, 14));
        baselinePosition.put("jLabel2-55-14", new Integer(11));
        compBounds.put("jTextField5", new Rectangle(190, 63, 59, 20));
        baselinePosition.put("jTextField5-59-20", new Integer(14));
        compBounds.put("jTextField2", new Rectangle(190, 11, 59, 20));
        baselinePosition.put("jTextField2-59-20", new Integer(14));
        compMinSize.put("Form", new Dimension(259, 94));
        compBounds.put("Form", new Rectangle(0, 0, 400, 300));
        prefPadding.put("jTextField4-jLabel5-0-0-0", new Integer(4)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-1", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-2", new Integer(10)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        prefPadding.put("jTextField4-jLabel5-0-0-3", new Integer(18)); // comp1Id-comp2Id-dimension-comp2Alignment-paddingType
        compPrefSize.put("jTextField5", new Dimension(59, 20));
        compPrefSize.put("jTextField2", new Dimension(59, 20));
        prefPaddingInParent.put("Form-jTextField3-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField2-0-1", new Integer(10)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField4-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jLabel5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        prefPaddingInParent.put("Form-jTextField5-1-1", new Integer(11)); // parentId-compId-dimension-compAlignment
        ld.updateCurrentState();
// < UPDATE CURRENT STATE
    }
}
