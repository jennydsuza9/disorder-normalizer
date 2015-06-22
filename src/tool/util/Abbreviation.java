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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tool.util.Util;

/**
 *
 * @author
 */
public class Abbreviation {
    
    private static Map<String, List<String>> wikiAbbreviationExpansionListMap = new HashMap<>();    
    
    //reads a file line by line
    //each line is double bar delimited containing an abbreviation and its expansion
    //stores all abbreviations in lowercase
    public static void setWikiAbbreviationExpansionMap(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));        
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] token = s.split("\\|\\|");
            token[0] = token[0].toLowerCase();
            wikiAbbreviationExpansionListMap = Util.setMap(wikiAbbreviationExpansionListMap, token[0], token[1].toLowerCase());
        }
    }    
    public static Map<String, List<String>> getAbbreviationMap() {
        return wikiAbbreviationExpansionListMap;
    }    
 
    private Map<String, String> textAbbreviationExpansionMap = new HashMap<>();
    public static String getExpansionByHearstAlgorithm(String shortForm, String longForm) {
        int sIndex;
        int lIndex;
        char currChar;
                
        sIndex = shortForm.length() - 1;
        lIndex = longForm.length() - 1;
        
        for ( ; sIndex >= 0; sIndex--) {
            currChar = Character.toLowerCase(shortForm.charAt(sIndex));
            if (!Character.isLetterOrDigit(currChar))
                continue;
            
            while (((lIndex >= 0) && 
                    (Character.toLowerCase(longForm.charAt(lIndex)) != currChar)) ||
                    ((sIndex == 0) && (lIndex > 0) && 
                    (Character.isLetterOrDigit(longForm.charAt(lIndex-1)))))
                lIndex--;
            if (lIndex < 0)
                return "";
            lIndex--;
        }
                
        lIndex = longForm.lastIndexOf(" ", lIndex) + 1;
        longForm = longForm.substring(lIndex);    
        
        return longForm;
    }    
    public static String getTentativeExpansion(String[] tokens, int i, int abbreviationLength) {
        String expansion = "";
        while (i >= 0 && abbreviationLength > 0) {
            expansion = tokens[i]+" "+expansion;
            i--;
            abbreviationLength--;
        }
        return expansion.trim();
    }    
    private void setTextAbbreviationExpansionMap(String[] tokens, int abbreviationLength, String abbreviation, int expansionIndex) {
        String expansion = getTentativeExpansion(tokens, expansionIndex, abbreviationLength);
        expansion = Ling.correctSpelling(getExpansionByHearstAlgorithm(abbreviation, expansion).toLowerCase()).trim();        
        if (!expansion.equals(""))
            textAbbreviationExpansionMap.put(abbreviation, expansion);
    }    
    public void setTextAbbreviationExpansionMap (File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));        
        while (in.ready()) {
            String s = in.readLine().trim().replaceAll("\\s+", " ");             
            String[] tokens = s.split("\\s");
            int size = tokens.length;
            for (int i = 0; i < size; i++) {
                int expansionIndex = -1;                
                
                if (tokens[i].matches("\\(\\w+(\\-\\w+)?\\)(,|\\.)?") || tokens[i].matches("\\([A-Z]+(;|,|\\.)")) 
                    expansionIndex = i-1;
                else if (tokens[i].matches("[A-Z]+\\)")) 
                    expansionIndex = Util.firstIndexOf(tokens, i, "\\(");
                
                if (expansionIndex == -1)
                    continue;
                
                String abbreviation = tokens[i].replace("(", "").replace(")", "").toLowerCase();
                String reversedAbbreviation = Ling.reverse(abbreviation);
                
                if (abbreviation.charAt(abbreviation.length()-1) == ',' || 
                        abbreviation.charAt(abbreviation.length()-1) == '.' || 
                        abbreviation.charAt(abbreviation.length()-1) == ';')
                    abbreviation = abbreviation.substring(0, abbreviation.length()-1);
                                
                if (textAbbreviationExpansionMap.containsKey(abbreviation) || textAbbreviationExpansionMap.containsKey(reversedAbbreviation))
                    continue;

                int abbreviationLength = abbreviation.length();                
                setTextAbbreviationExpansionMap(tokens, abbreviationLength, abbreviation, expansionIndex);
                if (!textAbbreviationExpansionMap.containsKey(abbreviation))
                    setTextAbbreviationExpansionMap(tokens, abbreviationLength, reversedAbbreviation, expansionIndex);
            }
        }
    }
    public Map<String, String> getTextAbbreviationExpansionMap() {
        return textAbbreviationExpansionMap;
    }
        
    public static String getBestExpansion(String text, List<String> expansionList) {
        int maxNumberOfContentWords = 0;
        int maxContainedContentWords = 0;
        String returnExpansion = "";
        for (String expansion : expansionList) {
            List<String> expansionContentWordsList = Ling.getContentWordsList(expansion.split("\\s"));
                        
            int tempNumberOfContentWords = expansionContentWordsList.size();
            int tempContainedContentWords = 0;
            for (String expansionContentWord : expansionContentWordsList) {
                if (text.contains(" "+expansionContentWord) || text.contains(expansionContentWord+" "))
                    tempContainedContentWords++;
            }
            
            if (tempNumberOfContentWords > maxNumberOfContentWords && tempContainedContentWords == tempNumberOfContentWords) {
                maxNumberOfContentWords = tempNumberOfContentWords;
                maxContainedContentWords = 1000;
                returnExpansion = expansion;
            }
            else if (tempNumberOfContentWords >= maxNumberOfContentWords && tempContainedContentWords > maxContainedContentWords) {
                maxNumberOfContentWords = tempNumberOfContentWords;
                maxContainedContentWords = tempContainedContentWords;
                returnExpansion = expansion;
            }
            
        }
        return returnExpansion;
    }
    
    public static String getAbbreviationExpansion(Abbreviation abbreviationObject, String text, String string, String indexes) {
        Map<String, String> shortForm_longForm_map = abbreviationObject.getTextAbbreviationExpansionMap();
        String[] stringTokens = string.split("\\s");
        if (stringTokens.length == 1 && stringTokens[0].length() == 1) 
            stringTokens[0] = getEntireAbbreviation(text, string, indexes.split("\\|"));
        String newString = "";
        
        for (String stringToken : stringTokens) {
            if (shortForm_longForm_map != null && shortForm_longForm_map.containsKey(stringToken)) {
                newString += shortForm_longForm_map.get(stringToken)+" ";
                continue;
            }
            
            List<String> candidateExpansionsList = wikiAbbreviationExpansionListMap.containsKey(stringToken) ? wikiAbbreviationExpansionListMap.get(stringToken) : null;
            
            if (candidateExpansionsList == null)
                newString += stringToken + " ";
            else {
                String expansion = candidateExpansionsList.size() == 1 ? candidateExpansionsList.get(0) : getBestExpansion(text, candidateExpansionsList);                
                if (expansion.equals(""))
                    newString += stringToken + " ";
                else
                    newString += expansion + " ";
            }
        }        
        
        if (stringTokens.length == 1 && !stringTokens[0].equals(string)) 
            newString = getTrimmedExpansion(text, string, indexes.split("\\|"), newString.split("/"));
        
        newString = newString.trim();
        return newString.equals(string) ? "" : newString;
    }      
    
    public static String getEntireAbbreviation(String text, String string, String[] indexes) {
        if (indexes.length != 2)
            return string;
        int begin = Integer.parseInt(indexes[0]);
        int end = Integer.parseInt(indexes[1]);
        if (text.substring(begin-3, end+3).toLowerCase().matches("(^|\\s|\\W)[a-zA-Z]/"+string+"/[a-zA-Z](\\s|$|\\W)"))
            return text.substring(begin-2, end+2).toLowerCase();
        else if (text.substring(begin-1, end+5).toLowerCase().matches("(^|\\s|\\W)"+string+"/[a-zA-Z]/[a-zA-Z](\\s|$|\\W)"))
            return text.substring(begin, end+4).toLowerCase();
        else if (text.substring(begin-5, end+1).toLowerCase().matches("(^|\\s|\\W)[a-zA-Z]/[a-zA-Z]/"+string+"(\\s|$|\\W)"))
            return text.substring(begin-4, end).toLowerCase();
        return string;
    }
    
    public static String getTrimmedExpansion(String text, String string, String[] indexes, String[] expansion) {
        if (indexes.length != 2)
            return string;
        int begin = Integer.parseInt(indexes[0]);
        int end = Integer.parseInt(indexes[1]);
        if (text.substring(begin-3, end+3).toLowerCase().matches("(^|\\s|\\W)[a-zA-Z]/"+string+"/[a-zA-Z](\\s|$|\\W)"))
            return expansion[1].toLowerCase();
        else if (text.substring(begin-1, end+5).toLowerCase().matches("(^|\\s|\\W)"+string+"/[a-zA-Z]/[a-zA-Z](\\s|$|\\W)"))
            return expansion[0].toLowerCase();
        else if (text.substring(begin-5, end+1).toLowerCase().matches("(^|\\s|\\W)[a-zA-Z]/[a-zA-Z]/"+string+"(\\s|$|\\W)"))
            return expansion[2].toLowerCase();
        return string;        
    }
    
}
