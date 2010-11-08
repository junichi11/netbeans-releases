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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.git.ui.diff;

/**
 *
 * @author Maros Sandor
 */
public class MultiDiffPanel extends javax.swing.JPanel {

    public MultiDiffPanel () {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnModeGroup = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setFloatable(false);
        jPanel2.setRollover(true);

        btnModeGroup.add(tgbHeadVsWorking);
        tgbHeadVsWorking.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/head_vs_working.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/git/ui/diff/Bundle"); // NOI18N
        tgbHeadVsWorking.setToolTipText(bundle.getString("MultiDiffPanel.tgbHeadVsWorking.toolTipText")); // NOI18N
        tgbHeadVsWorking.setFocusable(false);
        jPanel2.add(tgbHeadVsWorking);

        btnModeGroup.add(tgbHeadVsIndex);
        tgbHeadVsIndex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/head_vs_index.png"))); // NOI18N
        tgbHeadVsIndex.setToolTipText(bundle.getString("MultiDiffPanel.tgbHeadVsIndex.toolTipText")); // NOI18N
        tgbHeadVsIndex.setFocusable(false);
        jPanel2.add(tgbHeadVsIndex);

        btnModeGroup.add(tgbIndexVsWorking);
        tgbIndexVsWorking.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/index_vs_working.png"))); // NOI18N
        tgbIndexVsWorking.setToolTipText(bundle.getString("MultiDiffPanel.tgbIndexVsWorking.toolTipText")); // NOI18N
        tgbIndexVsWorking.setFocusable(false);
        jPanel2.add(tgbIndexVsWorking);

        jPanel1.setMaximumSize(new java.awt.Dimension(80, 32767));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel1);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/diff-next.png"))); // NOI18N
        nextButton.setToolTipText(org.openide.util.NbBundle.getMessage(MultiDiffPanel.class, "MultiDiffPanel.nextButton.toolTipText")); // NOI18N
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(nextButton);

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/diff-prev.png"))); // NOI18N
        prevButton.setToolTipText(org.openide.util.NbBundle.getMessage(MultiDiffPanel.class, "MultiDiffPanel.prevButton.toolTipText")); // NOI18N
        prevButton.setFocusable(false);
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel2.add(prevButton);

        jPanel4.setMaximumSize(new java.awt.Dimension(30, 32767));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/refresh.png"))); // NOI18N
        btnRefresh.setToolTipText(bundle.getString("MultiDiffPanel.btnRefresh.toolTipText")); // NOI18N
        btnRefresh.setActionCommand(null);
        btnRefresh.setFocusable(false);
        btnRefresh.setPreferredSize(new java.awt.Dimension(22, 23));
        jPanel2.add(btnRefresh);

        jPanel3.setOpaque(false);
        jPanel2.add(jPanel3);

        btnCheckout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/checkout_paths.png"))); // NOI18N
        btnCheckout.setToolTipText(bundle.getString("MultiDiffPanel.btnCheckout.toolTipText")); // NOI18N
        btnCheckout.setFocusable(false);
        btnCheckout.setPreferredSize(new java.awt.Dimension(22, 25));
        jPanel2.add(btnCheckout);

        btnCommit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/git/resources/icons/commit.png"))); // NOI18N
        btnCommit.setToolTipText(bundle.getString("MultiDiffPanel.btnCommit.toolTipText")); // NOI18N
        btnCommit.setFocusable(false);
        btnCommit.setPreferredSize(new java.awt.Dimension(22, 25));
        jPanel2.add(btnCommit);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(330, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton btnCheckout = new javax.swing.JButton();
    final javax.swing.JButton btnCommit = new javax.swing.JButton();
    private javax.swing.ButtonGroup btnModeGroup;
    final javax.swing.JButton btnRefresh = new javax.swing.JButton();
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    final javax.swing.JButton nextButton = new javax.swing.JButton();
    final javax.swing.JButton prevButton = new javax.swing.JButton();
    final javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane();
    final javax.swing.JToggleButton tgbHeadVsIndex = new javax.swing.JToggleButton();
    final javax.swing.JToggleButton tgbHeadVsWorking = new javax.swing.JToggleButton();
    final javax.swing.JToggleButton tgbIndexVsWorking = new javax.swing.JToggleButton();
    // End of variables declaration//GEN-END:variables
}
