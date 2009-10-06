/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.visualweb.faces.dt.std;

import org.netbeans.modules.visualweb.faces.dt.util.ComponentBundle;
import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.CustomizerResult;
import com.sun.rave.designtime.Result;
import com.sun.rave.designtime.impl.BasicDisplayAction;

public class ColumnDataBindingCustomizerAction extends BasicDisplayAction {

    private static final ComponentBundle bundle = ComponentBundle.getBundle(
        ColumnDataBindingCustomizerAction.class);

    public ColumnDataBindingCustomizerAction(DesignBean bean, String contextText, String dialogTitle) {
        super(contextText, null, "projrave_ui_elements_webform_dataref_binding_db"); //NOI18N
        this.bean = bean;
        this.dialogTitle = dialogTitle;
    }

    public ColumnDataBindingCustomizerAction(DesignBean bean) {
        super(bundle.getMessage("fillListDbEllipse"), null,
            "projrave_ui_elements_webform_dataitems_binding_db"); //NOI18N
        this.bean = bean;
    }

    protected String dialogTitle;
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    protected DesignBean bean;

    public Result invoke() {
        ColumnDataBindingCustomizer cdbc = dialogTitle != null
            ? new ColumnDataBindingCustomizer(dialogTitle)
            : new ColumnDataBindingCustomizer();
        return new CustomizerResult(bean, cdbc);
    }
}
