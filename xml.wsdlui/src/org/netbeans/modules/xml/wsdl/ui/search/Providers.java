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

package org.netbeans.modules.xml.wsdl.ui.search;

import java.awt.Component;
import org.netbeans.modules.xml.wsdl.model.WSDLComponent;
import org.netbeans.modules.xml.wsdl.ui.view.treeeditor.WSDLElementNode;
import org.netbeans.modules.xml.xam.ui.category.Category;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 * Provides utility methods for the search provider implementations.
 *
 * @author Nathan Fiedler
 */
public class Providers {

    /**
     * Creates a new instance of Providers.
     */
    private Providers() {
    }

    /**
     * Retrieve the selected component for the given Category.
     *
     * @param  category  Category for which to find selected component.
     * @return  selected component, or null if not known.
     */
    public static WSDLComponent getSelectedComponent(Category category) {
        Component parent = category.getComponent().getParent();
        while (parent != null) {
            if (parent instanceof TopComponent) {
                Node[] nodes = ((TopComponent) parent).getActivatedNodes();
                if (nodes != null && nodes.length > 0) {
                    for (Node node : nodes) {
                        WSDLElementNode wen = (WSDLElementNode) node.
                                getCookie(WSDLElementNode.class);
                        if (wen != null) {
                            return wen.getWSDLComponent();
                        }
                    }
                }
            }
            parent = parent.getParent();
        }
        return null;
    }
}
