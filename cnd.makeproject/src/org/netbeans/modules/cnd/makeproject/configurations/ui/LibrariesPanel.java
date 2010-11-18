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
package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.makeproject.api.MakeArtifact;
import org.netbeans.modules.cnd.makeproject.api.configurations.LibraryItem;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.cnd.utils.FileFilterFactory;
import org.netbeans.modules.cnd.makeproject.ui.utils.PathPanel;
import org.netbeans.modules.cnd.utils.ui.FileChooser;
import org.netbeans.modules.cnd.utils.CndPathUtilitities;
import org.netbeans.modules.cnd.makeproject.api.MakeProjectOptions;
import org.netbeans.modules.cnd.makeproject.api.ProjectSupport;
import org.netbeans.modules.cnd.makeproject.platform.Platforms;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class LibrariesPanel extends javax.swing.JPanel implements HelpCtx.Provider, PropertyChangeListener {

    private Project project;
    private MakeConfiguration conf;
    private MyListEditorPanel myListEditorPanel;
    private String baseDir;
    private PropertyEditorSupport editor;
    private JButton addProjectButton;
    private JButton addStandardLibraryButton;
    private JButton addLibraryButton;
    private JButton addLibraryFileButton;
    private JButton addLibraryOption;

    public LibrariesPanel(Project project, MakeConfiguration conf, String baseDir, List<LibraryItem> data, PropertyEditorSupport editor, PropertyEnv env) {
        this.project = project;
        this.conf = conf;
        this.baseDir = baseDir;
        this.editor = editor;
        initComponents();
        //
        addProjectButton = new JButton(getString("ADD_PROJECT_BUTTON_TXT")); // NOI18N
        addProjectButton.setToolTipText(getString("ADD_PROJECT_BUTTON_TT")); // NOI18N
        addProjectButton.setMnemonic(getString("ADD_PROJECT_BUTTON_MN").charAt(0)); // NOI18N

        addStandardLibraryButton = new JButton(getString("ADD_STANDARD_LIBRARY_BUTTON_TXT")); // NOI18N
        addStandardLibraryButton.setToolTipText(getString("ADD_STANDARD_LIBRARY_BUTTON_TT")); // NOI18N
        addStandardLibraryButton.setMnemonic(getString("ADD_STANDARD_LIBRARY_BUTTON_MN").charAt(0)); // NOI18N

        addLibraryButton = new JButton(getString("ADD_LIBRARY_BUTTON_TXT")); // NOI18N
        addLibraryButton.setToolTipText(getString("ADD_LIBRARY_BUTTON_TT")); // NOI18N
        addLibraryButton.setMnemonic(getString("ADD_LIBRARY_BUTTON_MN").charAt(0)); // NOI18N

        addLibraryFileButton = new JButton(getString("ADD_LIBRARY_FILE_BUTTON_TXT")); // NOI18N
        addLibraryFileButton.setToolTipText(getString("ADD_LIBRARY_FILE_BUTTON_TT")); // NOI18N
        addLibraryFileButton.setMnemonic(getString("ADD_LIBRARY_FILE_BUTTON_MN").charAt(0)); // NOI18N

        addLibraryOption = new JButton(getString("ADD_OPTION_BUTTON_TXT")); // NOI18N
        addLibraryOption.setToolTipText(getString("ADD_OPTION_BUTTON_TT")); // NOI18N
        addLibraryOption.setMnemonic(getString("ADD_OPTION_BUTTON_MN").charAt(0)); // NOI18N

        JButton[] extraButtons = new JButton[]{addProjectButton, addStandardLibraryButton, addLibraryButton, addLibraryFileButton, addLibraryOption};
        myListEditorPanel = new MyListEditorPanel(data, extraButtons);
        addProjectButton.addActionListener(new AddProjectButtonAction());
        addStandardLibraryButton.addActionListener(new AddStandardLibraryButtonAction());
        addLibraryButton.addActionListener(new AddLibraryButtonAction());
        addLibraryOption.addActionListener(new AddLinkerOptionButtonAction());
        addLibraryFileButton.addActionListener(new AddLibraryFileButtonAction());
        //
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        outerPanel.add(myListEditorPanel, gridBagConstraints);
        instructionsTextArea.setBackground(instructionPanel.getBackground());
        setPreferredSize(new java.awt.Dimension(700, 450));

        env.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
        env.addPropertyChangeListener(this);
    }

    public void setInstructionsText(String txt) {
        instructionsTextArea.setText(txt);
    }

    public void setListData(List<LibraryItem> data) {
        myListEditorPanel.setListData(data);
    }

    public List<LibraryItem> getListData() {
        return myListEditorPanel.getListData();
    }

    private ArrayList<LibraryItem> getPropertyValue() throws IllegalStateException {
        return new ArrayList<LibraryItem>(getListData());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName()) && evt.getNewValue() == PropertyEnv.STATE_VALID) {
            editor.setValue(getPropertyValue());
        }
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("Libraries"); // NOI18N
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        outerPanel = new javax.swing.JPanel();
        instructionPanel = new javax.swing.JPanel();
        instructionsTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(323, 223));
        outerPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(outerPanel, gridBagConstraints);

        instructionPanel.setLayout(new java.awt.GridBagLayout());

        instructionsTextArea.setEditable(false);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        instructionPanel.add(instructionsTextArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 12);
        add(instructionPanel, gridBagConstraints);
    }

    private class MyListEditorPanel extends TableEditorPanel {

        public MyListEditorPanel(List<LibraryItem> objects, JButton[] extraButtons) {
            super(objects, extraButtons, baseDir);
            getAddButton().setVisible(false);
            //getCopyButton().setVisible(false);
            getEditButton().setVisible(false);
            getDefaultButton().setVisible(false);
        }

        @Override
        public String getListLabelText() {
            return getString("LIBRARIES_AND_OPTIONS_TXT");
        }

        @Override
        public char getListLabelMnemonic() {
            return getString("LIBRARIES_AND_OPTIONS_MN").charAt(0);
        }

        @Override
        public LibraryItem copyAction(LibraryItem o) {
            LibraryItem libraryItem = o;
            return libraryItem.clone();
        }
    }

    private final class AddProjectButtonAction implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            MakeArtifact[] artifacts = MakeArtifactChooser.showDialog(MakeArtifactChooser.ArtifactType.LIBRARY, project, myListEditorPanel);
            if (artifacts != null) {
                for (int i = 0; i < artifacts.length; i++) {
                    String location = ProjectSupport.toProperPath(baseDir, artifacts[i].getProjectLocation(), project);
                    String workingdir = ProjectSupport.toProperPath(baseDir, artifacts[i].getWorkingDirectory(), project);
                    location = CndPathUtilitities.normalizeSlashes(location);
                    workingdir = CndPathUtilitities.normalizeSlashes(workingdir);
                    artifacts[i].setProjectLocation(location);
                    artifacts[i].setWorkingDirectory(workingdir);
                    myListEditorPanel.addObjectAction(new LibraryItem.ProjectItem(artifacts[i]));
                }
            }
        }
    }

    private final class AddStandardLibraryButtonAction implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            StdLibPanel stdLibPanel = new StdLibPanel(Platforms.getPlatform(conf.getDevelopmentHost().getBuildPlatform()).getStandardLibraries());
            DialogDescriptor dialogDescriptor = new DialogDescriptor(stdLibPanel, getString("SELECT_STATNDARD_LIBRARY_DIALOG_TITLE"));
            DialogDisplayer.getDefault().notify(dialogDescriptor);
            if (dialogDescriptor.getValue() != DialogDescriptor.OK_OPTION) {
                return;
            }
            LibraryItem.StdLibItem[] libs = stdLibPanel.getSelectedStdLibs();
            for (int i = 0; i < libs.length; i++) {
                myListEditorPanel.addObjectAction(libs[i]);
            }
        }
    }

    private final class AddLibraryButtonAction implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            String seed = null;
            if (FileChooser.getCurrentChooserFile() != null) {
                seed = FileChooser.getCurrentChooserFile().getPath();
            }
            if (seed == null) {
                seed = baseDir;
            }
            FileFilter[] filters;
            if (Utilities.isWindows()) {
                filters = new FileFilter[]{
                            FileFilterFactory.getPeStaticLibraryFileFilter(),
                            FileFilterFactory.getPeDynamicLibraryFileFilter()};
            } else if (Utilities.getOperatingSystem() == Utilities.OS_MAC) {
                filters = new FileFilter[]{
                            FileFilterFactory.getElfStaticLibraryFileFilter(),
                            FileFilterFactory.getMacOSXDynamicLibraryFileFilter()};
            } else {
                filters = new FileFilter[]{
                            FileFilterFactory.getElfStaticLibraryFileFilter(),
                            FileFilterFactory.getElfDynamicLibraryFileFilter()};
            }
            FileChooser fileChooser = new FileChooser(getString("SELECT_LIBRARY_CHOOSER_TITLE"), getString("SELECT_CHOOSER_BUTTON"), JFileChooser.FILES_ONLY, filters, seed, true);
            int ret = fileChooser.showOpenDialog(myListEditorPanel);
            if (ret == JFileChooser.CANCEL_OPTION) {
                return;
            }
            String libName = fileChooser.getSelectedFile().getName();
            if (libName.startsWith("lib")) // NOI18N
            {
                libName = libName.substring(3);
            }
            if (libName.endsWith(".so") || // NOI18N
                    libName.endsWith(".dll") || // NOI18N
                    libName.endsWith(".dylib") || // NOI18N
                    libName.endsWith(".lib") || // NOI18N
                    libName.endsWith(".a")) { // NOI18N
                int i = libName.lastIndexOf('.');
                libName = libName.substring(0, i);
            }
            myListEditorPanel.addObjectAction(new LibraryItem.LibItem(libName));
        }
    }

    private final class AddLibraryFileButtonAction implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            String seed = null;
            if (FileChooser.getCurrentChooserFile() != null) {
                seed = FileChooser.getCurrentChooserFile().getPath();
            }
            if (seed == null) {
                seed = baseDir;
            }
            FileFilter[] filters;
            if (Utilities.isWindows()) {
                filters = new FileFilter[]{
                            FileFilterFactory.getPeStaticLibraryFileFilter(),
                            FileFilterFactory.getPeDynamicLibraryFileFilter()};
            } else if (Utilities.getOperatingSystem() == Utilities.OS_MAC) {
                filters = new FileFilter[]{
                            FileFilterFactory.getElfStaticLibraryFileFilter(),
                            FileFilterFactory.getMacOSXDynamicLibraryFileFilter()};
            } else {
                filters = new FileFilter[]{
                            FileFilterFactory.getElfStaticLibraryFileFilter(),
                            FileFilterFactory.getElfDynamicLibraryFileFilter()};
            }
            FileChooser fileChooser = new FileChooser(getString("SELECT_LIBRARY_FILE_CHOOSER_TITLE"), getString("SELECT_CHOOSER_BUTTON"), JFileChooser.FILES_ONLY, filters, seed, true);
            PathPanel pathPanel = new PathPanel();
            fileChooser.setAccessory(pathPanel);
            int ret = fileChooser.showOpenDialog(null);
            if (ret == JFileChooser.CANCEL_OPTION) {
                return;
            }
            // FIXUP: why are baseDir UNIX path when remote?
            String path = ProjectSupport.toProperPath(baseDir, fileChooser.getSelectedFile().getPath(), project);
            path = CndPathUtilitities.normalizeSlashes(path);
            myListEditorPanel.addObjectAction(new LibraryItem.LibFileItem(path));
        }
    }

    private final class AddLinkerOptionButtonAction implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            LibraryOptionPanel libraryOptionPanel = new LibraryOptionPanel();
            DialogDescriptor dialogDescriptor = new DialogDescriptor(libraryOptionPanel, getString("SELECT_OPTION_DIALOG_TITLE"));
            DialogDisplayer.getDefault().notify(dialogDescriptor);
            if (dialogDescriptor.getValue() != DialogDescriptor.OK_OPTION) {
                return;
            }
            if (libraryOptionPanel.getOption(conf).trim().length() == 0) {
                return;
            }
            myListEditorPanel.addObjectAction(new LibraryItem.OptionItem(libraryOptionPanel.getOption(conf)));
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel instructionPanel;
    private javax.swing.JTextArea instructionsTextArea;
    private javax.swing.JPanel outerPanel;
    // End of variables declaration//GEN-END:variables
    /** Look up i18n strings here */
    private static ResourceBundle bundle;

    private static String getString(String s) {
        if (bundle == null) {
            bundle = NbBundle.getBundle(LibrariesPanel.class);
        }
        return bundle.getString(s);
    }
}
