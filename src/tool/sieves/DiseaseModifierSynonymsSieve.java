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
public class DiseaseModifierSynonymsSieve extends Sieve {
    
    public static String apply(Concept concept) {
        if (!Ling.PLURAL_DISORDER_SYNONYMS.contains(concept.getName()) && !Ling.SINGULAR_DISORDER_SYNONYMS.contains(concept.getName())) {
            transformName(concept);
            return normalize(concept.getNamesKnowledgeBase());
        }
        return "";
    }  
    
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();        
        
        for (String nameForTransformation : namesForTransformation) {
            String[] nameForTransformationTokens = nameForTransformation.split("\\s+");
            String modifier = getModifier(nameForTransformationTokens, Ling.PLURAL_DISORDER_SYNONYMS);
            if (!modifier.equals("")) {
                transformedNames = Util.addUnique(transformedNames, substituteDiseaseModifierWithSynonyms(nameForTransformation, modifier, Ling.PLURAL_DISORDER_SYNONYMS));
                transformedNames = Util.setList(transformedNames, deleteTailModifier(nameForTransformationTokens, modifier));
                continue;
            }
                      
            modifier = getModifier(nameForTransformationTokens, Ling.SINGULAR_DISORDER_SYNONYMS);
            if (!modifier.equals("")) {
                transformedNames = Util.addUnique(transformedNames, substituteDiseaseModifierWithSynonyms(nameForTransformation, modifier, Ling.SINGULAR_DISORDER_SYNONYMS));
                transformedNames = Util.setList(transformedNames, deleteTailModifier(nameForTransformationTokens, modifier));
                continue;
            }            
            transformedNames = Util.addUnique(transformedNames, appendModifier(nameForTransformation, Ling.SINGULAR_DISORDER_SYNONYMS));
        }
        
        concept.setNamesKnowledgeBase(transformedNames);   
    }
    
    public static List<String> appendModifier(String string, List<String> modifiers) {
        List<String> newPhrases = new ArrayList<>();
        for (String modifier : modifiers) {
            String newPhrase = string + " " + modifier;
            newPhrases = Util.setList(newPhrases, newPhrase);
        }
        return newPhrases;                
    }

    public static String deleteTailModifier(String[] stringTokens, String modifier) {
        return stringTokens[stringTokens.length-1].equals(modifier) ? Ling.getSubstring(stringTokens, 0, stringTokens.length-1) : "";
    }
    
    public static List<String> substituteDiseaseModifierWithSynonyms(String string, String toReplaceWord, List<String> synonyms) {
        List<String> newPhrases = new ArrayList<>();
        for (String synonym : synonyms) {
            if (toReplaceWord.equals(synonym)) 
                continue;
            String newPhrase = string.replace(toReplaceWord, synonym);
            newPhrases = Util.setList(newPhrases, newPhrase);
        }
        return newPhrases;        
    }     
    
    public static String getModifier(String[] stringTokens, List<String> modifiers) {
        for (String modifier : modifiers) {
            int index = Util.getTokenIndex(stringTokens, modifier);
            if (index != -1)
                return stringTokens[index];
        }
        return "";
    }
    
}
