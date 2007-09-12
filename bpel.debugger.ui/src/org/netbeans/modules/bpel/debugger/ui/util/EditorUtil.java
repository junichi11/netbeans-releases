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

package org.netbeans.modules.bpel.debugger.ui.util;

import java.io.File;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.bpel.model.api.BpelModel;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Lookup;

/**
 * @author Vladimir Yaroslavskiy
 * @version 2005.10.27
 */
public final class EditorUtil {

    private EditorUtil() {}
    
    public static Line getLine(DataObject dataObject, int lineNumber) {
        LineCookie lineCookie = (LineCookie)dataObject.getCookie(LineCookie.class);
        if (lineCookie == null) {
            Log.out("Line cookie is null: " + dataObject); // NOI18N
            return null;
        }
        
        Line.Set lineSet = lineCookie.getLineSet();
        if (lineSet == null) {
            Log.out("Line set is null :" + lineCookie); // NOI18N
            return null;
        }
        
        try {
            return lineSet.getCurrent(lineNumber - 1);
        } catch (IndexOutOfBoundsException e) {
            Log.out("Out of bounds: " + lineNumber); // NOI18N
            return null;
        }
    }
    
    public static DataObject getDataObject(String url) {
        FileObject fileObject = FileUtil.toFileObject(new File(url));
        if (fileObject == null) {
            Log.out("fileObject is null :" + url); // NOI18N
            return null;
        }
        
        try {
            return DataObject.find(fileObject);
        } catch (DataObjectNotFoundException e) {
            Log.out("Can't find data object for " + fileObject); // NOI18N
            return null;
        }
    }
    
    public static BpelModel getBpelModel(DataObject dataObject) {
        if (dataObject instanceof Lookup.Provider) {
            Lookup.Provider lookupProvider = (Lookup.Provider)dataObject;
            return (BpelModel)lookupProvider.getLookup().lookup(BpelModel.class);
        } else {
            Log.out("Can't lookup a BpelModel - not a Lookup.Provider :" + dataObject); // NOI18N
            return null;
        }
    }
    
    public static BpelModel getBpelModel(String url) {
        DataObject dataObject = getDataObject(url);
        if (dataObject == null) {
            Log.out("DataObject is null :" + url); // NOI18N
            return null;
        }
        
        return getBpelModel(dataObject);
    }
    
    public static StyledDocument getDocument(DataObject dataObject) {
        EditorCookie editorCookie =
                (EditorCookie)dataObject.getCookie(EditorCookie.class);
        if (editorCookie == null) {
            Log.out("Editor cookie is null"); // NOI18N
            return null;
        }
        
        return editorCookie.getDocument();
    }
    
    public static String getFileName(String name) {
        return getTail(getTail(name, "/"), "\\"); // NOI18N
    }
    
    public static int findOffset(Document doc, int lineNumber) {
        Element rootElement = doc.getDefaultRootElement();
        Element lineElement = rootElement.getElement(lineNumber - 1);
        int lineOffset = lineElement.getStartOffset();
        String lineText = "";
        try {
            lineText = doc.getText(
                    lineElement.getStartOffset(),
                    lineElement.getEndOffset() - lineElement.getStartOffset() + 1);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        int column = findNotSpace(lineText);
        if (column < 0) {
            column = 0;
        }
        return lineOffset + column;
    }
    
    private static int findNotSpace(String str) {
        for (int i=0; i < str.length(); i++) {
            if (str.charAt(i) > ' ') {
                return i;
            }
        }
        return -1;
    }
    
    private static String getTail (String value, String delim) {
        int k = value.lastIndexOf (delim);

        if (k != -1) {
          return value.substring(k+1, value.length());
        }
        return value;
    }
}
