package files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class TextFiles {
    private static final Logger log = LogManager.getLogger(TextFiles.class);

    public static StringBuilder readTextResource(String fileName, Class shaderClass) {
        StringBuilder sb = new StringBuilder();
        URL filePath = shaderClass.getResource(fileName);
        File file = new File(filePath.getFile());

        log.info("Reading text from " + file.getName());
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024];
            int total = 0;

            while (true) {
                int numRead = inputStream.read(buffer);

                if (numRead == -1)
                    break;

                total += numRead;
                sb.append(new String(buffer, 0, numRead, "UTF-8"));
            }

            log.info(total + " bytes read.");
        } catch (IOException ioEx) {
            log.info("Problem with text file reading: " + ioEx.toString());
            throw new RuntimeException("Problem with text file reading.");
        }

        return sb;
    }
}
