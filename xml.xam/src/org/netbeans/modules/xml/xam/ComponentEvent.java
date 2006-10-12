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

package org.netbeans.modules.xml.xam;


import java.util.EventObject;

/**
 *
 * @author Nam Nguyen
 * @author Rico Cruz
 * @author Chris Webster
 */
public class ComponentEvent extends EventObject {
    static final long serialVersionUID = 1L;
    
    private EventType event;
    
    /**
     * Creates a new instance of ComponentEvent
     */
    public ComponentEvent(Object source, EventType t) {
        super(source);
        event = t;
    }
    
    public enum EventType {
        /**
         * Component value (attributes or properties) changed
         */
        VALUE_CHANGED {
            public void fireEvent(ComponentEvent evt, 
                                           ComponentListener l) {
                l.valueChanged(evt);
                }}, 
        /**
         * Childen component added
         */
        CHILD_ADDED {
                public void fireEvent(ComponentEvent evt, 
                                           ComponentListener l) {
                l.childrenAdded(evt);
                }}, 
        /**
         * Childen component removed
         */
        CHILD_REMOVED {
                public void fireEvent(ComponentEvent evt, 
                                           ComponentListener l) {
                l.childrenDeleted(evt);
                }};
        
        public abstract void fireEvent(ComponentEvent evt, 
                                       ComponentListener l);
    }
    
    public EventType getEventType() {
        return event;
    }
}
