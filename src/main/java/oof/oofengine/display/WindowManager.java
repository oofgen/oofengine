package oof.oofengine.display;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowManager {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 740;

    private long window;


    public WindowManager() {
        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        try {
            this.window = glfwCreateWindow(WIDTH, HEIGHT, "OOF", NULL, NULL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        glfwMakeContextCurrent(this.window);
        glfwSwapInterval(1);
        glfwShowWindow(this.window);
        GL.createCapabilities();
    }


    public void updateWindow() {
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        //glRecti(0, 190, 100, 290);
        glfwSwapBuffers(this.window);
        glfwPollEvents();
    }

    public void closeWindow() {
        glfwDestroyWindow(this.window);
        glfwTerminate();
    }
}
