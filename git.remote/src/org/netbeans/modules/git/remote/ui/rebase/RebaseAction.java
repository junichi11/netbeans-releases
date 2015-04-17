/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.git.remote.ui.rebase;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.netbeans.modules.git.remote.cli.GitBranch;
import org.netbeans.modules.git.remote.cli.GitClient.RebaseOperationType;
import org.netbeans.modules.git.remote.cli.GitException;
import org.netbeans.modules.git.remote.cli.GitRebaseResult;
import org.netbeans.modules.git.remote.cli.GitRepositoryState;
import org.netbeans.modules.git.remote.cli.GitRevisionInfo;
import org.netbeans.modules.git.remote.cli.SearchCriteria;
import org.netbeans.modules.git.remote.cli.progress.ProgressMonitor;
import org.netbeans.modules.git.remote.Git;
import org.netbeans.modules.git.remote.client.GitClient;
import org.netbeans.modules.git.remote.client.GitClientExceptionHandler;
import org.netbeans.modules.git.remote.client.GitProgressSupport;
import org.netbeans.modules.git.remote.client.ProgressDelegate;
import org.netbeans.modules.git.remote.ui.actions.GitAction;
import org.netbeans.modules.git.remote.ui.actions.SingleRepositoryAction;
import org.netbeans.modules.git.remote.ui.conflicts.ResolveConflictsExecutor;
import org.netbeans.modules.git.remote.ui.output.OutputLogger;
import org.netbeans.modules.git.remote.ui.repository.RepositoryInfo;
import org.netbeans.modules.git.remote.utils.GitUtils;
import org.netbeans.modules.git.remote.utils.LogUtils;
import org.netbeans.modules.git.remote.utils.ResultProcessor;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.remotefs.versioning.hooks.GitHook;
import org.netbeans.modules.remotefs.versioning.hooks.GitHookContext;
import org.netbeans.modules.remotefs.versioning.hooks.VCSHooks;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Mnemonics;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Ondrej Vrabec
 */
@ActionID(id = "org.netbeans.modules.git.remote.ui.rebase.RebaseAction", category = "GitRemote")
@ActionRegistration(displayName = "#LBL_RebaseAction_Name")
@NbBundle.Messages({
    "LBL_RebaseAction_Name=&Rebase...", "LBL_RebaseAction_PopupName=Rebase..."
})
public class RebaseAction extends SingleRepositoryAction {

    private static final Logger LOG = Logger.getLogger(RebaseAction.class.getName());
    private static final String NETBEANS_REBASE_ORIGHEAD = "netbeans-rebase.orighead"; //NOI18N
    private static final String NETBEANS_REBASE_UPSTREAM = "netbeans-rebase.upstream"; //NOI18N
    private static final String NETBEANS_REBASE_ONTO = "netbeans-rebase.onto"; //NOI18N
    private static final String REBASE_MERGE_DIR = "rebase-merge"; //NOI18N
    private static final String REBASE_APPLY_DIR = "rebase-apply"; //NOI18N
    private static final String MESSAGE = "message"; //NOI18N

    @Override
    protected void performAction (VCSFileProxy repository, VCSFileProxy[] roots, VCSContext context) {
        RepositoryInfo info = RepositoryInfo.getInstance(repository);
        info.refresh();
        rebase(repository, info);
    }

    @NbBundle.Messages({
        "# {0} - repository state", "MSG_RebaseAction_rebaseNotAllowed=Rebase is not allowed in this state: {0}",
        "CTL_RebaseAction.continueButton.text=C&ontinue",
        "CTL_RebaseAction.continueButton.TTtext=Continue the interrupted rebase",
        "CTL_RebaseAction.abortButton.text=Abo&rt",
        "CTL_RebaseAction.abortButton.TTtext=Abort the interrupted rebase",
        "CTL_RebaseAction.skipButton.text=&Skip",
        "CTL_RebaseAction.skipButton.TTtext=Skip the current commit and continue the interrupted rebase",
        "LBL_Rebase.rebasingState.title=Unfinished Rebase",
        "# {0} - repository name", "MSG_Rebase.rebasingState.text=Repository {0} is in the middle of an unfinished rebase.\n"
            + "Do you want to continue or abort the unfinished rebase\n"
            + "or skip the current commit from the rebase?"
    })
    private void rebase (VCSFileProxy repository, RepositoryInfo info) {
        GitRepositoryState state = info.getRepositoryState();
        if (state == GitRepositoryState.SAFE) {
            GitBranch current = info.getActiveBranch();
            Rebase rebase = new Rebase(repository, info.getBranches(), current);
            if (rebase.showDialog()) {
                runRebase(repository, RebaseOperationType.BEGIN, rebase.getRevisionSource(), rebase.getRevisionBase(),
                        rebase.getRevisionDest());
            }
        } else if (state == GitRepositoryState.REBASING) {
            // abort or continue?
            JButton btnContinue = new JButton();
            Mnemonics.setLocalizedText(btnContinue, Bundle.CTL_RebaseAction_continueButton_text());
            btnContinue.setToolTipText(Bundle.CTL_RebaseAction_continueButton_TTtext());
            JButton btnAbort = new JButton();
            Mnemonics.setLocalizedText(btnAbort, Bundle.CTL_RebaseAction_abortButton_text());
            btnAbort.setToolTipText(Bundle.CTL_RebaseAction_abortButton_TTtext());
            JButton btnSkip = new JButton();
            Mnemonics.setLocalizedText(btnSkip, Bundle.CTL_RebaseAction_skipButton_text());
            btnSkip.setToolTipText(Bundle.CTL_RebaseAction_skipButton_TTtext());
            Map<Object, RebaseOperationType> operations = new HashMap<>();
            operations.put(btnContinue, RebaseOperationType.CONTINUE);
            operations.put(btnSkip, RebaseOperationType.SKIP);
            operations.put(btnAbort, RebaseOperationType.ABORT);
            Object value = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                    Bundle.MSG_Rebase_rebasingState_text(repository.getName()),
                    Bundle.LBL_Rebase_rebasingState_title(),
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    new Object[] { btnContinue, btnSkip, btnAbort, NotifyDescriptor.CANCEL_OPTION }, 
                    btnContinue));
            RebaseOperationType op = operations.get(value);
            if (op != null) {
                runRebase(repository, op, null, null, null);
            }
        } else {
            GitClientExceptionHandler.annotate(Bundle.MSG_RebaseAction_rebaseNotAllowed(state));
        }
    }

    @NbBundle.Messages("MSG_RebaseAction_progress=Rebasing...")
    private void runRebase (final VCSFileProxy repository, final RebaseOperationType op, final String source, final String upstream,
    final String onto) {
        GitProgressSupport supp = new GitProgressSupport() {

            @Override
            protected void perform () {
                try {
                    final ProgressDelegate progress = getProgress();
                    GitUtils.runWithoutIndexing(new Callable<Void>() {
                        @Override
                        public Void call () throws Exception {
                            String lSource = source;
                            String lOnto = onto;
                            String lUpstream = upstream;
                            if (op == RebaseOperationType.BEGIN) {
                                
                            } else {
                                if (op != RebaseOperationType.ABORT && !checkRebaseFolder(repository)) {
                                    return null;
                                }
                                lSource = getRebaseFileContent(repository, NETBEANS_REBASE_ORIGHEAD);
                                lOnto = getOnto(repository);
                                lUpstream = getRebaseFileContent(repository, NETBEANS_REBASE_UPSTREAM);
                            }
                            GitClient client = getClient();
                            RebaseResultProcessor rrp = new RebaseResultProcessor(client, repository, lOnto, lUpstream, lSource,
                                    progress, getProgressSupport().getLogger());
                            RebaseOperationType nextAction = op;
                            while (nextAction != null && !isCanceled()) {
                                GitRebaseResult result = client.rebase(nextAction, lOnto, getProgressMonitor());
                                rrp.processResult(result);
                                nextAction = rrp.getNextAction();
                            }
                            return null;
                        }
                    });
                } catch (GitException ex) {
                    GitClientExceptionHandler.notifyException(ex, true);
                } finally {
                    setDisplayName(NbBundle.getMessage(GitAction.class, "LBL_Progress.RefreshingStatuses")); //NOI18N
                    Git.getInstance().getFileStatusCache().refreshAllRoots(Collections.<VCSFileProxy, Collection<VCSFileProxy>>singletonMap(repository, Git.getInstance().getSeenRoots(repository)));
                    GitUtils.headChanged(repository);
                }
            }

            private GitProgressSupport getProgressSupport () {
                return this;
            }
        };
        supp.start(Git.getInstance().getRequestProcessor(repository), repository, Bundle.MSG_RebaseAction_progress());
    }

    @NbBundle.Messages({
        "LBL_RebaseAction.continueExternalRebase.title=Cannot Continue Rebase",
        "MSG_RebaseAction.continueExternalRebase.text=Rebase was probably started by an external tool "
                + "in non-interactive mode. \nNetBeans IDE cannot continue and finish the action.\n\n"
                + "Please use the external tool or abort and restart rebase inside NetBeans."
    })
    private boolean checkRebaseFolder (VCSFileProxy repository) {
        VCSFileProxy folder = getRebaseFolder(repository);
        VCSFileProxy messageFile = VCSFileProxy.createFileProxy(folder, MESSAGE);
        if (messageFile.exists()) {
            return true;
        } else {
            Mutex.EVENT.readAccess(new Runnable() {
                @Override
                public void run () {
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(),
                        Bundle.MSG_RebaseAction_continueExternalRebase_text(),
                        Bundle.LBL_RebaseAction_continueExternalRebase_title(),
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            return false;
        }
    }

    @NbBundle.Messages({
        "MSG_RebaseAction.noMergeStrategies=Cannot continue rebase. It was probably started externally "
            + "with an unknown algorithm to the NetBeans IDE.\n"
            + "Please use the external tool you started rebase with to finish it."
    })
    private static String getRebaseFileContent (VCSFileProxy repository, String filename) throws GitException {
        VCSFileProxy rebaseFolder = getRebaseFolder(repository);
        if (rebaseFolder.exists()) {
            VCSFileProxy file = VCSFileProxy.createFileProxy(rebaseFolder, filename);
            if (VCSFileProxySupport.canRead(file)) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(file.getInputStream(false), "UTF-8")); //NOI18N
                    return br.readLine();
                } catch (IOException ex) {
                    LOG.log(Level.FINE, null, ex);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException ex) {}
                    }
                }
            }
        } else {
            throw new GitException(Bundle.MSG_RebaseAction_noMergeStrategies());
        }
        return null;
    }

    private static VCSFileProxy getRebaseFolder (VCSFileProxy repository) {
        VCSFileProxy rebaseFolder = VCSFileProxy.createFileProxy(GitUtils.getGitFolderForRoot(repository), REBASE_APPLY_DIR);
        if (!rebaseFolder.exists()) {
            rebaseFolder = VCSFileProxy.createFileProxy(GitUtils.getGitFolderForRoot(repository), REBASE_MERGE_DIR);
        }
        return rebaseFolder;
    }

    private static String getOnto (VCSFileProxy repository) throws GitException {
        String onto = getRebaseFileContent(repository, NETBEANS_REBASE_ONTO);
        if (onto == null) {
            onto = getRebaseFileContent(repository, "onto-name"); //NOI18N
        }
        if (onto == null) {
            onto = getRebaseFileContent(repository, "onto_name"); //NOI18N
        }
        if (onto == null) {
            onto = getRebaseFileContent(repository, "onto"); //NOI18N
        }
        return onto;
    }
    
    public static class RebaseResultProcessor extends ResultProcessor {

        private final OutputLogger logger;
        private final String onto;
        private final String origHead;
        private final String upstream;
        private RebaseOperationType nextAction;
        private final ProgressDelegate progress;
        
        public RebaseResultProcessor (GitClient client, VCSFileProxy repository, String onto, String upstream, String origHead,
                ProgressDelegate progress, OutputLogger logger) {
            super(client, repository, onto, progress.getProgressMonitor());
            this.origHead = origHead;
            this.onto = onto;
            this.upstream = upstream;
            this.logger = logger;
            this.progress = progress;
        }
        
        @NbBundle.Messages({
            "# {0} - rebase status", "MSG_RebaseAction.result=Rebase Result: {0}\n",
            "# {0} - head commit id", "MSG_RebaseAction.result.aborted=Rebase aborted and the current HEAD reset to {0}\n",
            "MSG_RebaseAction.result.conflict=Rebase interrupted because of conflicts in:\n",
            "# {0} - head commit id", "MSG_RebaseAction.result.ok=Rebase successfully finished and HEAD now points to {0}:\n",
            "# {0} - rebase target revision", "MSG_RebaseAction.result.alreadyUpToDate=HEAD already in sync with {0}"
        })
        public void processResult (GitRebaseResult result) {
            nextAction = null;
            StringBuilder sb = new StringBuilder(Bundle.MSG_RebaseAction_result(result.getRebaseStatus().toString()));
            GitRevisionInfo info;
            String base = null;
            try {
                info = client.log(GitUtils.HEAD, GitUtils.NULL_PROGRESS_MONITOR);
                if (origHead != null && onto != null) {
                    GitRevisionInfo i = client.getCommonAncestor(new String[] { origHead, onto }, pm);
                    if (i != null) {
                        base = i.getRevision();
                    }
                }
            } catch (GitException ex) {
                GitClientExceptionHandler.notifyException(ex, true);
                return;
            }
            persistNBConfig();
            boolean logActions = false;
            switch (result.getRebaseStatus()) {
                case ABORTED:
                    sb.append(Bundle.MSG_RebaseAction_result_aborted(info.getRevision()));
                    GitUtils.printInfo(sb, info);
                    break;
                case FAILED:
                case CONFLICTS:
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "Local modifications in WT during rebase: {0} - {1}", new Object[] { repository, result.getFailures() }); //NOI18N
                    }
                    try {
                        if (resolveLocalChanges(result.getFailures().toArray(new VCSFileProxy[result.getFailures().size()]))) {
                            nextAction = RebaseOperationType.BEGIN;
                        }
                    } catch (GitException ex) {
                        GitClientExceptionHandler.notifyException(ex, true);
                    }
                    break;
                case NOTHING_TO_COMMIT:
                    nextAction = resolveNothingToCommit();
                    break;
                case FAST_FORWARD:
                case OK:
                    sb.append(Bundle.MSG_RebaseAction_result_ok(info.getRevision()));
                    GitUtils.printInfo(sb, info, false);
                    logActions = true;
                    updatePushHooks();
                    break;
                case STOPPED:
                    sb.append(Bundle.MSG_RebaseAction_result_conflict());
                    printConflicts(logger, sb, result.getConflicts());
                    nextAction = resolveRebaseConflicts(result.getConflicts());
                    break;
                case UP_TO_DATE:
                    sb.append(Bundle.MSG_RebaseAction_result_alreadyUpToDate(onto));
                    break;
            }
            if (sb.length() > 0) {
                logger.outputLine(sb.toString());
            }
            if (logActions) {
                logRebaseResult(info.getRevision(), base);
            }
        }

        public RebaseOperationType getNextAction () {
            return nextAction;
        }

        @NbBundle.Messages({
            "LBL_RebaseResultProcessor.abortButton.text=&Abort",
            "LBL_RebaseResultProcessor.abortButton.TTtext=Abort the interrupted rebase and reset back to the original commit.",
            "LBL_RebaseResultProcessor.resolveButton.text=&Resolve",
            "LBL_RebaseResultProcessor.resolveButton.TTtext=Files in conflict will be opened in a Resolve Conflict dialog.",
            "LBL_RebaseResultProcessor.resolveConflicts=Resolve Conflicts",
            "MSG_RebaseResultProcessor.resolveConflicts=Rebase produced unresolved conflicts.\n"
                + "You can resolve them manually or review them in the Versioning view\n"
                + "or completely abort the rebase.",
            "LBL_RebaseResultProcessor.revertButton.text=&Revert",
            "LBL_RebaseResultProcessor.revertButton.TTtext=Revert local changes to the state in the HEAD and removes unversioned files.",
            "LBL_RebaseResultProcessor.reviewButton.text=Re&view",
            "LBL_RebaseResultProcessor.reviewButton.TTtext=Opens the Versioning view and lists the conflicted files.",
            "MSG_Rebase.resolving=Resolving conflicts..."
        })
        private RebaseOperationType resolveRebaseConflicts (Collection<VCSFileProxy> conflicts) {
            RebaseOperationType action = null;
            JButton abort = new JButton();
            Mnemonics.setLocalizedText(abort, Bundle.LBL_RebaseResultProcessor_abortButton_text());
            abort.setToolTipText(Bundle.LBL_RebaseResultProcessor_abortButton_TTtext());
            JButton resolve = new JButton();
            Mnemonics.setLocalizedText(resolve, Bundle.LBL_RebaseResultProcessor_resolveButton_text());
            resolve.setToolTipText(Bundle.LBL_RebaseResultProcessor_resolveButton_TTtext());
            JButton review = new JButton();
            Mnemonics.setLocalizedText(review, Bundle.LBL_RebaseResultProcessor_reviewButton_text());
            review.setToolTipText(Bundle.LBL_RebaseResultProcessor_reviewButton_TTtext());
            Object o = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                    Bundle.MSG_RebaseResultProcessor_resolveConflicts(),
                    Bundle.LBL_RebaseResultProcessor_resolveConflicts(),
                    NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE,
                    new Object[] { resolve, review, abort, NotifyDescriptor.CANCEL_OPTION }, resolve));
            if (o == review) {
                openInVersioningView(conflicts);
            } else if (o == resolve) {
                GitProgressSupport supp = new ResolveConflictsExecutor(conflicts.toArray(new VCSFileProxy[conflicts.size()]));
                supp.start(Git.getInstance().getRequestProcessor(repository), repository, Bundle.MSG_Rebase_resolving());
            } else if (o == abort) {
                action = RebaseOperationType.ABORT;
            }
            return action;
        }

        @NbBundle.Messages({
            "LBL_RebaseResultProcessor.nothingToCommit=Nothing to Commit",
            "MSG_RebaseResultProcessor.nothingToCommit=No modifications to commit for the current rebase step.\n"
                + "Do you want to skip the commit from the rebase and continue?",
            "LBL_RebaseResultProcessor.skipButton.text=&Skip",
            "LBL_RebaseResultProcessor.skipButton.TTtext=Skip the commit and continue rebase."
        })
        private RebaseOperationType resolveNothingToCommit () {
            RebaseOperationType action = null;
            JButton abort = new JButton();
            Mnemonics.setLocalizedText(abort, Bundle.LBL_RebaseResultProcessor_abortButton_text());
            abort.setToolTipText(Bundle.LBL_RebaseResultProcessor_abortButton_TTtext());
            JButton skip = new JButton();
            Mnemonics.setLocalizedText(skip, Bundle.LBL_RebaseResultProcessor_skipButton_text());
            skip.setToolTipText(Bundle.LBL_RebaseResultProcessor_skipButton_TTtext());
            Object o = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                    Bundle.MSG_RebaseResultProcessor_nothingToCommit(),
                    Bundle.LBL_RebaseResultProcessor_nothingToCommit(),
                    NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE,
                    new Object[] { skip, abort, NotifyDescriptor.CANCEL_OPTION }, skip));
            if (o == skip) {
                action = RebaseOperationType.SKIP;
            } else if (o == abort) {
                action = RebaseOperationType.ABORT;
            }
            return action;
        }

        private void persistNBConfig () {
            if (onto != null && upstream != null && origHead != null) {
                VCSFileProxy rebaseFolder = getRebaseFolder(repository);
                if (rebaseFolder.canWrite()) {
                    try {
                        VCSFileProxySupport.copyStreamToFile(new ByteArrayInputStream(onto.getBytes("UTF-8")), VCSFileProxy.createFileProxy(rebaseFolder, NETBEANS_REBASE_ONTO)); //NOI18N
                        VCSFileProxySupport.copyStreamToFile(new ByteArrayInputStream(upstream.getBytes("UTF-8")), VCSFileProxy.createFileProxy(rebaseFolder, NETBEANS_REBASE_UPSTREAM)); //NOI18N
                        VCSFileProxySupport.copyStreamToFile(new ByteArrayInputStream(origHead.getBytes("UTF-8")), VCSFileProxy.createFileProxy(rebaseFolder, NETBEANS_REBASE_ORIGHEAD)); //NOI18N
                    } catch (IOException ex) {
                        LOG.log(Level.INFO, null, ex);
                    }
                }
            }
        }

        private void logRebaseResult (String newHeadId, String base) {
            if (base != null && newHeadId != null) {
                String oldId = base;
                String newId = newHeadId;
                String branchName = RepositoryInfo.getInstance(repository).getActiveBranch().getName();
                LogUtils.logBranchUpdateReview(repository, branchName, oldId, newId, logger);
            }
        }
        
        @NbBundle.Messages("MSG_RebaseAction.updatingHooks=Updating push hooks")
        private void updatePushHooks () {
            if (onto != null && upstream != null && origHead != null) {
                Collection<GitHook> hooks = VCSHooks.getInstance().getHooks(GitHook.class);
                if (!hooks.isEmpty() && !pm.isCanceled()) {
                    progress.setProgress(Bundle.MSG_RebaseAction_updatingHooks());
                    try {
                        GitHookContext.LogEntry[] originalEntries = getEntries(client, upstream, origHead, pm);
                        if (pm.isCanceled()) {
                            return;
                        }
                        GitHookContext.LogEntry[] newEntries = getEntries(client, onto, GitUtils.HEAD, pm);
                        if (pm.isCanceled()) {
                            return;
                        }
                        Map<String, String> mapping = findChangesetMapping(originalEntries, newEntries);
                        for (GitHook gitHook : hooks) {
                            gitHook.afterCommitReplace(new GitHookContext(new VCSFileProxy[] { repository }, null, originalEntries),
                                    new GitHookContext(new VCSFileProxy[] { repository }, null, newEntries),
                                    mapping);
                        }
                    } catch (GitException ex) {
                        LOG.log(Level.INFO, null, ex);
                    }
                }
            }
        }
    }
    
    private static Map<String, String> findChangesetMapping (GitHookContext.LogEntry[] originalEntries, GitHookContext.LogEntry[] newEntries) {
        Map<String, String> mapping = new HashMap<>(originalEntries.length);
        for (GitHookContext.LogEntry original : originalEntries) {
            boolean found = false;
            for (GitHookContext.LogEntry newEntry : newEntries) {
                if (original.getChangeset().equals(newEntry.getChangeset()) || (
                        original.getDate().equals(newEntry.getDate())
                        && original.getAuthor().equals(newEntry.getAuthor())
                        && original.getMessage().equals(newEntry.getMessage()))) {
                    // is it really the same commit???
                    mapping.put(original.getChangeset(), newEntry.getChangeset());
                    found = true;
                    break;
                }
            }
            if (!found) {
                // delete ????
                mapping.put(original.getChangeset(), null);
            }
        }
        return mapping;
    }
    
    private static GitHookContext.LogEntry[] getEntries (GitClient client, String revisionFrom, String revisionTo,
            ProgressMonitor pm) throws GitException {
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionFrom(revisionFrom);
        crit.setRevisionTo(revisionTo);
        GitRevisionInfo[] log = client.log(crit, false, pm);
        return convertToEntries(log);
    }    

    private static GitHookContext.LogEntry[] convertToEntries (GitRevisionInfo[] messages) {
        List<GitHookContext.LogEntry> entries = new ArrayList<>(messages.length);
        for (GitRevisionInfo msg : messages) {
            entries.add(new GitHookContext.LogEntry(
                    msg.getFullMessage(),
                    msg.getAuthor().toString(),
                    msg.getRevision(),
                    new Date(msg.getCommitTime())));
        }
        return entries.toArray(new GitHookContext.LogEntry[entries.size()]);
    }
    
}
