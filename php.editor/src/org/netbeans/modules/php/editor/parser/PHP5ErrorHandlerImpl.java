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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.parser.GSFPHPParser.Context;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.InLineHtml;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Pisl, Ondrej Brejla
 */
public class PHP5ErrorHandlerImpl implements PHP5ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(PHP5ErrorHandler.class.getName());

    private final List<SyntaxError> syntaxErrors;

    private final Context context;

    private volatile boolean handleErrors = true;

    public PHP5ErrorHandlerImpl(Context context) {
        super();
        this.context = context;
        syntaxErrors = new ArrayList<SyntaxError>();
    }

    @Override
    public void handleError(Type type, short[] expectedtokens, Symbol current, Symbol previous) {
        if (handleErrors) {
            if (type == ParserErrorHandler.Type.SYNTAX_ERROR) {
                // logging syntax error
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("Syntax error:"); //NOI18N
                    LOGGER.log(Level.FINEST, "Current [{0}, {1}]({2}): {3}", new Object[]{current.left, current.right, Utils.getASTScannerTokenName(current.sym), current.value}); //NOI18N
                    LOGGER.log(Level.FINEST, "Previous [{0}, {1}] ({2}):{3}", new Object[]{previous.left, previous.right, Utils.getASTScannerTokenName(previous.sym), previous.value}); //NOI18N
                    StringBuilder message = new StringBuilder();
                    message.append("Expected tokens:"); //NOI18N
                    for (int i = 0; i < expectedtokens.length; i += 2) {
                        message.append(" ").append( Utils.getASTScannerTokenName(expectedtokens[i])); //NOI18N
                    }
                    LOGGER.finest(message.toString());
                }
                syntaxErrors.add(new SyntaxError(expectedtokens, current, previous));
            }
        }
    }

    @Override
    public void disableHandling() {
        handleErrors = false;
    }

    @Override
    public List<Error> displayFatalError(){
        Error error = new FatalError(context);
        return Arrays.asList(error);
    }

    @Override
    public List<Error> displaySyntaxErrors(Program program) {
        List<Error> errors = new ArrayList<Error>();
        for (SyntaxError syntaxError : syntaxErrors) {
            if (isErrorAfterCommonToken(syntaxError)) {
                errors.add(defaultSyntaxErrorHandling(syntaxError));
            }
        }
        return errors;
    }

    private boolean isErrorAfterCommonToken(SyntaxError syntaxError) {
        Symbol previousToken = syntaxError.getPreviousToken();
        return (!(previousToken.value instanceof List) && !(previousToken.value instanceof ASTNode)) || (previousToken.value instanceof InLineHtml);
    }

    @NbBundle.Messages({
        "SE_After=after",
        "SE_EOF=End of File",
        "SE_Message=Syntax error",
        "SE_Unexpected=unexpected",
        "SE_Expected=expected"
    })
    private Error defaultSyntaxErrorHandling(SyntaxError syntaxError) {
        String unexpectedText = ""; //NOI18N
        StringBuilder message = new StringBuilder();
        boolean isUnexpected;
        boolean isAfter;
        String afterText = ""; //NOI18N
        Symbol currentToken = syntaxError.getCurrentToken();
        int start  = currentToken.left;
        int end = currentToken.right;

        if (currentToken.sym == ASTPHP5Symbols.EOF) {
            isUnexpected = true;
            unexpectedText = Bundle.SE_EOF();
            start = end - 1;
        } else if (currentToken.sym == ASTPHP5Symbols.T_STRING || currentToken.sym == ASTPHP5Symbols.T_CONSTANT_ENCAPSED_STRING ||
                currentToken.sym == ASTPHP5Symbols.T_DNUMBER || currentToken.sym == ASTPHP5Symbols.T_LNUMBER ||
                currentToken.sym == ASTPHP5Symbols.T_VARIABLE) {
            isUnexpected = true;
            unexpectedText = getTokenTextForm(currentToken.sym) + " '" + String.valueOf(currentToken.value) + "'";
            end = start + ((String) currentToken.value).trim().length();
        } else {
            String currentText = getTokenTextForm(currentToken.sym);
            isUnexpected = StringUtils.hasText(currentText);
            if (isUnexpected) {
                unexpectedText = currentText.trim();
                end = start + unexpectedText.length();
            }
        }
        Symbol previousToken = syntaxError.getPreviousToken();
        if (previousToken.sym == ASTPHP5Symbols.T_STRING || previousToken.sym == ASTPHP5Symbols.T_CONSTANT_ENCAPSED_STRING ||
                previousToken.sym == ASTPHP5Symbols.T_DNUMBER || previousToken.sym == ASTPHP5Symbols.T_LNUMBER ||
                previousToken.sym == ASTPHP5Symbols.T_VARIABLE) {
            isAfter = true;
            afterText = getTokenTextForm(previousToken.sym) + " '" + String.valueOf(previousToken.value) + "'";
        } else {
            String previousText = getTokenTextForm(previousToken.sym);
            isAfter = StringUtils.hasText(previousText);
            if (isAfter) {
                afterText = previousText.trim();
            }
        }

        List<String> possibleTags = new ArrayList<String>();
        for (int i = 0; i < syntaxError.getExpectedTokens().length; i += 2) {
            String text = getTokenTextForm(syntaxError.getExpectedTokens()[i]);
            if (text != null) {
                possibleTags.add(text);
            }
        }

        message.append(Bundle.SE_Message());
        if (isUnexpected) {
            message.append("\n ").append(Bundle.SE_Unexpected()); //NOI18N
            message.append(":\t"); //NOI18N
            message.append(unexpectedText);
        }
        if (isAfter) {
            message.append("\n ").append(Bundle.SE_After()); //NOI18N
            message.append(":\t"); //NOI18N
            message.append(afterText);
        }
        if (possibleTags.size() > 0) {
            message.append("\n ").append(Bundle.SE_Expected()); //NOI18N
            message.append(":\t"); //NOI18N
            boolean addOR = false;
            for (String tag : possibleTags) {
                if (addOR) {
                    message.append(", "); //NOI18N
                } else {
                    addOR = true;
                }
                message.append(tag);
            }
        }
        return new GSFPHPError(message.toString(), context.getSnapshot().getSource().getFileObject(), start, end, Severity.ERROR, new Object[]{syntaxError});
    }

    @Override
    public List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    private String getTokenTextForm(int token) {
        String text = null;
        switch (token) {
            case ASTPHP5Symbols.T_BOOLEAN_AND : text = "&&"; break; //NOI18N
            case ASTPHP5Symbols.T_INLINE_HTML : text = "inline html"; break; //NOI18N
            case ASTPHP5Symbols.T_EMPTY : text = "empty"; break; //NOI18N
            case ASTPHP5Symbols.T_PROTECTED : text = "protected"; break; //NOI18N
            case ASTPHP5Symbols.T_CLOSE_RECT : text = "]"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_NOT_EQUAL : text = "!="; break; //NOI18N
            case ASTPHP5Symbols.T_INCLUDE : text = "include"; break; //NOI18N
            case ASTPHP5Symbols.T_QUATE : text = "'\"'"; break; //NOI18N
            case ASTPHP5Symbols.T_GLOBAL : text = "global"; break; //NOI18N
            case ASTPHP5Symbols.T_PRINT : text = "print"; break; //NOI18N
            case ASTPHP5Symbols.T_OR_EQUAL : text = "|="; break; //NOI18N
            case ASTPHP5Symbols.T_LOGICAL_XOR : text = "XOR"; break; //NOI18N
            case ASTPHP5Symbols.T_FUNCTION : text = "function"; break; //NOI18N
            case ASTPHP5Symbols.T_STATIC : text = "static"; break; //NOI18N
            case ASTPHP5Symbols.T_NEKUDA : text = "'.'"; break; //NOI18N
            case ASTPHP5Symbols.T_THROW : text = "throw"; break; //NOI18N
            case ASTPHP5Symbols.T_CLASS : text = "class"; break; //NOI18N
            case ASTPHP5Symbols.T_ABSTRACT : text = "abstract"; break; //NOI18N
            case ASTPHP5Symbols.T_ENCAPSED_AND_WHITESPACE : text = "String"; break; //NOI18N
            case ASTPHP5Symbols.T_MOD_EQUAL : text = "%="; break; //NOI18N
            case ASTPHP5Symbols.T_BREAK : text = "break"; break; //NOI18N
            case ASTPHP5Symbols.T_WHILE : text = "while"; break; //NOI18N
            case ASTPHP5Symbols.T_DO : text = "do"; break; //NOI18N
            case ASTPHP5Symbols.T_CONST : text = "const"; break; //NOI18N
            case ASTPHP5Symbols.T_CONTINUE : text = "continue"; break; //NOI18N
            case ASTPHP5Symbols.T_FUNC_C : text = "__FUNCTION__"; break; //NOI18N
            case ASTPHP5Symbols.T_DIV : text = "/"; break; //NOI18N
            case ASTPHP5Symbols.T_LOGICAL_OR : text = "OR"; break; //NOI18N
            case ASTPHP5Symbols.T_DIR : text = "__DIR__"; break; //NOI18N
            case ASTPHP5Symbols.T_OPEN_PARENTHESE : text = "("; break; //NOI18N
            case ASTPHP5Symbols.T_REFERENCE : text = "&"; break; //NOI18N
            case ASTPHP5Symbols.T_COMMA : text = "','"; break; //NOI18N
            case ASTPHP5Symbols.T_ELSE : text = "else"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_EQUAL : text = "=="; break; //NOI18N
            case ASTPHP5Symbols.T_LIST : text = "list"; break; //NOI18N
            case ASTPHP5Symbols.T_NAMESPACE : text = "namespace"; break; //NOI18N
            case ASTPHP5Symbols.T_NS_SEPARATOR : text = "\\"; break; //NOI18N
            case ASTPHP5Symbols.T_OR : text = "|"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_IDENTICAL : text = "==="; break; //NOI18N
            case ASTPHP5Symbols.T_INC : text = "++"; break; //NOI18N
            case ASTPHP5Symbols.T_ELSEIF : text = "elseif"; break; //NOI18N
            case ASTPHP5Symbols.T_TRY : text = "try"; break; //NOI18N
            case ASTPHP5Symbols.T_START_NOWDOC : text = "<<<'...'"; break; //NOI18N
            case ASTPHP5Symbols.T_PRIVATE : text = "private"; break; //NOI18N
            case ASTPHP5Symbols.T_UNSET_CAST : text = "(unset)"; break; //NOI18N
            case ASTPHP5Symbols.T_INCLUDE_ONCE : text = "include_once"; break; //NOI18N
            case ASTPHP5Symbols.T_ENDIF : text = "endif"; break; //NOI18N
            case ASTPHP5Symbols.T_SR_EQUAL : text = ">>="; break; //NOI18N
            case ASTPHP5Symbols.T_PUBLIC : text = "public"; break; //NOI18N
            case ASTPHP5Symbols.T_OBJECT_OPERATOR : text = "->"; break; //NOI18N
            case ASTPHP5Symbols.T_TILDA : text = "~"; break; //NOI18N
            case ASTPHP5Symbols.T_PAAMAYIM_NEKUDOTAYIM : text = "::"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_SMALLER_OR_EQUAL : text = "<="; break; //NOI18N
            case ASTPHP5Symbols.T_XOR_EQUAL : text = "^="; break; //NOI18N
            case ASTPHP5Symbols.T_ENDFOREACH : text = "endforeach"; break; //NOI18N
            case ASTPHP5Symbols.T_CONSTANT_ENCAPSED_STRING : text = "String"; break; //NOI18N
            case ASTPHP5Symbols.T_BACKQUATE : text = "'`'"; break; //NOI18N
            case ASTPHP5Symbols.T_AT : text = "@"; break; //NOI18N
            case ASTPHP5Symbols.T_AS : text = "as"; break; //NOI18N
            case ASTPHP5Symbols.T_CURLY_CLOSE : text = "}"; break; //NOI18N
            case ASTPHP5Symbols.T_ENDDECLARE : text = "enddeclare"; break; //NOI18N
            case ASTPHP5Symbols.T_CATCH : text = "catch"; break; //NOI18N
            case ASTPHP5Symbols.T_CASE : text = "case"; break; //NOI18N
            case ASTPHP5Symbols.T_VARIABLE : text = "variable"; break; //NOI18N
            case ASTPHP5Symbols.T_INSTEADOF : text = "insteadof"; break; //NOI18N
            case ASTPHP5Symbols.T_NEW : text = "new"; break; //NOI18N
            case ASTPHP5Symbols.T_MINUS_EQUAL : text = "-="; break; //NOI18N
            case ASTPHP5Symbols.T_PLUS : text = "+"; break; //NOI18N
            case ASTPHP5Symbols.T_SL_EQUAL : text = "<<="; break; //NOI18N
            case ASTPHP5Symbols.T_ENDWHILE : text = "endwhile"; break; //NOI18N
            case ASTPHP5Symbols.T_ENDFOR : text = "endfor"; break; //NOI18N
            case ASTPHP5Symbols.T_TRAIT : text = "trait"; break; //NOI18N
            case ASTPHP5Symbols.T_CLONE : text = "clone"; break; //NOI18N
            case ASTPHP5Symbols.T_BOOLEAN_OR : text = "||"; break; //NOI18N
            case ASTPHP5Symbols.T_UNSET : text = "unset"; break; //NOI18N
            case ASTPHP5Symbols.T_INTERFACE : text = "interface"; break; //NOI18N
            case ASTPHP5Symbols.T_SWITCH : text = "switch"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_GREATER_OR_EQUAL : text = ">="; break; //NOI18N
            case ASTPHP5Symbols.T_OPEN_RECT : text = "["; break; //NOI18N
            case ASTPHP5Symbols.T_CURLY_OPEN_WITH_DOLAR : text = "{$"; break; //NOI18N
            case ASTPHP5Symbols.T_FINAL : text = "final"; break; //NOI18N
            case ASTPHP5Symbols.T_REQUIRE : text = "require"; break; //NOI18N
            case ASTPHP5Symbols.T_FILE : text = "__FILE__"; break; //NOI18N
            case ASTPHP5Symbols.T_DEC : text = "--"; break; //NOI18N
            case ASTPHP5Symbols.T_CLOSE_PARENTHESE : text = ")"; break; //NOI18N
            case ASTPHP5Symbols.T_CLASS_C : text = "__CLASS__"; break; //NOI18N
            case ASTPHP5Symbols.T_EVAL : text = "eval"; break; //NOI18N
            case ASTPHP5Symbols.T_RGREATER : text = "<"; break; //NOI18N
            case ASTPHP5Symbols.T_IS_NOT_IDENTICAL : text = "!=="; break; //NOI18N
            case ASTPHP5Symbols.T_NOT : text = "!"; break; //NOI18N
            case ASTPHP5Symbols.T_REQUIRE_ONCE : text = "require_once"; break; //NOI18N
            case ASTPHP5Symbols.T_NS_C : text = "__NAMESPACE__"; break; //NOI18N
            case ASTPHP5Symbols.T_DOLLAR_OPEN_CURLY_BRACES : text = "${"; break; //NOI18N
            case ASTPHP5Symbols.T_VAR : text = "var"; break; //NOI18N
            case ASTPHP5Symbols.T_START_HEREDOC : text = "<<<\"...\""; break; //NOI18N
            case ASTPHP5Symbols.T_ENDSWITCH : text = "endswitch"; break; //NOI18N
            case ASTPHP5Symbols.T_OBJECT_CAST : text = "(object)"; break; //NOI18N
            case ASTPHP5Symbols.T_ECHO : text = "echo"; break; //NOI18N
            case ASTPHP5Symbols.T_LINE : text = "__LINE__"; break; //NOI18N
            case ASTPHP5Symbols.T_FOR : text = "for"; break; //NOI18N
            case ASTPHP5Symbols.T_IMPLEMENTS : text = "implements"; break; //NOI18N
            case ASTPHP5Symbols.T_ARRAY_CAST : text = "(array)"; break; //NOI18N
            case ASTPHP5Symbols.T_DOLLAR : text = "$"; break; //NOI18N
            case ASTPHP5Symbols.T_TIMES : text = "*"; break; //NOI18N
            case ASTPHP5Symbols.T_DOUBLE_CAST : text = "(double)"; break; //NOI18N
            case ASTPHP5Symbols.T_BOOL_CAST : text = "(bool)"; break; //NOI18N
            case ASTPHP5Symbols.T_PRECENT : text = "%"; break; //NOI18N
            case ASTPHP5Symbols.T_LNUMBER : text = "integer"; break; //NOI18N
            case ASTPHP5Symbols.T_CURLY_OPEN : text = "{"; break; //NOI18N
            case ASTPHP5Symbols.T_DEFINE : text = "define"; break; //NOI18N
            case ASTPHP5Symbols.T_QUESTION_MARK : text = "?"; break; //NOI18N
            case ASTPHP5Symbols.T_END_NOWDOC : text = "END_NOWDOC"; break; //NOI18N
            case ASTPHP5Symbols.T_USE : text = "use"; break; //NOI18N
            case ASTPHP5Symbols.T_KOVA : text = "^"; break; //NOI18N
            case ASTPHP5Symbols.T_IF : text = "if"; break; //NOI18N
            case ASTPHP5Symbols.T_MUL_EQUAL : text = "*="; break; //NOI18N
            case ASTPHP5Symbols.T_ARRAY : text = "array"; break; //NOI18N
            case ASTPHP5Symbols.T_LGREATER : text = ">"; break; //NOI18N
            case ASTPHP5Symbols.T_SEMICOLON : text = ";"; break; //NOI18N
            case ASTPHP5Symbols.T_NEKUDOTAIM : text = ":"; break; //NOI18N
            case ASTPHP5Symbols.T_VAR_COMMENT : text = "VAR_COMMENT"; break; //NOI18N
            case ASTPHP5Symbols.T_CONCAT_EQUAL : text = ".="; break; //NOI18N
            case ASTPHP5Symbols.T_AND_EQUAL : text = "&="; break; //NOI18N
            case ASTPHP5Symbols.T_DNUMBER : text = "double"; break; //NOI18N
            case ASTPHP5Symbols.T_MINUS : text = "-"; break; //NOI18N
            case ASTPHP5Symbols.T_FOREACH : text = "foreach"; break; //NOI18N
            case ASTPHP5Symbols.T_EXIT : text = "exit"; break; //NOI18N
            case ASTPHP5Symbols.T_DECLARE : text = "declare"; break; //NOI18N
            case ASTPHP5Symbols.T_STRING_VARNAME : text = "STRING_VARNAME"; break; //NOI18N
            case ASTPHP5Symbols.T_EXTENDS : text = "extends"; break; //NOI18N
            case ASTPHP5Symbols.T_METHOD_C : text = "__METHOD__"; break; //NOI18N
            case ASTPHP5Symbols.T_INT_CAST : text = "(int)"; break; //NOI18N
            case ASTPHP5Symbols.T_ISSET : text = "isset"; break; //NOI18N
            case ASTPHP5Symbols.T_LOGICAL_AND : text = "&&"; break; //NOI18N
            case ASTPHP5Symbols.T_RETURN : text = "return"; break; //NOI18N
            case ASTPHP5Symbols.T_DEFAULT : text = "default"; break; //NOI18N
            case ASTPHP5Symbols.T_SR : text = ">>"; break; //NOI18N
            case ASTPHP5Symbols.T_EQUAL : text = "="; break; //NOI18N
            case ASTPHP5Symbols.T_SL : text = "<<"; break; //NOI18N
            case ASTPHP5Symbols.T_END_HEREDOC : text = "END_HEREDOC"; break; //NOI18N
            case ASTPHP5Symbols.T_DOUBLE_ARROW : text = "=>"; break; //NOI18N
            case ASTPHP5Symbols.T_STRING_CAST : text = "(string)"; break; //NOI18N
            case ASTPHP5Symbols.T_STRING : text = "identifier"; break; //NOI18N
            case ASTPHP5Symbols.T_PLUS_EQUAL : text = "+="; break; //NOI18N
            case ASTPHP5Symbols.T_INSTANCEOF : text = "instanceof"; break; //NOI18N
            case ASTPHP5Symbols.T_DIV_EQUAL : text = "/="; break; //NOI18N
            case ASTPHP5Symbols.T_NUM_STRING : text = "NUM_STRING"; break; //NOI18N
            case ASTPHP5Symbols.T_HALT_COMPILER : text = "__halt_compiler"; break; //NOI18N
            case ASTPHP5Symbols.T_GOTO : text = "goto"; break; //NOI18N
            default:
                //no-op
        }
        return text;
    }

    private static class FatalError extends GSFPHPError {

        @NbBundle.Messages("MSG_FatalError=Unable to parse the file")
        FatalError(Context context){
            super(Bundle.MSG_FatalError(),
                context.getSnapshot().getSource().getFileObject(),
                0, context.getSource().length(),
                Severity.ERROR, null);
        }

        @Override
        public boolean isLineError() {
            return false;
        }
    }
}
