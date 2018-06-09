package gamestate;

import entity.Player;
import main.Game;
import model.ObservableWorld;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderengine.Renderer;
import shader.Shader;

import static org.lwjgl.glfw.GLFW.*;

public class PlayState implements GameState {
    private static float speed = 5f;

    private Shader shader;
    private Renderer renderer;
    private Player player;
    private ObservableWorld observableWorld;
    private Vector3f lastPlayerPosition;

    private Vector2f rotation = new Vector2f(0.0f, 0.0f);
    private long window;

    public PlayState(long window) {
        this.window = window;
        glfwSetKeyCallback(window, this::readInput);
        glfwSetMouseButtonCallback(window, this::mouseInput);

        this.shader = new Shader("default");
        this.player = new Player(new Vector3f(0.0f, 25.0f, 0.0f));
        this.lastPlayerPosition = new Vector3f(player.getPosition());
        this.observableWorld = new ObservableWorld(shader, player);
        this.renderer = new Renderer(shader, player, observableWorld);
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

        lastPlayerPosition = lastPlayerPosition.set(player.getPosition());
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            player.moveForward(dt * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            player.moveForward(-dt * speed);
        }

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            player.moveLeft(-dt * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            player.moveLeft(dt * speed);
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            player.moveUp(dt * 2 * speed);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            player.moveUp(-dt * 2 * speed);
        }

        updateObservableWorld();

        player.getCamera().update();

        rotation.set(0.0f);
    }

    private void updateObservableWorld() {
        int lastChunkX = (int) Math.floor(lastPlayerPosition.x / 16);
        int lastChunkZ = (int) Math.floor(lastPlayerPosition.z / 16);

        int currentChunkX = (int) Math.floor(player.getPosition().x / 16);
        int currentChunkZ = (int) Math.floor(player.getPosition().z / 16);

        if (currentChunkX - lastChunkX != 0 || currentChunkZ - lastChunkZ != 0) {
            observableWorld.onPlayerChunkChange(
                    currentChunkX
                    , currentChunkZ
                    , currentChunkX - lastChunkX
                    , currentChunkZ - lastChunkZ
            );
        }
    }

    @Override
    public void render() {
        renderer.render();
    }

    private void readInput(long window, int key, int scanCode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private void mouseInput(long window, int button, int action, int mods) {
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
            observableWorld.onLmbPress();
        }

        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
            observableWorld.onLmbRelease();
        }
    }
}
