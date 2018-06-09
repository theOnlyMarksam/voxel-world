package model;

import entity.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkMap {
    private static final Logger log = LogManager.getLogger(ChunkMap.class);

    private Chunk[] chunks;
    private Player player;
    private int worldSideLength;
    private int numOfChunks;
    private int worldSize;
    private int chunkOffsetX;
    private int chunkOffsetZ;

    public ChunkMap(int worldSize, Player player, Chunk[] initChunks) {
        this.worldSize = worldSize+1; // make the world one unit bigger than requested
        this.worldSideLength = 2*this.worldSize + 1;
        this.player = player;
        this.numOfChunks = worldSideLength * worldSideLength;
        this.chunks = new Chunk[numOfChunks];
        this.chunkOffsetX = (int) Math.floor(player.getPosition().x / 16);
        this.chunkOffsetZ = (int) Math.floor(player.getPosition().z / 16);

        if (initChunks.length != numOfChunks) {
            log.error("Initial chunk array size was " + initChunks.length
                    + ", but needed to be " + numOfChunks);
            throw new IllegalArgumentException("Illegal number of initial chunks.");
        }

        for (Chunk chunk : initChunks) {
            put(chunk);
        }
    }

    public Chunk[] getChunks() {
        Chunk[] renderChunks = new Chunk[(2*(worldSize - 1) + 1)*(2*(worldSize - 1) + 1)];
        int chunkCount = 0;

        for (int x = 1; x < worldSideLength-1; x++) {
            for (int y = 1; y < worldSideLength-1; y++) {
                renderChunks[chunkCount++] = chunks[x * worldSideLength + y];
            }
        }

        return renderChunks;
    }

    public void put(Chunk chunk) {
        int index = numOfChunks / 2 -
                worldSideLength * (chunk.getChunkZ() - chunkOffsetZ) -
                (chunk.getChunkX() - chunkOffsetX);
        chunks[index] = chunk;

        log.debug("Chunk added to the chunkmap with absolute coordinates: x="
                + chunk.getChunkX() + ", z=" + chunk.getChunkZ()
                + " and relative coordinates: x=" + (chunk.getChunkX() - chunkOffsetX)
                + ", z=" + (chunk.getChunkZ() - chunkOffsetZ));
    }

    public Chunk get(int x, int z) {
        if (Math.abs(x - chunkOffsetX) > worldSize || Math.abs(z - chunkOffsetZ) > worldSize) {
            return null;
        }

        int index = numOfChunks / 2 - worldSideLength * (z - chunkOffsetZ) - (x - chunkOffsetX);
        return chunks[index];
    }

    public int[] onPlayerChunkChange(int dX, int dZ) {
        if (dX == 0) {
            chunkOffsetZ += dZ;
            return shiftZ(dZ);
        } else if (dZ == 0) {
            chunkOffsetX += dX;
            return shiftX(dX);
        } else {
            chunkOffsetX += dX;
            chunkOffsetZ += dZ;
            return shiftBoth(dX, dZ);
        }
    }

    public Block getBlockAtWorldCoords(int worldX, int worldY, int worldZ) {
        int chunkX = Math.floorDiv(worldX, 16);
        int chunkZ = Math.floorDiv(worldZ, 16);

        Chunk c = get(chunkX, chunkZ);

        if (c == null) {
            return null;
        }

        return c.getBlock(worldX - 16*chunkX, worldY, worldZ - 16*chunkZ);
    }

    private int[] shiftX(int dX) {
        int[] requiredChunks = new int[worldSideLength * 2];

        for (int i = 0; i < requiredChunks.length; i += 2) {
            requiredChunks[i] = dX * worldSize + chunkOffsetX;
            requiredChunks[i + 1] = worldSize - i / 2 + chunkOffsetZ;
        }

        if (dX == 1) {
            System.arraycopy(chunks, 0, chunks, 1, chunks.length - 1);
        } else if (dX == -1) {
            System.arraycopy(chunks, 1, chunks, 0, chunks.length - 1);
        } else {
            log.error("dX=" + dX);
            throw new IllegalArgumentException("Absolute value of dX cannot be bigger than 1");
        }

        return requiredChunks;
    }

    private int[] shiftZ(int dZ) {
        int[] requiredChunks = new int[worldSideLength * 2];

        for (int i = 0; i < requiredChunks.length; i += 2) {
            requiredChunks[i] = worldSize - i / 2 + chunkOffsetX;
            requiredChunks[i + 1] = dZ * worldSize + chunkOffsetZ;
        }

        if (dZ == 1) {
            System.arraycopy(chunks, 0, chunks, worldSideLength, chunks.length - worldSideLength);
        } else if (dZ == -1) {
            System.arraycopy(chunks, worldSideLength, chunks, 0, chunks.length - worldSideLength);
        } else {
            log.error("dZ=" + dZ);
            throw new IllegalArgumentException("Absolute value of dZ cannot be bigger than 1");
        }

        return requiredChunks;
    }

    // TODO finish
    private int[] shiftBoth(int dX, int dZ) {
        if (worldSize == 0) {
            return new int[] {chunkOffsetX, chunkOffsetZ};
        }

        int[] requiredChunks = new int[worldSideLength * 4 - 2];

        for (int i = 0; i < requiredChunks.length; i += 2) {

        }

        if (dX == 1 && dZ == 1) {

        } else if (dX == -1 && dZ == 1) {

        } else if (dX == 1 && dZ == -1) {

        } else if (dX == -1 && dZ == -1) {
            System.arraycopy(
                    chunks
                    , worldSideLength + 1
                    , chunks
                    , 0
                    , chunks.length - worldSideLength - 1
            );
        } else {
            log.error("dX=" + dX + ", dZ=" + dZ);
            throw new IllegalArgumentException("Absolute value of d* cannot be bigger than 1");
        }

        return requiredChunks;
    }
}
