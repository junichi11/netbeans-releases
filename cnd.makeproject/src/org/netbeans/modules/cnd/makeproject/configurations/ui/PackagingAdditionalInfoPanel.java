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
package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.util.List;
import java.util.ResourceBundle;
import org.netbeans.modules.cnd.makeproject.api.configurations.PackagingConfiguration;
import org.netbeans.modules.cnd.makeproject.ui.utils.ListEditorPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class PackagingAdditionalInfoPanel extends ListEditorPanel<String> {

    private PackagingConfiguration packagingConfiguration;

    public PackagingAdditionalInfoPanel(List<String> infoList, PackagingConfiguration packagingConfiguration) {
        super(infoList);
        this.packagingConfiguration = packagingConfiguration;

        getEditButton().setVisible(false);
        getDefaultButton().setVisible(false);
        getCopyButton().setVisible(false);
    }

    @Override
    public String getListLabelText() {
        return getString("AdditionalInfoLabel_txt");
    }

    @Override
    public char getListLabelMnemonic() {
        return getString("AdditionalInfoLabel_mn").toCharArray()[0];
    }

    @Override
    public String addAction() {
        NotifyDescriptor.InputLine notifyDescriptor = new NotifyDescriptor.InputLine("", getString("ADD_DIALOG_LABEL_TXT"));
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue() != NotifyDescriptor.OK_OPTION) {
            return null;
        }
        String newS = notifyDescriptor.getInputText().trim();
        if (newS.length() == 0) {
            return null;
        }
        return newS;
    }

    /** Look up i18n strings here */
    private static ResourceBundle bundle;

    private static String getString(String s) {
        if (bundle == null) {
            bundle = NbBundle.getBundle(PackagingAdditionalInfoPanel.class);
        }
        return bundle.getString(s);
    }
}
