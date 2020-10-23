package oof.oofengine.util;

import oof.oofengine.model.Texture;
import org.junit.Test;

import java.nio.file.Paths;

class TextureUtilsTest {

    @Test
    public void loadTexture() {
    }

    @Test
    public void loadTextureAsResource() {
        Texture texture = TextureUtils.getTextureAsResource(Paths.get("texture/uvtemplate.bmp"));
        assert texture.getPixelBuffer().hasRemaining();
    }
}
