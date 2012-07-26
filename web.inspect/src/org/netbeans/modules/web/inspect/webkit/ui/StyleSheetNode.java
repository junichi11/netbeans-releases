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
package org.netbeans.modules.web.inspect.webkit.ui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.inspect.actions.OpenResourceAction;
import org.netbeans.modules.web.inspect.actions.Resource;
import org.netbeans.modules.web.webkit.debugging.api.css.CSS;
import org.netbeans.modules.web.webkit.debugging.api.css.Rule;
import org.netbeans.modules.web.webkit.debugging.api.css.StyleSheetBody;
import org.netbeans.modules.web.webkit.debugging.api.css.StyleSheetHeader;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 * A node representing a style sheet.
 *
 * @author Jan Stola
 */
public class StyleSheetNode extends AbstractNode {
    /** Icon base of the node. */
    static final String ICON_BASE = "org/netbeans/modules/web/inspect/resources/matchedRules.png"; // NOI18N
    /** Header of the style sheet. */
    private StyleSheetHeader header;

    /**
     * Creates a new {@code StyleSheetNode}.
     *
     * @param css CSS domain of the corresponding WebKit debugging.
     * @param header header of the represented stylesheet.
     */
    StyleSheetNode(CSS css, StyleSheetHeader header) {
        super(Children.create(new StyleSheetChildFactory(css, header), true),
                Lookups.fixed(new Resource(header.getSourceURL())));
        this.header = header;
        updateDisplayName();
        setIconBaseWithExtension(ICON_BASE);
    }

    /**
     * Updates the display name of the node.
     */
    private void updateDisplayName() {
        String sourceURL = header.getSourceURL();
        String displayName = sourceURL;
        if (sourceURL != null && sourceURL.startsWith("file://")) { // NOI18N
            try {
                URI uri = new URI(sourceURL);
                if ((uri.getAuthority() != null) || (uri.getFragment() != null) || (uri.getQuery() != null)) {
                    uri = new URI(uri.getScheme(), null, uri.getPath(), null, null);
                }
                Project project = FileOwnerQuery.getOwner(uri);
                if (project != null) {
                    FileObject projectDir = project.getProjectDirectory();
                    File file = new File(uri);
                    file = FileUtil.normalizeFile(file);
                    FileObject fob = FileUtil.toFileObject(file);
                    String relativePath = FileUtil.getRelativePath(projectDir, fob);
                    displayName = relativePath;
                }
            } catch (URISyntaxException usex) {}
        }
        String title = header.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            displayName = title + " (" + displayName + ")"; // NOI18N
        }
        setDisplayName(displayName);
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenResourceAction.class);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[] {
            SystemAction.get(OpenResourceAction.class)
        };
    }

    /**
     * Factory for children of {@code StyleSheetNode}.
     */
    static class StyleSheetChildFactory extends ChildFactory<Rule> {
        /** CSS domain of the corresponding WebKit debugging. */
        private CSS css;
        /** Header of the style sheet. */
        private StyleSheetHeader header;

        /**
         * Creates a new {@code StyleSheetChildFactory}.
         *
         * @param css CSS domain of the corresponding WebKit debugging.
         * @param header header of the style sheet.
         */
        StyleSheetChildFactory(CSS css, StyleSheetHeader header) {
            this.css = css;
            this.header = header;
        }

        @Override
        protected boolean createKeys(List<Rule> toPopulate) {
            StyleSheetBody body = css.getStyleSheet(header.getStyleSheetId());
            toPopulate.addAll(body.getRules());
            return true;
        }

        @Override
        protected Node createNodeForKey(Rule key) {
            return new RuleNode(key, header.getSourceURL());
        }

    }

}
