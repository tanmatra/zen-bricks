package zen.bricks.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.jface.action.Action;
import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.io.ZenFileType;
import zen.bricks.io.ZenTextWriter;
import zen.bricks.io.ZenWriter;

public class SaveActionBase extends Action
{
    protected static final String DEFAULT_PATH = "samples/";

    protected final MainWindow mainWindow;

    protected SaveActionBase(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    protected void save(ZenFileType type, Brick document, String fileName) throws IOException {
        try (final OutputStream output = new FileOutputStream(fileName)) {
            final ZenWriter writer = type.openWriter(output);
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
