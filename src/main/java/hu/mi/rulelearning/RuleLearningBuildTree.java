package hu.mi.rulelearning;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLearningBuildTree {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLearningBuildTree.class);
    private static final int FELBONTASMERETE = 4;
    private final SortedMap<String, RuleLearningValtozasEnum> tenyezokEvesViselkedeseAzInflacioval = new TreeMap<String, RuleLearningValtozasEnum>();
    private final SortedMap<String, RuleLearningValtozasEnum> tenyezoCsoportosViselkedeseAzInflacioval = new TreeMap<String, RuleLearningValtozasEnum>();

    public void vizsgalInflacioByTenyezo(SortedMap<Integer, Double> tenyezoErtekek, SortedMap<Integer, Double> inflacioErtekek, String kulcs){
        LOGGER.debug("vizsgalInflacioByTenyezo tenyezoErtekek meret: {}, kulcs: {}", tenyezoErtekek.size(), kulcs);
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for(Map.Entry<Integer, Double> entry : tenyezoErtekek.entrySet()){
            Double aktualisErtek = entry.getValue();
            if(aktualisErtek > max){
                max = aktualisErtek;
            }
            
            if(aktualisErtek < min){
                min = aktualisErtek;
            }
        }
        
        double felbontasEgySzakasza = ((max - min) / FELBONTASMERETE) + 1;
        
        for(Map.Entry<Integer, Double> entry : tenyezoErtekek.entrySet()){
            Double aktualisErtek = entry.getValue();
            for(int i = 1; i <= FELBONTASMERETE; i++){
                if(aktualisErtek < min + felbontasEgySzakasza*i){
                    //ebbe a csoportba tartozik az érték
                    if(inflacioErtekek.get(entry.getKey()) < inflacioErtekek.get(entry.getKey() + 1)){
                        tenyezokEvesViselkedeseAzInflacioval.put(kulcs + "_" + entry.getKey() + "_" + i, RuleLearningValtozasEnum.INFLACIO_NO);
                    }else if(inflacioErtekek.get(entry.getKey()) > inflacioErtekek.get(entry.getKey() + 1)){
                        tenyezokEvesViselkedeseAzInflacioval.put(kulcs + "_" + entry.getKey() + "_" + i, RuleLearningValtozasEnum.INFLACIO_CSOKKEN);
                    }
                    break;
                }
            }
        }
        
    }
    
    public void kiszamolSzabalyok(){
        LOGGER.debug("kiszamolSzabalyok");
        
        for(Map.Entry<String, RuleLearningValtozasEnum> entry : tenyezokEvesViselkedeseAzInflacioval.entrySet()){
        
            String [] azonositoDarabok = entry.getKey().split("_");
            String tenyezoEsCsoport = azonositoDarabok[0] + "_" + azonositoDarabok[2];
            
            if(tenyezoCsoportosViselkedeseAzInflacioval.containsKey(tenyezoEsCsoport)){
                continue;//Ezt a csoportot már vizsgáltuk
            }
            
            RuleLearningValtozasEnum szabaly = RuleLearningValtozasEnum.MEG_NEM_ELDONTOTT;
            
            for(Map.Entry<String, RuleLearningValtozasEnum> belsoEntry : tenyezokEvesViselkedeseAzInflacioval.entrySet()){
                
                String [] belsoAzonositoDarabok = belsoEntry.getKey().split("_");
                String belsoTenyezoEsCsoport = belsoAzonositoDarabok[0] + "_" + belsoAzonositoDarabok[2];
                if(!(belsoTenyezoEsCsoport.equals(tenyezoEsCsoport))){
                    continue;
                }
                
                if(szabaly == RuleLearningValtozasEnum.MEG_NEM_ELDONTOTT){
                    szabaly = belsoEntry.getValue();
                }else{
                    if(!(szabaly == belsoEntry.getValue())){
                        tenyezoCsoportosViselkedeseAzInflacioval.put(tenyezoEsCsoport, RuleLearningValtozasEnum.NINCS_SZABALY);
                        break;
                    }
                }
                
            }
            //Végig ért a for ciklus és nem volt eltérés
            if(!(tenyezoCsoportosViselkedeseAzInflacioval.containsKey(tenyezoEsCsoport))){
                tenyezoCsoportosViselkedeseAzInflacioval.put(tenyezoEsCsoport, szabaly);
            }
        
        }
        
    }

    public Map<String, RuleLearningValtozasEnum> getTenyezokEvesViselkedeseAzInflacioval() {
        return tenyezokEvesViselkedeseAzInflacioval;
    }

    public Map<String, RuleLearningValtozasEnum> getTenyezoCsoportosViselkedeseAzInflacioval() {
        return tenyezoCsoportosViselkedeseAzInflacioval;
    }
   
}
