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

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.PlatformsCustomizer;
import org.netbeans.modules.apisupport.project.ui.platform.NbPlatformCustomizer;
import org.netbeans.modules.apisupport.project.ui.platform.PlatformComponentFactory;
import org.netbeans.modules.apisupport.project.universe.NbPlatform;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Represents <em>Libraries</em> panel in NetBeans Module customizer.
 *
 * @author mkrauskopf
 */
public class CustomizerLibraries extends NbPropertyPanel.Single {
    
    /** Creates new form CustomizerLibraries */
    public CustomizerLibraries(final SingleModuleProperties props) {
        super(props, CustomizerLibraries.class);
        initComponents();
        initAccessibility();
        refresh();
        dependencyList.setCellRenderer(CustomizerComponentFactory.getDependencyCellRenderer(false));
        platformValue.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // set new platform
                    getProperties().setActivePlatform((NbPlatform) platformValue.getSelectedItem());
                    // refresh dependencies list
                    dependencyList.setModel(getProperties().getDependenciesListModel());
                    updateEnabled();
                }
            }
        });
        dependencyList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateEnabled();
                }
            }
        });
        javaPlatformCombo.setRenderer(JavaPlatformComponentFactory.javaPlatformListCellRenderer());
        javaPlatformCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // set new platform
                    getProperties().setActiveJavaPlatform((JavaPlatform) javaPlatformCombo.getSelectedItem());
                }
            }
        });
    }
    
    void refresh() {
        refreshJavaPlatforms();
        refreshPlatforms();
        platformValue.setEnabled(getProperties().isStandalone());
        managePlafsButton.setEnabled(getProperties().isStandalone());
        boolean javaEnabled = getProperties().isStandalone() || getProperties().isNetBeansOrg();
        javaPlatformCombo.setEnabled(javaEnabled);
        javaPlatformButton.setEnabled(javaEnabled);
        updateEnabled();
        reqTokenList.setModel(getProperties().getRequiredTokenListModel());
        dependencyList.setModel(getProperties().getDependenciesListModel());
    }
    
    private void refreshJavaPlatforms() {
        javaPlatformCombo.setModel(JavaPlatformComponentFactory.javaPlatformListModel());
        javaPlatformCombo.setSelectedItem(getProperties().getActiveJavaPlatform());
    }
    
    private void refreshPlatforms() {
        platformValue.setModel(new PlatformComponentFactory.NbPlatformListModel()); // refresh
        platformValue.setSelectedItem(getProperties().getActivePlatform());
        platformValue.requestFocusInWindow();
    }
    
    private void updateEnabled() {
        // if there is no selection disable edit/remove buttons
        boolean enabled = getProperties().isActivePlatformValid() && dependencyList.getSelectedIndex() != -1;
        editDepButton.setEnabled(enabled);
        removeDepButton.setEnabled(enabled);
        addDepButton.setEnabled(getProperties().isActivePlatformValid());
    }
    
    private CustomizerComponentFactory.DependencyListModel getDepListModel() {
        return (CustomizerComponentFactory.DependencyListModel) dependencyList.getModel();
    }
    
    private String getMessage(String key) {
        return NbBundle.getMessage(CustomizerLibraries.class, key);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        modDepLabel = new javax.swing.JLabel();
        depButtonPanel = new javax.swing.JPanel();
        addDepButton = new javax.swing.JButton();
        removeDepButton = new javax.swing.JButton();
        space1 = new javax.swing.JLabel();
        editDepButton = new javax.swing.JButton();
        dependencySP = new javax.swing.JScrollPane();
        dependencyList = new javax.swing.JList();
        platformsPanel = new javax.swing.JPanel();
        platformValue = org.netbeans.modules.apisupport.project.ui.platform.PlatformComponentFactory.getNbPlatformsComboxBox();
        platform = new javax.swing.JLabel();
        managePlafsButton = new javax.swing.JButton();
        javaPlatformLabel = new javax.swing.JLabel();
        javaPlatformCombo = new javax.swing.JComboBox();
        javaPlatformButton = new javax.swing.JButton();
        reqTokens = new javax.swing.JLabel();
        reqTokenSP = new javax.swing.JScrollPane();
        reqTokenList = new javax.swing.JList();
        tokenButtonPanel = new javax.swing.JPanel();
        addTokenButton = new javax.swing.JButton();
        removeTokenButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        modDepLabel.setLabelFor(dependencyList);
        org.openide.awt.Mnemonics.setLocalizedText(modDepLabel, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "LBL_ModuleDependencies"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 2, 0);
        add(modDepLabel, gridBagConstraints);

        depButtonPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(addDepButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_AddButton"));
        addDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModuleDependency(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        depButtonPanel.add(addDepButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeDepButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_RemoveButton"));
        removeDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModuleDependency(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        depButtonPanel.add(removeDepButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        depButtonPanel.add(space1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(editDepButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_EditButton"));
        editDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editModuleDependency(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        depButtonPanel.add(editDepButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(depButtonPanel, gridBagConstraints);

        dependencySP.setViewportView(dependencyList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(dependencySP, gridBagConstraints);

        platformsPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        platformsPanel.add(platformValue, gridBagConstraints);

        platform.setLabelFor(platformValue);
        org.openide.awt.Mnemonics.setLocalizedText(platform, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "LBL_NetBeansPlatform"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        platformsPanel.add(platform, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(managePlafsButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_ManagePlatform"));
        managePlafsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managePlatforms(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        platformsPanel.add(managePlafsButton, gridBagConstraints);

        javaPlatformLabel.setLabelFor(javaPlatformCombo);
        org.openide.awt.Mnemonics.setLocalizedText(javaPlatformLabel, NbBundle.getMessage(CustomizerLibraries.class, "LBL_Java_Platform"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        platformsPanel.add(javaPlatformLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        platformsPanel.add(javaPlatformCombo, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(javaPlatformButton, NbBundle.getMessage(CustomizerLibraries.class, "LBL_Manage_Java_Platforms"));
        javaPlatformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                javaPlatformButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        platformsPanel.add(javaPlatformButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(platformsPanel, gridBagConstraints);

        reqTokens.setLabelFor(reqTokenList);
        org.openide.awt.Mnemonics.setLocalizedText(reqTokens, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "LBL_RequiredTokens"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 12);
        add(reqTokens, gridBagConstraints);

        reqTokenSP.setViewportView(reqTokenList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(reqTokenSP, gridBagConstraints);

        tokenButtonPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(addTokenButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_AddButton_NoMnem"));
        addTokenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToken(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        tokenButtonPanel.add(addTokenButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeTokenButton, org.openide.util.NbBundle.getMessage(CustomizerLibraries.class, "CTL_RemoveButton_NoMnem"));
        removeTokenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeToken(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        tokenButtonPanel.add(removeTokenButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(tokenButtonPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void javaPlatformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_javaPlatformButtonActionPerformed
        PlatformsCustomizer.showCustomizer((JavaPlatform) javaPlatformCombo.getSelectedItem());
        refreshJavaPlatforms();
    }//GEN-LAST:event_javaPlatformButtonActionPerformed
    
    private void removeToken(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeToken
        CustomizerComponentFactory.RequiredTokenListModel model = (CustomizerComponentFactory.RequiredTokenListModel) reqTokenList.getModel();
        Object[] selected = reqTokenList.getSelectedValues();
        for (int i = 0; i < selected.length; i++) {
            model.removeToken((String) selected[i]);
        }
        if (model.getSize() > 0) {
            reqTokenList.setSelectedIndex(0);
        }
        reqTokenList.requestFocusInWindow();
    }//GEN-LAST:event_removeToken
    
    private void addToken(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToken
        // create add panel
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panel.setLayout(new BorderLayout(0, 2));
        JList tokenList = new JList(getProperties().getAllTokens());
        JScrollPane tokenListSP = new JScrollPane(tokenList);
        JLabel provTokensTxt = new JLabel();
        provTokensTxt.setLabelFor(tokenList);
        Mnemonics.setLocalizedText(provTokensTxt, getMessage("LBL_ProvidedTokens_T"));
        panel.getAccessibleContext().setAccessibleDescription(getMessage("ACS_ProvidedTokensTitle"));
        tokenList.getAccessibleContext().setAccessibleDescription(getMessage("ACS_LBL_ProvidedTokens"));
        tokenListSP.getVerticalScrollBar().getAccessibleContext().setAccessibleName(getMessage("ACS_CTL_ProvidedTokensVerticalScroll"));
        tokenListSP.getVerticalScrollBar().getAccessibleContext().setAccessibleDescription(getMessage("ACSD_CTL_ProvidedTokensVerticalScroll"));
        tokenListSP.getHorizontalScrollBar().getAccessibleContext().setAccessibleName(getMessage("ACS_CTL_ProvidedTokensHorizontalScroll"));
        tokenListSP.getHorizontalScrollBar().getAccessibleContext().setAccessibleDescription(getMessage("ACSD_CTL_ProvidedTokensHorizontalScroll"));
        
        panel.add(provTokensTxt, BorderLayout.NORTH);
        panel.add(tokenListSP, BorderLayout.CENTER);
        
        DialogDescriptor descriptor = new DialogDescriptor(panel,
                getMessage("LBL_ProvidedTokens_NoMnem"));
        Dialog d = DialogDisplayer.getDefault().createDialog(descriptor);
        d.setVisible(true);
        d.dispose();
        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
            Object[] selected = tokenList.getSelectedValues();
            CustomizerComponentFactory.RequiredTokenListModel model = (CustomizerComponentFactory.RequiredTokenListModel) reqTokenList.getModel();
            for (int i = 0; i < selected.length; i++) {
                model.addToken((String) selected[i]);
            }
        }
        reqTokenList.requestFocusInWindow();
    }//GEN-LAST:event_addToken
    
    private void managePlatforms(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managePlatforms
        NbPlatformCustomizer.showCustomizer();
        refreshPlatforms();
    }//GEN-LAST:event_managePlatforms
    
    private void editModuleDependency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editModuleDependency
        ModuleDependency origDep = getDepListModel().getDependency(
                dependencyList.getSelectedIndex());
        ModuleDependency editedDep = getDepListModel().findEdited(origDep);
        EditDependencyPanel editPanel = new EditDependencyPanel(
                editedDep == null ? origDep : editedDep, getProperties().getActivePlatform());
        DialogDescriptor descriptor = new DialogDescriptor(editPanel,
                getMessage("CTL_EditModuleDependencyTitle"));
        descriptor.setHelpCtx(new HelpCtx(EditDependencyPanel.class));
        Dialog d = DialogDisplayer.getDefault().createDialog(descriptor);
        d.setVisible(true);
        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
            getDepListModel().editDependency(origDep, editPanel.getEditedDependency());
        }
        d.dispose();
        dependencyList.requestFocusInWindow();
    }//GEN-LAST:event_editModuleDependency
    
    private void removeModuleDependency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeModuleDependency
        getDepListModel().removeDependencies(Arrays.asList(dependencyList.getSelectedValues()));
        if (dependencyList.getModel().getSize() > 0) {
            dependencyList.setSelectedIndex(0);
        }
        dependencyList.requestFocusInWindow();
    }//GEN-LAST:event_removeModuleDependency
    
    private void addModuleDependency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModuleDependency
        final AddModulePanel addPanel = new AddModulePanel(getProperties());
        final DialogDescriptor descriptor = new DialogDescriptor(addPanel,
                getMessage("CTL_AddModuleDependencyTitle"));
        descriptor.setHelpCtx(new HelpCtx(AddModulePanel.class));
        descriptor.setClosingOptions(new Object[0]);
        final Dialog d = DialogDisplayer.getDefault().createDialog(descriptor);
        descriptor.setButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (DialogDescriptor.OK_OPTION.equals(e.getSource()) &&
                        addPanel.getSelectedDependencies().length == 0) {
                    return;
                }
                d.setVisible(false);
                d.dispose();
            }
        });
        d.setVisible(true);
        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
            ModuleDependency[] newDeps = addPanel.getSelectedDependencies();
            for (int i = 0; i < newDeps.length; i++) {
                if (!getDepListModel().getDependencies().contains(newDeps[i])) {
                    getDepListModel().addDependency(newDeps[i]);
                }
                dependencyList.setSelectedValue(newDeps[i], true);
            }
        }
        d.dispose();
        dependencyList.requestFocusInWindow();
    }//GEN-LAST:event_addModuleDependency
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDepButton;
    private javax.swing.JButton addTokenButton;
    private javax.swing.JPanel depButtonPanel;
    private javax.swing.JList dependencyList;
    private javax.swing.JScrollPane dependencySP;
    private javax.swing.JButton editDepButton;
    private javax.swing.JButton javaPlatformButton;
    private javax.swing.JComboBox javaPlatformCombo;
    private javax.swing.JLabel javaPlatformLabel;
    private javax.swing.JButton managePlafsButton;
    private javax.swing.JLabel modDepLabel;
    private javax.swing.JLabel platform;
    private javax.swing.JComboBox platformValue;
    private javax.swing.JPanel platformsPanel;
    private javax.swing.JButton removeDepButton;
    private javax.swing.JButton removeTokenButton;
    private javax.swing.JList reqTokenList;
    private javax.swing.JScrollPane reqTokenSP;
    private javax.swing.JLabel reqTokens;
    private javax.swing.JLabel space1;
    private javax.swing.JPanel tokenButtonPanel;
    // End of variables declaration//GEN-END:variables
    
    private void initAccessibility() {
        addTokenButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_SrcLevelValue"));
        dependencyList.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_DependencyList"));
        editDepButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_EditDepButton"));
        removeDepButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_RemoveDepButton"));
        removeTokenButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_RemoveTokenButton"));
        addDepButton.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_AddDepButton"));
        reqTokenList.getAccessibleContext().setAccessibleDescription(getMessage("ACSD_ReqTokenList"));
    }

}
