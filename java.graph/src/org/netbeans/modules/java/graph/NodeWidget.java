/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LevelOfDetailsWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import static org.netbeans.modules.java.graph.Bundle.ACT_FixVersionConflict;
import static org.netbeans.modules.java.graph.Bundle.TIP_MultipleConflict;
import static org.netbeans.modules.java.graph.Bundle.TIP_MultipleWarning;
import static org.netbeans.modules.java.graph.Bundle.TIP_SingleConflict;
import static org.netbeans.modules.java.graph.Bundle.TIP_SingleWarning;
import static org.netbeans.modules.java.graph.DependencyGraphScene.VersionProvider.VERSION_CONFLICT;
import static org.netbeans.modules.java.graph.DependencyGraphScene.VersionProvider.VERSION_NO_CONFLICT;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Parameters;

/**
 *
 * @author mkleint
 */
class NodeWidget<I extends GraphNodeImplementation> extends Widget implements ActionListener {

    static final Color ROOT = new Color(178, 228, 255);
    static final Color DIRECTS = new Color(178, 228, 255);
    static final Color DIRECTS_CONFLICT = new Color(235, 88, 194);
    static final Color DISABLE_HIGHTLIGHT = new Color(255, 255, 194);
    static final Color HIGHTLIGHT = new Color(255, 255, 129);
    static final Color DISABLE_CONFLICT = new Color(219, 155, 153);
    static final Color CONFLICT = new Color(219, 11, 5);
    static final Color MANAGED = new Color(30, 255, 150);
    static final Color WARNING = new Color(255, 150, 20);
    static final Color DISABLE_WARNING = EdgeWidget.deriveColor(WARNING, 0.7f);

    private static final int LEFT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_TOP = 3;
    private static final int RIGHT_BOTTOM = 4;

    private static final @StaticResource String LOCK_ICON = "org/netbeans/modules/java/graph/resources/lock.png";
    private static final @StaticResource String LOCK_BROKEN_ICON = "org/netbeans/modules/java/graph/resources/lock-broken.png";
    private static final @StaticResource String BULB_ICON = "org/netbeans/modules/java/graph/resources/bulb.gif";
    private static final @StaticResource String BULB_HIGHLIGHT_ICON = "org/netbeans/modules/java/graph/resources/bulb-highlight.gif";

    private GraphNode<I> node;
    private boolean readable = false;
    private boolean enlargedFromHover = false;

    private Timer hoverTimer;
    private Color hoverBorderC;

    private Widget nodeW;
    private LabelWidget versionW;
    private Widget contentW;
    private ImageWidget lockW, fixHintW;

    private int paintState = EdgeWidget.REGULAR;

    private Font origFont;
    private Color origForeground;

    private String tooltipText;
    private final WidgetAction fixConflictAction;
    private final WidgetAction sceneHoverActionAction; 
    private final DependencyGraphScene.ScopeProvider<I> scopeProvider;
    
    // for use from FruchtermanReingoldLayout
    public double locX, locY, dispX, dispY; 
    private boolean fixed; 
    
    NodeWidget(@NonNull DependencyGraphScene scene, GraphNode<I> node, Icon icon, DependencyGraphScene.ScopeProvider<I> scopeProvider, final Action fixConflictAction, final WidgetAction sceneHoverActionAction) {        
        super(scene);
        Parameters.notNull("node", node);
        if(fixConflictAction != null) {
            Parameters.notNull("sceneHoverActionAction", sceneHoverActionAction);
        }
        this.node = node;
        this.scopeProvider = scopeProvider;
        this.fixConflictAction = fixConflictAction != null ? ActionFactory.createSelectAction(new SelectProvider() {
            @Override public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return false;
            }

            @Override public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }

            @Override
            public void select(Widget widget, Point localLocation, boolean invertSelection) {
                fixConflictAction.actionPerformed(null);
            }
        }) : null;
        this.sceneHoverActionAction = sceneHoverActionAction;
        
        setLayout(LayoutFactory.createVerticalFlowLayout());

        updateTooltip();
        initContent(scene, node.getImpl(), icon);

        hoverTimer = new Timer(500, this);
        hoverTimer.setRepeats(false);

        hoverBorderC = UIManager.getColor("TextPane.selectionBackground");
        if (hoverBorderC == null) {
            hoverBorderC = Color.GRAY;
        }
    }
    
    @Messages({"TIP_Text=<html>{0}<br>{1}</html>",
               "TIP_SingleConflict=Conflict with <b>{0}</b> version required by <b>{1}</b>",
               "TIP_SingleWarning=Warning, older version <b>{0}</b> requested by <b>{1}</b>",
               "TIP_MultipleConflict=Conflicts with:<table><thead><tr><th>Version</th><th>Artifact</th></tr></thead><tbody>",
               "TIP_MultipleWarning=Warning, older versions requested:<table><thead><tr><th>Version</th><th>Artifact</th></tr></thead><tbody>"})
    private void updateTooltip () {
        DependencyGraphScene scene = getDependencyGraphScene();
        tooltipText = Bundle.TIP_Text(node.getTooltipText(), scene.supportsVersions() ? getConflictTooltip(node) : "");
        setToolTipText(tooltipText);
    }

    public String getConflictTooltip(GraphNode<I> node) {
        DependencyGraphScene<I> scene = getDependencyGraphScene();
        StringBuilder tooltip = new StringBuilder();
        int conflictCount = 0;
        I firstConflict = null;
        int conflictType = node.getConflictType(scene::isConflict, scene::compareVersions);
        if (conflictType != VERSION_NO_CONFLICT) {
            for (I nd : node.getDuplicatesOrConflicts()) {
                if (scene.isConflict(nd)) {
                    conflictCount++;
                    if (firstConflict == null) {
                        firstConflict = nd;
                    }
                }
            }
        }

        if (conflictCount == 1) {
            I parent = firstConflict.getParent();
            String version = scene.getVersion(firstConflict);
            String requester = parent != null ? parent.getName() : "???";
            tooltip.append(conflictType == VERSION_CONFLICT ? TIP_SingleConflict(version, requester) : TIP_SingleWarning(version, requester));
        } else if (conflictCount > 1) {
            tooltip.append(conflictType == VERSION_CONFLICT ? TIP_MultipleConflict() : TIP_MultipleWarning());
            for (I nd : node.getDuplicatesOrConflicts()) {
                if (scene.isConflict(nd)) {
                    tooltip.append("<tr><td>");
                    tooltip.append(scene.getVersion(nd));
                    tooltip.append("</td>");
                    tooltip.append("<td>");
                    GraphNodeImplementation parent = nd.getParent();
                    if (parent != null) {
//                        Artifact artifact = parent.getArtifact();
//                        assert artifact != null;
                        tooltip.append(parent.getName());
                    }
                    tooltip.append("</td></tr>");
                }
            }
            tooltip.append("</tbody></table>");
        }
        return tooltip.toString();
    }
    
    private DependencyGraphScene getDependencyGraphScene() {
        return (DependencyGraphScene)getScene();
    }

    public void highlightText(String searchTerm) {
        if (searchTerm != null && node.getName().contains(searchTerm)) {
            nodeW.setBackground(HIGHTLIGHT);
            nodeW.setOpaque(true);
            setPaintState(EdgeWidget.REGULAR);
            setReadable(true);
        } else {
            //reset
            nodeW.setBackground(Color.WHITE);
            nodeW.setOpaque(false);
            setPaintState(EdgeWidget.GRAYED);
            setReadable(false);
        }
    }

    public void setPaintState (int state) {
        if (this.paintState == state) {
            return;
        }
        this.paintState = state;

        updatePaintContent();
    }

    public int getPaintState () {
        return paintState;
    }

    void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    boolean isFixed() {
        return fixed;
    }
    
    private void updatePaintContent() {
        if (origForeground == null) {
            origForeground = getForeground();
        }

        boolean isDisabled = paintState == EdgeWidget.DISABLED;

        Color foreC = origForeground;
        if (paintState == EdgeWidget.GRAYED || isDisabled) {
            foreC = UIManager.getColor("textInactiveText");
            if (foreC == null) {
                foreC = Color.LIGHT_GRAY;
            }
            if (isDisabled) {
                foreC = new Color ((int)(foreC.getAlpha() / 1.3f), foreC.getRed(),
                        foreC.getGreen(), foreC.getBlue());
            }
        }

        contentW.setBorder(BorderFactory.createLineBorder(10, foreC));
        nodeW.setForeground(foreC);
        if(versionW != null) {
            versionW.setForeground(foreC);
        }
        if (lockW != null) {
            lockW.setPaintAsDisabled(paintState == EdgeWidget.GRAYED);
            lockW.setVisible(!isDisabled);
        }

        setToolTipText(paintState != EdgeWidget.DISABLED ? tooltipText : null);

        contentW.repaint();
    }

    @Messages("ACT_FixVersionConflict=Fix Version Conflict...")
    private void initContent (DependencyGraphScene scene, GraphNodeImplementation impl, Icon icon) {
        contentW = new LevelOfDetailsWidget(scene, 0.05, 0.1, Double.MAX_VALUE, Double.MAX_VALUE);
        contentW.setBorder(BorderFactory.createLineBorder(10));
        contentW.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));

        //dependecy node name (with optional project icon on the left)
        nodeW = new Widget(scene);
        nodeW.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 4));
        if (null != icon) {
            nodeW.addChild(new ImageWidget(scene, ImageUtilities.icon2Image(icon)));
        }
        final LabelWidget labelWidget = new LabelWidget(scene, impl.getName() + "  ");
        labelWidget.setUseGlyphVector(true);
        nodeW.addChild(labelWidget);
      
        if (node.isRoot()) {
            Font defF = scene.getDefaultFont();
            nodeW.setFont(defF.deriveFont(Font.BOLD, defF.getSize() + 3f));
        }
        contentW.addChild(nodeW);
        
        if(getDependencyGraphScene().supportsVersions()) {
            Widget versionDetW = new LevelOfDetailsWidget(scene, 0.5, 0.7, Double.MAX_VALUE, Double.MAX_VALUE);
            versionDetW.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER, 2));
            contentW.addChild(versionDetW);
            versionW = new LabelWidget(scene);
            versionW.setLabel(scene.getVersion(node.getImpl()));
            int mngState = node.getManagedState();
            if (mngState != GraphNode.UNMANAGED) { 
                 lockW = new ImageWidget(scene,
                        mngState == GraphNode.MANAGED ? ImageUtilities.loadImage(LOCK_ICON) : ImageUtilities.loadImage(LOCK_BROKEN_ICON));
            }
            versionDetW.addChild(versionW);
            if (lockW != null) {
                versionDetW.addChild(lockW);
            }
        }

        // fix hint
        if (fixConflictAction != null) {
            Widget rootW = new Widget(scene);
            rootW.setLayout(LayoutFactory.createOverlayLayout());
            fixHintW = new ImageWidget(scene, ImageUtilities.loadImage(BULB_ICON));
            fixHintW.setVisible(false);
            fixHintW.setToolTipText(ACT_FixVersionConflict());
            fixHintW.getActions().addAction(sceneHoverActionAction);
            fixHintW.getActions().addAction(fixConflictAction);
            Widget panelW = new Widget(scene);
            panelW.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 0));
            panelW.setBorder(BorderFactory.createEmptyBorder(0, 3));
            panelW.addChild(fixHintW);
            rootW.addChild(panelW);
            rootW.addChild(contentW);
            addChild(rootW);
        } else {
            addChild(contentW);
        }

    }

    public void modelChanged () {
        DependencyGraphScene scene = getDependencyGraphScene();
        if(scene.supportsVersions()) {
            versionW.setLabel(scene.getVersion(node.getImpl()));
            if (fixConflictAction == null && fixHintW != null) {
                fixHintW.setVisible(false);
                fixHintW = null;
            }
        }
        
        updateTooltip();        
        
        repaint();
    }

    @Override
    protected void paintBackground() {
        super.paintBackground();

        if (paintState == EdgeWidget.DISABLED) {
            return;
        }

        DependencyGraphScene scene = getDependencyGraphScene();
        Graphics2D g = scene.getGraphics();
        Rectangle bounds = getClientArea();

        if (node.isRoot()) {
            paintBottom(g, bounds, ROOT, Color.WHITE, bounds.height / 2);
        } else {
            Color scopeC = scopeProvider != null ? scopeProvider.getColor(node.getImpl()) : null;
            if(scopeC != null) {
                paintCorner(RIGHT_BOTTOM, g, bounds, scopeC, Color.WHITE, bounds.width / 2, bounds.height / 2);
            }
            int conflictType = scene.supportsVersions() ? node.getConflictType(scene::isConflict, scene::compareVersions) : VERSION_NO_CONFLICT;
            Color leftTopC = null;
            if (conflictType != VERSION_NO_CONFLICT) {
                leftTopC = conflictType == VERSION_CONFLICT
                        ? (paintState == EdgeWidget.GRAYED ? DISABLE_CONFLICT : CONFLICT)
                        : (paintState == EdgeWidget.GRAYED ? DISABLE_WARNING : WARNING);
            } else {
                int state = node.getManagedState();
                if (GraphNode.OVERRIDES_MANAGED == state) {
                    leftTopC = WARNING;
                }
            }
            if (leftTopC != null) {
                paintCorner(LEFT_TOP, g, bounds, leftTopC, Color.WHITE, bounds.width, bounds.height / 2);
            }

            if (node.getPrimaryLevel() == 1) {
                paintBottom(g, bounds, DIRECTS, Color.WHITE, bounds.height / 6);
            }
        }

        if (getState().isHovered() || getState().isSelected()) {
            paintHover(g, bounds, hoverBorderC, getState().isSelected());
        }
    }

    private static void paintCorner (int corner, Graphics2D g, Rectangle bounds,
            Color c1, Color c2, int x, int y) {
        double h = y*y + x*x;
        int gradX = (int)(y*y*x / h);
        int gradY = (int)(y*x*x / h);

        Point startPoint = new Point();
        Point direction = new Point();
        switch (corner) {
            case LEFT_TOP:
                startPoint.x = bounds.x;
                startPoint.y = bounds.y;
                direction.x = 1;
                direction.y = 1;
            break;
            case LEFT_BOTTOM:
                startPoint.x = bounds.x;
                startPoint.y = bounds.y + bounds.height;
                direction.x = 1;
                direction.y = -1;
            break;
            case RIGHT_TOP:
                startPoint.x = bounds.x + bounds.width;
                startPoint.y = bounds.y;
                direction.x = -1;
                direction.y = 1;
            break;
            case RIGHT_BOTTOM:
                startPoint.x = bounds.x + bounds.width;
                startPoint.y = bounds.y + bounds.height;
                direction.x = -1;
                direction.y = -1;
            break;
            default:
                throw new IllegalArgumentException("Corner id not valid"); //NOI18N
        }
        
        g.setPaint(new GradientPaint(startPoint.x, startPoint.y, c1,
                startPoint.x + direction.x * gradX,
                startPoint.y + direction.y * gradY, c2));
        g.fillRect(
                Math.min(startPoint.x, startPoint.x + direction.x * x),
                Math.min(startPoint.y, startPoint.y + direction.y * y),
                x, y);
    }

    private static void paintBottom (Graphics2D g, Rectangle bounds, Color c1, Color c2, int thickness) {
        g.setPaint(new GradientPaint(bounds.x, bounds.y + bounds.height, c1,
                bounds.x, bounds.y + bounds.height - thickness, c2));
        g.fillRect(bounds.x, bounds.y + bounds.height - thickness, bounds.width, thickness);
    }

    private static void paintHover (Graphics2D g, Rectangle bounds, Color c, boolean selected) {
        g.setColor(c);
        g.drawRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
        if (!selected) {
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
        }
        g.drawRect(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        if (selected) {
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
        } else {
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 75));
        }
        g.drawRect(bounds.x + 3, bounds.y + 3, bounds.width - 6, bounds.height - 6);
    }

    @Override
    protected void notifyStateChanged(ObjectState previousState, ObjectState state) {
        super.notifyStateChanged(previousState, state);

        boolean repaintNeeded = false;
        boolean updateNeeded = false;

        if (paintState != EdgeWidget.DISABLED) {
            if (!previousState.isHovered() && state.isHovered()) {
                hoverTimer.restart();
                repaintNeeded = true;
            }

            if (previousState.isHovered() && !state.isHovered()) {
                hoverTimer.stop();
                repaintNeeded = true;
                updateNeeded = enlargedFromHover;
                enlargedFromHover = false;
            }
        }
        
        if (previousState.isSelected() != state.isSelected()) {
            updateNeeded = true;
        }

        if (updateNeeded) {
            updateContent(getDependencyGraphScene().isAnimated());
        } else if (repaintNeeded) {
            repaint();
        }

    }

    @Override public void actionPerformed(ActionEvent e) {
        enlargedFromHover = true;
        updateContent(getDependencyGraphScene().isAnimated());
    }

    public void setReadable (boolean readable) {
        if (this.readable == readable) {
            return;
        }
        this.readable = readable;
        updateContent(getDependencyGraphScene().isAnimated());
    }

    public boolean isReadable () {
        return readable;
    }

    public GraphNode getNode () {
        return node;
    }

    /**
     * readable widgets are calculated  based on scene zoom factor when zoom factor changes, the readable scope should too
     */
    public void updateReadableZoom() {
        if (isReadable()) {
            updateContent(false);
        }
    }

    private void updateContent (boolean isAnimated) {

        if (isAnimated) {
            nodeW.setPreferredBounds(nodeW.getPreferredBounds());
        }

        boolean makeReadable = getState().isSelected() || enlargedFromHover || readable;

        Font origF = getOrigFont();
        Font newF = origF;
        if (makeReadable) {
            bringToFront();
            // enlarge fonts so that content is readable
            newF = getReadable(getScene(), origF);
        }

        nodeW.setFont(newF);
        if(versionW != null) {
            versionW.setFont(newF);
        }

        if (isAnimated) {
            getScene().getSceneAnimator().animatePreferredBounds(nodeW, null);
        }

        if (fixHintW != null) {
            fixHintW.setVisible(makeReadable);
        }
    }

    private Font getOrigFont () {
        if (origFont == null) {
            origFont = nodeW.getFont();
            if (origFont == null) {
                origFont = getScene().getDefaultFont();
            }
        }
        return origFont;
    }

    public static Font getReadable (Scene scene, Font original) {
        float fSizeRatio = scene.getDefaultFont().getSize() / (float)original.getSize();
        float ratio = (float) Math.max (1, fSizeRatio / Math.max(0.0001f, scene.getZoomFactor()));
        if (ratio != 1.0f) {
            return original.deriveFont(original.getSize() * ratio);
        }
        return original;
    }

    public void bulbHovered () {
        if (fixHintW != null) {
            fixHintW.setImage(ImageUtilities.loadImage(BULB_HIGHLIGHT_ICON));
        }
    }

    public void bulbUnhovered () {
        if (fixHintW != null) {
            fixHintW.setImage(ImageUtilities.loadImage(BULB_ICON));
        }
    }
    
}
