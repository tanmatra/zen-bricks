package zen.bricks;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

public class MainWindow extends ApplicationWindow
{
    // ============================================================ Class Fields

    private static final String DEFAULT_THEME_FILE =
            "themes/default.theme.properties";

    // =========================================================== Class Methods

    public static void main(String[] args) throws IOException {
        final ImageDescriptor imageDesc =
                ImageDescriptor.createFromFile(MainWindow.class, "bricks.png");
        final Image shellImage = imageDesc.createImage();
        Window.setDefaultImage(shellImage);
        final MainWindow window = new MainWindow();
        window.setBlockOnOpen(true);
        window.open();
        shellImage.dispose();
        Display.getCurrent().dispose();
    }

    // ================================================================== Fields

    Editor editor;

    private Properties defaultTheme;

    // ============================================================ Constructors

    public MainWindow() throws IOException {
        super(null);
        defaultTheme = UI.loadProperties(DEFAULT_THEME_FILE);
        addMenuBar();
        addStatusLine();
    }

    // ================================================================= Methods

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Bricks");
    }

    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    protected MenuManager createMenuManager() {
        final MenuManager mainMenu = super.createMenuManager();

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        final MenuManager viewMenu = new MenuManager("&View");
        mainMenu.add(viewMenu);

        final Action fontAction = new Action("&Font...") {
            public void run() {
                final FontDialog fontDialog = new FontDialog(getShell());
                final UI ui = editor.ui;
                fontDialog.setFontList(new FontData[] { ui.fontData });
                final FontData data = fontDialog.open();
                if (data == null) {
                    return;
                }
                ui.changeFont(data);
                editor.setUI(ui);
            }
        };
        viewMenu.add(fontAction);

        final Action adjustFontAction = new Action("&Adjust font...") {
            public void run() {
                final AdjustFontDialog dialog = new AdjustFontDialog(getShell());
                final UI ui = editor.ui;
                dialog.fontData = ui.fontData;
                if (dialog.open() != Window.OK) {
                    return;
                }
                ui.changeFont(dialog.fontData);
                editor.setUI(ui);
            }
        };
        viewMenu.add(adjustFontAction);

        viewMenu.add(new Separator());

        final Action loadThemeAction = new Action("&Load theme...") {
            public void run() {
                final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                dialog.setFilterNames(new String[] { "Properties" });
                dialog.setFilterExtensions(new String[] { "*.properties" });
                dialog.setFilterPath(new File("themes/").toString());
                final String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                final Properties props = new Properties(defaultTheme);
                try {
                    UI.loadProperties(props, fileName);
                } catch (IOException e) {
                    handleException(e, "Error loading theme");
                    return;
                }
                setEditorTheme(props);
            }
        };
        viewMenu.add(loadThemeAction);

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        return mainMenu;
    }

    protected Control createContents(Composite parent) {
        final Composite contents = (Composite) super.createContents(parent);
        final FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 5;
        contents.setLayout(layout);

        editor = new Editor(this, contents);
        setEditorTheme(defaultTheme);
        editor.setRoot(Editor.makeSample());

        getStatusLineManager().setMessage("Ready.");

        return contents;
    }

    void handleException(Exception e, String dialogTitle) {
        e.printStackTrace();
        final IStatus status = new Status(IStatus.ERROR, "zen.bricks",
                e.getClass().getName(), e);
        ErrorDialog.openError(getShell(), dialogTitle, null, status);
    }

    public void setTitle(String fileName) {
        getShell().setText("Bricks - " + fileName);
    }

    void setEditorTheme(final Properties props) {
        final UI ui = new UI(getShell().getDisplay(), props);
        editor.setUI(ui);
    }
}
