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

package org.netbeans.modules.git.remote.ui.clone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.libs.git.remote.GitBranch;
import org.netbeans.libs.git.remote.GitException;
import org.netbeans.libs.git.remote.GitRemoteConfig;
import org.netbeans.libs.git.remote.GitSubmoduleStatus;
import org.netbeans.libs.git.remote.GitTransportUpdate;
import org.netbeans.libs.git.remote.GitTransportUpdate.Type;
import org.netbeans.libs.git.remote.GitURI;
import org.netbeans.modules.git.remote.Git;
import org.netbeans.modules.git.remote.client.CredentialsCallback;
import org.netbeans.modules.git.remote.client.GitClient;
import org.netbeans.modules.git.remote.client.GitClientExceptionHandler;
import org.netbeans.modules.git.remote.client.GitProgressSupport;
import org.netbeans.modules.git.remote.ui.actions.ContextHolder;
import org.netbeans.modules.git.remote.ui.output.OutputLogger;
import org.netbeans.modules.git.remote.utils.GitUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.netbeans.modules.versioning.util.ProjectUtilities;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author Tomas Stupka
 */
@ActionID(id = "org.netbeans.modules.git.remote.ui.clone.CloneAction", category = "GitRemote")
@ActionRegistration(displayName = "#LBL_CloneAction_Name")
@ActionReferences({
   @ActionReference(path="Versioning/Git/Actions/Global", position=310)
})
@NbBundle.Messages("LBL_CloneAction_Name=&Clone...")
public class CloneAction implements ActionListener, HelpCtx.Provider {
    private final VCSContext ctx;
    private static final Logger LOG = Logger.getLogger(CloneAction.class.getName());

    public CloneAction (ContextHolder ctx) {
        this.ctx = ctx.getContext();
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.netbeans.modules.git.remote.ui.clone.CloneAction");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        Utils.logVCSActionEvent("Git"); //NOI18N
        String cloneFromPath = null;
        FileSystem fs = null;
        if(ctx != null) {
            Set<VCSFileProxy> roots = ctx.getRootFiles();
            if(roots.size() == 1) {
                Lookup l = ctx.getElements();
                Project project = null;
                if(l != null) {
                    Collection<? extends Node> nodes = l.lookupAll(Node.class);
                    if(nodes != null && !nodes.isEmpty()) {
                        project = nodes.iterator().next().getLookup().lookup(Project.class);
                    }
                }
                if(project == null) {
                    FileObject fo = roots.iterator().next().toFileObject();
                    if(fo != null && fo.isFolder()) {
                        try {
                            project = ProjectManager.getDefault().findProject(fo);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IllegalArgumentException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
                if(project != null) {
                    FileObject fo = project.getProjectDirectory();
                    VCSFileProxy file = VCSFileProxy.createFileProxy(fo);
                    if(file != null) {
                        if(Git.getInstance().isManaged(file) ) {
                            cloneFromPath = Git.getInstance().getRepositoryRoot(file).getPath();
                        }
                    }
                }
            }
            if (roots.size() > 1) {
                fs = VCSFileProxySupport.getFileSystem(roots.iterator().next());
            }
        }
        if (fs != null) {
            performClone(fs, cloneFromPath, null);
        }
    }

    private static void performClone(FileSystem fs, String url, PasswordAuthentication pa) throws MissingResourceException {
        performClone(fs, url, pa, false);
    }
    
    @NbBundle.Messages({
        "LBL_Clone.confirmSubmoduleInit.title=Initialize Submodules",
        "MSG_Clone.confirmSubmoduleInit.text=Uninitialized submodules found in the cloned repository.\n\n"
                + "Do you want to automatically initialize and clone them?",
        "MSG_Clone.progress.initializingRepository=Initializing repository",
        "MSG_Clone.progress.fetchingCommits=Fetching commits",
        "# {0} - remote name", "MSG_Clone.progress.settingRemote=Setting up \"{0}\" remote",
        "# {0} - branch name", "MSG_Clone.progress.creatingBranch=Creating \"{0}\" branch",
        "# {0} - branch name", "MSG_Clone.progress.checkingoutBranch=Checking-out \"{0}\" branch",
        "MSG_Clone.progress.refreshingFiles=Refreshing files",
        "MSG_Clone.progress.scanningForProjects=Scanning for NetBeans projects",
        "MSG_Clone.progress.checkingForSubmodules=Checking for submodules",
        "MSG_Clone.progress.initializingSubmodules=Initializing submodules",
        "MSG_Clone.progress.updatingSubmodules=Updating submodules",
        "# {0} - submodule folder", "MSG_Clone.progress.updatingSubmodule=Submodule {0}"
    })
    public static VCSFileProxy performClone(FileSystem fs, String url, PasswordAuthentication pa, boolean waitFinished) throws MissingResourceException {
        final CloneWizard wiz = new CloneWizard(fs, pa, url);
        Boolean ok = Mutex.EVENT.readAccess(new Mutex.Action<Boolean>() {
            @Override
            public Boolean run () {
                return wiz.show();
            }
        });
        if (Boolean.TRUE.equals(ok)) {            
            final GitURI remoteUri = wiz.getRemoteURI();
            final VCSFileProxy destination = wiz.getDestination();
            final String remoteName = wiz.getRemoteName();
            List<String> branches = wiz.getBranchNames();
            final List<String> refSpecs;
            if (branches == CloneWizard.ALL_BRANCHES) {
                // all branches to fetch
                refSpecs = Collections.<String>singletonList(GitUtils.getGlobalRefSpec(remoteName));
            } else {
                refSpecs = new ArrayList<String>(branches.size());
                for (String branchName : branches) {
                    refSpecs.add(GitUtils.getRefSpec(branchName, remoteName));
                }
            }
            final GitBranch branch = wiz.getBranch();
            final boolean scan = wiz.scanForProjects();
            
            GitProgressSupport supp = new GitProgressSupport(10) {
                @Override
                protected void perform () {
                    try {
                        GitUtils.runWithoutIndexing(new Callable<Void>() {
                            @Override
                            public Void call () throws Exception {
                                GitClient client = getClient();
                                setDisplayName(Bundle.MSG_Clone_progress_initializingRepository());
                                client.init(getProgressMonitor());
                                setDisplayName(Bundle.MSG_Clone_progress_fetchingCommits(), 1);
                                Map<String, GitTransportUpdate> updates = client.fetch(remoteUri.toPrivateString(), refSpecs, getProgressMonitor());
                                log(updates);

                                if(isCanceled()) {
                                    return null;
                                }
                                
                                List<String> refs = Arrays.asList(GitUtils.getGlobalRefSpec(remoteName));
                                setDisplayName(Bundle.MSG_Clone_progress_settingRemote(remoteName), 2);
                                String username = new CredentialsCallback().getUsername(remoteUri.toString(), "");
                                GitURI uriToSave = remoteUri;
                                if (username != null && !username.isEmpty()) {
                                    uriToSave = uriToSave.setUser(username);
                                }
                                client.setRemote(new CloneRemoteConfig(remoteName, uriToSave, refs).toGitRemote(), getProgressMonitor());
                                org.netbeans.modules.versioning.util.Utils.logVCSExternalRepository("GIT", remoteUri.getHost()); //NOI18N
                                if (branch == null) {
                                    setDisplayName(Bundle.MSG_Clone_progress_creatingBranch(GitUtils.MASTER), 1);
                                    client.createBranch(GitUtils.MASTER, GitUtils.PREFIX_R_REMOTES + remoteName + "/" + GitUtils.MASTER, getProgressMonitor());
                                } else {
                                    setDisplayName(Bundle.MSG_Clone_progress_checkingoutBranch(branch.getName()), 1);
                                    client.createBranch(branch.getName(), remoteName + "/" + branch.getName(), getProgressMonitor());
                                    client.checkoutRevision(branch.getName(), true, getProgressMonitor());
                                    client.reset(branch.getName(), org.netbeans.libs.git.remote.GitClient.ResetType.HARD, getProgressMonitor());
                                }

                                setDisplayName(Bundle.MSG_Clone_progress_refreshingFiles(), 2);
                                Git.getInstance().getFileStatusCache().refreshAllRoots(destination);
                                
                                if (!isCanceled()) {
                                    initSubmodules();
                                }
                                
                                Git.getInstance().versionedFilesChanged();                       

                                if(scan && !isCanceled()) {
                                    setDisplayName(Bundle.MSG_Clone_progress_scanningForProjects(), 1);
                                    scanForProjects(destination);
                                }
                                return null;
                            }

                            private void initSubmodules () {
                                try {
                                    GitClient client = getClient();
                                    setDisplayName(Bundle.MSG_Clone_progress_checkingForSubmodules(), 1);
                                    Map<VCSFileProxy, GitSubmoduleStatus> statuses = client.getSubmoduleStatus(new VCSFileProxy[0], getProgressMonitor());
                                    List<VCSFileProxy> toInit = new ArrayList<>(statuses.size());
                                    for (Map.Entry<VCSFileProxy, GitSubmoduleStatus> e : statuses.entrySet()) {
                                        if (e.getValue().getStatus() == GitSubmoduleStatus.StatusType.UNINITIALIZED) {
                                            toInit.add(e.getKey());
                                        }
                                    }
                                    if (!isCanceled() && !toInit.isEmpty() && confirmSubmoduleInit(toInit)) {
                                        setDisplayName(Bundle.MSG_Clone_progress_initializingSubmodules(), 1);
                                        client.initializeSubmodules(toInit.toArray(new VCSFileProxy[toInit.size()]), getProgressMonitor());
                                        setDisplayName(Bundle.MSG_Clone_progress_updatingSubmodules(), 1);
                                        for (VCSFileProxy submoduleRoot : toInit) {
                                            if (isCanceled()) {
                                                return;
                                            }
                                            try {
                                                setProgress(Bundle.MSG_Clone_progress_updatingSubmodule(submoduleRoot.getName()));
                                                client.updateSubmodules(new VCSFileProxy[] { submoduleRoot }, getProgressMonitor());
                                            } catch (GitException ex) {
                                                LOG.log(Level.INFO, null, ex);
                                            }
                                        }
                                    } else {
                                        updateProgress(2);
                                    }
                                } catch (GitException ex) {
                                    LOG.log(Level.INFO, null, ex);
                                }
                            }

                            private boolean confirmSubmoduleInit (List<VCSFileProxy> subrepos) {
                                return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                                        Bundle.MSG_Clone_confirmSubmoduleInit_text(),
                                        Bundle.LBL_Clone_confirmSubmoduleInit_title(),
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                            }
                        }, destination);
                        
                    } catch (GitException ex) {
                        GitClientExceptionHandler.notifyException(ex, true);
                    }
                }

                private void log (Map<String, GitTransportUpdate> updates) {
                    OutputLogger logger = getLogger();
                    if (updates.isEmpty()) {
                        logger.outputLine(NbBundle.getMessage(CloneAction.class, "MSG_CloneAction.updates.noChange")); //NOI18N
                    } else {
                        for (Map.Entry<String, GitTransportUpdate> e : updates.entrySet()) {
                            GitTransportUpdate update = e.getValue();
                            if (update.getType() == Type.BRANCH) {
                                logger.outputLine(NbBundle.getMessage(CloneAction.class, "MSG_CloneAction.updates.updateBranch", new Object[] { //NOI18N
                                    update.getLocalName(), 
                                    update.getOldObjectId(),
                                    update.getNewObjectId(),
                                    update.getResult(),
                                }));
                            } else {
                                logger.outputLine(NbBundle.getMessage(CloneAction.class, "MSG_CloneAction.updates.updateTag", new Object[] { //NOI18N
                                    update.getLocalName(), 
                                    update.getResult(),
                                }));
                            }
                        }
                    }
                }

                public void scanForProjects (VCSFileProxy workingFolder) {
                    Map<Project, Set<Project>> checkedOutProjects = new HashMap<Project, Set<Project>>();
                    checkedOutProjects.put(null, new HashSet<Project>()); // initialize root project container
                    VCSFileProxy normalizedWorkingFolder = workingFolder.normalizeFile();
                    FileObject fo = normalizedWorkingFolder.toFileObject();
                    if (fo == null || !fo.isFolder()) {
                        return;
                    } else {
                        ProjectUtilities.scanForProjects(fo, checkedOutProjects);
                    }
                    if (isCanceled()) {
                        return;
                    }
                    // open project selection
                    org.netbeans.modules.remotefs.versioning.api.ProjectUtilities.openClonedOutProjects(checkedOutProjects, workingFolder);
                }
            };
            Task task = supp.start(Git.getInstance().getRequestProcessor(destination), destination, NbBundle.getMessage(CloneAction.class, "LBL_CloneAction.progressName")); //NOI18N
            if(waitFinished) {
                task.waitFinished();
            }
            return destination;
        }
        return null;
    }
    
    private static class CloneRemoteConfig {
        private final String remoteName;
        private final GitURI remoteUri;
        private final List<String> refSpecs;
        public CloneRemoteConfig(String remoteName, GitURI remoteUri, List<String> refSpecs) {
            this.remoteName = remoteName;
            this.remoteUri = remoteUri;
            this.refSpecs = refSpecs;
        }
        public String getRemoteName() {
            return remoteName;
        }
        public List<String> getUris() {
            return Arrays.asList(remoteUri.toPrivateString());
        }
        public List<String> getPushUris() {
            return Collections.emptyList();
        }
        public List<String> getFetchRefSpecs() {
            return refSpecs;
        }
        public List<String> getPushRefSpecs() {
            return Collections.emptyList();
        }

        private GitRemoteConfig toGitRemote () {
            return new GitRemoteConfig(remoteName, getUris(), getPushUris(), getFetchRefSpecs(), getPushRefSpecs());
        }
    }    
}
