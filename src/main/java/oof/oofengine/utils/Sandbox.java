package oof.oofengine.utils;
import oof.oofengine.data.Object3D;
import oof.oofengine.data.OofConfig;
import org.apache.commons.io.FileUtils;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Sandbox {

    private static final Logger logger = LoggerFactory.getLogger(Sandbox.class);
    private final Object3D object;
    private final OofConfig config;
    // The window handle
    private long window;

    public Sandbox(OofConfig config) {
        this.config = config;
        this.object = DefaultOof();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        FileUtils.deleteQuietly(new File("image.png"));

        init();

        if(config.debug()) {
            loop();
        }

        render();
        serializeCurrentFrame(window);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void render() {

        /* Declare buffers for using inside the loop */
        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);

        float ratio;

        /* Get width and height to calcualte the ratio */
        glfwGetFramebufferSize(window, width, height);
        ratio = width.get() / (float) height.get();


        /* Rewind buffers for next get */
        width.rewind();
        height.rewind();

        /* Set viewport and clear screen */
        glViewport(0, 0, width.get(), height.get());
        glClear(GL_COLOR_BUFFER_BIT);

        /* Set ortographic projection */
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-ratio, ratio, -1f, 1f, 1f, -1f);
        glMatrixMode(GL_MODELVIEW);

        /* Rotate matrix */

        glLoadIdentity();
        glRotatef((float) glfwGetTime() * 50f, 0f, 0f, 1f);

        // set color
        glClearColor(0, 255, 255, 0.10f);

        /* Render triangle */
        glBegin(GL_TRIANGLES);
        glColor3f(1f, 0f, 0f);
        glVertex3f(-0.6f, -0.4f, 0f);
        glColor3f(0f, 1f, 0f);
        glVertex3f(0.6f, -0.4f, 0f);
        glColor3f(0f, 0f, 1f);
        glVertex3f(0f, 0.6f, 0f);
        glEnd();
    }

    private static void serializeCurrentFrame(long windowId) {
        IntBuffer _width = MemoryUtil.memAllocInt(1);
        IntBuffer _height = MemoryUtil.memAllocInt(1);
        glfwGetFramebufferSize(windowId, _width, _height);

        int width = _width.get();
        int height = _height.get();

        int bytesPerPixel = 4; // r, g, b, a
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bytesPerPixel);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        String imageFile = "image";
        encodePNG(width, height, bytesPerPixel, buffer, imageFile);
    }

    private static void encodePNG(int width, int height, int bytesPerPixel, ByteBuffer buffer, String imagePath) {
        String format = "PNG";
        imagePath = imagePath.concat(".").concat(format.toLowerCase());
        File imageFile = new File(imagePath);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int i = (x + (width * y)) * bytesPerPixel;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }

        }

        try {
            ImageIO.write(image, format, imageFile);
        } catch(IOException e) {
            e.printStackTrace();
            logger.error("Was unable to write file due to IOException", e);
        } catch(Exception e) {
            logger.error("Unknown exception was caught!", e);
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(config.getWidthPx(), config.getHeightPx(), "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        GL.createCapabilities();

        // Make the window visible
        if(config.debug()) {
            glfwShowWindow(window);
        }
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glfwMakeContextCurrent(window);

        // Set the clear color
        glClearColor(0, 255, 255, 0.10f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static Object3D DefaultOof() {
        return new Object3D(
                loadResource("oof/bighead.obj"),
                loadResource("oof/bighead.mtl"),
                loadResource("oof/ooftex.png")
        );
    }

    public static File loadResource(String filePath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            return new File(Objects.requireNonNull(loader.getResource(filePath)).getFile());
        } catch (NullPointerException e) {
            throw new RuntimeException("Couldn't find file {}", e);
        }
    }

    public static boolean validateObject3D(Object3D obj) {
        if(!obj.getOofModel().exists()) {
            return false;
        }

        if(!obj.getOofMTL().exists()) {
            return false;
        }

        if(!obj.getOofTexture().exists()) {
            return false;
        }

        return true;
    }
}
