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

package org.netbeans.modules.javaee.wildfly.ide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.javaee.wildfly.WildflyDeploymentManager;
import org.netbeans.modules.javaee.wildfly.util.WildFlyProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Petr Hejl
 */
class WildflyStopRunnable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(WildflyStopRunnable.class.getName());

    private static final String JBOSS_CLI_SH = "/bin/jboss-cli.sh"; // NOI18N
    private static final String JBOSS_CLI_BAT = "/bin/jboss-cli.bat"; // NOI18N

    private static final int TIMEOUT = 300000;

    private final WildflyDeploymentManager dm;

    private final WildflyStartServer startServer;

    WildflyStopRunnable(WildflyDeploymentManager dm, WildflyStartServer startServer) {
        this.dm = dm;
        this.startServer = startServer;
    }

    private String[] createEnvironment() {

        WildFlyProperties properties = dm.getProperties();

        JavaPlatform platform = properties.getJavaPlatform();
        FileObject fo = (FileObject) platform.getInstallFolders().iterator().next();
        String javaHome = FileUtil.toFile(fo).getAbsolutePath();
        List<String> envp = new ArrayList<String>(3);
        envp.add("JAVA=" + javaHome + "/bin/java"); // NOI18N
        envp.add("JAVA_HOME=" + javaHome); // NOI18N
        envp.add("JBOSS_HOME=" + properties.getRootDir().getAbsolutePath());    // NOI18N
        if (Utilities.isWindows()) {
            // the shutdown script should not wait for a key press
            envp.add("NOPAUSE=true"); // NOI18N
        }
        return (String[]) envp.toArray(new String[envp.size()]);
    }

    public void run() {

        InstanceProperties ip = dm.getInstanceProperties();

        String configName = ip.getProperty("server"); // NOI18N
        if ("minimal".equals(configName)) { // NOI18N
            startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, NbBundle.getMessage(WildflyStopRunnable.class, "MSG_STOP_SERVER_FAILED_MINIMAL")));//NOI18N
            return;
        }

        String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
        try {
         dm.getClient().shutdownServer();
        } catch (java.io.IOException ioe) {
            LOGGER.log(Level.INFO, null, ioe);
            startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                    NbBundle.getMessage(WildflyStopRunnable.class, "MSG_STOP_SERVER_FAILED_PD", serverName)));//NOI18N

            return;
        }
        startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING,
                NbBundle.getMessage(WildflyStopRunnable.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));
        LOGGER.log(Level.FINER, "Entering the loop"); // NOI18N

        int elapsed = 0;
        while (elapsed < TIMEOUT) {
            if (startServer.isRunning()) {
                startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING,
                        NbBundle.getMessage(WildflyStopRunnable.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));//NOI18N
                LOGGER.log(Level.FINER, "STOPPING message fired"); // NOI18N
                try {
                    elapsed += 500;
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            } else {
                LOGGER.log(Level.FINER, "JBoss has been stopped, going to stop the Log Writer thread");
                WildflyOutputSupport outputSupport = WildflyOutputSupport.getInstance(ip, false);
                try {
                    if (outputSupport != null) {
                        try {
                            outputSupport.waitForStop(10000);
                        } catch (TimeoutException ex) {
                            LOGGER.log(Level.FINE, null, ex);
                        }
                        outputSupport.stop();
                    }
                } catch (InterruptedException ex) {
                    startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                            NbBundle.getMessage(WildflyStopRunnable.class, "MSG_StopServerInterrupted", serverName)));//NOI18N
                    LOGGER.log(Level.INFO, null, ex);
                    Thread.currentThread().interrupt();
                    return;
                } catch (ExecutionException ex) {
                    startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                            NbBundle.getMessage(WildflyStopRunnable.class, "MSG_STOP_SERVER_FAILED", serverName)));//NOI18N
                    LOGGER.log(Level.INFO, null, ex);
                    return;
                }

                startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.COMPLETED,
                        NbBundle.getMessage(WildflyStopRunnable.class, "MSG_SERVER_STOPPED", serverName)));//NOI18N
                LOGGER.log(Level.FINER, "STOPPED message fired"); // NOI18N
                
                startServer.setConsoleConfigured(false);
                return;
            }
        }

        startServer.fireHandleProgressEvent(null, new WildflyDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                NbBundle.getMessage(WildflyStopRunnable.class, "MSG_StopServerTimeout")));
        LOGGER.log(Level.FINER, "TIMEOUT expired"); // NOI18N
    }
}
