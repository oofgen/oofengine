package oof.oofengine.control.impl;

import oof.oofengine.control.Camera;
import org.joml.Matrix4f;

public class ObjectCenteredCamera implements Camera {
    private Matrix4f view;
    private Matrix4f projection;





    @Override
    public void computeMatricesFromInputs() {

    }

    @Override
    public Matrix4f getProjection() {
        return projection;
    }

    @Override
    public Matrix4f getView() {
        return view;
    }
}
