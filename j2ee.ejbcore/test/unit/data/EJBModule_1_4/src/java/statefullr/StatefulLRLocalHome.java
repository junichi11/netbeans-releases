/*
 * StatefulLRLocalHome.java
 *
 * Created on Feb 15, 2007, 4:02:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package statefullr;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 *
 * @author klingo
 */
public interface StatefulLRLocalHome extends EJBLocalHome {
    
    statefullr.StatefulLRLocal create()  throws CreateException;

}
