/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudson.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.hudson.api.ConnectionBuilder;
import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.spi.HudsonJobChangeItem;
import org.netbeans.modules.hudson.spi.HudsonSCM;
import org.netbeans.modules.hudson.spi.ProjectHudsonJobCreatorFactory.ConfigurationStatus;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ServiceProvider(service=HudsonSCM.class, position=300)
public class HudsonGitSCM implements HudsonSCM {

    private static final Logger LOG = Logger.getLogger(HudsonGitSCM.class.getName());

    private static final String GITHUB_SSH_PREFIX = "ssh://git@github.com/"; // http://stackoverflow.com/questions/3189520/hudson-git-plugin-wont-clone-repo-on-linux
    @Messages({"# {0} - original URL path", "ssh_url=The location " + GITHUB_SSH_PREFIX + "{0} might not work from Hudson; if not, try using git://github.com/{0} in project configuration."})
    @Override public Configuration forFolder(File folder) {
        final URI origin = getRemoteOrigin(folder.toURI(), null);
        if (origin == null) {
            return null;
        }
        return new Configuration() {
            @Override public void configure(Document doc) {
                Element root = doc.getDocumentElement();
                Element configXmlSCM = (Element) root.appendChild(doc.createElement("scm"));
                configXmlSCM.setAttribute("class", "hudson.plugins.git.GitSCM");
                // GitSCM config is horribly complex. Let readResolve do the hard work for now.
                // Note that all this will be wrong if the local repo is using a nondefault remote, or a branch, etc. etc.
                configXmlSCM.appendChild(doc.createElement("source")).appendChild(doc.createTextNode(origin.toString()));
                Helper.addTrigger(doc);
            }
            @Override public ConfigurationStatus problems() {
                if (origin.toString().startsWith(GITHUB_SSH_PREFIX)) {
                    return ConfigurationStatus.withWarning(Bundle.ssh_url(origin.toString().substring(GITHUB_SSH_PREFIX.length())));
                } else {
                    return null;
                }
            }
        };
    }

    @Override public String translateWorkspacePath(HudsonJob job, String workspacePath, File localRoot) {
        return null; // XXX
    }

    @Override public List<? extends HudsonJobChangeItem> parseChangeSet(HudsonJob job, Element changeSet) {
        return Collections.emptyList();//XXX
    }

    static @CheckForNull URI getRemoteOrigin(URI repository, HudsonJob job) {
        assert repository.toString().endsWith("/");
        URI cfg = repository.resolve(".git/config");
        String origin = null;
        ClassLoader l = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(HudsonGitSCM.class.getClassLoader()); // #141364
        try {
            ConnectionBuilder cb = new ConnectionBuilder();
            if (job != null) {
                cb = cb.job(job);
            }
            InputStream is = cb.url(cfg.toURL()).connection().getInputStream();
            try {
                Ini ini = new Ini(is);
                Ini.Section section = ini.get("remote \"origin\"");
                if (section != null) {
                    origin = section.get("url");
                }
            } finally {
                is.close();
            }
        } catch (InvalidFileFormatException x) {
            LOG.log(Level.FINE, "{0} was malformed, perhaps no workspace: {1}", new Object[] {cfg, x});
            return null;
        } catch (FileNotFoundException x) {
            LOG.log(Level.FINE, "{0} not found", cfg);
            return null;
        } catch (Exception x) {
            LOG.log(Level.WARNING, "Could not parse " + cfg, x);
            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(l);
        }
        if (origin == null) {
            LOG.log(Level.FINE, "{0} does not specify remote under name ''origin''", cfg);
            return null;
        }
        Matcher m = Pattern.compile("([^@:/]+@[^/:]+):(.+)").matcher(origin);
        if (m.matches()) {
            origin = "ssh://" + m.group(1) + "/" + m.group(2);
        }
        // XXX deal with local file paths; need to translate to file: URLs
        // XXX any further processing needed? absolutization, password stripping?
        try {
            return new URI(origin);
        } catch (URISyntaxException x) {
            LOG.log(Level.FINE, "could not load origin from {0}: {1}", new Object[] {cfg, x});
            return null;
        }
    }

}
