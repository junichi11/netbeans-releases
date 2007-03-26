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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.earproject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.earproject.ui.customizer.EarProjectProperties;
import org.openide.util.Mutex;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.util.ChangeSupport;

class EarSources implements Sources, PropertyChangeListener, ChangeListener  {

    private final AntProjectHelper helper;
    private final PropertyEvaluator evaluator;
    private Sources delegate;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private SourcesHelper sourcesHelper;

    EarSources(AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.helper = helper;
        this.evaluator = evaluator;
        initSources(); // have to register external build roots eagerly
    }


    public SourceGroup[] getSourceGroups(final String type) {
        return (SourceGroup[]) ProjectManager.mutex().readAccess(new Mutex.Action() {
            public Object run() {
                if (delegate == null) {
                    delegate = initSources();
                    delegate.addChangeListener(EarSources.this);
                }
                return delegate.getSourceGroups(type);
            }
        });
    }

    private Sources initSources() {
        sourcesHelper = new SourcesHelper(helper, evaluator);
        String configFilesLabel = org.openide.util.NbBundle.getMessage(EarSources.class, "LBL_Node_ConfigBase"); //NOI18N
        sourcesHelper.addPrincipalSourceRoot("${"+EarProjectProperties.META_INF+"}", configFilesLabel, /*XXX*/null, null);
        // XXX add build dir too?
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {
                sourcesHelper.registerExternalRoots(FileOwnerQuery.EXTERNAL_ALGORITHM_TRANSIENT);
            }
        });
        return sourcesHelper.createSources();
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    private void fireChange() {
        synchronized (this) {
            if (delegate != null) {
                delegate.removeChangeListener(this);
                delegate = null;
            }
        }
        changeSupport.fireChange();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
    }

    public void stateChanged (ChangeEvent event) {
        this.fireChange();
    }

}
