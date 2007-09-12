/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

/*
 * NewJPanel_CustomizerJarContent.java
 *
 * Created on June 5, 2006, 11:20 PM
 */

package org.netbeans.modules.bpel.project.ui.customizer;

import java.awt.Dialog;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import org.netbeans.modules.bpel.project.ProjectConstants;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.compapp.projects.base.ui.customizer.IcanproProjectProperties;

/** Customizer for WAR packaging.
 */
public class CustomizerJarContent extends JPanel implements IcanproCustomizer.Panel, HelpCtx.Provider {

    private Dialog dialog;
    //private final AddFilter filterDlg = new AddFilter();
    //private DefaultListModel dlm = new DefaultListModel();
    IcanproProjectProperties webProperties;
    private VisualPropertySupport vps;
    private VisualArchiveIncludesSupport vas;
    private ActionListener actionListener;
    
    /** Creates new form CustomizerCompile */
    public CustomizerJarContent(IcanproProjectProperties webProperties) {
        initComponents();
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerGeneral.class, "ACS_CustomizeWAR_A11YDesc")); //NOI18N

        this.webProperties = webProperties;
        vps = new VisualPropertySupport(webProperties);
        vas = new VisualArchiveIncludesSupport(webProperties,
                                           // jTableComp,
                                            jTableAddContent,
                                            //jButtonUpdate,
                                            jButtonConfig,
                                            jButtonAddProject,
                                            jButtonRemove);
       // vas.initTable();
    }

    public void initValues() {
        //vps.register(jTextFieldFileName, IcanproProjectProperties.JAR_NAME);
        vps.register(vas, ProjectConstants.WS_CONTENT_ADDITIONAL);

        vas.initTableValues();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabelAddContent = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAddContent = new javax.swing.JTable();
        jButtonAddProject = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonConfig = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setMinimumSize(new java.awt.Dimension(208, 240));
        jLabelAddContent.setLabelFor(jTableAddContent);

        jTableAddContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(jTableAddContent);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddProject, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_AddProject_JButton"));
        jButtonAddProject.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemove, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_Remove_JButton"));
        jButtonRemove.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonConfig, org.openide.util.NbBundle.getMessage(CustomizerJarContent.class, "LBL_CustomizeWAR_Config_JButton"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelAddContent)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jButtonAddProject)
                        .add(jButtonRemove))
                    .add(jButtonConfig))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jButtonAddProject, jButtonConfig, jButtonRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelAddContent)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jButtonAddProject)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonRemove)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonConfig)
                        .add(331, 331, 331))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddProject;
    private javax.swing.JButton jButtonConfig;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JLabel jLabelAddContent;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableAddContent;
    // End of variables declaration//GEN-END:variables

    /** Help context where to find more about the paste type action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerJarContent.class);
    }    
}
