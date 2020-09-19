package oof.oofengine.data;

import net.buttology.lwjgl.dds.DDSFile;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.Arrays;

public class Texture {
    private DDSFile file;
    private int width, height;
    private ByteBuffer pixelBuffer;

    @SuppressWarnings("unused")
    private Texture() {}

    public static Texture from(Path texturePath) {
        try {
            DDSFile file = new DDSFile(texturePath.toFile());
            return new Texture(file.getWidth(), file.getHeight(), file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot load texture: %s", texturePath), e);
        }
    }

    public Texture(int width, int height, DDSFile file) {
        this.width = width;
        this.height = height;
        this.file = file;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        System.out.println(data.length + "-" + width * height * 3);
        this.pixelBuffer = BufferUtils.createByteBuffer(width * height * 3);
        this.pixelBuffer.put(data);
    }

    public ByteBuffer getPixelBuffer() {
        return pixelBuffer;
    }


    public DDSFile getFile() {
        return file;
    }
}
