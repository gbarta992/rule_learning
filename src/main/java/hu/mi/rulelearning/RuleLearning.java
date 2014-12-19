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
    private final Map<String, Double> tenyezoEntropiak = new HashMap<String, Double>();
    private final Map<String, Double> temporaryTenyezoEntropiak = new HashMap<String, Double>();
    private SortedMap<Integer, Double> inflacioErtekek = null;
    private final Map<String, SortedMap<Integer, Double>> tenyezokAdatai = new HashMap<String, SortedMap<Integer, Double>>();
    private final RuleLearningBuildTree ruleLearningBuildTree = new RuleLearningBuildTree();

    public void start() {
        mapFeltolteseTenyezokkel();
        
        inflacioErtekek = fileBeolvas("/inflacio/inflacio.txt");
        LOGGER.debug("inflacioErtekek merete: {}", inflacioErtekek.size());
        
        //Temporary map feltöltése, majd a meghívott függvényben rekurzív törlése
        temporaryTenyezoEntropiak.putAll(tenyezoEntropiak);
        
        tenyezokAlapjanVizsgalat();
        
        ruleLearningBuildTree.kiszamolSzabalyok();
        
        LOGGER.info("Tenyezok eves viselkedese");
        
        for(Map.Entry<String, RuleLearningValtozasEnum> entry : ruleLearningBuildTree.getTenyezokEvesViselkedeseAzInflacioval().entrySet()){
            LOGGER.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
        }
        
        LOGGER.info("Csoportos eredmenyek");
        
        for(Map.Entry<String, RuleLearningValtozasEnum> entry : ruleLearningBuildTree.getTenyezoCsoportosViselkedeseAzInflacioval().entrySet()){
            LOGGER.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
        }
    }
    
    private void tenyezokAlapjanVizsgalat(){
        
        if(temporaryTenyezoEntropiak.isEmpty()){
            return;//megállítja a rekurziót
        }
        
        String legkisebbKulcs = getLegkisebbEntropia();
        LOGGER.debug("legkisebbKulcs: {}", legkisebbKulcs);
        
        //osszevetTenyezoInflacio(legkisebbKulcs);
        
        ruleLearningBuildTree.vizsgalInflacioByTenyezo(tenyezokAdatai.get(legkisebbKulcs).headMap(visszaAdKozepsoMapKulcs(legkisebbKulcs)), inflacioErtekek, legkisebbKulcs);
        
        temporaryTenyezoEntropiak.remove(legkisebbKulcs);
        
        tenyezokAlapjanVizsgalat();
    }
    
    private Integer visszaAdKozepsoMapKulcs(String kulcs){
    SortedMap<Integer, Double> aktualisTenyezoAdatai = tenyezokAdatai.get(kulcs);
        
        int counter = 0;
        for(Map.Entry<Integer, Double> entry : aktualisTenyezoAdatai.entrySet()){
            
            
            if(counter == aktualisTenyezoAdatai.size()/2 - 1){
                return entry.getKey();
            }
            counter++;
        }
        return null;
    }
    
    private String getLegkisebbEntropia(){
        double legkisebbErtek = Double.MAX_VALUE;
        String legkisebbKulcs = null;
        
        for(Map.Entry<String, Double>entry : temporaryTenyezoEntropiak.entrySet()){
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
        tenyezoEntropiak.put("otp", getEntropyFromStuff("otp.txt"));
    }

    private double getEntropyFromStuff(String filenev) {
        LOGGER.debug("getEntropyFromStuff filenev: {}", filenev);
        SortedMap<Integer, Double> ertekek = fileBeolvas("/tenyezok/" + filenev);
        
        tenyezokAdatai.put(filenev.substring(0, filenev.lastIndexOf(".")), ertekek);

        ertekek = RuleLearningCalculateEntropy.getMapResze(ertekek, true);

        List<Double> valoszinusegek = RuleLearningCalculateEntropy.getValoszinusegek(ertekek);

        double entropy = RuleLearningCalculateEntropy.calculateEntropy(valoszinusegek);
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
