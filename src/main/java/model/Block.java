package model;

public enum Block {
    AIR ((byte) 0, (byte) 0),
    DIRT ((byte) 1, (byte) 1),
    GRASS ((byte) 2, (byte) 3);

    private final byte value;
    private final byte numOfTextures;

    Block(byte value, byte numOfTextures) {
        this.value = value;
        this.numOfTextures = numOfTextures;
    }

    public byte getValue() {
        return value;
    }

    public byte getNumOfTextures() {
        return numOfTextures;
    }
}
