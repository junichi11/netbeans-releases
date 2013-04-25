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

package org.netbeans.modules.web.jsf.wizards;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
import org.netbeans.modules.html.editor.lib.api.elements.Attribute;
import org.netbeans.modules.html.editor.lib.api.elements.OpenTag;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.netbeans.modules.web.common.api.LexerUtils;
import org.netbeans.modules.web.jsf.JsfConstants;
import org.netbeans.modules.web.jsf.dialogs.BrowseFolders;
import org.netbeans.modules.web.jsf.wizards.TemplateClientPanel.TemplateEntry;
import org.netbeans.modules.web.jsfapi.api.DefaultLibraryInfo;
import org.netbeans.modules.web.jsfapi.api.NamespaceUtils;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Parameters;

/**
 *
 * @author Petr Pisl
 */

public class TemplateClientPanelVisual extends javax.swing.JPanel implements HelpCtx.Provider {
    
    private WizardDescriptor wizardDescriptor;
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);

    private final static String TAG_NAME = "ui:insert";    //NOI18N
    private final static String VALUE_NAME = "name";    //NOI18N

    /** Creates new form TemplateClientPanel */
    public TemplateClientPanelVisual(WizardDescriptor wizardDescriptor) {
        initComponents();
        this.wizardDescriptor = wizardDescriptor;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgRootTag = new javax.swing.ButtonGroup();
        jrbHtml = new javax.swing.JRadioButton();
        jrbComposition = new javax.swing.JRadioButton();
        jlRootTag = new javax.swing.JLabel();
        jlTemplate = new javax.swing.JLabel();
        jtfTemplate = new javax.swing.JTextField();
        jbBrowse = new javax.swing.JButton();

        bgRootTag.add(jrbHtml);
        jrbHtml.setSelected(true);
        jrbHtml.setText("<html>&lt;html&gt;</html>");
        jrbHtml.setActionCommand("html");
        jrbHtml.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jrbHtml.setMargin(new java.awt.Insets(0, 0, 0, 0));

        bgRootTag.add(jrbComposition);
        jrbComposition.setText("<ui:composition>");
        jrbComposition.setActionCommand("composition");
        jrbComposition.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jrbComposition.setMargin(new java.awt.Insets(0, 0, 0, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle"); // NOI18N
        jlRootTag.setText(bundle.getString("LBL_RootTag")); // NOI18N

        jlTemplate.setText(bundle.getString("LBL_SelectTemplate")); // NOI18N

        jtfTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtfTemplateKeyReleased(evt);
            }
        });

        jbBrowse.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/wizards/Bundle").getString("MNE_Browse").charAt(0));
        jbBrowse.setText(bundle.getString("LBL_Browse")); // NOI18N
        jbBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlTemplate)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jtfTemplate, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbBrowse))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jlRootTag)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jrbComposition)
                    .addComponent(jrbHtml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(250, 250, 250))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlTemplate)
                    .addComponent(jbBrowse)
                    .addComponent(jtfTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlRootTag)
                    .addComponent(jrbHtml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jrbComposition)
                .addContainerGap(33, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void jtfTemplateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfTemplateKeyReleased
        //validateTemplate(new File(jtfTemplate.getText()));
        templateData = Collections.EMPTY_SET;
        fireChangeEvent();
    }//GEN-LAST:event_jtfTemplateKeyReleased

    @Messages({
        "TemplateClientPanelVisual.lbl.resource.library.contract=Resource Library Contract",
        "TemplateClientPanelVisual.lbl.web.pages=Web Pages"
    })
    private void jbBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBrowseActionPerformed
        String projectDirPath = Templates.getProject(wizardDescriptor).getProjectDirectory().getPath();
        final boolean projectNamedContracts = projectDirPath.contains(JsfConstants.CONTRACTS_FOLDER);
        BrowseFolders bf = new BrowseFolders(getFaceletTemplateRoots(), new BrowseFolders.Naming() {
            @Override
            public String getName(String path, String folderName) {
                boolean isContract;
                if (projectNamedContracts) {
                    String[] split = path.split(JsfConstants.CONTRACTS_FOLDER);
                    isContract = split.length > 2;
                } else {
                    isContract = path.contains(JsfConstants.CONTRACTS_FOLDER);
                }
                if (isContract) {
                    return Bundle.TemplateClientPanelVisual_lbl_resource_library_contract() + " : " + folderName; //NOI18N
                } else {
                    return Bundle.TemplateClientPanelVisual_lbl_web_pages();
                }
            }
        });
        org.openide.filesystems.FileObject fo = bf.showDialog();
        if (fo != null) {
            String path = fo.getPath();
            // presume resource library contract
            if (path.contains(JsfConstants.CONTRACTS_FOLDER)) {
                jtfTemplate.setText(getRelativePathInsideResourceLibrary(path));
            } else {
                jtfTemplate.setText(path);
            }
            templateData = Collections.EMPTY_SET;
        }
        fireChangeEvent();
    }//GEN-LAST:event_jbBrowseActionPerformed

    private FileObject[] getFaceletTemplateRoots() {
        List<FileObject> roots = new ArrayList<FileObject>();
        // web project document roots
        roots.addAll(getSourceRoots());
        // web project contracts roots
        roots.addAll(getProjectContractsRoots());
        // libraries contracts roots
        roots.addAll(getLibContractsRoots());

        return roots.toArray(new FileObject[roots.size()]);
    }

    private SourceGroup[] getProjectDocumentSourceGroups() {
        Sources sources = ProjectUtils.getSources(Templates.getProject(wizardDescriptor));
        SourceGroup[] sourceGroups = sources.getSourceGroups(WebProjectConstants.TYPE_DOC_ROOT);
        return sourceGroups;
    }

    /**
     * Gets templates from the document roots of the project.
     */
    private List<FileObject> getSourceRoots() {
        List<FileObject> roots = new ArrayList<FileObject>();
        for (SourceGroup sourceGroup : getProjectDocumentSourceGroups()) {
            roots.add(sourceGroup.getRootFolder());
        }
        return roots;
    }

    /**
     * Gets templates from contracts folders of the external .JARs.
     */
    private List<FileObject> getLibContractsRoots() {
        List<FileObject> roots = new ArrayList<FileObject>();
        if (getProjectDocumentSourceGroups().length > 0) {
            ClassPathProvider cpp = Templates.getProject(wizardDescriptor).getLookup().lookup(ClassPathProvider.class);
            for (SourceGroup sourceGroup : getProjectDocumentSourceGroups()) {
                ClassPath cp = cpp.findClassPath(sourceGroup.getRootFolder(), ClassPath.COMPILE);
                for (FileObject root : cp.getRoots()) {
                    FileObject contracts = root.getFileObject("META-INF/" + JsfConstants.CONTRACTS_FOLDER); //NOI18N
                    if (contracts != null && contracts.isValid() && contracts.isFolder()) {
                        for (FileObject contract : contracts.getChildren()) {
                            roots.add(contract);
                        }
                    }
                }
            }
        }
        return roots;
    }

    /**
     * Gets templates from contracts folders of the document roots.
     */
    private List<FileObject> getProjectContractsRoots() {
        List<FileObject> roots = new ArrayList<FileObject>();
        for (SourceGroup sourceGroup : getProjectDocumentSourceGroups()) {
            FileObject contractsRoot = sourceGroup.getRootFolder().getFileObject(JsfConstants.CONTRACTS_FOLDER);
            if (contractsRoot != null && contractsRoot.isValid() && contractsRoot.isFolder()) {
                for (FileObject fileObject : contractsRoot.getChildren()) {
                    if (fileObject.isValid() && fileObject.isFolder()) {
                        roots.add(fileObject);
                    }
                }
            }
        }
        return roots;
    }

    /**
     * Gets relative path inside the resource library contract if available. Otherwise it returns the entered full path.
     * @param fullPath to detect inside contracts
     * @return relative path within contract if any, full path otherwise
     */
    protected static String getRelativePathInsideResourceLibrary(String fullPath) {
        if (!fullPath.contains(JsfConstants.CONTRACTS_FOLDER)) {
            return fullPath;
        }
        int rootIndex = fullPath.lastIndexOf(JsfConstants.CONTRACTS_FOLDER) + JsfConstants.CONTRACTS_FOLDER.length() + 1;
        int nextSlashOffset = fullPath.indexOf("/", rootIndex); //NOI18N
        // root folder selected
        if (nextSlashOffset != -1) {
            String resourceLibraryName = fullPath.substring(rootIndex, nextSlashOffset);
            return fullPath.substring(fullPath.indexOf(resourceLibraryName) + resourceLibraryName.length());
        }
        return fullPath;
    }

    private FileObject obtainContractTemplate(String path) {
        List<FileObject> contractsRoots = getProjectContractsRoots();
        contractsRoots.addAll(getLibContractsRoots());
        for (FileObject contract : contractsRoots) {
            FileObject fileObject = contract.getFileObject(path);
            if (fileObject != null && fileObject.isValid() && !fileObject.isFolder()) {
                return fileObject;
            }
        }
        return null;
    }

    protected boolean validateTemplate() {
        if (templateData != null && !templateData.isEmpty()) {
            return true;
        }
        String message = null;
        String path = jtfTemplate.getText();
        if (path == null || "".equals(path)) {
            message = "MSG_NoTemplateSelected"; //NOI18N
        } else {
            File file = new File(path);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    FileObject fo = FileUtil.toFileObject(file);
                    parseTemplateData(fo);
                    if (templateData == null || templateData.isEmpty()) {
                       message = "MSG_NoFaceletsTemplate"; //NOI18N
                    }
                } else {
                    message = "MSG_TemplateHasToBeFile";   //NOI18N
                }
            } else {
                FileObject contractFO = obtainContractTemplate(path);
                if (contractFO == null) {
                    message = "MSG_EneterExistingTemplate";    //NOI18N
                } else {
                    parseTemplateData(contractFO);
                    if (templateData == null || templateData.isEmpty()) {
                       message = "MSG_NoFaceletsTemplate"; //NOI18N
                    }
                }
            }
        }
        if (message != null){
            wizardDescriptor.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE,        //NOI18N
                    NbBundle.getMessage(TemplateClientPanelVisual.class, message));
        }
        return (message == null);
    }

    private void parseTemplateData(FileObject fo) {
        Parameters.notNull("fileObject", fo); //NOI18N
        Source source = Source.create(fo);
        try {
            ParserManager.parse(Collections.singleton(source), new UserTask() {

                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    templateData = new LinkedHashSet<String>();
                    Result result = resultIterator.getParserResult(0);
                    if (result.getSnapshot().getMimeType().equals("text/html")) {
                        HtmlParserResult htmlResult = (HtmlParserResult)result;
                        String ns = null;
                        if (htmlResult.getNamespaces().containsKey(DefaultLibraryInfo.FACELETS.getNamespace())) {
                            ns = DefaultLibraryInfo.FACELETS.getNamespace();
                        } else if (htmlResult.getNamespaces().containsKey(DefaultLibraryInfo.FACELETS.getLegacyNamespace())) {
                            ns = DefaultLibraryInfo.FACELETS.getLegacyNamespace();
                        }
                        if (ns != null) {
                            List<OpenTag> foundNodes = findValue(htmlResult.root(ns).children(OpenTag.class), TAG_NAME, new ArrayList<OpenTag>());

                            for (OpenTag node : foundNodes) {
                                Attribute attr = node.getAttribute(VALUE_NAME);
                                if (attr !=null) {
                                     String value = attr.unquotedValue().toString();
                                    if (value != null && !"".equals(value)) {   //NOI18N
                                        templateData.add(value);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private List<OpenTag> findValue(Collection<OpenTag> nodes, String tagName, List<OpenTag> foundNodes) {
        if (nodes == null) {
            return foundNodes;
        }
        for (OpenTag ot : nodes) {
            if(LexerUtils.equals(tagName, ot.name(), true, false)) {
                foundNodes.add(ot);
            } else {
                foundNodes = findValue(ot.children(OpenTag.class), tagName, foundNodes);
            }

        }
        return foundNodes;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(TemplateClientPanelVisual.class);
    }
    
    public InputStream getTemplateClient(){
        String path = "org/netbeans/modules/web/jsf/facelets/resources/templates/";  //NOI18N
        path = path + bgRootTag.getSelection().getActionCommand() + "TemplateClient.template";          //NOI18N
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        return is;
    }

    Collection<String> templateData = Collections.EMPTY_SET;
    
    public Collection<String> getTemplateData(){
        return templateData;
    }

    public TemplateEntry getTemplate() {
        TemplateEntry templateEntry = new TemplateEntry(null);
        String path = jtfTemplate.getText();
        if (path != null && !"".equals(path)) {
            File file = new File(path);
            FileObject template = FileUtil.toFileObject(file);
            if (template == null || !template.isValid() || template.isFolder()) {
                template = obtainContractTemplate(path);
                templateEntry = new TemplateEntry(template, true);
            } else {
                templateEntry = new TemplateEntry(template);
            }
        }
        return templateEntry;
    }
    
    protected void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    protected void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgRootTag;
    private javax.swing.JButton jbBrowse;
    private javax.swing.JLabel jlRootTag;
    private javax.swing.JLabel jlTemplate;
    private javax.swing.JRadioButton jrbComposition;
    private javax.swing.JRadioButton jrbHtml;
    private javax.swing.JTextField jtfTemplate;
    // End of variables declaration//GEN-END:variables

}
