package zen.bricks;

import java.util.LinkedList;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

public class Editor
{
    // ================================================================== Fields

    private final MainWindow mainWindow;

    final Canvas canvas;

    final RootBrick root;

    Brick document;

    private UI ui;

    private Brick selection;

    // ============================================================ Constructors

    public Editor(UI ui, MainWindow mainWindow, Composite parent) {
        this.mainWindow = mainWindow;
        this.ui = ui;
        canvas = new Canvas(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER
                | SWT.DOUBLE_BUFFERED /*| SWT.NO_BACKGROUND*/
                | SWT.NO_REDRAW_RESIZE | SWT.NO_MERGE_PAINTS);

        final Caret caret = new Caret(canvas, SWT.NONE);
        caret.setVisible(false);

        createListeners();
        root = new RootBrick(this);

        ui.addEditor(this);
    }

    // ================================================================= Methods

    public UI getUI() {
        return ui;
    }

    public void setUI(UI ui) {
        if (this.ui != null) {
            this.ui.removeEditor(this);
        }
        this.ui = ui;
        ui.applyTo(this);
        ui.addEditor(this);
    }

    public void uiChanged() {
        refresh();
    }

    public void setDocument(TupleBrick documentBrick) {
        selection = null;
        canvas.getCaret().setVisible(false);
        if (document != null) {
            document.detach(this);
        }
        document = documentBrick;
        root.addChild(documentBrick);
        documentBrick.attach(this);
        refresh();
    }

    public void refresh() {
        if (root != null) {
            root.attach(this);
            root.validate(this);
            root.canvasResized();
        }
        canvas.redraw();
        displayCaretFor(selection);
    }

    static TupleBrick makeSample() {
        final TupleBrick rootBrick = new TupleBrick(null,
        "Quick brown fox\njumps over the lazy dog");

        new ColorBrick(rootBrick, 200, 50,
                new RGB(192, 64, 64),
                new RGB(255, 128, 128));
        rootBrick.newLine();
        final TupleBrick b1 = new TupleBrick(rootBrick, "Jumps over");
        new TupleBrick(b1, "the lazy dog.");
        rootBrick.newLine();
        new ColorBrick(rootBrick, 50, 100,
                new RGB(64, 192, 64),
                new RGB(128, 255, 128));
        rootBrick.newLine();
        new ColorBrick(rootBrick, 100, 100,
                new RGB(64, 64, 192),
                new RGB(128, 128, 255));

        return rootBrick;
    }

    private void createListeners() {
        canvas.addListener(SWT.Dispose, new Listener() {
            public void handleEvent(Event e) {
                disposed();
            }
        });
        final Listener mouseListener = new Listener() {
            public void handleEvent(Event event) {
                handleMouseEvent(event);
            }
        };
        canvas.addListener(SWT.MouseDown, mouseListener);
        canvas.addListener(SWT.MouseUp, mouseListener);
        canvas.addListener(SWT.MouseDoubleClick, mouseListener);
        // canvas.addListener(SWT.MouseHover, mouseListener);
        // canvas.addListener(SWT.MouseMove, mouseListener);
        canvas.addListener(SWT.KeyDown, new Listener() {
            public void handleEvent(Event e) {
                keyDown(e);
            }
        });
    }


    void disposed() {
        if (root != null) {
            root.detach(this);
//            root = null;
        }
        if (ui != null) {
            ui.dispose();
            ui = null;
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    void handleMouseEvent(Event event) {
        Brick target = root;
        int x = event.x;
        int y = event.y;
        while (target != null) {
            x -= target.x;
            y -= target.y;
            target = target.mouseEvent(x, y, event, this);
        }
    }

    void keyDown(Event e) {
        if (e.keyCode == SWT.PAGE_UP && e.stateMask == 0) {
            scrollPageUp();
        } else if (e.keyCode == SWT.PAGE_DOWN && e.stateMask == 0) {
            scrollPageDown();
        } else if (e.keyCode == SWT.ARROW_UP && e.stateMask == SWT.CTRL) {
            scrollLineUp();
        } else if (e.keyCode == SWT.ARROW_DOWN && e.stateMask == SWT.CTRL) {
            scrollLineDown();
//        } else if (e.keyCode == SWT.HOME && e.stateMask == 0) {
//            navigateLevelUp();
//        } else if (e.keyCode == SWT.ARROW_LEFT && e.stateMask == 0) {
//            navigatePreceding();
//        } else if (e.keyCode == SWT.ARROW_RIGHT && e.stateMask == 0) {
//            navigateFollowing();
//        } else if (e.keyCode == SWT.ARROW_UP && e.stateMask == SWT.ALT) {
//            navigatePrevious(true);
//        } else if (e.keyCode == SWT.ARROW_UP && e.stateMask == 0) {
//            navigatePrevious(false);
//        } else if (e.keyCode == SWT.ARROW_DOWN && e.stateMask == 0) {
//            navigateNextOrUp();
//        } else if (e.keyCode == SWT.ARROW_DOWN && e.stateMask == SWT.ALT) {
//            navigateNext(false);
//        } else if (e.keyCode == ' ' && e.stateMask == 0) {
//            scrollToSelected();
        } else if (e.keyCode == SWT.CR && e.stateMask == 0) {
            editBrick();
        }
    }

    private void editBrick() {
        if (!(selection instanceof TupleBrick)) {
            return;
        }
        final TupleBrick tupleBrick = (TupleBrick) selection;
        final InputDialog dialog =
                new InputDialog(mainWindow.getShell(), "Edit",
                        "Brick text:", tupleBrick.text, null);
        if (dialog.open() == Window.CANCEL) {
            return;
        }
        tupleBrick.setText(dialog.getValue());
        tupleBrick.invalidate();
//        root.paintOnly(tupleBrick); // LATER
        root.validate(this);
        root.paintAll();
    }

    private void scrollPageUp() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getPageIncrement();
        bar.setSelection(bar.getSelection() - increment);
        root.vertScroll();
    }

    private void scrollPageDown() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getPageIncrement();
        bar.setSelection(bar.getSelection() + increment);
        root.vertScroll();
    }

    private void scrollLineUp() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getIncrement();
        bar.setSelection(bar.getSelection() - increment);
        root.vertScroll();
    }

    private void scrollLineDown() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getIncrement();
        bar.setSelection(bar.getSelection() + increment);
        root.vertScroll();
    }

    void navigateLevelUp() {
        if (selection == null) {
            return;
        }
        final Brick parent = selection.getParent();
        if (!(parent instanceof RootBrick)) {
            setSelection(parent, true);
        }
    }

    void navigatePreceding() {
        if (selection == null) {
            return;
        }
        Brick brick = selection;
        Brick prec = brick.getPreviousSibling();
        if (prec != null) {
            setSelection(prec.getLastDescendantOrSelf(), true);
            return;
        }
        brick = brick.getParent();
        if (!isTop(brick)) {
            setSelection(brick, true);
        }
    }

    void navigateFollowing() {
        if (selection == null) {
            return;
        }
        Brick brick = selection;
        Brick following = brick.getFirstChild();
        if (following != null) {
            setSelection(following, true);
            return;
        }
        do {
            following = brick.getNextSibling();
            if (following != null) {
                setSelection(following, true);
                return;
            }
            brick = brick.getParent();
        } while (!isTop(brick));
    }

    void navigatePrevious(boolean allowParent) {
        if (selection == null) {
            return;
        }
        Brick previous = selection.getPreviousSibling();
        if (previous != null) {
            setSelection(previous, true);
            return;
        }
        if (!allowParent) {
            return;
        }
        previous = selection.getParent();
        if (isTop(previous)) {
            return;
        }
        setSelection(previous, true);
    }

    void navigateNext(boolean allowParent) {
        Brick brick = selection;
        if (brick == null) {
            return;
        }
        Brick next = brick.getNextSibling();
        if (next != null) {
            setSelection(next, true);
            return;
        }
        if (!allowParent) {
            return;
        }
        next = brick.getParent();
        if ((next != null) && !(next instanceof RootBrick)) {
            setSelection(next, true);
        }
    }

    void navigateNextOrUp() {
        Brick brick = selection;
        while (!isTop(brick)) {
            final Brick next = brick.getNextSibling();
            if (next != null) {
                setSelection(next, true);
                return;
            }
            brick = brick.getParent();
        }
    }

    void scrollToSelected() {
        if (selection != null) {
            root.scrollTo(selection);
        }
    }

    public void setSelection(Brick newSel, boolean scroll) {
        setSelection(newSel);
        if (scroll && (newSel != null)) {
            root.scrollTo(newSel);
        }
    }

    public void setSelection(Brick newSel) {
        final Brick oldSel = selection;
        selection = newSel;
        if (oldSel != null) {
            root.paintOnly(oldSel);
        }
        if (newSel != null) {
            root.paintOnly(newSel);
        }
        displayCaretFor(newSel);
//        mainWindow.setStatus("Selected: " + newSel); // DEBUG
        mainWindow.setStatus("Path = " + getPath(newSel)); // DEBUG
    }

    void displayCaretFor(Brick brick) {
        if (brick != null) {
            final Rectangle rect = brick.toScreen();
            final Caret caret = canvas.getCaret();
            caret.setBounds(rect.x + ui.caretOffset, rect.y,
                    ui.caretWidth, rect.height);
            caret.setVisible(true);
        } else {
            canvas.getCaret().setVisible(false);
        }
    }

    private static boolean isTop(Brick brick) {
        return (brick == null) || (brick instanceof RootBrick);
    }

    private static String getPath(Brick brick) {
        final LinkedList<String> list = new LinkedList<String>();
        while ((brick != null) && !(brick instanceof RootBrick)) {
            if (brick instanceof TupleBrick) {
                final TupleBrick tuple = (TupleBrick) brick;
                list.addFirst(brick.index + ":" +
                        Strings.removeChar(tuple.text, '\n'));
            } else {
                list.addFirst(brick.index + ":" +
                        brick.getClass().getName());
            }
            brick = brick.getParent();
        }
        return Strings.join(list, " / ");
    }

    public Brick getSelection() {
        return selection;
    }
}
