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
package org.netbeans.modules.visual.action;

import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author David Kaspar
 */
public final class MoveAction extends WidgetAction.LockedAdapter {

    private MoveStrategy strategy;
    private MoveProvider provider;

    private Widget movingWidget = null;
    private Point dragSceneLocation = null;
    private Point originalSceneLocation = null;

    public MoveAction (MoveStrategy strategy, MoveProvider provider) {
        this.strategy = strategy;
        this.provider = provider;
    }

    protected boolean isLocked () {
        return movingWidget != null;
    }

    public State mousePressed (Widget widget, WidgetMouseEvent event) {
        if (event.getButton () == MouseEvent.BUTTON1  &&  event.getClickCount () == 1) {
            movingWidget = widget;
            originalSceneLocation = widget.getPreferredLocation ();
            if (originalSceneLocation == null)
                originalSceneLocation = new Point ();
            dragSceneLocation = widget.convertLocalToScene (event.getPoint ());
            provider.movementStarted (widget);
            return State.createLocked (widget, this);
        }
        return State.REJECTED;
    }

    public State mouseReleased (Widget widget, WidgetMouseEvent event) {
        boolean state = move (widget, event.getPoint ());
        if (state) {
            movingWidget = null;
            provider.movementFinished (widget);
        }
        return state ? State.CONSUMED : State.REJECTED;
    }

    public State mouseDragged (Widget widget, WidgetMouseEvent event) {
        return move (widget, event.getPoint ()) ? State.createLocked (widget, this) : State.REJECTED;
    }

    private boolean move (Widget widget, Point newLocation) {
        if (movingWidget != widget)
            return false;
        newLocation = widget.convertLocalToScene (newLocation);
        Point location = new Point (originalSceneLocation.x + newLocation.x - dragSceneLocation.x, originalSceneLocation.y + newLocation.y - dragSceneLocation.y);
        provider.setNewLocation (widget, strategy.locationSuggested (widget, originalSceneLocation, location));
        return true;
    }

}
