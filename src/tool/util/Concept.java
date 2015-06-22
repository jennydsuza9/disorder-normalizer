/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author
 */
public class Concept {
    
    private String indexes;
    private String name;
    private String nameExpansion;
    private String stemmedName;
    private String goldMeSHorSNOMEDCui;  
    private List<String> goldOMIMCuis;
    private String cui;
    private List<String> alternateCuis;
    private int normalizingSieveLevel = 0;
    private List<String> namesKnowledgeBase = new ArrayList<>();
    private List<String> stemmedNamesKnowledgeBase = new ArrayList<>();
    
    public Concept(String name) {
        this.name = Ling.correctSpelling(name.toLowerCase().trim());
    }
    
    public Concept(String indexes, String name, String goldMeSHorSNOMEDCui, List<String> goldOMIMCuis) {
        this.indexes = indexes;
        this.name = Ling.correctSpelling(name.toLowerCase().trim());
        this.goldMeSHorSNOMEDCui = goldMeSHorSNOMEDCui;
        this.goldOMIMCuis = goldOMIMCuis;
    }
    
    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }
    
    public String getIndexes() {
        return indexes;
    }
    
    public void setName(String name) {
        this.name = Ling.correctSpelling(name.toLowerCase().trim());
    }
    
    public String getName() {
        return name;
    }

    public void setNameExpansion(String text, Abbreviation abbreviationObject) {
        nameExpansion = Abbreviation.getAbbreviationExpansion(abbreviationObject, text, name, indexes);        
    }
    
    public String getNameExpansion() {
        return nameExpansion;
    }
    
    public void setStemmedName() {
        stemmedName = Ling.getStemmedPhrase(name);
    }
    
    public String getStemmedName() {
        return stemmedName;
    }
    
    public void setCui(String cui) {
        this.cui = cui;
    }
        
    public String getCui() {
        return cui;
    }
    
    public void setAlternateCuis(List<String> alternateCuis) {
        this.alternateCuis = new ArrayList<>();
        for (String alternateCui : alternateCuis)
            alternateCuis = Util.setList(this.alternateCuis, alternateCui);
    }
    
    public List<String> getAlternateCuis() {
        return alternateCuis;
    }
    
    public void setNormalizingSieveLevel(int sieveLevel) {
        this.normalizingSieveLevel = sieveLevel;
    }
    
    public int getNormalizingSieve() {
        return normalizingSieveLevel;
    }
    
    public String getGoldMeSHorSNOMEDCui() {
        return goldMeSHorSNOMEDCui;
    }    
    
    public List<String> getGoldOMIMCuis() {
        return goldOMIMCuis;
    }
    
    public String getGoldCui() {
        if (!goldMeSHorSNOMEDCui.equals(""))
            return goldMeSHorSNOMEDCui;
        else 
            return goldOMIMCuis.size() == 1 ? goldOMIMCuis.get(0) : goldOMIMCuis.toString();
    }
 
    public void reinitializeNamesKnowledgeBase() {
        this.namesKnowledgeBase = new ArrayList<>();
    }
    
    public void setNamesKnowledgeBase(String name) {
        this.namesKnowledgeBase = Util.setList(this.namesKnowledgeBase, name);
    }
    
    public void setNamesKnowledgeBase(List<String> namesList) {
        this.namesKnowledgeBase = Util.addUnique(this.namesKnowledgeBase, namesList);
    }
    
    public List<String> getNamesKnowledgeBase() {
        return namesKnowledgeBase;
    }

    public void setStemmedNamesKnowledgeBase(List<String> namesList) {
        this.stemmedNamesKnowledgeBase = Util.addUnique(this.stemmedNamesKnowledgeBase, namesList);
    }
    
    public List<String> getStemmedNamesKnowledgeBase() {
        return stemmedNamesKnowledgeBase;
    }    
    
}
