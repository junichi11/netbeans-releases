/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.javadoc;
 
import java.io.File;

import java.util.Enumeration;
import java.lang.reflect.Method;
import javax.swing.event.*;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

import org.openide.util.Utilities;
import org.openide.src.nodes.FilterFactory;
import org.openide.actions.CutAction;
import org.openide.util.actions.SystemAction;
import org.openide.modules.ModuleInstall;
import org.openide.TopManager;
import org.openide.filesystems.*;
import org.openide.loaders.*;

import com.netbeans.developer.modules.javadoc.settings.StdDocletSettings;
import com.netbeans.developer.modules.javadoc.comments.JavaDocPropertySupportFactory;
import com.netbeans.developer.modules.javadoc.search.SearchDocAction;
import com.netbeans.developer.modules.javadoc.search.DocFileSystem;

/** Class for initializing Javadoc module on IDE startup.

 @author Petr Hrebejk
*/
public class JavadocModule extends ModuleInstall {
  
  private static int numberOfStarts = 0;
  
  /** By first install of module in the IDE, check whether standard documentation folder
  * exists. If not creates it.
  */
  public void installed() {
    // Install Search Action

    try {
      createFirstAction (SearchDocAction.class, 
        DataFolder.create (org.openide.TopManager.getDefault ().getPlaces ().folders().menus (), "Help") );
    } 
    catch (Exception e) {
      if (System.getProperty ("netbeans.debug.exceptions") != null) {
        e.printStackTrace ();
      }
      // ignore failure to install
    }

    restored();
  }

  /** By uninstalling module from the IDE do nothing. 
  */
  public void uninstalled () {

    // Remove doc search action
    try {
      removeAction (SearchDocAction.class, DataFolder.create (org.openide.TopManager.getDefault ().getPlaces ().folders().menus (), "Help"));
    } 
    catch (Exception e) {
      if (System.getProperty ("netbeans.debug.exceptions") != null) {
        e.printStackTrace ();
      }
    } 
  }
  
  /** Called on IDE startup. Registers actions for generating documentation 
  * on DataFolder and JavaDataObject.
  */
  public void restored() {
    
    numberOfStarts ++;
        
    if ( numberOfStarts < 3 ) {
      /* 
       * This works only on the first start when called from
       * install method or on second start - but only if doesn't
       * exist the project.basic & project.last file int system
       * directory. If these files exist on second start mounted
       * filesystems are overriden by project settings.
       */
      installJavadocDirectories();
    }
      
    // Install the factory for adding JavaDoc property to nodes
    invokeDynamic( "com.netbeans.developer.modules.loaders.java.JavaDataObject",
                   "addExplorerFilterFactory",
                   new JavaDocPropertySupportFactory() );
    invokeDynamic( "com.netbeans.developer.modules.loaders.java.JavaDataObject",
                   "addBrowserFilterFactory",
                   new JavaDocPropertySupportFactory() );
    
    // Assign the Ctrl+F1 to JavaDoc Index Search Action

    Keymap map = TopManager.getDefault ().getGlobalKeymap ();
    try {
      assign ("C-F1", "com.netbeans.developer.modules.javadoc.search.SearchDocAction", map);
    } catch (ClassNotFoundException e) {
      // print and go on
      e.printStackTrace();
    }
  }
	
  // UTILITY METHODS ----------------------------------------------------------------------

  private void createFirstAction ( Class actionClass, DataFolder folder )
  throws java.io.IOException {
    String actionShortName = Utilities.getShortClassName (actionClass);
    String actionName = actionClass.getName ();

    if (InstanceDataObject.find (folder, actionShortName, actionName) != null) return;  

    DataObject[] children = folder.getChildren ();
    DataObject[] newOrder = new DataObject [children.length + 2 ];
    
    System.arraycopy (children, 0, newOrder, 2, children.length );
    InstanceDataObject actionInstance = InstanceDataObject.create (folder, actionShortName, actionName);
    InstanceDataObject afterSeparator = InstanceDataObject.create (folder, "Separator2-"+actionShortName, "javax.swing.JSeparator");

    newOrder[0] = actionInstance;
    newOrder[1] = afterSeparator;

    folder.setOrder (newOrder);

  }

 
  private void removeAction (Class actionClass, DataFolder folder) throws java.io.IOException {
    String actionShortName = Utilities.getShortClassName (actionClass);
    InstanceDataObject.remove (folder, actionShortName, actionClass.getName ());
    try {
      InstanceDataObject.remove (folder, "Separator1-"+actionShortName, "javax.swing.JSeparator");
      InstanceDataObject.remove (folder, "Separator2-"+actionShortName, "javax.swing.JSeparator");
    } catch (Exception e) {
      // these do not have to exist, wo we will catch the exception silently
    }
  }

  

  /** Assigns a key to an action
  * @param key key name
  * @param action name of the action
  */
  private static void assign (String key, String action, Keymap map) throws ClassNotFoundException {
    KeyStroke str = Utilities.stringToKey (key);
    if (str == null) {
      System.err.println ("Not a valid key: " + key);
      // go on
      return;
    }

    Class actionClass = Class.forName (action);

    // create instance of the action
    SystemAction a = SystemAction.get (actionClass);
  
    map.addActionForKeyStroke (str, a);
    a.setEnabled( true );
  }

  /** Dynamicaly invokes a method
   */
  private void invokeDynamic( String className, String methodName, FilterFactory factory ) {  

    try {
      Class dataObject = TopManager.getDefault().systemClassLoader().loadClass( className );
    
      if ( dataObject == null )
        return;

      Method method = dataObject.getDeclaredMethod( methodName, new Class[] { FilterFactory.class }  );
      if ( method == null )
        return;
        
      method.invoke( null, new Object[] { factory } );
    }
    catch ( java.lang.ClassNotFoundException e ) { 
    }
    catch ( java.lang.NoSuchMethodException e ) {  
    }
    catch ( java.lang.IllegalAccessException e ) {
    }
    catch ( java.lang.reflect.InvocationTargetException e ) {
    }
  }
  
  /** Tries to find standard Javadoc directory, open-api javadoc directrory
   * and directroy for javadoc output and mounts it into javadoc repository
   */
  
  void installJavadocDirectories() {

    // Try to find Java documantation 
    
    File jdkDocsDir = new File ( System.getProperty ("java.home")  + java.io.File.separator + ".." 
                                 + java.io.File.separator + "docs" );
    mount( jdkDocsDir, true );
    
    // Try to find NetBeans open-api documentation
        
    File apiDocsDir = new File ( System.getProperty ("netbeans.user")  + java.io.File.separator + "docs" 
                                 + java.io.File.separator + "openide-api" );
    mount( apiDocsDir, true );
    
    // Create default directory for JavaDoc
    StdDocletSettings sdsTemp = new StdDocletSettings();
    File jdOutputDir = sdsTemp.getDirectory();
    
    if ( !jdOutputDir.isDirectory() ) 
      jdOutputDir.mkdirs();
    mount( jdOutputDir, false );    
    
  }
  
  
  /** Method finds out wether directory exists and whether it is
   *  a searchable javadoc directory if so mounts the directory
   *  into Javadoc repository
   */
  void mount( File root, boolean testSearchability ) {

    
    if ((root != null) && (root.isDirectory())) {
      String dirName = root.getAbsolutePath();

      FileSystemCapability.Bean cap = new FileSystemCapability.Bean();
      cap.setCompile( false );
      cap.setExecute( false );
      cap.setDebug( false );
      cap.setDoc( true );

      LocalFileSystem localFS = new LocalFileSystem( cap );
      localFS.setHidden( true );

      try {          
        localFS.setRootDirectory (new File (dirName));
        Repository r = TopManager.getDefault ().getRepository ();

        if (r.findFileSystem(localFS.getSystemName()) == null) {

          if( !testSearchability ||
              DocFileSystem.getDocFileObject( localFS ) != null ) {  
            r.addFileSystem (localFS);
          }  
        }
      } 
      catch (java.io.IOException ex) {} 
      catch (java.beans.PropertyVetoException ex) {}
    }
  }

  // Implementation of java.io.Externalizable ------------------
  
  public void readExternal(final java.io.ObjectInput objectInput ) 
              throws java.io.IOException, java.lang.ClassNotFoundException {

    super.readExternal( objectInput );
        
    numberOfStarts = objectInput.readInt();
    
  }
  
  public void writeExternal(final java.io.ObjectOutput objectOutput ) 
              throws java.io.IOException {
    super.writeExternal( objectOutput );
    
    objectOutput.writeInt( numberOfStarts );
  }
}

/* 
 * Log
 *  19   Gandalf   1.18        10/27/99 Petr Hrebejk    Bug fixes & back button 
 *       in Javadoc Quickview
 *  18   Gandalf   1.17        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  17   Gandalf   1.16        10/7/99  Petr Hrebejk    Module Externalization 
 *       fix
 *  16   Gandalf   1.15        10/1/99  Petr Hrebejk    org.openide.modules.ModuleInstall
 *        changed to class + some methods added
 *  15   Gandalf   1.14        8/13/99  Petr Hrebejk    Initialization of JDoc 
 *       repository on first and second start added
 *  14   Gandalf   1.13        7/21/99  Petr Hrebejk    Action installation fix
 *  13   Gandalf   1.12        7/20/99  Petr Hrebejk    Action installation 
 *       added
 *  12   Gandalf   1.11        7/9/99   Petr Hrebejk    JavaDoc comments support
 *       added to module
 *  11   Gandalf   1.10        6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  10   Gandalf   1.9         6/8/99   Petr Hrebejk    
 *  9    Gandalf   1.8         5/27/99  Petr Hrebejk    
 *  8    Gandalf   1.7         5/16/99  Petr Hrebejk    
 *  7    Gandalf   1.6         5/14/99  Petr Hrebejk    
 *  6    Gandalf   1.5         5/11/99  Petr Hrebejk    
 *  5    Gandalf   1.4         5/11/99  Petr Hrebejk    
 *  4    Gandalf   1.3         5/7/99   Petr Hrebejk    
 *  3    Gandalf   1.2         4/27/99  Petr Hrebejk    GenerateDocAction for 
 *       all producersOf JavaDataObjects
 *  2    Gandalf   1.1         4/23/99  Petr Hrebejk    
 *  1    Gandalf   1.0         4/23/99  Petr Hrebejk    
 * $ 
 */ 