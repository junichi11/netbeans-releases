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
package org.netbeans.jellytools.modules.editor;

import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.modules.editor.search.SearchBar;

/**
 *
 * @author jprox
 */
public class SearchBarOperator extends EditorPanelOperator {

    private JTextComponentOperator findOp;
    private JButtonOperator nextButtonOp;
    private JButtonOperator prevButtonOp;
    private JButtonOperator closeButtonOp;
    private JCheckBoxOperator match;
    private JCheckBoxOperator whole;
    private JCheckBoxOperator regular;
    private JCheckBoxOperator highlight;
    private JCheckBoxOperator wrap;
    
    private SearchBarOperator() {
        super(SearchBar.class);
    }

    @Override
    protected void invokeAction(EditorOperator editorOperator) {
        editorOperator.pushKey(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
    }

    @Override
    protected JButton getExpandButton() {
        if(buttons.size()<=3) return null;
        if(buttons.size()==4) return buttons.get(2);
        return null;
        
    }
    
    public static SearchBarOperator invoke(EditorOperator editorOperator) {
        SearchBarOperator sbo = new SearchBarOperator();
        sbo.openPanel(editorOperator);
        return sbo;
    }
    
    public static SearchBarOperator getPanel(EditorOperator editorOperator) {
        SearchBarOperator sbo = new SearchBarOperator();
        JPanel panel = sbo.getOpenedPanel(editorOperator);
        if(panel==null) throw new IllegalArgumentException("Panel is not found");
        return sbo;
    }
    
    public JTextComponentOperator findCombo() {
        if (findOp == null) {
            findOp = new JTextComponentOperator(getContainerOperator());
        }
        return findOp;
    }

    public JButtonOperator prevButton() {
        if (prevButtonOp == null) {
            prevButtonOp = new JButtonOperator(getButton(0));
        }
        return prevButtonOp;
    }

    public JButtonOperator nextButton() {
        if (nextButtonOp == null) {
            nextButtonOp = new JButtonOperator(getButton(1));
        }
        return nextButtonOp;
    }

    public JButtonOperator closeButton() {
        if (closeButtonOp == null) {
            closeButtonOp = new JButtonOperator(getButton(buttons.size()-1));
        }
        return closeButtonOp;
    }

    public JCheckBoxOperator matchCaseCheckBox() {
        return getCheckbox(0);

    }

    public JCheckBoxOperator highlightResultsCheckBox() {
        return getCheckbox(3);

    }

    public JCheckBoxOperator reqularExpressionCheckBox() {
        return getCheckbox(2);

    }

    public JCheckBoxOperator wholeWordsCheckBox() {
        return getCheckbox(1);

    }
    
    public JCheckBoxOperator wrapAroundCheckBox() {
        return getCheckbox(4);
    }

    void uncheckAll() {
        matchCaseCheckBox().setSelected(false);
        highlightResultsCheckBox().setSelected(false);
        reqularExpressionCheckBox().setSelected(false);
//        highlightResultsCheckBox().setSelected(false);
//        wrapAroundCheckBox().setSelected(false);
    }
    
}
