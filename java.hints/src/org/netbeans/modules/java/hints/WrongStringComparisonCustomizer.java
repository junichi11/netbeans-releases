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

package org.netbeans.modules.java.hints;

import java.util.prefs.Preferences;


/**
 * Options Customiser panel for {@link WrongStringComparison} hint
 *
 * @author Mark Lewis
 */
public class WrongStringComparisonCustomizer extends javax.swing.JPanel {

    private Preferences p;

    /** Creates new customizer WrongStringComparisonCustomizer */
    public WrongStringComparisonCustomizer(Preferences p) {
        initComponents();
        this.p = p;
        ternaryNullCheck.setSelected(WrongStringComparison.getTernaryNullCheck(p));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ternaryNullCheck = new javax.swing.JCheckBox();

        ternaryNullCheck.setText(org.openide.util.NbBundle.getMessage(WrongStringComparisonCustomizer.class, "WrongStringComparisonCustomizer.ternaryNullCheck.text")); // NOI18N
        ternaryNullCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ternaryNullCheckActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(ternaryNullCheck, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(ternaryNullCheck)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        ternaryNullCheck.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(WrongStringComparisonCustomizer.class, "ACSD_Ternary_Null_Check")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void ternaryNullCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ternaryNullCheckActionPerformed
        WrongStringComparison.setTernaryNullCheck(p, ternaryNullCheck.isSelected());
    }//GEN-LAST:event_ternaryNullCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ternaryNullCheck;
    // End of variables declaration//GEN-END:variables

}
