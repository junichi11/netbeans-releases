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
package org.netbeans.modules.dlight.tha;

/**
 *
 * @author ak119685
 */
public class THAControlPanel extends javax.swing.JPanel {

    /** Creates new form THAControlPanel */
    public THAControlPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlButton = new javax.swing.JButton();
        deadlocksButton = new javax.swing.JButton();
        racesButton = new javax.swing.JButton();

        setToolTipText(org.openide.util.NbBundle.getMessage(THAControlPanel.class, "THAControlPanel.toolTipText")); // NOI18N

        controlButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/start24.png"))); // NOI18N

        deadlocksButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_active24.png"))); // NOI18N
        deadlocksButton.setText(org.openide.util.NbBundle.getMessage(THAControlPanel.class, "THAControlPanel.deadlocksButton.text")); // NOI18N
        deadlocksButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_inactive24.png"))); // NOI18N
        deadlocksButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        deadlocksButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        racesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_active24.png"))); // NOI18N
        racesButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_inactive24.png"))); // NOI18N
        racesButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        racesButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(controlButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deadlocksButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(racesButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(controlButton)
                    .add(deadlocksButton)
                    .add(racesButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton controlButton;
    private javax.swing.JButton deadlocksButton;
    private javax.swing.JButton racesButton;
    // End of variables declaration//GEN-END:variables

    void setDeadlocks(int deadlocks) {
        deadlocksButton.setText(deadlocks + " dealocks");
        deadlocksButton.setEnabled(0 < deadlocks);
    }

    void setDataRaces(int dataraces) {
        racesButton.setText(dataraces + " dataraces");
        racesButton.setEnabled(0 < dataraces);
    }

    void reset() {
        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
