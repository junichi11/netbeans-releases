/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.hints.spiimpl.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author Jan Becicka
 */
@NbBundle.Messages({
    "CTL_NewConfig=New...",
    "ConfigDefaultName=newConfig",
    "CTL_Rename=Rename...",
    "CTL_Delete=Delete",
    "CTL_Duplicate=Duplicate",
    "MSG_ReallyDeleteConfig=Really want to delete {0}",
    "DeleteConfigTitle=Delete Configuration"
})
public class ConfigurationsComboModel extends AbstractListModel implements ComboBoxModel, ChangeListener {

    private New aNew = new New(Bundle.CTL_NewConfig(), Bundle.ConfigDefaultName());
    private Delete delete = new Delete();
    private New duplicate = new New(Bundle.CTL_Duplicate(), null);
    private Rename rename = new Rename();
    private Object selected;
    private Configuration lastSelected;
    private boolean canModify;

    public ConfigurationsComboModel(boolean canModify) {
        selected = getSize() == 0 ? null :getElementAt(0);
        lastSelected = (Configuration) selected;
        this.canModify = canModify;
        ConfigurationsManager.getDefault().addChangeListener(WeakListeners.change(this, ConfigurationsManager.getDefault()));
    }

    @Override
    public int getSize() {
        return ConfigurationsManager.getDefault().size() + (canModify ? 4 : 0);
    }

    @Override
    public Object getElementAt(int i) {
        if (canModify) {
            if (i == getSize() - 4) {
                return aNew;
            } else if (i == getSize() - 3) {
                return duplicate;
            } else if (i == getSize() - 2) {
                return rename;
            } else if (i == getSize() - 1) {
                return delete;
            }
        }
        return ConfigurationsManager.getDefault().getConfiguration(i);
    }

    public boolean canModify() {
        return canModify;
    }

    @Override
    public void setSelectedItem(Object o) {
        setLastSelected(selected);
        selected = o;
        fireContentsChanged(this, -1, -1);
    }

    private void setLastSelected(Object o) {
        if (o instanceof Configuration) {
            lastSelected = (Configuration) o;
        }
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        fireContentsChanged(this, -1, -1);
    }

    private class New implements ActionListener, FocusListener, KeyListener, PopupMenuListener {

        private final String actionName;
        private final String configName;

        public New(String actionName, String configName) {
            this.actionName = actionName;
            this.configName = configName;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            JComboBox combo = (JComboBox) ae.getSource();
            combo.setEditable(true);
            combo.getEditor().getEditorComponent().addFocusListener(this);
            combo.getEditor().getEditorComponent().addKeyListener(this);
            combo.addPopupMenuListener(this);
            combo.setSelectedItem(configName == null ? lastSelected + "1" : configName);
        }

        @Override
        public String toString() {
            return actionName;
        }

        @Override
        public void focusGained(FocusEvent fe) {
        }

        @Override
        public void focusLost(FocusEvent fe) {
            confirm(fe);
        }

        private void confirm(EventObject fe) {
            JTextField tf = (JTextField) fe.getSource();
            JComboBox combo = (JComboBox) tf.getParent();
            if (combo==null)
                return;
            if (fe instanceof FocusEvent) {
                combo.getEditor().getEditorComponent().removeFocusListener(this);
            } else {
                combo.getEditor().getEditorComponent().removeKeyListener(this);
            }
            Configuration config = configName==null ? 
                    ConfigurationsManager.getDefault().duplicate(lastSelected, tf.getText(), tf.getText()):
                    ConfigurationsManager.getDefault().create(tf.getText(), tf.getText());
            combo.setSelectedItem(config);
            combo.setEditable(false);
        }

        @Override
        public void keyTyped(KeyEvent ke) {
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == ke.VK_ENTER || ke.getKeyCode() == ke.VK_ESCAPE) {
                confirm(ke);
            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            JComboBox combo = (JComboBox) e.getSource();
            confirm(new EventObject(combo.getEditor().getEditorComponent()));
            combo.removePopupMenuListener(this);
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
    }

    private class Rename implements ActionListener, FocusListener, KeyListener, PopupMenuListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JComboBox combo = (JComboBox) ae.getSource();
            combo.setEditable(true);
            JTextField editorComponent = (JTextField) combo.getEditor().getEditorComponent();
            editorComponent.addFocusListener(this);
            editorComponent.addKeyListener(this);
            combo.setSelectedItem(lastSelected);
            combo.addPopupMenuListener(this);
        }

        @Override
        public String toString() {
            return Bundle.CTL_Rename();
        }

        @Override
        public void focusGained(FocusEvent fe) {
        }

        @Override
        public void focusLost(FocusEvent fe) {
            confirm(fe);
        }

        private void confirm(EventObject fe) {
            JTextField tf = (JTextField) fe.getSource();
            JComboBox combo = (JComboBox) tf.getParent();
            if (combo==null)
                return;
            if (fe instanceof FocusEvent) {
                combo.getEditor().getEditorComponent().removeFocusListener(this);
            } else {
                combo.getEditor().getEditorComponent().removeKeyListener(this);
            }
            Configuration config = lastSelected;
            config.setDisplayName(tf.getText());
            combo.setSelectedItem(config);
            combo.setEditable(false);
        }

        @Override
        public void keyTyped(KeyEvent ke) {
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyChar() == KeyEvent.VK_ENTER || ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                confirm(ke);
            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
        }
       
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            JComboBox combo = (JComboBox) e.getSource();
            confirm(new EventObject(combo.getEditor().getEditorComponent()));
            combo.removePopupMenuListener(this);
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
    }

    private class Delete implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ConfigurationsManager.getDefault().size() == 1) {
                setSelectedItem(getElementAt(0));
                return;
            }
            JComboBox combo = (JComboBox) ae.getSource();
            combo.setSelectedItem(lastSelected);
            if (JOptionPane.showConfirmDialog(combo, 
            Bundle.MSG_ReallyDeleteConfig(lastSelected),
            Bundle.DeleteConfigTitle(),
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ConfigurationsManager.getDefault().remove(lastSelected);
                setSelectedItem(getElementAt(0));
            }
        }

        @Override
        public String toString() {
            return Bundle.CTL_Delete();
        }
    }
}
