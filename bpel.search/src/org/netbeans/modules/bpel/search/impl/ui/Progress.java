/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.bpel.search.impl.ui;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

import org.netbeans.modules.xml.xam.ui.search.api.SearchEvent;
import org.netbeans.modules.xml.xam.ui.search.spi.SearchListener;

import static org.netbeans.modules.print.ui.PrintUI.*;

/**
 * @author Vladimir Yaroslavskiy
 * @version 2007.01.25
 */
final class Progress implements SearchListener {

  public void searchStarted(SearchEvent event) {
    myProgress = ProgressHandleFactory.createHandle(
      i18n(Progress.class, "LBL_Search_Progress")); // NOI18N
    myProgress.start();
  }

  public void searchFinished(SearchEvent event) {
    myProgress.finish();
  }

  public void searchFound(SearchEvent event) {}

  private ProgressHandle myProgress;
}
