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

package com.netbeans.developer.editors;

import java.awt.Rectangle;
import java.util.ResourceBundle;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
*
* @author   Ian Formanek
* @version  1.00, 01 Sep 1998
*/
public class RectangleCustomEditor extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {

  // the bundle to use
  static ResourceBundle bundle = NbBundle.getBundle (
    RectangleCustomEditor.class);

  /** Initializes the Form */
  public RectangleCustomEditor(RectangleEditor editor) {
    initComponents ();
    this.editor = editor;
    Rectangle rectangle = (Rectangle)editor.getValue ();
    xField.setText (""+rectangle.x);
    yField.setText (""+rectangle.y);
    widthField.setText (""+rectangle.width);
    heightField.setText (""+rectangle.height);

    setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5)));
    jPanel2.setBorder (new javax.swing.border.CompoundBorder (
      new javax.swing.border.TitledBorder (
        new javax.swing.border.EtchedBorder (), 
        " " + bundle.getString ("CTL_Rectangle") + " "), 
      new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5))));

    xLabel.setText (bundle.getString ("CTL_X"));
    yLabel.setText (bundle.getString ("CTL_Y"));
    widthLabel.setText (bundle.getString ("CTL_Width"));
    heightLabel.setText (bundle.getString ("CTL_Height"));
    HelpCtx.setHelpIDString (this, RectangleCustomEditor.class.getName ());
  }

  public java.awt.Dimension getPreferredSize () {
    return new java.awt.Dimension (280, 160);
  }

  public Object getPropertyValue () throws IllegalStateException {
    try {
      int x = Integer.parseInt (xField.getText ());
      int y = Integer.parseInt (yField.getText ());
      int width = Integer.parseInt (widthField.getText ());
      int height = Integer.parseInt (heightField.getText ());
      return new Rectangle (x, y, width, height);
    } catch (NumberFormatException e) {
      throw new IllegalStateException ();
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    setLayout (new java.awt.BorderLayout ());

    jPanel2 = new javax.swing.JPanel ();
    jPanel2.setLayout (new java.awt.GridBagLayout ());
    java.awt.GridBagConstraints gridBagConstraints1;

      xLabel = new javax.swing.JLabel ();
      xLabel.setText (null);

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add (xLabel, gridBagConstraints1);

      xField = new javax.swing.JTextField ();
      xField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateRectangle (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    jPanel2.add (xField, gridBagConstraints1);

      yLabel = new javax.swing.JLabel ();
      yLabel.setText (null);

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add (yLabel, gridBagConstraints1);

      yField = new javax.swing.JTextField ();
      yField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateRectangle (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    jPanel2.add (yField, gridBagConstraints1);

      widthLabel = new javax.swing.JLabel ();
      widthLabel.setText (null);

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add (widthLabel, gridBagConstraints1);

      widthField = new javax.swing.JTextField ();
      widthField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateRectangle (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    jPanel2.add (widthField, gridBagConstraints1);

      heightLabel = new javax.swing.JLabel ();
      heightLabel.setText (null);

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add (heightLabel, gridBagConstraints1);

      heightField = new javax.swing.JTextField ();
      heightField.addActionListener (new java.awt.event.ActionListener () {
          public void actionPerformed (java.awt.event.ActionEvent evt) {
            updateRectangle (evt);
          }
        }
      );

    gridBagConstraints1 = new java.awt.GridBagConstraints ();
    gridBagConstraints1.gridwidth = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
    gridBagConstraints1.weightx = 1.0;
    jPanel2.add (heightField, gridBagConstraints1);


    add (jPanel2, "Center");

  }//GEN-END:initComponents


  private void updateRectangle (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRectangle
    try {
      int x = Integer.parseInt (xField.getText ());
      int y = Integer.parseInt (yField.getText ());
      int width = Integer.parseInt (widthField.getText ());
      int height = Integer.parseInt (heightField.getText ());
      editor.setValue (new Rectangle (x, y, width, height));
    } catch (NumberFormatException e) {
      // [PENDING beep]
    }
  }//GEN-LAST:event_updateRectangle


// Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel2;
  private javax.swing.JLabel xLabel;
  private javax.swing.JTextField xField;
  private javax.swing.JLabel yLabel;
  private javax.swing.JTextField yField;
  private javax.swing.JLabel widthLabel;
  private javax.swing.JTextField widthField;
  private javax.swing.JLabel heightLabel;
  private javax.swing.JTextField heightField;
// End of variables declaration//GEN-END:variables

  private RectangleEditor editor;

}


/*
 * Log
 *  7    Gandalf   1.6         7/8/99   Jesse Glick     Context help.
 *  6    Gandalf   1.5         6/30/99  Ian Formanek    Reflecting changes in 
 *       editors packages and enhanced property editor interfaces
 *  5    Gandalf   1.4         6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  4    Gandalf   1.3         6/2/99   Ian Formanek    Fixed event handlers
 *  3    Gandalf   1.2         5/31/99  Ian Formanek    Updated to X2 format
 *  2    Gandalf   1.1         3/4/99   Jan Jancura     bundle moved
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
