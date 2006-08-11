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

package org.netbeans.modules.uihandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Jaroslav Tulach
 */
final class UINode extends AbstractNode implements VisualData {
    private static final SimpleFormatter FORMATTER = new SimpleFormatter();
    private LogRecord log;

    private UINode(LogRecord r, Children ch) {
        super(ch);
        log = r;
        setName(r.getMessage());
        
        Sheet.Set s = new Sheet.Set();
        s.setName(Sheet.PROPERTIES);
        s.put(createPropertyDate(this));
        s.put(createPropertyLogger(this));
        s.put(createPropertyMessage(this));
        getSheet().put(s);
    }

    public long getMillis() {
        return log.getMillis();
    }
    
    public String getLoggerName() {
        return log.getLoggerName();
    }
    
    public String getMessage() {
        return FORMATTER.format(log);
    }
    
    static Node create(LogRecord r) {
        Children ch;
        if (r.getThrown() != null) {
            ch = new StackTraceChildren(r.getThrown());
        } else {
            ch = Children.LEAF;
        }
        
        
        return new UINode(r, ch);
    }
    
    static Node.Property createPropertyDate(final VisualData source) {
        class NP extends PropertySupport.ReadOnly<Date> {
            public NP() {
                super(
                    "date", Date.class, 
                    NbBundle.getMessage(UINode.class, "MSG_DateDisplayName"),
                    NbBundle.getMessage(UINode.class, "MSG_DateShortDescription")
                );
            }

            public Date getValue() throws IllegalAccessException, InvocationTargetException {
                return source == null ? null : new Date(source.getMillis());
            }
            
            public int hashCode() {
                return getClass().hashCode();
            }
            public boolean equals(Object o) {
                return o != null && o.getClass().equals(getClass());
            }
        }
        return new NP();
    }

    static Node.Property createPropertyLogger(final VisualData source) {
        class NP extends PropertySupport.ReadOnly<String> {
            public NP() {
                super(
                    "logger", String.class, 
                    NbBundle.getMessage(UINode.class, "MSG_LoggerDisplayName"),
                    NbBundle.getMessage(UINode.class, "MSG_LoggerShortDescription")
                );
            }

            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (source == null) {
                    return null;
                }
                String full = source.getLoggerName();
                if (full.startsWith("org.netbeans.ui")) {
                    if (full.equals("org.netbeans.ui")) {
                        return "UI General";
                    }
                    
                    return full.substring("org.netbeans.ui".length());
                }
                return full;
            }
            
            public int hashCode() {
                return getClass().hashCode();
            }
            public boolean equals(Object o) {
                return o != null && o.getClass().equals(getClass());
            }
        }
        return new NP();
    }
    static Node.Property createPropertyMessage(final VisualData source) {
        class NP extends PropertySupport.ReadOnly<String> {
            public NP() {
                super(
                    "message", String.class, 
                    NbBundle.getMessage(UINode.class, "MSG_MessageDisplayName"),
                    NbBundle.getMessage(UINode.class, "MSG_MessageShortDescription")
                );
            }

            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return source == null ? null : source.getMessage();
            }
            
            public int hashCode() {
                return getClass().hashCode();
            }
            public boolean equals(Object o) {
                return o != null && o.getClass().equals(getClass());
            }
        }
        return new NP();
    }

    
    private static final class StackTraceChildren extends Children.Keys<StackTraceElement> {
        private Throwable throwable;
        public StackTraceChildren(Throwable t) {
            throwable = t;
        }
        
        protected void addNotify() {
            setKeys(throwable.getStackTrace());
        }
        
        protected Node[] createNodes(StackTraceElement key) {
            AbstractNode an = new AbstractNode(Children.LEAF);
            an.setName(key.getClassName() + "." + key.getMethodName());
            an.setDisplayName(NbBundle.getMessage(UINode.class, "MSG_StackTraceElement", 
                new Object[] { 
                    key.getFileName(),
                    key.getClassName(),
                    key.getMethodName(),
                    key.getLineNumber(),
                    afterLastDot(key.getClassName()),
                }
            ));
            return new Node[] { an };
        }
        
    } // end of StackTraceElement

    private static String afterLastDot(String s) {
        int index = s.lastIndexOf('.');
        if (index == -1) {
            return s;
        }
        return s.substring(index + 1);
    }
    

}
