/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.support;

import org.netbeans.modules.jshell.model.ConsoleSection;
import org.netbeans.modules.jshell.model.ConsoleModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;

/**
 *
 * @author sdedic
 */
@EmbeddingProvider.Registration(
        mimeType = "text/x-repl", targetMimeType = "text/x-java")
public class ConsoleEmbeddingProvider extends EmbeddingProvider {
    
    @Override
    public void cancel() {
        
    }
    
    @Override
    public List<Embedding> getEmbeddings(Snapshot snapshot) {
        Document d = snapshot.getSource().getDocument(false);
        if (d == null) {
            return Collections.emptyList();
        }
        ShellSession session = ShellSession.get(d);
        if (session == null) {
            return Collections.emptyList();
        }
        ConsoleModel model = session.getModel();
        if (model == null) {
            return Collections.emptyList();
        }
        EmbeddingProcessor p = new EmbeddingProcessor(session, model, snapshot);
        return p.process();
    }
    
    private Embedding createEmbedding(ShellSession session, ConsoleSection s, Snapshot snapshot) {
        if (!s.hasMoreParts()) {
            int start = Math.min(s.getPartBegin(), snapshot.getText().length());
            int end = Math.min(s.getPartBegin() + s.getPartLen(), snapshot.getText().length());
            
            return snapshot.create(
                    start, end - start , "text/x-java");
        } else {
            return Embedding.create(
                Arrays.stream(s.getPartRanges()).map(
                        r -> {
                            int start = Math.min(snapshot.getText().length(), r.start);
                            int end = Math.min(snapshot.getText().length(), r.end);
                            return snapshot.create(start, end - start, "text/x-java");
                        }
                ).collect(Collectors.toList())
            );
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
