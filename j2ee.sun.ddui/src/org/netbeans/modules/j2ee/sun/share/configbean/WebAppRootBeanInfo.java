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

package org.netbeans.modules.j2ee.sun.share.configbean;

import java.beans.*;
import org.openide.util.Exceptions;

public class WebAppRootBeanInfo extends SimpleBeanInfo {
	
	/** Return an appropriate icon (currently, only 16x16 color is available)
	 */
	public java.awt.Image getIcon(int iconKind) {
		return loadImage("resources/WebAppRootIcon16.gif");	// NOI18N
	}
	
	/**
	 * Gets the bean's <code>BeanDescriptor</code>s.
	 *
	 * @return BeanDescriptor describing the editable
	 * properties of this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 */
	public BeanDescriptor getBeanDescriptor() {
            BeanDescriptor beanDescriptor = new BeanDescriptor  ( org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class , org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.WebAppRootCustomizer.class );
            beanDescriptor.setDisplayName ( "WARDisplayName" );
            beanDescriptor.setShortDescription ( "WARShortDescription" );//GEN-HEADEREND:BeanDescriptor

                    // Here you can add code for customizing the BeanDescriptor.

            return beanDescriptor;
	}
	
	/**
	 * Gets the bean's <code>PropertyDescriptor</code>s.
	 *
	 * @return An array of PropertyDescriptors describing the editable
	 * properties supported by this bean.  May return null if the
	 * information should be obtained by automatic analysis.
	 * <p>
	 * If a property is indexed, then its entry in the result array will
	 * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
	 * A client of getPropertyDescriptors can use "instanceof" to check
	 * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
            int PROPERTY_classLoader = 0;
            int PROPERTY_contextRoot = 1;
            int PROPERTY_delegate = 2;
            int PROPERTY_extraClassPath = 3;
            int PROPERTY_identity = 4;
            int PROPERTY_jspConfig = 5;
            int PROPERTY_localeCharsetInfo = 6;
            int PROPERTY_property = 7;
            int PROPERTY_refIdentity = 8;
            PropertyDescriptor[] properties = new PropertyDescriptor[9];

            try {
                properties[PROPERTY_classLoader] = new PropertyDescriptor ( "classLoader", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "isClassLoader", "setClassLoader" );
                properties[PROPERTY_classLoader].setExpert ( true );
                properties[PROPERTY_contextRoot] = new PropertyDescriptor ( "contextRoot", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getContextRoot", "setContextRoot" );
                properties[PROPERTY_delegate] = new PropertyDescriptor ( "delegate", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "isDelegate", "setDelegate" );
                properties[PROPERTY_extraClassPath] = new PropertyDescriptor ( "extraClassPath", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getExtraClassPath", "setExtraClassPath" );
                properties[PROPERTY_identity] = new PropertyDescriptor ( "identity", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getIdentity", "setIdentity" );
                properties[PROPERTY_jspConfig] = new PropertyDescriptor ( "jspConfig", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getJspConfig", "setJspConfig" );
                properties[PROPERTY_jspConfig].setPropertyEditorClass ( org.netbeans.modules.j2ee.sun.share.configbean.editors.DummyPropertyEditor.class );
                properties[PROPERTY_localeCharsetInfo] = new PropertyDescriptor ( "localeCharsetInfo", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getLocaleCharsetInfo", "setLocaleCharsetInfo" );
                properties[PROPERTY_property] = new IndexedPropertyDescriptor ( "property", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, null, null, "getProperty", null );
                properties[PROPERTY_property].setExpert ( true );
                properties[PROPERTY_property].setPropertyEditorClass ( org.netbeans.modules.j2ee.sun.share.configbean.editors.DummyPropertyEditor.class );
                properties[PROPERTY_refIdentity] = new PropertyDescriptor ( "refIdentity", org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "getRefIdentity", null );
            }
            catch( IntrospectionException e) {
                Exceptions.printStackTrace(e);
            }
            return properties;
	}
	
	/**
	 * Gets the bean's <code>EventSetDescriptor</code>s.
	 *
	 * @return  An array of EventSetDescriptors describing the kinds of
	 * events fired by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
            int EVENT_propertyChangeListener = 0;
            int EVENT_vetoableChangeListener = 1;
            EventSetDescriptor[] eventSets = new EventSetDescriptor[2];

            try {
                eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
                eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
            }
            catch( IntrospectionException e) {
                Exceptions.printStackTrace(e);
            }
            return eventSets;
	}
	
	/**
	 * Gets the bean's <code>MethodDescriptor</code>s.
	 *
	 * @return  An array of MethodDescriptors describing the methods
	 * implemented by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public MethodDescriptor[] getMethodDescriptors() {
            return new MethodDescriptor[0];
	}
	
}

