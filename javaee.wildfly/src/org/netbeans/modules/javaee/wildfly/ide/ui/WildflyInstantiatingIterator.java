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
package org.netbeans.modules.javaee.wildfly.ide.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.javaee.wildfly.WildflyDeploymentFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;


/**
 *
 * @author Ivan Sidorkin
 */
public class WildflyInstantiatingIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {

    private static final String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N

    private static final String WILDFLY_JAVA_OPTS = "-Xms128m -Xmx512m"; // NOI18N
    /**
     * skipServerLocationStep allow to skip Select Location step in New Instance Wizard
     * if this step allready was passed
     */
    public final boolean skipServerLocationStep = false;

    private transient AddServerLocationPanel locationPanel = null;
    private transient AddServerPropertiesPanel propertiesPanel = null;

    private WizardDescriptor wizard;
    private transient int index = 0;
    private transient WizardDescriptor.Panel[] panels = null;


    // private InstallPanel panel;
    private final transient Set listeners = new HashSet(1);
    @Override
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void previousPanel() {
        index--;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }

    @Override
    public String name() {
        return "JBoss Server AddInstanceIterator";  // NOI18N
    }

    public static void showInformation(final String msg,  final String title) {
        Runnable info = new Runnable() {
            @Override
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(title);
                DialogDisplayer.getDefault().notify(d);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            info.run();
        } else {
            SwingUtilities.invokeLater(info);
        }
    }

    @Override
    public Set instantiate() throws IOException {
        Set result = new HashSet();

        String displayName =  (String)wizard.getProperty(PROP_DISPLAY_NAME);
        WildflyPluginUtils.Version version = WildflyPluginUtils.getServerVersion(new File(installLocation));
        String url = WildflyDeploymentFactory.URI_PREFIX;
        if(version != null && "7".equals(version.getMajorNumber())){
            url += "//"+host + ":" + port+"?targetType=as7";    // NOI18N
        } else {
            url += host + ":" + port;    // NOI18N
        }
        if (server != null && !server.equals(""))                           // NOI18N
            url += "#" + server;                                            // NOI18N
        url += "&"+ installLocation;                                        // NOI18N

        try {
            Map<String, String> initialProperties = new HashMap<String, String>();
            initialProperties.put(WildflyPluginProperties.PROPERTY_SERVER, server);
            initialProperties.put(WildflyPluginProperties.PROPERTY_DEPLOY_DIR, deployDir);
            initialProperties.put(WildflyPluginProperties.PROPERTY_SERVER_DIR, serverPath);
            initialProperties.put(WildflyPluginProperties.PROPERTY_ROOT_DIR, installLocation);
            initialProperties.put(WildflyPluginProperties.PROPERTY_HOST, host);
            initialProperties.put(WildflyPluginProperties.PROPERTY_PORT, port);
            initialProperties.put(WildflyPluginProperties.PROPERTY_CONFIG_FILE, configFile);
            initialProperties.put(WildflyPluginProperties.PROPERTY_PROTOCOL, protocol);
            initialProperties.put(WildflyPluginProperties.PROPERTY_ADMIN_PORT, adminPort);
            initialProperties.put(WildflyPluginProperties.PROPERTY_JAVA_OPTS, WILDFLY_JAVA_OPTS);

            InstanceProperties ip = InstanceProperties.createInstanceProperties(url,
                    userName, password, displayName, initialProperties);

            result.add(ip);
        } catch (InstanceCreationException e){
            showInformation(e.getLocalizedMessage(), NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "MSG_INSTANCE_REGISTRATION_FAILED")); //NOI18N
            Logger.getLogger("global").log(Level.SEVERE, e.getMessage());
        }

        return result;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    protected String[] createSteps() {
        if(!skipServerLocationStep){
            return new String[] { NbBundle.getMessage(WildflyInstantiatingIterator.class, "STEP_ServerLocation"),  NbBundle.getMessage(WildflyInstantiatingIterator.class, "STEP_Properties") };    // NOI18N
        } else {
            if (!WildflyPluginProperties.getInstance().isCurrentServerLocationValid()){
                return new String[] { NbBundle.getMessage(WildflyInstantiatingIterator.class, "STEP_ServerLocation"),  NbBundle.getMessage(WildflyInstantiatingIterator.class, "STEP_Properties") };    // NOI18N
            } else {
                return new String[] { NbBundle.getMessage(WildflyInstantiatingIterator.class, "STEP_Properties") };    // NOI18N
            }
        }
    }

    protected final String[] getSteps() {
        if (steps == null) {
            steps = createSteps();
        }
        return steps;
    }

    protected final WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = createPanels();
        }
        return panels;
    }

    protected WizardDescriptor.Panel[] createPanels() {
        if (locationPanel == null) {
            locationPanel = new AddServerLocationPanel(this);
            locationPanel.addChangeListener(this);
        }
        if (propertiesPanel == null) {
            propertiesPanel = new AddServerPropertiesPanel(this);
            propertiesPanel.addChangeListener(this);
        }

        if (skipServerLocationStep){
            if (!WildflyPluginProperties.getInstance().isCurrentServerLocationValid()){
                return new WizardDescriptor.Panel[] {
                    (WizardDescriptor.Panel)locationPanel,
                            (WizardDescriptor.Panel)propertiesPanel
                };
            } else {
                return new WizardDescriptor.Panel[] {
                    (WizardDescriptor.Panel)propertiesPanel
                };
            }
        }else{
            return new WizardDescriptor.Panel[] {
                (WizardDescriptor.Panel)locationPanel,
                        (WizardDescriptor.Panel)propertiesPanel
            };
        }
    }

    private transient String[] steps = null;

    protected final int getIndex() {
        return index;
    }

    @Override
    public WizardDescriptor.Panel current() {
        WizardDescriptor.Panel result = getPanels()[index];
        JComponent component = (JComponent)result.getComponent();
        component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, getSteps());  // NOI18N
        component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(getIndex()));// NOI18N
        return result;
    }

    @Override
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        fireChangeEvent();
    }

    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }

    private String host;
    private String port;
    private String protocol = "remote+http";
    private String adminPort;
    private String userName="";
    private String password="";
    private String server;
    private String installLocation;
    private String deployDir;
    private String serverPath;
    private String configFile="standalone-full.xml";

     public void setConfigFile(String configFile){
        this.configFile = configFile.trim();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHost(String host){
        this.host = host.trim();
    }

    public void setPort(String port){
        this.port = port.trim();
    }

    public void setAdminPort(String port){
        this.adminPort = port.trim();
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setServer(String server){
        this.server = server;
    }

    public void setServerPath(String serverPath){
        this.serverPath = serverPath;
    }

    public void setDeployDir(String deployDir){
        this.deployDir = deployDir;
    }

    public void setInstallLocation(String installLocation){
        this.installLocation = installLocation;
        propertiesPanel.installLocationChanged();
    }

}
