/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cordova.platforms.ios;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cordova.platforms.Device;
import org.netbeans.modules.cordova.platforms.MobileDebugTransport;
import org.netbeans.modules.cordova.platforms.MobilePlatform;
import org.netbeans.modules.cordova.platforms.PlatformManager;
import org.netbeans.modules.cordova.platforms.ProcessUtils;
import org.netbeans.modules.cordova.platforms.PropertyProvider;
import org.netbeans.modules.web.clientproject.spi.platform.ProjectConfigurationCustomizer;
import org.netbeans.spi.project.ActionProvider;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Becicka
 */
public enum IOSDevice implements Device {
    
    IPHONE("iPhone", "--family iphone", true), //NOI18N
    IPHONE_RETINA("iPhone (Retina)", "--family iphone --retina", true), //NOI18N
    IPAD("iPad", "--family ipad", true), //NOI18N
    IPAD_RETINA("iPad (Retina)", "--family ipad --retina", true), //NOI18N
    CONNECTED("Connected Device", null, false);
    
    String displayName;
    String args;
    private boolean simulator;
    
    private Logger LOG = Logger.getLogger(IOSDevice.class.getName());

    IOSDevice(String name, String args, boolean simulator) {
        this.displayName = name;
        this.args = args;
        this.simulator = simulator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArgs() {
        return args;
    }

    @Override
    public boolean isEmulator() {
        return simulator;
    }

    @Override
    public MobilePlatform getPlatform() {
        return PlatformManager.getPlatform(PlatformManager.IOS_TYPE);
    }

    @Override
    public void addProperties(Properties props) {
        props.put("ios.sim.exec", getPlatform().getSimulatorPath());//NOI18N
        props.put("ios.device.args", getArgs());//NOI18N
    }

    @Override
    public ActionProvider getActionProvider(Project p) {
        return new IOSActionProvider(p);
    }

    @Override
    public ProjectConfigurationCustomizer getProjectConfigurationCustomizer(Project project, PropertyProvider aThis) {
        return new IOSConfigurationPanel.IOSConfigurationCustomizer(project, aThis);
    }
    
    @Override
    public void openUrl(final String url) {
        if (!simulator) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        Object[] options = {"OK"};
                        JOptionPane.showOptionDialog(null,
                                "Start Mobile Safari and open " + url, "Device Setup",
                                JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);
                    }
                    
                });
                return;
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        try {
            try {
                ProcessUtils.callProcess("killall", true, 5000, "MobileSafari");
            } catch (IOException ex) {
            }
            String sim = InstalledFileLocator.getDefault().locate("bin/ios-sim", "org.netbeans.modules.cordova.platforms.ios", false).getPath();
            String retVal = ProcessUtils.callProcess(sim, true, 5000, "launch", "/Applications/Xcode.app/Contents/Developer/Platforms/iPhoneSimulator.platform/Developer/SDKs/" + getIPhoneSimName() +".sdk/Applications/MobileSafari.app", "--exit", "--args", "-u", url); //NOI18N
            LOG.finest(retVal);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private String getIPhoneSimName() {
        return getPlatform().getPrefferedTarget().getIdentifier().replace("p","P").replace("s", "S");
    }

    @Override
    public MobileDebugTransport getDebugTransport() {
        if (simulator) {
            return new SimulatorDebugTransport();
        } else {
            return new DeviceDebugTransport();
        }
    }
}
