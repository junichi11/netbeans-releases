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

package org.netbeans.modules.php.project.connections.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.netbeans.modules.php.project.connections.RemoteException;
import org.netbeans.modules.php.project.connections.common.PasswordPanel;
import org.netbeans.modules.php.project.connections.common.RemoteUtils;
import org.netbeans.modules.php.project.connections.ftp.FtpConfiguration.Encryption;
import org.netbeans.modules.php.project.connections.spi.RemoteClient;
import org.netbeans.modules.php.project.connections.spi.RemoteFile;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * @author Tomas Mysik
 */
public class FtpClient implements RemoteClient {

    static final Logger LOGGER = Logger.getLogger(FtpClient.class.getName());

    private static final RequestProcessor KEEP_ALIVE_RP = new RequestProcessor("PHP FTP client keep-alive thread", 1);
    // store not provided passwords in memory only
    private static final Map<Integer, String> PASSWORDS = new HashMap<Integer, String>();
    private static final int[] PERMISSIONS_ACCESSES = new int[] {
        FTPFile.USER_ACCESS, FTPFile.GROUP_ACCESS, FTPFile.WORLD_ACCESS
    };

    private final FtpConfiguration configuration;
    // @GuardedBy(this)
    private final FTPClient ftpClient;
    private final ProtocolCommandListener protocolCommandListener;
    private final int keepAliveInterval;
    private final RequestProcessor.Task keepAliveTask;
    private final AtomicInteger keepAliveCounter = new AtomicInteger();

    // @GuardedBy(this)
    private Long timestampDiff = null;


    public FtpClient(FtpConfiguration configuration, InputOutput io) {
        assert configuration != null;
        this.configuration = configuration;

        LOGGER.log(Level.FINE, "FTP client creating");
        ftpClient = createFtpClient(configuration);
        if (io != null) {
            protocolCommandListener = new PrintCommandListener(io);
            addProtocolCommandListener();
            LOGGER.log(Level.FINE, "Protocol command listener added");
        } else {
            protocolCommandListener = null;
        }
        keepAliveInterval = configuration.getKeepAliveInterval() * 1000;
        if (keepAliveInterval <= 0) {
            keepAliveTask = null;
        } else {
            keepAliveTask = KEEP_ALIVE_RP.create(new Runnable() {
                @Override
                public void run() {
                    keepAlive();
                }
            });
        }
    }

    private FTPClient createFtpClient(FtpConfiguration configuration) {
        FtpConfiguration.Security security = configuration.getSecurity();
        if (!security.isPresent()) {
            LOGGER.log(Level.FINE, "No encryption used");
            return new FTPClient();
        }
        Encryption encryption = security.getEncryption();
        LOGGER.log(Level.FINE, "Used encryption {0}", encryption.name());
        // can be debugged by setting -J-Djavax.net.debug=all
        return new FTPSClient(encryption.getProtocol(), encryption.isImplicit());
    }

    private void addProtocolCommandListener() {
        if (protocolCommandListener == null) {
            return;
        }
        synchronized (this) {
            ftpClient.addProtocolCommandListener(protocolCommandListener);
        }
    }

    private void removeProtocolCommandListener() {
        if (protocolCommandListener == null) {
            return;
        }
        synchronized (this) {
            ftpClient.removeProtocolCommandListener(protocolCommandListener);
        }
    }

    @Override
    public synchronized void connect() throws RemoteException {
        try {
            // first of all - not to force user to put password faster than timeout
            String password = getPassword();

            // connect
            int timeout = configuration.getTimeout() * 1000;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Connecting to {0} [timeout: {1} ms]", new Object[] {configuration.getHost(), timeout});
            }
            ftpClient.setDefaultTimeout(timeout);
            ftpClient.setControlKeepAliveReplyTimeout(keepAliveInterval);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Keep-alive interval is {0} ms", keepAliveInterval);
            }
            ftpClient.connect(configuration.getHost(), configuration.getPort());
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Reply is {0}", getReplyString());
            }
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                LOGGER.fine("Disconnecting because of negative reply");
                ftpClient.disconnect();
                throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpRefusedConnection", configuration.getHost()), getReplyString());
            }

            // login
            LOGGER.log(Level.FINE, "Login as {0}", configuration.getUserName());
            if (!ftpClient.login(configuration.getUserName(), password)) {
                LOGGER.fine("Login unusuccessful -> logout");
                ftpClient.logout();
                // remove password from a memory storage
                PASSWORDS.remove(configuration.hashCode());
                throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpLoginFailed"), getReplyString());
            }
            LOGGER.fine("Login successful");

            // set ssl commands?
            if (configuration.getSecurity().isPresent()) {
                FTPSClient ftpsClient = (FTPSClient) ftpClient;
                // set protection buffer size
                ftpsClient.execPBSZ(0);
                if (!configuration.getSecurity().isOnlyLoginEncrypted()) {
                    // set data channel protection to private
                    ftpsClient.execPROT("P"); // NOI18N
                }
            }

            if (configuration.isPassiveMode()) {
                LOGGER.fine("Setting passive mode");
                ftpClient.enterLocalPassiveMode();
            }

            // binary mode as a default
            LOGGER.fine("Setting file type to BINARY");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Remote system is {0}", ftpClient.getSystemType());
            }

            LOGGER.fine("Setting data timeout");
            ftpClient.setDataTimeout(timeout);
            scheduleKeepAlive();
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    LOGGER.log(Level.FINE, "Exception while disconnecting", e);
                }
            }
            LOGGER.log(Level.INFO, "Exception while connecting", ex);
            // # 169796
            if (ex instanceof UnknownHostException) {
                // no I18N to be consistent
                ex = new IOException("Unknown host " + configuration.getHost()); // NOI18N
            }
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotConnect", configuration.getHost()), ex, getReplyString());
        }
    }

    @Override
    public synchronized void disconnect() throws RemoteException {
        LOGGER.log(Level.FINE, "Remote client trying to disconnect");
        if (keepAliveTask != null) {
            keepAliveTask.cancel();
        }
        if (ftpClient.isConnected()) {
            LOGGER.log(Level.FINE, "Remote client connected -> disconnecting");
            try {
                ftpClient.logout();
            } catch (IOException ex) {
                LOGGER.log(Level.FINE, "Error while logout", ex);
                if (configuration.getIgnoreDisconnectErrors()) {
                    LOGGER.log(Level.FINE, "Error while logout ignored by configuration");
                } else {
                    throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotLogout", configuration.getHost()), ex, getReplyString());
                }
            } finally {
                try {
                    ftpClient.disconnect();
                    LOGGER.log(Level.FINE, "Remote client disconnected");
                } catch (IOException ex) {
                    LOGGER.log(Level.FINE, "Remote client disconnected with exception", ex);
                }
            }
        }
    }

    private String getPassword() {
        String password = configuration.getPassword();
        assert password != null;
        if (password.length() > 0) {
            return password;
        }
        password = PASSWORDS.get(configuration.hashCode());
        if (password != null) {
            return password;
        }
        PasswordPanel passwordPanel = PasswordPanel.forUser(configuration.getDisplayName(), configuration.getUserName());
        if (passwordPanel.open()) {
            password = passwordPanel.getPassword();
            PASSWORDS.put(configuration.hashCode(), password);
            return password;
        }
        return ""; // NOI18N
    }

    @Override
    public synchronized String getReplyString() {
        String reply = ftpClient.getReplyString();
        if (reply == null) {
            return null;
        }
        return reply.trim();
    }

    @Override
    public synchronized String getNegativeReplyString() {
        int replyCode = ftpClient.getReplyCode();
        if (FTPReply.isNegativePermanent(replyCode)
                || FTPReply.isNegativeTransient(replyCode)) {
            return getReplyString();
        }
        return null;
    }

    @Override
    public synchronized boolean isConnected() {
        return ftpClient.isConnected();
    }

    @Override
    public synchronized String printWorkingDirectory() throws RemoteException {
        try {
            return ftpClient.printWorkingDirectory();
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while pwd", ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotPwd", configuration.getHost()), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean storeFile(String remote, InputStream local) throws RemoteException {
        try {
            boolean fileStored = ftpClient.storeFile(remote, local);
            scheduleKeepAlive();
            return fileStored;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while storing file " + remote, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotStoreFile", remote), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean deleteFile(String pathname) throws RemoteException {
        try {
            boolean fileDeleted = ftpClient.deleteFile(pathname);
            scheduleKeepAlive();
            return fileDeleted;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while deleting file " + pathname, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotDeleteFile", pathname), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean deleteDirectory(String pathname) throws RemoteException {
        try {
            boolean directoryDeleted = ftpClient.removeDirectory(pathname);
            scheduleKeepAlive();
            return directoryDeleted;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while deleting file " + pathname, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotDeleteFile", pathname), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean rename(String from, String to) throws RemoteException {
        try {
            boolean fileRenamed = ftpClient.rename(from, to);
            scheduleKeepAlive();
            return fileRenamed;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, String.format("Error while renaming file %s -> %s", from, to), ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotRenameFile", from, to), ex, getReplyString());
        }
    }

    @Override
    public synchronized List<RemoteFile> listFiles() throws RemoteException {
        List<RemoteFile> result = null;
        String pwd = null;
        try {
            pwd = ftpClient.printWorkingDirectory();
            FTPFile[] files = ftpClient.listFiles(pwd);
            result = new ArrayList<RemoteFile>(files.length);
            for (FTPFile f : files) {
                // #142682
                if (f == null) {
                    // hmm, really weird...
                    LOGGER.log(Level.FINE, "NULL returned for listing of {0}", pwd);
                    continue;
                }
                result.add(new RemoteFileImpl(f, pwd));
            }
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while listing files for " + pwd, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotListFiles", pwd), ex, getReplyString());
        }
        scheduleKeepAlive();
        return result;
    }

    @Override
    public synchronized boolean retrieveFile(String remote, OutputStream local) throws RemoteException {
        try {
            boolean fileRetrieved = ftpClient.retrieveFile(remote, local);
            scheduleKeepAlive();
            return fileRetrieved;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while retrieving file " + remote, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotStoreFile", remote), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean changeWorkingDirectory(String pathname) throws RemoteException {
        try {
            return ftpClient.changeWorkingDirectory(pathname);
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while changing directory " + pathname, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotChangeDirectory", pathname), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean makeDirectory(String pathname) throws RemoteException {
        try {
            boolean directoryMade = ftpClient.makeDirectory(pathname);
            scheduleKeepAlive();
            return directoryMade;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while creating directory " + pathname, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotCreateDirectory", pathname), ex, getReplyString());
        }
    }

    @Override
    public int getPermissions(String path) throws RemoteException {
        try {
            return getPermissions(getFile(path));
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while getting permissions for " + path, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotGetPermissions", path), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean setPermissions(int permissions, String path) throws RemoteException {
        try {
            return ftpClient.sendSiteCommand("chmod " + permissions + " " + path); // NOI18N
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Error while setting permissions for " + path, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotSetPermissions", path), ex, getReplyString());
        }
    }

    @Override
    public synchronized boolean exists(String parent, String name) throws RemoteException {
        try {
            boolean found = false;
            ftpClient.changeWorkingDirectory(parent);
            for (RemoteFile file : listFiles()) {
                if (file.getName().equals(name)) {
                    found = true;
                    break;
                }
            }
            scheduleKeepAlive();
            return found;
        } catch (IOException ex) {
            WindowsJdk7WarningPanel.warn();
            String fullPath = parent + "/" + name; // NOI18N
            LOGGER.log(Level.FINE, "Error while checking existence of " + fullPath, ex);
            throw new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotCheckFileExistence", fullPath), ex, getReplyString());
        }
    }

    private synchronized FTPFile getFile(String path) throws IOException {
        assert path != null && path.trim().length() > 0;

        FTPFile[] files = ftpClient.listFiles(path);
        // in fact, the size of the list should be exactly 1
        LOGGER.fine(String.format("Exactly 1 file should be found for %s; found %d", path, files.length));
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    private int getPermissions(FTPFile file) {
        // see #listFiles(PathInfo)
        if (file == null) {
            return -1;
        }
        // not the fastest solution but at least, it's readable
        StringBuilder sb = new StringBuilder(3);
        for (int access : PERMISSIONS_ACCESSES) {
            int rights = 0;
            if (file.hasPermission(access, FTPFile.READ_PERMISSION)) {
                rights += 4;
            }
            if (file.hasPermission(access, FTPFile.WRITE_PERMISSION)) {
                rights += 2;
            }
            if (file.hasPermission(access, FTPFile.EXECUTE_PERMISSION)) {
                rights += 1;
            }
            sb.append(rights);
        }
        assert sb.length() == 3 : "Buffer lenght is incorrect: " + sb.length();
        int rights = Integer.valueOf(sb.toString());
        return rights;
    }

    synchronized long getTimestampDiff() {
        if (timestampDiff != null) {
            return timestampDiff;
        }
        timestampDiff = 0L;
        // try to calculate the time difference between remote and local pc
        removeProtocolCommandListener();
        try {
            File tmpFile = File.createTempFile("netbeans-timestampdiff-", ".txt"); // NOI18N
            long now = tmpFile.lastModified();

            final String remotePath = configuration.getInitialDirectory() + "/" + tmpFile.getName(); // NOI18N
            InputStream is = new FileInputStream(tmpFile);
            try {
                if (storeFile(remotePath, is)) {
                    FTPFile remoteFile = getFile(remotePath);
                    if (remoteFile != null) {
                        timestampDiff = now - remoteFile.getTimestamp().getTimeInMillis();
                    }
                    deleteFile(remotePath);
                }
            } finally {
                is.close();
                if (!tmpFile.delete()) {
                    tmpFile.deleteOnExit();
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "Unable to calculate time difference", ex);
        } finally {
            addProtocolCommandListener();
        }
        return timestampDiff;
    }

    synchronized void keepAlive() {
        if (!ftpClient.isConnected()) {
            LOGGER.log(Level.FINE, "Ending keep-alive (NOOP) for {0}, not connected", configuration.getHost());
            keepAliveTask.cancel();
            return;
        }
        try {
            LOGGER.log(Level.FINE, "Keep-alive (NOOP) for {0}", configuration.getHost());
            ftpClient.noop();
            ftpClient.getReplyString();
            preventNoOperationTimeout();
            scheduleKeepAlive();
        } catch (IOException ex) {
            silentDisconnect();
            WindowsJdk7WarningPanel.warn();
            LOGGER.log(Level.FINE, "Keep-alive (NOOP/PWD) error for " + configuration.getHost(), ex);
            // #201828
            RemoteException exc = new RemoteException(NbBundle.getMessage(FtpClient.class, "MSG_FtpCannotKeepAlive", configuration.getHost()), ex, getReplyString());
            RemoteUtils.processRemoteException(exc);
        }
    }

    void silentDisconnect() {
        try {
            disconnect();
        } catch (RemoteException ex) {
            LOGGER.log(Level.FINE, "Error while silently disconnecting", ex);
        }
    }

    // #203987
    private void preventNoOperationTimeout() throws IOException {
        int counter = keepAliveCounter.incrementAndGet();
        if (counter == 10) {
            keepAliveCounter.set(0);
            LOGGER.log(Level.FINE, "Keep-alive (PWD) for {0}", configuration.getHost());
            removeProtocolCommandListener();
            try {
                ftpClient.pwd();
                ftpClient.getReplyString();
            } finally {
                addProtocolCommandListener();
            }
        }
    }

    private void scheduleKeepAlive() {
        if (keepAliveTask != null) {
            keepAliveTask.schedule(keepAliveInterval);
        }
    }

    private static final class PrintCommandListener implements ProtocolCommandListener {
        private final InputOutput io;

        public PrintCommandListener(InputOutput io) {
            assert io != null;
            this.io = io;
        }

        @Override
        public void protocolCommandSent(ProtocolCommandEvent event) {
            processEvent(event);
        }

        @Override
        public void protocolReplyReceived(ProtocolCommandEvent event) {
            processEvent(event);
        }

        private void processEvent(ProtocolCommandEvent event) {
            String message = event.getMessage();
            if (message.startsWith("PASS ")) { // NOI18N
                // hide password
                message = "PASS ******"; // NOI18N
            }
            OutputWriter writer = null;
            if (event.isReply()
                    && (FTPReply.isNegativeTransient(event.getReplyCode()) || FTPReply.isNegativePermanent(event.getReplyCode()))) {
                writer = io.getErr();
            } else {
                writer = io.getOut();
            }
            writer.println(message.trim());
            writer.flush();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Command listener: {0}", message.trim());
            }
        }
    }

    private final class RemoteFileImpl implements RemoteFile {
        private final FTPFile ftpFile;
        private final String parentDirectory;

        public RemoteFileImpl(FTPFile ftpFile, String parentDirectory) {
            assert ftpFile != null;
            assert parentDirectory != null;
            this.ftpFile = ftpFile;
            this.parentDirectory = parentDirectory;
        }

        @Override
        public String getName() {
            return ftpFile.getName();
        }

        @Override
        public String getParentDirectory() {
            return parentDirectory;
        }

        @Override
        public boolean isDirectory() {
            return ftpFile.isDirectory();
        }

        @Override
        public boolean isFile() {
            return ftpFile.isFile();
        }

        @Override
        public boolean isLink() {
            return ftpFile.isSymbolicLink();
        }

        @Override
        public long getSize() {
            return ftpFile.getSize();
        }

        @Override
        public long getTimestamp() {
            return TimeUnit.SECONDS.convert(ftpFile.getTimestamp().getTimeInMillis() + getTimestampDiff(), TimeUnit.MILLISECONDS);
        }

        @Override
        public String toString() {
            return "FtpFile[name: " + getName() + ", parent directory: " + getParentDirectory() + "]";
        }

    }

}
