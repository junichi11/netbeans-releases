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
/*
 * WebAppRootCustomizer.java
 *
 * Created on September 4, 2003, 5:28 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp;

import java.util.ResourceBundle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;

import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;

import org.netbeans.modules.j2ee.sun.share.configbean.StorageBeanFactory;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.ServletVersion;
import org.netbeans.modules.j2ee.sun.share.configbean.ErrorMessageDB;
import org.netbeans.modules.j2ee.sun.share.configbean.ValidationError;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.BaseCustomizer;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTableModel;

/**
 *
 * @author Peter Williams
 */
public class WebAppRootCustomizer extends BaseCustomizer implements PropertyChangeListener { 

    /** These two properties are defined here because they are specifically for 
     *  this customizer to indicate the specific event only to it's subcomponents,
     *  not to anyone else.
     */
	public static final String CACHE_HELPER_LIST_CHANGED = "CacheHelperListChanged"; //NOI18N
	public static final String SERVLET_LIST_CHANGED = "ServletListChanged"; //NOI18N

	
	private static final ResourceBundle webappBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.Bundle");	// NOI18N
		
	private static final int NUM_SERVLET24_PANELS = 1;
	private static final int NUM_AS81_PANELS = 1;
	private static final int PROPERTIES_TAB_INDEX = 2;
	private static final int MESSAGE_TAB_INDEX = 4;
	
	private WebAppRoot theBean;
	private boolean servlet24FeaturesVisible;
	
    // true if AS 8.1+ fields are visible.
    private boolean as81FeaturesVisible;
    
	/** Creates new form WebAppRootCustomizer */
	public WebAppRootCustomizer() {
		initComponents();
		initUserComponents();
	}
	
	public WebAppRoot getBean() {
		return theBean;
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        webAppTabbedPanel = new javax.swing.JTabbedPane();

        setLayout(new java.awt.GridBagLayout());

        webAppTabbedPanel.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        webAppTabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                webAppTabbedPanelStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(webAppTabbedPanel, gridBagConstraints);

    }//GEN-END:initComponents

	private void webAppTabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_webAppTabbedPanelStateChanged
		showErrors();
	}//GEN-LAST:event_webAppTabbedPanelStateChanged
		
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane webAppTabbedPanel;
    // End of variables declaration//GEN-END:variables

	private WebAppGeneralPanel generalPanel;
	private WebAppClassloaderPanel classloaderPanel;
    private WebAppPropertiesPanel propertiesPanel;
	private WebAppSessionConfigPanel sessionConfigPanel;
	private WebAppMessagesPanel messagesPanel;
	private WebAppLocalePanel localeMappingPanel;
	private WebAppCachePanel cachePanel;
	
	private void initUserComponents() {
		as81FeaturesVisible = true;
        
		// Add title panel
		addTitlePanel(webappBundle.getString("TITLE_SunWebApplication"));	// NOI18N
		getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_SunWebApplication"));	// NOI18N
		getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_SunWebApplication"));	// NOI18N
		
		// Add general panel
		generalPanel = new WebAppGeneralPanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("GENERAL_TAB"), generalPanel);	// NOI18N

		// Add classloader panel
        // TODO - Ideally, this is a tab for AS 8.1, 9.0, but merely a subpanel on general for AS 7.0
		classloaderPanel = new WebAppClassloaderPanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("CLASSLOADER_TAB"), classloaderPanel);	// NOI18N
        
		// Add session configuration panel
		propertiesPanel = new WebAppPropertiesPanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("PROPERTIES_TAB"), propertiesPanel);	// NOI18N

		// Add session configuration panel
		sessionConfigPanel = new WebAppSessionConfigPanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("SESSION_CONFIG_TAB"), sessionConfigPanel);	// NOI18N

		// Add messages panel
		servlet24FeaturesVisible = true;
		messagesPanel = new WebAppMessagesPanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("MESSAGES_TAB"), messagesPanel);	// NOI18N
		
		// Add locale panel
		localeMappingPanel = new WebAppLocalePanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("LOCALE_TAB"), localeMappingPanel);	// NOI18N
		
		// Add cache panel
		cachePanel = new WebAppCachePanel(this);
		webAppTabbedPanel.addTab(webappBundle.getString("CACHE_TAB"), cachePanel);	// NOI18N

		// Add error panel
		addErrorPanel();
	}
	
	protected void initFields() {
		generalPanel.initFields(theBean);
        classloaderPanel.initFields(theBean);
		
        if(ASDDVersion.SUN_APPSERVER_8_1.compareTo(theBean.getAppServerVersion()) <= 0) {
            showAS81Panels();
            propertiesPanel.initFields(theBean);
        } else {
            hideAS81Panels();
        }
        
		sessionConfigPanel.initFields(theBean);
        
		if(theBean.getJ2EEModuleVersion().compareTo(ServletVersion.SERVLET_2_4) >= 0) {
			showServlet24Panels();
			messagesPanel.initFields(theBean);
		} else {
			hideServlet24Panels();
		}
		
		localeMappingPanel.initFields(theBean);
		cachePanel.initFields(theBean);
	}
	
	private void showServlet24Panels() {
		if(!servlet24FeaturesVisible) {
			webAppTabbedPanel.insertTab(webappBundle.getString("MESSAGES_TAB"),	// NOI18N
				null, messagesPanel, null, getMessagesTabIndex());
			servlet24FeaturesVisible = true;
		}
	}
	
	private void hideServlet24Panels() {
		if(servlet24FeaturesVisible) {
			webAppTabbedPanel.removeTabAt(getMessagesTabIndex());	// Remove messages panel
			servlet24FeaturesVisible = false;
		}
	}
    
    private void showAS81Panels() {
        if(!as81FeaturesVisible) {
			webAppTabbedPanel.insertTab(webappBundle.getString("PROPERTIES_TAB"),	// NOI18N
				null, propertiesPanel, null, PROPERTIES_TAB_INDEX);
			as81FeaturesVisible = true;
        }
    }
    
	private void hideAS81Panels() {
        if(as81FeaturesVisible) {
			webAppTabbedPanel.removeTabAt(PROPERTIES_TAB_INDEX); // Remove properties panel
			as81FeaturesVisible = false;
        }
    }
    
	public void addListeners() {
		super.addListeners();
		
		generalPanel.addListeners();
        classloaderPanel.addListeners();
        propertiesPanel.addListeners();
		sessionConfigPanel.addListeners();
		messagesPanel.addListeners();
		localeMappingPanel.addListeners();
		cachePanel.addListeners();
		
		theBean.addPropertyChangeListener(this);
	}
	
	public void removeListeners() {
		super.removeListeners();
		
		cachePanel.removeListeners();
		localeMappingPanel.removeListeners();
		messagesPanel.removeListeners();
		sessionConfigPanel.removeListeners();
        propertiesPanel.removeListeners();
        classloaderPanel.removeListeners();
		generalPanel.removeListeners();
		
		theBean.removePropertyChangeListener(this);
	}
	
	public void partitionStateChanged(ErrorMessageDB.PartitionState oldState, ErrorMessageDB.PartitionState newState) {
		if(newState.getPartition() == getPartition()) {
			showErrors();
		}
		
		if(oldState.hasMessages() != newState.hasMessages()) {
			webAppTabbedPanel.setIconAt(newState.getPartition().getTabIndex(), newState.hasMessages() ? panelErrorIcon : null);
		}
	}
	
	protected boolean setBean(Object bean) {
		boolean result = super.setBean(bean);
		
		if(bean instanceof WebAppRoot) {
			theBean = (WebAppRoot) bean;
			result = true;
		} else {
			// if bean is not a WebAppRoot, then it shouldn't have passed Base either.
			assert (result == false) : 
				"WebAppRootCustomizer was passed wrong bean type in setBean(Object bean)";	// NOI18N
				
			theBean = null;
			result = false;
		}
		
		return result;
	}
    
    private int getMessagesTabIndex() {
        int result = MESSAGE_TAB_INDEX;
        if(!as81FeaturesVisible) {
            result -= 1;
        }
        return result;
    }
	
	private int getAdjustedTabIndex() {
		// Determine which tab has focus.
		int selectedTabIndex = webAppTabbedPanel.getSelectedIndex();
		
		// Adjust tab index to normalize tab indices of tabs that come after
		// the AS 8.1 specific panels, in case they are not showing.
        if(!as81FeaturesVisible && selectedTabIndex >= PROPERTIES_TAB_INDEX) {
            selectedTabIndex += NUM_AS81_PANELS;
        }
        
		// Adjust tab index to normalize tab indices of tabs that come after
		// the Servlet 2.4 specific panels, in case they are not showing.
		if(!servlet24FeaturesVisible && selectedTabIndex >= MESSAGE_TAB_INDEX) {
			selectedTabIndex += NUM_SERVLET24_PANELS;
		}
		
		return selectedTabIndex;
	}
	
	/** Returns the help ID for sun-web-app.  Not only does this customizer have
	 *  several (4-6) tabs, two of those have subtabs.
	 *
	 * @return String representing the current active help ID for this customizer 
	 */
	public String getHelpId() {
		String result = "AS_CFG_WebAppGeneral"; // NOI18N
		
		// Determine which tab has focus and return help context for that tab.
		switch(getAdjustedTabIndex()) {
			case 6:
				result = cachePanel.getHelpId();
				break;
			case 5:
				result = "AS_CFG_WebAppLocale"; // NOI18N
				break;
			case 4:
				result = "AS_CFG_WebAppMessages"; // NOI18N
				break;
			case 3:
				result = sessionConfigPanel.getHelpId();
				break;
			case 2:
				result = "AS_CFG_WebAppProperties"; // NOI18N
				break;
			case 1:
				result = "AS_CFG_WebAppClassloader"; // NOI18N
				break;
		}
		
		return result;
	}
	
	/** Retrieve the partition that should be associated with the current 
	 *  selected tab.
	 *
	 *  @return ValidationError.Partition
	 */
	public ValidationError.Partition getPartition() {
		switch(getAdjustedTabIndex()) {
			case 6:
				return cachePanel.getPartition();
			case 5:
				return ValidationError.PARTITION_WEB_LOCALE;
			case 4:
				return ValidationError.PARTITION_WEB_MESSAGES;
			case 3:
				return sessionConfigPanel.getPartition();
			case 2:
				return ValidationError.PARTITION_WEB_PROPERTIES;
			case 1:
				return ValidationError.PARTITION_WEB_CLASSLOADER;
			default:
				return ValidationError.PARTITION_WEB_GENERAL;
		}
	}
	
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		if(WebAppRoot.SERVLET_LIST_CHANGED.equals(propertyChangeEvent.getPropertyName())) {
            // Make sure we handle this on the swing event thread from here on out.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    firePropertyChange(WebAppRootCustomizer.SERVLET_LIST_CHANGED, false, true);
                }
            });
		}
	}
    
    
    // New for migration to sun DD API model.  Factory instance to pass to generic table model
    // to allow it to create webProperty beans.  Since web property is so common, this factory
    // is package-protected and used by several subpanels as well.
	static GenericTableModel.ParentPropertyFactory webPropertyFactory =
        new GenericTableModel.ParentPropertyFactory() {
            public CommonDDBean newParentProperty(ASDDVersion asVersion) {
                return StorageBeanFactory.getStorageBeanFactory(asVersion).createWebProperty();
            }
        };
}
