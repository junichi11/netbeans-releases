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
package org.netbeans.modules.web.inspect.webkit;

import java.awt.Image;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.inspect.actions.Resource;
import org.netbeans.modules.web.inspect.webkit.actions.GoToNodeSourceAction;
import org.netbeans.modules.web.inspect.webkit.actions.ShowKnockoutContextAction;
import org.netbeans.modules.web.inspect.webkit.knockout.KnockoutTCController;
import org.netbeans.modules.web.webkit.debugging.api.dom.Node;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * DOM node.
 *
 * @author Jan Stola
 */
public class DOMNode extends AbstractNode {
    /** Lookup path with context actions. */
    private static final String ACTIONS_PATH = "Navigation/DOM/Actions"; // NOI18N
    /** Icon base of the node. */
    static final String ICON_BASE = "org/netbeans/modules/web/inspect/resources/domElement.png"; // NOI18N
    /** WebKit node represented by this node. */
    private Node node;
    /** Property sets of the node. */
    private PropertySet[] propertySets;
    /** Determines whether nodeId should be appended to display name. */
    private final boolean nodeIdInDisplayName = Boolean.getBoolean("org.netbeans.modules.web.inspect.nodeIdInDisplayName"); // NOI18N
    /** Page model this node belongs to. */
    private final WebKitPageModel model;

    /**
     * Creates a new {@code DOMNode}.
     * 
     * @param model page model the node belongs to.
     * @param node WebKit node represented by the node.
     */
    public DOMNode(WebKitPageModel model, Node node) {
        super(shouldBeLeaf(node) ? Children.LEAF : new DOMChildren(model), lookupFor(model, node));
        this.node = node;
        this.model = model;
        setIconBaseWithExtension(ICON_BASE);
        setName(node.getNodeName());
        updateDisplayName();
    }

    /**
     * Creates a lookup for the given page model and node.
     * 
     * @param model page model the node belongs to.
     * @param node WebKit node represented by the node.
     * @return lookup for the given page model and node.
     */
    private static Lookup lookupFor(WebKitPageModel model, Node node) {
        List<Object> items = new ArrayList<Object>();

        items.add(node);

        Project project = model.getProject();
        items.add(new DOMSourceElementHandle(node, project));

        String documentURL = node.getDocumentURL();
        if (documentURL != null) {
            items.add(new Resource(project, documentURL));
        }

        if (project != null) {
            items.add(project);
        }

        return Lookups.fixed(items.toArray());
    }

    /**
     * Returns HTML display name for a node with the specified tag name and selector.
     * 
     * @param tagName tag name of the node.
     * @param selector selector of the node.
     * @return HTML display name for the node.
     */
    public static String htmlDisplayName(String tagName, String selector) {
        String pattern = NbBundle.getMessage(DOMNode.class, "DOMNode.elementDisplayName"); //NOI18N
        int maxSelectorLength = 100;
        if (selector.length() > maxSelectorLength) {
            selector = selector.substring(0, maxSelectorLength) + "..."; // NOI18N
        }
        return MessageFormat.format(pattern, tagName.toLowerCase(), selector);
    }

    /**
     * Returns a selector for a node with the specified ID and classes
     * (value of {@code class} attribute).
     * 
     * @param nodeId node ID.
     * @param nodeClasses node classes (value of {@code class} attribute).
     * @return selector for the node.
     */
    public static String selector(String nodeId, String nodeClasses) {
        StringBuilder selector = new StringBuilder();
        if (nodeId != null) {
            selector.append('#').append(nodeId);
        }
        if (nodeClasses != null) {
            StringTokenizer st = new StringTokenizer(nodeClasses);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                selector.append('.').append(token.trim());
            }
        }
        return selector.toString();
    }

    @Override
    public String getHtmlDisplayName() {
        ResourceBundle bundle = NbBundle.getBundle(DOMNode.class);
        String displayName;
        int nodeType = node.getNodeType();
        if (nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
            // Element
            String tagName = node.getNodeName().toLowerCase(Locale.ENGLISH);
            String selector = getSelector();
            displayName = htmlDisplayName(tagName, selector);
        } else if (nodeType == org.w3c.dom.Node.DOCUMENT_NODE) {
            displayName = bundle.getString("DOMNode.documentDisplayName"); //NOI18N
        } else if (nodeType == org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE) {
            displayName = bundle.getString("DOMNode.shadowRootDisplayName"); // NOI18N
        } else {
            // Not used by now
            displayName = node.getNodeType() + " " + node.getNodeName() + " " + node.getNodeValue(); // NOI18N
        }
        if (nodeIdInDisplayName) {
            displayName += " (" + getNode().getNodeId() + ")"; // NOI18N
        }
        return displayName;
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        return DOMNodeAnnotator.getDefault().annotateIcon(node, image);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image image = super.getIcon(type);
        return DOMNodeAnnotator.getDefault().annotateIcon(node, image);
    }

    /**
     * Forces update of the icon.
     */
    void updateIcon() {
        fireIconChange();
        fireOpenedIconChange();
    }

    /**
     * Returns ID and class-based selector that corresponds to this node.
     * 
     * @return ID and class-based selector that corresponds to this node.
     */
    private String getSelector() {
        Node.Attribute idAttr = node.getAttribute("id"); // NOI18N
        String nodeId = (idAttr == null) ? null : idAttr.getValue();
        Node.Attribute classAttr = node.getAttribute("class"); // NOI18
        String nodeClasses = (classAttr == null) ? null : classAttr.getValue();
        return selector(nodeId, nodeClasses);
    }

    @Override
    public synchronized PropertySet[] getPropertySets() {
        if (propertySets == null) {
            propertySets = createPropertySets();
        }
        return propertySets;
    }

    /**
     * Creates property sets for this node.
     * 
     * @return property sets of this node.
     */
    private PropertySet[] createPropertySets() {
        return new PropertySet[] { new AttributesPropertySet(this) };
    }

    /**
     * Forces update of attributes (i.e. attribute property set) from the model.
     */
    synchronized void updateAttributes() {
        if (propertySets != null) {
            for (PropertySet set : propertySets) {
                if (set instanceof AttributesPropertySet) {
                    ((AttributesPropertySet)set).update();
                }
            }
            firePropertySetsChange(null, null);
        }
        updateDisplayName();
    }

    /**
     * Forces update of character data.
     */
    void updateCharacterData() {
        updateDisplayName();
    }

    /**
     * Forces update of the display name.
     */
    private void updateDisplayName() {
        String tagName = node.getNodeName().toLowerCase(Locale.ENGLISH);
        String selector = getSelector();
        setDisplayName(tagName+selector);
    }

    /**
     * Returns the WebKit node that this node represents.
     * 
     * @return WebKit node represented by this node.
     */
    Node getNode() {
        return node;
    }

    /**
     * Forces update of the children/sub-nodes.
     */
    void updateChildren(final Node node) {
        updateChildren(node, null);
    }

    /**
     * Forces update of the children/sub-nodes.
     */
    void updateChildren(Node node, Node childToRefresh) {
        DOMNode.this.node = node;
        boolean shouldBeLeaf = shouldBeLeaf(node);
        if (shouldBeLeaf != isLeaf()) {
            setChildren(shouldBeLeaf ? Children.LEAF : new DOMChildren(model));
        }
        if (!shouldBeLeaf) {
            DOMChildren children = (DOMChildren)getChildren();
            children.updateKeys(node, childToRefresh);
        }
    }

    /**
     * Determines whether {@code DOMNode} that corresponds to the given
     * WebKit node should be a leaf node.
     * 
     * @param node WebKit node to evaluate.
     * @return {@code true} if it should be a leaf node, {@code false} otherwise.
     */
    private static boolean shouldBeLeaf(Node node) {
        if (node.getContentDocument() != null || !node.getShadowRoots().isEmpty()) {
            return false;
        }
        List<Node> subNodes = node.getChildren();
        if (subNodes == null) {
            return false;
        } else {
            for (Node subNode : subNodes) {
                boolean isElement = (subNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE);
                if (isElement && !subNode.isInjectedByNetBeans()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "[nodeId=" + getNode().getNodeId() // NOI18N
                + ", identityHashCode=" + System.identityHashCode(this) + "]"; // NOI18N
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(SystemAction.get(GoToNodeSourceAction.class));
        if (KnockoutTCController.isKnockoutUsed()) {
            actions.add(SystemAction.get(ShowKnockoutContextAction.class));
        }
        if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
            for (Action action : org.openide.util.Utilities.actionsForPath(ACTIONS_PATH)) {
                if (action instanceof ContextAwareAction) {
                    Lookup lookup = new ProxyLookup(Lookups.fixed(this), getLookup());
                    action = ((ContextAwareAction)action).createContextAwareInstance(lookup);
                }
                actions.add(action);
            }
            actions.add(null);
            actions.add(SystemAction.get(PropertiesAction.class));
        }
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(GoToNodeSourceAction.class);
    }

    /**
     * Children for {@code DOMNode}.
     */
    static class DOMChildren extends Children.Keys<Integer> {
        /** Page model this node belongs to. */
        private final WebKitPageModel pageModel;

        /**
         * Creates a new {@code DOMChildren}.
         * 
         * @param pageModel page model the node belongs to.
         */
        DOMChildren(WebKitPageModel pageModel) {
            this.pageModel = pageModel;
        }

        /**
         * Forces update of the keys/sub-nodes from the model.
         * 
         * @param node parent node of this children object.
         * @param childToRefresh child that may need refresh.
         */
        void updateKeys(Node node, Node childToRefresh) {
            List<Integer> keys = new ArrayList<Integer>();
            List<Node> subNodes = node.getChildren();
            if (subNodes != null) {
                for (Node subNode : subNodes) {
                    boolean isElement = (subNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE);
                    if (isElement && !subNode.isInjectedByNetBeans()) {
                        keys.add(subNode.getNodeId());
                    }
                }
            }
            Node contentDocument = node.getContentDocument();
            if (contentDocument != null) {
                keys.add(contentDocument.getNodeId());
            }
            for (Node shadowRoot : node.getShadowRoots()) {
                keys.add(shadowRoot.getNodeId());
            }
            setKeys(keys);
            // Issue 230038: make sure the node for the key is up to date
            if (childToRefresh != null) {
                refreshKey(childToRefresh.getNodeId());
            }
            getNodes(true);
        }

        @Override
        protected org.openide.nodes.Node[] createNodes(Integer nodeId) {
            DOMNode node = pageModel.getNode(nodeId);
            org.openide.nodes.Node[] result;
            if (node == null) {
                result = null;
            } else {
                org.openide.nodes.Node oldParent = node.getParentNode();
                org.openide.nodes.Node newParent = getNode();
                if (oldParent == null || oldParent == newParent) {
                    result = new org.openide.nodes.Node[] { node };
                } else {
                    // Should not happen - a bug in WebKit protocol ?!?
                    Logger.getLogger(DOMChildren.class.getName()).log(Level.INFO,
                            "Node {0} cannot be added to node {1} because it already belongs to {2}!", // NOI18N
                            new Object[]{node, newParent, oldParent});
                    result = null;
                }
            }
            return result;
        }
        
    }

}
