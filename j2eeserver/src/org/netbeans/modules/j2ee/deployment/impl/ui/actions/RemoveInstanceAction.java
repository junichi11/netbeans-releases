/*
 * RemoveIntanceAction.java
 *
 * Created on September 23, 2003, 10:42 AM
 */

package org.netbeans.modules.j2ee.deployment.impl.ui.actions;

import org.openide.util.actions.*;
import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.j2ee.deployment.impl.*;
import org.netbeans.modules.j2ee.deployment.impl.ui.*;

/**
 *
 * @author  nn136682
 */
public class RemoveInstanceAction extends CookieAction {
    
    /** Creates a new instance of RemoveIntanceAction */
    public RemoveInstanceAction() {
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { ServerInstance.class };
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        //PENDING:
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(RemoveInstanceAction.class, "LBL_Remove");
    }
    
    protected void performAction(org.openide.nodes.Node[] nodes) {
        for (int i=0; i<nodes.length; i++) {
            ServerInstance instance = (ServerInstance) nodes[i].getCookie(ServerInstance.class);
            if (instance == null)
                continue;
            
            instance.remove();
            //Node parentNode = nodes[i].getParentNode();
            //parentNode.getChildren().remove(new Node[] { nodes[i] });
        }
    }
    
    protected int mode() {
        return MODE_ALL;
    }
    
    protected boolean asynchronous() { return false; }
    
}
