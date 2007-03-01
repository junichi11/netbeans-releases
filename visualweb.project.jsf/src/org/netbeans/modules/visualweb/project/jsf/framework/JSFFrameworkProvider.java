/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.visualweb.project.jsf.framework;

import org.netbeans.modules.visualweb.project.jsf.JsfProjectTemplateJakarta;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectConstants;
import org.netbeans.modules.visualweb.project.jsf.api.ProjectTemplate;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectUtils;
import org.netbeans.modules.visualweb.project.jsf.api.JsfProjectClassPathExtender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.Set;
import java.util.HashSet;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;

import org.netbeans.modules.j2ee.dd.api.web.*;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileLock;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;

import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.FrameworkConfigurationPanel;
import org.openide.util.NbBundle;

/**
 *
 * @author Po-Ting Wu
 */
public class JSFFrameworkProvider extends WebFrameworkProvider {

    private JSFConfigurationPanel panel;
    /** Creates a new instance of JSFFrameworkProvider */
    public JSFFrameworkProvider() {
        super(
                NbBundle.getMessage(JSFFrameworkProvider.class, "JSF_Name"),               // NOI18N
                NbBundle.getMessage(JSFFrameworkProvider.class, "JSF_Description"));       //NOI18N
    }

    public Set extend (WebModule webModule) {
        FileObject fileObject = webModule.getDocumentBase();;
        Project project = FileOwnerQuery.getOwner(fileObject);

        // <RAVE> Add the VWP libraries to the project
        ProjectTemplate template = new JsfProjectTemplateJakarta();
        try {
            template.addLibrary(project);

            FileObject dd = webModule.getDeploymentDescriptor();
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            ClassPath cp = ClassPath.getClassPath(fileObject, ClassPath.COMPILE);
            boolean isMyFaces = cp.findResource("org/apache/myfaces/webapp/StartupServletContextListener.class") != null; //NOI18N
            if (ddRoot != null) {
                if (!WebApp.VERSION_2_5.equals(ddRoot.getVersion())) {
                    if (isMyFaces) {
                        JsfProjectUtils.removeLibraryReferences(project,
                                new Library[]{ LibraryManager.getDefault().getLibrary("jsf-designtime")},
                                JsfProjectClassPathExtender.LIBRARY_ROLE_DESIGN);
                        JsfProjectUtils.removeLibraryReferences(project,
                                new Library[]{ LibraryManager.getDefault().getLibrary("jsf-runtime")},
                                JsfProjectClassPathExtender.LIBRARY_ROLE_DEPLOY);
                    } else {
                        boolean hasJstl = cp.findResource("javax/servlet/jsp/jstl/core/Config.class") != null; // NOI18N
 
                        if (!hasJstl) {
                            Library jstlLibrary = LibraryManager.getDefault().getLibrary("jstl11");
                            
                            if (jstlLibrary != null) {
                                JsfProjectUtils.addLibraryReferences(project, new Library[] { jstlLibrary },
                                        JsfProjectClassPathExtender.LIBRARY_ROLE_DESIGN);
                                JsfProjectUtils.addLibraryReferences(project, new Library[] { jstlLibrary },
                                        JsfProjectClassPathExtender.LIBRARY_ROLE_DEPLOY);
                            }
                        }
                    }
                }
            }

            FileSystem fileSystem = webModule.getWebInf().getFileSystem();
            fileSystem.runAtomicAction(new CreateFacesConfig(webModule, isMyFaces, template));
        } catch (FileNotFoundException exc) {
            ErrorManager.getDefault().notify(exc);
        } catch (IOException exc) {
            ErrorManager.getDefault().notify(exc);
        }
        return null;
    }

    public static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuffer sbuffer = new StringBuffer();
        String lineSep = System.getProperty("line.separator");//NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sbuffer.append(line);
            sbuffer.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sbuffer.toString();
    }
    
    public java.io.File[] getConfigurationFiles(org.netbeans.modules.web.api.webmodule.WebModule wm) {
        // The JavaEE 5 introduce web modules without deployment descriptor. In such wm can not be jsf used.
        FileObject dd = wm.getDeploymentDescriptor();
        if (dd != null){
            FileObject[] filesFO = JSFConfigUtilities.getConfiFilesFO(wm.getDeploymentDescriptor());
            File[] files = new File[filesFO.length];
            for (int i = 0; i < filesFO.length; i++)
                files[i] = FileUtil.toFile(filesFO[i]);
            if (files.length > 0)
                return files;
        }
        return null;
    }
    
    public FrameworkConfigurationPanel getConfigurationPanel(WebModule webModule) {
        boolean defaultValue = (webModule == null || !isInWebModule(webModule));
        panel = new JSFConfigurationPanel(!defaultValue);
        if (!defaultValue){
            // get configuration panel with values from the wm
            Servlet servlet = JSFConfigUtilities.getActionServlet(webModule.getDeploymentDescriptor());
            panel.setServletName(servlet.getServletName());
            panel.setURLPattern(JSFConfigUtilities.getActionServletMapping(webModule.getDeploymentDescriptor()));
            panel.setValidateXML(JSFConfigUtilities.validateXML(webModule.getDeploymentDescriptor()));
            panel.setVerifyObjects(JSFConfigUtilities.verifyObjects(webModule.getDeploymentDescriptor()));
        }
        
        return panel;
    }
    
    public boolean isInWebModule(org.netbeans.modules.web.api.webmodule.WebModule wm) {
        return JSFConfigUtilities.getActionServlet(wm.getDeploymentDescriptor()) == null ? false : true;
    }
    
    public static void createFile(FileObject target, String content, String encoding) throws IOException{
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();
            
        } finally {
            lock.releaseLock();
        }
    }
    
    private class  CreateFacesConfig implements FileSystem.AtomicAction{
        WebModule webModule;
        boolean isMyFaces;
        ProjectTemplate template;
        
        public CreateFacesConfig(WebModule webModule, boolean isMyFaces, ProjectTemplate template){
            this.webModule = webModule;
            this.isMyFaces = isMyFaces;
            this.template = template;
        }
        
        public void run() throws IOException {            
            // Enter servlet into the deployment descriptor
            FileObject dd = webModule.getDeploymentDescriptor();
            WebApp ddRoot = DDProvider.getDefault().getDDRoot(dd);
            String j2eeLevel = webModule.getJ2eePlatformVersion();
            if (ddRoot != null){
                try{
                    // Set the context parameter
                    InitParam contextParam = (InitParam)ddRoot.createBean("InitParam"); // NOI18N
                    contextParam.setParamName("javax.faces.STATE_SAVING_METHOD"); // NOI18N
                    contextParam.setParamValue("server"); // NOI18N
                    ddRoot.addContextParam(contextParam);
                    
                    contextParam = (InitParam)ddRoot.createBean("InitParam"); // NOI18N
                    contextParam.setParamName("javax.faces.CONFIG_FILES"); // NOI18N
                    contextParam.setParamValue("/WEB-INF/navigation.xml,/WEB-INF/managed-beans.xml"); // NOI18N
                    ddRoot.addContextParam(contextParam);
                    
                    contextParam = (InitParam)ddRoot.createBean("InitParam"); //NOI18N
                    contextParam.setParamName("com.sun.faces.validateXml"); //NOI18N
                    if(panel == null || panel.validateXML())
                        contextParam.setParamValue("true"); //NOI18N
                    else
                        contextParam.setParamValue("false"); //NOI18N
                    ddRoot.addContextParam(contextParam);
                    
                    contextParam = (InitParam)ddRoot.createBean("InitParam"); //NOI18N
                    contextParam.setParamName("com.sun.faces.verifyObjects");  //NOI18N
                    if (panel != null && panel.verifyObjects())
                        contextParam.setParamValue("true");  //NOI18N
                    else
                        contextParam.setParamValue("false");  //NOI18N
                    ddRoot.addContextParam(contextParam);
                    
                    // The UpLoad Filter
                    Filter filter = (Filter)ddRoot.createBean("Filter"); // NOI18N
                    filter.setFilterName("UploadFilter"); // NOI18N
                    if (J2eeModule.JAVA_EE_5.equals(j2eeLevel))
                        filter.setFilterClass("com.sun.webui.jsf.util.UploadFilter"); // NOI18N
                    else
                        filter.setFilterClass("com.sun.rave.web.ui.util.UploadFilter"); // NOI18N
                    
                    contextParam = (InitParam)filter.createBean("InitParam"); // NOI18N
                    contextParam.setDescription("The maximum allowed upload size in bytes.  If this is set " +
                            "to a negative value, there is no maximum.  The default " +
                            "value is 1000000."); // NOI18N
                    contextParam.setParamName("maxSize"); // NOI18N
                    contextParam.setParamValue("1000000"); // NOI18N
                    filter.addInitParam(contextParam);
                    
                    contextParam = (InitParam)filter.createBean("InitParam"); // NOI18N
                    contextParam.setDescription("The size (in bytes) of an uploaded file which, if it is " +
                            "exceeded, will cause the file to be written directly to " +
                            "disk instead of stored in memory.  Files smaller than or " +
                            "equal to this size will be stored in memory.  The default " +
                            "value is 4096."); // NOI18N
                    contextParam.setParamName("sizeThreshold"); // NOI18N
                    contextParam.setParamValue("4096"); // NOI18N
                    filter.addInitParam(contextParam);
                    ddRoot.addFilter(filter);
                    
                    FilterMapping filterMapping = (FilterMapping)ddRoot.createBean("FilterMapping"); // NOI18N
                    filterMapping.setFilterName("UploadFilter"); // NOI18N
                    filterMapping.setServletName(panel.getServletName());
                    ddRoot.addFilterMapping(filterMapping);
                    
                    // The Servlets
                    Servlet servlet = (Servlet)ddRoot.createBean("Servlet"); // NOI18N
                    servlet.setServletName(panel.getServletName());
                    servlet.setServletClass("javax.faces.webapp.FacesServlet"); // NOI18N    
                    servlet.setLoadOnStartup(new BigInteger("1"));// NOI18N
                    ddRoot.addServlet(servlet);

                    servlet = (Servlet)ddRoot.createBean("Servlet"); // NOI18N
                    servlet.setServletName("ExceptionHandlerServlet");
                    servlet.setServletClass("com.sun.errorhandler.ExceptionHandler"); // NOI18N    

                    contextParam = (InitParam)servlet.createBean("InitParam"); // NOI18N
                    contextParam.setParamName("errorHost"); // NOI18N
                    contextParam.setParamValue("localhost"); // NOI18N
                    servlet.addInitParam(contextParam);

                    contextParam = (InitParam)servlet.createBean("InitParam"); // NOI18N
                    contextParam.setParamName("errorPort"); // NOI18N
                    contextParam.setParamValue("24444"); // NOI18N
                    servlet.addInitParam(contextParam);

                    ddRoot.addServlet(servlet);

                    servlet = (Servlet)ddRoot.createBean("Servlet"); // NOI18N
                    servlet.setServletName("ThemeServlet"); // NOI18N

                    if (J2eeModule.JAVA_EE_5.equals(j2eeLevel))
                        servlet.setServletClass("com.sun.webui.theme.ThemeServlet"); // NOI18N
                    else
                        servlet.setServletClass("com.sun.rave.web.ui.theme.ThemeServlet"); // NOI18N

                    ddRoot.addServlet(servlet);
                    
                    // The Servlet Mappings
                    ServletMapping mapping = (ServletMapping)ddRoot.createBean("ServletMapping"); // NOI18N
                    mapping.setServletName(panel.getServletName());
                    mapping.setUrlPattern(panel.getURLPattern());
                    ddRoot.addServletMapping(mapping);

                    mapping = (ServletMapping)ddRoot.createBean("ServletMapping"); // NOI18N
                    mapping.setServletName("ExceptionHandlerServlet");
                    mapping.setUrlPattern("/error/ExceptionHandler");
                    ddRoot.addServletMapping(mapping);

                    mapping = (ServletMapping)ddRoot.createBean("ServletMapping"); // NOI18N
                    mapping.setServletName("ThemeServlet"); // NOI18N
                    mapping.setUrlPattern("/theme/*"); // NOI18N
                    ddRoot.addServletMapping(mapping);

                    // Adjust the path to the startpage based on JSF parameters
                    WelcomeFileList wfl = ddRoot.getSingleWelcomeFileList();
                    wfl.setWelcomeFile(new String[] { "faces/index.jsp" });

                    // Catch ServletException
                    ErrorPage errorPage = (ErrorPage)ddRoot.createBean("ErrorPage");
                    errorPage.setExceptionType("javax.servlet.ServletException");
                    errorPage.setLocation("/error/ExceptionHandler");
                    ddRoot.addErrorPage(errorPage);

                    // Catch IOException
                    errorPage = (ErrorPage)ddRoot.createBean("ErrorPage");
                    errorPage.setExceptionType("java.io.IOException");
                    errorPage.setLocation("/error/ExceptionHandler");
                    ddRoot.addErrorPage(errorPage);

                    // Catch FacesException
                    errorPage = (ErrorPage)ddRoot.createBean("ErrorPage");
                    errorPage.setExceptionType("javax.faces.FacesException");
                    errorPage.setLocation("/error/ExceptionHandler");
                    ddRoot.addErrorPage(errorPage);

                    // Catch ApplicationException
                    errorPage = (ErrorPage)ddRoot.createBean("ErrorPage");
                    errorPage.setExceptionType("com.sun.rave.web.ui.appbase.ApplicationException");
                    errorPage.setLocation("/error/ExceptionHandler");
                    ddRoot.addErrorPage(errorPage);

                    // The JSP Configuration
                    if (!J2eeModule.J2EE_13.equals(j2eeLevel)) {
                        JspConfig jspConfig = (JspConfig)ddRoot.addBean("JspConfig"); // NOI18N
                        JspPropertyGroup jspGroup = (JspPropertyGroup)jspConfig.createBean("JspPropertyGroup"); // NOI18N
                        jspGroup.addUrlPattern("*.jspf");
                        jspGroup.setIsXml(true);
                        jspConfig.addJspPropertyGroup(jspGroup);
                    }
                    
                    if (isMyFaces) {
                        Listener facesListener = (Listener) ddRoot.createBean("Listener");
                        facesListener.setListenerClass("org.apache.myfaces.webapp.StartupServletContextListener");
                        ddRoot.addListener(facesListener);
                    }
                    ddRoot.write(dd);
                }
                catch (ClassNotFoundException cnfe){
                    ErrorManager.getDefault().notify(cnfe);
                }
            }
            
            FileObject documentBase = webModule.getDocumentBase();
            Project project = FileOwnerQuery.getOwner(documentBase);
            template.create(project, webModule.getJ2eePlatformVersion());

            /* Replace index.jsp and index.java with Page1.jsp and Page.java.
             * The reason to do this is web project always produces a welcome file index.jsp and open it as default.
             * Creator cannot just delete it and use jsp/java template to create the pair because the 'FileObject'
             * is 'baked' from the web project. Creator needs to replace the contents of index.jsp and also creates
             * its paired index.java.
             */
            FileObject indexjsp = documentBase.getFileObject("index.jsp"); //NOI18N
            FileObject pagejsp = documentBase.getFileObject("Page1.jsp"); //NOI18N
            if (indexjsp != null && pagejsp != null) {
                String content = readResource(pagejsp.getInputStream(), "UTF-8"); //NOI18N
                createFile(indexjsp, content.replaceAll("\\bPage1\\b", "index"), "UTF-8"); //NOI18N
                JsfProjectUtils.createProjectProperty(project, JsfProjectConstants.PROP_START_PAGE, "index.jsp"); // NOI18N
                pagejsp.delete();
            }

            FileObject beanBase = JsfProjectUtils.getPageBeanRoot(project);
            FileObject pagejava = beanBase.getFileObject("Page1.java"); //NOI18N
            if (pagejava != null) {
                FileObject indexjava = FileUtil.moveFile(pagejava, beanBase, "index"); //NOI18N
                String content = readResource(indexjava.getInputStream(), "UTF-8"); //NOI18N
                createFile(indexjava, content.replaceAll("\\bPage1\\b", "index"), "UTF-8"); //NOI18N
            }
        }
    }
}
