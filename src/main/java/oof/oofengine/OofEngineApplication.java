package oof.oofengine;

import oof.oofengine.render.Tutorial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OofEngineApplication {
    private static final Logger logger = LoggerFactory.getLogger(OofEngineApplication.class);

    public static void main(String[] args) {

        Runnable engine = new Tutorial();
        try {
            engine.run();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }


}
