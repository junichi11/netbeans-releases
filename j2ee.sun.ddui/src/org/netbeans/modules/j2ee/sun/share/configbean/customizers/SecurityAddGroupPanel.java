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
/*
 * SecurityAddGroupPanel.java
 *
 * Created on April 12, 2006, 11:57 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.netbeans.modules.j2ee.sun.share.Constants;
import org.netbeans.modules.j2ee.sun.share.SecurityMasterListModel;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.HelpContext;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.InputDialog;
import org.openide.util.NbBundle;

/**
 *
 * @author Peter Williams
 */
public class SecurityAddGroupPanel extends JPanel implements ListSelectionListener {
    
	private final ResourceBundle customizerBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.Bundle"); // NOI18N
    
	private final GroupTableModel groupModel;

    private Dimension initialPreferredSize;
    
	private String groupName;
    
    /** group names table & model
     */
    private JTable existingGroupsTable;
    private SecurityMasterListModel existingGroupsModel;
    
    /** 
     * Creates new form SecurityAddGroupPanel
     */
    public SecurityAddGroupPanel(GroupTableModel gml) {
        groupModel = gml;
        groupName = null;
        
        initComponents();
        initUserComponents();
        initFields();
    }
    
	protected String getGrouplName() {
		return groupName;
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLblGroupName = new javax.swing.JLabel();
        jTxtGroupName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(customizerBundle.getString("LBL_GroupEntryDesc")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 5);
        add(jLabel1, gridBagConstraints);

        jLblGroupName.setLabelFor(jTxtGroupName);
        jLblGroupName.setText(customizerBundle.getString("LBL_GroupName_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 0);
        add(jLblGroupName, gridBagConstraints);

        jTxtGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtGroupNameKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 11);
        add(jTxtGroupName, gridBagConstraints);
        jTxtGroupName.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_GroupName")); // NOI18N
        jTxtGroupName.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_GroupName")); // NOI18N

        jLabel2.setText(customizerBundle.getString("LBL_GroupTableDescription")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 5);
        add(jLabel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtGroupNameKeyReleased
        groupName = jTxtGroupName.getText();
		firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_jTxtGroupNameKeyReleased
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLblGroupName;
    private javax.swing.JTextField jTxtGroupName;
    // End of variables declaration//GEN-END:variables
    
    private void initUserComponents() {
		/* Save preferred size before adding table.  We have our own width and
		 * will add a constant of our own choosing for the height in init(), below.
		 */
		initialPreferredSize = getPreferredSize();
        
        /** Add table after preferred size is saved.
         */
        existingGroupsTable = new FixedHeightJTable();
        existingGroupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        existingGroupsTable.getSelectionModel().addListSelectionListener(this);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(existingGroupsTable);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 11, 11, 11);
        add(scrollPane, gridBagConstraints);
        
        getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_AddGroupName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_AddGroupName")); // NOI18N
    }
    
    private void initFields() {
        // Initialize table data model
        existingGroupsModel = SecurityMasterListModel.getGroupMasterModel();
        existingGroupsTable.setModel(existingGroupsModel);
        
        // Initialize text fields.
        updateTextFields();
        
        // Set preferred size (just height really) to be something reasonable (because
        // the default is unnecessarily tall).
        setPreferredSize(new Dimension(initialPreferredSize.width, initialPreferredSize.height + 148));
    }
    
    private void updateTextFields() {
        jTxtGroupName.setText(groupName);
    }

    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel selModel = existingGroupsTable.getSelectionModel();
        if(!selModel.getValueIsAdjusting()) {
            int selectedRow = existingGroupsTable.getSelectedRow();
            if(selectedRow != -1) {
                Object entry = existingGroupsModel.getRow(selectedRow);
                if(entry instanceof String) {
                    groupName = (String) entry;
                    updateTextFields();
                    firePropertyChange(Constants.USER_DATA_CHANGED, null, null);                    
                }
            }
        }
    }

    Collection getErrors() {
        // Validate what the user typed in as a valid group name
        ArrayList errors = new ArrayList();
        String newGroupName = getGrouplName();

        /** New name must not be blank (for add or edit version)
         */
        if(!Utils.notEmpty(newGroupName)) {
            errors.add(customizerBundle.getString("ERR_BlankGroupName")); // NOI18N
        }

        /** Duplicate checking:				 
         *    Add operations always need to check for duplicates against
         *    the entire list.
         */
        if(newGroupName != null && groupModel.contains(newGroupName)) {
            errors.add(MessageFormat.format(customizerBundle.getString("ERR_GroupExists"), new Object [] { newGroupName })); // NOI18N
        }

        return errors;
    }
    
    private void commit() {
        String newGroupName = getGrouplName();

        // Add to security model of this descriptor
        groupModel.addElement(newGroupName);

        // Also add to global mapping list if not already present.
        if(!existingGroupsModel.contains(newGroupName)) {
            existingGroupsModel.addElement(newGroupName);
        }
    }
    
    /** Puts up an 'Add...' dialog, doing validation against the supplied model,
     *  and ultimately updating the data model if the user hits <OK> and clears
     *  any errors.
     *
     * @param parent JPanel that is the parent of this popup - used for centering and sizing.
     * @param theModel The particular Security model instance we're updating.
     */
    static void addGroupName(JPanel parent, GroupTableModel model) {
        SecurityAddGroupPanel addGroupPanel = new SecurityAddGroupPanel(model);
        addGroupPanel.displayDialog(parent, NbBundle.getBundle(
            "org.netbeans.modules.j2ee.sun.share.configbean.customizers.Bundle").getString("TITLE_AddGroup"), // NOI18N 
            HelpContext.HELP_SECURITY_NEW_GROUP); // NOI18N
    }
    
    private void displayDialog(JPanel parent, String title, String helpId) {
        BetterInputDialog dialog = new BetterInputDialog(parent, title, helpId, this);

        do {
            int dialogChoice = dialog.display();

            if(dialogChoice == dialog.CANCEL_OPTION) {
                break;
            }

            if(dialogChoice == dialog.OK_OPTION) {
                Collection errors = getErrors();

                if(dialog.hasErrors()) {
                    // !PW is this even necessary w/ new validation model?
                    dialog.showErrors();
                } else {
                    commit();
                }
            }
        } while(dialog.hasErrors());
    }    

    private static class BetterInputDialog extends InputDialog {
        private final SecurityAddGroupPanel dialogPanel;
        private final String panelHelpId;

        public BetterInputDialog(JPanel parent, String title, String helpId, SecurityAddGroupPanel childPanel) {
            super(parent, title);

            dialogPanel = childPanel;
            panelHelpId = helpId;

            dialogPanel.setPreferredSize(new Dimension(parent.getWidth()*3/4, 
                dialogPanel.getPreferredSize().height));

            this.getAccessibleContext().setAccessibleName(dialogPanel.getAccessibleContext().getAccessibleName());
            this.getAccessibleContext().setAccessibleDescription(dialogPanel.getAccessibleContext().getAccessibleDescription());

            getContentPane().add(childPanel, BorderLayout.CENTER);
            addListeners();
            pack();
            setLocationInside(parent);
            handleErrorDisplay();
        }

        private void addListeners() {
            dialogPanel.addPropertyChangeListener(Constants.USER_DATA_CHANGED, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    handleErrorDisplay();
                }
            });
        }

        private void handleErrorDisplay() {
            ArrayList errors = new ArrayList();
            errors.addAll(dialogPanel.getErrors());
            setErrors(errors);
        }

        protected String getHelpId() {
            return panelHelpId;
        }
    }
}
