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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.mercurial.ui.shelve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.Action;
import org.netbeans.modules.mercurial.FileInformation;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.mercurial.HgProgressSupport;
import org.netbeans.modules.mercurial.Mercurial;
import org.netbeans.modules.mercurial.OutputLogger;
import org.netbeans.modules.mercurial.ui.actions.ContextAction;
import org.netbeans.modules.mercurial.ui.diff.ExportDiffChangesAction;
import org.netbeans.modules.mercurial.ui.diff.Setup;
import org.netbeans.modules.mercurial.ui.update.RevertModificationsAction;
import org.netbeans.modules.mercurial.util.HgUtils;
import org.netbeans.modules.versioning.shelve.ShelveChangesActionsRegistry.ShelveChangesActionProvider;
import org.netbeans.modules.versioning.shelve.ShelveChangesSupport;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Ondra Vrabec
 */
@ActionID(id = "org.netbeans.modules.mercurial.ui.shelve.ShelveChangesAction", category = "Mercurial")
@ActionRegistration(displayName = "#CTL_ShelveChanges_Title")
public class ShelveChangesAction extends ContextAction {
    private static ShelveChangesActionProvider ACTION_PROVIDER;

    @Override
    public boolean enable(Node[] nodes) {
        VCSContext ctx = HgUtils.getCurrentContext(nodes);
        if(!HgUtils.isFromHgRepository(ctx) || !Mercurial.getInstance().getFileStatusCache().containsFileOfStatus(ctx, FileInformation.STATUS_LOCAL_CHANGE, true)) {
            return false;
        }
        return super.enable(nodes);
    }

    @Override
    protected String getBaseName (Node[] activatedNodes) {
        return "CTL_ShelveChanges_Title"; //NOI18N
    }

    @Override
    protected void performContextAction (Node[] nodes) {
        VCSContext ctx = HgUtils.getCurrentContext(nodes);
        File root = HgUtils.getRootFile(ctx);
        if (root == null) {
            Mercurial.LOG.log(Level.FINE, "No versioned folder in the selected context for {0}", nodes); //NOI18N
            return;
        }
        HgShelveChangesSupport supp = new HgShelveChangesSupport();
        if (supp.prepare()) {
            RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(root);
            supp.startAsync(rp, root, ctx);
        }
    }

    private static class HgShelveChangesSupport extends ShelveChangesSupport {
        private HgProgressSupport support;
        private OutputLogger logger;
        private Set<File> filteredRoots;

        @Override
        protected void exportPatch (File toFile, File commonParent) throws IOException {
            support.setDisplayName(NbBundle.getMessage(ShelveChangesAction.class, "MSG_ShelveChanges.progress.exporting")); //NOI18N
            List<Setup> setups = new ArrayList<Setup>(filteredRoots.size());
            for (File file : filteredRoots) {
                Setup setup = new Setup(file, null, Setup.DIFFTYPE_LOCAL);
                setups.add(setup);
            }
            SystemAction.get(ExportDiffChangesAction.class).exportDiff(setups, toFile, commonParent, support);
        }

        @Override
        protected void postExportCleanup () {
            Map<File, Set<File>> sorted = HgUtils.sortUnderRepository(filteredRoots);
            for (Map.Entry<File, Set<File>> e : sorted.entrySet()) {
                File root = e.getKey();
                Set<File> roots = e.getValue();
                if (!roots.isEmpty()) {
                    support.setDisplayName(NbBundle.getMessage(ShelveChangesAction.class, "MSG_ShelveChanges.progress.reverting", root.getName())); //NOI18N
                    RevertModificationsAction.performRevert(root, null, roots.toArray(new File[roots.size()]), HgModuleConfig.getDefault().getBackupOnRevertModifications(), logger);
                }
            }
        }

        @Override
        protected boolean isCanceled () {
            return support == null ? false : support.isCanceled();
        }
        
        private void startAsync (RequestProcessor rp, File root, final VCSContext context) {
            support = new HgProgressSupport() {
                @Override
                protected void perform () {
                    logger = getLogger();
                    filteredRoots = new HashSet<File>(Arrays.asList(HgUtils.getModifiedFiles(context, FileInformation.STATUS_LOCAL_CHANGE, true)));
                    shelveChanges(filteredRoots.toArray(new File[filteredRoots.size()]));
                }
            };
            support.start(rp, root, NbBundle.getMessage(ShelveChangesAction.class, "LBL_ShelveChanges_Progress")); //NOI18N
        }
    };
    
    public static ShelveChangesActionProvider getProvider () {
        if (ACTION_PROVIDER == null) {
            ACTION_PROVIDER = new ShelveChangesActionProvider() {
                @Override
                public Action getAction () {
                    Action a = SystemAction.get(ShelveChangesAction.class);
                    Utils.setAcceleratorBindings("Actions/Mercurial", a); //NOI18N
                    return a;
                }
            };
        };
        return ACTION_PROVIDER;
    }
}
