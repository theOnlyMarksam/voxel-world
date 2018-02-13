package model.mesh;

import model.Block;
import model.Chunk;
import model.ChunkMap;
import model.texture.TextureMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shader.Shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {

    private static final float[] top_face = {
            // TOP
            // TR1
            0f, 1f, 0f, 1f, 1f, 0f,
            1f, 1f, 1f, 0f, 0f, 0f,
            1f, 1f, 0f, 0f, 1f, 0f,
            // TR2
            0f, 1f, 0f, 1f, 1f, 0f,
            0f, 1f, 1f, 1f, 0f, 0f,
            1f, 1f, 1f, 0f, 0f, 0f,
    };

    private static final float[] bottom_face = {
            // BOTTOM
            // TR1
            0f, 0f, 1f, 1f, 1f, 0f,
            1f, 0f, 0f, 0f, 0f, 0f,
            1f, 0f, 1f, 0f, 1f, 0f,
            // TR2
            0f, 0f, 1f, 1f, 1f, 0f,
            0f, 0f, 0f, 1f, 0f, 0f,
            1f, 0f, 0f, 0f, 0f, 0f,
    };

    private static final float[] front_face = {
            // FRONT FACE
            // TR1
            0f, 0f, 0f, 1f, 1f, 0f,
            1f, 1f, 0f, 0f, 0f, 0f,
            1f, 0f, 0f, 0f, 1f, 0f,
            // TR2
            0f, 0f, 0f, 1f, 1f, 0f,
            0f, 1f, 0f, 1f, 0f, 0f,
            1f, 1f, 0f, 0f, 0f, 0f,
    };

    private static final float[] left_face = {
            // LEFT SIDE
            // TR1
            1f, 0f, 0f, 1f, 1f, 0f,
            1f, 1f, 1f, 0f, 0f, 0f,
            1f, 0f, 1f, 0f, 1f, 0f,
            // TR2
            1f, 0f, 0f, 1f, 1f, 0f,
            1f, 1f, 0f, 1f, 0f, 0f,
            1f, 1f, 1f, 0f, 0f, 0f,
    };

    private static final float[] right_face = {
            // RIGHT SIDE
            // TR1
            0f, 0f, 1f, 1f, 1f, 0f,
            0f, 1f, 0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f, 1f, 0f,
            // TR2
            0f, 0f, 1f, 1f, 1f, 0f,
            0f, 1f, 1f, 1f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f, 0f,
    };

    private static final float[] back_face = {
            // BACK
            // TR1
            1f, 0f, 1f, 1f, 1f, 0f,
            0f, 1f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 1f, 0f,
            // TR2
            1f, 0f, 1f, 1f, 1f, 0f,
            1f, 1f, 1f, 1f, 0f, 0f,
            0f, 1f, 1f, 0f, 0f, 0f,
    };

    private static final Logger log = LogManager.getLogger(Mesh.class);

    private VertexList vertexList = new VertexList();
    private ChunkMap world;
    private TextureMap textureMap;
    private Chunk chunk;
    private int vao;
    private Shader shader;

    public Mesh(ChunkMap world, TextureMap textureMap, Chunk chunk, Shader shader) {
        this.world = world;
        this.textureMap = textureMap;
        this.chunk = chunk;
        this.shader = shader;

        // Generate the names for vao and texture
        vao = glGenVertexArrays();
    }

    public void generateMesh() {
        glBindVertexArray(vao);

        generateVertexData();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexList.getDenseVertexArray(), GL_STATIC_DRAW);

        int vertexPos = glGetAttribLocation(shader.getProgram(), "position");
        int uvPos = glGetAttribLocation(shader.getProgram(), "uv");

        glVertexAttribPointer(vertexPos, 3, GL_FLOAT, false, 6*4, 0);
        glEnableVertexAttribArray(vertexPos);

        glVertexAttribPointer(uvPos, 3, GL_FLOAT, false, 6*4, 3*4);
        glEnableVertexAttribArray(uvPos);

        glBindVertexArray(0);
        log.debug("Mesh with " + getSize() + " vertices generated.");
    }

    public int getVao() {
        return vao;
    }

    public int getSize() {
        return vertexList.getVertexCount();
    }


    private void generateVertexData() {
        // clear the vertex list
        vertexList.clear();

        // generate mesh only for one chunk
        // Add top faces to the mesh
        for (int y = 255; y >= 0; y--) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z) == Block.AIR) {
                        continue;
                    }

                    int worldX = world.get(chunk.getChunkX(), chunk.getChunkZ()).getChunkX() * 16 + x;
                    int worldZ = world.get(chunk.getChunkX(), chunk.getChunkZ()).getChunkZ() * 16 + z;

                    // check top faces
                    if (y == 255 || world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y + 1, z) == Block.AIR &&
                            world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z) != Block.AIR) {
                        pushNewUnitQuad(worldX, y, worldZ, top_face);
                    }

                    // check bottom faces
                    if (y == 0 || world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y - 1, z) == Block.AIR &&
                            world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z) != Block.AIR) {
                        pushNewUnitQuad(worldX, y, worldZ, bottom_face);
                    }

                    // check front and back faces
                    if (z == 0) {
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z + 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }

                        if (world.get(chunk.getChunkX(), chunk.getChunkZ() - 1) != null) {
                            if (world.get(chunk.getChunkX(), chunk.getChunkZ() - 1).getBlock(x, y, 15) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, front_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }
                    } else if (z == 15){
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z - 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }

                        if (world.get(chunk.getChunkX(), chunk.getChunkZ() + 1) != null) {
                            if (world.get(chunk.getChunkX(), chunk.getChunkZ() + 1).getBlock(x, y, 0) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, back_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }
                    } else {
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z - 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }

                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x, y, z + 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }
                    }

                    // check left and right faces
                    if (x == 0) {
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x + 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, left_face);
                        }

                        if (world.get(chunk.getChunkX() - 1, chunk.getChunkZ()) != null) {
                            if (world.get(chunk.getChunkX() - 1, chunk.getChunkZ()).getBlock(15, y, z) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, right_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }
                    } else if (x == 15) {
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x - 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }

                        if (world.get(chunk.getChunkX() + 1, chunk.getChunkZ()) != null) {
                            if (world.get(chunk.getChunkX() + 1, chunk.getChunkZ()).getBlock(0, y, z) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, left_face);
                            }
                        }else {
                            pushNewUnitQuad(worldX, y, worldZ, left_face);
                        }
                    } else {
                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x - 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }

                        if (world.get(chunk.getChunkX(), chunk.getChunkZ()).getBlock(x + 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, left_face);
                        }
                    }
                }
            }
        }
    }

    private void pushNewUnitQuad(int x, int y, int z, float[] face) {

        for (int i = 0; i < face.length; i += 6) {
            vertexList.add(
                    face[i] + x
                    , face[i+1] + y
                    , face[i+2] + z
                    , face[i+3]
                    , face[i+4]
                    , face[i+5]  // TODO read values from textureMap
            );
        }
    }
}
