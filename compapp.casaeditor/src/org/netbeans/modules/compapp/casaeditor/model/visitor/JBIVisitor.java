/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.compapp.casaeditor.model.visitor;

import org.netbeans.modules.compapp.casaeditor.model.*;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Connection;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Connections;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Consumer;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Consumes;
import org.netbeans.modules.compapp.casaeditor.model.jbi.ExtensibilityElement;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Identification;
import org.netbeans.modules.compapp.casaeditor.model.jbi.JBI;
import org.netbeans.modules.compapp.casaeditor.model.jbi.JBIComponent;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Provider;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Provides;
import org.netbeans.modules.compapp.casaeditor.model.jbi.ServiceAssembly;
import org.netbeans.modules.compapp.casaeditor.model.jbi.ServiceUnit;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Services;
import org.netbeans.modules.compapp.casaeditor.model.jbi.Target;

/**
 *
 * @author jqian
 */
public interface JBIVisitor {
    
    void visit(JBI component);
    void visit(Services component);
    void visit(Consumes component);
    void visit(Provides component);
    
    void visit(ServiceAssembly component);
    void visit(ServiceUnit component);
    void visit(Connections component);
    void visit(Connection component);
    void visit(Consumer component);
    void visit(Provider component);
    void visit(Identification component);
    void visit(Target component);   
    void visit(ExtensibilityElement component);
    

    /**
     * Default shallow visitor.
     */
    public static class Default implements JBIVisitor {
        public void visit(JBI component) {
            visitComponent(component);
        }
        
        public void visit(Services component) {
            visitComponent(component);
        }
        
        public void visit(Consumes component) {
            visitComponent(component);
        }
        
        public void visit(Provides component) {
            visitComponent(component);
        }
        
        public void visit(ServiceAssembly component) {
            visitComponent(component);
        }

        public void visit(ServiceUnit component) {
            visitComponent(component);
        }

        public void visit(Connections component) {
            visitComponent(component);
        }

        public void visit(Connection component) {
            visitComponent(component);
        }

        public void visit(Consumer component) {
            visitComponent(component);
        }

        public void visit(Provider component) {
            visitComponent(component);
        }

        public void visit(Identification component) {
            visitComponent(component);
        }

        public void visit(Target component) {
            visitComponent(component);
        }
        
        public void visit(ExtensibilityElement component) {
            visitComponent(component);
        }
        
        protected void visitComponent(JBIComponent component) {
            ;
        }
    }
    
    /**
     * Deep visitor.
     */
    public static class Deep extends Default {
        protected void visitChild(JBIComponent component) {
            for (JBIComponent child : component.getChildren()) {
                child.accept(this);
            }
        }
    }
}
