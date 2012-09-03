/* The following code was generated by JFlex 1.4.3 on 3.9.12 13:27 */

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

package org.netbeans.modules.php.twig.editor.lexer;

import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * This class is a scanner generated by
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 3.9.12 13:27 from the specification file
 * <tt>/home/warden/NetBeansProjects/web-main-new/php.twig/tools/TwigColoringLexer.flex</tt>
 */
public class TwigColoringLexer {
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

  /** This character denotes the end of file */
  public static final int YYEOF = LexerInput.EOF;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int ST_COMMENT = 8;
  public static final int ST_HIGHLIGHTING_ERROR = 16;
  public static final int ST_INTERPOLATION = 14;
  public static final int ST_D_STRING = 10;
  public static final int YYINITIAL = 0;
  public static final int ST_BLOCK_START = 4;
  public static final int ST_BLOCK = 2;
  public static final int ST_S_STRING = 12;
  public static final int ST_VAR = 6;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7,
     8, 8
  };

  /**
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED =
    "\11\0\1\1\1\1\2\0\1\1\22\0\1\1\1\21\1\32\1\35"+
    "\1\0\1\3\1\0\1\34\1\27\1\27\1\24\1\13\1\27\1\14"+
    "\1\26\1\25\12\30\1\27\1\0\1\22\1\7\1\22\1\27\1\0"+
    "\1\5\1\16\1\40\1\20\1\37\1\45\1\31\1\46\1\23\1\31"+
    "\1\43\1\42\1\44\1\10\1\11\1\41\1\31\1\15\1\6\1\12"+
    "\1\36\1\31\1\47\1\17\2\31\1\27\1\33\1\27\1\0\1\31"+
    "\1\0\1\5\1\16\1\40\1\20\1\37\1\45\1\31\1\46\1\23"+
    "\1\31\1\43\1\42\1\44\1\10\1\11\1\41\1\31\1\15\1\6"+
    "\1\12\1\36\1\31\1\47\1\17\2\31\1\2\1\27\1\4\1\13"+
    "\201\31\uff00\0";

  /**
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\11\0\1\1\1\2\1\1\1\3\1\4\2\5\1\4"+
    "\2\5\1\4\1\5\1\1\1\5\2\4\1\3\1\6"+
    "\2\1\7\7\1\1\4\7\1\3\1\1\1\2\2\1"+
    "\1\2\1\10\3\1\1\2\1\1\1\10\1\1\1\11"+
    "\1\12\1\1\1\13\1\14\1\15\1\16\1\17\1\20"+
    "\1\4\2\5\3\0\1\21\3\0\1\22\6\7\1\23"+
    "\14\7\1\24\2\0\1\25\1\0\1\10\5\0\1\10"+
    "\3\0\1\10\3\0\1\26\3\0\1\6\1\21\1\22"+
    "\16\7\1\27\2\0\1\27\3\0\13\7\1\23\3\7"+
    "\3\0\1\10\1\27\3\0\1\10\1\27\14\7";

  private static int [] zzUnpackAction() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\50\0\120\0\170\0\240\0\310\0\360\0\u0118"+
    "\0\u0140\0\u0168\0\u0190\0\u01b8\0\u0168\0\u01e0\0\u0208\0\u0230"+
    "\0\u0258\0\u0280\0\u02a8\0\u0168\0\u02d0\0\u0258\0\u02f8\0\u0320"+
    "\0\u0348\0\u0370\0\u0398\0\u03c0\0\u03e8\0\u0410\0\u0438\0\u0460"+
    "\0\u0488\0\u04b0\0\u04d8\0\u0500\0\u0370\0\u0528\0\u0550\0\u0578"+
    "\0\u05a0\0\u05c8\0\u05f0\0\u0618\0\u0640\0\u0668\0\u0690\0\u06b8"+
    "\0\u06e0\0\u0708\0\u0730\0\u0758\0\u0780\0\u07a8\0\u07d0\0\u0168"+
    "\0\u0168\0\u07f8\0\u0168\0\u0820\0\u0168\0\u0168\0\u0168\0\u0168"+
    "\0\u0230\0\u0848\0\u0870\0\u0898\0\u08c0\0\u03c0\0\u0168\0\u08e8"+
    "\0\u03e8\0\u0910\0\u0168\0\u0938\0\u0960\0\u0988\0\u09b0\0\u09d8"+
    "\0\u0a00\0\u0460\0\u0a28\0\u0a50\0\u0a78\0\u0aa0\0\u0ac8\0\u0af0"+
    "\0\u0b18\0\u0b40\0\u0b68\0\u0b90\0\u0bb8\0\u0be0\0\u0168\0\u05f0"+
    "\0\u0640\0\u05f0\0\u0668\0\u0168\0\u06e0\0\u0708\0\u06b8\0\u0c08"+
    "\0\u0c30\0\u0668\0\u0730\0\u0780\0\u07d0\0\u0730\0\u07a8\0\u0c58"+
    "\0\u0c80\0\u0168\0\u0ca8\0\u0cd0\0\u0cf8\0\u08c0\0\u03c0\0\u03e8"+
    "\0\u0d20\0\u0d48\0\u0d70\0\u0d98\0\u0dc0\0\u0de8\0\u0e10\0\u0e38"+
    "\0\u0e60\0\u0e88\0\u0eb0\0\u0ed8\0\u0f00\0\u0f28\0\u0f50\0\u0f78"+
    "\0\u0fa0\0\u0fc8\0\u0ff0\0\u1018\0\u1040\0\u1068\0\u1090\0\u10b8"+
    "\0\u10e0\0\u1108\0\u1130\0\u1158\0\u1180\0\u11a8\0\u11d0\0\u11f8"+
    "\0\u1220\0\u1248\0\u1270\0\u1298\0\u0f50\0\u12c0\0\u12e8\0\u0f50"+
    "\0\u06b8\0\u0fc8\0\u1310\0\u1338\0\u0fc8\0\u07a8\0\u1360\0\u1388"+
    "\0\u13b0\0\u13d8\0\u1400\0\u1428\0\u1450\0\u1478\0\u14a0\0\u14c8"+
    "\0\u14f0\0\u1518";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\12\1\13\1\14\46\12\1\13\1\15\1\16\1\15"+
    "\1\17\1\20\1\21\1\22\1\23\1\20\2\24\1\20"+
    "\1\25\2\20\1\26\1\21\1\27\1\30\1\31\1\32"+
    "\1\15\1\33\1\20\1\34\1\12\1\35\1\12\12\20"+
    "\1\12\1\13\1\12\1\24\1\12\1\36\1\37\1\21"+
    "\3\40\2\24\1\41\1\42\1\40\1\43\1\26\1\21"+
    "\1\44\1\30\1\31\1\45\2\12\1\40\4\12\1\46"+
    "\1\47\4\40\1\50\1\51\2\40\1\12\1\13\1\15"+
    "\1\24\1\52\1\17\1\20\1\21\1\22\1\23\1\20"+
    "\2\24\1\20\1\25\2\20\1\26\1\21\1\27\1\30"+
    "\1\31\1\32\1\15\1\33\1\20\1\34\1\12\1\35"+
    "\1\12\12\20\1\53\1\54\33\53\1\55\12\53\1\56"+
    "\1\57\30\56\1\60\1\61\1\56\1\62\12\56\1\63"+
    "\1\64\31\63\1\65\1\66\1\67\12\63\1\12\1\13"+
    "\1\70\1\24\1\71\1\17\1\20\1\21\1\22\1\23"+
    "\1\20\2\24\1\20\1\25\2\20\1\26\1\21\1\27"+
    "\1\30\1\31\1\32\1\15\1\33\1\20\3\12\1\72"+
    "\12\20\1\73\1\74\46\73\51\0\1\13\50\0\1\75"+
    "\1\76\31\0\1\77\16\0\1\100\50\0\1\20\1\101"+
    "\1\0\1\102\2\20\2\0\4\20\2\0\1\20\4\0"+
    "\2\20\4\0\12\20\5\0\2\20\1\0\3\20\2\0"+
    "\4\20\2\0\1\20\4\0\2\20\4\0\12\20\7\0"+
    "\1\24\45\0\2\20\1\0\1\20\1\103\1\20\2\0"+
    "\4\20\2\0\1\20\4\0\2\20\4\0\12\20\5\0"+
    "\2\20\1\0\3\20\2\0\1\101\3\20\2\0\1\20"+
    "\4\0\2\20\4\0\12\20\5\0\2\20\1\0\3\20"+
    "\1\0\1\104\4\20\2\0\1\20\4\0\2\20\4\0"+
    "\12\20\5\0\1\20\1\101\1\0\1\101\2\20\2\0"+
    "\4\20\2\0\1\20\4\0\2\20\4\0\12\20\24\0"+
    "\1\24\50\0\1\24\50\0\1\24\47\0\1\105\1\0"+
    "\1\33\17\0\32\106\1\107\1\110\14\106\33\111\1\112"+
    "\1\113\13\111\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\114\11\40\5\0"+
    "\1\115\1\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\1\40\1\116\1\40\1\117\6\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\12\40\5\0\1\120\1\40\1\0"+
    "\3\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\12\40\5\0\2\40\1\0\3\40\1\0\1\104\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\4\40\1\121\5\40"+
    "\5\0\2\40\1\0\1\40\1\122\1\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\12\40\5\0\2\40"+
    "\1\0\1\123\2\40\2\0\4\40\2\0\1\40\4\0"+
    "\2\40\4\0\6\40\1\124\1\122\2\40\5\0\1\40"+
    "\1\125\1\0\3\40\2\0\4\40\2\0\1\40\4\0"+
    "\2\40\4\0\12\40\5\0\2\40\1\0\1\126\2\40"+
    "\2\0\2\40\1\127\1\40\2\0\1\40\4\0\2\40"+
    "\4\0\4\40\1\130\1\40\1\131\3\40\5\0\1\132"+
    "\1\40\1\0\3\40\2\0\4\40\2\0\1\40\4\0"+
    "\2\40\4\0\12\40\5\0\2\40\1\0\1\40\1\133"+
    "\1\40\2\0\1\134\3\40\2\0\1\135\4\0\2\40"+
    "\4\0\4\40\1\136\5\40\4\0\1\137\43\0\35\140"+
    "\1\141\13\140\1\54\33\140\1\141\16\140\1\142\30\140"+
    "\1\141\12\140\32\143\1\144\1\145\1\143\1\146\13\143"+
    "\1\57\30\143\1\144\1\145\1\143\1\146\12\143\32\147"+
    "\1\144\1\150\1\147\1\151\12\147\32\143\1\152\1\145"+
    "\1\143\1\146\14\143\1\0\27\143\1\0\15\143\33\153"+
    "\1\154\1\144\1\155\13\153\1\64\31\153\1\154\1\144"+
    "\1\155\45\153\1\154\1\156\1\155\12\153\33\157\1\160"+
    "\1\144\1\161\12\157\2\153\1\0\31\153\1\0\13\153"+
    "\2\0\1\162\46\0\1\74\53\0\2\20\1\0\3\20"+
    "\2\0\3\20\1\101\2\0\1\20\4\0\2\20\4\0"+
    "\12\20\5\0\2\20\1\0\2\20\1\101\2\0\4\20"+
    "\2\0\1\20\4\0\2\20\4\0\12\20\5\0\1\163"+
    "\3\0\1\164\5\0\1\165\60\0\1\166\17\0\32\106"+
    "\1\167\1\110\14\106\33\111\1\112\1\170\13\111\5\0"+
    "\2\40\1\0\2\40\1\171\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\12\40\5\0\2\40\1\0\1\172"+
    "\2\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\12\40\5\0\2\40\1\0\2\40\1\122\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\12\40\5\0\1\173"+
    "\1\40\1\0\3\40\2\0\4\40\2\0\1\40\4\0"+
    "\2\40\4\0\12\40\5\0\2\40\1\0\3\40\2\0"+
    "\4\40\2\0\1\40\4\0\2\40\4\0\11\40\1\122"+
    "\5\0\2\40\1\0\1\40\1\174\1\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\12\40\5\0\2\40"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\2\40\1\175\7\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\3\40"+
    "\1\176\6\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\40\1\122\10\40"+
    "\5\0\2\40\1\0\3\40\2\0\3\40\1\177\2\0"+
    "\1\40\4\0\2\40\4\0\12\40\5\0\2\40\1\0"+
    "\2\40\1\200\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\12\40\5\0\1\40\1\201\1\0\3\40\2\0"+
    "\4\40\2\0\1\40\4\0\2\40\4\0\12\40\5\0"+
    "\2\40\1\0\3\40\2\0\1\40\1\202\2\40\2\0"+
    "\1\40\4\0\2\40\4\0\12\40\5\0\2\40\1\0"+
    "\3\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\2\40\1\203\7\40\5\0\2\40\1\0\3\40\2\0"+
    "\1\122\3\40\2\0\1\40\4\0\2\40\4\0\12\40"+
    "\5\0\2\40\1\0\1\40\1\204\1\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\12\40\5\0\2\40"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\4\40\1\205\5\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\1\206"+
    "\11\40\32\147\1\60\1\150\1\147\1\151\14\147\1\207"+
    "\27\147\1\0\1\210\1\147\1\211\12\147\33\157\1\160"+
    "\1\66\1\161\14\157\1\212\30\157\1\213\1\0\1\214"+
    "\12\157\10\0\1\215\54\0\1\24\43\0\1\164\43\0"+
    "\2\40\1\0\1\40\1\216\1\40\2\0\4\40\2\0"+
    "\1\40\4\0\2\40\4\0\12\40\5\0\2\40\1\0"+
    "\3\40\2\0\3\40\1\217\2\0\1\40\4\0\2\40"+
    "\4\0\12\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\2\40\1\220\7\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\2\40\1\221\7\40\5\0\2\40"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\4\40\1\222\5\40\5\0\2\40\1\0\1\40"+
    "\1\223\1\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\12\40\5\0\1\36\1\37\1\0\3\40\2\0"+
    "\1\41\1\224\2\40\2\0\1\225\4\0\2\40\4\0"+
    "\1\40\1\226\4\40\1\50\1\227\2\40\5\0\2\40"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\1\40\1\230\10\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\1\40"+
    "\1\231\10\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\40\1\232\10\40"+
    "\5\0\2\40\1\0\3\40\2\0\1\43\3\40\2\0"+
    "\1\40\4\0\2\40\4\0\12\40\5\0\2\40\1\0"+
    "\3\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\6\40\1\122\3\40\5\0\2\40\1\0\2\40\1\233"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\12\40"+
    "\5\0\1\40\1\234\1\0\3\40\2\0\4\40\2\0"+
    "\1\40\4\0\2\40\4\0\12\40\32\235\1\0\1\236"+
    "\1\235\1\237\12\235\32\147\1\240\1\150\1\147\1\151"+
    "\14\147\1\241\27\147\1\144\1\150\1\147\1\151\12\147"+
    "\33\242\1\243\1\0\1\244\12\242\33\157\1\160\1\245"+
    "\1\161\14\157\1\246\30\157\1\160\1\144\1\161\12\157"+
    "\20\0\1\24\34\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\40\1\247\10\40"+
    "\5\0\2\40\1\0\3\40\2\0\1\40\1\250\2\40"+
    "\2\0\1\40\4\0\2\40\4\0\12\40\5\0\2\40"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\1\40\1\251\10\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\5\40"+
    "\1\122\4\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\252\11\40\5\0"+
    "\2\40\1\0\3\40\2\0\1\116\3\40\2\0\1\40"+
    "\4\0\2\40\4\0\12\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\4\40"+
    "\1\121\5\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\7\40\1\122\2\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\6\40\1\131\3\40\5\0\2\40"+
    "\1\0\1\40\1\133\1\40\2\0\4\40\2\0\1\135"+
    "\4\0\2\40\4\0\12\40\5\0\2\40\1\0\1\253"+
    "\2\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\12\40\5\0\2\40\1\0\3\40\2\0\4\40\2\0"+
    "\1\225\4\0\2\40\4\0\12\40\5\0\2\40\1\0"+
    "\3\40\2\0\3\40\1\122\2\0\1\40\4\0\2\40"+
    "\4\0\12\40\5\0\2\40\1\0\3\40\2\0\4\40"+
    "\2\0\1\40\4\0\2\40\4\0\1\40\1\133\10\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\10\40\1\122\1\40\33\235\1\236"+
    "\1\235\1\237\14\235\1\207\27\235\1\0\1\236\1\235"+
    "\1\237\12\235\33\242\1\243\1\242\1\244\14\242\1\212"+
    "\30\242\1\243\1\0\1\244\12\242\5\0\1\40\1\254"+
    "\1\0\3\40\2\0\4\40\2\0\1\40\4\0\2\40"+
    "\4\0\12\40\5\0\2\40\1\0\1\40\1\255\1\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\12\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\4\40\1\256\5\40\5\0\2\40"+
    "\1\0\3\40\2\0\3\40\1\125\2\0\1\40\4\0"+
    "\2\40\4\0\12\40\5\0\2\40\1\0\3\40\2\0"+
    "\3\40\1\257\2\0\1\40\4\0\2\40\4\0\12\40"+
    "\5\0\2\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\2\40\1\260\7\40\5\0\2\40"+
    "\1\0\3\40\2\0\2\40\1\122\1\40\2\0\1\40"+
    "\4\0\2\40\4\0\12\40\5\0\2\40\1\0\3\40"+
    "\2\0\4\40\2\0\1\40\4\0\2\40\4\0\1\40"+
    "\1\261\10\40\5\0\1\40\1\122\1\0\3\40\2\0"+
    "\4\40\2\0\1\40\4\0\2\40\4\0\12\40\5\0"+
    "\1\262\1\40\1\0\3\40\2\0\4\40\2\0\1\40"+
    "\4\0\2\40\4\0\12\40\5\0\1\40\1\257\1\0"+
    "\3\40\2\0\4\40\2\0\1\40\4\0\2\40\4\0"+
    "\12\40\5\0\2\40\1\0\3\40\2\0\4\40\2\0"+
    "\1\40\4\0\2\40\4\0\3\40\1\125\6\40";

  private static int [] zzUnpackTrans() {
    int [] result = new int[5440];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\11\0\1\11\2\1\1\11\6\1\1\11\43\1\2\11"+
    "\1\1\1\11\1\1\4\11\3\1\3\0\1\11\3\0"+
    "\1\11\23\1\1\11\2\0\1\1\1\0\1\11\5\0"+
    "\1\1\3\0\1\1\3\0\1\11\3\0\22\1\2\0"+
    "\1\1\3\0\17\1\3\0\2\1\3\0\16\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[178];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the
   * matched text
   */
  private int yycolumn;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF = false;

  /* user code: */

    private TwigStateStack stack = new TwigStateStack();
    private LexerInput input;
    private int brackets = 0;

    public TwigColoringLexer(LexerRestartInfo info) {
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
        final TwigStateStack stack;
        /** the current state of the DFA */
        final int zzState;
        /** the current lexical state */
        final int zzLexicalState;

        LexerState(TwigStateStack stack, int zzState, int zzLexicalState) {
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



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public TwigColoringLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public TwigColoringLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /**
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 184) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }



  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return input.readText().toString();
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
     return input.readText().charAt(pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return input.readLength();
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    input.backup(number);
    //zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public TwigTokenId findNextToken() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    //int zzCurrentPosL;
    //int zzMarkedPosL;
    //int zzEndReadL = zzEndRead;
    //char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      //zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      //zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
      int tokenLength = 0;

      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
            zzInput = input.read();

            if(zzInput == LexerInput.EOF) {
                //end of input reached
                zzInput = YYEOF;
                break zzForAction;
                //notice: currently LexerInput.EOF == YYEOF
            }

          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            tokenLength = input.readLength();
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      if(zzInput != YYEOF) {
         input.backup(input.readLength() - tokenLength);
      }

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 21:
          { popState();
        return TwigTokenId.T_TWIG_COMMENT;
          }
        case 24: break;
        case 18:
          { yypushback(yylength());
        pushState(ST_S_STRING);
          }
        case 25: break;
        case 12:
          { popState();
        return TwigTokenId.T_TWIG_WHITESPACE;
          }
        case 26: break;
        case 22:
          { brackets++;
        return TwigTokenId.T_TWIG_INTERPOLATION_START;
          }
        case 27: break;
        case 20:
          { popState();
        return TwigTokenId.T_TWIG_VAR_END;
          }
        case 28: break;
        case 23:
          { yypushback(2);
        pushState(ST_INTERPOLATION);
        return TwigTokenId.T_TWIG_STRING;
          }
        case 29: break;
        case 19:
          { popState();
        pushState(ST_BLOCK);
        return TwigTokenId.T_TWIG_TAG;
          }
        case 30: break;
        case 13:
          { pushState(ST_VAR);
        return TwigTokenId.T_TWIG_VAR_START;
          }
        case 31: break;
        case 3:
          { return TwigTokenId.T_TWIG_PUNCTUATION;
          }
        case 32: break;
        case 1:
          { yypushback(yylength());
        pushState(ST_HIGHLIGHTING_ERROR);
          }
        case 33: break;
        case 7:
          { popState();
        pushState(ST_BLOCK);
        return TwigTokenId.T_TWIG_NAME;
          }
        case 34: break;
        case 8:
          { popState();
        return TwigTokenId.T_TWIG_STRING;
          }
        case 35: break;
        case 5:
          { return TwigTokenId.T_TWIG_NAME;
          }
        case 36: break;
        case 11:
          { return TwigTokenId.T_TWIG_OTHER;
          }
        case 37: break;
        case 9:
          { brackets++;
        return TwigTokenId.T_TWIG_PUNCTUATION;
          }
        case 38: break;
        case 16:
          { popState();
        return TwigTokenId.T_TWIG_BLOCK_END;
          }
        case 39: break;
        case 6:
          { return TwigTokenId.T_TWIG_NUMBER;
          }
        case 40: break;
        case 10:
          { brackets--;
        if (brackets == 0) {
            popState();
            return TwigTokenId.T_TWIG_INTERPOLATION_END;
        } else {
            return TwigTokenId.T_TWIG_PUNCTUATION;
        }
          }
        case 41: break;
        case 4:
          { return TwigTokenId.T_TWIG_OPERATOR;
          }
        case 42: break;
        case 17:
          { yypushback(yylength());
        pushState(ST_D_STRING);
          }
        case 43: break;
        case 14:
          { pushState(ST_BLOCK_START);
        return TwigTokenId.T_TWIG_BLOCK_START;
          }
        case 44: break;
        case 2:
          { return TwigTokenId.T_TWIG_WHITESPACE;
          }
        case 45: break;
        case 15:
          { pushState(ST_COMMENT);
          }
        case 46: break;
        default:
          if (zzInput == YYEOF)
            //zzAtEOF = true;
              {         if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return TwigTokenId.T_TWIG_OTHER;
        } else {
            return null;
        }
 }

          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
