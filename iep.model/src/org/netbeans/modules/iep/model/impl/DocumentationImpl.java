package org.netbeans.modules.iep.model.impl;

import org.netbeans.modules.iep.model.Documentation;
import org.netbeans.modules.iep.model.IEPComponent;
import org.netbeans.modules.iep.model.IEPModel;
import org.netbeans.modules.iep.model.IEPQNames;
import org.netbeans.modules.iep.model.IEPVisitor;
import org.w3c.dom.Element;

public class DocumentationImpl extends IEPComponentBase implements Documentation {

	public DocumentationImpl(IEPModel model,  Element e) {
    	super(model, e);
    }

    public DocumentationImpl(IEPModel model) {
        this(model, createNewElement(IEPQNames.COMPONENT.getQName(), model));
    }

    public void accept(IEPVisitor visitor) {
    	
    }

    public IEPComponent createChild(Element childEl) {
        IEPComponent child = null;
        return child;
    }

    public void setContent(String content) {
    	
    	
    }
    
    public String getContent() {
    	// TODO Auto-generated method stub
    	return null;
    }
}
