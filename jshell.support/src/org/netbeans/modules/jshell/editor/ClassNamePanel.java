/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.editor;

import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import javax.lang.model.SourceVersion;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.UIResource;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.NotificationLineSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author sdedic
 */
class ClassNamePanel extends javax.swing.JPanel implements DocumentListener {
    private RequestProcessor RP = new RequestProcessor(ClassNamePanel.class);
    
    private Project             project;
    private final FileObject    anchor;
    private final RequestProcessor.Task checkTask = RP.create(this::delayedCheck, true);
    private ChangeListener listener;
    private NotificationLineSupport notifier;
    
    /**
     * Creates new form ClassNamePanel
     */
    public ClassNamePanel(Project project, FileObject anchor, String initialName) {
        this.project = project;
        this.anchor = anchor;
        initComponents();
        
        Project openProjects[] = OpenProjects.getDefault().getOpenProjects();
        Arrays.sort( openProjects, new ProjectByDisplayNameComparator());
        DefaultComboBoxModel projectsModel = new DefaultComboBoxModel( openProjects );
        projectSelector.setModel( projectsModel );                
        if (project != null) {
            projectSelector.setSelectedItem( project );
            projectSelector.setEnabled(false);
            projectLabel.setEnabled(false);
        } else if (projectsModel.getSize() > 0) {
            this.project = (Project)projectsModel.getElementAt(0);
            projectsModel.setSelectedItem(this.project);
        }
        projectSelector.setRenderer(new ProjectCellRenderer());
        locationSelect.setRenderer(new GroupCellRenderer());
        packageSelect.setRenderer(PackageView.listRenderer());
        
        updateRoots();
        updatePackages();
        
        selectInitialPackage();
        
        ActionListener al = this::actionPerformed;
        locationSelect.addActionListener(al);
        packageSelect.addActionListener(al);
        packageSelect.getEditor().addActionListener(al);
        projectSelector.addActionListener(al);
        className.getDocument().addDocumentListener(this);
        
        if (initialName != null) {
            className.setText(initialName);
        }
    }
    
    public void setNotifier(NotificationLineSupport support) {
        this.notifier = support;
    }
    
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::initDisplay);
    }
    
    private void initDisplay() {
        checkErrors();
        className.requestFocus();
    }
    
    public void addChangeListener(ChangeListener l) {
        this.listener = l;
    }
    
    public void removeChangeListener(ChangeListener l) {
        this.listener = null;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        packageLabel = new javax.swing.JLabel();
        classLabel = new javax.swing.JLabel();
        className = new javax.swing.JTextField();
        packageSelect = new javax.swing.JComboBox();
        locationLabel = new javax.swing.JLabel();
        locationSelect = new javax.swing.JComboBox();
        message = new javax.swing.JLabel();
        projectSelector = new javax.swing.JComboBox<>();
        projectLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        org.openide.awt.Mnemonics.setLocalizedText(packageLabel, org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.packageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(classLabel, org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.classLabel.text")); // NOI18N

        className.setText(org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.className.text")); // NOI18N
        className.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classNameActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.locationLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(message, org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.message.text")); // NOI18N

        projectLabel.setLabelFor(projectSelector);
        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(ClassNamePanel.class, "ClassNamePanel.projectLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(projectLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                            .addComponent(projectSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(locationLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(locationSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(packageLabel)
                                .addComponent(classLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(43, 43, 43)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(packageSelect, 0, 297, Short.MAX_VALUE)
                                .addComponent(className))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addComponent(message, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLabel)
                    .addComponent(projectSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(message)
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(locationSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(locationLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(packageSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(packageLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(classLabel)
                            .addComponent(className, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void classNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_classNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField className;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JComboBox locationSelect;
    private javax.swing.JLabel message;
    private javax.swing.JLabel packageLabel;
    private javax.swing.JComboBox packageSelect;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JComboBox<String> projectSelector;
    // End of variables declaration//GEN-END:variables


    private SourceGroup[] groups;
    

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkTask.schedule(200);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkTask.schedule(200);
    }
    
    private void delayedCheck() {
        if (isVisible()) {
            checkErrors();
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) { }
    
    
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == projectSelector) {
            projectSelected(e);
        } else if (e.getSource() == locationSelect) {
            updatePackages();
            checkErrors();
        } else if (e.getSource() == packageSelect) {
            checkErrors();
        } else {
            // combo box was edited; schedule a delay
        }
    }
    
    private void projectSelected(ActionEvent e) {
        Project p = (Project)projectSelector.getSelectedItem();
        if (p == null) {
            return;
        }
        this.project = p;
        updateRoots();
        updatePackages();
        checkErrors();
    }
    
    private void enableDisable() {
        boolean enableLocation = project != null;
        boolean enablePackage = enableLocation && locationSelect.getSelectedItem() != null;
        boolean enableClass = enablePackage && packageSelect.getSelectedItem() != null;
        
        locationSelect.setEnabled(enableLocation);
        locationLabel.setEnabled(enableLocation);
        packageSelect.setEnabled(enablePackage);
        packageLabel.setEnabled(enablePackage);
        className.setEnabled(enableClass);
        classLabel.setEnabled(enableClass);
    }
    
    private void updateRoots() {
        enableDisable();
        if (project == null) {
            return;
        }
        Sources sources = ProjectUtils.getSources(project);
        groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        // XXX why?? This is probably wrong. If the project has no Java groups,
        // you cannot move anything into it.
        if (groups.length == 0) {
            groups = sources.getSourceGroups( Sources.TYPE_GENERIC ); 
        }
        
        int preselectedItem = 0;
        if (anchor != null) {
            for( int i = 0; i < groups.length; i++ ) {
                if (groups[i].contains(anchor)) {
                    preselectedItem = i;
                    break;
                }
            }
        }
                
        // Setup comboboxes 
        locationSelect.setModel(new DefaultComboBoxModel(groups));
        if(groups.length > 0) {
            locationSelect.setSelectedIndex(preselectedItem);
        }
    }

    private void updatePackages() {
        enableDisable();
        SourceGroup g = (SourceGroup) locationSelect.getSelectedItem();
        packageSelect.setModel(g != null
                ? PackageView.createListView(g)
                : new DefaultComboBoxModel());
    }
    
    private void reportError(String err) {
        notifier.setErrorMessage(err);
        if (listener != null) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    @NbBundle.Messages({
       "ERR_ClassnameNotSpecified=Error: class name has not been specified",
       "# {0} - class name",
       "ERR_ClassnameInvalid=Error: {0} is not a valid Java class name",
       "ERR_PackagDoesNotExist=Error: existing package must be selected",
       "ERR_NoSourceGroups=Error: no place to generate class, set up a Source folder",
       "INFO_ClassAlreadyExists=Note: The class already exists. It will be overwritten.",
       "ERR_ProjectNotSelected=Project is not selected"
    })
    private void checkErrors() {
        enableDisable();
        if (project == null) {
            reportError(Bundle.ERR_ProjectNotSelected());
            return;
        }
        if (packageSelect.getItemCount() == 0) {
            reportError(Bundle.ERR_NoSourceGroups());
            return;
        }
        if (packageSelect.getSelectedItem() == null) {
            reportError(Bundle.ERR_PackagDoesNotExist());
            return;
        }
        String n = className.getText().trim();
        if (n.isEmpty()) {
            reportError(Bundle.ERR_ClassnameNotSpecified());
            return;
        }
        
        if (!SourceVersion.isName(n)) {
            reportError(Bundle.ERR_ClassnameInvalid(n));
            return;
        }
        
        notifier.clearMessages();

        FileObject folder = getTarget();
        if (folder != null) {
            n = getClassName();
            FileObject existing = folder.getFileObject(n, "java"); // NOI18N
            if (existing != null) {
                notifier.setInformationMessage(Bundle.INFO_ClassAlreadyExists());
            }
        }
        
        if (listener != null) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }
    
    public String getClassName() {
        String n = className.getText().trim();
        if (n.isEmpty() || !SourceVersion.isName(n)) {
            return null;
        }
        return n;
    }
    
    public FileObject getRootFolder() {
        Object item = locationSelect.getSelectedItem();
        if (item == null) {
            return null;
        }
        return ((SourceGroup) item).getRootFolder();
    }
    
    public String getPackageName() {
        String packageName = packageSelect.getSelectedItem().toString();
        return packageName; // NOI18N
    }
    
    public FileObject getTarget() {
        FileObject root = getRootFolder();
        if (root == null) {
            return null;
        }
        String pkg = getPackageName().replace(".", "/");
        return root.getFileObject(pkg);
    }

    private void selectInitialPackage() {
        FileObject root = getRootFolder();
        if (root == null) {
            return;
        }
        if (anchor == null || !FileUtil.isParentOf(root, anchor)) {
            return;
        }
        String rp = FileUtil.getRelativePath(root, anchor.getParent()).replace("/", ".");
        for (int i = 0; i < packageSelect.getItemCount(); i++) {
            Object o = packageSelect.getItemAt(i);
            if (rp.equals(o.toString())) {
                packageSelect.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public boolean hasErrors() {
        return getTarget() == null || 
               getClassName() == null;
    }


    private abstract static class BaseCellRenderer extends JLabel implements ListCellRenderer, UIResource {
        
        public BaseCellRenderer () {
            setOpaque(true);
        }
        
        // #89393: GTK needs name to render cell renderer "natively"
        @Override
        public String getName() {
            String name = super.getName();
            return name == null ? "ComboBox.renderer" : name;  // NOI18N
        }
    }
    
    /** Groups combo renderer, used also in MoveMembersPanel */
    static class GroupCellRenderer extends BaseCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
            // #89393: GTK needs name to render cell renderer "natively"
            setName("ComboBox.listRenderer"); // NOI18N
            
            if (value instanceof SourceGroup) {
                SourceGroup g = (SourceGroup) value;
                setText(g.getDisplayName());
                setIcon(g.getIcon(false));
            } else {
                setText(""); // NOI18N
                setIcon(null);
            }
            
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());             
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
    }
    

    /** Projects combo renderer, used also in MoveMembersPanel */
    static class ProjectCellRenderer extends BaseCellRenderer {
        
        @Override
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
            // #89393: GTK needs name to render cell renderer "natively"
            setName("ComboBox.listRenderer"); // NOI18N
            
            if ( value != null ) {
                ProjectInformation pi = ProjectUtils.getInformation((Project)value);
                setText(pi.getDisplayName());
                setIcon(pi.getIcon());
            }
            
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());             
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
    }
    
    //Copy/pasted from OpenProjectList
    //remove this code as soon as #68827 is fixed.
    static class ProjectByDisplayNameComparator implements Comparator {
        
        private static Comparator COLLATOR = Collator.getInstance();
        
        @Override
        public int compare(Object o1, Object o2) {
            
            if ( !( o1 instanceof Project ) ) {
                return 1;
            }
            if ( !( o2 instanceof Project ) ) {
                return -1;
            }
            
            Project p1 = (Project)o1;
            Project p2 = (Project)o2;
            
            return COLLATOR.compare(ProjectUtils.getInformation(p1).getDisplayName(), ProjectUtils.getInformation(p2).getDisplayName());
        }
    }    
}
