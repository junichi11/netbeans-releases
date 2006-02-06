/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.subversion.ui.diff;

import org.netbeans.api.diff.StreamSource;
import org.netbeans.api.diff.DiffView;
import org.netbeans.modules.subversion.*;
import org.openide.util.NbBundle;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.MessageFormat;

/**
 * Represents on DIFF setup.
 * 
 * @author Maros Sandor
 */
public final class Setup {
    
    public static final int DIFFTYPE_LOCAL     = 0;
    public static final int DIFFTYPE_REMOTE    = 1;
    public static final int DIFFTYPE_ALL       = 2;
    
    public static final String REVISION_BASE = "BASE";
    public static final String REVISION_CURRENT = "LOCAL";
    public static final String REVISION_HEAD    = "HEAD";
    
    private final File      baseFile;
    private final String    firstRevision;
    private final String    secondRevision;

    private DiffStreamSource    firstSource;
    private DiffStreamSource    secondSource;

    private DiffView view;

    private String    title;

    public Setup(File baseFile, int type) {
        this.baseFile = baseFile;
        FileInformation info = Subversion.getInstance().getStatusCache().getStatus(baseFile);
        int status = info.getStatus();
        String revision = REVISION_BASE;
        
        ResourceBundle loc = NbBundle.getBundle(Setup.class);
        String firstTitle;
        String secondTitle;
        if (type == DIFFTYPE_ALL && status == FileInformation.STATUS_VERSIONED_MERGE) {
            firstRevision = REVISION_HEAD;
            firstTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_RemoteModified"), new Object [] { revision });
        } else if (
                status == FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY ||
                status == FileInformation.STATUS_VERSIONED_ADDEDLOCALLY ||
                status == FileInformation.STATUS_VERSIONED_NEWINREPOSITORY
        ) {
            firstRevision = null;
            firstTitle = loc.getString("MSG_DiffPanel_NoBaseRevision");
        } else {
            firstRevision = revision;
            firstTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_BaseRevision"), new Object [] { revision });
        }

        if (status == FileInformation.STATUS_VERSIONED_CONFLICT) {
            secondRevision = REVISION_CURRENT;
            secondTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_LocalConflict"), new Object [] { revision });
        } else if (status == FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY || status == FileInformation.STATUS_VERSIONED_ADDEDLOCALLY) {
            secondRevision = REVISION_CURRENT;
            secondTitle = loc.getString("MSG_DiffPanel_LocalNew");
        } else if (status == FileInformation.STATUS_VERSIONED_DELETEDLOCALLY || status == FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY) {
            secondRevision = null;
            secondTitle = loc.getString("MSG_DiffPanel_LocalDeleted");
        } else if (status == FileInformation.STATUS_VERSIONED_NEWINREPOSITORY) {
            secondRevision = REVISION_HEAD;
            secondTitle = loc.getString("MSG_DiffPanel_RemoteNew");
        } else if (status == FileInformation.STATUS_VERSIONED_REMOVEDINREPOSITORY) {
            secondRevision = null;
            secondTitle = loc.getString("MSG_DiffPanel_RemoteDeleted");
        } else if (status == FileInformation.STATUS_VERSIONED_MODIFIEDINREPOSITORY) {
            secondRevision = REVISION_HEAD;
            secondTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_RemoteModified"), new Object [] { revision });
        } else if (type == DIFFTYPE_REMOTE) {
            secondRevision = REVISION_HEAD;
            secondTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_RemoteModified"), new Object [] { revision });
        } else {
            secondRevision = REVISION_CURRENT;
            secondTitle = MessageFormat.format(loc.getString("MSG_DiffPanel_LocalModified"), new Object [] { revision });
        }

        firstSource = new DiffStreamSource(baseFile, firstRevision, firstTitle);
        secondSource = new DiffStreamSource(baseFile, secondRevision, secondTitle);
        title = "<html>" + Subversion.getInstance().getAnnotator().annotateNameHtml(baseFile, info); // NOI18N
    }

    /**
     * Text file setup for arbitrary revisions.
     * @param firstRevision first revision or <code>null</code> for inital.
     * @param secondRevision second revision
     */
    public Setup(File baseFile, String firstRevision, String secondRevision) {
        this.baseFile = baseFile;
        this.firstRevision = firstRevision;
        this.secondRevision = secondRevision;
        firstSource = new DiffStreamSource(baseFile, firstRevision, firstRevision);
        secondSource = new DiffStreamSource(baseFile, secondRevision, secondRevision);
    }

    public File getBaseFile() {
        return baseFile;
    }

    public void setView(DiffView view) {
        this.view = view;
    }

    public DiffView getView() {
        return view;
    }

    public StreamSource getFirstSource() {
        return firstSource;
    }

    public StreamSource getSecondSource() {
        return secondSource;
    }

    public String toString() {
        return title;
    }

    /**
     * Loads data over network
     * @param group that carries shared state. Note that this group must not be executed later on. 
     */
    void initSources() throws IOException {
        if (firstSource != null) firstSource.init();
        if (secondSource != null) secondSource.init();
    }
}
