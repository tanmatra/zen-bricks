package zen.bricks;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

class ImportXMLAction extends Action
{
    private final MainWindow mainWindow;

    ImportXMLAction(MainWindow mainWindow, String text) {
        super(text);
        this.mainWindow = mainWindow;
    }

    public void run() {
        final FileDialog dialog =
                new FileDialog(mainWindow.getShell(), SWT.OPEN);
        dialog.setFilterNames(new String[] { "XML files" });
        dialog.setFilterExtensions(new String[] { "*.xml" });
        dialog.setFilterPath(new File("./").toString());
        final String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        // TODO
    }
}
