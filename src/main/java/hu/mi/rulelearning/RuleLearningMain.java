package hu.mi.rulelearning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleLearningMain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLearningMain.class);
    
    public static void main(String[] args){
        LOGGER.info("Fut az alkalmazás");
        
        RuleLearning ruleLearning = new RuleLearning();
        ruleLearning.start();
        
        LOGGER.info("Véget ért az alkalmazás");
    }
}
