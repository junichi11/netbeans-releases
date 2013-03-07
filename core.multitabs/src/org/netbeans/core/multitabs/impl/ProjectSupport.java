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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.core.multitabs.impl;

import java.beans.PropertyChangeListener;
import org.netbeans.swing.tabcontrol.TabData;
import org.openide.util.Lookup;
import org.openide.util.Parameters;

/**
 * Abstraction of Project API
 *
 * @author S. Aubrecht
 */
public abstract class ProjectSupport {

    private static ProjectSupport theInstance = null;

    public static ProjectSupport getDefault() {
        synchronized( ProjectSupport.class ) {
            if( null == theInstance ) {
                theInstance = Lookup.getDefault().lookup( ProjectSupport.class );
                if( null == theInstance ) {
                    theInstance = new DummyProjectSupport();
                }
            }
        }
        return theInstance;
    }

    public abstract boolean isEnabled();


    public abstract void addPropertyChangeListener( PropertyChangeListener l );

    public abstract void removePropertyChangeListener( PropertyChangeListener l );

    public abstract ProjectProxy[] getOpenProjects();

    public abstract ProjectProxy getProjectForTab( TabData tab );

    public static final class ProjectProxy {

        private final Object token;
        private final String displayName;
        private final String path;

        public ProjectProxy( Object token, String displayName, String path ) {
            Parameters.notNull( "token", token ); //NOI18N
            this.token = token;
            this.displayName = displayName;
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + (this.token != null ? this.token.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals( Object obj ) {
            if( obj == null ) {
                return false;
            }
            if( getClass() != obj.getClass() ) {
                return false;
            }
            final ProjectProxy other = ( ProjectProxy ) obj;
            if( this.token != other.token && (this.token == null || !this.token.equals( other.token )) ) {
                return false;
            }
            return true;
        }

        final Object getToken() {
            return token;
        }
    }

    private static class DummyProjectSupport extends ProjectSupport {

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void addPropertyChangeListener( PropertyChangeListener l ) {
        }

        @Override
        public void removePropertyChangeListener( PropertyChangeListener l ) {
        }

        @Override
        public ProjectProxy[] getOpenProjects() {
            return new ProjectProxy[0];
        }

        @Override
        public ProjectProxy getProjectForTab( TabData tab ) {
            return null;
        }
    }
}
