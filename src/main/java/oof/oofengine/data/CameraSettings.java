package oof.oofengine.data;

import org.joml.Vector3f;

public class CameraSettings {
    private Vector3f initialPosition;
    private Vector3f initialDirection;
    private Vector3f initialRotation;

    public float getCameraSpeed() {
        return cameraSpeed;
    }

    public void setCameraSpeed(float cameraSpeed) {
        this.cameraSpeed = cameraSpeed;
    }

    public float getMouseSpeed() {
        return mouseSpeed;
    }

    public void setMouseSpeed(float mouseSpeed) {
        this.mouseSpeed = mouseSpeed;
    }

    private float cameraSpeed;
    private float mouseSpeed;

    //(float) Math.toRadians(fov), aspectRatio, 0.1f, 100.0f
    private double fov;
    private float width;
    private float height;

    public CameraSettings() {
    }

    public Vector3f getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Vector3f initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Vector3f getInitialDirection() {
        return initialDirection;
    }

    public void setInitialDirection(Vector3f initialDirection) {
        this.initialDirection = initialDirection;
    }

    public Vector3f getInitialRotation() {
        return initialRotation;
    }

    public void setInitialRotation(Vector3f initialRotation) {
        this.initialRotation = initialRotation;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    private float aspectRatio;
}
