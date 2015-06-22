/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tool;

import tool.sieves.AffixationSieve;
import tool.sieves.CompoundPhraseSieve;
import tool.sieves.DiseaseModifierSynonymsSieve;
import tool.sieves.HyphenationSieve;
import tool.sieves.PartialMatchNCBISieve;
import tool.sieves.PartialMatchSieve;
import tool.sieves.PrepositionalTransformSieve;
import tool.sieves.Sieve;
import tool.sieves.SimpleNameSieve;
import tool.sieves.StemmingSieve;
import tool.sieves.SymbolReplacementSieve;
import tool.util.Concept;
import tool.util.Terminology;

/**
 *
 * @author
 */
public class MultiPassSieveNormalizer {
     
    public static int maxSieveLevel;    
    
    public static boolean pass(Concept concept, int currentSieveLevel) {
        if (!concept.getCui().equals("")) {
            concept.setAlternateCuis(Sieve.getAlternateCuis(concept.getCui()));
            concept.setNormalizingSieveLevel(currentSieveLevel-1);
            //Terminology.storeNormalizedConcept(concept);
            return false;
        }
        
        if (currentSieveLevel > maxSieveLevel)
            return false;
        
        return true;
    }
        
    //behavior of this class
    public static void applyMultiPassSieve(Concept concept) {
        int currentSieveLevel = 1;
        //match with names in training data
        //Sieve 1        
        concept.setCui(Sieve.exactMatchSieve(concept.getName()));        
        if (!pass(concept, ++currentSieveLevel))
            return;
        
        //Sieve 2
        concept.setCui(Sieve.exactMatchSieve(concept.getNameExpansion()));
        if (!pass(concept, ++currentSieveLevel))
            return;

        //Sieve 3
        concept.setCui(PrepositionalTransformSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel))
            return;
        
        //Sieve 4
        concept.setCui(SymbolReplacementSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel))
            return;
        
        //Sieve 5
        concept.setCui(HyphenationSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel)) {            
            return;  
        }
        
        //Sieve 6
        concept.setCui(AffixationSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel))
            return;        
        
        //Sieve 7
        concept.setCui(DiseaseModifierSynonymsSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel)) {            
            return;                  
        }
        
        //Sieve 8
        concept.setCui(StemmingSieve.apply(concept));
        if (!pass(concept, ++currentSieveLevel))
            return;       
        
        //Sieve 9
        concept.setCui(Main.test_data_dir.toString().contains("ncbi") ? CompoundPhraseSieve.applyNCBI(concept.getName()) : CompoundPhraseSieve.apply(concept.getName()));
        if (!pass(concept, ++currentSieveLevel)) {            
            return;         
        }
        
        //Sieve 10
        concept.setCui(SimpleNameSieve.apply(concept));
        pass(concept, ++currentSieveLevel);
        --currentSieveLevel;
        if (!concept.getCui().equals(""))
            return;                 
        //Sieve 10
        concept.setCui(Main.test_data_dir.toString().contains("ncbi") ? PartialMatchNCBISieve.apply(concept) : PartialMatchSieve.apply(concept));
        pass(concept, ++currentSieveLevel);        
                
    }
                    
}
