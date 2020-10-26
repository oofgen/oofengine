package oof.oofengine.render.shader;

import it.unimi.dsi.big.util.StringMap;
import it.unimi.dsi.fastutil.longs.Long2CharSortedMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import oof.oofengine.OofEngineApplication;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.io.FileUtils;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class Shader {
    private static Logger logger = LoggerFactory.getLogger(Shader.class);
    private final int shaderHandle;
    private int vertexHandle;
    private int fragmentHandle;
    private final Object2IntOpenHashMap<String> uniforms = new Object2IntOpenHashMap<>();

    public Shader(int shaderHandle) {
        this.shaderHandle = shaderHandle;
    }

    public int getShaderHandle() {
        return shaderHandle;
    }

    public int createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocationARB(shaderHandle, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
        return uniformLocation;
    }

    public int getUniformHandle(String uniformName) {
        if(uniforms.containsKey(uniformName)) {
            return uniforms.getInt(uniformName);
        } else {
            throw new RuntimeException("Could not find uniform:" + uniformName);
        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
        glUniformMatrix4fvARB(uniforms.getInt(uniformName), false, value.get(buffer));
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.getInt(uniformName), value);
    }

    private int createShader(String resource, int type) {
        logger.info("Compiling shader: {}", resource); //with source \n----\n{}\n----\n", resource, shaderSource);

        int subShaderHandle = glCreateShaderObjectARB(type);

        URL shaderPath = OofEngineApplication.class.getClassLoader().getResource(resource);
        File shaderFile = new File(shaderPath.getFile());
        String shaderSource = null;
        try {
            shaderSource = FileUtils.readFileToString(shaderFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        glShaderSourceARB(subShaderHandle, shaderSource);
        glCompileShaderARB(subShaderHandle);

        // check shader compilation
        String shaderLog = GL33.glGetShaderInfoLog(subShaderHandle);
        if(shaderLog.trim().length() > 0) {
            logger.info(shaderLog);
        }
        int compiled = GL33.glGetShaderi(subShaderHandle, GL33.GL_COMPILE_STATUS);
        if (compiled == 0) {
            throw new RuntimeException("Could not compile shader");
        }
        glAttachObjectARB(shaderHandle, subShaderHandle);


        return subShaderHandle;
    }

    public void createVertexShader(String vertexShader) {
        vertexHandle = createShader(vertexShader, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String fragmentShader) {
        fragmentHandle = createShader(fragmentShader, GL_FRAGMENT_SHADER);
    }
}
