package oof.oofengine.render;

import com.sun.istack.internal.NotNull;
import oof.oofengine.data.ObjectMatrixSamples;
import oof.oofengine.render.shader.ShaderManager;
import oof.oofengine.util.TextureUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBShaderObjects.glGetUniformLocationARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexShader.glDisableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glEnableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12C.GL_BGR;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
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
    int vertexId = -200;

    final float[] colorBufferData = ObjectMatrixSamples.cube_colors;
    IntBuffer colorArrayId = BufferUtils.createIntBuffer(1);
    IntBuffer colorBuffer = BufferUtils.createIntBuffer(1);
    int colorId = -200;

    int textureId = -200;
    final IntBuffer textureBuffer = BufferUtils.createIntBuffer(1);

    IntBuffer uvBuffer = BufferUtils.createIntBuffer(1);
    int uvId = -1;
    private int texture;


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
        shaderProgramId = ShaderManager.loadShaderProgram(getVertexShader(), getFragmentShader());
        // 50% grey background
        glClearColor(0.33f, 0.33f, 0.33f, 0.0f);

        // vertexes
        glGenVertexArrays(vertexArrayId);
        glBindVertexArray(vertexArrayId.get());

        glGenBuffersARB(vertexBuffer);
        vertexId = vertexBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexBufferData, GL_STATIC_DRAW_ARB);

//        // colors
//        glGenBuffersARB(colorBuffer);
//        colorId = colorBuffer.get();
//        glBindBufferARB(GL_ARRAY_BUFFER_ARB, colorId);
//        glBufferDataARB(GL_ARRAY_BUFFER_ARB, colorBufferData, GL_STATIC_DRAW_ARB);

        // uvs
        glGenBuffersARB(uvBuffer);
        uvId = uvBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, uvId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, ObjectMatrixSamples.uvs, GL_STATIC_DRAW_ARB);


        // texture
        texture = TextureUtils.loadTextureAsResource(Paths.get("texture/uvtemplate.DDS"));
        // Get a handle for our "myTextureSampler" uniform
        textureId = glGetUniformLocationARB(shaderProgramId, "myTextureSampler");
        if(textureId == -1) {
            throw new RuntimeException(String.format("\nglGetUniformLocationARB failed with params:\nshaderProgramId = %s\nname = \"myTextureSampler\"", shaderProgramId));
        }

        // Get a handle for our "MVP" uniform
        matrixId = glGetUniformLocationARB(shaderProgramId, "MVP");

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

    @NotNull
    private String getFragmentShader() {
        return "shader/tutorial5/textureShader.fsh";
        //return "shader/tutorial_3_singlecolor.fsh";
    }

    @NotNull
    private String getVertexShader() {
        return "shader/tutorial5/textureShader.vsh";
        //return "shader/tutorial_3_simpletransform.vsh";
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
            int errorCode = 0;
            do {
                errorCode = glGetError();
                if(errorCode != GL_NO_ERROR) {
                    logger.error(String.format("GL reported error code %s", errorCode));
                }
            } while(errorCode != GL_NO_ERROR);


            // Clear the screen. It's not mentioned before Tutorial 02, but it can cause flickering, so it's there nonetheless.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use our shader
            GL33.glUseProgram(shaderProgramId);

            glUniformMatrix4fv(matrixId, false, modelViewProjection.get(BufferUtils.createFloatBuffer(4 * 4)));

            // Bind our texture in Texture Unit 0
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            // Set our "myTextureSampler" sampler to use Texture Unit 0
            glUniform1i(textureId, 0);

            // first attribute buffer : vertices
            glEnableVertexAttribArrayARB(0);
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexId);
            glVertexAttribPointerARB(0, 3, GL_FLOAT, false,0, 0);
//            // colors
//            glEnableVertexAttribArrayARB(1);
//            glBindBufferARB(GL_ARRAY_BUFFER_ARB, colorId);
//            glVertexAttribPointerARB(
//                    1,                                // attribute. No particular reason for 1, but must match the layout in the shader.
//                    3,                                // size
//                    GL_FLOAT,                         // type
//                    false,                         // normalized?
//                    0,                                // stride
//                    0                          // array buffer offset
//            );


            // 2nd attribute buffer : UVs
            glEnableVertexAttribArrayARB(1);
            glBindBufferARB(GL_ARRAY_BUFFER_ARB, uvId);
            glVertexAttribPointerARB(
                    1,                                // attribute. No particular reason for 1, but must match the layout in the shader.
                    2,                                // size : U+V => 2
                    GL_FLOAT,                         // type
                    false,                         // normalized?
                    0,                                // stride
                    0                          // array buffer offset
		    );


            // Draw
            glDrawArrays(GL_TRIANGLES, 0, vertexBufferData.length);

            glDisableVertexAttribArrayARB(0);
            glDisableVertexAttribArrayARB(1);

            // Swap buffers
            glfwSwapBuffers(window);
            glfwPollEvents();

        } // Check if the ESC key was pressed or the window was closed
        while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS && !glfwWindowShouldClose(window) );


        // Cleanup VBO and shader
        glDeleteBuffers(vertexId);
        glDeleteBuffers(uvId);
        GL33.glDeleteProgram(shaderProgramId);
        glDeleteTextures(textureId);
        glDeleteVertexArrays(vertexArrayId);

        // Close OpenGL window and terminate GLFW
        glfwTerminate();
    }

}
