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

package org.netbeans.modules.web.jsps.parserapi;

import java.util.*;

import org.openide.ErrorManager;

//import org.apache.jasper.Constants;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.FunctionInfo;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;

/**
 * A repository for various info about the translation unit under compilation.
 *
 * @author Kin-man Chung, Petr Jiricka
 */

public abstract class PageInfo {

    public static final String JSP_SERVLET_BASE = "org.apache.jasper.runtime.HttpJspBase";
    
    private List imports;
    private List dependants;

//    private BeanRepository beanRepository;
    private BeanData[] beans;
    private Map taglibsMap;
    private Map jspPrefixMapper;
    private Map xmlPrefixMapper;
    /** Approximate XML prefix mapper. Same as xmlPrefixMapper, but does not "forget" mappings (by popping them). */
    private Map approxXmlPrefixMapper;
    private String defaultLanguage = "java";
    private String language;
    private String defaultExtends = JSP_SERVLET_BASE;
    private String xtends;
    private String contentType = null;
    private String session;
    private boolean isSession = true;
    private String bufferValue;
    private int buffer = 8*1024;	// XXX confirm
    private String autoFlush;
    private boolean isAutoFlush = true;
    private String isThreadSafeValue;
    private boolean isThreadSafe = true;
    private String isErrorPageValue;
    private boolean isErrorPage = false;
    private String errorPage = null;
    private String info;

    private boolean scriptless = false;
    private boolean scriptingInvalid = false;
    private String isELIgnoredValue;
    private boolean isELIgnored = false;
    private String omitXmlDecl = null;

    private String doctypeName = null;
    private String doctypePublic = null;
    private String doctypeSystem = null;
 
    private boolean isJspPrefixHijacked;

    // Set of all element and attribute prefixes used in this translation unit
    private Set prefixes;

    private boolean hasJspRoot = false;
    private List includePrelude;
    private List includeCoda;
    private List pluginDcls;		// Id's for tagplugin declarations


    public PageInfo(/*BeanRepository beanRepository*/
            Map taglibsMap,
            Map jspPrefixMapper,
            Map xmlPrefixMapper,
            Map approxXmlPrefixMapper,
            List imports,
            List dependants,
            List includePrelude,
            List includeCoda,
            List pluginDcls,
            Set prefixes
        ) {
	//this.beanRepository = beanRepository;
	this.taglibsMap = taglibsMap;
	this.jspPrefixMapper = jspPrefixMapper;
	this.xmlPrefixMapper = xmlPrefixMapper;
        this.approxXmlPrefixMapper = approxXmlPrefixMapper;
	this.imports = imports;
        this.dependants = dependants;
	this.includePrelude = includePrelude;
	this.includeCoda = includeCoda;
	this.pluginDcls = pluginDcls;
	this.prefixes = prefixes;
    }

    /**
     * Check if the plugin ID has been previously declared.  Make a not
     * that this Id is now declared.
     * @return true if Id has been declared.
     */
    public boolean isPluginDeclared(String id) {
	if (pluginDcls.contains(id))
	    return true;
	pluginDcls.add(id);
	return false;
    }

    public void addImports(List imports) {
	this.imports.addAll(imports);
    }

    public void addImport(String imp) {
	this.imports.add(imp);
    }

    public List getImports() {
	return imports;
    }

    public void addDependant(String d) {
	if (!dependants.contains(d))
            dependants.add(d);
    }
     
    public List getDependants() {
        return dependants;
    }

    public void setScriptless(boolean s) {
	scriptless = s;
    }

    public boolean isScriptless() {
	return scriptless;
    }

    public void setScriptingInvalid(boolean s) {
	scriptingInvalid = s;
    }

    public boolean isScriptingInvalid() {
	return scriptingInvalid;
    }

    public List getIncludePrelude() {
	return includePrelude;
    }

    public void setIncludePrelude(Vector prelude) {
	includePrelude = prelude;
    }

    public List getIncludeCoda() {
	return includeCoda;
    }

    public void setIncludeCoda(Vector coda) {
	includeCoda = coda;
    }

    public void setHasJspRoot(boolean s) {
	hasJspRoot = s;
    }

    public boolean hasJspRoot() {
	return hasJspRoot;
    }

    public String getOmitXmlDecl() {
	return omitXmlDecl;
    }

    public void setOmitXmlDecl(String omit) {
	omitXmlDecl = omit;
    }

    public String getDoctypeName() {
        return doctypeName;
    }

    public void setDoctypeName(String doctypeName) {
        this.doctypeName = doctypeName;
    }

    public String getDoctypeSystem() {
        return doctypeSystem;
    }

    public void setDoctypeSystem(String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }

    public String getDoctypePublic() {
        return doctypePublic;
    }

    public void setDoctypePublic(String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }

    /* Tag library and XML namespace management methods */

    public void setIsJspPrefixHijacked(boolean isHijacked) {
	isJspPrefixHijacked = isHijacked;
    }

    public boolean isJspPrefixHijacked() {
	return isJspPrefixHijacked;
    }

    /*
     * Adds the given prefix to the set of prefixes of this translation unit.
     * 
     * @param prefix The prefix to add
     */
    public void addPrefix(String prefix) {
	prefixes.add(prefix);
    }

    /*
     * Checks to see if this translation unit contains the given prefix.
     *
     * @param prefix The prefix to check
     *
     * @return true if this translation unit contains the given prefix, false
     * otherwise
     */
    public boolean containsPrefix(String prefix) {
	return prefixes.contains(prefix);
    }

    /*
     * Maps the given URI to the given tag library.
     *
     * @param uri The URI to map
     * @param info The tag library to be associated with the given URI
     */
    public void addTaglib(String uri, TagLibraryInfo info) {
	taglibsMap.put(uri, info);
    }

    /*
     * Gets the tag library corresponding to the given URI.
     *
     * @return Tag library corresponding to the given URI
     */
    public TagLibraryInfo getTaglib(String uri) {
	return (TagLibraryInfo) taglibsMap.get(uri);
    }

    /*
     * Gets the collection of tag libraries that are associated with a URI
     *
     * @return Collection of tag libraries that are associated with a URI
     */
    public Collection getTaglibs() {
	return taglibsMap.values();
    }

    /*
     * Checks to see if the given URI is mapped to a tag library.
     *
     * @param uri The URI to map
     *
     * @return true if the given URI is mapped to a tag library, false
     * otherwise
     */
    public boolean hasTaglib(String uri) {
	return taglibsMap.containsKey(uri);
    }

    /*
     * Maps the given prefix to the given URI.
     *
     * @param prefix The prefix to map
     * @param uri The URI to be associated with the given prefix
     */
    public void addPrefixMapping(String prefix, String uri) {
	jspPrefixMapper.put(prefix, uri);
    }

    /*
     * Pushes the given URI onto the stack of URIs to which the given prefix
     * is mapped.
     *
     * @param prefix The prefix whose stack of URIs is to be pushed
     * @param uri The URI to be pushed onto the stack
     */
    public void pushPrefixMapping(String prefix, String uri) {
	LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
	if (stack == null) {
	    stack = new LinkedList();
        xmlPrefixMapper.put(prefix, stack);
	}
	stack.addFirst(uri);
    }

    /*
     * Removes the URI at the top of the stack of URIs to which the given 
     * prefix is mapped. 
     *
     * @param prefix The prefix whose stack of URIs is to be popped
     */
    public void popPrefixMapping(String prefix) {
	LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
	if (stack == null || stack.size() == 0) {
	    // XXX throw new Exception("XXX");
	}
	stack.removeFirst();
    }

    /*
     * Returns the URI to which the given prefix maps.
     *
     * @param prefix The prefix whose URI is sought
     *
     * @return The URI to which the given prefix maps
     */
    public String getURI(String prefix) {

	String uri = null;

	LinkedList stack = (LinkedList) xmlPrefixMapper.get(prefix);
	if (stack == null || stack.size() == 0) {
	    uri = (String) jspPrefixMapper.get(prefix);
	} else {
	    uri = (String) stack.getFirst();
	}

	return uri;
    }


    /* Page/Tag directive attributes */

    /*
     * language
     */
    public void setLanguage(String value) {
	language = value;
    }

    public String getLanguage(boolean useDefault) {
	return (language == null && useDefault ? defaultLanguage : language);
    }

    public String getLanguage() {
	return getLanguage(true);
    }


    /*
     * extends
     */
    public void setExtends(String value) {

	xtends = value;

	/*
	 * If page superclass is top level class (i.e. not in a package)
	 * explicitly import it. If this is not done, the compiler will assume
	 * the extended class is in the same pkg as the generated servlet.
	 */
	//if (value.indexOf('.') < 0)
	//    n.addImport(value);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @param useDefault TRUE if the default
     * (org.apache.jasper.runtime.HttpJspBase) should be returned if this
     * attribute has not been set, FALSE otherwise
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.jasper.runtime.HttpJspBase) if this attribute has
     * not been set and useDefault is TRUE
     */
    public String getExtends(boolean useDefault) {
	return (xtends == null && useDefault ? defaultExtends : xtends);
    }

    /**
     * Gets the value of the 'extends' page directive attribute.
     *
     * @return The value of the 'extends' page directive attribute, or the
     * default (org.apache.jasper.runtime.HttpJspBase) if this attribute has
     * not been set
     */
    public String getExtends() {
	return getExtends(true);
    }


    /*
     * contentType
     */
    public void setContentType(String value) {
	contentType = value;
    }

    public String getContentType() {
	return contentType;
    }


    /*
     * buffer
     */
    public void setBufferValue(String value) throws JspException {
        if (value == null) 
            return;

	if ("none".equalsIgnoreCase(value))
	    buffer = 0;
	else {
	    if (value == null || !value.endsWith("kb"))
		throw new JspException(value);
	    try {
		Integer k = new Integer(value.substring(0, value.length()-2));
		buffer = k.intValue() * 1024;
	    } catch (NumberFormatException e) {
                throw new JspException(value);
	    }
	}

	bufferValue = value;
    }

    public String getBufferValue() {
	return bufferValue;
    }

    public int getBuffer() {
	return buffer;
    }


    /*
     * session
     */
    public void setSession(String value) throws JspException {
        if (value == null) 
            return;

	if ("true".equalsIgnoreCase(value))
	    isSession = true;
	else if ("false".equalsIgnoreCase(value))
	    isSession = false;
	else
	    throw new JspException(value);

	session = value;
    }

    public String getSession() {
	return session;
    }

    public boolean isSession() {
	return isSession;
    }


    /*
     * autoFlush
     */
    public void setAutoFlush(String value) throws JspException {
        if (value == null) 
            return;

	if ("true".equalsIgnoreCase(value))
	    isAutoFlush = true;
	else if ("false".equalsIgnoreCase(value))
	    isAutoFlush = false;
	else
	    throw new JspException(value);

	autoFlush = value;
    }

    public String getAutoFlush() {
	return autoFlush;
    }

    public boolean isAutoFlush() {
	return isAutoFlush;
    }


    /*
     * isThreadSafe
     */
    public void setIsThreadSafe(String value) throws JspException {
        if (value == null) 
            return;

	if ("true".equalsIgnoreCase(value))
	    isThreadSafe = true;
	else if ("false".equalsIgnoreCase(value))
	    isThreadSafe = false;
	else
	    throw new JspException(value);

	isThreadSafeValue = value;
    }

    public String getIsThreadSafe() {
	return isThreadSafeValue;
    }

    public boolean isThreadSafe() {
	return isThreadSafe;
    }


    /*
     * info
     */
    public void setInfo(String value) {
	info = value;
    }

    public String getInfo() {
	return info;
    }

    
    /*
     * errorPage
     */
    public void setErrorPage(String value) {
	errorPage = value;
    }

    public String getErrorPage() {
	return errorPage;
    }


    /*
     * isErrorPage
     */
    public void setIsErrorPage(String value) throws JspException {
        if (value == null) 
            return;

	if ("true".equalsIgnoreCase(value))
	    isErrorPage = true;
	else if ("false".equalsIgnoreCase(value))
	    isErrorPage = false;
	else
	    throw new JspException(value);

	isErrorPageValue = value;
    }

    public String getIsErrorPage() {
	return isErrorPageValue;
    }

    public boolean isErrorPage() {
	return isErrorPage;
    }


    /*
     * isELIgnored
     */
    public void setIsELIgnored(String value) throws JspException {
        if (value == null) 
            return;

	if ("true".equalsIgnoreCase(value))
	    isELIgnored = true;
	else if ("false".equalsIgnoreCase(value))
	    isELIgnored = false;
	else {
            throw new JspException(value);
	}

	isELIgnoredValue = value;
    }

    public void setELIgnored(boolean s) {
	isELIgnored = s;
    }

    public String getIsELIgnored() {
	return isELIgnoredValue;
    }

    public boolean isELIgnored() {
	return isELIgnored;
    }
    
    // added in NetBeans
    
    public Map getTagLibraries() {
        return taglibsMap;
    }
    
    public Map getJspPrefixMapper() {
        return jspPrefixMapper;
    }
    
    public Map getXMLPrefixMapper() {
        return xmlPrefixMapper;
    }
    
    public Map getApproxXmlPrefixMapper() {
        return approxXmlPrefixMapper;
    }
    
    public BeanData[] getBeans() {
        return beans;
    }

    public void setBeans(BeanData beans[]) {
        this.beans = beans;
    }

    
    /** Returns the FunctionMapper for a particular prefix. 
     * @param currentPrefix relevant tag library prefix. If the expression to evaluate is
     * inside an attribute value of a custom tag, then the prefix with which the tag's 
     * tag library is declared, should be passed in. If the expression is plain
     * JSP text, null should be passed in.
     * @return FunctionMapper relevant to the given prefix.
     */
    //public abstract FunctionMapper getFunctionMapper(String currentPrefix);
        
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String nl = "\n"; // NOI18N
        String indent = "      ";  // NOI18N
        String nlIndent = nl + indent;
        
        sb.append("----- PageInfo -----\n");  // NOI18N
        sb.append(indent).append("imports:\n").append(collectionToString(imports, indent + "  "));  // NOI18N
        sb.append(indent).append("dependants:\n").append(collectionToString(dependants, indent + "  "));  // NOI18N
        sb.append(indent).append("taglibsMap:\n").append(taglibsMapToString(taglibsMap, indent + "  "));  // NOI18N
        sb.append(indent).append("prefixMapper:\n").append(mapToString(jspPrefixMapper, indent + "  "));  // NOI18N
        // PENDING -xmlPrefixMapper
        // sb.append(indent).append("xmlprefixMapper:\n").append(mapToString(xmlPrefixMapper, indent + "  "));
        sb.append(indent).append("approxXmlPrefixMapper :\n").append(mapToString(approxXmlPrefixMapper, indent + "  "));  // NOI18N
        sb.append(indent).append("language            : ").append(language).append("\n");  // NOI18N
        sb.append(indent).append("xtends              : ").append(xtends).append("\n");  // NOI18N
        sb.append(indent).append("contentType         : ").append(contentType).append("\n");  // NOI18N
        sb.append(indent).append("session             : ").append(isSession).append("\n");  // NOI18N
        sb.append(indent).append("buffer              : ").append(buffer).append("\n");  // NOI18N
        sb.append(indent).append("autoFlush           : ").append(isAutoFlush).append("\n");  // NOI18N
        sb.append(indent).append("threadSafe          : ").append(isThreadSafe).append("\n");  // NOI18N
        sb.append(indent).append("isErrorPage         : ").append(isErrorPage).append("\n");  // NOI18N
        sb.append(indent).append("errorPage           : ").append(errorPage).append("\n");  // NOI18N
        sb.append(indent).append("scriptless          : ").append(scriptless).append("\n");  // NOI18N
        sb.append(indent).append("scriptingInvalid    : ").append(scriptingInvalid).append("\n");  // NOI18N
        sb.append(indent).append("elIgnored           : ").append(isELIgnored).append("\n");  // NOI18N
        sb.append(indent).append("omitXmlDecl         : ").append(omitXmlDecl).append("\n");  // NOI18N
        sb.append(indent).append("isJspPrefixHijacked : ").append(isJspPrefixHijacked).append("\n");  // NOI18N
        sb.append(indent).append("doctypeName         : ").append(doctypeName).append("\n");  // NOI18N
        sb.append(indent).append("doctypeSystem       : ").append(doctypeSystem).append("\n");  // NOI18N
        sb.append(indent).append("doctypePublic       : ").append(doctypePublic).append("\n");  // NOI18N
        sb.append(indent).append("hasJspRoot          : ").append(hasJspRoot).append("\n");  // NOI18N
        sb.append(indent).append("prefixes:\n").append(collectionToString(prefixes, indent + "  "));  // NOI18N
        sb.append(indent).append("includePrelude:\n").append(collectionToString(includePrelude, indent + "  "));  // NOI18N
        sb.append(indent).append("includeCoda:\n").append(collectionToString(includeCoda, indent + "  "));  // NOI18N
        sb.append(indent).append("pluginDcls:\n").append(collectionToString(pluginDcls, indent + "  "));  // NOI18N
        sb.append(indent).append("beans:\n").append(beansToString(beans, indent + "  "));  // NOI18N

        return sb.toString();
    }
    
    private String collectionToString(Collection c, String indent) {
        StringBuffer sb = new StringBuffer();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            sb.append(indent).append(it.next()).append("\n");  // NOI18N
        }
        return sb.toString();
    }
    
    private String taglibsMapToString(Map m, String indent) {
        StringBuffer sb = new StringBuffer();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            sb.append(indent).append("tag library: ").append(key).append("\n");  // NOI18N
            sb.append(tagLibraryInfoToString((TagLibraryInfo)m.get(key), indent + "    "));  // NOI18N
        }
        return sb.toString();
    }
    
    public String tagLibraryInfoToString(TagLibraryInfo info, String indent) {
        StringBuffer sb = new StringBuffer();
        sb.append(indent).append("tlibversion : ").append(getFieldByReflection("tlibversion", info)).append("\n");  // NOI18N
        sb.append(indent).append("jspversion  : ").append(info.getRequiredVersion()).append("\n");  // NOI18N
        sb.append(indent).append("shortname   : ").append(info.getShortName()).append("\n");  // NOI18N
        sb.append(indent).append("urn         : ").append(info.getReliableURN()).append("\n");  // NOI18N
        sb.append(indent).append("info        : ").append(info.getInfoString()).append("\n");  // NOI18N
        sb.append(indent).append("uri         : ").append(info.getURI()).append("\n");  // NOI18N
        
        TagInfo tags[] = info.getTags();
        if (tags != null) {
            for (int i = 0; i < tags.length; i++)
                sb.append(tagInfoToString(tags[i], indent + "  "));  // NOI18N
        }
        
        TagFileInfo tagFiles[] = info.getTagFiles();
        if (tagFiles != null) {
            for (int i = 0; i < tagFiles.length; i++)
                sb.append(tagFileToString(tagFiles[i], indent + "  "));  // NOI18N
        }
        
        FunctionInfo functions[] = info.getFunctions();
        if (functions != null) {
            for (int i = 0; i < functions.length; i++)
                sb.append(functionInfoToString(functions[i], indent + "  "));  // NOI18N
        }
        
        return sb.toString();
    }
    
    public String tagInfoToString(TagInfo tag, String indent) {
        StringBuffer sb = new StringBuffer();
        if (tag != null) {
            sb.append(indent).append("tag name     : ").append(tag.getTagName()).append("\n");  // NOI18N
            sb.append(indent).append("    class    : ").append(tag.getTagClassName()).append("\n");  // NOI18N
            sb.append(indent).append("    attribs  : [");  // NOI18N
            TagAttributeInfo attrs[] = tag.getAttributes();
            for (int i = 0; i < attrs.length; i++) {
                sb.append(attrs[i].getName());
                if (i < attrs.length - 1) {
                    sb.append(", ");  // NOI18N
                }
            }
            sb.append("]\n");  // NOI18N
        }
        else {
            sb.append(indent).append("taginfo is null\n");  // NOI18N
        }
        return sb.toString();
    }
    
    public String functionInfoToString(FunctionInfo function, String indent) {
        StringBuffer sb = new StringBuffer();
        if (function != null) {
            sb.append(indent).append("function name     : ").append(function.getName()).append("\n");  // NOI18N
            sb.append(indent).append("         class    : ").append(function.getFunctionClass()).append("\n");  // NOI18N
            sb.append(indent).append("         signature: ").append(function.getFunctionSignature()).append("\n");  // NOI18N
        }
        else {
            sb.append(indent).append("functioninfo is null\n");  // NOI18N
        }
        return sb.toString();
    }
    
    public String tagFileToString(TagFileInfo tagFile, String indent) {
        StringBuffer sb = new StringBuffer();
        sb.append(indent).append("tagfile path : ").append(tagFile.getPath()).append("\n");  // NOI18N
        sb.append(tagInfoToString(tagFile.getTagInfo(), indent));
        return sb.toString();
    }
    
    private Object getFieldByReflection(String fieldName, TagLibraryInfo info) {
        try {
            java.lang.reflect.Field f = TagLibraryInfo.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(info);
        }
        catch (NoSuchFieldException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        catch (IllegalAccessException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
    }
    
    private String beansToString(BeanData beans[], String indent) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < beans.length; i++) {
            sb.append(indent).append("bean : ").append(beans[i].getId()).append(",    ").  // NOI18N
                append(scopeToString(beans[i].getScope())).append(",    ").  // NOI18N
                append(beans[i].getClassName()).append("\n");  // NOI18N
        }
        return sb.toString();
    }
    
    private String scopeToString(int scope) {
        switch (scope) {
            case PageContext.PAGE_SCOPE        : return "PAGE";  // NOI18N
            case PageContext.SESSION_SCOPE     : return "SESSION";  // NOI18N
            case PageContext.APPLICATION_SCOPE : return "APPLICATION";  // NOI18N
            case PageContext.REQUEST_SCOPE     : return "REQUEST";  // NOI18N
        }
        return " !!! ";
    }

    /** Interface which describes data for beans used by this page. */
    public interface BeanData {

        /** Identifier of the bean in the page (variable name). */
        public String getId();

        /** Scope for this bean. Returns constants defined in {@link javax.servlet.jsp.PageContext}. */
        public int getScope();

        /** Returns the class name for this bean. */
        public String getClassName();

    } // interface BeanData

    // helper methods for help implement toString() 
    private static String mapToString(Map m, String indent) {
        StringBuffer sb = new StringBuffer();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            sb.append(indent).append(key).append(" -> ").append(m.get(key)).append("\n");
        }
        return sb.toString();
    }
    
}
