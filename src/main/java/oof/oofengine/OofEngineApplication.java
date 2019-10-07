package oof.oofengine;

import oof.oofengine.christiantest.ChristianTest;
import oof.oofengine.data.Object3D;
import oof.oofengine.display.WindowManager;
import oof.oofengine.renderEngine.Loader;
import oof.oofengine.renderEngine.RawModel;
import oof.oofengine.renderEngine.Renderer;
import oof.oofengine.shaders.StaticShader;

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

        /*WindowManager win = new WindowManager();
        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();


        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
        };
        int[] indices ={
                0,1,3,
                3,1,2
        };

        RawModel model = loader.loadToVAO(vertices, indices);


        int i = 0;
        shader.start();
        while (i < 150) {
            renderer.prepare();
            renderer.render(model);
            win.updateWindow();
            i++;
        }
        shader.stop();

        shader.cleanUp();
        loader.cleanUp();
        win.closeWindow();*/
    }


}
