package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
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

    public Editor(MainWindow mainWindow, Composite parent) {
        this.mainWindow = mainWindow;
        canvas = new Canvas(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER
                | SWT.DOUBLE_BUFFERED /*| SWT.NO_BACKGROUND*/
                | SWT.NO_REDRAW_RESIZE | SWT.NO_MERGE_PAINTS);
        createListeners();
        root = new RootBrick(this);
    }

    // ================================================================= Methods

    public UI getUI() {
        return ui;
    }

    public void setUI(UI ui) {
        if (this.ui != null) {
            this.ui.dispose();
        }
        this.ui = ui;
        ui.applyTo(this);
        refresh();
    }

    public void setDocument(TupleBrick documentBrick) {
        selection = null;
        if (document != null) {
            document.dispose();
        }
        document = documentBrick;
        root.addChild(documentBrick);
        documentBrick.realize(this);
        refresh();
    }

    public void refresh() {
        if (root != null) {
            root.realize(this);
            root.calculateSize(ui, this);
            root.canvasResized();
        }
        canvas.redraw();
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
            root.dispose();
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
        } else if (e.keyCode == SWT.ARROW_LEFT && e.stateMask == 0) {
            navigateLevelUp();
        } else if (e.keyCode == SWT.ARROW_RIGHT && e.stateMask == 0) {
            navigateLevelDown();
        } else if (e.keyCode == SWT.ARROW_UP && e.stateMask == 0) {
            navigatePreviousElement();
        } else if (e.keyCode == SWT.ARROW_DOWN && e.stateMask == 0) {
            navigateNextElement();
        }
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

    private void navigateLevelUp() {
        if (selection == null) {
            return;
        }
        final ContainerBrick parent = selection.getParent();
        if (!(parent instanceof RootBrick)) {
            setSelection(parent);
        }
    }

    private void navigateLevelDown() {
        if (!(selection instanceof ContainerBrick)) {
            return;
        }
        final ContainerBrick container = (ContainerBrick) selection;
        final Brick brick = container.getFirstChild();
        if (brick != null) {
            setSelection(brick);
        }
    }

    private void navigatePreviousElement() {
        if (selection == null) {
            return;
        }
        Brick previous = selection.getPreviousSibling();
        if (previous == null) {
            previous = selection.getParent();
            if (previous instanceof RootBrick) {
                return;
            }
        }
        setSelection(previous);
    }

    private void navigateNextElement() {
        Brick brick = selection;
        while ((brick != null) && !(brick instanceof RootBrick)) {
            final Brick next = brick.getNextSibling();
            if (next != null) {
                setSelection(next);
                return;
            }
            brick = brick.getParent();
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
        mainWindow.setStatus("Selected: " + newSel); // DEBUG
    }

    public Brick getSelection() {
        return selection;
    }
}
