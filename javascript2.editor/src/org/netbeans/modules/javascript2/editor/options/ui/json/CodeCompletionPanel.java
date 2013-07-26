/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javascript2.editor.options.ui.json;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.netbeans.modules.javascript2.editor.options.OptionsUtils;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 * XXX copied from PHP
 */
public class CodeCompletionPanel extends JPanel {

    private final Preferences preferences;
    private final ItemListener defaultCheckBoxListener = new DefaultCheckBoxListener();

    public CodeCompletionPanel(Preferences preferences) {
        assert preferences != null;

        this.preferences = preferences;

        initComponents();

        initAutoCompletion();
    }

    public static PreferencesCustomizer.Factory getCustomizerFactory() {
        return new PreferencesCustomizer.Factory() {

            @Override
            public PreferencesCustomizer create(Preferences preferences) {
                return new CodeCompletionPreferencesCustomizer(preferences);
            }
        };
    }

    private void initAutoCompletion() {
        boolean codeCompletionSmartQuotes = preferences.getBoolean(
                OptionsUtils.AUTO_COMPLETION_SMART_QUOTES,
                OptionsUtils.AUTO_COMPLETION_SMART_QUOTES_DEFAULT);
        autoCompletionSmartQuotesCheckBox.setSelected(codeCompletionSmartQuotes);
        autoCompletionSmartQuotesCheckBox.addItemListener(defaultCheckBoxListener);
    }

    void validateData() {
        preferences.putBoolean(OptionsUtils.AUTO_COMPLETION_SMART_QUOTES, autoCompletionSmartQuotesCheckBox.isSelected());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoCompletionSmartQuotesLabel = new JLabel();
        autoCompletionSmartQuotesCheckBox = new JCheckBox();

        Mnemonics.setLocalizedText(autoCompletionSmartQuotesLabel, NbBundle.getMessage(CodeCompletionPanel.class, "CodeCompletionPanel.autoCompletionSmartQuotesLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(autoCompletionSmartQuotesCheckBox, NbBundle.getMessage(CodeCompletionPanel.class, "CodeCompletionPanel.autoCompletionSmartQuotesCheckBox.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(autoCompletionSmartQuotesLabel)
                    .addComponent(autoCompletionSmartQuotesCheckBox))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(autoCompletionSmartQuotesLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(autoCompletionSmartQuotesCheckBox)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CodeCompletionPanel.class, "CodeCompletionPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CodeCompletionPanel.class, "CodeCompletionPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox autoCompletionSmartQuotesCheckBox;
    private JLabel autoCompletionSmartQuotesLabel;
    // End of variables declaration//GEN-END:variables

    private final class DefaultCheckBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            validateData();
        }
    }

    static final class CodeCompletionPreferencesCustomizer implements PreferencesCustomizer {

        private final Preferences preferences;

        private CodeCompletionPreferencesCustomizer(Preferences preferences) {
            this.preferences = preferences;
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDisplayName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx("org.netbeans.modules.javascript2.editor.options.CodeCompletionPanel");
        }

        @Override
        public JComponent getComponent() {
            return new CodeCompletionPanel(preferences);
        }
    }
}
