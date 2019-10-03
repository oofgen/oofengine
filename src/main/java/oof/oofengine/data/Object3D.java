package oof.oofengine.data;

import org.lwjgl.glfw.GLFW;

public class Object3D {
    public void render() {
        String title = "";
        long monitor = 1000L;
        long share = 1000L;
        System.out.printf("REEEE, %s %d %d\n", title, monitor, share);
        GLFW.glfwCreateWindow(400, 400, title, monitor, share);
    }
}
