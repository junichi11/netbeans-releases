/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.multiview.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import org.openide.nodes.Node;
/**
 *
 * @author mkuchtiak
 */
public class SectionView extends PanelView {
    private JPanel scrollPanel, filler;
    private javax.swing.JScrollPane scrollPane;
    private java.util.Hashtable map;
    private int sectionCount=0;
    private NodeSectionPanel activePanel;
    private boolean sectionFocused; 
    
    /** Creates a new instance of SectionView */
    public SectionView() {
        initComponents();
        map = new java.util.Hashtable();
    }
    
    public void initComponents() {
        setLayout(new java.awt.BorderLayout());
        scrollPanel = new JPanel();
        scrollPanel.setLayout(new java.awt.GridBagLayout());
        scrollPane = new javax.swing.JScrollPane();
        scrollPane.setViewportView(scrollPanel);
        filler = new JPanel();
        filler.setBackground(SectionVisualTheme.getDocumentBackgroundColor());
        add (scrollPane, BorderLayout.CENTER);
    }
    
    private void openSection(Node node){
        NodeSectionPanel panel = (NodeSectionPanel) map.get(node);
        if (panel != null) {
            panel.open();
            panel.setActive(true);
            openParents((JPanel)panel);
            panel.scroll(scrollPane);
        }
    }
    
    private void openParents(JPanel panel){
        javax.swing.JScrollPane scrollP = null;
        NodeSectionPanel parentSection=null;
        java.awt.Container ancestor = panel.getParent();
        while (ancestor !=null && scrollP == null){
            if (ancestor instanceof javax.swing.JScrollPane){
                scrollP = (javax.swing.JScrollPane) ancestor;
            }
            if (ancestor instanceof NodeSectionPanel){
                parentSection = (NodeSectionPanel) ancestor;
                parentSection.open();
            }
            ancestor = ancestor.getParent();
        }
    }
    
    public void mapSection(NodeSectionPanel panel){
        mapSection(panel.getNode(),panel);
    }
    
    public void mapSection(Node key, NodeSectionPanel panel){
        map.put(key,panel);
    }
    
    public NodeSectionPanel getSection(Node key){
        return (NodeSectionPanel)map.get(key);
    }    
    
    public void addSection(NodeSectionPanel section){
        scrollPanel.remove(filler);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = sectionCount;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        //gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 6);
        scrollPanel.add((JPanel)section,gridBagConstraints);  
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = sectionCount+1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 2.0;
        scrollPanel.add(filler,gridBagConstraints);  
        
        mapSection(section.getNode(), section);
        sectionCount++;
        System.out.println("addSection, map size = :"+map.size()+":"+sectionCount);
    }
    /*
    public void addSection(Node node, String name, JPanel panel){
        addSection(node, name, panel,true);
    }
    
    public void addSection(Node node, String name,JPanel panel, boolean initialyOpen){
        addSection( new SectionPanel_1(node, name, panel,initialyOpen));
    }
    */
    public void setActivePanel(NodeSectionPanel activePanel) {
        if (this.activePanel!=null && this.activePanel!=activePanel) {
            this.activePanel.setActive(false);
        }
        this.activePanel = activePanel;
        setManagerSelection(new Node[]{activePanel.getNode()});
    }
    
    public void showSelection(org.openide.nodes.Node[] nodes) {
        System.out.println("showSelection "+nodes[0]+":"+sectionFocused);
        if (sectionFocused) {
            sectionFocused=false;
            return;
        }
        if (nodes!=null && nodes.length>0) {
         final Node n = nodes[0];
         javax.swing.SwingUtilities.invokeLater(new Runnable () {
             public void run() {
                 //sectionPanel.openSection(n);
                 //sectionPanel.setActiveNode(n);
                 openSection(n);
             }
         });
        }
    }
    
    public void setSectionFocused(boolean sectionFocused) {
        this.sectionFocused=sectionFocused;
    }
    
}
