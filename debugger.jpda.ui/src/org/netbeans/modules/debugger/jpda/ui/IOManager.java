/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.debugger.jpda.ui;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

public class IOManager {

//    /** DebuggerManager output constant. */
//    public static final int                 DEBUGGER_OUT = 1;
//    /** Process output constant. */
//    public static final int                 PROCESS_OUT = 2;
//    /** Status line output constant. */
//    public static final int                 STATUS_OUT = 4;
//    /** All outputs constant. */
//    public static final int                 ALL_OUT = DEBUGGER_OUT + 
//                                                PROCESS_OUT + STATUS_OUT;
//    /** Standart process output constant. */
//    public static final int                 STD_OUT = 1;
//    /** Error process output constant. */
//    public static final int                 ERR_OUT = 2;

    
    // variables ...............................................................
    
    protected InputOutput                   debuggerIO = null;
    private OutputWriter                    debuggerOut;
    private String                          name;
    private JPDADebugger                    debugger;
    
    /** output writer Thread */
    private Hashtable                       lines = new Hashtable ();
    private Listener                        listener = new Listener ();

    
    // init ....................................................................
    
    public IOManager (
        String title,
        JPDADebugger debugger
    ) {
        debuggerIO = IOProvider.getDefault ().getIO (title, true);
        debuggerIO.setFocusTaken (false);
        debuggerOut = debuggerIO.getOut ();
        this.debugger = debugger;
    }
    
    
    // public interface ........................................................

    private LinkedList buffer = new LinkedList ();
    private RequestProcessor.Task task;
    
    /**
    * Prints given text to the output.
    */
    public void println (
        final String text, 
    //    final int where, 
        final Line line
    ) {
        if (text == null)
            throw new NullPointerException ();
        synchronized (buffer) {
            buffer.addLast (new Text (text, line));
        }
        if (task == null)
            task = RequestProcessor.getDefault ().post (new Runnable () {
                public void run () {
                    synchronized (buffer) {
                        int i, k = buffer.size ();
                        for (i = 0; i < k; i++) {
                            Text t = (Text) buffer.removeFirst ();
                            try {
                                //if ((t.where & DEBUGGER_OUT) != 0) {
                                    if (t.line != null) {
                                        debuggerOut.println (t.text, listener);
                                        lines.put (t.text, t.line);
                                    } else
                                        debuggerOut.println (t.text);
                                    debuggerOut.flush ();
                                //}
                               // if ((t.where & STATUS_OUT) != 0) 
                                    StatusDisplayer.getDefault ().setStatusText (t.text);
                            } catch (IOException ex) {
                                ex.printStackTrace ();
                            }
                        }
                    }
                }
            }, 500, Thread.MIN_PRIORITY);
        else 
            task.schedule (500);
    }

    void closeStream () {
        debuggerOut.close ();
    }

    void close () {
        debuggerIO.closeInputOutput ();
    }
    
    
    // innerclasses ............................................................
    
    private class Listener implements OutputListener {
        public void outputLineSelected (OutputEvent ev) {
        }
        public void outputLineAction (OutputEvent ev) {
            String t = ev.getLine ();
            Line l = (Line) lines.get (t);
            if (l == null) return;
            l.show ();
        }
        public void outputLineCleared (OutputEvent ev) {
            lines = new Hashtable ();
        }
    }
    
    private static class Text {
        private String text;
        private Line line;
     //   private int where;
        
        private Text (String text, Line line) {
            this.text = text;
            //this.where = where;
            this.line = line;
        }
    }
    
    static class Line {
        private String url;
        private int lineNumber;
        private JPDADebugger debugger;
        
        Line (String url, int lineNumber, JPDADebugger debugger) {
            this.url = url;
            this.lineNumber = lineNumber;
        }
        
        void show () {
            EditorContextBridge.showSource (url, lineNumber, debugger);
        }
    }
}
