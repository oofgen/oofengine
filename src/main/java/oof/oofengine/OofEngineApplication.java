package oof.oofengine;

import oof.oofengine.data.Object3D;
import oof.oofengine.data.OofConfig;
import oof.oofengine.utils.Introduction;
import oof.oofengine.utils.RenderToPng;
import oof.oofengine.utils.Sandbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class OofEngineApplication {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(OofEngineApplication.class);

        //logger.info("Hello Oof!");

        OofConfig oofConfig = new OofConfig()
                .withHeightPx(512)
                //.withDebugMode()
                .withWidthPx(512)
                .validate();



        Sandbox sandbox = new Sandbox(oofConfig);
        sandbox.run();

        //Introduction.run();

        //new RenderToPng().run();
    }
}
