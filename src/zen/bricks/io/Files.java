package zen.bricks.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import zen.bricks.Brick;

public class Files
{
    public static final String DEFAULT_PATH = "samples/";

    public static void save(ZenFileType type, Brick document, String fileName) throws IOException {
        try (final OutputStream output = new FileOutputStream(fileName)) {
            final ZenWriter writer = type.openWriter(output);
            writer.write(document);
        }
    }

    private Files() { }
}
