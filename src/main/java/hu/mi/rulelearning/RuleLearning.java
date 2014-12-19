package hu.mi.rulelearning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLearning {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLearning.class);
    private static final double EPSILON = 0.001;
    private final Map<String, Double> tenyezoEntropiak = new HashMap<String, Double>();
    private SortedMap<Integer, Double> inflacioErtekek = null;
    private Map<String, SortedMap<Integer, Double>> tenyezokAdatai = new HashMap<String, SortedMap<Integer, Double>>();

    public void start() {
        mapFeltolteseTenyezokkel();
        
        inflacioErtekek = fileBeolvas("/inflacio/inflacio.txt");
        LOGGER.debug("inflacioErtekek merete: {}", inflacioErtekek.size());
        
        tenyezokAlapjanVizsgalat();
    }
    
    private void tenyezokAlapjanVizsgalat(){
        
        if(tenyezoEntropiak.isEmpty()){
            return;//megállítja a rekurziót
        }
        
        String legkisebbKulcs = getLegkisebbEntropia();
        LOGGER.debug("legkisebbKulcs: {}", legkisebbKulcs);
        
        osszevetTenyezoInflacio(legkisebbKulcs);
        
        tenyezoEntropiak.remove(legkisebbKulcs);
        
        tenyezokAlapjanVizsgalat();
    }
    
    private void osszevetTenyezoInflacio(String kulcs){
        SortedMap<Integer, Double> aktualisTenyezoAdatai = tenyezokAdatai.get(kulcs);
        
        int counter = 0;
        int irany = 0;
        double elozoInflacio = 0.0;
        double elozoTenyezo = 0.0;
        
        for(Map.Entry<Integer, Double> entry : aktualisTenyezoAdatai.entrySet()){
            if(elozoInflacio == 0.0){
                elozoInflacio = inflacioErtekek.get(entry.getKey());
                elozoTenyezo = entry.getValue();
                continue;
            }
            
            //if(irany == 0){
                irany = iranySzamito(elozoInflacio, inflacioErtekek.get(entry.getKey()), elozoTenyezo, entry.getValue());
                LOGGER.debug("tenyezo: {}, evszam: {}, irany: {}", kulcs, entry.getKey(), irany);
            //}
            
            elozoInflacio = inflacioErtekek.get(entry.getKey());
            elozoTenyezo = entry.getValue();
            counter++;
            
            if(counter == aktualisTenyezoAdatai.size()/2 - 1){
                break;
            }
        }
    }
    
    private int iranySzamito(double elozoInflacio, double aktualisInflacio, double elozoTenyezo, double aktualisTenyezo){
        LOGGER.debug("iranySzamito elozoInflacio: {}, aktualisInflacio: {}", elozoInflacio, aktualisInflacio);
        LOGGER.debug("iranySzamito elozoTenyezo: {}, aktualisTenyezo: {}", elozoTenyezo, aktualisTenyezo);
        if(elozoInflacio < aktualisInflacio && elozoTenyezo < aktualisTenyezo){
            return 1;
        }else if(elozoInflacio > aktualisInflacio && elozoTenyezo > aktualisTenyezo){
            return 2;
        }else if(Math.abs(elozoInflacio - aktualisInflacio) < EPSILON && (Math.abs(elozoTenyezo - aktualisTenyezo)) < EPSILON){
            return 3;
        }else{
            return 4;
        }
    }
    
    private String getLegkisebbEntropia(){
        double legkisebbErtek = Double.MAX_VALUE;
        String legkisebbKulcs = null;
        
        for(Map.Entry<String, Double>entry : tenyezoEntropiak.entrySet()){
            if(entry.getValue() < legkisebbErtek){
                legkisebbKulcs = entry.getKey();
                legkisebbErtek = entry.getValue();
            }
        }
        return legkisebbKulcs;
    }

    private void mapFeltolteseTenyezokkel() {
        tenyezoEntropiak.put("munkanelkuliseg", getEntropyFromStuff("munkanelkuliseg.txt"));
        tenyezoEntropiak.put("alapkamat", getEntropyFromStuff("alapkamat.txt"));
        tenyezoEntropiak.put("gdp", getEntropyFromStuff("gdp.txt"));
    }

    private double getEntropyFromStuff(String filenev) {
        LOGGER.debug("getEntropyFromStuff filenev: {}", filenev);
        SortedMap<Integer, Double> ertekek = fileBeolvas("/tenyezok/" + filenev);
        
        tenyezokAdatai.put(filenev.substring(0, filenev.lastIndexOf(".")), ertekek);

        ertekek = RuleLearningUtil.getMapResze(ertekek, true);

        List<Double> valoszinusegek = RuleLearningUtil.getValoszinusegek(ertekek);

        double entropy = RuleLearningUtil.calculateEntropy(valoszinusegek);
        LOGGER.debug("entropy: {}", entropy);

        return entropy;
    }

    private SortedMap<Integer, Double> fileBeolvas(String filenev) {
        BufferedReader br = null;
        SortedMap<Integer, Double> response = new TreeMap<Integer, Double>();
        try {
            File beolvas = new File(getClass().getResource(filenev).toURI());
            br = new BufferedReader(new FileReader(beolvas));
            String line;

            while ((line = br.readLine()) != null) {
                String[] elemek = line.split(";");

                if (elemek.length != 2) {
                    continue;
                }
                try {
                    response.put(Integer.parseInt(elemek[0]), Double.parseDouble(elemek[1]));
                } catch (NumberFormatException e) {
                    LOGGER.error("Aktualis sor kimarad", e);
                }
            }

        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LOGGER.error("IOException", ex);
            }
        }
        return response;
    }
}
