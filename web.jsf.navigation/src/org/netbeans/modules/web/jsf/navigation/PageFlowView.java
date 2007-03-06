/*
 * JSFTopComponent.java
 *
 * Created on February 8, 2007, 12:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.web.jsf.navigation;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.modules.web.jsf.api.editor.JSFConfigEditorContext;
import org.netbeans.modules.web.jsf.api.facesmodel.JSFConfigModel;
import org.netbeans.modules.web.jsf.api.facesmodel.NavigationCase;
import org.netbeans.modules.web.jsf.api.facesmodel.NavigationRule;
import org.netbeans.modules.web.jsf.navigation.graph.PageFlowScene;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 *
 * @author Joelle Lam
 */
public class PageFlowView  extends TopComponent{
    private JSFConfigEditorContext context;
    private PageFlowScene scene;
    private JSFConfigModel configModel;
    
    PageFlowView(JSFConfigEditorContext context){
        this(context, new InstanceContent());
    }
    
    PageFlowView(JSFConfigEditorContext context, InstanceContent ic ){
        super( new AbstractLookup( ic ) );
        ic.add( initializePalette() );
        this.context = context;
        init();
        PageFlowController pfc = new PageFlowController( context,  this );
        layoutGraph();

    }
    
    /*
     * Initializes the Panel and the graph
     **/
    private void init(){
        setLayout(new BorderLayout());
        
        scene = new PageFlowScene();
        add(scene.createView());        
        
        try{
            Node node = DataObject.find(context.getFacesConfigFile()).getNodeDelegate();
            setActivatedNodes(new Node[] { node });
        } catch (Exception e){}
        
    }
    
    protected void layoutGraph(){                
        if(  scene instanceof PageFlowScene ) {
            ((PageFlowScene)scene).layoutScene();
        }
    }
    
    //    private static final Image IMAGE_LIST = Utilities.loadImage("org/netbeans/modules/web/jsf/navigation/graph/resources/list_32.png"); // NOI18N
    private static final Image IMAGE_LIST = null; // NOI18N
    
    private void clearGraph() {
        scene.removeChildren();
    }
    
    protected VMDNodeWidget createNode(  String page, String type, List<Image> glyphs) {
        VMDNodeWidget widget = (VMDNodeWidget) scene.addNode(page);
        widget.setNodeProperties(IMAGE_LIST, page, type, glyphs);
        scene.addPin(page, page +"pin");
        
        return widget;
    }
    
    protected VMDPinWidget createPin( String page, String navComp) {
        //        Pin pin = new Pin(page, navComp);
        VMDPinWidget widget = (VMDPinWidget) scene.addPin(page, navComp);
        //        VMDPinWidget widget = (VMDPinWidget) graphScene.addPin(page, pin);
        //        if( navComp != null ){
        //            widget.setProperties(navComp, Arrays.asList(navComp.getBufferedIcon()));
        //        }
        return widget;
    }
    
    protected void createEdge( NavigationRule rule, NavigationCase navCase) {
        
        String toPage = navCase.getToViewId();
        String caseName = navCase.getFromOutcome();
        String action = navCase.getFromAction();
        String fromPage = rule.getFromViewId();
        
        ConnectionWidget widget = (ConnectionWidget)scene.addEdge(navCase);
        
        LabelWidget label = new LabelWidget(scene, caseName);
        label.setOpaque(true);
        widget.addChild(label);
        widget.setConstraint(label, LayoutFactory.ConnectionWidgetLayoutAlignment.TOP_RIGHT, 10);
        
        //        graphScene.setEdgeSource(navCase, label);
        scene.setEdgeSource(navCase, fromPage+"pin");
        scene.setEdgeTarget(navCase, toPage+"pin");
        
//        Collection<String> pins = graphScene.getPins();
//        String targetPin = null;
//        String sourcePin = null;
//        for (String pin : pins ){
//            if (pin.equals(toPage)) {
//                sourcePin = pin;
//                if( targetPin != null ) {
//                    break;
//                } else {
//                    continue;
//                }
//            } else if (pin.equals(fromPage)) {
//                targetPin = fromPage;
//                if( sourcePin != null ) {
//                    break;
//                } else {
//                    continue;
//                }
//            }
//        }
//
//        graphScene.setEdgeTarget(navCase, targetPin);
//        graphScene.setEdgeSource(navCase, sourcePin);
        
        
    }
    
    public PaletteController initializePalette() {
        try {
            return PaletteFactory.createPalette( "MyPalette", new PaletteActions() {
                public Action[] getCustomCategoryActions(Lookup lookup) {
                    return new Action[0];
                }
                public Action[] getCustomItemActions(Lookup lookup) {
                    return new Action[0];
                }
                public Action[] getCustomPaletteActions() {
                    return new Action[0];
                }
                public Action[] getImportActions() {
                    return new Action[0];
                }
                public Action getPreferredAction(Lookup lookup) {
                    return null; //TODO
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            
        }
        return null;
    }
    
    
    
    
    
}
