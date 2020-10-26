package oof.oofengine.render;

import oof.oofengine.control.Camera;
import oof.oofengine.control.impl.FreeCamera;
import oof.oofengine.data.Constants;
import oof.oofengine.data.LoaderUtils;
import oof.oofengine.model.CameraSettings;
import oof.oofengine.model.ObjectMatrixSamples;
import oof.oofengine.model.SimpleModel;
import oof.oofengine.model.api.IModel;
import oof.oofengine.render.shader.Shader;
import oof.oofengine.render.shader.ShaderManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static oof.oofengine.util.MiscUtils.handleGLErrors;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Tutorial implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);

    private long window;
    private float width = 1024.0f / 2;
    private float height = 768.0f / 2;
    private Shader shader;
    private int matrixUniformHandle;
    private final double fov = 45.0;
    private final float aspectRatio = width / height;


    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f();
    Matrix4f model = new Matrix4f();
    Matrix4f modelViewProjection = new Matrix4f();
    Matrix4f viewProjection = new Matrix4f();

    ConcurrentHashMap<String, IModel> models = new ConcurrentHashMap<String, IModel>();

    private Camera camera;
    private final SimpleModel cube = new SimpleModel(ObjectMatrixSamples.cube, "texture/uvtemplate_flipped.DDS");

    private Camera getCamera(long windowId) {
        CameraSettings settings = new CameraSettings();
        settings.setFov(fov);
        settings.setAspectRatio(aspectRatio);
        settings.setHeight(height);
        settings.setWidth(width);

        settings.setInitialDirection(new Vector3f(0, 0, 0));
        settings.setInitialPosition(new Vector3f(4, 3, 3));
        settings.setInitialRotation(new Vector3f(0, 1, 0));

        settings.setCameraSpeed(3.0f);
        settings.setMouseSpeed(1.0f);

        return new FreeCamera(settings, windowId);
    }


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
            logger.error(Constants.ERROR_FAILED_TO_OPEN_GLFW_WINDOW);
            glfwTerminate();
            throw new RuntimeException(Constants.ERROR_FAILED_TO_OPEN_GLFW_WINDOW);
        }

        camera = getCamera(window);

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Ensure we can capture the escape key being pressed below
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);
        // Hide the mouse and enable unlimited shmovement
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Set the mouse at the center of the screen
        glfwPollEvents();
        glfwSetCursorPos(window, width/2, height/2);

        // Enable depth test
        glEnable(GL_DEPTH_TEST);
        // Accept fragment if it closer to the Camera than the former one
        glDepthFunc(GL_LESS);

        // Cull triangles which normal is not towards the camera
        glEnable(GL_CULL_FACE);
    }

    private void render() throws Exception {
        shader = ShaderManager.loadShaderProgram(getVertexShader(), getFragmentShader());
        // 50% grey background
        glClearColor(0.33f, 0.33f, 0.33f, 0.0f);

        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");

        addModels();

        // finally, show window
        glfwShowWindow(window);
    }

    private void addModels() {
        models.put("oof", LoaderUtils.load("oof/bighead.obj",
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
                        | aiProcess_FixInfacingNormals | aiProcess_LimitBoneWeights));
        //models.put("cube", cube);

        for(IModel model : models.values()) {
            model.init(shader);
        }
    }

    @NotNull
    private String getFragmentShader() {
        return "shader/3dGameDevWithLWJGL_Transformations/fragment.fsh";
    }

    @NotNull
    private String getVertexShader() {
        return "shader/3dGameDevWithLWJGL_Transformations/vertex.vsh";
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
        boolean firstLoop;
        do {
            handleGLErrors();
            // Clear the screen. It's not mentioned before Tutorial 02, but it can cause flickering, so it's there nonetheless.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use our shader
            GL33.glUseProgram(shader.getShaderHandle());

            //- view -----------------------------------------------------------------------------------------------------------
            camera.computeMatricesFromInputs();
            projection = camera.getProjection();
            shader.setUniform("projectionMatrix", projection);

            view = camera.getView();
            //------------------------------------------------------------------------------------------------------------------

            //---- draw --------------------------------------------------------------------------------------------------------
            for(IModel model : models.values()) {
                model.draw(shader, view);
            }
            //------------------------------------------------------------------------------------------------------------------

            // Swap buffers
            glfwSwapBuffers(window);
            glfwPollEvents();

        } // Check if the ESC key was pressed or the window was closed
        while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS && !glfwWindowShouldClose(window) );

        // Cleanup VBO and shader
        GL33.glDeleteProgram(shader.getShaderHandle());

        // Close OpenGL window and terminate GLFW
        glfwTerminate();
    }

}
