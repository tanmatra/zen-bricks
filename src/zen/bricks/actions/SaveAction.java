package zen.bricks.actions;

import zen.bricks.io.Files;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.Editor;
import zen.bricks.MainWindow;
import zen.bricks.Strings;
import zen.bricks.io.ZenFileType;

public class SaveAction extends SaveActionBase
{
    public SaveAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    @Override
    public void run() {
        final Editor editor = mainWindow.getEditor();
        final Brick document = editor.getDocument();
        if (document == null) {
            return;
        }

        String fileName = editor.getFileName();
        ZenFileType type = editor.getFileType();

        boolean fileIsNew = false;
        if (Strings.isEmpty(fileName) || (type == null)) {
            fileIsNew = true;
            final FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.SAVE);
            dialog.setFilterPath(Files.DEFAULT_PATH);
            dialog.setFilterExtensions(ZenFileType.getAllFilterExtensions());
            dialog.setFilterNames(ZenFileType.getAllFilterNames());
            dialog.setOverwrite(true);
            if (type != null) {
                dialog.setFilterIndex(ZenFileType.indexOf(type));
            }

            fileName = dialog.open();
            if (fileName == null) {
                return;
            }

            final int filterIndex = dialog.getFilterIndex();
            if (filterIndex < 0) {
                return;
            }
            type = ZenFileType.values()[filterIndex];
        }

        try {
            Files.save(type, document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        if (fileIsNew) {
            editor.setFileName(fileName);
            editor.setFileType(type);
            mainWindow.setStatus("File saved as: " + fileName);
        } else {
            mainWindow.setStatus("File saved.");
        }
    }
}
