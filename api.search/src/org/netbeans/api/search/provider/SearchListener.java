/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.api.search.provider;

import org.netbeans.spi.search.SearchFilterDefinition;
import org.openide.filesystems.FileObject;

/**
 * Listener for various events during searching.
 *
 * @author jhavlin
 */
public abstract class SearchListener {

    /**
     * Called when the search is starting.
     */
    public void searchStarted() {
    }

    /**
     * Called when a file is skipped - filtered out by a filter.
     *
     * @param fileObject the skipped file object.
     * @param filter filter that filtered out the file (can be null).
     * @param message message describing reasons for skipping (can be null).
     */
    public void fileSkipped(FileObject fileObject,
            SearchFilterDefinition filter, String message) {
    }

    /**
     * Called when a directory was visited.
     *
     * @param path Path of the visited directory.
     */
    public void directoryEntered(String path) {
    }

    /**
     * Called when matching in file content is to start.
     *
     * @param fileName Name of file.
     */
    public void fileContentMatchingStarted(String fileName) {
    }

    /**
     * Called when matching in file progresses.
     *
     * If matching in a file reaches some interresting point (e.g. next matched
     * line), the matching algorithm can call this method. The implementators
     * will probably update progress bar or do something similar.
     *
     * @param fileName Name of file whose content is being read.
     * @param fileOffset Offset in file that has been processed.
     */
    public void fileContentMatchingProgress(String fileName,
            long fileOffset) {
    }

    /**
     * Called when an exception occurs during file content checking.
     *
     * @param fileName File that caused the error.
     * @param throwable Error description.
     */
    public void fileContentMatchingError(String fileName,
            Throwable throwable) {
    }

    /**
     * Called when a general error occurs.
     *
     * @param t Error description.
     */
    public void generalError(Throwable t) {
    }
    /**
     * Called when the search is finishing.
     */
    public void searchFinished() {
    }
}
