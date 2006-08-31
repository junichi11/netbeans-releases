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
package org.netbeans.api.visual.action;

import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.modules.visual.action.*;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kaspar
 */
public final class ActionFactory {

    private static final BasicStroke STROKE = new BasicStroke (1.0f, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_BUTT, 5.0f, new float[] { 6.0f, 3.0f }, 0.0f);

    private static final MoveStrategy MOVE_STRATEGY_FREE = new MoveStrategy () {
        public Point locationSuggested (Widget widget, Point originalLocation, Point suggestedLocation) {
            return suggestedLocation;
        }
    };

    private static final MoveProvider MOVE_PROVIDER_DEFAULT = new MoveProvider () {
        public void movementStarted (Widget widget) {
        }
        public void movementFinished (Widget widget) {
        }
        public Point getOriginalLocation (Widget widget) {
            return widget.getPreferredLocation ();
        }
        public void setNewLocation (Widget widget, Point location) {
            widget.setPreferredLocation (location);
        }
    };

    private static final AlignWithMoveDecorator ALIGN_WITH_MOVE_DECORATOR_DEFAULT = new AlignWithMoveDecorator() {
        public ConnectionWidget createLineWidget (Scene scene) {
            ConnectionWidget widget = new ConnectionWidget (scene);
            widget.setStroke (STROKE);
            widget.setForeground (Color.BLUE);
            return widget;
        }
    };

    private static final MoveControlPointProvider MOVE_CONTROL_POINT_PROVIDER_FREE = new FreeMoveControlPointProvider ();

    private static final MoveControlPointProvider MOVE_CONTROL_POINT_PROVIDER_ORTHOGONAL = new OrthogonalMoveControlPointProvider ();

    private static final ConnectDecorator CONNECT_DECORATOR_DEFAULT = new ConnectDecorator() {
        public ConnectionWidget createConnectionWidget (Scene scene) {
            ConnectionWidget widget = new ConnectionWidget (scene);
            widget.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
            return widget;
        }
        public Anchor createSourceAnchor (Widget sourceWidget) {
            return AnchorFactory.createCenterAnchor (sourceWidget);
        }
        public Anchor createTargetAnchor (Widget targetWidget) {
            return AnchorFactory.createCenterAnchor (targetWidget);
        }
        public Anchor createFloatAnchor (Point location) {
            return AnchorFactory.createFixedAnchor (location);
        }
    };

    private static final ReconnectDecorator RECONNECT_DECORATOR_DEFAULT = new ReconnectDecorator () {
        public Anchor createReplacementWidgetAnchor (Widget replacementWidget) {
            return AnchorFactory.createCenterAnchor (replacementWidget);
        }
        public Anchor createFloatAnchor (Point location) {
            return AnchorFactory.createFixedAnchor (location);
        }
    };

    private static final ResizeProvider RESIZE_PROVIDER_DEFAULT = new ResizeProvider() {
        public void resizingStarted (Widget widget) {
        }
        public void resizingFinished (Widget widget) {
        }
    };

    private static final ResizeStrategy RESIZE_STRATEGY_FREE = new ResizeStrategy () {
        public Rectangle boundsSuggested (Widget widget, Rectangle originalBounds, Rectangle suggestedBounds, ResizeProvider.ControlPoint controlPoint) {
            return suggestedBounds;
        }
    };

    private static final WidgetAction MOVE_CONTROL_POINT_ACTION_FREE = createMoveControlPointAction (createFreeMoveControlPointProvider ());

    private static final WidgetAction MOVE_CONTROL_POINT_ACTION_ORTHOGONAL = createMoveControlPointAction (createOrthogonalMoveControlPointProvider ());

    private static final WidgetAction MOVE_ACTION = createMoveAction (null, null);

    private static final WidgetAction RESIZE_ACTION = createResizeAction (null, null);

    private ActionFactory () {
    }

    public static WidgetAction createAcceptAction (AcceptProvider provider) {
        assert provider != null;
        return new AcceptAction (provider);
    }

    public static WidgetAction createAlignWithMoveAction (LayerWidget collectionLayer, LayerWidget interractionLayer, AlignWithMoveDecorator decorator) {
        assert collectionLayer != null;
        return createAlignWithMoveAction (new SingleLayerAlignWithWidgetCollector (collectionLayer), interractionLayer, decorator != null ? decorator : ALIGN_WITH_MOVE_DECORATOR_DEFAULT);
    }

    public static WidgetAction createAlignWithMoveAction (AlignWithWidgetCollector collector, LayerWidget interractionLayer, AlignWithMoveDecorator decorator) {
        assert collector != null  &&  interractionLayer != null  &&  decorator != null;
        AlignWithMoveStrategyProvider sp = new AlignWithMoveStrategyProvider (collector, interractionLayer, decorator);
        return createMoveAction (sp, sp);
    }

    public static WidgetAction createConnectAction (LayerWidget interractionLayer, ConnectProvider provider) {
        return createConnectAction (null, interractionLayer, provider);
    }

    public static WidgetAction createConnectAction (ConnectDecorator decorator, LayerWidget interractionLayer, ConnectProvider provider) {
        assert interractionLayer != null  &&  provider != null;
        return new ConnectAction (decorator != null ? decorator : createDefaultConnectDecorator (), interractionLayer, provider);
    }

    public static WidgetAction createEditAction (EditProvider provider) {
        assert provider != null;
        return new EditAction (provider);
    }

    public static WidgetAction createHoverAction (HoverProvider provider) {
        assert provider != null;
        return new MouseHoverAction (provider);
    }

    public static WidgetAction createHoverAction  (TwoStateHoverProvider provider) {
        assert provider != null;
        return new TwoStatedMouseHoverAction (provider);
    }

    public static WidgetAction createInplaceEditorAction (TextFieldInplaceEditor editor) {
        return createInplaceEditorAction (new TextFieldInplaceEditorProvider (editor));
    }

    public static <C extends JComponent> WidgetAction createInplaceEditorAction (InplaceEditorProvider<C> provider) {
        return new InplaceEditorAction<C> (provider);
    }

    public static WidgetAction createMoveAction () {
        return MOVE_ACTION;
    }

    public static WidgetAction createMoveAction (MoveStrategy strategy, MoveProvider provider) {
        return new MoveAction (strategy != null ? strategy : createFreeMoveStrategy (), provider != null ? provider : createDefaultMoveProvider ());
    }

    public static WidgetAction createFreeMoveControlPointAction () {
        return MOVE_CONTROL_POINT_ACTION_FREE;
    }

    public static WidgetAction createOrthogonalMoveControlPointAction () {
        return MOVE_CONTROL_POINT_ACTION_ORTHOGONAL;
    }

    public static WidgetAction createMoveControlPointAction (MoveControlPointProvider provider) {
        assert provider != null;
        return new MoveControlPointAction (provider);
    }

    public static WidgetAction createPanAction () {
        return new PanAction ();
    }

    public static WidgetAction createPopupMenuAction (final PopupMenuProvider provider) {
        assert provider != null;
        return new PopupMenuAction (provider);
    }

    public static WidgetAction createReconnectAction (ReconnectProvider provider) {
        return createReconnectAction (null, provider);
    }

    public static WidgetAction createReconnectAction (ReconnectDecorator decorator, ReconnectProvider provider) {
        return new ReconnectAction (decorator != null ? decorator : createDefaultReconnectDecorator (), provider);
    }

    public static WidgetAction createRectangularSelectAction (ObjectScene scene, LayerWidget interractionLayer) {
        assert scene != null;
        return createRectangularSelectAction (ActionFactory.createDefaultRectangularSelectDecorator (scene), interractionLayer, ActionFactory.createObjectSceneRectangularSelectProvider (scene));
    }

    public static WidgetAction createRectangularSelectAction (RectangularSelectDecorator decorator, LayerWidget interractionLayer, RectangularSelectProvider provider) {
        assert decorator != null  &&  interractionLayer != null  &&  provider != null;
        return new RectangularSelectAction (decorator, interractionLayer, provider);
    }

    public static WidgetAction createResizeAction () {
        return RESIZE_ACTION;
    }

    public static WidgetAction createResizeAction (ResizeStrategy strategy, ResizeProvider provider) {
        return new ResizeAction (strategy != null ? strategy : createFreeResizeStategy (), provider != null ? provider : createDefaultResizeProvider ());
    }

    public static WidgetAction createSelectAction (SelectProvider provider) {
        assert provider != null;
        return new SelectAction (provider);
    }

    public static WidgetAction createSwitchCardAction (Widget cardLayoutWidget) {
        assert cardLayoutWidget != null;
        return new SelectAction (new SwitchCardProvider (cardLayoutWidget));
    }

    public static WidgetAction createZoomAction () {
        return createZoomAction (1.2, true);
    }

    public static WidgetAction createZoomAction (double zoomMultiplier, boolean animated) {
        return new ZoomAction (zoomMultiplier, animated);
    }

    public static MoveStrategy createFreeMoveStrategy () {
        return MOVE_STRATEGY_FREE;
    }

    public static MoveStrategy createSnapToGripMoveStrategy (int horizontalGridSize, int verticalGridSize) {
        assert horizontalGridSize > 0  &&  verticalGridSize > 0;
        return new SnapToGridMoveStrategy (horizontalGridSize, verticalGridSize);
    }

    public static MoveProvider createDefaultMoveProvider () {
        return MOVE_PROVIDER_DEFAULT;
    }

    public static AlignWithMoveDecorator createDefaultAlignWithMoveDecorator () {
        return ALIGN_WITH_MOVE_DECORATOR_DEFAULT;
    }

    public static MoveControlPointProvider createFreeMoveControlPointProvider () {
        return MOVE_CONTROL_POINT_PROVIDER_FREE;
    }

    public static MoveControlPointProvider createOrthogonalMoveControlPointProvider () {
        return MOVE_CONTROL_POINT_PROVIDER_ORTHOGONAL;
    }

    public static RectangularSelectDecorator createDefaultRectangularSelectDecorator (Scene scene) {
        assert scene != null;
        return new DefaultRectangularSelectDecorator (scene);
    }

    public static RectangularSelectProvider createObjectSceneRectangularSelectProvider (ObjectScene scene) {
        assert scene != null;
        return new ObjectSceneRectangularSelectProvider (scene);
    }

    public static ConnectDecorator createDefaultConnectDecorator () {
        return CONNECT_DECORATOR_DEFAULT;
    }

    public static ReconnectDecorator createDefaultReconnectDecorator () {
        return RECONNECT_DECORATOR_DEFAULT;
    }

    public static ResizeStrategy createFreeResizeStategy () {
        return RESIZE_STRATEGY_FREE;
    }

    public static ResizeProvider createDefaultResizeProvider () {
        return RESIZE_PROVIDER_DEFAULT;
    }

}
