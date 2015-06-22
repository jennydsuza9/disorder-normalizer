/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author
 */
public class DocumentConcepts {
    
    private String filename;
    private String text;
    private List<Concept> concepts;
    
    public DocumentConcepts(String filename, String text) {
        this.filename = filename;
        this.text = text;
        concepts = new ArrayList<>();
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getText() {
        return text;
    }
        
    public List<Concept> getConcepts() {
        return concepts;
    }
    
    public void setConcept(String[] tokens, Abbreviation abbreviationObject) {
        String[] cuis = tokens[4].contains("+") ? tokens[4].split("\\+") : tokens[4].split("\\|");
        String MeSHorSNOMEDcuis = Terminology.getMeSHorSNOMEDCuis(cuis);
        List<String> OMIMcuis = Terminology.getOMIMCuis(cuis);
        Concept concept = new Concept(tokens[1], tokens[3], MeSHorSNOMEDcuis, OMIMcuis);
        concept.setNameExpansion(text, abbreviationObject);
        concept.setStemmedName();
        concepts.add(concept);
    }
    
}
