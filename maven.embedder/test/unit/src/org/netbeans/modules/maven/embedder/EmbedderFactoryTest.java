/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.embedder;

import java.io.File;
import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.netbeans.junit.NbTestCase;
import org.openide.util.test.TestFileUtils;

public class EmbedderFactoryTest extends NbTestCase {
    
    public EmbedderFactoryTest(String n) {
        super(n);
    }

    protected @Override void setUp() throws Exception {
        clearWorkDir();
    }

    // XXX find some way to verify that interesting things do not cause Wagon HTTP requests

    public void testCreateModelLineage() throws Exception {
        File pom = TestFileUtils.writeFile(new File(getWorkDir(), "pom.xml"), "<project xmlns='http://maven.apache.org/POM/4.0.0'>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<groupId>grp</groupId>" +
            "<artifactId>art</artifactId>" +
            "<packaging>jar</packaging>" +
            "<version>1.0-SNAPSHOT</version>" +
            "</project>");
        List<Model> lineage = EmbedderFactory.createModelLineage(pom, EmbedderFactory.createProjectLikeEmbedder());
        assertEquals(/* second is inherited master POM */2, lineage.size());
        assertEquals("grp:art:jar:1.0-SNAPSHOT", lineage.get(0).getId());
        // #195295: JDK activation
        pom = TestFileUtils.writeFile(new File(getWorkDir(), "pom.xml"), "<project xmlns='http://maven.apache.org/POM/4.0.0'>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<groupId>grp</groupId>" +
            "<artifactId>art2</artifactId>" +
            "<packaging>jar</packaging>" +
            "<version>1.0-SNAPSHOT</version>" +
            "<profiles>" +
            "<profile>" +
            "<id>jdk15</id>" +
            "<activation>" +
            "<jdk>1.5</jdk>" +
            "</activation>" +
            "</profile>" +
            "</profiles>" +
            "</project>");
        lineage = EmbedderFactory.createModelLineage(pom, EmbedderFactory.createProjectLikeEmbedder());
        assertEquals(2, lineage.size());
        assertEquals("grp:art2:jar:1.0-SNAPSHOT", lineage.get(0).getId());
        assertEquals(1, lineage.get(0).getProfiles().size());
        // #197288: groupId and version can be inherited from parents
        TestFileUtils.writeFile(new File(getWorkDir(), "parent.xml"), "<project xmlns='http://maven.apache.org/POM/4.0.0'>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<groupId>grp</groupId>" +
            "<artifactId>parent</artifactId>" +
            "<version>1.0</version>" +
            "<packaging>pom</packaging>" +
            "</project>");
        pom = TestFileUtils.writeFile(new File(getWorkDir(), "pom.xml"), "<project xmlns='http://maven.apache.org/POM/4.0.0'>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<parent>" +
            "<relativePath>parent.xml</relativePath>" +
            "<groupId>grp</groupId>" +
            "<artifactId>parent</artifactId>" +
            "<version>1.0</version>" +
            "</parent>" +
            "<artifactId>art3</artifactId>" +
            "</project>");
        lineage = EmbedderFactory.createModelLineage(pom, EmbedderFactory.createProjectLikeEmbedder());
        assertEquals(3, lineage.size());
        assertEquals("[inherited]:art3:jar:[inherited]", lineage.get(0).getId());
        assertEquals("grp", lineage.get(0).getParent().getGroupId());
        assertEquals("1.0", lineage.get(0).getParent().getVersion());
        assertEquals("grp:parent:pom:1.0", lineage.get(1).getId());
    }

    public void testInvalidRepositoryException() throws Exception { // #197831
        File pom = TestFileUtils.writeFile(new File(getWorkDir(), "pom.xml"), "<project xmlns='http://maven.apache.org/POM/4.0.0'>" +
            "<modelVersion>4.0.0</modelVersion>" +
            "<groupId>grp</groupId>" +
            "<artifactId>art</artifactId>" +
            "<packaging>jar</packaging>" +
            "<version>1.0-SNAPSHOT</version>" +
            "<repositories><repository><url>http://nowhere.net/</url></repository></repositories>" +
            "</project>");
        try {
            EmbedderFactory.createModelLineage(pom, EmbedderFactory.createProjectLikeEmbedder());
            fail();
        } catch (ModelBuildingException x) {
            // right
        }
    }

}
