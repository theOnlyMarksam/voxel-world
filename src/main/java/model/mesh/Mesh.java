package model.mesh;

import files.TextureUtil;
import model.Block;
import model.ChunkMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shader.Shader;

import java.net.URL;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

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

    private int vao;
    private int textureHandle;
    private ChunkMap world;
    private Shader shader;
    private VertexList vertexList = new VertexList();

    public Mesh(ChunkMap world, Shader shader) {
        this.world = world;
        this.shader = shader;

        // Generate the names for vao and texture
        vao = glGenVertexArrays();
        textureHandle = glGenTextures();
    }

    public void generateMesh() {
        glBindVertexArray(vao);

        int vertexPos = glGetAttribLocation(shader.getProgram(), "position");
        int uvPos = glGetAttribLocation(shader.getProgram(), "uv");

        generateVertexData();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexList.getDenseVertexArray(), GL_STATIC_DRAW);

        glVertexAttribPointer(vertexPos, 3, GL_FLOAT, false, 6*4, 0);
        glEnableVertexAttribArray(vertexPos);

        glVertexAttribPointer(uvPos, 3, GL_FLOAT, false, 6*4, 3*4);
        glEnableVertexAttribArray(uvPos);

        // TODO implement image loading separately from mesh generation
        int[] x = new int[1];
        int[] y = new int[1];

        URL imageName = getClass().getClassLoader().getResource("textures/blocks/grass_top.png");
        ByteBuffer image = TextureUtil.loadPngImage(imageName, x, y, 3);

        textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureHandle);

        int mipLevelCount = 1;
        int layerCount = 1;

        glTexStorage3D(GL_TEXTURE_2D_ARRAY, mipLevelCount, GL_RGB8, x[0], y[0], layerCount);
        glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY
                , 0
                , 0
                , 0
                , 0
                , x[0]
                , y[0]
                , layerCount
                , GL_RGB
                , GL_UNSIGNED_BYTE
                , image
        );

        // Always set reasonable texture parameters
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glBindVertexArray(0);
        log.debug("Mesh with " + getSize() + " vertices generated.");
    }

    public int getVao() {
        return vao;
    }

    public int getTextureHandle() {
        return textureHandle;
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
                    if (world.get(0, 0).getBlock(x, y, z) == Block.AIR) {
                        continue;
                    }

                    int worldX = world.get(0, 0).getChunkX() * 16 + x;
                    int worldZ = world.get(0, 0).getChunkZ() * 16 + z;

                    // check top faces
                    if (y == 255 || world.get(0, 0).getBlock(x, y + 1, z) == Block.AIR &&
                            world.get(0, 0).getBlock(x, y, z) != Block.AIR) {
                        pushNewUnitQuad(worldX, y, worldZ, top_face);
                    }

                    // check bottom faces
                    if (y == 0 || world.get(0, 0).getBlock(x, y - 1, z) == Block.AIR &&
                            world.get(0, 0).getBlock(x, y, z) != Block.AIR) {
                        pushNewUnitQuad(worldX, y, worldZ, bottom_face);
                    }

                    // check front and back faces
                    if (z == 0) {
                        if (world.get(0, 0).getBlock(x, y, z + 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }

                        if (world.get(0, -1) != null) {
                            if (world.get(0, -1).getBlock(x, y, 15) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, front_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }
                    } else if (z == 15){
                        if (world.get(0, 0).getBlock(x, y, z - 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }

                        if (world.get(0, 1) != null) {
                            if (world.get(0, 1).getBlock(x, y, 0) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, back_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }
                    } else {
                        if (world.get(0, 0).getBlock(x, y, z - 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, front_face);
                        }

                        if (world.get(0, 0).getBlock(x, y, z + 1) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, back_face);
                        }
                    }

                    // check left and right faces
                    if (x == 0) {
                        if (world.get(0, 0).getBlock(x + 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, left_face);
                        }

                        if (world.get(-1, 0) != null) {
                            if (world.get(-1, 0).getBlock(15, y, z) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, right_face);
                            }
                        } else {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }
                    } else if (x == 15) {
                        if (world.get(0, 0).getBlock(x - 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }

                        if (world.get(1, 0) != null) {
                            if (world.get(1, 0).getBlock(0, y, z) == Block.AIR) {
                                pushNewUnitQuad(worldX, y, worldZ, left_face);
                            }
                        }else {
                            pushNewUnitQuad(worldX, y, worldZ, left_face);
                        }
                    } else {
                        if (world.get(0, 0).getBlock(x - 1, y, z) == Block.AIR) {
                            pushNewUnitQuad(worldX, y, worldZ, right_face);
                        }

                        if (world.get(0, 0).getBlock(x + 1, y, z) == Block.AIR) {
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
                    , face[i+5]
            );
        }
    }
}
