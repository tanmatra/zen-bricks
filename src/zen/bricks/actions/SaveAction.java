package zen.bricks.actions;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.MainWindow;
import zen.bricks.io.ZenFileType;

public class SaveAction extends SaveActionBase
{
    public SaveAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    @Override
    public void run() {
        final Brick document = mainWindow.getEditor().getDocument();
        if (document == null) {
            return;
        }
        String fileName = mainWindow.getEditorFileName();

        boolean unnamedFile = false;
        if ((fileName == null) || fileName.isEmpty()) {
            unnamedFile = true;
            final FileDialog dialog =
                    new FileDialog(mainWindow.getShell(), SWT.SAVE);
            dialog.setFilterPath(DEFAULT_PATH);
            dialog.setFilterExtensions(ZenFileType.getAllFilterExtensions());
            dialog.setFilterNames(ZenFileType.getAllFilterNames());
            dialog.setOverwrite(true);
            fileName = dialog.open();
            if (fileName == null) {
                return;
            }
        }

        try {
            saveAsText(document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        if (unnamedFile) {
            mainWindow.setEditorFileName(fileName);
            mainWindow.setStatus("File saved as: " + fileName);
        } else {
            mainWindow.setStatus("File saved.");
        }
    }
}
