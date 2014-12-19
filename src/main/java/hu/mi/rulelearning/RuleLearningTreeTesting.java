package hu.mi.rulelearning;

import java.util.Map;
import java.util.SortedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLearningTreeTesting {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLearningTreeTesting.class);
    private static final int FELBONTASMERETE = 4;
    private final RuleLearningBuildTree ruleLearningBuildTree;
    private final Map<String, Double> tenyezoEntropiak;

    public RuleLearningTreeTesting(RuleLearningBuildTree ruleLearningBuildTree, Map<String, Double> tenyezoEntropiak) {
        this.ruleLearningBuildTree = ruleLearningBuildTree;
        this.tenyezoEntropiak = tenyezoEntropiak;
    }

    public void testingTreeInflacioByTenyezo(SortedMap<Integer, Double> tenyezoErtekek, SortedMap<Integer, Double> inflacioErtekek, String kulcs) {
        LOGGER.debug("testingTreeInflacioByTenyezo tenyezoErtekek meret: {}, kulcs: {}", tenyezoErtekek.size(), kulcs);
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Map.Entry<Integer, Double> entry : tenyezoErtekek.entrySet()) {
            Double aktualisErtek = entry.getValue();
            if (aktualisErtek > max) {
                max = aktualisErtek;
            }

            if (aktualisErtek < min) {
                min = aktualisErtek;
            }
        }

        double felbontasEgySzakasza = ((max - min) / FELBONTASMERETE) + 1;
        int faMelyseg = 0;
        //Megmondja, hogy a fa alapján meg tudta-e mondani az inflációt
        boolean voltTalalat = false;

        String tenyezoNev = getTenyezoNevByMelyseg(faMelyseg);

        for (Map.Entry<Integer, Double> entry : tenyezoErtekek.entrySet()) {
            Double aktualisErtek = entry.getValue();
            
            for (int i = 1; i <= FELBONTASMERETE; i++) {
                if (aktualisErtek < min + felbontasEgySzakasza * i) {
                    //ebbe a csoportba tartozik az érték
                    //Tanító halmazból csoportvaltozas lekerese
                    RuleLearningValtozasEnum csoportValtozasa = ruleLearningBuildTree.getTenyezoCsoportosViselkedeseAzInflacioval().get(tenyezoNev + "_" + i);

                    while (faMelyseg < 4) {

                        if (inflacioErtekek.get(entry.getKey() - 1) < inflacioErtekek.get(entry.getKey())) {
                            if (csoportValtozasa == RuleLearningValtozasEnum.INFLACIO_NO) {
                                LOGGER.debug("Jo meghatarozas no inflacio. Tenyezo: {}, TenyezoNevAlapjan: {}", kulcs + "_" + entry.getKey(), tenyezoNev);
                                voltTalalat = true;
                                break;
                            }
                        } else if (inflacioErtekek.get(entry.getKey() - 1) > inflacioErtekek.get(entry.getKey())) {
                            if (csoportValtozasa == RuleLearningValtozasEnum.INFLACIO_CSOKKEN) {
                                LOGGER.debug("Jo meghatarozas csokken inflacio. Tenyezo: {}, TenyezoNevAlapjan: {}", kulcs + "_" + entry.getKey(), tenyezoNev);
                                voltTalalat = true;
                                break;
                            }
                        }
                        faMelyseg++;
                        tenyezoNev = getTenyezoNevByMelyseg(faMelyseg);
                    }
                    if(!voltTalalat){
                        LOGGER.debug("Fa alapjan nincs meghatarozas. Tenyezo: {}", kulcs + "_" + entry.getKey());
                    }
                    voltTalalat = false;
                    faMelyseg = 0;
                    break;
                }
            }
        }

    }

    private String getTenyezoNevByMelyseg(int melyseg) {
        LOGGER.debug("getTenyezoNevByMelyseg melyseg: {}", melyseg);

        int i = 0;

        for (Map.Entry<String, Double> entry : tenyezoEntropiak.entrySet()) {

            if (i == melyseg) {
                LOGGER.debug("getTenyezoNevByMelyseg tenyezoNev: {}", entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }

}
