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
    private Vector3f up = new Vector3f();

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

        horizontalAngle = clamp(horizontalAngle);
        verticalAngle = clamp(verticalAngle);

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

        up = right.cross(direction, up);
        Vector3f newUp = new Vector3f(up);

        // Move forward
        if (isKeyPressed(GLFW_KEY_W)){
            position = position.add(direction.mul(deltaTime).mul(settings.getMouseSpeed()));
        }
        // Move backward
        if (isKeyPressed(GLFW_KEY_S)){
            position = position.sub(direction.mul(deltaTime).mul(settings.getMouseSpeed()));
        }

        // Strafe right
        if (isKeyPressed(GLFW_KEY_D)){
            right = right.mul(deltaTime).mul(settings.getMouseSpeed());
            position = position.add(right);
            logger.info(String.valueOf(position));
        }
        // Strafe left
        if (isKeyPressed(GLFW_KEY_A)){
            right = right.mul(deltaTime).mul(settings.getMouseSpeed());
            position = position.sub(right);
        }
        direction = direction.add(position); // and looks here : at the same position, plus "direction"

        // center object
        if (isKeyPressed(GLFW_KEY_R)) {
            direction = new Vector3f(3.320E+0f,  2.483E+0f,  2.480E+0f);
            horizontalAngle = -171.86925f;
            verticalAngle = 175.38533f;
        }

        if(isKeyPressed(GLFW_KEY_J)) {
            direction = new Vector3f(0, 0, 0);
            horizontalAngle = 0;
            verticalAngle = 0;
        }
        // debug position
        if (isKeyPressed(GLFW_KEY_L)) {
            System.out.printf("direction: %s | horAngle: %s | verAngle: %s\r", direction, horizontalAngle, verticalAngle);
        }

        projection = new Matrix4f().perspective((float) Math.toRadians(initialFoV), settings.getAspectRatio(), 0.1f, 100.0f);
        view = new Matrix4f().lookAt(
                position,           // Camera is here
                direction,          // and looks here : at the same position, plus "direction"
                up                  // Head is up (set to 0,-1,0 to look upside-down)
        );
    }

    private boolean isKeyPressed(int key) {
        return glfwGetKey(windowId, key) == GLFW_PRESS;
    }

    private float clamp(float angle) {
        if(angle > 360) return 360;
        if(angle < -360) return 0;
        return angle;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        return view;
    }
}
