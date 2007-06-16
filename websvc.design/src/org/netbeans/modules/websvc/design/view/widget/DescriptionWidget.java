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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.websvc.design.view.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.websvc.design.javamodel.JavadocModel;
import org.netbeans.modules.websvc.design.javamodel.MethodModel;
import org.netbeans.modules.websvc.design.view.layout.CenterRightLayout;
import org.openide.util.NbBundle;

/**
 *
 * @author Ajit
 */
public class DescriptionWidget extends AbstractTitledWidget implements TabWidget {
    
    private static final Color BORDER_COLOR = new Color(255,138,76);
    private static final int GAP = 16;
    
    private MethodModel method;
    private transient JavadocModel model;
    
    private transient Widget buttons;
    private transient ImageLabelWidget headerLabelWidget;
    private transient Widget tabComponent;
    
    /** Creates a new instance of Description
     * @param scene
     * @param method
     */
    public DescriptionWidget(Scene scene, MethodModel method) {
        super(scene,0,RADIUS,0,BORDER_COLOR_BLACK);
        this.method = method;
        model = method.getJavadoc();
        createContent();
    }
    
    protected Paint getTitlePaint(Rectangle bounds) {
        return TITLE_COLOR_DESC;
    }
    
    private void createContent() {
        populateContentWidget(getContentWidget());
        getContentWidget().setBorder(BorderFactory.createEmptyBorder(1));
        getHeaderWidget().setLayout(new CenterRightLayout(8));
        headerLabelWidget = new ImageLabelWidget(getScene(), getIcon(), getTitle());
        headerLabelWidget.getLabelWidget().setFont(getScene().getFont().deriveFont(Font.BOLD));
        getHeaderWidget().addChild(headerLabelWidget);
        
        buttons = new Widget(getScene());
        buttons.setLayout(LayoutFactory.createHorizontalFlowLayout(
                LayoutFactory.SerialAlignment.JUSTIFY, 8));
        buttons.addChild(getExpanderWidget());
        getHeaderWidget().addChild(buttons);
    }
    
    private void populateContentWidget(Widget parentWidget) {
        EditorPaneWidget descPaneWidget = new EditorPaneWidget(getScene(),model.getText(),"text/java");
        descPaneWidget.setBorder(BorderFactory.createBevelBorder(false));
        parentWidget.addChild(descPaneWidget);
    }
    
    public Object hashKey() {
        return method==null?null:method.getOperationName()+"_Description";
    }
    
    public String getTitle() {
        return NbBundle.getMessage(DescriptionWidget.class, "LBL_Description");
    }
    
    public Image getIcon() {
        return null;
    }
    
    public Widget getComponentWidget() {
        if(tabComponent==null) {
            tabComponent = createContentWidget();
            tabComponent.setBorder(BorderFactory.createEmptyBorder());
            populateContentWidget(tabComponent);
        }
        return tabComponent;
    }
}
