package oof.oofengine.control;

import org.joml.Matrix4f;

public interface Camera {
    public void computeMatricesFromInputs();
    public Matrix4f getProjection();
    public Matrix4f getView();
}
