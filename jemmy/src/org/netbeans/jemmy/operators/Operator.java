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

import org.netbeans.jemmy.Action;
import org.netbeans.jemmy.ActionProducer;
import org.netbeans.jemmy.CharBindingMap;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.QueueTool.QueueAction;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.Timeouts;

import java.awt.Component;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.lang.reflect.InvocationTargetException;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Keeps all environment and low-level methods.
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public abstract class Operator extends Object 
    implements Timeoutable, Outputable {

    private static Vector operatorPkgs;

    private Timeouts timeouts;
    private TestOut output;
    private ClassReference codeDefiner;
    private int model;
    private CharBindingMap map;
    private ComponentVisualizer visualizer;
    private StringComparator comparator;
    private QueueTool queueTool;

    /**
     * Inits environment.
     */
    public Operator() {
	super();
	initEnvironment();
    }

    /**
     * Defines object to be used by default to prepare component.
     * Each new operator created after the method using will have
     * defined visualizer.
     * Default implementation is org.netbeans.jemmy.util.DefaultVisualizer class.
     * @param visualizer ComponentVisualizer implementation
     * @return previous value
     * @see #setVisualizer(Operator.ComponentVisualizer)
     * @see #getDefaultComponentVisualizer()
     * @see org.netbeans.jemmy.util.DefaultVisualizer
     */
    public static ComponentVisualizer setDefaultComponentVisualizer(ComponentVisualizer visualizer) {
	return((ComponentVisualizer)JemmyProperties.
	       setCurrentProperty("ComponentOperator.ComponentVisualizer", visualizer));
    }

    /**
     * @return Object is used by default to prepare component
     * @see #getVisualizer()
     * @see #setDefaultComponentVisualizer(Operator.ComponentVisualizer)
     */
    public static ComponentVisualizer getDefaultComponentVisualizer() {
	return((ComponentVisualizer)JemmyProperties.
	       getCurrentProperty("ComponentOperator.ComponentVisualizer"));
    }

    /**
     * Defines string comparator to be assigned in constructor.
     * @see #getDefaultStringComparator()
     * @see Operator.StringComparator
     */
    public static StringComparator setDefaultStringComparator(StringComparator comparator) {
	return((StringComparator)JemmyProperties.
	       setCurrentProperty("ComponentOperator.StringComparator", comparator));
    }

    /**
     * Returns string comparator used to init operators.
     * @see #setDefaultStringComparator(Operator.StringComparator)
     * @see Operator.StringComparator
     */
    public static StringComparator getDefaultStringComparator() {
	return((StringComparator)JemmyProperties.
	       getCurrentProperty("ComponentOperator.StringComparator"));
    }

    /**
     * Compares caption (button text, window title, ...) with a sample text.
     * @param caption String to be compared with match. Method returns false, if parameter is null.
     * @param match Sample to compare with. Method returns true, if parameter is null.
     * @param ce Compare exactly. If true, text can be a substring of caption.
     * @param ccs Compare case sensitively. If true, both text and caption are 
     * converted to upper case before comparison.
     * @see #isCaptionEqual(String, String, Operator.StringComparator)
     * @see #isCaptionEqual(String, String)
     */
    public static boolean isCaptionEqual(String caption, String match, boolean ce, boolean ccs) {
	return(new DefaultStringComparator(ce, ccs).equals(caption, match));
    }

    /**
     * Compares caption (button text, window title, ...) with a sample text.
     * @param caption String to be compared with match
     * @param match Sample to compare with
     * @param comparator StringComparator instance.
     * @see #isCaptionEqual(String, String, boolean, boolean)
     * @see #isCaptionEqual(String, String)
     */
    public static boolean isCaptionEqual(String caption, String match, StringComparator comparator) {
	return(comparator.equals(caption, match));
    }

    /**
     * Returns default mouse button mask. (InputEvent.BUTTON1_MASK)
     */
    public static int getDefaultMouseButton() {
	return(InputEvent.BUTTON1_MASK);
    }

    /**
     * Returns mask of mouse button which used to popup expanding. (InputEvent.BUTTON3_MASK)
     */
    public static int getPopupMouseButton() {
	return(InputEvent.BUTTON3_MASK);
    }

    /**
     * Creates operator for component.
     * Tries to find class with "<operator package>.<class name>Operator" name,
     * where <operator package> is a package from operator packages list,
     * and <class name> is the name of class or one of its superclasses.
     * @param comp Component to create operator for.
     * @see #addOperatorPackage(String)
     */
    public static ComponentOperator createOperator(Component comp) {
	//hack!
	try {
	    Class cclass = Class.forName("java.awt.Component");
	    Class compClass = comp.getClass();
	    ComponentOperator result;
	    do {
		if((result = createOperator(comp, compClass)) != null) {
		    return(result);
		}
	    } while(cclass.isAssignableFrom(compClass = compClass.getSuperclass()));
	} catch(ClassNotFoundException e) {
	}
	return(null);
    }

    /**
     * Adds package to the list of packages containing operators. <BR>
     * "org.netbeans.jemmy.operators" is in the list by default.
     * @param pkgName Package name.
     * @see #createOperator(Component)
     */
    public static void addOperatorPackage(String pkgName) {
	operatorPkgs.add(pkgName);
    }


    /**
     * Returns an operator containing default environment.
     */
    public static Operator getEnvironmentOperator() {
	return(new NullOperator());
    }

    static {
	try {
	    setDefaultComponentVisualizer((ComponentVisualizer)
					  new ClassReference("org.netbeans.jemmy.util.DefaultVisualizer").
					  newInstance(null, null));
	} catch(ClassNotFoundException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	} catch(NoSuchMethodException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	} catch(InvocationTargetException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	} catch(IllegalAccessException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	} catch(InstantiationException e) {
	    JemmyProperties.getCurrentOutput().printStackTrace(e);
	}
	operatorPkgs = new Vector ();
	setDefaultStringComparator(new DefaultStringComparator(false, false));
	addOperatorPackage("org.netbeans.jemmy.operators");
    }

    /**
     * Returns object operator is used for.
     */
    public abstract Component getSource();

    ////////////////////////////////////////////////////////
    //Environment                                         //
    ////////////////////////////////////////////////////////

    /**
     * Returns QueueTool is used to work with queue.
     */
    public QueueTool getQueueTool() {
	return(queueTool);
    }

    /**
     * Copies all environment (output, timeouts, dispatching model,
     * visualizer) from another operator.
     */
    public void copyEnvironment(Operator anotherOperator) {
	setTimeouts(anotherOperator.getTimeouts());
	setOutput(anotherOperator.getOutput());
	setVisualizer(anotherOperator.getVisualizer());
	setDispatchingModel(anotherOperator.getDispatchingModel());
	setComparator(anotherOperator.getComparator());
    }

    /**
     * Defines current timeouts.
     * @param timeouts A collection of timeout assignments.
     * @see org.netbeans.jemmy.Timeoutable
     * @see org.netbeans.jemmy.Timeouts
     */
    public void setTimeouts(Timeouts timeouts) {
	this.timeouts = timeouts;
	queueTool.setTimeouts(timeouts);
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
     * Returns component visualizer.
     * Visualizer is used from from makeComponentVisible() method.
     * @see #getDefaultComponentVisualizer()
     * @see #setVisualizer(Operator.ComponentVisualizer)
     */
    public ComponentVisualizer getVisualizer() {
 	return(visualizer);
    }
    
    /**
     * Changes component visualizer.
     * Visualizer is used from from makeComponentVisible() method.
     * @see #setDefaultComponentVisualizer(Operator.ComponentVisualizer)
     * @see #getVisualizer()
     */
    public void setVisualizer(ComponentVisualizer vo) {
 	visualizer = vo;
    }

    /**
     * Force operator to use dispatching model.
     * @param m New model value.
     * @see org.netbeans.jemmy.JemmyProperties#setCurrentDispatchingModel(int)
     */
    public void setDispatchingModel(int m) {
	model = m;
    }

    /**
     * @return Current dispatching model.
     * @see #setDispatchingModel(int)
     */
    public int getDispatchingModel() {
	return(model);
    }

    /**
     * Defines CharBindingMap.
     * @see org.netbeans.jemmy.CharBindingMap
     * @see org.netbeans.jemmy.JemmyProperties#setCurrentCharBindingMap(CharBindingMap)
     */
    public void setCharBindingMap(CharBindingMap map) {
	this.map = map;
    }

    /**
     * Defines print output streams or writers.
     * @param out Identify the streams or writers used for print output.
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public void setOutput(TestOut out) {
	output = out;
	queueTool.setOutput(output.createErrorOutput());
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
     * Returns object which is used for string comparison.
     * @see org.netbeans.jemmy.operators.Operator.StringComparator
     * @see org.netbeans.jemmy.operators.Operator.DefaultStringComparator
     */
    public StringComparator getComparator() {
	return(comparator);
    }

    /**
     * Returns object which is used for string comparison. 
     * @see org.netbeans.jemmy.operators.Operator.StringComparator
     * @see org.netbeans.jemmy.operators.Operator.DefaultStringComparator
     */
    public void setComparator(StringComparator comparator) {
	this.comparator = comparator;
    }

    ////////////////////////////////////////////////////////
    //Util                                                //
    ////////////////////////////////////////////////////////

    /**
     * Parses strings like "1|2|3" into arrays {"1", "2", "3"}
     */
    public String[] parseString(String path, String delim) {
	if(path.equals("")) {
	    return(new String[0]);
	}
	Vector nameVector = new Vector();
	int curIndex = 0;
	String restPath = path;
	while((curIndex = nextDelimIndex(restPath, delim)) != -1) {
	    nameVector.add(restPath.substring(0, curIndex));
	    restPath = restPath.substring(curIndex + 1);
	}
	nameVector.add(restPath);
	String[] spath = new String[nameVector.size()];
	for(int i = 0; i < spath.length; i++) {
	    spath[i] = (String)nameVector.get(i);
	}
	return(spath);
    }

    /**
     * Returns key code to by pressed for character typing.
     * @param c Character to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int getCharKey(char c) {
	return(map.getCharKey(c));
    }

    /**
     * Returns modifiers mask for character typing.
     * @param c Character to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int getCharModifiers(char c) {
	return(map.getCharModifiers(c));
    }

    /**
     * Returns key codes to by pressed for characters typing.
     * @param c Characters to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int[] getCharsKeys(char[] c) {
	int[] result = new int[c.length];
	for(int i = 0; i < c.length; i++) {
	    result[i] = getCharKey(c[i]);
	}
	return(result);
    }

    /**
     * Returns modifiers masks for characters typing.
     * @param c Characters to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int[] getCharsModifiers(char[] c) {
	int[] result = new int[c.length];
	for(int i = 0; i < c.length; i++) {
	    result[i] = getCharModifiers(c[i]);
	}
	return(result);
    }

    /**
     * Returns key codes to by pressed for the string typing.
     * @param s String to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int[] getCharsKeys(String s) {
	return(getCharsKeys(s.toCharArray()));
    }

    /**
     * Returns modifiers masks for the string typing.
     * @param s String to be typed.
     * @see org.netbeans.jemmy.CharBindingMap
     */
    public int[] getCharsModifiers(String s) {
	return(getCharsModifiers(s.toCharArray()));
    }

    /**
     * Compares string using getComparator StringComparator.
     * @see #isCaptionEqual(String, String, Operator.StringComparator)
     * @see #isCaptionEqual(String, String, boolean, boolean)
     */
    public boolean isCaptionEqual(String caption, String match) {
	return(comparator.equals(caption, match));
    }

    /**
     * Prints component information into operator output.
     */
    public void printDump() {
	Hashtable result = getDump();
	Object[] keys = result.keySet().toArray();
	for(int i = 0; i < result.size(); i++) {
	    output.printLine((String)keys[i] + 
			     " = " + 
			     (String)result.get(keys[i]));
	}
    }

    /**
     * Returns information about component.
     */
    public Hashtable getDump() {
	Hashtable result = new Hashtable();
	result.put("Class", getSource().getClass().getName());
	result.put("toString", getSource().toString());
	return(result);
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //
    ////////////////////////////////////////////////////////

    /**
     * Makes easier noblocking operations.
     */
    protected void produceNoBlocking(NoBlockingAction action, Object param) {
	try {
	    ActionProducer noBlockingProducer = new ActionProducer(action, false);
	    noBlockingProducer.setOutput(output.createErrorOutput());
	    noBlockingProducer.setTimeouts(timeouts);
	    noBlockingProducer.produceAction(param);
	} catch(InterruptedException e) {
	    throw(new JemmyException("Exception during \"" + 
				     action.getDescription() +
				     "\" execution",
				     e));
	}
	if(action.exception != null) {
	    throw(new JemmyException("Exception during nonblocking \"" +
				     action.getDescription() + "\"",
				     action.exception));
	}
    }

    /**
     * Makes easier noblocking operations.
     */
    protected void produceNoBlocking(NoBlockingAction action) {
	produceNoBlocking(action, null);
    }

    /**
     * Equivalent to getQueue().lock();
     */
    protected void lockQueue() {
	if((getDispatchingModel() & JemmyProperties.QUEUE_MODEL_MASK) != 0) {
	    queueTool.lock();
	}
    }

    /**
     * Equivalent to getQueue().unlock();
     */
    protected void unlockQueue() {
	queueTool.unlock();
    }

    /**
     * Unlocks Queue and then throw exception.
     */
    protected void unlockAndThrow(Exception e) {
	unlockQueue();
	throw(new JemmyException("Exception during queue locking"));
    }

    /**
     * To map nonprimitive type component's method.
     * @see Operator.MapAction
     */
    protected Object runMapping(MapAction action) {
	return(queueTool.invokeAndWait(action));
    }

    /**
     * To map char component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapCharacterAction
     */
    protected char runMapping(MapCharacterAction action) {
	return(((Character)queueTool.invokeAndWait(action)).charValue());
    }

    /**
     * To map byte component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapByteAction
     */
    protected byte runMapping(MapByteAction action) {
	return(((Byte)queueTool.invokeAndWait(action)).byteValue());
    }

    /**
     * To map int component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapIntegerAction
     */
    protected int runMapping(MapIntegerAction action) {
	return(((Integer)queueTool.invokeAndWait(action)).intValue());
    }

    /**
     * To map long component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapLongAction
     */
    protected long runMapping(MapLongAction action) {
	return(((Long)queueTool.invokeAndWait(action)).longValue());
    }

    /**
     * To map float component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapFloatAction
     */
    protected float runMapping(MapFloatAction action) {
	return(((Float)queueTool.invokeAndWait(action)).floatValue());
    }

    /**
     * To map double component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapDoubleAction
     */
    protected double runMapping(MapDoubleAction action) {
	return(((Double)queueTool.invokeAndWait(action)).doubleValue());
    }

    /**
     * To map boolean component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapBooleanAction
     */
    protected boolean runMapping(MapBooleanAction action) {
	return(((Boolean)queueTool.invokeAndWait(action)).booleanValue());
    }

    /**
     * To map void component's method.
     * @see #runMapping(Operator.MapAction)
     * @see Operator.MapVoidAction
     */
    protected void runMapping(MapVoidAction action) {
	queueTool.invokeAndWait(action);
    }

    /**
     * Adds array of objects to dump hashtable.
     * Is used for multiple properties such as list items and tree nodes.
     */
    protected String[] addToDump(Hashtable table, String title, Object[] items) {
	String[] names = createNames(title + "_", items.length);
	for(int i = 0; i < items.length; i++) {
	    table.put(names[i], items[i].toString());
	}
	return(names);
    }

    /**
     * Adds two dimentional array of objects to dump hashtable.
     * Is used for multiple properties such as table cells.
     */
    protected String[] addToDump(Hashtable table, String title, Object[][] items) {
	String[] names = createNames(title + "_", items.length);
	for(int i = 0; i < items.length; i++) {
	    addToDump(table, names[i], items[i]);
	}
	return(names);
    }
    ////////////////////////////////////////////////////////
    //Private                                             //
    ////////////////////////////////////////////////////////

    private String[] createNames(String title, int count) {
	String[] result = new String[count];
	int indexLength = Integer.toString(count).length();
	String zeroString = "";
	for(int i = 0; i < indexLength; i++) {
	    zeroString = zeroString + "0";
	}
	String indexString;
	for(int i = 0; i < count; i++) {
	    indexString = Integer.toString(i);
	    result[i] = title + 
		zeroString.substring(0, indexLength - indexString.length()) + 
		indexString;
	}
	return(result);
    }

    private static ComponentOperator createOperator(Component comp, Class compClass) {
	StringTokenizer token = new StringTokenizer(compClass.getName(), ".");
	String className = "";
	while(token.hasMoreTokens()) {
	    className = token.nextToken();
	}
	Object[] params = {comp};
	Class[] param_classes = {compClass};
	String operatorPackage;
	for(int i = 0; i < operatorPkgs.size(); i++) {
	    operatorPackage = (String)operatorPkgs.get(i);
	    try {
		return((ComponentOperator)
		       new ClassReference(operatorPackage + "." +
					  className + "Operator").
		       newInstance(params, param_classes));
	    } catch(ClassNotFoundException e) {
	    } catch(InvocationTargetException e) {
	    } catch(NoSuchMethodException e) {
	    } catch(IllegalAccessException e) {
	    } catch(InstantiationException e) {
	    }
	}
	return(null);
    }

    private void initEnvironment() {
	try {
	    codeDefiner = new ClassReference("java.awt.event.KeyEvent");
	} catch(ClassNotFoundException e) {
	}
	queueTool = new QueueTool();
	setTimeouts(JemmyProperties.getProperties().getTimeouts());
	setOutput(JemmyProperties.getProperties().getOutput());
	setDispatchingModel(JemmyProperties.getProperties().getDispatchingModel());
	setCharBindingMap(JemmyProperties.getProperties().getCharBindingMap());
	setVisualizer(getDefaultComponentVisualizer());
	setComparator(getDefaultStringComparator());
    }

    private int nextDelimIndex(String path, String delim) {
	String restPath = path;
	int ind = 0;
	while((ind = restPath.indexOf(delim)) != -1) {
	    if(ind == 0 ||
	       restPath.substring(ind - 1, ind) != "\\") {
		return(ind);
	    }
	}
	return(-1);
    }


    /**
     * Interface used to make component visible & ready to to make operations with.
     */
    public interface ComponentVisualizer {
	/**
	 * Should prepare component.
	 * @param compOper Operator asking for necessary actions.
	 */
	public void makeVisible(ComponentOperator compOper);
    }


    /**
     * Interface to compare string resources like labels, button text, ...
     * with match. <BR>
     */
    public interface StringComparator {
	/**
	 * Imlementation mast return true if strings are equal.
	 */
	public boolean equals(String caption, String match);
    }


    /**
     * Default StringComparator implementation.
     */
    public static class DefaultStringComparator implements StringComparator {
	boolean ce;
	boolean ccs;

	/**
	 * @param ce Compare exactly. If true, text can be a substring of caption.
	 * @param ccs Compare case sensitively. If true, both text and caption are 
	 */
	public DefaultStringComparator(boolean ce, boolean ccs) {
	    this.ce = ce;
	    this.ccs = ccs;
	}

	/**	
	 * @param caption String to be compared with match. Method returns false, if parameter is null.
	 * @param match Sample to compare with. Method returns true, if parameter is null.
	 */
	public boolean equals(String caption, String match) {
	    if(match == null) {
		return(true);
	    }
	    if(caption == null) {
		return(false);
	    }
	    String c, t;
	    if(!ccs) {
		c = caption.toUpperCase();
		t = match.toUpperCase();
	    } else {
		c = caption;
		t = match;
	    }
	    if(ce) {
		return(c.equals(t));
	    } else {
		return(c.indexOf(t) != -1);
	    }
	}
    }

    /**
     * Can be used to make nonblocking operation implementation.
     * Typical scenario is: <BR>
     *	produceNoBlocking(new NoBlockingAction("Button pushing") {<BR>
     *		public Object doAction(Object param) {<BR>
     *		    push();<BR>
     *		    return(null);<BR>
     *		}<BR>
     *	    });<BR>
     */
    protected abstract class NoBlockingAction implements Action {
	String description;
	Exception exception;
	boolean finished;
	public NoBlockingAction(String description) {
	    this.description = description;
	    exception = null;
	    finished = false;
	}
	public final Object launch(Object param) {
	    Object result = null;
	    try {
		result = doAction(param);
	    } catch(Exception e) {
		exception = e;
	    }
	    finished = true;
	    return(result);
	}
	public abstract Object doAction(Object param);
	public String getDescription() {
	    return(description);
	}
	protected void setException(Exception e) {
	    exception = e;
	}
	public Exception getException() {
	    return(exception);
	}
    }

    /**
     * Can be used to simplify nonprimitive type component's methods mapping.
     * Like this: <BR>
     * public Color getBackground() { <BR>
     *     return((Color)runMapping(new MapAction("getBackground") { <BR>
     *         public Object map() { <BR>
     *             return(((Component)getSource()).getBackground()); <BR>
     *         } <BR>
     *     })); <BR>
     * } <BR>
     * @see #runMapping(Operator.MapAction)
     */
    protected abstract class MapAction extends QueueTool.QueueAction {
	public MapAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(map());
	}
	public abstract Object map() throws Exception;
    }

    /**
     * Can be used to simplify char component's methods mapping.
     * @see #runMapping(Operator.MapCharacterAction)
     */
    protected abstract class MapCharacterAction extends QueueTool.QueueAction {
	public MapCharacterAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Character(map()));
	}
	public abstract char map() throws Exception;
    }

    /**
     * Can be used to simplify byte component's methods mapping.
     * @see #runMapping(Operator.MapByteAction)
     */
    protected abstract class MapByteAction extends QueueTool.QueueAction {
	public MapByteAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Byte(map()));
	}
	public abstract byte map() throws Exception;
    }

    /**
     * Can be used to simplify int component's methods mapping.
     * @see #runMapping(Operator.MapIntegerAction)
     */
    protected abstract class MapIntegerAction extends QueueTool.QueueAction {
	public MapIntegerAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Integer(map()));
	}
	public abstract int map() throws Exception;
    }

    /**
     * Can be used to simplify long component's methods mapping.
     * @see #runMapping(Operator.MapLongAction)
     */
    protected abstract class MapLongAction extends QueueTool.QueueAction {
	public MapLongAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Long(map()));
	}
	public abstract long map() throws Exception;
    }

    /**
     * Can be used to simplify float component's methods mapping.
     * @see #runMapping(Operator.MapFloatAction)
     */
    protected abstract class MapFloatAction extends QueueTool.QueueAction {
	public MapFloatAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Float(map()));
	}
	public abstract float map() throws Exception;
    }

    /**
     * Can be used to simplify double component's methods mapping.
     * @see #runMapping(Operator.MapDoubleAction)
     */
    protected abstract class MapDoubleAction extends QueueTool.QueueAction {
	public MapDoubleAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Double(map()));
	}
	public abstract double map() throws Exception;
    }

    /**
     * Can be used to simplify boolean component's methods mapping.
     * @see #runMapping(Operator.MapBooleanAction)
     */
    protected abstract class MapBooleanAction extends QueueTool.QueueAction {
	public MapBooleanAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    return(new Boolean(map()));
	}
	public abstract boolean map() throws Exception;
    }

    /**
     * Can be used to simplify void component's methods mapping.
     * @see #runMapping(Operator.MapVoidAction)
     */
    protected abstract class MapVoidAction extends QueueTool.QueueAction {
	public MapVoidAction(String description) {
	    super(description);
	}
	public final Object launch() throws Exception {
	    map();
	    return(null);
	}
	public abstract void map() throws Exception;
    }

    private static class NullOperator extends Operator {
	public NullOperator() {
	    super();
	}
	public Component getSource() {
	    return(null);
	}
    }
}


