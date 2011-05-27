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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudson.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.hudson.api.ConnectionBuilder;
import org.netbeans.modules.hudson.api.UI;
import org.netbeans.modules.hudson.impl.HudsonInstanceImpl;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import static org.netbeans.modules.hudson.ui.actions.Bundle.*;

/**
 * #161911: downloads & runs latest hudson.war and configures it for you.
 */
public class AddTestInstanceAction extends AbstractAction implements Runnable {

    private static final Logger LOG = Logger.getLogger(AddTestInstanceAction.class.getName());

    @Messages("AddTestInstanceAction.label=Try Hudson on &Localhost")
    public AddTestInstanceAction() {
        super(AddTestInstanceAction_label());
    }

    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().post(this);
    }

    @Messages({
        "# {0} - path to javaws", "AddTestInstanceAction.no_javaws=Could not find {0}. Run: javaws https://hudson.dev.java.net/hudson.jnlp",
        "AddTestInstanceAction.could_not_run=Could not download or run Hudson. Run: javaws https://hudson.dev.java.net/hudson.jnlp",
        "AddTestInstanceAction.starting=Downloading & running Hudson...",
        "AddTestInstanceAction.instance_name=Local Test Server"
    })
    public void run() {
        // XXX could use JavaPlatformManager.default.defaultPlatform.findTool("javaws") if could depend on java.platform
        File javaHome = new File(System.getProperty("java.home"));
        File bindir = new File(javaHome.getParentFile(), "bin");
        if (!bindir.isDirectory()) { // #171884
            bindir = new File(javaHome, "bin");
        }
        File javaws = new File(bindir, "javaws.exe");
        if (!javaws.isFile()) {
            javaws = new File(bindir, "javaws");
        }
        if (!javaws.isFile()) {
            warning(AddTestInstanceAction_no_javaws(javaws));
            return;
        }
        try {
            int exit = new ProcessBuilder(javaws.getAbsolutePath(), "https://hudson.dev.java.net/hudson.jnlp").start().waitFor();
            if (exit != 0) {
                warning(AddTestInstanceAction_could_not_run());
                return;
            }
        } catch (Exception x) {
            warning(AddTestInstanceAction_could_not_run());
            LOG.log(Level.INFO, null, x);
            return;
        }
        final AtomicBoolean cancelled = new AtomicBoolean();
        ProgressHandle progress = ProgressHandleFactory.createHandle(
                AddTestInstanceAction_starting(), new Cancellable() {
            public boolean cancel() {
                cancelled.set(true);
                return true;
            }
        });
        progress.start();
        try {
            String localhost = "http://localhost:8080/"; // NOI18N
            while (!cancelled.get()) {
                try {
                    Thread.sleep(1000); // wait a little bit
                } catch (InterruptedException x) {
                    LOG.log(Level.INFO, null, x);
                }
                try {
                    new ConnectionBuilder().url(localhost).connection();
                    // Success!
                    HudsonInstanceImpl.createHudsonInstance(AddTestInstanceAction_instance_name(), localhost, "1"); // NOI18N
                    UI.selectNode(localhost);
                    break;
                } catch (IOException x) {
                    LOG.log(Level.FINER, null, x);
                    // Not up & running yet.
                }
            }
        } finally {
            progress.finish();
        }
    }

    private void warning(String message) throws MissingResourceException {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE));
    }

}
