/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.project.ui.customizer;

import java.awt.Component;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.project.classpath.BasePathSupport;
import org.netbeans.modules.php.project.ui.PathUiSupport;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;


public class CustomizerSeleniumTesting extends JPanel {

    private static final long serialVersionUID = -654768735165768L;

    private final ProjectCustomizer.Category category;
    private final PhpProjectProperties uiProps;
    private final DefaultListModel<BasePathSupport.Item> seleniumTestDirectoriesPathListModel;


    CustomizerSeleniumTesting(ProjectCustomizer.Category category, PhpProjectProperties uiProps) {
        assert category != null;
        assert uiProps != null;

        this.category = category;
        this.uiProps = uiProps;
        seleniumTestDirectoriesPathListModel = uiProps.getSeleniumTestDirectoriesListModel();

        initComponents();
        init();
    }

    private void init() {
        initTestDirectories();
    }

    private void initTestDirectories() {
        PathUiSupport.EditMediator.FileChooserDirectoryHandler directoryHandler = new PathUiSupport.EditMediator.FileChooserDirectoryHandler() {
            @Override
            public String getDirKey() {
                return CustomizerSeleniumTesting.class.getName();
            }
            @Override
            public File getCurrentDirectory() {
                return FileUtil.toFile(uiProps.getProject().getProjectDirectory());
            }
        };
        testDirsList.setModel(seleniumTestDirectoriesPathListModel);
        testDirsList.setCellRenderer(uiProps.getSeleniumTestDirectoriesListRenderer());
        seleniumTestDirectoriesPathListModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                validateAndStore();
            }
            @Override
            public void intervalRemoved(ListDataEvent e) {
                validateAndStore();
            }
            @Override
            public void contentsChanged(ListDataEvent e) {
                validateAndStore();
            }
        });
        PathUiSupport.EditMediator.register(uiProps.getProject(),
                                               testDirsList,
                                               addFolderButton.getModel(),
                                               removeButton.getModel(),
                                               moveUpButton.getModel(),
                                               moveDownButton.getModel(),
                                               directoryHandler);
    }

    void validateAndStore() {
        validateData();
    }

    private void validateData() {
        assert EventQueue.isDispatchThread();
        // test dirs
        ValidationResult result = new TestDirectoriesPathSupport.Validator()
                .validatePaths(uiProps.getProject(), convertToList(seleniumTestDirectoriesPathListModel))
                .getResult();
        if (result.hasErrors()) {
            category.setErrorMessage(result.getErrors().get(0).getMessage());
            category.setValid(false);
            return;
        }
        if (result.hasWarnings()) {
            category.setErrorMessage(result.getWarnings().get(0).getMessage());
            category.setValid(true);
            return;
        }
        // everything ok
        category.setErrorMessage(" "); // NOI18N
        category.setValid(true);
    }

    private List<BasePathSupport.Item> convertToList(DefaultListModel<BasePathSupport.Item> listModel) {
        List<BasePathSupport.Item> items = new ArrayList<>(listModel.getSize());
        for (int i = 0; i < listModel.getSize(); i++) {
            items.add(listModel.get(i));
        }
        return items;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        testDirsLabel = new JLabel();
        testDirsScrollPane = new JScrollPane();
        testDirsList = new JList<BasePathSupport.Item>();
        addFolderButton = new JButton();
        removeButton = new JButton();
        moveUpButton = new JButton();
        moveDownButton = new JButton();

        Mnemonics.setLocalizedText(testDirsLabel, NbBundle.getMessage(CustomizerSeleniumTesting.class, "CustomizerSeleniumTesting.testDirsLabel.text")); // NOI18N

        testDirsScrollPane.setViewportView(testDirsList);

        Mnemonics.setLocalizedText(addFolderButton, NbBundle.getMessage(CustomizerSeleniumTesting.class, "CustomizerSeleniumTesting.addFolderButton.text")); // NOI18N

        Mnemonics.setLocalizedText(removeButton, NbBundle.getMessage(CustomizerSeleniumTesting.class, "CustomizerSeleniumTesting.removeButton.text")); // NOI18N

        Mnemonics.setLocalizedText(moveUpButton, NbBundle.getMessage(CustomizerSeleniumTesting.class, "CustomizerSeleniumTesting.moveUpButton.text")); // NOI18N

        Mnemonics.setLocalizedText(moveDownButton, NbBundle.getMessage(CustomizerSeleniumTesting.class, "CustomizerSeleniumTesting.moveDownButton.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(testDirsScrollPane, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(addFolderButton, GroupLayout.Alignment.TRAILING)
                    .addComponent(removeButton, GroupLayout.Alignment.TRAILING)
                    .addComponent(moveUpButton, GroupLayout.Alignment.TRAILING)
                    .addComponent(moveDownButton, GroupLayout.Alignment.TRAILING)))
            .addGroup(layout.createSequentialGroup()
                .addComponent(testDirsLabel)
                .addGap(0, 242, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {addFolderButton, moveDownButton, moveUpButton, removeButton});

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(testDirsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(testDirsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addFolderButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(moveUpButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveDownButton)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addFolderButton;
    private JButton moveDownButton;
    private JButton moveUpButton;
    private JButton removeButton;
    private JLabel testDirsLabel;
    private JList<BasePathSupport.Item> testDirsList;
    private JScrollPane testDirsScrollPane;
    // End of variables declaration//GEN-END:variables

}
