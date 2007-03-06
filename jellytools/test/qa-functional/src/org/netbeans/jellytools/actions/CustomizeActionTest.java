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
package org.netbeans.jellytools.actions;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.junit.NbTestSuite;

/** Test org.netbeans.jellytools.actions.CustomizeAction.
 *
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @author Jiri.Skrivanek@sun.com
 */
public class CustomizeActionTest extends JellyTestCase {
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public CustomizeActionTest(String testName) {
        super(testName);
    }
    
    /** method used for explicit testsuite definition
     */
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new CustomizeActionTest("testPerformPopup"));
        suite.addTest(new CustomizeActionTest("testPerformAPI"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE
     * @param args command line arguments
     */
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    // "Customizer Dialog"
    private final static String CUSTOMIZER_DIALOG_TITLE = Bundle.getString("org.netbeans.core.windows.services.Bundle", "CTL_Customizer_dialog_title");
    private static Node node;

    public void setUp() {
        System.out.println("### "+getName()+" ###");  // NOI18N
        if(node == null) {
            node = new Node(new SourcePackagesNode("SampleProject"), "sample1|properties.properties"); // NOI18N
        }
    }
    
    public void tearDown() {
        new JDialogOperator(CUSTOMIZER_DIALOG_TITLE).close();
    }
    
    /** Test performPopup. */
    public void testPerformPopup() {
        new CustomizeAction().performPopup(node);
    }
    
    /** Test performAPI. */
    public void testPerformAPI() {
        new CustomizeAction().performAPI(node);
    }
}
