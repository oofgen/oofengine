package oof.oofengine.model;

import oof.oofengine.model.api.IModel;
import oof.oofengine.render.shader.Shader;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.ARBShaderObjects.glUniform3fvARB;
import static org.lwjgl.opengl.ARBShaderObjects.glUniformMatrix3fvARB;
import static org.lwjgl.opengl.ARBShaderObjects.glUniformMatrix4fvARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBVertexProgram.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Model implements IModel {

    public AIScene scene;
    public List<Mesh> meshes;
    public List<Material> materials;

    public Vector3f position;
    private Matrix4f model = new Matrix4f().identity();
    private Matrix4f modelViewProjection = null;
    private FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

    public Model(AIScene scene) {
        this.scene = scene;

        int meshCount = scene.mNumMeshes();
        PointerBuffer meshesBuffer = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshCount; ++i) {
            meshes.add(new Mesh(AIMesh.create(meshesBuffer.get(i))));
        }

        int materialCount = scene.mNumMaterials();
        PointerBuffer materialsBuffer = scene.mMaterials();
        materials = new ArrayList<>();
        for (int i = 0; i < materialCount; ++i) {
            materials.add(new Material(AIMaterial.create(materialsBuffer.get(i))));
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, StandardToStringStyle.SHORT_PREFIX_STYLE)
                .append("meshCount", meshes.size())
                .append("materialCount", materials.size())
                .build();
    }


    @Override
    public void free() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        materials = null;
    }

    @Override
    public void init(Shader shader) {
    }

    @Override
    public void draw(Shader shader, Matrix4f viewProjection) {
        for (Mesh mesh : meshes) {
            //glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.vertexArrayBuffer);
            //glVertexAttribPointerARB(vertexAttribute, 3, GL_FLOAT, false, 0, 0);
            //glBindBufferARB(GL_ARRAY_BUFFER_ARB, mesh.normalArrayBuffer);
            //glVertexAttribPointerARB(normalAttribute, 3, GL_FLOAT, false, 0, 0);

            modelViewProjection = viewProjection.mul(model, modelViewProjection);
            shader.setUniform("MVP", modelViewProjection);
            //glUniformMatrix4fvARB(viewProjectionMatrixUniform, false, viewProjectionMatrix.get(viewProjectionMatrixBuffer));
            //normalMatrix.set(modelMatrix).invert().transpose();
            //glUniformMatrix3fvARB(normalMatrixUniform, false, normalMatrix.get(normalMatrixBuffer));
            //glUniform3fvARB(lightPositionUniform, lightPosition.get(lightPositionBuffer));
            //glUniform3fvARB(viewPositionUniform, viewPosition.get(viewPositionBuffer));

            Material material = materials.get(mesh.mesh.mMaterialIndex());
            //nglUniform3fvARB(ambientColorUniform, 1, material.mAmbientColor.address());
            //nglUniform3fvARB(diffuseColorUniform, 1, material.mDiffuseColor.address());
            //nglUniform3fvARB(specularColorUniform, 1, material.mSpecularColor.address());

            glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, mesh.elementArrayBuffer);
            glDrawElements(GL_TRIANGLES, mesh.elementCount, GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public Vector3f getRotation() {
        return null;
    }

    @Override
    public Vector3f getPosition() {
        return null;
    }
}
