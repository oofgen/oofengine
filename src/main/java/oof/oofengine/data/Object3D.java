package oof.oofengine.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Object3D {
    public static Object3D loadObjModel(String filename) {
        FileReader fileRead = null;

        try {
            fileRead = new FileReader(new File(String.format("../../../../resources/oof/%s.obj", filename)));
        } catch (FileNotFoundException e) {
            System.err.printf("Could not open %.obj\n", filename);
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(fileRead);
        String line = "";

        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Integer> indices = new ArrayList<Integer>();

        float[] verticesArray = null;
        float[] texturesArray = null;
        float[] normalsArray = null;
        int[] indicesArray = null;


        try {
            boolean reachedF = false;
            while (!reachedF) {
                line = reader.readLine();

                if (line.startsWith("v ")) {
                    vertices.add(new Vector3f(line));
                } else if (line.startsWith("vn ")) {
                    normals.add(new Vector3f(line));
                } else if (line.startsWith("vt ")) {
                    textures.add(new Vector2f(line));
                } else if (line.startsWith("f ")) {
                    texturesArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 2];
                    reachedF = true;
                }
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f vertex:vertices) {
            verticesArray[vertexPointer++] = vertex.getX();
            verticesArray[vertexPointer++] = vertex.getY();
            verticesArray[vertexPointer++] = vertex.getZ();
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        //return loader.loadToVAO(verticesArray, texturesArray, indicesArray);
        return null;
    }


    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTex.getX();
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.getY();

        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.getX();
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.getY();
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.getZ();
    }
}
