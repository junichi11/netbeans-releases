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
package org.netbeans.xtest.plugin.ide;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.Calendar;
import org.netbeans.xtest.util.NativeKill;
import org.netbeans.xtest.util.PNGEncoder;

/**
 * Kill IDE when timeout expires.
 * You can test it if you decrease timeout: ant -Dxtest.timeout=60000
 */
public class IdeWatchdog implements Runnable {

    public String xtestWorkdir = null;

    public static void main(String[] args) throws Exception {
        
        // this part is used in finishWatchdog target
        if(args.length == 1) {
            String xtestWorkdir = args[0];
            if(new File(xtestWorkdir, "watchdog.killed").exists()) {
                // IDE was killed => print message
                System.out.println(Calendar.getInstance().getTime());
                System.out.println("XTest: IDE killed because hard timeout was reached.");
            } else {
                // stop watchdog
                new File(xtestWorkdir, "watchdog.finish").createNewFile();
            }
            return;
        }
        
        IdeWatchdog ideWatchdog = new IdeWatchdog();
        long timeout = Long.parseLong(args[0]);
        String xtestHome = args[1];
        ideWatchdog.xtestWorkdir = args[2];
        String ideUserdir = args[3];

        // Because we run watchdog in separate VM we cannot see output messages.
        // Even if we redirect it to a file, it can't be deleted sometimes.
        // So, if you want to debug it uncomment the following line
        // redirect output stream
        //System.setOut(new PrintStream(new FileOutputStream(new File(ideWatchdog.xtestWorkdir, "watchdog.log"))));
        System.out.println("TIMEOUT="+timeout);
        System.out.println("XTEST.HOME="+xtestHome);
        System.out.println("XTEST.WORKDIR="+ideWatchdog.xtestWorkdir);
        System.out.println("IDE.USERDIR="+ideUserdir);

        Thread watchThread = new Thread(ideWatchdog);
        watchThread.start();
        try {
            System.out.println(System.currentTimeMillis()+" IdeWatchdog(): waiting for "+timeout);
            watchThread.join(timeout);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if(watchThread.isAlive()) {
            System.out.println(System.currentTimeMillis()+" Timeout expired.");
            watchThread.interrupt();
            // capture screen shot
            try {
                System.out.println("IdeWatchdog: Trying to capture screen shot.");
                PNGEncoder.captureScreenToIdeUserdir(ideUserdir, "screenshot-kill.png");
            } catch (Exception captureException) {
                // we have a problem when capturing
                System.out.println("Exception thrown when capturing IDE screenshot: "+captureException.getMessage());
            }
            System.out.println(System.currentTimeMillis()+" IdeWatchdog(): Going to kill IDE");
            new File(ideWatchdog.xtestWorkdir, "watchdog.killed").createNewFile();
            ideWatchdog.killIde(ideWatchdog.xtestWorkdir, xtestHome);
            System.out.println(System.currentTimeMillis()+" IdeWatchdog(): IDE killed");
        }
        System.out.println(System.currentTimeMillis()+" IdeWatchdog finished.");
    }
    
    public IdeWatchdog() {
        System.out.println(System.currentTimeMillis()+" IdeWatchdog created.");
    }
    
    public void run() {
        // Remove all signal files to suppress concurrent running of two watchdogs
        File xtestWorkdirFile = new File(xtestWorkdir);
        File[] filesToRemove = xtestWorkdirFile.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().startsWith("watchdog");
            }
        });
        for(int i=0;i<filesToRemove.length;i++) {
            if(!filesToRemove[i].delete()) {
                System.out.println(System.currentTimeMillis()+" IdeWatchdog: "+filesToRemove[i]+" cannot be deleted.");
            }
        }
        // create a file to be used for handling unexpected crashes of tests
        File watchdogRunningFile = null;
        try {
            watchdogRunningFile = new File(xtestWorkdir, "watchdog"+System.currentTimeMillis()+".running");
            watchdogRunningFile.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        File finishFile = new File(xtestWorkdir, "watchdog.finish");
        while(true) {
            if(finishFile.exists()) {
                System.out.println(System.currentTimeMillis()+" IdeWatchdog: IDE Finished before timeout expires.");
                return;
            }
            if(!watchdogRunningFile.exists()) {
                // watchdog should be finished (tests were killed in the middle and started another ones)
                System.out.println(System.currentTimeMillis()+" IdeWatchdog: IDE and tests killed - watchdog will be finished.");
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
    
    /** Version with "ide.flag" file 
    public void run0() {
        System.out.println(System.currentTimeMillis()+" IdeWatchdog: xtestWorkdir="+xtestWorkdir);
        File ideFlagFile = new File(xtestWorkdir, "ide.flag");
        boolean ideStarted = false;
        while(true) {
            if(!ideStarted) {
                ideStarted = ideFlagFile.exists();
                if(ideStarted) {
                    System.out.println(System.currentTimeMillis()+" IdeWatchdog: IDE started");
                }
            } else {
                if(!ideFlagFile.exists()) {
                    // IDE finished correctly => stop this watchdog
                    System.out.println(System.currentTimeMillis()+" IdeWatchdog: IDE finished");
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
     */
    
    /** Kill IDE. */
    public static boolean killIde(String xtestWorkdir, String xtestHome) {
        // create flag file indicating running tests
        File idePidFile = new File(xtestWorkdir, "ide.pid");
        if (idePidFile.exists()) {
            try {
                LineNumberReader reader = new LineNumberReader(new FileReader(idePidFile));
                String line = reader.readLine();
                if (line != null) {
                    try {
                        long pid = Long.parseLong(line);
                        System.setProperty("xtest.home", xtestHome);
                        System.out.println("IdeWatchdog - requesting thread dump on process with PID="+pid);
                        NativeKill.dumpProcess(pid);
                        // sleep a bit, so resources can be released
                        Thread.sleep(2000);
                        System.out.println("IdeWatchdog - killing process with PID="+pid);
                        boolean result = NativeKill.killProcess(pid);
                        // sleep a bit, so resources can be released
                        Thread.sleep(2000);
                        return result;
                    } catch (NumberFormatException nfe) {
                        System.out.println("ERROR - cannot parse PID written in the ide.flag file: "+line+" - not killing");
                    }
                }
            } catch (IOException ioe) {
                System.out.println("IOException when reading PID from ide.flag file - cannot kill");
                System.out.println(ioe.toString());
            } catch (Exception e) {
                System.out.println("Exception when trying to kill IDE");
                System.out.println(e.toString());
            }
        } else {
            System.out.println("ERROR - cannot find file "+idePidFile);
        }
        return false;
    }
    
}
