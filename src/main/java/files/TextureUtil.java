package files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class TextureUtil {
    private static final Logger log = LogManager.getLogger(TextureUtil.class);

    public static ByteBuffer loadPngImage(URL texture, int[] x, int[] y, int desiredChannels) {
        if (desiredChannels != 3 && desiredChannels != 4) {
            throw new RuntimeException("Desired channels not correct");
        }

        BufferedImage image;

        try {
            image = ImageIO.read(texture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        x[0] = image.getWidth();
        y[0] = image.getHeight();
        ByteBuffer buffer = BufferUtils.createByteBuffer(x[0] * y[0] * desiredChannels);

        for (int imageY = 0; imageY < y[0]; imageY++) {
            for (int imageX = 0; imageX < x[0]; imageX++) {
                int pixel = image.getRGB(imageX, imageY);

                buffer.put((byte) ((pixel >> 16) & 0xFF));      // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));       // Green component
                buffer.put((byte) (pixel & 0xFF));              // Blue component

                if (desiredChannels == 4)
                    buffer.put((byte) ((pixel >> 24) & 0xFF));  // Alpha component
            }
        }

        buffer.flip();
        return buffer;
    }
}
