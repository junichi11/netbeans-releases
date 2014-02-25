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

package org.netbeans.modules.cnd.model.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 * CsmFile analogue of JavaSourceTaskFactoryManager
 * 
 * @author Sergey Grinev
 */
public class CsmFileTaskFactoryManager implements LookupListener {
    private static CsmFileTaskFactoryManager INSTANCE;
    //public static final boolean USE_MORE_SEMANTIC = Boolean.getBoolean("cnd.semantic.advanced"); // NOI18N
    
    public static synchronized void register() {
        //TODO: JavaSource one registers this way but we need something less
        // permanent to don't flood engine with our tasks unless we really have c/c++ file
        if (INSTANCE == null) {
            INSTANCE = new CsmFileTaskFactoryManager();
        }
    }
    
    private final Lookup.Result<CsmFileTaskFactory> factories;
    private final RequestProcessor.Task updateTask;
    private CsmFileTaskFactoryManager() {
        updateTask = new RequestProcessor("CsmFileTaskFactoryManager Worker", 1).create(new Runnable() { //NOI18N
            @Override
            public void run() {
                update();
            }
        });
        
        factories = Lookup.getDefault().lookupResult(CsmFileTaskFactory.class);
        Logger logger = Logger.getLogger(CsmFileTaskFactoryManager.class.getName());
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("CsmFileTaskFactoryManager: " + factories.allInstances().size() + " factories were found.");
        }
        // postpone loading services (IZ164684)
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                factories.addLookupListener(CsmFileTaskFactoryManager.this);
                resultChanged(null);
            }
        });
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        updateTask.schedule(0);
    }
    
    private void update() {
        for (CsmFileTaskFactory f : factories.allInstances()) {
            ACCESSOR.fireChangeEvent(f);
        }
    }
    
    /*public*/ static interface Accessor {
        public abstract void fireChangeEvent(CsmFileTaskFactory f);
    }
    
    /*public*/ static Accessor ACCESSOR;
    
}