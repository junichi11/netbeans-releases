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

package org.netbeans.api.progress;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import junit.framework.TestCase;
import org.netbeans.progress.module.Controller;
import org.netbeans.progress.spi.InternalHandle;
import org.netbeans.progress.spi.ProgressEvent;
import org.netbeans.progress.spi.ProgressUIWorker;
import org.openide.util.Cancellable;

/**
 *
 * @author Milos Kleint (mkleint@netbeans.org)
 */
public class ProgressHandleFactoryTest extends TestCase {

    public ProgressHandleFactoryTest(String testName) {
        super(testName);
    }

    /**
     * Test of createHandle method, of class org.netbeans.progress.api.ProgressHandleFactory.
     */
    public void testCreateHandle() {
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("task 1");
        InternalHandle internal = handle.getInternalHandle();
        assertEquals("task 1", internal.getDisplayName());
        assertFalse(internal.isAllowCancel());
        assertFalse(internal.isCustomPlaced());
        assertEquals(InternalHandle.STATE_INITIALIZED, internal.getState());
        
        handle = ProgressHandleFactory.createHandle("task 2", new TestCancel());
        internal = handle.getInternalHandle();
        assertEquals("task 2", internal.getDisplayName());
        assertTrue(internal.isAllowCancel());
        assertFalse(internal.isCustomPlaced());
        assertEquals(InternalHandle.STATE_INITIALIZED, internal.getState());
        
    }

    
    public void testCustomComponentIsInitialized() {
        Controller.defaultInstance = new TestController();
        
        ProgressHandle handle = ProgressHandleFactory.createHandle("task 1");
        JComponent component = ProgressHandleFactory.createProgressComponent(handle);
        
        handle.start(15);
        handle.progress(2);
        waitForTimerFinish();
        
        assertEquals(15, ((JProgressBar) component).getMaximum());
        assertEquals(2, ((JProgressBar) component).getValue());
        
        handle = ProgressHandleFactory.createHandle("task 2");
        component = ProgressHandleFactory.createProgressComponent(handle);
        
        handle.start(20);
        waitForTimerFinish();
        
        assertEquals(20, ((JProgressBar) component).getMaximum());
        assertEquals(0, ((JProgressBar) component).getValue());
        
    }
     
     private static class TestCancel implements Cancellable {
         public boolean cancel() {
             return true;
         }
         
   }
   
     
    private class TestController extends Controller {
        public TestController() {
            super(new ProgressUIWorker() {
                public void processProgressEvent(ProgressEvent event) { }
                public void processSelectedProgressEvent(ProgressEvent event) { }
            });
        }
        
        public Timer getTestTimer() {
            return timer;
        }
    }
    
    private void waitForTimerFinish() {
        TestController tc = (TestController)Controller.defaultInstance;
        int count = 0;
        do {
            if (count > 10) {
                fail("Takes too much time");
            }
            try {
                count = count + 1;
                Thread.sleep(300);
            } catch (InterruptedException exc) {
                System.out.println("interrupted");
            }        
        } while (tc.getTestTimer().isRunning());

    }
     
    
}
