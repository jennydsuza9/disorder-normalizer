/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tool.sieves.Sieve;
import tool.util.Concept;
import tool.util.Terminology;

/**
 *
 * @author
 */
public class Logger {
            
    private static FileOutputStream log;

    public static void setLogFile(FileOutputStream logFile) {
        log = logFile;
    }

    public static void writeLogFile(String string) throws IOException {
        log.write((string+"\n").getBytes());
    }      
    
}
