/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.sieves;

import java.util.List;
import tool.util.Ling;
import tool.util.Terminology;
import tool.util.Util;

/**
 *
 * @author
 */
public class CompoundPhraseSieve extends Sieve {
    
    public static String applyNCBI(String name) {
        String cui = apply(name);
        if (!cui.equals("") || (!name.contains(" and ") && !name.contains(" or "))) 
            return cui;
        
        String compoundWord = name.contains(" and ") ? "and" : "or";
        String[] nameTokens = name.split("\\s+");
        int index = Util.getTokenIndex(nameTokens, compoundWord);
        
        if (index == 1) {
            String replacement1 = nameTokens[0];
            String replacement2 = nameTokens[2].equals("the") ? nameTokens[2]+" "+nameTokens[3] : nameTokens[2];
            String phrase = replacement1+" "+compoundWord+" "+replacement2;
            replacement2 = nameTokens[2].equals("the") ? nameTokens[3] : nameTokens[2];            
            String cui1 = exactMatchSieve(name.replace(phrase, replacement1));
                        
            String cui2 = exactMatchSieve(name.replace(phrase, replacement2));
            if (!cui1.equals("") && !cui2.equals("")) {
                return Sieve.getTrainingDataTerminology().getCuiToNameListMap().containsKey(cui2+"|"+cui1) ? cui2+"|"+cui1 : cui1+"|"+cui2;
            }
        }
        return "";        
    }
    
    public static String apply(String name) {
        String cui = getTerminologyNameCui(Sieve.getTrainingDataTerminology().getCompoundNameToCuiListMap(), name);
        if (!cui.equals("")) {
            return cui;
        }
        
        return getTerminologyNameCui(Sieve.getStandardTerminology().getCompoundNameToCuiListMap(), name);   
    }
    
    public static void setCompoundNameTerminology(Terminology terminology, String conceptName, String[] conceptNameTokens, String cui) {
        if (conceptName.contains("and/or")) {
            List<Integer> indexes = Util.getTokenIndexes(conceptNameTokens, "and/or");
            if (indexes.size() == 1) {
                int index = indexes.get(0);                
                if (conceptName.matches("[a-zA-Z]+, [a-zA-Z]+ and/or [a-zA-Z]+.*")) {
                    String replacement1 = conceptNameTokens[index-2].replace(",", "");
                    String replacement2 = conceptNameTokens[index-1];
                    String replacement3 = conceptNameTokens[index+1];
                    String phrase = replacement1+", "+replacement2+" "+conceptNameTokens[index]+" "+replacement3;        
                    
                    terminology.setCompoundNameToCuiListMap(conceptName.replace(phrase, replacement1), cui);
                    terminology.setCompoundNameToCuiListMap(conceptName.replace(phrase, replacement2), cui);
                    terminology.setCompoundNameToCuiListMap(conceptName.replace(phrase, replacement3), cui);
                }
                else {
                    String replacement1 = conceptNameTokens[index-1];
                    String replacement2 = conceptNameTokens.length-1 == index+2 ? 
                            conceptNameTokens[index+1]+" "+conceptNameTokens[index+2] : 
                            conceptNameTokens[index+1];
                    String phrase = replacement1+" "+conceptNameTokens[index]+" "+replacement2;        
                    terminology.setCompoundNameToCuiListMap(conceptName.replace(phrase, replacement1), cui);
                    terminology.setCompoundNameToCuiListMap(conceptName.replace(phrase, replacement2), cui);
                }
            }
        }
    }
    
}
