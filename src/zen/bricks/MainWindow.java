package zen.bricks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

import zen.bricks.styleeditor.EditStylesDialog;


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

    private static Properties loadProperties(String filePath)
            throws IOException
    {
        final InputStream inputStream = new FileInputStream(filePath);
        try {
            final Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    // ================================================================== Fields

    Editor editor;

    private final Properties defaultThemeProps;

    String themeFileName;

    private Action reloadThemeAction;

    // ============================================================ Constructors

    public MainWindow() throws IOException {
        super(null);
        defaultThemeProps = loadProperties(DEFAULT_THEME_FILE);
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
                new ImportXMLAction(this, "Import XML...\tF3");
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

        final Action editStylesAction = new Action("&Edit styles...") {
            public void run() {
                new EditStylesDialog(getShell(), editor.ui, editor).open();
            }
        };
        viewMenu.add(editStylesAction);

        final Action fontAction = new Action("&Font...") {
            public void run() {
                final FontDialog fontDialog = new FontDialog(getShell());
                final UI ui = editor.ui;
                fontDialog.setFontList(ui.getBasicStyle().getFontList());
                if (fontDialog.open() == null) {
                    return;
                }
                ui.changeBasicFont(fontDialog.getFontList());
                editor.setUI(ui);
            }
        };
        viewMenu.add(fontAction);

        final Action adjustFontAction = new Action("&Adjust font...") {
            public void run() {
                final AdjustFontDialog dialog = new AdjustFontDialog(getShell());
                final UI ui = editor.ui;
                dialog.fontList = ui.getBasicStyle().getFontList();
                if (dialog.open() != Window.OK) {
                    return;
                }
                ui.changeBasicFont(dialog.fontList);
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
                themeFileName = fileName;
                loadTheme();
            }
        };
        viewMenu.add(loadThemeAction);

        reloadThemeAction = new Action("&Reload theme\tF5") {
            public void run() {
                if (themeFileName == null) {
                    return;
                }
                loadTheme();
            }
        };
        reloadThemeAction.setEnabled(false);
        viewMenu.add(reloadThemeAction);

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        return mainMenu;
    }

    protected Control createContents(Composite parent) {
        final Composite contents = (Composite) super.createContents(parent);
        final FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 5;
        contents.setLayout(layout);

        editor = new Editor(this, contents);
        setEditorTheme(defaultThemeProps);
        editor.setDocument(Editor.makeSample());

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

    void setEditorTheme(Properties props) {
        final UI ui;
        try {
            ui = new UI(getShell().getDisplay(), props);
        } catch (Exception e) {
            handleException(e, "Error setting theme");
            return;
        }
        editor.setUI(ui);
    }

    void loadTheme() {
        final Properties props;
        try {
            props = loadProperties(themeFileName);
        } catch (IOException e) {
            handleException(e, "Error loading theme");
            return;
        }
        setEditorTheme(props);
        getStatusLineManager().setMessage(
                "Loaded theme \"" + themeFileName + "\"");
        reloadThemeAction.setEnabled(true);
    }
}
