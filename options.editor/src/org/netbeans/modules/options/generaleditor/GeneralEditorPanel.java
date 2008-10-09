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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.options.generaleditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * Implementation of one panel in Options Dialog.
 *
 * @author Jan Jancura
 */
public class GeneralEditorPanel extends JPanel implements ActionListener {

    private boolean         changed = false;
    private boolean         listen = false;
    
    /** 
     * Creates new form GeneralEditorPanel.
     */
    public GeneralEditorPanel () {
        initComponents ();
                
        loc (lCodeFolding, "Code_Folding");
        loc (lUseCodeFolding, "Code_Folding_Section");
        loc (lCollapseByDefault, "Fold_by_Default");
        loc (lCodeCompletion, "Code_Completion");
        loc (lCodeCompletion2, "Code_Completion_Section");
            
        loc (cbUseCodeFolding, "Use_Folding");
        loc (cbFoldMethods, "Fold_Methods");
        loc (cbFoldInnerClasses, "Fold_Classes");
        loc (cbFoldImports, "Fold_Imports");
        loc (cbFoldJavadocComments, "Fold_JavaDoc");
        loc (cbFoldInitialComments, "Fold_Licence");

        loc (cbAutoPopup, "Auto_Popup_Completion_Window");
        loc (cbDocsAutoPopup, "Auto_Popup_Documentation_Window");
        loc (cbInsertSingleProposalsAutomatically, "Insert_Single_Proposals_Automatically");
        loc (cbCaseSensitive, "Case_Sensitive_Code_Completion");
        loc (cbShowDeprecated, "Show_Deprecated_Members");
        loc (cbInsertClosingBracketsAutomatically, "Pair_Character_Completion");
        loc (cbGuessMethodArgs, "Guess_Filled_Method_Arguments");
        
        loc (lCamelCaseBehavior, "Camel_Case_Behavior");
        loc (cbCamelCaseBehavior, "Enable_Camel_Case_In_Java");
        loc (lCamelCaseBehaviorExample, "Camel_Case_Behavior_Example");
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lCodeFolding = new javax.swing.JLabel();
        lUseCodeFolding = new javax.swing.JLabel();
        lCollapseByDefault = new javax.swing.JLabel();
        cbUseCodeFolding = new javax.swing.JCheckBox();
        cbFoldMethods = new javax.swing.JCheckBox();
        cbFoldInnerClasses = new javax.swing.JCheckBox();
        cbFoldImports = new javax.swing.JCheckBox();
        cbFoldJavadocComments = new javax.swing.JCheckBox();
        cbFoldInitialComments = new javax.swing.JCheckBox();
        lCodeCompletion = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lCodeCompletion2 = new javax.swing.JLabel();
        cbAutoPopup = new javax.swing.JCheckBox();
        cbInsertSingleProposalsAutomatically = new javax.swing.JCheckBox();
        cbCaseSensitive = new javax.swing.JCheckBox();
        cbShowDeprecated = new javax.swing.JCheckBox();
        cbInsertClosingBracketsAutomatically = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        lCamelCaseBehavior = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        cbCamelCaseBehavior = new javax.swing.JCheckBox();
        lCamelCaseBehaviorExample = new javax.swing.JLabel();
        cbDocsAutoPopup = new javax.swing.JCheckBox();
        cbGuessMethodArgs = new javax.swing.JCheckBox();
        cbJavadocNextToCC = new javax.swing.JCheckBox();

        setForeground(new java.awt.Color(99, 130, 191));

        lCodeFolding.setText("Code Folding");

        lUseCodeFolding.setText("Use Code Folding:");

        lCollapseByDefault.setText("Collapse by Default:");

        cbUseCodeFolding.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbFoldMethods.setText("Methods");
        cbFoldMethods.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbFoldInnerClasses.setText("Inner Classes");
        cbFoldInnerClasses.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbFoldImports.setText("Imports");
        cbFoldImports.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbFoldJavadocComments.setText("Javadoc Comments");
        cbFoldJavadocComments.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbFoldInitialComments.setText("Initial Comments");
        cbFoldInitialComments.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        lCodeCompletion.setText("Code Completion");

        lCodeCompletion2.setText("Code Completion:");

        cbAutoPopup.setText("Auto Popup Code Completion Window");
        cbAutoPopup.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbInsertSingleProposalsAutomatically.setText("Insert Single Proposals Automatically");
        cbInsertSingleProposalsAutomatically.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbCaseSensitive.setText("Case Sensitive Code Completion");
        cbCaseSensitive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbShowDeprecated.setText("Show Deprecated Members In Code Completion");
        cbShowDeprecated.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbInsertClosingBracketsAutomatically.setText("Insert Closing Brackets Automatically");
        cbInsertClosingBracketsAutomatically.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        lCamelCaseBehavior.setText("Camel Case  Behavior");

        cbCamelCaseBehavior.setText("Enable Camel Case Navigation For Java");
        cbCamelCaseBehavior.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        lCamelCaseBehaviorExample.setText("Example: Caret stops at J, T, N in \"JavaTypeName\" when using next/previous word acctions");

        cbDocsAutoPopup.setText("Auto Popup Documentation Window");
        cbDocsAutoPopup.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbGuessMethodArgs.setText("Guess Filled Method Arguments");
        cbGuessMethodArgs.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cbJavadocNextToCC.setText(org.openide.util.NbBundle.getMessage(GeneralEditorPanel.class, "CTL_Javadoc_Next_To_CC")); // NOI18N
        cbJavadocNextToCC.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lUseCodeFolding)
                            .add(lCodeCompletion2)
                            .add(lCollapseByDefault)))
                    .add(lCamelCaseBehavior))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cbCamelCaseBehavior)
                            .add(cbInsertClosingBracketsAutomatically)
                            .add(cbShowDeprecated)
                            .add(cbFoldInitialComments)
                            .add(cbFoldJavadocComments)
                            .add(cbFoldImports)
                            .add(cbFoldInnerClasses)
                            .add(cbFoldMethods)
                            .add(cbUseCodeFolding)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cbAutoPopup)
                                    .add(cbInsertSingleProposalsAutomatically)
                                    .add(cbCaseSensitive))
                                .add(91, 91, 91)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cbJavadocNextToCC)
                                    .add(cbGuessMethodArgs)
                                    .add(cbDocsAutoPopup)))
                            .add(lCamelCaseBehaviorExample))
                        .addContainerGap(118, Short.MAX_VALUE))
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)))
            .add(layout.createSequentialGroup()
                .add(138, 138, 138)
                .add(lCamelCaseBehaviorExample)
                .addContainerGap(195, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(lCodeCompletion)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(lCodeFolding)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lCodeFolding)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lUseCodeFolding)
                    .add(cbUseCodeFolding))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lCollapseByDefault)
                    .add(cbFoldMethods))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFoldInnerClasses)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFoldImports)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFoldJavadocComments)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbFoldInitialComments)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lCodeCompletion)
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lCodeCompletion2)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cbAutoPopup)
                        .add(cbDocsAutoPopup)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbInsertSingleProposalsAutomatically)
                    .add(cbGuessMethodArgs))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cbCaseSensitive)
                    .add(cbJavadocNextToCC))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbShowDeprecated)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbInsertClosingBracketsAutomatically)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lCamelCaseBehavior))
                    .add(layout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbCamelCaseBehavior)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lCamelCaseBehaviorExample)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        cbJavadocNextToCC.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(GeneralEditorPanel.class, "AN_Javadoc_Next_To_CC")); // NOI18N
        cbJavadocNextToCC.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(GeneralEditorPanel.class, "AD_Javadoc_Next_To_CC")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbAutoPopup;
    private javax.swing.JCheckBox cbCamelCaseBehavior;
    private javax.swing.JCheckBox cbCaseSensitive;
    private javax.swing.JCheckBox cbDocsAutoPopup;
    private javax.swing.JCheckBox cbFoldImports;
    private javax.swing.JCheckBox cbFoldInitialComments;
    private javax.swing.JCheckBox cbFoldInnerClasses;
    private javax.swing.JCheckBox cbFoldJavadocComments;
    private javax.swing.JCheckBox cbFoldMethods;
    private javax.swing.JCheckBox cbGuessMethodArgs;
    private javax.swing.JCheckBox cbInsertClosingBracketsAutomatically;
    private javax.swing.JCheckBox cbInsertSingleProposalsAutomatically;
    private javax.swing.JCheckBox cbJavadocNextToCC;
    private javax.swing.JCheckBox cbShowDeprecated;
    private javax.swing.JCheckBox cbUseCodeFolding;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lCamelCaseBehavior;
    private javax.swing.JLabel lCamelCaseBehaviorExample;
    private javax.swing.JLabel lCodeCompletion;
    private javax.swing.JLabel lCodeCompletion2;
    private javax.swing.JLabel lCodeFolding;
    private javax.swing.JLabel lCollapseByDefault;
    private javax.swing.JLabel lUseCodeFolding;
    // End of variables declaration//GEN-END:variables
    
    
    private static String loc (String key) {
        return NbBundle.getMessage (GeneralEditorPanel.class, key);
    }
    
    private static void loc (Component c, String key) {
        if (!(c instanceof JLabel)) {
            c.getAccessibleContext ().setAccessibleName (loc ("AN_" + key));
            c.getAccessibleContext ().setAccessibleDescription (loc ("AD_" + key));
        }
        if (c instanceof AbstractButton) {
            Mnemonics.setLocalizedText (
                (AbstractButton) c, 
                loc ("CTL_" + key)
            );
        } else {
            Mnemonics.setLocalizedText (
                (JLabel) c, 
                loc ("CTL_" + key)
            );
        }
    }
    
    private Model model;
    
    void update () {
        listen = false;
        if (model == null) {
            model = new Model ();
            cbUseCodeFolding.addActionListener (this);
            cbFoldMethods.addActionListener (this);
            cbFoldInnerClasses.addActionListener (this);
            cbFoldImports.addActionListener (this);
            cbFoldJavadocComments.addActionListener (this);
            cbFoldInitialComments.addActionListener (this);
            cbAutoPopup.addActionListener (this);
            cbJavadocNextToCC.addActionListener(this);
            cbDocsAutoPopup.addActionListener (this);
            cbInsertSingleProposalsAutomatically.addActionListener (this);
            cbCaseSensitive.addActionListener (this);
            cbShowDeprecated.addActionListener (this);
            cbInsertClosingBracketsAutomatically.addActionListener (this);
            cbGuessMethodArgs.addActionListener (this);
            cbCamelCaseBehavior.addActionListener (this);
        }
        
        // init code folding
        cbUseCodeFolding.setSelected (model.isShowCodeFolding ());
        cbFoldImports.setSelected (model.isFoldImports ());
        cbFoldInitialComments.setSelected (model.isFoldInitialComment ());
        cbFoldInnerClasses.setSelected (model.isFoldInnerClasses ());
        cbFoldJavadocComments.setSelected (model.isFoldJavaDocComments ());
        cbFoldMethods.setSelected (model.isFoldMethods ());
        
        // code completion options
        cbInsertClosingBracketsAutomatically.setSelected 
            (model.isPairCharacterCompletion ());
        cbAutoPopup.setSelected 
            (model.isCompletionAutoPopup ());
        cbJavadocNextToCC.setSelected
                (model.isDocumentationNextToCC());
        cbDocsAutoPopup.setSelected 
            (model.isDocumentationAutoPopup ());
        cbShowDeprecated.setSelected 
            (model.isShowDeprecatedMembers ());
        cbInsertSingleProposalsAutomatically.setSelected 
            (model.isCompletionInstantSubstitution ());
        cbCaseSensitive.setSelected
            (model.isCompletionCaseSensitive ());
        cbGuessMethodArgs.setSelected 
            (model.isGuessMethodArguments ());

        // Java Camel Case Navigation
        Boolean ccJava = model.isCamelCaseJavaNavigation();
        if ( ccJava == null ) {
            cbCamelCaseBehavior.setEnabled(false);
            cbCamelCaseBehavior.setSelected(false);            
        }
        else {
            cbCamelCaseBehavior.setEnabled(true);
            cbCamelCaseBehavior.setSelected(ccJava);
        }
        
        updateEnabledState ();
        
        listen = true;
    }
    
    void applyChanges () {
        
        if (model == null || !changed) return;
        
        // code folding options
        model.setFoldingOptions (
            cbUseCodeFolding.isSelected (),
            cbFoldImports.isSelected (),
            cbFoldInitialComments.isSelected (),
            cbFoldInnerClasses.isSelected (),
            cbFoldJavadocComments.isSelected (),
            cbFoldMethods.isSelected ()
        );
        
        // code completion options
        model.setCompletionOptions (
            cbInsertClosingBracketsAutomatically.isSelected (),
            cbAutoPopup.isSelected (),
            cbDocsAutoPopup.isSelected (),
            cbJavadocNextToCC.isSelected(),
            cbShowDeprecated.isSelected (),
            cbInsertSingleProposalsAutomatically.isSelected (),
            cbCaseSensitive.isSelected (),
            cbGuessMethodArgs.isSelected ()
        );
        
        // java camel case navigation
        model.setCamelCaseNavigation(cbCamelCaseBehavior.isSelected());
        
        changed = false;
    }
    
    void cancel () {
        changed = false;
    }
    
    boolean dataValid () {
        return true;
    }
    
    boolean isChanged () {
        return changed;
    }
    
    public void actionPerformed (ActionEvent e) {
        if (!listen) return;
        if (e.getSource () == cbUseCodeFolding)
            updateEnabledState ();
        changed = true;
    }
    
    
    // other methods ...........................................................
    
    private void updateEnabledState () {
        boolean useCodeFolding = cbUseCodeFolding.isSelected ();
        cbFoldImports.setEnabled (useCodeFolding);
        cbFoldInitialComments.setEnabled (useCodeFolding);
        cbFoldInnerClasses.setEnabled (useCodeFolding);
        cbFoldJavadocComments.setEnabled (useCodeFolding);
        cbFoldMethods.setEnabled (useCodeFolding);        
    }
}
