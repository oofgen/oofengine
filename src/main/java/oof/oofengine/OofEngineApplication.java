package oof.oofengine;

import oof.oofengine.data.Object3D;
import oof.oofengine.display.WindowManager;
import oof.oofengine.renderEngine.Loader;
import oof.oofengine.renderEngine.RawModel;
import oof.oofengine.renderEngine.Renderer;
import oof.oofengine.shaders.StaticShader;

import java.awt.*;

public class OofEngineApplication {
    public static void main(String[] args) {
        WindowManager win = new WindowManager();
        Loader loader = new Loader();
        Renderer renderer = new Renderer();
//        StaticShader shader = new StaticShader();


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
        while (i < 150) {
//            shader.start();
            renderer.prepare();
            renderer.render(model);
            win.updateWindow();
//            shader.stop();
            i++;
        }

//        shader.cleanUp();
        loader.cleanUp();
        win.closeWindow();
    }
}
