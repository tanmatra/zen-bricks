package zen.bricks.actions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.Editor;
import zen.bricks.MainWindow;
import zen.bricks.io.Files;
import zen.bricks.io.ZenFileType;
import zen.bricks.io.ZenReader;

public class OpenAction extends Action
{
    private final MainWindow mainWindow;

    public OpenAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        final FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.OPEN | SWT.SINGLE);
        dialog.setFilterPath(Files.DEFAULT_PATH);
        dialog.setFilterExtensions(ZenFileType.getAllFilterExtensions());
        dialog.setFilterNames(ZenFileType.getAllFilterNames());

        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        final int filterIndex = dialog.getFilterIndex();
        if (filterIndex < 0) {
            return;
        }
        final ZenFileType type = ZenFileType.values()[filterIndex];

        final Brick document;
        try (final InputStream input = new FileInputStream(fileName)) {
            final ZenReader reader = type.openReader(input);
            document = reader.read(null);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error loading file");
            return;
        }
        final Editor editor = mainWindow.getEditor();
        editor.setDocument(document);
        editor.setFileType(type);
        editor.setFileName(fileName);
    }
}
