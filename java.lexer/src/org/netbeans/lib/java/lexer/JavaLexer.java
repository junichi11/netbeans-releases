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

package org.netbeans.lib.java.lexer;

import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexical analyzer for java language.
 * <br/>
 * It recognizes "version" attribute and expects <code>java.lang.Integer</code>
 * value for it. The default value is Integer.valueOf(5). The lexer changes
 * its behavior in the following way:
 * <ul>
 *     <li> Integer.valueOf(4) - "assert" recognized as keyword (not identifier)
 *     <li> Integer.valueOf(5) - "enum" recognized as keyword (not identifier)
 * </ul>
 *
 * @author Miloslav Metelka
 * @version 1.00
 */
//XXX: be carefull about flyweight tokens - needs to check if the inputX.readLength() matches the image!
public class JavaLexer implements Lexer<JavaTokenId> {
    
    private static final int EOF = LexerInput.EOF;

    private final LexerInput input;
    
    private final TokenFactory<JavaTokenId> tokenFactory;
    
    private final int version;

    public JavaLexer(LexerRestartInfo<JavaTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // never set to non-null value in state()
        
        Integer ver = (Integer)info.getAttributeValue("version");
        this.version = (ver != null) ? ver.intValue() : 9; // TODO: Java 1.8 used by default
    }
    
    public Object state() {
        return null; // always in default state after token recognition
    }
    
    int previousLength = -1;
    int currentLength = -1;
    
    public int nextChar() {
        previousLength = currentLength;
        
        int backupReadLength = input.readLength();
        int c = input.read();
        
        if (c != '\\') {
            currentLength = 1;
            return c;
        }
        
        boolean wasU = false;
        int first;
        
        while ((first = input.read()) == 'u')
            wasU = true;
        
        if (!wasU) {
            input.backup(input.readLengthEOF()- backupReadLength);
            currentLength = 1;
            return input.read();
        }
        
        int second = input.read();
        int third = input.read();
        int fourth = input.read();
        
        if (fourth == LexerInput.EOF) {
            //TODO: broken unicode
            input.backup(input.readLengthEOF()- backupReadLength);
            currentLength = 1;
            return input.read();
        }
        
        first = Character.digit(first, 16);
        second = Character.digit(second, 16);
        third = Character.digit(third, 16);
        fourth = Character.digit(fourth, 16);
        
        if (first == (-1) || second == (-1) || third == (-1) || fourth == (-1)) {
            //TODO: broken unicode
            input.backup(input.readLengthEOF()- backupReadLength);
            currentLength = 1;
            return input.read();
        }
        
        currentLength = input.readLength() - backupReadLength;
        return ((first * 16 + second) * 16 + third) * 16 + fourth;
    }
    
    public void backup(int howMany) {
        switch (howMany) {
            case 1:
                assert currentLength != (-1);
                input.backup(currentLength);
                currentLength = previousLength;
                previousLength = (-1);
                break;
            case 2:
                assert currentLength != (-1) && previousLength != (-1);
                input.backup(currentLength + previousLength);
                currentLength = previousLength = (-1);
                break;
            default:
                assert false : howMany;
        }
    }
    
    public void consumeNewline() {
        if (nextChar() != '\n') backup(1);
    }
    
    public Token<JavaTokenId> nextToken() {
        while(true) {
            int c = nextChar();
            JavaTokenId lookupId = null;
            switch (c) {
                case '#':
                    //Support for exotic identifiers has been removed 6999438
                    return token(JavaTokenId.ERROR);
                case '"': // string literal
                    if (lookupId == null) lookupId = JavaTokenId.STRING_LITERAL;
                    while (true)
                        switch (nextChar()) {
                            case '"': // NOI18N
                                return token(lookupId);
                            case '\\':
                                nextChar();
                                break;
                            case '\r': consumeNewline();
                            case '\n':
                            case EOF:
                                return tokenFactory.createToken(lookupId, //XXX: \n handling for exotic identifiers?
                                        input.readLength(), PartType.START);
                        }

                case '\'': // char literal
                    while (true)
                        switch (nextChar()) {
                            case '\'': // NOI18N
                                return token(JavaTokenId.CHAR_LITERAL);
                            case '\\':
                                nextChar(); // read escaped char
                                break;
                            case '\r': consumeNewline();
                            case '\n':
                            case EOF:
                                return tokenFactory.createToken(JavaTokenId.CHAR_LITERAL,
                                        input.readLength(), PartType.START);
                        }

                case '/':
                    switch (nextChar()) {
                        case '/': // in single-line comment
                            while (true)
                                switch (nextChar()) {
                                    case '\r': consumeNewline();
                                    case '\n':
                                    case EOF:
                                        return token(JavaTokenId.LINE_COMMENT);
                                }
                        case '=': // found /=
                            return token(JavaTokenId.SLASHEQ);
                        case '*': // in multi-line or javadoc comment
                            c = nextChar();
                            if (c == '*') { // either javadoc comment or empty multi-line comment /**/
                                    c = nextChar();
                                    if (c == '/')
                                        return token(JavaTokenId.BLOCK_COMMENT);
                                    while (true) { // in javadoc comment
                                        while (c == '*') {
                                            c = nextChar();
                                            if (c == '/')
                                                return token(JavaTokenId.JAVADOC_COMMENT);
                                            else if (c == EOF)
                                                return tokenFactory.createToken(JavaTokenId.JAVADOC_COMMENT,
                                                        input.readLength(), PartType.START);
                                        }
                                        if (c == EOF)
                                            return tokenFactory.createToken(JavaTokenId.JAVADOC_COMMENT,
                                                        input.readLength(), PartType.START);
                                        c = nextChar();
                                    }

                            } else { // in multi-line comment (and not after '*')
                                while (true) {
                                    c = nextChar();
                                    while (c == '*') {
                                        c = nextChar();
                                        if (c == '/')
                                            return token(JavaTokenId.BLOCK_COMMENT);
                                        else if (c == EOF)
                                            return tokenFactory.createToken(JavaTokenId.BLOCK_COMMENT,
                                                    input.readLength(), PartType.START);
                                    }
                                    if (c == EOF)
                                        return tokenFactory.createToken(JavaTokenId.BLOCK_COMMENT,
                                                input.readLength(), PartType.START);
                                }
                            }
                    } // end of switch()
                    backup(1);
                    return token(JavaTokenId.SLASH);

                case '=':
                    if (nextChar() == '=')
                        return token(JavaTokenId.EQEQ);
                    backup(1);
                    return token(JavaTokenId.EQ);

                case '>':
                    switch (nextChar()) {
                        case '>': // after >>
                            switch (c = nextChar()) {
                                case '>': // after >>>
                                    if (nextChar() == '=')
                                        return token(JavaTokenId.GTGTGTEQ);
                                    backup(1);
                                    return token(JavaTokenId.GTGTGT);
                                case '=': // >>=
                                    return token(JavaTokenId.GTGTEQ);
                            }
                            backup(1);
                            return token(JavaTokenId.GTGT);
                        case '=': // >=
                            return token(JavaTokenId.GTEQ);
                    }
                    backup(1);
                    return token(JavaTokenId.GT);

                case '<':
                    switch (nextChar()) {
                        case '<': // after <<
                            if (nextChar() == '=')
                                return token(JavaTokenId.LTLTEQ);
                            backup(1);
                            return token(JavaTokenId.LTLT);
                        case '=': // <=
                            return token(JavaTokenId.LTEQ);
                    }
                    backup(1);
                    return token(JavaTokenId.LT);

                case '+':
                    switch (nextChar()) {
                        case '+':
                            return token(JavaTokenId.PLUSPLUS);
                        case '=':
                            return token(JavaTokenId.PLUSEQ);
                    }
                    backup(1);
                    return token(JavaTokenId.PLUS);

                case '-':
                    switch (nextChar()) {
                        case '-':
                            return token(JavaTokenId.MINUSMINUS);
                        case '=':
                            return token(JavaTokenId.MINUSEQ);
                        case '>':
                            return token(JavaTokenId.ARROW);
                    }
                    backup(1);
                    return token(JavaTokenId.MINUS);

                case '*':
                    switch (nextChar()) {
                        case '/': // invalid comment end - */
                            return token(JavaTokenId.INVALID_COMMENT_END);
                        case '=':
                            return token(JavaTokenId.STAREQ);
                    }
                    backup(1);
                    return token(JavaTokenId.STAR);

                case '|':
                    switch (nextChar()) {
                        case '|':
                            return token(JavaTokenId.BARBAR);
                        case '=':
                            return token(JavaTokenId.BAREQ);
                    }
                    backup(1);
                    return token(JavaTokenId.BAR);

                case '&':
                    switch (nextChar()) {
                        case '&':
                            return token(JavaTokenId.AMPAMP);
                        case '=':
                            return token(JavaTokenId.AMPEQ);
                    }
                    backup(1);
                    return token(JavaTokenId.AMP);

                case '%':
                    if (nextChar() == '=')
                        return token(JavaTokenId.PERCENTEQ);
                    backup(1);
                    return token(JavaTokenId.PERCENT);

                case '^':
                    if (nextChar() == '=')
                        return token(JavaTokenId.CARETEQ);
                    backup(1);
                    return token(JavaTokenId.CARET);

                case '!':
                    if (nextChar() == '=')
                        return token(JavaTokenId.BANGEQ);
                    backup(1);
                    return token(JavaTokenId.BANG);

                case '.':
                    if ((c = nextChar()) == '.')
                        if (nextChar() == '.') { // ellipsis ...
                            return token(JavaTokenId.ELLIPSIS);
                        } else
                            backup(2);
                    else if ('0' <= c && c <= '9') { // float literal
                        return finishNumberLiteral(nextChar(), true);
                    } else
                        backup(1);
                    return token(JavaTokenId.DOT);

                case '~':
                    return token(JavaTokenId.TILDE);
                case ',':
                    return token(JavaTokenId.COMMA);
                case ';':
                    return token(JavaTokenId.SEMICOLON);
                case ':':
                    if (nextChar() == ':')
                        return token(JavaTokenId.COLONCOLON);
                    backup(1);
                    return token(JavaTokenId.COLON);
                case '?':
                    return token(JavaTokenId.QUESTION);
                case '(':
                    return token(JavaTokenId.LPAREN);
                case ')':
                    return token(JavaTokenId.RPAREN);
                case '[':
                    return token(JavaTokenId.LBRACKET);
                case ']':
                    return token(JavaTokenId.RBRACKET);
                case '{':
                    return token(JavaTokenId.LBRACE);
                case '}':
                    return token(JavaTokenId.RBRACE);
                case '@':
                    return token(JavaTokenId.AT);

                case '0': // in a number literal
		    c = nextChar();
                    if (c == 'x' || c == 'X') { // in hexadecimal (possibly floating-point) literal
                        boolean inFraction = false;
                        boolean afterDigit = false;
                        while (true) {
                            switch (nextChar()) {
                                case '0': case '1': case '2': case '3': case '4':
                                case '5': case '6': case '7': case '8': case '9':
                                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                    afterDigit = true;
                                    break;
                                case '.': // hex float literal
                                    if (!inFraction) {
                                        inFraction = true;
                                        afterDigit = false;
                                    } else { // two dots in the float literal
                                        return token(JavaTokenId.FLOAT_LITERAL_INVALID);
                                    }
                                    break;
                                case 'p': case 'P': // binary exponent
                                    return finishFloatExponent();
                                case 'l': case 'L':
                                    return token(JavaTokenId.LONG_LITERAL);
                                case '_':
                                    if (this.version >= 7 && afterDigit) {
                                        int cc = nextChar();
                                        backup(1);
                                        if (cc >= '0' && cc <= '9' || cc >= 'a' && cc <= 'f' || cc >= 'A' && cc <= 'F' || cc == '_') {
                                            break;
                                        }
                                    }
                                default:
                                    backup(1);
                                    // if float then before mandatory binary exponent => invalid
                                    return token(inFraction ? JavaTokenId.FLOAT_LITERAL_INVALID
                                            : JavaTokenId.INT_LITERAL);
                            }
                        } // end of while(true)
                    } else if (this.version >= 7 && (c == 'b' || c == 'B')) { // in binary literal
                        boolean afterDigit = false;
                        while (true) {
                            switch (nextChar()) {
                                case '0': case '1':
                                    afterDigit = true;
                                    break;
                                case 'l': case 'L':
                                    return token(JavaTokenId.LONG_LITERAL);
                                case '_':
                                    if (afterDigit) {
                                        int cc = nextChar();
                                        backup(1);
                                        if (cc == '0' || cc == '1' || cc == '_') {
                                            break;
                                        }
                                    }
                                default:
                                    backup(1);
                                    return token(JavaTokenId.INT_LITERAL);
                            }
                        }
                    }
                    return finishNumberLiteral(c, false);
                    
                case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    return finishNumberLiteral(nextChar(), false);

                    
                // Keywords lexing    
                case 'a':
                    switch (c = nextChar()) {
                        case 'b':
                            if ((c = nextChar()) == 's'
                             && (c = nextChar()) == 't'
                             && (c = nextChar()) == 'r'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'c'
                             && (c = nextChar()) == 't')
                                return keywordOrIdentifier(JavaTokenId.ABSTRACT);
                            break;
                        case 's':
                            if ((c = nextChar()) == 's'
                             && (c = nextChar()) == 'e'
                             && (c = nextChar()) == 'r'
                             && (c = nextChar()) == 't')
                                return (version >= 4)
                                        ? keywordOrIdentifier(JavaTokenId.ASSERT)
                                        : finishIdentifier();
                            break;
                    }
                    return finishIdentifier(c);

                case 'b':
                    switch (c = nextChar()) {
                        case 'o':
                            if ((c = nextChar()) == 'o'
                             && (c = nextChar()) == 'l'
                             && (c = nextChar()) == 'e'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'n')
                                return keywordOrIdentifier(JavaTokenId.BOOLEAN);
                            break;
                        case 'r':
                            if ((c = nextChar()) == 'e'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'k')
                                return keywordOrIdentifier(JavaTokenId.BREAK);
                            break;
                        case 'y':
                            if ((c = nextChar()) == 't'
                             && (c = nextChar()) == 'e')
                                return keywordOrIdentifier(JavaTokenId.BYTE);
                            break;
                    }
                    return finishIdentifier(c);

                case 'c':
                    switch (c = nextChar()) {
                        case 'a':
                            switch (c = nextChar()) {
                                case 's':
                                    if ((c = nextChar()) == 'e')
                                        return keywordOrIdentifier(JavaTokenId.CASE);
                                    break;
                                case 't':
                                    if ((c = nextChar()) == 'c'
                                     && (c = nextChar()) == 'h')
                                        return keywordOrIdentifier(JavaTokenId.CATCH);
                                    break;
                            }
                            break;
                        case 'h':
                            if ((c = nextChar()) == 'a'
                             && (c = nextChar()) == 'r')
                                return keywordOrIdentifier(JavaTokenId.CHAR);
                            break;
                        case 'l':
                            if ((c = nextChar()) == 'a'
                             && (c = nextChar()) == 's'
                             && (c = nextChar()) == 's')
                                return keywordOrIdentifier(JavaTokenId.CLASS);
                            break;
                        case 'o':
                            if ((c = nextChar()) == 'n') {
                                switch (c = nextChar()) {
                                    case 's':
                                        if ((c = nextChar()) == 't')
                                            return keywordOrIdentifier(JavaTokenId.CONST);
                                        break;
                                    case 't':
                                        if ((c = nextChar()) == 'i'
                                         && (c = nextChar()) == 'n'
                                         && (c = nextChar()) == 'u'
                                         && (c = nextChar()) == 'e')
                                            return keywordOrIdentifier(JavaTokenId.CONTINUE);
                                        break;
                                }
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'd':
                    switch (c = nextChar()) {
                        case 'e':
                            if ((c = nextChar()) == 'f'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'u'
                             && (c = nextChar()) == 'l'
                             && (c = nextChar()) == 't')
                                return keywordOrIdentifier(JavaTokenId.DEFAULT);
                            break;
                        case 'o':
                            switch (c = nextChar()) {
                                case 'u':
                                    if ((c = nextChar()) == 'b'
                                     && (c = nextChar()) == 'l'
                                     && (c = nextChar()) == 'e')
                                        return keywordOrIdentifier(JavaTokenId.DOUBLE);
                                    break;
                                default:
                                    return keywordOrIdentifier(JavaTokenId.DO, c);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'e':
                    switch (c = nextChar()) {
                        case 'l':
                            if ((c = nextChar()) == 's'
                             && (c = nextChar()) == 'e')
                                return keywordOrIdentifier(JavaTokenId.ELSE);
                            break;
                        case 'n':
                            if ((c = nextChar()) == 'u'
                             && (c = nextChar()) == 'm')
                                return (version >= 5)
                                        ? keywordOrIdentifier(JavaTokenId.ENUM)
                                        : finishIdentifier();
                            break;
                        case 'x':
                            if ((c = nextChar()) == 't'
                             && (c = nextChar()) == 'e'
                             && (c = nextChar()) == 'n'
                             && (c = nextChar()) == 'd'
                             && (c = nextChar()) == 's')
                                return keywordOrIdentifier(JavaTokenId.EXTENDS);
                            break;
                    }
                    return finishIdentifier(c);

                case 'f':
                    switch (c = nextChar()) {
                        case 'a':
                            if ((c = nextChar()) == 'l'
                             && (c = nextChar()) == 's'
                             && (c = nextChar()) == 'e')
                                return keywordOrIdentifier(JavaTokenId.FALSE);
                            break;
                        case 'i':
                            if ((c = nextChar()) == 'n'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'l')
                                switch (c = nextChar()) {
                                    case 'l':
                                        if ((c = nextChar()) == 'y')
                                            return keywordOrIdentifier(JavaTokenId.FINALLY);
                                        break;
                                    default:
                                        return keywordOrIdentifier(JavaTokenId.FINAL, c);
                                }
                            break;
                        case 'l':
                            if ((c = nextChar()) == 'o'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 't')
                                return keywordOrIdentifier(JavaTokenId.FLOAT);
                            break;
                        case 'o':
                            if ((c = nextChar()) == 'r')
                                return keywordOrIdentifier(JavaTokenId.FOR);
                            break;
                    }
                    return finishIdentifier(c);

                case 'g':
                    if ((c = nextChar()) == 'o'
                     && (c = nextChar()) == 't'
                     && (c = nextChar()) == 'o')
                        return keywordOrIdentifier(JavaTokenId.GOTO);
                    return finishIdentifier(c);
                    
                case 'i':
                    switch (c = nextChar()) {
                        case 'f':
                            return keywordOrIdentifier(JavaTokenId.IF);
                        case 'm':
                            if ((c = nextChar()) == 'p') {
                                switch (c = nextChar()) {
                                    case 'l':
                                        if ((c = nextChar()) == 'e'
                                         && (c = nextChar()) == 'm'
                                         && (c = nextChar()) == 'e'
                                         && (c = nextChar()) == 'n'
                                         && (c = nextChar()) == 't'
                                         && (c = nextChar()) == 's')
                                            return keywordOrIdentifier(JavaTokenId.IMPLEMENTS);
                                        break;
                                    case 'o':
                                        if ((c = nextChar()) == 'r'
                                         && (c = nextChar()) == 't')
                                            return keywordOrIdentifier(JavaTokenId.IMPORT);
                                        break;
                                }
                            }
                            break;
                        case 'n':
                            switch (c = nextChar()) {
                                case 's':
                                    if ((c = nextChar()) == 't'
                                     && (c = nextChar()) == 'a'
                                     && (c = nextChar()) == 'n'
                                     && (c = nextChar()) == 'c'
                                     && (c = nextChar()) == 'e'
                                     && (c = nextChar()) == 'o'
                                     && (c = nextChar()) == 'f')
                                        return keywordOrIdentifier(JavaTokenId.INSTANCEOF);
                                    break;
                                case 't':
                                    switch (c = nextChar()) {
                                        case 'e':
                                            if ((c = nextChar()) == 'r'
                                             && (c = nextChar()) == 'f'
                                             && (c = nextChar()) == 'a'
                                             && (c = nextChar()) == 'c'
                                             && (c = nextChar()) == 'e')
                                                return keywordOrIdentifier(JavaTokenId.INTERFACE);
                                            break;
                                        default:
                                            return keywordOrIdentifier(JavaTokenId.INT, c);
                                    }
                                    break;
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'l':
                    if ((c = nextChar()) == 'o'
                     && (c = nextChar()) == 'n'
                     && (c = nextChar()) == 'g')
                        return keywordOrIdentifier(JavaTokenId.LONG);
                    return finishIdentifier(c);

                case 'n':
                    switch (c = nextChar()) {
                        case 'a':
                            if ((c = nextChar()) == 't'
                             && (c = nextChar()) == 'i'
                             && (c = nextChar()) == 'v'
                             && (c = nextChar()) == 'e')
                                return keywordOrIdentifier(JavaTokenId.NATIVE);
                            break;
                        case 'e':
                            if ((c = nextChar()) == 'w')
                                return keywordOrIdentifier(JavaTokenId.NEW);
                            break;
                        case 'u':
                            if ((c = nextChar()) == 'l'
                             && (c = nextChar()) == 'l')
                                return keywordOrIdentifier(JavaTokenId.NULL);
                            break;
                    }
                    return finishIdentifier(c);

                case 'p':
                    switch (c = nextChar()) {
                        case 'a':
                            if ((c = nextChar()) == 'c'
                             && (c = nextChar()) == 'k'
                             && (c = nextChar()) == 'a'
                             && (c = nextChar()) == 'g'
                             && (c = nextChar()) == 'e')
                                return keywordOrIdentifier(JavaTokenId.PACKAGE);
                            break;
                        case 'r':
                            switch (c = nextChar()) {
                                case 'i':
                                    if ((c = nextChar()) == 'v'
                                     && (c = nextChar()) == 'a'
                                     && (c = nextChar()) == 't'
                                     && (c = nextChar()) == 'e')
                                        return keywordOrIdentifier(JavaTokenId.PRIVATE);
                                    break;
                                case 'o':
                                    if ((c = nextChar()) == 't'
                                     && (c = nextChar()) == 'e'
                                     && (c = nextChar()) == 'c'
                                     && (c = nextChar()) == 't'
                                     && (c = nextChar()) == 'e'
                                     && (c = nextChar()) == 'd')
                                        return keywordOrIdentifier(JavaTokenId.PROTECTED);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = nextChar()) == 'b'
                             && (c = nextChar()) == 'l'
                             && (c = nextChar()) == 'i'
                             && (c = nextChar()) == 'c')
                                return keywordOrIdentifier(JavaTokenId.PUBLIC);
                            break;
                    }
                    return finishIdentifier(c);

                case 'r':
                    if ((c = nextChar()) == 'e'
                     && (c = nextChar()) == 't'
                     && (c = nextChar()) == 'u'
                     && (c = nextChar()) == 'r'
                     && (c = nextChar()) == 'n')
                        return keywordOrIdentifier(JavaTokenId.RETURN);
                    return finishIdentifier(c);

                case 's':
                    switch (c = nextChar()) {
                        case 'h':
                            if ((c = nextChar()) == 'o'
                             && (c = nextChar()) == 'r'
                             && (c = nextChar()) == 't')
                                return keywordOrIdentifier(JavaTokenId.SHORT);
                            break;
                        case 't':
                            switch (c = nextChar()) {
                                case 'a':
                                    if ((c = nextChar()) == 't'
                                     && (c = nextChar()) == 'i'
                                     && (c = nextChar()) == 'c')
                                        return keywordOrIdentifier(JavaTokenId.STATIC);
                                    break;
                                case 'r':
                                    if ((c = nextChar()) == 'i'
                                     && (c = nextChar()) == 'c'
                                     && (c = nextChar()) == 't'
                                     && (c = nextChar()) == 'f'
                                     && (c = nextChar()) == 'p')
                                        return keywordOrIdentifier(JavaTokenId.STRICTFP);
                                    break;
                            }
                            break;
                        case 'u':
                            if ((c = nextChar()) == 'p'
                             && (c = nextChar()) == 'e'
                             && (c = nextChar()) == 'r')
                                return keywordOrIdentifier(JavaTokenId.SUPER);
                            break;
                        case 'w':
                            if ((c = nextChar()) == 'i'
                             && (c = nextChar()) == 't'
                             && (c = nextChar()) == 'c'
                             && (c = nextChar()) == 'h')
                                return keywordOrIdentifier(JavaTokenId.SWITCH);
                            break;
                        case 'y':
                            if ((c = nextChar()) == 'n'
                             && (c = nextChar()) == 'c'
                             && (c = nextChar()) == 'h'
                             && (c = nextChar()) == 'r'
                             && (c = nextChar()) == 'o'
                             && (c = nextChar()) == 'n'
                             && (c = nextChar()) == 'i'
                             && (c = nextChar()) == 'z'
                             && (c = nextChar()) == 'e'
                             && (c = nextChar()) == 'd')
                                return keywordOrIdentifier(JavaTokenId.SYNCHRONIZED);
                            break;
                    }
                    return finishIdentifier(c);

                case 't':
                    switch (c = nextChar()) {
                        case 'h':
                            switch (c = nextChar()) {
                                case 'i':
                                    if ((c = nextChar()) == 's')
                                        return keywordOrIdentifier(JavaTokenId.THIS);
                                    break;
                                case 'r':
                                    if ((c = nextChar()) == 'o'
                                     && (c = nextChar()) == 'w')
                                        switch (c = nextChar()) {
                                            case 's':
                                                return keywordOrIdentifier(JavaTokenId.THROWS);
                                            default:
                                                return keywordOrIdentifier(JavaTokenId.THROW, c);
                                        }
                                    break;
                            }
                            break;
                        case 'r':
                            switch (c = nextChar()) {
                                case 'a':
                                    if ((c = nextChar()) == 'n'
                                     && (c = nextChar()) == 's'
                                     && (c = nextChar()) == 'i'
                                     && (c = nextChar()) == 'e'
                                     && (c = nextChar()) == 'n'
                                     && (c = nextChar()) == 't')
                                        return keywordOrIdentifier(JavaTokenId.TRANSIENT);
                                    break;
                                case 'u':
                                    if ((c = nextChar()) == 'e')
                                        return keywordOrIdentifier(JavaTokenId.TRUE);
                                    break;
                                case 'y':
                                    return keywordOrIdentifier(JavaTokenId.TRY);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'v':
                    if ((c = nextChar()) == 'o') {
                        switch (c = nextChar()) {
                            case 'i':
                                if ((c = nextChar()) == 'd')
                                    return keywordOrIdentifier(JavaTokenId.VOID);
                                break;
                            case 'l':
                                if ((c = nextChar()) == 'a'
                                 && (c = nextChar()) == 't'
                                 && (c = nextChar()) == 'i'
                                 && (c = nextChar()) == 'l'
                                 && (c = nextChar()) == 'e')
                                    return keywordOrIdentifier(JavaTokenId.VOLATILE);
                                break;
                        }
                    }
                    return finishIdentifier(c);

                case 'w':
                    if ((c = nextChar()) == 'h'
                     && (c = nextChar()) == 'i'
                     && (c = nextChar()) == 'l'
                     && (c = nextChar()) == 'e')
                        return keywordOrIdentifier(JavaTokenId.WHILE);
                    return finishIdentifier(c);

                // Rest of lowercase letters starting identifiers
                case 'h': case 'j': case 'k': case 'm': case 'o':
                case 'q': case 'u': case 'x': case 'y': case 'z':
                // Uppercase letters starting identifiers
                case 'A': case 'B': case 'C': case 'D': case 'E':
                case 'F': case 'G': case 'H': case 'I': case 'J':
                case 'K': case 'L': case 'M': case 'N': case 'O':
                case 'P': case 'Q': case 'R': case 'S': case 'T':
                case 'U': case 'V': case 'W': case 'X': case 'Y':
                case 'Z':
                case '$':
                    return finishIdentifier();
                    
                case '_':
                    if (this.version >= 9)
                        return keywordOrIdentifier(JavaTokenId.UNDERSCORE);
                    return finishIdentifier();
                    
                // All Character.isWhitespace(c) below 0x80 follow
                // ['\t' - '\r'] and [0x1c - ' ']
                case '\t':
                case '\n':
                case 0x0b:
                case '\f':
                case '\r':
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                    return finishWhitespace();
                case ' ':
                    c = nextChar();
                    if (c == EOF || !Character.isWhitespace(c)) { // Return single space as flyweight token
                        backup(1);
                        return   input.readLength() == 1
                               ? tokenFactory.getFlyweightToken(JavaTokenId.WHITESPACE, " ")
                               : tokenFactory.createToken(JavaTokenId.WHITESPACE);
                    }
                    return finishWhitespace();

                case EOF:
                    return null;

                default:
                    if (c >= 0x80) { // lowSurr ones already handled above
                        c = translateSurrogates(c);
                        if (Character.isJavaIdentifierStart(c))
                            return finishIdentifier();
                        if (Character.isWhitespace(c))
                            return finishWhitespace();
                    }

                    // Invalid char
                    return token(JavaTokenId.ERROR);
            } // end of switch (c)
        } // end of while(true)
    }
    
    private int translateSurrogates(int c) {
        if (Character.isHighSurrogate((char)c)) {
            int lowSurr = nextChar();
            if (lowSurr != EOF && Character.isLowSurrogate((char)lowSurr)) {
                // c and lowSurr form the integer unicode char.
                c = Character.toCodePoint((char)c, (char)lowSurr);
            } else {
                // Otherwise it's error: Low surrogate does not follow the high one.
                // Leave the original character unchanged.
                // As the surrogates do not belong to any
                // specific unicode category the lexer should finally
                // categorize them as a lexical error.
                backup(1);
            }
        }
        return c;
    }

    private Token<JavaTokenId> finishWhitespace() {
        while (true) {
            int c = nextChar();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (c == EOF || !Character.isWhitespace(c)) {
                backup(1);
                return tokenFactory.createToken(JavaTokenId.WHITESPACE);
            }
        }
    }
    
    private Token<JavaTokenId> finishIdentifier() {
        return finishIdentifier(nextChar());
    }
    
    private Token<JavaTokenId> finishIdentifier(int c) {
        while (true) {
            if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
                // For surrogate 2 chars must be backed up
                backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactory.createToken(JavaTokenId.IDENTIFIER);
            }
            c = nextChar();
        }
    }

    private Token<JavaTokenId> keywordOrIdentifier(JavaTokenId keywordId) {
        return keywordOrIdentifier(keywordId, nextChar());
    }

    private Token<JavaTokenId> keywordOrIdentifier(JavaTokenId keywordId, int c) {
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
            // For surrogate 2 chars must be backed up
            backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
            return token(keywordId);
        } else // c is identifier part
            return finishIdentifier();
    }
    
    private Token<JavaTokenId> finishNumberLiteral(int c, boolean inFraction) {
        boolean afterDigit = true;
        while (true) {
            switch (c) {
                case '.':
                    if (!inFraction) {
                        inFraction = true;
                        afterDigit = false;
                    } else { // two dots in the literal
                        return token(JavaTokenId.FLOAT_LITERAL_INVALID);
                    }
                    break;
                case 'l': case 'L': // 0l or 0L
                    return token(JavaTokenId.LONG_LITERAL);
                case 'd': case 'D':
                    return token(JavaTokenId.DOUBLE_LITERAL);
                case 'f': case 'F':
                    return token(JavaTokenId.FLOAT_LITERAL);
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    afterDigit = true;
                    break;
                case 'e': case 'E': // exponent part
                    return finishFloatExponent();
                case '_':
                    if (this.version >= 7 && afterDigit) {
                        int cc = nextChar();
                        backup(1);
                        if (cc >= '0' && cc <= '9' || cc == '_') {
                            break;
                        }
                    }
                default:
                    backup(1);
                    return token(inFraction ? JavaTokenId.DOUBLE_LITERAL
                            : JavaTokenId.INT_LITERAL);
            }
            c = nextChar();
        }
    }
    
    private Token<JavaTokenId> finishFloatExponent() {
        int c = nextChar();
        if (c == '+' || c == '-') {
            c = nextChar();
        }
        if (c < '0' || '9' < c)
            return token(JavaTokenId.FLOAT_LITERAL_INVALID);
        do {
            c = nextChar();
        } while ('0' <= c && c <= '9'); // reading exponent
        switch (c) {
            case 'd': case 'D':
                return token(JavaTokenId.DOUBLE_LITERAL);
            case 'f': case 'F':
                return token(JavaTokenId.FLOAT_LITERAL);
            default:
                backup(1);
                return token(JavaTokenId.DOUBLE_LITERAL);
        }
    }
    
    private Token<JavaTokenId> token(JavaTokenId id) {
        String fixedText = id.fixedText();
        return (fixedText != null && fixedText.length() == input.readLength())
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }
    
    public void release() {
    }

}
