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

package org.netbeans.lib.ddl.impl;

import java.util.*;
import java.sql.*;
import java.text.ParseException;
import org.openide.util.NbBundle;
import org.netbeans.lib.ddl.*;
import org.netbeans.lib.ddl.util.*;

/**
* Argument of procedure. Encapsulates name, type (in/out) and datatype.
*/
public class ProcedureArgument implements Argument {
    /** Argument name */
    private String name;

    /** Argument type */
    private int type;

    /** Argument datatype */
    private int dtype;

    /** Format */
    private String format;

    /** Additional properties */
    private Map addprops;

    private static ResourceBundle bundle = NbBundle.getBundle("org.netbeans.lib.ddl.resources.Bundle"); // NOI18N
    
    public static String getArgumentTypeName(int type)
    {
        String typename = null;
        switch (type) {
        case java.sql.DatabaseMetaData.procedureColumnIn: typename = "IN"; break; // NOI18N
        case java.sql.DatabaseMetaData.procedureColumnOut: typename = "OUT"; break; // NOI18N
        case java.sql.DatabaseMetaData.procedureColumnInOut: typename = "INOUT"; break; // NOI18N
        }

        return typename;
    }

    /** Returns name */
    public String getName()
    {
        return name;
    }

    /** Sets name */
    public void setName(String aname)
    {
        name = aname;
    }

    /** Returns name of column */
    public String getFormat()
    {
        return format;
    }

    /** Sets name of column */
    public void setFormat(String fmt)
    {
        format = fmt;
    }

    /** Returns general property */
    public Object getProperty(String pname)
    {
        return addprops.get(pname);
    }

    /** Sets general property */
    public void setProperty(String pname, Object pval)
    {
        if (addprops == null) addprops = new HashMap();
        addprops.put(pname, pval);
    }

    /** Describes type of argument: in, out, in/out or return value
    * of procedure. Particular values you can find in DatabaseMetadata;
    */
    public int getType()
    {
        return type;
    }

    /** Translates numeric representation of type into IN/OUT/INOUT strings.
    */
    public String getTypeName()
    {
        return getArgumentTypeName(type);
    }

    /** Sets type of argument */
    public void setType(int atype)
    {
        type = atype;
    }

    /** Returns datatype of argument */
    public int getDataType()
    {
        return dtype;
    }

    /** Sets datatype of argument */
    public void setDataType(int atype)
    {
        dtype = atype;
    }

    /**
    * Returns properties and it's values supported by this object.
    * argument.name		Name of argument
    * argument.type		Type of argument 
    * argument.datatype	Datatype of argument
    * Throws DDLException if object name is not specified.
    */
    public Map getColumnProperties(AbstractCommand cmd) throws DDLException {
        HashMap args = new HashMap();
        DatabaseSpecification spec = cmd.getSpecification();
        Map typemap = (Map)spec.getProperties().get("ProcedureArgumentMap"); // NOI18N
        String typename = (String)typemap.get(getArgumentTypeName(type));
        args.put("argument.name", cmd.quote(name)); // NOI18N
        args.put("argument.type", typename); // NOI18N
        args.put("argument.datatype", spec.getType(dtype)); // NOI18N
        return args;
    }

    /**
    * Returns full string representation of argument.
    */
    public String getCommand(CreateProcedure cmd)
    throws DDLException
    {
        Map cprops;
        if (format == null) throw new DDLException(bundle.getString("EXC_NoFormatSpec")); // NOI18N
        try {
            cprops = getColumnProperties(cmd);
            return CommandFormatter.format(format, cprops);
        } catch (Exception e) {
            throw new DDLException(e.getMessage());
        }
    }
}
