package oof.oofengine.util;

import oof.oofengine.data.Texture;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

class TextureUtilsTest {

    @Test
    void loadTexture() {
    }

    @Test
    void loadTextureAsResource() {
        Texture texture = TextureUtils.getTextureAsResource(Paths.get("texture/uvtemplate.bmp"));
        assert texture.getPixelBuffer().hasRemaining();
    }
}