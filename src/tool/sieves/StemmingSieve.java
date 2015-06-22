/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tool.util.Concept;
import tool.util.Ling;
import tool.util.Terminology;
import tool.util.Util;

/**
 *
 * @author
 */
public class StemmingSieve extends Sieve {
    
    public static String apply(Concept concept) {
        transformName(concept);
        return StemmingSieve.normalize(concept);
    }     
    
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();        
        
        for (String nameForTransformation : namesForTransformation) {
            transformedNames = Util.setList(transformedNames, Ling.getStemmedPhrase(nameForTransformation));
        }
        
        concept.setStemmedNamesKnowledgeBase(transformedNames);   
    }    

    public static String normalize(Concept concept) {
        for (String name : concept.getStemmedNamesKnowledgeBase()) {
            String cui = StemmingSieve.exactMatchSieve(name);            
            if (!cui.equals(""))
                return cui;
        }
        return "";
    }    
    
    public static String exactMatchSieve(String name) {
        String cui = "";
        //checks against names normalized by multi-pass sieve
        cui = getTerminologyNameCui(Terminology.getStemmedNormalizedNameToCuiListMap(), name);
        if (!cui.equals(""))
            return cui;
        
        //checks against names in training data
        cui = getTerminologyNameCui(Sieve.getTrainingDataTerminology().getStemmedNameToCuiListMap(), name);
        if (!cui.equals(""))
            return cui;        
        
        //checks against names in dictionary
        cui = getTerminologyNameCui(Sieve.getStandardTerminology().getStemmedNameToCuiListMap(), name);       
        return cui;
    }    
    
}
