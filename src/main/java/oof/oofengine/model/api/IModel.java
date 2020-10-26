package oof.oofengine.model.api;

import oof.oofengine.render.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface IModel {

    public void init(Shader shader);
    public void free();
    public void draw(Shader shader, Matrix4f transformation);

    public Vector3f getRotation();
    public Vector3f getPosition();
}
