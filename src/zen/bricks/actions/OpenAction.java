package zen.bricks.actions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.ZenTextReader;

public class OpenAction extends Action
{
    private static final String[] FILTER_NAMES = new String[] { "Zen files" };

    private static final String[] FILTER_EXTENSIONS = new String[] { "*.zen" };

    private final MainWindow mainWindow;

    public OpenAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

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
        try {
            document = load(fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error loading file");
            return;
        }
        mainWindow.getEditor().setDocument(document);
        mainWindow.setEditorFileName(fileName);
    }

    private Brick load(String fileName) throws IOException {
        final InputStream input = new FileInputStream(fileName);
        try {
            final ZenTextReader reader = new ZenTextReader(input);
            try {
                return reader.readBrick(null);
            } finally {
                reader.close();
            }
        } finally {
            input.close();
        }
    }
}
