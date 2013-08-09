package zen.bricks.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.jface.action.Action;
import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.io.ZenBinaryWriter;
import zen.bricks.io.ZenTextWriter;
import zen.bricks.io.ZenWriter;

public class SaveActionBase extends Action
{
    protected static final String[] FILTER_NAMES =
            new String[] { "Zen Text files", "Zen Binary files" };

    protected static final String[] FILTER_EXTENSIONS =
            new String[] { "*.zen", "*.zn" };

    protected static final String DEFAULT_PATH = "samples/";

    protected final MainWindow mainWindow;

    protected SaveActionBase(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    private static ZenWriter createWriter(int index, OutputStream output) throws IOException {
        switch (index) {
            case 0:
                return new ZenTextWriter(output);
            case 1:
                return new ZenBinaryWriter(output);
            default:
                throw new IllegalArgumentException();
        }
    }

    protected void save(int index, Brick document, String fileName) throws IOException {
        try (final OutputStream output = new FileOutputStream(fileName)) {
            final ZenWriter writer = createWriter(index, output);
            writer.write(document);
        }
    }

    @Deprecated
    protected void saveAsText(Brick document, String fileName) throws IOException {
        try (final OutputStream output = new FileOutputStream(fileName)) {
            final ZenTextWriter writer = new ZenTextWriter(output);
            writer.write(document);
        }
    }
}
