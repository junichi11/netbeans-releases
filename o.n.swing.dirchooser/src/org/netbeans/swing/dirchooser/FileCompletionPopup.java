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
 *
 * Contributor(s): Soot Phengsy
 */

package org.netbeans.swing.dirchooser;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * A class that handles the "File Name:" text field auto-completion drop-down selection list.
 *
 * @author Soot Phengsy
 */
public class FileCompletionPopup extends JPopupMenu {
    
    private JList list;
    private JTextField textField;
    private JFileChooser chooser;
    
    public FileCompletionPopup(JFileChooser chooser, JTextField textField, Vector<File> files) {
        this.list = new JList(files);
        this.textField = textField;
        this.chooser = chooser;
        list.setVisibleRowCount(4);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane jsp = new JScrollPane(list);
        add(jsp);
        
        list.setFocusable(false);
        jsp.setFocusable(false);
        setFocusable(false);
        
        list.addFocusListener(new FocusHandler());
        list.addMouseListener(new MouseHandler());
        list.addMouseMotionListener(new MouseHandler());
        
        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "KeyDown");
        textField.getActionMap().put("KeyDown", new KeyHandler(KeyEvent.VK_DOWN));
        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "KeyUp");
        textField.getActionMap().put("KeyUp", new KeyHandler(KeyEvent.VK_UP));
        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "KeyEnter");
        textField.getActionMap().put("KeyEnter", new KeyHandler(KeyEvent.VK_ENTER));
        textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "KeyEscape");
        textField.getActionMap().put("KeyEscape", new KeyHandler(KeyEvent.VK_ESCAPE));
    }
     
    public void setDataList(Vector files) {
        list.setListData(files);
    }
    
    private void setSelectNext() {
        if (list.getModel().getSize() > 0) {
            int cur = (list.getSelectedIndex() + 1) % list.getModel().getSize();
            list.setSelectedIndex(cur);
            list.ensureIndexIsVisible(cur);
        }
    }
    
    private void setSelectPrevious() {
        if (list.getModel().getSize() > 0) {
            int cur = (list.getSelectedIndex() == -1) ? 0
                    : list.getSelectedIndex();
            cur = (cur == 0) ? list.getModel().getSize() - 1 : cur - 1;
            list.setSelectedIndex(cur);
            list.ensureIndexIsVisible(cur);
        }
    }
    
    public void showPopup(JTextComponent source, int x, int y) {
        if(list.getModel().getSize() == 0) {
            return;
        }
        setPreferredSize(new Dimension(source.getWidth(), source.getHeight() * 4));
        show(source,  x, y);
        setVisible(true);
    }
    
    private class KeyHandler extends AbstractAction {
        final int code;
        KeyHandler(int keyCode) {
            this.code = keyCode;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (isVisible()) {
                if (code == KeyEvent.VK_DOWN) {
                    setSelectNext();
                } else if (code == KeyEvent.VK_UP) {
                    setSelectPrevious();
                } else if (code == KeyEvent.VK_ENTER) {
                    File file = (File)list.getSelectedValue();
                    if(file != null) { 
                        if(file.equals(chooser.getCurrentDirectory())) {
                            chooser.firePropertyChange(JFileChooser.DIRECTORY_CHANGED_PROPERTY, false, true);
                        } else {
                            chooser.setSelectedFiles(new File[] {file});
                            chooser.setCurrentDirectory(file);
                        }
                    }
                    setVisible(false);
                    textField.requestFocus();
                } else if (code == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                    textField.requestFocus();
                }
            }
        }
    }
    
    private class FocusHandler extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (!e.isTemporary()) {
                setVisible(false);
                textField.requestFocus();
            }
        }
    }
    
    private class MouseHandler extends MouseAdapter implements MouseMotionListener{
        public void mouseMoved(MouseEvent e) {
            if (e.getSource() == list) {
                Point location = e.getPoint();
                int index = list.locationToIndex(location);
                Rectangle r = new Rectangle();
                list.computeVisibleRect( r );
                if ( r.contains( location ) ) {
                    list.setSelectedIndex(index);
                }
            }
        }
        
        public void mouseDragged(MouseEvent e) {
            if (e.getSource() == list) {
                return;
            }
            if ( isVisible() ) {
                MouseEvent newEvent = convertMouseEvent( e );
                Rectangle r = new Rectangle();
                list.computeVisibleRect( r );
                Point location =  newEvent.getPoint();
                int index = list.locationToIndex(location);
                if ( r.contains( location ) ) {
                    list.setSelectedIndex(index);
                }
            }
        }
        
        public void mouseClicked(MouseEvent e) {
            Point p = e.getPoint();
            int index = list.locationToIndex(p);
            list.setSelectedIndex(index);
            setVisible(false);
            File file = (File)list.getSelectedValue();
            if(file.equals(chooser.getCurrentDirectory())) {
                chooser.firePropertyChange(JFileChooser.DIRECTORY_CHANGED_PROPERTY, false, true);
            } else {
                chooser.setCurrentDirectory(file);
            }
            textField.requestFocus();
        }
        
        private MouseEvent convertMouseEvent( MouseEvent e ) {
            Point convertedPoint = SwingUtilities.convertPoint( (Component)e.getSource(),
                    e.getPoint(), list );
            MouseEvent newEvent = new MouseEvent( (Component)e.getSource(),
                    e.getID(),
                    e.getWhen(),
                    e.getModifiers(),
                    convertedPoint.x,
                    convertedPoint.y,
                    e.getClickCount(),
                    e.isPopupTrigger() );
            return newEvent;
        }
    }
}
