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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.types;

import org.netbeans.modules.cnd.debugger.common2.utils.props.IntegerProperty;
import org.netbeans.modules.cnd.debugger.common2.utils.props.StringProperty;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;
import org.netbeans.modules.cnd.debugger.common2.debugger.DebuggerAnnotation;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.NativeBreakpoint;
import org.netbeans.modules.cnd.utils.CndPathUtilitities;

public final class LineBreakpoint extends NativeBreakpoint {

    public IntegerProperty lineNumber = 
	new IntegerProperty(pos, "lineNumber", null, false, -1); // NOI18N
    public StringProperty fileName =
	new StringProperty(pos, "fileName", null, false, null); // NOI18N

    public LineBreakpoint(int flags) {
	super(LineBreakpointType.getDefault(), flags);
    }

    public void setFileName(String fileName) {
	if (IpeUtils.sameString(this.fileName.toString(), fileName))
	    return;
	this.fileName.set(fileName);
    } 

    public String getShortFileName() {
	if (fileName.get() == null)
	    return "";
	else
	    return CndPathUtilitities.getBaseName(fileName.get());
    } 

    public String getFileName() {
	if (fileName.get() == null)
	    return "";
	else
	    return fileName.get();
    } 

    /**
     * Transfer line number information from annotation to slot so
     * getLineNumber() will have something to return before proceeding
     * with the regular NativeBreakpoint.removeAnnotations().
     */

    // override NativeBreakpoint
    public void removeAnnotations() {

	int annoLineNo = annoLineNo();
	if (annoLineNo != 0)
	    lineNumber.set(annoLineNo);

	super.removeAnnotations();
    }

    // was package private -- only used by makeEditableCopy
    public void setLineNumber(int newLineNumber) {
	/* DEBUG
	System.out.println("LineBreakpoint.setLineNumber(): " + newLineNumber);
	*/
	if (annoLineNo() != 0) {
	    // DEBUG System.out.println("\thas annotation -- adjusting");
	    // DEBUG Thread.dumpStack();

	    removeAnnotations();

	    // removeAnnotations sets lineNumber, so set it again
	    lineNumber.set(newLineNumber);

	    addAnnotation(fileName.get(), lineNumber.get(), 0);
	} else {
	    // DEBUG System.out.println("\taccepted");
	    lineNumber.set(newLineNumber);
	}
    }

    // was package private -- only used by makeEditableCopy
    public void setLineNumberInitial(int newLineNumber) {
	/* DEBUG
	System.out.println("LineBreakpoint.setLineNumber(): " + newLineNumber);
	*/
	if (annoLineNo() != 0) {
	    // DEBUG System.out.println("\thas annotation -- adjusting");
	    // DEBUG Thread.dumpStack();

	    removeAnnotations();

	    // removeAnnotations sets lineNumber, so set it again
	    lineNumber.setFromObjectInitial(newLineNumber);

	    addAnnotation(fileName.get(), lineNumber.get(), 0);
	} else {
	    // DEBUG System.out.println("\taccepted");
	    lineNumber.setFromObjectInitial(newLineNumber);
	}
    }

    /**
     * Return a representative line number.
     * If we can get the line number from annotatiosn, get it from there,
     * otherwise fall back on the slot.
     */

    public int getLineNumber() {
	int lineNo = annoLineNo();
	if (lineNo == 0) {
	    lineNo = lineNumber.get();
	    /* DEBUG
	    System.out.println("LineBreakpoint.getLineNumber(): from slot " +
		lineNo);
	    */
	} else {
	    /* DEBUG
	    System.out.println("LineBreakpoint.getLineNumber(): from anno " +
		lineNo);
	    */
	}
	return lineNo;
    }

    /**
     * Extract a representative lineno for this bpt from annotations.
     */
    private int annoLineNo() {
	DebuggerAnnotation[] annotations = annotations(); 
	if (annotations.length > 0) {
	    DebuggerAnnotation a = annotations[0];
	    return a.getLineNo();
	}
	return 0;
    }


    public void setFileAndLine(String fileName, int lineNumber) {
	setFileName(fileName);
	setLineNumber(lineNumber);
    }

    protected final String getSummary() {
	return Catalog.format("CTL_Line_event_name", // NOI18N
		              getFileName(),
		              getLineNumber());
    } 

    protected String getDisplayNameHelp() {
	return CndPathUtilitities.getBaseName(getFileName()) +
	    ":" + getLineNumber(); // NOI18N
    }

    protected void processOriginalEventspec(String oeventspec) {
	assert IpeUtils.isEmpty(oeventspec);
    }
}
