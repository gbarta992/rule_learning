package hu.mi.rulelearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLearningCalculateEntropy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLearningCalculateEntropy.class);
    private static final int FELBONTASMERETE = 4;

    public static double calculateEntropy(List<Double> valoszinuseg) {
        LOGGER.debug("calculateEntropy valoszinuseg darabszama: {}", valoszinuseg.size());
        double response = 0;

        for (Double aktualisValoszinuseg : valoszinuseg) {
            if(aktualisValoszinuseg > 0.0){
                response += aktualisValoszinuseg * Math.log10(aktualisValoszinuseg) / Math.log10(2);
            }
        }
        
        response *= -1;

        return response;
    }
    
    public static List<Double> getValoszinusegek(SortedMap<Integer, Double> map){
        LOGGER.debug("getValoszinusegek map meret: {}", map.size());
        List<Double> response = new ArrayList<Double>();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for(Map.Entry<Integer, Double> entry : map.entrySet()){
            Double aktualisErtek = entry.getValue();
            if(aktualisErtek > max){
                max = aktualisErtek;
            }
            
            if(aktualisErtek < min){
                min = aktualisErtek;
            }
        }
        
        double felbontasEgySzakasza = ((max - min) / FELBONTASMERETE) + 1;
        Map<Integer, Integer> elofordulasokDarabszam = new HashMap<Integer, Integer>();
        
        for(int i = 1; i <= FELBONTASMERETE; i++){
            elofordulasokDarabszam.put(i, 0);
        }
        
        for(Map.Entry<Integer, Double> entry : map.entrySet()){
            Double aktualisErtek = entry.getValue();
            for(int i = 1; i <= FELBONTASMERETE; i++){
                if(aktualisErtek < min + felbontasEgySzakasza*i){
                    elofordulasokDarabszam.put(i, elofordulasokDarabszam.get(i) + 1);
                    break;
                }
            }
        }
        
        for(Map.Entry<Integer, Integer> entry : elofordulasokDarabszam.entrySet()){
            double mapSize = map.size();
            double valoszinuseg = entry.getValue() / mapSize;
            response.add(valoszinuseg);
        }
        
        return response;
    }

    public static SortedMap<Integer, Double> getMapResze(SortedMap<Integer, Double> map, boolean eleje) {
        LOGGER.debug("getMapResze map meret: {}, eleje: {}", map.size(), eleje);
        SortedMap<Integer, Double> response = new TreeMap<Integer, Double>();
        int counter = 0;
        
        for(Map.Entry<Integer, Double> entry : map.entrySet()){
            if(counter == map.size()/2){
                if(eleje){
                    response.putAll(map.headMap(entry.getKey()));
                }else{
                    response.putAll(map.tailMap(entry.getKey()));
                }
            }
            counter++;
        }
        
        LOGGER.debug("getMapResze response meret: {}", response.size());
        return response;
    }

}
