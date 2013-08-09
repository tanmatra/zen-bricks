package zen.bricks.actions;

import org.eclipse.jface.action.Action;
import zen.bricks.MainWindow;

public class SaveActionBase extends Action
{
    protected final MainWindow mainWindow;

    protected SaveActionBase(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }
}
