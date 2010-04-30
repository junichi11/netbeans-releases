/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.source.parsing;

import java.net.URL;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomas Zezula
 */
public final class SiblingSupport implements SiblingSource {

    private static final Logger LOG = Logger.getLogger(SiblingSupport.class.getName());

    private final Stack<URL> siblings = new Stack<URL>();
    private final SiblingProvider provider = new Provider();

    private SiblingSupport() {
    }

    @Override
    public void push(URL sibling) {
        assert sibling != null;
        siblings.push(sibling);
        LOG.log(Level.FINE, "Pushed sibling: {0} size: {1}", new Object[]{sibling, siblings.size()});    //NOI18N
    }

    @Override
    public URL pop() {
        final URL removed = siblings.pop();
        if (LOG.isLoggable(Level.FINEST)) {
            StackTraceElement[] td = Thread.currentThread().getStackTrace();
            LOG.log(Level.FINEST, "Poped sibling: {0} size: {1} caller:\n{2}", new Object[] {removed, siblings.size(), formatCaller(td)});     //NOI18N
        } else {
            LOG.log(Level.FINE, "Poped sibling: {0} size: {1}", new Object[] {removed, siblings.size()});     //NOI18N
        }
        return removed;
    }

    @Override
    public SiblingProvider getProvider() {
        return provider;
    }

    public static SiblingSource create() {
        return new SiblingSupport();
    }

    private final class Provider implements SiblingProvider {
        private Provider() {
        }

        @Override
        public URL getSibling() {
            final URL result = siblings.peek();
            LOG.log(Level.FINER, "Returns sibling: {0}", new Object[] {result});  //NOI18N
            return result;
        }

        @Override
        public boolean hasSibling() {
            boolean result = !siblings.isEmpty();
            LOG.log(Level.FINER, "Has sibling: {0}", new Object[] {result});  //NOI18N
            return result;
        }
    }

    private static String formatCaller (final StackTraceElement[] elements) {
        final StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : elements) {
            sb.append(String.format("%s.%s (%s:%d)\n",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber()));
        }
        return sb.toString();
    }

}
