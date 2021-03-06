/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.glassfish.spi;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.glassfish.tooling.TaskState;
import org.netbeans.modules.glassfish.tooling.TaskStateListener;
import org.netbeans.modules.glassfish.tooling.admin.ResultString;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;
import org.netbeans.modules.glassfish.common.GlassFishJvmMode;
import org.netbeans.modules.glassfish.common.GlassfishInstanceProvider;


/**
 * Interface implemented by common server support.  Always available in server
 * instance lookup.
 *
 * @author Peter Williams
 */
public interface GlassfishModule {
    
    // Attribute keys for InstanceProperties map
    public static final String URL_ATTR = "url"; // NOI18N
    public static final String INSTALL_FOLDER_ATTR = "installfolder"; // NOI18N
    public static final String GLASSFISH_FOLDER_ATTR = "homefolder"; // NOI18N
    public static final String DISPLAY_NAME_ATTR = "displayName"; // NOI18N
    public static final String USERNAME_ATTR = "username"; // NOI18N
    public static final String PASSWORD_ATTR = "password"; // NOI18N
    public static final String ADMINPORT_ATTR = "adminPort"; // NOI18N
    public static final String TARGET_ATTR = "target";
    public static final String HTTPPORT_ATTR = "httpportnumber"; // NOI18N
    public static final String HOSTNAME_ATTR = "host"; // NOI18N
    public static final String JRUBY_HOME = "jruby.home"; // NOI18N
    public static final String GEM_HOME = "GEM_HOME"; // NOI18N
    public static final String GEM_PATH = "GEM_PATH"; // NOI18N
    public static final String DOMAINS_FOLDER_ATTR = "domainsfolder"; // NOI18N
    public static final String DOMAIN_NAME_ATTR = "domainname";
    public static final String JAVA_PLATFORM_ATTR = "java.platform";
    public static final String HTTP_MONITOR_FLAG = "httpMonitorOn";
    public static final String DRIVER_DEPLOY_FLAG = "driverDeployOn";
    public static final String START_DERBY_FLAG = "derbyStartOn";
    public static final String USE_IDE_PROXY_FLAG = "useIDEProxyOn";
    public static final String LOOPBACK_FLAG = "loopbackOn";
    public static final String HTTPHOST_ATTR = "httphostname";  // NOI18N -- necessary for cluster and instance support
    
    public static final String USE_SHARED_MEM_ATTR = "use.shared.mem"; // NOI18N
    public static final String DEBUG_PORT = "debugPort"; // NOI18N
    public static final String DEBUG_MEM = "debugMem"; // NOI18N
    public static final String JVM_MODE = "jvmMode"; // NOI18N
    public static final String NORMAL_MODE = GlassFishJvmMode.NORMAL.toString();
    public static final String DEBUG_MODE = GlassFishJvmMode.DEBUG.toString();
    public static final String PROFILE_MODE = GlassFishJvmMode.PROFILE.toString();
    
    public static final String COMET_FLAG = "v3.grizzly.cometSupport"; // NOI18N
    
    // Contract provider constants (identify the different containers in V3)
    public static final String EAR_CONTAINER = "ear"; // NOI18N
    public static final String WEB_CONTAINER = "web"; // NOI18N
    public static final String JRUBY_CONTAINER = "jruby"; // NOI18N
    public static final String EJB_CONTAINER = "ejb"; // NOI18N
    public static final String APPCLIENT_CONTAINER = "appclient"; // NOI18N
    public static final String CONNECTOR_CONTAINER = "connector"; // NOI18N

    // Resource types
    public static final String JDBC = "JDBC"; // NOI18N
    public static final String JDBC_RESOURCE = "jdbc-resource"; // NOI18N
    public static final String JDBC_CONNECTION_POOL = "jdbc-connection-pool"; // NOI18N
    public static final String SESSION_PRESERVATION_FLAG = "preserveSessionsOn";

    public static final String CONNECTORS = "CONNECTORS"; // NOI18N
    public static final String CONN_RESOURCE = "connector-resource"; // NOI18N
    public static final String CONN_CONNECTION_POOL = "connector-connection-pool"; // NOI18N
    public static final String ADMINOBJECT_RESOURCE = "admin-object"; // NOI18N

    public static final String JAVAMAIL = "JAVAMAIL"; // NOI18N
    public static final String JAVAMAIL_RESOURCE = "javamail-resource"; // NOI18N

    /** Key to mark properties already imported into NetBeans 7.3 and fixed. */
    public static final String NB73_IMPORT_FIXED = "nb73ImportFixed";

    /** Properties fetching timeout [ms]. */
    public static final int PROPERTIES_FETCH_TIMEOUT = 10000;

    public CommandFactory getCommandFactory();

    public String getResourcesXmlName();

    public boolean isWritable();

    /**
     * Enum for the current state of the server (stopped, running, etc.)
     */
    public static enum ServerState {
        STARTING,
        RUNNING,
        RUNNING_JVM_DEBUG,
        RUNNING_JVM_PROFILER,
        STOPPING,
        STOPPED,
        STOPPED_JVM_BP,
        STOPPED_JVM_PROFILER,
        UNKNOWN}
    
    /**
     * Returns a read-only map of the current instance properties.  Use the 
     * attribute constants defined in this file to locate specific properties.
     * 
     * @return read-only map containing all current instance properties
     */
    public Map<String, String> getInstanceProperties();

    public GlassfishInstanceProvider getInstanceProvider();

    /**
     * Returns true if server is remote.  Remote servers have special
     * properties, such as being non-startable from within the IDE.
     *
     * @return true if this is a remote server.
     */
    public boolean isRemote();

    /**
     * Sets a property in the instance properties map, if that property has not
     * been set yet.
     * 
     * @param name key for this map entry. 
     * @param value value for this key.
     * @param overwrite true if you want to overwrite the existing key value if any.
     * 
     * @return the current value of this key, if present in the map already
     *   and overwrite is false.  Otherwise, returns the new value being set.
     */
    public String setEnvironmentProperty(String name, String value, boolean overwrite);    

    /**
     * Start the server.
     * 
     * @param stateListener listener to listen message describing the startup 
     *   process as it progresses.  Can be null.
     * @param endState {@linkplain ServerState} the server is to be put to upon successful startup
     * @return Future instance that finishes when the server startup has
     *   completed (or failed).
     */
    public Future<TaskState> startServer(TaskStateListener stateListener, ServerState endState);

    /**
     * Stop the server.
     *
     * XXX returned Future instance shouldn't "finish" until server vm has
     *   terminated (or been killed if it hangs).
     * 
     * @param stateListener listener to listen message describing the shutdown 
     *   process as it progresses.  Can be null.
     * 
     * @return Future instance that finishes when the server shutdown message
     *   has been acknowledged.
     * 
     */
    public Future<TaskState> stopServer(TaskStateListener stateListener);

    /**
     * Terminates local GlassFish server process when started from UI.
     * <p/>
     * @param stateListener listener to listen message describing the shutdown 
     *                      process as it progresses.  Can be null.
     * @return Asynchronous GlassFish server termination task that finishes
     *         when the server stops responding.
     */
    public Future<TaskState> killServer(final TaskStateListener stateListener);

    /**
     * Restart the server.  Starts the server if it's not running.  Stops and
     * then starts the server if it is currently running.
     *
     * @param stateListener listener to listen message describing the startup
     *   process as it progresses.  Can be null.
     *
     * @return Future instance that finishes when the server startup has
     *   completed (or failed).
     */
    public Future<TaskState> restartServer(TaskStateListener stateListener);

    /**
     * Deploy the specified directory or application archive onto the server.
     * 
     * @param stateListener listener to listen message describing the deploy 
     *   process as it progresses.  Can be null.
     * @param application either the root folder of the application (directory
     *   deployment) or the application archive (e.g. war file, etc.)
     * @param name name to deploy this application under.
     * 
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */
    public Future<ResultString> deploy(TaskStateListener stateListener, 
            File application, String name);

    /**
     * Deploy the specified directory or application archive onto the server.
     * 
     * @param stateListener listener to listen message describing the deploy 
     *   process as it progresses.  Can be null.
     * @param application either the root folder of the application (directory
     *   deployment) or the application archive (e.g. war file, etc.)
     * @param name name to deploy this application under.
     * @param contextRoot to use for this application on deploy.
     * 
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */
    
    public Future<ResultString> deploy(TaskStateListener stateListener, 
            File application, String name, String contextRoot);
    
    /**
     * Deploy the specified directory or application archive onto the server.
     *
     * @param stateListener listener to listen message describing the deploy
     *   process as it progresses.  Can be null.
     * @param application either the root folder of the application (directory
     *   deployment) or the application archive (e.g. war file, etc.)
     * @param name name to deploy this application under.
     * @param contextRoot to use for this application on deploy.
     * @param properties deployment properties
     *
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */

    public Future<ResultString> deploy(TaskStateListener stateListener,
            File application, String name, String contextRoot, Map<String,String> properties);

    /**
     * Redeploy the named application onto the server.  The application must
     * have previously been directory deployed.  If not, use deploy().
     * 
     * @param stateListener listener to listen message describing the redeploy 
     *   process as it progresses.  Can be null.
     * @param name name this application is deployed under.
     * 
     * @return Future instance that finishes when the redeploy command has been
     *   completed.
     */
    public Future<ResultString> redeploy(final TaskStateListener stateListener, 
            final String name, final boolean resourcesChanged);
       
    /**
     * Redeploy the named application onto the server with a new context root
     * value.  The application must have previously been directory deployed.
     * If not, use deploy().
     * 
     * @param stateListener listener to listen message describing the redeploy 
     *   process as it progresses.  Can be null.
     * @param name name this application is deployed under.
     * @param contextRoot to use for this application on deploy (can be null to
     *   reuse existing contextRoot.)
     * 
     * @return Future instance that finishes when the redeploy command has been
     *   completed.
     */
    public Future<ResultString> redeploy(final TaskStateListener stateListener, 
            final String name, final String contextRoot, final boolean resourcesChanged);
    
    /**
     * Undeploy the named application.
     * 
     * @param stateListener listener to listen message describing the undeploy 
     *   process as it progresses.  Can be null.
     * @param name name of application to undeploy.
     * 
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */
    public Future<ResultString> undeploy(TaskStateListener stateListener, 
            String name);
    
    /**
     * Enable the named application.
     *
     * @param stateListener listener to listen message describing the enable
     *   process as it progresses.  Can be null.
     * @param name name of application to enable.
     *
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */
    public Future<ResultString> enable(TaskStateListener stateListener,
            String name);
    /**
     * Disable the named application.
     *
     * @param stateListener listener to listen message describing the disable
     *   process as it progresses.  Can be null.
     * @param name name of application to disable.
     *
     * @return Future instance that finishes when the deploy command has been
     *   completed.
     */
    public Future<ResultString> disable(TaskStateListener stateListener,
            String name);
//    /**
//     * Execute the specified server command.
//     * 
//     * @param command Object representing the server command to execute.
//     * 
//     * @return Future instance that finishes when the command has been completed.
//     */
//    public Future<TaskState> execute(ServerCommand command);
    
    /**
     * List the applications currently deployed on the server.
     * 
     * @return array of application names current deployed.
     */
    public AppDesc [] getModuleList(String container);
    
    /**
     * Map of the resources of specified type currently deployed on the server.
     *
     * @return map of resources current deployed.
     */
    public Map<String, ResourceDesc> getResourcesMap(String type);

    /**
     * Returns the current server state (stopped, running, etc.)
     * 
     * @return current server state as an enum.  See enum declaration for 
     *   possible states.
     */
    public ServerState getServerState();
    
    /**
     * Adds a listener that is notified when the server state changes.  Call
     * <code>getServerState()</code> in the listener's method body to find out
     * the current server state.
     * 
     * @param listener listener to add.
     */
    public void addChangeListener(ChangeListener listener);
    
    /**
     * Removes a server state change listener previously added.
     * 
     * @param listener listener to remove.
     */
    public void removeChangeListener(ChangeListener listener);

    public static final String PASSWORD_CONVERTED_FLAG =
            "this really long string is used to identify a password that has been stored in the Keyring";
    /**
     * Get <code>GlassfishInstance</code> object associated with this object.
     * <p/>
     * @return <code>GlassfishInstance</code> object associated with this object.
     */
    public GlassFishServer getInstance();

    /**
     * get the password for this server
     */
    public String getPassword();

    public boolean supportsRestartInDebug();

    public boolean isRestfulLogAccessSupported();

}
