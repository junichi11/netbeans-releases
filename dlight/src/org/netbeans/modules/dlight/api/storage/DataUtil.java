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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.dlight.api.storage;

/**
 * Some functions useful when dealing with {@link DataRow}s.
 *
 * @author Alexey Vladykin
 */
public final class DataUtil {

    private DataUtil() {
    }

    /**
     * Tries to convert given object to array of integers.
     * <code>Number</code> subclasses are converted using <code>intValue()</code> method.
     * <code>String</code>s are parsed with <code>Integer.parseInt()</code>.
     * string can have multiple integers divided by space
     *
     * @param obj  converted object
     * @return coversion result
     */
    public static int[] toInts(Object obj) {
        if (obj instanceof Number) {
            return new int[] {((Number)obj).intValue()};
        } else if (obj instanceof String) {
            String[] split = ((String)obj).split(" "); // NOI18N
            int out[] = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                String string = split[i];
                out[i] = toInt(string);
            }
            return out;
        }
        return new int[0];
    }
    
    /**
     * Shortcut for {@link #toInt(java.lang.Object, int)}
     * with <code>defaultValue = 0</code>.
     * 
     * @param obj  converted object
     * @return conversion result
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }

    /**
     * Tries to convert given object to int.
     * <code>Number</code> subclasses are converted using <code>intValue()</code> method.
     * <code>String</code>s are parsed with <code>Integer.parseInt()</code>.
     * In case string is malformed, or object is of any other class,
     * or <code>null</code> is given, default value is returned.
     *
     * @param obj  converted object
     * @param defaultValue  what to return if conversion fails
     * @return coversion result
     */
    public static int toInt(Object obj, int defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    /**
     * Shortcut for {@link #toLong(java.lang.Object, long)}
     * with <code>defaultValue = 0</code>.
     *
     * @param obj  converted object
     * @return conversion result
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0);
    }

    /**
     * Tries to convert given object to long.
     * <code>Number</code> subclasses are converted using <code>longValue()</code> method.
     * <code>String</code>s are parsed with <code>Long.parseLong()</code>.
     * In case string is malformed, or object is of any other class,
     * or <code>null</code> is given, default value is returned.
     *
     * @param obj  converted object
     * @param defaultValue  what to return if conversion fails
     * @return coversion result
     */
    public static long toLong(Object obj, long defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    /**
     * Shortcut for {@link #toFloat(java.lang.Object, float)}
     * with <code>defaultValue = 0</code>.
     *
     * @param obj  converted object
     * @return conversion result
     */
    public static float toFloat(Object obj) {
        return toFloat(obj, 0f);
    }

    /**
     * Tries to convert given object to float.
     * <code>Number</code> subclasses are converted using <code>floatValue()</code> method.
     * <code>String</code>s are parsed with <code>Float.parseFloat()</code>
     * (both <code>'.'</code> and <code>','</code> are allowed as decimal separator).
     * In case string is malformed, or object is of any other class,
     * or <code>null</code> is given, default value is returned.
     *
     * @param obj  converted object
     * @param defaultValue  what to return if conversion fails
     * @return coversion result
     */
    public static float toFloat(Object obj, float defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).floatValue();
        } else if (obj instanceof String) {
            try {
                return Float.parseFloat(((String) obj).replace(',', '.'));
            } catch (NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    /**
     * Shortcut for {@link #toDouble(java.lang.Object, double)}
     * with <code>defaultValue = 0</code>.
     *
     * @param obj  converted object
     * @return conversion result
     */
    public static double toDouble(Object obj) {
        return toDouble(obj, 0);
    }

    /**
     * Tries to convert given object to double.
     * <code>Number</code> subclasses are converted using <code>doubleValue()</code> method.
     * <code>String</code>s are parsed with <code>Double.parseDouble()</code>
     * (both <code>'.'</code> and <code>','</code> are allowed as decimal separator).
     * In case string is malformed, or object is of any other class,
     * or <code>null</code> is given, default value is returned.
     *
     * @param obj  converted object
     * @param defaultValue  what to return if conversion fails
     * @return coversion result
     */
    public static double toDouble(Object obj, double defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble(((String) obj).replace(',', '.'));
            } catch (NumberFormatException ex) {
            }
        }
        return defaultValue;
    }

    /**
     * Extracts timestamp (if any) from data row.
     *
     * @param row  data row
     * @return  timestamp, or <code>-1</code> if not available
     */
    public static long getTimestamp(DataRow row) {
        Object timestamp = row.getData("timestamp"); // NOI18N
        return toLong(timestamp, -1);
    }
}
