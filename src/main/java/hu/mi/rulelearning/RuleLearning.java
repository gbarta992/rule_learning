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
    private SortedMap<Integer, Double> inflacioErtekek = null;

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
        
        //TODO
        
        tenyezoEntropiak.remove(legkisebbKulcs);
        
        tenyezokAlapjanVizsgalat();
    }
    
    private String getLegkisebbEntropia(){
        double legkisebbErtek = Double.MAX_VALUE;
        String legkisebbKulcs = null;
        
        for(Map.Entry<String, Double>entry : tenyezoEntropiak.entrySet()){
            if(entry.getValue() < legkisebbErtek){
                legkisebbKulcs = entry.getKey();
            }
        }
        return legkisebbKulcs;
    }

    private void mapFeltolteseTenyezokkel() {
        tenyezoEntropiak.put("munkanelkuliseg", getEntropyFromStuff("munkanelkuliseg.txt"));
        tenyezoEntropiak.put("alapkamat", getEntropyFromStuff("alapkamat.txt"));
    }

    private double getEntropyFromStuff(String filenev) {
        LOGGER.debug("getEntropyFromStuff filenev: {}", filenev);
        SortedMap<Integer, Double> ertekek = fileBeolvas("/tenyezok/" + filenev);

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
