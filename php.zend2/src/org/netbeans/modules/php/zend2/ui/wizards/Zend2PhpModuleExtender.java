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
package org.netbeans.modules.php.zend2.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.composer.api.Composer;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.zend2.options.Zend2Options;
import org.netbeans.modules.php.zend2.ui.options.Zend2OptionsPanelController;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Zend 2 framework extender.
 */
public class Zend2PhpModuleExtender extends PhpModuleExtender {

    private static final Logger LOGGER = Logger.getLogger(Zend2PhpModuleExtender.class.getName());

    static final String SKELETON_ZIP_ENTRY_PREFIX = "ZendSkeletonApplication-master/"; // NOI18N

    // @GuardedBy("this")
    private NewProjectConfigurationPanel panel = null;


    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        String error = getPanel().getErrorMessage();
        if (error != null) {
            return error;
        }
        try {
            // validate composer
            Composer.getDefault();
        } catch (InvalidPhpExecutableException ex) {
            return ex.getLocalizedMessage();
        }
        return null;
    }

    @Override
    public String getWarningMessage() {
        return getPanel().getWarningMessage();
    }

    @NbBundle.Messages("Zend2PhpModuleExtender.not.extended=<html>Zend 2 project not created!<br>(verify <i>Zend Application Skeleton</i> in Tools > Options > PHP > Zend 2 or review IDE log)")
    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        try {
            unpackSkeleton(phpModule);
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot unpack Zend Application Skeleton.", ex);
            throw new ExtendingException(Bundle.Zend2PhpModuleExtender_not_extended(), ex);
        }

        // install framework via composer
        try {
            Composer.getDefault().install(phpModule).get();
        } catch (InvalidPhpExecutableException ex) {
            assert false : "Should not happen since Composer is validated in the wizard panel";
            LOGGER.log(Level.INFO, "Composer is not valid so no install cannot be done.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, Zend2OptionsPanelController.OPTIONS_SUBPATH);
        }

        return getInitialFiles(phpModule);
    }

    private void unpackSkeleton(PhpModule phpModule) throws IOException {
        String skeleton = Zend2Options.getInstance().getSkeleton();
        final File sourceDir = FileUtil.toFile(phpModule.getSourceDirectory());
        FileUtils.unzip(skeleton, sourceDir, new FileUtils.ZipEntryFilter() {
            @Override
            public boolean accept(ZipEntry zipEntry) {
                return !SKELETON_ZIP_ENTRY_PREFIX.equals(zipEntry.getName());
            }
            @Override
            public String getName(ZipEntry zipEntry) {
                String entryName = zipEntry.getName();
                if (entryName.startsWith(SKELETON_ZIP_ENTRY_PREFIX)) {
                    entryName = entryName.replaceFirst(SKELETON_ZIP_ENTRY_PREFIX, ""); // NOI18N
                }
                return entryName;
            }
        });
    }

    private Set<FileObject> getInitialFiles(PhpModule phpModule) {
        Set<FileObject> files = new HashSet<>();
        addSourceFile(files, phpModule, "config/application.config.php"); // NOI18N
        addSourceFile(files, phpModule, "module/Application/src/Application/Controller/IndexController.php"); // NOI18N
        addSourceFile(files, phpModule, "module/Application/view/application/index/index.phtml"); // NOI18N
        return files;
    }

    private void addSourceFile(Set<FileObject> files, PhpModule phpModule, String relativePath) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            // broken project
            assert false : "Module extender for no sources of: " + phpModule.getName();
            return;
        }
        FileObject fileObject = sourceDirectory.getFileObject(relativePath);
        if (fileObject != null) {
            files.add(fileObject);
        }
    }

    private synchronized NewProjectConfigurationPanel getPanel() {
        assert Thread.holdsLock(this);
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        return panel;
    }

}
