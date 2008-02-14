/*
 * EditorOptionsPanel.java
 *
 * Created on January 30, 2008, 2:41 PM
 */

package org.netbeans.modules.cnd.editor.options;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import org.netbeans.modules.cnd.editor.api.CodeStyle;
import org.openide.util.NbBundle;

/**
 * was cloned from org.netbeans.modules.java.ui.FormatingOptionsPanel
 * 
 * @author  Alexander Simon
 */
public class EditorOptionsPanel extends javax.swing.JPanel implements ActionListener, PropertyChangeListener {
    private List<Category> categories = new ArrayList<Category>();
    private EditorOptionsPanelController topControler;
    private boolean loaded = false;
    private CodeStyle.Language currentLanguage;

    /** Creates new form EditorOptionsPanel */
    public EditorOptionsPanel(EditorOptionsPanelController topControler) {
        //this.language = language;
        this.topControler = topControler;
        initComponents();
        setName("Tab_Name"); // NOI18N (used as a bundle key)
        if( "Windows".equals(UIManager.getLookAndFeel().getID()) ) { //NOI18N
            setOpaque( false );
        }
        previewPane.setContentType("text/x-c++"); // NOI18N
        // Don't highlight caret row 
        previewPane.putClientProperty(
            "HighlightsLayerExcludes", // NOI18N
            "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" // NOI18N
        );
        previewPane.setText("1234567890123456789012345678901234567890"); // NOI18N
        previewPane.setDoubleBuffered(true);
        initLanguages();
        initCategories();
    }

    private void initLanguages(){
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new LanguageItem(CodeStyle.Language.C));
        model.addElement(new LanguageItem(CodeStyle.Language.CPP));
        languagesComboBox.setModel(model);
        currentLanguage = CodeStyle.Language.C;
        languagesComboBox.setSelectedIndex(0);
        languagesComboBox.addActionListener(this);
    }

    private void initCategories(){
        createCategories(CodeStyle.Language.C);
        createCategories(CodeStyle.Language.CPP);
        for (Category category : categories) {
            category.addPropertyChangeListener(this);
        }
        initLanguageCategory();
        categoryComboBox.addActionListener(this);
    }

    private void createCategories(CodeStyle.Language language) {
        categories.add(TabsIndentsPanel.getController(language));
        categories.add(AlignmentBracesPanel.getController(language));
        categories.add(SpacesPanel.getController(language));
        categories.add(BlankLinesPanel.getController(language));
        categories.add(OtherPanel.getController(language));
    }

    private void initLanguageCategory(){
        int index = categoryComboBox.getSelectedIndex();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Category category : categories) {
            if (currentLanguage.equals(category.getLanguage())){
                model.addElement(category);
            }
        }
        categoryComboBox.setModel(model);
        categoryComboBox.setSelectedIndex(index);
        actionPerformed(new ActionEvent(categoryComboBox, 0, null));
    }
    
    
    void load() {
        loaded = false;
        for (Category category : categories) {
            category.update();
        }
        loaded = true;
        repaintPreview();        
    }
    
    void store() {
        for (Category category : categories) {
            category.applyChanges();
        }
        //EditorOptions.flush();
        //EditorOptions.lastValues = null;
    }
    
    void cancel() {
        //EditorOptions.lastValues = null;
    }

    // Change in the combo
    public void actionPerformed(ActionEvent e) {
        if (categoryComboBox.equals(e.getSource())){
            Category category = (Category) categoryComboBox.getSelectedItem();
            if (category != null) {
                categoryPanel.setVisible(false);
                categoryPanel.removeAll();
                categoryPanel.add(category.getComponent(null), BorderLayout.CENTER);
                categoryPanel.setVisible(true);
                if (CodeStyle.Language.C.equals(currentLanguage)){
                    previewPane.setContentType("text/x-c"); // NOI18N
                } else {
                    previewPane.setContentType("text/x-c++"); // NOI18N
                }
                if (loaded) {
                    repaintPreview();
                }
            }
        } else if (languagesComboBox.equals(e.getSource())){
            LanguageItem langItem = (LanguageItem) languagesComboBox.getSelectedItem();
            currentLanguage = langItem.language;
            initLanguageCategory();
        }
    }

    // Change in some of the subpanels
    public void propertyChange(PropertyChangeEvent evt) {
        if ( !loaded ) {
            return;
        }
        // Notify the main controler that the page has changed
        topControler.changed();
        // Repaint the preview
        repaintPreview();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        oprionsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        categoryComboBox = new javax.swing.JComboBox();
        categoryPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        languagesComboBox = new javax.swing.JComboBox();
        previewPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        previewPane = new javax.swing.JEditorPane();

        setName(null);
        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(300);

        oprionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        oprionsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(categoryComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditorOptionsPanel.class, "Label_Category")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        oprionsPanel.add(jLabel1, gridBagConstraints);

        categoryComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        oprionsPanel.add(categoryComboBox, gridBagConstraints);

        categoryPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        oprionsPanel.add(categoryPanel, gridBagConstraints);

        jLabel2.setLabelFor(languagesComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditorOptionsPanel.class, "LBL_Language")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        oprionsPanel.add(jLabel2, gridBagConstraints);

        languagesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        oprionsPanel.add(languagesComboBox, gridBagConstraints);

        jSplitPane1.setLeftComponent(oprionsPanel);

        previewPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        previewPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(previewPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        previewPanel.add(jScrollPane1, gridBagConstraints);

        jSplitPane1.setRightComponent(previewPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox categoryComboBox;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox languagesComboBox;
    private javax.swing.JPanel oprionsPanel;
    private javax.swing.JEditorPane previewPane;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables

    private static String getString(String key) {
        return NbBundle.getMessage(EditorOptionsPanel.class, key);
    }

    private void repaintPreview() { 
        Preferences p = new PreviewPreferences();
        for (Category category : categories) {
            if (currentLanguage.equals(category.getLanguage())){
                category.storeTo(p);
            }
        }
        Category category = (Category)categoryComboBox.getSelectedItem();
        if (category != null) {
            jScrollPane1.setIgnoreRepaint(true);
            category.refreshPreview(previewPane, p);
            previewPane.setIgnoreRepaint(false);
            previewPane.scrollRectToVisible(new Rectangle(0,0,10,10) );
            previewPane.repaint(100);
        }
    }
    
    public static class PreviewPreferences extends AbstractPreferences {
        private Map<String,Object> map = new HashMap<String, Object>();
        public PreviewPreferences() {
            super(null, ""); // NOI18N
        }
        protected void putSpi(String key, String value) {
            map.put(key, value);            
        }
        protected String getSpi(String key) {
            return (String)map.get(key);                    
        }
        protected void removeSpi(String key) {
            map.remove(key);
        }
        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        protected String[] keysSpi() throws BackingStoreException {
            String array[] = new String[map.keySet().size()];
            return map.keySet().toArray( array );
        }
        protected String[] childrenNamesSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        protected AbstractPreferences childSpi(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        protected void syncSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        protected void flushSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    private static class LanguageItem {
        private final CodeStyle.Language language;
        private LanguageItem(CodeStyle.Language language){
            this.language = language;
        }

        @Override
        public String toString() {
            return EditorOptionsPanel.getString("LBL_Language_"+language.name());
        }
    }
}
