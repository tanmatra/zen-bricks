package zen.bricks;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

        return mainMenu;
    }

    protected Control createContents(Composite parent) {
        final Composite contents = (Composite) super.createContents(parent);
        final FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 5;
        contents.setLayout(layout);
        editor = new Editor(contents);
//        editor.getCanvas();

        getStatusLineManager().setMessage("Ready.");

        return contents;
    }

    public static void main(String[] args) {
        final MainWindow window = new MainWindow();
        window.setBlockOnOpen(true);
        window.open();
        Display.getCurrent().dispose();
    }
}
