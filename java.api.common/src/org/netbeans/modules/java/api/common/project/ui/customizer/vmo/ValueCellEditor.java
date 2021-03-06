/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 */
package org.netbeans.modules.java.api.common.project.ui.customizer.vmo;

import org.netbeans.api.annotations.common.NonNull;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Rastislav Komara
 */
public class ValueCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private JTextField textEditor;
    private JCheckBox booleanEditor;
    private JLabel label;
    private JCheckBox booleanLabel;
    private static final String EMPTY_TEXT = ""; //NOI18N
    private JComponent currentEditor;
    private int editedRow, editedColumn;
    private SoftReference<JTable> lastTable;    
    private static final String DEFAULT_USER_PROPERTY_NAME_VALUE =
            NbBundle.getMessage(ValueCellEditor.class, "ValueCellEditor.virtualUserProperty.name"); //NOI18N
    private static final String DEFAULT_USER_PROPERTY_TEXT =
            NbBundle.getMessage(ValueCellEditor.class, "ValueCellEditor.virtualUserProperty.text"); //NOI18N
    private static final ResourceBundle BUNDLE = NbBundle.getBundle(ValueCellEditor.class);

    public ValueCellEditor() {
        textEditor = new JTextField();
        booleanEditor = new JCheckBox();
        booleanEditor.setOpaque(false);
        booleanEditor.getModel().addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                stopCellEditing();
            }
        });

        booleanLabel = new JCheckBox();
        booleanLabel.setOpaque(false);
        label = new JLabel();
        label.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
    }

    @SuppressWarnings({"unchecked"})
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String text;
        if (value instanceof JavaVMOption) {
            JavaVMOption<?> option = (JavaVMOption<?>) value;
            if (column == 1 && (option instanceof SwitchNode || option.getValue() instanceof OptionValue.SwitchOnly)) {
                SwitchNode sn = (SwitchNode) option;
                booleanLabel.setSelected(sn.getValue().getValue());
                return booleanLabel;
            }
            text = extractText(option, column);
        } else {
            text = "";      //NOI18N
        }
        label.setText(text);
        return label;
    }

    private String extractText(
            @NonNull final JavaVMOption<?> option,
            final int column) {
        final OptionValue<?> ov = option.getValue();
        if (option instanceof UserPropertyNode) {
            Map.Entry<String, String> entry = (ov != null ? ((OptionValue.StringPair) ov).getValue() : null);
            if (entry == null) {
                return column == 0 ? DEFAULT_USER_PROPERTY_TEXT : EMPTY_TEXT;
            }
            switch (column) {
                case 0: {
                    final String s = entry.getKey();
                    return (s != null ? s : DEFAULT_USER_PROPERTY_TEXT);
                }
                case 1: {
                    final String s = entry.getValue();
                    return s != null ? s : EMPTY_TEXT;
                }
            }
        } else {
            switch (column) {
                case 0: {
                    final String rawName = option.getName();
                    final String key = "ValueCellEditor."+rawName+".text."+option.getClass().getSimpleName(); //NOI18N
                    if (BUNDLE.containsKey(key)) {
                        return BUNDLE.getString(key);
                    }
                    return rawName;
                }
                case 1: {
                    final Object o = ov.getValue();
                    return o != null ? o.toString() : EMPTY_TEXT;
                }
                default:
                    return EMPTY_TEXT;
            }
        }
        return EMPTY_TEXT;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        lastTable = new SoftReference<JTable>(table);
        editedColumn = column;
        editedRow = row;
        JavaVMOption<?> option = (JavaVMOption<?>) value;
        if (column == 0) {
            textEditor.setText(extractText(option, column));
            currentEditor = textEditor;
            return textEditor;
        } else if (option instanceof SwitchNode) {
            SwitchNode sn = (SwitchNode) option;
            booleanEditor.setSelected(sn.getValue().getValue());
            currentEditor = booleanEditor;
            return booleanEditor;
        } else {
            textEditor.setText(extractText(option, column));
            currentEditor = textEditor;     
            return textEditor;
        }
    }

    public Object getCellEditorValue() {
        if (currentEditor == booleanEditor) {
            return new OptionValue.SwitchOnly(booleanEditor.isSelected());
        } else {
            if (lastTable != null) {
                final JTable table = lastTable.get(); //todo this maybe should be avoided by using hard reference.
                if (table == null) throw new IllegalStateException("The reference to table is null"); //NOI18N
                JavaVMOption<?> option = (JavaVMOption<?>) table.getValueAt(editedRow, editedColumn);
                final OptionValue<?> value = option.getValue();
                if (option instanceof UserPropertyNode) {
                    OptionValue.StringPair pair = value != null ? (OptionValue.StringPair) value : new OptionValue.StringPair();
                    final Map.Entry<String, String> sse = pair.getValue();
                    if (editedColumn == 0) {
                        //The D is missing in entries from customizer but is in entries from parser, how it shoud behave?
                        //Probably it's good to have it there to find out there it's a property.
                        String text = textEditor.getText();
                        //if user didn't enter any value or value is empty set it to default
                        if (text == null || text.isEmpty() || DEFAULT_USER_PROPERTY_TEXT.equals(text.trim())) {
                            //Don't generate wrong value!
                            text = UserPropertyNode.NAME + DEFAULT_USER_PROPERTY_NAME_VALUE;
                        } else { //if user enter something check that first letter is D otherwise append D
                            if(!text.startsWith(UserPropertyNode.NAME)) {
                                text = UserPropertyNode.NAME + text;
                        }
                        }
                        Map.Entry<String, String> replacement
                                = new AbstractMap.SimpleEntry<>(
                                        text.trim(), sse.getValue());
                        pair.setValue(replacement);
                        return pair;
                    } else if (editedColumn == 1) {
                        sse.setValue(textEditor.getText());
                        pair.setValue(sse);
                        return pair; 
                    }
                } else if (value instanceof OptionValue.SimpleString) {
                    ((OptionValue.SimpleString) value).setValue(textEditor.getText());
                    return value;
                } 
            }
        }
        throw new IllegalStateException("This option is not supported.");//NOI18N
    }
}
