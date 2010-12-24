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

    String themeFileName;

    private Action reloadThemeAction;

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
        super.configureShell(shell);
        shell.setText("Bricks");
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
    private void createViewMenu(final MenuManager mainMenu) {
        final MenuManager viewMenu = new MenuManager("&View");
        mainMenu.add(viewMenu);

        // ---------------------------------------------------------------------
        final Action editStylesAction = new Action("&Edit styles...") {
            private WeakReference<Style> lastStyleRef;

            public void run() {
                Style lastStyle;
                final EditStylesDialog dialog = new EditStylesDialog(
                        getShell(), editor);

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
                themeFileName = fileName;
                loadTheme();
            }
        };
        viewMenu.add(loadThemeAction);

        // ---------------------------------------------------------------------
        final Action saveThemeAction = new Action("Save theme as...") {
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

        editor = new Editor(this, contents);

        themeFileName = preferences.get(LAST_THEME_KEY, DEFAULT_THEME_FILE);
        loadTheme();

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

    void setEditorTheme(Preferences themePrefs) {
        final UI ui;
        try {
            ui = new UI(getShell().getDisplay(), themePrefs);
        } catch (Exception e) {
            handleException(e, "Error setting theme");
            return;
        }
        editor.setUI(ui);
    }

    void loadTheme() {
        preferences.put(LAST_THEME_KEY, themeFileName);

        try {
            themePreferences = PropertiesPreferences.load(themeFileName);
        } catch (IOException e) {
            handleException(e, "Error loading theme");
            return;
        }
        setEditorTheme(themePreferences);
        getStatusLineManager().setMessage(
                "Loaded theme \"" + themeFileName + "\"");
        reloadThemeAction.setEnabled(true);
    }

    void saveTheme(String fileName) {
        try {
            themePreferences.save(fileName);
        } catch (IOException e) {
            handleException(e, "Error saving theme");
            return;
        }
        getStatusLineManager().setMessage(
                "Saved theme \"" + fileName + "\"");
    }
}
