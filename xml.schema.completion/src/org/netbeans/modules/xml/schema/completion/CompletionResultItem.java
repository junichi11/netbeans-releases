/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.schema.completion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.text.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.netbeans.editor.Utilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;

import org.netbeans.editor.ext.ExtFormatter;
import org.netbeans.modules.xml.axi.AXIComponent;
import org.netbeans.modules.xml.axi.Element;
import org.netbeans.modules.xml.axi.Attribute;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Samaresh (Samaresh.Panda@Sun.Com)
 */
public abstract class CompletionResultItem implements CompletionItem {

    /**
     * Creates a new instance of CompletionUtil
     */
    public CompletionResultItem(AXIComponent component, String typedChars) {
        this.axiComponent = component;
        this.typedChars = typedChars;
    }
        
    Icon getIcon(){
        return icon;
    }
    
    public AXIComponent getAXIComponent() {
        return axiComponent;
    }
    
    /**
     * The text user sees in the CC list.
     * Implementors should initialize the displayText here and return.
     */
    public abstract String getItemText();
    
    /**
     * Insert following text into document.
     */
    public String getReplacementText(){
        return replacementText;
    }
    
    public String toString() {
        return getItemText();
    }
        
    Color getPaintColor() { return Color.BLUE; }
    
    /**
     * Actually replaces a piece of document by passes text.
     * @param component a document source
     * @param text a string to be inserted
     * @param offset the target offset
     * @param len a length that should be removed before inserting text
     */
    private boolean replaceText( JTextComponent component, String text, int offset, int len) {
        BaseDocument doc = (BaseDocument)component.getDocument();
        doc.atomicLock();
        try {
            doc.remove( offset, len );
            doc.insertString( offset, text, null);
            //reformat the line
            ((ExtFormatter)doc.getFormatter()).reformat(doc, Utilities.getRowStart(doc, offset), offset+text.length(), true);
        } catch( BadLocationException exc ) {
            return false;    //not sucessfull
        } catch (IOException e) {
            return false;
        } finally {
            doc.atomicUnlock();
        }
        return true;
    }
    ////////////////////////////////////////////////////////////////////////////////
    ///////////////////methods from CompletionItem interface////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new DocumentationQuery(this));
    }
    
    public CompletionTask createToolTipTask() {
        return new AsyncCompletionTask(new ToolTipQuery(this));
    }
    
    public void defaultAction(JTextComponent component) {
        int charsToRemove = (typedChars==null)?0:typedChars.length();
        int substOffset = component.getCaretPosition() - charsToRemove;
        
        if(!shift) Completion.get().hideAll();
        replaceText(component, getReplacementText(), substOffset, charsToRemove);
    }
        
    public CharSequence getInsertPrefix() {
        return getItemText();
    }
    
    public abstract Component getPaintComponent(boolean isSelected);
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        Component renderComponent = getPaintComponent(false);
        return renderComponent.getPreferredSize().width;
    }
    
    public int getSortPriority() {
        return 0;
    }
    
    public CharSequence getSortText() {
        return getItemText();
    }
    
    public boolean instantSubstitution(JTextComponent component) {
        defaultAction(component);
        return true;
    }
    
    public void processKeyEvent(KeyEvent e) {
        shift = (e.getKeyCode() == KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED && e.isShiftDown());
    }
    
    public void render(Graphics g, Font defaultFont, 
            Color defaultColor, Color backgroundColor,
            int width, int height, boolean selected) {
        Component renderComponent = getPaintComponent(selected);
        renderComponent.setFont(defaultFont);
        renderComponent.setForeground(defaultColor);
        renderComponent.setBackground(backgroundColor);
        renderComponent.setBounds(0, 0, width, height);
        ((CompletionPaintComponent)renderComponent).paintComponent(g);
    }
        
    protected boolean shift = false;
    protected String typedChars;
    protected String replacementText;
    protected String displayText;
    protected javax.swing.Icon icon;
    protected CompletionPaintComponent component;
    protected AXIComponent axiComponent;
    
    private static Color foreground = Color.black;
    private static Color background = Color.white;
    private static Color selectionForeground = Color.black;
    private static Color selectionBackground = new Color(204, 204, 255);    
    private static final int XML_ITEMS_SORT_PRIORITY = 20;
    
    public static final String ICON_ELEMENT    = "element.png"; //NOI18N
    public static final String ICON_ATTRIBUTE  = "attribute.png"; //NOI18N
    public static final String ICON_LOCATION   = "/org/netbeans/modules/xml/schema/completion/resources/"; //NOI18N
}
