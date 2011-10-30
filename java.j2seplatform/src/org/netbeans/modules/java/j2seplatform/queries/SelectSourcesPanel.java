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

/*
 * SelectSourcesPanel.java
 *
 * Created on Aug 8, 2011, 6:47:20 PM
 */
package org.netbeans.modules.java.j2seplatform.queries;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.java.j2seplatform.queries.SourceJavadocAttacherUtil.Function;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Zezula
 */
class SelectSourcesPanel extends javax.swing.JPanel {

    private final String displayName;
    private final Callable<List<? extends String>> browseCall;
    private final Function<String,URI> convertor;

    /** Creates new form SelectSourcesPanel */
    SelectSourcesPanel (
            @NonNull final String displayName,
            @NonNull final Callable<List<? extends String>> browseCall,
            @NonNull final Function<String,URI> convertor) {
        assert displayName != null;
        assert browseCall != null;
        assert convertor != null;
        this.displayName = displayName;
        this.browseCall = browseCall;
        this.convertor = convertor;
        initComponents();
    }

    @CheckForNull
    List<? extends URI> getSources() throws Exception {
        final List<URI> paths = new ArrayList<URI>();
        final String str = sourcesField.getText();
        for (String pathElement : str.split(File.pathSeparator)) {
            pathElement = pathElement.trim();
            if (pathElement.length() > 0) {
                paths.add(convertor.call(pathElement));
            }
        }
        return paths;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        attachTo = new javax.swing.JLabel();
        lblSources = new javax.swing.JLabel();
        sourcesField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        attachTo.setText(NbBundle.getMessage(SelectSourcesPanel.class, "TXT_AttachSourcesTo",displayName));

        lblSources.setLabelFor(sourcesField);
        org.openide.awt.Mnemonics.setLocalizedText(lblSources, org.openide.util.NbBundle.getMessage(SelectSourcesPanel.class, "TXT_LocalSources")); // NOI18N

        sourcesField.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(SelectSourcesPanel.class, "TXT_Browse")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attachTo)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSources)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourcesField, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attachTo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSources)
                    .addComponent(sourcesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void browse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browse
    try {
        final List<? extends String> paths = browseCall.call();
        if (paths != null) {
            final StringBuilder sb = new StringBuilder();
            for (String pathElement : paths) {
                if (sb.length() != 0) {
                    sb.append(File.pathSeparatorChar);  //NOI18N
                }
                sb.append(pathElement);
            }
            sourcesField.setText(sb.toString());
        }
    } catch (Exception ex) {
        Exceptions.printStackTrace(ex);
    }
}//GEN-LAST:event_browse

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attachTo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel lblSources;
    private javax.swing.JTextField sourcesField;
    // End of variables declaration//GEN-END:variables
}
