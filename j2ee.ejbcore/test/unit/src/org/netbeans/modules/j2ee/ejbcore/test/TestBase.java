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

package org.netbeans.modules.j2ee.ejbcore.test;

import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Common ancestor for all test classes.
 *
 * @author Andrei Badea
 */
public class TestBase extends NbTestCase {

    static {
        // set the lookup which will be returned by Lookup.getDefault()
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
        assertEquals("Unable to set the default lookup!", Lkp.class, Lookup.getDefault().getClass());
        setLookups();
        assertEquals(RepositoryImpl.class, Lookup.getDefault().lookup(Repository.class).getClass());
        assertEquals("The default Repository is not our repository!", RepositoryImpl.class, Repository.getDefault().getClass());
    }
    
    public TestBase(String name) {
        super(name);
    }
    
    public static void setLookups(Object... lookups) {
        ((Lkp)Lookup.getDefault()).setProxyLookups(Lookups.fixed(lookups));
    }
    
    public static final class Lkp extends ProxyLookup {
        
        private final Repository repository = new RepositoryImpl();
        
        public Lkp() {
            setProxyLookups(new Lookup[0]);
        }
        
        private void setProxyLookups(Lookup... lookups) {
            Lookup[] allLookups = new Lookup[lookups.length + 3];
            ClassLoader classLoader = TestBase.class.getClassLoader();
            allLookups[0] = Lookups.singleton(classLoader);
            allLookups[1] = Lookups.singleton(repository);
            System.arraycopy(lookups, 0, allLookups, 2, lookups.length);
            allLookups[allLookups.length - 1] = Lookups.metaInfServices(classLoader);
            setLookups(allLookups);
        }
        
    }
    
}
