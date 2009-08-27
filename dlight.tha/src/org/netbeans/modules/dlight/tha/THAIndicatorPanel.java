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

/*
 * THAIndicatorPanel.java
 *
 * Created on Aug 27, 2009, 3:46:54 PM
 */
package org.netbeans.modules.dlight.tha;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import org.openide.util.NbBundle;

/**
 *
 * @author mt154047
 */
public class THAIndicatorPanel extends javax.swing.JPanel {

    private final Action deadlocksAction;
    private final Action racesAction;
    /** Creates new form THAIndicatorPanel */
    public THAIndicatorPanel(Action deadlocksAction, Action racesAction) {
        initComponents();
        deadlocksLabel.setEnabled(false);
        racesLabel.setEnabled(false);
        this.deadlocksAction = deadlocksAction;
        this.racesAction = racesAction;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        titleLabel = new javax.swing.JLabel();
        deadlocksLabel = new javax.swing.JLabel();
        racesLabel = new javax.swing.JLabel();
        deadlocksDetails = new javax.swing.JLabel();
        racesDetails = new javax.swing.JLabel();

        jLabel2.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.jLabel2.text")); // NOI18N

        jScrollPane1.setViewportView(jEditorPane1);

        titleLabel.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.titleLabel.text")); // NOI18N

        deadlocksLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_active16.png"))); // NOI18N
        deadlocksLabel.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.deadlocksLabel.text")); // NOI18N
        deadlocksLabel.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_inactive16.png"))); // NOI18N

        racesLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_active16.png"))); // NOI18N
        racesLabel.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.racesLabel.text")); // NOI18N
        racesLabel.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_inactive16.png"))); // NOI18N

        deadlocksDetails.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.deadlocksDetails.text")); // NOI18N

        racesDetails.setText(org.openide.util.NbBundle.getMessage(THAIndicatorPanel.class, "THAIndicatorPanel.racesDetails.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(deadlocksLabel)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(77, 77, 77)
                                .add(titleLabel))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deadlocksDetails))))
                    .add(layout.createSequentialGroup()
                        .add(racesLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(racesDetails)))
                .addContainerGap(194, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(28, 28, 28)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(deadlocksLabel)
                            .add(deadlocksDetails)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(titleLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(racesLabel)
                    .add(racesDetails))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel deadlocksDetails;
    private javax.swing.JLabel deadlocksLabel;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel racesDetails;
    private javax.swing.JLabel racesLabel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    void setDeadlocks(int deadlocks) {
        if (0 < deadlocks) {
            deadlocksLabel.setText(getMessage("THAControlPanel.deadlocksButton.deadlocks", deadlocks));//NOI18N
            deadlocksLabel.setEnabled(true);
            deadlocksDetails.setForeground(Color.blue);
            deadlocksDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deadlocksDetails.setText(getMessage("THAIndicatorPanel.details"));//NOI18N
            deadlocksDetails.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)){
                        deadlocksAction.actionPerformed(null);
                    }
                }

            });
        } else {
            deadlocksLabel.setText(getMessage("THAControlPanel.deadlocksButton.nodeadlocks"));//NOI18N
            deadlocksLabel.setEnabled(false);
        }
    }

    void setDataRaces(int dataraces) {
        if (0 < dataraces) {
            racesLabel.setText(getMessage("THAControlPanel.dataracesButton.dataraces", dataraces));//NOI18N
            racesLabel.setEnabled(true);
            racesDetails.setForeground(Color.blue);
            racesDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            racesDetails.setText(getMessage("THAIndicatorPanel.details"));//NOI18N
            racesDetails.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)){
                        racesAction.actionPerformed(null);
                    }
                }

            });
        } else {
            racesLabel.setText(getMessage("THAControlPanel.dataracesButton.nodataraces"));//NOI18N
            racesLabel.setEnabled(false);
        }
    }

    void reset() {
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private static String getMessage(String name, Object... args) {
        return NbBundle.getMessage(THAIndicatorPanel.class, name, args);
    }
}
