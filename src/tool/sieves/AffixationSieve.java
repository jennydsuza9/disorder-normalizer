/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import tool.util.Concept;
import tool.util.Ling;
import tool.util.Util;

/**
 *
 * @author
 */
public class AffixationSieve extends Sieve {
    
    public static String apply(Concept concept) {
        transformName(concept);
        return normalize(concept.getNamesKnowledgeBase());
    }  
    
    private static void transformName(Concept concept) {
        List<String> namesForTransformation = new ArrayList<>(concept.getNamesKnowledgeBase());
        List<String> transformedNames = new ArrayList<>();
        
        for (String nameForTransformation : namesForTransformation) {
            transformedNames = Util.addUnique(transformedNames, affix(nameForTransformation));
        }
        
        concept.setNamesKnowledgeBase(transformedNames);        
    }
    
    public static List<String> getAllStringTokenSuffixationCombinations(String[] stringTokens) {
        List<String> suffixatedPhrases = new ArrayList<>();
        for (String stringToken : stringTokens) {            
            String suffix = Ling.getSuffix(stringToken); 
            List<String> forSuffixation = suffix.equals("") ? null : Ling.getSuffixMap().get(suffix);
                        
            if (suffixatedPhrases.isEmpty()) {
                if (forSuffixation == null)
                    suffixatedPhrases = Util.setList(suffixatedPhrases, stringToken);
                else if (forSuffixation.isEmpty())
                    suffixatedPhrases = Util.setList(suffixatedPhrases, stringToken.replace(suffix, ""));
                else {
                    for (int i = 0; i < forSuffixation.size(); i++) 
                        suffixatedPhrases = Util.setList(suffixatedPhrases, stringToken.replace(suffix, forSuffixation.get(i)));
                }
            }
            else {
                if (forSuffixation == null) {
                    for (int i = 0; i < suffixatedPhrases.size(); i++) 
                        suffixatedPhrases.set(i, suffixatedPhrases.get(i)+" "+stringToken);
                }
                else if (forSuffixation.isEmpty()) {
                    for (int i = 0; i < suffixatedPhrases.size(); i++) 
                        suffixatedPhrases.set(i, suffixatedPhrases.get(i)+" "+stringToken.replace(suffix, ""));  
                }
                else {
                    List<String> tempSuffixatedPhrases = new ArrayList<>();
                    for (int i = 0; i < suffixatedPhrases.size(); i++) {
                        String suffixatedPhrase = suffixatedPhrases.get(i);
                        for (int j = 0; j < forSuffixation.size(); j++) 
                            tempSuffixatedPhrases = Util.setList(tempSuffixatedPhrases, suffixatedPhrase+" "+stringToken.replace(suffix, forSuffixation.get(j)));
                    }
                    suffixatedPhrases = new ArrayList<>(tempSuffixatedPhrases);
                    tempSuffixatedPhrases = null;
                }                
            }
        }
        return suffixatedPhrases;
    }
    
    public static List<String> getUniformStringTokenSuffixations(String[] stringTokens, String string) {
        List<String> suffixatedPhrases = new ArrayList<>();
        for (String stringToken : stringTokens) {            
            String suffix = Ling.getSuffix(stringToken); 
            List<String> forSuffixation = suffix.equals("") ? null : Ling.getSuffixMap().get(suffix);
            
            if (forSuffixation == null)
                continue;
            
            if (forSuffixation.isEmpty()) {
                Util.setList(suffixatedPhrases, string.replaceAll(suffix, ""));
                continue;
            }
            for (int i = 0; i < forSuffixation.size(); i++) 
                suffixatedPhrases = Util.setList(suffixatedPhrases, string.replaceAll(suffix, forSuffixation.get(i)));
        }
        return suffixatedPhrases;
    }
    
    public static List<String> suffixation(String[] stringTokens, String string) {
        List<String> suffixatedPhrases = getAllStringTokenSuffixationCombinations(stringTokens);
        return Util.addUnique(suffixatedPhrases, getUniformStringTokenSuffixations(stringTokens, string));
    }    
    
    public static String prefixation(String[] stringTokens, String string) {
        String prefixatedPhrase = "";
        for (String stringToken : stringTokens) {
            String prefix = Ling.getPrefix(stringToken);
            String forPrefixation = prefix.equals("") ? "" : Ling.getPrefixMap().get(prefix);
            prefixatedPhrase = prefixatedPhrase.equals("") ? (prefix.equals("") ? stringToken : stringToken.replace(prefix, forPrefixation)) :
                    (prefix.equals("") ? prefixatedPhrase+" "+stringToken : prefixatedPhrase+" "+stringToken.replace(prefix, forPrefixation));
        }
        return prefixatedPhrase;
    }    
    
    public static String affixation(String[] stringTokens, String string) {
        String affixatedPhrase = "";
        for (String stringToken : stringTokens) {
            String affix = stringToken.matches(".*("+Ling.AFFIX+").*") ? 
                    (stringToken.contains(Ling.AFFIX.split("\\|")[0]) ? Ling.AFFIX.split("\\|")[0] : Ling.AFFIX.split("\\|")[1]) : "";
            String forAffixation = affix.equals("") ? "" : Ling.getAffixMap().get(affix);
            affixatedPhrase = affixatedPhrase.equals("") ? (affix.equals("") ? stringToken : stringToken.replace(affix, forAffixation)) :
                    (affix.equals("") ? affixatedPhrase+" "+stringToken : affixatedPhrase+" "+stringToken.replace(affix, forAffixation));
        }
        return affixatedPhrase;
    }    
    
    public static List<String> affix(String string) {
        String[] stringTokens = string.split("\\s");
        List<String> newPhrases = suffixation(stringTokens, string);
        newPhrases = Util.setList(newPhrases, prefixation(stringTokens, string));
        newPhrases = Util.setList(newPhrases, affixation(stringTokens, string));        
        return newPhrases;
    }    
}
