package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
    private static final Logger log = LogManager.getLogger(Chunk.class);

    private byte[] blocks = new byte[16 * 16 * 256];

    public void setBlock(int x, int y, int z, byte blockType) {
        checkCoordinates(x, y, z);

        int i = 256 * y;
        i += 16 * z;
        i += x;

        blocks[i] = blockType;
    }

    public byte getblock(int x, int y, int z) {
        checkCoordinates(x, y, z);

        return blocks[x + 256*y + 16*z];
    }

    public byte[] getBlocks() {
        return blocks;
    }

    private void checkCoordinates(int x, int y, int z) {
        if (x < 0 || x > 15) {
            log.error("x coordinate out of bounds: " + x);
            throw new RuntimeException("Problem with set block");
        } else if (y < 0 || y > 255) {
            log.error("y coordinate out of bounds: " + y);
            throw new RuntimeException("Problem with set block");
        } else if (z < 0 || z > 15) {
            log.error("z coordinate out of bounds: " + z);
            throw new RuntimeException("Problem with set block");
        }
    }
}
