package model;

import entity.Player;
import model.texture.TextureMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shader.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ObservableWorld {
    private static final Logger log = LogManager.getLogger(ObservableWorld.class);

    private int radius = 1;
    private ChunkMap observableChunks;
    private TextureMap textureMap;
    private Player player;
    private Shader shader;
    private int playerChunkX;
    private int playerChunkZ;
    private boolean canBreakBlock = true;

    public ObservableWorld(Shader shader, Player player) {
        this.shader = shader;
        this.player = player;

        playerChunkX = (int) Math.floor(player.getPosition().x / 16);
        playerChunkZ = (int) Math.floor(player.getPosition().z / 16);

        this.observableChunks = new ChunkMap(radius, player, initChunks());
        this.textureMap = new TextureMap();

        initMeshes();
        textureMap.sendDataToShader(shader);
    }

    private void initMeshes() {
        for (Chunk chunk : observableChunks.getChunks()) {
            chunk.createMesh(observableChunks, textureMap, shader);
        }
    }

    private Chunk[] initChunks() {
        int numOfChunks = (2*(radius+1) + 1)*(2*(radius+1) + 1);
        int chunkCount = 0;
        Chunk[] chunks = new Chunk[numOfChunks];

        for (int x = -radius-1; x <= radius+1; x++) {
            for (int z = -radius-1; z <= radius+1; z++) {
                chunks[chunkCount] = generateChunk(x + playerChunkX, z + playerChunkZ);
                chunkCount++;
            }
        }

        return chunks;
    }

    public void render() {

        // this is the goal
        for (Chunk chunk : observableChunks.getChunks()) {
            glBindVertexArray(chunk.getMesh().getVao());
            glDrawArrays(GL_TRIANGLES, 0, chunk.getMesh().getSize());
        }

        glBindVertexArray(0);
    }

    private Chunk generateChunk(int chunkX, int chunkZ) {
        log.debug("Generating chunk for x=" + chunkX + ", z=" + chunkZ);
        Chunk chunk = new Chunk(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 21; y++) {
                    chunk.setBlock(x, y, z, Block.DIRT);
                }
            }
        }

        return chunk;
    }

    public void onPlayerChunkChange(int playerChunkX, int playerChunkZ, int dX, int dZ) {
        log.debug("Player entered chunk x=" + playerChunkX
                + ", z=" + playerChunkZ);

        this.playerChunkX = playerChunkX;
        this.playerChunkZ = playerChunkZ;

        int[] requiredChunks = observableChunks.onPlayerChunkChange(dX, dZ);
        for (int i = 0; i < requiredChunks.length; i += 2) {
            observableChunks.put(generateChunk(requiredChunks[i], requiredChunks[i + 1]));
        }

        for (int i = 2; i < requiredChunks.length-2; i += 2) {
            observableChunks.get(requiredChunks[i]-dX, requiredChunks[i + 1]-dZ)
                    .createMesh(observableChunks, textureMap, shader);
        }
    }

    public void onLmbPress() {
        if (canBreakBlock) {
            canBreakBlock = false;
            // TODO finish
        }
    }

    public void onLmbRelease() {
        canBreakBlock = true;
    }
}
