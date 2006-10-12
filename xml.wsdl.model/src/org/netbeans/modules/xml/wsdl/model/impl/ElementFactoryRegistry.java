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

package org.netbeans.modules.xml.wsdl.model.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.netbeans.modules.xml.wsdl.model.spi.*;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author rico
 * @author Nam Nguyen
 *
 * Registry for factories of WSDL elements. In order to register an ElementFactory,
 * a QName must be provided of an element for which the factory will create a
 * WSDLComponent.
 */
public class ElementFactoryRegistry {
    
    private static ElementFactoryRegistry registry = null;
    private Map<QName, ElementFactory> factories = null;
    
    private ElementFactoryRegistry() {
        initialize();
    }
    
    public static ElementFactoryRegistry getDefault(){
        if (registry == null) {
            registry = new ElementFactoryRegistry();
        }
        return registry;
    }

    //TODO listen on changes when we have external extensions
    private void initialize(){
        factories = new Hashtable<QName, ElementFactory>();
        Lookup.Result results = Lookup.getDefault().lookup(new Lookup.Template(ElementFactory.class));
        for (Object service : results.allInstances()){
            register((ElementFactory)service);
        }
        
        //try meta-inf services lookup using this class's classloader
        //if no factories are found, This is required for lookup to work
        //from comp app project ant task
        if (factories.size() < 1) {
            Lookup lu2 = Lookups.metaInfServices(this.getClass().getClassLoader());
            Lookup.Result results2 = lu2.lookup(new Lookup.Template(ElementFactory.class));
            for (Object service : results2.allInstances()){
                register((ElementFactory)service);
            }
        }
    }
    
    public void register(ElementFactory factory) {
        for (QName q : factory.getElementQNames()) {
            factories.put(q, factory);
        }
        resetQNameCache();
    }
    
    public void unregister(ElementFactory fac){
        for (QName q : fac.getElementQNames()) {
            factories.remove(q);
        }
        resetQNameCache();
    }
    
    public ElementFactory get(QName type){
        return factories.get(type);
    }
    
    private Set<Class> knownEmbeddedModelTypes = null;
    private Set<QName> knownQNames = null;
    private Set<String> knownNames = null;
    
    public void resetQNameCache() {
        knownEmbeddedModelTypes = null;
        knownQNames = null;
        knownNames = null;
    }
    
    public Set<QName> getKnownQNames() {
        return Collections.unmodifiableSet(knownQNames());
    }
    
    private Set<QName> knownQNames() {
        if (knownQNames == null) {
            knownQNames = new HashSet<QName>();
            for (ElementFactory f : factories.values()) {
                for (QName q : f.getElementQNames()) {
                    if (! knownQNames.add(q)) {
                        String msg = "Duplicate factory for: "+q;
                        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "getKnownQNames", msg); //NOI18N
                    }
                }
            }
        }
        return knownQNames;
    }

    public Set<String> getKnownElementNames() {
        return Collections.unmodifiableSet(knownElementNames());
    }
    
    private Set<String> knownElementNames() {
        if (knownNames == null) {
            knownNames = new HashSet<String>();
            for (QName q : knownQNames()) {
                knownNames.add(q.getLocalPart());
            }
        }
        return knownNames;
    }
    
    public void addEmbeddedModelQNames(AbstractDocumentModel embeddedModel) {
        if (knownEmbeddedModelTypes == null) {
            knownEmbeddedModelTypes = new HashSet();
        }
        if (! knownEmbeddedModelTypes.contains(embeddedModel.getClass())) {
            knownQNames().addAll(embeddedModel.getQNames());
            knownNames = null;
        }
    }
}
