/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.visualizers;

import org.netbeans.modules.dlight.spi.SourceSupportProvider;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata;
import org.netbeans.modules.dlight.core.stack.api.FunctionCallWithMetric;
import org.netbeans.modules.dlight.core.stack.api.support.FunctionDatatableDescription;
import org.netbeans.modules.dlight.core.stack.dataprovider.FunctionsListDataProvider;
import org.netbeans.modules.dlight.management.api.DLightSession;
import org.netbeans.modules.dlight.management.api.SessionStateListener;
import org.netbeans.modules.dlight.spi.SourceFileInfoProvider.SourceFileInfo;
import org.netbeans.modules.dlight.util.DLightExecutorService;
import org.netbeans.modules.dlight.util.UIThread;
import org.netbeans.modules.dlight.visualizers.api.FunctionsListViewVisualizerConfiguration;
import org.netbeans.modules.dlight.visualizers.api.impl.FunctionsListViewVisualizerConfigurationAccessor;
import org.openide.awt.MouseUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.netbeans.modules.dlight.api.storage.DataTableMetadata.Column;
import org.netbeans.modules.dlight.core.stack.spi.AnnotatedSourceSupport;
import org.netbeans.modules.dlight.management.api.DLightSession.SessionState;
import org.netbeans.modules.dlight.spi.visualizer.Visualizer;
import org.netbeans.modules.dlight.spi.visualizer.VisualizerContainer;
import org.netbeans.modules.dlight.util.DLightLogger;
import org.netbeans.modules.dlight.visualizers.api.ColumnsUIMapping;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.outline.Outline;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mt154047
 */
public class FunctionsListViewVisualizer extends JPanel implements
        Visualizer<FunctionsListViewVisualizerConfiguration>, OnTimerTask, ComponentListener, ExplorerManager.Provider {

    private final static long MIN_REFRESH_MILLIS = 500;
    private final static Logger log = DLightLogger.getLogger(FunctionsListViewVisualizer.class);
    private Future<Boolean> task;
    private Future<Boolean> detailedTask;
    private final QueryLock queryLock = new QueryLock();
    private final DetailsQueryLock detailsQueryLock = new DetailsQueryLock();
    private final SourcePrefetchExecutorLock sourcePrefetchExecutorLock = new SourcePrefetchExecutorLock();
    private final UILock uiLock = new UILock();
    private JButton refresh;
    private boolean isEmptyContent;
    private boolean isLoadingContent;
    private boolean isShown = true;
    private final OutlineView outlineView;
    private final ExplorerManager explorerManager;
    private final FunctionDatatableDescription functionDatatableDescription;
    private final FunctionsListDataProvider dataProvider;
    private final DataTableMetadata metadata;
    private final ColumnsUIMapping columnsUIMapping;
    private final List<Column> metrics;
    private final FunctionsListViewVisualizerConfiguration configuration;
    private final VisualizersSupport visSupport;
    private FunctionCallChildren currentChildren;
    private ExecutorService sourcePrefetchExecutor;
    private static final boolean isMacLaf = "Aqua".equals(UIManager.getLookAndFeel().getID()); // NOI18N
    private static final Color macBackground = UIManager.getColor("NbExplorerView.background"); // NOI18N
    private static final boolean useHtmlFormat;
    private static final String htmlDisabledColorFormat;
//    private final FocusTraversalPolicy focusPolicy = new FocusTraversalPolicyImpl() ;
    private Map<Integer, Boolean> ascColumnValues = new HashMap<Integer, Boolean>();
    private final SourceSupportProvider sourceSupportProvider;

    static {
        String property = System.getProperty("FunctionsListViewVisualizer.usehtml", "true"); // NOI18N
        useHtmlFormat = "true".equalsIgnoreCase(property); // NOI18N

        Color dlc = UIManager.getDefaults().getColor("Label.disabledForeground"); // NOI18N

        if (dlc == null) {
            dlc = Color.GRAY;
        }

        htmlDisabledColorFormat = String.format("<font color='#%02x%02x%02x'>", dlc.getRed(), dlc.getGreen(), dlc.getBlue()) + "%s</font>"; // NOI18N
    }

    public FunctionsListViewVisualizer(FunctionsListDataProvider dataProvider, FunctionsListViewVisualizerConfiguration configuration) {
        super(new BorderLayout());
        sourceSupportProvider = Lookup.getDefault().lookup(SourceSupportProvider.class);
        visSupport = new VisualizersSupport(new VisualizerImplSessionStateListener());
        explorerManager = new ExplorerManager();
        this.configuration = configuration;
        this.functionDatatableDescription = FunctionsListViewVisualizerConfigurationAccessor.getDefault().getFunctionDatatableDescription(configuration);
        this.metrics = FunctionsListViewVisualizerConfigurationAccessor.getDefault().getMetricsList(configuration);
        columnsUIMapping = FunctionsListViewVisualizerConfigurationAccessor.getDefault().getColumnsUIMapping(configuration);
        this.dataProvider = dataProvider;
        this.metadata = configuration.getMetadata();
        addComponentListener(this);
        String nodeLabel = columnsUIMapping == null ||
                columnsUIMapping.getDisplayedName(functionDatatableDescription.getNameColumn()) == null ? metadata.getColumnByName(functionDatatableDescription.getNameColumn()).getColumnUName() : columnsUIMapping.getDisplayedName(functionDatatableDescription.getNameColumn());
        outlineView = new OutlineView(nodeLabel);
        outlineView.setDragSource(false);
        outlineView.setDropTarget(false);
        outlineView.setAllowedDragActions(DnDConstants.ACTION_NONE);
        outlineView.setAllowedDropActions(DnDConstants.ACTION_NONE);
        final Outline outline = outlineView.getOutline();
        outline.getTableHeader().setReorderingAllowed(false);
        outline.setRootVisible(false);
        outline.setDefaultRenderer(Object.class, new ExtendedTableCellRendererForNode());
        outline.setDefaultRenderer(Node.Property.class, new FunctionsListSheetCell.OutlineSheetCell(outlineView.getOutline(), metrics));
        outline.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && MouseUtils.isDoubleClick(e)) {
                    Node[] nodes = explorerManager.getSelectedNodes();
                    if (nodes != null && nodes.length > 0 && nodes[0] instanceof FunctionCallNode) {
                        FunctionCallNode node = (FunctionCallNode) nodes[0];
                        Action a = node.getGoToSourceAction();
                        if (a != null) {
                            if (a.isEnabled()) {
                                a.actionPerformed(new ActionEvent(node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
                            } else {
                                Logger.getLogger(OutlineView.class.getName()).info("Action " + a + " on node " + node + " is disabled"); // NOI18N
                            }
                            e.consume();
                            return;
                        }
                    }
                }

                super.mouseClicked(e);
            }
        });

        List<Property<?>> result = new ArrayList<Property<?>>();
        for (Column c : metrics) {
            String displayedName = columnsUIMapping == null || columnsUIMapping.getDisplayedName(c.getColumnName()) == null ? c.getColumnUName() : columnsUIMapping.getDisplayedName(c.getColumnName());
            String displayedTooltip = columnsUIMapping == null || columnsUIMapping.getTooltip(c.getColumnName()) == null ? c.getColumnLongUName() : columnsUIMapping.getTooltip(c.getColumnName());
            @SuppressWarnings("unchecked")
            Property<?> property = new PropertySupport(c.getColumnName(), c.getColumnClass(),
                    displayedName, displayedTooltip, true, false) {

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return null;
                }

                @Override
                public void setValue(Object arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                }
            };
            result.add(property);

        }
        //add Alt+Column Number for sorting
        int columnCount = metrics.size() + 1;
        int firstKey = KeyEvent.VK_1;
        for (int i = 1; i <= columnCount; i++) {
            final int columnNumber = i - 1;
            KeyStroke columnKey = KeyStroke.getKeyStroke(firstKey++, KeyEvent.ALT_MASK, true);
            outlineView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(columnKey, "ascSortFor" + i);//NOI18N
            outlineView.getActionMap().put("ascSortFor" + i, new AbstractAction() {// NOI18N

                public void actionPerformed(ActionEvent e) {
                    // ok, do the sorting
                    int column = columnNumber;
                    ETableColumnModel columnModel = null;
                    if (outline.getColumnModel() instanceof ETableColumnModel) {
                        columnModel = (ETableColumnModel) outline.getColumnModel();
                        columnModel.clearSortedColumns();
                    }
                    boolean asc = !ascColumnValues.containsKey(column) ? true : ascColumnValues.get(column);
                    outline.setColumnSorted(column, asc, 1);
                    ascColumnValues.put(column, !asc);
                    outline.getTableHeader().resizeAndRepaint();
                }
            });
//            KeyStroke columnDescKey = KeyStroke.getKeyStroke(firstKey++, KeyEvent.ALT_MASK + KeyEvent.SHIFT_MASK, true);
//            outlineView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(columnDescKey, "descSortFor" + i);//NOI18N
//            outlineView.getActionMap().put("descSortFor" + i, new AbstractAction() {// NOI18N
//                public void actionPerformed(ActionEvent e) {
//                    // ok, do the sorting
//                    int column = columnNumber;
//                    ETableColumnModel columnModel = null;
//                    if (outline.getColumnModel() instanceof ETableColumnModel){
//                        columnModel = (ETableColumnModel)outline.getColumnModel();
//                        columnModel.clearSortedColumns();
//                    }
//                   boolean asc = !ascColumnValues.containsKey(column)  ?  false :  ascColumnValues.get(column);
//                    outline.setColumnSorted(column, !asc  , 1);
//                    ascColumnValues.put(column,! asc);
//                    outline.getTableHeader().resizeAndRepaint();
//                }
//            });
        }
        outline.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outlineView.setProperties(result.toArray(new Property<?>[0]));
        VisualizerTopComponentTopComponent.findInstance().addComponentListener(this);

        KeyStroke returnKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        outlineView.getOutline().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(returnKey, "return"); // NOI18N
        outlineView.getOutline().getActionMap().put("return", new AbstractAction() {// NOI18N

            public void actionPerformed(ActionEvent e) {
                refresh.requestFocus(false);
            }
        });

        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true);
        outlineView.getOutline().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enterKey, "enter"); // NOI18N
        outlineView.getOutline().getActionMap().put("enter", new AbstractAction() { // NOI18N

            public void actionPerformed(ActionEvent e) {
                Node[] nodes = explorerManager.getSelectedNodes();

                if (nodes != null && nodes.length > 0 && nodes[0] instanceof FunctionCallNode) {
                    FunctionCallNode callNode = (FunctionCallNode) nodes[0];
                    Action goToSourceAction = callNode.getGoToSourceAction();
                    if (goToSourceAction != null) {
                        goToSourceAction.actionPerformed(null);
                    }
                }
            }
        });

        ETableColumnModel colModel = (ETableColumnModel) outline.getColumnModel();
        TableColumn firstColumn = colModel.getColumn(0);
        ETableColumn col = (ETableColumn) firstColumn;
        col.setNestedComparator(new Comparator<FunctionCallNode>() {

            public int compare(FunctionCallNode o1, FunctionCallNode o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (refresh != null) {
            refresh.requestFocus();
        }
    }

    public FunctionsListViewVisualizerConfiguration getVisualizerConfiguration() {
        return configuration;
    }

    public JComponent getComponent() {
        return this;
    }

    public VisualizerContainer getDefaultContainer() {
        return VisualizerTopComponentTopComponent.findInstance();
    }

    public void refresh() {
        asyncFillModel(false);
    }

    private void asyncFillModel(boolean cancelIfNotDone) {
        synchronized (queryLock) {
            if (task != null && !task.isDone()) {
                if (cancelIfNotDone) {
                    task.cancel(true);
                } else {
                    return;
                }
            }

            UIThread.invoke(new Runnable() {

                public void run() {
                    setLoadingContent();
                }
            });

            task = DLightExecutorService.submit(new Callable<Boolean>() {

                public Boolean call() {
                    syncFillModel(true);
                    return Boolean.TRUE;
                }
            }, "FunctionsListViewVisualizer Async data load for " + // NOI18N
                    configuration.getID() + " from main table " + // NOI18N
                    configuration.getMetadata().getName());
        }
    }

    private void syncFillModel(boolean wait) {
        long startTime = System.currentTimeMillis();

        List<FunctionCallWithMetric> callsList =
                dataProvider.getFunctionsList(metadata, functionDatatableDescription, metrics);

        List<FunctionCallWithMetric> detailedCallsList =
                dataProvider.getDetailedFunctionsList(metadata, functionDatatableDescription, metrics);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        try {
            if (wait && duration < MIN_REFRESH_MILLIS) {
                // ensure that request does not finish too fast -- IZ #172160
                Thread.sleep(MIN_REFRESH_MILLIS - duration);
            }

            updateList(callsList, detailedCallsList);
        } catch (InterruptedException ex) {
        }
    }

//    private void asyncNotifyAnnotedSourceProviders(boolean cancelIfNotDone) {
//        synchronized (detailsQueryLock) {
//            if (detailedTask != null && !detailedTask.isDone()) {
//                if (cancelIfNotDone) {
//                    detailedTask.cancel(true);
//                } else {
//                    return;
//                }
//            }
//
//            detailedTask = DLightExecutorService.submit(new Callable<Boolean>() {
//
//                public Boolean call() {
//                    List<FunctionCallWithMetric> detailedCallsList =
//                            dataProvider.getDetailedFunctionsList(metadata, functionDatatableDescription, metrics);
//                    List<FunctionCallWithMetric> functionsList = Collections.<FunctionCallWithMetric>emptyList();
//                    if (!dataProvider.hasTheSameDetails(metadata, functionDatatableDescription, metrics)) {
//                        functionsList =
//                                (explorerManager.getRootContext() != null &&
//                                explorerManager.getRootContext().getChildren() != null &&
//                                explorerManager.getRootContext().getChildren() instanceof FunctionCallChildren) ? ((FunctionCallChildren) explorerManager.getRootContext().getChildren()).list : Collections.<FunctionCallWithMetric>emptyList();
//                    }
//
//                    asyncNotifyAnnotedSourceProviders(functionsList, detailedCallsList);
//                    return Boolean.TRUE;
//                }
//            }, "FunctionsListViewVisualizer Async Detailed data load for " + // NOI18N
//                    configuration.getID() + " from main table " + // NOI18N
//                    configuration.getMetadata().getName());
//        }
//    }
    private void asyncNotifyAnnotedSourceProviders(final List<FunctionCallWithMetric> functionsList, final List<FunctionCallWithMetric> list) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        Collection<? extends AnnotatedSourceSupport> supports = Lookup.getDefault().lookupAll(AnnotatedSourceSupport.class);
        for (final AnnotatedSourceSupport sourceSupport : supports) {
            DLightExecutorService.submit(new Runnable() {

                public void run() {
                    sourceSupport.updateSource(dataProvider, metrics, list, functionsList);
                }
            }, "Annoted Source from FunctionsListView Visualizer");//NOI18N
        }
    }

    private void updateList(final List<FunctionCallWithMetric> list, final List<FunctionCallWithMetric> detailedList) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        // Our own executor service prefetching source infos.
        // Don't use DLightExecutorService because:
        // 1) We want to be able to cancel all pending prefetch tasks
        //    when the function list is reloaded
        // 2) We want regular GoToSourceActions
        //    not to be blocked by prefetch tasks
        final boolean isEmptyConent = list == null || list.isEmpty();
        synchronized (sourcePrefetchExecutorLock) {
            if (sourcePrefetchExecutor != null) {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {

                    public Object run() {
                        return sourcePrefetchExecutor.shutdownNow();
                    }
                });
                sourcePrefetchExecutor = null;
            }
            if (!isEmptyContent) {
                sourcePrefetchExecutor = Executors.newFixedThreadPool(2);
            }
        }
        asyncNotifyAnnotedSourceProviders(list, detailedList);
        UIThread.invoke(new Runnable() {

            public void run() {
                synchronized (uiLock) {
                    setContent(isEmptyConent);
                    if (!isEmptyConent) {
                        final FunctionCallChildren children = new FunctionCallChildren(list);
                        currentChildren = children;
                        if (!Children.MUTEX.isReadAccess()) {
                            Children.MUTEX.writeAccess(new Runnable() {

                                public void run() {
                                    explorerManager.setRootContext(new AbstractNode(children));
                                    setNonEmptyContent();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void setEmptyContent() {
        isEmptyContent = true;
        this.removeAll();
        JLabel label = new JLabel(visSupport != null && visSupport.isSessionAnalyzed() ? FunctionsListViewVisualizerConfigurationAccessor.getDefault().getEmptyAnalyzeMessage(configuration) : FunctionsListViewVisualizerConfigurationAccessor.getDefault().getEmptyRunningMessage(configuration), JLabel.CENTER);
        add(label, BorderLayout.CENTER);
        add(createToolbar(), BorderLayout.WEST);
        repaint();
        revalidate();
    }

    private void setLoadingContent() {
        isEmptyContent = false;
        isLoadingContent = true;
        this.removeAll();
        JLabel label = new JLabel(getMessage("Loading"), JLabel.CENTER); // NOI18N
        add(label, BorderLayout.CENTER);
        repaint();
        revalidate();
    }

    private void setContent(boolean isEmpty) {
        if (isLoadingContent && isEmpty) {
            isLoadingContent = false;
            setEmptyContent();
            return;
        }
        if (isLoadingContent && !isEmpty) {
            isLoadingContent = false;
            setNonEmptyContent();
            return;
        }
        if (isEmptyContent && !isEmpty) {
            setNonEmptyContent();
            return;
        }
        if (isEmpty) {
            setEmptyContent();
            return;
        }

    }

    private void setNonEmptyContent() {
        isEmptyContent = false;
        this.removeAll();

        JToolBar toolbar = createToolbar();

        add(toolbar, BorderLayout.WEST);
//        JComponent treeTableView =
//            Models.createView(compoundModel);
//        add(treeTableView, BorderLayout.CENTER);
//        treeModelImpl.fireTreeModelChanged();
//        tableView = new TableView();
        add(outlineView, BorderLayout.CENTER);


        repaint();
        validate();
        //    this.setFocusTraversalPolicyProvider(true);
//        ArrayList<Component> order = new ArrayList<Component>();
//        order.add(outlineView);
//        order.add(refresh);
//        FocusTraversalPolicy newPolicy = new MyOwnFocusTraversalPolicy(this, order);
//        setFocusTraversalPolicy(newPolicy);
        refresh.requestFocus();
    }

    private JToolBar createToolbar() {
        JToolBar buttonsToolbar = new JToolBar();
        if (isMacLaf) {
            buttonsToolbar.setBackground(macBackground);
        }
        buttonsToolbar.setFloatable(false);
        buttonsToolbar.setOrientation(1);
        buttonsToolbar.setRollover(true);

        // Refresh button...
        refresh = new JButton();
        refresh.setIcon(ImageLoader.loadIcon("refresh.png")); // NOI18N
        refresh.setToolTipText(getMessage("Refresh.Tooltip")); // NOI18N
        refresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refresh.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asyncFillModel(false);
            }
        });

        buttonsToolbar.add(refresh);

        return buttonsToolbar;
    }

    public int onTimer() {
//        syncFillModel(false);
        return 0;
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        synchronized (queryLock) {
            if (task != null) {
                task.cancel(false);
                task = null;
            }
        }

        removeComponentListener(this);
        VisualizerTopComponentTopComponent.findInstance().removeComponentListener(this);
    }

    public void timerStopped() {
        if (isEmptyContent) {
            //should set again to chahe Label message
            setEmptyContent();
        }
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
        if (isShown) {
            return;
        }
        isShown = isShowing();
//        if (isShown) {
//            asyncFillModel(true);
//        }
    }

    public void componentHidden(ComponentEvent e) {
        isShown = false;
    }

    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

//    private final FunctionCallNode findNodeByName(String name) {
//        if (currentChildren == null) {
//            return null;
//        }
//        for (Node node : currentChildren.getNodes()) {
//            FunctionCallNode currentNode = (FunctionCallNode) node;
//            String displayName = currentNode.getDisplayName();
//            if (displayName != null && displayName.equals(name)) {
//                return currentNode;
//            }
//        }
//        return null;
//    }
    public void updateVisualizerConfiguration(FunctionsListViewVisualizerConfiguration configuration) {
    }

    public class FunctionCallChildren extends Children.Keys<FunctionCallWithMetric> {

        private final List<FunctionCallWithMetric> list;

        public FunctionCallChildren(List<FunctionCallWithMetric> list) {
            this.list = list;
        }

        protected Node[] createNodes(FunctionCallWithMetric key) {
            return new Node[]{new FunctionCallNode(key)};
        }

        @Override
        protected void addNotify() {
            setKeys(list);
        }
    }

    private class FunctionCallNode extends AbstractNode {

        private final FunctionCallWithMetric functionCall;
        private PropertySet propertySet;
        private final Action[] actions;
        private final GoToSourceAction goToSourceAction;
        private String plainDisplayName = null;
        private String htmlDisplayName = null;
        private String functionName = null;

        FunctionCallNode(FunctionCallWithMetric row) {
            super(Children.LEAF);
            functionCall = row;
            goToSourceAction = new GoToSourceAction(this);
            actions = new Action[]{goToSourceAction};
            propertySet = new PropertySet() {

                @Override
                public Property<?>[] getProperties() {
                    List<Property<?>> result = new ArrayList<Property<?>>();
                    //create for metrics
                    for (final Column metric : metrics) {
                        @SuppressWarnings("unchecked")
                        Property<?> property = new PropertySupport(metric.getColumnName(), metric.getColumnClass(),
                                metric.getColumnUName(), metric.getColumnLongUName(), true, false) {

                            @Override
                            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                                return !functionCall.hasMetric(metric.getColumnName()) ? getMessage("NotDefined") // NOI18N
                                        : functionCall.getMetricValue(metric.getColumnName());
                            }

                            @Override
                            public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                            }
                        };
                        result.add(property);
                    }

                    return result.toArray(new Property[0]);
                }
            };
            updateNames();
        }

        @Override
        public synchronized String getName() {
            return functionName;
        }

        public FunctionCallWithMetric getFunctionCall() {
            return functionCall;
        }

        @Override
        public Image getIcon(int type) {
            return null;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return null;
        }

        @Override
        public Action getPreferredAction() {
            return null;
        }

        GoToSourceAction getGoToSourceAction() {
            return goToSourceAction;
        }

        private void fire() {
            updateNames();
            fireDisplayNameChange(null, getDisplayName());
        }

        @Override
        public Action[] getActions(boolean context) {
            return actions;
            //return super.getActions(context);
        }

        @Override
        public synchronized String getDisplayName() {
            return useHtmlFormat ? htmlDisplayName : plainDisplayName;
        }

        @Override
        public synchronized String getHtmlDisplayName() {
            return htmlDisplayName;
        }

        private synchronized void updateNames() {
            plainDisplayName = functionCall.getDisplayedName();

            String name = functionCall.getFunction().getName();
            String funcName = functionCall.getFunction().getQuilifiedName();
            int idx1 = name.indexOf(funcName);

            int idx2 = funcName.lastIndexOf(':');
            if (idx2 > 0) {
                idx1 += idx2 + 1;
                funcName = funcName.substring(idx2 + 1);
            }

            this.functionName = funcName;

            String prefix = name.substring(0, idx1);
            String suffix = name.substring(idx1 + funcName.length());

            prefix = toHtml(prefix);
            funcName = toHtml(funcName);
            suffix = toHtml(suffix);
            funcName = "<b>" + funcName + "</b>"; // NOI18N

            String dispName = prefix + funcName + suffix + "&nbsp;"; // NOI18N

            final GoToSourceAction action = getGoToSourceAction();
            StringBuilder result = new StringBuilder("<html>"); // NOI18N

            String infoSuffix = null;

            if (action.isEnabled()) {
                result.append(dispName);

                SourceFileInfo sourceInfo = action.getSource();
                if (sourceInfo != null && sourceInfo.isSourceKnown()) {
                    String fname = new File(sourceInfo.getFileName()).getName();
                    int line = sourceInfo.getLine();
                    String infoPrefix = line > 0
                            ? getMessage("FunctionCallNode.prefix.withLine") // NOI18N
                            : getMessage("FunctionCallNode.prefix.withoutLine"); // NOI18N

                    infoSuffix = infoPrefix + "&nbsp;" + fname + (line > 0 ? ":" + line : ""); // NOI18N
                    result.append(String.format(htmlDisabledColorFormat, infoSuffix));
                }
            } else {
                result.append(String.format(htmlDisabledColorFormat, dispName));
            }

            result.append("</html>"); // NOI18N

            htmlDisplayName = result.toString();
        }

        @Override
        public PropertySet[] getPropertySets() {
            return new PropertySet[]{propertySet};
        }

        private String toHtml(String plain) {
            plain = plain.replace("&", "&amp;"); // NOI18N
            plain = plain.replace("<", "&lt;"); // NOI18N
            plain = plain.replace(">", "&gt;"); // NOI18N
            plain = plain.replace(" ", "&nbsp;"); // NOI18N
            return plain;
        }
    }

    private class GoToSourceAction extends AbstractAction {

        private final FunctionCallNode functionCallNode;
        private SourceFileInfo sourceInfo;
        private Future<Boolean> goToSourceTask;

        public GoToSourceAction(FunctionCallNode funcCallNode) {
            super(NbBundle.getMessage(FunctionsListViewVisualizer.class, "GoToSourceActionName"));//NOI18N
            this.functionCallNode = funcCallNode;

            synchronized (sourcePrefetchExecutorLock) {
                if (sourcePrefetchExecutor == null) {
                    sourcePrefetchExecutor = Executors.newFixedThreadPool(2);
                }
            }

            sourcePrefetchExecutor.submit(new Runnable() {

                public void run() {
                    FunctionCallWithMetric functionCall = functionCallNode.getFunctionCall();
                    SourceFileInfo result = dataProvider.getSourceFileInfo(functionCall);

                    if (result != null && result.isSourceKnown()) {
                        setEnabled(true);
                    }

                    synchronized (this) {
                        sourceInfo = result;
                    }

                    functionCallNode.fire();
                }
            });

            setEnabled(false);
        }

        public synchronized void actionPerformed(ActionEvent e) {
            if (goToSourceTask != null && !goToSourceTask.isDone()) {
                // Already in progress...
                return;
            }

            goToSourceTask = DLightExecutorService.submit(new Callable<Boolean>() {

                public Boolean call() {
                    SourceFileInfo source = getSource();

                    if (source == null || !source.isSourceKnown()) {
                        return false;
                    }

                    sourceSupportProvider.showSource(source);
                    return true;
                }
            }, "GoToSource from Functions List View"); // NOI18N
        }

        private synchronized SourceFileInfo getSource() {
            return sourceInfo;
        }
    }

    private class ExtendedTableCellRendererForNode extends DefaultTableCellRenderer {

        private int strLen = 0;

        public ExtendedTableCellRendererForNode() {
            super();
            setVerticalAlignment(javax.swing.SwingConstants.TOP);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Even when this renderer is set as default for any object,
            // we need to call super, as it sets bacgrounds and does some other
            // things... 
            Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (renderer != this) {
                return renderer;
            }

            Node node = currentChildren.getNodeAt(row);

            if (node != null && node instanceof FunctionCallNode) {
                Action goToSourceAction = ((FunctionCallNode) node).getGoToSourceAction();
                String displayText = node.getHtmlDisplayName();
                strLen = displayText.length();
                setEnabled(goToSourceAction != null && goToSourceAction.isEnabled());
                setToolTipText(displayText);
            }

            return this;
        }

        /**
         * see IZ#176678 do not wrap lines in TreeCellRenderer if it's html
         * To make html renderer not to wrap the line - just extend width
         * to be lagre enough to fit all the text...
         *
         */
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, strLen * 10, height);
        }
    }

    private static class VisualizerImplSessionStateListener implements SessionStateListener {

        public void sessionStateChanged(DLightSession session, SessionState oldState, SessionState newState) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static String getMessage(String key) {
        return NbBundle.getMessage(FunctionsListViewVisualizer.class, key);
    }

    private final static class QueryLock {
    }

    private final static class DetailsQueryLock {
    }

    private final static class SourcePrefetchExecutorLock {
    }

    private final static class UILock {
    }
}
