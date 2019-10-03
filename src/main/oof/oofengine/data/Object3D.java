package main.oof.oofengine.data;

public class Object3D {
    public void render() {
        String title = "";
        long monitor = 1000L;
        long share = 1000L;
        GLFW.glfwCreateWindow(400, 400, title, monitor, share);
    }
}
