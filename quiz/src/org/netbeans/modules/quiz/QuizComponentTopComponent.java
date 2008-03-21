/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.quiz;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.openide.awt.HtmlBrowser;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Jindrich Sedek
 */
final class QuizComponentTopComponent extends TopComponent implements HyperlinkListener, Runnable{

    private static final Logger LOG = Logger.getLogger(QuizAction.class.getName());
    boolean updCentPingResult, contentLoadResult;
    private RequestProcessor rp = new RequestProcessor("Quiz UC check");
    private static QuizComponentTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/quiz/quiz.png";
    
    private static final String PREFERRED_ID = "QuizComponentTopComponent";
    private static final String DEFAULT_URL_STR = NbBundle.getMessage(QuizComponentTopComponent.class, "DefaultPageURL") + findIdentity();

    private QuizComponentTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(QuizComponentTopComponent.class, "CTL_QuizComponentTopComponent"));
        setToolTipText(NbBundle.getMessage(QuizComponentTopComponent.class, "HINT_QuizComponentTopComponent"));
        setIcon(Utilities.loadImage(ICON_PATH, true));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized QuizComponentTopComponent getDefault() {
        if (instance == null) {
            instance = new QuizComponentTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the QuizComponentTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized QuizComponentTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(QuizComponentTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof QuizComponentTopComponent) {
            return (QuizComponentTopComponent) win;
        }
        Logger.getLogger(QuizComponentTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        jScrollPane1.setViewportView(new PleaseWaitPanel());

        final Task ucTask = RequestProcessor.getDefault().post(new Runnable() {

            public void run() {
                pingUC();
            }
        });
        
        final Task contentTask = RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                contentLoadResult = true;
                try {
                    URLConnection urlConnection;
                    urlConnection = getURL().openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(false);
                    InputStream iStream = urlConnection.getInputStream();
                    while (iStream.available() > 0){
                        iStream.read();
                    }
                } catch (IOException ex) {
                    contentLoadResult = false;
                    LOG.log(Level.INFO, "Loading quiz page failed", ex);
                }
            }
        });
        
        TaskListener listener = new TaskListener() {
            public void taskFinished(Task task) {
                if (ucTask.isFinished() && contentTask.isFinished()){
                    EventQueue.invokeLater(instance);
                }
            }
        };
        ucTask.addTaskListener(listener);
        contentTask.addTaskListener(listener);

    }

    private void pingUC() {
        List<UpdateUnitProvider> ucs = UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false);

        updCentPingResult = true;
        for (final UpdateUnitProvider uc : ucs) {
            if (UpdateUnitProvider.CATEGORY.STANDARD.equals(uc.getCategory())) {
                RequestProcessor.Task task = rp.post(new Runnable() {

                    public void run() {
                        try {
                            uc.refresh(null, true);
                        } catch (IOException ex) {
                            updCentPingResult = false;
                            LOG.log(Level.INFO, "Connecting update center failed", ex);
                        }
                    }
                });
                try {
                    task.waitFinished(1000);
                } catch (InterruptedException ex) {
                    LOG.log(Level.WARNING, "Waiting updatecenter verification failed", ex);
                }

            }
        }
    }

    public void run() {
        if (contentLoadResult && updCentPingResult){
            try {
                JEditorPane browser = null;
                browser = new JEditorPane();
                browser.setContentType("text/html"); // NOI18N
                browser.setEditable(false);
                browser.addHyperlinkListener(this);
                browser.setPage(getURL());
                jScrollPane1.setViewportView(browser);
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }else{
            ProxyProblemPanel proxyPanel = new ProxyProblemPanel();
            proxyPanel.setReloadListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    componentOpened();
                }
            });
            jScrollPane1.setViewportView(proxyPanel);
        }
    }
    
    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    protected static String findIdentity() {
        Preferences p = NbPreferences.root().node("org/netbeans/modules/autoupdate"); // NOI18N
        String id = p.get("ideIdentity", null);// NOI18N
        Logger.getLogger(QuizAction.class.getName()).log(Level.INFO, "findIdentity: {0}", id);// NOI18N
        return id;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return QuizComponentTopComponent.getDefault();
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
            HtmlBrowser.URLDisplayer.getDefault().showURL(e.getURL());
        }
    }
    
    private URL getURL() throws MalformedURLException{
        String replace = System.getProperty("org.netbeans.modules.quiz.LoadURI");
        if (replace != null){
            return new URL(replace);
        }
        return new URL(DEFAULT_URL_STR);
    }
}
