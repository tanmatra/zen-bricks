package zen.bricks.actions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.io.ZenBinaryReader;
import zen.bricks.io.ZenReader;
import zen.bricks.io.ZenTextReader;

public class OpenAction extends Action
{
    private static final String[] FILTER_NAMES =
            new String[] { "Zen Text files", "Zen Binary files" };

    private static final String[] FILTER_EXTENSIONS =
            new String[] { "*.zen", "*.zn" };

    private final MainWindow mainWindow;

    public OpenAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.OPEN | SWT.SINGLE);
        dialog.setFilterPath("samples/");
        dialog.setFilterExtensions(FILTER_EXTENSIONS);
        dialog.setFilterNames(FILTER_NAMES);

        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        final Brick document;
        try (final InputStream input = new FileInputStream(fileName);
             final ZenReader reader = openReader(dialog.getFilterIndex(), input))
        {
            document = reader.read(null);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error loading file");
            return;
        }
        mainWindow.getEditor().setDocument(document);
        mainWindow.setEditorFileName(fileName);
    }

    private static ZenReader openReader(int index, InputStream input) {
        switch (index) {
            case 0:
                return new ZenTextReader(input);
            case 1:
                return new ZenBinaryReader(input);
            default:
                throw new IllegalArgumentException();
        }
    }
}
