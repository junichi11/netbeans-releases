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

package org.netbeans.jemmy.drivers.lists;

import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.InputEvent;

import org.netbeans.jemmy.QueueTool;

import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;

import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.ComponentOperator;

public class JListMouseDriver extends LightSupportiveDriver implements MultiSelListDriver {
    QueueTool queueTool;
    public JListMouseDriver() {
	super(new String[] {"org.netbeans.jemmy.operators.JListOperator"});
	queueTool = new QueueTool();
    }
    public void selectItem(ComponentOperator oper, int index) {
	clickOnItem((JListOperator)oper, index);
    }
    public void selectItems(ComponentOperator oper, int[] indices) {
	clickOnItem((JListOperator)oper, indices[0]);
	for(int i = 1; i < indices.length; i++) {
	    clickOnItem((JListOperator)oper, indices[i], InputEvent.CTRL_MASK);
	}
    }
    protected void clickOnItem(JListOperator oper, int index) {
	clickOnItem(oper, index, 0);
    }
    protected void clickOnItem(final JListOperator oper, final int index, final int modifiers) {
        if(!queueTool.isDispatchThread()) {
            oper.scrollToItem(index);
        }
        queueTool.invokeSmoothly(new QueueTool.QueueAction("Path selecting") {
                public Object launch() {
                    Rectangle rect = oper.getCellBounds(index, index);
                    DriverManager.getMouseDriver(oper).
                        clickMouse(oper, 
                                   rect.x + rect.width / 2,
                                   rect.y + rect.height / 2,
                                   1, oper.getDefaultMouseButton(), modifiers,
                                   oper.getTimeouts().create("ComponentOperator.MouseClickTimeout"));
                    return(null);
                }
            });
    }
}
