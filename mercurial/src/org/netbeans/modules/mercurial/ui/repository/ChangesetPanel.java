/*
 * ChangesetPanel.java
 *
 * Created on 05 March 2008, 09:53
 */

package org.netbeans.modules.mercurial.ui.repository;

import org.netbeans.modules.mercurial.ui.log.HgLogMessage;

/**
 *
 * @author  jr140578
 */
public class ChangesetPanel extends javax.swing.JPanel {

    private static final String FETCHING_REVISION_DATA = org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "MSG_Fetching_Revisions"); // NOI18N

    /** Creates new form ChangesetPanel */
    public ChangesetPanel() {
        initComponents();
    }

    public void setInfo(HgLogMessage info){
        if(info != null ){
            this.setDescription(info.getMessage());
            this.setAuthor(info.getAuthor());
            this.setDate(info.getDate().toString());
        }
    }
    
    public void clearInfo(){
        this.setDescription(FETCHING_REVISION_DATA);
        this.setAuthor(FETCHING_REVISION_DATA);
        this.setDate(FETCHING_REVISION_DATA);
    }

    public void setDescription(String desc){
        jTextArea1.setText(desc);
    }
    public void setAuthor(String author){
        jTextField2.setText(author);
    }
    public void setDate(String date){
        jTextField3.setText(date);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jPanel1.border.title"))); // NOI18N

        jLabel1.setLabelFor(jTextArea1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jLabel1.text")); // NOI18N

        jLabel2.setLabelFor(jTextField2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jLabel2.text")); // NOI18N

        jLabel3.setLabelFor(jTextField3);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jLabel3.text")); // NOI18N

        jTextField2.setEditable(false);
        jTextField2.setText(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jTextField2.text")); // NOI18N

        jTextField3.setEditable(false);
        jTextField3.setText(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jTextField3.text")); // NOI18N

        jScrollPane1.setBorder(null);

        jTextArea1.setBackground(jTextField2.getBackground());
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setForeground(jTextField2.getForeground());
        jTextArea1.setRows(1);
        jTextArea1.setBorder(jTextField2.getBorder());
        jScrollPane1.setViewportView(jTextArea1);
        jTextArea1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jTextField1.AccessibleContext.accessibleDescription")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTextField2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jTextField2.AccessibleContext.accessibleDescription")); // NOI18N
        jTextField3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChangesetPanel.class, "ChangesetPanel.jTextField3.AccessibleContext.accessibleDescription")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

}
