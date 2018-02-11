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

    public ChunkMap(int worldSize, Player player) {
        this.worldSize = worldSize;
        this.worldSideLength = 2*worldSize + 1;
        this.player = player;
        this.numOfChunks = worldSideLength * worldSideLength;
        this.chunks = new Chunk[numOfChunks];
        this.chunkOffsetX = (int) Math.floor(player.getPosition().x / 16);
        this.chunkOffsetZ = (int) Math.floor(player.getPosition().z / 16);
    }

    public Chunk[] getChunks() {
        return chunks;
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
        if (Math.abs(x) > worldSize || Math.abs(z) > worldSize) {
            return null;
        }

        int index = numOfChunks / 2 - worldSideLength * z - x;
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
            System.arraycopy(chunks, 0, chunks, worldSize, chunks.length - worldSize);
        } else if (dZ == -1) {
            System.arraycopy(chunks, worldSize, chunks, 0, chunks.length - worldSize);
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
