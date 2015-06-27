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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tool.Main;
import tool.sieves.CompoundPhraseSieve;
import tool.sieves.SimpleNameSieve;

/**
 *
 * @author
 */
public class Terminology {
    
    public static File terminologyFile;
    
    private Map<String, List<String>> tokenToNameListMap = new HashMap<>();
    private Map<String, List<String>> nameToCuiListMap = new HashMap<>();
    private Map<String, List<String>> simpleNameToCuiListMap = new HashMap<>();
    private Map<String, List<String>> compoundNameToCuiListMap = new HashMap<>();
    private Map<String, List<String>> cuiToNameListMap = new HashMap<>();
    private Map<String, List<String>> stemmedNameToCuiListMap = new HashMap<>();
    private Map<String, List<String>> cuiToStemmedNameListMap = new HashMap<>();
    private Map<String, List<String>> cuiAlternateCuiMap = new HashMap<>();    
    
    public static Map<String, Map<String, List<String>>> cuiNameFileListMap = new HashMap<>();
    
    public Map<String, List<String>> getTokenToNameListMap() {
        return tokenToNameListMap;
    }
    
    public Map<String, List<String>> getNameToCuiListMap() {
        return nameToCuiListMap;
    }
    
    public Map<String, List<String>> getSimpleNameToCuiListMap() {
        return simpleNameToCuiListMap;
    }    
    
    public Map<String, List<String>> getCompoundNameToCuiListMap() {
        return compoundNameToCuiListMap;
    }
    
    public void setCompoundNameToCuiListMap(String name, String cui) {
        compoundNameToCuiListMap = Util.setMap(compoundNameToCuiListMap, name, cui);        
    }
    
    public Map<String, List<String>> getCuiToNameListMap() {
        return cuiToNameListMap;
    }
    
    public Map<String, List<String>> getStemmedNameToCuiListMap() {
        return stemmedNameToCuiListMap;
    }
    
    public Map<String, List<String>> getCuiToStemmedNameListMap() {
        return cuiToStemmedNameListMap;
    }
    
    public Map<String, List<String>> getCuiAlternateCuiMap() {
        return cuiAlternateCuiMap;
    }
    
    private String get_preferredID_set_altID(String[] identifiers) {
        String preferredID = "";
        boolean set = false;
        List<String> altIDs = new ArrayList<>();
        
        for (int i = 0; i < identifiers.length; i++) {
            if (identifiers[i].contains("OMIM"))
                identifiers[i] = identifiers[i].split(":")[1];
            if (i == 0)
                preferredID = identifiers[i];
            if (Character.isLetter(identifiers[i].charAt(0)) && set == false) {
                preferredID = identifiers[i];                        
                set = true;
                continue;
            }            
            altIDs.add(identifiers[i]);
        }
        
        if (!altIDs.isEmpty()) {
            cuiAlternateCuiMap.put(preferredID, altIDs);
        }
                
        return preferredID;
    }    
    
    private void loadMaps(String conceptName, String cui) {        
        nameToCuiListMap = Util.setMap(nameToCuiListMap, conceptName, cui);
        cuiToNameListMap = Util.setMap(cuiToNameListMap, cui, conceptName);

        String stemmedConceptName = Ling.getStemmedPhrase(conceptName);
        stemmedNameToCuiListMap = Util.setMap(stemmedNameToCuiListMap, stemmedConceptName, cui);
        cuiToStemmedNameListMap = Util.setMap(cuiToStemmedNameListMap, cui, stemmedConceptName);
        
        String[] conceptNameTokens = conceptName.split("\\s");
        for (String conceptNameToken : conceptNameTokens) {
            if (Ling.getStopwordsList().contains(conceptNameToken))
                continue;
            tokenToNameListMap = Util.setMap(tokenToNameListMap, conceptNameToken, conceptName);
        } 
        
        if (Main.training_data_dir.toString().contains("semeval")) {
            CompoundPhraseSieve.setCompoundNameTerminology(this, conceptName, conceptNameTokens, cui);        
        }
        else if (cui.contains("|")) {
            nameToCuiListMap.remove(conceptName);
            stemmedNameToCuiListMap.remove(stemmedConceptName);
            for (String conceptNameToken : conceptNameTokens) {
                if (Ling.getStopwordsList().contains(conceptNameToken))
                    continue;       
                tokenToNameListMap.get(conceptNameToken).remove(conceptName);
            }
            compoundNameToCuiListMap = Util.setMap(compoundNameToCuiListMap, conceptName, cui);
        }
    }
    
    public void loadTerminology() throws IOException {
        String cui = "";
        try (BufferedReader br = new BufferedReader(new FileReader(terminologyFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals(""))
                    continue;
                String[] token = line.split("\\|\\|");
                
                cui = token[0].contains("|") ? get_preferredID_set_altID(token[0].split("\\|")) : token[0];
                
                String[] conceptNames = token[1].toLowerCase().split("\\|");
                
                for (String conceptName : conceptNames)
                    loadMaps(conceptName, cui);
            }
        }
    }        
    
    private void setOMIM(String cuis, String MeSHorSNOMEDcuis, String conceptName) {
        if (MeSHorSNOMEDcuis.equals("")) {
            cuis = cuis.replaceAll("OMIM:", "");
            loadMaps(conceptName, cuis);
        }
        else {
            String[] cuis_arr = cuis.split("\\|");
            for (String cui : cuis_arr) {
                if (!cui.contains("OMIM"))
                    continue;
                cui = cui.split(":")[1];
                cuiAlternateCuiMap = Util.setMap(cuiAlternateCuiMap, MeSHorSNOMEDcuis, cui);
            }
        }
    }
    
    public static List<String> getOMIMCuis(String[] cuis) {
        List<String> OMIMcuis = new ArrayList<>();
        for (String cui : cuis) {
            if (!cui.contains("OMIM"))
                continue;
            cui = cui.split(":")[1];
            OMIMcuis = Util.setList(OMIMcuis, cui);
        }
        return OMIMcuis;
    }
    
    public static String getMeSHorSNOMEDCuis(String[] cuis) {
        String cuiStr = "";
        for (String cui : cuis) {
            if (cui.contains("OMIM"))
                continue;
            cuiStr = cuiStr.equals("") ? cui : cuiStr+"|"+cui;
        }
        return cuiStr;
    }
    
    public void loadTrainingDataTerminology(File dir) throws IOException {
        Map<String, List<String>> cuiNamesMap = new HashMap<>();
        for (File file : dir.listFiles()) {
            if (!file.toString().contains(".concept"))
                continue;            
            cuiNamesMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();                    
                    String[] tokens = line.split("\\|\\|");
                    String conceptName = Ling.correctSpelling(tokens[3].toLowerCase().trim());                     
                    String[] cuis = tokens[4].contains("+") ? tokens[4].split("\\+") : tokens[4].split("\\|");                    
                    String MeSHorSNOMEDcuis = getMeSHorSNOMEDCuis(cuis);
                    if (!MeSHorSNOMEDcuis.equals(""))
                        loadMaps(conceptName, MeSHorSNOMEDcuis);
                    setOMIM(tokens[4], MeSHorSNOMEDcuis, conceptName);
                    String cui = !MeSHorSNOMEDcuis.equals("") ? MeSHorSNOMEDcuis : tokens[4].replaceAll("OMIM:", "");
                    
                    cuiNamesMap = Util.setMap(cuiNamesMap, cui, conceptName);
                    
                    //-------------remove-------------------------------
                    Map<String, List<String>> nameFileListMap = cuiNameFileListMap.get(MeSHorSNOMEDcuis);
                    if (nameFileListMap == null)
                        cuiNameFileListMap.put(MeSHorSNOMEDcuis, nameFileListMap = new HashMap<>());
                    nameFileListMap = Util.setMap(nameFileListMap, conceptName, tokens[0]);
                    //-------------remove-------------------------------
                    
                    List<String> simpleConceptNames = SimpleNameSieve.getTerminologySimpleNames(conceptName.split("\\s+"));
                    for (String simpleConceptName : simpleConceptNames) 
                        simpleNameToCuiListMap = Util.setMap(simpleNameToCuiListMap, simpleConceptName, cui);                    
                    
                }
            }
            if (Main.training_data_dir.toString().contains("ncbi"))
                continue;
            for (String cui : cuiNamesMap.keySet()) {
                List<String> names = cuiNamesMap.get(cui);
                List<String> namesToPrune = new ArrayList<>();
                for (String name : names) {
                    String[] nameTokens = name.split("\\s+");
                    if (nameTokens.length < 3)
                        continue;
                    if (names.contains(nameTokens[0]+" "+nameTokens[1]))
                        namesToPrune = Util.setList(namesToPrune, nameTokens[0]+" "+nameTokens[1]);
                    else if (names.contains(nameTokens[nameTokens.length-2]+" "+nameTokens[nameTokens.length-1]))
                        namesToPrune = Util.setList(namesToPrune, nameTokens[nameTokens.length-2]+" "+nameTokens[nameTokens.length-1]);
                }
                for (String nameToPrune : namesToPrune) {
                    nameToCuiListMap.remove(nameToPrune);
                    cuiToNameListMap.get(cui).remove(nameToPrune);
                    stemmedNameToCuiListMap.remove(Ling.getStemmedPhrase(nameToPrune));
                    cuiToStemmedNameListMap.get(cui).remove(Ling.getStemmedPhrase(nameToPrune));
                    String[] nameToPruneTokens = nameToPrune.split("\\s+");
                    for (String nameToPruneToken : nameToPruneTokens) {
                        if (Ling.getStopwordsList().contains(nameToPruneToken))
                            continue;
                        tokenToNameListMap.get(nameToPruneToken).remove(nameToPrune);
                    }
                }
            }
        }
    }
    
    private static Map<String, List<String>> normalizedNameToCuiListMap = new HashMap<>();
    public static void setNormalizedNameToCuiListMap(String name, String cui) {
        normalizedNameToCuiListMap = Util.setMap(normalizedNameToCuiListMap, name, cui);
    }
    public static Map<String, List<String>> getNormalizedNameToCuiListMap() {
        return normalizedNameToCuiListMap;
    }
    
    private static Map<String, List<String>> stemmedNormalizedNameToCuiListMap = new HashMap<>();
    public static void setStemmedNormalizedNameToCuiListMap(String stemmedName, String cui) {
        stemmedNormalizedNameToCuiListMap = Util.setMap(stemmedNormalizedNameToCuiListMap, stemmedName, cui);
    }
    public static Map<String, List<String>> getStemmedNormalizedNameToCuiListMap() {
        return stemmedNormalizedNameToCuiListMap;
    }        
    
    public static void storeNormalizedConcept(Concept concept) {
        setNormalizedNameToCuiListMap(concept.getNormalizingSieve() == 2 ? concept.getNameExpansion() : concept.getName(), concept.getCui());
        setStemmedNormalizedNameToCuiListMap(concept.getNormalizingSieve() == 2 ? Ling.getStemmedPhrase(concept.getNameExpansion()) : concept.getStemmedName(), concept.getCui());
    }
    
}
