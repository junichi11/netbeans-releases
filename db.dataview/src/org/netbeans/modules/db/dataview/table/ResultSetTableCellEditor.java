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
 * Portions Copyrighted 2009-2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.dataview.table.celleditor;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.jdesktop.swingx.renderer.JRendererCheckBox;
import org.netbeans.modules.db.dataview.meta.DBColumn;
import org.netbeans.modules.db.dataview.table.ResultSetJXTable;
import org.netbeans.modules.db.dataview.util.DBReadWriteHelper;
import org.netbeans.modules.db.dataview.util.DataViewUtils;
import org.openide.awt.StatusDisplayer;

public class ResultSetTableCellEditor extends DefaultCellEditor {

    protected Object val;
    protected boolean editable = true;
    protected JTable table;
    static final boolean isGtk = "GTK".equals (UIManager.getLookAndFeel ().getID ()); //NOI18N

    public ResultSetTableCellEditor(final JTextField textField) {
        super(textField);
        delegate = new EditorDelegate() {

            @Override
            public void setValue(Object value) {
                val = value;
                textField.setText((value != null) ? value.toString() : "");
            }

            @Override
            public boolean isCellEditable(EventObject evt) {
                if (evt instanceof MouseEvent) {
                    return ((MouseEvent) evt).getClickCount() >= 2;
                }
                return true;
            }

            @Override
            public Object getCellEditorValue() {
                String txtVal = textField.getText();
                if (val == null && txtVal.equals("")) {
                    return null;
                } else {
                    try {
                        assert table != null;
                        int col = table.getEditingColumn();
                        //textField.addKeyListener(new TableKeyListener());
                        return DBReadWriteHelper.validate(txtVal, ((ResultSetJXTable) table).getDBColumn(col));
                    } catch (Exception ex) {
                        StatusDisplayer.getDefault().setStatusText(ex.getMessage());
                        return txtVal;
                    }
                }
            }
        };

        textField.addActionListener(delegate);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (DataViewUtils.isSQLConstantString(value)) {
            value = "";
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public ResultSetTableCellEditor(final JRendererCheckBox checkBox) {
        super(checkBox);
        delegate = new EditorDelegate() {

            @Override
            public void setValue(Object value) {
                val = value;
                checkBox.setSelected((value != null) ? checkBox.isSelected() : false);
            }

            @Override
            public boolean isCellEditable(EventObject evt) {
                if (evt instanceof MouseEvent) {
                    return ((MouseEvent) evt).getClickCount() >= 2;
                }
                return true;
            }

            @Override
            public Object getCellEditorValue() {
                Boolean bolVal = checkBox.isSelected();
                if (val == null && !checkBox.isSelected()) {
                    return null;
                } else {
                    return bolVal;
                }
            }
        };

        checkBox.addActionListener(delegate);
    }

    protected void setEditable(int column, Component c, boolean celleditable) {
        assert table != null;
        DBColumn dbCol = ((ResultSetJXTable) table).getDBColumn(column);
        if (dbCol.isGenerated()) {
            editable = false;
        }
        if (! celleditable) {
            editable = false;
        } else {
            editable = dbCol.isEditable();
        }

        if (c instanceof JTextField) {
            ((JTextField) c).setEditable(editable);
        } else if (c instanceof JComponent) {
            ((JComponent) c).setEnabled(editable);
        }
    }
}