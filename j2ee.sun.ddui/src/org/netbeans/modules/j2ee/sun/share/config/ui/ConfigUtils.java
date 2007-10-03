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

/*
 * ConfigUtils.java
 *
 * Created on August 15, 2001, 4:29 PM
 */

package org.netbeans.modules.j2ee.sun.share.config.ui;

import java.beans.*;
import java.lang.reflect.Method;
import java.util.*;

import javax.enterprise.deploy.spi.DConfigBean;

import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.Lookup;

import org.netbeans.modules.j2ee.sun.share.config.ConfigBeanStorage;


/*
 * @author  gfink
 * @version
 */
public class ConfigUtils {

    static Map infoMap = new HashMap();

    public static BeanInfo createBeanInfo(Object bean) {
        BeanInfo info;
        if (bean == null) {
            return null;
        }
        if(infoMap.containsKey(bean.getClass())) {
            return (BeanInfo) infoMap.get(bean.getClass());
        }
        try {
            // Introspector doesn't work!
            //            info = (BeanInfo) Class.forName(bean.getClass().getName() + "BeanInfo").newInstance();
            //            info = Introspector.getBeanInfo(bean.getClass()/*,ConfigBean.class */);  
//                System.out.println("ConfigUtils.createBeanInfo BeanInfo: " +  dConfigBean.getClass().getName() + "BeanInfo");
            info = (BeanInfo)
                bean.getClass().getClassLoader().loadClass(bean.getClass().getName() + "BeanInfo").newInstance();
                //              System.out.println("Creating info " + info);             
            
            
        } catch (/* Introspection */ Exception ie) {
//            ie.getMessage();
            // BeanInfo not found.  Must create one?
            info = new DefaultBeanInfo(bean.getClass());
        }
        infoMap.put(bean.getClass(),info);
        return info;
    }
    
    // don't do the classloader lookup
    static BeanInfo createDefaultBeanInfo(Object bean) {
        BeanInfo info = new DefaultBeanInfo(bean.getClass());
        infoMap.put(bean.getClass(),info);
        return info;
    }

    public static Sheet.Set[] createSheets(Object bean) {
        return null;
    }
    
    public static Sheet.Set createSheet(Object bean) {
        //        System.err.println("Creating sheet for " + bean);
        Sheet.Set basic = new Sheet.Set();
        try {
            BeanInfo info = null;
            if (bean instanceof ConfigBeanStorage) {
                info = createBeanInfo(((ConfigBeanStorage)bean).getConfigBean());
            }
            else {
                info = createBeanInfo(bean);
            }
            if(info == null) return basic;
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            if(descriptors == null) {
                System.err.println("Beaninfo doesn't have property descriptors for " + bean.getClass() + " info " + info.getClass());
  /*              BeanInfo defInfo = createDefaultBeanInfo(bean);
                if(defInfo != null) {
                    info = defInfo;
                    descriptors = info.getPropertyDescriptors();
                } */
                Node.Property property = ConfigProperty.getBraindead(info.getBeanDescriptor());
                basic.put(property);
            }
            else
                for(int i = 0; i < descriptors.length; i++) {
                    if(descriptors[i].isHidden()) continue;
                    if(descriptors[i].getName().equals("xpaths")) continue;
                    if(descriptors[i].getName().equals("standardDDBean")) continue;
                    //            System.err.println("Creating property for " + descriptors[i].getName());
                    DConfigBean dConfigBean = ((ConfigBeanStorage)bean).getConfigBean();
                    Node.Property property = ConfigProperty.getProperty(dConfigBean,descriptors[i]);
//                    Node.Property property = ConfigProperty.getProperty(bean,descriptors[i]);
                    basic.put(property);
                }
        } catch (Exception e) {
            ErrorManager em = (ErrorManager) Lookup.getDefault().lookup(ErrorManager.class);
            em.notify(e);
        }
        // need some way of returning null if there are no properties in
        // a set.
        return basic;
    }
    
    public static Object getBeanPropertyValue(DConfigBean config, String propName) {
        Class cls = config.getClass();
        StringBuffer name = new StringBuffer("get"+propName); //NOI18N
        name.setCharAt(3,Character.toUpperCase(propName.charAt(0)));
        try {
            Method method = cls.getDeclaredMethod(name.toString(), new Class[0]);
            method.setAccessible (true);
            return method.invoke(config,new Object[0]);
        } catch (Exception ex) {
            org.openide.ErrorManager.getDefault().log(ErrorManager.USER, ex.toString());
        }
        return null;
    }
    
    public static void setBeanPropertyValue(DConfigBean config, String propName, String value) {
        Class cls = config.getClass();
        StringBuffer name = new StringBuffer("set"+propName); //NOI18N
        name.setCharAt(3,Character.toUpperCase(propName.charAt(0)));
        try {
            Method method = cls.getDeclaredMethod(name.toString(), new Class[] { java.lang.String.class });
            method.setAccessible (true);
            method.invoke(config, new Object[] { value });
        } catch (Exception ex) {
            org.openide.ErrorManager.getDefault().log(ErrorManager.USER, ex.toString());
        }
    }}
