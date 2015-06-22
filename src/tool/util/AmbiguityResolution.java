/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tool.Evaluation;
import tool.Logger;
import tool.sieves.Sieve;

/**
 *
 * @author 
 */
public class AmbiguityResolution {
    
    public static void start(DocumentConcepts concepts, Map<String, List<String>> cuiNamesMap) throws IOException {
        for (Concept concept : concepts.getConcepts()) {
            if (concept.getNormalizingSieve() != 1 || concept.getCui().equals("CUI-less")) {
                Evaluation.evaluateClassification(concept, concepts);
                Terminology.storeNormalizedConcept(concept);
                continue;          
            }
            
            String conceptName = concept.getName();
            String[] conceptNameTokens = conceptName.split("\\s+");
            
            List<String> trainingDataCuis = Sieve.getTrainingDataTerminology().getNameToCuiListMap().containsKey(conceptName) ?
                    Sieve.getTrainingDataTerminology().getNameToCuiListMap().get(conceptName) : null;
            if (trainingDataCuis == null || trainingDataCuis.size() == 1) {
                Evaluation.evaluateClassification(concept, concepts);
                Terminology.storeNormalizedConcept(concept);
                continue;
            }
            
            if (conceptNameTokens.length > 1) 
                concept.setCui("CUI-less");
            else {                
                int countCUIMatch = 0;
                for (String cui : trainingDataCuis) {
                    List<String> names = cuiNamesMap.containsKey(cui) ? cuiNamesMap.get(cui) : new ArrayList<String>();
                    for (String name : names) {
                        String[] nameTokens = name.split("\\s+");
                        if (nameTokens.length == 1)
                            continue;
                        if (name.matches(conceptName+" .*")) {
                            countCUIMatch++;
                        }
                    }
                }
                if (countCUIMatch > 0) 
                    concept.setCui("CUI-less");
                else
                    Terminology.storeNormalizedConcept(concept);
            }
            Evaluation.evaluateClassification(concept, concepts);
        }
    }
    
}
