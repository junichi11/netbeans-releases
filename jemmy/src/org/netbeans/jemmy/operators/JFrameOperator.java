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

package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.FrameWaiter;

import java.awt.Component;
import java.awt.Container;

import javax.accessibility.AccessibleContext;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;

/**
 * <BR><BR>Timeouts used: <BR>
 * FrameWaiter.WaitFrameTimeout - time to wait frame displayed <BR>
 * FrameWaiter.AfterFrameTimeout - time to sleep after frame has been dispayed <BR>
 *
 * @see org.netbeans.jemmy.Timeouts
 * 
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class JFrameOperator extends FrameOperator {

    /**
     * Constructor.
     */
    public JFrameOperator(JFrame w) {
	super(w);
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Constructor can be used in complicated cases when
     * output or timeouts should differ from default.
     * @param title
     * @param index
     * @param timeouts 
     * @param output
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JFrameOperator(String title, int index, Operator env) {
	this((JFrame)waitFrame(new JFrameSubChooser(new FrameByTitleChooser(title, 
									    env.getComparator())),
			       index,
			       env.getTimeouts(),
			       env.getOutput()));
	copyEnvironment(env);
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Uses current timeouts and output values.
     * @param title
     * @param index
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @throws TimeoutExpiredException
     */
    public JFrameOperator(String title, int index) {
	this(title, index,
	     ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Constructor.
     * Waits for the frame with "title" subtitle.
     * Uses current timeouts and output values.
     * @param title
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see JemmyProperties#getCurrentTimeouts()
     * @see JemmyProperties#getCurrentOutput()
     * @throws TimeoutExpiredException
     */
    public JFrameOperator(String title) {
	this(title, 0);
    }

    /**
     * Constructor.
     * Waits for the index'th frame.
     * Uses current timeout and output for waiting and to init operator.
     * @param index Ordinal component index.
     * @throws TimeoutExpiredException
     */
    public JFrameOperator(int index) {
	this((JFrame)
	     (JFrame)waitFrame(new JFrameSubChooser(ComponentSearcher.
						    getTrueChooser("Any JFrame")),
			       index,
			       ComponentOperator.getEnvironmentOperator().getTimeouts(),
			       ComponentOperator.getEnvironmentOperator().getOutput()));
	copyEnvironment(ComponentOperator.getEnvironmentOperator());
    }

    /**
     * Constructor.
     * Waits for the first frame.
     * Uses current timeout and output for waiting and to init operator.
     * @throws TimeoutExpiredException
     */
    public JFrameOperator() {
	this(0);
    }

    /**
     * Searches an index'th frame.
     */
    public static JFrame findJFrame(ComponentChooser chooser, int index) {
	return((JFrame)FrameWaiter.getFrame(new JFrameSubChooser(chooser), index));
    }

    /**
     * Searches a frame.
     */
    public static JFrame findJFrame(ComponentChooser chooser) {
	return(findJFrame(chooser, 0));
    }

    /**
     * Searches an index'th frame by title.
     * @param title Frame title
     */
    public static JFrame findJFrame(String title, boolean ce, boolean cc, int index) {
	return((JFrame)FrameWaiter.
	       getFrame(new JFrameSubChooser(new FrameByTitleChooser(title, 
								     new DefaultStringComparator(ce, cc))), 
			index));
    }

    /**
     * Searches a frame by title.
     * @param title Frame title
     */
    public static JFrame findJFrame(String title, boolean ce, boolean cc) {
	return(findJFrame(title, ce, cc, 0));
    }

    /**
     * Waits an index'th frame.
     * @throws TimeoutExpiredException
     */
    public static JFrame waitJFrame(ComponentChooser chooser, int index) {
	return((JFrame)waitFrame(new JFrameSubChooser(chooser), index,
				 JemmyProperties.getCurrentTimeouts(),
				 JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits a frame.
     * @throws TimeoutExpiredException
     */
    public static JFrame waitJFrame(ComponentChooser chooser) {
	return(waitJFrame(chooser, 0));
    }

    /**
     * Waits an index'th frame by title.
     * @param title Frame title
     * @throws TimeoutExpiredException
     */
    public static JFrame waitJFrame(String title, boolean ce, boolean cc, int index) {
	try {
	    return((JFrame)(new FrameWaiter()).
		   waitFrame(new JFrameSubChooser(new 
						  FrameByTitleChooser(title, 
								      new DefaultStringComparator(ce, cc))), 
			     index));
	} catch(InterruptedException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	    return(null);
	}
    }

    /**
     * Waits a frame by title.
     * @param title Frame title
     * @throws TimeoutExpiredException
     */
    public static JFrame waitJFrame(String title, boolean ce, boolean cc) {
	return(waitJFrame(title, ce, cc, 0));
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>JFrame.getAccessibleContext()</code> through queue*/
    public AccessibleContext getAccessibleContext() {
	return((AccessibleContext)runMapping(new MapAction("getAccessibleContext") {
		public Object map() {
		    return(((JFrame)getSource()).getAccessibleContext());
		}}));}

    /**Maps <code>JFrame.getContentPane()</code> through queue*/
    public Container getContentPane() {
	return((Container)runMapping(new MapAction("getContentPane") {
		public Object map() {
		    return(((JFrame)getSource()).getContentPane());
		}}));}

    /**Maps <code>JFrame.getDefaultCloseOperation()</code> through queue*/
    public int getDefaultCloseOperation() {
	return(runMapping(new MapIntegerAction("getDefaultCloseOperation") {
		public int map() {
		    return(((JFrame)getSource()).getDefaultCloseOperation());
		}}));}

    /**Maps <code>JFrame.getGlassPane()</code> through queue*/
    public Component getGlassPane() {
	return((Component)runMapping(new MapAction("getGlassPane") {
		public Object map() {
		    return(((JFrame)getSource()).getGlassPane());
		}}));}

    /**Maps <code>JFrame.getJMenuBar()</code> through queue*/
    public JMenuBar getJMenuBar() {
	return((JMenuBar)runMapping(new MapAction("getJMenuBar") {
		public Object map() {
		    return(((JFrame)getSource()).getJMenuBar());
		}}));}

    /**Maps <code>JFrame.getLayeredPane()</code> through queue*/
    public JLayeredPane getLayeredPane() {
	return((JLayeredPane)runMapping(new MapAction("getLayeredPane") {
		public Object map() {
		    return(((JFrame)getSource()).getLayeredPane());
		}}));}

    /**Maps <code>JFrame.getRootPane()</code> through queue*/
    public JRootPane getRootPane() {
	return((JRootPane)runMapping(new MapAction("getRootPane") {
		public Object map() {
		    return(((JFrame)getSource()).getRootPane());
		}}));}

    /**Maps <code>JFrame.setContentPane(Container)</code> through queue*/
    public void setContentPane(final Container container) {
	runMapping(new MapVoidAction("setContentPane") {
		public void map() {
		    ((JFrame)getSource()).setContentPane(container);
		}});}

    /**Maps <code>JFrame.setDefaultCloseOperation(int)</code> through queue*/
    public void setDefaultCloseOperation(final int i) {
	runMapping(new MapVoidAction("setDefaultCloseOperation") {
		public void map() {
		    ((JFrame)getSource()).setDefaultCloseOperation(i);
		}});}

    /**Maps <code>JFrame.setGlassPane(Component)</code> through queue*/
    public void setGlassPane(final Component component) {
	runMapping(new MapVoidAction("setGlassPane") {
		public void map() {
		    ((JFrame)getSource()).setGlassPane(component);
		}});}

    /**Maps <code>JFrame.setJMenuBar(JMenuBar)</code> through queue*/
    public void setJMenuBar(final JMenuBar jMenuBar) {
	runMapping(new MapVoidAction("setJMenuBar") {
		public void map() {
		    ((JFrame)getSource()).setJMenuBar(jMenuBar);
		}});}

    /**Maps <code>JFrame.setLayeredPane(JLayeredPane)</code> through queue*/
    public void setLayeredPane(final JLayeredPane jLayeredPane) {
	runMapping(new MapVoidAction("setLayeredPane") {
		public void map() {
		    ((JFrame)getSource()).setLayeredPane(jLayeredPane);
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    private static class JFrameSubChooser implements ComponentChooser {
	private ComponentChooser chooser;
	public JFrameSubChooser(ComponentChooser c) {
	    super();
	    chooser = c;
	}
	public boolean checkComponent(Component comp) {
	    if(comp instanceof JFrame) {
		return(chooser.checkComponent(comp));
	    } else {
		return(false);
	    }
	}
	public String getDescription() {
	    return(chooser.getDescription());
	}
    }
}
