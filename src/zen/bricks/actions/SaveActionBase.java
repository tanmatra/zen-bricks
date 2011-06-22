package zen.bricks.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jface.action.Action;

import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.ZenTextWriter;

public class SaveActionBase extends Action
{
    protected static final String[] FILTER_NAMES =
            new String[] { "Zen files" };

    protected static final String[] FILTER_EXTENSIONS =
            new String[] { "*.zen" };

    protected static final String DEFAULT_PATH = "samples/";

    protected final MainWindow mainWindow;

    protected SaveActionBase(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    protected void save(Brick document, String fileName) throws IOException {
        final OutputStream output = new FileOutputStream(fileName);
        try {
            final ZenTextWriter writer = new ZenTextWriter(output);
            try {
                writer.write(document);
            } finally {
                writer.close();
            }
        } finally {
            output.close();
        }
    }
}
