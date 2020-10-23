package oof.oofengine.util;

import oof.oofengine.OofEngineApplication;
import oof.oofengine.model.Texture;
import org.lwjgl.opengl.GL13;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

public class TextureUtils {

    /**
     * loadTexture - read texture into memory and pass to OpenGL
     * @param texture texture to load
     * @return id of texture that we just loaded
     */
    public static int bufferTexture(Texture texture) {
        // texture
        int textureId = glGenTextures();
        GL13.glActiveTexture(GL_TEXTURE0);     // Depends on your implementation
        glBindTexture(GL_TEXTURE_2D, textureId);


        for (int level = 0; level < texture.getFile().getMipMapCount(); level++)
            GL13.glCompressedTexImage2D(
                    GL_TEXTURE_2D,
                    level,
                    texture.getFile().getFormat(),
                    texture.getFile().getWidth(level),
                    texture.getFile().getHeight(level),
                    0,
                    texture.getFile().getBuffer(level)
            );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, texture.getFile().getMipMapCount() - 1);

        return textureId;
    }

    public static int loadTexture(Path texturePath) {
        return bufferTexture(Texture.from(texturePath));
    }

    public static int loadTextureAsResource(Path texturePath) {
        return bufferTexture(getTextureAsResource(texturePath));

    }

    public static Texture getTextureAsResource(Path texturePath) {
        try {
            Path textureResourcePath = Paths.get(Objects.requireNonNull(OofEngineApplication.class.getClassLoader().getResource(texturePath.toString())).toURI());
            return Texture.from(textureResourcePath);
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }


}
