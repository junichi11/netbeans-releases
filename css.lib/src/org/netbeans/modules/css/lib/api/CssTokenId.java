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
 * or only the GPL Version , indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.css.lib.api;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.css.lib.nblexer.CssLanguageHierarchy;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.css.lib.Css3Lexer;
import static org.netbeans.modules.css.lib.api.CssTokenIdCategory.*;
import org.netbeans.modules.css.lib.api.properties.Token;

/**
 * Token ids of CSS language
 *
 * @author Marek Fukala
 */
public enum CssTokenId implements TokenId {
        
     /* Defined categories:
     * -------------------
     * others
     * brace
     * separator
     * hash
     * operator
     * string
     * url
     * keyword
     * number
//     * rgb
     * identifier
//     * function
     * whitespace
     * comment
     */
   
    //see the Css3.g lexer definition to find out which of the tokens are only
    //token fragments (will not show up in the output tokens list)

    EOF(Css3Lexer.EOF, OTHERS),
    
    ERROR(org.antlr.runtime.Token.INVALID_TOKEN_TYPE, ERRORS),
    
    AND(Css3Lexer.AND, OPERATORS),
    NOT(Css3Lexer.NOT, OPERATORS),
    ONLY(Css3Lexer.ONLY, OPERATORS),
    RESOLUTION(Css3Lexer.RESOLUTION, NUMBERS),
    WS(Css3Lexer.WS, WHITESPACES),
    CHARSET_SYM(Css3Lexer.CHARSET_SYM, AT_RULE_SYMBOL),
    STRING(Css3Lexer.STRING, STRINGS),
    SEMI(Css3Lexer.SEMI, SEPARATORS),
    IMPORT_SYM(Css3Lexer.IMPORT_SYM, AT_RULE_SYMBOL),
    URI(Css3Lexer.URI, URIS),
    COMMA(Css3Lexer.COMMA, SEPARATORS),
    MEDIA_SYM(Css3Lexer.MEDIA_SYM, AT_RULE_SYMBOL),
    LBRACE(Css3Lexer.LBRACE, BRACES),
    RBRACE(Css3Lexer.RBRACE, BRACES),
    IDENT(Css3Lexer.IDENT, IDENTIFIERS),
    PAGE_SYM(Css3Lexer.PAGE_SYM, AT_RULE_SYMBOL),
    COLON(Css3Lexer.COLON, SEPARATORS),
    DCOLON(Css3Lexer.DCOLON, SEPARATORS),
    SOLIDUS(Css3Lexer.SOLIDUS, OTHERS),
    PLUS(Css3Lexer.PLUS, OPERATORS),
    GREATER(Css3Lexer.GREATER, OPERATORS),
    TILDE(Css3Lexer.TILDE, OPERATORS),
    MINUS(Css3Lexer.MINUS, OPERATORS),
    STAR(Css3Lexer.STAR, OPERATORS),
    HASH(Css3Lexer.HASH, HASHES),
    DOT(Css3Lexer.DOT, OPERATORS),
    LBRACKET(Css3Lexer.LBRACKET, BRACES),
    OPEQ(Css3Lexer.OPEQ, OTHERS),
    INCLUDES(Css3Lexer.INCLUDES, OPERATORS),
    DASHMATCH(Css3Lexer.DASHMATCH, OPERATORS),
    RBRACKET(Css3Lexer.RBRACKET, BRACES),
    LPAREN(Css3Lexer.LPAREN, BRACES),
    RPAREN(Css3Lexer.RPAREN, BRACES),
    IMPORTANT_SYM(Css3Lexer.IMPORTANT_SYM, KEYWORDS),
    NUMBER(Css3Lexer.NUMBER, NUMBERS),
    PERCENTAGE(Css3Lexer.PERCENTAGE, NUMBERS),
    LENGTH(Css3Lexer.LENGTH, NUMBERS),
    EMS(Css3Lexer.EMS, NUMBERS),
    REM(Css3Lexer.REM, NUMBERS),
    EXS(Css3Lexer.EXS, NUMBERS),
    ANGLE(Css3Lexer.ANGLE, NUMBERS),
    TIME(Css3Lexer.TIME, NUMBERS),
    FREQ(Css3Lexer.FREQ, NUMBERS),
    HEXCHAR(Css3Lexer.HEXCHAR, NUMBERS),
    NONASCII(Css3Lexer.NONASCII, OTHERS),
    UNICODE(Css3Lexer.UNICODE, OTHERS),
    ESCAPE(Css3Lexer.ESCAPE, OTHERS),
    NMSTART(Css3Lexer.NMSTART, OTHERS),
    NMCHAR(Css3Lexer.NMCHAR, OTHERS),
    NAME(Css3Lexer.NAME, OTHERS),
    URL(Css3Lexer.URL, URIS),
    A(Css3Lexer.A, OTHERS),
    B(Css3Lexer.B, OTHERS),
    C(Css3Lexer.C, OTHERS),
    D(Css3Lexer.D, OTHERS),
    E(Css3Lexer.E, OTHERS),
    F(Css3Lexer.F, OTHERS),
    G(Css3Lexer.G, OTHERS),
    H(Css3Lexer.H, OTHERS),
    I(Css3Lexer.I, OTHERS),
    J(Css3Lexer.J, OTHERS),
    K(Css3Lexer.K, OTHERS),
    L(Css3Lexer.L, OTHERS),
    M(Css3Lexer.M, OTHERS),
    N(Css3Lexer.N, OTHERS),
    O(Css3Lexer.O, OTHERS),
    P(Css3Lexer.P, OTHERS),
    Q(Css3Lexer.Q, OTHERS),
    R(Css3Lexer.R, OTHERS),
    S(Css3Lexer.S, OTHERS),
    T(Css3Lexer.T, OTHERS),
    U(Css3Lexer.U, OTHERS),
    V(Css3Lexer.V, OTHERS),
    W(Css3Lexer.W, OTHERS),
    X(Css3Lexer.X, OTHERS),
    Y(Css3Lexer.Y, OTHERS),
    Z(Css3Lexer.Z, OTHERS),
    
    COMMENT(Css3Lexer.COMMENT, COMMENTS),
    LINE_COMMENT(Css3Lexer.LINE_COMMENT, COMMENTS),
    
    //following two should possibly not be part of the grammar at all
    CDO(Css3Lexer.CDO, OTHERS), //<!--
    CDC(Css3Lexer.CDC, OTHERS), // -->
    
    INVALID(Css3Lexer.INVALID, OTHERS),
    DIMENSION(Css3Lexer.DIMENSION, NUMBERS),
    NL(Css3Lexer.NL, OTHERS), //newline
    PIPE(Css3Lexer.PIPE, OPERATORS),  //NOI18N
    
    GEN(Css3Lexer.GEN, OTHERS),
    NAMESPACE_SYM(Css3Lexer.NAMESPACE_SYM, AT_RULE_SYMBOL),
    
    TOPLEFTCORNER_SYM(Css3Lexer.TOPLEFTCORNER_SYM, AT_RULE_SYMBOL),
    TOPLEFT_SYM(Css3Lexer.TOPLEFT_SYM, AT_RULE_SYMBOL),
    TOPCENTER_SYM(Css3Lexer.TOPCENTER_SYM, AT_RULE_SYMBOL),
    TOPRIGHT_SYM(Css3Lexer.TOPRIGHT_SYM, AT_RULE_SYMBOL),
    TOPRIGHTCORNER_SYM(Css3Lexer.TOPRIGHTCORNER_SYM, AT_RULE_SYMBOL),
    BOTTOMLEFTCORNER_SYM(Css3Lexer.BOTTOMLEFTCORNER_SYM, AT_RULE_SYMBOL),
    BOTTOMLEFT_SYM(Css3Lexer.BOTTOMLEFT_SYM, AT_RULE_SYMBOL),
    BOTTOMCENTER_SYM(Css3Lexer.BOTTOMCENTER_SYM, AT_RULE_SYMBOL),
    BOTTOMRIGHT_SYM(Css3Lexer.BOTTOMRIGHT_SYM, AT_RULE_SYMBOL),
    BOTTOMRIGHTCORNER_SYM(Css3Lexer.BOTTOMRIGHTCORNER_SYM, AT_RULE_SYMBOL),
    LEFTTOP_SYM(Css3Lexer.LEFTTOP_SYM, AT_RULE_SYMBOL),
    LEFTMIDDLE_SYM(Css3Lexer.LEFTMIDDLE_SYM, AT_RULE_SYMBOL),
    LEFTBOTTOM_SYM(Css3Lexer.LEFTBOTTOM_SYM, AT_RULE_SYMBOL),
    RIGHTTOP_SYM(Css3Lexer.RIGHTTOP_SYM, AT_RULE_SYMBOL),
    RIGHTMIDDLE_SYM(Css3Lexer.RIGHTMIDDLE_SYM, AT_RULE_SYMBOL),
    RIGHTBOTTOM_SYM(Css3Lexer.RIGHTBOTTOM_SYM, AT_RULE_SYMBOL),
    
    WEBKIT_KEYFRAMES_SYM(Css3Lexer.WEBKIT_KEYFRAMES_SYM, AT_RULE_SYMBOL),
    
    COUNTER_STYLE_SYM(Css3Lexer.COUNTER_STYLE_SYM, AT_RULE_SYMBOL),
    
    BEGINS(Css3Lexer.BEGINS, OPERATORS),
    ENDS(Css3Lexer.ENDS, OPERATORS),
    CONTAINS(Css3Lexer.CONTAINS, OPERATORS),
    
    FONT_FACE_SYM(Css3Lexer.FONT_FACE_SYM, AT_RULE_SYMBOL),
    HASH_SYMBOL(Css3Lexer.HASH_SYMBOL, OTHERS),
    
    /**
     * '...' in less_args_list rule
     */
    LESS_VARARG(Css3Lexer.LESS_DOTS, OTHERS),
    /**
     * '@rest...' in less_args_list rule
     */
    LESS_REST(Css3Lexer.LESS_REST, OTHERS),
    
    /**
     * WHEN keyword in guarded mixin
     * .mixin (@a) "when" (@a > 10), (@a < -10) { ... }
     */
    LESS_WHEN(Css3Lexer.LESS_WHEN, OTHERS),
    
    /**
     * & operator in rules:
     * 
     * .parent & {
     *       color: black;
     * }
     */
    LESS_AND(Css3Lexer.LESS_AND, OTHERS),
    
    MOZ_DOCUMENT_SYM(Css3Lexer.MOZ_DOCUMENT_SYM, AT_RULE_SYMBOL),
    MOZ_DOMAIN(Css3Lexer.MOZ_DOMAIN, URIS),
    MOZ_URL_PREFIX(Css3Lexer.MOZ_URL_PREFIX, URIS),
    MOZ_REGEXP(Css3Lexer.MOZ_REGEXP, STRINGS),
    
    AT_IDENT(Css3Lexer.AT_IDENT, AT_RULE_SYMBOL),
    
    GREATER_OR_EQ(Css3Lexer.GREATER_OR_EQ, OPERATORS),
    LESS(Css3Lexer.LESS, OPERATORS),
    LESS_OR_EQ(Css3Lexer.LESS_OR_EQ, OPERATORS),
    
    LESS_DOTS(Css3Lexer.LESS_DOTS, IDENTIFIERS),
    SASS_VAR(Css3Lexer.SASS_VAR, IDENTIFIERS),
    SASS_MIXIN(Css3Lexer.SASS_MIXIN, AT_RULE_SYMBOL),
    SASS_INCLUDE(Css3Lexer.SASS_INCLUDE, AT_RULE_SYMBOL),
    SASS_DEFAULT(Css3Lexer.SASS_DEFAULT, KEYWORDS),
    SASS_EXTEND(Css3Lexer.SASS_EXTEND, KEYWORDS),
    SASS_EXTEND_ONLY_SELECTOR(Css3Lexer.SASS_EXTEND_ONLY_SELECTOR, IDENTIFIERS),
    SASS_OPTIONAL(Css3Lexer.SASS_OPTIONAL, KEYWORDS),
    SASS_DEBUG(Css3Lexer.SASS_DEBUG, KEYWORDS),
    SASS_WARN(Css3Lexer.SASS_WARN, KEYWORDS),
    SASS_IF(Css3Lexer.SASS_IF, KEYWORDS),
    SASS_FOR(Css3Lexer.SASS_FOR, KEYWORDS),
    SASS_EACH(Css3Lexer.SASS_EACH, KEYWORDS),
    SASS_WHILE(Css3Lexer.SASS_WHILE, KEYWORDS),
    
    CP_EQ(Css3Lexer.CP_EQ, KEYWORDS),
    SASS_FUNCTION(Css3Lexer.SASS_FUNCTION, KEYWORDS),
    SASS_RETURN(Css3Lexer.SASS_RETURN, KEYWORDS),
    
    ;
    
    private static final Map<Integer, CssTokenId> codesMap = new HashMap<Integer, CssTokenId>();
    static {
        for(CssTokenId id : values()) {
            codesMap.put(id.code, id);
        }
    }

    public static  CssTokenId forTokenTypeCode(int tokenTypeCode) {
        return codesMap.get(tokenTypeCode);
    }
 
    private final CssTokenIdCategory primaryCategory;
    private final int code;

    private static final Language<CssTokenId> language = new CssLanguageHierarchy().language();
    
    CssTokenId(int code, CssTokenIdCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
        this.code = code;
    }

    /** Gets a LanguageDescription describing a set of token ids
     * that comprise the given language.
     *
     * @return non-null LanguageDescription
     */
    public static Language<CssTokenId> language() {
        return language;
    }

    /**
     * Get name of primary token category into which this token belongs.
     * <br/>
     * Other token categories for this id can be defined in the language hierarchy.
     *
     * @return name of the primary token category into which this token belongs
     *  or null if there is no primary category for this token.
     */
    @Override
    public String primaryCategory() {
        return primaryCategory.name().toLowerCase();
    }
    
    /**
     * same as primaryCategory() but returns CssTokenIdCategory enum member
     */
    public CssTokenIdCategory getTokenCategory() {
        return primaryCategory;
    }

    /**
     * Verifies whether the given input text is lexed as on token of this type.
     * 
     * If some part of the input text is not matched by the css token the method returns false.
     * 
     * @since 1.12
     * @param input source code to be lexed
     * @return true if the whole source code is lexed as a token of this type.
     */
    public boolean matchesInput(CharSequence input) {
        TokenHierarchy<CharSequence> th = TokenHierarchy.create(input, CssTokenId.language());
        TokenSequence<CssTokenId> ts = th.tokenSequence(CssTokenId.language());
        ts.moveStart();
        if(!ts.moveNext()) {
            return false;
        }
        org.netbeans.api.lexer.Token<CssTokenId> t = ts.token();
        return !ts.moveNext() && t.id() == this;
    }

}
