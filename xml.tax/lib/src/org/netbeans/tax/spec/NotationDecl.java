/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.tax.spec;

import org.netbeans.tax.TreeNotationDecl;
import org.netbeans.tax.TreeException;
import org.netbeans.tax.InvalidArgumentException;

/**
 *
 * @author  Libor Kramolis
 * @version 0.1
 */
public interface NotationDecl {

    //
    // Constraints
    //
    
    /**
     *
     */
    public static interface Constraints {

    	public void checkNotationDeclName (String name) throws InvalidArgumentException;
    
    	public boolean isValidNotationDeclName (String name);
    

    	public void checkNotationDeclPublicId (String publicId) throws InvalidArgumentException;
    
    	public boolean isValidNotationDeclPublicId (String publicId);
    

    	public void checkNotationDeclSystemId (String systemId) throws InvalidArgumentException;
    
    	public boolean isValidNotationDeclSystemId (String systemId);
    
    } // end: interface Constraints


    //
    // Creator
    //

    /**
     *
     */
    public static interface Creator {
	
	/**
	 * @throws InvalidArgumentException
	 */
  	public TreeNotationDecl createNotationDecl (String name, String publicId, String systemId);

    } // end: interface Creator


    //
    // Writer
    //

    /**
     *
     */
    public static interface Writer {
	
	public void writeNotationDecl (TreeNotationDecl notationDecl) throws TreeException;

    } // end: interface Writer

}
