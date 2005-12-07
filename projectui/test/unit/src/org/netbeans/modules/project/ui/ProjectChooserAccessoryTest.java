/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.project.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.project.ui.actions.TestSupport.ChangeableLookup;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectChooserAccessoryTest extends NbTestCase {
    
    public ProjectChooserAccessoryTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    /**The cycles in project dependencies should be handled gracefully:
     */
    public void testAddSubprojects() {
        ChangeableLookup l1 = new ChangeableLookup(new Object[0]);
        ChangeableLookup l2 = new ChangeableLookup(new Object[0]);
        Project p1 = new TestProject(l1);
        Project p2 = new TestProject(l2);
        
        Set subprojects1 = new HashSet();
        Set subprojects2 = new HashSet();
        
        subprojects1.add(p2);
        subprojects2.add(p1);
        
        l1.change(new Object[] {new SubprojectProviderImpl(subprojects1)});
        l2.change(new Object[] {new SubprojectProviderImpl(subprojects2)});
        
        List result = new ArrayList();
        
        ProjectChooserAccessory.addSubprojects(p1, result, new HashMap());
        
        assertTrue(new HashSet(Arrays.asList(new Object[] {p1, p2})).equals(new HashSet(result)));
    }
    
    private final class TestProject implements Project {
        
        private Lookup l;
        
        public TestProject(Lookup l) {
            this.l = l;
        }
        
        public FileObject getProjectDirectory() {
            throw new UnsupportedOperationException("Should not be called in this test.");
        }
        
        public Lookup getLookup() {
            return l;
        }
    }
    
    private static final class SubprojectProviderImpl implements SubprojectProvider {
        
        private Set subprojects;
        
        public SubprojectProviderImpl(Set subprojects) {
            this.subprojects = subprojects;
        }
        
        public Set getSubprojects() {
            return subprojects;
        }

        public void addChangeListener(ChangeListener listener) {
        }

        public void removeChangeListener(ChangeListener listener) {
        }
        
    }
}
