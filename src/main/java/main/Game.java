package main;

import gamestate.PlayState;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {
    private static final Logger log = LogManager.getLogger(Game.class);

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private long window;

    private void run() {
        log.info("LWJGL version: " + Version.getVersion());

        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW!");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Voxel-World", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window!");

        /*glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->{
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });*/

        try(MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Check if OpenGL version is at least 4.5.0
        String openGL_version = glGetString(GL_VERSION);
        checkOpenGlVersion(openGL_version);

        log.info("OpenGL version: " + openGL_version);
        log.info("Graphics card: " + glGetString(GL_RENDERER));


        glViewport(0, 0, WIDTH, HEIGHT);
        glfwSwapInterval(0);
        glfwShowWindow(window);
    }

    private void checkOpenGlVersion(String versionInfo) {
        String version = versionInfo.split(" ")[0];

        String[] versionParts = version.split("[.]");
        int mainNumber = Integer.parseInt(versionParts[0]);
        int secondaryNumber = Integer.parseInt(versionParts[1]);

        if (mainNumber < 4 || secondaryNumber < 5) {
            log.error("Minimum required OpenGL version: 4.5.0; Your OpenGL version: " + version);
            throw new RuntimeException("OpenGL version does not match requirements.");
        }
    }

    private void loop() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        PlayState playState = new PlayState(window);

        float delta;
        long lastUpdate;
        long currentUpdate = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            lastUpdate = currentUpdate;
            currentUpdate = System.nanoTime();
            delta = (float)(currentUpdate - lastUpdate)/1e9f;

            playState.update(delta);
            playState.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Game().run();
    }
}
