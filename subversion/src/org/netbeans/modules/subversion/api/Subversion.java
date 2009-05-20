/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.subversion.api;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.netbeans.modules.subversion.FileInformation;
import org.netbeans.modules.subversion.FileStatusCache;
import org.netbeans.modules.subversion.RepositoryFile;
import org.netbeans.modules.subversion.SvnFileNode;
import org.netbeans.modules.subversion.SvnModuleConfig;
import org.netbeans.modules.subversion.client.SvnClient;
import org.netbeans.modules.subversion.client.SvnClientExceptionHandler;
import org.netbeans.modules.subversion.client.SvnClientFactory;
import org.netbeans.modules.subversion.client.SvnProgressSupport;
import org.netbeans.modules.subversion.hooks.spi.SvnHook;
import org.netbeans.modules.subversion.ui.browser.Browser;
import org.netbeans.modules.subversion.ui.checkout.CheckoutAction;
import org.netbeans.modules.subversion.ui.commit.CommitAction;
import org.netbeans.modules.subversion.ui.commit.CommitOptions;
import org.netbeans.modules.subversion.ui.history.SearchHistoryAction;
import org.netbeans.modules.subversion.ui.repository.RepositoryConnection;
import org.netbeans.modules.subversion.util.Context;
import org.netbeans.modules.subversion.util.SvnUtils;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

/**
 *
 * @author Marian Petras
 */
public class Subversion {


    private static final String WORKINGDIR_KEY_PREFIX = "working.dir."; //NOI18N
    private static final String RELATIVE_PATH_ROOT = "/";               //NOI18N
    public static final String CLIENT_UNAVAILABLE_ERROR_MESSAGE = "SVN client unavailable"; //NOI18N

    /**
     * Displays a dialog for selection of one or more Subversion repository
     * folders.
     *
     * @param  dialogTitle  title of the dialog
     * @param  repositoryUrl  URL of the Subversion repository to browse
     * @return  relative paths of the selected folders,
     *          or {@code null} if the user cancelled the selection
     * @throws  java.net.MalformedURLException  if the given URL is invalid
     * @throws  IOException  some error, e.g. unavailable client
     */
    public static String[] selectRepositoryFolders(String dialogTitle,
                                                   String repositoryUrl)
                throws MalformedURLException, IOException {
        return selectRepositoryFolders(dialogTitle, repositoryUrl, null, null);
    }

    /**
     * Displays a dialog for selection of one or more Subversion repository
     * folders.
     * 
     * @param  dialogTitle  title of the dialog
     * @param  repositoryUrl  URL of the Subversion repository to browse
     * @param  username  username for access to the given repository
     * @param  password  password for access to the given repository
     * @return  relative paths of the selected folders,
     *          or {@code null} if the user cancelled the selection
     * @throws  java.net.MalformedURLException  if the given URL is invalid
     * @throws  IOException  some error, e.g. unavailable client
     */
    public static String[] selectRepositoryFolders(String dialogTitle,
                                                   String repositoryUrl,
                                                   String username,
                                                   String password)
                throws MalformedURLException, IOException {

        if (!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }
        
        RepositoryConnection conn = new RepositoryConnection(repositoryUrl);
        SVNUrl svnUrl = conn.getSvnUrl();
        SVNRevision svnRevision = conn.getSvnRevision();

        RepositoryFile repositoryFile = new RepositoryFile(svnUrl, svnRevision);
        Browser browser = new Browser(dialogTitle,
                                      Browser.BROWSER_SHOW_FILES | Browser.BROWSER_FOLDERS_SELECTION_ONLY,
                                      repositoryFile,
                                      null,     //files to select
                                      (username != null) ? username : "", //NOI18N
                                      ((username != null) && (password != null)) ? password : "", //NOI18N
                                      null,     //node actions
                                      Browser.BROWSER_HELP_ID_CHECKOUT);    //PENDING - is this help ID correct?

        RepositoryFile[] selectedFiles = browser.getRepositoryFiles();
        if ((selectedFiles == null) || (selectedFiles.length == 0)) {
            return null;
        }

        String[] relativePaths = makeRelativePaths(repositoryFile, selectedFiles);
        return relativePaths;
    }

    /**
     * Checks out a given folder from a given Subversion repository. The method blocks
     * until the whole chcekout is done. Do not call in AWT.
     *
     * @param  repositoryUrl  URL of the Subversion repository
     * @param  relativePaths  relative paths denoting folder the folder in the
     *                       repository that is to be checked-out; to specify
     *                       that the whole repository folder should be
     *                       checked-out, use use a sing arrya containig one empty string
     * @param  localFolder  local folder to store the checked-out files to
     * @param  scanForNewProjects scans the created working copy for netbenas projects
     *                            and presents a dialog to open them eventually
     */
    public static void checkoutRepositoryFolder(String repositoryUrl,
                                                   String[] relativePaths,
                                                   File localFolder,
                                                   boolean scanForNewProjects)
            throws MalformedURLException, IOException {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote repository. Do not call in awt!";
        checkoutRepositoryFolder(repositoryUrl,
                                        relativePaths,
                                        localFolder,
                                        null,
                                        null,
                                        false,
                                        scanForNewProjects);
    }

    /**
     * Checks out a given folder from a given Subversion repository. The method blocks
     * until the whole chcekout is done. Do not call in AWT.
     *
     * @param  repositoryUrl  URL of the Subversion repository
     * @param  relativePaths  relative paths denoting folder the folder in the
     *                       repository that is to be checked-out; to specify
     *                       that the whole repository folder should be
     *                       checked-out, use use a sing arrya containig one empty string
     * @param  localFolder  local folder to store the checked-out files to
     * @param  atLocalFolderLevel if true the contents from the remote url with be
     *         checked out into the given local folder, otherwise a new folder with the remote
     *         folders name will be created in the local folder
     * @param  scanForNewProjects scans the created working copy for netbenas projects
     *                            and presents a dialog to open them eventually
     * @throws IOException when an error occurrs
     */
    public static void checkoutRepositoryFolder(String repositoryUrl,
                                                   String[] relativePaths,
                                                   File localFolder,
                                                   boolean atLocalFolderLevel,
                                                   boolean scanForNewProjects)
            throws MalformedURLException, IOException {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote repository. Do not call in awt!";
        checkoutRepositoryFolder(repositoryUrl,
                                        relativePaths,
                                        localFolder,
                                        null,
                                        null,
                                        scanForNewProjects);
    }

    /**
     * Checks out a given folder from a given Subversion repository. The method blocks
     * until the whole chcekout is done. Do not call in AWT.
     *
     * @param  repositoryUrl  URL of the Subversion repository
     * @param  relativePaths  relative paths denoting folder the folder in the
     *                       repository that is to be checked-out; to specify
     *                       that the whole repository folder should be
     *                       checked-out, use a string array containig one empty string
     * @param  localFolder  local folder to store the checked-out files to
     * @param  username  username for access to the given repository
     * @param  password  password for access to the given repository
     * @param  scanForNewProjects scans the created working copy for netbenas projects
     *                            and presents a dialog to open them eventually
     * @throws IOException when an error occurrs
     */
    public static void checkoutRepositoryFolder(String repositoryUrl,
                                                   String[] repoRelativePaths,
                                                   File localFolder,
                                                   String username,
                                                   String password,
                                                   boolean scanForNewProjects) throws MalformedURLException, IOException {
        checkoutRepositoryFolder(
                repositoryUrl,
                repoRelativePaths,
                localFolder,
                username,
                password,
                false,
                scanForNewProjects);
    }

    /**
     * Checks out a given folder from a given Subversion repository. The method blocks
     * until the whole chcekout is done. Do not call in AWT.
     *
     * @param  repositoryUrl  URL of the Subversion repository
     * @param  relativePaths  relative paths denoting folder the folder in the
     *                       repository that is to be checked-out; to specify
     *                       that the whole repository folder should be
     *                       checked-out, use a string array containig one empty string
     * @param  localFolder  local folder to store the checked-out files to
     * @param  username  username for access to the given repository
     * @param  password  password for access to the given repository
     * @param  atLocalFolderLevel if true the contents from the remote url with be
     *         checked out into the given local folder, otherwise a new folder with the remote
     *         folders name will be created in the local folder
     * @param  scanForNewProjects scans the created working copy for netbenas projects
     *                            and presents a dialog to open them eventually
     * @throws IOException when an error occurrs
     */
    public static void checkoutRepositoryFolder(String repositoryUrl,
                                                   String[] repoRelativePaths,
                                                   File localFolder,
                                                   String username,
                                                   String password,
                                                   boolean atLocalFolderLevel,
                                                   boolean scanForNewProjects)
            throws MalformedURLException, IOException {

        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote repository. Do not call in awt!";

        if (!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }

        RepositoryConnection conn = new RepositoryConnection(repositoryUrl);

        SVNUrl svnUrl = conn.getSvnUrl();
        SVNRevision svnRevision = conn.getSvnRevision();

        SvnClient client = getClient(svnUrl, username, password);

        RepositoryFile[] repositoryFiles;
        if(repoRelativePaths.length == 0 || (repoRelativePaths.length == 1 && repoRelativePaths[0].trim().equals(""))) {
            repositoryFiles = new RepositoryFile[1];
            repositoryFiles[0] = new RepositoryFile(svnUrl, ".", svnRevision);
        } else {
            repositoryFiles = new RepositoryFile[repoRelativePaths.length];
            for (int i = 0; i < repoRelativePaths.length; i++) {
                String repoRelativePath = repoRelativePaths[i];
                repoRelativePath = polishRelativePath(repoRelativePath);
                repositoryFiles[i] = new RepositoryFile(svnUrl, repoRelativePath, svnRevision);
            }
        }

        boolean notVersionedYet = localFolder.exists() && !SvnUtils.isManaged(localFolder);

        CheckoutAction.performCheckout(
                svnUrl,
                client,
                repositoryFiles,
                localFolder,
                atLocalFolderLevel,
                scanForNewProjects).waitFinished();

        try {
            storeWorkingDir(new URL(repositoryUrl), localFolder.toURI().toURL());
        } catch (Exception e) {
            Logger.getLogger(Subversion.class.getName()).log(Level.FINE, "Cannot store subversion workdir preferences", e);
        }

        if(notVersionedYet) {
            getSubversion().versionedFilesChanged();
            SvnUtils.refreshParents(localFolder);
            getSubversion().getStatusCache().refreshRecursively(localFolder);
        }
    }

    /**
     * Creates a new remote folder with the given url. Missing parents also wil be created.
     *
     * @param url
     * @param user
     * @param password
     * @param message
     * @throws java.net.MalformedURLException
     * @throws IOException when an error occurrs
     */
    public static void mkdir(String url, String user, String password, String message) throws MalformedURLException, IOException {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote repository. Do not call in  awt!";

        if (!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }

        SVNUrl svnUrl = new SVNUrl(url);

        SvnClient client = getClient(svnUrl, user, password);
        try {
            client.mkdir(svnUrl, true, message);
        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, false, true);
            throw new IOException(ex.getMessage());
        }

    }

    /**
     * Adds a remote url for the combos used in Checkout and Import wizard
     *
     * @param url
     * @throws java.net.MalformedURLException
     */
    public static void addRecentUrl(String url) throws MalformedURLException {
        new SVNUrl(url); // check url format

        RepositoryConnection rc = new RepositoryConnection(url);
        SvnModuleConfig.getDefault().insertRecentUrl(rc);
    }

    /**
     * Commits all local chages under the given root
     *
     * @param root
     * @param message
     * @throws IOException when an error occurrs
     */
    public static void commit(final File[] roots, final String user, final String password, final String message) throws IOException {
        if (!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }

        FileStatusCache cache = getSubversion().getStatusCache();
        File[] files = cache.listFiles(roots, FileInformation.STATUS_LOCAL_CHANGE);

        if (files.length == 0) {
            return;
        }

        SvnFileNode[] nodes = new SvnFileNode[files.length];
        for (int i = 0; i < files.length; i++) {
            nodes[i] = new SvnFileNode(files[i]);
        }
        CommitOptions[] commitOptions = SvnUtils.createDefaultCommitOptions(nodes, false);
        final Map<SvnFileNode, CommitOptions> commitFiles = new HashMap<SvnFileNode, CommitOptions>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            commitFiles.put(nodes[i], commitOptions[i]);
        }

        try {
            final SVNUrl repositoryUrl = SvnUtils.getRepositoryRootUrl(roots[0]);
            RequestProcessor rp = getSubversion().getRequestProcessor(repositoryUrl);
            SvnProgressSupport support = new SvnProgressSupport() {
                public void perform() {
                    SvnClient client;
                    try {
                        client = getSubversion().getClient(repositoryUrl, user, password, this);
                    } catch (SVNClientException ex) {
                        SvnClientExceptionHandler.notifyException(ex, true, true); // should not hapen
                        return;
                    }
                    CommitAction.performCommit(client, message, commitFiles, new Context(roots), this, false, Collections.<SvnHook>emptyList() );
                }
            };
            support.start(rp, repositoryUrl, org.openide.util.NbBundle.getMessage(CommitAction.class, "LBL_Commit_Progress")).waitFinished(); // NOI18N

        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, true, true);
        }
    }

    /**
     * Opens search history panel with a specific DiffResultsView, which does not moves accross differences but initially fixes on the given line.
     * Right panel shows current local changes if the file, left panel shows revisions in the file's repository.
     * Do not run in AWT, IllegalStateException is thrown.
     * Validity of the arguments is checked and result is returned as a return value
     * @param path requested file absolute path. Must be a versioned file (not a folder), otherwise false is returned and the panel would not open
     * @param lineNumber requested line number to fix on
     * @return true if suplpied arguments are valid and the search panel is opened, otherwise false
     */
    public static boolean showFileHistory (final File file, final int lineNumber) throws IOException {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote repository. Do not call in  awt!";

        if (!file.exists()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Trying to show history for non-existent file {0}", file.getAbsolutePath());
            return false;
        }
        if (!file.isFile()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Trying to show history for a folder {0}", file.getAbsolutePath());
            return false;
        }
        if (!SvnUtils.isManaged(file)) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.INFO, "Trying to show history for an unmanaged file {0}", file.getAbsolutePath());
            return false;
        }
        if(!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.WARNING, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }

        /**
         * Open in AWT
         */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SearchHistoryAction.openSearch(file, lineNumber);
            }
        });
        return true;
    }

    /**
     * Tries to resolve the given URL and determine if the URL represents a subversion repository
     * Should not be called inside AWT, this might access network.
     * @param url repository URL
     * @return true if:
     * <ul>
     * <li>protocol is svn</li>
     * <li>protocol is svn+</li>
     * <li>svn client is available and invoked 'svn info' returns valid data</li>
     * </ul>
     * Note that this may not be 100% successful for private projects requiring authentication.
     */
    public static boolean isRepository (final String url) {
        boolean retval = false;
        try {
            // do not call getSubversion().checkClientAvailable() at this point.
            // The tested url may be from another vcs and we don't want to open a
            // dialog with an error just becasue svn is not installed.
            SvnClientFactory.checkClientAvailable();
        } catch (SVNClientException ex) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.INFO, "svn client not available");
            return false;
        }
        RepositoryConnection conn = new RepositoryConnection(url);
        SVNUrl svnUrl = null;
        try {
            svnUrl = new SVNUrl(conn.getSvnUrl().toString()); // this is double check filters file://
        } catch (MalformedURLException ex) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.FINE, "Invalid svn url " + url, ex);
        }
        if (svnUrl != null) {
            String protocol = svnUrl.getProtocol();
            if ("svn".equals(protocol) || protocol.startsWith("svn+")) {
                // svn protocol belongs to subversion module
                retval = true;
            } else {
                SvnClient client = null;
                try {
                    // DO NOT HANDLE ANY EXCEPTIONS
                    client = getSubversion().getClient(svnUrl, conn.getUsername(), conn.getPassword(), 0);
                } catch (SVNClientException ex) {
                    org.netbeans.modules.subversion.Subversion.LOG.log(Level.INFO, "Cannot create client for url: " + url, ex);
                }
                if (client != null) {
                    try {
                        ISVNInfo info = client.getInfo(svnUrl);
                        if (info != null) {
                            // repository url is valid
                            retval = true;
                        }
                    } catch (SVNClientException ex) {
                        String msg = ex.getMessage().toLowerCase();
                        if(SvnClientExceptionHandler.isAuthentication(msg)
                                || SvnClientExceptionHandler.isNoCertificate(msg)) {
                            retval = true;
                        } else {
                            org.netbeans.modules.subversion.Subversion.LOG.log(Level.FINE, "Invalid url: " + url, ex);
                        }
                    }
                }
            }
        }
        return retval;
    }

    /**
     * Opens standard checkout wizard
     * @param url repository url to checkout
     * @throws java.net.MalformedURLException in case the url is invalid
     * @throws IOException when an error occurrs
     */
    public static void openCheckoutWizard (final String url) throws MalformedURLException, IOException {
        addRecentUrl(url);
        if (!getSubversion().checkClientAvailable()) {
            org.netbeans.modules.subversion.Subversion.LOG.log(Level.INFO, "Subversion client is unavailable");
            throw new IOException(CLIENT_UNAVAILABLE_ERROR_MESSAGE);
        }
        SystemAction.get(CheckoutAction.class).performAction();
    }

    private static org.netbeans.modules.subversion.Subversion getSubversion() {
        return org.netbeans.modules.subversion.Subversion.getInstance();
    }

    private static String[] makeRelativePaths(RepositoryFile repositoryFile,
                                              RepositoryFile[] selectedFiles) {
        String[] result = new String[selectedFiles.length];

        String[] repoPathSegments = repositoryFile.getPathSegments();

        for (int i = 0; i < selectedFiles.length; i++) {
            RepositoryFile selectedFile = selectedFiles[i];
            result[i] = makeRelativePath(repoPathSegments, selectedFile.getPathSegments());
        }
        return result;
    }

    private static String makeRelativePath(String[] repoPathSegments,
                                           String[] selectedPathSegments) {
        assert isPrefixOf(repoPathSegments, selectedPathSegments);
        int delta = selectedPathSegments.length - repoPathSegments.length;

        if (delta == 0) {
            return "/";     //root of the repository selected           //NOI18N
        }

        if (delta == 1) {
            return selectedPathSegments[selectedPathSegments.length - 1];
        }

        StringBuilder buf = new StringBuilder(120);
        int startIndex = repoPathSegments.length;
        int endIndex = selectedPathSegments.length;
        buf.append(selectedPathSegments[startIndex++]);
        for (int i = startIndex; i < endIndex; i++) {
            buf.append('/');
            buf.append(selectedPathSegments[i]);
        }
        return buf.toString();
    }

    private static boolean isPrefixOf(String[] prefix, String[] path) {
        if (prefix.length > path.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (!path[i].equals(prefix[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stores working directory for specified remote root
     * into NetBeans preferences.
     * These are later used in kenai.ui module
     */
    private static void storeWorkingDir(URL remoteUrl, URL localFolder) {
        Preferences prf = NbPreferences.forModule(Subversion.class);
        prf.put(WORKINGDIR_KEY_PREFIX + remoteUrl, localFolder.toString());
    }

    private static String polishRelativePath(String path) {
        if (path.length() == 0) {
            throw new IllegalArgumentException("empty path");           //NOI18N
        }
        path = removeDuplicateSlashes(path);
        if (path.equals("/")) {                                         //NOI18N
            return RELATIVE_PATH_ROOT;
        }
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static boolean isRootRelativePath(String relativePath) {
        return relativePath.equals(RELATIVE_PATH_ROOT);
    }

    private static String removeDuplicateSlashes(String str) {
        int len = str.length();

        StringBuilder buf = null;
        boolean lastWasSlash = false;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == '/') {
                if (lastWasSlash) {
                    if (buf == null) {
                        buf = new StringBuilder(len);
                        buf.append(str, 0, i);  //up to the first slash in a row
                    }
                    continue;
                }
                lastWasSlash = true;
            } else {
                lastWasSlash = false;
            }
            if (buf != null) {
                buf.append(c);
            }
        }
        return (buf != null) ? buf.toString() : str;
    }

    private static SvnClient getClient(SVNUrl url, String username, String password) {       
        try {
            if(username != null) {
                password = password != null ? password : "";                    // NOI18N
                return getSubversion().getClient(url, username, password);
            } else {
                return getSubversion().getClient(url);
            }
        } catch (SVNClientException ex) {
            SvnClientExceptionHandler.notifyException(ex, false, true);
        }        
        return null;
    }
}
