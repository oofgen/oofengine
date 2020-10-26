package oof.oofengine.model;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_STATIC_DRAW_ARB;

public class Mesh {

    public AIMesh aiMesh;
    public int vertexArrayBuffer;
    public int normalArrayBuffer;
    public int elementArrayBuffer;
    public int elementCount;

    public Mesh(AIMesh aiMesh) {
        this.aiMesh = aiMesh;

        vertexArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, vertexArrayBuffer);
        AIVector3D.Buffer vertices = aiMesh.mVertices();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * vertices.remaining(),
                vertices.address(), GL_STATIC_DRAW_ARB);

        normalArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, normalArrayBuffer);
        AIVector3D.Buffer normals = aiMesh.mNormals();
        nglBufferDataARB(GL_ARRAY_BUFFER_ARB, AIVector3D.SIZEOF * normals.remaining(), normals.address(), GL_STATIC_DRAW_ARB);

        int faceCount = aiMesh.mNumFaces();
        elementCount = faceCount * 3;
        IntBuffer elementArrayBufferData = BufferUtils.createIntBuffer(elementCount);
        AIFace.Buffer facesBuffer = aiMesh.mFaces();
        for (int i = 0; i < faceCount; ++i) {
            AIFace face = facesBuffer.get(i);
            if (face.mNumIndices() != 3) {
                throw new IllegalStateException("AIFace.mNumIndices() != 3");
            }
            elementArrayBufferData.put(face.mIndices());
        }
        elementArrayBufferData.flip();
        elementArrayBuffer = glGenBuffersARB();
        glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBuffer);
        glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, elementArrayBufferData, GL_STATIC_DRAW_ARB);
    }
}

