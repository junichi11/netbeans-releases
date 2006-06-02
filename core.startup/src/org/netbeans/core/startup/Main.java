/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.startup;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.netbeans.Util;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import org.openide.modules.Dependency;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Main class for NetBeans when run in GUI mode.
 */
public final class Main extends Object {
    /** module subsystem */
    private static ModuleSystem moduleSystem;
    /** module subsystem is fully ready */
    private static boolean moduleSystemInitialized;

  /** is there a splash screen or not */
  private static Splash.SplashOutput splash;
  
  /** is there progress bar in splash or not */
  private static final boolean noBar = Boolean.getBoolean("netbeans.splash.nobar");

  /** Defines a max value for splash progress bar.
   */
  public static void setSplashMaxSteps(int maxSteps)
  {
      if (noBar || CLIOptions.isNoSplash() || splash == null)
          return;
      splash.setMaxSteps(maxSteps);
  }
  
  /** Adds temporary steps to create a max value for splash progress bar later.
   */
  public static void addToSplashMaxSteps(int steps)
  {
      if (noBar || CLIOptions.isNoSplash() || splash == null)
          return;
      splash.addToMaxSteps(steps);
  }
  
  /** Adds temporary steps and creates a max value for splash progress bar.
   */
  public static void addAndSetSplashMaxSteps(int steps)
  {
      if (noBar || CLIOptions.isNoSplash() || splash == null)
          return;
      splash.addAndSetMaxSteps(steps);
  }
  
  /** Increments a current value of splash progress bar by one step.
   */
  public static void incrementSplashProgressBar()
  {
      incrementSplashProgressBar(1);
  }
  
  /** Increments a current value of splash progress bar by given steps.
   */
  public static void incrementSplashProgressBar(int steps)
  {
      if (noBar || CLIOptions.isNoSplash() || splash == null)
          return;
      splash.increment(steps);
  }
  
  /** Prints the text to splash screen or to status line, if available.
   */
  public static void setStatusText (String msg) {
        if (splash != null) {
            splash.print (msg);
        }
        if (moduleSystemInitialized) {
            org.netbeans.core.startup.CoreBridge.conditionallyPrintStatus (msg);
        }
  }
  
  /** Starts TopThreadGroup which properly overrides uncaughtException
   * Further - new thread in the group execs main
   */
  public static void main (String[] argv) throws Exception {
    TopThreadGroup tg = new TopThreadGroup ("IDE Main", argv); // NOI18N - programatic name
    StartLog.logStart ("Forwarding to topThreadGroup"); // NOI18N
    tg.start ();
    StartLog.logProgress ("Main.main finished"); // NOI18N
  }


  private static boolean nbFactoryInitialized;
  /** Initializes default stream factory */
  public static void initializeURLFactory () {
    if (!nbFactoryInitialized) {
        java.net.URLStreamHandlerFactory fact = new NbURLStreamHandlerFactory();
        try {
            java.net.URL.setURLStreamHandlerFactory(fact);
        } catch (Error e) {
            // Can happen if we try to start NB twice in the same VM.
            // Print the error but try to continue.
            e.printStackTrace();
        }
        nbFactoryInitialized = true;
    }
  }
  
  /**
   * Sets up the custom font size and theme url for the plaf library to
   * process.
   */
  static void initUICustomizations() {
      if (!CLIOptions.isGui ()) {
          return;
      }
    
      URL themeURL = null;
      boolean wantTheme = Boolean.getBoolean ("netbeans.useTheme") ||
          CLIOptions.uiClass != null && CLIOptions.uiClass.getName().indexOf("MetalLookAndFeel") >= 0;

      try {
          if (wantTheme) {
              //Put a couple things into UIDefaults for the plaf library to process if it wants
               FileObject fo =
                    Repository.getDefault().getDefaultFileSystem().findResource("themes.xml"); //NOI18N
               if (fo == null) {            
                    // File on SFS failed --> try to load from a jar from path
                    // /org/netbeans/core/startup/resources/themes.xml
                    try {
                        themeURL = new URL("nbresloc:/org/netbeans/core/startup/resources/themes.xml"); //NOI18N
                        // check whether the file is there:
                        themeURL.openStream().close();
                    } catch (IOException ex) {
                        themeURL = null;
                    }
               } else {
                    try {
                        themeURL = fo.getURL();
                    } catch (FileStateInvalidException fsie) {
                        //do nothing
                    }
               }
          }
          //Bugfix #33546: If fontsize was not set from cammand line try to set it from bundle key
          if (CLIOptions.uiFontSize == 0) {
              String key = "";
              try {
                  key = NbBundle.getMessage (Main.class, "CTL_globalFontSize"); //NOI18N
              } catch (MissingResourceException mre) {
                  //Key not found, nothing to do
              }
              if (key.length() > 0) {
                  try {
                      CLIOptions.uiFontSize = Integer.parseInt(key);
                  } catch (NumberFormatException exc) {
                      //Incorrect value, nothing to do
                  }
              }
          }
      } finally {
          CoreBridge.getDefault ().initializePlaf(CLIOptions.uiClass, CLIOptions.uiFontSize, themeURL);
      }
      if (CLIOptions.uiFontSize > 0 && "GTK".equals(UIManager.getLookAndFeel().getID())) { //NOI18N
          Util.err.warning(NbBundle.getMessage(Main.class,
          "GTK_FONTSIZE_UNSUPPORTED")); //NOI18N
      }
      StartLog.logProgress("Fonts updated"); // NOI18N
  }
    /** Get and initialize module subsystem.  */
    public static ModuleSystem getModuleSystem() {
        synchronized (Main.class) {
            if (moduleSystem != null) {
                return moduleSystem;
            }

            StartLog.logStart ("Modules initialization"); // NOI18N
            try {
                moduleSystem = new ModuleSystem(Repository.getDefault().getDefaultFileSystem());
            } catch (IOException ioe) {
                // System will be screwed up.
                throw (IllegalStateException) new IllegalStateException("Module system cannot be created").initCause(ioe); // NOI18N
            }
            StartLog.logProgress ("ModuleSystem created"); // NOI18N
        }

        moduleSystem.loadBootModules();
        moduleSystem.readList();
        Main.addAndSetSplashMaxSteps(30); // additional steps after loading all modules
        moduleSystem.restore();
        StartLog.logEnd ("Modules initialization"); // NOI18N

        moduleSystemInitialized = true;
        
        return moduleSystem;
    }
    
    /** Is used to find out whether the system has already been initialized
     * for the first time or not yet.
     * @return true if changes in the lookup shall mean real changes, false if it just
     *   the first initalization
     */
    public static boolean isInitialized() {
        return moduleSystemInitialized;
    }
    
  
  /**
  * @exception SecurityException if it is called multiple times
  */
  static void start (String[] args) throws SecurityException {
    StartLog.logEnd ("Forwarding to topThreadGroup"); // NOI18N
    StartLog.logStart ("Preparation"); // NOI18N

    // just setup some reasonable values for this deprecated property
    // 6.2 seems to be like the right version as that is the last one
    // that ever saw openide
    System.setProperty ("org.openide.specification.version", "6.2"); // NOI18N
    System.setProperty ("org.openide.version", "deprecated"); // NOI18N
    System.setProperty ("org.openide.major.version", "IDE/1"); // NOI18N

    // Enforce JDK 1.5+ since we would not work without it.
    if (Dependency.JAVA_SPEC.compareTo(new SpecificationVersion("1.5")) < 0) { // NOI18N
        System.err.println("The IDE requires JDK 5 or higher to run."); // XXX I18N?
        org.netbeans.TopSecurityManager.exit(1);
    }

    // In the past we derived ${jdk.home} from ${java.home} by appending
    // "/.." to the end of ${java.home} assuming that JRE is under JDK
    // directory.  It does not always work.  On MacOS X JDK and JRE files
    // are mixed together, thus ${jdk.home} == ${java.home}.  In several
    // Linux distros JRE and JDK are installed at the same directory level
    // with ${jdk.home}/jre a symlink to ${java.home}, which means
    // ${java.home}/.. != ${jdk.home}.
    //
    // Now the launcher can set ${jdk.home} explicitly because it knows
    // best where the JDK is.

    String jdkHome = System.getProperty("jdk.home");  // NOI18N

    if (jdkHome == null) {
        jdkHome = System.getProperty("java.home");  // NOI18N

        if (Utilities.getOperatingSystem() != Utilities.OS_MAC) {
            jdkHome += File.separator + "..";  // NOI18N
        }

        System.setProperty("jdk.home", jdkHome);  // NOI18N
    }

    // read environment properties from external file, if any
    readEnvMap ();

    // initialize the URL factory
    initializeURLFactory();
  
    if (System.getProperties ().get ("org.openide.TopManager") == null) { // NOI18N
      // this tells the system that we run in guy mode
      System.setProperty ("org.openide.TopManager.GUI", "true"); // NOI18N
      // update the top manager to our main if it has not been provided yet
      System.getProperties().put (
        // Note that it is no longer actually a TopManager; historical relic:
        "org.openide.TopManager", // NOI18N
        "org.netbeans.core.NonGui" // NOI18N
      );
    }

    CLIOptions.initialize();
    StartLog.logProgress ("Command line parsed"); // NOI18N


// 5. initialize GUI 
    StartLog.logStart ("XML Factories"); //NOI18N
    
    org.netbeans.core.startup.SAXFactoryImpl.install();
    org.netbeans.core.startup.DOMFactoryImpl.install();
    //Bugfix #35919: Log message to console when initialization of local
    //graphics environment fails eg. due to incorrect value of $DISPLAY
    //on X Windows (Linux, Solaris). In such case IDE will not start
    //so we must inform user about error.
      
    if (CLIOptions.isGui ()) {
        try {
            java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        } catch (java.lang.InternalError exc) {
            String s = NbBundle.getMessage(Main.class, "EXC_GraphicsStartFails1", exc.getMessage());
            System.out.println(s);
            s = NbBundle.getMessage(Main.class, "EXC_GraphicsStartFails2", CLIOptions.getUserDir() + "/var/log/messages.log");
            System.out.println(s);
            throw exc;
        }
    }
    StartLog.logEnd ("XML Factories"); //NOI18N
    
    

    org.netbeans.core.startup.InstalledFileLocatorImpl.prepareCache();

    // Initialize beans - [PENDING - better place for this ?]
    //                    [PENDING - can PropertyEditorManager garbage collect ?]
    String[] sysbisp = Introspector.getBeanInfoSearchPath();
    String[] nbbisp = new String[] {
        "org.netbeans.beaninfo", // NOI18N
    };
    String[] allbisp = new String[sysbisp.length + nbbisp.length];
    System.arraycopy(nbbisp, 0, allbisp, 0, nbbisp.length);
    System.arraycopy(sysbisp, 0, allbisp, nbbisp.length, sysbisp.length);
    Introspector.setBeanInfoSearchPath(allbisp);


    try {
        if ((System.getProperty ("netbeans.full.hack") == null) && (System.getProperty ("netbeans.close") == null)) {
	    // -----------------------------------------------------------------------------------------------------
	    // License check
            if (!handleLicenseCheck()) {
                org.netbeans.TopSecurityManager.exit(0);
            }
	    // -----------------------------------------------------------------------------------------------------
	    // Upgrade
            if (!handleImportOfUserDir ()) {
                org.netbeans.TopSecurityManager.exit(0);
            }
        }
    } catch (Exception e) {
        ErrorManager.getDefault().notify(e);
    }
    StartLog.logProgress ("License check performed and upgrade wizard consulted"); // NOI18N

    //
    // 8.5 - we can show the splash only after the upgrade wizard finished
    //

    showSplash ();

    // -----------------------------------------------------------------------------------------------------

    setStatusText (NbBundle.getMessage(Main.class, "MSG_IDEInit"));

    
    // -----------------------------------------------------------------------------------------------------
    // 9. Modules
    
    assert Repository.getDefault() instanceof NbRepository : "Has to be NbRepository: " + Repository.getDefault(); // NOI18N
    getModuleSystem ();
    
    // property editors are registered in modules, so wait a while before loading them
    CoreBridge.getDefault().registerPropertyEditors();
    StartLog.logProgress ("PropertyEditors registered"); // NOI18N

    CoreBridge.getDefault().loadSettings();
    StartLog.logProgress ("IDE settings loaded"); // NOI18N
    
    {
        Iterator it = Lookup.getDefault().lookupAll(RunLevel.class).iterator();
        
        while (it.hasNext ()) {
            RunLevel level = (RunLevel)it.next ();
            level.run ();
        }
    }
    
    org.netbeans.Main.finishInitialization();
    StartLog.logProgress("Ran any delayed command-line options"); // NOI18N

    // finish starting
    if (splash != null) {
      Splash.hideSplash(splash);
      splash = null;
    }
    StartLog.logProgress ("Splash hidden"); // NOI18N
    StartLog.logEnd ("Preparation"); // NOI18N
  }
  
    /** Return splash screen.
    */
    final static Splash.SplashOutput getSplash() {
        return splash;
    }
  
    /** This is a notification about hiding wizards 
     * during startup (Import, Setup). It makes splash screen visible again.
     */
    protected static void showSplash () {
        if (!CLIOptions.isNoSplash()) {
            if (splash != null) {
                if (Splash.isVisible(splash))
                    return;
                splash = null;
            }
            splash = Splash.showSplash ();
        }
    }
    
    /** Loads a class from available class loaders. */
    private static final Class getKlass(String cls) {
        try {
            ClassLoader loader;
            ModuleSystem ms = moduleSystem;
            if (ms != null) {
                loader = ms.getManager ().getClassLoader ();
            } else {
                loader = Main.class.getClassLoader ();
            }
            
            return Class.forName(cls, false, loader);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getLocalizedMessage());
        }
    }

    /** Does import of userdir. Made non-private just for testing purposes.
     *
     * @return true if the execution should continue or false if it should
     *     stop
     */
    static boolean handleImportOfUserDir () {
        class ImportHandler implements Runnable {
            private File installed = new File (new File (CLIOptions.getUserDir (), "var"), "imported"); // NOI18N
            private String classname;
            private boolean executedOk; 
            
            public boolean shouldDoAnImport () {
                classname = System.getProperty ("netbeans.importclass"); // NOI18N
                
                return classname != null && !installed.exists ();
            }
            
            
            public void run() {
                // This module is included in our distro somewhere... may or may not be turned on.
                // Whatever - try running some classes from it anyway.
                try {
                    Class clazz = getKlass (classname);
                
                    // Method showMethod = wizardClass.getMethod( "handleUpgrade", new Class[] { Splash.SplashOutput.class } ); // NOI18N
                    Method showMethod = clazz.getMethod( "main", new Class[] { String[].class } ); // NOI18N
                    showMethod.invoke (null, new Object[] {
                        new String[0]
                    });
                    executedOk = true;
                } catch (java.lang.reflect.InvocationTargetException ex) {
                    // canceled by user, all is fine
                    if (ex.getTargetException () instanceof org.openide.util.UserCancelException) {
                        executedOk = true;
                    } else {
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    // If exceptions are thrown, notify them - something is broken.
                    e.printStackTrace();
                } catch (LinkageError e) {
                    // These too...
                    e.printStackTrace();
                }
            }
            
            
            public boolean canContinue () {
                if (shouldDoAnImport ()) {
                    try {
                        SwingUtilities.invokeAndWait (this);
                        if (executedOk) {
                            // if the import went fine, then we are fine
                            // just create the file
                            installed.getParentFile ().mkdirs ();
                            installed.createNewFile ();
                            return true;
                        } else {
                            return false;
                        }
                    } catch (IOException ex) {
                        // file was not created a bit of problem but go on
                        ex.printStackTrace();
                        return true;
                    } catch (java.lang.reflect.InvocationTargetException ex) {
                        return false;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                } else {
                    // if there is no need to upgrade that every thing is good
                    return true;
                }
            }
        }
        
        
        ImportHandler handler = new ImportHandler ();
        
        return handler.canContinue ();
    }
    
    /** Displays license to user to accept if necessary. Made non-private just for testing purposes.
     *
     * @return true if the execution should continue or false if it should
     * stop
     */
    static boolean handleLicenseCheck () {
        class LicenseHandler implements Runnable {
            private String classname;
            private boolean executedOk; 
            
            /** Checks if licence was accepted already or not. */
            public boolean shouldDisplayLicense () {
                File f = InstalledFileLocator.getDefault().locate("var/license_accepted",null,false); // NOI18N
                if (f != null) {
                    return false;
                }
                classname = System.getProperty("netbeans.accept_license_class"); // NOI18N
                return (classname != null);
            }
            
            public void run() {
                // This module is included in our distro somewhere... may or may not be turned on.
                // Whatever - try running some classes from it anyway.
                try {
                    Class clazz = getKlass (classname);
                
                    Method showMethod = clazz.getMethod("showLicensePanel", new Class[] {}); // NOI18N
                    showMethod.invoke (null, new Object [] {});
                    executedOk = true;
                    //User accepted license => create file marker in userdir
                    File f = new File(new File(CLIOptions.getUserDir(), "var"), "license_accepted"); // NOI18N
                    if (!f.exists()) {
                        f.getParentFile().mkdirs();
                        try {
                            f.createNewFile();
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                } catch (java.lang.reflect.InvocationTargetException ex) {
                    // canceled by user, all is fine
                    if (ex.getTargetException() instanceof org.openide.util.UserCancelException) {
                        executedOk = false;
                    } else {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    // If exceptions are thrown, notify them - something is broken.
                    ex.printStackTrace();
                } catch (LinkageError ex) {
                    // These too...
                    ex.printStackTrace();
                }
            }
            
            public boolean canContinue () {
                if (shouldDisplayLicense()) {
                    try {
                        SwingUtilities.invokeAndWait(this);
                        if (executedOk) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (java.lang.reflect.InvocationTargetException ex) {
                        return false;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                } else {
                    // if there is no need to upgrade that every thing is good
                    return true;
                }
            }
        }
                
        LicenseHandler handler = new LicenseHandler ();
        
        return handler.canContinue ();
    }

    /** Reads system properties from a file on a disk and stores them 
     * in System.getPropeties ().
     */
    private static void readEnvMap () {
        java.util.Properties env = System.getProperties ();
        
	Map<String, String> m = System.getenv();
	for (Iterator<Map.Entry<String,String>> it = m.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry<String,String> entry = it.next();
	    String key = entry.getKey();
	    String value = entry.getValue();

	    env.put("Env-".concat(key), value); // NOI18N
	    // E.g. on Turkish Unix, want env-display not env-d\u0131splay:
	    env.put("env-".concat(key.toLowerCase(Locale.US)), value); // NOI18N
	}
    }

    
}
