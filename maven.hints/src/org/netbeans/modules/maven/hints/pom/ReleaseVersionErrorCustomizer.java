/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.hints.pom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class ReleaseVersionErrorCustomizer extends javax.swing.JPanel {
    private Preferences preferences;

    /** Creates new form ReleaseVersionErrorCustomizer */
    public ReleaseVersionErrorCustomizer(Preferences prefs) {
        initComponents();
        this.preferences = prefs;
        cbRelease.setSelected(preferences.getBoolean(ReleaseVersionError.PROP_RELEASE, true));
        cbLatest.setSelected(preferences.getBoolean(ReleaseVersionError.PROP_LATEST, true));
        cbSnapshot.setSelected(preferences.getBoolean(ReleaseVersionError.PROP_SNAPSHOT, false));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbRelease = new JCheckBox();
        cbLatest = new JCheckBox();
        cbSnapshot = new JCheckBox();
        Mnemonics.setLocalizedText(cbRelease, NbBundle.getMessage(ReleaseVersionErrorCustomizer.class, "ReleaseVersionErrorCustomizer.cbRelease.text"));
        cbRelease.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbReleaseActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(cbLatest, NbBundle.getMessage(ReleaseVersionErrorCustomizer.class, "ReleaseVersionErrorCustomizer.cbLatest.text"));
        cbLatest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbLatestActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(cbSnapshot, NbBundle.getMessage(ReleaseVersionErrorCustomizer.class, "ReleaseVersionErrorCustomizer.cbSnapshot.text"));
        cbSnapshot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbSnapshotActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(cbRelease)
                    .add(cbLatest)
                    .add(cbSnapshot))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(cbRelease)
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(cbLatest)
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(cbSnapshot)
                .addContainerGap(215, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbReleaseActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbReleaseActionPerformed
        preferences.putBoolean(ReleaseVersionError.PROP_RELEASE, cbRelease.isSelected());
    }//GEN-LAST:event_cbReleaseActionPerformed

    private void cbLatestActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbLatestActionPerformed
        preferences.putBoolean(ReleaseVersionError.PROP_LATEST, cbLatest.isSelected());
    }//GEN-LAST:event_cbLatestActionPerformed

    private void cbSnapshotActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbSnapshotActionPerformed
        preferences.putBoolean(ReleaseVersionError.PROP_SNAPSHOT, cbSnapshot.isSelected());
    }//GEN-LAST:event_cbSnapshotActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox cbLatest;
    private JCheckBox cbRelease;
    private JCheckBox cbSnapshot;
    // End of variables declaration//GEN-END:variables

}
