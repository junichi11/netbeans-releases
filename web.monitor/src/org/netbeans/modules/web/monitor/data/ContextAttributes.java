/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/**
 * ContextAttributes.java
 * 
 * Matches the DTD element ContextAttributes
 *
 * Created: Tue Jan 15 18:22:27 2002
 *
 * @author Ana von Klopp
 * @version
 */

package org.netbeans.modules.web.monitor.data;
import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

public class ContextAttributes extends BaseBean {

    static Vector comparators = new Vector();

    static public final String PARAM = "Param"; // NOI18N

    public ContextAttributes() {
	this(Common.USE_DEFAULT_VALUES);
    }

    public ContextAttributes(int options) {
	super(ContextAttributes.comparators, new org.netbeans.modules.schema2beans.Version(1, 0, 6));
	// Properties (see root bean comments for the bean graph)
	this.createProperty("Param", PARAM,  // NOI18N
			    Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			    Param.class);
	this.createAttribute(PARAM, "name", "Name",  // NOI18N
			     AttrProp.CDATA | AttrProp.REQUIRED,
			     null, null);
	this.createAttribute(PARAM, "value", "Value",  // NOI18N
			     AttrProp.CDATA | AttrProp.IMPLIED,
			     null, null);
	this.initialize(options);
    }

    // Setting the default values of the properties
    void initialize(int options) {

    }

    // This attribute is an array, possibly empty
    public void setParam(int index, Param value) {
	this.setValue(PARAM, index, value);
    }

    //
    public Param getParam(int index) {
	return (Param)this.getValue(PARAM, index);
    }

    // This attribute is an array, possibly empty
    public void setParam(Param[] value) {
	this.setValue(PARAM, value);
    }

    //
    public Param[] getParam() {
	return (Param[])this.getValues(PARAM);
    }

    public Hashtable getHashtable() {

	Param[] attributes =  (Param[])this.getValues(PARAM);
	int numAttributes = attributes.length;
	Hashtable ht = new Hashtable(numAttributes);
	
	for(int i=0; i<numAttributes; ++i) {
	    String name =  attributes[i].getAttributeValue("name");  // NOI18N
	    String value = attributes[i].getAttributeValue("value"); // NOI18N
	    ht.put(name, value);
	}	
	return ht;
    }


    // Return the number of properties
    public int sizeParam() {
	return this.size(PARAM);
    }

    // Add a new element returning its index in the list
    public int addParam(Param value) {
	return this.addValue(PARAM, value);
    }

    //
    // Remove an element using its reference
    // Returns the index the element had in the list
    //
    public int removeParam(Param value) {
	return this.removeValue(PARAM, value);
    }

    // This method verifies that the mandatory properties are set
    public boolean verify() {
	return true;
    }

    //
    static public void addComparator(BeanComparator c) {
	ContextAttributes.comparators.add(c);
    }

    //
    static public void removeComparator(BeanComparator c) {
	ContextAttributes.comparators.remove(c);
    }
    //
    public void addPropertyChangeListener(PropertyChangeListener l) {
	BeanProp p = this.beanProp();
	if (p != null)
	    p.addPCListener(l);
    }

    //
    public void removePropertyChangeListener(PropertyChangeListener l) {
	BeanProp p = this.beanProp();
	if (p != null)
	    p.removePCListener(l);
    }

    //
    public void addPropertyChangeListener(String n,
					  PropertyChangeListener l) {
	BeanProp p = this.beanProp(n);
	if (p != null)
	    p.addPCListener(l);
    }

    //
    public void removePropertyChangeListener(String n,
					     PropertyChangeListener l) { 
	BeanProp p = this.beanProp(n);
	if (p != null)
	    p.removePCListener(l);
    }

    // Dump the content of this bean returning it as a String
    public void dump(StringBuffer str, String indent) {
	String s;
	BaseBean n;
	str.append(indent);
	str.append("Param["+this.sizeParam()+"]"); // NOI18N
	for(int i=0; i<this.sizeParam(); i++)
	    {
		str.append(indent+"\t"); // NOI18N
		str.append("#"+i+":");  // NOI18N
		n = this.getParam(i);
		if (n != null)
		    n.dump(str, indent + "\t");  // NOI18N
		else
		    str.append(indent+"\tnull");  // NOI18N
		this.dumpAttributes(PARAM, i, str, indent);
	    }

    }

    public String dumpBeanNode() {
	StringBuffer str = new StringBuffer();
	str.append("ContextAttributes\n");  // NOI18N
	this.dump(str, "\n  ");  // NOI18N
	return str.toString();
    }

    public String toString() {
	StringBuffer buf = new StringBuffer("ContextAttributes\n");  // NOI18N
	
	Param[] params = getParam();
	buf.append(String.valueOf(params.length));
	buf.append(" attribute lines\n");  // NOI18N
	for(int i=0; i<params.length; ++i) {
	    buf.append(String.valueOf(i));
	    buf.append(". Attribute: ");  // NOI18N
	    buf.append(params[i].getAttributeValue("name"));  // NOI18N
	    buf.append(", Value: ");  // NOI18N
	    buf.append(params[i].getAttributeValue("value"));  // NOI18N
	    buf.append("\n");  // NOI18N
	}
	return buf.toString();
    }
}
