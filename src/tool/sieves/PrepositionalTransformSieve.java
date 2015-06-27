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
 * @author Jen
 */
public class PrepositionalTransformSieve extends Sieve {
    
    public static String apply(Concept concept) {
        PrepositionalTransformSieve.init(concept);
        transformName(concept);
        return normalize(concept.getNamesKnowledgeBase());
    }
    
    public static void init(Concept concept) {
        concept.setNamesKnowledgeBase(concept.getName());
        if (!concept.getNameExpansion().equals(""))
            concept.setNamesKnowledgeBase(concept.getNameExpansion());
    }
           
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();
        
        for (String nameForTransformation : namesForTransformation) {
            String prepositionInName = Ling.getStringPreposition(nameForTransformation);            
            //if the phrase has a preposition, we:
            //1. create new phrases by substituting with other prepositions
            //2. removing the preposition in the phrase and swapping the surrounding parts of the string
            if (!prepositionInName.equals("")) {
                transformedNames = Util.addUnique(transformedNames, substitutePrepositionsInPhrase(prepositionInName, nameForTransformation));
                transformedNames = Util.setList(transformedNames, swapPhrasalSubjectAndObject(prepositionInName, nameForTransformation.split("\\s+")));
            }
            //if the phrase does not have a preposition, we:
            //generate new phrases by inserting prepositions,
            //1. near the beginning and 
            //2. near the end of the string.
            else {
                transformedNames = Util.addUnique(transformedNames, insertPrepositionsInPhrase(nameForTransformation, nameForTransformation.split("\\s+")));
            }
        }   
        concept.setNamesKnowledgeBase(transformedNames);   
    }
    
    public static List<String> insertPrepositionsInPhrase(String phrase, String[] phraseTokens) {
        
        List<String> newPrepositionalPhrases = new ArrayList<>();
        for (String preposition : Ling.PREPOSITIONS) {
            //insert preposition near the end of the string
            String newPrepositionalPhrase = (Ling.getSubstring(phraseTokens, 1, phraseTokens.length)+" "+preposition+" "+phraseTokens[0]).trim();
            newPrepositionalPhrases = Util.setList(newPrepositionalPhrases, newPrepositionalPhrase);
            //insert preposition near the beginning of the string
            newPrepositionalPhrase = (phraseTokens[phraseTokens.length-1]+" "+preposition+" "+Ling.getSubstring(phraseTokens, 0, phraseTokens.length-1)).trim();
            newPrepositionalPhrases = Util.setList(newPrepositionalPhrases, newPrepositionalPhrase);
        }        
        return newPrepositionalPhrases;
    }    
    
    private static List<String> substitutePrepositionsInPhrase(String prepositionInPhrase, String phrase) {
        List<String> newPrepositionalPhrases = new ArrayList<>();
        for (String preposition : Ling.PREPOSITIONS) {
            if (preposition.equals(prepositionInPhrase))
                continue;
            String newPrepositionalPhrase = (phrase.replace(" "+prepositionInPhrase+" ", " "+preposition+" ")).trim();
            newPrepositionalPhrases = Util.setList(newPrepositionalPhrases, newPrepositionalPhrase);
        }
        return newPrepositionalPhrases;
    }     
    
    private static String swapPhrasalSubjectAndObject(String prepositionInPhrase, String[] phraseTokens) {
        int prepositionTokenIndex = Util.getTokenIndex(phraseTokens, prepositionInPhrase);
        return prepositionTokenIndex != -1 ? (Ling.getSubstring(phraseTokens, prepositionTokenIndex+1, phraseTokens.length)+" "+
                Ling.getSubstring(phraseTokens, 0, prepositionTokenIndex)).trim() : "";
    }    
    
}
