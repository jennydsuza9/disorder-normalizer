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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tool.util.Util;

/**
 *
 * @author
 */
public class Ling {
    
    private static List<String> stopwords = new ArrayList<>();
    public static void setStopwordsList(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            if (s.equals(""))
                continue;
            stopwords = Util.setList(stopwords, s);
        }
    }
    public static List<String> getStopwordsList() {
        return stopwords;
    }
    
    public static String getStemmedPhrase(String string) {
        String stemmed_name = "";
        String[] str_tokens = string.split("\\s");
        for (String token : str_tokens) {
            if (stopwords.contains(token)) {
                stemmed_name += token + " ";
                continue;
            }
            String stemmed_token = PorterStemmer.get_stem(token).trim();
            if (stemmed_token.equals(""))
                stemmed_token = token;
            stemmed_name += stemmed_token + " ";
        }
        stemmed_name = stemmed_name.trim();
        return stemmed_name;
    }
    
    private static Map<String, String> spellingCorrectionMap = new HashMap<>();
    public static void setSpellingCorrectionMap(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] tokens = s.split("\\|\\|");
            String value = tokens.length == 1 ? "" : tokens[1];
            spellingCorrectionMap.put(tokens[0], value);
        }
    }
    public static Map<String, String> getSpellingCorrectionMap() {
        return spellingCorrectionMap;
    }
    public static String correctSpelling(String phrase) {
        String[] phrase_tokens = phrase.split("\\s+");
        phrase = "";
        for (String phrase_token : phrase_tokens) {
            phrase_token = spellingCorrectionMap.containsKey(phrase_token) ? spellingCorrectionMap.get(phrase_token) : phrase_token;
            phrase += phrase_token + " ";
        }
        phrase = phrase.trim();
        return phrase;
    }
    
    public static String reverse(String string) {
        String reversedString = "";
        int size = string.length()-1;
        for (int i = size; i >= 0; i--) {
            reversedString += string.charAt(i);
        }
        return reversedString;
    }    
 
    public static List<String> getContentWordsList(String[] words) {
        List<String> contentWordsList = new ArrayList<>();
        for (String word : words) {
            if (stopwords.contains(word))
                continue;
            contentWordsList = Util.setList(contentWordsList, word);
        }
        return contentWordsList;
    }
    
    public static final List<String> PREPOSITIONS = Arrays.asList("in", "with", "on", "of");
    public static String getStringPreposition(String string) {
        for (String preposition : Ling.PREPOSITIONS) {
            if (string.contains(" "+preposition+" ")) 
                return preposition;
        }
        return "";
    }    
    
    public static String getSubstring(String[] tokens, int begin, int end) {
        String substring = "";
        for (int i = begin; i < end; i++) {
            substring += tokens[i]+" ";
        }
        substring = substring.trim();
        return substring;
    } 
    
    private static Map<String, List<String>> digitToWordMap = new HashMap<>();
    private static Map<String, String> wordToDigitMap = new HashMap<>();    
    public static void setDigitToWordformMapAndReverse(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] tokens = s.split("\\|\\|");
            digitToWordMap = Util.setMap(digitToWordMap, tokens[0], tokens[1]);
            wordToDigitMap.put(tokens[1], tokens[0]);
        }
    }
    public static Map<String, List<String>> getDigitToWordMap() {
        return digitToWordMap;
    }
    public static Map<String, String> getWordToDigitMap() {
        return wordToDigitMap;
    }
    
    public static final List<String> SINGULAR_DISORDER_SYNONYMS = Arrays.asList("disease", "disorder", "condition", "syndrome", "symptom",
            "abnormality", "NOS", "event", "episode", "issue", "impairment");
    public static final List<String> PLURAL_DISORDER_SYNONYMS = Arrays.asList("diseases", "disorders", "conditions", "syndromes", "symptoms",
            "abnormalities", "events", "episodes", "issues", "impairments");    
    
    private static Map<String, List<String>> suffixMap = new HashMap<>();
    private static Map<String, String> prefixMap = new HashMap<>();
    public static final String AFFIX = "ganglioma|cancer";
    private static Map<String, String> affixMap = new HashMap<>();
    
    public static void setSuffixMap(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] tokens = s.split("\\|\\|");
            if (tokens.length == 1) {
                List<String> values = suffixMap.get(tokens[0]);
                if (values == null)
                    suffixMap.put(tokens[0], values = new ArrayList<>());
            }
            else
                suffixMap = Util.setMap(suffixMap, tokens[0], tokens[1]);
        }
    }
    public static Map<String, List<String>> getSuffixMap() {
        return suffixMap;
    }
    public static String getSuffix(String str, int len) {
        if (str.length() < len) {
            return "";
        }
        return str.substring(str.length() - len);
    }      
    public static String getSuffix(String str) {
        return suffixMap.containsKey(getSuffix(str, 10)) ? getSuffix(str, 10) :
                suffixMap.containsKey(getSuffix(str, 7)) ? getSuffix(str, 7) : 
                suffixMap.containsKey(getSuffix(str, 6)) ? getSuffix(str, 6) : 
                suffixMap.containsKey(getSuffix(str, 5)) ? getSuffix(str, 5) : 
                suffixMap.containsKey(getSuffix(str, 4)) ? getSuffix(str, 4) : 
                suffixMap.containsKey(getSuffix(str, 3)) ? getSuffix(str, 3) : 
                suffixMap.containsKey(getSuffix(str, 2)) ? getSuffix(str, 2) : ""; 
    }
    
    public static void setPrefixMap(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] tokens = s.split("\\|\\|");
            String value = tokens.length == 1 ? "" : tokens[1];
            prefixMap.put(tokens[0], value);
        }
    }
    public static Map<String, String> getPrefixMap() {
        return prefixMap;
    }
    public static String getPrefix(String str, int len) {
        if (str.length() < len) {
            return "";
        }
        return str.substring(0, len);
    }    
    public static String getPrefix(String str) {
        return prefixMap.containsKey(getPrefix(str, 5)) ? getPrefix(str, 5) : 
                prefixMap.containsKey(getPrefix(str, 4)) ? getPrefix(str, 4) : 
                prefixMap.containsKey(getPrefix(str, 3)) ? getPrefix(str, 3) : "";            
    }
        
    public static void setAffixMap(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));    
        while (in.ready()) {
            String s = in.readLine().trim();
            String[] tokens = s.split("\\|\\|");
            String value = tokens.length == 1 ? "" : tokens[1];
            affixMap.put(tokens[0], value);
        }
    }
    public static Map<String, String> getAffixMap() {
        return affixMap;
    }    
    
    public static boolean exactTokenMatch(String phrase1, String phrase2) {
        List<String> tokens = new ArrayList<>(Arrays.asList(phrase1.split("\\s+")));
        tokens.removeAll(new ArrayList<>(Arrays.asList(phrase2.split("\\s+"))));
        return tokens.isEmpty() && phrase1.split("\\s+").length == phrase2.split("\\s+").length ? true : false;
    }
    
    public static int getMatchingTokensCount(String phrase1, String phrase2) {
        List<String> tokens = new ArrayList<>(Arrays.asList(phrase1.split("\\s+")));
        tokens.retainAll(new ArrayList<>(Arrays.asList(phrase2.split("\\s+"))));
        tokens.removeAll(Ling.stopwords);
        return tokens.isEmpty() ? 0 : tokens.size();
    }
}
