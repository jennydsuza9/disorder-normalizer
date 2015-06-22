/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.ArrayList;
import java.util.List;
import tool.util.Concept;
import tool.util.Ling;
import tool.util.Util;

/**
 *
 * @author
 */
public class SymbolReplacementSieve extends Sieve {
    
    public static String apply(Concept concept) {
        transformName(concept);
        return normalize(concept.getNamesKnowledgeBase());
    }     
    
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();        
        
        for (String nameForTransformation : namesForTransformation) {
            transformedNames = Util.addUnique(transformedNames, substituteSymbolsInStringWithWords(nameForTransformation));
            transformedNames = Util.addUnique(transformedNames, substituteWordsInStringWithSymbols(nameForTransformation));
        }
        
        concept.setNamesKnowledgeBase(transformedNames);   
    }
    
    public static List<String> getClinicalReportTypeSubstitutions(String string) {
        List<String> newStrings = new ArrayList<>();
        for (String digit : Ling.getDigitToWordMap().keySet()) {
            if (!string.contains(digit)) 
                continue;
            List<String> wordsList = Ling.getDigitToWordMap().get(digit);
            for (String word : wordsList) {
                String newString = string.replaceAll(digit, word);
                if (!newString.equals(string))
                    newStrings = Util.setList(newStrings, newString);
            }
        }        
        return newStrings;
    }
    
    public static String getBiomedicalTypeSubstitutions(String string) {
        if (string.contains("and/or")) 
            string = string.replaceAll("and/or", "and");
        if (string.contains("/"))
            string = string.replaceAll("/", " and ");
        if (string.contains(" (") && string.contains(")"))
            string = string.replace(" (", "").replace(")", "");
        else if (string.contains("(") && string.contains(")"))
            string = string.replace("(", "").replace(")", "");
        return string;
    }
    
    public static List<String> substituteSymbolsInStringWithWords(String string) {
        List<String> newStrings = getClinicalReportTypeSubstitutions(string);
        List<String> tempNewStrings = new ArrayList<>();
        for (String newString : newStrings)
            tempNewStrings = Util.setList(tempNewStrings, getBiomedicalTypeSubstitutions(newString));        
        newStrings = Util.addUnique(newStrings, tempNewStrings);
        newStrings = Util.setList(newStrings, getBiomedicalTypeSubstitutions(string));        
        return newStrings;
    }
    
    public static List<String> substituteWordsInStringWithSymbols(String string) {
        List<String> newStrings = new ArrayList<>();
        for (String word : Ling.getWordToDigitMap().keySet()) {
            if (!string.contains(word))
                continue;
            String digit = Ling.getWordToDigitMap().get(word);
            String newString = string.replaceAll(word, digit);
            if (!newString.equals(string))
                newStrings = Util.setList(newStrings, newString);
        }
        return newStrings;
    }
        
}
