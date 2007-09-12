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

package org.netbeans.modules.soa.mapper.common.basicmapper.methoid;

import org.netbeans.modules.soa.mapper.common.basicmapper.literal.ILiteralUpdater;

/**
 * <p>
 *
 * Title: IField </p> <p>
 *
 * Description: Generic interface describes the basic functionality of a methoid
 * field. IField is the base interface holding meta data of methoid field, to
 * allow IFieldNode to be constructed and added to IMethoidNode. Subclass should
 * fire property change on the name, type and tooltip properties once changed.
 * </p> <p>
 *
 * Copyright: Copyright (c) 2002 </p> <p>
 *
 * Company: </p>
 *
 * @author    Un Seng Leong
 * @created   December 4, 2002
 * @version   1.0
 */
public interface IField {

    /**
     * Return the name of this methoid field.
     *
     * @return   the name of this methoid field.
     */
    public String getName();

    /**
     * Return the type of this methoid field.
     *
     * @return   the type of this methoid field.
     */
    public String getType();

    /**
     * Return the tooltip text of this methoid field.
     *
     * @return   the tooltip text of this methoid field.
     */
    public String getToolTipText();

    /**
     * Return the methoid field in another object repersentation.
     *
     * @return   the methoid field in another object repersentation.
     */
    public Object getData();

    /**
     * Return true if this methoid field is a input field, false otherwise.
     *
     * @return   true if this methoid field is a input field, false otherwise.
     */
    public boolean isInput();

    /**
     * Return true if this methoid field is a output field, false otherwise.
     *
     * @return   true if this methoid field is a output field, false otherwise.
     */
    public boolean isOutput();
    
    /**
     * Return any optional literal updater for the field.
     * If no literal updater exists for the field (null literal updater), then
     * the field does not support being connected-to/represented-by literals.
     * Otherwise, the field will support literals.
     * 
     * @return the literal updater, or null if none exists
     */
    public ILiteralUpdater getLiteralUpdater();
    
    /**
     * Sets the optional literal updater for the field.
     * 
     * @param literalUpdater the literal updater
     *
     */ 
    public void setLiteralUpdater(ILiteralUpdater literalUpdater);
    
    /**
     * Sets the name of this methoid field.
     *
     * @param name the field name
     */
    public void setName(String name);

    /**
     * Sets the type of this methoid field.
     *
     * @param type the field type
     */
    public void setType(String type);

    /**
     * Sets the tooltip text of this methoid field.
     *
     * @param tooltip the field tooltip
     */
    public void setToolTipText(String tooltip);

    /**
     * Sets the methoid field in another object repersentation.
     *
     * @param data field data
     */
    public void setData(Object data);

    /**
     * Sets whether this methoid field is a input field.
     *
     * @param value true if the field is an input field
     */
    public void setInput(boolean value);

    /**
     * Sets whether this methoid field is a output field.
     *
     * @param value true if the field is an output field
     */
    public void setOutput(boolean value);
}
