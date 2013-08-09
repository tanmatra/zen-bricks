package zen.bricks.actions;

import java.io.File;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.io.ZenFileType;

public class SaveAsAction extends SaveActionBase
{
    public SaveAsAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    @Override
    public void run() {
        final Brick document = mainWindow.getEditor().getDocument();
        if (document == null) {
            return;
        }

        String fileName = mainWindow.getEditorFileName();
        String path;
        if (fileName == null || fileName.isEmpty()) {
            fileName = "";
            path = DEFAULT_PATH;
        } else {
            final File file = new File(fileName);
            path = file.getParent();
            if (path == null) {
                path = DEFAULT_PATH;
            }
        }
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.SAVE);
        dialog.setFilterPath(path);
        dialog.setFileName(fileName);
        dialog.setFilterExtensions(ZenFileType.getAllFilterExtensions());
        dialog.setFilterNames(ZenFileType.getAllFilterNames());
        dialog.setOverwrite(true);

        fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        final ZenFileType type = ZenFileType.values()[dialog.getFilterIndex()];
        try {
            save(type, document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        mainWindow.setEditorFileName(fileName);
        mainWindow.setStatus("File saved as: " + fileName);
    }
}
