/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.wizard.loader;

import java.awt.Color;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.WizardDescriptor;

/**
 * the second panel in loaders wizard.
 *
 * @author Milos Kleint
 */
final class NameAndLocationPanel extends BasicWizardIterator.Panel {
    private NewLoaderIterator.DataModel data;
    
    /** Creates new NameAndLocationPanel */
    public NameAndLocationPanel(final WizardDescriptor setting, final NewLoaderIterator.DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        Color lblBgr = UIManager.getColor("Label.background"); // NOI18N
        putClientProperty("NewFileWizard_Title", getMessage("LBL_LoaderWizardTitle"));
        modifiedFilesValue.setBackground(lblBgr);
        createdFilesValue.setBackground(lblBgr);
        
        DocumentListener dListener = new UIUtil.DocumentAdapter() {
            public void insertUpdate(DocumentEvent e) {
                checkValidity();
                updateData();
            }
        };
        txtPrefix.getDocument().addDocumentListener(dListener);
        txtIcon.getDocument().addDocumentListener(dListener);
        
        if (comPackageName.getEditor().getEditorComponent() instanceof JTextField) {
            JTextField txt = (JTextField)comPackageName.getEditor().getEditorComponent();
            txt.getDocument().addDocumentListener(dListener);
        }
        
    }
    
    protected void storeToDataModel() {
        updateData();
    }
    
    private void updateData() {
        data.setPackageName(comPackageName.getEditor().getItem().toString());
        String icon = txtIcon.getText().trim();
        data.setIconPath(icon.length() == 0 ? (String)null : icon);
        data.setPrefix(txtPrefix.getText().trim());
        NewLoaderIterator.generateFileChanges(data);
        createdFilesValue.setText(generateText(data.getCreatedModifiedFiles().getCreatedPaths()));
        modifiedFilesValue.setText(generateText(data.getCreatedModifiedFiles().getModifiedPaths()));
    }
    
    protected void readFromDataModel() {
        checkValidity();
        txtPrefix.setText(data.getPrefix());
        txtIcon.setText(data.getPrefix());
    }
    
    private static JComboBox createComboBox(SourceGroup srcRoot) {
        JComboBox packagesComboBox = new JComboBox(PackageView.createListView(srcRoot));
        packagesComboBox.setRenderer(PackageView.listRenderer());
        return packagesComboBox;
    }
    
    protected String getPanelName() {
        return getMessage("LBL_NameLocation_Title");
    }
    
    private void checkValidity() {
        //TODO:
        setValid(Boolean.TRUE);
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPrefix = new javax.swing.JLabel();
        txtPrefix = new javax.swing.JTextField();
        lblIcon = new javax.swing.JLabel();
        txtIcon = new javax.swing.JTextField();
        btnIcon = new javax.swing.JButton();
        lblProjectName = new javax.swing.JLabel();
        txtProjectName = new JTextField(ProjectUtils.getInformation(this.data.getProject()).getDisplayName());
        lblPackageName = new javax.swing.JLabel();
        comPackageName = this.createComboBox(this.data.getSourceRootGroup());
        createdFiles = new javax.swing.JLabel();
        modifiedFiles = new javax.swing.JLabel();
        filler = new javax.swing.JLabel();
        createdFilesValue = new javax.swing.JTextArea();
        modifiedFilesValue = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        lblPrefix.setLabelFor(txtPrefix);
        org.openide.awt.Mnemonics.setLocalizedText(lblPrefix, org.openide.util.NbBundle.getMessage(NameAndLocationPanel.class, "LBL_Prefix"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 6, 12);
        add(lblPrefix, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 6, 0);
        add(txtPrefix, gridBagConstraints);

        lblIcon.setLabelFor(txtIcon);
        org.openide.awt.Mnemonics.setLocalizedText(lblIcon, org.openide.util.NbBundle.getMessage(NameAndLocationPanel.class, "LBL_Icon"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(lblIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(txtIcon, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnIcon, org.openide.util.NbBundle.getMessage(NameAndLocationPanel.class, "LBL_Icon_Browse"));
        btnIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIconActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(btnIcon, gridBagConstraints);

        lblProjectName.setLabelFor(txtProjectName);
        org.openide.awt.Mnemonics.setLocalizedText(lblProjectName, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/librarydescriptor/Bundle").getString("LBL_ProjectName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 6, 12);
        add(lblProjectName, gridBagConstraints);

        txtProjectName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 6, 0);
        add(txtProjectName, gridBagConstraints);

        lblPackageName.setLabelFor(comPackageName);
        org.openide.awt.Mnemonics.setLocalizedText(lblPackageName, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/librarydescriptor/Bundle").getString("LBL_PackageName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(lblPackageName, gridBagConstraints);

        comPackageName.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(comPackageName, gridBagConstraints);

        createdFiles.setLabelFor(createdFilesValue);
        org.openide.awt.Mnemonics.setLocalizedText(createdFiles, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/librarydescriptor/Bundle").getString("LBL_CreatedFiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(36, 0, 6, 12);
        add(createdFiles, gridBagConstraints);

        modifiedFiles.setLabelFor(modifiedFilesValue);
        org.openide.awt.Mnemonics.setLocalizedText(modifiedFiles, java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/wizard/librarydescriptor/Bundle").getString("LBL_ModifiedFiles"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(modifiedFiles, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

        createdFilesValue.setColumns(20);
        createdFilesValue.setRows(5);
        createdFilesValue.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(36, 0, 6, 0);
        add(createdFilesValue, gridBagConstraints);

        modifiedFilesValue.setColumns(20);
        modifiedFilesValue.setRows(5);
        modifiedFilesValue.setToolTipText("modifiedFilesValue");
        modifiedFilesValue.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(modifiedFilesValue, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void btnIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIconActionPerformed
        JFileChooser chooser = new JFileChooser();
//        chooser.setSelectedFile();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new GifPngFilter());
        chooser.setFileView(new FileView() {
            public Icon getIcon(File f) {
                // Show icons right in the chooser, to make it easier to find the right one.
                if (f.getName().endsWith(".gif") || f.getName().endsWith(".png")) { // NOI18N
                    Icon icon = new ImageIcon(f.getAbsolutePath());
                    if (icon.getIconWidth() == 16 && icon.getIconHeight() == 16) {
                        return icon;
                    }
                }
                return null;
            }
        });
        int ret = chooser.showDialog(this, getMessage("LBL_Select"));
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file =  chooser.getSelectedFile();
            txtIcon.setText(file.getAbsolutePath());
        }        
        
    }//GEN-LAST:event_btnIconActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIcon;
    private javax.swing.JComboBox comPackageName;
    private javax.swing.JLabel createdFiles;
    private javax.swing.JTextArea createdFilesValue;
    private javax.swing.JLabel filler;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblPackageName;
    private javax.swing.JLabel lblPrefix;
    private javax.swing.JLabel lblProjectName;
    private javax.swing.JLabel modifiedFiles;
    private javax.swing.JTextArea modifiedFilesValue;
    private javax.swing.JTextField txtIcon;
    private javax.swing.JTextField txtPrefix;
    private javax.swing.JTextField txtProjectName;
    // End of variables declaration//GEN-END:variables
    
    private static String generateText(String[] relPaths) {
        StringBuffer sb = new StringBuffer();
        if (relPaths.length > 0) {
            for (int i = 0; i < relPaths.length; i++) {
                if (i > 0) {
                    sb.append("\n");//NOI18N
                }
                sb.append(relPaths[i]);
            }
        }
        
        return sb.toString();
    }
    
    
    private static final class GifPngFilter extends FileFilter {
             public boolean accept(File pathname) {
                 return  pathname.isDirectory() || pathname.getName().toLowerCase().endsWith("gif") || pathname.getName().toLowerCase().endsWith("png");
             }
             public String getDescription() {
                 return "*.gif, *.png";
             }
        
    }    
}
