/*
 * OperationConfigurationPanel.java
 *
 * Created on July 3, 2007, 10:33 AM
 */

package org.netbeans.modules.xml.wsdl.ui.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.openide.util.NbBundle;

/**
 *
 * @author  skini
 */
public class OperationConfigurationPanel extends javax.swing.JPanel {

    private Project mProject;

    private Document mCommonOperationTextFieldDocument = new JTextField().getDocument();
    private Map<String, String> namespaceToPrefixMap;
    private WSDLModel mModel;
    private final boolean showPortType;
    public static final String FAULT_PARTS_LISTENER = "FAULT_PARTS_LISTENER";

    private boolean mIsShowMessageComboBoxes = false;

    public OperationConfigurationPanel() {
        showPortType = false;
        initComponents();
    }
    
    public OperationConfigurationPanel(Project project) {
        this(project, false, null, true);
    }
    
    /** Creates new form OperationConfigurationPanel */
    public OperationConfigurationPanel(Project project, boolean isShowMessageComboBoxes, WSDLModel model, boolean showPortType) {
        mProject = project;
        this.mIsShowMessageComboBoxes = isShowMessageComboBoxes;
        if (model != null) {
            mModel = model;
            namespaceToPrefixMap = new HashMap<String, String>();
            Map<String, String> prefixes = ((AbstractDocumentComponent) model.getDefinitions()).getPrefixes();
            if (prefixes != null) {
                for (String prefix : prefixes.keySet()) {
                    namespaceToPrefixMap.put(prefixes.get(prefix), prefix);
                }
            }
        } else {
            namespaceToPrefixMap = new HashMap<String, String>();
            if (!namespaceToPrefixMap.containsKey("xsd")) {
                namespaceToPrefixMap.put("http://www.w3.org/2001/XMLSchema", "xsd");
            }
        }
        this.showPortType = showPortType;
        initComponents();
        initGUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        requestReplyOperationPanel = new org.netbeans.modules.xml.wsdl.ui.view.RequestReplyOperationPanel(this.mProject, this.mCommonOperationTextFieldDocument, namespaceToPrefixMap, mIsShowMessageComboBoxes, mModel, showPortType);
        oneWayOperationPanel = new org.netbeans.modules.xml.wsdl.ui.view.OneWayOperationPanel(this.mProject, this.mCommonOperationTextFieldDocument, namespaceToPrefixMap, mIsShowMessageComboBoxes, mModel, showPortType);

        setName("Form"); // NOI18N
        setLayout(new java.awt.CardLayout());

        requestReplyOperationPanel.setName("requestReplyOperationPanel"); // NOI18N
        add(requestReplyOperationPanel, "OPERATION_REQUEST_REPLY");

        oneWayOperationPanel.setName("oneWayOperationPanel"); // NOI18N
        add(oneWayOperationPanel, "OPERATION_ONE_WAY");
    }// </editor-fold>//GEN-END:initComponents

    private void initGUI() {
        OperationType op1 = new OperationType(OperationType.OPERATION_REQUEST_REPLY,
                NbBundle.getMessage(OperationConfigurationPanel.class, "OperationConfigurationPanel.REQUEST_RESPONSE"));
        OperationType op2 = new OperationType(OperationType.OPERATION_ONE_WAY,
                NbBundle.getMessage(OperationConfigurationPanel.class, "OperationConfigurationPanel.ONE_WAY"));
        
        DefaultComboBoxModel model = new DefaultComboBoxModel(new OperationType[] {op1, op2});
        
        requestReplyOperationPanel.getOperationTypeComboBox().setModel(model);
        oneWayOperationPanel.getOperationTypeComboBox().setModel(model);
        
        ActionListener al = new ActionListener() {
        
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JComboBox) {
                    JComboBox jcb = (JComboBox) e.getSource();
                    OperationType ot = (OperationType) jcb.getSelectedItem();
                    String operationName = selectedOperationConfiguration.getOperationName();
                    String portTypeName = selectedOperationConfiguration.getPortTypeName();
                    boolean autoGenPLT = selectedOperationConfiguration.isAutoGeneratePartnerLinkType();
                    CardLayout operationCardLayout = (CardLayout) getLayout();

                    operationCardLayout.show(OperationConfigurationPanel.this, ot.getOperationType());
                    selectedOperationConfiguration = findVisibleConfiguration();
                    revalidate();
                    selectedOperationConfiguration.setPortTypeName(portTypeName);
                    selectedOperationConfiguration.setOperationName(operationName);
                    selectedOperationConfiguration.setAutoGeneratePartnerLinkType(autoGenPLT);
                    selectedOperationType = ot;
                    selectedOperationConfiguration.getOperationTypeComboBox().requestFocus();
                }
            }
        };
        
        requestReplyOperationPanel.getOperationTypeComboBox().addActionListener(al);
        requestReplyOperationPanel.addPropertyChangeListener(FAULT_PARTS_LISTENER, new PropertyChangeListener() {
        
            public void propertyChange(PropertyChangeEvent evt) {
                PropertyChangeListener[] changeListeners = 
                    getPropertyChangeListeners(FAULT_PARTS_LISTENER);
                for (PropertyChangeListener listener : changeListeners) {
                    listener.propertyChange(evt);
                }
            }
        
        });
        oneWayOperationPanel.getOperationTypeComboBox().addActionListener(al);
        
        this.selectedOperationType = op1;
        selectedOperationConfiguration = requestReplyOperationPanel;
    }
    
    private OperationConfiguration findVisibleConfiguration() {
        OperationConfiguration conf = null;
        
        Component[] comps = getComponents();
        for(int i = 0; i < comps.length; i++) {
            if(comps[i].isVisible()) {
                conf = (OperationConfiguration) comps[i];
                break;
            }
        }
        
        return conf;
    }
   
    public String getPortTypeName() {
        return this.selectedOperationConfiguration.getPortTypeName();
    }
    
    public void setPortTypeName(String portTypeName) {
        this.selectedOperationConfiguration.setPortTypeName(portTypeName);
    }
    
    
    public JTextField getPortTypeNameTextField() {
        return this.selectedOperationConfiguration.getPortTypeNameTextField();
    }

    public void setInputMessages(String[] existingMessages, String newMessageName, javax.swing.event.DocumentListener msgNameDocumentListener) {
        this.requestReplyOperationPanel.setInputMessages(existingMessages, newMessageName, msgNameDocumentListener);
        this.oneWayOperationPanel.setInputMessages(existingMessages, newMessageName, msgNameDocumentListener);
    }
    
    public void setOutputMessages(String[] existingMessages, String newMessageName, javax.swing.event.DocumentListener msgNameDocumentListener) {
        this.requestReplyOperationPanel.setOutputMessages(existingMessages, newMessageName, msgNameDocumentListener);
    }
    
    public void setFaultMessages(String[] existingMessages, String newMessageName, javax.swing.event.DocumentListener msgNameDocumentListener) {
        this.requestReplyOperationPanel.setFaultMessages(existingMessages, newMessageName, msgNameDocumentListener);
    }
    
    public boolean isNewOutputMessage() {
        return selectedOperationConfiguration.isNewOutputMessage();
    }
    
    public boolean isNewInputMessage() {
        return selectedOperationConfiguration.isNewInputMessage();
    }
    
    
    public boolean isNewFaultMessage() {
        return selectedOperationConfiguration.isNewFaultMessage();
    }
    
    public String getOperationName() {
        return selectedOperationConfiguration.getOperationName();
    }
    
    public Map<String, String> getNamespaceToPrefixMap() {
        return namespaceToPrefixMap;
    }
    
    public void setOperationName(String operationName) {
        selectedOperationConfiguration.setOperationName(operationName);
    }
    
    public OperationType getOperationType() {
        return selectedOperationType;
    }
    
    public List<PartAndElementOrTypeTableModel.PartAndElementOrType> getInputMessageParts() {
        return selectedOperationConfiguration.getInputMessageParts();
    }
    
    public List<PartAndElementOrTypeTableModel.PartAndElementOrType> getOutputMessageParts() {
        return selectedOperationConfiguration.getOutputMessageParts();
    }
    
    public List<PartAndElementOrTypeTableModel.PartAndElementOrType> getFaultMessageParts() {
        return selectedOperationConfiguration.getFaultMessageParts();
    }
    
    
    public String getOutputMessageName() {
        return this.selectedOperationConfiguration.getOutputMessageName();
    }
    
    
    public String getInputMessageName() {
        return this.selectedOperationConfiguration.getInputMessageName();
    }
    
    
    public String getFaultMessageName() {
        return this.selectedOperationConfiguration.getFaultMessageName();
    }
    
    public JTextField getOperationNameTextField() {
        return selectedOperationConfiguration.getOperationNameTextField();
    }
    
    public boolean isAutoGeneratePartnerLinkType() {
        return selectedOperationConfiguration.isAutoGeneratePartnerLinkType();
    }

    private OperationConfiguration selectedOperationConfiguration;
    
    private  OperationType selectedOperationType;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.xml.wsdl.ui.view.OneWayOperationPanel oneWayOperationPanel;
    private org.netbeans.modules.xml.wsdl.ui.view.RequestReplyOperationPanel requestReplyOperationPanel;
    // End of variables declaration//GEN-END:variables
}
