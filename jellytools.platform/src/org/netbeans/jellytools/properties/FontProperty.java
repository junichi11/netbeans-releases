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
package org.netbeans.jellytools.properties;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.properties.editors.FontCustomEditorOperator;

/** Operator serving property of type Font
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a> */
public class FontProperty extends Property {

    /** String constant for plain font style */
    public static final String STYLE_PLAIN = FontCustomEditorOperator.STYLE_PLAIN;
    /** String constant for bold font style */
    public static final String STYLE_BOLD = FontCustomEditorOperator.STYLE_BOLD; 
    /** String constant for italic font style */    
    public static final String STYLE_ITALIC = FontCustomEditorOperator.STYLE_ITALIC; 
    /** String constant for bold italic font style */    
    public static final String STYLE_BOLDITALIC = FontCustomEditorOperator.STYLE_BOLDITALIC;
   
    /** Creates a new instance of FontProperty
     * @param propertySheetOper PropertySheetOperator where to find property.
     * @param name String property name 
     */
    public FontProperty(PropertySheetOperator propertySheetOper, String name) {
        super(propertySheetOper, name);
    }
    
    /** invokes custom property editor and returns proper custom editor operator
     * @return FontCustomEditorOperator */    
    public FontCustomEditorOperator invokeCustomizer() {
        openEditor();
        return new FontCustomEditorOperator(getName());
    }
    
    /** setter for Font value through Custom Editor
     * @param fontName String font name
     * @param fontStyle String font style
     * @param fontSize String font size */    
    public void setFontValue(String fontName, String fontStyle, String fontSize) {
        FontCustomEditorOperator customizer=invokeCustomizer();
        if (fontName!=null)
            customizer.setFontName(fontName);
        if (fontStyle!=null)
            customizer.setFontStyle(fontStyle);
        if (fontSize!=null)
            customizer.setFontSize(fontSize);
        customizer.ok();
    }        
    
    /** getter for Font value through Custom Editor
     * @return String[3] font name, font style and font size */    
    public String[] getFontValue() {
        String[] value=new String[3];
        FontCustomEditorOperator customizer=invokeCustomizer();
        value[0]=customizer.getFontName();
        value[1]=customizer.getFontStyle();
        value[2]=customizer.getFontSize();
        customizer.close();
        return value;
    }

    /** Performs verification by accessing all sub-components */    
    public void verify() {
        invokeCustomizer().verify();
        new NbDialogOperator(getName()).close();
    }        
}
