/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy.explorer;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;

import java.awt.AWTEvent;
import java.awt.Component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Auxiliary class to find an event sequence which should be posted
 * to reproduce user actions.
 *	
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *
 */

public class TrialListenerManager implements Outputable {

    Component comp;
    TrialMouseListener mListener;
    TrialMouseMotionListener mmListener;
    TrialKeyListener kListener;
    TestOut output;

    /**
     * Contructor.
     * @param comp Component to display event sequence for.
     */
    public TrialListenerManager(Component comp) {
	this.comp = comp;
	mListener = new TrialMouseListener();
	mmListener = new TrialMouseMotionListener();
	kListener = new TrialKeyListener();
	output = JemmyProperties.getCurrentOutput();
    }

    /**
     * Sets output.
     * @param timeouts org.netbeans.jemmy.TestOut instance.
     * @see org.netbeans.jemmy.TestOut
     */
    public void setOutput(TestOut output) {
	this.output = output;
    }

    /**
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public TestOut getOutput() {
	return(output);
    }

    /**
     * Removes mouse listener.
     */
    public void removeMouseListener() {
	comp.removeMouseListener(mListener);
    }
    
    /**
     * Adds mouse listener.
     */
    public void addMouseListener() {
	removeMouseListener();
	comp.addMouseListener(mListener);
    }

    /**
     * Removes mouse motion listener.
     */
    public void removeMouseMotionListener() {
	comp.removeMouseMotionListener(mmListener);
    }
    
    /**
     * Adds mouse motion listener.
     */
    public void addMouseMotionListener() {
	removeMouseMotionListener();
	comp.addMouseMotionListener(mmListener);
    }

    /**
     * Removes key listener.
     */
    public void removeKeyListener() {
	comp.removeKeyListener(kListener);
    }
    
    /**
     * Adds key listener.
     */
    public void addKeyListener() {
	removeKeyListener();
	comp.addKeyListener(kListener);
    }

    void printEvent(AWTEvent e) {
	output.printLine(e.toString());
    }

    private class TrialMouseListener implements MouseListener {
	public void mouseClicked(MouseEvent e) {
	    printEvent(e);
	}
	public void mouseEntered(MouseEvent e) {
	    printEvent(e);
	}
	public void mouseExited(MouseEvent e) {
	    printEvent(e);
	}
	public void mousePressed(MouseEvent e) {
	    printEvent(e);
	}
	public void mouseReleased(MouseEvent e) {
	    printEvent(e);
	}
    }

    private class TrialMouseMotionListener implements MouseMotionListener {
	public void mouseDragged(MouseEvent e) {
	    printEvent(e);
	}
	public void mouseMoved(MouseEvent e) {
	    printEvent(e);
	}
    }

    private class TrialKeyListener implements KeyListener {
	public void keyPressed(KeyEvent e) {
	    printEvent(e);
	}
	public void keyReleased(KeyEvent e) {
	    printEvent(e);
	}
	public void keyTyped(KeyEvent e) {
	    printEvent(e);
	}
    }
}
