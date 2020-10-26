package oof.oofengine.model;

import oof.oofengine.model.api.IModel;
import oof.oofengine.render.shader.Shader;
import oof.oofengine.util.TextureUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.ARBShaderObjects.glGetUniformLocationARB;
import static org.lwjgl.opengl.ARBShaderObjects.glUniformMatrix4fvARB;
import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBVertexShader.glDisableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glEnableVertexAttribArrayARB;
import static org.lwjgl.opengl.ARBVertexShader.glVertexAttribPointerARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class SimpleModel implements IModel {
    private final String textureResourcePath;
    private final float[] vertexBufferData;

    private final IntBuffer vertexArrayId = BufferUtils.createIntBuffer(1);
    private final IntBuffer vertexBuffer = BufferUtils.createIntBuffer(1);
    private int vertexId = -200;
    private int textureId = -200;
    private final IntBuffer textureBuffer = BufferUtils.createIntBuffer(1);
    private final IntBuffer uvBuffer = BufferUtils.createIntBuffer(1);
    private int uvId = -1;
    private int texture;
    private boolean init;
    private FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(4 * 4);
    // Model matrix : an identity matrix (model will be at the origin to start)

    private Matrix4f model = new Matrix4f().identity();
    private Matrix4f modelView = new Matrix4f();
    private Vector3f rotation = new Vector3f();
    private Vector3f position = new Vector3f();
    private float scale = 1.0f;

    public SimpleModel(float[] vertexBufferData, String textureResourcePath) {
        this.vertexBufferData = vertexBufferData;
        this.textureResourcePath = textureResourcePath;
    }

    @Override
    public void init(Shader shader) {
        if(checkInit()) {
            return;
        }
        // vertexes
        glGenVertexArrays(vertexArrayId);
        glBindVertexArray(vertexArrayId.get());

        glGenBuffersARB(vertexBuffer);
        vertexId = vertexBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, vertexBufferData, GL_STATIC_DRAW_ARB);

        // uvs
        glGenBuffersARB(uvBuffer);
        uvId = uvBuffer.get();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, uvId);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, ObjectMatrixSamples.uvs, GL_STATIC_DRAW_ARB);

        // texture
        texture = TextureUtils.loadTextureAsResource(Paths.get(textureResourcePath));
        // Get a handle for our "myTextureSampler" uniform
        textureId = shader.createUniform("texture_sampler");
    }

    private boolean checkInit() {
        if(init) {
            return true;
        }
        init = true;
        return false;
    }

    public Matrix4f getModelViewMatrix(Matrix4f viewMatrix) {
        modelView.identity().translate(position).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(scale);
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelView);
    }

    @Override
    public void draw(Shader shader, Matrix4f view) {
        if(!init) {
            throw new RuntimeException("Trying to draw uninitialized SimpleModel!");
        }

        shader.setUniform("modelViewMatrix", getModelViewMatrix(view));

        // Bind our texture in Texture Unit 0
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        // Set our "texture_sampler" sampler to use Texture Unit 0
        shader.setUniform("texture_sampler", 0);

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
    }

    @Override
    public Vector3f getRotation() {
        return null;
    }

    @Override
    public Vector3f getPosition() {
        return null;
    }

    public void free() {
        glDeleteBuffers(vertexId);
        glDeleteBuffers(uvId);
        glDeleteTextures(textureId);
        glDeleteVertexArrays(vertexArrayId);
    }
}
