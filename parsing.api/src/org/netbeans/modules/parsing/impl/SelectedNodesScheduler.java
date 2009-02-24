/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.openide.util.lookup.ServiceProvider;


/**
 *
 * @author Jan Jancura
 */
@ServiceProvider(service=Scheduler.class)
public class SelectedNodesScheduler extends Scheduler {


    private Source              source;

    public SelectedNodesScheduler () {
        TopComponent.getRegistry ().addPropertyChangeListener (new AListener ());
    }
    
    private void refresh () {
        final Node[] nodes = TopComponent.getRegistry ().getActivatedNodes ();
        if (nodes.length == 1) {
            final DataObject dataObject = nodes [0].getLookup ().lookup (DataObject.class);
            if (dataObject != null) {
                final FileObject fileObject = dataObject.getPrimaryFile ();
                source = Source.create (fileObject);
                if (source != null) {
                    schedule (source, new SchedulerEvent (this) {});
                    return;
                }
            }
        }
        //schedule (null, null);
    }
    
    @Override
    public String toString () {
        return "SelectedNodesScheduller";
    }

    @Override
    protected SchedulerEvent createSchedulerEvent (SourceModificationEvent event) {
        if (event.getModifiedSource () == source)
            return new SchedulerEvent (this) {};
        return null;
    }
    
    private class AListener implements PropertyChangeListener {
    
        public void propertyChange (PropertyChangeEvent evt) {
            if (evt.getPropertyName () == null ||
                evt.getPropertyName ().equals (TopComponent.Registry.PROP_ACTIVATED_NODES)
            ) {
                refresh ();
            }
        }
    }
}
