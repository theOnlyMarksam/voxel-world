package model;

import entity.Player;
import model.mesh.Mesh;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shader.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ObservableWorld {
    private static final Logger log = LogManager.getLogger(ObservableWorld.class);

    private int radius = 0;
    private ChunkMap observableWorld;
    private Player player;
    private Mesh mesh;

    public ObservableWorld(Shader shader, Player player) {
        this.player = player;
        this.observableWorld = new ChunkMap(radius, player);

        observableWorld.put(generateChunk(0, 0));

        this.mesh = new Mesh(observableWorld, shader);
        mesh.generateMesh();
    }

    public void render() {
        // this is the goal
        /*for (Chunk chunk : observableWorld.getChunks()) {
            glBindVertexArray(chunk.getMesh().getVao());
            glDrawArrays(GL_TRIANGLES, 0, mesh.getSize());
        }*/

        glBindVertexArray(mesh.getVao());
        glDrawArrays(GL_TRIANGLES, 0, mesh.getSize());

        glBindVertexArray(0);
    }

    private Chunk generateChunk(int chunkX, int chunkZ) {
        log.debug("Generating chunk for x=" + chunkX + ", z=" + chunkZ);
        Chunk chunk = new Chunk(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 21; y += 2) {
                    chunk.setBlock(x, y, z, Block.DIRT);
                }
            }
        }

        return chunk;
    }

    public void onPlayerChunkChange(int playerChunkX, int playerChunkZ, int dX, int dZ) {
        log.debug("Player entered chunk x=" + playerChunkX
                + ", z=" + playerChunkZ);

        int[] requiredChunks = observableWorld.onPlayerChunkChange(dX, dZ);
        for (int i = 0; i < requiredChunks.length; i += 2) {
            observableWorld.put(generateChunk(requiredChunks[i], requiredChunks[i + 1]));
        }
        mesh.generateMesh();
    }
}
