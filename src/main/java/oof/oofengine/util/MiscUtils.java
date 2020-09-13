package oof.oofengine.util;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class MiscUtils {


    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }


}
