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
package org.netbeans.modules.xslt.project;

import java.awt.Dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.xslt.project.wizard.FoldersListSettings;
import org.openide.util.NbBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

import java.net.URISyntaxException;
import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import java.util.logging.Logger;
import java.text.MessageFormat;

import org.netbeans.modules.xml.wsdl.model.WSDLModelFactory;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.schema.model.SchemaModelFactory;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.locator.CatalogModelException;
import org.netbeans.modules.xml.xam.locator.CatalogModelFactory;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressHandle;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
/**
 * This class is used to populate catalog
 *
 * @author Sreenivasan Genipudi
 * @author Vitaly Bychkov
 * @version 1.0
 */
public class XsltProjectRetriever {
    ProgressHandle pg = null;
    private RetrieverWrapper retrieveWrap = new RetrieverWrapper();

    /**
     * Logger instance
     */
    private Logger logger =
        Logger.getLogger(XsltProjectRetriever.class.getName());

    private volatile String mStatus = null;

    private RetrieverUpdater mRetUpd = null;
    Dialog mDialog = null;
    /**
     * Construtor - takes in Project Location (File Object) as parameter 
     * and tries to populate the catalog
     * @param projectDirectory FileObject instance of project directory
     */
    public XsltProjectRetriever(FileObject projectDirectory) {
        retrieveWrap.mProjectDirectoryPath = projectDirectory.getPath();
        //mProjectDirectoryPath = projectDirectory.getPath();
    }

    private void init() {
        String initMsg = NbBundle.getMessage( XsltProjectRetriever.class, "LBL_Populate_Catalog" );
        pg = ProgressHandleFactory.createHandle(initMsg,  new org.openide.util.Cancellable() {
            public boolean cancel() {
                try {
                    pg.finish();
                }catch (Exception ex) {

                }
                return true;
            }});
    //  pg.setInitialDelay(2000);
      //  pg.setDisplayName(initMsg);

       mRetUpd = new RetrieverUpdater(pg);
       DialogDescriptor dd = new DialogDescriptor (mRetUpd,
                                                    initMsg,
                                                    true, // modal
                                                    new Object [0],
                                                    null,
                                                    DialogDescriptor.DEFAULT_ALIGN,
                                                    null,
                                                    null,
                                                    true);
        mDialog = DialogDisplayer.getDefault ().createDialog (dd);
        pg.start ();


    }

    public void execute() {
        try {
            init();
            Thread t = new Thread(retrieveWrap);
            t.start();
            mDialog.setVisible (true);

        }catch(Exception ex) {
            mRetUpd.setProgressMessage(NbBundle.getMessage( XsltProjectRetriever.class, "LBL_Populate_Catalog_Error" ));
        }
        finally {
            dispose();
        }
    }

    void dispose() {
        try {
            mDialog.setVisible(false);
            mDialog.dispose();
            mDialog = null;
        }catch (Exception ex) {

        }
        try {
            pg.finish();
        }catch (Exception ex) {

        }
    }


   class RetrieverWrapper implements Runnable {
       private File mProjectDirectory = null;
       private String mProjectDirectoryPath = null;
       private String mProjectSourcePath= null;
       private File mRetrieveToDirectory = null;
       private HashSet mVisitedXMLResources = new HashSet();


        public void run() {
            try {
                String projectDirPath = mProjectDirectoryPath;
                String sourceDirectoryPath = projectDirPath + "/"+ "src";
                mProjectSourcePath = sourceDirectoryPath.replace('\\','/');
                CommandlineXsltProjectXmlCatalogProvider.getInstance().setSourceDirectory(sourceDirectoryPath);

                mProjectDirectory = new File(projectDirPath);
                File sourceDirectory =
                    new File(sourceDirectoryPath);
                mRetrieveToDirectory =
                        new File(CommandlineXsltProjectXmlCatalogProvider.getInstance().getRetrieverPath());
                if (!mRetrieveToDirectory.exists()) {
                    mRetrieveToDirectory.mkdirs();
                }
                processSourceDir(sourceDirectory);
                moveCachedDirs();
                displayStatus(NbBundle.getMessage( XsltProjectRetriever.class, "LBL_Populate_Catalog_Complete" ));

            }catch(Exception ex) {
                displayStatus(NbBundle.getMessage( XsltProjectRetriever.class, "LBL_Populate_Catalog_Error" ));
            }
            finally {
                File localCatalogFile = new File(CommandlineXsltProjectXmlCatalogProvider.getInstance().getProjectWideCatalog());
                if (localCatalogFile.exists() && localCatalogFile.length() == 0) {
                    localCatalogFile.delete();
                }
                dispose();
            }

        }

       /**
        * Replace the retrieved dirs with long name to simpler ones...
        *
        * @throws Exception
        */
       private void moveCachedDirs() throws Exception {
           File catFile = new File(mRetrieveToDirectory.getParent(), "catalog.xml"); // NOI18N
           if (catFile.exists()) {
               DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
               DocumentBuilder builder = fact.newDocumentBuilder();
               Document catlogDoc = builder.parse(catFile);
               ArrayList uris = new ArrayList();
               NodeList sas = catlogDoc.getElementsByTagName("system");  // NOI18N
               HashMap dirMap = new HashMap();
               if (sas != null) { // find them
                   for (int i=0; i<sas.getLength(); i++) {
                       Element sys = (Element) sas.item(i);
                       String furi = sys.getAttribute("uri");  // NOI18N
                       if (!furi.startsWith("src/cached")) {  // NOI18N
                           uris.add(furi);

                           // map retreived dir to cached dir..
                           File cf = new File(mRetrieveToDirectory.getParentFile(), furi);
                           File pcf = cf.getParentFile();
                           File mpcf = (File) dirMap.get(pcf);
                           if (mpcf == null) { // create a new cached dir for this
                               String cacheDirName = null;
                               int baseCount = FoldersListSettings.getDefault().getNewProjectCount() + 1;
                               String formater = NbBundle.getMessage(XsltProjectRetriever.class, "LBL_DefaultCacheDirName");  // NOI18N
                               while ((cacheDirName = validFreeCacheDirName(mRetrieveToDirectory, formater, baseCount)) == null) {
                                       baseCount++;
                               }
                               mpcf = new File(mRetrieveToDirectory, cacheDirName);
                               mpcf.mkdirs();
                               dirMap.put(pcf, mpcf);
                           }

                           // move the file
                           FileObject dst = FileUtil.toFileObject(FileUtil.normalizeFile(mpcf));
                           FileObject src = FileUtil.toFileObject(FileUtil.normalizeFile(cf));
                           FileUtil.moveFile(src, dst, src.getName());

                           // update catalog.xml uirs...  furi -> mpcf
                           Attr atr = sys.getAttributeNode("uri");   // NOI18N
                           atr.setNodeValue("src/" + mpcf.getName() + "/"+cf.getName());
                           // System.out.println("newURI: "+sys.getAttribute("uri"));
                       }
                   }

                   // remove retreived dirs..
                   Iterator it = dirMap.keySet().iterator();
                   while (it.hasNext()) {
                       File key = (File)it.next();
                       FileObject src = FileUtil.toFileObject(FileUtil.normalizeFile(key));
                       FileObject root = FileUtil.toFileObject(FileUtil.normalizeFile(mRetrieveToDirectory));
                       while (src.getParent() != root) {
                           src = src.getParent();
                       }
                       src.delete();
                   }

                   // update catalog.xml
                   DOMSource src = new DOMSource(catlogDoc);
                   FileOutputStream fos = new FileOutputStream(catFile);
                   StreamResult rest = new StreamResult(fos);
                   TransformerFactory transFact = TransformerFactory.newInstance();
                   Transformer transformer = transFact.newTransformer();
                   transformer.transform(src, rest);
               }
           }
       }

       private String validFreeCacheDirName(final File parentFolder, final String formater, final int index) {
           String name = MessageFormat.format(formater, new Object[] {new Integer(index)});
           File file = new File(parentFolder, name);
           return file.exists() ? null : name;
       }

           /**
            * Process the list of source directories to generate JBI.xml
            * @param sourceDirs list of source directory
            */
           private void processSourceDirs(List<File> sourceDirs) {
               Iterator<File> it = sourceDirs.iterator();
               while (it.hasNext()) {
                   File sourceDir = it.next();
                   processSourceDir(sourceDir);
               }

           }

           /**
            * Proces the source directory to generate JBI.xml
            * @param sourceDir
            */
           private void processSourceDir(File sourceDir) {
               processFileObject(sourceDir);
           }

           /**
            * Process the file object to generate JBI.xml
            * @param file BPEL file location
            */
           private void processFileObject(File file) {
               if (file.isDirectory()) {
                   processFolder(file);
               } else {
                   processFile(file);
               }
           }

           /**
            * Process the folder to generate JBI.xml
            * @param fileDir  Folder location
            */
           private void processFolder(File fileDir) {
               File[] children = fileDir.listFiles();

               for (int i = 0; i < children.length; i++) {
                   processFileObject(children[i]);
               }
           }

           /**
            * Process the file to generate JBI.xml
            * @param file input file
            */
           private void processFile(File file) {
               String fileName = file.getName();
               String fileExtension = null;
               int dotIndex = fileName.lastIndexOf('.');
               if (dotIndex != -1) {
                   fileExtension = fileName.substring(dotIndex + 1);
               }

// TODO a&m | r               
//               if (fileExtension != null && fileExtension.equalsIgnoreCase("bpel")) {
//
//                   BpelModelFactory bpelFactory = (BpelModelFactory)Lookup.getDefault().lookup(BpelModelFactory.class);
//                   FileObject fobj = FileUtil.toFileObject(FileUtil.normalizeFile(file));
//                   ModelSource ms = null;
//                   try {
//                       ms = Utilities.createModelSource(fobj, true);
//                   }catch (Exception ex) {
//                       logger.log(Level.SEVERE,
//                                  "Error encountered while creating module source - " +
//                                  file.toURI());
//                       throw new RuntimeException("Error encountered while creating module source - " +
//                                                  file.toURI());
//                   }
//
//                   BpelModel bm = bpelFactory.getModel(ms);
//                   org.netbeans.modules.bpel.model.api.Import[] imports = bm.getProcess().getImports();
//                   WSDLModel wsdlModel= null;
//                   SchemaModel schModel = null;
//
//                   for (org.netbeans.modules.bpel.model.api.Import imprt:imports) {
//                       wsdlModel = getWsdlModel(imprt);
//                       if (wsdlModel != null) {
//                           processWSDLImport(wsdlModel, imprt);
//                       } else {
//
//                          SchemaModel scMdl =  getSchemaModel(imprt);
//                          if (scMdl != null) {
//                               processSchemaImport(scMdl, imprt);
//                          }
//                       }
//
//                   }
//
//               }

           }

//           void processSchemaImport(SchemaModel scm, org.netbeans.modules.bpel.model.api.Import imports ) {
//               String importLocation = imports.getLocation();
//               if (importLocation != null) {
//                   String importLocationLowerCase = importLocation;
//                   if (!mVisitedXMLResources.contains(importLocationLowerCase)) {
//                       mVisitedXMLResources.add(importLocationLowerCase);
//                       String resourceName = null;
//                       Document doc = scm.getDocument();
//                       if (doc != null) {
//                           resourceName= doc.getLocalName();
//                       } else {
//                           resourceName = "";
//                       }
//                       URI resourceURI = externalResource(resourceName, importLocationLowerCase);
//                       if (resourceURI != null) {
//                           if (!ApacheResolverHelper.isPresent(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog()).getAbsolutePath(),
//                                                               resourceURI.toString())) {
//                               try {
//                                   FileObject catalogFO = FileUtil.toFileObject(FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath())));
//                                   displayStatus(importLocationLowerCase);
//
//
//                                    Retriever.getDefault().retrieveResource(catalogFO,
//                                                                   FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog())).toURI(),
//                                                                            resourceURI);
//
//                               } catch (Exception ex) {
//                                   logger.log(Level.SEVERE,
//                                              "Error encountered while retreiving file - " +
//                                              importLocation);
//                               }
//                           }
//                       } else {
//                           SchemaModel scMdl =  getSchemaModel(imports);
//                           if (scMdl != null) {
//                              Collection<org.netbeans.modules.xml.schema.model.Import> subImports = scMdl.getSchema().getImports();
//                              processSchemaImport(scMdl, subImports);
//                           }
//                       }
//                   } else {
//                       System.out.println(" ALREADY DOWNLOADED!!"+importLocationLowerCase);
//                   }
//               }
//
//           }
//           void processSchemaImport(SchemaModel scm, Collection<org.netbeans.modules.xml.schema.model.Import> colImports ) {
//               for (org.netbeans.modules.xml.schema.model.Import imports: colImports) {
//                   String importLocation = imports.getSchemaLocation();
//                   if (importLocation != null) {
//                       String importLocationLowerCase = importLocation;
//                       if (!mVisitedXMLResources.contains(importLocationLowerCase)) {
//                           mVisitedXMLResources.add(importLocationLowerCase);
//                           String resourceName = null;
//                           Document doc = scm.getDocument();
//                           if (doc != null) {
//                               resourceName= doc.getLocalName();
//                           } else {
//                               resourceName = "";
//                           }
//                           URI resourceURI = externalResource(resourceName, importLocationLowerCase);
//                           if (resourceURI != null) {
//                               if (!ApacheResolverHelper.isPresent(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog()).getAbsolutePath(),
//                                                                   resourceURI.toString())) {
//                                   try {
//                 /*                      Retriever.getDefault().retrieveResource(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath()), 
//                                                                               new URI(importLocation));
//                 */
//                                       FileObject catalogFO = FileUtil.toFileObject(FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath())));
//                                       displayStatus(importLocationLowerCase);
//
//
//                                        Retriever.getDefault().retrieveResource(catalogFO,
//                                                                       FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog())).toURI(),
//                                                                                resourceURI);
//
//                                   } catch (Exception ex) {
//                                       logger.log(Level.SEVERE,
//                                                  "Error encountered while retreiving file - " +
//                                                  importLocation);
//                                       //throw new RuntimeException(ex);
//                                   }
//                               }
//                           } else {
//                               SchemaModel scMdl =  getSchemaModel(imports);
//                               if (scMdl != null) {
//                          /*         String targetNameSpace = scMdl.getSchema().getTargetNamespace();
//                      String versionInfo =scMdl.getSchema().getVersion();
//                      if (versionInfo == null) {
//                          versionInfo = "";
//                      }
//                      String xsdId = scMdl.getSchema().getId();
//                      if (xsdId == null) {
//                          xsdId = "";
//                      }
//                      String xsdKey = targetNameSpace+versionInfo+xsdId;
//                      if (!mVisitedXMLResources.contains(xsdKey)) {
//                          mVisitedXMLResources.add(xsdKey);*/
//                                      Collection<org.netbeans.modules.xml.schema.model.Import> subImports = scMdl.getSchema().getImports();
//                                      processSchemaImport(scMdl, subImports);
//                                   //}    
//                               }
//                           }
//                       } else {
//                           System.out.println(" ALREADY DOWNLOADED!!"+importLocationLowerCase);
//                       }
//                   }
//               }
//
//           }
//
//           void processWSDLImport(WSDLModel wsdlModel, org.netbeans.modules.bpel.model.api.Import imports ) {
//               String importLocation = imports.getLocation();
//               String importLocationLowerCase = importLocation;
//               String wsdlKey = importLocationLowerCase;
//               if (!mVisitedXMLResources.contains(wsdlKey)) {
//                   mVisitedXMLResources.add(wsdlKey);
//                   String resourceName = null;
//                   Document doc = wsdlModel.getDocument();
//                   if (doc != null) {
//                       resourceName= doc.getLocalName();
//                   } else {
//                       resourceName = "";
//                   }
//                   URI resourceURI = externalResource(wsdlModel.getDocument().getLocalName(), importLocationLowerCase);
//                   if (resourceURI != null) {
//                       if (!ApacheResolverHelper.isPresent(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog()).getAbsolutePath(),
//                                                           resourceURI.toString())) {
//                           try {
//                               /*                        Retriever.getDefault().retrieveResource(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath()),
//                                                                        new URI(importLocation));
//                                                                       */
//                               FileObject catalogFO =
//                                   FileUtil.toFileObject(FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath())));
//                               displayStatus(importLocationLowerCase);
//
//                               Retriever.getDefault().retrieveResource(catalogFO,
//                                                                       FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog())).toURI(),
//                                                                       resourceURI);
//
//                           } catch (Exception ex) {
//                               logger.log(Level.SEVERE,
//                                          "Error encountered while retreiving file - " +
//                                          importLocation);
//                               //throw new RuntimeException(ex);
//                           }
//                       }
//                   } else {
//                       WSDLModel wm = wsdlModel;
//
//                       Collection<org.netbeans.modules.xml.wsdl.model.Import> subImports =
//                           wm.getDefinitions().getImports();
//                       if (subImports != null && subImports.size() > 0) {
//                           processWSDLImport(wm, subImports);
//                       }
//                       Types types = wm.getDefinitions().getTypes();
//                       if (types != null) {
//                           Collection<org.netbeans.modules.xml.schema.model.Schema> schemas =
//                               types.getSchemas();
//
//                           for (Schema schema: schemas) {
//                                   Collection<org.netbeans.modules.xml.schema.model.Import> colImports1 =
//                                       schema.getImports();
//                                   processSchemaImport(schema.getModel(), colImports1);
//                           }
//                       }
//                   }
//               }    else {
//                       System.out.println(" ALREADY DOWNLOADED!!"+importLocationLowerCase);
//                   }
//           }
//
//
//           void processWSDLImport(WSDLModel wsdlModel, Collection<org.netbeans.modules.xml.wsdl.model.Import> colImports) {
//               for (org.netbeans.modules.xml.wsdl.model.Import imports: colImports) {
//                   String importLocation = imports.getLocation();
//                   String importLocationLowerCase = importLocation;
//                   String wsdlKey = importLocationLowerCase;
//                   if (!mVisitedXMLResources.contains(wsdlKey)) {
//                       mVisitedXMLResources.add(wsdlKey);
//                       String resourceName = null;
//                       Document doc = wsdlModel.getDocument();
//                       if (doc != null) {
//                           resourceName= doc.getLocalName();
//                       } else {
//                           resourceName = "";
//                       }
//                       URI resourceURI = externalResource(wsdlModel.getDocument().getLocalName(), importLocationLowerCase);
//                       if (resourceURI != null) {
//                           if (!ApacheResolverHelper.isPresent(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog()).getAbsolutePath(),
//                                                               resourceURI.toString())) {
//                               try {
//                                   /*                        Retriever.getDefault().retrieveResource(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath()),
//                                                                            new URI(importLocation));
//                                                                           */
//                                   FileObject catalogFO =
//                                       FileUtil.toFileObject(FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getRetrieverPath())));
//                                   displayStatus(importLocationLowerCase);
//
//                                   Retriever.getDefault().retrieveResource(catalogFO,
//                                                                           FileUtil.normalizeFile(new File(CommandlineBpelProjectXmlCatalogProvider.getInstance().getProjectWideCatalog())).toURI(),
//                                                                           resourceURI);
//
//                               } catch (Exception ex) {
//                                   logger.log(Level.SEVERE,
//                                              "Error encountered while retreiving file - " +
//                                              importLocation);
//                                   //throw new RuntimeException(ex);
//                               }
//                           }
//                       } else {
//                           WSDLModel wm = getWsdlModel(imports);
//                           if (wm != null) {
//           /*                    String targetNameSpace = 
//                                   wm.getDefinitions().getTargetNamespace();
//                               String wsdlName = wm.getDefinitions().getName();
//                               if (wsdlName == null) {
//                                   wsdlName = "";
//                               }
//                               String wsdlKey = targetNameSpace + wsdlName;
//                               if (!mVisitedXMLResources.contains(wsdlKey)) {
//                                   mVisitedXMLResources.add(wsdlKey);*/
//
//
//                                   Collection<org.netbeans.modules.xml.wsdl.model.Import> subImports =
//                                       wm.getDefinitions().getImports();
//                                   if (subImports != null && subImports.size() > 0) {
//                                       processWSDLImport(wm, subImports);
//                                   }
//                                   Types types = wm.getDefinitions().getTypes();
//                                   if (types != null) {
//                                       Collection<org.netbeans.modules.xml.schema.model.Schema> schemas =
//                                           types.getSchemas();
//
//                                       for (Schema schema: schemas) {
//                                    /*       String targetNameSpace1 = 
//                                               schema.getTargetNamespace();
//                                           String versionInfo1 = schema.getVersion();
//                                           if (versionInfo1 == null) {
//                                               versionInfo1 = "";
//                                           }
//                                           String xsdId1 = schema.getId();
//                                           if (xsdId1 == null) {
//                                               xsdId1 = "";
//                                           }
//                                           String xsdKey1 = 
//                                               targetNameSpace1 + versionInfo1 + xsdId1;
//                                           if (!mVisitedXMLResources.contains(xsdKey1)) {
//                                               mVisitedXMLResources.add(xsdKey1);
//                                               */
//                                               Collection<org.netbeans.modules.xml.schema.model.Import> colImports1 =
//                                                   schema.getImports();
//                                               processSchemaImport(schema.getModel(), colImports1);
//                                          // }
//                                       }
//                                   }
//                               }
//                           }
//                   }    else {
//                           System.out.println(" ALREADY DOWNLOADED!!"+importLocationLowerCase);
//                       }
//
//               }
//
//           }

           public void displayStatus(String stats ){
               //pg.progress(stats);  
                mRetUpd.setProgressMessage( NbBundle.getMessage( XsltProjectRetriever.class, "LBL_Retrieving", new Object[] {stats} ));
           }


//           public  WSDLModel getWsdlModel( Import imp ) {
//              if (!Import.WSDL_IMPORT_TYPE.equals(imp.getImportType())) {
//                  return null;
//              }
//              String location = imp.getLocation();
//              WSDLModel wsdlModel;
//              if (location == null) {
//                  return null;
//              }
//              try {
//                  URI uri = new URI(location);
//                  ModelSource source = CatalogModelFactory.getDefault()
//                          .getCatalogModel(imp.getModel().getModelSource())
//                          .getModelSource(uri, imp.getModel().getModelSource());
//                  wsdlModel = WSDLModelFactory.getDefault().getModel(source);
//              }
//              catch (URISyntaxException e) {
//                  wsdlModel = null;
//              }
//              catch (CatalogModelException e) {
//                  wsdlModel = null;
//              }
//              if (wsdlModel != null && wsdlModel.getState() == Model.State.NOT_WELL_FORMED) {
//                  return null;
//              }
//              return wsdlModel;
//          }
//
//           public SchemaModel getSchemaModel( Import imp ) {
//               if ( !Import.SCHEMA_IMPORT_TYPE.equals( imp.getImportType())){
//                   return null;
//               }
//               String location = imp.getLocation();
//               SchemaModel schemaModel ;
//               if (location == null) {
//                   return null;
//               }
//               try {
//                   URI uri = new URI( location );
//                   ModelSource modelSource = CatalogModelFactory.getDefault().
//                               getCatalogModel(imp.getModel().getModelSource())
//                               .getModelSource(uri, imp.getModel().getModelSource());
//
//                   schemaModel = SchemaModelFactory.getDefault().
//                       getModel( modelSource );
//               }
//               catch (URISyntaxException e) {
//                   schemaModel = null;
//               }
//               catch (CatalogModelException e) {
//                   schemaModel = null;
//               }
//               if (schemaModel != null && schemaModel.getState() == Model.State.NOT_WELL_FORMED) {
//                   schemaModel = null;
//               }
//               return schemaModel;
//           }

           public SchemaModel getSchemaModel( org.netbeans.modules.xml.schema.model.Import imp ) {
               String location = imp.getSchemaLocation();
               SchemaModel schemaModel ;
               if (location == null) {
                   return null;
               }
               try {
                   URI uri = new URI( location );
                   ModelSource modelSource = CatalogModelFactory.getDefault().
                               getCatalogModel(imp.getModel().getModelSource())
                               .getModelSource(uri, imp.getModel().getModelSource());

                   schemaModel = SchemaModelFactory.getDefault().
                       getModel( modelSource );
               }
               catch (URISyntaxException e) {
                   schemaModel = null;
               }
               catch (CatalogModelException e) {
                   schemaModel = null;
               }
               if (schemaModel != null && schemaModel.getState() == Model.State.NOT_WELL_FORMED) {
                   schemaModel = null;
               }
               return schemaModel;
           }

           public  WSDLModel getWsdlModel( org.netbeans.modules.xml.wsdl.model.Import imp ) {
              String location = imp.getLocation();
              WSDLModel wsdlModel;
              if (location == null) {
                  return null;
              }
              try {
                  URI uri = new URI(location);
                  ModelSource source = CatalogModelFactory.getDefault()
                          .getCatalogModel(imp.getModel().getModelSource())
                          .getModelSource(uri, imp.getModel().getModelSource());
                  wsdlModel = WSDLModelFactory.getDefault().getModel(source);
              }
              catch (URISyntaxException e) {
                  wsdlModel = null;
              }
              catch (CatalogModelException e) {
                  wsdlModel = null;
              }
              if (wsdlModel != null && wsdlModel.getState() == Model.State.NOT_WELL_FORMED) {
                  return null;
              }
              return wsdlModel;
           }

           URI externalResource(String resourceName, String location) {
               try {
                   if (location.startsWith("http:") || location.startsWith("https:")) {
                       return new URI(location);
                   }
                   File resourceFile = null;
                   File normalizedLocation = null;
                   if (location.startsWith("file:")) {
                       URI fileURI = new URI(location);
                       resourceFile = new File(fileURI);
                       normalizedLocation = FileUtil.normalizeFile(resourceFile);
                       if (normalizedLocation.getAbsolutePath().replace('\\','/').indexOf(mProjectSourcePath) != -1) {
                           return null;
                       }
                       if (resourceFile.exists()) {
                           return normalizedLocation.toURI();
                       }
                   }
                    if (resourceFile == null) {
                        resourceFile = new File(location);
                    }
                    if (!resourceFile.exists()) {
                        resourceFile = new File(this.mProjectSourcePath,location );
                    }
                    normalizedLocation = FileUtil.normalizeFile(resourceFile);
                    if (normalizedLocation.exists()) {
                        if (normalizedLocation.getAbsolutePath().replace('\\','/').indexOf(mProjectSourcePath) != -1) {
                            return null;
                        } else {
                            return normalizedLocation.toURI();
                        }
                    }

               }catch (Exception ex) {
                   logger.severe("Problem in the imported  location "+location+" of "+resourceName);
               }
               return null;
           }

   }

    /**
     *
     */
    class RetrieverUpdater extends javax.swing.JPanel {

        private JComponent progress;

        /**
         * Creates new form ModuleUpdaterProgress
         */
        public RetrieverUpdater(ProgressHandle handle) {
            progress = ProgressHandleFactory.createProgressComponent(handle);
            initComponents();
            //#67914: On macosx, the background of JTextField is white even if non-editable:
            message.setBackground(javax.swing.UIManager.getColor("Panel.background")); // NOI18N
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        private void initComponents() {                          
            java.awt.GridBagConstraints gridBagConstraints;

            innerPanel = new javax.swing.JPanel();
            message = new javax.swing.JTextField();

            setLayout(new java.awt.GridBagLayout());

            setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 11, 11));
            innerPanel.setLayout(new java.awt.BorderLayout());

            innerPanel.add(progress, BorderLayout.CENTER);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            add(innerPanel, gridBagConstraints);

            message.setColumns(40);
            message.setEditable(false);
            message.setFont(javax.swing.UIManager.getFont("Label.font"));
            message.setBorder(null);
            message.setDisabledTextColor(javax.swing.UIManager.getColor("Label.foreground"));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
            add(message, gridBagConstraints);

        }                        


        // Variables declaration - do not modify                     
        public javax.swing.JPanel innerPanel;
        public javax.swing.JTextField message;
        // End of variables declaration                   

        public void setProgressMessage (String name) {
            message.setText (name);
        }
    }
}
