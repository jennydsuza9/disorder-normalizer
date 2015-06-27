/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tool.Main;
import tool.MultiPassSieveNormalizer;
import tool.util.AmbiguityResolution;
import tool.util.Concept;
import tool.util.Terminology;
import tool.util.Util;

/**
 *
 * @author
 */
public abstract class Sieve {
    
    //state of this class
    private static Terminology standardTerminology = new Terminology();    
    public static void setStandardTerminology() throws IOException {
        standardTerminology.loadTerminology();
    }
    public static Terminology getStandardTerminology() {
        return standardTerminology;
    }
    
    private static Terminology trainingDataTerminology = new Terminology();
    public static void setTrainingDataTerminology() throws IOException {
        trainingDataTerminology.loadTrainingDataTerminology(Main.training_data_dir);
    }    
    public static Terminology getTrainingDataTerminology() {
        return trainingDataTerminology;
    }        
    
    public static List<String> getTerminologyNameCuis(Map<String, List<String>> nameToCuiListMap, String name) {
        return nameToCuiListMap.containsKey(name) ? nameToCuiListMap.get(name) : null;
    }
    
    public static String getTerminologyNameCui(Map<String, List<String>> nameToCuiListMap, String name) {
        return nameToCuiListMap.containsKey(name) && nameToCuiListMap.get(name).size() == 1 ? nameToCuiListMap.get(name).get(0) : "";        
    }
    
    public static String exactMatchSieve(String name) {
        String cui = "";
        //checks against names normalized by multi-pass sieve
        cui = getTerminologyNameCui(Terminology.getNormalizedNameToCuiListMap(), name);
        if (!cui.equals("")) {
            return cui;
        }
        
        //checks against names in training data
        cui = getTerminologyNameCui(trainingDataTerminology.getNameToCuiListMap(), name);
        if (!cui.equals("")) {
            return cui;       
        }
        
        //checks against names in dictionary
        return getTerminologyNameCui(standardTerminology.getNameToCuiListMap(), name);               
    }

    public static List<String> getAlternateCuis(String cui) {
        List<String> alternateCuis = new ArrayList<>();
        if (trainingDataTerminology.getCuiAlternateCuiMap().containsKey(cui)) {
            alternateCuis.addAll(trainingDataTerminology.getCuiAlternateCuiMap().get(cui));
        }
        if (standardTerminology.getCuiAlternateCuiMap().containsKey(cui)) {
            alternateCuis.addAll(standardTerminology.getCuiAlternateCuiMap().get(cui));
        }
        return alternateCuis;
    } 
 
    public static String normalize(List<String> namesKnowledgeBase) {
        for (String name : namesKnowledgeBase) {
            String cui = exactMatchSieve(name);            
            if (!cui.equals(""))
                return cui;
        }
        return "";
    }
    
}
