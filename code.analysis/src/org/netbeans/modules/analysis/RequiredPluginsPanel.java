/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.analysis;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.swing.UIManager;
import org.netbeans.modules.analysis.spi.Analyzer.MissingPlugin;
import org.openide.util.ImageUtilities;

/**
 *
 * @author lahvac
 */
public class RequiredPluginsPanel extends javax.swing.JPanel {

    private Set<MissingPlugin> plugins = new HashSet<MissingPlugin>();

    public RequiredPluginsPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warning = new javax.swing.JLabel();
        install = new javax.swing.JButton();

        warning.setText(org.openide.util.NbBundle.getMessage(RequiredPluginsPanel.class, "RequiredPluginsPanel.warning.text")); // NOI18N

        install.setText(org.openide.util.NbBundle.getMessage(RequiredPluginsPanel.class, "RequiredPluginsPanel.install.text")); // NOI18N
        install.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(warning)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(install))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(warning)
                .addComponent(install))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void installActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installActionPerformed
        Utils.installMissingPlugins(this.plugins);
    }//GEN-LAST:event_installActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton install;
    private javax.swing.JLabel warning;
    // End of variables declaration//GEN-END:variables

    public void setRequiredPlugins(Set<MissingPlugin> plugins, boolean critical) {
        this.plugins = plugins;
        
        if (critical) {
            //copied from DialogDisplayer
            Color nbErrorForeground = UIManager.getColor("nb.errorForeground"); //NOI18N
            if (nbErrorForeground == null) {
                //nbErrorForeground = new Color(89, 79, 191); // RGB suggested by Bruce in #28466
                nbErrorForeground = new Color(255, 0, 0); // RGB suggested by jdinga in #65358
            }

            warning.setForeground(nbErrorForeground);
            warning.setIcon(ImageUtilities.loadImageIcon("org/netbeans/modules/dialogs/error.gif", false));
        } else {
            //copied from DialogDisplayer
            Color nbWarningForeground = UIManager.getColor("nb.warningForeground"); //NOI18N
            if (nbWarningForeground == null) {
                nbWarningForeground = new Color(51, 51, 51); // Label.foreground
            }

            warning.setForeground(nbWarningForeground);
            warning.setIcon(ImageUtilities.loadImageIcon("org/netbeans/modules/dialogs/warning.gif", false));
        }
    }
}
