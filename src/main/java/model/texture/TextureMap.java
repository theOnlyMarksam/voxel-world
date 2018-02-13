package model.texture;

import files.TextureUtil;
import model.Block;
import model.mesh.Mesh;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shader.Shader;

import java.net.URL;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

public class TextureMap {
    private static final Logger log = LogManager.getLogger(Mesh.class);

    public void loadTexture(Block block) {
    }

    public void sendDataToShader(Shader shader) {
        int[] x = new int[1];
        int[] y = new int[1];

        URL imageName = getClass().getClassLoader().getResource("textures/blocks/dirt.png");
        ByteBuffer image = TextureUtil.loadPngImage(imageName, x, y, 3);

        int textureHandle = glGenTextures();
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

        log.debug("Total of " + image.capacity() + " bytes sent to shader texture array.");
    }
}
