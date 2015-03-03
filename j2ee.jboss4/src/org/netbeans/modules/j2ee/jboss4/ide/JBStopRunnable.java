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

package org.netbeans.modules.j2ee.jboss4.ide;

import java.io.File;
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
import org.netbeans.modules.j2ee.jboss4.JBDeploymentManager;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginProperties;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginUtils;
import org.netbeans.modules.j2ee.jboss4.util.JBProperties;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Kirill Sorokin
 * @author Libor Kotouc
 */
class JBStopRunnable implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(JBStopRunnable.class.getName());
    
    private static final String SHUTDOWN_SH = "/bin/shutdown.sh"; // NOI18N
    private static final String JBOSS_CLI_SH = "/bin/jboss-cli.sh"; // NOI18N
    private static final String SHUTDOWN_BAT = "/bin/shutdown.bat"; // NOI18N
    private static final String JBOSS_CLI_BAT = "/bin/jboss-cli.bat"; // NOI18N

    private static final int TIMEOUT = 300000;
    
    private final JBDeploymentManager dm;
    private final JBStartServer startServer;

    JBStopRunnable(JBDeploymentManager dm, JBStartServer startServer) {
        this.dm = dm;
        this.startServer = startServer;
    }
    
    private boolean isJBoss7() {
        return dm.getProperties().isVersion(JBPluginUtils.JBOSS_7_0_0);
    }
    
    private String[] createEnvironment() {
        
        JBProperties properties = dm.getProperties();

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
            startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_FAILED_MINIMAL")));//NOI18N
            return;
        }

        String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);

        String serverLocation = ip.getProperty(JBPluginProperties.PROPERTY_ROOT_DIR);
        String serverStopFileName = serverLocation + (isJBoss7() ? Utilities.isWindows() ? JBOSS_CLI_BAT : JBOSS_CLI_SH :Utilities.isWindows() ? SHUTDOWN_BAT : SHUTDOWN_SH);

        File serverStopFile = new File(serverStopFileName);
        if (!serverStopFile.exists()){
            startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_FAILED_FNF", serverName)));//NOI18N
            return;
        }
        
        JBProperties properties = dm.getProperties();
        StringBuilder additionalParams = new StringBuilder(32);
        NbProcessDescriptor pd;
        if (isJBoss7()) {
            pd = new NbProcessDescriptor(serverStopFileName,
                    "--connect --controller=localhost:" + JBPluginUtils.getJmxPortNumber(properties, ip) + " --command=:shutdown"); // NOI18N
        } else {
            if (dm.getProperties().getServerVersion().compareTo(JBPluginUtils.JBOSS_6_0_0) < 0) {
                int jnpPort = JBPluginUtils.getJnpPortNumber(ip.getProperty(JBPluginProperties.PROPERTY_SERVER_DIR));
                additionalParams.append(" -s jnp://localhost:").append(jnpPort); // NOI18N
            } else {
                // FIXME changed for JBoss 6
                // see http://community.jboss.org/message/546904
                // and http://community.jboss.org/wiki/StartStopJBoss
            }

            additionalParams.append(" -u ").append(properties.getUsername()); // NOI18N
            additionalParams.append(" -p ").append(properties.getPassword()); // NOI18N

            // Currently there is a problem stopping JBoss when Profiler agent is loaded.
            // As a workaround for now, --halt parameter has to be used for stopping the server.
    //        NbProcessDescriptor pd = (startServer.getMode() == JBStartServer.MODE.PROFILE ?
    //                                  new NbProcessDescriptor(serverStopFileName, "--halt=0 " + credentialsParams) :   // NOI18N
    //                                  new NbProcessDescriptor(serverStopFileName, "--shutdown " + credentialsParams)); // NOI18N

            /* 2008-09-10 The usage of --halt doesn't solve the problem on Windows; it even creates another problem
                            of NB Profiler not being notified about the fact that the server was stopped */
                pd = new NbProcessDescriptor(
                    serverStopFileName, "--shutdown " + additionalParams); // NOI18N
        }
        Process stoppingProcess = null;
        try {
            String envp[] = createEnvironment();
            stoppingProcess = pd.exec(null, envp, true, null);
        } catch (java.io.IOException ioe) {
            LOGGER.log(Level.INFO, null, ioe);

            startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                    NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_FAILED_PD", serverName, serverStopFileName)));//NOI18N

            return;
        }

        startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING,
                NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));

        LOGGER.log(Level.FINER, "Entering the loop"); // NOI18N
        
        int elapsed = 0;
        while (elapsed < TIMEOUT) {
            // check whether the stopping process did not fail
            try {
                int processExitValue = stoppingProcess.exitValue();
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "The stopping process has terminated with the exit value " + processExitValue); // NOI18N
                }
                if (processExitValue != 0) {
                    // stopping process failed
                    String msg = NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_FAILED", serverName);
                    startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, msg));
                    return;
                }
            } catch (IllegalThreadStateException e) {
                // process is still running
            }
            if (startServer.isRunning()) {
                startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.RUNNING,
                        NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_IN_PROGRESS", serverName)));//NOI18N
                LOGGER.log(Level.FINER, "STOPPING message fired"); // NOI18N
                try {
                    elapsed += 500;
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            } else {
                LOGGER.log(Level.FINER, "JBoss has been stopped, going to stop the Log Writer thread");
                JBOutputSupport outputSupport = JBOutputSupport.getInstance(ip, false);
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
                    startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                            NbBundle.getMessage(JBStopRunnable.class, "MSG_StopServerInterrupted", serverName)));//NOI18N                    
                    LOGGER.log(Level.INFO, null, ex);
                    Thread.currentThread().interrupt();
                    return;
                } catch (ExecutionException ex) {
                    startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                            NbBundle.getMessage(JBStopRunnable.class, "MSG_STOP_SERVER_FAILED", serverName)));//NOI18N                    
                    LOGGER.log(Level.INFO, null, ex);
                    return;
                }

                startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.COMPLETED,
                        NbBundle.getMessage(JBStopRunnable.class, "MSG_SERVER_STOPPED", serverName)));//NOI18N
                LOGGER.log(Level.FINER, "STOPPED message fired"); // NOI18N

                return;
            }
        }
        
        startServer.fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED,
                NbBundle.getMessage(JBStopRunnable.class, "MSG_StopServerTimeout")));
        if (stoppingProcess != null) {
            stoppingProcess.destroy();
        }
        
        LOGGER.log(Level.FINER, "TIMEOUT expired"); // NOI18N
    }
}
    
