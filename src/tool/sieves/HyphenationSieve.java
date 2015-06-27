/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.ArrayList;
import java.util.List;
import tool.util.Concept;
import tool.util.Util;

/**
 *
 * @author
 */
public class HyphenationSieve extends Sieve{
    
    public static String apply(Concept concept) {
        transformName(concept);
        return normalize(concept.getNamesKnowledgeBase());
    }      
 
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();        
        
        for (String nameForTransformation : namesForTransformation) {
            transformedNames = Util.addUnique(transformedNames, hyphenateString(nameForTransformation.split("\\s+")));
            transformedNames = Util.addUnique(transformedNames, dehyphenateString(nameForTransformation.split("\\-")));
        }
        
        concept.setNamesKnowledgeBase(transformedNames);   
    }    
    
    public static List<String> hyphenateString(String[] stringTokens) {
        List<String> hyphenatedStrings = new ArrayList<>();
        for (int i = 1; i < stringTokens.length; i++) {
            String hyphenatedString = "";
            for (int j = 0; j < stringTokens.length; j++) {
                if (j == i)
                    hyphenatedString += "-"+stringTokens[j];
                else 
                    hyphenatedString = hyphenatedString.equals("") ? stringTokens[j] : hyphenatedString+" "+stringTokens[j];
            }
            hyphenatedStrings = Util.setList(hyphenatedStrings, hyphenatedString);
        }
        return hyphenatedStrings;
    }    
    
    public static List<String> dehyphenateString(String[] stringTokens) {
        List<String> dehyphenatedStrings = new ArrayList<>();
        for (int i = 1; i < stringTokens.length; i++) {
            String dehyphenatedString = "";
            for (int j = 0; j < stringTokens.length; j++) {
                if (j == i)
                    dehyphenatedString += " "+stringTokens[j];
                else
                    dehyphenatedString = dehyphenatedString.equals("") ? stringTokens[j] : dehyphenatedString+"-"+stringTokens[j];
            }
            dehyphenatedStrings = Util.setList(dehyphenatedStrings, dehyphenatedString);
        }
        return dehyphenatedStrings;
    }    
    
}
