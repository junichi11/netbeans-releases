/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.subversion.remote.ui.repository;

import java.net.MalformedURLException;
import java.util.logging.Level;
import org.netbeans.modules.subversion.remote.Subversion;
import org.netbeans.modules.subversion.remote.api.SVNRevision;
import org.netbeans.modules.subversion.remote.api.SVNUrl;
import org.netbeans.modules.subversion.remote.config.Scrambler;
import org.netbeans.modules.subversion.remote.util.SvnUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class RepositoryConnection {
    
    private static final String RC_DELIMITER = "~=~"; //NOI18N
    
    private String url;   
    private String username;
    private char[] password;
    private String externalCommand;
    private boolean savePassword;
    private SVNUrl svnUrl;
    private SVNRevision svnRevision;
    private String certFile;
    private char[] certPassword;
    private static Boolean keepUserInHostname;
    private int sshPortNumber;
    
    public RepositoryConnection(RepositoryConnection rc) {
        this(rc.url, rc.username, rc.password, rc.externalCommand, rc.savePassword, rc.certFile, rc.certPassword, rc.sshPortNumber);
    }
    
    public RepositoryConnection(String url) {
        this(url, null, null, null, false, null, null, -1);
    }
            
    public RepositoryConnection(String url, String username, char[] password, String externalCommand, boolean savePassword, String certFile, char[] certPassword, int sshPortNumber) {
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
        this.setExternalCommand(externalCommand);  
        this.savePassword = savePassword;
        this.certFile = certFile;
        this.certPassword = certPassword;
        setSshPortNumber(sshPortNumber);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username == null ? "" : username; //NOI18N
    }

    /**
     *
     * @return can be null
     */
    public char[] getPassword() {
        return password;
    }

    public String getExternalCommand() {
        return externalCommand == null ? "" : externalCommand;
    }
    
    public boolean getSavePassword() {
        return savePassword;
    }

    public String getCertFile() {
        return certFile == null ? "" : certFile;
    }

    public char[] getCertPassword() {
        return certPassword;
    }
    
    public int getSshPortNumber () {
        return sshPortNumber;
    }

    /**
     * 
     * @param fullForm if set to true then the returned value will contain port number (if specified) as well
     * @return
     * @throws MalformedURLException 
     */
    public SVNUrl getSvnUrl() throws MalformedURLException {
        if(svnUrl == null) {
            parseUrlString(url);
        }
        return svnUrl;
    }
    
    public SVNRevision getSvnRevision() throws MalformedURLException {
        if(svnRevision == null) {
            parseUrlString(url);
        }
        return svnRevision;        
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;   
        }            
        if (getClass() != o.getClass()) {
            return false;
        }            
        
        final RepositoryConnection test = (RepositoryConnection) o;

        if (this.url != test.url && this.url != null && !this.url.equals(test.url)) {
            return false;
        }        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.url != null ? this.url.hashCode() : 0);        
        return hash;
    }

    void setUrl(String url) {
        this.url = url;
        svnUrl = null;
        svnRevision = null;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(char[] password) {
        this.password = password;
    }
    
    final void setSshPortNumber (int portNumber) {
        if (portNumber > 0 && portNumber <= 65535) {
            this.sshPortNumber = portNumber;
        } else {
            this.sshPortNumber = -1;
        }
    }

    void setExternalCommand(String externalCommand) {
        this.externalCommand = externalCommand;
    }

    void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    public void setCertFile(String certFile) {
        this.certFile = certFile;
    }

    public void setCertPassword(char[] certPassword) {
        this.certPassword = certPassword;
    }
    
    @Override
    public String toString() {
        return url;
    }

    private void parseUrlString(String urlString) throws MalformedURLException {
        int idx = urlString.lastIndexOf('@');
        int hostIdx = urlString.indexOf("://");                         // NOI18N
        int firstSlashIdx = urlString.indexOf("/", hostIdx + 3);        // NOI18N
        if (urlString.contains("\\")) { //NOI18N
            throw new MalformedURLException(NbBundle.getMessage(Repository.class, "MSG_Repository_InvalidSvnUrl", urlString)); //NOI18N
        }
        if(idx < 0 || firstSlashIdx < 0 || idx < firstSlashIdx) {
            svnRevision = SVNRevision.HEAD;
        } else /*if (acceptRevision)*/ {
            if( idx + 1 < urlString.length()) {
                String revisionString = "";                             // NOI18N
                try {
                    revisionString = urlString.substring(idx + 1);
                    svnRevision = SvnUtils.getSVNRevision(revisionString);
                } catch (NumberFormatException ex) {
                    throw new MalformedURLException(NbBundle.getMessage(Repository.class, "MSG_Repository_WrongRevision", revisionString));     // NOI18N
                }
            } else {
                svnRevision = SVNRevision.HEAD;
            }
            urlString = urlString.substring(0, idx);
        }
        SVNUrl normalizedUrl = removeEmptyPathSegments(new SVNUrl(urlString));
        if ("file".equals(normalizedUrl.getProtocol()) && normalizedUrl.getHost() != null //NOI18N
                && normalizedUrl.getPathSegments().length == 0) {
            throw new MalformedURLException(NbBundle.getMessage(Repository.class, "MSG_Repository_InvalidSvnUrl", SvnUtils.decodeToString(normalizedUrl))); //NOI18N
        }
        svnUrl = normalizedUrl;
    }
    
    private SVNUrl removeEmptyPathSegments(SVNUrl url) throws MalformedURLException {
        String[] pathSegments = url.getPathSegments();
        StringBuilder urlString = new StringBuilder();
        urlString.append(url.getProtocol());
        urlString.append("://");                                                // NOI18N
        urlString.append(ripUserFromHost(url.getHost()));
        if(url.getPort() > 0) {
            urlString.append(":");                                              // NOI18N
            urlString.append(url.getPort());
        }
        boolean gotSegments = false;
        for (int i = 0; i < pathSegments.length; i++) {
            if(!pathSegments[i].trim().equals("")) {                            // NOI18N
                gotSegments = true;
                urlString.append("/");                                          // NOI18N
                urlString.append(pathSegments[i]);                
            }
        }
        try {
            if(gotSegments) {
                return new SVNUrl(urlString.toString());
            } else {
                return url;
            }
        } catch (MalformedURLException ex) {
            throw ex;
        }
    }
    
    public static String getString(RepositoryConnection rc) {
        SVNUrl url;
        try {        
            url = rc.getSvnUrl();
        } catch (MalformedURLException mue) {
            // should not happen
            Subversion.LOG.log(Level.INFO, null, mue); 
            return "";                                                          // NOI18N
        }        
        StringBuilder sb = new StringBuilder();        
        sb.append(url.toString());
        sb.append(RC_DELIMITER);
        if(rc.getSavePassword()) {
            sb.append(rc.getUsername());
        }
        sb.append(RC_DELIMITER);
        sb.append(RC_DELIMITER);
        sb.append(rc.getExternalCommand());
        sb.append(RC_DELIMITER);        
        sb.append(rc.getSavePassword());
        sb.append(RC_DELIMITER);
        sb.append(rc.getCertFile());
        sb.append(RC_DELIMITER);
        sb.append(RC_DELIMITER);
        sb.append(rc.getSshPortNumber());
        sb.append(RC_DELIMITER);
        return sb.toString();
    }
    
    public static RepositoryConnection parse(String str) {        
        String[] fields = str.split(RC_DELIMITER);
        int l = fields.length;
        String url          =           fields[0];
        String username     = l > 1 && !fields[1].equals("") ? fields[1] : null;
        String password     = l > 2 && !fields[2].equals("") ? Scrambler.getInstance().descramble(fields[2]) : null;
        String extCmd       = l > 3 && !fields[3].equals("") ? fields[3] : null;
        boolean save        = l > 4 && !fields[4].equals("") ? Boolean.parseBoolean(fields[4]) : true;
        String certFile     = l > 5 && !fields[5].equals("") ? fields[5] : null;
        String certPassword = l > 6 && !fields[6].equals("") ? Scrambler.getInstance().descramble(fields[6]) : null;
        String portNumberString = l > 7 ? fields[7] : "-1"; //NOI18N
        int portNumber = -1;
        try {
            portNumber = Integer.parseInt(portNumberString);
        } catch (NumberFormatException ex) {}
        return new RepositoryConnection(url, username, password == null ? null : password.toCharArray(), extCmd, save, certFile,
                certPassword == null ? null : certPassword.toCharArray(), portNumber);
    }

    private static String ripUserFromHost (String hostname) {
        if (keepUserInHostname == null) {
            keepUserInHostname = new Boolean("false".equals(System.getProperty("subversion.ripUserFromHostnames", "true"))); //NOI18N
        }
        return keepUserInHostname.booleanValue() ? hostname : SvnUtils.ripUserFromHost(hostname);
    }
}
