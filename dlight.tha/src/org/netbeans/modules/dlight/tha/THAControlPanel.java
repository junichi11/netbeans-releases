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

import javax.swing.Action;
import org.openide.util.NbBundle;

/**
 *
 * @author ak119685
 */
public class THAControlPanel extends javax.swing.JPanel {

    private final boolean started;
    private final Action toggleAction;
    private final Action deadlocksAction;

    /** Creates new form THAControlPanel */
    public THAControlPanel(boolean started, Action toggleAction, Action deadlocksAction) {
        this.started = started;
        this.toggleAction = toggleAction;
        this.deadlocksAction = deadlocksAction;
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

        toggleButton = new javax.swing.JToggleButton();
        deadlocksButton = new javax.swing.JButton();
        racesButton = new javax.swing.JButton();

        setToolTipText(org.openide.util.NbBundle.getMessage(THAControlPanel.class, "THAControlPanel.toolTipText")); // NOI18N

        toggleButton.setAction(toggleAction);
        toggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/start24.png"))); // NOI18N
        toggleButton.setSelected(started);
        toggleButton.setRolloverEnabled(false);
        toggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/pause24.png"))); // NOI18N
        add(toggleButton);

        deadlocksButton.setAction(deadlocksAction);
        deadlocksButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_active24.png"))); // NOI18N
        deadlocksButton.setText(org.openide.util.NbBundle.getMessage(THAControlPanel.class, "THAControlPanel.deadlocksButton.nodeadlocks")); // NOI18N
        deadlocksButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/deadlock_inactive24.png"))); // NOI18N
        deadlocksButton.setEnabled(false);
        add(deadlocksButton);

        racesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_active24.png"))); // NOI18N
        racesButton.setText(org.openide.util.NbBundle.getMessage(THAControlPanel.class, "THAControlPanel.dataracesButton.nodataraces")); // NOI18N
        racesButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/dlight/tha/resources/races_inactive24.png"))); // NOI18N
        racesButton.setEnabled(false);
        add(racesButton);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deadlocksButton;
    private javax.swing.JButton racesButton;
    private javax.swing.JToggleButton toggleButton;
    // End of variables declaration//GEN-END:variables

    void setDeadlocks(int deadlocks) {
        if (0 < deadlocks) {
            deadlocksButton.setText(getMessage("THAControlPanel.deadlocksButton.deadlocks", deadlocks));
            deadlocksButton.setEnabled(true);
        } else {
            deadlocksButton.setText(getMessage("THAControlPanel.deadlocksButton.nodeadlocks"));
            deadlocksButton.setEnabled(false);
        }
    }

    void setDataRaces(int dataraces) {
        if (0 < dataraces) {
            racesButton.setText(getMessage("THAControlPanel.dataracesButton.dataraces", dataraces));
            racesButton.setEnabled(true);
        } else {
            racesButton.setText(getMessage("THAControlPanel.dataracesButton.nodataraces"));
            racesButton.setEnabled(false);
        }
    }

    void reset() {
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private static String getMessage(String name, Object... args) {
        return NbBundle.getMessage(THAControlPanel.class, name, args);
    }
}
