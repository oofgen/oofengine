package oof.oofengine.model;

import oof.oofengine.util.TextureUtils;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.opengl.ARBShaderObjects.glGetUniformLocationARB;
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

public class SimpleModel {
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

    public SimpleModel(float[] vertexBufferData, String textureResourcePath) {
        this.vertexBufferData = vertexBufferData;
        this.textureResourcePath = textureResourcePath;
    }

    public void init(int shaderProgramId) {
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
        textureId = glGetUniformLocationARB(shaderProgramId, "textureSampler");
        if(textureId == -1) {
            throw new RuntimeException(String.format("\nglGetUniformLocationARB failed with params:\nshaderProgramId = %s\nname = \"textureSampler\"", shaderProgramId));
        }
    }

    public void draw() {
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
    };

    public void free() {
        glDeleteBuffers(vertexId);
        glDeleteBuffers(uvId);
        glDeleteTextures(textureId);
        glDeleteVertexArrays(vertexArrayId);
    }
}
