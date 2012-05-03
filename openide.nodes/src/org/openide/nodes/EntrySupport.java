/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.openide.nodes;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.Children.Entry;
import org.openide.util.Utilities;

/**
 *
 * @author t_h
 */
abstract class EntrySupport {

    /** children we are attached to */
    public final Children children;

    protected List<Entry> entries = Collections.emptyList();

    /** Creates a new instance of EntrySupport */
    protected EntrySupport(Children children) {
        this.children = children;
    }

    //
    // API methods to be called from Children
    //
    public abstract int getNodesCount(boolean optimalResult);

    public abstract Node[] getNodes(boolean optimalResult);

    /** Getter for a node at given position. If node with such index
     * does not exists it should return null.*/
    public abstract Node getNodeAt(int index);

    /**
     * @return currently created nodes or null if no node is created
     */
    public abstract Node[] testNodes();

    public abstract boolean isInitialized();

    abstract void notifySetEntries();

    final void setEntries(Collection<? extends Entry> entries) {
        setEntries(entries, false);
    }
    abstract void setEntries(Collection<? extends Entry> entries, boolean noCheck);

    /** Access to copy of current entries.
     * @return copy of entries in the objects
     */
    protected final List<Entry> getEntries() {
        return new ArrayList<Entry>(this.entries);
    }

    /** Abililty to create a snaphshot
     * @return immutable and unmodifiable list of Nodes that represent the children at current moment
     */
    abstract List<Node> snapshot();

    /** Refreshes content of one entry. Updates the state of children appropriately. */
    abstract void refreshEntry(Entry entry);
}
