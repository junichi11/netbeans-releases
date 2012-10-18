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
package org.netbeans.modules.web.clientproject.sites;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.Places;
import org.openide.util.NbBundle;

public final class SiteHelper {

    private static final Logger LOGGER = Logger.getLogger(SiteHelper.class.getName());
    private static final String JS_LIBS_DIR = "jslibs"; // NOI18N


    private SiteHelper() {
    }

    /**
     * Return <i>&lt;var/cache>/jslibs</i> directory.
     * @return <i>&lt;var/cache>/jslibs</i> directory
     */
    public static File getJsLibsDirectory() {
        return Places.getCacheSubdirectory(JS_LIBS_DIR);
    }

    /**
     * Download the given URL to the target file.
     * @param url URL to be downloaded
     * @param target target file
     * @param progressHandle progress handle, can be {@code null}
     * @throws IOException if any error occurs
     */
    @NbBundle.Messages({
        "# {0} - file name",
        "SiteHelper.progress.download=Downloading file {0}"
    })
    public static void download(String url, File target, @NullAllowed ProgressHandle progressHandle) throws IOException {
        assert !EventQueue.isDispatchThread();
        if (progressHandle != null) {
            progressHandle.progress(Bundle.SiteHelper_progress_download(target.getName()));
        }
        try {
            InputStream is = new URL(url).openStream();
            try {
                copyToFile(is, target);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            // error => ensure file is deleted
            target.delete();
            throw ex;
        }
    }

    /**
     * Unzip the given ZIP file to the given target directory. The target directory must exist.
     * @param zipFile ZIP file to be extracted
     * @param targetDirectory existing target directory
     * @param progressHandle progress handle, can be {@code null}
     * @throws IOException if any error occurs
     */
    @NbBundle.Messages({
        "# {0} - file name",
        "SiteHelper.progress.unzip=Unziping file {0}"
    })
    public static void unzipProjectTemplate(@NonNull FileObject targetDir, @NonNull File zipFile, @NullAllowed ProgressHandle progressHandle, String... ignoredFiles) throws IOException {
        assert targetDir != null;
        if (progressHandle != null) {
            progressHandle.progress(Bundle.SiteHelper_progress_unzip(zipFile.getName()));
        }
        String rootFolder = getZipRootFolder(new FileInputStream(zipFile));
        unzipProjectTemplateFile(targetDir, new FileInputStream(zipFile), rootFolder, ignoredFiles);
    }

    public static void runOnZipEntries(@NonNull File zipFile, @NonNull ZipEntryTask entryTask, @NullAllowed ZipEntryFilter entryFilter) throws IOException {
        ZipFile zip = new ZipFile(zipFile);
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                boolean accept = true;
                if (entryFilter != null) {
                    accept = entryFilter.accept(zipEntry);
                }
                if (accept) {
                    InputStream inputStream = zip.getInputStream(zipEntry);
                    try {
                        entryTask.run(inputStream);
                    } finally {
                        inputStream.close();
                    }
                }
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Get list of files from the given ZIP file according to the given {@link ZipEntryFilter filter}.
     * @param zipFile ZIP file to be listed
     * @param entryFilter filter to be applied on the ZIP file entries
     * @return list of files from the given ZIP file according to the given {@link ZipEntryFilter filter}
     * @throws IOException if any error occurs
     */
    public static List<String> listZipFiles(File zipFile, ZipEntryFilter entryFilter) throws IOException {
        assert zipFile != null;
        assert entryFilter != null;
        List<String> files = new ArrayList<String>();
        ZipFile zip = new ZipFile(zipFile);
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (entryFilter.accept(zipEntry)) {
                    files.add(zipEntry.getName());
                }
            }
        } finally {
            zip.close();
        }
        return files;
    }

    /**
     * Get list of JS file names (just filenames, without any relative path) from the given ZIP file.
     * <p>
     * If any error occurs, this error is logged with INFO level and an empty list is returned.
     * @param zipFile ZIP file to be listed
     * @return list of JS file names (just filenames, without any relative path) from the given ZIP file
     * @see #listZipFiles(File, ZipEntryFilter)
     */
    public static List<String> listJsFilenamesFromZipFile(File zipFile) {
        try {
            List<String> entries = SiteHelper.listZipFiles(zipFile, new SiteHelper.ZipEntryFilter() {
                @Override
                public boolean accept(ZipEntry zipEntry) {
                    return !zipEntry.isDirectory()
                            && zipEntry.getName().toLowerCase().endsWith(".js"); // NOI18N
                }
            });
            List<String> files = new ArrayList<String>(entries.size());
            for (String entry : entries) {
                String[] segments = entry.split("/"); // NOI18N
                files.add(segments[segments.length - 1]);
            }
            return files;
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, null, ex);
        }
        return Collections.emptyList();
    }

    @NbBundle.Messages("SiteHelper.error.emptyZip=ZIP file with site template is either empty or its download failed.")
    private static void unzipProjectTemplateFile(FileObject targetDir, InputStream source, String rootFolder, String... ignoredFiles) throws IOException {
        boolean firstItem = true;
        try {
            int stripLen = rootFolder != null ? rootFolder.length() : 0;
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            Set<String> ignored = Collections.emptySet();
            if (ignoredFiles != null && ignoredFiles.length > 0) {
                ignored = new HashSet<String>(Arrays.asList(ignoredFiles));
            }
            while ((entry = str.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (stripLen > 0) {
                    entryName = entryName.substring(stripLen);
                }
                if (entryName.length() == 0) {
                    continue;
                }
                if (ignored.contains(entryName)) {
                    continue;
                }
                firstItem = false;
                if (entry.isDirectory()) {
                    // ignore build folder from mobile boilerplate; unrelated junk IMO.
                    if (entryName.startsWith("build") || entryName.startsWith("nbproject")) { //NOI18N
                        continue;
                    }
                    if (targetDir.getFileObject(entryName) == null) {
                        FileUtil.createFolder(targetDir, entryName);
                    }
                } else {
                    // ignore internal GIT files:
                    if (entryName.startsWith(".git") || entryName.contains("/.git")) { //NOI18N
                        continue;
                    }
                    // ignore build folder from mobile boilerplate; unrelated junk IMO.
                    if (entryName.startsWith("build/") || entryName.startsWith("nbproject/")) { //NOI18N
                        continue;
                    }
                    FileObject fo = FileUtil.createData(targetDir, entryName);
                    writeFile(str, fo);
                }
            }
        } finally {
            source.close();
            if (firstItem) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.SiteHelper_error_emptyZip()));
            }
        }
    }

    private static void writeFile(ZipInputStream str, FileObject fo) throws IOException {
        OutputStream out = fo.getOutputStream();
        try {
            FileUtil.copy(str, out);
        } finally {
            out.close();
        }
    }

    private static File copyToFile(InputStream is, File target) throws IOException {
        OutputStream os = new FileOutputStream(target);
        try {
            FileUtil.copy(is, os);
        } finally {
            os.close();
        }
        return target;
    }

    private static String getZipRootFolder(InputStream source) throws IOException {
        String folder = null;
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            boolean first = true;
            while ((entry = str.getNextEntry()) != null) {
                if (first) {
                    first = false;
                    if (entry.isDirectory()) {
                        folder = entry.getName();
                    } else {
                        String fileName = entry.getName();
                        int slashIndex = fileName.indexOf('/');
                        if (slashIndex != -1) {
                            String name = fileName.substring(slashIndex+1);
                            folder = fileName.substring(0, slashIndex);
                            if (name.length() == 0 || folder.length() == 0) {
                                return null;
                            }
                            folder += "/"; //NOI18N
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (!entry.getName().startsWith(folder)) {
                        return null;
                    }
                }
            }
        } finally {
            source.close();
        }
        return folder;
    }

    //~ Inner classes

    /**
     * Filter for {@link ZipEntry}s.
     * <p>
     * Instances of this interface may be passed to the {@link SiteHelper#listZipFiles(File, ZipEntryFilter)} method.
     * @see SiteHelper#listZipFiles(File, ZipEntryFilter)
     */
    public interface ZipEntryFilter {

        /**
         * Test whether or not the specified {@link ZipEntry} should be
         * accepted.
         *
         * @param zipEntry the {@link ZipEntry} to be tested
         * @return {@ code true} if {@link ZipEntry} should be accepted, {@code false} otherwise
         */
        boolean accept(ZipEntry zipEntry);
    }

    /**
     * Task for {@link ZipEntry}s, their content.
     * @see SiteHelper#runOnZipEntries(File, ZipEntryTask, ZipEntryFilter)
     */
    public interface ZipEntryTask {

        /**
         * Run task on the given content, typically read it.
         * @param zipEntryInputStream content of the given {@link ZipEntry}
         */
        void run(InputStream zipEntryInputStream);
    }

}
