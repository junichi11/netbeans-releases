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


package org.netbeans.modules.pdf;


import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.SimpleBeanInfo;

import org.openide.ErrorManager;
import org.openide.loaders.UniFileLoader;
import org.openide.util.Utilities;


/** BeanInfo for <code>PDFDataLoader</code>.
 *
 * @author Jesse Glick
 */
public class PDFDataLoaderBeanInfo extends SimpleBeanInfo {

    /** Gets additional bean infos. Overrides superclass method. */
    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (UniFileLoader.class) };
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            
            return null;
        }
    }

    /** Gets icon. Overrides superclass method. */
    public Image getIcon (int type) {
        return Utilities.loadImage("org/netbeans/modules/pdf/PDFDataIcon.png"); // NOI18N
    }

}
