package model;

import entity.Player;

public class ObservableWorld {
    private int radius = 0;
    private Chunk[] chunks = new Chunk[(int)Math.pow(2*radius + 1, 2)];
    private Player player;

    public ObservableWorld() {
        chunks[0] = new Chunk();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 64; y++) {
                    chunks[0].setBlock(x, y, z, (byte)1);
                }
            }
        }
    }

    public Chunk[] getChunks() {
        return chunks;
    }
}
