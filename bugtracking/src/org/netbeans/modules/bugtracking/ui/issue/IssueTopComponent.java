/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.ui.issue;

import org.netbeans.modules.bugtracking.team.spi.NBBugzillaUtils;
import org.netbeans.modules.bugtracking.ui.repository.RepositoryComboRenderer;
import org.netbeans.modules.bugtracking.ui.repository.RepositoryComboSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.bugtracking.APIAccessor;
import org.netbeans.modules.bugtracking.BugtrackingManager;
import org.netbeans.modules.bugtracking.RepositoryRegistry;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.IssueImpl;
import org.netbeans.modules.bugtracking.RepositoryImpl;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.commons.HyperlinkSupport;
import org.netbeans.modules.bugtracking.commons.HyperlinkSupport.IssueRefProvider;
import org.netbeans.modules.bugtracking.spi.IssueFinder;
import org.netbeans.modules.team.spi.OwnerInfo;
import org.netbeans.modules.bugtracking.util.*;
import org.netbeans.modules.team.spi.TeamAccessorUtils;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 * Top component that displays information about one issue.
 *
 * @author Jan Stola, Tomas Stupka
 */
public final class IssueTopComponent extends TopComponent implements PropertyChangeListener {
    /** Set of opened {@code IssueTopComponent}s. */
    private static Set<IssueTopComponent> openIssues = new HashSet<IssueTopComponent>();
    /** Issue displayed by this top-component. */
    private IssueImpl issue;
    private RequestProcessor rp = new RequestProcessor("Bugtracking issue", 1, true); // NOI18N
    private Task prepareTask;
    private RepositoryComboSupport rs;
    private File context;
    private DelegatingUndoRedoManager delegatingUndoRedoManager;

    private final InstanceContent instanceContent = new InstanceContent();
    
    /**
     * Creates new {@code IssueTopComponent}.
     */
    public IssueTopComponent() {
        initComponents();
        associateLookup(new AbstractLookup(instanceContent));
        RepositoryRegistry.getInstance().addPropertyChangeListener(this);
        preparingLabel.setVisible(false);
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewClick();
            }
        });
        JComponent findBar = FindSupport.create(this).getFindBar();
        findBar.setVisible(false);
        issuePanel.add(findBar, BorderLayout.PAGE_END);
    }

    @Override
    public UndoRedo getUndoRedo() {
        if(delegatingUndoRedoManager == null) {
            delegatingUndoRedoManager = new DelegatingUndoRedoManager();
        }
        return delegatingUndoRedoManager;
    }
    
    /**
     * Returns issue displayed by this top-component.
     *
     * @return issue displayed by this top-component.
     */
    public IssueImpl getIssue() {
        return issue;
    }

    public void initNewIssue(RepositoryImpl toSelect, File context) {
        initNewIssue(toSelect, false, context);
    }

    public void initNewIssue(RepositoryImpl defaultRepository, boolean suggestedSelectionOnly, File context) {
        this.context = context;

        Font f = new JLabel().getFont();
        int s = f.getSize();
        findIssuesLabel.setFont(repoLabel.getFont().deriveFont(s * 1.7f));

        if ((defaultRepository != null) && !suggestedSelectionOnly) {
            /* fixed selection that cannot be changed by user */
            DefaultComboBoxModel  repoModel = new DefaultComboBoxModel();
            repoModel.addElement(defaultRepository.getRepository());
            repositoryComboBox.setModel(repoModel);
            repositoryComboBox.setRenderer(new RepositoryComboRenderer());
            repositoryComboBox.setSelectedItem(defaultRepository.getRepository());
            repositoryComboBox.setEnabled(false);
            newButton.setEnabled(false);
            onRepoSelected();
        } else {
            if(defaultRepository == null) {
                rs = RepositoryComboSupport.setup(this, repositoryComboBox, false);
            } else {
                rs = RepositoryComboSupport.setup(this, repositoryComboBox, defaultRepository.getRepository());
            }
        }
        repositoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    onRepoSelected();
                }
            }
        });
        setNameAndTooltip();
    }

    public void initNoIssue(final String issueId) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                preparingLabel.setVisible(true);
                repoPanel.setVisible(false);
                if(issueId != null) {
                    String desc = NbBundle.getMessage(IssueTopComponent.class, "LBL_OPENING_ISSUE", new Object[]{issueId});
                    preparingLabel.setText(desc);
                    setName(NbBundle.getMessage(IssueTopComponent.class, "LBL_LOADING_ISSUE", new Object[]{issueId}));
                    setToolTipText(desc);
                } else {
                    setNameAndTooltip();
                }
            }
        });
    }

    /**
     * Sets issue displayed by this top-component.
     *
     * @param issue displayed by this top-component.
     */
    void setIssue(IssueImpl issue) {
        assert (this.issue == null);
        this.issue = issue;
        instanceContent.add(issue.getIssue());
        preparingLabel.setVisible(false);
        issuePanel.add(issue.getController().getComponent(), BorderLayout.CENTER);
        
        if(isOpened()) {
            // #opened() did not fire beacuse of null issue -> fire afterwards
            getController().opened();
        }
        ((DelegatingUndoRedoManager)getUndoRedo()).init();
        
        repoPanel.setVisible(false);
        setNameAndTooltip();
        registerListeners();
        
        if(!issue.isNew()) {
            BugtrackingManager.getInstance().addRecentIssue(issue.getRepositoryImpl(), issue);
        }
        
        registerForIssue();
    }

    private void registerForIssue() {
        undoRedoListener.register(this, true);
        HyperlinkSupport.getInstance().register(this);
        issueLinksListener.register(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        repoPanel = new javax.swing.JPanel();
        repositoryComboBox = new javax.swing.JComboBox();
        findIssuesLabel = new javax.swing.JLabel();
        repoLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        newButton = new org.netbeans.modules.bugtracking.commons.LinkButton();
        jSeparator1 = new javax.swing.JSeparator();
        issuePanel = new javax.swing.JPanel();
        preparingLabel = new javax.swing.JLabel();

        repoPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));

        org.openide.awt.Mnemonics.setLocalizedText(findIssuesLabel, org.openide.util.NbBundle.getMessage(IssueTopComponent.class, "IssueTopComponent.findIssuesLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(repoLabel, org.openide.util.NbBundle.getMessage(IssueTopComponent.class, "IssueTopComponent.repoLabel.text")); // NOI18N
        repoLabel.setFocusCycleRoot(true);

        jPanel1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(newButton, org.openide.util.NbBundle.getMessage(IssueTopComponent.class, "IssueTopComponent.newButton.text")); // NOI18N

        javax.swing.GroupLayout repoPanelLayout = new javax.swing.GroupLayout(repoPanel);
        repoPanel.setLayout(repoPanelLayout);
        repoPanelLayout.setHorizontalGroup(
            repoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(repoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(repoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(repoPanelLayout.createSequentialGroup()
                        .addComponent(repoLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(repositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(findIssuesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
            .addComponent(jSeparator1)
        );
        repoPanelLayout.setVerticalGroup(
            repoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(repoPanelLayout.createSequentialGroup()
                .addGroup(repoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(repoPanelLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(repoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(findIssuesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(repoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(repoLabel)
                            .addComponent(repositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        issuePanel.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.background"));
        issuePanel.setLayout(new java.awt.BorderLayout());

        preparingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(preparingLabel, org.openide.util.NbBundle.getMessage(IssueTopComponent.class, "IssueTopComponent.preparingLabel.text")); // NOI18N
        issuePanel.add(preparingLabel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(repoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(repoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onNewClick() {
        RepositoryImpl repoImpl = BugtrackingUtil.createRepository();
        if(repoImpl != null) {
            Repository repo = repoImpl.getRepository();
            repositoryComboBox.addItem(repo);
            repositoryComboBox.setSelectedItem(repo);
        }
    }

    private IssueController controller;
    private void onRepoSelected() {
        if(prepareTask != null) {
            prepareTask.cancel();
        }
        Cancellable c = new Cancellable() {
            @Override
            public boolean cancel() {
                if(prepareTask != null) {
                    prepareTask.cancel();
                }
                return true;
            }
        };
        final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IssueTopComponent.class, "CTL_PreparingIssue"), c); // NOI18N
        prepareTask = rp.post(new Runnable() {
            @Override
            public void run() {
                try {
                    handle.start();
                    preparingLabel.setVisible(true);
                    RepositoryImpl repo = getRepository();
                    if (repo == null) {
                        return;
                    }
                    if(issue != null) {
                        if(controller != null) {
                            issuePanel.remove(controller.getComponent());
                            controller.closed();
                        }
                        unregisterListeners();
                        instanceContent.remove(issue.getIssue());
                    }
                    issue = repo.createNewIssue();
                    if (issue == null) {
                        return;
                    }
                    instanceContent.add(issue.getIssue());
                    
                    registerForIssue();
                    ((DelegatingUndoRedoManager)getUndoRedo()).init();
                    
                    if(context != null && NBBugzillaUtils.isNbRepository(repo.getUrl())) {
                        OwnerInfo ownerInfo = TeamAccessorUtils.getOwnerInfo(context);
                        if(ownerInfo != null) {
                            issue.setContext(ownerInfo);
                        }
                    }
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            controller = getController();
                            issuePanel.add(controller.getComponent(), BorderLayout.CENTER);
                            controller.opened(); // XXX TC wasn't realy opened
                            registerListeners();
                            revalidate();
                            repaint();

                            focusFirstEnabledComponent();
                        }
                    });
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            preparingLabel.setVisible(false);
                        }
                    });
                    handle.finish();
                    prepareTask = null;
                }
            }
        });
    }

    private void unregisterListeners() {
        issue.removePropertyChangeListener(this);
        getController().removePropertyChangeListener(this);
    }

    private void registerListeners() {
        issue.addPropertyChangeListener(this);
        getController().addPropertyChangeListener(this);
    }

    private RepositoryImpl getRepository() {
        Object item = repositoryComboBox.getSelectedItem();
        if (item == null || !(item instanceof Repository)) {
            return null;
        }
        return APIAccessor.IMPL.getImpl((Repository) item);
    }

    private void focusFirstEnabledComponent() {
        repositoryComboBox.requestFocusInWindow();
        if(!repositoryComboBox.isEnabled()) {
            newButton.requestFocusInWindow();
            if(!newButton.isEnabled()) {
                newButton.transferFocus();
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel findIssuesLabel;
    private javax.swing.JPanel issuePanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private org.netbeans.modules.bugtracking.commons.LinkButton newButton;
    private javax.swing.JLabel preparingLabel;
    private javax.swing.JLabel repoLabel;
    private javax.swing.JPanel repoPanel;
    private javax.swing.JComboBox repositoryComboBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        openIssues.add(this);
        if(issue != null) {
            getController().opened();
            registerForIssue();
        }
        BugtrackingManager.LOG.log(Level.FINE, "IssueTopComponent Opened {0}", (issue != null ? issue.getID() : "null")); // NOI18N
    }

    @Override
    public void componentClosed() {
        openIssues.remove(this);
        if(issue != null) {
            undoRedoListener.register(this, false);
            unregisterListeners();
            getController().closed();
        }
        if(prepareTask != null) {
            prepareTask.cancel();
        }
        BugtrackingManager.LOG.log(Level.FINE, "IssueTopComponent Closed {0}", (issue != null ? issue.getID() : "null")); // NOI18N
    }

    @NbBundle.Messages({
        "CTL_Save=Save",
        "CTL_Discard=Discard",
        "# {0} - stands for a task name", "MSG_HasChanges=Task {0}\nhas changes. Save?",
        "#Question is simply i dialog title", "LBL_Question=Question"
    })
    @Override
    public boolean canClose() {
        if(issue != null) {
            IssueSavable savable = getSavable();
            if(savable != null) {
                JButton save = new JButton(Bundle.CTL_Save());
                JButton discard = new JButton(Bundle.CTL_Discard());
                NotifyDescriptor nd = 
                    new NotifyDescriptor(
                        Bundle.MSG_HasChanges(issue.getShortenedDisplayName()), 
                        Bundle.LBL_Question(), 
                        NotifyDescriptor.YES_NO_CANCEL_OPTION, 
                        NotifyDescriptor.INFORMATION_MESSAGE, 
                        new Object[] {save, discard, NotifyDescriptor.CANCEL_OPTION}, null);
                Object ret = DialogDisplayer.getDefault().notify(nd);
                boolean canClose = false;
                if(ret == save) {
                    canClose = issue.getController().saveChanges();
                } else if(ret == discard) {
                    canClose = issue.getController().discardUnsavedChanges();
                } 
                if(canClose) {
                    savable.destroy();
                }
            }
        }
        return super.canClose(); 
    }
    
    /**
     * Returns top-component that should display the given issue.
     *
     * @param issue issue for which the top-component should be found.
     * @return top-component that should display the given issue.
     */
    public static synchronized IssueTopComponent find(IssueImpl issue) {
        return find(issue, true);
    }

    /**
     * Returns top-component that should display the given issue. 
     *
     * @param issue issue for which the top-component should be found.
     * @param forceCreate determines wheter a TopComponent is created if none available yet
     *
     * @return top-component that should display the given issue.
     */
    public static synchronized IssueTopComponent find(IssueImpl issue, boolean forceCreate) {
        for (IssueTopComponent tc : openIssues) {
            if (issue.equals(tc.getIssue())) {
                return tc;
            }
        }
        if(!forceCreate) {
            return null;
        }
        IssueTopComponent tc = new IssueTopComponent();
        tc.setIssue(issue);
        return tc;
    }

    /**
     * Returns top-component that should display the issue with the given issueId.
     *
     * @param issueId
     * @return
     */
    public static synchronized IssueTopComponent find(String issueId) {
        assert issueId != null;
        for (IssueTopComponent tc : openIssues) {
            IssueImpl i = tc.getIssue();
            if(i == null) continue;
            if (issueId.equals(i.getID())) {
                return tc;
            }
        }
        IssueTopComponent tc = new IssueTopComponent();
        return tc;
    }

    private void setNameAndTooltip() throws MissingResourceException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(issue != null) {
                    String name = issue.getShortenedDisplayName();
                    if(getSavable() != null) {
                        name = "<html><b>" + name + "</b></html>"; // NOI18N
                    }
                    setName(name);
                    setToolTipText(issue.getTooltip());
                } else {
                    setName(NbBundle.getMessage(IssueTopComponent.class, "CTL_IssueTopComponent")); // NOI18N
                    setToolTipText(NbBundle.getMessage(IssueTopComponent.class, "CTL_IssueTopComponent")); // NOI18N
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(IssueImpl.EVENT_ISSUE_DATA_CHANGED)) {
            repoPanel.setVisible(false);
            setNameAndTooltip();
        } else if(evt.getPropertyName().equals(RepositoryRegistry.EVENT_REPOSITORIES_CHANGED)) {
            if(!repositoryComboBox.isEnabled()) {
                // well, looks like there shuold be only one repository available
                return;
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(rs != null) {
                        rs.refreshRepositoryModel();
                    }
                }
            });
        } else if(evt.getPropertyName().equals(IssueController.PROPERTY_ISSUE_CHANGED)) {
            if (getLookup().lookup(IssueSavable.class) == null) {
                instanceContent.add(new IssueSavable(IssueTopComponent.this));
                setNameAndTooltip();
            }
        } else if(evt.getPropertyName().equals(IssueController.PROPERTY_ISSUE_SAVED)) {
            IssueSavable savable = getSavable();
            if(savable != null) {
                savable.destroy();
                setNameAndTooltip();
            }
        }
    }

    private IssueSavable getSavable() {
        return getLookup().lookup(IssueSavable.class);
    }
    
    @Override
    public boolean requestFocusInWindow() {
        if (issue == null) {
            return repositoryComboBox.requestFocusInWindow();
        } else {
            return getController().getComponent().requestFocusInWindow();
        }
    }

    private IssueController getController() {
        return issue.getController();
    }

    private class DelegatingUndoRedoManager implements UndoRedo {
        private UndoRedo delegate;
        final List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();
    
        void init() {
            delegate = UndoRedoSupport.getUndoRedo(issue);
            synchronized(this) {
                for (ChangeListener l : listeners) {
                    delegate.addChangeListener(l);
                }
            }
            for (ChangeListener l : listeners) {
                l.stateChanged(new ChangeEvent(delegate));
            }
        }
        
        @Override
        public boolean canUndo() {
            return delegate != null ? delegate.canUndo() : UndoRedo.NONE.canUndo();
        }

        @Override
        public boolean canRedo() {
            return delegate != null ? delegate.canRedo() : UndoRedo.NONE.canRedo();
        }

        @Override
        public void undo() throws CannotUndoException {
            if(delegate != null) {
                delegate.undo();
            } else {
                UndoRedo.NONE.undo();
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            if(delegate != null) {
                delegate.redo();
            } else {
                UndoRedo.NONE.redo();
            }
        }
        @Override
        public void addChangeListener(ChangeListener l) {
            if(delegate != null) {
                delegate.addChangeListener(l);
            } else {
                synchronized(this) {
                    listeners.add(l); 
                }
            }
        }
        @Override
        public void removeChangeListener(ChangeListener l) {
            if(delegate != null) {
                delegate.removeChangeListener(l);
            } else {
                synchronized(this) {
                    listeners.remove(l); 
                }
            }
        }
        @Override
        public String getUndoPresentationName() {
            return delegate != null ? delegate.getUndoPresentationName() : UndoRedo.NONE.getUndoPresentationName();
        }
        @Override
        public String getRedoPresentationName() {
            return delegate != null ? delegate.getRedoPresentationName() : UndoRedo.NONE.getRedoPresentationName();
        }
    }
    
    private final UndoRedoListener undoRedoListener = new UndoRedoListener();
    private class UndoRedoListener implements ContainerListener {
        
        @Override
        public void componentAdded(ContainerEvent e) {
            register((Container)e.getComponent(), true);
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            register((Container)e.getComponent(), false);
        }
        
        void register(Component c, boolean register) {
            if(c instanceof Container) {
                register((Container)c, register, issue != null ? UndoRedoSupport.getSupport(issue) : null);
            }            
        }
        
        private void register(Component c, boolean register, UndoRedoSupport urs) {
            if(urs != null && c instanceof JTextComponent) {
                JTextComponent tx = (JTextComponent) c;
                if(register) {
                    urs.register(tx);
                } else {
                    urs.unregister(tx);
                }
            }
            if(c instanceof Container) {
                Container container = (Container) c;
                container.removeContainerListener(this);
                if(register) {
                    container.addContainerListener(this);
                }
                Component[] components = container.getComponents();
                for (Component cmp : components) {
                    register(cmp, register, urs);
                }
            }
        }
    }
    
    private IssueLinksListener issueLinksListener = new IssueLinksListener();
    private class IssueLinksListener implements ContainerListener {
        
        @Override
        public void componentAdded(ContainerEvent e) {
            register((Container)e.getComponent());
        }

        @Override
        public void componentRemoved(ContainerEvent e) { }
        
        void register(Component c) {
            if(c instanceof JTextPane) {
                JTextPane tp = (JTextPane) c;
                if(!tp.isEditable()) {
                    HyperlinkSupport.getInstance().registerForIssueLinks(tp, issueLink, issueRefProvider);
                }
            }
            if(c instanceof Container) {
                Container container = (Container) c;
                container.removeContainerListener(this);
                container.addContainerListener(this);
                Component[] components = container.getComponents();
                for (Component cmp : components) {
                    register(cmp);
                }
            }
        }
    }
    
    private static class IssueSavable extends AbstractSavable {
        private final IssueTopComponent tc;
        
        IssueSavable(IssueTopComponent tc) {
            this.tc = tc;
            register();
        }

        @Override
        protected String findDisplayName() {
            if(tc.issue != null) {
                return tc.issue.getDisplayName();
            }
            return tc.getName();
        }

        @Override
        protected void handleSave() throws IOException {
            if(tc.issue != null) {
                tc.issue.getController().saveChanges();
            }
        }

        void destroy() {
            tc.instanceContent.remove(this);
            unregister();
        }
        
        @Override
        public boolean equals(Object obj) {
             if (obj instanceof IssueSavable) {
                return tc == ((IssueSavable)obj).tc;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return tc.hashCode();
        }

    }
    
    private IssueRefProvider issueRefProvider = issueRefProvider = new HyperlinkSupport.IssueRefProvider() {
        @Override
        public int[] getIssueRefSpans(CharSequence text) {
            if(issue == null) {
                return new int[0];
            }
            final RepositoryImpl repo = issue.getRepositoryImpl();
            IssueFinder ifn = repo.getIssueFinder();
            if(ifn == null) {
                return new int[0];
            }
            return ifn.getIssueSpans(text);
        }
    };
    
    HyperlinkSupport.Link issueLink = new HyperlinkSupport.Link() {
            @Override
            public void onClick(String linkText) {
                if(issue == null) {
                    return;
                }
                final RepositoryImpl repo = issue.getRepositoryImpl();
                IssueFinder ifn = repo.getIssueFinder();
                if(ifn == null) {
                    return;
                }
                final String issueKey = ifn.getIssueId(linkText);
                rp.post(new Runnable() {
                    @Override
                    public void run() {
                        Collection<IssueImpl> issues = repo.getIssueImpls(issueKey);
                        if (issues != null && !issues.isEmpty()) {
                            issues.iterator().next().open();
                        }
                    }
                });
            }
        };
}
