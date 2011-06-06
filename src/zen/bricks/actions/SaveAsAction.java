package zen.bricks.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import zen.bricks.Brick;
import zen.bricks.MainWindow;

public class SaveAsAction extends SaveActionBase
{
    public SaveAsAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    public void run() {
        final Brick document = mainWindow.getEditor().getDocument();
        if (document == null) {
            return;
        }

        String fileName = mainWindow.getEditorFileName();
        final File file = new File(fileName);
        String path = file.getParent();
        if (path == null) {
            path = DEFAULT_PATH;
        }
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.SAVE);
        dialog.setFilterPath(path);
        dialog.setFileName(file.getName());
        dialog.setFilterExtensions(FILTER_EXTENSIONS);
        dialog.setFilterNames(FILTER_NAMES);
        dialog.setOverwrite(true);

        fileName = dialog.open();
        if (fileName == null) {
            return;
        }

        try {
            save(document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        mainWindow.setEditorFileName(fileName);
        mainWindow.setStatus("File saved as: " + fileName);
    }
}
