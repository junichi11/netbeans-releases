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
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.Waitable;
import org.netbeans.jemmy.Waiter;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.event.KeyEvent;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;

import javax.swing.plaf.TreeUI;

import javax.swing.text.JTextComponent;

import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * <BR><BR>Timeouts used: <BR>
 * JTreeOperator.WaitNodeExpandedTimeout - time to wait node expanded <BR>
 * JTreeOperator.WaitNodeCollapsedTimeout - time to wait node collapsed <BR>
 * JTreeOperator.WaitAfterNodeExpandedTimeout - time to to sleep after node expanded <BR>
 * JTreeOperator.WaitNextNodeTimeout - time to wait next node displayed <BR>
 * JTreeOperator.WaitNodeVisibleTimeout - time to wait node visible <BR>
 * JTreeOperator.BeforeEditTimeout - time to sleep before edit click <BR>
 * JTreeOperator.WaitEditingTimeout - time to wait node editing <BR>
 * ComponentOperator.WaitComponentTimeout - time to wait component displayed <BR>
 * WindowWaiter.WaitWindowTimeout - time to wait popup window displayed <BR>
 * JScrollBarOperator.WholeScrollTimeout - time for the whole scrolling <BR>
 *
 * @see org.netbeans.jemmy.Timeouts
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 *	
 */

public class JTreeOperator extends JComponentOperator
    implements Timeoutable, Outputable{

    private final static long WAIT_NODE_EXPANDED_TIMEOUT = 10000;
    private final static long WAIT_NODE_COLLAPSED_TIMEOUT = 10000;
    private final static long WAIT_AFTER_NODE_EXPANDED_TIMEOUT = 0;
    private final static long WAIT_NEXT_NODE_TIMEOUT = 10000;
    private final static long WAIT_NODE_VISIBLE_TIMEOUT = 10000;
    private final static long BEFORE_EDIT_TIMEOUT = 1000;
    private final static long WAIT_EDITING_TIMEOUT = 10000;

    private TestOut output;
    private Timeouts timeouts;
    private CellEditor editor;

    /**
     * Constructor.
     */
    public JTreeOperator(JTree b) {
	super(b);
	setTreeCellEditor(new DefaultCellEditor());
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param text Text of a row which is currently selected. 
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTreeOperator(ContainerOperator cont, String text, int row, int index) {
	this((JTree)waitComponent(cont, 
				  new JTreeByItemFinder(text, row,
							cont.getComparator()),
				  index));
	copyEnvironment(cont);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param text Text of a row which is currently selected. 
     * @param index Ordinal component index.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTreeOperator(ContainerOperator cont, String text, int index) {
	this(cont, text, -1, index);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param text Text of a row which is currently selected. 
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public JTreeOperator(ContainerOperator cont, String text) {
	this(cont, text, 0);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param index Ordinal component index.
     * @throws TimeoutExpiredException
     */
    public JTreeOperator(ContainerOperator cont, int index) {
	this((JTree)
	     waitComponent(cont, 
			   new JTreeFinder(ComponentSearcher.
					   getTrueChooser("Any JTree")),
			   index));
	copyEnvironment(cont);
    }

    /**
     * Constructor.
     * Waits component in container first.
     * Uses cont's timeout and output for waiting and to init operator.
     * @throws TimeoutExpiredException
     */
    public JTreeOperator(ContainerOperator cont) {
	this(cont, 0);
    }

    /**
     * Searches JTree in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JTree instance or null if component was not found.
     */
    public static JTree findJTree(Container cont, ComponentChooser chooser, int index) {
	return((JTree)findComponent(cont, new JTreeFinder(chooser), index));
    }

    /**
     * Searches 0'th JTree in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JTree instance or null if component was not found.
     */
    public static JTree findJTree(Container cont, ComponentChooser chooser) {
	return(findJTree(cont, chooser, 0));
    }

    /**
     * Searches JTree by item.
     * @param cont Container to search component in.
     * @param text Item text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param rowIndex Index of row to compare text. If -1, selected row is checked.
     * @param index Ordinal component index.
     * @return JTree instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTree findJTree(Container cont, String text, boolean ce, boolean ccs, int rowIndex, int index) {
	return(findJTree(cont, new JTreeByItemFinder(text, rowIndex, new DefaultStringComparator(ce, ccs)), index));
    }

    /**
     * Searches JTree by item.
     * @param cont Container to search component in.
     * @param text Item text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param rowIndex Index of row to compare text. If -1, selected row is checked.
     * @return JTree instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     */
    public static JTree findJTree(Container cont, String text, boolean ce, boolean ccs, int rowIndex) {
	return(findJTree(cont, text, ce, ccs, rowIndex, 0));
    }

    /**
     * Waits JTree in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return JTree instance or null if component was not found.
     * @throws TimeoutExpiredException
     */
    public static JTree waitJTree(Container cont, ComponentChooser chooser, int index) {
	return((JTree)waitComponent(cont, new JTreeFinder(chooser), index));
    }

    /**
     * Waits 0'th JTree in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return JTree instance or null if component was not found.
     * @throws TimeoutExpiredException
     */
    public static JTree waitJTree(Container cont, ComponentChooser chooser) {
	return(waitJTree(cont, chooser, 0));
    }

    /**
     * Waits JTree by item.
     * @param cont Container to search component in.
     * @param text Item text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param rowIndex Index of row to compare text. If -1, selected row is checked.
     * @param index Ordinal component index.
     * @return JTree instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JTree waitJTree(Container cont, String text, boolean ce, boolean ccs, int rowIndex, int index) {
	return(waitJTree(cont, new JTreeByItemFinder(text, rowIndex, new DefaultStringComparator(ce, ccs)), index));
    }

    /**
     * Waits JTree by item.
     * @param cont Container to search component in.
     * @param text Item text. If null, contents is not checked.
     * @param ce Compare text exactly.
     * @param ccs Compare text case sensitively.
     * @param rowIndex Index of row to compare text. If -1, selected row is checked.
     * @return JTree instance or null if component was not found.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public static JTree waitJTree(Container cont, String text, boolean ce, boolean ccs, int rowIndex) {
	return(waitJTree(cont, text, ce, ccs, rowIndex, 0));
    }

    static {
	Timeouts.initDefault("JTreeOperator.WaitNodeExpandedTimeout", WAIT_NODE_EXPANDED_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.WaitNodeCollapsedTimeout", WAIT_NODE_COLLAPSED_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.WaitAfterNodeExpandedTimeout", WAIT_AFTER_NODE_EXPANDED_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.WaitNextNodeTimeout", WAIT_NEXT_NODE_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.WaitNodeVisibleTimeout", WAIT_NODE_VISIBLE_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.BeforeEditTimeout", BEFORE_EDIT_TIMEOUT);
	Timeouts.initDefault("JTreeOperator.WaitEditingTimeout", WAIT_EDITING_TIMEOUT);
    }

    /**
     * Defines current timeouts.
     * @param times A collection of timeout assignments.
     * @see org.netbeans.jemmy.Timeoutable
     * @see org.netbeans.jemmy.Timeouts
     */
    public void setTimeouts(Timeouts times) {
	this.timeouts = times;
	super.setTimeouts(timeouts);
    }

    /**
     * Return current timeouts.
     * @return the collection of current timeout assignments.
     * @see org.netbeans.jemmy.Timeoutable
     * @see org.netbeans.jemmy.Timeouts
     */
    public Timeouts getTimeouts() {
	return(timeouts);
    }

    /**
     * Defines print output streams or writers.
     * @param out Identify the streams or writers used for print output.
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public void setOutput(TestOut out) {
	output = out;
	super.setOutput(output.createErrorOutput());
    }

    /**
     * Returns print output streams or writers.
     * @return an object that contains references to objects for
     * printing to output and err streams.
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public TestOut getOutput() {
	return(output);
    }

    /**
     * @see JTreeOperator.CellEditor
     */
    public void setTreeCellEditor(CellEditor editor) {
	this.editor = editor;
    }

    /**
     * @see JTreeOperator.CellEditor
     */
    public CellEditor getTreeCellEditor() {
	return(editor);
    }

    /**
     * Expands path.
     * @throws TimeoutExpiredException
     */
    public void doExpandPath(TreePath path) {
	Object[] params = {path};
	Class[] paramClasses = {path.getClass()};
	output.printLine("Expanding \"" + path.getPathComponent(path.getPathCount() - 1).toString() +
			 "\" node");
	output.printGolden("Expanding \"" + path.getPathComponent(path.getPathCount() - 1).toString() +
			 "\" node");
	getEventDispatcher().setOutput(output.createErrorOutput());
	if(!isExpanded(path)) {
	    if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) == 0) {
		getEventDispatcher().invokeExistingMethod("fireTreeWillExpand", params, paramClasses, output);
		getEventDispatcher().invokeExistingMethod("expandPath", params, paramClasses, output);
		getEventDispatcher().invokeExistingMethod("fireTreeExpanded", params, paramClasses, output);
	    } else {
		Point point = getPointToClick(path);
		clickMouse((int)point.getX(), (int)point.getY(), 2);
	    }
	}
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", 
			 timeouts.getTimeout("JTreeOperator.WaitNodeExpandedTimeout"));
	times.setTimeout("Waiter.AfterWaitingTime", 
			 timeouts.getTimeout("JTreeOperator.WaitAfterNodeExpandedTimeout"));
	Waiter expandedWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		if(isExpanded(((TreePath)obj))) {
		    return("OK");
		} else {
		    return(null);
		}
	    }
	    public String getDescription() {
		return("Wait node expanded");
	    }
	});
	expandedWaiter.setTimeouts(times);
	expandedWaiter.setOutput(output.createErrorOutput());
	try {
	    expandedWaiter.waitAction(path);
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	}
    }

    /**
     * Expands path on row.
     * @throws TimeoutExpiredException
     */
    public void doExpandRow(int row) {
	expandPath(getPathForRow(row));
    }

    /**
     * Ensures that the node identified by path is currently viewable.
     * @throws TimeoutExpiredException
     */
    public void doMakeVisible(TreePath path)  {
	output.printLine("Making \"" + path.toString() + "\" path visible");
	output.printGolden("Making path visible");
	getEventDispatcher().setOutput(output.createErrorOutput());
	Object[] params = {path};
	Class[] paramClasses = {path.getClass()};
	getEventDispatcher().invokeExistingMethod("makeVisible", params, paramClasses, output);
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", timeouts.getTimeout("JTreeOperator.WaitNodeVisibleTimeout"));
	Waiter visibleWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		if(isVisible((TreePath)obj)) {
		    return("OK");
		} else {
		    return(null);
		}
	    }
	    public String getDescription() {
		return("Wait node visible");
	    }
	});
	visibleWaiter.setTimeouts(times);
	visibleWaiter.setOutput(output.createErrorOutput());
	try {
	    visibleWaiter.waitAction(path);
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	}
    }

    /**
     * Returns number of child.
     */
    public int getChildCount(Object node) {
	int result = 0;
	try {
	    lockQueue();
	    result = ((JTree)getSource()).getModel().getChildCount(node);
	    unlockQueue();
	} catch(Exception e) {
	    unlockAndThrow(e);
	}
	return(result);
    }
    
    /**
     * Returns node children.
     */
    public Object[] getChildren(Object node) {
	Object[] result = null;
	try {
	    lockQueue();
	    TreeModel md = ((JTree)getSource()).getModel();
	    result = new Object[md.getChildCount(node)];
	    for(int i= 0; i < md.getChildCount(node); i++) {
		result[i] = md.getChild(node, i);
	    }
	    unlockQueue();
	} catch(Exception e) {
	    unlockAndThrow(e);
	}
	return(result);
    }

    /**
     * Returns node child.
     */
    public Object getChild(Object node, int index) {
	Object result = null;
	try {
	    lockQueue();
	    result = ((JTree)getSource()).getModel().getChild(node, index);
	    unlockQueue();
	} catch(Exception e) {
	    unlockAndThrow(e);
	}
	return(result);
    }

    /**
     * Returns number of child.
     */
    public int getChildCount(TreePath path) {
	return(getChildCount(path.
			     getLastPathComponent()));
    }

    /**
     * Returns node child path.
     */
    public TreePath getChildPath(TreePath path, int index) {
	return(path.
	       pathByAddingChild(getChild(path.
					  getLastPathComponent(), index)));
    }

    /**
     * Returns node children pathes.
     */
    public TreePath[] getChildPaths(TreePath path) {
	Object[] children = getChildren(path.
					getLastPathComponent());
	TreePath[] result = new TreePath[children.length];
	for(int i = 0; i < children.length; i++) {
	    result[i] = path.
		pathByAddingChild(children[i]);
	}
	return(result);
    }

    /**
     * Returns the root of the tree.
     * @throws TimeoutExpiredException
     */
    public Object getRoot() {
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", timeouts.getTimeout("JTreeOperator.WaitNodeVisibleTimeout"));
	Waiter rootWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		Object root = ((TreeModel)getModel()).getRoot();
		if(root == null || root.toString() == null || root.toString().equals("null")) {
		    return(null);
		} else {
		    return(root);
		}
	    }
	    public String getDescription() {
		return("Wait root node");
	    }
	});
	rootWaiter.setTimeouts(times);
	rootWaiter.setOutput(output.createErrorOutput());
	try {
	    return(rootWaiter.waitAction(null));
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	    return(null);
	}
    }

    /**
     * Searches path in tree.
     * @param chooser TreePathChooser implementation.
     * @see TreePathChooser
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(TreePathChooser chooser) {
	output.printLine("Search for a tree path " + chooser.getDescription());
	output.printGolden("Search for a tree path");
	TreePath rootPath = new TreePath(getRoot());
	if(chooser.checkPath(rootPath, 0)) {
	    return(rootPath);
	}
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", timeouts.getTimeout("JTreeOperator.WaitNextNodeTimeout"));
	Waiter loadedWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		int count = 0;
		TreePathChooser chsr = (TreePathChooser)((Object[])obj)[0];
		TreePath path = (TreePath)((Object[])obj)[1];
		TreeModel model = getModel();
		Object[] result = new Object[2];
		for(int j = 0; j < model.getChildCount(path.getLastPathComponent()); j++) {
		    result[0] = path.pathByAddingChild(model.getChild(path.getLastPathComponent(), j));
		    if(chsr.checkPath((TreePath)result[0], j)) {
			result[1] = new Boolean(true);
			return(result);
		    }
		    if(chsr.hasAsParent((TreePath)result[0], j)) {
			result[1] = new Boolean(false);
			return(result);
		    }
		}
		return(null);
	    }
	    public String getDescription() {
		return("Wait next node loaded");
	    }
	});
	loadedWaiter.setTimeouts(times);
	loadedWaiter.setOutput(output.createErrorOutput());
	return(findPathPrimitive(rootPath, chooser, loadedWaiter));
    }

    /**
     * Searches index'th row by row chooser.
     * @param chooser
     * @param index
     * @return Row index or -1 if search was insuccessful.
     * @see JTreeOperator.TreeRowChooser
     */
    public int findRow(TreeRowChooser chooser, int index) {
	TreeModel model = getModel();
	int count = 0;
	for(int i = 0; i < getRowCount(); i++) {
	    if(chooser.checkRow(this, index)) {
		if(count == index) {
		    return(i);
		} else {
		    count++;
		}
	    }
	}
	return(-1);
    }

    /**
     * Searches a row by row chooser.
     * @param chooser
     * @return Row index or -1 if search was insuccessful.
     * @see JTreeOperator.TreeRowChooser
     */
    public int findRow(TreeRowChooser chooser) {
	return(findRow(chooser, 0));
    }

    private int findRow(String item, StringComparator comparator, int index){
	return(findRow(new BySubStringTreeRowChooser(item, comparator), index));
    }

    /**
     * Searches index'th row by substring.
     * @param item Substring.
     * @param boolean ce Compare exactly
     * @param boolean ccs Compare case sensitivelly.
     * @param index
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(String item, boolean ce, boolean cc, int index){
	return(findRow(item, 
		       new DefaultStringComparator(ce, cc), 
		       index));
    }

    /**
     * Searches index'th row by substring.
     * Uses StringComparator assigned to this object.
     * @param item Substring.
     * @param index
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(String item, int index){
	return(findRow(item, 
		       getComparator(), 
		       index));
    }

    /**
     * Searches a row by substring.
     * @param item Substring.
     * @param boolean ce Compare exactly
     * @param boolean ccs Compare case sensitivelly.
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(String item, boolean ce, boolean cc) {
	return(findRow(item, ce, cc, 0));
    }

    /**
     * Searches a row by substring.
     * Uses StringComparator assigned to this object.
     * @param item Substring.
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(String item){
	return(findRow(item, 
		       getComparator(), 
		       0));
    }

    /**
     * Searches index'th row by rendered component.
     * @param chooser Component checking object.
     * @param index
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(ComponentChooser chooser, int index) {
	return(findRow(new ByRenderedComponentTreeRowChooser(chooser), index));
    }

    /**
     * Searches a row by rendered component.
     * @param chooser Component checking object.
     * @return Row index or -1 if search was insuccessful.
     */
    public int findRow(ComponentChooser chooser) {
	return(findRow(chooser, 0));
    }

    /**
     * Searches path in tree.
     * Can be used to find one of the nodes with the same text.
     * Example:<BR>
     * root<BR>
     * +-+node<BR>
     * | +--subnode<BR>
     * +-+node<BR>
     * | +--subnode<BR>
     * | +--subnode<BR>
     * ...<BR>
     * String[] names = {"node", "subnode"};<BR>
     * int[] indexes = {1, 0};<BR>
     * TreePath path = findPath(names, indexes, true, true);<BR>
     * "path" will points to the second (from the top) "subnode" node.
     * @param names Node texts array.
     * @param indexes Nodes indexes.
     * @param ce Compare exactly.
     * @param ccs Compare case sensitively.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String[] names, int[] indexes, boolean ce, boolean ccs) {
	return(findPath(names, indexes, new DefaultStringComparator(ce, ccs)));
    }

    /**
     * Searches path in tree.
     * Uses StringComparator assigned to this object.
     * @param names Node texts array.
     * @param indexes Nodes indexes.
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String[] names, int[] indexes) {
	return(findPath(names, indexes, getComparator()));
    }

    /**
     * Searches path in tree.
     * @param names Node texts array.
     * @param ce Compare exactly.
     * @param ccs Compare case sensitively.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String[] names, boolean ce, boolean ccs) {
	int[] indexes = new int[0];
	return(findPath(names, indexes, ce, ccs));
    }

    /**
     * Searches path in tree.
     * Uses StringComparator assigned to this object.
     * @param names Node texts array.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String[] names) {
	int[] indexes = new int[0];
	return(findPath(names, indexes, getComparator()));
    }

    /**
     * Searches path in tree.
     * @param path String representing tree path.
     * Path components should be devided by "delim" parameter.
     * @param indexes String representing indexes to search path components.
     * Indexes should be devided by "delim" parameter.
     * @param delim Path components delimiter.
     * @param ce Compare exactly.
     * @param ccs Compare case sensitively.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String path, String indexes, String delim, boolean ce, boolean ccs) {
	String[] indexStrings = parseString(indexes, delim);
	int[] indInts = new int[indexStrings.length];
	for(int i = 0; i < indexStrings.length; i++) {
	    indInts[i] = Integer.parseInt(indexStrings[i]);
	}
	return(findPath(parseString(path, delim), indInts, ce, ccs));
    }

    /**
     * Searches path in tree.
     * Uses StringComparator assigned to this object.
     * @param path String representing tree path.
     * Path components should be devided by "delim" parameter.
     * @param indexes String representing indexes to search path components.
     * Indexes should be devided by "delim" parameter.
     * @param delim Path components delimiter.
     * @see #findPath(JTreeOperator.TreePathChooser)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String path, String indexes, String delim) {
	String[] indexStrings = parseString(indexes, delim);
	int[] indInts = new int[indexStrings.length];
	for(int i = 0; i < indexStrings.length; i++) {
	    indInts[i] = Integer.parseInt(indexStrings[i]);
	}
	return(findPath(parseString(path, delim), indInts, getComparator()));
    }

    /**
     * Searches path in tree.
     * @param path String representing tree path.
     * Path components should be devided by "delim" parameter.
     * @param delim Path components delimiter.
     * @param ce Compare exactly.
     * @param ccs Compare case sensitively.
     * @see ComponentOperator#isCaptionEqual(String, String, boolean, boolean)
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String path, String delim, boolean ce, boolean ccs) {
	return(findPath(parseString(path, delim), ce, ccs));
    }

    /**
     * Searches path in tree.
     * Uses StringComparator assigned to this object.
     * @param path String representing tree path.
     * Path components should be devided by "delim" parameter.
     * @param delim Path components delimiter.
     * @see #findPath(String[], boolean, boolean)
     * @see #findPath(String[], int[], boolean, boolean)
     * @see #findPath(String, String, String, boolean, boolean)
     * @throws TimeoutExpiredException
     */
    public TreePath findPath(String path, String delim) {
	return(findPath(parseString(path, delim)));
    }

    /**
     * Ensures that the node identified by the specified path is collapsed and viewable.
     * @throws TimeoutExpiredException
     */
    public void doCollapsePath(TreePath path) {
	output.printLine("Collapsing \"" + path.toString() + "\" path");
	output.printGolden("Collapsing path");
	getEventDispatcher().setOutput(output.createErrorOutput());
	Object[] params = {path};
	Class[] paramClasses = {path.getClass()};
	if(isExpanded(path)) {
	    if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) == 0) {
		getEventDispatcher().invokeExistingMethod("fireTreeWillCollapse", params, paramClasses, output);
		getEventDispatcher().invokeExistingMethod("collapsePath", params, paramClasses, output);
		getEventDispatcher().invokeExistingMethod("fireTreeCollapsed", params, paramClasses, output);
	    } else {
		Point point = getPointToClick(path);
		clickMouse((int)point.getX(), (int)point.getY(), 2);
	    }
	}
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", 
			 timeouts.getTimeout("JTreeOperator.WaitNodeCollapsedTimeout"));
	times.setTimeout("Waiter.AfterWaitingTime", 
			 timeouts.getTimeout("JTreeOperator.WaitAfterNodeExpandedTimeout"));
	Waiter expandedWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		if(!isExpanded((TreePath)obj)) {
		    return("OK");
		} else {
		    return(null);
		}
	    }
	    public String getDescription() {
		return("Wait node collapsed");
	    }
	});
	expandedWaiter.setTimeouts(times);
	expandedWaiter.setOutput(output.createErrorOutput());
	try {
	    expandedWaiter.waitAction(path);
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	}
    }

    /**
     * Ensures that the node in the specified row is collapsed.
     * @throws TimeoutExpiredException
     */
    public void doCollapseRow(int row) {
	collapsePath(getPathForRow(row));
    }

    /**
     * Selects the path.
     */
    public void selectPath(TreePath path) {
	output.printLine("Selecting \"" + path.toString() + "\" path");
	output.printGolden("Selecting path");
	getEventDispatcher().setOutput(output.createErrorOutput());
	Object[] params = {path};
	Class[] paramClasses = {path.getClass()};
	getEventDispatcher().invokeExistingMethod("clearSelection", null, null, output);
	getEventDispatcher().invokeExistingMethod("addSelectionPath", params, paramClasses, output);
    }

    /**
     * Selects the node in the specified row.
     */
    public void selectRow(int row) {
	selectPath(getPathForRow(row));
    }

    /**
     * Selects some pathes.
     */
    public void selectPaths(TreePath[] paths) {
	output.printLine("Selecting paths:");
	for(int i = 0; i < paths.length; i++) {
	    output.printLine("    " + paths[i].toString());
	}
	output.printGolden("Selecting paths");
	getEventDispatcher().setOutput(output.createErrorOutput());
	Object[] params = {paths};
	Class[] paramClasses = {paths.getClass()};
	getEventDispatcher().invokeExistingMethod("clearSelection", null, null, output);
	getEventDispatcher().invokeExistingMethod("addSelectionPaths", params, paramClasses, output);
    }

    /** 
     * Retuns points which can be used to click on path.
     */
    public Point getPointToClick(TreePath path) {
	Object[] params = {path};
	Class[] paramClasses = {path.getClass()};
	Rectangle rect = getPathBounds(path);
	return(new Point((int)(rect.getX() + rect.getWidth() / 2),
			 (int)(rect.getY() + rect.getHeight() / 2)));
    }

    /** 
     * Clicks on the node.
     * @throws TimeoutExpiredException
     */
    public void clickOnPath(TreePath path, int clickCount) {
	output.printLine("Click on \"" + path.toString() + 
			 "\" path");
	output.printGolden("Click on path");
	makeComponentVisible();
	if(path.getParentPath() != null) {
	    expandPath(path.getParentPath());
	}
	makeVisible(path);
	Point point = getPointToClick(path);
	clickMouse((int)point.getX(), (int)point.getY(), clickCount);
    }

    /** 
     * Clicks on the node.
     * @throws TimeoutExpiredException
     */
    public void clickOnPath(TreePath path) {
	clickOnPath(path, 1);
    }

    /** 
     * Calls popup on the specified pathes.
     * @throws TimeoutExpiredException
     */
    public JPopupMenu callPopupOnPaths(TreePath[] paths, int mouseButton) {
	if(paths.length == 1) {
	    output.printLine("Call popup on \"" + paths[0].toString() + 
			     "\" path");
	    output.printGolden("Call popup on path");
	} else {
	    output.printLine("Call popup on some pathes:");
	    for(int i = 0; i < paths.length; i++) {
		output.printLine("    " + paths[i].toString());
	    }
	    output.printGolden("Call popup on paths");
	}
	makeComponentVisible();
	for(int i = 0; i < paths.length; i++) {
	    if(paths[i].getParentPath() != null) {
		expandPath(paths[i].getParentPath());
	    }
	}
	selectPaths(paths);
	Point point = getPointToClick(paths[0]);
	return(JPopupMenuOperator.callPopup(getSource(), 
					    (int)point.getX(), 
					    (int)point.getY(), 
					    mouseButton));
    }

    /** 
     * Calls popup on the specified pathes.
     * @throws TimeoutExpiredException
     */
    public JPopupMenu callPopupOnPaths(TreePath[] paths) {
	return(callPopupOnPaths(paths, getPopupMouseButton()));
    }

    /** 
     * Calls popup on the specified path.
     * @throws TimeoutExpiredException
     */
    public JPopupMenu callPopupOnPath(TreePath path, int mouseButton) {
	TreePath[] paths = {path};
	return(callPopupOnPaths(paths, mouseButton));
    }

    /** 
     * Calls popup on the specified path.
     * @throws TimeoutExpiredException
     */
    public JPopupMenu callPopupOnPath(TreePath path) {
	return(callPopupOnPath(path, getPopupMouseButton()));
    }

    /**
     * Scrolls to a path if the tree is on a JScrollPane component.
     * @param path
     */
    public void scrollToPath(TreePath path) {
	output.printTrace("Scroll JTree to path \"" + path.toString() + "\"\n    : " +
			  getSource().toString());
	output.printGolden("Scroll JTree to path \"" + path.toString() + "\"");
	//try to find JScrollPane under.
	JScrollPane scroll = (JScrollPane)getContainer(new JScrollPaneOperator.
						       JScrollPaneFinder(ComponentSearcher.
									 getTrueChooser("JScrollPane")));
	if(scroll == null) {
	    return;
	}
	JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
	scroller.copyEnvironment(this);
	Rectangle rect = getPathBounds(path);
	scroller.scrollToComponentRectangle(getSource(), 
					    (int)rect.getX(),
					    (int)rect.getY(),
					    (int)rect.getWidth(),
					    (int)rect.getHeight());
    }

    /**
     * Turns path to the editing mode.
     * @param path
     * @throws TimeoutExpiredException
     */
    public void clickForEdit(TreePath path) {
	if(!isEditing() || !getEditingPath().equals(path)) {
	    if(!isPathSelected(path)) {
		clickOnPath(path);
	    }
	    timeouts.sleep("JTreeOperator.BeforeEditTimeout");
	    clickOnPath(path);
	    Waiter editingWaiter = new Waiter(new Waitable() {
		public Object actionProduced(Object obj) {
		    return(isEditing() ? "" : null);
		}
		public String getDescription() {
		    return("Wait JTree editing");
		}
	    });
	    editingWaiter.setOutput(output.createErrorOutput());
	    editingWaiter.setTimeouts(timeouts.cloneThis());
	    editingWaiter.getTimeouts().setTimeout("Waiter.WaitingTime",
						   timeouts.getTimeout("JTreeOperator.WaitEditingTimeout"));
	    try {
		editingWaiter.waitAction(null);
	    } catch(InterruptedException e) {
		output.printStackTrace(e);
	    }
	}
    }

    /**
     * Ask renderer for component to be displayed.
     * @param path
     * @param isSelected True if the specified cell is selected.
     * @param isExpanded True if the specified cell is expanded.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return Component to be displayed.
     * @see #getCellRenderer()
     */
    public Component getRenderedComponent(TreePath path, boolean isSelected, boolean isExpanded, boolean cellHasFocus) {
	return(getCellRenderer().
	       getTreeCellRendererComponent((JTree)getSource(),
					    path.getLastPathComponent(),
					    isSelected,
					    isExpanded,
					    getModel().isLeaf(path.getLastPathComponent()),
					    getRowForPath(path),
					    cellHasFocus));
    }

    /**
     * Ask renderer for component to be displayed.
     * Uses isPathSelected(TreePath) to determine whether path is selected.
     * Uses isExpanded(TreePath) to  determine whether path is expanded.
     * @param path
     * @return Component to be displayed.
     * @see #getCellRenderer()
     * @see #isPathSelected(TreePath)
     */
    public Component getRenderedComponent(TreePath path) {
	return(getRenderedComponent(path, 
				    isPathSelected(path), 
				    isExpanded(path),
				    false));
    }

    /**
     * Changes text of last path component.
     * @param path
     * @param newNodeText
     * @deprecated Use changePathObject(TreePath, Object) instead.
     * @see #changePathObject(TreePath, Object)
     * @throws TimeoutExpiredException
     */
    public void changePathText(TreePath path, String newNodeText) {
	changePathObject(path, newNodeText);
    }

    /**
     * Changes last path component using getCellEditor() editor.
     * @param path
     * @param newNodeText
     * @see #getCellEditor()
     * @throws TimeoutExpiredException
     */
    public void changePathObject(TreePath path, Object newValue){
	scrollToPath(path);
	if(!isEditing() ||
	   !getEditingPath().equals(path)) {
	    getTreeCellEditor().makeBeingEdited(this, path);
	}
	getTreeCellEditor().enterNewValue(this, waitEditor(path), path, newValue);
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>JTree.addSelectionInterval(int, int)</code> through queue*/
    public void addSelectionInterval(final int i, final int i1) {
	runMapping(new MapVoidAction("addSelectionInterval") {
		public void map() {
		    ((JTree)getSource()).addSelectionInterval(i, i1);
		}});}

    /**Maps <code>JTree.addSelectionPath(TreePath)</code> through queue*/
    public void addSelectionPath(final TreePath treePath) {
	runMapping(new MapVoidAction("addSelectionPath") {
		public void map() {
		    ((JTree)getSource()).addSelectionPath(treePath);
		}});}

    /**Maps <code>JTree.addSelectionPaths(TreePath[])</code> through queue*/
    public void addSelectionPaths(final TreePath[] treePath) {
	runMapping(new MapVoidAction("addSelectionPaths") {
		public void map() {
		    ((JTree)getSource()).addSelectionPaths(treePath);
		}});}

    /**Maps <code>JTree.addSelectionRow(int)</code> through queue*/
    public void addSelectionRow(final int i) {
	runMapping(new MapVoidAction("addSelectionRow") {
		public void map() {
		    ((JTree)getSource()).addSelectionRow(i);
		}});}

    /**Maps <code>JTree.addSelectionRows(int[])</code> through queue*/
    public void addSelectionRows(final int[] i) {
	runMapping(new MapVoidAction("addSelectionRows") {
		public void map() {
		    ((JTree)getSource()).addSelectionRows(i);
		}});}

    /**Maps <code>JTree.addTreeExpansionListener(TreeExpansionListener)</code> through queue*/
    public void addTreeExpansionListener(final TreeExpansionListener treeExpansionListener) {
	runMapping(new MapVoidAction("addTreeExpansionListener") {
		public void map() {
		    ((JTree)getSource()).addTreeExpansionListener(treeExpansionListener);
		}});}

    /**Maps <code>JTree.addTreeSelectionListener(TreeSelectionListener)</code> through queue*/
    public void addTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
	runMapping(new MapVoidAction("addTreeSelectionListener") {
		public void map() {
		    ((JTree)getSource()).addTreeSelectionListener(treeSelectionListener);
		}});}

    /**Maps <code>JTree.addTreeWillExpandListener(TreeWillExpandListener)</code> through queue*/
    public void addTreeWillExpandListener(final TreeWillExpandListener treeWillExpandListener) {
	runMapping(new MapVoidAction("addTreeWillExpandListener") {
		public void map() {
		    ((JTree)getSource()).addTreeWillExpandListener(treeWillExpandListener);
		}});}

    /**Maps <code>JTree.cancelEditing()</code> through queue*/
    public void cancelEditing() {
	runMapping(new MapVoidAction("cancelEditing") {
		public void map() {
		    ((JTree)getSource()).cancelEditing();
		}});}

    /**Maps <code>JTree.clearSelection()</code> through queue*/
    public void clearSelection() {
	runMapping(new MapVoidAction("clearSelection") {
		public void map() {
		    ((JTree)getSource()).clearSelection();
		}});}

    /**Maps <code>JTree.collapsePath(TreePath)</code> through queue*/
    public void collapsePath(final TreePath treePath) {
	runMapping(new MapVoidAction("collapsePath") {
		public void map() {
		    ((JTree)getSource()).collapsePath(treePath);
		}});}

    /**Maps <code>JTree.collapseRow(int)</code> through queue*/
    public void collapseRow(final int i) {
	runMapping(new MapVoidAction("collapseRow") {
		public void map() {
		    ((JTree)getSource()).collapseRow(i);
		}});}

    /**Maps <code>JTree.convertValueToText(Object, boolean, boolean, boolean, int, boolean)</code> through queue*/
    public String convertValueToText(final Object object, final boolean b, final boolean b1, final boolean b2, final int i, final boolean b3) {
	return((String)runMapping(new MapAction("convertValueToText") {
		public Object map() {
		    return(((JTree)getSource()).convertValueToText(object, b, b1, b2, i, b3));
		}}));}

    /**Maps <code>JTree.expandPath(TreePath)</code> through queue*/
    public void expandPath(final TreePath treePath) {
	runMapping(new MapVoidAction("expandPath") {
		public void map() {
		    ((JTree)getSource()).expandPath(treePath);
		}});}

    /**Maps <code>JTree.expandRow(int)</code> through queue*/
    public void expandRow(final int i) {
	runMapping(new MapVoidAction("expandRow") {
		public void map() {
		    ((JTree)getSource()).expandRow(i);
		}});}

    /**Maps <code>JTree.fireTreeCollapsed(TreePath)</code> through queue*/
    public void fireTreeCollapsed(final TreePath treePath) {
	runMapping(new MapVoidAction("fireTreeCollapsed") {
		public void map() {
		    ((JTree)getSource()).fireTreeCollapsed(treePath);
		}});}

    /**Maps <code>JTree.fireTreeExpanded(TreePath)</code> through queue*/
    public void fireTreeExpanded(final TreePath treePath) {
	runMapping(new MapVoidAction("fireTreeExpanded") {
		public void map() {
		    ((JTree)getSource()).fireTreeExpanded(treePath);
		}});}

    /**Maps <code>JTree.fireTreeWillCollapse(TreePath)</code> through queue*/
    public void fireTreeWillCollapse(final TreePath treePath) {
	runMapping(new MapVoidAction("fireTreeWillCollapse") {
		public void map() throws ExpandVetoException {
		    ((JTree)getSource()).fireTreeWillCollapse(treePath);
		}});}

    /**Maps <code>JTree.fireTreeWillExpand(TreePath)</code> through queue*/
    public void fireTreeWillExpand(final TreePath treePath) {
	runMapping(new MapVoidAction("fireTreeWillExpand") {
		public void map() throws ExpandVetoException {
		    ((JTree)getSource()).fireTreeWillExpand(treePath);
		}});}

    /**Maps <code>JTree.getCellEditor()</code> through queue*/
    public TreeCellEditor getCellEditor() {
	return((TreeCellEditor)runMapping(new MapAction("getCellEditor") {
		public Object map() {
		    return(((JTree)getSource()).getCellEditor());
		}}));}

    /**Maps <code>JTree.getCellRenderer()</code> through queue*/
    public TreeCellRenderer getCellRenderer() {
	return((TreeCellRenderer)runMapping(new MapAction("getCellRenderer") {
		public Object map() {
		    return(((JTree)getSource()).getCellRenderer());
		}}));}

    /**Maps <code>JTree.getClosestPathForLocation(int, int)</code> through queue*/
    public TreePath getClosestPathForLocation(final int i, final int i1) {
	return((TreePath)runMapping(new MapAction("getClosestPathForLocation") {
		public Object map() {
		    return(((JTree)getSource()).getClosestPathForLocation(i, i1));
		}}));}

    /**Maps <code>JTree.getClosestRowForLocation(int, int)</code> through queue*/
    public int getClosestRowForLocation(final int i, final int i1) {
	return(runMapping(new MapIntegerAction("getClosestRowForLocation") {
		public int map() {
		    return(((JTree)getSource()).getClosestRowForLocation(i, i1));
		}}));}

    /**Maps <code>JTree.getEditingPath()</code> through queue*/
    public TreePath getEditingPath() {
	return((TreePath)runMapping(new MapAction("getEditingPath") {
		public Object map() {
		    return(((JTree)getSource()).getEditingPath());
		}}));}

    /**Maps <code>JTree.getExpandedDescendants(TreePath)</code> through queue*/
    public Enumeration getExpandedDescendants(final TreePath treePath) {
	return((Enumeration)runMapping(new MapAction("getExpandedDescendants") {
		public Object map() {
		    return(((JTree)getSource()).getExpandedDescendants(treePath));
		}}));}

    /**Maps <code>JTree.getInvokesStopCellEditing()</code> through queue*/
    public boolean getInvokesStopCellEditing() {
	return(runMapping(new MapBooleanAction("getInvokesStopCellEditing") {
		public boolean map() {
		    return(((JTree)getSource()).getInvokesStopCellEditing());
		}}));}

    /**Maps <code>JTree.getLastSelectedPathComponent()</code> through queue*/
    public Object getLastSelectedPathComponent() {
	return((Object)runMapping(new MapAction("getLastSelectedPathComponent") {
		public Object map() {
		    return(((JTree)getSource()).getLastSelectedPathComponent());
		}}));}

    /**Maps <code>JTree.getLeadSelectionPath()</code> through queue*/
    public TreePath getLeadSelectionPath() {
	return((TreePath)runMapping(new MapAction("getLeadSelectionPath") {
		public Object map() {
		    return(((JTree)getSource()).getLeadSelectionPath());
		}}));}

    /**Maps <code>JTree.getLeadSelectionRow()</code> through queue*/
    public int getLeadSelectionRow() {
	return(runMapping(new MapIntegerAction("getLeadSelectionRow") {
		public int map() {
		    return(((JTree)getSource()).getLeadSelectionRow());
		}}));}

    /**Maps <code>JTree.getMaxSelectionRow()</code> through queue*/
    public int getMaxSelectionRow() {
	return(runMapping(new MapIntegerAction("getMaxSelectionRow") {
		public int map() {
		    return(((JTree)getSource()).getMaxSelectionRow());
		}}));}

    /**Maps <code>JTree.getMinSelectionRow()</code> through queue*/
    public int getMinSelectionRow() {
	return(runMapping(new MapIntegerAction("getMinSelectionRow") {
		public int map() {
		    return(((JTree)getSource()).getMinSelectionRow());
		}}));}

    /**Maps <code>JTree.getModel()</code> through queue*/
    public TreeModel getModel() {
	return((TreeModel)runMapping(new MapAction("getModel") {
		public Object map() {
		    return(((JTree)getSource()).getModel());
		}}));}

    /**Maps <code>JTree.getPathBounds(TreePath)</code> through queue*/
    public Rectangle getPathBounds(final TreePath treePath) {
	return((Rectangle)runMapping(new MapAction("getPathBounds") {
		public Object map() {
		    return(((JTree)getSource()).getPathBounds(treePath));
		}}));}

    /**Maps <code>JTree.getPathForLocation(int, int)</code> through queue*/
    public TreePath getPathForLocation(final int i, final int i1) {
	return((TreePath)runMapping(new MapAction("getPathForLocation") {
		public Object map() {
		    return(((JTree)getSource()).getPathForLocation(i, i1));
		}}));}

    /**Maps <code>JTree.getPathForRow(int)</code> through queue*/
    public TreePath getPathForRow(final int i) {
	return((TreePath)runMapping(new MapAction("getPathForRow") {
		public Object map() {
		    return(((JTree)getSource()).getPathForRow(i));
		}}));}

    /**Maps <code>JTree.getPreferredScrollableViewportSize()</code> through queue*/
    public Dimension getPreferredScrollableViewportSize() {
	return((Dimension)runMapping(new MapAction("getPreferredScrollableViewportSize") {
		public Object map() {
		    return(((JTree)getSource()).getPreferredScrollableViewportSize());
		}}));}

    /**Maps <code>JTree.getRowBounds(int)</code> through queue*/
    public Rectangle getRowBounds(final int i) {
	return((Rectangle)runMapping(new MapAction("getRowBounds") {
		public Object map() {
		    return(((JTree)getSource()).getRowBounds(i));
		}}));}

    /**Maps <code>JTree.getRowCount()</code> through queue*/
    public int getRowCount() {
	return(runMapping(new MapIntegerAction("getRowCount") {
		public int map() {
		    return(((JTree)getSource()).getRowCount());
		}}));}

    /**Maps <code>JTree.getRowForLocation(int, int)</code> through queue*/
    public int getRowForLocation(final int i, final int i1) {
	return(runMapping(new MapIntegerAction("getRowForLocation") {
		public int map() {
		    return(((JTree)getSource()).getRowForLocation(i, i1));
		}}));}

    /**Maps <code>JTree.getRowForPath(TreePath)</code> through queue*/
    public int getRowForPath(final TreePath treePath) {
	return(runMapping(new MapIntegerAction("getRowForPath") {
		public int map() {
		    return(((JTree)getSource()).getRowForPath(treePath));
		}}));}

    /**Maps <code>JTree.getRowHeight()</code> through queue*/
    public int getRowHeight() {
	return(runMapping(new MapIntegerAction("getRowHeight") {
		public int map() {
		    return(((JTree)getSource()).getRowHeight());
		}}));}

    /**Maps <code>JTree.getScrollableBlockIncrement(Rectangle, int, int)</code> through queue*/
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int i, final int i1) {
	return(runMapping(new MapIntegerAction("getScrollableBlockIncrement") {
		public int map() {
		    return(((JTree)getSource()).getScrollableBlockIncrement(rectangle, i, i1));
		}}));}

    /**Maps <code>JTree.getScrollableTracksViewportHeight()</code> through queue*/
    public boolean getScrollableTracksViewportHeight() {
	return(runMapping(new MapBooleanAction("getScrollableTracksViewportHeight") {
		public boolean map() {
		    return(((JTree)getSource()).getScrollableTracksViewportHeight());
		}}));}

    /**Maps <code>JTree.getScrollableTracksViewportWidth()</code> through queue*/
    public boolean getScrollableTracksViewportWidth() {
	return(runMapping(new MapBooleanAction("getScrollableTracksViewportWidth") {
		public boolean map() {
		    return(((JTree)getSource()).getScrollableTracksViewportWidth());
		}}));}

    /**Maps <code>JTree.getScrollableUnitIncrement(Rectangle, int, int)</code> through queue*/
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int i, final int i1) {
	return(runMapping(new MapIntegerAction("getScrollableUnitIncrement") {
		public int map() {
		    return(((JTree)getSource()).getScrollableUnitIncrement(rectangle, i, i1));
		}}));}

    /**Maps <code>JTree.getScrollsOnExpand()</code> through queue*/
    public boolean getScrollsOnExpand() {
	return(runMapping(new MapBooleanAction("getScrollsOnExpand") {
		public boolean map() {
		    return(((JTree)getSource()).getScrollsOnExpand());
		}}));}

    /**Maps <code>JTree.getSelectionCount()</code> through queue*/
    public int getSelectionCount() {
	return(runMapping(new MapIntegerAction("getSelectionCount") {
		public int map() {
		    return(((JTree)getSource()).getSelectionCount());
		}}));}

    /**Maps <code>JTree.getSelectionModel()</code> through queue*/
    public TreeSelectionModel getSelectionModel() {
	return((TreeSelectionModel)runMapping(new MapAction("getSelectionModel") {
		public Object map() {
		    return(((JTree)getSource()).getSelectionModel());
		}}));}

    /**Maps <code>JTree.getSelectionPath()</code> through queue*/
    public TreePath getSelectionPath() {
	return((TreePath)runMapping(new MapAction("getSelectionPath") {
		public Object map() {
		    return(((JTree)getSource()).getSelectionPath());
		}}));}

    /**Maps <code>JTree.getSelectionPaths()</code> through queue*/
    public TreePath[] getSelectionPaths() {
	return((TreePath[])runMapping(new MapAction("getSelectionPaths") {
		public Object map() {
		    return(((JTree)getSource()).getSelectionPaths());
		}}));}

    /**Maps <code>JTree.getSelectionRows()</code> through queue*/
    public int[] getSelectionRows() {
	return((int[])runMapping(new MapAction("getSelectionRows") {
		public Object map() {
		    return(((JTree)getSource()).getSelectionRows());
		}}));}

    /**Maps <code>JTree.getShowsRootHandles()</code> through queue*/
    public boolean getShowsRootHandles() {
	return(runMapping(new MapBooleanAction("getShowsRootHandles") {
		public boolean map() {
		    return(((JTree)getSource()).getShowsRootHandles());
		}}));}

    /**Maps <code>JTree.getUI()</code> through queue*/
    public TreeUI getUI() {
	return((TreeUI)runMapping(new MapAction("getUI") {
		public Object map() {
		    return(((JTree)getSource()).getUI());
		}}));}

    /**Maps <code>JTree.getVisibleRowCount()</code> through queue*/
    public int getVisibleRowCount() {
	return(runMapping(new MapIntegerAction("getVisibleRowCount") {
		public int map() {
		    return(((JTree)getSource()).getVisibleRowCount());
		}}));}

    /**Maps <code>JTree.hasBeenExpanded(TreePath)</code> through queue*/
    public boolean hasBeenExpanded(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("hasBeenExpanded") {
		public boolean map() {
		    return(((JTree)getSource()).hasBeenExpanded(treePath));
		}}));}

    /**Maps <code>JTree.isCollapsed(int)</code> through queue*/
    public boolean isCollapsed(final int i) {
	return(runMapping(new MapBooleanAction("isCollapsed") {
		public boolean map() {
		    return(((JTree)getSource()).isCollapsed(i));
		}}));}

    /**Maps <code>JTree.isCollapsed(TreePath)</code> through queue*/
    public boolean isCollapsed(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("isCollapsed") {
		public boolean map() {
		    return(((JTree)getSource()).isCollapsed(treePath));
		}}));}

    /**Maps <code>JTree.isEditable()</code> through queue*/
    public boolean isEditable() {
	return(runMapping(new MapBooleanAction("isEditable") {
		public boolean map() {
		    return(((JTree)getSource()).isEditable());
		}}));}

    /**Maps <code>JTree.isEditing()</code> through queue*/
    public boolean isEditing() {
	return(runMapping(new MapBooleanAction("isEditing") {
		public boolean map() {
		    return(((JTree)getSource()).isEditing());
		}}));}

    /**Maps <code>JTree.isExpanded(int)</code> through queue*/
    public boolean isExpanded(final int i) {
	return(runMapping(new MapBooleanAction("isExpanded") {
		public boolean map() {
		    return(((JTree)getSource()).isExpanded(i));
		}}));}

    /**Maps <code>JTree.isExpanded(TreePath)</code> through queue*/
    public boolean isExpanded(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("isExpanded") {
		public boolean map() {
		    return(((JTree)getSource()).isExpanded(treePath));
		}}));}

    /**Maps <code>JTree.isFixedRowHeight()</code> through queue*/
    public boolean isFixedRowHeight() {
	return(runMapping(new MapBooleanAction("isFixedRowHeight") {
		public boolean map() {
		    return(((JTree)getSource()).isFixedRowHeight());
		}}));}

    /**Maps <code>JTree.isLargeModel()</code> through queue*/
    public boolean isLargeModel() {
	return(runMapping(new MapBooleanAction("isLargeModel") {
		public boolean map() {
		    return(((JTree)getSource()).isLargeModel());
		}}));}

    /**Maps <code>JTree.isPathEditable(TreePath)</code> through queue*/
    public boolean isPathEditable(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("isPathEditable") {
		public boolean map() {
		    return(((JTree)getSource()).isPathEditable(treePath));
		}}));}

    /**Maps <code>JTree.isPathSelected(TreePath)</code> through queue*/
    public boolean isPathSelected(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("isPathSelected") {
		public boolean map() {
		    return(((JTree)getSource()).isPathSelected(treePath));
		}}));}

    /**Maps <code>JTree.isRootVisible()</code> through queue*/
    public boolean isRootVisible() {
	return(runMapping(new MapBooleanAction("isRootVisible") {
		public boolean map() {
		    return(((JTree)getSource()).isRootVisible());
		}}));}

    /**Maps <code>JTree.isRowSelected(int)</code> through queue*/
    public boolean isRowSelected(final int i) {
	return(runMapping(new MapBooleanAction("isRowSelected") {
		public boolean map() {
		    return(((JTree)getSource()).isRowSelected(i));
		}}));}

    /**Maps <code>JTree.isSelectionEmpty()</code> through queue*/
    public boolean isSelectionEmpty() {
	return(runMapping(new MapBooleanAction("isSelectionEmpty") {
		public boolean map() {
		    return(((JTree)getSource()).isSelectionEmpty());
		}}));}

    /**Maps <code>JTree.isVisible(TreePath)</code> through queue*/
    public boolean isVisible(final TreePath treePath) {
	return(runMapping(new MapBooleanAction("isVisible") {
		public boolean map() {
		    return(((JTree)getSource()).isVisible(treePath));
		}}));}

    /**Maps <code>JTree.makeVisible(TreePath)</code> through queue*/
    public void makeVisible(final TreePath treePath) {
	runMapping(new MapVoidAction("makeVisible") {
		public void map() {
		    ((JTree)getSource()).makeVisible(treePath);
		}});}

    /**Maps <code>JTree.removeSelectionInterval(int, int)</code> through queue*/
    public void removeSelectionInterval(final int i, final int i1) {
	runMapping(new MapVoidAction("removeSelectionInterval") {
		public void map() {
		    ((JTree)getSource()).removeSelectionInterval(i, i1);
		}});}

    /**Maps <code>JTree.removeSelectionPath(TreePath)</code> through queue*/
    public void removeSelectionPath(final TreePath treePath) {
	runMapping(new MapVoidAction("removeSelectionPath") {
		public void map() {
		    ((JTree)getSource()).removeSelectionPath(treePath);
		}});}

    /**Maps <code>JTree.removeSelectionPaths(TreePath[])</code> through queue*/
    public void removeSelectionPaths(final TreePath[] treePath) {
	runMapping(new MapVoidAction("removeSelectionPaths") {
		public void map() {
		    ((JTree)getSource()).removeSelectionPaths(treePath);
		}});}

    /**Maps <code>JTree.removeSelectionRow(int)</code> through queue*/
    public void removeSelectionRow(final int i) {
	runMapping(new MapVoidAction("removeSelectionRow") {
		public void map() {
		    ((JTree)getSource()).removeSelectionRow(i);
		}});}

    /**Maps <code>JTree.removeSelectionRows(int[])</code> through queue*/
    public void removeSelectionRows(final int[] i) {
	runMapping(new MapVoidAction("removeSelectionRows") {
		public void map() {
		    ((JTree)getSource()).removeSelectionRows(i);
		}});}

    /**Maps <code>JTree.removeTreeExpansionListener(TreeExpansionListener)</code> through queue*/
    public void removeTreeExpansionListener(final TreeExpansionListener treeExpansionListener) {
	runMapping(new MapVoidAction("removeTreeExpansionListener") {
		public void map() {
		    ((JTree)getSource()).removeTreeExpansionListener(treeExpansionListener);
		}});}

    /**Maps <code>JTree.removeTreeSelectionListener(TreeSelectionListener)</code> through queue*/
    public void removeTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
	runMapping(new MapVoidAction("removeTreeSelectionListener") {
		public void map() {
		    ((JTree)getSource()).removeTreeSelectionListener(treeSelectionListener);
		}});}

    /**Maps <code>JTree.removeTreeWillExpandListener(TreeWillExpandListener)</code> through queue*/
    public void removeTreeWillExpandListener(final TreeWillExpandListener treeWillExpandListener) {
	runMapping(new MapVoidAction("removeTreeWillExpandListener") {
		public void map() {
		    ((JTree)getSource()).removeTreeWillExpandListener(treeWillExpandListener);
		}});}

    /**Maps <code>JTree.scrollPathToVisible(TreePath)</code> through queue*/
    public void scrollPathToVisible(final TreePath treePath) {
	runMapping(new MapVoidAction("scrollPathToVisible") {
		public void map() {
		    ((JTree)getSource()).scrollPathToVisible(treePath);
		}});}

    /**Maps <code>JTree.scrollRowToVisible(int)</code> through queue*/
    public void scrollRowToVisible(final int i) {
	runMapping(new MapVoidAction("scrollRowToVisible") {
		public void map() {
		    ((JTree)getSource()).scrollRowToVisible(i);
		}});}

    /**Maps <code>JTree.setCellEditor(TreeCellEditor)</code> through queue*/
    public void setCellEditor(final TreeCellEditor treeCellEditor) {
	runMapping(new MapVoidAction("setCellEditor") {
		public void map() {
		    ((JTree)getSource()).setCellEditor(treeCellEditor);
		}});}

    /**Maps <code>JTree.setCellRenderer(TreeCellRenderer)</code> through queue*/
    public void setCellRenderer(final TreeCellRenderer treeCellRenderer) {
	runMapping(new MapVoidAction("setCellRenderer") {
		public void map() {
		    ((JTree)getSource()).setCellRenderer(treeCellRenderer);
		}});}

    /**Maps <code>JTree.setEditable(boolean)</code> through queue*/
    public void setEditable(final boolean b) {
	runMapping(new MapVoidAction("setEditable") {
		public void map() {
		    ((JTree)getSource()).setEditable(b);
		}});}

    /**Maps <code>JTree.setInvokesStopCellEditing(boolean)</code> through queue*/
    public void setInvokesStopCellEditing(final boolean b) {
	runMapping(new MapVoidAction("setInvokesStopCellEditing") {
		public void map() {
		    ((JTree)getSource()).setInvokesStopCellEditing(b);
		}});}

    /**Maps <code>JTree.setLargeModel(boolean)</code> through queue*/
    public void setLargeModel(final boolean b) {
	runMapping(new MapVoidAction("setLargeModel") {
		public void map() {
		    ((JTree)getSource()).setLargeModel(b);
		}});}

    /**Maps <code>JTree.setModel(TreeModel)</code> through queue*/
    public void setModel(final TreeModel treeModel) {
	runMapping(new MapVoidAction("setModel") {
		public void map() {
		    ((JTree)getSource()).setModel(treeModel);
		}});}

    /**Maps <code>JTree.setRootVisible(boolean)</code> through queue*/
    public void setRootVisible(final boolean b) {
	runMapping(new MapVoidAction("setRootVisible") {
		public void map() {
		    ((JTree)getSource()).setRootVisible(b);
		}});}

    /**Maps <code>JTree.setRowHeight(int)</code> through queue*/
    public void setRowHeight(final int i) {
	runMapping(new MapVoidAction("setRowHeight") {
		public void map() {
		    ((JTree)getSource()).setRowHeight(i);
		}});}

    /**Maps <code>JTree.setScrollsOnExpand(boolean)</code> through queue*/
    public void setScrollsOnExpand(final boolean b) {
	runMapping(new MapVoidAction("setScrollsOnExpand") {
		public void map() {
		    ((JTree)getSource()).setScrollsOnExpand(b);
		}});}

    /**Maps <code>JTree.setSelectionInterval(int, int)</code> through queue*/
    public void setSelectionInterval(final int i, final int i1) {
	runMapping(new MapVoidAction("setSelectionInterval") {
		public void map() {
		    ((JTree)getSource()).setSelectionInterval(i, i1);
		}});}

    /**Maps <code>JTree.setSelectionModel(TreeSelectionModel)</code> through queue*/
    public void setSelectionModel(final TreeSelectionModel treeSelectionModel) {
	runMapping(new MapVoidAction("setSelectionModel") {
		public void map() {
		    ((JTree)getSource()).setSelectionModel(treeSelectionModel);
		}});}

    /**Maps <code>JTree.setSelectionPath(TreePath)</code> through queue*/
    public void setSelectionPath(final TreePath treePath) {
	runMapping(new MapVoidAction("setSelectionPath") {
		public void map() {
		    ((JTree)getSource()).setSelectionPath(treePath);
		}});}

    /**Maps <code>JTree.setSelectionPaths(TreePath[])</code> through queue*/
    public void setSelectionPaths(final TreePath[] treePath) {
	runMapping(new MapVoidAction("setSelectionPaths") {
		public void map() {
		    ((JTree)getSource()).setSelectionPaths(treePath);
		}});}

    /**Maps <code>JTree.setSelectionRow(int)</code> through queue*/
    public void setSelectionRow(final int i) {
	runMapping(new MapVoidAction("setSelectionRow") {
		public void map() {
		    ((JTree)getSource()).setSelectionRow(i);
		}});}

    /**Maps <code>JTree.setSelectionRows(int[])</code> through queue*/
    public void setSelectionRows(final int[] i) {
	runMapping(new MapVoidAction("setSelectionRows") {
		public void map() {
		    ((JTree)getSource()).setSelectionRows(i);
		}});}

    /**Maps <code>JTree.setShowsRootHandles(boolean)</code> through queue*/
    public void setShowsRootHandles(final boolean b) {
	runMapping(new MapVoidAction("setShowsRootHandles") {
		public void map() {
		    ((JTree)getSource()).setShowsRootHandles(b);
		}});}

    /**Maps <code>JTree.setUI(TreeUI)</code> through queue*/
    public void setUI(final TreeUI treeUI) {
	runMapping(new MapVoidAction("setUI") {
		public void map() {
		    ((JTree)getSource()).setUI(treeUI);
		}});}

    /**Maps <code>JTree.setVisibleRowCount(int)</code> through queue*/
    public void setVisibleRowCount(final int i) {
	runMapping(new MapVoidAction("setVisibleRowCount") {
		public void map() {
		    ((JTree)getSource()).setVisibleRowCount(i);
		}});}

    /**Maps <code>JTree.startEditingAtPath(TreePath)</code> through queue*/
    public void startEditingAtPath(final TreePath treePath) {
	runMapping(new MapVoidAction("startEditingAtPath") {
		public void map() {
		    ((JTree)getSource()).startEditingAtPath(treePath);
		}});}

    /**Maps <code>JTree.stopEditing()</code> through queue*/
    public boolean stopEditing() {
	return(runMapping(new MapBooleanAction("stopEditing") {
		public boolean map() {
		    return(((JTree)getSource()).stopEditing());
		}}));}

    /**Maps <code>JTree.treeDidChange()</code> through queue*/
    public void treeDidChange() {
	runMapping(new MapVoidAction("treeDidChange") {
		public void map() {
		    ((JTree)getSource()).treeDidChange();
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    /**
     * Default editor. Supposes that double click converts tree to an editing mode
     * and a JTextComponent inheritor is displayed to edit.
     */
    protected static class DefaultCellEditor implements CellEditor {
	public void makeBeingEdited(JTreeOperator oper, TreePath path) {
	    oper.clickForEdit(path);
	}
	public boolean checkCellEditor(JTreeOperator oper, Component comp, TreePath path) {
	    return(comp instanceof JTextComponent);
	}
	public void enterNewValue(JTreeOperator oper, Component editor, TreePath path, Object value) {
	    JTextComponentOperator textOper =  
		new JTextComponentOperator((JTextComponent)editor);
	    textOper.copyEnvironment(oper);
	    textOper.clearText();
	    textOper.typeText(value.toString());
	    textOper.pushKey(KeyEvent.VK_ENTER);
	}
	public String getDescription() {
	    return("JTextComponent cell editor");
	}
    }

    /**
     * Iterface to choose tree row.
     */
    public interface TreeRowChooser {
	/**
	 * Should be true if row is good.
	 * @param oper Operator used to search item.
	 * @param row Row be checked.
	 */
	public boolean checkRow(JTreeOperator oper, int row);
	/**
	 * Row description.
	 */
	public String getDescription();
    }

    /**
     * Waits for an editor defined by setCellEditor method.
     * @see #setTreeCellEditor(JTreeOperator.CellEditor)
     */
    protected Component waitEditor(TreePath path) {
	return(waitEditor(getTreeCellEditor(), path));
    }

    /**
     * Waits for an editor.
     */
    protected Component waitEditor(CellEditor editor, TreePath path) {
	Waiter waiter = new Waiter(new EditorWaiter(this, editor, path));
	waiter.setOutput(getOutput());
	waiter.setTimeouts(getTimeouts().cloneThis());
	waiter.getTimeouts().setTimeout("Waiter.WaitingTime",
					getTimeouts().getTimeout("JTreeOperator.WaitEditingTimeout"));
	try {
	    return((Component)waiter.waitAction(null));
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	    return(null);
	}
    }

    /**
     * Searches for JTextComponent inside JTree.
     * This component should be displayed if tree is in being edited.
     */
    protected JTextComponent findEditor() {
	return((JTextComponent)findEditor(new ComponentChooser() {
		public boolean checkComponent(Component comp) {
		    return(comp instanceof JTextComponent);
		}
		public String getDescription() {
		    return("Tree text editor");
		}
	    }));
    }

    /**
     * Searches for component inside JTree.
     */
    protected Component findEditor(ComponentChooser chooser) {
	ComponentSearcher searcher = new ComponentSearcher((Container)getSource());
	searcher.setOutput(output.createErrorOutput());
	return(searcher.findComponent(chooser));
    }

    private TreePath findPathPrimitive(TreePath path, TreePathChooser chooser, Waiter loadedWaiter) {
	if(!isExpanded(path)) {
	    if(!isPathSelected(path)) {
		clickOnPath(path);
	    }
	    expandPath(path);
	}
	Object[] waitParam = {chooser, path};
	Object[] waitResult = null;
	try {
	    waitResult = (Object[])loadedWaiter.waitAction(waitParam);
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	    return(null);
	}
	TreePath nextPath = (TreePath)waitResult[0];
	boolean found = ((Boolean)waitResult[1]).booleanValue();
	if(found) {
	    return(nextPath);
	} else {
	    return(findPathPrimitive(nextPath, chooser, loadedWaiter));
	}
    }

    private String[] addChildrenToDump(Hashtable table, String title, Object node) {
	TreeModel model = ((JTree)getSource()).getModel();
	Object[] subNodes = new Object[model.getChildCount(node)];
	for(int i = 0; i < subNodes.length; i++) {
	    subNodes[i] = model.getChild(node, i);
	}
	String[] names = addToDump(table, title, subNodes);
	for(int i = 0; i < subNodes.length; i++) {
	    addChildrenToDump(table, names[i], subNodes[i]);
	}
	return(names);
    }

    private TreePath findPath(String[] names, int[] indexes, StringComparator comparator) {
	return(findPath(new StringArrayPathChooser(names, indexes, comparator)));
    }

    /**
     * Interface can be used to define a way to edit cells inside JTree.
     */
    public static interface CellEditor {
	/**
	 * Turns cell into editing mode.
	 * @param oper Operator used.
	 * @param path
	 */
	public void makeBeingEdited(JTreeOperator oper, TreePath path) ;
	/**
	 * Check if component is an editor.
	 * @param oper Operator used.
	 * @param comp Checked component.
	 * @param path
	 */
	public boolean checkCellEditor(JTreeOperator oper, Component comp, TreePath path);
	/**
	 * Changes cell value.
	 * @param oper Operator used.
	 * @param editor Editor component.
	 * @param path
	 * @param value Cell value to enter.
	 */
	public void enterNewValue(JTreeOperator oper, Component editor, TreePath path, Object value);
	/**
	 * Editor description.
	 */
	public String getDescription();
    }

    /**
     * Interface is intended to be used to find TreePath in the tree.
     */
    public interface TreePathChooser {
	/**
	 * @param path TreePath to check.
	 * @param indexInParent Index of the "path" in path's parent.
	 */
	public boolean checkPath(TreePath path, int indexInParent);
	/**
	 * @param path TreePath to check.
	 * @param indexInParent Index of the "path" in path's parent.
	 */
	public boolean hasAsParent(TreePath path, int indexInParent);
	public String getDescription();
    }
 
    /**
     * Returns information about component.
     */
    public Hashtable getDump() {
	Hashtable result = super.getDump();
	Object root = ((JTree)getSource()).getModel().getRoot();
	result.put("Root", root.toString());
	addChildrenToDump(result, "Node", root);
	int minSelection = ((JTree)getSource()).getMinSelectionRow();
	if( minSelection >= 0) {
	    Object minObject = ((JTree)getSource()).
		getPathForRow(minSelection).
		getLastPathComponent();
	    int maxSelection = ((JTree)getSource()).getMaxSelectionRow();
	    if(maxSelection > minSelection) {
		Object maxObject = ((JTree)getSource()).
		    getPathForRow(maxSelection).
		    getLastPathComponent();
		result.put("Selection from", minObject.toString());
		result.put("Selection to", maxObject.toString());
	    } else {
		result.put("Selection", minObject.toString());
	    }
	}
	return(result);
    }

    class StringArrayPathChooser implements TreePathChooser {
	String[] arr;
	int[] indices;
	StringComparator comparator;
	StringArrayPathChooser(String[] arr, int[] indices, StringComparator comparator) {
	    this.arr = arr;
	    this.comparator = comparator;
	    this.indices = indices;
	}
	public boolean checkPath(TreePath path, int indexInParent) {
	    return(path.getPathCount() - 1 == arr.length &&
		   hasAsParent(path, indexInParent));
	}
	public boolean hasAsParent(TreePath path, int indexInParent) {
	    Object[] comps = path.getPath();
	    for(int i = 1; i < comps.length; i++) {
		if(arr.length < path.getPathCount() - 1) {
		    return(false);
		}
		if(!comparator.equals(comps[i].toString(), arr[i - 1])) {
		    return(false);
		}
		if(indices.length >= path.getPathCount() - 1 &&
		   indices[path.getPathCount() - 2] != indexInParent) {
		    return(false);
		}
	    }
	    return(true);
	}
	public String getDescription() {
	    String desc = "";
	    for(int i = 0; i < arr.length; i++) {
		desc = desc + arr[i] + ", ";
	    }
	    if(desc.length() > 0) {
		desc = desc.substring(0, desc.length() - 2);
	    }
	    return("[ " + desc + " ]");
	}
    }

    private class BySubStringTreeRowChooser implements TreeRowChooser {
	String subString;
	StringComparator comparator;

	public BySubStringTreeRowChooser(String subString, StringComparator comparator) {
	    this.subString = subString;
	    this.comparator = comparator;
	}

	public boolean checkRow(JTreeOperator oper, int row) {
	    return(comparator.equals(oper.getPathForRow(row).getLastPathComponent().toString(), 
				     subString));
	}

	public String getDescription() {
	    return("Row containing \"" + subString + "\" string");
	}
    }

    private class ByRenderedComponentTreeRowChooser implements TreeRowChooser {
	ComponentChooser chooser;
	public ByRenderedComponentTreeRowChooser(ComponentChooser chooser) {
	    this.chooser = chooser;
	}
	public boolean checkRow(JTreeOperator oper, int row) {
	    return(chooser.checkComponent(oper.getRenderedComponent(oper.getPathForRow(row))));
	}
	public String getDescription() {
	    return(chooser.getDescription());
	}
    }

    private static class JTreeFinder implements ComponentChooser {
	ComponentChooser subFinder;
	public JTreeFinder(ComponentChooser sf) {
	    subFinder = sf;
	}
	public boolean checkComponent(Component comp) {
	    if(comp instanceof JTree) {
		return(subFinder.checkComponent(comp));
	    }
	    return(false);
	}
	public String getDescription() {
	    return(subFinder.getDescription());
	}
    }

    private static class JTreeByItemFinder implements ComponentChooser {
	String label;
	int rowIndex;
	StringComparator comparator;
	public JTreeByItemFinder(String lb, int ii, StringComparator comparator) {
	    label = lb;
	    rowIndex = ii;
	    this.comparator = comparator;
	}
	public boolean checkComponent(Component comp) {
	    if(comp instanceof JTree) {
		if(label == null) {
		    return(true);
		}
		if(((JTree)comp).getRowCount() > rowIndex) {
		    int ii = rowIndex;
		    if(ii == -1) {
			int[] rows = ((JTree)comp).getSelectionRows();
			if(rows != null && rows.length > 0) {
			    ii = rows[0];
			} else {
			    return(false);
			}
		    }
		    TreePath path = ((JTree)comp).getPathForRow(ii);
		    return(comparator.equals(path.getPathComponent(path.getPathCount() - 1).toString(),
					     label));
		}
	    }
	    return(false);
	}
	public String getDescription() {
	    return("JTree with text \"" + label + "\" in " + 
		   (new Integer(rowIndex)).toString() + "'th row");
	}
    }

    private class EditorWaiter implements Waitable {
	private Point pnt;
	private CellEditor editor;
	private TreePath path;
	private JTreeOperator oper;
	ComponentSearcher searcher;
	public EditorWaiter(JTreeOperator oper, CellEditor editor, TreePath path) {
	    this.editor = editor;
	    this.path = path;
	    this.oper = oper;
	    pnt = getPointToClick(path);
	    searcher = 
		new ComponentSearcher((Container)getSource());
	    searcher.setOutput(getOutput().createErrorOutput());
	}

	public Object actionProduced(Object obj) {
	    return(searcher.findComponent(new ComponentChooser() {
		    public boolean checkComponent(Component comp) {
			return(editor.checkCellEditor(oper, comp, path));
		    }
		    public String getDescription() {
			return(editor.getDescription());
		    }
		}));
	}

	public String getDescription() {
	    return(editor.getDescription());
	}
    }
}
