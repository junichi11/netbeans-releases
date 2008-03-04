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
/*
 * CPPropertiesPanelVisualPanel.java
 *
 * Created on October 8, 2003
 */

package org.netbeans.modules.j2ee.sun.ide.sunresources.wizards;

import org.openide.util.NbBundle;
import javax.swing.table.TableColumn;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelListener;
import javax.swing.ListSelectionModel;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.sunresources.beans.Field;
import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;
import org.netbeans.modules.j2ee.sun.sunresources.beans.WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroupHelper;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldHelper;

/** A single panel for a wizard - the GUI portion.
 *
 * @author nityad
 */
public class CPPropertiesPanelVisualPanel extends javax.swing.JPanel implements WizardConstants, TableModelListener{
    
    /** The wizard panel descriptor associated with this GUI panel.
     * If you need to fire state changes or something similar, you can
     * use this handle to do so.
     */
    private final CPPropertiesPanelPanel panel;
    private ResourceConfigHelper helper;
    private FieldGroup generalGroup, propertiesGroup;
    private Field dsField, typeField;
        
    /** Create the wizard panel and set up some basic properties. */
    public CPPropertiesPanelVisualPanel(CPPropertiesPanelPanel panel, ResourceConfigHelper helper, Wizard wizardInfo) {
        this.panel = panel;
        this.helper = helper;
        this.tableModel = new PropertiesTableModel(this.helper.getData());     
        
        initComponents();
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(CPPropertiesPanelVisualPanel.class, "TITLE_ConnPoolWizardPanel_properties")); //NOI18N
        
        this.generalGroup = FieldGroupHelper.getFieldGroup(wizardInfo, __General); 
        this.propertiesGroup = FieldGroupHelper.getFieldGroup(wizardInfo, __Properties);  
        this.dsField = FieldHelper.getField(generalGroup, __DatasourceClassname);  
      
        setPropTableCellEditor();
        this.tableModel.addTableModelListener(this);
        this.jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        String dsClassName = this.helper.getData().getString(__DatasourceClassname);
        if (dsClassName.length() == 0)
            dsClassName = FieldHelper.getDefaultValue(dsField);
        classNameField.setText(dsClassName);
        
        this.typeField = FieldHelper.getField(generalGroup, __ResType);         
        String resType = this.helper.getData().getString(__ResType);
        if (resType.length() == 0) {
            resType = FieldHelper.getDefaultValue(typeField);
        }    
        resTypeCombo.setSelectedItem(resType);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        classNameField = new javax.swing.JTextField();
        classNameLabel = new javax.swing.JLabel();
        resTypeLabel = new javax.swing.JLabel();
        descField = new javax.swing.JTextField();
        descLabel = new javax.swing.JLabel();
        resTypeCombo = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new PropertiesTable();
        tableButtonsPane = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        classNameField.setText(this.helper.getData().getString(__DatasourceClassname));
        classNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classNameFieldActionPerformed(evt);
            }
        });
        classNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                classNameFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(classNameField, gridBagConstraints);
        classNameField.getAccessibleContext().setAccessibleName(this.helper.getData().getString(__DatasourceClassname));
        classNameField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_datasource-classnamefieldA11yDesc"));

        classNameLabel.setDisplayedMnemonic(bundle.getString("LBL_datasource-classname_Mnemonic").charAt(0));
        classNameLabel.setLabelFor(classNameField);
        classNameLabel.setText(bundle.getString("LBL_datasource-classname"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(classNameLabel, gridBagConstraints);
        classNameLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_datasource-classname"));

        resTypeLabel.setDisplayedMnemonic(bundle.getString("LBL_res-type_Mnemonic").charAt(0));
        resTypeLabel.setLabelFor(resTypeCombo);
        resTypeLabel.setText(bundle.getString("LBL_res-type"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(resTypeLabel, gridBagConstraints);
        resTypeLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_res-type"));
        resTypeLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_res-typefieldA11yDesc"));

        descField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descFieldActionPerformed(evt);
            }
        });
        descField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                descFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(descField, gridBagConstraints);

        descLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_description_Mnemonic").charAt(0));
        descLabel.setLabelFor(descField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle"); // NOI18N
        descLabel.setText(bundle.getString("LBL_description")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 49;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(descLabel, gridBagConstraints);
        descLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_description")); // NOI18N
        descLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_description")); // NOI18N

        resTypeCombo.setModel(getResourceTypeComboBoxModel());
        resTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resTypeComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(resTypeCombo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
        jPanel1.getAccessibleContext().setAccessibleName(bundle.getString("TITLE_ConnPoolWizardPanel_properties")); // NOI18N
        jPanel1.getAccessibleContext().setAccessibleDescription(bundle.getString("CPPropertyPanel_Description")); // NOI18N

        jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 17));

        jTable1.setModel(tableModel);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getAccessibleContext().setAccessibleName(bundle.getString("LBL_properties")); // NOI18N
        jTable1.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_propTableCommon_A11yDesc")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 11);
        add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getAccessibleContext().setAccessibleName(bundle.getString("LBL_properties")); // NOI18N
        jScrollPane1.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_propTableCommon_A11yDesc")); // NOI18N

        tableButtonsPane.setLayout(new java.awt.GridBagLayout());

        addButton.setMnemonic(bundle.getString("LBL_Add_Mnemonic").charAt(0));
        addButton.setText(bundle.getString("LBL_Add"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        tableButtonsPane.add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_AddButtonA11yDesc"));

        removeButton.setMnemonic(bundle.getString("LBL_Remove_Mnemonic").charAt(0));
        removeButton.setText(bundle.getString("LBL_Remove"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        tableButtonsPane.add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Remove"));
        removeButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_RemoveButtonA11yDesc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipady = 100;
        add(tableButtonsPane, gridBagConstraints);
        tableButtonsPane.getAccessibleContext().setAccessibleName(bundle.getString("LBL_properties")); // NOI18N
        tableButtonsPane.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_propTableCommon_A11yDesc")); // NOI18N

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setText(bundle.getString("CPPropertyPanel_Description"));
        descriptionTextArea.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 0);
        add(descriptionTextArea, gridBagConstraints);
        descriptionTextArea.getAccessibleContext().setAccessibleName(bundle.getString("CPPropertyPanel_Description"));
        descriptionTextArea.getAccessibleContext().setAccessibleDescription(bundle.getString("CPPropertyPanel_Description"));

        jLabel1.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle").getString("LBL_properties_Mnemonic").charAt(0));
        jLabel1.setLabelFor(jTable1);
        jLabel1.setText(bundle.getString("LBL_properties")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jLabel1, gridBagConstraints);

        getAccessibleContext().setAccessibleName(bundle.getString("TITLE_ConnPoolWizardPanel_properties")); // NOI18N
        getAccessibleContext().setAccessibleDescription(bundle.getString("CPPropertyPanel_Description")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void classNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_classNameFieldKeyReleased
        // Add your handling code here:
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__DatasourceClassname);
        String newValue = classNameField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__DatasourceClassname, newValue);
            //fireChange(this);
        }
        this.panel.fireChangeEvent(this);
    }//GEN-LAST:event_classNameFieldKeyReleased

    private void descFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descFieldKeyReleased
        // Add your handling code here:
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__Description);
        String newValue = descField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__Description, newValue);
            //fireChange(this);
        }
        this.panel.fireChangeEvent(this);
    }//GEN-LAST:event_descFieldKeyReleased

    private void descFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descFieldActionPerformed
        // Add your handling code here:
        ResourceConfigData data = this.helper.getData();
        String item = descField.getText();
        Object value = data.get(__Description);  
        if (!item.equals((String)value)){
            data.setString(__Description, item);  
        }
        
        this.panel.fireChangeEvent(this);
        if((this.getRootPane().getDefaultButton() != null) && (this.getRootPane().getDefaultButton().isEnabled())){
            this.getRootPane().getDefaultButton().doClick();
        }
    }//GEN-LAST:event_descFieldActionPerformed

    private void classNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classNameFieldActionPerformed
        // Add your handling code here:
        ResourceConfigData data = this.helper.getData();
        String item = classNameField.getText();
        Object value = data.get(__DatasourceClassname);  
        if (value == null)
            value = FieldHelper.getDefaultValue(dsField);
        if (!item.equals((String)value))
            data.setString(__DatasourceClassname, item);  
        
        this.panel.fireChangeEvent(this);
        if((this.getRootPane().getDefaultButton() != null) && (this.getRootPane().getDefaultButton().isEnabled())){
            this.getRootPane().getDefaultButton().doClick();
        }
    }//GEN-LAST:event_classNameFieldActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // Add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            //Fix for bug#4958730 - value overwrites into next row
            jTable1.editingStopped(new ChangeEvent (this));
            this.helper.getData().removeProperty(selectedRow);
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Add your handling code here:
        //Fix for bug#4958730 - value overwrites into next row
        jTable1.editingStopped(new ChangeEvent (this));
        ResourceConfigData data = this.helper.getData();
        data.addProperty(new NameValuePair()); 
        tableModel.fireTableDataChanged();
    }//GEN-LAST:event_addButtonActionPerformed

    private void resTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resTypeComboActionPerformed
        String itemValue = (String)resTypeCombo.getSelectedItem();
        ResourceConfigData data = this.helper.getData();
        data.setString(__ResType, itemValue);
    }//GEN-LAST:event_resTypeComboActionPerformed

    public void tableChanged(javax.swing.event.TableModelEvent e) {
         setPropTableCellEditor();
         this.panel.fireChangeEvent(this);
    }    
    
    public void setPropTableCellEditor() {
        javax.swing.JComboBox propNameComboBox = new javax.swing.JComboBox();
        String[] remainingProperties = FieldHelper.getRemainingFieldNames(propertiesGroup, this.helper.getData().getPropertyNames());
        for (int i = 0; i < remainingProperties.length; i++) 
            propNameComboBox.addItem(remainingProperties[i]);
        
        this.nameColumn = jTable1.getColumnModel().getColumn(0);
        propNameComboBox.setEditable(true);
        this.nameColumn.setCellEditor(new javax.swing.DefaultCellEditor(propNameComboBox));
        this.valueColumn = jTable1.getColumnModel().getColumn(1);
                
        javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(new javax.swing.JTextField());
        editor.setClickCountToStart(1);
        this.valueColumn.setCellEditor(editor);
    }
    
     public void refreshFields() {
        ResourceConfigData data = this.helper.getData();
        String item = classNameField.getText();
        String val = data.getString(__DatasourceClassname);
        if (!item.equals(val))
            classNameField.setText(val);
        
        item = (String)resTypeCombo.getSelectedItem();
        val = data.getString(__ResType);  
        if (!item.equals(val)) {
            resTypeCombo.setSelectedItem(val);
        }    
        ((PropertiesTableModel)jTable1.getModel()).setData(this.helper.getData());
    }
     
    public void setInitialFocus(){
        new setFocus(classNameField);
    }
    
    private String[] resourceTypes = {
        WizardConstants.__Type_Datasource,    
        WizardConstants.__Type_XADatasource, 
        WizardConstants.__Type_ConnectionPoolDataSource
    };
    
    private ComboBoxModel getResourceTypeComboBoxModel() {
        ComboBoxModel model = new javax.swing.DefaultComboBoxModel(resourceTypes);
        return model;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField classNameField;
    private javax.swing.JLabel classNameLabel;
    private javax.swing.JTextField descField;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox resTypeCombo;
    private javax.swing.JLabel resTypeLabel;
    private javax.swing.JPanel tableButtonsPane;
    // End of variables declaration//GEN-END:variables
    private ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.sunresources.wizards.Bundle"); //NOI18N
    private PropertiesTableModel tableModel;
    private TableColumn nameColumn;
    private TableColumn valueColumn;
    
}

