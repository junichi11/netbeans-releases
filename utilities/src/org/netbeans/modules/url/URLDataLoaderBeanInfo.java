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


package org.netbeans.modules.url;


import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.SimpleBeanInfo;

import org.openide.ErrorManager;
import org.openide.loaders.UniFileLoader;
import org.openide.util.Utilities;


/** <code>URLDataLoader</code> bean info.
 *
 * @author Ian Formanek
 */
public class URLDataLoaderBeanInfo extends SimpleBeanInfo {

    /** Gets additional beaninfo. */
    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (UniFileLoader.class) };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            
            return null;
        }
    }

    /** @param type Desired type of the icon
     * @return returns the URL loader's icon
     */
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/url/urlObject.gif"); // NOI18N
    }

}
