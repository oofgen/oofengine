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

    public static int loadShaderProgram(String vertexShader, String fragmentShader) throws Exception {
        logger.info("loading shaders...");
        int programId = glCreateProgramObjectARB();

        int vertexShaderId = createShader(vertexShader, GL_VERTEX_SHADER_ARB);
        int fragmentShaderId = createShader(fragmentShader, GL_FRAGMENT_SHADER_ARB);

        glAttachObjectARB(programId, vertexShaderId);
        glAttachObjectARB(programId, fragmentShaderId);
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
        return programId;
    }

    public static int createShader(String resource, int type) throws IOException {
        logger.info("Compiling shader: {}", resource); //with source \n----\n{}\n----\n", resource, shaderSource);

        int shader = glCreateShaderObjectARB(type);

        URL shaderPath = OofEngineApplication.class.getClassLoader().getResource(resource);
        File shaderFile = new File(shaderPath.getFile());
        String shaderSource = FileUtils.readFileToString(shaderFile, Charset.defaultCharset());

        glShaderSourceARB(shader, shaderSource);
        glCompileShaderARB(shader);

        // check shader compilation
        String shaderLog = GL33.glGetShaderInfoLog(shader);
        if(shaderLog.trim().length() > 0) {
            logger.info(shaderLog);
        }
        int compiled = GL33.glGetShaderi(shader, GL33.GL_COMPILE_STATUS);
        if (compiled == 0) {
            throw new RuntimeException("Could not compile shader");
        }

        return shader;
    }
}
