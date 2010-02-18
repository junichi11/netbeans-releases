/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.spi.debugger.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseCaret;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.highlighting.HighlightAttributeValue;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;

import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Support for Step Into action implementations. It allows the user to select
 * directly in a source file a method call the debugger should step into.
 * A simple graphical interface is provided. The user can navigate among available
 * method calls. The navigation can be done using a keyboard as well as a mouse.
 *
 * <p>The method chooser is initialized by an url (pointing to a source file), an array of
 * {@link Segment} elements (each of them corresponds typically to a method call name
 * in the source file) and an index of the segment element which is displayed
 * as the default selection.
 *
 * <p>Optionally, two sets of (additional) shortcuts that confirm, resp. cancel the selection
 * mode can be specified.
 * It is also possible to pass a text, which should be shown at the editor pane's
 * status line after the selection mode has been activated. This text serves as a hint
 * to the user how to make the method call selection.
 *
 * <p>Method chooser does not use any special highlighting for the background of the
 * area where the selection takes place. If it is required it can be done by attaching
 * instances of {@link Annotation} to the proper source file's lines. These annotation should
 * be added before calling {@link #showUI} and removed after calling {@link #releaseUI}.
 *
 * <p>To display the method chooser's ui correctly, it is required to register
 * {@link HighlightsLayerFactory} created by {@link #createHighlihgtsLayerFactory}
 * in an xml layer. An example follows.
 *
 * <pre class="examplecode">
    &lt;folder name=&quot;Editors&quot;&gt;
        &lt;folder name=&quot;text&quot;&gt;
            &lt;folder name=&quot;x-java&quot;&gt;
                &lt;file name=&quot;org.netbeans.spi.editor.highlighting.HighlightsLayerFactory.instance&quot;&gt;
                    &lt;attr name=&quot;instanceCreate&quot; methodvalue=&quot;org.netbeans.spi.debugger.ui.MethodChooser.createHighlihgtsLayerFactory&quot;/&gt;
                &lt;/file&gt;
            &lt;/folder&gt;
        &lt;/folder&gt;
    &lt;/folder&gt;</pre>
 * <code>"x-java"</code> should be replaced by the targeted mime type.
 *
 * @author Daniel Prusa
 * @since 2.22
 */
public class MethodChooser {

    private static AttributeSet defaultHyperlinkHighlight;

    private String url;
    private Segment[] segments;
    private int selectedIndex = -1;
    private String hintText;
    private KeyStroke[] stopEvents;
    private KeyStroke[] confirmEvents;
    
    private AttributeSet attribsLeft = null;
    private AttributeSet attribsRight = null;
    private AttributeSet attribsMiddle = null;
    private AttributeSet attribsAll = null;
    
    private AttributeSet attribsArea = null;
    private AttributeSet attribsMethod = null;
    private AttributeSet attribsHyperlink = null;

    private Cursor handCursor;
    private Cursor arrowCursor;
    private Cursor originalCursor;

    private CentralListener mainListener;
    private Document doc;
    private JEditorPane editorPane;
    private List<ReleaseListener> releaseListeners = new ArrayList<ReleaseListener>();

    private int startLine;
    private int endLine;
    private int mousedIndex = -1;
    private boolean isInSelectMode = false;

    /**
     * Creates an instance of {@link MethodChooser}.
     *
     * @param url Url of the source file.
     * @param segments Array of segments where each of the segments represents one method
     *      call. The user traverses the calls in the order given by the array.
     * @param initialIndex Index of a call that should be preselected when the method chooser
     *      is shown.
     */
    public MethodChooser(String url, Segment[] segments, int initialIndex) {
        this(url, segments, initialIndex, null, new KeyStroke[0], new KeyStroke[0]);
    }

    /**
     * Creates an instance of {@link MethodChooser}. Supports optional parameters.
     *
     * @param url Url of the source file.
     * @param segments Array of segments where each of the segments represents one method
     *      call. The user traverses the calls in the order given by the array.
     * @param initialIndex Index of a call that should be preselected when the method chooser
     *      is shown.
     * @param hintText Text which is displayed in the editor pane's status line. Serves as a hint
     *      informing briefly the user how to make a selection.
     * @param stopEvents Custom key strokes which should stop the selection mode.
     *      For example, it is possible to pass a {@link KeyStroke} corresponding to
     *      the shortcut of Step Over action. Then, whenever the shorcut is pressed, the selection
     *      mode is cancelled. The generated {@link KeyEvent} is not consumed thus can be
     *      handled and invokes Step Over action.
     *      Note that a method chooser can be always cancelled by Esc or by clicking outside the
     *      visualized area in the source editor.
     * @param confirmEvents Custom key strokes which confirm the current selection.
     *      By default, a selection can be confirmed by Enter or Space Bar. It is possible
     *      to extend this set of confirmation keys.
     */
    public MethodChooser(String url, Segment[] segments, int initialIndex, String hintText,
            KeyStroke[] stopEvents, KeyStroke[] confirmEvents) {
        this.url = url;
        this.segments = segments;
        this.selectedIndex = initialIndex;
        this.hintText = hintText;
        if (stopEvents == null) {
            stopEvents = new KeyStroke[0];
        }
        if (confirmEvents == null) {
            confirmEvents = new KeyStroke[0];
        }
        this.stopEvents = stopEvents;
        this.confirmEvents = confirmEvents;
    }

    /**
     * Sets up and displays the method selection mode.
     *
     * @return <code>true</code> if a {@link JEditorPane} has been found and the selection mode
     *          has been properly displayed
     */
    public boolean showUI() {
        findEditorPane();
        if (editorPane == null) {
            return false; // cannot do anything without editor
        }
        doc = editorPane.getDocument();
        // compute start line and end line
        int minOffs = Integer.MAX_VALUE;
        int maxOffs = 0;
        for (int x = 0; x < segments.length; x++) {
            minOffs = Math.min(segments[x].getStartOffset(), minOffs);
            maxOffs = Math.max(segments[x].getEndOffset(), maxOffs);
        }
        try {
            startLine = Utilities.getLineOffset((BaseDocument)doc, minOffs) + 1;
            endLine = Utilities.getLineOffset((BaseDocument)doc, maxOffs) + 1;
        } catch (BadLocationException e) {
        }
        // continue by showing method selection ui
        mainListener = new CentralListener();
        editorPane.putClientProperty(MethodChooser.class, this);
        editorPane.addKeyListener(mainListener);
        editorPane.addMouseListener(mainListener);
        editorPane.addMouseMotionListener(mainListener);
        editorPane.addFocusListener(mainListener);
        originalCursor = editorPane.getCursor();
        handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        arrowCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        editorPane.setCursor(arrowCursor);
        Caret caret = editorPane.getCaret();
        if (caret instanceof BaseCaret) {
            ((BaseCaret)caret).setVisible(false);
        }
        requestRepaint();
        if (hintText != null && hintText.trim().length() > 0) {
            Utilities.setStatusText(editorPane, " " + hintText);
        }
        isInSelectMode = true;
        return true;
    }

    /**
     * Ends the method selection mode, clears all used ui elements. Notifies each registered
     * {@link ReleaseListener}.
     *
     * @param performAction <code>true</code> indicates that the current selection should
     *          be used to perform an action, <code>false</code> means that the selection mode
     *          has beencancelled
     */
    public synchronized void releaseUI(boolean performAction) {
        if (!isInSelectMode) {
            return; // do nothing
        }
        getHighlightsBag(doc).clear();
        editorPane.removeKeyListener(mainListener);
        editorPane.removeMouseListener(mainListener);
        editorPane.removeMouseMotionListener(mainListener);
        editorPane.removeFocusListener(mainListener);
        editorPane.putClientProperty(MethodChooser.class, null);
        editorPane.setCursor(originalCursor);
        Caret caret = editorPane.getCaret();
        if (caret instanceof BaseCaret) {
            ((BaseCaret)caret).setVisible(true);
        }

        if (hintText != null && hintText.trim().length() > 0) {
            Utilities.clearStatusText(editorPane);
        }
        isInSelectMode = false;
        for (ReleaseListener listener : releaseListeners) {
            listener.released(performAction);
        }
    }

    /**
     * Can be used to check whether the selection mode is activated.
     *
     * @return <code>true</code> if the method selection mode is currently displayed
     */
    public boolean isUIActive() {
        return isInSelectMode;
    }

    /**
     * Returns index of {@link Segment} that is currently selected. If the method
     * chooser has been released, it corresponds to the final selection made by the user.
     *
     * @return index of currently selected method
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Registers {@link ReleaseListener}. The listener is notified when the selection
     * mode finishes. This occurs whenever the user comfirms (or cancels) the current
     * selection. It also occrus when {@link #releaseUI} is called.
     *
     * @param listener an instance of {@link ReleaseListener} to be registered
     */
    public synchronized void addReleaseListener(ReleaseListener listener) {
        releaseListeners.add(listener);
    }

    /**
     * Unregisters {@link ReleaseListener}.
     *
     * @param listener an instance of {@link ReleaseListener} to be unregistered
     */
    public synchronized void removeReleaseListener(ReleaseListener listener) {
        releaseListeners.remove(listener);
    }

    /**
     * This method should be referenced in xml layer files. To display the method
     * chooser ui correctly, it is required to register an instance of
     * {@link HighlightsLayerFactory} using the following pattern.
     * <pre class="examplecode">
    &lt;folder name=&quot;Editors&quot;&gt;
        &lt;folder name=&quot;text&quot;&gt;
            &lt;folder name=&quot;x-java&quot;&gt;
                &lt;file name=&quot;org.netbeans.spi.editor.highlighting.HighlightsLayerFactory.instance&quot;&gt;
                    &lt;attr name=&quot;instanceCreate&quot; methodvalue=&quot;org.netbeans.spi.debugger.ui.MethodChooser.createHighlihgtsLayerFactory&quot;/&gt;
                &lt;/file&gt;
            &lt;/folder&gt;
        &lt;/folder&gt;
    &lt;/folder&gt;</pre>
     * <code>"x-java"</code> should be replaced by the targeted mime type
     *
     * @return highligts layer factory that handles method chooser ui visualization
     */
    public static HighlightsLayerFactory createHighlihgtsLayerFactory() {
        return new MethodChooserHighlightsLayerFactory();
    }

    static OffsetsBag getHighlightsBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(MethodChooser.class);
        if (bag == null) {
            doc.putProperty(MethodChooser.class, bag = new OffsetsBag(doc, true));
        }
        return bag;
    }

    private void findEditorPane() {
        editorPane = null;
        FileObject file;
        try {
            file = URLMapper.findFileObject(new URL(url));
        } catch (MalformedURLException e) {
            return;
        }
        if (file == null) {
            return;
        }
        DataObject dobj = null;
        try {
            dobj = DataObject.find(file);
        } catch (DataObjectNotFoundException ex) {
        }
        if (dobj == null) {
            return;
        }
        final EditorCookie ec = (EditorCookie) dobj.getCookie(EditorCookie.class);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    JEditorPane[] openedPanes = ec.getOpenedPanes();
                    if (openedPanes != null) {
                        editorPane = openedPanes[0];
                    }
                }
            });
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void requestRepaint() {
        if (attribsLeft == null) {
            Color foreground = editorPane.getForeground();

            attribsLeft = createAttribs(EditorStyleConstants.LeftBorderLineColor, foreground, EditorStyleConstants.TopBorderLineColor, foreground, EditorStyleConstants.BottomBorderLineColor, foreground);
            attribsRight = createAttribs(EditorStyleConstants.RightBorderLineColor, foreground, EditorStyleConstants.TopBorderLineColor, foreground, EditorStyleConstants.BottomBorderLineColor, foreground);
            attribsMiddle = createAttribs(EditorStyleConstants.TopBorderLineColor, foreground, EditorStyleConstants.BottomBorderLineColor, foreground);
            attribsAll = createAttribs(EditorStyleConstants.LeftBorderLineColor, foreground, EditorStyleConstants.RightBorderLineColor, foreground, EditorStyleConstants.TopBorderLineColor, foreground, EditorStyleConstants.BottomBorderLineColor, foreground);

            attribsHyperlink = getHyperlinkHighlight();
            
            attribsMethod = createAttribs(StyleConstants.Foreground, foreground,
                    StyleConstants.Bold, Boolean.TRUE);
            
            attribsArea = createAttribs(
                    StyleConstants.Foreground, foreground,
                    StyleConstants.Italic, Boolean.FALSE,
                    StyleConstants.Bold, Boolean.FALSE);
        }
        
        OffsetsBag newBag = new OffsetsBag(doc, true);
        int start = segments[0].getStartOffset();
        int end = segments[segments.length - 1].getEndOffset();
        newBag.addHighlight(start, end, attribsArea);
        
        for (int i = 0; i < segments.length; i++) {
            int startOffset = segments[i].getStartOffset();
            int endOffset = segments[i].getEndOffset();
            newBag.addHighlight(startOffset, endOffset, attribsMethod);
            if (selectedIndex == i) {
                int size = endOffset - startOffset;
                if (size == 1) {
                    newBag.addHighlight(startOffset, endOffset, attribsAll);
                } else if (size > 1) {
                    newBag.addHighlight(startOffset, startOffset + 1, attribsLeft);
                    newBag.addHighlight(endOffset - 1, endOffset, attribsRight);
                    if (size > 2) {
                        newBag.addHighlight(startOffset + 1, endOffset - 1, attribsMiddle);
                    }
                }
            }
            if (mousedIndex == i) {
                AttributeSet attr = AttributesUtilities.createComposite(
                    attribsHyperlink,
                    AttributesUtilities.createImmutable(EditorStyleConstants.Tooltip, new TooltipResolver())
                );
                newBag.addHighlight(startOffset, endOffset, attr);
            }
        }
        
        OffsetsBag bag = getHighlightsBag(doc);
        bag.setHighlights(newBag);
    }

    private AttributeSet createAttribs(Object... keyValuePairs) {
        List<Object> list = new ArrayList<Object>();
        for (int i = keyValuePairs.length / 2 - 1; i >= 0; i--) {
            Object attrKey = keyValuePairs[2 * i];
            Object attrValue = keyValuePairs[2 * i + 1];

            if (attrKey != null && attrValue != null) {
                list.add(attrKey);
                list.add(attrValue);
            }
        }
        return AttributesUtilities.createImmutable(list.toArray());
    }

    private AttributeSet getHyperlinkHighlight() {
        synchronized(this) {
            if (defaultHyperlinkHighlight == null) {
                defaultHyperlinkHighlight = AttributesUtilities.createImmutable(
                        StyleConstants.Foreground, Color.BLUE, StyleConstants.Underline, Color.BLUE);
            }
        }
        return defaultHyperlinkHighlight;
    }
    
    // **************************************************************************
    // public inner classes
    // **************************************************************************

    /**
     * Represents an interval of offsets in a document. Used to pass entry points
     * of method calls among which the user selects the desired one to step into.
     */
    public static class Segment {
        int startOffset;
        int endOffset;

        /**
         * Creates a new instance of {@link Segment}.
         *
         * @param startOffset segment start offset (inclusive)
         * @param endOffset segment end offset (exclusive)
         */
        public Segment(int startOffset, int endOffset) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        /**
         * Returns the start offset.
         *
         * @return the start offset
         */
        public int getStartOffset() {
            return startOffset;
        }

        /**
         * Returns the endt offset.
         *
         * @return the end offset
         */
        public int getEndOffset() {
            return endOffset;
        }

    }

    /**
     * An instance of {@link ReleaseListener} can be registered using {@link MethodChooser#addReleaseListener}.
     * It is notified when the selection mode finishes (e.g. if the user confirms the current selection).
     * The selection mode finishes whenever {@link #releaseUI} is called.
     */
    public interface ReleaseListener {

        /**
         * Called on the method selection mode finish.
         *
         * @param performAction <code>true</code> means that the current selection has been confirmed
         *          and the proper action (Step Into) is expected to be performed, <code>false</code>
         *          means that the method chooser has been cancelled
         */
        public void released(boolean performAction);

    }

    // **************************************************************************
    // private inner classes
    // **************************************************************************

    private class CentralListener implements KeyListener, MouseListener, MouseMotionListener, FocusListener {

        // **************************************************************************
        // KeyListener implementation
        // **************************************************************************

        @Override
        public void keyTyped(KeyEvent e) {
            e.consume();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            boolean consumeEvent = true;
            switch (code) {
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    // selection confirmed
                    releaseUI(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    // action canceled
                    releaseUI(false);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_TAB:
                    selectedIndex++;
                    if (selectedIndex == segments.length) {
                        selectedIndex = 0;
                    }
                    requestRepaint();
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_UP:
                    selectedIndex--;
                    if (selectedIndex < 0) {
                        selectedIndex = segments.length - 1;
                    }
                    requestRepaint();
                    break;
                case KeyEvent.VK_HOME:
                    selectedIndex = 0;
                    requestRepaint();
                    break;
                case KeyEvent.VK_END:
                    selectedIndex = segments.length - 1;
                    requestRepaint();
                    break;
                default:
                    int mods = e.getModifiersEx();
                    for (int x = 0; x < stopEvents.length; x++) {
                        if (stopEvents[x].getKeyCode() == code &&
                                (stopEvents[x].getModifiers() & mods) == stopEvents[x].getModifiers()) {
                            releaseUI(false);
                            consumeEvent = false;
                            break;
                        }
                    }
                    for (int x = 0; x < confirmEvents.length; x++) {
                        if (confirmEvents[x].getKeyCode() == code &&
                                (confirmEvents[x].getModifiers() & mods) == confirmEvents[x].getModifiers()) {
                            releaseUI(true);
                            break;
                        }
                    }
            }
            if (consumeEvent) {
                e.consume();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            e.consume();
        }

        // **************************************************************************
        // MouseListener and MouseMotionListener implementation
        // **************************************************************************

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.isPopupTrigger()) {
                return;
            }
            e.consume();
            int position = editorPane.viewToModel(e.getPoint());
            if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                if (position < 0) {
                    return;
                }
                if (mousedIndex != -1) {
                    selectedIndex = mousedIndex;
                    releaseUI(true);
                    return;
                }
            }
            try {
                int line = Utilities.getLineOffset((BaseDocument) doc, position) + 1;
                if (line < startLine || line > endLine) {
                    releaseUI(false);
                    return;
                }
            } catch (BadLocationException ex) {
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            e.consume();
            int position = editorPane.viewToModel(e.getPoint());
            int newIndex = -1;
            if (position >= 0) {
                for (int x = 0; x < segments.length; x++) {
                    int start = segments[x].getStartOffset();
                    int end = segments[x].getEndOffset();
                    if (position >= start && position <= end) {
                        newIndex = x;
                        break;
                    }
                } // for
            } // if
            if (newIndex != mousedIndex) {
                if (newIndex == -1) {
                    editorPane.setCursor(arrowCursor);
                } else {
                    editorPane.setCursor(handCursor);
                }
                mousedIndex = newIndex;
                requestRepaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            e.consume();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            e.consume();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            e.consume();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            e.consume();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            e.consume();
        }

        // **************************************************************************
        // FocusListener implementation
        // **************************************************************************

        @Override
        public void focusGained(FocusEvent e) {
            editorPane.getCaret().setVisible(false);
        }

        @Override
        public void focusLost(FocusEvent e) {
        }

    }

    static class MethodChooserHighlightsLayerFactory implements HighlightsLayerFactory {

        @Override
        public HighlightsLayer[] createLayers(Context context) {
            return new HighlightsLayer[] {
                HighlightsLayer.create(MethodChooser.class.getName(),
                        ZOrder.TOP_RACK, false, MethodChooser.getHighlightsBag(context.getDocument()))
            };
        }

    }

    private static final class TooltipResolver implements HighlightAttributeValue<String> {

        public TooltipResolver() {
        }

        @Override
        public String getValue(JTextComponent component, Document document, Object attributeKey, int startOffset, int endOffset) {
            return NbBundle.getMessage(MethodChooser.class, "MSG_Step_Into_Method");
        }

    }

}
