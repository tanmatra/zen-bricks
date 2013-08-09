package zen.bricks.actions;

import org.eclipse.jface.action.Action;
import zen.bricks.Editor;
import zen.bricks.MainWindow;
import zen.bricks.TupleBrick;

public class NewDocumentAction extends Action
{
    private final MainWindow mainWindow;

    public NewDocumentAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    @Override
    public void run() {
        final TupleBrick document = new TupleBrick(null, "");
        final Editor editor = mainWindow.getEditor();
        editor.setDocument(document);
        editor.setFileType(null);
        editor.setFileName(null);
    }
}
