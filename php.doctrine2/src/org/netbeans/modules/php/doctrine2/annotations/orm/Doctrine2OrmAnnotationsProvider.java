/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.doctrine2.annotations.orm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.php.spi.annotations.PhpAnnotationTag;
import org.netbeans.modules.php.spi.annotations.PhpAnnotationsProvider;
import org.openide.util.NbBundle;

// http://docs.doctrine-project.org/projects/doctrine-orm/en/latest/reference/annotations-reference.html
public final class Doctrine2OrmAnnotationsProvider extends PhpAnnotationsProvider {

    @NbBundle.Messages("Doctrine2OrmAnnotationsProvider.name=Doctrine2 ORM")
    public Doctrine2OrmAnnotationsProvider() {
        super("Doctrine2 ORM Annotations", // NOI18N
                Bundle.Doctrine2OrmAnnotationsProvider_name(),
                null);
    }

    @Override
    public List<PhpAnnotationTag> getFunctionAnnotations() {
        return Collections.emptyList();
    }

    @Override
    public List<PhpAnnotationTag> getTypeAnnotations() {
        return Arrays.<PhpAnnotationTag>asList(
                new ChangeTrackingPolicyTag(),
                new DiscriminatorColumnTag(),
                new DiscriminatorMapTag(),
                new EntityTag(),
                new HasLifecycleCallbacksTag(),
                new IndexTag(),
                new InheritanceTypeTag(),
                new ManyToManyTag(),
                new MappedSuperclassTag(),
                new TableTag(),
                new UniqueConstraintTag());
    }

    @Override
    public List<PhpAnnotationTag> getFieldAnnotations() {
        return Arrays.<PhpAnnotationTag>asList(
                new ColumnTag(),
                new GeneratedValueTag(),
                new IdTag(),
                new JoinColumnTag(),
                new JoinColumnsTag(),
                new JoinTableTag(),
                new ManyToOneTag(),
                new ManyToManyTag(),
                new OneToOneTag(),
                new OneToManyTag(),
                new OrderByTag(),
                new SequenceGeneratorTag(),
                new VersionTag());
    }

    @Override
    public List<PhpAnnotationTag> getMethodAnnotations() {
        return Arrays.<PhpAnnotationTag>asList(
                new PostLoadTag(),
                new PostPersistTag(),
                new PostRemoveTag(),
                new PostUpdateTag(),
                new PrePersistTag(),
                new PreRemoveTag(),
                new PreUpdateTag());
    }

}
