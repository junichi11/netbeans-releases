package org.netbeans.modules.websvc.core.webservices.ui.panels;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import org.openide.util.NbBundle;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

public class EnterWSDLUrlPanel extends JPanel {
    private String defaultWSDLUrl;
    
    public EnterWSDLUrlPanel(String defaultWSDLUrl) {
        this.defaultWSDLUrl = defaultWSDLUrl;
        initComponents();
        populateWSDLUrls();
        
    }
    
    private void populateWSDLUrls() {
        String[] urls = new String[]{defaultWSDLUrl};  //FIX-ME:what else shd we include?
        for(int i = 0; i < urls.length; i++) {
            wsdlURLComboBox.addItem(urls[i]);
        }
    }
    
    public String getSelectedWSDLUrl() {
        return wsdlURLComboBox.getSelectedItem().toString();
    }
    
    private void initComponents() {
        inputLabel = new JLabel(NbBundle.getMessage(EnterWSDLUrlPanel.class, "LBL_Input_WSDL_Url"));
        wsdlURLComboBox = new JComboBox();
        wsdlURLComboBox.setEditable(true);
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(inputLabel, gbc);
        gbc.gridy = 1;
        gbc.weightx = 2.0;
        add(wsdlURLComboBox, gbc);
    }
    
    private JLabel inputLabel;
    private JComboBox wsdlURLComboBox;
    
}
