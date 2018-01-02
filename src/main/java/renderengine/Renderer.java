package renderengine;

import entity.Cube;
import entity.Player;
import model.Chunk;
import model.ObservableWorld;
import org.joml.Matrix4f;
import shader.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {
    private Shader shader;

    private int program;
    private Cube cube;
    private Player player;
    private ObservableWorld world;

    public Renderer(Shader shader, Cube cube, Player player) {
        this.shader = shader;
        this.cube = cube;
        this.player = player;
        this.world = new ObservableWorld();
        program = shader.getProgram();

        glUseProgram(program);
        this.shader.uniform4fv("projection", player.getCamera().getProjectionMatrix());

    }

    public void render() {
        Matrix4f transform = new Matrix4f();
        shader.uniform4fv("view", player.getCamera().getViewMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE0, cube.getTextureHandle());
        glBindVertexArray(cube.getVao());

        for (Chunk c: world.getChunks()) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (c.getblock(x, y, z) == 1) {
                            transform.translation((float) x, (float) y, (float) z);
                            shader.uniform4fv("transform", transform);
                            glDrawElements(GL_TRIANGLES, cube.getSize(), GL_UNSIGNED_INT, 0);
                            transform.identity();
                        }
                    }
                }
            }
        }

        /*transform.translation((float) 0, (float) 0, (float) -5);
        shader.uniform4fv("transform", transform);
        glDrawElements(GL_TRIANGLES, cube.getSize(), GL_UNSIGNED_INT, 0);
        transform.identity();*/

        glBindVertexArray(0);
    }

    public void cleanUp() {
        glUseProgram(0);
    }
}
