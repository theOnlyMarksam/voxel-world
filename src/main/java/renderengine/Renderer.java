package renderengine;

import entity.Cube;
import entity.Player;
import shader.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {
    private Shader shader;

    //private int vao;
    //private int vbo;
    private int program;
    private Cube cube;
    private Player player;

    public Renderer(Shader shader, Cube cube, Player player) {
        this.shader = shader;
        this.cube = cube;
        this.player = player;
        program = shader.getProgram();

        glUseProgram(program);
        this.shader.uniform4fv("projection", player.getCamera().getProjectionMatrix());
    }

    public void render() {
        shader.uniform4fv("view", player.getCamera().getViewMatrix());
        shader.uniform4fv("transform", cube.getTransform());

        glBindVertexArray(cube.getVao());
        glDrawElements(GL_TRIANGLES, cube.getSize(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanUp() {
        glUseProgram(0);
    }
}
