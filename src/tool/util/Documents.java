/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tool.Main;

/**
 *
 * @author
 */
public class Documents {
    
   
    public static List<DocumentConcepts> getDataSet() throws IOException {
        List<DocumentConcepts> dataset = new ArrayList<>();
        for (File file : Main.test_data_dir.listFiles()) {
            if (!file.toString().contains(".concept"))
                continue;            
            File textFile = new File(file.toString().replace(".concept", ".txt"));
            Abbreviation abbreviationObject = new Abbreviation();
            abbreviationObject.setTextAbbreviationExpansionMap(textFile);
            DocumentConcepts documentConceptsObject = new DocumentConcepts(textFile.getName(), Util.read(textFile));            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    String[] tokens = line.split("\\|\\|");
                    documentConceptsObject.setConcept(tokens, abbreviationObject);
                }
            }
            dataset.add(documentConceptsObject);
        }        
        return dataset;
    }    
    
}
