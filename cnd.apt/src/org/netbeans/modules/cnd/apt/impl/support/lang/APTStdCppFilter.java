/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.apt.impl.support.lang;

import org.netbeans.modules.cnd.apt.support.APTTokenTypes;

/**
 * filter for Std C++ language
 * @author Vladimir Voskresensky
 */
public class APTStdCppFilter extends APTBaseLanguageFilter {
    
    public APTStdCppFilter() {
        initialize();
    }
    
    private void initialize() {
        // TODO clean up!
        filter("operator", APTTokenTypes.LITERAL_OPERATOR); // NOI18N
        filter("alignof", APTTokenTypes.LITERAL_alignof); // NOI18N
        filter("__alignof__", APTTokenTypes.LITERAL___alignof__); // NOI18N
        filter("typeof", APTTokenTypes.LITERAL_typeof); // NOI18N
        filter("__typeof", APTTokenTypes.LITERAL___typeof); // NOI18N
        filter("__typeof__", APTTokenTypes.LITERAL___typeof__); // NOI18N
        filter("template", APTTokenTypes.LITERAL_template); // NOI18N
        filter("typedef", APTTokenTypes.LITERAL_typedef); // NOI18N
        filter("enum", APTTokenTypes.LITERAL_enum); // NOI18N
        filter("namespace", APTTokenTypes.LITERAL_namespace); // NOI18N
        filter("extern", APTTokenTypes.LITERAL_extern); // NOI18N
        filter("inline", APTTokenTypes.LITERAL_inline); // NOI18N
        filter("_inline", APTTokenTypes.LITERAL__inline); // NOI18N
        filter("__inline", APTTokenTypes.LITERAL___inline); // NOI18N
        filter("__inline__", APTTokenTypes.LITERAL___inline__); // NOI18N
        filter("virtual", APTTokenTypes.LITERAL_virtual); // NOI18N
        filter("explicit", APTTokenTypes.LITERAL_explicit); // NOI18N
        filter("friend", APTTokenTypes.LITERAL_friend); // NOI18N
        filter("_stdcall", APTTokenTypes.LITERAL__stdcall); // NOI18N
        filter("__stdcall", APTTokenTypes.LITERAL___stdcall); // NOI18N
        filter("typename", APTTokenTypes.LITERAL_typename); // NOI18N
        filter("auto", APTTokenTypes.LITERAL_auto); // NOI18N
        filter("register", APTTokenTypes.LITERAL_register); // NOI18N
        filter("static", APTTokenTypes.LITERAL_static); // NOI18N
        filter("mutable", APTTokenTypes.LITERAL_mutable); // NOI18N
        filter("const", APTTokenTypes.LITERAL_const); // NOI18N
        filter("__const", APTTokenTypes.LITERAL___const); // NOI18N
        filter("const_cast", APTTokenTypes.LITERAL_const_cast); // NOI18N
        filter("volatile", APTTokenTypes.LITERAL_volatile); // NOI18N
        filter("__volatile__", APTTokenTypes.LITERAL___volatile__); // NOI18N
        filter("char", APTTokenTypes.LITERAL_char); // NOI18N
        filter("wchar_t", APTTokenTypes.LITERAL_wchar_t); // NOI18N
        filter("bool", APTTokenTypes.LITERAL_bool); // NOI18N
        filter("short", APTTokenTypes.LITERAL_short); // NOI18N
        filter("int", APTTokenTypes.LITERAL_int); // NOI18N
        filter("long", APTTokenTypes.LITERAL_long); // NOI18N
        filter("signed", APTTokenTypes.LITERAL_signed); // NOI18N
        filter("__signed__", APTTokenTypes.LITERAL___signed__); // NOI18N
        filter("unsigned", APTTokenTypes.LITERAL_unsigned); // NOI18N
        filter("__unsigned__", APTTokenTypes.LITERAL___unsigned__); // NOI18N
        filter("float", APTTokenTypes.LITERAL_float); // NOI18N
        filter("double", APTTokenTypes.LITERAL_double); // NOI18N
        filter("void", APTTokenTypes.LITERAL_void); // NOI18N
        filter("_declspec", APTTokenTypes.LITERAL__declspec); // NOI18N
        filter("__declspec", APTTokenTypes.LITERAL___declspec); // NOI18N
        filter("class", APTTokenTypes.LITERAL_class); // NOI18N
        filter("struct", APTTokenTypes.LITERAL_struct); // NOI18N
        filter("union", APTTokenTypes.LITERAL_union); // NOI18N        
        filter("this", APTTokenTypes.LITERAL_this); // NOI18N
        filter("true", APTTokenTypes.LITERAL_true); // NOI18N
        filter("false", APTTokenTypes.LITERAL_false); // NOI18N
        filter("public", APTTokenTypes.LITERAL_public); // NOI18N
        filter("protected", APTTokenTypes.LITERAL_protected); // NOI18N
        filter("private", APTTokenTypes.LITERAL_private); // NOI18N
        filter("throw", APTTokenTypes.LITERAL_throw); // NOI18N
        filter("case", APTTokenTypes.LITERAL_case); // NOI18N
        filter("default", APTTokenTypes.LITERAL_default); // NOI18N
        filter("if", APTTokenTypes.LITERAL_if); // NOI18N
        filter("else", APTTokenTypes.LITERAL_else); // NOI18N
        filter("switch", APTTokenTypes.LITERAL_switch); // NOI18N
        filter("while", APTTokenTypes.LITERAL_while); // NOI18N
        filter("do", APTTokenTypes.LITERAL_do); // NOI18N
        filter("for", APTTokenTypes.LITERAL_for); // NOI18N
        filter("goto", APTTokenTypes.LITERAL_goto); // NOI18N
        filter("continue", APTTokenTypes.LITERAL_continue); // NOI18N
        filter("break", APTTokenTypes.LITERAL_break); // NOI18N
        filter("return", APTTokenTypes.LITERAL_return); // NOI18N
        filter("try", APTTokenTypes.LITERAL_try); // NOI18N
        filter("catch", APTTokenTypes.LITERAL_catch); // NOI18N
        filter("using", APTTokenTypes.LITERAL_using); // NOI18N
        filter("asm", APTTokenTypes.LITERAL_asm); // NOI18N
        filter("_asm", APTTokenTypes.LITERAL__asm); // NOI18N
        filter("__asm", APTTokenTypes.LITERAL___asm); // NOI18N
        filter("__asm__", APTTokenTypes.LITERAL___asm__); // NOI18N
        filter("sizeof", APTTokenTypes.LITERAL_sizeof); // NOI18N
        filter("dynamic_cast", APTTokenTypes.LITERAL_dynamic_cast); // NOI18N
        filter("static_cast", APTTokenTypes.LITERAL_static_cast); // NOI18N
        filter("reinterpret_cast", APTTokenTypes.LITERAL_reinterpret_cast); // NOI18N
        filter("new", APTTokenTypes.LITERAL_new); // NOI18N
        filter("_cdecl", APTTokenTypes.LITERAL__cdecl); // NOI18N
        filter("__cdecl", APTTokenTypes.LITERAL___cdecl); // NOI18N
        filter("_near", APTTokenTypes.LITERAL__near); // NOI18N
        filter("__near", APTTokenTypes.LITERAL___near); // NOI18N
        filter("_far", APTTokenTypes.LITERAL__far); // NOI18N
        filter("__far", APTTokenTypes.LITERAL___far); // NOI18N
        filter("__interrupt", APTTokenTypes.LITERAL___interrupt); // NOI18N
        filter("pascal", APTTokenTypes.LITERAL_pascal); // NOI18N
        filter("_pascal", APTTokenTypes.LITERAL__pascal); // NOI18N
        filter("__pascal", APTTokenTypes.LITERAL___pascal); // NOI18N
        filter("delete", APTTokenTypes.LITERAL_delete); // NOI18N
        filter("_int64", APTTokenTypes.LITERAL__int64); // NOI18N
        filter("__int64", APTTokenTypes.LITERAL___int64); // NOI18N
        filter("__w64", APTTokenTypes.LITERAL___w64); // NOI18N
        filter("__extension__", APTTokenTypes.LITERAL___extension__); // NOI18N
        filter("__attribute__", APTTokenTypes.LITERAL___attribute__); // NOI18N
        filter("__restrict", APTTokenTypes.LITERAL___restrict); // NOI18N
        filter("__complex__", APTTokenTypes.LITERAL___complex__); // NOI18N
        filter("__imag__", APTTokenTypes.LITERAL___imag); // NOI18N
        filter("__real__", APTTokenTypes.LITERAL___real); // NOI18N      
        filter("export", APTTokenTypes.LITERAL_export); // NOI18N            
    }
    
}
