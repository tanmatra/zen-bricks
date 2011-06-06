package zen.bricks.actions;

import java.io.IOException;

import zen.bricks.Brick;
import zen.bricks.MainWindow;

public class SaveAction extends SaveActionBase
{
    public SaveAction(MainWindow mainWindow, String text) {
        super(mainWindow, text);
    }

    public void run() {
        final Brick document = mainWindow.getEditor().getDocument();
        if (document == null) {
            return;
        }
        final String fileName = mainWindow.getEditorFileName();
        try {
            save(document, fileName);
        } catch (IOException ex) {
            mainWindow.showException(ex, "Error saving file");
        }
    }
}
