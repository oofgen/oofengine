package oof.oofengine.util;

import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class MiscUtils {


    private static Logger logger = LoggerFactory.getLogger(MiscUtils.class);

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Call this in debugger line-by-line if you're desperate.
     */
    public static void handleGLErrors() {
        int errorCode = 0;
        do {
            errorCode = glGetError();
            if(errorCode != GL_NO_ERROR) {
                String message = String.format("GL reported error code %s", errorCode);
                logger.error(message);
                throw new RuntimeException(message);
            }
        } while(errorCode != GL_NO_ERROR);
    }


}
