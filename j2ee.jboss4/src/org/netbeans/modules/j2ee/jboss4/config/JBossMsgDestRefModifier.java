/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.j2ee.jboss4.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.j2ee.jboss4.config.EjbDeploymentConfiguration.BEAN_TYPE;
import org.netbeans.modules.j2ee.jboss4.config.gen.EnterpriseBeans;
import org.netbeans.modules.j2ee.jboss4.config.gen.Entity;
import org.netbeans.modules.j2ee.jboss4.config.gen.Jboss;
import org.netbeans.modules.j2ee.jboss4.config.gen.MessageDestinationRef;
import org.netbeans.modules.j2ee.jboss4.config.gen.MessageDriven;
import org.netbeans.modules.j2ee.jboss4.config.gen.Session;

/**
 * This class implements the core of the jboss.xml file modifications.
 *
 * @author lkotouc
 */
final class JBossMsgDestRefModifier {

    /**
     * Add a reference to the given message destination to the enterprise beans of the given type if it does not exist yet.
     *
     * @param modifiedJboss Jboss graph instance being modified
     * @param msgDestRefName message destination reference name
     * @param beanNames the beans (ejb-name value) which might need to add message destination reference specified by msgDestRefName
     * @param beanType type of bean to add message destination reference to
     * @param destPrefix prefix of the message destination
     * @param destName message destination name
     */
    static void modify(Jboss modifiedJboss, String msgDestRefName, Set beanNames, 
            BEAN_TYPE beanType, String destPrefix, String destName) {

        assert(beanNames.size() > 0);

        if (modifiedJboss.getEnterpriseBeans() == null)
            modifiedJboss.setEnterpriseBeans(new EnterpriseBeans());

        if (beanType == BEAN_TYPE.SESSION) {
            addSessionMsgDestReference(modifiedJboss, msgDestRefName, beanNames, destPrefix, destName);
        } else
        if (beanType == BEAN_TYPE.ENTITY) {
            addEntityMsgDestReference(modifiedJboss, msgDestRefName, beanNames, destPrefix, destName);
        }
    }
    
    /**
     * Add a new message destination reference to the session beans without it.
     * 
     * @param modifiedJboss Jboss instance being modified
     * @param resRefName message destination reference name
     * @param sessionNames the sessions (ejb-name value) which might need to add message destination reference specified by msgDestRefName
     * @param destPrefix prefix of the message destination
     * @param destName message destination name
     */
    private static void addSessionMsgDestReference(Jboss modifiedJboss, String msgDestRefName, 
            Set sessionNames, String destPrefix, String destName) {

        List/*<Session>*/ sesssionsWithoutReference = new LinkedList();

        EnterpriseBeans eb = modifiedJboss.getEnterpriseBeans();
        
        Session[] sessions = eb.getSession();
        for (int i = 0; i < sessions.length; i++) {
            String ejbName = sessions[i].getEjbName();
            if (sessionNames.contains(ejbName)) { // session found -> check whether it has the message-destination-ref
                sessionNames.remove(ejbName);     // we don't care about it anymore
                MessageDestinationRef[] msgDestRefs = sessions[i].getMessageDestinationRef();
                int j = 0;
                for ( ; j < msgDestRefs.length; j++) {
                    String mdrn = msgDestRefs[j].getMessageDestinationRefName();
                    if (msgDestRefName.equals(mdrn))
                        break; // message-destination-ref found, continuing with the next session
                }
                if (j == msgDestRefs.length) // resource-ref not found
                    sesssionsWithoutReference.add(sessions[i]);
            }
        }

        //no session tag yet (sessions.length == 0) or 
        //there are sessions in sessionNames which were not found among the existing ones (those were not removed)
        for (Iterator it = sessionNames.iterator(); it.hasNext(); ) {
            String sessionName = (String)it.next();
            Session session = new Session();
            session.setEjbName(sessionName);
            session.setJndiName(sessionName);

            //add the new session to enterprise-beans
            eb.addSession(session);

            //add the new session to the list of sessions without the resource reference
            sesssionsWithoutReference.add(session);
        }

        //the message destination reference will be added to each session without it
        for (Iterator it = sesssionsWithoutReference.iterator(); it.hasNext(); ) {
            MessageDestinationRef mdr = new MessageDestinationRef();
            mdr.setMessageDestinationRefName(msgDestRefName);
            String jndiName = getJndiName(destName, destPrefix);
            mdr.setJndiName(jndiName);
            Session session = (Session)it.next();
            session.addMessageDestinationRef(mdr);
        }

    }
    
    /**
     * Add a new message destination reference to the entity beans without it.
     * 
     * @param modifiedJboss Jboss instance being modified
     * @param resRefName message destination reference name
     * @param sessionNames the entities (ejb-name value) which might need to add message destination reference specified by msgDestRefName
     * @param destPrefix prefix of the message destination
     * @param destName message destination name
     */
    private static void addEntityMsgDestReference(Jboss modifiedJboss, String msgDestRefName, 
            Set entityNames, String destPrefix, String destName) {

        List/*<Entity>*/ entitiesWithoutReference = new LinkedList();

        EnterpriseBeans eb = modifiedJboss.getEnterpriseBeans();

        Entity[] entities = eb.getEntity();
        for (int i = 0; i < entities.length; i++) {
            String ejbName = entities[i].getEjbName();
            if (entityNames.contains(ejbName)) { // entity found -> check whether it has the message-destination-ref
                entityNames.remove(ejbName);     // we don't care about it anymore
                MessageDestinationRef[] msgDestRefs = entities[i].getMessageDestinationRef();
                int j = 0;
                for ( ; j < msgDestRefs.length; j++) {
                    String mdrn = msgDestRefs[j].getMessageDestinationRefName();
                    if (msgDestRefName.equals(mdrn))
                        break; // message-destination-ref found, continuing with the next session
                }
                if (j == msgDestRefs.length) // resource-ref not found
                    entitiesWithoutReference.add(entities[i]);
            }
        }

        //no entity tag yet (entities.length == 0) or 
        //there are entities in entityNames which were not found among the existing ones (those were not removed)
        for (Iterator it = entityNames.iterator(); it.hasNext(); ) {
            String entityName = (String)it.next();
            Entity entity = new Entity();
            entity.setEjbName(entityName);
            entity.setJndiName(entityName);

            //add the new entity to enterprise-beans
            eb.addEntity(entity);

            //add the new entity to the list of entities without the resource reference
            entitiesWithoutReference.add(entity);
        }

        //the resource reference will be added to each entity without it
        for (Iterator it = entitiesWithoutReference.iterator(); it.hasNext(); ) {
            MessageDestinationRef mdr = new MessageDestinationRef();
            mdr.setMessageDestinationRefName(msgDestRefName);
            String jndiName = getJndiName(destName, destPrefix);
            mdr.setJndiName(jndiName);
            Entity entity = (Entity)it.next();
            entity.addMessageDestinationRef(mdr);
        }

    }
    
    /**
     * Add a reference to the given message destination to the message-driven beans if it does not exist yet.
     *
     * @param modifiedJboss Jboss graph instance being modified
     * @param msgDestRefName message destination reference name
     * @param beans the beans (ejb-name value) which might need to add message destination reference specified by msgDestRefName
     * @param destPrefix prefix of the message destination
     * 
     * @deprecated
     */
    static void modifyMsgDrv(Jboss modifiedJboss, String msgDestRefName, Map beans, String destPrefix) {

        assert(beans.size() > 0);

        if (modifiedJboss.getEnterpriseBeans() == null)
            modifiedJboss.setEnterpriseBeans(new EnterpriseBeans());

        addMsgDrvMsgDestReference(modifiedJboss, msgDestRefName, beans, destPrefix);
    }
    
    /**
     * Add a new message destination reference to the message-driven beans without it.
     * 
     * @param modifiedJboss Jboss instance being modified
     * @param msgDestRefName message destination reference name
     * @param beans the beans (ejb-name value) which might need to add message destination reference specified by msgDestRefName
     * @param destPrefix prefix of the message destination
     * 
     * @deprecated
     */
    private static void addMsgDrvMsgDestReference(Jboss modifiedJboss, String msgDestRefName, Map beans, String destPrefix) {

        List/*<Entity>*/ msgdrvsWithoutReference = new LinkedList();

        EnterpriseBeans eb = modifiedJboss.getEnterpriseBeans();

        MessageDriven[] msgDrivens = eb.getMessageDriven();
        for (int i = 0; i < msgDrivens.length; i++) {
            String ejbName = msgDrivens[i].getEjbName();
            if (beans.containsKey(ejbName)) { // msgdrv found -> check whether it has the message-destination-ref
                beans.remove(ejbName);        // we don't care about it anymore
                MessageDestinationRef[] msgDestRefs = msgDrivens[i].getMessageDestinationRef();
                int j = 0;
                for ( ; j < msgDestRefs.length; j++) {
                    String mdrn = msgDestRefs[j].getMessageDestinationRefName();
                    if (msgDestRefName.equals(mdrn))
                        break; // message-destination-ref found, continuing with the next mdb
                }
                if (j == msgDestRefs.length) // message-destination-ref not found
                    msgdrvsWithoutReference.add(msgDrivens[i]);
            }
        }

        //no message-driven tag yet (msgDrivens.length == 0) or 
        //there are MDBs in beans map which were not found among the existing ones (those were not removed)
        for (Iterator it = beans.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            MessageDriven mdb = new MessageDriven();
            mdb.setEjbName((String) entry.getKey());
            mdb.setDestinationJndiName((String) entry.getValue());

            //add the new mdb to enterprise-beans
            eb.addMessageDriven(mdb);

            //add the new mdb to the list of mdbs without the resource reference
            msgdrvsWithoutReference.add(mdb);
        }

        //the resource reference will be added to each mdb without it
        for (Iterator it = msgdrvsWithoutReference.iterator(); it.hasNext(); ) {
            MessageDestinationRef mdr = new MessageDestinationRef();
            mdr.setMessageDestinationRefName(msgDestRefName);
            String jndiName = getJndiName(msgDestRefName, destPrefix);
            mdr.setJndiName(jndiName);
            MessageDriven mdb = (MessageDriven)it.next();
            mdb.addMessageDestinationRef(mdr);
        }

    }
    
    /**
     * Add a reference to the given message destination to the message-driven beans if it does not exist yet.
     *
     * @param modifiedJboss Jboss graph instance being modified
     * @param msgDestRefName message destination reference name
     * @param mdbName the MDB (ejb-name value) which might need to add message 
     *        destination reference specified by msgDestRefName
     * @param destPrefix prefix of the message destination
     * @param destName message destination name
     */
    static void modifyMsgDrv(Jboss modifiedJboss, String msgDestRefName, 
            String mdbName, String destPrefix, String destName) {

        if (modifiedJboss.getEnterpriseBeans() == null)
            modifiedJboss.setEnterpriseBeans(new EnterpriseBeans());

        addMsgDrvMsgDestReference(modifiedJboss, msgDestRefName, mdbName, destPrefix, destName);
    }
    
    private static void addMsgDrvMsgDestReference(Jboss modifiedJboss, String msgDestRefName, 
            String mdbName, String destPrefix, String destName) {

        EnterpriseBeans eb = modifiedJboss.getEnterpriseBeans();

        for (MessageDriven mdb : eb.getMessageDriven()) {
            String ejbName = mdb.getEjbName();
            if (mdbName.equals(ejbName)) { // msgdrv found -> check whether it has the message-destination-ref
                MessageDestinationRef[] msgDestRefs = mdb.getMessageDestinationRef();
                int j = 0;
                for ( ; j < msgDestRefs.length; j++) {
                    String mdrn = msgDestRefs[j].getMessageDestinationRefName();
                    if (msgDestRefName.equals(mdrn))
                        return; // message-destination-ref found
                }
                if (j == msgDestRefs.length) { // message-destination-ref not found
                    MessageDestinationRef mdr = new MessageDestinationRef();
                    mdr.setMessageDestinationRefName(msgDestRefName);
                    String jndiName = getJndiName(destName, destPrefix);
                    mdr.setJndiName(jndiName);
                    mdb.addMessageDestinationRef(mdr);
                }
            }
        }
    }
    
    private static String getJndiName(String destName, String destPrefix) {
        return destPrefix + destName;
    }
    
}
