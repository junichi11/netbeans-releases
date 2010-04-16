/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.HostInfo.OSFamily;
import org.netbeans.modules.nativeexecution.api.pty.PtySupport;
import org.netbeans.modules.nativeexecution.api.pty.PtySupport.Pty;
import org.netbeans.modules.nativeexecution.api.util.WindowsSupport;
import org.netbeans.modules.nativeexecution.pty.PtyProcessStartUtility;
import org.netbeans.modules.nativeexecution.spi.pty.PtyImpl;
import org.netbeans.modules.nativeexecution.spi.support.pty.PtyImplAccessor;
import org.openide.util.Exceptions;

/**
 *
 * @author ak119685
 */
public final class PtyNativeProcess extends AbstractNativeProcess {

    private Pty pty = null;
    private PtyImpl ptyImpl = null;
    private AbstractNativeProcess delegate = null;
    private volatile boolean cancelled;

    public PtyNativeProcess(NativeProcessInfo info) {
        super(info);
        cancelled = false;
    }

    public Pty getPty() {
        return pty;
    }

    @Override
    protected void create() throws Throwable {
        ExecutionEnvironment env = info.getExecutionEnvironment();
        Pty _pty = info.getPty();

        if (_pty == null) {
            try {
                _pty = PtySupport.allocate(env);
            } catch (IOException ex) {
                String msg = "Unable to allocate a pty for the process: " + info.getExecutable() + " [" + ex.getMessage() + "]";
                throw new IOException(msg); // NOI18N
            }
        }

        if (cancelled) {
            return;
        }

        pty = _pty;
        ptyImpl = PtyImplAccessor.getDefault().getImpl(pty);

        if (ptyImpl != null) {
            setInputStream(ptyImpl.getInputStream());
            setOutputStream(ptyImpl.getOutputStream());
        }

        String executable = PtyProcessStartUtility.getInstance().getPath(env);

        List<String> newArgs = new ArrayList<String>();
        newArgs.add("-p"); // NOI18N
        newArgs.add(pty.getSlaveName());

        String processExecutable = info.getExecutable();
        if (hostInfo.getOSFamily() == OSFamily.WINDOWS) {
            // process_start requires Unix style executable path
            processExecutable = WindowsSupport.getInstance().convertToShellPath(processExecutable);
        }
        newArgs.add(processExecutable);

        newArgs.addAll(info.getArguments());

        // TODO: Clone Info!!!!
        info.setExecutable(executable);
        info.setArguments(newArgs.toArray(new String[0]));

        // Listeners...
        // listeners are copied already in super()
        // and never accessed via info anymore...
        // so when we change listeners here,
        // this change has effect on delegate only...

        info.getListeners().clear();
//        info.getListeners().add(new DelegateListener());

        if (env.isLocal()) {
            delegate = new LocalNativeProcess(info);
        } else {
            delegate = new RemoteNativeProcess(info);
        }

        delegate.createAndStart();

        readPID(delegate.getInputStream());
    }

    @Override
    protected void cancel() {
        cancelled = true;

        if (delegate != null) {
            delegate.destroy();
        }
    }

    @Override
    protected int waitResult() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }

        if (delegate == null) {
            return 1;
        }

        return delegate.waitResult();
    }

    public void closePty() {
        try {
            if (ptyImpl != null) {
                ptyImpl.close();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
