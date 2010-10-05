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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.explorer.dlg;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.lib.ddl.DDLException;
import org.netbeans.modules.db.ExceptionListener;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.action.ConnectUsingDriverAction;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class ConnectionPanel implements AddConnectionWizard.Panel, WizardDescriptor.ValidatingPanel<AddConnectionWizard>, WizardDescriptor.FinishablePanel<AddConnectionWizard> {

    private DatabaseConnection databaseConnection;

    public ConnectionPanel() {
    }
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private NewConnectionPanel component;
    public static final String ORACLE_THIN_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    public static final String ORACLE_OCI_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private AddConnectionWizard pw;
    // Oracle
    public static final String ORACLE_THIN_DRIVER_NAME = "Oracle Thin";
    public static final String ORACLE_THIN_DRIVER_DISPLAY_NAME = "Oracle Thin";
    public static final String ORACLE_SAMPLE_DB_USER = "hr";
    public static final String ORACLE_SAMPLE_DB_PASSWORD = "hr";
    public static final String ORACLE_SAMPLE_DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    public static final String ORACLE_DEFAULT_SCHEMA = "hr";
    // MySQL
    public static final String MYSQL_DRIVER_NAME = "MySQL";
    public static final String MYSQL_DRIVER_DISPLAY_NAME = "MySQL (Connector/J driver)";
    public static final String MYSQL_SAMPLE_DB_USER = "root";
    public static final String MYSQL_SAMPLE_DB_PASSWORD = "vsds";
    public static final String MYSQL_SAMPLE_DB_URL = "jdbc:mysql://localhost:3306/mysql";
    public static final String MYSQL_DEFAULT_SCHEMA = "hr";

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Component getComponent() {
        if (component == null) {
            if (pw == null) {
                return null;
            }
            assert pw != null : "ConnectionPanel must be initialized.";
            String driverName = pw.getDriverName();
            String driverClass = null;
            databaseConnection = new DatabaseConnection();
            if (true /* oracle */) {
                driverClass = ORACLE_THIN_DRIVER_CLASS;
                databaseConnection.setDriver(driverClass);
                databaseConnection.setDriverName(ORACLE_THIN_DRIVER_NAME);
                databaseConnection.setUser(ORACLE_SAMPLE_DB_USER);
                databaseConnection.setPassword(ORACLE_SAMPLE_DB_PASSWORD);
                databaseConnection.setDatabase(ORACLE_SAMPLE_DB_URL);
                //cinfo.setDefaultSchema(ORACLE_DEFAULT_SCHEMA);
            } else if (true /* mysql */) {
                driverClass = MYSQL_DRIVER_CLASS;
                databaseConnection.setDriver(driverClass);
                databaseConnection.setDriverName(MYSQL_DRIVER_NAME);
                databaseConnection.setUser(MYSQL_SAMPLE_DB_USER);
                databaseConnection.setPassword(MYSQL_SAMPLE_DB_PASSWORD);
                databaseConnection.setDatabase(MYSQL_SAMPLE_DB_URL);
                //cinfo.setDefaultSchema(ORACLE_DEFAULT_SCHEMA);
            } else {
                // others
            }
            databaseConnection.setRememberPassword(databaseConnection.getPassword() != null && ! databaseConnection.getPassword().isEmpty());
            component = new NewConnectionPanel(pw, driverClass, databaseConnection);
            JComponent jc = (JComponent) component;
            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 1);
            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, pw.getSteps());
            jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.setName(pw.getSteps()[1]);
            fireChangeEvent();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    @Override
    public void readSettings(AddConnectionWizard settings) {
        this.pw = settings;
    }

    @Override
    public void storeSettings(AddConnectionWizard settings) {
        // store values from from into connection
        component.setConnectionInfo();
        pw.setDatabaseConnection(databaseConnection);
    }

    private String state;
    private String errorMessage;

    @Override
    @SuppressWarnings("SleepWhileHoldingLock")
    public void validate() throws WizardValidationException {
        state = null;
        PropertyChangeListener connectionListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("connecting")) { // NOI18N
                } else if (event.getPropertyName().equals("failed")) { // NOI18N
                    state = event.getPropertyName();
                } else if (event.getPropertyName().equals("connected")) { //NOI18N
                    try {
                        databaseConnection.getConnector().finishConnect(null, databaseConnection, databaseConnection.getConnection());
                        state = event.getPropertyName();
                    } catch (DatabaseException exc) {
                        Logger.getLogger(ConnectionPanel.class.getName()).log(Level.INFO, exc.getLocalizedMessage(), exc);
                        state = "failed";
                    }
                    //boolean result = retrieveSchemas(schemaPanel, cinfo, cinfo.getUser());
                }
            }
        };
        ExceptionListener excListener = new ExceptionListener() {
            @Override
            public void exceptionOccurred(Exception exc) {
                if (exc instanceof DDLException) {
                    Logger.getLogger(ConnectionPanel.class.getName()).log(Level.INFO, exc.getLocalizedMessage(), exc.getCause());
                } else {
                    Logger.getLogger(ConnectionPanel.class.getName()).log(Level.INFO, exc.getLocalizedMessage(), exc);
                }
                String message = null;
                if (exc instanceof ClassNotFoundException) {
                    message = NbBundle.getMessage (ConnectUsingDriverAction.class, "EXC_ClassNotFound", exc.getMessage()); //NOI18N
                } else {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(exc.getMessage());
                    if (exc instanceof DDLException && exc.getCause() instanceof SQLException) {
                        SQLException sqlEx = ((SQLException)exc.getCause()).getNextException();
                        while (sqlEx != null) {
                            buffer.append("\n\n").append(sqlEx.getMessage()); // NOI18N
                            sqlEx = sqlEx.getNextException();
                        }
                    }
                    message = buffer.toString();
                }
                errorMessage = message;
            }
        };


        databaseConnection.addPropertyChangeListener(connectionListener);
        databaseConnection.addExceptionListener(excListener);
        databaseConnection.connectAsync();
        int maxLoops = 60;
        int loop = 0;
        while ((! "connected".equals(state) || ! "failed".equals(state)) && loop < maxLoops) { // NOI18N
            try {
                Thread.sleep(1000);
                loop++;
            } catch (InterruptedException ex) {
            }
            if ("connected".equals(state)) { // NOI18N
                // all ok
                databaseConnection.removePropertyChangeListener(connectionListener);
                databaseConnection.removeExceptionListener(excListener);
                List<String> schemas = null;
                try {
                    DatabaseMetaData dbMetaData = databaseConnection.getConnection().getMetaData();
                    if (dbMetaData.supportsSchemasInTableDefinitions()) {
                        ResultSet rs = dbMetaData.getSchemas();
                        if (rs != null) {
                            while (rs.next()) {
                                if (schemas == null) {
                                    schemas = new ArrayList<String>();
                                }
                                schemas.add(rs.getString(1).trim());
                            }
                        }
                    }
                } catch (SQLException exc) {
                    Logger.getLogger(ConnectionPanel.class.getName()).log(Level.INFO, exc.getLocalizedMessage(), exc);
                    //String message = NbBundle.getMessage(ConnectUsingDriverAction.class, "ERR_UnableObtainSchemas", exc.getMessage()); // NOI18N
                    //DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE));
                }
                pw.setSchemas(schemas);
                return ;
            } else if ("failed".equals(state)) { // NOI18N
                databaseConnection.removePropertyChangeListener(connectionListener);
                databaseConnection.removeExceptionListener(excListener);
                throw new WizardValidationException((JComponent) component, state, errorMessage);
            } else if (loop >= maxLoops) {
                databaseConnection.removePropertyChangeListener(connectionListener);
                databaseConnection.removeExceptionListener(excListener);
                throw new WizardValidationException((JComponent) component, "Too long", "Too long"); // NOI18N
            }
        }
        databaseConnection.removePropertyChangeListener(connectionListener);
        databaseConnection.removeExceptionListener(excListener);
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

}
