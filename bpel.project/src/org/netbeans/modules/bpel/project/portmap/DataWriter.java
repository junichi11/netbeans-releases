/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

// DataWriter.java - XML writer for data-oriented files.
package org.netbeans.modules.bpel.project.portmap;

import java.io.Writer;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * Write data- or field-oriented XML.
 *
 * <p>This filter pretty-prints field-oriented XML without mixed content.
 * all added indentation and newlines will be passed on down
 * the filter chain (if any).</p>
 *
 * <p>In general, all whitespace in an XML document is potentially
 * significant, so a general-purpose XML writing tool like the
 * {@link com.megginson.sax.XMLWriter XMLWriter} class cannot
 * add newlines or indentation.</p>
 *
 * <p>There is, however, a large class of XML documents where information
 * is strictly fielded: each element contains either character data
 * or other elements, but not both.  For this special case, it is possible
 * for a writing tool to provide automatic indentation and newlines
 * without requiring extra work from the user.  Note that this class
 * will likely not yield appropriate results for document-oriented
 * XML like XHTML pages, which mix character data and elements together.</p>
 *
 * <p>This writer will automatically place each start tag on a new line,
 * optionally indented if an indent step is provided (by default, there
 * is no indentation).  If an element contains other elements, the end
 * tag will also appear on a new line with leading indentation.  Consider,
 * for example, the following code:</p>
 *
 * <pre>
 * DataWriter w = new DataWriter();
 *
 * w.setIndentStep(2);
 * w.startDocument();
 * w.startElement("Person");
 * w.dataElement("name", "Jane Smith");
 * w.dataElement("date-of-birth", "1965-05-23");
 * w.dataElement("citizenship", "US");
 * w.endElement("Person");
 * w.endDocument();
 * </pre>
 *
 * <p>This code will produce the following document:</p>
 *
 * <pre>
 * &lt;?xml version="1.0" standalone="yes"?>
 *
 * &lt;Person>
 *   &lt;name>Jane Smith&lt;/name>
 *   &lt;date-of-birth>1965-05-23&lt;/date-of-birth>
 *   &lt;citizenship>US&lt;/citizenship>
 * &lt;/Person>
 * </pre>
 *
 * <p>This class inherits from {@link com.megginson.sax.XMLWriter
 * XMLWriter}, and provides all of the same support for Namespaces.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 0.2
 * @see com.megginson.sax.XMLWriter
 */
public class DataWriter extends XMLWriter
{



    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Create a new data writer for standard output.
     */
    public DataWriter ()
    {
	super();
    }


    /**
     * Create a new data writer for standard output.
     *
     * <p>Use the XMLReader provided as the source of events.</p>
     *
     * @param xmlreader The parent in the filter chain.
     */
    public DataWriter (XMLReader xmlreader)
    {
	super(xmlreader);
    }


    /**
     * Create a new data writer for the specified output.
     *
     * @param writer The character stream where the XML document
     *        will be written.
     */
    public DataWriter (Writer writer)
    {
	super(writer);
    }


    /**
     * Create a new data writer for the specified output.
     * <p>Use the XMLReader provided as the source of events.</p>
     *
     * @param xmlreader The parent in the filter chain.
     * @param writer The character stream where the XML document
     *        will be written.
     */
    public DataWriter (XMLReader xmlreader, Writer writer)
    {
	super(xmlreader, writer);
    }



    ////////////////////////////////////////////////////////////////////
    // Accessors and setters.
    ////////////////////////////////////////////////////////////////////


    /**
     * Return the current indent step.
     *
     * <p>Return the current indent step: each start tag will be
     * indented by this number of spaces times the number of
     * ancestors that the element has.</p>
     *
     * @return The number of spaces in each indentation step,
     *         or 0 or less for no indentation.
     * @see #setIndentStep
     */
    public int getIndentStep ()
    {
	return indentStep;
    }


    /**
     * Set the current indent step.
     *
     * @param indentStep The new indent step (0 or less for no
     *        indentation).
     * @see #getIndentStep
     */
    public void setIndentStep (int indentStep)
    {
	this.indentStep = indentStep;
    }



    ////////////////////////////////////////////////////////////////////
    // Override methods from XMLWriter.
    ////////////////////////////////////////////////////////////////////


    /**
     * Reset the writer so that it can be reused.
     *
     * <p>This method is especially useful if the writer failed
     * with an exception the last time through.</p>
     *
     * @see com.megginson.sax.XMLWriter#reset
     */
    public void reset ()
    {
	depth = 0;
	state = SEEN_NOTHING;
	stateStack = new Stack();
	super.reset();
    }


    /**
     * Write a start tag.
     *
     * <p>Each tag will begin on a new line, and will be
     * indented by the current indent step times the number
     * of ancestors that the element has.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the start tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#startElement(String, String, String, Attributes)
     */
    public void startElement (String uri, String localName,
			      String qName, Attributes atts)
	throws SAXException
    {
	stateStack.push(SEEN_ELEMENT);
	state = SEEN_NOTHING;
	if (depth > 0) {
	    super.characters("\n");
	}
	doIndent();
	super.startElement(uri, localName, qName, atts);
	depth++;
    }


    /**
     * Write an end tag.
     *
     * <p>If the element has contained other elements, the tag
     * will appear indented on a new line; otherwise, it will
     * appear immediately following whatever came before.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the end tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#endElement(String, String, String)
     */
    public void endElement (String uri, String localName, String qName)
	throws SAXException
    {
	depth--;
	if (state == SEEN_ELEMENT) {
	    super.characters("\n");
	    doIndent();
	}
	super.endElement(uri, localName, qName);
	state = stateStack.pop();
    }


    /**
     * Write a empty element tag.
     *
     * <p>Each tag will appear on a new line, and will be
     * indented by the current indent step times the number
     * of ancestors that the element has.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the empty tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#emptyElement(String, String, String, Attributes)
     */
    public void emptyElement (String uri, String localName,
			      String qName, Attributes atts)
	throws SAXException
    {
	state = SEEN_ELEMENT;
	if (depth > 0) {
	    super.characters("\n");
	}
	doIndent();
	super.emptyElement(uri, localName, qName, atts);
    }


    /**
     * Write a sequence of characters.
     *
     * @param ch The characters to write.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the characters, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#characters(char[], int, int)
     */
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
	state = SEEN_DATA;
	super.characters(ch, start, length);
    }



    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Print indentation for the current level.
     *
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the indentation characters, or if a filter
     *            further down the chain raises an exception.
     */
    private void doIndent ()
	throws SAXException
    {
	if (indentStep > 0 && depth > 0) {
	    int n = indentStep * depth;
	    char ch[] = new char[n];
	    for (int i = 0; i < n; i++) {
		ch[i] = ' ';
	    }
	    characters(ch, 0, n);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Constants.
    ////////////////////////////////////////////////////////////////////

    private final static Object SEEN_NOTHING = new Object();
    private final static Object SEEN_ELEMENT = new Object();
    private final static Object SEEN_DATA = new Object();



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private Object state = SEEN_NOTHING;
    private Stack stateStack = new Stack();

    private int indentStep = 0;
    private int depth = 0;

}

// end of DataWriter.java
