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

package org.netbeans.modules.cnd.dwarfdump.dwarf;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ak119685
 */
public class DwarfNameLookupTable {
    public long unit_length;
    public int  version;
    public long debug_info_offset;
    public long debug_info_length;

    public HashMap<Long, String> entries = new HashMap<Long, String>();

    public void dump(PrintStream out) {
        out.println("\nPublic names:\n"); // NOI18N
        out.printf("  %-40s %s%n", "Length:", unit_length); // NOI18N
        out.printf("  %-40s %s%n", "Version:", version); // NOI18N
        out.printf("  %-40s %s%n", "Offset into .debug_info section:", debug_info_offset); // NOI18N
        out.printf("  %-40s %s%n", "Size of area in .debug_info section:", debug_info_length); // NOI18N
        out.println("%n    Offset      Name"); // NOI18N


        for (Map.Entry<Long, String> entry : entries.entrySet()) {
            long offset = entry.getKey();
            out.printf("%d (0x%x)\t%s%n", offset, offset, entry.getValue()); // NOI18N
        }
    }

    public void addEntry(long offset, String name) {
        entries.put(offset, name);
    }

    public String getName(long offset) {
        return entries.get(offset);
    }

}
