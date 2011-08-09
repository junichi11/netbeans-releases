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
package org.netbeans.modules.css.editor.csl;

import java.util.Arrays;
import java.util.Collection;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.css.editor.module.main.DefaultProperties;
import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.util.NbBundle;

/**
 * @todo Add some support for CSS versions
 * @todo Localize !!!!!!!
 *
 * @author marek
 */
public class CssAnalyser {

    private static final String UNKNOWN_PROPERTY_BUNDLE_KEY = "unknown_property";
    private static final String UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER = "/";
    private static final String UNKNOWN_PROPERTY_ERROR_KEY = "unknown_property" + UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER;
    private static final String INVALID_PROPERTY_VALUE = "invalid_property_value";
    private static final String INVALID_CONTENT = "invalid_content";

    private static final String INVALID_CONTENT_MSG = NbBundle.getMessage(CssAnalyser.class, INVALID_CONTENT);
    
    public static List<Error> checkForErrors(final Snapshot snapshot, final Node node) {
        final ArrayList<Error> errors = new ArrayList<Error>();
//        final DefaultProperties model = DefaultProperties.instance();
//        NodeVisitor visitor = new NodeVisitor() {
//
//            @Override
//            public void visit(SimpleNode node) {
//                if (node.kind() == CssParserTreeConstants.JJTDECLARATION) {
//                    SimpleNode propertyNode = SimpleNodeUtil.getChildByType(node, CssParserTreeConstants.JJTPROPERTY);
//                    SimpleNode valueNode = SimpleNodeUtil.getChildByType(node, CssParserTreeConstants.JJTEXPR);
//
//                    if (propertyNode != null) {
//                        String propertyName = propertyNode.image().trim();
//
//                        //check non css 2.1 compatible properties and ignore them
//                        //values are not checked as well
//                        if(isNonCss21CompatibleDeclarationPropertyName(propertyName)) {
//                            return ;
//                        }
//
//                        //check for vendor specific properies - ignore them
//                        Property property = model.getProperty(propertyName);
//                        if (!CssGSFParser.containsGeneratedCode(propertyName) && !isVendorSpecificProperty(propertyName) && property == null) {
//                            //unknown property - report
//                            String msg = NbBundle.getMessage(CssAnalyser.class, UNKNOWN_PROPERTY_BUNDLE_KEY, propertyName);
//                            Error error = makeError(propertyNode.startOffset(),
//                                    propertyNode.endOffset(),
//                                    snapshot,
//                                    UNKNOWN_PROPERTY_ERROR_KEY + propertyName,
//                                    msg,
//                                    msg,
//                                    false /* not line error */,
//                                    Severity.WARNING);
//                            if(error != null) {
//                                errors.add(error);
//                            }
//                        }
//
//                        //check value
//                        if (valueNode != null && property != null) {
//                            String valueImage = valueNode.image().trim();
//                            
//                            //do not check values which contains generated code
//                            //we are no able to identify the templating semantic
//                            if (!CssGSFParser.containsGeneratedCode(valueImage)) {
//                                CssPropertyValue pv = new CssPropertyValue(property, valueImage);
//                                if (!pv.success()) {
//                                    String errorMsg = null;
//                                    if (pv instanceof CustomErrorMessageProvider) {
//                                        errorMsg = ((CustomErrorMessageProvider) pv).customErrorMessage();
//                                    }
//
//                                    //error in property 
//                                    String unexpectedToken = pv.left().get(pv.left().size() - 1);
//
//                                    if(isNonCss21CompatiblePropertyValue(unexpectedToken)) {
//                                        return ;
//                                    }
//
//                                    if (errorMsg == null) {
//                                        errorMsg = NbBundle.getMessage(CssAnalyser.class, INVALID_PROPERTY_VALUE, unexpectedToken);
//                                    }
//
//                                    Error error = makeError(valueNode.startOffset(),
//                                            valueNode.endOffset(),
//                                            snapshot,
//                                            INVALID_PROPERTY_VALUE,
//                                            errorMsg,
//                                            errorMsg,
//                                            false /* not line error */,
//                                            Severity.WARNING);
//                                    if(error != null) {
//                                        errors.add(error);
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                } else if(node.kind() == CssParserTreeConstants.JJTERROR_SKIP_TO_WHITESPACE && node.image().length() > 0) {
//                    Error error = makeError(
//                            node.startOffset(),
//                            node.endOffset(),
//                            snapshot,
//                            INVALID_CONTENT,
//                            INVALID_CONTENT_MSG,
//                            INVALID_CONTENT_MSG,
//                            true,
//                            Severity.ERROR);
//                    if(error != null) {
//                        errors.add(error);
//                    }
//                }
//            }
//        };
//        
//        if(node != null) {
//            SimpleNodeUtil.visitChildren(node, visitor);
//        }
        return errors;
    }

    private static Error makeError(int astFrom, int astTo, Snapshot snapshot, String key, String displayName, String description, boolean lineError, Severity severity) {
        assert astFrom <= astTo;

        int from = snapshot.getOriginalOffset(astFrom);
        int to = snapshot.getOriginalOffset(astTo);

        if (from == -1 || to == -1) {
            //error in virtual content, we cannot map back to the document :-(
            return null;
        }

        assert from <= to;

        return DefaultError.createDefaultError(key,
                            displayName,
                            description,
                            snapshot.getSource().getFileObject(),
                            from,
                            to,
                            lineError,
                            severity);

    }

    public static boolean isConfigurableError(String errorKey) {
        //unknown property errors can be suppressed, so far
        return isUnknownPropertyError(errorKey);
    }
    
    public static boolean isUnknownPropertyError(String errorKey) {
        return errorKey.startsWith(UNKNOWN_PROPERTY_ERROR_KEY);
    }

    public static String getUnknownPropertyName(String unknownPropertyErrorKey) {
        assert unknownPropertyErrorKey.startsWith(UNKNOWN_PROPERTY_ERROR_KEY);
        int index = unknownPropertyErrorKey.indexOf(UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER);//NOI18N
        return unknownPropertyErrorKey.substring(index + 1);
    }

    

    //this is only a temporary hack for being able to filter out the css 2.1 errors for
    //commonly used properties not defined in the specification
    private static boolean isNonCss21CompatibleDeclarationPropertyName(String propertyName) {
        return NON_CSS21_DECLARATION_PROPERTY_NAMES.contains(propertyName);
    }

    private static boolean isNonCss21CompatiblePropertyValue(String propertyValue) {
        return NON_CSS21_DECLARATION_PROPERTY_VALUES.contains(propertyValue);
    }

    private static final Collection<String> NON_CSS21_DECLARATION_PROPERTY_NAMES = Arrays.asList(
            new String[]{"opacity", "resize", "text-overflow", "text-shadow", "filter"}); //NOI18N

    private static final Collection<String> NON_CSS21_DECLARATION_PROPERTY_VALUES = Arrays.asList(
            new String[]{"expression"}); //NOI18N

}
