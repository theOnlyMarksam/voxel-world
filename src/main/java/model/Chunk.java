package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
    private static final Logger log = LogManager.getLogger(Chunk.class);

    private Block[] blocks = new Block[16 * 16 * 256];
    private int chunkX;
    private int chunkZ;

    public Chunk(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = Block.AIR;
        }
    }

    public void setBlock(int x, int y, int z, Block blockType) {
        checkCoordinates(x, y, z);

        int i = 256 * y;
        i += 16 * z;
        i += x;

        blocks[i] = blockType;
    }

    public Block getBlock(int x, int y, int z) {
        checkCoordinates(x, y, z);

        return blocks[x + 256*y + 16*z];
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    private void checkCoordinates(int x, int y, int z) {
        if (x < 0 || x > 15) {
            log.error("x coordinate out of bounds: " + x);
            throw new RuntimeException("Problem with coordinate");
        } else if (y < 0 || y > 255) {
            log.error("y coordinate out of bounds: " + y);
            throw new RuntimeException("Problem with coordinate");
        } else if (z < 0 || z > 15) {
            log.error("z coordinate out of bounds: " + z);
            throw new RuntimeException("Problem with coordinate");
        }
    }
}
