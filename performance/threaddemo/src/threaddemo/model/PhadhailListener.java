/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package threaddemo.model;

import java.util.EventListener;

/**
 * Listener for changes in phadhails.
 * @author Jesse Glick
 */
public interface PhadhailListener extends EventListener {
    
    /** list of children has changed (must have children), call getChildren again */
    void childrenChanged(PhadhailEvent ev);
    
    /** name (and therefore also path) changed */
    void nameChanged(PhadhailNameEvent ev);
    
}
