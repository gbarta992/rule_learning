package hu.mi.rulelearning;

import hu.mi.rulelearning.model.BaseRate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseRateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRateTest.class);

    @Test
    public void readFromFile() {
        BufferedReader br = null;
        List<BaseRate> listBaseRate = new ArrayList<BaseRate>();
        try {
            File beolvas = new File(getClass().getResource("/baserate.txt").toURI());
            br = new BufferedReader(new FileReader(beolvas));
            String line;
            

            while ((line = br.readLine()) != null) {
                String[] elemek = line.split(";");

                if (elemek.length != 2) {
                    continue;
                }

                BaseRate baseRate = new BaseRate();
                baseRate.setYear(elemek[0]);
                baseRate.setPercentage(Double.parseDouble(elemek[1]));
                listBaseRate.add(baseRate);

            }

        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        } catch (URISyntaxException e){ 
            LOGGER.error("URISyntaxException", e);
        }finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                LOGGER.error("IOException", ex);
            }
        }
        Assert.assertEquals(3, listBaseRate.size());
    }
}
