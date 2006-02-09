/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.openide.util.Utilities;

/** Dialog that can be used in create from template.
*
* @author  Jaroslav Tulach, David Strupl, Jiri Rechtacek
*/
final class NewObjectPanel extends javax.swing.JPanel implements DocumentListener {

    /** listener to changes in panel */
    private ChangeListener listener;
    
    /**  Creates new form NewObjectPanel */
    public NewObjectPanel() {
        initComponents ();

        setName(getString("LAB_NewObjectPanelName")); // NOI18N

        setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(8, 8, 8, 8)));

        // registers itself to listen to changes in the content of document
        newObjectName.getDocument().addDocumentListener(this);
        newObjectName.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(NewObjectPanel.class);
        jLabel1.setDisplayedMnemonic(bundle.getString("CTL_NewObjectName_Mnemonic").charAt(0)); // NOI18N
        
        setNewObjectName(""); // NOI18N

        putClientProperty("WizardPanel_contentData", new String[] { getName() }); //NOI18N
        putClientProperty("WizardPanel_contentSelectedIndex", new Integer(0)); //NOI18N
        
        newObjectName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_NewObjectName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_NewObjectPanel")); // NOI18N
    }

    /** Preffered size */
    public java.awt.Dimension getPreferredSize() {
        return TemplateWizard.PREF_DIM;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        namePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        newObjectName = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout(0, 8));

        setPreferredSize(new java.awt.Dimension(560, 520));
        namePanel.setLayout(new java.awt.GridBagLayout());

        namePanel.setPreferredSize(new java.awt.Dimension(0, 0));
        jLabel1.setLabelFor(newObjectName);
        jLabel1.setText(java.util.ResourceBundle.getBundle("org/openide/loaders/Bundle").getString("CTL_NewObjectName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        namePanel.add(jLabel1, gridBagConstraints);

        newObjectName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                newObjectNameFocusGained(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        namePanel.add(newObjectName, gridBagConstraints);

        add(namePanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void packageNameFocusGained (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packageNameFocusGained

    }//GEN-LAST:event_packageNameFocusGained

    private void packageModelChanged (javax.swing.event.ListDataEvent evt) {//GEN-FIRST:event_packageModelChanged
    }//GEN-LAST:event_packageModelChanged

    private void newObjectNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_newObjectNameFocusGained
        if (
            Utilities.getOperatingSystem() == Utilities.OS_SOLARIS ||
            Utilities.getOperatingSystem() == Utilities.OS_SUNOS
        ) {
            // does not work on CDE window manager, so better do nothin
            return;
        }

        newObjectName.selectAll ();
    }//GEN-LAST:event_newObjectNameFocusGained

    private void templatesTreeValueChanged (javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_templatesTreeValueChanged
    }//GEN-LAST:event_templatesTreeValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField newObjectName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel namePanel;
    // End of variables declaration//GEN-END:variables

    /** */
    public void changedUpdate(final javax.swing.event.DocumentEvent p1) {
        if (p1.getDocument () == newObjectName.getDocument ()) {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    if (newObjectName.getText().equals ("")) { // NOI18N
                        setNewObjectName (""); // NOI18N
                    }
                    fireStateChanged();
                }
            });
        }
    }

    public void removeUpdate(final javax.swing.event.DocumentEvent p1) {
        changedUpdate (p1);
    }
    
    public void insertUpdate(final javax.swing.event.DocumentEvent p1) {
        changedUpdate (p1);
    }

    public void addNotify () {
        super.addNotify ();
        newObjectName.requestFocus ();
    }

    /** Add a listener to changes of the panel's validity.
    * @param l the listener to add
    * @see #isValid
    */
    void addChangeListener (ChangeListener l) {
        if (listener != null) {
            throw new IllegalStateException ();
        }
        listener = l;
    }

    /** Remove a listener to changes of the panel's validity.
    * @param l the listener to remove
    */
    void removeChangeListener (ChangeListener l) {
        listener = null;
    }

    /** Fires info to listener.
    */
    private void fireStateChanged () {
        if (listener != null) {
            listener.stateChanged (new ChangeEvent (this));
        }
    }
  
    /** Sets the class name to some reasonable value.
    * @param name the name to set the name to
    */
    private void setNewObjectName (String name) {
        String n = name;
        if (name == null || name.length () == 0) {
            n = defaultNewObjectName ();
        }

        newObjectName.getDocument().removeDocumentListener(this);
        newObjectName.setText (n);
        newObjectName.getDocument().addDocumentListener(this);

        if (name == null || name.length () == 0) {
            newObjectName.selectAll ();
        }
    }

    /** */
    public String getNewObjectName() {
        return newObjectName.getText();
    }
    
    /** Getter for default name of a class.
    * @return the default name.
    */
    static String defaultNewObjectName () {
        return getString("FMT_DefaultNewObjectName"); // NOI18N
    }
    
    private static String getString(String key) {
        return org.openide.util.NbBundle.getBundle(NewObjectPanel.class).getString(key);
    }
}
