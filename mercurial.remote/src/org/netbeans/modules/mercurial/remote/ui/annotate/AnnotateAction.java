/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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
package org.netbeans.modules.mercurial.remote.ui.annotate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.mercurial.remote.FileInformation;
import org.netbeans.modules.mercurial.remote.FileStatus;
import org.netbeans.modules.mercurial.remote.FileStatusCache;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.HgProgressSupport;
import org.netbeans.modules.mercurial.remote.Mercurial;
import org.netbeans.modules.mercurial.remote.OutputLogger;
import org.netbeans.modules.mercurial.remote.ui.actions.ContextAction;
import org.netbeans.modules.mercurial.remote.ui.log.HgLogMessage;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.modules.versioning.core.spi.VCSContext;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Annotate action for mercurial: 
 * hg annotate - show changeset information per file line 
 * 
 * @author John Rice
 */
@Messages({
    "CTL_MenuItem_ShowAnnotations=Show A&nnotations",
    "CTL_MenuItem_HideAnnotations=Hide A&nnotations"
})
public class AnnotateAction extends ContextAction {
    public static final String ICON_RESOURCE = "org/netbeans/modules/mercurial/remote/resources/icons/annotate.png"; //NOI18N
    
    public AnnotateAction () {
        super(ICON_RESOURCE);
    }
    
    @Override
    protected boolean enable(Node[] nodes) {
        VCSContext context = HgUtils.getCurrentContext(nodes);
        if(!HgUtils.isFromHgRepository(context)) {
            return false;
        }

        if (context.getRootFiles().size() > 0 && activatedEditorCookie(nodes) != null) {
            FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
            VCSFileProxy file = activatedFile(nodes);
            if (file == null) {
                return false;
            }
            FileInformation info  = cache.getCachedStatus(file);
            if(info != null) {
                int status = info.getStatus();
                if (status == FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY ||
                    status == FileInformation.STATUS_NOTVERSIONED_EXCLUDED) {
                    return false;
                } else {
                    return true;
                }
            } else {
                // XXX won't work properly when staus not chached yet. we should at least force a cahce.refresh
                // at this point
                return true;
            }
        } else {
            return false;
        } 
    } 

    @Override
    protected String getBaseName(Node[] nodes) {
        return visible(nodes) ? "CTL_MenuItem_HideAnnotations" : "CTL_MenuItem_ShowAnnotations"; //NOI18N
    }

    @Override
    protected String iconResource () {
        return ICON_RESOURCE;
    }

    @Override
    protected void performContextAction(Node[] nodes) {
        if (visible(nodes)) {
            JEditorPane pane = activatedEditorPane(nodes);
            AnnotationBarManager.hideAnnotationBar(pane);
        } else {
            EditorCookie ec = activatedEditorCookie(nodes);
            if (ec == null) {
                return;
            }

            final VCSFileProxy file = activatedFile(nodes);

            JEditorPane[] panes = ec.getOpenedPanes();
            if (panes == null) {
                ec.open();
                panes = ec.getOpenedPanes();
            }

            if (panes == null) {
                return;
            }
            final JEditorPane currentPane = panes[0];
            showAnnotations(currentPane, file, null, true);
        }
    }

    public static void showAnnotations(JEditorPane currentPane, final VCSFileProxy file, final String revision) {
        showAnnotations(currentPane, file, revision, true);
    }
    
    static void showAnnotations(JEditorPane currentPane, final VCSFileProxy file, final String revision, boolean requestActive) {
        if (currentPane == null || file == null) {
            return;
        }
        if (requestActive) {
            TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, currentPane);
            tc.requestActive();
        }

        final AnnotationBar ab = AnnotationBarManager.showAnnotationBar(currentPane);
        ab.setAnnotationMessage(NbBundle.getMessage(AnnotateAction.class, "CTL_AnnotationSubstitute")); // NOI18N;

        final VCSFileProxy repository = Mercurial.getInstance().getRepositoryRoot(file);
        if (repository == null) {
            return;
        }

        RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(repository);
        HgProgressSupport support = new HgProgressSupport() {
            @Override
            public void perform() {
                VCSFileProxy annotatedFile = file;
                FileStatus st = Mercurial.getInstance().getFileStatusCache().getStatus(file).getStatus(null);
                if (st != null && st.isCopied() && st.getOriginalFile() != null) {
                    annotatedFile = st.getOriginalFile();
                    ab.setReferencedFile(annotatedFile);
                }
                if (revision != null) {
                    // showing annotations from past, the referenced file differs from the one being displayed
                    ab.setReferencedFile(annotatedFile);
                }
                OutputLogger logger = getLogger();
                logger.outputInRed(
                        NbBundle.getMessage(AnnotateAction.class,
                        "MSG_ANNOTATE_TITLE")); // NOI18N
                logger.outputInRed(
                        NbBundle.getMessage(AnnotateAction.class,
                        "MSG_ANNOTATE_TITLE_SEP")); // NOI18N
                computeAnnotations(repository, annotatedFile, this, ab, revision);
                logger.output("\t" + file.getPath()); // NOI18N
                logger.outputInRed(
                        NbBundle.getMessage(AnnotateAction.class,
                        "MSG_ANNOTATE_DONE")); // NOI18N
            }
        };
        support.start(rp, repository, NbBundle.getMessage(AnnotateAction.class, "MSG_Annotation_Progress")); // NOI18N
    }

    private static void computeAnnotations(VCSFileProxy repository, VCSFileProxy file, HgProgressSupport progress, AnnotationBar ab, String revision) {
        List<String> list = null;
        try {
             list = HgCommand.doAnnotate(repository, file, revision, progress.getLogger());
        } catch (HgException.HgCommandCanceledException ex) {
            // canceled by user, do nothing
        } catch (HgException ex) {
            HgUtils.notifyException(ex);
        }
        if (progress.isCanceled()) {
            ab.setAnnotationMessage(NbBundle.getMessage(AnnotateAction.class, "CTL_AnnotationFailed")); // NOI18N;
            return;
        }
        if (list == null) {
            ab.setAnnotationMessage(NbBundle.getMessage(AnnotateAction.class, "CTL_AnnotationFailed")); // NOI18N;
            return;
        }
        AnnotateLine [] lines = toAnnotateLines(list);
        List<String> revisions = getRevisionNumbers(lines);
        HgLogMessage initialRevision = null;
        HgLogMessage [] logs = new HgLogMessage[0];
        if (!revisions.isEmpty()) {
            logs = HgCommand.getLogMessages(repository, Collections.singleton(file), 
                    revisions.get(0), "0", false, false, false, 1, Collections.<String>emptyList(), OutputLogger.getLogger(null), true); //NOI18N
            if (logs.length == 1) {
                initialRevision = logs[0];
            }
            logs = HgCommand.getRevisionInfo(repository, revisions, progress.getLogger());
        }
        if (progress.isCanceled()) {
            return;
        }
        if (logs == null) {
            return;
        }
        fillCommitMessages(lines, logs, initialRevision);
        ab.setAnnotatedRevision(revision);
        ab.annotationLines(file, Arrays.asList(lines));
    }

    private static List<String> getRevisionNumbers (AnnotateLine[] lines) {
        Set<String> revisions = new HashSet<>(lines.length);
        for (AnnotateLine line : lines) {
            if (!(line instanceof FakeAnnotationLine)) {
                String revision = line.getRevision();
                try {
                    Long.parseLong(revision);
                    revisions.add(revision);
                } catch (NumberFormatException ex) {
                    // probably a fake item or a non-existent revision
                }
            }
        }
        List<String> retval = new ArrayList<>(revisions);
        Collections.sort(retval);
        return retval;
    }

    private static void fillCommitMessages(AnnotateLine [] annotations, HgLogMessage [] logs, HgLogMessage initialRevision) {
        for (int i = 0; i < annotations.length; i++) {
            AnnotateLine annotation = annotations[i];
            if (annotation == null) {
                Mercurial.LOG.log(Level.WARNING, "AnnotateAction: annotation {0} of {1} is null", new Object[]{i, annotations.length}); //NOI18N
                continue;
            }
            for (int j = 0; j < logs.length; j++) {
                HgLogMessage log = logs[j];
                if (log == null) {
                    Mercurial.LOG.log(Level.WARNING, "AnnotateAction: log {0} of {1} is null", new Object[]{j, logs.length}); //NOI18N
                    continue;
                }
                if (annotation.getRevision().equals(log.getRevisionNumber())) {
                    annotation.setDate(log.getDate());
                    annotation.setId(log.getCSetShortID());
                    annotation.setCommitMessage(log.getMessage());
                }
            }
        }
        String lowestRev = initialRevision == null ? "-1" : initialRevision.getRevisionNumber(); //NOI18N
        for (int i = 0; i < annotations.length; i++) {
            AnnotateLine annotation = annotations[i];
            if (annotation == null) {
                Mercurial.LOG.log(Level.WARNING, "AnnotateAction: annotation {0} of {1} is null", new Object[]{i, annotations.length}); //NOI18N
            }else{
                annotation.setCanBeRolledBack(!annotation.getRevision().equals(lowestRev));
            }
        }
    }

    private static AnnotateLine [] toAnnotateLines(List<String> annotations)
    {
        final int GROUP_AUTHOR = 1;
        final int GROUP_REVISION = 2;
        final int GROUP_FILENAME = 3;
        final int GROUP_LINE_NUMBER = 4;
        final int GROUP_CONTENT = 5;
        
        List<AnnotateLine> lines = new ArrayList<>();
        int i = 0;
        Pattern p = Pattern.compile("^\\s*(\\S+\\b)\\s+(\\d+)\\s+(\\b\\S*):\\s*(\\d+):\\s(.*)$"); //NOI18N
        for (String line : annotations) {
            i++;
            Matcher m = p.matcher(line);
            AnnotateLine anLine;
            if (!m.matches()){
                Mercurial.LOG.log(Level.WARNING, "AnnotateAction: toAnnotateLines(): Failed when matching: {0}", new Object[] {line}); //NOI18N
                anLine = new FakeAnnotationLine();
            } else {
                anLine = new AnnotateLine();
                anLine.setAuthor(m.group(GROUP_AUTHOR));
                anLine.setRevision(m.group(GROUP_REVISION));
                anLine.setFileName(m.group(GROUP_FILENAME));
                try {
                    anLine.setPrevLineNum(Integer.parseInt(m.group(GROUP_LINE_NUMBER)));
                } catch (NumberFormatException ex) {
                    anLine.setPrevLineNum(-1);
                }
                anLine.setContent(m.group(GROUP_CONTENT));
            }
            anLine.setLineNum(i);
            
            lines.add(anLine);
        }
        return lines.toArray(new AnnotateLine[lines.size()]);
    }

    /**
     * @param nodes or null (then taken from windowsystem, it may be wrong on editor tabs #66700).
     */
    public boolean visible(Node[] nodes) {
        JEditorPane currentPane = activatedEditorPane(nodes);
        return AnnotationBarManager.annotationBarVisible(currentPane);
    }


    /**
     * @return active editor pane or null if selected node
     * does not have any or more nodes selected.
     */
    private JEditorPane activatedEditorPane(Node[] nodes) {
        final EditorCookie ec = activatedEditorCookie(nodes);
        if (ec != null) {
            return Mutex.EVENT.readAccess(new Mutex.Action<JEditorPane>() {
                @Override
                public JEditorPane run () {
                    return NbDocument.findRecentEditorPane(ec);
                }
            });
        }
        return null;
    }

    private EditorCookie activatedEditorCookie(Node[] nodes) {
        if (nodes == null) {
            nodes = WindowManager.getDefault().getRegistry().getActivatedNodes();
        }
        if (nodes.length == 1) {
            Node node = nodes[0];
            return node.getLookup().lookup(EditorCookie.class);
        }
        return null;
    }

    private VCSFileProxy activatedFile(Node[] nodes) {
        if (nodes.length == 1) {
            Node node = nodes[0];
            DataObject dobj = node.getLookup().lookup(DataObject.class);
            if (dobj != null) {
                FileObject fo = dobj.getPrimaryFile();
                return VCSFileProxy.createFileProxy(fo);
            }
        }
        return null;
    }

    private static class FakeAnnotationLine extends AnnotateLine {
        public FakeAnnotationLine() {
            String fakeItem = NbBundle.getMessage(AnnotateAction.class, "MSG_AnnotateAction.lineDetail.unknown");
            setAuthor(fakeItem);
            setContent(fakeItem);
            setRevision(fakeItem);
            setFileName(fakeItem);
        }
    }
}
