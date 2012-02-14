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
package org.netbeans.api.search.provider;

import org.netbeans.api.search.SearchInfoDefinitionFactory;
import org.netbeans.api.search.SearchScopeOptions;
import org.netbeans.api.search.provider.impl.CompoundSearchInfo;
import org.netbeans.api.search.provider.impl.DelegatingSearchInfo;
import org.netbeans.api.search.provider.impl.EmptySearchInfo;
import org.netbeans.spi.search.SearchFilterDefinition;
import org.netbeans.spi.search.SearchInfoDefinition;
import org.netbeans.spi.search.impl.SearchInfoDefinitionUtils;
import org.netbeans.spi.search.provider.TerminationFlag;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

/**
 * Method for getting SearchInfo for nodes.
 *
 * @author jhavlin
 */
public final class SearchInfoUtils {

    /**
     * Gets a search info for node.
     *
     * @return Search info for a node. If no search info was defined and it
     * cannot be created for this node, null is returned.
     */
    public static SearchInfo getSearchInfoForNode(Node node) {
        SearchInfoDefinition sid =
                SearchInfoDefinitionUtils.getSearchInfoDefinition(node);
        if (sid == null) {
            return null;
        } else {
            return new DelegatingSearchInfo(sid);
        }
    }

    /**
     * Get a search info for a node, if it was explicitly defined in node's
     * lookup.
     *
     * @return Defined SearchInfo, or null if not available.
     */
    public static SearchInfo findDefinedSearchInfo(Node node) {
        SearchInfoDefinition sid =
                SearchInfoDefinitionUtils.findSearchInfoDefinition(node);
        if (sid != null) {
            return new DelegatingSearchInfo(sid);
        } else {
            return null;
        }
    }

    /**
     * Create a new SearchInfo for a SearchInfoDefinition instance.
     */
    public static SearchInfo createForDefinition(
            SearchInfoDefinition definition) {
        if (definition == null) {
            throw new NullPointerException("No definition passed.");    //NOI18N
        }
        return new DelegatingSearchInfo(definition);
    }

    /**
     * Creates a
     * <code>SearchInfo</code> compound of the given delegates. It combines the
     * delegates such that: <ul> <li>its method {@link SearchInfo#canSearch()}
     * returns
     * <code>true</code> if and only if at least one of the delegate's
     * <code>canSearch()</code> returns
     * <code>true</code></li> <li>its method
     * {@link SearchInfo#getFilesToSearch(SearchScopeOptions, SearchListener, TerminationFlag)}
     * chains iterators of the delegates, skipping those delegates whose
     * <code>canSearch()</code> method returns
     * <code>false</code></li> </ul>
     *
     * @param delegates delegates the compound
     * <code>SearchInfo</code> should delegate to
     * @return created compound
     * <code>SearchInfo</code>
     * @exception java.lang.IllegalArgumentException if the argument is
     * <code>null</code>
     * @since 3.13
     */
    public static SearchInfo createCompoundSearchInfo(
            SearchInfo... delegates) {
        if (delegates == null) {
            throw new IllegalArgumentException("null");                 //NOI18N
        }
        return new CompoundSearchInfo(delegates);
    }

    /**
     * Create a SearchInstance that is always unsearchable.
     */
    public static SearchInfo createEmptySearchInfo() {
        return new EmptySearchInfo();
    }

    /**
     * Create a search info for a FileObject and a set of filters.
     */
    public static SearchInfo createSearchInfoForRoot(FileObject root,
            SearchFilterDefinition... filters) {

        if (root == null) {
            throw new NullPointerException("Root cannot be null.");     //NOI18N
        }

        return new DelegatingSearchInfo(
                SearchInfoDefinitionFactory.createSearchInfo(root, filters));
    }

    /**
     * Create a search info for an array of FileObjects and a set of filters.
     */
    public static SearchInfo createSearchInfoForRoots(FileObject[] folders,
            SearchFilterDefinition... filters) {

        if (folders == null) {
            throw new NullPointerException("Folders cannot be null.");  //NOI18N
        }

        return new DelegatingSearchInfo(
                SearchInfoDefinitionFactory.createSearchInfo(folders, filters));
    }
}
