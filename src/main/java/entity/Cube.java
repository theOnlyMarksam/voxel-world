package entity;

import files.TextureUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.net.URL;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Cube {
    private static final float[] cube = {
            -0.5f, -0.5f,  0.5f, 0.0f, 0.0f,
             0.5f, -0.5f,  0.5f, 1.0f, 0.0f,
             0.5f,  0.5f,  0.5f, 1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
             0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
             0.5f,  0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f, 0.0f, 1.0f,
    };

    private static final int[] indices = {
            0, 1, 2,
            0, 2, 3,
            1, 5, 6,
            1, 6, 2,
            0, 4, 1,
            1, 4, 5,
            0, 3, 4,
            3, 7, 4,
            2, 6, 3,
            3, 6, 7,
            4, 6, 5,
            4, 7, 6
    };

    private int vao;
    private Vector3f position;
    private int textureHandle;

    public Cube(Vector3f position, int program) {
        this.position = position;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, cube, GL_STATIC_DRAW);

        int loc = glGetAttribLocation(program, "position");
        glVertexAttribPointer(loc, 3, GL_FLOAT, false, 5*4, 0);
        glEnableVertexAttribArray(loc);

        loc = glGetAttribLocation(program, "uv");
        glVertexAttribPointer(loc, 2, GL_FLOAT, false, 5*4, 3*4);
        glEnableVertexAttribArray(loc);

        int indicesVbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int[] x = new int[1];
        int[] y = new int[1];

        URL imageName = getClass().getClassLoader().getResource("textures/blocks/dirt.png");
        ByteBuffer image = TextureUtil.loadPngImage(imageName, x, y, 3);

        textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureHandle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, x[0], y[0], 0, GL_RGB, GL_UNSIGNED_BYTE, image);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public Matrix4f getTransform() {
        return new Matrix4f().translation(position);
    }

    public int getVao() {
        return vao;
    }

    public int getSize() {
        return indices.length;
    }

    public int getTextureHandle() {
        return textureHandle;
    }
}
