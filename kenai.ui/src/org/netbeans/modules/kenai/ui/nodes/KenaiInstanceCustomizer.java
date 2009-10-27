/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.kenai.ui.nodes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.NotificationLineSupport;
import org.openide.util.NbBundle;


/**
 *
 * @author Jan Becicka
 */
public class KenaiInstanceCustomizer extends javax.swing.JPanel implements java.beans.Customizer, DocumentListener {

    private Object bean;
    private NotificationLineSupport ns;
    private DialogDescriptor dd;

    /** Creates new customizer KenaiInstanceCustomizer */
    public KenaiInstanceCustomizer() {
        initComponents();
        txtDisplayName.getDocument().addDocumentListener(this);
        txtUrl.getDocument().addDocumentListener(this);
    }

    public void setObject(Object bean) {
        this.bean = bean;
    }

    public String getDisplayName() {
        return txtDisplayName.getText();
    }

    public String getUrl() {
        return txtUrl.getText();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        lblUrl = new javax.swing.JLabel();
        txtDisplayName = new javax.swing.JTextField();
        txtUrl = new javax.swing.JTextField();

        lblName.setLabelFor(txtDisplayName);
        org.openide.awt.Mnemonics.setLocalizedText(lblName, org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "KenaiInstanceCustomizer.lblName.text")); // NOI18N

        lblUrl.setLabelFor(txtUrl);
        org.openide.awt.Mnemonics.setLocalizedText(lblUrl, org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "KenaiInstanceCustomizer.lblUrl.text")); // NOI18N

        txtUrl.setText(org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "KenaiInstanceCustomizer.txtUrl.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblName)
                    .add(lblUrl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .add(txtDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txtDisplayName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtUrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblUrl))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblUrl;
    private javax.swing.JTextField txtDisplayName;
    private javax.swing.JTextField txtUrl;
    // End of variables declaration//GEN-END:variables

    private void validateInput() {
        clearError();
        String name = getDisplayName();
        if (name.trim().length()==0) {
            showError(org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_NoName"));
            return;
        }
        if (name.contains(",")) {//NOI18N
            showError(org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_IllegalCharacter","','"));
            return;
        }
        if (name.contains(";")) {//NOI18N
            showError(org.openide.util.NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_IllegalCharacter","';'"));
            return;
        }
        String e = urlValid(getUrl());
        if (e!=null) {
            showError(e);
            return;
        }
    }

    private void showError(String text) {
        ns.setInformationMessage(text);
        dd.setValid(false);
    }

    private void clearError() {
        ns.clearMessages();
        dd.setValid(true);
    }

    private static Pattern urlPatten = Pattern.compile("https://([a-zA-Z0-9\\-\\.])+\\.(([a-zA-Z]{2,3})|(info)|(name)|(aero)|(coop)|(museum)|(jobs)|(mobi)|(travel))/?$");



    private static String urlValid(String s) {
        if (!s.startsWith("https://")) { //NOI18N
            return NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_NotHttps");
        }

        if (!urlPatten.matcher(s).matches()) {
            return NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_UrlNotValid");
        }
        for (KenaiInstance instance : KenaiInstancesManager.getDefault().getInstances()) {
            if (instance.getUrl().toString().equals(s.endsWith("/") ? s.substring(0, s.length() - 1) : s)) {
                return NbBundle.getMessage(KenaiInstanceCustomizer.class, "ERR_UrlUsed", s);
            }
        }

        try {
            new URL(s);
            return null;
        } catch (MalformedURLException ex) {
            return ex.getMessage();
        }
    }

    public void insertUpdate(DocumentEvent e) {
        validateInput();
    }

    public void removeUpdate(DocumentEvent e) {
        validateInput();
    }

    public void changedUpdate(DocumentEvent e) {
        validateInput();
    }

    void setNotificationsSupport(NotificationLineSupport support) {
        this.ns = support;
    }

    void setDialogDescriptor(DialogDescriptor dd) {
        this.dd = dd;
    }


}
