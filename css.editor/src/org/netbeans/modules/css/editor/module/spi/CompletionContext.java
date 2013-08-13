/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.editor.module.spi;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.CodeCompletionHandler.QueryType;
import org.netbeans.modules.css.lib.api.CssParserResult;
import org.netbeans.modules.css.lib.api.CssTokenId;
import org.netbeans.modules.css.lib.api.CssTokenIdCategory;
import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.css.lib.api.NodeUtil;

/**
 * A code completion context. An instance of this class is passed to the CssModule.getCompletionProposals() method.
 * The context is basically parser result based and contains lots of useful information for
 * the completion result providers.
 *
 * @author mfukala@netbeans.org
 */
public class CompletionContext extends EditorFeatureContext {

    private final QueryType queryType;
    private final int anchorOffset, embeddedCaretOffset, embeddedAnchorOffset, activeTokenDiff;
    private final String prefix;
    private final Node activeNode;
    private final TokenSequence<CssTokenId> tokenSequence;
    private final Node activeTokenNode;
    private final int tsIndex;

    /**
     * @doto use class accessor so clients cannot instantiate this.
     */
    public CompletionContext(Node activeNode, Node activeTokeNode, CssParserResult result, 
            TokenSequence<CssTokenId> tokenSequence, int tsIndex, int activeTokenDiff, 
            QueryType queryType, int caretOffset, int anchorOffset, int embeddedCaretOffset, 
            int embeddedAnchorOffset, String prefix) {
        super(result, caretOffset);
        this.tokenSequence = tokenSequence;
        this.tsIndex = tsIndex;
        this.activeNode = activeNode;
        this.activeTokenNode = activeTokeNode;
        this.queryType = queryType;
        this.anchorOffset = anchorOffset;
        this.embeddedCaretOffset = embeddedCaretOffset;
        this.embeddedAnchorOffset = embeddedAnchorOffset;
        this.prefix = prefix;
        this.activeTokenDiff = activeTokenDiff;
    }

    /**
     * 
     * @return a TokenSequence of Css tokens created on top of the *virtual* css source.
     * The TokenSequence is positioned on a token laying at the getAnchorOffset() offset.
     * 
     * Clients using the context must reposition the token sequence back to the original state
     * before exiting!
     * @todo - ensure this automatically or at least check it
     */
    @Override
    public TokenSequence<CssTokenId> getTokenSequence() {
        return tokenSequence;
    }
    
    /**
     * @return the top most parse tree node for the getEmbeddedCaretOffset() position. 
     * The node is never of the NodeType.token type and is typically obtained by
     * getActiveTokenNode().parent().
     */
    public Node getActiveNode() {
        return activeNode;
    }
    
    /**
     * Gets the {@link CssTokenId} of the {@link Token} at the caret offset.
     * 
     * @since 1.51
     * @return the token id or null if no token can be achieved.
     */
    public CssTokenId getActiveTokenId() {
        try {
            TokenSequence<CssTokenId> ts = getTokenSequence();
            return ts.token() == null ? null : ts.token().id();
        } finally {
            restoreTokenSequence();
        }
    }
    
    /**
     * If the current token is WS, then this method scans tokens bacwards until it finds
     * a non white token. Then it finds corresponding parse tree node for the end offset
     * of the found non white token.
     * 
     * @since 1.51
     * @return the found node or root node, never null
     */
    @NonNull
    public Node getNodeForNonWhiteTokenBackward() {
        TokenSequence<CssTokenId> ts = getTokenSequence();
        restoreTokenSequence();
        try {
            for(;;) {
                Token t = ts.token();
                if(t == null) {
                    //empty file
                    return getParseTreeRoot();
                }
                if(!CssTokenIdCategory.WHITESPACES.name().toLowerCase().equals(t.id().primaryCategory())) {
                    return NodeUtil.findNonTokenNodeAtOffset(getParseTreeRoot(), ts.offset() + t.length());
                } else {
                    if(!ts.movePrevious()) {
                        break;
                    }
                }
            }
            return getParseTreeRoot();
        } finally {
            //reposition the token sequence back
            restoreTokenSequence();
        }
    }

    /**
     * If the current token is WS, then this method scans tokens bacwards until it finds
     * a non white token. 
     * 
     * @since 1.57
     * @return the non-white token id or null if there isn't any.
     */
    public CssTokenId getNonWhiteTokenIdBackward() {
        TokenSequence<CssTokenId> ts = getTokenSequence();
        restoreTokenSequence();
        try {
            for(;;) {
                Token<CssTokenId> t = ts.token();
                if(t == null) {
                    //empty file
                    return null;
                }
                if(!CssTokenIdCategory.WHITESPACES.name().toLowerCase().equals(t.id().primaryCategory())) {
                    return t.id();
                } else {
                    if(!ts.movePrevious()) {
                        break;
                    }
                }
            }
            return null;
        } finally {
            //reposition the token sequence back
            restoreTokenSequence();
        }
    }
    
    /**
     * Restores the {@link TokenSequence} obtained by {@link #getTokenSequence()} to the original state.
     * @since 1.51
     */
    public void restoreTokenSequence() {
        getTokenSequence().moveIndex(tsIndex);
        getTokenSequence().moveNext();
    }
    
    /**
     * @return the top most parse tree node of NodeType.token for the getEmbeddedCaretOffset() position
     */
    public Node getActiveTokenNode() {
        return activeTokenNode;
    }
    
    /**
     * anchor offset = caret offset - prefix length.
     * Relative to the edited document.
     * 
     */
    public int getAnchorOffset() {
        return anchorOffset;
    }

    /**
     * Same as getCaretOffset() but relative to the embedded css code.
     */
    public int getEmbeddedCaretOffset() {
        return embeddedCaretOffset;
    }

    /**
     * Same as getAnchorOffset() but relative to the embedded css code.
     */
    public int getEmbeddedAnchorOffset() {
        return embeddedAnchorOffset;
    }

    /**
     * @return computed completion prefix. Some clients may ignore this and compute
     * the prefix themselves. In such case the resulting CompletionProposal-s needs 
     * to use non-default anchor.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return QueryType of the completion query.
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * @return a diff from the getEmbeddedCaretOffset() and the token found for the position. 
     * Is result of getTokenSequence().move(getEmbeddedCaretOffset());
     */
    public int getActiveTokenDiff() {
        return activeTokenDiff;
    }
    
}
