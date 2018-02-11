package renderengine;

import entity.Player;
import model.ObservableWorld;
import shader.Shader;

import static org.lwjgl.opengl.GL20.*;

public class Renderer {
    private Shader shader;

    private int program;
    private Player player;
    private ObservableWorld world;

    public Renderer(Shader shader, Player player, ObservableWorld world) {
        this.shader = shader;
        this.player = player;
        this.world = world;
        program = shader.getProgram();

        glUseProgram(program);
        this.shader.uniform4fv("projection", player.getCamera().getProjectionMatrix());

    }

    public void render() {
        shader.uniform4fv("view", player.getCamera().getViewMatrix());

        world.render();
    }

    public void cleanUp() {
        glUseProgram(0);
    }
}
