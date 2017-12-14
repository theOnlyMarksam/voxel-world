package gamestate;

import entity.Cube;
import entity.Player;
import main.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderengine.Renderer;
import shader.Shader;

import static org.lwjgl.glfw.GLFW.*;

public class PlayState implements GameState {
    private static float speed = 5f;

    private Shader shader;
    private Cube cube;
    private Renderer renderer;
    private Player player;

    private Vector2f rotation = new Vector2f(0.0f, 0.0f);
    private long window;

    public PlayState(long window) {
        this.window = window;
        glfwSetKeyCallback(window, this::readInput);

        shader = new Shader("default");
        cube = new Cube(new Vector3f(0.0f, 0.0f, -5f), shader.getProgram());
        player = new Player(new Vector3f(0.0f, 0.0f, 0f));
        renderer = new Renderer(shader, cube, player);
    }

    @Override
    public void update(float dt) {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        glfwGetCursorPos(window, xPos, yPos);

        rotation.x += Game.HEIGHT/2.0f - (float)yPos[0];
        rotation.y += Game.WIDTH/2.0f - (float)xPos[0];

        glfwSetCursorPos(window, Game.WIDTH/2.0, Game.HEIGHT/2.0);

        rotation.mul(dt * 0.2f);

        player.rotateView(rotation.x, rotation.y);

        if (glfwGetKey(window, GLFW_KEY_W) == 1) {
            player.moveForward(dt * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == 1) {
            player.moveForward(-dt * speed);
        }

        if (glfwGetKey(window, GLFW_KEY_A) == 1) {
            player.moveLeft(-dt * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == 1) {
            player.moveLeft(dt * speed);
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == 1) {
            player.moveUp(dt * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
            player.moveUp(-dt * speed);
        }

        player.getCamera().update();

        rotation.set(0.0f);
    }

    @Override
    public void render() {
        renderer.render();
    }

    private void readInput(long window, int key, int scanCode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            glfwSetWindowShouldClose(window, true);
    }
}
