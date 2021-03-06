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

package org.netbeans.modules.web.struts.wizards;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.struts.StrutsConfigUtilities;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.xml.sax.InputSource;

public class ActionPanelVisual extends javax.swing.JPanel implements HelpCtx.Provider, ActionListener, DocumentListener {
    
    static final String DEFAULT_ACTION = "org.apache.struts.action.Action"; //NOI18N
    static final String DISPATCH_ACTION = "org.apache.struts.actions.DispatchAction"; //NOI18N
    static final String MAPPING_DISPATCH_ACTION = "org.apache.struts.actions.MappingDispatchAction"; //NOI18N
    static final String LOOKUP_DISPATCH_ACTION = "org.apache.struts.actions.LookupDispatchAction"; //NOI18N

    private static final String[] SUPERCLASS_LIST = {DEFAULT_ACTION, DISPATCH_ACTION, MAPPING_DISPATCH_ACTION, LOOKUP_DISPATCH_ACTION};
    private final WebModule webModule;
    private final List/*<ChangeListener>*/ listeners = new ArrayList();
    
    /** Creates new form ActionPanelVisual */
    public ActionPanelVisual(ActionPanel panel) {    
        initComponents();
        jComboBoxSuperclass.setModel(new javax.swing.DefaultComboBoxModel(SUPERCLASS_LIST));
        jComboBoxSuperclass.getEditor().addActionListener( this );
        Project proj = panel.getProject();
        webModule = WebModule.getWebModule(proj.getProjectDirectory());
        if (webModule != null){
            String[] configFiles = StrutsConfigUtilities.getConfigFiles(webModule.getDeploymentDescriptor());
            jComboBoxConfigFile.setModel(new javax.swing.DefaultComboBoxModel(configFiles));
        }
        jComboBoxConfigFile.addActionListener( this );
        jTextFieldPath.getDocument().addDocumentListener( this );
        
        Component superclassEditor = jComboBoxSuperclass.getEditor().getEditorComponent();
        if ( superclassEditor instanceof javax.swing.JTextField ) {
            ((javax.swing.JTextField)superclassEditor).getDocument().addDocumentListener( this );
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelSuperclass = new javax.swing.JLabel();
        jComboBoxSuperclass = new javax.swing.JComboBox();
        jLabelConfigFile = new javax.swing.JLabel();
        jComboBoxConfigFile = new javax.swing.JComboBox();
        jLabelPath = new javax.swing.JLabel();
        jTextFieldPath = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jLabelSuperclass.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_Superlass_mnem").charAt(0));
        jLabelSuperclass.setText(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_Superclass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabelSuperclass, gridBagConstraints);

        jComboBoxSuperclass.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jComboBoxSuperclass, gridBagConstraints);
        jComboBoxSuperclass.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_jComboBoxSuperclass"));

        jLabelConfigFile.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_ConfigFile_mnem").charAt(0));
        jLabelConfigFile.setLabelFor(jComboBoxConfigFile);
        jLabelConfigFile.setText(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_ConfigFile"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelConfigFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jComboBoxConfigFile, gridBagConstraints);
        jComboBoxConfigFile.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_jComboBoxConfigFile"));

        jLabelPath.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_ActionPath_mnem").charAt(0));
        jLabelPath.setLabelFor(jTextFieldPath);
        jLabelPath.setText(org.openide.util.NbBundle.getMessage(ActionPanelVisual.class, "LBL_ActionPath"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabelPath, gridBagConstraints);

        jTextFieldPath.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jTextFieldPath, gridBagConstraints);
        jTextFieldPath.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/struts/wizards/Bundle").getString("ACSD_jTextFieldPath"));

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxConfigFile;
    private javax.swing.JComboBox jComboBoxSuperclass;
    private javax.swing.JLabel jLabelConfigFile;
    private javax.swing.JLabel jLabelPath;
    private javax.swing.JLabel jLabelSuperclass;
    private javax.swing.JTextField jTextFieldPath;
    // End of variables declaration//GEN-END:variables
 
    boolean valid(WizardDescriptor wizardDescriptor) {
        // check super class
        String superclass = (String) jComboBoxSuperclass.getEditor().getItem();
        if (superclass == null || superclass.trim().equals("")){
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,                            //NOI18N             
                    NbBundle.getMessage(ActionPanelVisual.class, "MSG_NoSuperClassSelected"));  //NOI18N
            return false;
        }
        
        // check configuration file
        String configFile = (String) jComboBoxConfigFile.getSelectedItem();
        if (configFile == null || configFile.trim().equals("")){
            // Should dislpay only warning. We should allow to create action outside module. #68034
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,                            //NOI18N
                    NbBundle.getMessage(ActionPanelVisual.class, "MSG_NoConfFileSelectedForAction"));//NOI18N
            // don't check the action path, when the configuration file is not needed. 
            return true;
        } else if (webModule != null) {
            FileObject fo = webModule.getDocumentBase().getFileObject(configFile);
            if (fo == null || !fo.isValid()) {
                // Check for valid path for struts-config.xml #123610
                wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                    NbBundle.getMessage(ActionPanelVisual.class, "MSG_NoConfFileSelectedForAction"));   //NOI18N
                return false;
            } else {
                try {
                    // Check for valid struts-config.xml document #123610
                    ValidateXMLSupport validator = new ValidateXMLSupport(new InputSource(fo.getInputStream()));
                    if (!validator.validateXML(null)) {
                        wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                            NbBundle.getMessage(ActionPanelVisual.class, "MSG_ConfFileWithErrors"));   //NOI18N
                        return false;
                    }
                } catch (FileNotFoundException ex) {
                    wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,
                        NbBundle.getMessage(ActionPanelVisual.class, "MSG_NoConfFileSelectedForAction"));   //NOI18N
                    return false;
                }
            }
        }

        // check Action path
        String actionPath = jTextFieldPath.getText();
        if (actionPath == null || actionPath.trim().equals("") || actionPath.trim().equals("/")){//NOI18N
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,                            //NOI18N
                    NbBundle.getMessage(ActionPanelVisual.class, "MSG_WrongActionPath"));       //NOI18N
            return false;
        }
        return true;
    }
    
    void read (WizardDescriptor settings) {
    }

    void store(WizardDescriptor settings) {
        settings.putProperty(WizardProperties.ACTION_PATH, jTextFieldPath.getText()); //NOI18N
        settings.putProperty(WizardProperties.ACTION_SUPERCLASS, (String)jComboBoxSuperclass.getSelectedItem()); //NOI18N
        settings.putProperty(WizardProperties.ACTION_CONFIG_FILE, (String)jComboBoxConfigFile.getSelectedItem()); //NOI18N
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(ActionPanelVisual.class);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
         fireChange();
    }
    
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(e);
        }
    }
    
    // DocumentListener implementation -----------------------------------------
    
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        fireChange();        
    }    
    
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
    
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        changedUpdate( e );
    }
}
