package zen.bricks;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.prefs.Preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import zen.bricks.styleeditor.EditStylesDialog;
import zen.bricks.utils.CustomImageRegistry;
import zen.bricks.utils.DOMPreferences;
import zen.bricks.utils.PropertiesPreferences;
import zen.bricks.utils.StoredPreferences;


public class MainWindow extends ApplicationWindow
{
    // ============================================================ Class Fields

    private static final String LAST_THEME_KEY = "theme";

    private static final String DEFAULT_THEME_FILE =
            "themes/default.theme.properties";

    static final String THEMES_DIR = "themes/";

    static final String[] THEME_FILTER_NAMES = { "Properties" };

    static final String[] THEME_FILTER_EXTENSIONS = { "*.properties" };

    // =========================================================== Class Methods

    public static void main(String[] args) {
        final Display display = new Display();
        try {
            final CustomImageRegistry imageRegistry =
                    new CustomImageRegistry(display, MainWindow.class,
                            "/zen/bricks/");
            final Image shellImage = imageRegistry.load("bricks.png");
            Window.setDefaultImage(shellImage);
            final MainWindow window = new MainWindow();
            window.setBlockOnOpen(true);
            window.open();
        } finally {
            display.dispose();
        }
    }

    // ================================================================== Fields

    UI ui;

    Editor editor;

    String themeFileName;

    private final Preferences preferences;

    private StoredPreferences themePreferences;

    // ============================================================ Constructors

    public MainWindow() {
        super(null);
        preferences = Preferences.userNodeForPackage(getClass());
        addMenuBar();
        addStatusLine();
    }

    // ================================================================= Methods

    protected void configureShell(Shell shell) {
        shell.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event e) {
                disposed();
            }
        });
        super.configureShell(shell);
        shell.setText("Bricks");
    }

    void disposed() {
        if (ui != null) {
            ui.dispose();
            ui = null;
        }
    }

    protected Point getInitialSize() {
        return new Point(400, 300);
    }

    protected MenuManager createMenuManager() {
        final MenuManager mainMenu = super.createMenuManager();
        createFileMenu(mainMenu);
        createNavigateMenu(mainMenu);
        createViewMenu(mainMenu);
        return mainMenu;
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void createFileMenu(final MenuManager mainMenu) {
        final MenuManager fileMenu = new MenuManager("&File");
        mainMenu.add(fileMenu);

        fileMenu.add(new OpenAction(this, "&Open...\tCtrl+O"));

        fileMenu.add(new SaveAction(this, "&Save...\tCtrl+S"));

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
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void createNavigateMenu(MenuManager mainMenu) {
        final MenuManager navigateMenu = new MenuManager("&Navigate");
        mainMenu.add(navigateMenu);

        navigateMenu.add(new Action("Go to &up level") {
            { setAccelerator(SWT.HOME); }
            public void run() {
                editor.navigateLevelUp();
            }
        });

        navigateMenu.add(new Action("Go to preceding") {
            { setAccelerator(SWT.ARROW_LEFT); }
            public void run() {
                editor.navigatePreceding();
            }
        });

        navigateMenu.add(new Action("Go to following") {
            { setAccelerator(SWT.ARROW_RIGHT); }
            public void run() {
                editor.navigateFollowing();
            }
        });

        navigateMenu.add(new Action("Go to previous") {
            { setAccelerator(SWT.ARROW_UP); }
            public void run() {
                editor.navigatePrevious(true);
            }
        });
        navigateMenu.add(new Action("Go to previous only") {
            { setAccelerator(SWT.ARROW_UP | SWT.ALT); }
            public void run() {
                editor.navigatePrevious(false);
            }
        });

        navigateMenu.add(new Action("Go to next") {
            { setAccelerator(SWT.ARROW_DOWN); }
            public void run() {
                editor.navigateNextOrUp();
            }
        });
        navigateMenu.add(new Action("Go to next only") {
            { setAccelerator(SWT.ARROW_DOWN | SWT.ALT); }
            public void run() {
                editor.navigateNext(false);
            }
        });
        navigateMenu.add(new Separator());
        navigateMenu.add(new Action("&Scroll to selected") {
            { setAccelerator(' '); }
            public void run() {
                editor.scrollToSelected();
            }
        });
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void createViewMenu(MenuManager mainMenu) {
        final MenuManager viewMenu = new MenuManager("&View");
        mainMenu.add(viewMenu);

        // ---------------------------------------------------------------------
        final Action editStylesAction = new Action("&Edit styles...")
        {
            private WeakReference<Style> lastStyleRef;

            public void run() {
                Style lastStyle;
                final EditStylesDialog dialog =
                        new EditStylesDialog(getShell(), ui);

                if (lastStyleRef != null) {
                    lastStyle = lastStyleRef.get();
                    if (lastStyle != null) {
                        dialog.setSelectedStyle(lastStyle);
                    }
                }

                dialog.open();

                lastStyle = dialog.getSelectedStyle();
                if (lastStyle != null) {
                    lastStyleRef = new WeakReference<Style>(lastStyle);
                } else {
                    lastStyleRef = null;
                }
            }
        };
        viewMenu.add(editStylesAction);

        // ---------------------------------------------------------------------
        final Action fontAction = new Action("&Font...") {
            public void run() {
                final FontDialog fontDialog = new FontDialog(getShell());
                final UI ui = editor.getUI();
                fontDialog.setFontList(ui.getBasicStyle().getFontList());
                if (fontDialog.open() == null) {
                    return;
                }
                ui.changeBasicFont(fontDialog.getFontList());
                editor.refresh();
            }
        };
        viewMenu.add(fontAction);

        // ---------------------------------------------------------------------
        final Action adjustFontAction = new Action("&Adjust font...") {
            public void run() {
                final AdjustFontDialog dialog = new AdjustFontDialog(getShell());
                final UI ui = editor.getUI();
                dialog.setFontList(ui.getBasicStyle().getFontList());
                if (dialog.open() != Window.OK) {
                    return;
                }
                ui.changeBasicFont(dialog.getFontList());
                editor.refresh();
            }
        };
        viewMenu.add(adjustFontAction);

        // ---------------------------------------------------------------------
        viewMenu.add(new Separator());

        // ---------------------------------------------------------------------
        final Action loadThemeAction = new Action("&Load theme...") {
            public void run() {
                final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                initThemesFileDialog(dialog);
                final String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                loadTheme(fileName);
            }
        };
        viewMenu.add(loadThemeAction);

        // ---------------------------------------------------------------------
        viewMenu.add(new Action("&Save theme") {
            public void run() {
                saveTheme(themeFileName);
            }
        });

        // ---------------------------------------------------------------------
        final Action saveThemeAction = new Action("Sa&ve theme as...") {
            public void run() {
                final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                initThemesFileDialog(dialog);
                dialog.setFileName(new File(themeFileName).getName());
                final String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                saveTheme(fileName);
            }
        };
        viewMenu.add(saveThemeAction);

        // ---------------------------------------------------------------------
        final Action saveXMLThemeAction = new Action("Save theme as &XML...") {
            public void run() {
                final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
                dialog.setFilterNames(new String[] { "XML files" });
                dialog.setFilterExtensions(new String[] { "*.xml" });
                dialog.setFilterPath(new File(THEMES_DIR).toString());
//                dialog.setFileName(new File(themeFileName).getName());
                final String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                final DOMPreferences domPrefs = new DOMPreferences();
                ui.save(domPrefs);
                try {
                    domPrefs.save(fileName);
                } catch (IOException e) {
                    showException(e, "Error saving theme");
                }
            }
        };
        viewMenu.add(saveXMLThemeAction);

        // ---------------------------------------------------------------------
        final Action reloadThemeAction = new Action("&Reload theme\tF5") {
            public void run() {
                if (themeFileName == null) {
                    return;
                }
                loadTheme(themeFileName);
            }
        };
        viewMenu.add(reloadThemeAction);
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    static void initThemesFileDialog(FileDialog dialog) {
        dialog.setFilterNames(THEME_FILTER_NAMES);
        dialog.setFilterExtensions(THEME_FILTER_EXTENSIONS);
        dialog.setFilterPath(new File(THEMES_DIR).toString());
    }

    protected Control createContents(Composite parent) {
        final Composite contents = (Composite) super.createContents(parent);
        final FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 5;
        contents.setLayout(layout);

        ui = new UI(getShell().getDisplay());

        editor = new Editor(ui, this, contents);

        loadStartupTheme();

        editor.setDocument(Editor.makeSample());

        return contents;
    }

    void showException(Throwable exception, String dialogTitle) {
        exception.printStackTrace();
        final IStatus status =
                new Status(IStatus.ERROR, "zen.bricks",
                        exception.getClass().getName(), exception);
        ErrorDialog.openError(getShell(), dialogTitle, null, status);
    }

    private void loadStartupTheme() {
        final String fileName = preferences.get(LAST_THEME_KEY, null);
        LOAD_LAST: if (fileName != null) {
            try {
                loadThemeImpl(fileName);
            } catch (Exception ex) {
                System.err.println(
                        ex.getMessage() + " : " + ex.getCause().getMessage());
                break LOAD_LAST;
            }
            getStatusLineManager().setMessage(
                    "Loaded theme \"" + themeFileName + "\"");
            return;
        }
        try {
            loadThemeImpl(DEFAULT_THEME_FILE);
        } catch (Exception ex2) {
            showException(ex2.getCause(), ex2.getMessage());
            return;
        }
        getStatusLineManager().setMessage("Loaded default theme");
    }

    public void setTitle(String fileName) {
        getShell().setText("Bricks - " + fileName);
    }

    /* Does not interact with GUI */
    private void loadThemeImpl(String fileName) throws Exception {
        try {
            themePreferences = PropertiesPreferences.load(fileName);
        } catch (IOException ex) {
            throw new Exception("Error loading theme", ex);
        }
        try {
            ui.load(themePreferences);
        } catch (Exception ex) {
            throw new Exception("Error setting theme", ex);
        }
        themeFileName = fileName;
        preferences.put(LAST_THEME_KEY, fileName);
    }

    void loadTheme(String fileName) {
        try {
            loadThemeImpl(fileName);
        } catch (Exception ex) {
            showException(ex.getCause(), ex.getMessage());
            return;
        }
        getStatusLineManager().setMessage(
                "Loaded theme \"" + fileName + "\"");
    }

    void saveTheme(String fileName) {
        try {
            editor.getUI().save(themePreferences);
            themePreferences.save(fileName);
        } catch (IOException ex) {
            showException(ex, "Error saving theme");
            return;
        }
        getStatusLineManager().setMessage(
                "Saved theme \"" + fileName + "\"");
    }
}
