package oof.oofengine.control;

import oof.oofengine.data.CameraSettings;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.DoubleBuffer;

import static java.lang.Math.*;
import static java.lang.Math.sin;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private final long windowId;
    private Vector3f position;
    private final Vector3f rotation;
    private CameraSettings settings;

    private Matrix4f projection;
    private Matrix4f view;
    private Vector3f direction;
    private Vector3f right;
    private Vector3f up;

    private float horizontalAngle = 3.14f;
    // vertical angle : 0, look at the horizon
    private float verticalAngle = 0.0f;
    // Initial Field of View
    private double initialFoV;


    private double lastTime;
    private final Logger logger = LoggerFactory.getLogger(Camera.class);


    public Camera(CameraSettings settings, long windowId) {
        this.settings = settings;
        this.windowId = windowId;
        this.initialFoV = settings.getFov();
        this.position = settings.getInitialPosition();
        this.rotation = settings.getInitialRotation();
        this.direction = settings.getInitialDirection();
    }

    public void computeMatricesFromInputs() {
        double currentTime = glfwGetTime();
        float deltaTime = (float) (currentTime - lastTime);
        lastTime = currentTime;

        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(windowId, x, y);
        glfwSetCursorPos(windowId, settings.getWidth() / 2, settings.getHeight() / 2);

        horizontalAngle += settings.getMouseSpeed() * deltaTime * (settings.getWidth()/2 - x.get() );
        verticalAngle   += settings.getMouseSpeed() * deltaTime * (settings.getHeight()/2 - y.get() );

        direction = new Vector3f(
                (float) cos(verticalAngle) * (float) sin(horizontalAngle),
                (float) sin(verticalAngle),
                (float) cos(verticalAngle) * (float) cos(horizontalAngle)
        );

        right = new Vector3f(
                (float) sin(horizontalAngle - 3.14f/2.0f),
                0,
                (float) cos(horizontalAngle - 3.14f/2.0f)
        );
        // Up vector : perpendicular to both direction and right
        up = right.cross(direction);

        // Move forward
        if (glfwGetKey( windowId, GLFW_KEY_W ) == GLFW_PRESS){
            position = position.add(direction.mul(deltaTime).mul(settings.getMouseSpeed()));
        }
        // Move backward
        if (glfwGetKey( windowId, GLFW_KEY_S ) == GLFW_PRESS){
            position = position.sub(direction.mul(deltaTime).mul(settings.getMouseSpeed()));
        }
        // Strafe right
        if (glfwGetKey( windowId, GLFW_KEY_D ) == GLFW_PRESS){
            position = position.add(right.mul(deltaTime).mul(settings.getMouseSpeed()));
        }
        // Strafe left
        if (glfwGetKey( windowId, GLFW_KEY_A ) == GLFW_PRESS){
            position = position.sub(right.mul(deltaTime).mul(settings.getMouseSpeed()));
        }

        projection = new Matrix4f().perspective((float) Math.toRadians(initialFoV), settings.getAspectRatio(), 0.1f, 100.0f);
        view = new Matrix4f().lookAt(
                position,           // Camera is here
                direction.add(position), // and looks here : at the same position, plus "direction"
                up                  // Head is up (set to 0,-1,0 to look upside-down)
        );
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        return view;
    }
}
