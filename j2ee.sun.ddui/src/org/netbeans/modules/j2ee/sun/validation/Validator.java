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

package org.netbeans.modules.j2ee.sun.validation;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.netbeans.modules.j2ee.sun.validation.Constants;
import org.netbeans.modules.j2ee.sun.validation.constraints.Constraint;
import org.netbeans.modules.j2ee.sun.validation.util.BundleReader;
import org.netbeans.modules.j2ee.sun.validation.util.Utils;
import org.netbeans.modules.j2ee.sun.validation.Validatee;

/**
 * Validator  is an <code>Object</code> that knows how to validate its
 * corresponding <Object>Object</code> called {@link <code>Validatee</code>}
 * <p>
 * Every Validatee has a corresponding {@link Validator} object and this object
 * knows how to validate its <code>Validatee</code>. <code>Validator</code> 
 * maintains a list of {@link <code>Constraint</code>}s that needs to be applied
 * for each of the element of its <code>Validatee</code>.
 * <code>Constraint</code> objects are built using an two file , Validation File
 * and Constraints File.
 * {@ValidationManager} is an object that constructs the <code>Validator</code>
 * objects for all the Validatees. <code>ValidationManager</code> maintains a
 * map of xpaths to Validators. It constructs this by reading two files -
 * Validation File and Constraints File. Validation File specifies
 * constraints to be applied to the elements. Constraints File provides 
 * information about the Constraints such as Constraints class name, number and
 * type of Constraint's constructor arguments.
 * <p>
 * Validations are performed, recursively.
 * Two types of Validations are perfomed, Structural validations and specified
 * validations. Structural validations are expressed through 
 * <code>{@link CardinalConstraint}</code>. <code>CardinalConstraint</code> is 
 * an implicit constraint. Its always applied to each of the element; you dont
 * need to specify it explicitly. Whereas , specified Constaints need to be
 * explicitly specified for each element you wanted to apply this 
 * <code>Constaint</code> to. You can also define, your own custom
 * <code>Constraint</code>s and apply them to any elements.
 *
 * @see Constraint
 * @see Validator
 * @see ValidationManager
 * @see CardianlConstraint
 *
 * @author  Rajeshwar Patil
 * @version %I%, %G%
 */
class Validator {
    /* A class implementation comment can go here. */
    
    private static String SIZE_PREFIX = "size";                         //NOI18N
    
    /**
     * A map that stores <code>Constraint</code> lists for the elements
     * of the <code>Validatee</code> that this Object corresponds to.
     */
    private HashMap elementToConstraints = null;

    private Utils utils = null;

    /** Creates a new instance of Validator */
    Validator() {
        elementToConstraints = new HashMap();
        utils = new Utils();
    }


    /**
     * Adds the given <code>Constraint</code> for the given element in the
     * map maintained by this object. This maps maintains Constraint lists for 
     * each of the elements of the Corresponding Validatee.
     * 
     * @param element the element for which the given 
     * <code>constraint</code> to be added to its list
     * @param constraint the given <code>Constraint</code> to be added
     * to the list of Constraints of the given <code>element</code>.
     */
    void addElementConstraint(String element, Constraint constraint){
        ArrayList list = (ArrayList) elementToConstraints.get(element);
        if(null == list) {
            list = new ArrayList();
        }
        list.add(constraint);
        elementToConstraints.put(element, list);
    }


    /**
     * Validates the given element. The given element could be a
     * single element or an array.
     * 
     * @param elementName the element to be validated
     * @param elementDtdName the dtd name of the element to be validated
     * @param validatee the <code>Validatee</code> object that this object
     * corresponds to.
     *
     * @return <code>Collection</code> the Collection of
     * <code>ConstraintFailure</code> Objects. Collection is empty
     * if there are no failures.
     */
    Collection validate(String elementName,String elementDtdName,
                Validatee validatee){
        ArrayList failures = new ArrayList();
        boolean isIndexed = validatee.isIndexed(elementName);

        if(isIndexed){
            failures.addAll(validateProperties(elementName,
                elementDtdName, validatee));
        } else {
            failures.addAll(validateProperty(elementName,
                elementDtdName, validatee));
        }
        return failures;
    }


    /**
     * Validates the given Element.
     * 
     * @param property the Element to be validated
     * @param absoluteDtdName the complete dtd name of <code>property</code>
     * @param fieldName the name of the GUI field, associated 
     * with <code>property</code>
     *
     * @return <code>Collection</code> the Collection of
     * <code>ConstraintFailure</code> Objects. Collection is empty
     * if there are no failures.
     */
    Collection validateIndividualProperty(String property,
            String absoluteDtdName, String fieldName){
        //This method  --fetches the constraint list for the specified element
        //             --applies the constraints to the specified element

        ArrayList failures = new ArrayList();
        String elementDtdName = utils.getName(absoluteDtdName,
                Constants.XPATH_DELIMITER_CHAR);

        ArrayList constraintList =
            (ArrayList)elementToConstraints.get(elementDtdName);

        if(constraintList != null) {
            failures.addAll(validate(property, constraintList, fieldName));
        } else {
            ///String format = 
            ///    BundleReader.getValue("MSG_No_definition_for");      //NOI18N
            ///    Object[] arguments = 
            ///        new Object[]{"Constraints", absoluteDtdName};    //NOI18N
            ///System.out.println(MessageFormat.format(format, arguments));
        }
        return failures;
    }


    /**
     * Validates the given element. The given element is a scalar element.
     * 
     * @param elementName the element to be validated
     * @param elementDtdName the dtd name of the element to be validated
     * @param validatee the <code>Validatee</code> object that this object
     * corresponds to.
     *
     * @return <code>Collection</code> the Collection of
     * <code>ConstraintFailure</code> Objects. Collection is empty
     * if there are no failures.
     */
    private Collection validateProperty(String elementName,
            String elementDtdName, Validatee validatee){
        //This method  --fetches the value of the specified element
        //             --fetches the constraint list for the specified element
        //             --applies the constraints to the specified element
        
        ArrayList failures = new ArrayList();
        ArrayList constraintList =
            (ArrayList)elementToConstraints.get(elementDtdName);
        String name = validatee.getXPath() + Constants.XPATH_DELIMITER +
            elementDtdName;
        if(constraintList != null) {
            String property = (String)validatee.getElement(elementName);
            failures.addAll(validate(property, constraintList, name));
        } else {
            ///String format = 
            ///    BundleReader.getValue("MSG_No_definition_for");      //NOI18N
            ///    Object[] arguments = new Object[]{"Constraints", name}; //NOI18N
            ///System.out.println(MessageFormat.format(format, arguments));
        }
        return failures;
    }


    /**
     * Validates the given element. The given element is an array element.
     * 
     * @param elementName the element to be validated
     * @param elementDtdName the dtd name of the element to be validated
     * @param validatee the <code>Validatee</code> object that this object
     * corresponds to.
     *
     * @return <code>Collection</code> the Collection of
     * <code>ConstraintFailure</code> Objects. Collection is empty
     * if there are no failures.
     */
    private Collection validateProperties(String elementName,
            String elementDtdName, Validatee validatee){
        ArrayList failures = new ArrayList();
        int noOfElements = 0;
        String sizeMethodName =  utils.methodNameFromBeanName(
            elementName, SIZE_PREFIX);
        Method sizeMethod = validatee.getMethod(sizeMethodName);
        noOfElements = ((Integer)validatee.invoke(sizeMethod)).intValue();
        
        ArrayList constraintList =
            (ArrayList)elementToConstraints.get(elementDtdName);
        if(constraintList != null) {
            for(int i=0; i<noOfElements; i++){
                String property = (String)validatee.getElement(elementName,
                    i);
                String name = validatee.getXPath() + Constants.XPATH_DELIMITER +
                    utils.getIndexedName(elementDtdName, i);
                failures.addAll(validate(property, constraintList, name));
            }
        } else {
            ///String format = 
            ///    BundleReader.getValue("MSG_No_definition_for");      //NOI18N
            ///    Object[] arguments = 
            ///        new Object[]{"Constraints", elementName};        //NOI18N
            ///System.out.println(MessageFormat.format(format, arguments));
        }
        return failures;
    }


    /**
     * Applies the given set of <code>constraints</code> to the given string
     * Returns set of constraints that the given string fails to satisfy.
     * 
     * @param value the value to be validated
     * @param constraints the given set of <code>Constraint</code>s
     * @param name the name of the element being validated
     *
     * @return <code>Collection</code> the Collection of
     * <code>ConstraintFailure</code> Objects. Collection is empty
     * if there are no failures.
     */
    private Collection validate(String value, ArrayList constraints,
            String name){
        ArrayList failed_constrains = new ArrayList();
        int size = constraints.size();
        Constraint constraint =  null;
        for(int i=0; i<size; i++) {
            constraint = (Constraint)constraints.get(i);
            failed_constrains.addAll(constraint.match(value, name));
        }
        return failed_constrains;
    }
}   
