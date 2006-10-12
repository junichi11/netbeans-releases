/*
 * MergeTest.java
 * JUnit based test
 *
 * Created on October 28, 2005, 3:40 PM
 */

package org.netbeans.modules.xml.schema.model.impl.xdm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import junit.framework.*;
import org.netbeans.modules.xml.schema.model.*;
import org.netbeans.modules.xml.schema.model.impl.GlobalElementImpl;
import org.netbeans.modules.xml.schema.model.impl.GlobalSimpleTypeImpl;
import org.netbeans.modules.xml.schema.model.impl.LocalComplexTypeImpl;
import org.netbeans.modules.xml.schema.model.impl.SchemaAttributes;
import org.netbeans.modules.xml.schema.model.impl.SchemaImpl;
import org.netbeans.modules.xml.schema.model.impl.SequenceImpl;
import org.netbeans.modules.xml.xam.ComponentEvent;
import org.netbeans.modules.xml.xam.ComponentListener;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.netbeans.modules.xml.xam.dom.DocumentComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author ajit
 */
public class SyncTest extends TestCase {
    
    public static final String TEST_XSD     = "resources/PurchaseOrder.xsd";
    public static final String TEST_XSD_OP     = "resources/PurchaseOrderSyncTest.xsd";
    
    public SyncTest(String testName) {
        super(testName);
    }
    @Override
    protected void setUp() throws Exception {
	
    }
    
    @Override
    protected void tearDown() {
        TestCatalogModel.getDefault().clearDocumentPool();
    }
    
    class TestComponentListener implements ComponentListener {
        ArrayList<ComponentEvent> accu = new ArrayList<ComponentEvent>();
        public void valueChanged(ComponentEvent evt) {
            accu.add(evt);
        }
        public void childrenAdded(ComponentEvent evt) {
            accu.add(evt);
        }
        public void childrenDeleted(ComponentEvent evt) {
            accu.add(evt);
        }
        public void reset() { accu.clear(); }
        public int getEventCount() { return accu.size(); }
        public java.util.List<ComponentEvent> getEvents() { return accu; }
    
        private void assertEvent(ComponentEvent.EventType type, DocumentComponent source) {
            for (ComponentEvent e : accu) {
                if (e.getEventType().equals(type) &&
                    e.getSource() == source) {
                    return;
                }
            }
            assertTrue("Expect component change event " + type +" on source " + source +
                    ". Instead received: " + accu, false);
        }
    }    
    
    public void testUpdateAtSchemaRoot() throws Exception {
        model = Util.loadSchemaModel(TEST_XSD);
        Document doc = AbstractDocumentModel.class.cast(model).getBaseDocument();
        
        Util.setDocumentContentTo(doc, TEST_XSD_OP);
        model.sync();
        
        assertEquals("qualified", model.getSchema().getAttributeFormDefault().toString());
        assertEquals(3,model.getSchema().getElements().size());
        GlobalElement ge = (GlobalElement)model.getSchema().getElements().toArray()[2];
        assertEquals("comment2",ge.getName());
        assertEquals(2,model.getSchema().getComplexTypes().size());
        ElementReference poComment = (ElementReference)
            Util.findComponent(model.getSchema(), "/schema/complexType[@name='PurchaseOrderType']/sequence/element[3]");
        // FIXME:  DiffFinder miss attribute change when parent got position change
        // assertEquals(1,poComment.getMinOccurs().intValue());
        assertEquals(1,model.getSchema().getSimpleTypes().size());
    }
    
    public void testUpdateDirectParentOnly() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/SyncTestNonGlobal_before.xsd");
        Document doc = AbstractDocumentModel.class.cast(model).getBaseDocument();
        SchemaImpl schema = (SchemaImpl) model.getSchema();
        Node schemaNode = schema.getPeer();
        GlobalElementImpl gei = (GlobalElementImpl) schema.getElements().iterator().next();
        Node elementNode = gei.getPeer();
        LocalComplexTypeImpl lcti = (LocalComplexTypeImpl) gei.getInlineType();
        Node lctiNode = lcti.getPeer();
        SequenceImpl seq = (SequenceImpl) lcti.getDefinition();
        
        Util.setDocumentContentTo(doc, "resources/SyncTestNonGlobal_after.xsd");
        model.sync();
        
        //make sure elements and nodes on the path before sequence is same, only peer updated 
        assertSame("testUpdateElementOnly.schema", schema, model.getSchema());
        assertNotSame("testUpdateElementOnly.schema.node", schemaNode, schema.getPeer());
        assertSame("testUpdateElementOnly.element", gei, schema.getElements().iterator().next());
        assertNotSame("testUpdateElementOnly.element.node", elementNode, gei.getPeer());
        assertSame("testUpdateElementOnly.element.type", lcti, gei.getInlineType());
        assertNotSame("testUpdateElementOnly.element.type.node", lctiNode, lcti.getPeer());
        assertSame("parent component should be the same", seq, lcti.getDefinition());
        seq = (SequenceImpl) lcti.getDefinition();
        assertEquals("testUpdateElementOnly.element.type.seq.count", 3, seq.getContent().size());
        assertEquals("testUpdateElementOnly.element.type.seq.element2", "Office", ((LocalElement)seq.getContent().get(2)).getName());
        assertEquals("testUpdateElementOnly.element.type.seq.element1", "Branch", ((LocalElement)seq.getContent().get(1)).getName());
    }
    
    public void testRemoveChildOfGlobalElement() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/SyncTestGlobal_before.xsd");
        Document doc = AbstractDocumentModel.class.cast(model).getBaseDocument();
        SchemaImpl schema = (SchemaImpl) model.getSchema();
        Node schemaNode = schema.getPeer();
        GlobalSimpleTypeImpl gst = (GlobalSimpleTypeImpl) schema.getSimpleTypes().iterator().next();
        
        Util.setDocumentContentTo(doc, "resources/SyncTestGlobal_after.xsd");
        model.sync();
        
        //make sure componentson the path before sequence is same, only peer get updated
        assertSame("testRemoveChildOfGlobalElement.schema", schema, model.getSchema());
        assertNotSame("testRemoveChildOfGlobalElement.schema.node", schemaNode, schema.getPeer());
        assertSame("parent component should be same as before sync", gst, schema.getSimpleTypes().iterator().next());
        assertNull("Annotation should have been remove", gst.getAnnotation());
        assertEquals("Attribute changed to new value", "allNNI", gst.getName());
    }
    
    public void testChangeAttributeOnly() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/SyncTestGlobal_before.xsd");
        Document doc = AbstractDocumentModel.class.cast(model).getBaseDocument();
        Schema schema = model.getSchema();
        
        Util.setDocumentContentTo(doc, "resources/SyncTestGlobal_after2.xsd");
        model.sync();
        
        //make sure elements and nodes on the path before sequence is same 
        assertSame("testRemoveChildOfGlobalElement.schema", schema, model.getSchema());
	assertEquals("parent component should be same as before sync", "allNNI-changed", schema.getSimpleTypes().iterator().next().getName());
    }
  
    public void testDocumentationText() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/loanApplication_annotationChanged.xsd");//"resources/loanApplication.xsd");
        Annotation ann = model.getSchema().getElements().iterator().next().getAnnotation();
        Iterator<Documentation> it = ann.getDocumentationElements().iterator();
        Documentation textDoc = it.next();
        Documentation htmlDoc = it.next();
        htmlDoc.getDocumentationElement();
        AppInfo appinfo = ann.getAppInfos().iterator().next();
        
        Util.setDocumentContentTo(model, "resources/loanApplication_annotationChanged.xsd");
        model.sync();
        
        assertEquals("text documentation sync", "A CHANGED loan application", textDoc.getContent());
        NodeList nl = htmlDoc.getDocumentationElement().getChildNodes();
        Element n = (Element) nl.item(1);
        n = (Element) n.getChildNodes().item(1);
        Text textNode = (Text) n.getChildNodes().item(0);
        assertEquals("html documentation sync", "Testing CHANGED documenation elemnent", textNode.getNodeValue());
        
        n = (Element) appinfo.getAppInfoElement().getChildNodes().item(1);
        textNode = (Text) n.getChildNodes().item(0);
        assertEquals("appinfo element sync", "checkForPrimesCHANGED", textNode.getNodeValue());
        n = (Element) appinfo.getAppInfoElement().getChildNodes().item(3);
        textNode = (Text) n.getChildNodes().item(0);
        assertEquals("appinfo element sync", "checkForPrimesADDED", textNode.getNodeValue());
    }
    
    public void testLocalElementReferenceTransform() throws Exception  {
        SchemaModel model = Util.loadSchemaModel("resources/PurchaseOrder.xsd");
        GlobalComplexType gct = model.getSchema().getComplexTypes().iterator().next();
        Sequence seq = (Sequence) gct.getDefinition();
        assertEquals("setup", "PurchaseOrderType", gct.getName());
        assertTrue("setup PurchaseOrderType.seqence[2]", seq.getContent().get(2) instanceof ElementReference);
        
        Util.setDocumentContentTo(model, "resources/PurchaseOrder_SyncElementRef.xsd");
        model.sync();
    
        LocalElement e = (LocalElement) seq.getContent().get(2);
        assertEquals("element ref transformed to local", "comment", e.getName());
        assertEquals("element ref transformed to local", "string", e.getType().get().getName());

        Util.setDocumentContentTo(model, "resources/PurchaseOrder.xsd");
        model.sync();
    
        ElementReference er = (ElementReference) seq.getContent().get(2);
        assertEquals("element ref transformed to local", "comment", er.getRef().get().getName());
    }
    
    public void testMultipleAdd() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/SyncTestNonGlobal_before.xsd");
        GlobalElement ge = model.getSchema().getElements().iterator().next();
        LocalComplexType lct = (LocalComplexType) ge.getInlineType();
        Sequence seq = (Sequence) lct.getDefinition();
        java.util.List<SequenceDefinition> sdl = seq.getContent();
        assertEquals("setup", 2, sdl.size());
        
        Util.setDocumentContentTo(model, "resources/SyncTestNonGlobal_multiple_adds.xsd");
        model.sync();
        
        assertEquals("multiple add to sequence", 5, seq.getContent().size());
    }
    
    public void testCreateGlobalElementUndoRedo() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/Empty.xsd");
        UndoManager ur = new UndoManager();
        model.addUndoableEditListener(ur);
        SchemaComponentFactory fact = model.getFactory();
        GlobalElement ge = fact.createGlobalElement();
        
        model.startTransaction();
        model.getSchema().addElement(ge);
        ge.setName("Foo"); // edit #1
        LocalComplexType lct = fact.createLocalComplexType();
        Sequence seq = fact.createSequence();
        lct.setDefinition(seq); 
        ge.setInlineType(lct);
        model.endTransaction();
        
        assertEquals(1, model.getSchema().getElements().size());
        ur.undo();
        assertEquals(0, model.getSchema().getElements().size());

        ur.redo();
        ge = model.getSchema().getElements().iterator().next();
        assertEquals("Foo", ge.getName());
        assertNotNull(ge.getInlineType());
        assertNotNull(((LocalComplexType)ge.getInlineType()).getDefinition());
    }
    
    public void testSetAttributeOnGlobalComplexTypeUndoRedo() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/PurchaseOrder.xsd");
        UndoManager ur = new UndoManager();
        model.addUndoableEditListener(ur);
        GlobalComplexType potype = model.getSchema().getComplexTypes().iterator().next();
        assertEquals("PurchaseOrderType", potype.getName());
        
        model.startTransaction();
        potype.setAbstract(Boolean.TRUE);
        model.endTransaction();
        
        ur.undo();
        assertNull(potype.getAttribute(SchemaAttributes.ABSTRACT));

        ur.redo();
        assertNotNull(potype.getAttribute(SchemaAttributes.ABSTRACT));
    }
    
    public void testCutAndPasteUndoRedo() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/PurchaseOrder.xsd");
        TestComponentListener listener = new TestComponentListener();
        model.addComponentListener(listener);
        UndoManager ur = new UndoManager();
        model.addUndoableEditListener(ur);
        
        ArrayList<GlobalComplexType> types = new ArrayList(model.getSchema().getComplexTypes());
        GlobalComplexType type = types.get(1);
        assertEquals("USAddress", type.getName());
        Sequence seq = (Sequence) type.getDefinition();
        java.util.List<SequenceDefinition> content = seq.getContent();
        LocalElement name = (LocalElement) content.get(0);
        assertEquals("name", name.getName());
        LocalElement street = (LocalElement) content.get(1);
        assertEquals("street", street.getName());
        Node nameNode = name.getPeer();
        Node streetNode = street.getPeer();
        SequenceDefinition copyName = (SequenceDefinition) name.copy(seq);
        SequenceDefinition copyStreet = (SequenceDefinition) street.copy(seq);
        
        model.startTransaction();
        seq.removeContent(name);
        seq.removeContent(street);
        seq.addContent(copyStreet, 0);
        seq.addContent(copyName, 1);
        model.endTransaction();
        
        content = seq.getContent();
        name = (LocalElement) content.get(1);
        street = (LocalElement) content.get(0);
        assertEquals("name", name.getName());
        assertEquals("street", street.getName());
        Node nameNode1 = name.getPeer();
        Node streetNode1 = street.getPeer();
        
        ur.undo();
        content = seq.getContent();
        name = (LocalElement) content.get(0);
        street = (LocalElement) content.get(1);
        assertEquals("name", name.getName());
        assertEquals("street", street.getName());
        Node nameNode2 = name.getPeer();
        Node streetNode2 = street.getPeer();
        assertTrue(name.referencesSameNode(nameNode));
        assertTrue(street.referencesSameNode(streetNode));
        
        listener.reset();
        ur.redo();
        content = seq.getContent();
        assertEquals(2, content.size());
        name = (LocalElement) content.get(1);
        street = (LocalElement) content.get(0);
        assertEquals("name", name.getName());
        assertEquals("street", street.getName());
        Node nameNode3 = name.getPeer();
        Node streetNode3 = street.getPeer();
        assertTrue(name.referencesSameNode(nameNode1));
        assertTrue(street.referencesSameNode(streetNode1));

        listener.assertEvent(ComponentEvent.EventType.CHILD_REMOVED, seq);
        listener.assertEvent(ComponentEvent.EventType.CHILD_ADDED, seq);
    }

    public void testRedefine() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/PurchaseOrder_redefine_0.xsd");
        Redefine redefine = model.getSchema().getRedefines().iterator().next();
        assertTrue(redefine.getChildren().isEmpty());
        
        Util.setDocumentContentTo(model, "resources/PurchaseOrder_redefine.xsd");
        model.sync();
        
        Redefine redefine2 = model.getSchema().getRedefines().iterator().next();
        assertTrue(redefine == redefine2);
        assertEquals("foo2", redefine.getAttributeGroups().iterator().next().getName());
        assertEquals("foo1", redefine.getGroupDefinitions().iterator().next().getName());
        assertEquals("PurchaseOrderType", redefine.getComplexTypes().iterator().next().getName());
        assertEquals("allNNI", redefine.getSimpleTypes().iterator().next().getName());

        Util.setDocumentContentTo(model, "resources/PurchaseOrder_redefine_0.xsd");
        model.sync();

        assertTrue(redefine == redefine2);
        assertTrue(redefine.getChildren().isEmpty());
    }
    
    public void testSyncUndo() throws Exception {
        SchemaModel model = Util.loadSchemaModel(TEST_XSD);
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);
        
        GlobalComplexType gct = (GlobalComplexType)Util.findComponent(
                model.getSchema(), "/schema/complexType[@name='PurchaseOrderType']");
        assertEquals(3, gct.getDefinition().getChildren().size());
        
        Util.setDocumentContentTo(model, "resources/PurchaseOrder_SyncUndo.xsd");
        model.sync();
        um.undo();
        assertEquals(3, gct.getDefinition().getChildren().size());

        um.redo();
        assertEquals(2, gct.getDefinition().getChildren().size());
    }
	
    public void testSyncUndoRename() throws Exception {
        SchemaModel model = Util.loadSchemaModel(TEST_XSD);
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);
        assertEquals(2, model.getSchema().getElements().size());
        
        Util.setDocumentContentTo(model, "resources/PurchaseOrder_SyncUndoRename.xsd");
        model.sync();
        assertEquals(2, model.getSchema().getElements().size());
        assertEquals("purchaseOrder2", model.getSchema().getElements().iterator().next().getName());

        um.undo();
        assertEquals(2, model.getSchema().getElements().size());
        assertEquals("purchaseOrder", model.getSchema().getElements().iterator().next().getName());

        um.redo();
        assertEquals(2, model.getSchema().getElements().size());
        assertEquals("purchaseOrder2", model.getSchema().getElements().iterator().next().getName());
    }
	
    public void testSyncUndoUndo() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/undoredotest.xsd");
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);
        
        GlobalComplexType gct = (GlobalComplexType)Util.findComponent(
                model.getSchema(), "/schema/complexType[@name='USAddress']");
        java.util.List<SchemaComponent> seqChildren = gct.getDefinition().getChildren();
        
        assertEquals(3, seqChildren.size());
        
        Util.setDocumentContentTo(model, "resources/undoredotest1.xsd");
        model.sync();
		assertEquals(3, seqChildren.size());
        um.undo();
        assertEquals(3, seqChildren.size());
		um.redo();
		assertEquals(3, seqChildren.size());
		
		Util.setDocumentContentTo(model, "resources/undoredotest2.xsd");
        model.sync();
		assertEquals(3, seqChildren.size());
        um.undo();
		assertEquals(3, seqChildren.size());
		um.undo();
		assertEquals(3, seqChildren.size());
    }	

    public void testUndoSequenceCopy() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/PO_copypasteundoSequence.xsd");
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);
        
        GlobalComplexType gct = (GlobalComplexType)Util.findComponent(
                model.getSchema(), "/schema/complexType[@name='Items']");
        Sequence seq1 = (Sequence) Util.findComponent(
                model.getSchema(), "/schema/complexType[@name='USAddress']/sequence");
        assertEquals(1, gct.getDefinition().getChildren().size());
        assertEquals(5, seq1.getChildren().size());
        
        Sequence seq2 = (Sequence) seq1.copy(gct);
        model.startTransaction();
        gct.setDefinition(seq2);
        model.endTransaction();
        assertEquals(5, gct.getDefinition().getChildren().size());
        
        um.undo();
        assertEquals(1, gct.getDefinition().getChildren().size());
    }
    
    public void testSyncAnnotationRemoveId() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/loanApplication_id.xsd");
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);

        Util.setDocumentContentTo(model, "resources/loanApplication_id_removed.xsd");
        model.sync();
        Annotation ann = model.getSchema().getElements().iterator().next().getAnnotation();
        assertNull(ann.getId());
    }
    
    public void testSyncReformat() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/reformat_before.xsd");
        UndoManager um = new UndoManager();
        model.addUndoableEditListener(um);

        Util.setDocumentContentTo(model, "resources/reformat_after.xsd");
        model.sync();
        assertNotNull(model.getSchema().getElements().iterator().next().getAnnotation());
    }
    
    public void testSyncInvalidRoot() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/Empty.xsd");
        
        Util.setDocumentContentTo(model, "resources/InvalidRoot.xsd");
        try {
            model.sync();
            assertFalse("Did not get IOException", true);
        } catch(IOException ioe) {
            assertEquals(model.getState(), Model.State.NOT_WELL_FORMED);
        }
        assertNotNull(model.getSchema());
        assertTrue(model.getSchema().getElements().isEmpty());

        Util.setDocumentContentTo(model, "resources/InvalidRoot_corrected.xsd");
        model.sync();
        assertEquals(1, model.getSchema().getElements().size());
    }
    
    public void testSyncTwoSequences() throws Exception {
        SchemaModel model = Util.loadSchemaModel("resources/Empty.xsd");
        
        Util.setDocumentContentTo(model, "resources/TwoSequences.xsd");
        model.sync();
        String xpath = "/xsd:schema/xsd:complexType";
        GlobalComplexType gct = (GlobalComplexType) Util.findComponent(model.getSchema(), xpath);
        assertEquals("Get 2 seqences from generic getChildren", 2, gct.getChildren(Sequence.class).size());
        assertNotNull("Get 1 sequence from getDefinition", gct.getDefinition());
        
        Util.setDocumentContentTo(model, "resources/OneSequence.xsd");
        model.sync();
        assertEquals("Get 1 seqences from generic getChildren", 1, gct.getChildren(Sequence.class).size());
        Sequence seq = (Sequence)gct.getDefinition();
        assertEquals("billTo", ((LocalElement)seq.getContent().iterator().next()).getName());
    }
    
    
    private SchemaModel model;
    
}
