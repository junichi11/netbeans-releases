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

package org.netbeans.modules.bpel.debugger.variables;

import org.netbeans.modules.bpel.debugger.api.variables.NamedValueHost;
import org.netbeans.modules.bpel.debugger.api.variables.Value;
import org.netbeans.modules.bpel.debugger.api.variables.WsdlMessageValue;
import org.netbeans.modules.bpel.debugger.BreakPosition;
import org.netbeans.modules.bpel.debuggerbdi.rmi.api.BPELVariable;
import org.netbeans.modules.bpel.debuggerbdi.rmi.api.WSDLMessage;
import org.w3c.dom.Element;

/**
 *
 * @author Alexander Zgursky
 */

public class WsdlMessageValueImpl implements WsdlMessageValue {
    private BreakPosition myBreakPosition;
    private WsdlMessageVariableImpl myVariable;
    private WsdlMessageValue.Part[] myParts;
    //Map<String, Value> myPartValues = new HashMap<String, Value>();
    
    /** Creates a new instance of WsdlMessageValueImpl */
    public WsdlMessageValueImpl(BreakPosition breakPosition, WsdlMessageVariableImpl variable) {
        myBreakPosition = breakPosition;
        myVariable = variable;
    }

    public WsdlMessageValue.Part[] getParts() {
        if (myParts == null) {
            BPELVariable engineVariable = myVariable.getEngineVariable();
            WSDLMessage engineWsdlMessage = engineVariable.getWSDLMessage();
            String[] partNames = engineWsdlMessage.getParts();
            myParts = new WsdlMessageValue.Part[partNames.length];
            for (int i=0; i<partNames.length; i++) {
                String partName = partNames[i];
                myParts[i] = new PartImpl(partName);
            }
        }
        return myParts;
    }

    public NamedValueHost getValueHost() {
        return myVariable;
    }
    
    public class PartImpl implements WsdlMessageValue.Part {
        private String myName;
        private Value myValue;
        private boolean myValueIsInitialized;
        
        public PartImpl(String name) {
            myName = name;
        }

        public String getName() {
            return myName;
        }
        
        public Value getValue() {
            if (!myValueIsInitialized) {
                myValueIsInitialized = true;
                BPELVariable engineVariable = myVariable.getEngineVariable();
                WSDLMessage engineWsdlMessage = engineVariable.getWSDLMessage();

                String valueAsString = engineWsdlMessage.getPart(myName);
                if (valueAsString != null) {
                    Element element = Util.parseXmlElement(valueAsString);
                    if (element != null) {
                        myValue = new XmlElementValueImpl(element, this);
                    } else {
                        myValue = new SimpleValueImpl(valueAsString, this);
                    }
                }
            }

            return myValue;
        }

        public WsdlMessageValue getMessage() {
            return WsdlMessageValueImpl.this;
        }
    }
}