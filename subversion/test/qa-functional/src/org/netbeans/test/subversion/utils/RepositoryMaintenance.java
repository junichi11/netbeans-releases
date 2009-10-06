/*
 * RepositoryMaintenance.java
 *
 * Created on 21 April 2006, 17:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.test.subversion.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author peter
 */
public final class RepositoryMaintenance {
    
    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            String[] files = folder.list();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(new File(folder, files[i]));
            }    
        }
        folder.delete();
    }
    
    public static int loadRepositoryFromFile(String repoPath, String dumpPath){
        int value = -1;
        
        File repo = new File(repoPath);
        repo.mkdir();
        File dump = new File(dumpPath);
        boolean gzip = false;
        if (!dump.isFile()) {
            File dumpgz = new File(dumpPath + ".gz");
            if (dumpgz.isFile()) {
                dump = dumpgz;
                gzip = true;
            }
        }
        
        File tmpOutput = new File(repo.getParent() + File.separator + "output.txt");
                
        StreamHandler shFile;
        StreamHandler shError;
        StreamHandler shOutput;
        
        try {
            String[] cmd = {"svnadmin", "load", repo.getCanonicalPath()};
            InputStream fis = new FileInputStream(dump);
            if (gzip) {
                fis = new GZIPInputStream(fis);
            }
            FileOutputStream fos = new FileOutputStream(tmpOutput);
            Process p = Runtime.getRuntime().exec(cmd);
            shFile = new StreamHandler(fis, p.getOutputStream());
            shError = new StreamHandler(p.getErrorStream(), System.err);
            shOutput = new StreamHandler(p.getInputStream(), fos);
            shFile.start();
            shError.start();
            shOutput.start();
            value = p.waitFor();
            shFile.join();
            shError.join();
            shOutput.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public static int createRepository(String path) {
        int value = -1;
        
        File file = new File(path);
        file.mkdirs();
        
        String[] cmd = {"svnadmin", "create", path};
        
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            value = p.waitFor();   
        } catch (IOException e) {
            System.out.println("ex");
        } catch (InterruptedException e) {
            System.out.println("ex");
        }
        
        return value;
    }
    
    public static String changeFileSeparator(String path, boolean backed) {
        String changedPath = "";
        if (!backed) {
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '\\') {
                    changedPath += '/';
                } else {
                    changedPath += path.charAt(i); 
                }       
            }
        } else {
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '/') {
                    changedPath += '\\' + '\\';
                } else {
                    changedPath += path.charAt(i); 
                }       
            }
        }
        if (changedPath.startsWith("/")) 
            changedPath = changedPath.substring(1, changedPath.length());
        return changedPath;
    }
    
}


/*create user/password - test/test
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + File.separator + "conf" + File.separator + "passwd"));
            String line = "[users]";
            bw.append(line, 0, line.length());
            bw.newLine();
            line = "test = test";
            bw.append(line, 0, line.length());
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }    
        //rw access to repository for test user and r access for anonymous
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + File.separator + "conf" + File.separator + "authz"));
            String line = "[/]";
            bw.append(line, 0, line.length());
            bw.newLine();
            line = "test = rw";
            bw.append(line, 0, line.length());
            bw.newLine();
            line = "* = r";
            bw.append(line, 0, line.length());
            bw.flush();
            bw.close();
        } catch (IOException e) {
        } */   
