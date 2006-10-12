/*
 * XMLModelMapperVisitorTest.java
 * JUnit based test
 *
 * Created on October 31, 2005, 11:06 AM
 */

package org.netbeans.modules.xml.schema.model.impl.xdm;

import junit.framework.*;
import org.netbeans.modules.xml.schema.model.Schema;
import org.netbeans.modules.xml.schema.model.SchemaComponent;
import org.netbeans.modules.xml.schema.model.Util;
import org.netbeans.modules.xml.schema.model.impl.SchemaModelImpl;
import org.netbeans.modules.xml.schema.model.visitor.FindSchemaComponentFromDOM;
import org.netbeans.modules.xml.xdm.nodes.Document;
import org.netbeans.modules.xml.xdm.nodes.Element;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
/**
 *
 * @author Administrator
 */
public class XMLModelMapperVisitorTest extends TestCase {
    
    public static final String TEST_XSD     = "resources/PurchaseOrder.xsd";
    
    private SchemaModelImpl model;
    private Schema schema;
    private Document doc;
    private FindSchemaComponentFromDOM instance;
    
    public XMLModelMapperVisitorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        model = (SchemaModelImpl)Util.loadSchemaModel(TEST_XSD);
        schema = model.getSchema();
        doc = (org.netbeans.modules.xml.xdm.nodes.Document)model.getDocument();
        instance = new FindSchemaComponentFromDOM();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(XMLModelMapperVisitorTest.class);
        
        return suite;
    }
    
    /**
     * Test of findComponent method, of class org.netbeans.modules.xml.schema.model.visitor.XMLModelMapperVisitor.
     */
    public void testFindComponent() {
        System.out.println("findComponent");
        
        Element poElement = (Element)doc.getDocumentElement().getChildNodes().item(1);
        SchemaComponent poComponent = schema.getChildren().get(0);
        SchemaComponent result = instance.findComponent(schema, poElement);
        assertEquals(poComponent, result);
        
        Element poTypeElement = (Element)doc.getDocumentElement().getChildNodes().item(5);
        SchemaComponent poGlobalType = schema.getChildren().get(2);
        result = instance.findComponent(schema, poTypeElement);
        assertEquals(poGlobalType, result);

        Element shiptoElement = (Element)doc.getDocumentElement().getChildNodes().item(5).
                getChildNodes().item(1).getChildNodes().item(1);
        SchemaComponent shiptoComponent = poGlobalType.getChildren().get(0).getChildren().get(0);
        result = instance.findComponent(schema, shiptoElement);
        assertEquals(shiptoComponent, result);
    }
    
}
