/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.search;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import org.netbeans.api.search.SearchHistory;
import org.netbeans.api.search.SearchPattern;
import org.netbeans.modules.search.ui.FormLayoutHelper;
import org.openide.awt.Mnemonics;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Base class for pattern sandboxes.
 *
 * @author jhavlin
 */
public abstract class PatternSandbox extends JPanel
        implements HierarchyListener {

    protected JComboBox cboxPattern;
    private JLabel lblPattern;
    protected JLabel lblHint;
    private JLabel lblOptions;
    private JPanel pnlOptions;
    protected JTextPane textPane;
    private JButton btnApply;
    private JButton btnCancel;
    private JScrollPane textScrollPane;
    protected Highlighter highlighter;
    protected Highlighter.HighlightPainter painter;
    protected BasicSearchCriteria searchCriteria;

    /**
     * Initialize UI components.
     */
    protected void initComponents() {

        cboxPattern = new JComboBox();
        cboxPattern.setEditable(true);
        lblPattern = new JLabel();
        lblPattern.setLabelFor(cboxPattern);
        lblHint = new JLabel();
        lblHint.setForeground(SystemColor.controlDkShadow);
        lblOptions = new JLabel();
        textPane = new JTextPane();
        textScrollPane = new JScrollPane();
        textScrollPane.setViewportView(textPane);
        textScrollPane.setPreferredSize(new Dimension(350, 100));
        textScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        searchCriteria = new BasicSearchCriteria();
        initSpecificComponents();
        pnlOptions = createOptionsPanel();
        btnApply = new JButton();
        btnCancel = new JButton();

        initTextPaneContent();
        initHighlighter();

        setMnemonics();
        layoutComponents();
        initInteraction();
        this.addHierarchyListener(this);
        highlightMatchesLater();
    }

    /**
     * Add listeners to buttons.
     */
    private void initButtonsInteraction() {
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        btnApply.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
                closeDialog();
            }
        });
    }

    /**
     * Add listeners to textual components.
     */
    private void initTextInputInteraction() {
        cboxPattern.getEditor().getEditorComponent().addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        highlightMatchesLater();
                    }
                });
        cboxPattern.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                highlightMatchesLater();
            }
        });

        textPane.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                highlightMatchesLater();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                highlightMatchesLater();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                highlightMatchesLater();
            }
        });
    }

    /**
     * Set dialog layout and place components.
     */
    private void layoutComponents() {
        FormLayoutHelper mainHelper =
                new FormLayoutHelper(this, FormLayoutHelper.EAGER_COLUMN);
        mainHelper.setAllGaps(true);

        JPanel form = createFormPanel();
        JPanel buttonsPanel = createButtonsPanel();

        mainHelper.addRow(GroupLayout.DEFAULT_SIZE,
                form.getPreferredSize().height,
                form.getPreferredSize().height,
                form);
        mainHelper.addRow(
                GroupLayout.DEFAULT_SIZE,
                200,
                Short.MAX_VALUE,
                textScrollPane);
        mainHelper.addRow(
                GroupLayout.DEFAULT_SIZE,
                buttonsPanel.getPreferredSize().height,
                buttonsPanel.getPreferredSize().height,
                buttonsPanel);
    }

    /**
     * Create panel for buttons.
     */
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        FormLayoutHelper buttonsHelper = new FormLayoutHelper(buttonsPanel,
                FormLayoutHelper.EAGER_COLUMN,
                FormLayoutHelper.DEFAULT_COLUMN,
                FormLayoutHelper.DEFAULT_COLUMN);
        buttonsHelper.setInlineGaps(true);
        buttonsHelper.addRow(getExtraButton(), btnApply, btnCancel);
        return buttonsPanel;
    }

    /**
     * Create panel for form components.
     */
    private JPanel createFormPanel() {
        JPanel form = new JPanel();
        FormLayoutHelper formHelper = new FormLayoutHelper(form,
                FormLayoutHelper.DEFAULT_COLUMN,
                FormLayoutHelper.EAGER_COLUMN);
        formHelper.setInlineGaps(true);
        formHelper.addRow(lblPattern, cboxPattern);
        if (lblHint.getText() != null
                && !"".equals(lblHint.getText())) {                     //NOI18N
            formHelper.addRow(new JLabel(), lblHint);
        }

        formHelper.addRow(lblOptions, pnlOptions);
        return form;
    }

    /**
     * Set localized text and accessible keys.
     */
    private void setMnemonics() {
        Mnemonics.setLocalizedText(lblPattern, getPatternLabelText());
        Mnemonics.setLocalizedText(lblHint, getHintLabelText());
        Mnemonics.setLocalizedText(btnCancel,
                getText("PatternSandbox.btnCancel.text"));              //NOI18N
        Mnemonics.setLocalizedText(btnApply,
                getText("PatternSandbox.btnApply.text"));               //NOI18N
    }

    /**
     * Initialize highlighter and painter.
     */
    private void initHighlighter() {
        highlighter = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
        textPane.setHighlighter(highlighter);
    }

    /**
     * Initialize listeners and add them to components.
     */
    private void initInteraction() {
        initTextInputInteraction();
        initButtonsInteraction();
    }

    /**
     * Set key events for the dialog - enter for applying and escape for
     * closing.
     */
    private void setKeys() {
        KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Object actionKey = "cancel"; // NOI18N
        getRootPane().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                k, actionKey);

        Action cancelAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ev) {
                closeDialog();
            }
        };

        getRootPane().getActionMap().put(actionKey, cancelAction);

        // pressing enter is equal to clicking Apply button
        getRootPane().setDefaultButton(btnApply);
    }

    /**
     * Schedule highlighting of matches.
     */
    protected void highlightMatchesLater() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                highlightMatches();
            }
        });
    }

    /**
     * Highlight matches in the text pane.
     */
    protected void highlightMatches() {

        highlighter.removeAllHighlights();

        Object value = cboxPattern.getEditor().getItem();
        if (value == null || value.toString().isEmpty()) {
            return;
        }
        String regex = value.toString();
        Pattern p;
        try {
            p = getPatternForHighlighting(regex);
            if (p == null) {
                throw new NullPointerException();
            }
            cboxPattern.getEditor().getEditorComponent().
                    setForeground(SystemColor.textText);
        } catch (Throwable e) {
            cboxPattern.getEditor().getEditorComponent().
                    setForeground(Color.red);
            return;
        }
        highlightIndividualMatches(p);
    }

    /**
     * Get localized text from the bundle by key.
     */
    private static String getText(String key) {
        return NbBundle.getMessage(PatternSandbox.class, key);
    }

    /**
     * Reverse items in a list. Create a new list, original list is untouched.
     */
    private static <T> List<T> reverse(List<T> list) {
        LinkedList<T> ll = new LinkedList<T>();
        for (T t : list) {
            ll.add(0, t);
        }
        return ll;
    }

    /**
     * Close the dialog.
     */
    private void closeDialog() {
        saveTextPaneContent();
        Window w = (Window) SwingUtilities.getAncestorOfClass(
                Window.class, this);
        if (w != null) {
            w.dispose();
        }
    }

    /**
     * Get current pattern as a string. Never returns null.
     */
    private static String getSelectedItemAsString(JComboBox cbox) {
        if (cbox.getSelectedItem() != null) {
            return cbox.getSelectedItem().toString();
        } else {
            return "";
        }
    }

    /**
     * Set keys when the panel is attached to a window.
     */
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (e.getID() == HierarchyEvent.HIERARCHY_CHANGED) {
            setKeys();
        }
    }

    /**
     * Get extra button (or a component) that will be displazed in the left
     * bottom corner of the dialog.
     */
    protected JComponent getExtraButton() {
        return new JLabel();
    }

    /**
     * Get label for Combo Box with pattern.
     */
    protected abstract String getPatternLabelText();

    /**
     * Get hint text for the pattern.
     */
    protected abstract String getHintLabelText();

    /**
     * Create panel containing controls for special options.
     */
    protected abstract JPanel createOptionsPanel();

    /**
     * Init components that are specific for concrete subtype. Should be called
     * when the object is constructed.
     */
    protected abstract void initSpecificComponents();

    /**
     * Apply pattern and options. Called when apply button is clicked or enter
     * key is pressed.
     */
    protected abstract void apply();

    /**
     * Returns a pattern that is used for highligting of the text pane. It
     * should correspond to current state of pattern combo box and associated
     * options.
     */
    protected abstract Pattern getPatternForHighlighting(String patternExpr);

    /**
     * Highlight matches in the text pane, using a valid patter object.
     */
    protected abstract void highlightIndividualMatches(Pattern p);

    /**
     * Load text pane content from find dialog history.
     */
    protected abstract void initTextPaneContent();

    /**
     * Save text pane content to find dialog history.
     */
    protected abstract void saveTextPaneContent();

    protected abstract String getTitle();

    /**
     * Sandox for find text pattern.
     *
     */
    static class TextPatternSandbox extends PatternSandbox
            implements ItemListener {

        private JCheckBox chkMatchCase;
        private String regexp;
        private boolean matchCase;

        public TextPatternSandbox(String regexp, boolean matchCase) {
            this.regexp = regexp;
            this.matchCase = matchCase;
            initComponents();
            searchCriteria.setRegexp(true);
        }

        @Override
        protected void initSpecificComponents() {

            chkMatchCase = new JCheckBox();
            chkMatchCase.addItemListener(this);
            setSpecificMnemonics();
            chkMatchCase.setSelected(matchCase);

            cboxPattern.setSelectedItem(regexp);
            SearchHistory history = SearchHistory.getDefault();
            for (SearchPattern sp : history.getSearchPatterns()) {
                cboxPattern.addItem(sp.getSearchExpression());
            }
        }

        private void setSpecificMnemonics() {
            Mnemonics.setLocalizedText(chkMatchCase,
                    getText("BasicSearchForm.chkCaseSensitive.text"));  //NOI18N
        }

        @Override
        protected String getPatternLabelText() {
            return getText(
                    "BasicSearchForm.lblTextToFind.text");              //NOI18N
        }

        @Override
        protected JPanel createOptionsPanel() {
            JPanel p = new JPanel();
            p.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
            p.add(chkMatchCase);
            return p;
        }

        @Override
        protected final void apply() {
            onApply(getSelectedItemAsString(cboxPattern),
                    chkMatchCase.isSelected());
        }

        protected void onApply(String regexpExpr, boolean matchCase) {
        }

        @Override
        protected void initTextPaneContent() {
            String c = FindDialogMemory.getDefault().getTextSandboxContent();
            textPane.setText(c);
        }

        @Override
        protected void saveTextPaneContent() {
            String c = textPane.getText();
            FindDialogMemory.getDefault().setTextSandboxContent(c);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            ItemSelectable is = e.getItemSelectable();
            if (is == chkMatchCase) {
                searchCriteria.setCaseSensitive(chkMatchCase.isSelected());
            }
            highlightMatchesLater();
        }

        @Override
        protected Pattern getPatternForHighlighting(String patternExpr) {
            searchCriteria.onOk();
            searchCriteria.setTextPattern(patternExpr);
            return searchCriteria.getTextPattern();
        }

        @Override
        protected void highlightIndividualMatches(Pattern p) {
            String text = textPane.getText().replaceAll("\r\n", "\n");  //NOI18N
            Matcher m = p.matcher(text);
            while (m.find()) {
                try {
                    highlighter.addHighlight(m.start(), m.end(), painter);
                } catch (BadLocationException ex) {
                    Logger.getLogger(
                            this.getClass().getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
            textPane.repaint();
        }

        @Override
        protected String getTitle() {
            return getText("TextPatternSandbox.title");                 //NOI18N
        }

        @Override
        protected String getHintLabelText() {
            return "";                                                  //NOI18N
        }
    }

    public static class PathPatternSandbox extends PatternSandbox {

        protected boolean pathRegexp;
        protected String value;

        public PathPatternSandbox(String value) {
            this.value = value;
            initComponents();
            searchCriteria.setFileNameRegexp(true);
        }

        @Override
        protected void initSpecificComponents() {

            cboxPattern.setSelectedItem(value);

            FindDialogMemory memory = FindDialogMemory.getDefault();
            for (String s : reverse(memory.getFileNamePatterns())) {
                cboxPattern.addItem(s);
            }
        }

        @Override
        protected String getPatternLabelText() {
            return getText(
                    "BasicSearchForm.lblFileNamePattern.text");         //NOI18N
        }

        @Override
        protected String getHintLabelText() {
            return "";                                                  //NOI18N
        }

        @Override
        protected JPanel createOptionsPanel() {
            return new JPanel();
        }

        @Override
        protected void apply() {
            onApply(getSelectedItemAsString(cboxPattern));
        }

        protected void onApply(String regexp) {
        }

        @Override
        protected void initTextPaneContent() {
            String c = FindDialogMemory.getDefault().getPathSandboxContent();
            textPane.setText(c);
        }

        @Override
        protected void saveTextPaneContent() {
            String c = textPane.getText();
            FindDialogMemory.getDefault().setPathSandboxContent(c);
        }

        @Override
        protected Pattern getPatternForHighlighting(String patternExpr) {
            searchCriteria.setFileNamePattern(patternExpr);
            searchCriteria.onOk();
            return searchCriteria.getFileNamePattern();
        }

        @Override
        protected void highlightIndividualMatches(Pattern p) {

            String text = textPane.getText().replaceAll("\r\n", "\n");  //NOI18N

            Pattern sep = Pattern.compile("\n");                        //NOI18N
            Matcher m = sep.matcher(text);
            int lastStart = 0;
            while (m.find()) {
                matchLine(text, p, lastStart, m.start());
                lastStart = m.end();
            }
            matchLine(text, p, lastStart, text.length());
            textPane.repaint();
        }

        /**
         * Highlight matches in a line. Line is a substring of text withing
         * start and end positions. Matching is done differently for standard
         * and regexp patterns.
         */
        private void matchLine(String text, Pattern p, int start, int end) {

            boolean matches;
            if (searchCriteria.isFileNameRegexp()) {
                Matcher m = p.matcher(text.substring(start, end));
                matches = m.find();
            } else {
                int fileNameStart; // start and end for pattern matching
                int lastSlash = text.lastIndexOf("/", end);             //NOI18N
                if (lastSlash == -1 || lastSlash < start) {
                    lastSlash = text.lastIndexOf("\\", end);            //NOI18N
                }
                if (lastSlash == -1 || lastSlash < start) {
                    fileNameStart = start;
                } else {
                    fileNameStart = lastSlash + 1;
                }
                Matcher m = p.matcher(text.substring(fileNameStart, end));
                matches = m.matches();
            }
            if (matches) {
                try {
                    highlighter.addHighlight(start, end, painter);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        /**
         * Add Browse button.
         */
        @Override
        protected JComponent getExtraButton() {
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
            final JButton b = new JButton();
            jp.add(b);
            Mnemonics.setLocalizedText(b,
                    getText("PathPatternSandbox.browseButton.text"));   //NOI18N
            b.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    jFileChooser.setMultiSelectionEnabled(true);
                    jFileChooser.showOpenDialog(b);
                    if (jFileChooser.getSelectedFiles() == null) {
                        return;
                    }
                    for (File f : jFileChooser.getSelectedFiles()) {
                        textPane.setText(textPane.getText() + "\n" //NOI18N
                                + f.getAbsolutePath());
                    }
                }
            });
            return jp;
        }

        @Override
        protected String getTitle() {
            return getText("PathPatternSandbox.title");                 //NOI18N
        }
    }

    /**
     * Sandbox for file path pattern.
     *
     */
    static class PathPatternComposer extends PathPatternSandbox
            implements ItemListener {

        private JCheckBox chkFileRegexp;

        public PathPatternComposer(String value, boolean pathRegexp) {
            super(value);
            this.pathRegexp = pathRegexp;
            initComponents();
            if (pathRegexp) {
                searchCriteria.setFileNameRegexp(true);
            }
        }

        @Override
        protected void initSpecificComponents() {
            super.initSpecificComponents();
            chkFileRegexp = new JCheckBox();
            chkFileRegexp.addItemListener(this);
            chkFileRegexp.addItemListener(new RegexpModeListener());
            chkFileRegexp.setSelected(pathRegexp);

            Mnemonics.setLocalizedText(chkFileRegexp,
                    getText("BasicSearchForm.chkFileNameRegex.text"));  //NOI18N
        }

        @Override
        protected JPanel createOptionsPanel() {
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
            jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
            jp.add(chkFileRegexp);
            return jp;
        }

        @Override
        protected final void apply() {
            onApply(getSelectedItemAsString(cboxPattern),
                    chkFileRegexp.isSelected());
        }

        protected void onApply(String pattern, boolean regexp) {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            ItemSelectable is = e.getItemSelectable();
            if (is == chkFileRegexp) {
                searchCriteria.setFileNameRegexp(chkFileRegexp.isSelected());
                highlightMatchesLater();
            }
        }

        @Override
        protected String getHintLabelText() {
            return getText(
                    "BasicSearchForm.cboxFileNamePattern.tooltip");     //NOI18N
        }
    }

    /**
     * Listener that modifies visibility of simple-pattern hint text if regexp
     * mode changes.
     */
    protected class RegexpModeListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            lblHint.setVisible(e.getStateChange() == ItemEvent.DESELECTED);
        }
    }

    public static void openDialog(PatternSandbox sandbox, JComponent baseComponent) {

        JDialog jd = new JDialog(
                (JDialog) SwingUtilities.getAncestorOfClass(
                JDialog.class, baseComponent));

        jd.add(sandbox);
        jd.setTitle(sandbox.getTitle());
        jd.setModal(true);
        jd.setLocationRelativeTo(baseComponent);
        jd.pack();
        jd.setVisible(true);
    }
}
