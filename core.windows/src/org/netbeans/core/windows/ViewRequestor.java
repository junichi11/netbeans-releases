/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.core.windows;


import java.awt.Frame;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;

import org.netbeans.core.windows.view.ViewEvent;
import org.netbeans.core.windows.view.View;
import org.netbeans.core.windows.view.ViewFactory;


/**
 * Class responsible for communication between central and view parts.
 * It sends requests to view. Also provides coallescing of more
 * requests and is also responsible for scheduling the requests into AWT thread.
 * This class is thread safe.
 *
 * @author  Peter Zavadsky
 */
class ViewRequestor {

    /** Associated central. */
    private final Central central;
    /** View of window system. */
    private final View view;
    
    // XXX This is to prevent loop, caused by TopComponent.requestFocus(), 
    // which causes problems, e.g. when selecting and activating component.
    // Note: Access only from AWT thread.
    private boolean processingRequest;
    
    
    /** List of requests to process. */
    private final List requests = new ArrayList(10);
    // PENDING
    /** Instance of snapshot which is passed to view. Reflects the actual state of model.
     * Manipulate it in AWT thread only. */
    private WindowSystemSnapshot snapshot;
    
    /** Creates a new instance of ViewRequestor */
    public ViewRequestor(Central central) {
        this.central = central;
        this.view = ViewFactory.createWindowSystemView(central);
    }

    
    // XXX PENDING Method requesting state of view... do not mix with the rest.
    // It shoudn't exist any link in direction view -> central.
    public boolean isDragInProgress() {
        return view.isDragInProgress();
    }
    
    // XXX PENDING Method requesting info of view... do not mix with the rest.
    // It shouldn't exist any link in directtion view -> central, but due to old API.
    public Frame getMainWindow() {
        return view.getMainWindow();
    }

    
    /** Schedules request into AWT. */
    public void scheduleRequest(ViewRequest request) {
        if(SwingUtilities.isEventDispatchThread() && processingRequest) {
            Debug.log(ViewRequestor.class, "Processing request, returning event"); // NOI18N
            Debug.dumpStack(ViewRequestor.class);
            // XXX Preventing loop caused by impl of TopComponent.requestFocus().
            return;
        }
        
        if(request.type == View.CHANGE_VISIBILITY_CHANGED) {
            // Most important request, takes precedence to all others.
            postVisibilityRequest(request);
            return;
        }

        coallesceRequest(request);
        
        postRequest();
    }

    /** Coallesce request. */
    private void coallesceRequest(ViewRequest request) {
        Object source = request.source;
        int type      = request.type;
        
        boolean doCoallesce = 
            type == View.CHANGE_ACTIVE_MODE_CHANGED
            || type == View.CHANGE_EDITOR_AREA_BOUNDS_CHANGED
            || type == View.CHANGE_EDITOR_AREA_CONSTRAINTS_CHANGED
            || type == View.CHANGE_EDITOR_AREA_STATE_CHANGED
            || type == View.CHANGE_EDITOR_AREA_FRAME_STATE_CHANGED
            || type == View.CHANGE_MAIN_WINDOW_BOUNDS_JOINED_CHANGED
            || type == View.CHANGE_MAIN_WINDOW_BOUNDS_SEPARATED_CHANGED
            || type == View.CHANGE_MAIN_WINDOW_FRAME_STATE_JOINED_CHANGED
            || type == View.CHANGE_MAIN_WINDOW_FRAME_STATE_SEPARATED_CHANGED
            || type == View.CHANGE_MAXIMIZED_MODE_CHANGED
            || type == View.CHANGE_MODE_SELECTED_TOPCOMPONENT_CHANGED
            || type == View.CHANGE_MODE_BOUNDS_CHANGED
            || type == View.CHANGE_MODE_CONSTRAINTS_CHANGED
            || type == View.CHANGE_MODE_FRAME_STATE_CHANGED
            || type == View.CHANGE_TOOLBAR_CONFIGURATION_CHANGED
            || type == View.CHANGE_TOPCOMPONENT_ICON_CHANGED
            || type == View.CHANGE_TOPCOMPONENT_DISPLAY_NAME_CHANGED
            || type == View.CHANGE_TOPCOMPONENT_DISPLAY_NAME_ANNOTATION_CHANGED
            || type == View.CHANGE_TOPCOMPONENT_TOOLTIP_CHANGED
            || type == View.CHANGE_TOPCOMPONENT_ACTIVATED
            || type == View.CHANGE_DND_PERFORMED
            || type == View.CHANGE_UI_UPDATE;
            
        synchronized(requests) {
            Object oldValue = null;
            if(doCoallesce) {
                for(Iterator it = requests.iterator(); it.hasNext(); ) {
                    ViewRequest r = (ViewRequest)it.next();
                    if(source == r.source && type == r.type) {
                        // Remove the old request (it will be replaced by new one).
                        it.remove();
                        oldValue = r.oldValue;
                        break;
                    }
                }
            }

            if(oldValue != null) {
                // Actually coallesce.
                requests.add(new ViewRequest(
                    request.source, request.type, oldValue, request.newValue));
            } else {
                requests.add(request);
            }
        }
    }
    
    private void postRequest() {
        if(SwingUtilities.isEventDispatchThread()) {
            processingRequest = true;
            try {
                processRequest();
            } finally {
                processingRequest = false;
            }
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    processingRequest = true;
                    try {
                        processRequest();
                    } finally {
                        processingRequest = false;
                    }
                }
            });
        }
    }
    
    private void postVisibilityRequest(final ViewRequest visibilityRequest) {
        if(SwingUtilities.isEventDispatchThread()) {
            processVisibilityRequest(visibilityRequest);
        } else {
            final long time = System.currentTimeMillis();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Debug.log(ViewRequestor.class,
                        "Rescheduling request into AWT took=" // NOI18N
                        + (System.currentTimeMillis() - time) + " ms"); // NOI18N
                    
                    processVisibilityRequest(visibilityRequest);
                }
            });
        }
    }
    
    
    ///////////////////////////////////////////
    // !! AWT thread only >>
    
    private void processRequest() {
        if(snapshot == null) {
            // The system was made invisible.
            return;
        }
        
        ViewRequest[] rs;
        synchronized(requests) {
            if(requests.isEmpty()) {
                // No requests left.
                return;
            }
            
            rs = (ViewRequest[])requests.toArray(new ViewRequest[0]);
            requests.clear();
        }
        
        List viewEvents = new ArrayList();
        
        updateSnapshot(rs);
        for(int i = 0; i < rs.length; i++) {
            ViewRequest r = rs[i];
            viewEvents.add(getViewEvent(r));
        }
        
        dispatchRequest((ViewEvent[])viewEvents.toArray(new ViewEvent[0]), snapshot);
    }
    
    private void processVisibilityRequest(ViewRequest visibilityRequest) {
        if(((Boolean)visibilityRequest.newValue).booleanValue()) {
            // Is visible, set snapshot
            snapshot = central.createWindowSystemSnapshot();
        } else {
            // Is invisible, clear it.
            snapshot = null;
        }
        
        dispatchRequest(new ViewEvent[] { new ViewEvent(
                visibilityRequest.source,
                visibilityRequest.type,
                visibilityRequest.oldValue,
                visibilityRequest.newValue)},
            snapshot);
    }
    
    private void dispatchRequest(ViewEvent[] viewEvents, WindowSystemSnapshot snapshot) {
        view.changeGUI(viewEvents, snapshot);
    }

    private void updateSnapshot(ViewRequest[] requests) {
        // PENDING Possibly optimization, update to snapshot only changed values
        // ans create new structure snapshot only in case it affect strucure update.
        
        long time = System.currentTimeMillis();

        WindowSystemSnapshot currentSnapshot = central.createWindowSystemSnapshot();
        snapshot.setMainWindowBoundsJoined(currentSnapshot.getMainWindowBoundsJoined());
        snapshot.setMainWindowBoundsSeparated(currentSnapshot.getMainWindowBoundsSeparated());
        snapshot.setMainWindowFrameStateJoined(currentSnapshot.getMainWindowFrameStateJoined());
        snapshot.setMainWindowFrameStateSeparated(currentSnapshot.getMainWindowFrameStateSeparated());
        snapshot.setEditorAreaState(currentSnapshot.getEditorAreaState());
        snapshot.setEditorAreaFrameState(currentSnapshot.getEditorAreaFrameState());
        snapshot.setEditorAreaBounds(currentSnapshot.getEditorAreaBounds());
        snapshot.setActiveModeSnapshot(currentSnapshot.getActiveModeSnapshot());
        snapshot.setMaximizedModeSnapshot(currentSnapshot.getMaximizedModeSnapshot());
        snapshot.setModeStructureSnapshot(currentSnapshot.getModeStructureSnapshot());
        snapshot.setToolbarConfigurationName(currentSnapshot.getToolbarConfigurationName());

        Debug.log(ViewRequestor.class,
            "Updating winsys snapshot took=" // NOI18N
            + (System.currentTimeMillis() - time) + " ms"); // NOI18N
        Debug.log(ViewRequestor.class, snapshot.toString());
    }
    
    // XXX PENDING Adjusts the source event to the view needs.
    private ViewEvent getViewEvent(ViewRequest request) {
        Object source   = request.source;
        int type        = request.type;
        Object oldValue = request.oldValue;
        Object newValue = request.newValue;

        /*if(type == View.CHANGE_ACTIVE_MODE_CHANGED) {
        } else if(type == View.CHANGE_MAXIMIZED_MODE_CHANGED) {
        } else if(type == View.CHANGE_EDITOR_AREA_BOUNDS_CHANGED) {
        } else if(type == View.CHANGE_EDITOR_AREA_STATE_CHANGED) {
        } else if(type == View.CHANGE_EDITOR_AREA_FRAME_STATE_CHANGED) {
        } else if(type == View.CHANGE_MAIN_WINDOW_BOUNDS_JOINED_CHANGED) {
        } else if(type == View.CHANGE_MAIN_WINDOW_BOUNDS_SEPARATED_CHANGED) {
        } else if(type == View.CHANGE_MAIN_WINDOW_FRAME_STATE_JOINED_CHANGED) {
        } else if(type == View.CHANGE_MAIN_WINDOW_FRAME_STATE_SEPARATED_CHANGED) {
        } else if(type == View.CHANGE_TOOLBAR_CONFIGURATION_CHANGED) {
        } else if(type == View.CHANGE_MODE_ADDED) {
        } else if(type == View.CHANGE_MODE_REMOVED) {
        } else if(type == View.CHANGE_SPLITS_CHANGED) {
        } else*/ if(type == View.CHANGE_MODE_SELECTED_TOPCOMPONENT_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
//        } else if(type == View.CHANGE_MODE_CONSTRAINTS_CHANGED) {
        } else if(type == View.CHANGE_MODE_TOPCOMPONENT_ADDED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_MODE_TOPCOMPONENT_REMOVED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_MODE_BOUNDS_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_MODE_FRAME_STATE_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_TOPCOMPONENT_ICON_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_TOPCOMPONENT_DISPLAY_NAME_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_TOPCOMPONENT_DISPLAY_NAME_ANNOTATION_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_TOPCOMPONENT_TOOLTIP_CHANGED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_TOPCOMPONENT_ACTIVATED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        } else if(type == View.CHANGE_MODE_CLOSED) {
            return new ViewEvent(((ModeImpl)source).getName(), type, oldValue, newValue);
        }

        return new ViewEvent(source, type, oldValue, newValue);
    }
    // !! AWT thread only <<
    ///////////////////////////////////////////

}

