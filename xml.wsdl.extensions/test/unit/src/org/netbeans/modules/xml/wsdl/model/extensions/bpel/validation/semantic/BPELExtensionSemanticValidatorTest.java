/*
 * BPELExtensionSemanticValidatorTest.java
 * JUnit based test
 *
 * Created on June 29, 2006, 7:32 PM
 */

package org.netbeans.modules.xml.wsdl.model.extensions.bpel.validation.semantic;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import junit.framework.*;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.extensions.TestCatalogModel;
import org.netbeans.modules.xml.wsdl.model.extensions.bpel.validation.ValidationHelper;
import org.netbeans.modules.xml.xam.spi.Validation;
import org.netbeans.modules.xml.xam.spi.Validation.ValidationType;
import org.netbeans.modules.xml.xam.spi.ValidationResult;
import org.netbeans.modules.xml.xam.spi.Validator.ResultItem;

/**
 *
 * @author radval
 */
public class BPELExtensionSemanticValidatorTest extends TestCase {
    
      private static final ResourceBundle mMessages =
        ResourceBundle.getBundle(BPELExtensionSemanticValidatorTest.class.getPackage().getName()+".Bundle");

    public BPELExtensionSemanticValidatorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testPropertyAliasMissingProperty() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PROPERTY_NAME"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPropertyAliasMissingProperty.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    public void testPropertyAlias1() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PROPERTY_ALIAS_MESSAGE_TYPE"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPropertyAlias1.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    public void testPropertyAlias2() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PROPERTY_ALIAS_ELEMENT"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPropertyAlias2.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    public void testPropertyAlias3() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PROPERTY_ALIAS_TYPE"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPropertyAlias3.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }

    public void testPortType1() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PORT_TYPE"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPortType1.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    public void testPortType2() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PORT_TYPE"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPortType2.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    public void testPartnerLinkType1() throws Exception {
         HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.add(mMessages.getString("VAL_INVALID_PARTNERLINK_TYPE"));
        
        String fileName = "/org/netbeans/modules/xml/wsdl/model/extensions/bpel/validation/semantic/resources/invalid/invalidPartnerLinkType1.wsdl";
        URL url = getClass().getResource(fileName);
        URI uri = url.toURI();
        
        validate(uri, expectedErrors);
    }
    
    private ValidationResult validate(URI relativePath) throws Exception {
        WSDLModel model = TestCatalogModel.getDefault().getWSDLModel(relativePath);
        Validation validation = new Validation();
        ValidationType validationType = Validation.ValidationType.COMPLETE;
        BPELExtensionSemanticValidator instance = new BPELExtensionSemanticValidator();
        ValidationResult result = 
            instance.validate(model, validation, validationType);
        return result;
    }
    
    private void validate(URI relativePath, HashSet<String> expectedErrors)
        throws Exception {
        System.out.println(relativePath);
        ValidationResult result = validate(relativePath);
        Iterator<ResultItem> it = result.getValidationResult().iterator();
        ValidationHelper.dumpExpecedErrors(expectedErrors);
        while (it.hasNext()) {
            ResultItem item = it.next();
//            System.out.println("    " + item.getDescription());
            assertTrue("Actual Error "+ item.getDescription() + "in " +relativePath, ValidationHelper.containsExpectedError(expectedErrors, item.getDescription()));
        }
        if (result.getValidationResult().size() == 0 && expectedErrors.size() > 0) {
            fail("Expected at least " + expectedErrors.size() + " error(s).  Got 0 errors instead");
        }
    }
    
}
