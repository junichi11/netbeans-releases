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

package org.netbeans.modules.maven.customizer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.POMComponentFactory;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.Properties;
import org.netbeans.modules.maven.options.MavenVersionSettings;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;

/**
 * Customizer panel for setting source level and encoding.
 * in future possibly also source roots and resource roots.
 * @author mkleint
 */
public class SourcesPanel extends JPanel implements HelpCtx.Provider {
    
    
    private String oldEncoding;
    private String encoding;
    private final String sourceEncoding;
    private String defaultEncoding;
    private String sourceLevel;
    private ModelHandle2 handle;

    private ModelOperation<POMModel> sourceLevelOperation = new ModelOperation<POMModel>() {

        @Override
        public void performOperation(POMModel model) {
            ModelUtils.setSourceLevel(model, sourceLevel);
        }
    };
    
    private ModelOperation<POMModel> encodingOperation = new ModelOperation<POMModel>() {

        @Override
        public void performOperation(POMModel model) {
        //new approach, assume all plugins conform to the new setting.
        POMComponentFactory fact = model.getFactory();
        Properties props = model.getProject().getProperties();
        if (props == null) {
            props = fact.createProperties();
            model.getProject().setProperties(props);
        }
        props.setProperty(Constants.ENCODING_PROP, encoding);
        boolean createPlugins = sourceEncoding == null;

        //check if compiler/resources plugins are configured and update them to ${project.source.encoding expression
        Build bld = model.getProject().getBuild();
        if (bld == null) {
            if (createPlugins) {
                bld = fact.createBuild();
                model.getProject().setBuild(bld);
            } else {
                return;
            }
        }

        Plugin plugin = bld.findPluginById(Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER);
        Plugin plugin2 = bld.findPluginById(Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_RESOURCES);

        String compilesource = PluginPropertyUtils.getPluginProperty(handle.getProject(),
                    Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER,
                    Constants.ENCODING_PARAM, null);
        String resourcesource = PluginPropertyUtils.getPluginProperty(handle.getProject(),
                    Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_RESOURCES,
                    Constants.ENCODING_PARAM, null);

        boolean updateCompiler = createPlugins || compilesource != null; /** configured in parent somehow */
        if (plugin == null && updateCompiler) {
            plugin = fact.createPlugin();
            plugin.setGroupId(Constants.GROUP_APACHE_PLUGINS);
            plugin.setArtifactId(Constants.PLUGIN_COMPILER);
            plugin.setVersion(MavenVersionSettings.getDefault().getVersion(MavenVersionSettings.VERSION_COMPILER));
            bld.addPlugin(plugin);
        }
        if (plugin != null) {
            Configuration conf = plugin.getConfiguration();
            if (conf == null && updateCompiler) {
                conf = fact.createConfiguration();
                plugin.setConfiguration(conf);
            }
            if (conf != null && updateCompiler) {
                conf.setSimpleParameter(Constants.ENCODING_PARAM, "${" + Constants.ENCODING_PROP + "}");
            }
        }

        boolean updateResources = createPlugins || resourcesource != null; /** configured in parent somehow */
        if (plugin2 == null && updateResources) {
            plugin2 = fact.createPlugin();
            plugin2.setGroupId(Constants.GROUP_APACHE_PLUGINS);
            plugin2.setArtifactId(Constants.PLUGIN_RESOURCES);
            plugin2.setVersion(MavenVersionSettings.getDefault().getVersion(MavenVersionSettings.VERSION_RESOURCES));
            bld.addPlugin(plugin2);
        }
        if (plugin2 != null) {
            Configuration conf = plugin2.getConfiguration();
            if (conf == null && updateResources) {
                conf = fact.createConfiguration();
                plugin2.setConfiguration(conf);
            }
            if (conf != null && updateResources) {
                conf.setSimpleParameter(Constants.ENCODING_PARAM, "${" + Constants.ENCODING_PROP + "}");
            }
        }
            
        }
    };

    public SourcesPanel( ModelHandle2 handle, NbMavenProjectImpl project ) {
        initComponents();
        this.handle = handle;
        FileObject projectFolder = project.getProjectDirectory();
        File pf = FileUtil.toFile( projectFolder );
        txtProjectFolder.setText( pf == null ? "" : pf.getPath() ); // NOI18N
        
        // XXX use ComboBoxUpdater to boldface the label when not an inherited default
        comSourceLevel.setEditable(false);
        sourceLevel = SourceLevelQuery.getSourceLevel(project.getProjectDirectory());
        comSourceLevel.setModel(new DefaultComboBoxModel(new String[] {
            "1.3", "1.4", "1.5", "1.6", "1.7", "1.8" //NOI18N
        }));
        
        comSourceLevel.setSelectedItem(sourceLevel);
        String enc = project.getOriginalMavenProject().getProperties().getProperty(Constants.ENCODING_PROP);
        if (enc == null) {
            enc = PluginPropertyUtils.getPluginProperty(project,
                    Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER, Constants.ENCODING_PARAM, null, Constants.ENCODING_PROP);
        }
        oldEncoding = enc;
        if (enc != null) {
            try {
                Charset chs = Charset.forName(enc);
                oldEncoding = chs.name();
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).info("IllegalCharsetName: " + enc); //NOI18N
            }
        }
        // TODO oh well, we fallback to default platform encoding.. that's correct
        // for times before the http://docs.codehaus.org/display/MAVENUSER/POM+Element+for+Source+File+Encoding
        // proposal. this proposal defines the default value as ISO-8859-1
        
        defaultEncoding = Charset.defaultCharset().toString();
        if (oldEncoding == null) {
            oldEncoding = defaultEncoding;
        }
        sourceEncoding = handle.getProject().getProperties().getProperty(Constants.ENCODING_PROP);
        
        comEncoding.setModel(ProjectCustomizer.encodingModel(oldEncoding));
        comEncoding.setRenderer(ProjectCustomizer.encodingRenderer());
        
        comSourceLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSourceLevelChange();
            }
        });
        
        comEncoding.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEncodingChange();
            }            
        });
        txtSrc.setText(handle.getProject().getBuild().getSourceDirectory());
        txtTestSrc.setText(handle.getProject().getBuild().getTestSourceDirectory());
    }
    
    private void handleSourceLevelChange() {
        sourceLevel = (String)comSourceLevel.getSelectedItem();
        handle.removePOMModification(sourceLevelOperation);
        String source = PluginPropertyUtils.getPluginProperty(handle.getProject(),
                Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER, Constants.SOURCE_PARAM,
                "compile"); //NOI18N
        if (source != null && source./*XXX not equals?*/contains(sourceLevel)) {
            return;
        }
        handle.addPOMModification(sourceLevelOperation);
    }

    
    
    private void handleEncodingChange () {
        Charset enc = (Charset) comEncoding.getSelectedItem();
        String encName;
        if (enc != null) {
            encName = enc.name();
        } else {
            encName = oldEncoding;
        }
        encoding = encName;
        handle.removePOMModification(encodingOperation);
        if (!encoding.equals(sourceEncoding)) {
            handle.addPOMModification(encodingOperation);
        }
        if (defaultEncoding.equals(encName)) {
            lblEncoding.setFont(lblEncoding.getFont().deriveFont(Font.PLAIN));
        } else { // XXX use ComboBoxUpdater for the standard technique
            lblEncoding.setFont(lblEncoding.getFont().deriveFont(Font.BOLD));
        }
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return CustomizerProviderImpl.HELP_CTX;
    }    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblProjectFolder = new javax.swing.JLabel();
        txtProjectFolder = new javax.swing.JTextField();
        lblSrc = new javax.swing.JLabel();
        txtSrc = new javax.swing.JTextField();
        lblTestSrc = new javax.swing.JLabel();
        txtTestSrc = new javax.swing.JTextField();
        lblGenerated = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblSourceLevel = new javax.swing.JLabel();
        comSourceLevel = new javax.swing.JComboBox();
        lblEncoding = new javax.swing.JLabel();
        comEncoding = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();

        lblProjectFolder.setLabelFor(txtProjectFolder);
        org.openide.awt.Mnemonics.setLocalizedText(lblProjectFolder, org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("CTL_ProjectFolder")); // NOI18N

        txtProjectFolder.setEditable(false);

        lblSrc.setLabelFor(txtSrc);
        org.openide.awt.Mnemonics.setLocalizedText(lblSrc, org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("SourcesPanel.lblSrc.text")); // NOI18N

        txtSrc.setEditable(false);

        lblTestSrc.setLabelFor(txtTestSrc);
        org.openide.awt.Mnemonics.setLocalizedText(lblTestSrc, org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("SourcesPanel.lblTestSrc.text")); // NOI18N

        txtTestSrc.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(lblGenerated, org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("SourcesPanel.lblGenerated.text")); // NOI18N
        lblGenerated.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblSourceLevel.setLabelFor(comSourceLevel);
        org.openide.awt.Mnemonics.setLocalizedText(lblSourceLevel, org.openide.util.NbBundle.getMessage(SourcesPanel.class, "TXT_SourceLevel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel1.add(lblSourceLevel, gridBagConstraints);

        comSourceLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1.4", "1.5" }));
        comSourceLevel.setMinimumSize(this.comSourceLevel.getPreferredSize());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(comSourceLevel, gridBagConstraints);
        comSourceLevel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(SourcesPanel.class).getString("AN_SourceLevel")); // NOI18N
        comSourceLevel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SourcesPanel.class, "SourcesPanel.comSourceLevel.AccessibleContext.accessibleDescription")); // NOI18N

        lblEncoding.setLabelFor(comEncoding);
        org.openide.awt.Mnemonics.setLocalizedText(lblEncoding, org.openide.util.NbBundle.getMessage(SourcesPanel.class, "TXT_Encoding")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
        jPanel1.add(lblEncoding, gridBagConstraints);

        comEncoding.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPanel1.add(comEncoding, gridBagConstraints);
        comEncoding.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SourcesPanel.class, "SourcesPanel.comEncoding.AccessibleContext.accessibleDescription")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblProjectFolder)
                    .addComponent(lblSrc)
                    .addComponent(lblTestSrc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTestSrc, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .addComponent(txtSrc, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .addComponent(txtProjectFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)))
            .addComponent(lblGenerated, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProjectFolder)
                    .addComponent(txtProjectFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSrc)
                    .addComponent(txtSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTestSrc)
                    .addComponent(txtTestSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblGenerated, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtProjectFolder.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SourcesPanel.class, "SourcesPanel.txtProjectFolder.AccessibleContext.accessibleDescription")); // NOI18N
        txtSrc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SourcesPanel.class, "SourcesPanel.txtSrc.AccessibleContext.accessibleDescription")); // NOI18N
        txtTestSrc.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SourcesPanel.class, "SourcesPanel.txtTestSrc.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comEncoding;
    private javax.swing.JComboBox comSourceLevel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblEncoding;
    private javax.swing.JLabel lblGenerated;
    private javax.swing.JLabel lblProjectFolder;
    private javax.swing.JLabel lblSourceLevel;
    private javax.swing.JLabel lblSrc;
    private javax.swing.JLabel lblTestSrc;
    private javax.swing.JTextField txtProjectFolder;
    private javax.swing.JTextField txtSrc;
    private javax.swing.JTextField txtTestSrc;
    // End of variables declaration//GEN-END:variables
    
}
