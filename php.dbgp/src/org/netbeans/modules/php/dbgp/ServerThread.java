package org.netbeans.modules.php.dbgp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.modules.php.dbgp.breakpoints.Utils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

class ServerThread extends SingleThread {
    private static final int TIMEOUT = 10000;
    private static final String PORT_OCCUPIED = "MSG_PortOccupied"; // NOI18N

    private int myPort;
    private ServerSocket myServer;
    private AtomicBoolean isStopped;

    ServerThread() {
        super();
        isStopped = new AtomicBoolean(false);
    }


    @Override
    public void run() {
        isStopped = new AtomicBoolean(false);
        DebugSession debugSession = getDebugSession();
        ProxyClient proxy = null;
        if (debugSession != null && createServerSocket(debugSession)) {            
            proxy = ProxyClient.getInstance(debugSession.getOptions());
            if (proxy != null)  { 
                proxy.register();
            }
            debugSession.startBackend();
            while (!isStopped() && getDebugSession() != null) {
                try {
                    Socket sessionSocket = myServer.accept();
                    if (!isStopped.get() && sessionSocket != null) {
                        debugSession.startProcessing(sessionSocket);
                    }
                } catch (SocketTimeoutException e) {
                    log(e, Level.FINEST);
                } catch (IOException e) {
                    log(e);
                }
            }
            closeSocket();
        }
        if (proxy != null) {
            proxy.unregister();
        }
    }

    private DebugSession getDebugSession() {
        DebugSession retval = DebuggerManager.getDebuggerManager().getCurrentEngine().lookupFirst(null, DebugSession.class);
        if (retval == null) {
            Session[] sessions = DebuggerManager.getDebuggerManager().getSessions();
            for (Session session : sessions) {
                retval = session.lookupFirst(null, DebugSession.class);
                if (retval != null) {
                    break;
                }
            }
        }
        return retval;
    }

    private void log(Throwable exception) {
        log(exception, Level.FINE);
    }
    private void log(Throwable exception, Level level) {
        Logger.getLogger(ServerThread.class.getName()).log(level, null, exception);
    }

    private boolean createServerSocket(DebugSession debugSession) {
        synchronized (ServerThread.class) {
            try {
                myServer = new ServerSocket(myPort = debugSession.getOptions().getPort());
                myServer.setSoTimeout(TIMEOUT);
                myServer.setReuseAddress(true);
            } catch (IOException e) {
                String mesg = NbBundle.getMessage(ServerThread.class, PORT_OCCUPIED);
                mesg = MessageFormat.format(mesg, myPort);
                NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(mesg, JOptionPane.YES_NO_OPTION);
                Object choice = DialogDisplayer.getDefault().notify(descriptor);
                if (choice.equals(JOptionPane.YES_OPTION)) {
                    Utils.openPhpOptionsDialog();
                }
                log(e);
                return false;
            }
            return true;
        }
    }

    private void closeSocket() {
        synchronized (ServerThread.class) {
            if (myServer == null) {
                return;
            }
            try {
                if (!myServer.isClosed()) {
                    myServer.close();
                }
            } catch (IOException e) {
                log(e);
            }
        }
    }

    @Override
    public boolean cancel() {
        isStopped.set(true);
        closeSocket();
        return true;
    }

    private boolean isStopped() {
        return isStopped.get();
    }
}
