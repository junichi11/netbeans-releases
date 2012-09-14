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
package org.netbeans.modules.javascript2.editor.hints;

import com.oracle.nashorn.ir.BinaryNode;
import com.oracle.nashorn.ir.Block;
import com.oracle.nashorn.ir.DoWhileNode;
import com.oracle.nashorn.ir.ExecuteNode;
import com.oracle.nashorn.ir.ForNode;
import com.oracle.nashorn.ir.FunctionNode;
import com.oracle.nashorn.ir.IfNode;
import com.oracle.nashorn.ir.LiteralNode;
import com.oracle.nashorn.ir.Node;
import com.oracle.nashorn.ir.ObjectNode;
import com.oracle.nashorn.ir.VarNode;
import com.oracle.nashorn.ir.WhileNode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Hint;
import org.netbeans.modules.csl.api.HintsProvider;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.Rule;
import org.netbeans.modules.javascript2.editor.hints.JsHintsProvider.JsRuleContext;
import org.netbeans.modules.javascript2.editor.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.lexer.LexUtilities;
import org.netbeans.modules.javascript2.editor.model.impl.ModelUtils;
import org.netbeans.modules.javascript2.editor.model.impl.PathNodeVisitor;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Pisl
 */
public class JsConventionRule extends JsAstRule {
    
    @Override
    void computeHints(JsRuleContext context, List<Hint> hints, HintsProvider.HintsManager manager) {
        Map<?, List<? extends AstRule>> allHints = manager.getHints();
        List<? extends AstRule> conventionHints = allHints.get(BetterConditionHint.JSCONVENTION_OPTION_HINTS);
        Rule betterConditionRule = null;
        Rule missingSemicolon = null;
        Rule duplicatePropertyName = null;
        Rule assignmentInCondition = null;
        Rule objectTrailingComma = null;
        Rule arrayTrailingComma = null;
        if (conventionHints != null) {
            for (AstRule astRule : conventionHints) {
                if (manager.isEnabled(astRule)) {
                    if (astRule instanceof BetterConditionHint) {
                        betterConditionRule = astRule;
                    } else if (astRule instanceof MissingSemicolonHint) {
                        missingSemicolon = astRule;
                    } else if (astRule instanceof DuplicatePropertyName) {
                        duplicatePropertyName = astRule;
                    } else if (astRule instanceof AssignmentInCondition) {
                        assignmentInCondition = astRule;
                    } else if (astRule instanceof ObjectTrailingComma) {
                        objectTrailingComma = astRule;
                    } else if (astRule instanceof ArrayTrailingComma) {
                        arrayTrailingComma = astRule;
                    }
                }
            }
        }
        ConventionVisitor conventionVisitor = new ConventionVisitor(this,
                betterConditionRule, missingSemicolon, duplicatePropertyName,
                assignmentInCondition, objectTrailingComma, arrayTrailingComma);
        conventionVisitor.process(context, hints);
    }

    @Override
    public Set<?> getKinds() {
        return Collections.singleton(JsAstRule.JS_OTHER_HINTS);
    }

    @Override
    public String getId() {
        return "jsconvention.hint"; //NOI18N
    }

    @Override
    @NbBundle.Messages("JsConventionHintDesc=JavaScript Code Convention Hint")
    public String getDescription() {
        return Bundle.JsConventionHintDesc();
    }

    @Override
    @NbBundle.Messages("JsConventionHintDisplayName=JavaScript Code Convention")
    public String getDisplayName() {
        return Bundle.JsConventionHintDisplayName();
    }

    private static class ConventionVisitor extends PathNodeVisitor {

        private List<Hint> hints;
        private JsRuleContext context;
        private final Rule rule;
        private final Rule betterConditionRule;
        private final Rule missingSemicolon;
        private final Rule duplicatePropertyName;
        private final Rule assignmentInCondition;
        private final Rule objectTrailingComma;
        private final Rule arrayTrailingComma;

        public ConventionVisitor(Rule rule, Rule betterCondition, Rule missingSemicolon, 
                Rule duplicatePropertyName, Rule assignmentInCondition,
                Rule objectTrailingComma, Rule arrayTrailingComma) {
            this.rule = rule;
            this.betterConditionRule = betterCondition;
            this.missingSemicolon = missingSemicolon;
            this.duplicatePropertyName = duplicatePropertyName;
            this.assignmentInCondition = assignmentInCondition;
            this.objectTrailingComma = objectTrailingComma;
            this.arrayTrailingComma = arrayTrailingComma;
        }
        
        @NbBundle.Messages({"# {0} - expected char or string",
            "# {1} - usually text, where is expected the first parameter",
            "ExpectedInstead=Expected \"{0}\" and instead saw \"{1}\"."})
        public void process(JsRuleContext context, List<Hint> hints) {
            this.hints = hints;
            this.context = context;
            FunctionNode root = context.getJsParserResult().getRoot();
            if (root != null) {
                context.getJsParserResult().getRoot().accept(this);
            }
        }
        
        @NbBundle.Messages({"# {0} - char where is expected the semicolon",
            "MissingSemicolon=Expected semicolon ; after \"{0}\"."})
        private void checkSemicolon(int offset) {
            if(missingSemicolon == null) {
                return;
            }
            int fileOffset = context.parserResult.getSnapshot().getOriginalOffset(offset);
            if (fileOffset == -1) {
                return;
            }
            TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(context.parserResult.getSnapshot(), offset);
            if (ts == null) {
                return;
            }
            ts.move(offset);
            if(ts.movePrevious() && ts.moveNext()) {
                JsTokenId id = ts.token().id();
                if(id == JsTokenId.STRING_END && ts.moveNext()) {
                    id = ts.token().id();
                }
                if ((id == JsTokenId.EOL || id == JsTokenId.LINE_COMMENT) && ts.movePrevious()) {
                    id = ts.token().id();
                }
                if (id != JsTokenId.OPERATOR_SEMICOLON && id != JsTokenId.OPERATOR_COMMA) {
                    id = LexUtilities.findPrevious(ts, Arrays.asList(JsTokenId.WHITESPACE)).id();
                    if (id != JsTokenId.OPERATOR_SEMICOLON && id != JsTokenId.OPERATOR_COMMA) {
                        // check again whether there is not semicolon
                        fileOffset = context.parserResult.getSnapshot().getOriginalOffset(ts.offset());
                        if (fileOffset >= 0) {
                            hints.add(new Hint(missingSemicolon, Bundle.MissingSemicolon(ts.token().text().toString()),
                                    context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                                    new OffsetRange(fileOffset, fileOffset + ts.token().length()), null, 500));
                        }
                    }
                }
            }
        }

        @NbBundle.Messages("AssignmentCondition=Expected a conditional expression and instead saw an assignment.")
        private void checkCondition(Node condition) {
            if (betterConditionRule == null && assignmentInCondition == null) {
                return;
            }
            if(condition instanceof BinaryNode) {
                BinaryNode binaryNode = (BinaryNode)condition;
                if (assignmentInCondition != null && binaryNode.isAssignment()) {
                    
                    hints.add(new Hint(assignmentInCondition, Bundle.AssignmentCondition(), 
                            context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                            ModelUtils.documentOffsetRange(context.getJsParserResult(), condition.getStart(), condition.getFinish()), null, 500));
                } else {
                    if (betterConditionRule == null) {
                        return;
                    }
                    String message = null;
                    switch(binaryNode.tokenType()) {
                        case EQ:
                            message = Bundle.ExpectedInstead("===", "=="); //NOI18N
                            break;
                        case NE:
                            message = Bundle.ExpectedInstead("!==", "!="); //NOI18N
                            break;
                    }
                    if (message != null) {
                        hints.add(new Hint(betterConditionRule, message, 
                            context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                            ModelUtils.documentOffsetRange(context.getJsParserResult(), condition.getStart(), condition.getFinish()), null, 500));
                    }
                }
                if (binaryNode.lhs() instanceof BinaryNode) {
                    checkCondition(binaryNode.lhs());
                }
                if (binaryNode.rhs() instanceof BinaryNode) {
                    checkCondition(binaryNode.rhs());
                }
            }
        }

        private enum State  { BEFORE_COLON, AFTER_COLON, AFTER_CURLY, AFTER_PAREN, AFTER_BRACKET};
        @NbBundle.Messages({"# {0} - name of the duplicated property",
            "DuplicateName=Duplicate name of property \"{0}\"."})
        private void checkDuplicateLabels(ObjectNode objectNode) {
            if (duplicatePropertyName == null) {
                return;
            }
            int startOffset = context.parserResult.getSnapshot().getOriginalOffset(objectNode.getStart());
            int endOffset = context.parserResult.getSnapshot().getOriginalOffset(objectNode.getFinish());
            if (startOffset == -1 || endOffset == -1) {
                return;
            }
            TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(context.parserResult.getSnapshot(), objectNode.getStart());
            if (ts == null) {
                return;
            }
            ts.move(objectNode.getStart());
            State state = State.BEFORE_COLON;
            int curlyBalance = 0;
            int parenBalance = 0;
            int bracketBalance = 0;
            if (ts.movePrevious() && ts.moveNext()) {
                HashSet<String> names = new HashSet<String>();
                while (ts.moveNext() && ts.offset() < objectNode.getFinish()) {
                    JsTokenId id = ts.token().id();
                    switch (state) {
                        case BEFORE_COLON:
                            if (id == JsTokenId.IDENTIFIER || id == JsTokenId.STRING) {
                                if (!names.add(ts.token().text().toString())) {
                                    int docOffset = context.parserResult.getSnapshot().getOriginalOffset(ts.offset());
                                    if (docOffset >= 0) {
                                        hints.add(new Hint(duplicatePropertyName, Bundle.DuplicateName(ts.token().text().toString()),
                                                context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                                                new OffsetRange(docOffset, docOffset + ts.token().length()), null, 500));
                                    }
                                }
                            } else if (id == JsTokenId.OPERATOR_COLON) {
                                state = State.AFTER_COLON;
                            }
                            break;
                        case AFTER_COLON:
                            if (id == JsTokenId.OPERATOR_COMMA) {
                                state = State.BEFORE_COLON;
                            } else if (id == JsTokenId.BRACKET_LEFT_CURLY) {
                                state = State.AFTER_CURLY;
                            } else if (id == JsTokenId.BRACKET_LEFT_PAREN) {
                                state = State.AFTER_PAREN;
                            } else if (id == JsTokenId.BRACKET_LEFT_BRACKET) {
                                state = State.AFTER_BRACKET;
                            }
                            break;
                        case AFTER_CURLY:
                            if (id == JsTokenId.BRACKET_LEFT_CURLY) {
                                curlyBalance++;
                            } else if (id == JsTokenId.BRACKET_RIGHT_CURLY) {
                                if (curlyBalance == 0) {
                                    state = State.AFTER_COLON;
                                } else {
                                    curlyBalance--;
                                }
                            }
                            break;
                        case AFTER_PAREN :
                            if (id == JsTokenId.BRACKET_LEFT_PAREN) {
                                parenBalance++;
                            } else if (id == JsTokenId.BRACKET_RIGHT_PAREN) {
                                if (parenBalance == 0) {
                                    state = State.AFTER_COLON;
                                } else {
                                    parenBalance--;
                                }
                            }
                            break;
                       case AFTER_BRACKET :
                            if (id == JsTokenId.BRACKET_LEFT_BRACKET) {
                                bracketBalance++;
                            } else if (id == JsTokenId.BRACKET_RIGHT_BRACKET) {
                                if (bracketBalance == 0) {
                                    state = State.AFTER_COLON;
                                } else {
                                    bracketBalance--;
                                }
                            }
                            break;
                    }
                }
            }
        }   

        @Override
        public Node enter(DoWhileNode doWhileNode) {
            checkCondition(doWhileNode.getTest());
            return super.enter(doWhileNode);
        }

        @Override
        public Node enter(ExecuteNode executeNode) {
            if (!(executeNode.getExpression() instanceof Block)) {
                checkSemicolon(executeNode.getFinish());
            }
            return super.enter(executeNode);
        }

        @Override
        public Node enter(ForNode forNode) {
            checkCondition(forNode.getTest());
            return super.enter(forNode);
        }

        @Override
        public Node enter(IfNode ifNode) {
            checkCondition(ifNode.getTest());
            return super.enter(ifNode);
        }

        @Override
        @NbBundle.Messages({"# {0} - the eunexpected token",
            "UnexpectedObjectTrailing=Unexpected \"{0}\"."})
        public Node enter(ObjectNode objectNode) {
            checkDuplicateLabels(objectNode);
            if (objectTrailingComma != null) {
                int offset = context.parserResult.getSnapshot().getOriginalOffset(objectNode.getFinish());
                if (offset > -1) {
                    TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(
                            context.parserResult.getSnapshot(), objectNode.getFinish());
                    if (ts == null) {
                        return super.enter(objectNode);
                    }
                    ts.move(objectNode.getFinish());
                    if (ts.movePrevious() && ts.moveNext() && ts.movePrevious()) {
                        LexUtilities.findPrevious(ts, Arrays.asList(
                                JsTokenId.EOL, JsTokenId.WHITESPACE,
                                JsTokenId.BRACKET_RIGHT_CURLY, JsTokenId.LINE_COMMENT,
                                JsTokenId.BLOCK_COMMENT, JsTokenId.DOC_COMMENT));
                        if (ts.token().id() == JsTokenId.OPERATOR_COMMA) {
                            offset = context.parserResult.getSnapshot().getOriginalOffset(ts.offset());
                            if (offset >= 0) {
                                hints.add(new Hint(objectTrailingComma, Bundle.UnexpectedObjectTrailing(ts.token().text().toString()),
                                        context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                                        new OffsetRange(ts.offset(), ts.offset() + ts.token().length()), null, 500));
                            }
                        }
                    }
                }
            }
            return super.enter(objectNode);
        }

        @Override
        @NbBundle.Messages({"# {0} - the eunexpected token",
            "UnexpectedArrayTrailing=Unexpected \"{0}\"."})
        public Node enter(LiteralNode literalNode) {
            if (arrayTrailingComma != null) {
                if (literalNode.getValue() instanceof Node[]) {
                    int offset = context.parserResult.getSnapshot().getOriginalOffset(literalNode.getFinish());
                    if (offset > -1) {
                        TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(
                                context.parserResult.getSnapshot(), literalNode.getFinish());
                        if (ts == null) {
                            return super.enter(literalNode);
                        }
                        ts.move(literalNode.getFinish());
                        if (ts.movePrevious() && ts.moveNext() && ts.movePrevious()) {
                            LexUtilities.findPrevious(ts, Arrays.asList(
                                    JsTokenId.EOL, JsTokenId.WHITESPACE,
                                    JsTokenId.BRACKET_RIGHT_BRACKET, JsTokenId.LINE_COMMENT,
                                    JsTokenId.BLOCK_COMMENT, JsTokenId.DOC_COMMENT));
                            if (ts.token().id() == JsTokenId.OPERATOR_COMMA) {
                                offset = context.parserResult.getSnapshot().getOriginalOffset(ts.offset());
                                if (offset >= 0) {
                                    hints.add(new Hint(arrayTrailingComma, Bundle.UnexpectedArrayTrailing(ts.token().text().toString()),
                                            context.getJsParserResult().getSnapshot().getSource().getFileObject(),
                                            new OffsetRange(ts.offset(), ts.offset() + ts.token().length()), null, 500));
                                }
                            }
                        }
                    }
                }
            }
            return super.enter(literalNode);
        }

        @Override
        public Node enter(VarNode varNode) {
            boolean check = true;
            Node previous = getPath().get(getPath().size() - 1);
            if (previous instanceof Block) {
                Block block = (Block) previous;
                if (block.getStatements().size() == 2 && block.getStatements().get(1) instanceof ForNode) {
                    check = false;
                }
            } else if (previous instanceof ForNode) {
                check = false;
            }
            if (check) {
                checkSemicolon(varNode.getFinish());
            }
            return super.enter(varNode);
        }

        @Override
        public Node enter(WhileNode whileNode) {
            checkCondition(whileNode.getTest());
            return super.enter(whileNode);
        }
    }
}
