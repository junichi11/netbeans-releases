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

package org.netbeans.modules.j2ee.sun.validation.constraints;

import junit.framework.*;
import java.util.ArrayList;

import org.netbeans.modules.j2ee.sun.validation.Constants;
import org.netbeans.modules.j2ee.sun.validation.Failure;

/**
 *
 * @author  Rajeshwar Patil
 * @version %I%, %G%
 */
public class ConstraintTest extends TestCase{

    private ArrayList primaryColours = new ArrayList();


    public ConstraintTest(String name){
        super(name);
    }


    public static void main(String args[]){
        junit.textui.TestRunner.run(suite());
    }


    public void testCreate() {
      Constraint constraint = new NumberConstraint();
      assertNotNull(constraint);
    }


    public void testConstraintFailure(){
      Failure failure = new ConstraintFailure("Constraint",             //NOI18N
        "Element Value", "Element Name", "Failure Message",
            "Generic Failure Message");                                 //NOI18N

      assertNotNull(failure);
      assertTrue("Failure Message".equals(failure.failureMessage()));   //NOI18N
      
      ConstraintFailure constraintFailure = (ConstraintFailure) failure;
      assertTrue("Constraint".equals(
            constraintFailure.getConstraint()));                        //NOI18N
      assertTrue("Element Value".equals(
            constraintFailure.getFailedValue()));                       //NOI18N
      assertTrue("Element Name".equals(constraintFailure.getName()));   //NOI18N
      assertTrue("Generic Failure Message".equals(
            constraintFailure.getGenericfailureMessage()));             //NOI18N
    }


    public void testRangeConstraint() {
      Constraint constraint = new RangeConstraint("100", "250");        //NOI18N
      assertNotNull(constraint);
      assertTrue("Value : 152",                                         //NOI18N
          constraint.match("152", "message").isEmpty());                //NOI18N
      assertTrue("Value : 300(out of range)",                           //NOI18N
          !(constraint.match("300",  "message").isEmpty()));            //NOI18N
      assertTrue("Value : xyz(non-numeric)",                            //NOI18N
          !(constraint.match("xyz",  "message").isEmpty()));            //NOI18N
    }


    public void testNonZeroLengthConstraint() {
      Constraint constraint = new NonZeroLengthConstraint();
      String str = new String();
      assertNotNull(constraint);
      assertNotNull(str);
      assertTrue("Value : xyz",                                         //NOI18N
          constraint.match("red", "message").isEmpty());                //NOI18N
      assertTrue("Value : null",                                        //NOI18N
          constraint.match(null,  "message").isEmpty());                //NOI18N
      assertTrue("Value : Empty String",                                //NOI18N
          !(constraint.match(str,  "message").isEmpty()));              //NOI18N
    }


    public void testNonBlankConstraint() {
      Constraint constraint = new NonBlankConstraint();
      String str = new String();
      assertNotNull(constraint);
      assertNotNull(str);
      assertTrue("Value : xyz",                                         //NOI18N
          constraint.match("xyz", "message").isEmpty());                //NOI18N
      assertTrue("Value : null",                                        //NOI18N
          constraint.match(null,  "message").isEmpty());                //NOI18N
      assertTrue("Value : Empty String",                                //NOI18N
          constraint.match(str,  "message").isEmpty());                 //NOI18N
      assertTrue("Value : Blank String",                                //NOI18N
          !(constraint.match("      ",  "message").isEmpty()));         //NOI18N
    }


    public void testZeroToMaxIntegerConstraint() {
      Constraint constraint = new ZeroToMaxIntegerConstraint();
      String str = new String();
      assertNotNull(constraint);
      assertNotNull(str);
      assertTrue("Value : 0",                                           //NOI18N
          constraint.match("0", "message").isEmpty());                  //NOI18N
      assertTrue("Value : null",                                        //NOI18N
          constraint.match(null,  "message").isEmpty());                //NOI18N
      assertTrue("Value : Empty String",                                //NOI18N
          constraint.match(str,  "message").isEmpty());                 //NOI18N
      assertTrue("Value : Blank String",                                //NOI18N
          !(constraint.match("      ",  "message").isEmpty()));         //NOI18N
      assertTrue("Value : 1234",                                        //NOI18N
          constraint.match("1234",  "message").isEmpty());              //NOI18N
      assertTrue("Value : -1",                                          //NOI18N
          !(constraint.match("-1",  "message").isEmpty()));             //NOI18N
    }


    public void testIntegerGreaterThanConstraint() {
      Constraint constraint = new IntegerGreaterThanConstraint("120");
      String str = new String();
      assertNotNull(constraint);
      assertNotNull(str);
      assertTrue("Value : xyz",                                         //NOI18N
          !(constraint.match("xyz", "message").isEmpty()));             //NOI18N
      assertTrue("Value : null",                                        //NOI18N
          constraint.match(null,  "message").isEmpty());                //NOI18N
      assertTrue("Value : Empty String",                                //NOI18N
          constraint.match(str,  "message").isEmpty());                 //NOI18N
      assertTrue("Value : Blank String",                                //NOI18N
          !(constraint.match("      ",  "message").isEmpty()));         //NOI18N
      assertTrue("Value : 120",                                         //NOI18N
          !(constraint.match("120",  "message").isEmpty()));            //NOI18N
      assertTrue("Value : 121",                                         //NOI18N
          constraint.match("121",  "message").isEmpty());               //NOI18N
    }


    public void testConstraintUtils() {
      ConstraintUtils constraintUtils = 
            new ConstraintUtils();
      assertNotNull(constraintUtils);
      
      String message = 
        constraintUtils.formatFailureMessage("Constraint",          //NOI18N
                "Element");                                             //NOI18N
      int index = message.lastIndexOf("failed_for");                    //NOI18N 
      //Test to make sure that the strings are picked up from the bundle.
      assertTrue(-1 == index);
    }


    public void testCardinalConstraint() {
      CardinalConstraint mandatoryConstraint = 
            new CardinalConstraint(Constants.MANDATORY_ELEMENT);
      CardinalConstraint opationalConstraint = 
            new CardinalConstraint(Constants.OPTIONAL_ELEMENT);
      CardinalConstraint mandatoryArrayConstraint = 
            new CardinalConstraint(Constants.MANDATORY_ARRAY);
      CardinalConstraint opationalArrayConstraint = 
            new CardinalConstraint(Constants.OPTIONAL_ARRAY);
      String[] array = { "abc", "xyz" };                                //NOI18N    
      String[] emptyArray = {};
      
      assertNotNull(mandatoryConstraint);
      assertNotNull(opationalConstraint);
      assertNotNull(mandatoryArrayConstraint);
      assertNotNull(opationalArrayConstraint);
      
      assertTrue("Value : xyz",                                         //NOI18N
          mandatoryConstraint.match("xyz",  "message").isEmpty());      //NOI18N
      assertTrue("Value : null",                                        //NOI18N
          !(mandatoryConstraint.match(null,  "message").isEmpty()));    //NOI18N

      assertTrue("Value : xyz",                                         //NOI18N
          opationalConstraint.match("xyz",  "message").isEmpty());      //NOI18N
      assertTrue("Value : null", //NOI18N
          opationalConstraint.match(null,  "message").isEmpty());       //NOI18N
      
      assertTrue("Value : String[]", //NOI18N
          mandatoryArrayConstraint.match(array,  "message").isEmpty()); //NOI18N
      assertTrue("Value : Empty String[]",                              //NOI18N
          !(mandatoryArrayConstraint.match(emptyArray,
                "message").isEmpty()));                                 //NOI18N
      
      assertTrue("Value : String[]",                                    //NOI18N
          opationalArrayConstraint.match(array,  "message").isEmpty()); //NOI18N
      assertTrue("Value : Empty String[]",                              //NOI18N
          opationalArrayConstraint.match(emptyArray,
                "message").isEmpty());                                  //NOI18N
    }


    public void testInConstraint() {
      ArrayList primaryColours = new ArrayList();
      primaryColours.add("red");                                        //NOI18N
      primaryColours.add("green");                                      //NOI18N
      primaryColours.add("yellow");                                     //NOI18N
      primaryColours.add("blue");                                       //NOI18N

      Constraint constraint = new InConstraint(primaryColours);
      assertNotNull(constraint);
      assertTrue("Value : red",                                         //NOI18N
          constraint.match("red", "message").isEmpty());                //NOI18N
      assertTrue("Value : black(not in enumeration)",                   //NOI18N
          !(constraint.match("black",  "message").isEmpty()));          //NOI18N
    }


    public void testBooleanConstraint() {
      Constraint constraint = new BooleanConstraint();
      assertNotNull(constraint);
      assertTrue("Value : TRUE",                                        //NOI18N
          constraint.match("TRUE", "message").isEmpty());               //NOI18N
      assertTrue("Value : xyz",                                         //NOI18N
          !(constraint.match("xyz", "message").isEmpty()));             //NOI18N
    }


    public void testAndConstraint() {
      Constraint constraint = new AndConstraint(new MandatoryConstraint(),
          new NumberConstraint());
      assertNotNull(constraint);
      assertTrue("Value : 123",                                         //NOI18N
          constraint.match("123", "message").isEmpty());                //NOI18N
      assertTrue("Value : xyz",                                         //NOI18N
          !(constraint.match("xyz", "message").isEmpty()));             //NOI18N
    }


    public void testNumberConstraint() {
      Constraint constraint = new NumberConstraint();
      assertNotNull(constraint);
      assertTrue("Value : 1234",                                        //NOI18N
      constraint.match("1234", "message" ).isEmpty());                  //NOI18N
      assertTrue("Value : abc",                                         //NOI18N
        !(constraint.match("abc", "message").isEmpty()));               //NOI18N
    }


    public void testMandatoryConstraint(){
          Constraint constraint = new MandatoryConstraint();
          assertNotNull(constraint);
          assertTrue("Value : abc",                                     //NOI18N
              constraint.match("abc", "message").isEmpty());            //NOI18N
          assertTrue("Value : null length string",                      //NOI18N
              constraint.match("", "message").isEmpty());               //NOI18N
          assertTrue("Value :  null",                                   //NOI18N
              !(constraint.match(null, "message").isEmpty()));          //NOI18N
    }


    /**
    * Define suite of all the Tests to run.
    */
    public static Test suite(){
        TestSuite suite = new TestSuite(ConstraintTest.class);
        return suite;
    }


    /**
    * Initialize; allocate any resources needed to perform Tests.
    */
    protected void setUp() {
    }


    /**
    * Free all the resources initilized/allocated to perform Tests.
    */
    protected void tearDown() {
    }


    private void nyi() {
        ///fail("Not yet implemented");                                 //NOI18N
    }
}
