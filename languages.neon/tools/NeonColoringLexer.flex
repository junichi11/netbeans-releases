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

package org.netbeans.modules.languages.neon;

import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
%%

%public
%class NeonColoringLexer
%type NeonTokenId
%function nextToken
%unicode
%caseless
%char

%eofval{
        if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return NeonTokenId.NEON_UNKNOWN;
        } else {
            return null;
        }
%eofval}

%{

    private StateStack stack = new StateStack();

    private LexerInput input;

    public NeonColoringLexer(LexerRestartInfo info) {
        this.input = info.input();
        if(info.state() != null) {
            //reset state
            setState((LexerState) info.state());
        } else {
            zzState = zzLexicalState = YYINITIAL;
            stack.clear();
        }

    }

    public static final class LexerState  {
        final StateStack stack;
        /** the current state of the DFA */
        final int zzState;
        /** the current lexical state */
        final int zzLexicalState;

        LexerState(StateStack stack, int zzState, int zzLexicalState) {
            this.stack = stack;
            this.zzState = zzState;
            this.zzLexicalState = zzLexicalState;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            LexerState state = (LexerState) obj;
            return (this.stack.equals(state.stack)
                && (this.zzState == state.zzState)
                && (this.zzLexicalState == state.zzLexicalState));
        }

        @Override
        public int hashCode() {
            int hash = 11;
            hash = 31 * hash + this.zzState;
            hash = 31 * hash + this.zzLexicalState;
            if (stack != null) {
                hash = 31 * hash + this.stack.hashCode();
            }
            return hash;
        }
    }

    public LexerState getState() {
        return new LexerState(stack.createClone(), zzState, zzLexicalState);
    }

    public void setState(LexerState state) {
        this.stack.copyFrom(state.stack);
        this.zzState = state.zzState;
        this.zzLexicalState = state.zzLexicalState;
    }

    protected int getZZLexicalState() {
        return zzLexicalState;
    }

    protected void popState() {
		yybegin(stack.popStack());
	}

	protected void pushState(final int state) {
		stack.pushStack(getZZLexicalState());
		yybegin(state);
	}


 // End user code

%}

IDENTIFIER=[[:letter:]_\x7f-\xff][[:letter:][:digit:]_\x7f-\xff\.]*"!"?
KEYWORD=("true" | "TRUE" | "false" | "FALSE" | "yes" | "YES" | "no" | "NO" | "null" | "NULL")
WHITESPACE=[ \t]+
NEWLINE=("\r"|"\n"|"\r\n")
ZERO=0
DECIMAL=[\+\-]?[1-9][0-9]*
OCTAL=0[0-7]+
HEXADECIMAL=0[xX][0-9A-Fa-f]+
EXPONENT=[eE][\+\-]?[0-9]+
FLOAT_1=[0-9]+\.[0-9]+{EXPONENT}?
FLOAT_2=\.[0-9]+{EXPONENT}?
FLOAT_3=[0-9]+\.{EXPONENT}?
FLOAT_4=[0-9]+{EXPONENT}
FLOAT={FLOAT_1} | {FLOAT_2} | {FLOAT_3} | {FLOAT_4}
NUMBER={ZERO} | {DECIMAL} | {OCTAL} | {HEXADECIMAL} | {FLOAT}
LITERAL=([^#%\"':,=\[\]\{\}\(\)\<\>\t\n\r ]|"::")+
ARRAY_CLOSE_DELIM = ("]" | "}" | ")")
ARRAY_MINUS_DELIM="-"
ARRAY_ITEM_DELIM=","
D_STRING="\""([^("\r"|"\n"|"\r\n"|"\"")]|"\\\"")*"\""
S_STRING="'"([^("\r"|"\n"|"\r\n"|"'")]|"\\'")*"'"
STRING = {D_STRING} | {S_STRING}
VARIABLE="%"{LITERAL}"%"?
ARRAY_KEY=({LITERAL} | {STRING} | {NUMBER}){WHITESPACE}*(":"|"=")
ARRAY_VALUE={LITERAL} | {STRING} | {NUMBER} | {VARIABLE} | {KEYWORD}
BLOCK_HEADER={IDENTIFIER}({WHITESPACE}*"<"{WHITESPACE}*{IDENTIFIER})?{WHITESPACE}*":"{WHITESPACE}*({NEWLINE} | {COMMENT})
COMMENT="#"[^("\r"|"\n"|"\r\n")]*
BLOCK_ARRAY_SEPARATOR=":" | "="

%state ST_IN_BLOCK
%state ST_BLOCK_HEADER
%state ST_VALUED_BLOCK
%state ST_IN_INHERITED_BLOCK
%state ST_IN_RIGHT_BLOCK
%state ST_IN_ARRAY_KEY
%state ST_IN_ARRAY_VALUE
%state ST_IN_MINUS_ARRAY_VALUE
%state ST_IN_SQ_ARRAY
%state ST_IN_CU_ARRAY
%state ST_IN_PA_ARRAY
%state ST_HIGHLIGHTING_ERROR


%%
<YYINITIAL>.|{NEWLINE} {
    yypushback(yylength());
    pushState(ST_IN_BLOCK);
}

<ST_IN_BLOCK, ST_IN_INHERITED_BLOCK, ST_IN_RIGHT_BLOCK, ST_IN_ARRAY_KEY, ST_IN_ARRAY_VALUE, ST_IN_MINUS_ARRAY_VALUE, ST_IN_SQ_ARRAY, ST_IN_CU_ARRAY, ST_IN_PA_ARRAY, ST_BLOCK_HEADER, ST_VALUED_BLOCK>{WHITESPACE}+ {
    return NeonTokenId.NEON_WHITESPACE;
}

<ST_IN_BLOCK, ST_IN_INHERITED_BLOCK, ST_IN_RIGHT_BLOCK, ST_IN_ARRAY_KEY, ST_IN_ARRAY_VALUE, ST_IN_MINUS_ARRAY_VALUE, ST_IN_SQ_ARRAY, ST_IN_CU_ARRAY, ST_IN_PA_ARRAY, ST_BLOCK_HEADER, ST_VALUED_BLOCK>{COMMENT} {
    return NeonTokenId.NEON_COMMENT;
}

<ST_IN_BLOCK> {
    {BLOCK_HEADER} {
        pushState(ST_BLOCK_HEADER);
        yypushback(yylength());
    }
    {NEWLINE} {
        return NeonTokenId.NEON_WHITESPACE;
    }
    {BLOCK_ARRAY_SEPARATOR} {
        pushState(ST_IN_RIGHT_BLOCK);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
    . {
        pushState(ST_VALUED_BLOCK);
        yypushback(yylength());
    }
}

<ST_BLOCK_HEADER, ST_VALUED_BLOCK> {
    {BLOCK_ARRAY_SEPARATOR} {
        popState();
        yypushback(yylength());
    }
    "<" {
        pushState(ST_IN_INHERITED_BLOCK);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
    {ARRAY_MINUS_DELIM} / {WHITESPACE}+ {
        pushState(ST_IN_MINUS_ARRAY_VALUE);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
    {NEWLINE} {
        popState();
        yypushback(yylength());
    }
}

<ST_BLOCK_HEADER> {
    {IDENTIFIER} {
        return NeonTokenId.NEON_BLOCK;
    }
}

<ST_VALUED_BLOCK> {
    {IDENTIFIER} | {STRING} {
        return NeonTokenId.NEON_VALUED_BLOCK;
    }
}

<ST_IN_INHERITED_BLOCK> {
    {BLOCK_ARRAY_SEPARATOR} {
        popState();
        yypushback(yylength());
    }
    {IDENTIFIER} {
        return NeonTokenId.NEON_BLOCK;
    }
    {NEWLINE} {
        popState();
        return NeonTokenId.NEON_WHITESPACE;
    }
}
<ST_IN_RIGHT_BLOCK, ST_IN_ARRAY_VALUE, ST_IN_MINUS_ARRAY_VALUE> {
    {KEYWORD} {
        return NeonTokenId.NEON_KEYWORD;
    }
    {NUMBER} {
        return NeonTokenId.NEON_NUMBER;
    }
    {LITERAL} {
        return NeonTokenId.NEON_LITERAL;
    }
    {VARIABLE} {
        return NeonTokenId.NEON_VARIABLE;
    }
    {STRING} {
        return NeonTokenId.NEON_STRING;
    }
}
<ST_IN_RIGHT_BLOCK, ST_IN_ARRAY_VALUE, ST_IN_MINUS_ARRAY_VALUE> {
    "[" {
        pushState(ST_IN_SQ_ARRAY);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
    "{" {
        pushState(ST_IN_CU_ARRAY);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
    "(" {
        pushState(ST_IN_PA_ARRAY);
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_RIGHT_BLOCK, ST_IN_MINUS_ARRAY_VALUE> {
    {NEWLINE} {
        yypushback(yylength());
        popState();
    }
}

<ST_IN_SQ_ARRAY, ST_IN_CU_ARRAY, ST_IN_PA_ARRAY> {
    {ARRAY_KEY} {
        pushState(ST_IN_ARRAY_KEY);
        yypushback(yylength());
    }
    {ARRAY_VALUE} {
        pushState(ST_IN_ARRAY_VALUE);
        yypushback(yylength());
    }
    {ARRAY_ITEM_DELIM} {
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_SQ_ARRAY> {
    "]" {
        popState();
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_CU_ARRAY> {
    "}" {
        popState();
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_PA_ARRAY> {
    ")" {
        popState();
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_ARRAY_KEY> {
    {NUMBER} {
        return NeonTokenId.NEON_NUMBER;
    }
    {LITERAL} {
        return NeonTokenId.NEON_LITERAL;
    }
    {STRING} {
        return NeonTokenId.NEON_STRING;
    }
    {BLOCK_ARRAY_SEPARATOR} {
        popState();
        return NeonTokenId.NEON_INTERPUNCTION;
    }
}

<ST_IN_ARRAY_VALUE> {
    {ARRAY_CLOSE_DELIM} | {ARRAY_ITEM_DELIM} {
        popState();
        yypushback(yylength());
    }
}

/* ============================================
   Stay in this state until we find a whitespace.
   After we find a whitespace we go the the prev state and try again from the next token.
   ============================================ */
<ST_HIGHLIGHTING_ERROR> {
	{WHITESPACE} {
        popState();
        return NeonTokenId.NEON_WHITESPACE;
    }
    . | {NEWLINE} {
        return NeonTokenId.NEON_UNKNOWN;
    }
}

/* ============================================
   This rule must be the last in the section!!
   it should contain all the states.
   ============================================ */
<YYINITIAL, ST_IN_BLOCK, ST_IN_INHERITED_BLOCK, ST_IN_RIGHT_BLOCK, ST_IN_ARRAY_KEY, ST_IN_ARRAY_VALUE, ST_IN_MINUS_ARRAY_VALUE, ST_IN_SQ_ARRAY, ST_IN_CU_ARRAY, ST_IN_PA_ARRAY, ST_BLOCK_HEADER, ST_VALUED_BLOCK> {
    . {
        yypushback(yylength());
        pushState(ST_HIGHLIGHTING_ERROR);
    }
    {NEWLINE} {
        yypushback(yylength());
        pushState(ST_HIGHLIGHTING_ERROR);
    }
}
