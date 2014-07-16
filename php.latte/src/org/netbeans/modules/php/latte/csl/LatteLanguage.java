/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.latte.csl;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.HintsProvider;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.php.latte.completion.LatteCompletionHandler;
import org.netbeans.modules.php.latte.hints.LatteHintsProvider;
import org.netbeans.modules.php.latte.lexer.LatteTopTokenId;
import org.netbeans.modules.php.latte.navigation.LatteStructureScanner;
import org.netbeans.modules.php.latte.parser.LatteParser;
import org.netbeans.modules.php.latte.parser.LatteParserResult;
import org.netbeans.modules.php.latte.semantic.LatteSemanticAnalyzer;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
@LanguageRegistration(mimeType = LatteLanguage.LATTE_MIME_TYPE, useCustomEditorKit = true)
public class LatteLanguage extends DefaultLanguageConfig {
    public static final String LATTE_MIME_TYPE = "text/x-latte"; //NOI18N

    @Override
    public Language<LatteTopTokenId> getLexerLanguage() {
        return LatteTopTokenId.language();
    }

    @Override
    public String getDisplayName() {
        return "Latte"; //NOI18N
    }

    @Override
    public boolean isIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) || (c == '$') || (c == '_');
    }

    @Override
    public Parser getParser() {
        return new LatteParser();
    }

    @Override
    public SemanticAnalyzer<LatteParserResult> getSemanticAnalyzer() {
        return new LatteSemanticAnalyzer();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new LatteStructureScanner();
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new LatteCompletionHandler();
    }

    @Override
    public boolean isUsingCustomEditorKit() {
        return true;
    }

    @Override
    public boolean hasHintsProvider() {
        return true;
    }

    @Override
    public HintsProvider getHintsProvider() {
        return new LatteHintsProvider();
    }

}
