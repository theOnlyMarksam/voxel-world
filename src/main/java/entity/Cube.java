package entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Cube {
    private static final float[] cube = {
            -0.5f, -0.5f,  0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f
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

    public Cube(Vector3f position, int program) {
        this.position = position;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, cube, GL_STATIC_DRAW);

        int vertexPosition = glGetAttribLocation(program, "position");
        glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexPosition);

        int indicesVbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

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
}
