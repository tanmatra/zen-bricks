package zen.bricks;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class MainWindow extends ApplicationWindow
{
    Editor editor;

    public MainWindow() {
        super(null);
        addMenuBar();
        addStatusLine();
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Bricks");
    }

    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    protected MenuManager createMenuManager() {
        final MenuManager mainMenu = super.createMenuManager();

        final MenuManager fileMenu = new MenuManager("&File");
        mainMenu.add(fileMenu);

        final Action importXmlAction =
                new ImportXMLAction(this, "Import XML...");
        fileMenu.add(importXmlAction);
        fileMenu.add(new Separator());
        final Action exitAction = new Action("E&xit") {
            public void run() {
                close();
            }
        };
        fileMenu.add(exitAction);

        final MenuManager viewMenu = new MenuManager("&View");
        mainMenu.add(viewMenu);

        final Action loadStyleAction = new Action("&Load style...") {
            public void run() {
                final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setFilterNames(new String[] { "Properties" });
                dialog.setFilterExtensions(new String[] { "*.properties" });
                dialog.setFilterPath(new File("styles/").toString());
                final String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                editor.loadUI(fileName);
            }
        };
        viewMenu.add(loadStyleAction);

        return mainMenu;
    }

    protected Control createContents(Composite parent) {
        final Composite contents = (Composite) super.createContents(parent);
        final FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 5;
        contents.setLayout(layout);
        try {
            editor = new Editor(this, contents);
        } catch (Exception e) {
            // go on
        }

        getStatusLineManager().setMessage("Ready.");

        return contents;
    }

    void handleException(Exception e, String dialogTitle) {
        e.printStackTrace();
        final IStatus status = new Status(IStatus.ERROR, "zen.bricks",
                e.getClass().getName(), e);
        ErrorDialog.openError(getShell(), dialogTitle, null, status);
    }

    public static void main(String[] args) {
        final MainWindow window = new MainWindow();
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    }

    public void setTitle(String fileName) {
        getShell().setText("Bricks - " + fileName);
    }
}
