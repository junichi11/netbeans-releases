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

package com.netbeans.developer.modules.loaders.image;

import java.awt.Dimension;

import org.openide.text.EditorSupport;
import org.openide.util.HelpCtx;
import org.openide.windows.*;

/** Top component providing a viewer for images.
*
* @author Petr Hamernik, Ian Formanek
*/
public class ImageViewer extends CloneableTopComponent {
  private static final int MINIMUM_WIDTH = 200;
  private static final int MINIMUM_HEIGHT = 150;

  private static final int DEFAULT_BORDER_WIDTH = 40;
  private static final int DEFAULT_BORDER_HEIGHT = 40;

  private ImageDataObject storedObject;
  private javax.swing.JLabel label;

  static final long serialVersionUID =6960127954234034486L;
  /** Create a new image viewer.
  * @param obj the data object holding the image
  */
  public ImageViewer(ImageDataObject obj) {
    super(obj);
    storedObject = obj;
    javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(label = new javax.swing.JLabel(new NBImageIcon(obj)));
    setLayout(new java.awt.BorderLayout());
    add(scroll, "Center");
  }

  /** Show the component on given workspace. If given workspace is
  * not active, component will be shown only after given workspace
  * will become visible.
  * Note that this method only makes it visible, but does not
  * give it focus.
  * @param workspace Workspace on which component should be opened.
  * @see #requestFocus
  */
  public void open (Workspace w) {
    if (w == null) w = org.openide.TopManager.getDefault ().getWindowManager ().getCurrentWorkspace ();
    Mode viewerMode = w.findMode(this);
    if (viewerMode == null) {
      Mode editorMode = w.findMode(EditorSupport.EDITOR_MODE);
      if (editorMode != null) editorMode.dockInto(this);
    }
    super.open (w);
  }
  
  public Dimension getPreferredSize () {
    Dimension pref = label.getPreferredSize ();
    return new Dimension (Math.max (DEFAULT_BORDER_WIDTH + pref.width, MINIMUM_WIDTH), Math.max (DEFAULT_BORDER_HEIGHT + pref.height, MINIMUM_HEIGHT));
  }
  
  public HelpCtx getHelpCtx () {
    return new HelpCtx(ImageViewer.class);
  }

  // Cloning the viewer uses the same underlying data object.
  protected CloneableTopComponent createClonedObject () {
    return new ImageViewer(storedObject);
  }

}

/*
 * Log
 *  13   Gandalf   1.12        11/27/99 Patrik Knakal   
 *  12   Gandalf   1.11        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  11   Gandalf   1.10        10/9/99  Ian Formanek    Fixed bug 4309 - opening
 *       an image file throws exception
 *  10   Gandalf   1.9         10/8/99  Ian Formanek    Removed debug printlns
 *  9    Gandalf   1.8         9/13/99  Ian Formanek    Fixed bug 3671 - Image 
 *       Viewer  window is opened much to small.
 *  8    Gandalf   1.7         7/20/99  Jesse Glick     Context help (window 
 *       system changes).
 *  7    Gandalf   1.6         6/24/99  Jesse Glick     Gosh-honest HelpID's.
 *  6    Gandalf   1.5         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  5    Gandalf   1.4         4/13/99  Jesse Glick     Clean-ups of comments 
 *       and such for public perusal.
 *  4    Gandalf   1.3         1/7/99   Jaroslav Tulach Uses OpenSupport
 *  3    Gandalf   1.2         1/7/99   Ian Formanek    
 *  2    Gandalf   1.1         1/7/99   Ian Formanek    
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
