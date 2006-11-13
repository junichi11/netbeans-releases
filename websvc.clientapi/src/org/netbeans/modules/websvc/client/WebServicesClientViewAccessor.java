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
package org.netbeans.modules.websvc.client;

import org.netbeans.modules.websvc.api.client.WebServicesClientView;
import org.netbeans.modules.websvc.spi.client.WebServicesClientViewImpl;

/* This class provides access to the {@link WebServicesClientView}'s private constructor
 * from outside in the way that this class is implemented by an inner class of
 * {@link WebServicesClientView} and the instance is set into the {@link DEFAULT}.
 */
public abstract class WebServicesClientViewAccessor {

    public static WebServicesClientViewAccessor DEFAULT;
    
    // force loading of WebServicesClientView class. That will set DEFAULT variable.
    static {
        Class c = WebServicesClientView.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public abstract WebServicesClientView createWebServicesClientView(WebServicesClientViewImpl spiWebServicesClientView);

    public abstract WebServicesClientViewImpl getWebServicesClientViewImpl(WebServicesClientView wscv);

}
