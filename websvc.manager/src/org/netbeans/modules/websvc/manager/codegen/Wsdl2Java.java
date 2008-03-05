/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.websvc.manager.codegen;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.modules.websvc.manager.WebServiceManager;
import org.netbeans.modules.websvc.manager.model.WebServiceData;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.websvc.manager.util.ManagerUtil;
import org.netbeans.modules.websvc.saas.util.WsdlUtil;
import org.openide.util.NbBundle;


/**
 * @author  Winston Prakash
 */
public class Wsdl2Java {
    
    private static final String wsImportCompileScriptName = "modules/ext/build-ws.xml";
    private static File wsImportCompileScript;

    private static final String WEBSVC_HOME_PROP = "websvc.home";
    private static final String USER_FILE_PROP = "user.properties.file";
    private static final String JAXWS_ENDORSED_DIR = "jaxws.endorsed.dir";
    private static final String WSDL_NAME_PROP = "serviceName";
    private static final String WSDL_DIRNAME_PROP = "serviceDirName";
    private static final String WSDL_FILE_NAME_PROP = "wsdlFileName";
    private static final String PACKAGE_NAME = "packageName";
    
    private static final String CONFIG_FILE_PROP = "config.file"; // for wscompile JAXRPC
    private static final String PROXY_SERVER = "proxy.server";
    private static final String TEST_URI = "http://schemas.xmlsoap.org/soap/http";
    
    private static final String CATALOG = "catalog.file";
    
    // TODO generate this dynamically instead of using a hardcoded value
    private static final String ENDORSED_REF = "modules/ext/jaxws21/api/jaxws-api.jar";
    
    private final String userDir = System.getProperty("netbeans.user");
    
    private WebServiceData webServiceData;
    private Properties properties;
    /** The wsimport catalog file (generated during the wsdl copy as part of the xml retriever) **/
    private File catalogFile;
    
    public Wsdl2Java(WebServiceData wsData) {
        webServiceData = wsData;
        properties = new Properties();
        this.catalogFile = new File(wsData.getCatalog());
    }

    /*
     * Creates the proxy jars for JAX-WS and JAX-RPC.
     * 
     */
    public boolean createProxyJars() {
        try {
            boolean jaxRPCAvailable = ManagerUtil.isJAXRPCAvailable();
            String wsdlUrl = webServiceData.getOriginalWsdlUrl();  //should use oringinal remote wsdl url
            String serviceDirName = WsdlUtil.getServiceDirName(webServiceData.getOriginalWsdlUrl());
            String serviceName = webServiceData.getName();
            String packageName = webServiceData.getPackageName();
            properties.put(WEBSVC_HOME_PROP, WebServiceManager.WEBSVC_HOME);
            // INFO - This build properties file contains the classpath information
            // about all the library reference in the IDE
            properties.put(USER_FILE_PROP, userDir+"/build.properties");
            properties.put(WSDL_DIRNAME_PROP, serviceDirName);
            properties.put(WSDL_NAME_PROP, serviceName);
            properties.put(WSDL_FILE_NAME_PROP, wsdlUrl);
            properties.put(PACKAGE_NAME, packageName == null ? "" : packageName);
            
            File endorsedDir = InstalledFileLocator.getDefault().locate(ENDORSED_REF, null, true).getParentFile();
            properties.put(JAXWS_ENDORSED_DIR, endorsedDir.getAbsolutePath());
            
            // set the catalog file for wsimport
            properties.put(CATALOG, catalogFile.getAbsolutePath());
            
            // Set the proxy for wscompile
            try {
                ProxySelector selector = ProxySelector.getDefault();
                List<Proxy> proxies = selector.select(new URI(TEST_URI));
                String proxyServer = "";
                if (proxies.size() > 0) {
                    InetSocketAddress address = (InetSocketAddress)proxies.get(0).address();
                    if (address != null) {
                        String host = "http://" + address.getHostName();
                        String port = String.valueOf(address.getPort());
                        proxyServer = host + ":" + port;
                    }
                }
                properties.put(PROXY_SERVER, proxyServer);
                
            }catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, ex.getLocalizedMessage(), ex);
            }
            
            boolean jaxWsCreated = createJaxWsProxyJars(properties);
            if (!jaxWsCreated) {
                webServiceData.setJaxWsEnabled(false);
                String errorMessage = NbBundle.getMessage(Wsdl2Java.class, "CODEGEN_ERROR_JAXWS");
                NotifyDescriptor d = new NotifyDescriptor.Message(errorMessage);
                DialogDisplayer.getDefault().notify(d);
            }else {
                webServiceData.setJaxWsEnabled(true);
            }
            
            boolean jaxRpcCreated = false;
            if (jaxRPCAvailable) {
                jaxRpcCreated = createJaxRpcProxyJars(properties);
            }
            
            if (!jaxRpcCreated) {
                webServiceData.setJaxRpcEnabled(false);
                
                // Suppress the redundant error message if the JAX-RPC library is not available
                if (jaxRPCAvailable) {
                    String errorMessage = NbBundle.getMessage(Wsdl2Java.class, "CODEGEN_ERROR_JAXRPC");
                    NotifyDescriptor d = new NotifyDescriptor.Message(errorMessage);
                    DialogDisplayer.getDefault().notify(d);
                }
            }else {
                webServiceData.setJaxRpcEnabled(true);
            }
            
            if (webServiceData.isJaxRpcEnabled()) {
                webServiceData.setJaxRpcDescriptorPath(serviceDirName + "/jaxrpc/" + serviceName + ".xml");
            }
            if (webServiceData.isJaxWsEnabled()) {
                webServiceData.setJaxWsDescriptorPath(serviceDirName + "/jaxws/" + serviceName + ".xml");
            }
            
            return jaxWsCreated || jaxRpcCreated;
        } catch (IllegalArgumentException exc) {
            ErrorManager.getDefault().notify(exc);
            return false;
        }
    }
    
    private boolean createJaxWsProxyJars(Properties properties) {
        try {
            ExecutorTask executorTask = ActionUtils.runTarget(FileUtil.toFileObject(getAntScript()),
                    new String[] {"wsimport-jar"}, properties);
            executorTask.waitFinished();
            boolean result = (executorTask.result() == 0);
            
            if (!result) {
                try {
                    String home = properties.getProperty(WEBSVC_HOME_PROP) + "/"
                            + properties.getProperty(WSDL_NAME_PROP) + "/jaxws";
                    
                    deleteWsDir(home);
                }catch (NullPointerException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, ex.getLocalizedMessage(), ex);
                }
            }
            
            return result;
        }catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
    }
    
    private boolean createJaxRpcProxyJars(Properties properties) {
        try {
            String wsdlUrlName = new File(webServiceData.getURL()).toURI().toURL().toString();
            createJaxrpcConfigFile(wsdlUrlName, properties);
            
            ExecutorTask executorTask = ActionUtils.runTarget(FileUtil.toFileObject(getAntScript()),
                    new String[] {"wscompile-jar"}, properties);

            executorTask.waitFinished();
            boolean result = (executorTask.result() == 0);
            
            if (!result) {
                try {
                    String home = properties.getProperty(WEBSVC_HOME_PROP) + "/"
                            + properties.getProperty(WSDL_NAME_PROP) + "/jaxrpc";
                    
                    deleteWsDir(home);
                }catch (NullPointerException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, ex.getLocalizedMessage(), ex);
                }
            }
            
            return result;
        }catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
    }
    
    /**
     * Deletes the files from the following directories under <code>wsDir</code>
     * - ./build
     * - ./*.jar
     * - ./src
     */
    private void deleteWsDir(String wsDir) {
        File wsDirFile = new File(wsDir);
        
        if (!wsDirFile.isDirectory()) {
            return;
        }else {
            File buildDir = new File(wsDirFile, "build"); // NOI18N
            rmDir(buildDir);
            
            File srcDir = new File(wsDirFile, "src"); // NOI18N
            rmDir(srcDir);
            
            //delete the jar files under the root directory
            File[] jarFiles = wsDirFile.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });
            
            for (int i = 0; jarFiles != null && i < jarFiles.length; i++) {
                jarFiles[i].delete();
            }
            
            wsDirFile.delete();
        }
    }
    
    private void rmDir(File dir) {
        if (dir.isDirectory()) {
            File[] subFiles = dir.listFiles();
            for (int i = 0; subFiles != null && i < subFiles.length; i++) {
                if (subFiles[i].isFile()) {
                    subFiles[i].delete();
                }else {
                    rmDir(subFiles[i]);
                }
            }
            dir.delete();
        }
    }
    
    /**
     * Find the ant script file.
     * The file is at <websvcmgr>/external and placed at
     */
    private File getAntScript() {
        if (wsImportCompileScript == null) {
            wsImportCompileScript = InstalledFileLocator.getDefault().locate(
                    wsImportCompileScriptName,
                    "", // NOI18N
                    false);
        }
        
        return wsImportCompileScript;
    }
    
    private void createJaxrpcConfigFile(String wsdlFileName, Properties properties){
        try {
            File cf = File.createTempFile("jaxrpcconfigfile", ".xml"); // NOI81N
            cf.deleteOnExit();
            OutputStream out = new FileOutputStream(cf);
            String packageName = webServiceData.getEffectivePackageName();
            if (packageName == null || packageName.trim().length() == 0) {
                String msg = NbBundle.getMessage(Wsdl2Java.class, "MSG_InvalidPackageName");
                throw new IllegalArgumentException(msg);
            }
            final String wsdlConfigEntry = "\t<wsdl location=\"" + wsdlFileName +
                    "\" packageName=\"" + packageName + "\"/>"; // NOI81N
            
            PrintWriter configWriter = new PrintWriter(out);
            
            try {
                configWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // NOI18N
                configWriter.println("<configuration xmlns=\"http://java.sun.com/xml/ns/jax-rpc/ri/config\">"); // NOI18N
                configWriter.println(wsdlConfigEntry);
                configWriter.println("</configuration>"); // NOI18N
            } finally {
                configWriter.close();
            }
            properties.put(CONFIG_FILE_PROP, cf.getAbsolutePath());
        } catch (IOException exc) {
            ErrorManager.getDefault().notify(exc);
        }
    }
}
