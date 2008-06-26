package org.netbeans.modules.apisupport.project.ui.wizard.glf;

import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;


public final class GLFTemplateVisualPanel2 extends JPanel {

    private GLFTemplateWizardPanel2 wizardPanel;
    
    /** Creates new form GLFTemplateVisualPanel2 */
    public GLFTemplateVisualPanel2 (GLFTemplateWizardPanel2 wizardPanel) {
        this.wizardPanel = wizardPanel;
        initComponents ();
        wizardPanel.getIterator().getWizardDescriptor().putProperty("NewFileWizard_Title",  // NOI18N
                NbBundle.getMessage(GLFTemplateVisualPanel2.class, "LBL_GLFWizardTitle"));
        DocumentListener documentListener = new DocumentListener () {
            public void insertUpdate (DocumentEvent e) {
                update ();
            }

            public void removeUpdate (DocumentEvent e) {
                update ();
            }

            public void changedUpdate (DocumentEvent e) {
                update ();
            }
            
        };
        tfExtensions.getDocument ().addDocumentListener (documentListener);
        tfMimeType.getDocument ().addDocumentListener (documentListener);
        update ();
    }
    
    private static final Pattern MIME_PATTERN = Pattern.compile("[\\w+-.]+/[\\w+-.]+");  // NOI18N
    private static final Pattern EXT_PATTERN = Pattern.compile("(\\w+\\s*)+");  // NOI18N
    
    private void update () {
        final WizardDescriptor wd = wizardPanel.getIterator().getWizardDescriptor();
        // reasonable mime type check
        if (! MIME_PATTERN.matcher(getMimeType().trim()).matches()) {
            wd.putProperty (
                "WizardPanel_errorMessage",  // NOI18N
                NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Invalid_Mime_Type"));
            wizardPanel.setValid (false);
            return;
        }
        if (! EXT_PATTERN.matcher(getExtensions ().trim ()).matches()) {
            wd.putProperty (
                "WizardPanel_errorMessage",  // NOI18N
                NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Invalid_Extensions"));
            wizardPanel.setValid (false);
            return;
        }
        wd.putProperty (
            "WizardPanel_errorMessage",  // NOI18N
            null
        );
        wizardPanel.setValid (true);
    }
    
    public @Override String getName () {
        return NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Step2");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lMimeType = new javax.swing.JLabel();
        tfMimeType = new javax.swing.JTextField();
        lExtensions = new javax.swing.JLabel();
        tfExtensions = new javax.swing.JTextField();
        extensionsHint = new javax.swing.JLabel();

        lMimeType.setLabelFor(tfMimeType);
        org.openide.awt.Mnemonics.setLocalizedText(lMimeType, org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Mime_Type")); // NOI18N

        lExtensions.setLabelFor(tfExtensions);
        org.openide.awt.Mnemonics.setLocalizedText(lExtensions, org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Extensions")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(extensionsHint, org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "CTL_Extensions_Comment")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lMimeType)
                    .add(lExtensions))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(extensionsHint)
                    .add(tfExtensions, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .add(tfMimeType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lMimeType)
                    .add(tfMimeType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lExtensions)
                    .add(tfExtensions, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(extensionsHint)
                .addContainerGap(223, Short.MAX_VALUE))
        );

        tfMimeType.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "GLFTemplateVisualPanel2.tfMimeType.AccessibleContext.accessibleDescription")); // NOI18N
        tfExtensions.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "GLFTemplateVisualPanel2.tfExtensions.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GLFTemplateVisualPanel2.class, "GLFTemplateVisualPanel2.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel extensionsHint;
    private javax.swing.JLabel lExtensions;
    private javax.swing.JLabel lMimeType;
    private javax.swing.JTextField tfExtensions;
    private javax.swing.JTextField tfMimeType;
    // End of variables declaration//GEN-END:variables
    
    String getMimeType () {
        return tfMimeType.getText ();
    }
    
    String getExtensions () {
        return tfExtensions.getText ();
    }
}

