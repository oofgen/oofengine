package oof.oofengine;

import oof.oofengine.christiantest.ChristianTest;

import java.awt.*;
import java.io.IOException;

public class OofEngineApplication {
    public static void main(String[] args) {

        ChristianTest christianTest = new ChristianTest();
        try {
            christianTest.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
