package oof.oofengine.render.shader;

import oof.oofengine.OofEngineApplication;
import oof.oofengine.render.Tutorial;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBShaderObjects.glCompileShaderARB;
import static org.lwjgl.opengl.ARBVertexShader.GL_VERTEX_SHADER_ARB;

public class ShaderManager {
    private static final Logger logger = LoggerFactory.getLogger(ShaderManager.class);
    private static Shader shader;

    public static Shader getShader() {
        if(shader == null) {
            throw new RuntimeException("No shaders loaded!");
        } else return shader;
    }

    public static Shader loadShaderProgram(String vertexShader, String fragmentShader) throws Exception {
        logger.info("loading shaders...");
        int programId = glCreateProgramObjectARB();
        shader = new Shader(programId);
        shader.createVertexShader(vertexShader);
        shader.createFragmentShader(fragmentShader);
        glLinkProgramARB(programId);

        // check shaderProgram link
        int linkStatus = GL33.glGetProgrami(programId, GL33.GL_LINK_STATUS);
        String programLog = GL33.glGetProgramInfoLog(programId);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linkStatus == 0) {
            throw new RuntimeException("Could not link program");
        }

        logger.info("loaded.");
        return shader;
    }
}
