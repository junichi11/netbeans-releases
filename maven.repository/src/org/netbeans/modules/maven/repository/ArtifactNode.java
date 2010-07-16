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
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.repository;

import java.awt.Image;

import org.netbeans.modules.maven.indexer.api.NBArtifactInfo;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;

import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 *
 * @author mkleint
 * @author Anuradha G
 */
public class ArtifactNode extends AbstractNode {
    private RepositoryInfo info;
    /** Creates a new instance of ArtifactNode */
    public ArtifactNode(RepositoryInfo info,String id, String art) {
        super(new ArtifactChildren(info,id, art));
        this.info=info;
        setName(art);
        setDisplayName(art);
    }

    public ArtifactNode(final RepositoryInfo info,final NBArtifactInfo artifactInfo) {
        super(new Children.Keys<NBVersionInfo>() {

            @Override
            protected Node[] createNodes(NBVersionInfo arg0) {


                return new Node[]{new VersionNode(info,arg0,arg0.isJavadocExists(),
                    arg0.isSourcesExists(), arg0.getGroupId() != null)
                };
            }

            @Override
            protected void addNotify() {
                super.addNotify();
                setKeys(artifactInfo.getVersionInfos());
            }
            });
        setName(artifactInfo.getName());
        setDisplayName(artifactInfo.getName());
    }

    @Override
    public Image getIcon(int arg0) {
        Image badge = ImageUtilities.loadImage("org/netbeans/modules/maven/repository/ArtifactBadge.png", true); //NOI18N
        return badge;
    }

    @Override
    public Image getOpenedIcon(int arg0) {
        return getIcon(arg0);
    }
}
