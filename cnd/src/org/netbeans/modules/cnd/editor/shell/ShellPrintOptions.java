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

package org.netbeans.modules.cnd.editor.shell;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.netbeans.modules.editor.options.*;

/**
* Options for the shell editor kit
*/
public class ShellPrintOptions extends BasePrintOptions {

    public static final String shell = "shell"; //NOI18N

    static final long serialVersionUID = -6336947579611466072L;

    public ShellPrintOptions() {
        this(ShellKit.class, shell);
    }

    public ShellPrintOptions(Class kitClass, String typeName) {
        super(kitClass, typeName);
    }

    /** @return localized string */
    protected String getString(String s) {
        try {
            String res = NbBundle.getBundle(ShellPrintOptions.class).getString(s);
            return (res == null) ? super.getString(s) : res;
        }
        catch (Exception e) {
            return super.getString(s);
        }
    }
    
    public HelpCtx getHelpCtx () {
        return new HelpCtx ("Welcome_opt_print_shell"); // NOI18N
    }
}
