/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.apt.impl.structure;

import antlr.Token;
import antlr.TokenStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.cnd.apt.impl.support.APTExpandedStream;
import org.netbeans.modules.cnd.apt.support.APTTokenTypes;
import org.netbeans.modules.cnd.apt.structure.APT;
import org.netbeans.modules.cnd.apt.support.APTMacroCallback;
import org.netbeans.modules.cnd.apt.support.APTToken;
import org.netbeans.modules.cnd.apt.support.APTTokenAbstact;
import org.netbeans.modules.cnd.apt.utils.APTUtils;
import org.netbeans.modules.cnd.apt.utils.ListBasedTokenStream;
import org.netbeans.modules.cnd.apt.utils.TokenBasedTokenStream;

/**
 * #include and #include_next base implementation
 * @author Vladimir Voskresensky
 */
public abstract class APTIncludeBaseNode extends APTTokenBasedNode
        implements Serializable {
    private static final long serialVersionUID = -2311241687965334550L;
    // support pp-tokens as include stream
    // expanded later based on macro map;
    // we support INCLUDE_STRING, SYS_INLUDE_STRING
    // and macro expansion like #include MACRO_EXPRESSION
    private Token includeFileToken = EMPTY_INCLUDE;
    
    private int endOffset = 0;
    /** Copy constructor */
    /**package*/ APTIncludeBaseNode(APTIncludeBaseNode orig) {
        super(orig);
        this.includeFileToken = orig.includeFileToken;
    }
    
    /** Constructor for serialization */
    protected APTIncludeBaseNode() {
    }
    
    /**
     * Creates a new instance of APTIncludeBaseNode
     */
    protected APTIncludeBaseNode(Token token) {
        super(token);
    }
    
    public int getEndOffset() {
        return endOffset;
    }
    
    public APT getFirstChild() {
        return null;
    }
    
    public void setFirstChild(APT child) {
        // do nothing
        assert (false) : "include doesn't support children"; // NOI18N
    }
    
    public boolean accept(Token token) {
        int ttype = token.getType();
        if (APTUtils.isEndDirectiveToken(ttype)) {
            endOffset = ((APTToken)token).getOffset();
            return false;
        }
        // eat all till END_PREPROC_DIRECTIVE
        switch (token.getType()) {
            case APTTokenTypes.INCLUDE_STRING:
            case APTTokenTypes.SYS_INCLUDE_STRING:
                if (includeFileToken == EMPTY_INCLUDE) {
                    this.includeFileToken = token;
                } else {
                    // append new token
                    ((MultiTokenInclude)includeFileToken).addToken(token);                    
                }
                break;
            case APTTokenTypes.COMMENT:
            case APTTokenTypes.CPP_COMMENT:
                // just skip comments, they are valid
                break;
            default:
                // token stream of macro expressions
                if (includeFileToken == EMPTY_INCLUDE) {
                    // the first token of expression
                    includeFileToken = new MultiTokenInclude(token);
                } else {
                    // not the first token
                    if (isSimpleIncludeToken()) {
                        // remember old token
                        includeFileToken = new MultiTokenInclude(includeFileToken);
                    }
                    // append new token
                    ((MultiTokenInclude)includeFileToken).addToken(token);
                }
        }
        return true;
    }
    
    public String getText() {
        String ret = super.getText();
        if (isSimpleIncludeToken()) {
            ret += " INCLUDE{" + (isSystem(null) ? "<S> ":"<U> ") + getInclude()+"}"; // NOI18N
        } else if (includeFileToken == EMPTY_INCLUDE) {
            ret += " INCLUDE{ **EMPTY** }"; // NOI18N
        } else {
            ret += " INCLUDE{ <M> " + getInclude()+"}"; // NOI18N
        }
        return ret;
    }
    ////////////////////////////////////////////////////////////////////////////
    // impl of interfaces APTInclude and APTIncludeNext
    
    public TokenStream getInclude() {
        if (isSimpleIncludeToken()) {
            return new TokenBasedTokenStream(includeFileToken);
        } else if (includeFileToken == EMPTY_INCLUDE) {
            return APTUtils.EMPTY_STREAM;
        } else {
            return new ListBasedTokenStream(((MultiTokenInclude)includeFileToken).getTokenList());
        }
    }
    
    public String getFileName(APTMacroCallback callback) {
        String file = getIncludeString(callback);
        int len = file.length();
        String out = "";//NOI18N
        if (len > 1) {
            if (file.startsWith("<") || file.startsWith("\"")) { // NOI18N
                out = file.substring(1, len - 1);
            } else {
                out = file;
            }
        }
        return out;
    }
    
    public boolean isSystem(APTMacroCallback callback) {
        String file = getIncludeString(callback);
        return file.length() > 0 ? file.charAt(0) == '<' : false; // NOI18N
    }
    
    private String getIncludeString(APTMacroCallback callback) {
        assert (includeFileToken != null);
        String file;
        if (!isSimpleIncludeToken()) {
            file = stringize(((MultiTokenInclude)includeFileToken).getTokenList(), callback);
        } else {
            file = includeFileToken.getText();
        }
        return file;
    }
    
    private boolean isSimpleIncludeToken() {
        assert (includeFileToken != null);
        return includeFileToken.getType() == APTTokenTypes.INCLUDE_STRING ||
                includeFileToken.getType() == APTTokenTypes.SYS_INCLUDE_STRING;
    }
    
    private static final MultiTokenInclude EMPTY_INCLUDE = new MultiTokenInclude(null);
    
    //TODO: what about Serializable
    private static class MultiTokenInclude extends APTTokenAbstact {
        private List/*<Token>*/ origTokens;
        
        public MultiTokenInclude(Token token) {
            if (token != null) {
                origTokens = new ArrayList(1);
                origTokens.add(token);
            } else {
                origTokens = new ArrayList(0);
            }
        }
        
        public void addToken(Token token) {
            assert origTokens != null;
            origTokens.add(token);
        }
        
        public String getText() {
            if (origTokens.size() > 0) {
                return stringize(getTokenList(), null);
            } else {
                return "{no include information}"; // NOI18N
            }
        }
        
        public List getTokenList() {
            return origTokens;
        }
    };
    
    private static String stringize(List/*<Token>*/ tokens, APTMacroCallback callback) {
        TokenStream expanded;
        if (callback != null) {
            expanded = new APTExpandedStream(new ListBasedTokenStream(tokens), callback);
        } else {
            expanded = new ListBasedTokenStream(tokens);
        }
        return APTUtils.stringize(expanded);
    }
    
}
