package oof.oofengine;

import oof.oofengine.data.Object3D;
import oof.oofengine.display.WindowManager;
import oof.oofengine.renderEngine.Loader;
import oof.oofengine.renderEngine.RawModel;
import oof.oofengine.renderEngine.Renderer;

import java.awt.*;

public class OofEngineApplication {
    public static void main(String[] args) {
        WindowManager win = new WindowManager();
        Loader loader = new Loader();
        Renderer renderer = new Renderer();


        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };
        RawModel model = loader.loadToVAO(vertices);


        int i = 0;
        while (i < 150) {
            renderer.prepare();
            renderer.render(model);

            win.updateWindow();
            i++;
        }
        loader.cleanUp();

        win.closeWindow();
    }
}
