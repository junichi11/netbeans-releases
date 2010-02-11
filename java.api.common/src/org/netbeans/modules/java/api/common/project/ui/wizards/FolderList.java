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

package org.netbeans.modules.java.api.common.project.ui.wizards;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.java.api.common.project.ui.customizer.SourceRootsUi;
import org.netbeans.spi.java.project.support.JavadocAndSourceRootDetection;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;


/**
 * List of source/test roots
 * @author tzezula
 */
public final class FolderList extends javax.swing.JPanel {

    public static final String PROP_FILES = "files";    //NOI18N
    public static final String PROP_LAST_USED_DIR = "lastUsedDir";  //NOI18N

    private String fcMessage;
    private File projectFolder;
    private File lastUsedFolder;
    private FolderList relatedFolderList;

    /** Creates new form FolderList */
    public FolderList (String label, char mnemonic, String accessibleDesc, String fcMessage,
                       char addButtonMnemonic, String addButtonAccessibleDesc,
                       char removeButtonMnemonic,String removeButtonAccessibleDesc) {
        this.fcMessage = fcMessage;
        initComponents();
        this.jLabel1.setText(label);
        this.jLabel1.setDisplayedMnemonic(mnemonic);
        this.roots.getAccessibleContext().setAccessibleName(accessibleDesc);
        this.roots.setCellRenderer(new Renderer());
        this.roots.setModel (new DefaultListModel());
        this.roots.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removeButton.setEnabled(roots.getSelectedIndices().length != 0);
                }
            }
        });
        this.roots.setDragEnabled(true);
        this.roots.setDropMode(DropMode.INSERT);
        this.roots.setTransferHandler(new DNDHandle());
        this.addButton.getAccessibleContext().setAccessibleDescription(addButtonAccessibleDesc);
        this.addButton.setMnemonic (addButtonMnemonic);        
        this.removeButton.getAccessibleContext().setAccessibleDescription(removeButtonAccessibleDesc);
        this.removeButton.setMnemonic (removeButtonMnemonic);
        this.removeButton.setEnabled(false);
    }
    
    public void setRelatedFolderList (FolderList relatedFolderList) {
        this.relatedFolderList = relatedFolderList;
    }    

    public File[] getFiles () {
        Object[] files = ((DefaultListModel)this.roots.getModel()).toArray();
        File[] result = new File[files.length];
        System.arraycopy(files, 0, result, 0, files.length);
        return result;
    }

    public void setProjectFolder (File projectFolder) {
        this.projectFolder = projectFolder;
    }

    public void setFiles (File[] files) {
        DefaultListModel model = ((DefaultListModel)this.roots.getModel());
        model.clear();
        for (int i=0; i<files.length; i++) {
            model.addElement (files[i]);
        }
        if (files.length>0) {
            this.roots.setSelectedIndex(0);
        }
    }

    public void setLastUsedDir (File lastUsedDir) {
        if (this.lastUsedFolder == null ? lastUsedDir != null : !this.lastUsedFolder.equals(lastUsedDir)) {
            File oldValue = this.lastUsedFolder;
            this.lastUsedFolder = lastUsedDir;
            this.firePropertyChange(PROP_LAST_USED_DIR, oldValue, this.lastUsedFolder);
        }
    }
    
    public File getLastUsedDir () {
        return this.lastUsedFolder;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        roots = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(roots);
        jLabel1.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N

        jScrollPane1.setViewportView(roots);
        roots.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_FolderList")); // NOI18N
        roots.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        addButton.setText(NbBundle.getMessage(FolderList.class, "CTL_AddFolder")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N

        removeButton.setText(NbBundle.getMessage(FolderList.class, "CTL_RemoveFolder")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FolderList.class, "ACSD_NA")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Object[] selection = this.roots.getSelectedValues ();
        for (int i=0; i<selection.length; i++) {
            ((DefaultListModel)this.roots.getModel()).removeElement (selection[i]);
        }
        this.firePropertyChange(PROP_FILES, null, null);
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(this.fcMessage);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        if (this.lastUsedFolder != null && this.lastUsedFolder.isDirectory()) {
            chooser.setCurrentDirectory (this.lastUsedFolder);
        }
        else if (this.projectFolder != null && this.projectFolder.isDirectory()) {
            chooser.setCurrentDirectory (this.projectFolder);
        }
        if (chooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
            final File[] files = normalizeFiles(chooser.getSelectedFiles());
            final AtomicReference<List<File>> toAddRef = new AtomicReference<List<File>>();
            class ScanTask implements ProgressRunnable<Void>, Cancellable {
                private final AtomicBoolean cancel = new AtomicBoolean();
                @Override
                public Void run(final ProgressHandle handle) {
                    final List<File> toAdd = new ArrayList<File>();
                    for (File file : files) {
                        if (cancel.get()) {
                            return null;
                        }
                        final Collection<? extends FileObject> detectedRoots = JavadocAndSourceRootDetection.findSourceRoots(FileUtil.toFileObject(file),cancel);
                        if (detectedRoots.isEmpty()) {
                            toAdd.add(file);
                        }
                        else {
                            for (FileObject detectedRoot : detectedRoots) {
                                toAdd.add (FileUtil.toFile(detectedRoot));
                            }
                        }
                    }
                    toAddRef.set(toAdd);    //threading: Needs to be a tail call!
                    return null;
                }

                @Override
                public boolean cancel() {
                    cancel.set(true);
                    return true;
                }
            };
            final ScanTask task = new ScanTask();
            ProgressUtils.showProgressDialogAndRun(task, NbBundle.getMessage(FolderList.class, "TXT_SearchingSourceRoots"), false);
            final List<File> toAdd = toAddRef.get();
            final File[] toAddArr = toAdd == null ? files : toAdd.toArray(new File[toAdd.size()]);
            int[] indecesToSelect = new int[toAddArr.length];
            DefaultListModel model = (DefaultListModel)this.roots.getModel();
            Set<File> invalidRoots = new HashSet<File>();
            File[] relatedFolders = this.relatedFolderList == null ?
                new File[0] : this.relatedFolderList.getFiles();
            for (int i=0, index=model.size(); i<toAddArr.length; i++) {
                File normalizedFile = toAddArr[i];
                if (!isValidRoot(normalizedFile, relatedFolders, this.projectFolder)) {
                    invalidRoots.add (normalizedFile);
                }
                else {
                    int pos = model.indexOf (normalizedFile);
                    if (pos == -1) {
                        model.addElement (normalizedFile);
                        indecesToSelect[i] = index;
                    }
                    else {
                        indecesToSelect[i] = pos;
                    }
                    index++;
                }
            }
            this.roots.setSelectedIndices(indecesToSelect);
            this.firePropertyChange(PROP_FILES, null, null);
            File cd = chooser.getCurrentDirectory();
            if (cd != null) {
                this.setLastUsedDir(FileUtil.normalizeFile(cd));
            }
            if (invalidRoots.size()>0) {
                SourceRootsUi.showIllegalRootsDialog(invalidRoots);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private static File[] normalizeFiles(final File... files) {
        for (int i=0; i< files.length; i++) {
            files[i] = FileUtil.normalizeFile(files[i]);
        }
        return files;
    }

    public static boolean isValidRoot (File file, File[] relatedRoots, File projectFolder) {
        Project p;
        if ((p = FileOwnerQuery.getOwner(file.toURI()))!=null 
            && !file.getAbsolutePath().startsWith(projectFolder.getAbsolutePath()+File.separatorChar)) {
            final Sources sources = p.getLookup().lookup(Sources.class);
            if (sources == null) {
                return false;
            }
            final SourceGroup[] sourceGroups = sources.getSourceGroups(Sources.TYPE_GENERIC);
            final SourceGroup[] javaGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
            final SourceGroup[] groups = new SourceGroup [sourceGroups.length + javaGroups.length];
            System.arraycopy(sourceGroups,0,groups,0,sourceGroups.length);
            System.arraycopy(javaGroups,0,groups,sourceGroups.length,javaGroups.length);
            final FileObject projectDirectory = p.getProjectDirectory();
            final FileObject fileObject = FileUtil.toFileObject(file);
            if (projectDirectory == null || fileObject == null) {
                return false;
            }
            for (int i = 0; i< groups.length; i++) {
                final FileObject sgRoot = groups[i].getRootFolder();
                if (fileObject.equals(sgRoot)) {
                    return false;
                }
                if (!projectDirectory.equals(sgRoot) && FileUtil.isParentOf(sgRoot, fileObject)) {
                    return false;
                }
            }
            return true;
        }
        else if (contains (file, relatedRoots)) {
            return false;
        }
        return true;
    }
    
    private static boolean contains (File folder, File[] roots) {
        String path = folder.getAbsolutePath ();
        for (int i=0; i<roots.length; i++) {
            String rootPath = roots[i].getAbsolutePath();
            if (rootPath.equals (path) || path.startsWith (rootPath + File.separatorChar)) {
                return true;
            }
        }
        return false;
    }

    private static class Renderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            File f = (File) value;
            String message = f.getAbsolutePath();
            Component result = super.getListCellRendererComponent(list, message, index, isSelected, cellHasFocus);
            return result;
        }        
    }

    private static class FileListTransferable implements Transferable {

        private final List<? extends File> data;

        public FileListTransferable(final List<? extends File> data) {
            data.getClass();
            this.data = data;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor == flavor;
        }

        @Override
        public List<? extends File> getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

    }

    private static class DNDHandle extends TransferHandler {

        private int[] indices = new int[0];

        @Override
        public int getSourceActions(JComponent comp) {
            return MOVE;
        }

        @Override
        public Transferable createTransferable(JComponent comp) {
            final JList list = (JList)comp;
            indices = list.getSelectedIndices();
            if (indices.length == 0) {
                return null;
            }
            return new FileListTransferable(safeCopy(list.getSelectedValues(),File.class));
        }

        private static<T> List<? extends T> safeCopy(Object[] data, Class<T> clazz) {
            final List<T> result = new ArrayList<T>(data.length);
            for (Object d : data) {
                result.add(clazz.cast(d));
            }
            return result;
        }

        @Override
        public void exportDone(JComponent comp, Transferable trans, int action) {
            if (action == MOVE) {
                final JList from = (JList) comp;
                final DefaultListModel model = (DefaultListModel) from.getModel();
                for (int i=indices.length-1; i>=0; i--) {
                    model.removeElementAt(indices[i]);
                }
            }
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {

            if (!support.isDrop()) {
                return false;
            }

            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }


            boolean actionSupported = (MOVE & support.getSourceDropActions()) == MOVE;
            if (!actionSupported) {
                return false;
            }

            support.setDropAction(MOVE);
            return true;
        }


        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
            int index = Math.max(0, dl.getIndex());
            List<? extends File> data;
            try {
                data = (List<? extends File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (java.io.IOException e) {
                return false;
            }
            JList list = (JList)support.getComponent();
            DefaultListModel model = (DefaultListModel)list.getModel();
            int[] indices = new int[data.size()];
            for (int i=0; i< data.size(); i++,index++) {
                model.insertElementAt(data.get(i), index);
                indices[i]=index;
                updateIndexes(index);
            }
            Rectangle rect = list.getCellBounds(indices[0], indices[indices.length-1]);
            list.scrollRectToVisible(rect);
            list.setSelectedIndices(indices);
            list.requestFocusInWindow();
            return true;
        }

        private void updateIndexes(int index) {
            for (int i=0; i< indices.length; i++) {
                if (index<indices[i]) {
                    indices[i]++;
                }
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JList roots;
    // End of variables declaration//GEN-END:variables
    
}
