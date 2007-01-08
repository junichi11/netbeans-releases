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

package org.netbeans.modules.loadgenerator.spi;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/**
 *
 * @author Jaroslav Bachorik
 */
public abstract class Engine {
  public static final String STATE = Engine.class.getName() + "#STATE"; // STATE property
  public static final String INSTANCE = Engine.class.getName() + "#INSTANCE"; // INSTANCE property
  
  private boolean lastReadyState = true;
  
  final private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
  final private ProcessInstanceListener pil = new ProcessInstanceListener() {
    public void generatorStarted(ProcessInstance provider) {
      setLastReadyState(false);
    }
    
    public void generatorStarted(ProcessInstance provider, String logPath) {
      setLastReadyState(false);
    }
    
    public void generatorStopped(ProcessInstance provider) {
      setLastReadyState(true);
    }
    
    public void instanceInvalidated(ProcessInstance instance) {
      processes.remove(instance);
    }
  };
  
  final private List<ProcessInstance> processes;
  
  public Engine() {
    processes = new ArrayList<ProcessInstance>();
  }
  
  /**
   * Must return a valid process (@see org.netbeans.modules.loadgenerator.spi.ProcessInstance)
   * The implementation may use the script name to return a previously created and cached process
   *
   *
   * @param scriptName A textual identification of the process to be returned (e.g. a path to the underlying script)
   * @return Returns a valid process
   */
  public ProcessInstance createProcess(final String scriptName) {
    // check for an already created process
    ProcessInstance process = getProcessByName(scriptName);
    if (process == null) {
      process = prepareInstance(scriptName);
      process.setCurrentScript(scriptName);
      process.addListener(pil);
      if (process.isNew()) {
        registerProcess(process);
      }
      process.touch();
    }
    
    return process;
  }
  
  /**
   * Returns a list of all processes attached to the particular load generator engine
   */
  public List<? extends ProcessInstance> getProcesses() {
    return processes;
  }
  
  /**
   * Finds and returns an instance created for the given script
   * May return NULL if such an instance doesn't exist
   */
  public ProcessInstance getProcessByName(final String scriptName) {
    for(ProcessInstance instance : processes) {
      if (instance.getCurrentScript() != null && instance.getCurrentScript().equals(scriptName)) {
        return instance;
      }
    }
    return null;
  }
  
  /**
   * Removes all non-running processes
   */
  public void cleanup() {
    if (!isReady())
      return;
    
    for(Iterator<ProcessInstance> iter = processes.iterator();iter.hasNext();) {
      ProcessInstance process = iter.next();
      if (!process.isRunning()) {
        process.detachFactory();
        iter.remove();
      }
    }
    
    pcs.firePropertyChange(INSTANCE, true, false);
  }
  
  public boolean canCleanup() {
    if (!isReady())
      return false;
    
    for(ProcessInstance process : processes) {
      if (!process.isRunning()) {
        return true;
      }
    }
    
    return false;
  }
  
  // <editor-fold defaultstate="collapsed" desc="Property Change Support">
  public void addPropertyChangeListener(final PropertyChangeListener pcl) {
    pcs.addPropertyChangeListener(pcl);
  }
  
  public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener pcl) {
    pcs.addPropertyChangeListener(propertyName, pcl);
  }
  
  public void removePropertyChangeListener(final PropertyChangeListener pcl) {
    pcs.removePropertyChangeListener(pcl);
  }
  
  public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener pcl) {
    pcs.removePropertyChangeListener(propertyName, pcl);
  }
  // </editor-fold>
  
  /**
   * Returns the actual state of the load generator engine
   * @returns TRUE if the load generator can run another instance; FALSE otherwise
   */
  public abstract boolean isReady();
  
  /**
   * Returns the icon representing the load generator engine if it exists
   * @return Returns the icon representing the load generator or null
   */
  public abstract Image getIcon();
  
  /**
   * Returns a descriptive name for the particular load generator engine
   *
   * @return Returns a descriptive name
   */
  public abstract String getDisplayName();
  
  /**
   * Returns the list of all supported file extensions
   */
  public abstract Collection<String> getSupportedExtensions();
  
  /**
   * The actual implementation of the instance creating strategy
   * To be provided by a subclass
   */
  protected abstract ProcessInstance prepareInstance(final String scriptName);
  
  /**
   * This method is to be called when a new ProcessInstance object was created
   */
  private void registerProcess(final ProcessInstance instance) {
    //    System.out.println("AbstractLoadGenerator: processing new instance event");
    processes.add(instance);
    pcs.firePropertyChange(INSTANCE, false, true);
  }
  
  /**
   * To be called when the state of the load generator has been changed (ready/not ready)
   */
  private void fireReadyStateChanged(final boolean oldValue, final boolean newValue) {
    pcs.firePropertyChange(STATE, oldValue, newValue);
  }
  
  private void setLastReadyState(final boolean value) {
    fireReadyStateChanged(lastReadyState, value);
    lastReadyState = value;
  }
}
