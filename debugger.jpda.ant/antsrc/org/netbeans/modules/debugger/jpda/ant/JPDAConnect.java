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

package org.netbeans.modules.debugger.jpda.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.util.RequestProcessor;

/**
 * Ant task to attach the NetBeans JPDA debugger to a remote process.
 * @see "#18708"
 * @author Jesse Glick
 */
public class JPDAConnect extends Task {
    
    private static final boolean startVerbose = System.getProperty ("netbeans.debugger.start") != null;
    
    private String host = "localhost";

    private String address;
    
    /** Explicit sourcepath of the debugged process. */
    private Path sourcepath = null;
    
    /** Explicit classpath of the debugged process. */
    private Path classpath = null;
    
    /** Explicit bootclasspath of the debugged process. */
    private Path bootclasspath = null;
        
    /** Name which will represent this debugging session in debugger UI.
     * If known in advance it should be name of the app which will be debugged.
     */
    private String name;

    /** Default transport is socket*/
    private String transport = "dt_socket";
    
    
    /**
     * Host to connect to.
     * By default, localhost.
     */
    public void setHost (String h) {
        host = h;
    }
    
    public void setAddress (String address) {
        this.address = address;
    }
    
    private String getAddress () {
        return address;
    }
    
    public void addClasspath (Path path) {
        if (classpath != null)
            throw new BuildException ("Only one classpath subelement is supported");
        classpath = path;
    }
    
    public void addBootclasspath (Path path) {
        if (bootclasspath != null)
            throw new BuildException ("Only one bootclasspath subelement is supported");
        bootclasspath = path;
    }
    
    public void addSourcepath (Path path) {
        if (sourcepath != null)
            throw new BuildException ("Only one sourcepath subelement is supported");
        sourcepath = path;
    }
    
    public void setTransport (String transport) {
        this.transport = transport;
    }
    
    private String getTransport () {
        return transport;
    }
    
    public void setName (String name) {
        this.name = name;
    }
    
    private String getName () {
        return name;
    }
    
    public void execute () throws BuildException {
        if (startVerbose)
            System.out.println("\nS JPDAConnect ***************");

        if (name == null)
            throw new BuildException ("name attribute must specify name of this debugging session", getLocation ());
        if (address == null)
            throw new BuildException ("address attribute must specify port number or memory allocation unit name of connection", getLocation ());
        if (transport == null)
            transport = "dt_socket";

        final Object[] lock = new Object [1];

        final ClassPath sourcePath = JPDAStart.createSourcePath (
            getProject (),
            classpath,
            sourcepath,
            bootclasspath
        );

        synchronized(lock) {
            RequestProcessor.getDefault ().post (new Runnable () {
                public void run() {
                    synchronized(lock) {
                        try {
                            if (startVerbose)
                                System.out.println("\nS JPDAConnect2");
                            // VirtualMachineManagerImpl can be initialized here, so needs
                            // to be inside RP thread.
                            if (startVerbose)
                                System.out.println("\nS JPDADebugger.attach: host = " + host + " port = " + address + " transport = " + transport);
                            if (transport.equals ("dt_socket"))
                                try {
                                    JPDADebugger.attach (
                                        host, 
                                        Integer.parseInt (address), 
                                        new Object[] {sourcePath}
                                    );
                                } catch (NumberFormatException e) {
                                    throw new BuildException ("address attribute must specify port number for dt_socket connection", getLocation ());
                                }
                            else
                                JPDADebugger.attach (address, new Object[] {sourcePath});
                        } catch (Throwable e) {
                            lock[0] = e;
                        } finally {
                            lock.notify();
                        }
                    }
                }
            });
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new BuildException(e);
            }
            if (lock[0] != null)  {
                throw new BuildException((Throwable) lock[0]);
            }

        }
        if (host == null)
            log ("Attached JPDA debugger to " + address);
        else
            log ("Attached JPDA debugger to " + host + ":" + address);
    }
}
