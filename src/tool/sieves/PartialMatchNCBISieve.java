/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.ArrayList;
import java.util.HashMap;
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
public class PartialMatchNCBISieve {
    
    public static String apply(Concept concept) {
        String name = concept.getName();
        String[] nameTokens = name.split("\\s+");
        return partialMatch(name, nameTokens);
    }      
        
    private static String partialMatch(String phrase, String[] phraseTokens) {
        List<String> partialMatchedPhrases = new ArrayList<>();
        Map<Integer, Map<String, Integer>> candidateCuiDataMap = init();
        
        for (String phraseToken : phraseTokens) {
            if (Ling.getStopwordsList().contains(phraseToken))
                continue;
            List<String> candidatePhrases = null;
            int map = -1;
            
            if (Sieve.getTrainingDataTerminology().getTokenToNameListMap().containsKey(phraseToken)) {
                candidatePhrases = new ArrayList<>(Sieve.getTrainingDataTerminology().getTokenToNameListMap().get(phraseToken));
                map = 2;
            }
            else if (Sieve.getStandardTerminology().getTokenToNameListMap().containsKey(phraseToken)) {
                candidatePhrases = new ArrayList<>(Sieve.getStandardTerminology().getTokenToNameListMap().get(phraseToken));
                map = 3;
            }
            
            if (candidatePhrases == null)
                continue;
                        
            candidatePhrases.removeAll(partialMatchedPhrases);
            
            candidateCuiDataMap = ncbiPartialMatch(phrase, candidatePhrases, partialMatchedPhrases, map == 2 ? Sieve.getTrainingDataTerminology() : Sieve.getStandardTerminology(), candidateCuiDataMap);
        }        
        return !candidateCuiDataMap.get(1).isEmpty() ? getCui(candidateCuiDataMap.get(1), candidateCuiDataMap.get(2)) : "";
    }     
    
    private static Map<Integer, Map<String, Integer>> init() {
        Map<Integer, Map<String, Integer>> candidateCuiDataMap = new HashMap<>();
        candidateCuiDataMap.put(1, new HashMap<String, Integer>());
        candidateCuiDataMap.put(2, new HashMap<String, Integer>());
        return candidateCuiDataMap;
    }    
    
    private static Map<Integer, Map<String, Integer>> ncbiPartialMatch(String phrase, List<String> candidatePhrases, List<String> partialMatchedPhrases, Terminology terminology, Map<Integer, Map<String, Integer>> cuiCandidateDataMap) {
        Map<String, Integer> cuiCandidateMatchingTokensCountMap = cuiCandidateDataMap.get(1);
        Map<String, Integer> cuiCandidateLengthMap = cuiCandidateDataMap.get(2);
        
        for (String candidatePhrase : candidatePhrases) {
            partialMatchedPhrases = Util.setList(partialMatchedPhrases, candidatePhrase);

            int count = Ling.getMatchingTokensCount(phrase, candidatePhrase);
            int length = candidatePhrase.split("\\s+").length;
            String cui = terminology.getNameToCuiListMap().get(candidatePhrase).get(0);

            if (cuiCandidateMatchingTokensCountMap.containsKey(cui)) {
                int oldCount = cuiCandidateMatchingTokensCountMap.get(cui);
                if (oldCount < count) {
                    cuiCandidateMatchingTokensCountMap.put(cui, count);
                    cuiCandidateLengthMap.put(cui, length);
                }
                continue;
            }

            cuiCandidateMatchingTokensCountMap.put(cui, count);
            cuiCandidateLengthMap.put(cui, length);
        }                    
        
        cuiCandidateDataMap.put(1, cuiCandidateMatchingTokensCountMap);
        cuiCandidateDataMap.put(2, cuiCandidateLengthMap);
        return cuiCandidateDataMap;
    }    
    
    private static String getCui(Map<String, Integer> cuiCandidateMatchedTokensCountMap, Map<String, Integer> cuiCandidateLengthMap) {
        String cui = "";
        int maxMatchedTokensCount = -1;
        Map<Integer, List<String>> matchedTokensCountCuiListMap = new HashMap<>();
        for (String candidateCui : cuiCandidateMatchedTokensCountMap.keySet()) {
            int matchedTokensCount = cuiCandidateMatchedTokensCountMap.get(candidateCui);
            if (matchedTokensCount >= maxMatchedTokensCount) {
                maxMatchedTokensCount = matchedTokensCount;                
                
                List<String> cuiList = matchedTokensCountCuiListMap.get(matchedTokensCount);
                if (cuiList == null) 
                    matchedTokensCountCuiListMap.put(matchedTokensCount, cuiList = new ArrayList<>());
                cuiList = Util.setList(cuiList, candidateCui);
            }
        }
        List<String> candidateCuiList = matchedTokensCountCuiListMap.get(maxMatchedTokensCount);
        if (candidateCuiList.size() == 1)
            return candidateCuiList.get(0);
        else {
            int minCandidateLength = 1000;
            for (String candidateCui : candidateCuiList) {
                int length = cuiCandidateLengthMap.get(candidateCui);
                if (length < minCandidateLength) {
                    minCandidateLength = length;
                    cui = candidateCui;
                }
            }
        }        
        return cui;
    }            
    
}
