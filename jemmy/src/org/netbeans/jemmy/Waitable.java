/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy;

/**
 * 
 * Defines criteria for waiting.
 * @see org.netbeans.jemmy.Waiter
 * 
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public interface Waitable{

    /**
     * Checks if wait criteria have been met.
     */
    public Object actionProduced(Object obj);

    /**
     * @return a description of the wait criteria.
     */
    public String getDescription();
}
