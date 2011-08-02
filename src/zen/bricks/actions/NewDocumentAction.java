package zen.bricks.actions;

import org.eclipse.jface.action.Action;

import zen.bricks.MainWindow;
import zen.bricks.TupleBrick;

public class NewDocumentAction extends Action
{
    private final MainWindow mainWindow;

    public NewDocumentAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    public void run() {
        final TupleBrick document = new TupleBrick(null, "");
        mainWindow.getEditor().setDocument(document);
    }
}
