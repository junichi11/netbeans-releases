/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * SelectedCacheMappingPanel.java
 *
 * Created on January 12, 2004, 12:04 AM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;

import org.netbeans.modules.j2ee.sun.dd.api.VersionNotSupportedException;
import org.netbeans.modules.j2ee.sun.dd.api.web.CacheMapping;
import org.netbeans.modules.j2ee.sun.dd.api.web.CacheHelper;
import org.netbeans.modules.j2ee.sun.dd.api.web.ConstraintField;

import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.ServletRef;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppCache;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot;

/**
 *
 * @author Peter Williams
 */
public class SelectedCacheMappingPanel extends javax.swing.JPanel {
	
	private static final ResourceBundle webappBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.Bundle");	// NOI18N

	private static final ResourceBundle commonBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.Bundle");	// NOI18N
	
	private WebAppRootCustomizer masterPanel;
	private CacheMapping selectedCacheMapping;
	
	private DefaultComboBoxModel servletNamesModel;
	private DefaultComboBoxModel helperRefsModel;
	
	private boolean isServletTarget; 
	private String servletName;
	private String urlPattern;
	
	private boolean isHelperReference;
	private String helperRef;
	private PolicyStorage policy;
	
	/** flag set during initialization to avoid unnecessary dirty flag setting.
	 */
	boolean duringInit;
	
	/** Creates new form SelectedCacheMappingPanel */
	public SelectedCacheMappingPanel(WebAppRootCustomizer src) {
		masterPanel = src;
		
		duringInit = false;
		
		initComponents();
		initUserComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jRBGCacheTarget = new javax.swing.ButtonGroup();
        jRBGCacheReference = new javax.swing.ButtonGroup();
        jLblSelectedMapping = new javax.swing.JLabel();
        jLblTarget = new javax.swing.JLabel();
        jRBnServletName = new javax.swing.JRadioButton();
        jCbxServletName = new javax.swing.JComboBox();
        jRBnURLPattern = new javax.swing.JRadioButton();
        jTxtURLPattern = new javax.swing.JTextField();
        jLblReference = new javax.swing.JLabel();
        jRBnCacheHelper = new javax.swing.JRadioButton();
        jCbxCacheHelper = new javax.swing.JComboBox();
        jRBnCachePolicy = new javax.swing.JRadioButton();
        jBtnEditPolicy = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_SelectedCacheMapping"));
        getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_SelectedCacheMapping"));
        jLblSelectedMapping.setText(webappBundle.getString("LBL_SelectedCacheMapping"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jLblSelectedMapping, gridBagConstraints);

        jLblTarget.setText(webappBundle.getString("LBL_CacheTarget_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jLblTarget, gridBagConstraints);

        jRBGCacheTarget.add(jRBnServletName);
        jRBnServletName.setSelected(true);
        jRBnServletName.setText(webappBundle.getString("LBL_ServletName"));
        jRBnServletName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBnServletNameActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jRBnServletName, gridBagConstraints);
        jRBnServletName.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_ServletName"));
        jRBnServletName.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_ServletName"));

        jCbxServletName.setPrototypeDisplayValue("");
        jCbxServletName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCbxServletNameActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(jCbxServletName, gridBagConstraints);
        jCbxServletName.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_ServletSelector"));
        jCbxServletName.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_ServletSelector"));

        jRBGCacheTarget.add(jRBnURLPattern);
        jRBnURLPattern.setText(webappBundle.getString("LBL_URLPattern"));
        jRBnURLPattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBnURLPatternActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jRBnURLPattern, gridBagConstraints);
        jRBnURLPattern.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_URLPattern"));
        jRBnURLPattern.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_URLPattern"));

        jTxtURLPattern.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtURLPatternKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(jTxtURLPattern, gridBagConstraints);
        jTxtURLPattern.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_URLPatternText"));
        jTxtURLPattern.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_URLPatternText"));

        jLblReference.setText(webappBundle.getString("LBL_CacheReference_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(jLblReference, gridBagConstraints);

        jRBGCacheReference.add(jRBnCacheHelper);
        jRBnCacheHelper.setSelected(true);
        jRBnCacheHelper.setText(webappBundle.getString("LBL_CacheHelper"));
        jRBnCacheHelper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBnCacheHelperActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jRBnCacheHelper, gridBagConstraints);
        jRBnCacheHelper.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_CacheHelper"));
        jRBnCacheHelper.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_CacheHelper"));

        jCbxCacheHelper.setPrototypeDisplayValue("");
        jCbxCacheHelper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCbxCacheHelperActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(jCbxCacheHelper, gridBagConstraints);
        jCbxCacheHelper.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_CacheHelperSelector"));
        jCbxCacheHelper.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_CacheHelperSelector"));

        jRBGCacheReference.add(jRBnCachePolicy);
        jRBnCachePolicy.setText(webappBundle.getString("LBL_Policy"));
        jRBnCachePolicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBnCachePolicyActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        add(jRBnCachePolicy, gridBagConstraints);
        jRBnCachePolicy.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_Policy"));
        jRBnCachePolicy.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_Policy"));

        jBtnEditPolicy.setText(webappBundle.getString("LBL_EditPolicy"));
        jBtnEditPolicy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEditPolicyActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jBtnEditPolicy, gridBagConstraints);
        jBtnEditPolicy.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_EditPolicy"));
        jBtnEditPolicy.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_EditPolicy"));

    }// </editor-fold>//GEN-END:initComponents

	private void jRBnCachePolicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBnCachePolicyActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			isHelperReference = false;
			selectedCacheMapping.setCacheHelperRef(null);
			
			// If there is a saved policy, restore it to the mapping object
			if(policy != null) {
				policy.savePolicyToMapping(selectedCacheMapping);
				policy = null;
			}
			
			enableHelperPolicyFields();

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jRBnCachePolicyActionPerformed

	private void jRBnCacheHelperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBnCacheHelperActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			isHelperReference = true;
			
			// Save the policy
			policy = new PolicyStorage(selectedCacheMapping);
			
			// Switch over to helper ref.
			// !PW note if helperRef is non-null then the remainder of this code
			// that null's out the policy members is redundant with generated
			// code in CacheMapping.  However, helperRef could be null and as
			// far as editing goes, there is nothing wrong with that.
			selectedCacheMapping.setCacheHelperRef(helperRef);
			selectedCacheMapping.setTimeout(null);
			selectedCacheMapping.setRefreshField(false);
			selectedCacheMapping.setHttpMethod(null);
            try {
                selectedCacheMapping.setDispatcher(null);
            } catch(VersionNotSupportedException ex) {
                // suppress and ignore
            }
			selectedCacheMapping.setKeyField(null);
			selectedCacheMapping.setConstraintField(null);
			enableHelperPolicyFields();

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jRBnCacheHelperActionPerformed

	private void jRBnURLPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBnURLPatternActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			isServletTarget = false;
			selectedCacheMapping.setUrlPattern(urlPattern);
			selectedCacheMapping.setServletName(null);
			enableServletURLFields();

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jRBnURLPatternActionPerformed

	private void jRBnServletNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBnServletNameActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			isServletTarget = true;
			selectedCacheMapping.setUrlPattern(null);
			selectedCacheMapping.setServletName(servletName);
			enableServletURLFields();

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jRBnServletNameActionPerformed

	private void jBtnEditPolicyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEditPolicyActionPerformed
		// Add your handling code here:
		if(CachePolicyPanel.invokeAsPopup((JPanel) getParent(), masterPanel.getBean().getAppServerVersion(), selectedCacheMapping)) {
			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jBtnEditPolicyActionPerformed

	private void jCbxCacheHelperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCbxCacheHelperActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			helperRef = (String) helperRefsModel.getSelectedItem();
			if(selectedCacheMapping != null) {
				selectedCacheMapping.setCacheHelperRef(helperRef);
			}

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jCbxCacheHelperActionPerformed

	private void jTxtURLPatternKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtURLPatternKeyReleased
		// Add your handling code here:
		urlPattern = jTxtURLPattern.getText();
//		if(urlPattern == null) {
//			urlPattern = ""; // NOI18N
//		}
		
		if(selectedCacheMapping != null) {
			selectedCacheMapping.setUrlPattern(urlPattern);
		}
		
		masterPanel.getBean().setDirty();	
	}//GEN-LAST:event_jTxtURLPatternKeyReleased

	private void jCbxServletNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCbxServletNameActionPerformed
		// Add your handling code here:
		if(!duringInit) {
			servletName = (String) servletNamesModel.getSelectedItem();
			if(selectedCacheMapping != null) {
				selectedCacheMapping.setServletName(servletName);
			}

			masterPanel.getBean().setDirty();
		}
	}//GEN-LAST:event_jCbxServletNameActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnEditPolicy;
    private javax.swing.JComboBox jCbxCacheHelper;
    private javax.swing.JComboBox jCbxServletName;
    private javax.swing.JLabel jLblReference;
    private javax.swing.JLabel jLblSelectedMapping;
    private javax.swing.JLabel jLblTarget;
    private javax.swing.ButtonGroup jRBGCacheReference;
    private javax.swing.ButtonGroup jRBGCacheTarget;
    private javax.swing.JRadioButton jRBnCacheHelper;
    private javax.swing.JRadioButton jRBnCachePolicy;
    private javax.swing.JRadioButton jRBnServletName;
    private javax.swing.JRadioButton jRBnURLPattern;
    private javax.swing.JTextField jTxtURLPattern;
    // End of variables declaration//GEN-END:variables
	
	private void initUserComponents() {
		masterPanel.addPropertyChangeListener(WebAppRootCustomizer.CACHE_HELPER_LIST_CHANGED,
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pce) {
					cacheHelperListChanged();
				}
			});
		
		masterPanel.addPropertyChangeListener(WebAppRootCustomizer.SERVLET_LIST_CHANGED, 
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pce) {
					servletListChanged();
				}
			});
	}
	
	public void setSelectedCacheMapping(CacheMapping mapping) {
		try {
			duringInit = true;
			selectedCacheMapping = mapping;
			policy = null;

			if(selectedCacheMapping != null) {
				servletNamesModel = getServletComboBoxModel();
				helperRefsModel = getHelperRefComboBoxModel();

				servletName = mapping.getServletName();
				urlPattern = mapping.getUrlPattern();
				isServletTarget = (servletName != null) ? true : false;
				helperRef = mapping.getCacheHelperRef();
				isHelperReference = (helperRef != null) ? true : false;
			} else {
				// clear all the fields
				servletNamesModel = new DefaultComboBoxModel();
				helperRefsModel = new DefaultComboBoxModel();
				servletName = null;
				urlPattern = null;
				isServletTarget = false;
				helperRef = null;
				isHelperReference = false;
			}

			if(isServletTarget) {
				// normalize values
				urlPattern = null;
				jRBnServletName.setSelected(true);
			} else {
				// normalize values
				servletName = null;
				if(urlPattern == null) {
					urlPattern = "";	// NOI18N
				}
				
				jRBnURLPattern.setSelected(true);
			}

			jCbxServletName.setModel(servletNamesModel);
			jCbxServletName.setSelectedItem(servletName);
			jTxtURLPattern.setText(urlPattern);

			if(isHelperReference) {
				jRBnCacheHelper.setSelected(true);
			} else {
				jRBnCachePolicy.setSelected(true);
			}

			jCbxCacheHelper.setModel(helperRefsModel);
			jCbxCacheHelper.setSelectedItem(helperRef);

			if(selectedCacheMapping != null) {
				enableServletURLFields();
				enableHelperPolicyFields();
			}
		} finally {
			duringInit = false;
		}
	}
	
	private DefaultComboBoxModel getServletComboBoxModel() {
		// initialize servlet name combo box with names of all servlets in this app
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		WebAppRoot bean = masterPanel.getBean();
		if(bean != null) {
			List servlets = bean.getServlets();
			for(Iterator iter = servlets.iterator(); iter.hasNext(); ) {
				ServletRef servletRef = (ServletRef) iter.next();
				model.addElement(servletRef.getServletName());
			}
		}
		
		return model;
	}
	
	private DefaultComboBoxModel getHelperRefComboBoxModel() {	
		// initialize helper reference combo box with names of all helpers in this app plus 'default'
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement("default-helper");	// NOI18N
		
		WebAppRoot bean = masterPanel.getBean();
		if(bean != null) {
			WebAppCache cacheBean = bean.getCacheBean();
			List helpers = cacheBean.getCacheHelpers();
			if(helpers != null) {
				for(Iterator iter = helpers.iterator(); iter.hasNext(); ) {
					CacheHelper helperRef = (CacheHelper) iter.next();
					model.addElement(helperRef.getName());
				}
			}
		}
		
		return model;
	}
	
	private void enableServletURLFields() {
		jCbxServletName.setEnabled(isServletTarget);
		jTxtURLPattern.setEnabled(!isServletTarget);
		jTxtURLPattern.setEditable(!isServletTarget);
	}
	
	private void enableHelperPolicyFields() {
		jCbxCacheHelper.setEnabled(isHelperReference);
		jBtnEditPolicy.setEnabled(!isHelperReference);
	}
	
	public void setContainerEnabled(Container container, boolean enabled) {
		Component [] components = container.getComponents();
		for(int i = 0; i < components.length; i++) {
			components[i].setEnabled(enabled);
			if(components[i] instanceof Container) {
				setContainerEnabled((Container) components[i], enabled);
			}
		}
	}
	
	private void cacheHelperListChanged() {
		// !PW FIXME performance optimization: incrementally update list instead
		// of rebuilding it from scratch
		if(selectedCacheMapping != null) {
			helperRefsModel = getHelperRefComboBoxModel();
			jCbxCacheHelper.setModel(helperRefsModel);
			
			if(isHelperReference) {
				int helperIndex = helperRefsModel.getIndexOf(helperRef);
				if(helperIndex >= 0) {
					jCbxCacheHelper.setSelectedIndex(helperIndex);
				} else {
					jCbxCacheHelper.setSelectedItem(null);
				}
			}
		}
	}
	
	private void servletListChanged() {
		// !PW FIXME performance optimization: incrementally update list instead
		// of rebuilding it from scratch
		if(selectedCacheMapping != null) {
			servletNamesModel = getServletComboBoxModel();
			jCbxServletName.setModel(servletNamesModel);
			
			if(isServletTarget) {
				int servletIndex = servletNamesModel.getIndexOf(servletName);
				if(servletIndex >= 0) {
					jCbxServletName.setSelectedIndex(servletIndex);
				} else {
					jCbxServletName.setSelectedItem(null);
				}
			}
		}
	}
	
	/** Private class that encapsulates the cache policy fields so they can be
	 *  saved and restored from and to a CacheMapping object easily.  The intent
	 *  of this class is to provide temporary storage for the policy if the user
	 *  switches the mapping from policy to helper.  Then if the user switches
	 *  back to policy, the former policy can be restored.
	 */
	private static class PolicyStorage {
		private String timeoutName;
		private String timeoutValue;
		private String timeoutScope;
		private String refreshFieldName;
		private String refreshFieldScope;
		private List httpMethods;    // of String
        private List dispatchers;    // of String
		private List keyFieldNames;  // of String
		private List keyFieldScopes; // of String
		private List constraints;    // of ConstraintField
		
		/** Creates a PolicyStorage object populated with the policy parameters
		 *  from the specified CacheMapping.
		 *
		 * @param mapping CacheMapping to retrieve the policy from.
		 */
		public PolicyStorage(CacheMapping mapping) {
			// Timeout
			if(Utils.notEmpty(mapping.getTimeoutName())) {
				timeoutValue = mapping.getTimeout();
				timeoutName = mapping.getTimeoutName();
				timeoutScope = mapping.getTimeoutScope();
			}

			// Refresh Field
			if(Utils.notEmpty(mapping.getRefreshFieldName())) {
				refreshFieldName = mapping.getRefreshFieldName();
				refreshFieldScope = mapping.getRefreshFieldScope();
			}

			// HTTP Methods
			String [] methods = mapping.getHttpMethod();
			if(methods != null) {
				httpMethods = new ArrayList(methods.length);
				for(int i = 0; i < methods.length; i++) {
					httpMethods.add(methods[i]);
				}
			}
            
            // Dispatchers
            try {
                String[] dsps;
                dsps = mapping.getDispatcher();
                if(dsps != null) {
                    dispatchers = new ArrayList(dsps.length);
                    for(int i = 0; i < dsps.length; i++) {
                        dispatchers.add(dsps[i]);
                    }
                }
            } catch(VersionNotSupportedException ex) {
                // suppress and do nothing.
                dispatchers = null;
            }
			
			// Key Fields
			int numFields = mapping.sizeKeyField();
			if(numFields > 0) {
				keyFieldNames = new ArrayList(numFields);
				keyFieldScopes = new ArrayList(numFields);
				
				for(int i = 0; i < numFields; i++) {
					keyFieldNames.add(mapping.getKeyFieldName(i));
					keyFieldScopes.add(mapping.getKeyFieldScope(i));
				}
			}
			
			// Constraint Fields
			// !PW Cannot use Util.arrayToList() for this use case, we need to
			//     clone the elements during copying.  The originals are going
			//     to be destroyed and we'd lose the attributes if we didn't
			//     clone the entire thing first.
			if(mapping.sizeConstraintField() > 0) {
				ConstraintField [] fields = mapping.getConstraintField();
				constraints = new ArrayList(fields.length);
				for(int i = 0; i < fields.length; i++) {
					constraints.add(fields[i].clone());
				}
			}
		}

		/** Restores the saved policy into the mapping passed in.
		 *
		 * @param mapping CacheMapping to restore policy into.
		 */
		public void savePolicyToMapping(CacheMapping mapping) {
			// Timeout
			if(Utils.notEmpty(timeoutName)) {
				mapping.setTimeout(timeoutValue);
				mapping.setTimeoutName(timeoutName);
				mapping.setTimeoutScope(timeoutScope);
			}
			
			// Refresh Field
			if(Utils.notEmpty(refreshFieldName)) {
				mapping.setRefreshField(true);
				mapping.setRefreshFieldName(refreshFieldName);
				mapping.setRefreshFieldScope(refreshFieldScope);
			} else {
				mapping.setRefreshField(false);
			}
			
			// HTTP Methods
			if(httpMethods != null) {
				mapping.setHttpMethod((String []) httpMethods.toArray(new String [0]));
			}
            
            // Dispatcher
            if(dispatchers != null) {
                try {
                    mapping.setDispatcher((String []) dispatchers.toArray(new String [0]));
                } catch (VersionNotSupportedException ex) {
                    // suppress and do nothing.
                }
            }
			
			// Key Fields
			if(keyFieldNames != null) {
				int numFields = keyFieldNames.size();
				mapping.setKeyField(new boolean [numFields]);
				for(int i = 0; i < numFields; i++) {
					mapping.setKeyField(i, true);
					mapping.setKeyFieldName(i, (String) keyFieldNames.get(i));
					mapping.setKeyFieldScope(i, (String) keyFieldScopes.get(i));
				}
			}
					
			// Constraint Fields
			if(constraints != null) {
				int size = constraints.size();
				ConstraintField [] fields = new ConstraintField [size];
				
				for(int i = 0; i < size; i++) {
					fields[i] = (ConstraintField) constraints.get(i);
				}
				
				mapping.setConstraintField(fields);
			}
		}	
	}	
}
