/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.httpserver;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.openide.util.NbBundle;
/**
 *
 * @author Radim Kubacki
 */
public class WrapperServlet extends NbBaseServlet {

    private static final long serialVersionUID = 8009602136746998361L;
    
    /** Creates new WrapperServlet */
    public WrapperServlet () {
    }

    /**
     * Transforms URL to another URL that uses HTTP or FTP protocol 
     * and can be used from outside of IDE
     *
     * @param url original URL
     * @return translated URL
     */ 
    public static URL createHttpURL (URL url) {
        if (url == null)
            return null;
        
        if ("http".equals (url.getProtocol ())
        ||  "ftp".equals (url.getProtocol ()))
            return url;
        
        try {
            String path = HttpServerSettings.mangle (url.toString ());
            URL newURL = new URL ("http", 
                                  InetAddress.getLocalHost ().getHostName (), 
                                  HttpServerSettings.OPTIONS.getPort (),
                                  HttpServerSettings.OPTIONS.getWrapperBaseURL () + path);
            return newURL;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    /** Processes the request for both HTTP GET and POST methods
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest (HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, java.io.IOException {
        if (!checkAccess(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                               NbBundle.getBundle(NbBaseServlet.class).getString("MSG_HTTP_FORBIDDEN"));
            return;
        }
        
        // output your page here
        String path = request.getPathInfo ();
        java.io.PrintWriter out = response.getWriter();;
        try {
            
            // resource name
            if (path.startsWith ("/")) path = path.substring (1); // NOI18N
            
            URL innerURL = new URL (HttpServerSettings.demangle (path));
System.out.println("demangled to "+innerURL.toString ());
            URLConnection conn = innerURL.openConnection ();
            
            response.setContentType(conn.getContentType ());   // NOI18N
            // PENDING: copy all info - headers, length, encoding, ...
            
            InputStream in = conn.getInputStream ();
            byte [] buff = new byte [256];
            int len;

            while ((len = in.read (buff)) != -1) {
                out.print (new String (buff, 0, len));
            }
            in.close ();

        }
        catch (java.net.MalformedURLException ex) {
            ex.printStackTrace ();
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace ();
        }
        finally {
            try { out.close(); } catch (Exception ex) {}
        }
    }



    /**
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return NbBundle.getBundle(ClasspathServlet.class).getString("MSG_WrapperServletDescr");
    }

}
