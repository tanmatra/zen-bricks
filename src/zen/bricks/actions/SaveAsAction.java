package zen.bricks.actions;

import java.io.File;
import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import zen.bricks.Brick;
import zen.bricks.Editor;
import zen.bricks.MainWindow;
import zen.bricks.Strings;
import zen.bricks.io.Files;
import zen.bricks.io.ZenFileType;

public class SaveAsAction extends SaveActionBase
{
    public SaveAsAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    @Override
    public void run() {
        final Editor editor = mainWindow.getEditor();
        final Brick document = editor.getDocument();
        if (document == null) {
            return;
        }

        ZenFileType type = editor.getFileType();

        String fileName = editor.getFileName();
        String path;
        if (Strings.isEmpty(fileName)) {
            fileName = "";
            path = Files.DEFAULT_PATH;
        } else {
            path = new File(fileName).getParent();
            if (path == null) {
                path = Files.DEFAULT_PATH;
            }
        }

        final FileDialog dialog = new FileDialog(mainWindow.getShell(), SWT.SAVE);
        dialog.setFileName(fileName);
        dialog.setFilterPath(path);
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

        try {
            Files.save(type, document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
        editor.setFileName(fileName);
        editor.setFileType(type);
        mainWindow.setStatus("File saved as: " + fileName);
    }
}
