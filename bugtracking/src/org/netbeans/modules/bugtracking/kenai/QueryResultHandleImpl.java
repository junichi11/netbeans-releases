/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.kenai;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import org.netbeans.modules.bugtracking.spi.Issue;
import org.netbeans.modules.bugtracking.spi.Query;
import org.netbeans.modules.bugtracking.ui.query.QueryAction;
import org.netbeans.modules.kenai.ui.spi.QueryResultHandle;

/**
 *
 * @author Tomas Stupka
 */
public class QueryResultHandleImpl extends QueryResultHandle implements ActionListener {

    private final Query query;
    private final String label;

    public QueryResultHandleImpl(Query query, String label) {
        this.query = query;
        this.label = label;
    }

    @Override
    public String getText() {
        return label;
    }

    public void actionPerformed(ActionEvent e) {
        QueryAction.openQuery(query);
    }

    public static QueryResultHandleImpl forStatus(Query query, int status) {
        Issue[] issues = query.getIssues(status);
        switch(status) {
            case Query.ISSUE_STATUS_NOT_OBSOLETE:
                return new QueryResultHandleImpl(query, MessageFormat.format(getStatusFormat(status), issues != null ? issues.length : 0));
            case Query.ISSUE_STATUS_NEW:
            case Query.ISSUE_STATUS_MODIFIED:
                if(issues == null || issues.length == 0) {
                    return null;
                }
                return new QueryResultHandleImpl(query, MessageFormat.format(getStatusFormat(status), issues.length));
            default:
                throw new IllegalStateException("wrong status value" + status);
        }
    }

    private static String getStatusFormat(int status) {
        switch(status) {
            case Query.ISSUE_STATUS_NOT_OBSOLETE:
                return "{0} total";
            case Query.ISSUE_STATUS_NEW:
                return "{0} new";
            case Query.ISSUE_STATUS_MODIFIED:
                return "{0} changed";
            default:
                throw new IllegalStateException("wrong status value" + status);
        }
    }
}
