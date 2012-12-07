/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.remote.sync;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.io.CharConversionException;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.remote.api.ui.ConnectionNotifier;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.xml.XMLUtil;

/**
 *
 * @author vk155633
 */
public class FsSkewNotifier {

    private static final FsSkewNotifier INSTANCE = new FsSkewNotifier();
    
    private FsSkewNotifier() {
    }
    
    private final Set<ExecutionEnvironment> alreadyNotified = new HashSet<ExecutionEnvironment>();
    private final Object lock = new Object();

    public static FsSkewNotifier getInstance() {
        return INSTANCE;
    }
    
    public void notify(final ExecutionEnvironment env) {        
        synchronized (lock) {
            if (alreadyNotified.contains(env)) {
                return;
            }
            alreadyNotified.add(env);
        }
        Notification n;
        Runnable edtRunner = new Runnable() {
            public void run() {
                String envString = env.getDisplayName(); // RemoteUtil.getDisplayName(env);
                String text = null;
                String title = NbBundle.getMessage(FsSkewNotifier.class, "FS_Skew_Title", envString);
                ImageIcon icon = ImageUtilities.loadImageIcon("org/netbeans/modules/cnd/remote/sync/exclamation.gif", false); // NOI18N
                String details = NbBundle.getMessage(FsSkewNotifier.class, "FS_Skew_Details", envString);
                JComponent baloonComponent = createDetails(details);
                JComponent popupComponent = createDetails(details);
                Notification n = NotificationDisplayer.getDefault().notify(
                        title, icon, baloonComponent,  popupComponent, NotificationDisplayer.Priority.HIGH);
            }
        };
        SwingUtilities.invokeLater(edtRunner);        
    }    

    private JComponent createDetails(String explanationText) {
        final JComponent res = new JPanel(new BorderLayout());
        JLabel text = new JLabel(explanationText);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        res.add(text, BorderLayout.CENTER);
        res.setOpaque(false);
        return res;
    }
}
