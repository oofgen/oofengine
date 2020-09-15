package oof.oofengine.render;

import oof.oofengine.data.ObjectMatrixSamples;
import oof.oofengine.render.shader.ShaderManager;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBShaderObjects.glGetUniformLocationARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexShader.glDisableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glEnableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Tutorial implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);

    private long window;
    private float width = 1024.0f;
    private float height = 768.0f;
    private int shaderProgramId;
    private int matrixId;
    private double fov = 45.0;
    private float aspectRatio = width / height;


    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f();
    Matrix4f model = new Matrix4f();
    Matrix4f modelViewProjection = new Matrix4f();

    final float[] vertexBufferData = ObjectMatrixSamples.cube;
    IntBuffer vertexArrayId = BufferUtils.createIntBuffer(1);
    IntBuffer vertexBuffer = BufferUtils.createIntBuffer(1);
    int vertexId = -1;

    final float[] colorBufferData = ObjectMatrixSamples.cube_colors;
    IntBuffer colorArrayId = BufferUtils.createIntBuffer(1);
    IntBuffer colorBuffer = BufferUtils.createIntBuffer(1);
    int colorId = -1;


    @Override
    public void run() {
        try {
            init();
            render();
            loop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws Exception {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        setHints();

        window = glfwCreateWindow((int) width, (int) height, "ChristianTest2", NULL, NULL);
        if( window == NULL) {
            logger.error("Failed to open GLFW window.");
            glfwTerminate();
            throw new RuntimeException();
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Enable depth test
        glEnable(GL_DEPTH_TEST);
        // Accept fragment if it closer to the camera than the former one
        glDepthFunc(GL_LESS);
    }

    private void render() throws Exception {
        // 50% grey background
        glClearColor(0.33f, 0.33f, 0.33f, 0.0f);

        glGenVertexArrays(vertexArrayId);
        glBindVertexArray(vertexArrayId.get());

        glGenBuffersARB(vertexBuffer);
        vertexId = vertexBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexBufferData, GL_STATIC_DRAW_ARB);

        glGenBuffersARB(colorBuffer);
        colorId = colorBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, colorId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, colorBufferData, GL_STATIC_DRAW_ARB);

        shaderProgramId = ShaderManager.loadShaderProgram(
                "shader/tutorial_3_simpletransform.vsh",
                "shader/tutorial_3_singlecolor.fsh"
        );

        // Get a handle for our "MVP" uniform
        matrixId = glGetUniformLocationARB(shaderProgramId, "mvp");

        // Projection matrix : 45° Field of View, 4:3 ratio, display range : 0.1 unit <-> 100 units
        projection = new Matrix4f().perspective((float) Math.toRadians(fov), aspectRatio, 0.1f, 100.0f);

        // View matrix
        view = new Matrix4f().lookAt(
                new Vector3f(4,3,3),    // POSTION: view (camera) is positioned at (4, 3, 3)xyz in world
                new Vector3f(0, 0,0),   // DIRECTION: view is angled at (0, 0, 0)xyz in world
                new Vector3f(0, 1, 0)   // ROTATION: view is right-side up (y=-1) for upside down
        );

        // Model matrix : an identity matrix (model will be at the origin)
        model = new Matrix4f().identity();

        modelViewProjection = projection.mul(view).mul(model);

        // finally, show window
        glfwShowWindow(window);
    }

    private void setHints() {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4); // 4x antialiasing
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // We want OpenGL 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // To make MacOS happy; should not be needed
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // We don't want the old OpenGL*/
    }

    private void loop() {
        // Ensure we can capture the escape key being pressed below
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);

        do {
            // Clear the screen. It's not mentioned before Tutorial 02, but it can cause flickering, so it's there nonetheless.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use our shader
            GL33.glUseProgram(shaderProgramId);

            glUniformMatrix4fv(matrixId, false, modelViewProjection.get(BufferUtils.createFloatBuffer(4 * 4)));

            // first attribute buffer : vertices
            glEnableVertexAttribArrayARB(0);
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexId);
            glVertexAttribPointerARB(0, 3, GL_FLOAT, false,0, 0);

            glEnableVertexAttribArrayARB(1);
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, colorId);
            glVertexAttribPointerARB(
                    1,                                // attribute. No particular reason for 1, but must match the layout in the shader.
                    3,                                // size
                    GL_FLOAT,                         // type
                    false,                         // normalized?
                    0,                                // stride
                    0                          // array buffer offset
);

            // Draw
            glDrawArrays(GL_TRIANGLES, 0, vertexBufferData.length);

            glDisableVertexAttribArrayARB(0);

            // Swap buffers
            glfwSwapBuffers(window);
            glfwPollEvents();

        } // Check if the ESC key was pressed or the window was closed
        while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS && !glfwWindowShouldClose(window) );
    }

}
