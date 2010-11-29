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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.autoupdate.services;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.Module;
import org.netbeans.api.autoupdate.InstallSupport;
import org.netbeans.api.autoupdate.InstallSupport.Installer;
import org.netbeans.api.autoupdate.OperationSupport.Restarter;
import org.netbeans.api.autoupdate.InstallSupport.Validator;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationContainer.OperationInfo;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.core.startup.Main;
import org.netbeans.modules.autoupdate.updateprovider.NetworkAccess;
import org.netbeans.modules.autoupdate.updateprovider.NetworkAccess.Task;
import org.netbeans.updater.ModuleDeactivator;
import org.netbeans.updater.ModuleUpdater;
import org.netbeans.updater.UpdaterInternal;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbCollections;

/**
 *
 * @author Jiri Rechtacek
 */
public class InstallSupportImpl {
    private InstallSupport support;
    private boolean progressRunning = false;
    private static final Logger LOG = Logger.getLogger (InstallSupportImpl.class.getName ());
    
    private static final String AUTOUPDATE_SERVICES_MODULE = "org.netbeans.modules.autoupdate.services"; // NOI18N
    
    private Map<UpdateElementImpl, File> element2Clusters = null;
    private final Set<File> downloadedFiles = new HashSet<File> ();
    private boolean isGlobal;
    private int wasDownloaded = 0;
    
    private Future<Boolean> runningTask;
    
    private static enum STEP {
        NOTSTARTED,
        DOWNLOAD,
        VALIDATION,
        INSTALLATION,
        RESTART,
        FINISHED,
        CANCEL
    }       

    private STEP currentStep = STEP.NOTSTARTED;
    
    // validation results
    private Collection<UpdateElementImpl> trusted = new ArrayList<UpdateElementImpl> ();
    private Collection<UpdateElementImpl> signed = new ArrayList<UpdateElementImpl> ();
    private Map<UpdateElement, Collection<Certificate>> certs = new HashMap<UpdateElement, Collection<Certificate>> ();
    private List<? extends OperationInfo> infos = null;
    
    private ExecutorService es = null;
    
    public InstallSupportImpl (InstallSupport installSupport) {
        support = installSupport;
    }
    
    public boolean doDownload (final ProgressHandle progress/*or null*/, boolean isGlobal) throws OperationException {
        this.isGlobal = isGlobal;
        Callable<Boolean> downloadCallable = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                assert support.getContainer ().listInvalid ().isEmpty () : support + ".listInvalid().isEmpty() but " + support.getContainer ().listInvalid ();
                synchronized(this) {
                    currentStep = STEP.DOWNLOAD;
                }

                infos = support.getContainer ().listAll ();
                List <OperationInfo<?>> newInfos = new ArrayList <OperationInfo<?>>();
                for(OperationInfo <?> i : infos) {
                    if(i.getUpdateUnit().getInstalled()!=null &&
                            i.getUpdateUnit().getInstalled().equals(i.getUpdateElement())   ) {
                        //internal update, replace by required elements
                        for(UpdateElement e : i.getRequiredElements()) {
                            boolean add = true;
                            for(OperationInfo <?> in : newInfos) {
                                if(in.getUpdateElement().equals(e)) {
                                    add = false;
                                    break;
                                }
                            }
                            if(add) {
                                OperationContainer<InstallSupport> upd = OperationContainer.createForUpdate();
                                OperationInfo<?> ii = upd.add(e);
                                newInfos.add(ii);
                            }
                        }
                    } else {
                        newInfos.add(i);
                    }
                }
                infos = newInfos;
                
                int size = 0;
                for (OperationInfo info : infos) {
                    size += info.getUpdateElement().getDownloadSize();
                }
                
                // start progress
                if (progress != null) {
                    progress.start();
                    progress.progress(NbBundle.getMessage(InstallSupportImpl.class, "InstallSupportImpl_Download_Estabilish"));
                    progressRunning = false;
                }
                
                int aggregateDownload = 0;
                
                try {
                    for (OperationInfo info : infos) {
                        if (cancelled()) return false;
                        
                        int increment = doDownload(info, progress, aggregateDownload, size);
                        if (increment == -1) {
                            return false;
                        }
                        aggregateDownload += increment;
                    }
                }  finally {
                    // end progress
                    if (progress != null) {
                        progress.progress("");
                        progress.finish();
                    }
                }
                
                assert size == aggregateDownload : "Was downloaded " + aggregateDownload + ", planned was " + size;
                wasDownloaded = aggregateDownload;
                return true;
            }
        };
        
        boolean retval =  false;
        try {
            runningTask = getExecutionService ().submit (downloadCallable);
            retval = runningTask.get ();
        } catch (CancellationException ex) {
            LOG.log (Level.FINE, "InstallSupport.doDownload was cancelled", ex); // NOI18N
            return false;
        } catch(InterruptedException iex) {
            Exceptions.printStackTrace(iex);
        } catch(ExecutionException iex) {
            if (! (iex.getCause() instanceof OperationException)) {
                Exceptions.printStackTrace(iex);
            } else {
                throw (OperationException) iex.getCause ();
            }
        }
        return retval;
    }

    public boolean doValidate (final Validator validator, final ProgressHandle progress/*or null*/) throws OperationException {
        assert validator != null;
        Callable<Boolean> validationCallable = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                synchronized(this) {
                    assert currentStep != STEP.FINISHED;
                    if (currentStep == STEP.CANCEL) return false;
                    currentStep = STEP.VALIDATION;
                }
                assert support.getContainer ().listInvalid ().isEmpty () : support + ".listInvalid().isEmpty() but " + support.getContainer ().listInvalid ();

                // start progress
                if (progress != null) {
                    progress.start (wasDownloaded);
                }
                
                int aggregateVerified = 0;
                
                try {
                    for (OperationInfo info : infos) {
                        if (cancelled()) return false;
                        UpdateElementImpl toUpdateImpl = Trampoline.API.impl(info.getUpdateElement());
                        boolean hasCustom = toUpdateImpl.getInstallInfo().getCustomInstaller() != null;
                        if (hasCustom) {
                            // XXX: validation of custom installed
                            assert false : "InstallSupportImpl cannot support CustomInstaller!";
                        } else {
                            aggregateVerified += doValidate (info, progress, aggregateVerified);
                        }
                    }
                } finally {
                    // end progress
                    if (progress != null) {
                        progress.progress("");
                        progress.finish();
                    }
                }
                return true;
            }
        };
        boolean retval =  false;
        try {
            runningTask = getExecutionService ().submit (validationCallable);
            retval = runningTask.get ();
        } catch (CancellationException ex) {
            LOG.log (Level.FINE, "InstallSupport.doValidate was cancelled", ex); // NOI18N
            return false;
        } catch(InterruptedException iex) {
            if (iex.getCause() instanceof OperationException) {
                throw (OperationException) iex.getCause();
            }
            Exceptions.printStackTrace(iex);
        } catch(ExecutionException iex) {
            if (iex.getCause() instanceof OperationException) {
                throw (OperationException) iex.getCause();
            }
            Exceptions.printStackTrace(iex);
        }
        return retval;
    }
    
    private Set<ModuleUpdateElementImpl> affectedModuleImpls = null;
    private Set<FeatureUpdateElementImpl> affectedFeatureImpls = null; 
    
    public Boolean doInstall (final Installer installer, final ProgressHandle progress/*or null*/) throws OperationException {
        assert installer != null;
        Callable<Boolean> installCallable = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                synchronized(this) {
                    assert currentStep != STEP.FINISHED : currentStep + " != STEP.FINISHED";
                    if (currentStep == STEP.CANCEL) return false;
                    currentStep = STEP.INSTALLATION;
                }
                assert support.getContainer ().listInvalid ().isEmpty () : support + ".listInvalid().isEmpty() but " + support.getContainer ().listInvalid ();
                
                // do trust of so far untrusted certificates
                addTrustedCertificates ();

                affectedModuleImpls = new HashSet<ModuleUpdateElementImpl> ();
                affectedFeatureImpls = new HashSet<FeatureUpdateElementImpl> ();
                
                if (progress != null) progress.start();
                
                for (OperationInfo info : infos) {
                    UpdateElementImpl toUpdateImpl = Trampoline.API.impl (info.getUpdateElement ());
                    switch (toUpdateImpl.getType ()) {
                    case KIT_MODULE :
                    case MODULE :
                        affectedModuleImpls.add ((ModuleUpdateElementImpl) toUpdateImpl);
                        break;
                    case STANDALONE_MODULE :
                    case FEATURE :
                        affectedFeatureImpls.add ((FeatureUpdateElementImpl) toUpdateImpl);
                        affectedModuleImpls.addAll (((FeatureUpdateElementImpl) toUpdateImpl).getContainedModuleElements ());
                        break;
                    default:
                        // XXX: what other types
                        assert false : "Unsupported type " + toUpdateImpl;
                    }
                }
                
                boolean needsRestart = false;
                File targetCluster = null;
                List <UpdaterInfo> updaterFiles = new ArrayList <UpdaterInfo> ();
                
                for (ModuleUpdateElementImpl moduleImpl : affectedModuleImpls) {
                    synchronized(this) {
                        if (currentStep == STEP.CANCEL) {
                            if (progress != null) progress.finish ();
                            return false;
                        }
                    }
                    
                    // skip installed element
                    if (Utilities.isElementInstalled (moduleImpl.getUpdateElement ())) {
                        continue;
                    }
                    
                    // find target dir
                    UpdateElement installed = moduleImpl.getUpdateUnit ().getInstalled ();
                    targetCluster = getTargetCluster (installed, moduleImpl, isGlobal);
                    
                    URL source = moduleImpl.getInstallInfo ().getDistribution ();
                    LOG.log (Level.FINE, "Source URL for " + moduleImpl.getCodeName () + " is " + source);
                    
                    File dest = getDestination(targetCluster, moduleImpl.getCodeName(), source);
                    assert dest != null : "Destination file exists for " + moduleImpl + " in " + targetCluster;
                    
                    // check if 'updater.jar' or 'updater_<branding>_<locale>.jar' is being installed
                    if (AUTOUPDATE_SERVICES_MODULE.equals(moduleImpl.getCodeName())) {
                        LOG.log(Level.FINEST, AUTOUPDATE_SERVICES_MODULE + " is being installed, check if contains " + ModuleUpdater.AUTOUPDATE_UPDATER_JAR_PATH);
                    }

                    JarFile jf = new JarFile(dest);
                    boolean added = false;
                    try {
                       for (JarEntry entry : Collections.list(jf.entries())) {
                            if (ModuleUpdater.AUTOUPDATE_UPDATER_JAR_PATH.equals(entry.toString()) ||
                                    entry.toString().matches(ModuleUpdater.AUTOUPDATE_UPDATER_JAR_LOCALE_PATTERN)) {
                                LOG.log(Level.FINE, entry.toString() + " is being installed from " + moduleImpl.getCodeName());
                                updaterFiles.add(new UpdaterInfo(entry, jf, targetCluster));
                                needsRestart = true;
                                added = true;
                             }
                         }
                    } finally {
                        if (jf != null && !added) {
                            jf.close();
                        }
                     }


                    needsRestart |= needsRestart(installed != null, moduleImpl, dest);
                }
                
                try {
                    // store source of installed files
                    Utilities.writeAdditionalInformation (getElement2Clusters ());
                    for(int i=0;i<updaterFiles.size();i++) {
                        UpdaterInfo info = updaterFiles.get(i);
                        Utilities.writeUpdateOfUpdaterJar (info.getUpdaterJarEntry(), info.getUpdaterJarFile(), info.getUpdaterTargetCluster());
                        boolean hasAnotherEntryInSameJarFile = false;
                        for(int j = i + 1; j < updaterFiles.size(); j++) {
                            if(updaterFiles.get(j).getUpdaterJarFile() == info.getUpdaterJarFile()) {
                                hasAnotherEntryInSameJarFile = true;
                                break;
                            }
                        }
                        if (!hasAnotherEntryInSameJarFile) {
                            try {
                                info.getUpdaterJarFile().close();
                            } catch (IOException e) {
                                LOG.log(Level.INFO, "Cannot close jar file " + info.getUpdaterJarFile());
                            }
                        }
                    }

                    if (! needsRestart) {
                        synchronized(this) {
                            if (currentStep == STEP.CANCEL) {
                                if (progress != null) progress.finish ();
                                return false;
                            }
                        }

                        if (progress != null) progress.switchToDeterminate (affectedModuleImpls.size ());

                        Set <File> files = null;
                        synchronized(downloadedFiles) {
                            files = new HashSet <File> (downloadedFiles);
                        }
                        if (! files.isEmpty ()) {
                            try {
                                UpdaterInternal.update(
                                    files,
                                    new RefreshModulesListener (progress),
                                    NbBundle.getBranding()
                                );
                                for (ModuleUpdateElementImpl impl : affectedModuleImpls) {
                                    int rerunWaitCount = 0;
                                    Module module = Utilities.toModule (impl.getCodeName(), impl.getSpecificationVersion ());
                                    for (; rerunWaitCount < 100 && module == null; rerunWaitCount++) {
                                        LOG.log(Level.FINE, "Waiting for {0}@{1} #{2}", new Object[]{ impl.getCodeName(), impl.getSpecificationVersion(), rerunWaitCount});
                                        Thread.sleep(100);
                                        Main.getModuleSystem().refresh();
                                        module = Utilities.toModule (impl.getCodeName(), impl.getSpecificationVersion ());
                                    }
                                    if (rerunWaitCount == 100) {
                                        LOG.log (Level.INFO, "Timeout waiting for loading module {0}@{1}", new Object[]{impl.getCodeName (), impl.getSpecificationVersion ()});
                                        afterInstall ();
                                        synchronized(downloadedFiles) {
                                            downloadedFiles.clear();
                                        }
                                        throw new OperationException (OperationException.ERROR_TYPE.INSTALL,
                                                NbBundle.getMessage(InstallSupportImpl.class, "InstallSupportImpl_TurnOnTimeout", // NOI18N
                                                impl.getUpdateElement ()));
                                    }
                                }
                            } catch(InterruptedException ie) {
                                LOG.log (Level.INFO, ie.getMessage (), ie);
                            }
                        }
                        afterInstall ();
                        synchronized(downloadedFiles) {
                            downloadedFiles.clear();
                        }
                    }
                } finally {
                    // end progress
                    if (progress != null) {
                        progress.progress("");
                        progress.finish();
                    }
                }
                
                return needsRestart ? Boolean.TRUE : Boolean.FALSE;
            }
        };
        
        boolean retval =  false;
        try {
            runningTask = getExecutionService ().submit (installCallable);
            retval = runningTask.get ();
        } catch (CancellationException ex) {
            LOG.log (Level.FINE, "InstallSupport.doInstall was cancelled", ex); // NOI18N
            return false;
        } catch(InterruptedException iex) {
            LOG.log (Level.INFO, iex.getLocalizedMessage (), iex);
        } catch(ExecutionException iex) {
            if (iex.getCause () instanceof OperationException) {
                throw (OperationException) iex.getCause ();
            } else {
                LOG.log (Level.INFO, iex.getLocalizedMessage (), iex);
            }
        } finally {
            if (! retval) {
                getElement2Clusters ().clear ();
            }
        }
        return retval;
    }
    
    private void afterInstall () {
        
        if (affectedModuleImpls != null) {
            for (ModuleUpdateElementImpl impl : affectedModuleImpls) {
                UpdateUnit u = impl.getUpdateUnit ();
                UpdateElement el = impl.getUpdateElement ();
                Trampoline.API.impl(u).updateInstalled(el);
            }
            affectedModuleImpls = null;
        }
        
        if (affectedFeatureImpls != null) {
            for (FeatureUpdateElementImpl impl : affectedFeatureImpls) {
                UpdateUnit u = impl.getUpdateUnit ();
                UpdateElement el = impl.getUpdateElement ();
                Trampoline.API.impl(u).updateInstalled(el);
            }
            affectedFeatureImpls = null;
        }
        
    }

    public void doRestart (Restarter restarter, ProgressHandle progress/*or null*/) throws OperationException {
        synchronized(this) {
            assert currentStep != STEP.FINISHED;
            currentStep = STEP.RESTART;
        }        
        Utilities.deleteAllDoLater ();
        getElement2Clusters ().clear ();
        
        LifecycleManager.getDefault ().exit ();
        
        // if exit&restart fails => use restart later as fallback
        doRestartLater (restarter);
    }
    
    public void doRestartLater(Restarter restarter) {
        // schedule module for install later
        if (affectedModuleImpls != null) {
            for (ModuleUpdateElementImpl impl : affectedModuleImpls) {
                UpdateUnitFactory.getDefault ().scheduleForRestart (impl.getUpdateElement ());
            }
        }

        Utilities.writeInstallLater(new HashMap<UpdateElementImpl, File>(getElement2Clusters ()));
        getElement2Clusters ().clear ();
        synchronized (downloadedFiles) {
            downloadedFiles.clear();
        }
    }
    public String getCertificate(Installer validator, UpdateElement uElement) {
        Collection<Certificate> certificates = certs.get (uElement);
        if (certificates != null) {
            String res = "";
            for (Certificate c :certificates) {
                res += c;
            }
            return res;
        } else {
            return null;
        }
    }

    public boolean isTrusted(Installer validator, UpdateElement uElement) {
        UpdateElementImpl impl = Trampoline.API.impl (uElement);
        boolean res = false;
        switch (impl.getType ()) {
        case KIT_MODULE :
        case MODULE :
            res = trusted.contains (impl);
            break;
        case STANDALONE_MODULE :
        case FEATURE :
            FeatureUpdateElementImpl toUpdateFeatureImpl = (FeatureUpdateElementImpl) impl;
            Set<ModuleUpdateElementImpl> moduleImpls = toUpdateFeatureImpl.getContainedModuleElements ();
            res = ! moduleImpls.isEmpty ();
            for (ModuleUpdateElementImpl moduleImpl : moduleImpls) {
                // skip installed element
                if (Utilities.isElementInstalled (moduleImpl.getUpdateElement ())) {
                    continue;
                }
                
                res &= trusted.contains (moduleImpl);
            }
            break;
        default:
            // XXX: what other types
            assert false : "Unsupported type " + impl;
        }
        return res;
    }

    public boolean isSigned(Installer validator, UpdateElement uElement) {
        UpdateElementImpl impl = Trampoline.API.impl (uElement);
        boolean res = false;
        switch (impl.getType ()) {
        case KIT_MODULE :
        case MODULE :
            res = signed.contains (impl);
            break;
        case STANDALONE_MODULE :
        case FEATURE :
            FeatureUpdateElementImpl toUpdateFeatureImpl = (FeatureUpdateElementImpl) impl;
            Set<ModuleUpdateElementImpl> moduleImpls = toUpdateFeatureImpl.getContainedModuleElements ();
            res = ! moduleImpls.isEmpty ();
            for (ModuleUpdateElementImpl moduleImpl : moduleImpls) {
                // skip installed element
                if (Utilities.isElementInstalled (moduleImpl.getUpdateElement ())) {
                    continue;
                }
                
                res &= signed.contains (moduleImpl);
            }
            break;
        default:
            // XXX: what other types
            assert false : "Unsupported type " + impl;
        }
        return res;
    }
    
    private void addTrustedCertificates () {
        // find untrusted so far
        Collection<UpdateElementImpl> untrusted = new HashSet<UpdateElementImpl> (signed);
        untrusted.removeAll (trusted);
        if (untrusted.isEmpty ()) {
            // all are trusted
            return;
        }
        
        // find corresponding certificates
        Collection<Certificate> untrustedCertificates = new HashSet<Certificate> ();
        for (UpdateElementImpl i : untrusted) {
            untrustedCertificates.addAll (certs.get (i.getUpdateElement ()));
        }
        
        if (! untrustedCertificates.isEmpty ()) {
            Utilities.addCertificates (untrustedCertificates);
        }
    }

    public void doCancel () throws OperationException {
        synchronized(this) {
            currentStep = STEP.CANCEL;
        }
        if (runningTask != null && ! runningTask.isDone () && ! runningTask.isCancelled ()) {
            boolean cancelled = runningTask.cancel (true);
            assert cancelled : runningTask + " was cancelled.";
        }
        synchronized(downloadedFiles) {
            for (File f : downloadedFiles) {
                if (f != null && f.exists ()) {
                    f.delete ();
                }
            }
            downloadedFiles.clear ();
        }
        Utilities.cleanUpdateOfUpdaterJar ();
        if (affectedFeatureImpls != null) affectedFeatureImpls = null;
        if (affectedModuleImpls != null) affectedModuleImpls = null;
        
        // also mapping elements to cluster clear because global vs. local may be changed
        getElement2Clusters ().clear ();
    }
    
    private int doDownload (OperationInfo info, ProgressHandle progress, final int aggregateDownload, final int totalSize) throws OperationException {
        UpdateElement toUpdateElement = info.getUpdateElement();
        UpdateElementImpl toUpdateImpl = Trampoline.API.impl (toUpdateElement);
        int res = 0;
        switch (toUpdateImpl.getType ()) {
        case KIT_MODULE :
        case MODULE :
            res += doDownload (toUpdateImpl, progress, aggregateDownload, totalSize);
            break;
        case STANDALONE_MODULE :
        case FEATURE :
            FeatureUpdateElementImpl toUpdateFeatureImpl = (FeatureUpdateElementImpl) toUpdateImpl;
            Set<ModuleUpdateElementImpl> moduleImpls = toUpdateFeatureImpl.getContainedModuleElements ();
            int nestedAggregateDownload = aggregateDownload;
            for (ModuleUpdateElementImpl moduleImpl : moduleImpls) {
                // skip installed element
                if (Utilities.isElementInstalled (moduleImpl.getUpdateElement ())) {
                    continue;
                }
                
                int increment = doDownload (moduleImpl, progress, nestedAggregateDownload, totalSize);
                if (increment == -1) {
                    return -1;
                }
                nestedAggregateDownload += increment;
                res += increment;
            }
            break;
        default:
            // XXX: what other types
            assert false : "Unsupported type " + toUpdateImpl;
        }
        return res;
    }
    
    private int doDownload (UpdateElementImpl toUpdateImpl, ProgressHandle progress, final int aggregateDownload, final int totalSize) throws OperationException {
        if (cancelled()) {
                return -1;
        }
        
        UpdateElement installed = toUpdateImpl.getUpdateUnit ().getInstalled ();
        
        // find target dir
        File targetCluster = getTargetCluster (installed, toUpdateImpl, isGlobal);
        assert targetCluster != null : "Target cluster for " + toUpdateImpl + " must exist.";
        if (targetCluster == null) {
            targetCluster = InstallManager.getUserDir ();
        }

        URL source = toUpdateImpl.getInstallInfo().getDistribution();
        LOG.log (Level.FINE, "Source URL for " + toUpdateImpl.getCodeName () + " is " + source);
        if(source==null) {
            final String errorString = NbBundle.getMessage(InstallSupportImpl.class, 
                    "InstallSupportImpl_NullSource", toUpdateImpl.getCodeName()); // NOI18N
            LOG.log (Level.INFO, errorString);
            throw new OperationException (OperationException.ERROR_TYPE.INSTALL, errorString);
        }

        File dest = getDestination (targetCluster, toUpdateImpl.getCodeName(), source);
        
        // skip already downloaded modules
        if (dest.exists ()) {
            LOG.log (Level.FINE, "Target NBM file " + dest + " of " + toUpdateImpl.getUpdateElement () + " already downloaded.");
            return toUpdateImpl.getDownloadSize ();
        }

        int c = 0;
        
        // download
        try {
            String label = toUpdateImpl.getDisplayName ();
            File normalized = FileUtil.normalizeFile (dest);
            synchronized(downloadedFiles) {
                downloadedFiles.add(normalized);
            }
            c = copy (source, dest, progress, toUpdateImpl.getDownloadSize (), aggregateDownload, totalSize, label);
        } catch (UnknownHostException x) {
            LOG.log (Level.INFO, x.getMessage (), x);
            throw new OperationException (OperationException.ERROR_TYPE.PROXY, source.toString ());
        } catch (FileNotFoundException x) {
            LOG.log (Level.INFO, x.getMessage (), x);
            throw new OperationException (OperationException.ERROR_TYPE.INSTALL, x.getLocalizedMessage ());
        } catch (IOException x) {
            LOG.log (Level.INFO, x.getMessage (), x);
            throw new OperationException (OperationException.ERROR_TYPE.PROXY, source.toString ());
        }
        
        return c;
    }

    private int doValidate (OperationInfo info, ProgressHandle progress, final int verified) throws OperationException {
        UpdateElement toUpdateElement = info.getUpdateElement();
        UpdateElementImpl toUpdateImpl = Trampoline.API.impl (toUpdateElement);
        int increment = 0;
        switch (toUpdateImpl.getType ()) {
        case KIT_MODULE :
        case MODULE :
            increment = doValidate (toUpdateImpl, progress, verified);
            break;
        case STANDALONE_MODULE :
        case FEATURE :
            FeatureUpdateElementImpl toUpdateFeatureImpl = (FeatureUpdateElementImpl) toUpdateImpl;
            Set<ModuleUpdateElementImpl> moduleImpls = toUpdateFeatureImpl.getContainedModuleElements ();
            int nestedVerified = verified;
            for (ModuleUpdateElementImpl moduleImpl : moduleImpls) {
                // skip installed element
                if (Utilities.isElementInstalled (moduleImpl.getUpdateElement ())) {
                    continue;
                }
                int singleIncrement = doValidate (moduleImpl, progress, nestedVerified);
                nestedVerified += singleIncrement;
                increment += singleIncrement;
            }
            break;
        default:
            // XXX: what other types
            assert false : "Unsupported type " + toUpdateImpl;
        }
        return increment;
    }

    private int doValidate (UpdateElementImpl toUpdateImpl, ProgressHandle progress, final int verified) throws OperationException {
        UpdateElement installed = toUpdateImpl.getUpdateUnit ().getInstalled ();
        
        // find target dir
        File targetCluster = getTargetCluster (installed, toUpdateImpl, isGlobal);

        URL source = toUpdateImpl.getInstallInfo().getDistribution();
        File dest = getDestination (targetCluster, toUpdateImpl.getCodeName(), source);
        assert dest.exists () : dest.getAbsolutePath();        
        
        int wasVerified = 0;

        // verify
        wasVerified = verifyNbm (toUpdateImpl.getUpdateElement (), dest, progress, verified);
        
        return wasVerified;
    }
    
    static File getDestination (File targetCluster, String codeName, URL source) {
        LOG.log (Level.FINE, "Target cluster for " + codeName + " is " + targetCluster);
        File destDir = new File (targetCluster, Utilities.DOWNLOAD_DIR);
        if (! destDir.exists ()) {
            destDir.mkdirs ();
        }
        String fileName = codeName.replace ('.', '-');
        String filePath = source.getFile().toLowerCase(Locale.US);
        String ext = filePath.endsWith(Utilities.NBM_EXTENTSION.toLowerCase(Locale.US)) ?
            Utilities.NBM_EXTENTSION : (
            filePath.endsWith(Utilities.JAR_EXTENSION.toLowerCase(Locale.US)) ?
                Utilities.JAR_EXTENSION : ""
            );

        File destFile = new File (destDir, fileName + ext);
        LOG.log(Level.FINE, "Destination file for " + codeName + " is " + destFile);
        return destFile;
    }
    
    private boolean cancelled() {
        synchronized (this) {
            return STEP.CANCEL == currentStep;
        }
    }

    private class OpenConnectionListener implements NetworkAccess.NetworkListener {
        private InputStream stream = null;
        int contentLength = -1;
        private URL source = null;
        private Exception ex = null;
        public OpenConnectionListener (URL source) {
            this.source = source;
        }
        public InputStream getInputStream() {
            return stream;
        }
        public int getContentLength() {
            return contentLength;
        }
        public void streamOpened(InputStream stream, int contentLength) {
            LOG.log(Level.FINEST, "Opened connection for " + source);
            this.stream = stream;
            this.contentLength = contentLength;
        }

        public void accessCanceled() {
            LOG.log(Level.INFO, "Opening connection for " + source + "was cancelled");
        }

        public void accessTimeOut() {
            LOG.log(Level.INFO, "Opening connection for " + source + "was finised due to timeout");
        }

        public void notifyException(Exception x) {
            ex = x;
        }
        public Exception getException() {
            return ex;
        }

    }
    private int copy (URL source, File dest, 
            ProgressHandle progress, final int estimatedSize, final int aggregateDownload, final int totalSize,
            String label) throws MalformedURLException, IOException {
        
        OpenConnectionListener listener = new OpenConnectionListener(source);
        final Task task = NetworkAccess.createNetworkAcessTask(source,
                AutoupdateSettings.getOpenConnectionTimeout(),
                listener);
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (task.isFinished()) {
                        break;
                    } else if(cancelled()) {
                        task.cancel();
                        break;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        //ignore
                    }                    
                }
            }
        }).start();
        
        task.waitFinished ();

        try {
            if(listener.getException()!=null) {
                throw listener.getException();
            }
        } catch (FileNotFoundException x) {
            LOG.log (Level.INFO, x.getMessage(), x);
            throw new IOException(NbBundle.getMessage(InstallSupportImpl.class,
                    "InstallSupportImpl_Download_Unavailable", source));            
        } catch (IOException x) {
            LOG.log (Level.INFO, x.getMessage(), x);
            throw new IOException(NbBundle.getMessage(InstallSupportImpl.class,
                    "InstallSupportImpl_Download_Unavailable", source));            
        } catch (Exception x) {
            LOG.log (Level.INFO, x.getMessage(), x);
            throw new IOException(NbBundle.getMessage(InstallSupportImpl.class,
                    "InstallSupportImpl_Download_Unavailable", source));
        }
        if (cancelled()) {
            LOG.log(Level.FINE, "Download of " + source + " was cancelled");
            throw new IOException("Download of " + source + " was cancelled");
        }

        InputStream is = listener.getInputStream();
        int contentLength = listener.getContentLength();

        BufferedInputStream bsrc = new BufferedInputStream (is);
        BufferedOutputStream bdest = null;
        
        LOG.log (Level.FINEST, "Copy " + source + " to " + dest + "[" + estimatedSize + "]");
        boolean canceled = false;
        int increment = 0;
        try {
            byte [] bytes = new byte [1024];
            int size;
            int c = 0;
            while (!(canceled = cancelled()) && (size = bsrc.read (bytes)) != -1) {
                if(bdest == null) {
                    bdest = new BufferedOutputStream (new FileOutputStream (dest));
                }
                bdest.write (bytes, 0, size);
                increment += size;
                c += size;
                if (! progressRunning && progress != null) {
                    progress.switchToDeterminate (totalSize);
                    progressRunning = true;
                }
                if (c > 1024) {
                    if (progress != null) {
                        assert progressRunning;
                        progress.switchToDeterminate (totalSize);
                        int i = aggregateDownload + (increment < estimatedSize ? increment : estimatedSize);
                        progress.progress (label, i < totalSize ? i : totalSize);
                    }
                    c = 0;
                }
            }
            //assert estimatedSize == increment : "Increment (" + increment
            //        + ") of is equal to estimatedSize (" + estimatedSize + ").";
            if (estimatedSize != increment) {
                LOG.log (Level.FINEST, "Increment (" + increment + ") of is not equal to estimatedSize (" + estimatedSize + ").");
            }            
        } catch (IOException ioe) {
            LOG.log (Level.INFO, "Writing content of URL " + source + " failed.", ioe);
        } finally {
            try {
                if (bsrc != null) bsrc.close ();
                if (bdest != null) bdest.flush ();
                if (bdest != null) bdest.close ();
            } catch (IOException ioe) {
                LOG.log (Level.INFO, ioe.getMessage (), ioe);
            }
        }
        if (contentLength != -1 && increment != contentLength) {
            if(canceled) {
                LOG.log(Level.FINE, "Download of " + source + " was cancelled");
            } else {
                LOG.log(Level.INFO, "Content length was reported as " + contentLength + " byte(s) but read " + increment + " byte(s)");
            }
            if(bdest!=null && dest.exists()) {
                LOG.log(Level.INFO, "Deleting not fully downloaded file " + dest);
                dest.delete();
                File normalized = FileUtil.normalizeFile (dest);
                synchronized(downloadedFiles) {
                    downloadedFiles.remove(normalized);
                }
            }
            if(canceled) {
                throw new IOException("Download of " + source + " was cancelled");
            } else {
                throw new IOException("Server closed connection unexpectedly");
            }
        }

        LOG.log (Level.FINE, "Destination " + dest + " is successfully wrote. Size " + dest.length());
        
        return estimatedSize;
    }
    
    private int verifyNbm (UpdateElement el, File nbmFile, ProgressHandle progress, int verified) throws OperationException {
        String res = null;
        try {
            verified += el.getDownloadSize ();
            if (progress != null) {
                progress.progress (el.getDisplayName (), verified < wasDownloaded ? verified : wasDownloaded);
            }
            Collection<Certificate> nbmCerts = getNbmCertificates (nbmFile);
            assert nbmCerts != null;
            if (nbmCerts.size () > 0) {
                certs.put (el, nbmCerts);
            }
            if (nbmCerts.isEmpty()) {
                res = "UNSIGNED";
            } else {
                List<Certificate> trustedCerts = new ArrayList<Certificate> ();
                UpdateElementImpl impl = Trampoline.API.impl(el);
                for (KeyStore ks : Utilities.getKeyStore ()) {
                    trustedCerts.addAll (getCertificates (ks));
                }
                // load user certificates
                KeyStore ks = Utilities.loadKeyStore ();
                if (ks != null) {
                    trustedCerts.addAll (getCertificates (ks));
                }
                if (trustedCerts.containsAll (nbmCerts)) {
                    res = "TRUSTED";
                    trusted.add (impl);
                    signed.add (impl);
                } else {
                    res = "UNTRUSTED";
                    signed.add (impl);
                }
            }
        } catch (IOException ioe) {
            LOG.log (Level.INFO, ioe.getMessage (), ioe);
            res = "BAD_DOWNLOAD";
            throw new OperationException (OperationException.ERROR_TYPE.INSTALL,
                    NbBundle.getMessage(InstallSupportImpl.class, "InstallSupportImpl_Validate_CorruptedNBM", nbmFile)); // NOI18N
        } catch (KeyStoreException kse) {
            LOG.log (Level.INFO, kse.getMessage (), kse);
            res = "CORRUPTED";
            throw new OperationException (OperationException.ERROR_TYPE.INSTALL,
                    NbBundle.getMessage(InstallSupportImpl.class, "InstallSupportImpl_Validate_CorruptedNBM", nbmFile)); // NOI18N
        }
        
        LOG.log (Level.FINE, "NBM " + nbmFile + " was verified as " + res);
        return el.getDownloadSize ();
    }
    
    private static Collection<Certificate> getCertificates (KeyStore keyStore) throws KeyStoreException {
        List<Certificate> certs = new ArrayList<Certificate> ();
        for (String alias: Collections.list (keyStore.aliases ())) {
            certs.add (keyStore.getCertificate (alias));
        }
        return certs;
    }
    
    private static Collection<Certificate> getNbmCertificates (File nbmFile) throws IOException {
        Set<Certificate> certs = new HashSet<Certificate>();
        JarFile jf = new JarFile(nbmFile);
        try {
            for (JarEntry entry : Collections.list(jf.entries())) {
                verifyEntry(jf, entry);
                if (entry.getCertificates() != null) {
                    certs.addAll(Arrays.asList(entry.getCertificates()));
                }
            }
        } finally {
            jf.close();
        }

        return certs;
    }
    
    /**
     * @throws SecurityException
     */
    private static void verifyEntry (JarFile jf, JarEntry je) throws IOException {
        InputStream is = null;
        try {
            is = jf.getInputStream (je);
            byte[] buffer = new byte[8192];
            int n;
            int c = 0;
            while ((n = is.read (buffer, 0, buffer.length)) != -1);
        } finally {
            if (is != null) is.close ();
        }
        
        return;
    }
    
    private boolean needsRestart (boolean isUpdate, UpdateElementImpl toUpdateImpl, File dest) {
        return InstallManager.needsRestart (isUpdate, toUpdateImpl, dest);
    }
    
    private static final class RefreshModulesListener implements PropertyChangeListener  {
        private ProgressHandle handle;
        private int i;
        
        public RefreshModulesListener (ProgressHandle handle) {
            this.handle = handle;
            this.i = 0;
        }
        
        @Override
        public void propertyChange(PropertyChangeEvent ev) {
            if (UpdaterInternal.RUNNING.equals (ev.getPropertyName ())) {
                if (handle != null) {
                    handle.progress (i++);
                }
            } else if (UpdaterInternal.FINISHED.equals (ev.getPropertyName ())){
                // XXX: the modules list should be refresh automatically when config/Modules/ changes
                Map<File,Long> modifiedFiles = NbCollections.checkedMapByFilter(
                    (Map)ev.getNewValue(), 
                    File.class, Long.class, true
                );
                long now = System.currentTimeMillis();
                for (Map.Entry<File,Long> e : modifiedFiles.entrySet()) {
                    touch(e.getKey(), Math.max(e.getValue(), now));
                }
                FileUtil.getConfigRoot().refresh();
                FileObject modulesRoot = FileUtil.getConfigFile(ModuleDeactivator.MODULES);
                if (modulesRoot != null) {
                    modulesRoot.refresh();
                }
            } else {
                assert false : "Unknown property " + ev.getPropertyName ();
            }
        }
    }
    
    private static void touch(File f, long minTime) {
        for (int cnt = 0; ;cnt++) {
            long time = f.lastModified();
            if (time > minTime) {
                break;
            }
            if (!f.exists()) {
                LOG.log(Level.FINE, "File {0} does not exist anymore", f);
                break;
            }
            LOG.log(Level.FINE, "Need to change time for {0} with delta {1}", new Object[]{f, minTime - f.lastModified()});
            try { synchronized (InstallSupportImpl.class) {
                InstallSupportImpl.class.wait(30);
            }} catch (InterruptedException ex) {}
            f.setLastModified(System.currentTimeMillis() - 1000);
        }
        LOG.log(Level.FINE, "Time stamp changed succcessfully {0}", f);
    }

    private File getTargetCluster(UpdateElement installed, UpdateElementImpl update, boolean isGlobal) {
        File cluster = getElement2Clusters ().get (update);
        if (cluster == null) {
            cluster = InstallManager.findTargetDirectory (installed, update, isGlobal);
            if (cluster != null) {
                getElement2Clusters ().put(update, cluster);
            }
        }
        return cluster;
    }
    
    private  Map<UpdateElementImpl, File> getElement2Clusters () {
        if (element2Clusters == null) {
            element2Clusters = new HashMap<UpdateElementImpl, File> ();
        }
        return element2Clusters;
    }
    
    private ExecutorService getExecutionService () {
        if (es == null || es.isShutdown ()) {
            es = Executors.newSingleThreadExecutor ();
        }
        return es;
    }
        private static class UpdaterInfo {
        private JarEntry updaterJarEntry;
        private JarFile updaterJarFile;
        private File updaterTargetCluster;

        public UpdaterInfo(JarEntry updaterJarEntry, JarFile updaterJarFile, File updaterTargetCluster) {
            this.updaterJarEntry = updaterJarEntry;
            this.updaterJarFile = updaterJarFile;
            this.updaterTargetCluster = updaterTargetCluster;
        }

        public JarEntry getUpdaterJarEntry() {
            return updaterJarEntry;
        }

        public void setUpdaterJarEntry(JarEntry updaterJarEntry) {
            this.updaterJarEntry = updaterJarEntry;
        }

        public JarFile getUpdaterJarFile() {
            return updaterJarFile;
        }

        public void setUpdaterJarFile(JarFile updaterJarFile) {
            this.updaterJarFile = updaterJarFile;
        }

        public File getUpdaterTargetCluster() {
            return updaterTargetCluster;
        }

        public void setUpdaterTargetCluster(File updaterTargetCluster) {
            this.updaterTargetCluster = updaterTargetCluster;
        }
    }
}
