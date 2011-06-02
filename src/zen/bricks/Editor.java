package zen.bricks;

import java.util.LinkedList;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
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
    // ============================================================ Class Fields

    private static final int CANVAS_STYLE = SWT.V_SCROLL | SWT.H_SCROLL
            | SWT.BORDER | SWT.DOUBLE_BUFFERED /* | SWT.NO_BACKGROUND */
            | SWT.NO_REDRAW_RESIZE | SWT.NO_MERGE_PAINTS;

    private static final int VERT_SCROLL_INCREMENT = 10;

    private static final int HORIZ_SCROLL_INCREMENT = 10;

    // ================================================================== Fields

    private final MainWindow mainWindow;

    final Canvas canvas;

    Brick document;

    private UI ui;

    private Brick selection;

    private Rectangle clientArea;

    private ScrollBar verticalBar;

    private ScrollBar horizontalBar;

    private final Margin frameMargin = new Margin(10, 10, 10, 10);

    private final Margin scrollMargin = new Margin(10, 10, 10, 10);

    // ============================================================ Constructors

    public Editor(UI ui, MainWindow mainWindow, Composite parent) {
        this.mainWindow = mainWindow;
        this.ui = ui;
        canvas = new Canvas(parent, CANVAS_STYLE);

        verticalBar = canvas.getVerticalBar();
        horizontalBar = canvas.getHorizontalBar();
        initScrollBars();

        canvas.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                canvasResized();
            }
        });
        canvas.addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event) {
                paint(event.gc);
            }
        });

        final Caret caret = new Caret(canvas, SWT.NONE);
        caret.setVisible(false);

        createListeners();

        ui.addEditor(this);
    }

    // ================================================================= Methods

    private void initScrollBars() {
        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setIncrement(VERT_SCROLL_INCREMENT);
        verticalBar.setPageIncrement(100);
        verticalBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                scrollCanvasVertically();
            }
        });
        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setIncrement(HORIZ_SCROLL_INCREMENT);
        horizontalBar.setPageIncrement(100);
        horizontalBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                scrollCanvasHorizontally();
            }
        });
    }

    void paint(GC gc) {
        final Caret caret = canvas.getCaret();
        final boolean caretVisible = caret.getVisible();
        if (caretVisible) {
            caret.setVisible(false);
        }
        ui.preparePaint(gc);
        if (document != null) {
            document.repaint(gc, 0, 0, gc.getClipping(), this); // ?? 0,0
        }
        if (caretVisible) {
            caret.setVisible(true);
        }
    }

    void canvasResized() {
        clientArea = canvas.getClientArea();
        boolean needRepaint = false;

        final ScrollBar verticalBar = canvas.getVerticalBar();
        final int height = document.getHeight() + frameMargin.getVerticalSum(); // FIXME
        verticalBar.setMaximum(height);
        verticalBar.setThumb(Math.min(height, clientArea.height));
        verticalBar.setPageIncrement(clientArea.height); // TODO
        int vertSelection = verticalBar.getSelection();
        final int vertGap = height - clientArea.height;
        if (vertSelection >= vertGap) {
            if (vertGap <= 0) {
                vertSelection = 0;
            }
            needRepaint = true;
            document.y = frameMargin.getTop() - vertSelection; // ???
        }

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        final int width = document.getWidth() + frameMargin.getHorizontalSum(); // FIXME
        horizontalBar.setMaximum(width);
        horizontalBar.setThumb(Math.min(width, clientArea.width));
        horizontalBar.setPageIncrement(clientArea.width); // TODO
        int horizSelection = horizontalBar.getSelection();
        final int horizGap = width - clientArea.width;
        if (horizSelection >= horizGap) {
            if (horizGap <= 0) {
                horizSelection = 0;
            }
            needRepaint = true;
            document.x = frameMargin.getLeft() - horizSelection; // ???
        }

        if (needRepaint) {
            canvas.redraw();
        }
    }

    void scrollCanvasVertically() {
        final int newY = frameMargin.getTop() - verticalBar.getSelection();
        final int yDelta = newY - document.y;
        canvas.scroll(
                0, yDelta, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x, p.y + yDelta);

        document.y = newY;
    }

    void scrollCanvasHorizontally() {
        final int newX = frameMargin.getLeft() - horizontalBar.getSelection();
        final int xDelta = newX - document.x;
        canvas.scroll(
                xDelta, 0, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x + xDelta, p.y);

        document.x = newX;
    }

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
        canvas.setBackground(ui.getCanvasBackgroundColor());
        refresh();
    }

    public void setDocument(Brick document) {
        selection = null;
        canvas.getCaret().setVisible(false);
        if (this.document != null) {
            this.document.detach(this);
        }
        this.document = document;
        document.x = frameMargin.getTop();
        document.y = frameMargin.getLeft();
        document.attach(this);
        refresh();
    }

    public void refresh() {
        if (document != null) {
            document.invalidate(true);
            document.doLayout(this, false);
            canvasResized(); // ??
        }
        canvas.redraw();
        displayCaretFor(selection);
    }

    private static TupleBrick appendBrick(TupleBrick parent, String text) {
        final TupleBrick result = new TupleBrick(parent, text);
        parent.appendChild(result);
        return result;
    }

    static TupleBrick makeSample() {
        final TupleBrick rootBrick = new TupleBrick(null,
                "Quick brown fox\njumps over the lazy dog");

        rootBrick.appendChild(new ColorBrick(rootBrick, 200, 50,
                new RGB(192, 64, 64),
                new RGB(255, 128, 128)));
        rootBrick.newLine();
        final TupleBrick b1 = appendBrick(rootBrick, "Jumps over");
        appendBrick(b1, "the lazy dog.");
        rootBrick.newLine();
        rootBrick.appendChild(new ColorBrick(rootBrick, 50, 100,
                new RGB(64, 192, 64),
                new RGB(128, 255, 128)));
        rootBrick.newLine();
        rootBrick.appendChild(new ColorBrick(rootBrick, 100, 100,
                new RGB(64, 64, 192),
                new RGB(128, 128, 255)));

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
        if (document != null) {
            document.detach(this);
            document = null;
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
        Brick target = document;
        int x = event.x;
        int y = event.y;
        while (target != null) {
            x -= target.x;
            y -= target.y;
            target = target.handleMouseEvent(x, y, event, this);
        }
        if (event.doit && (event.type == SWT.MouseDown)) {
            setSelection(null);
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
        } else if (e.keyCode == SWT.F2 && e.stateMask == 0) {
            editBrick();
        } else if (e.keyCode == SWT.INSERT && e.stateMask == 0) {
            insertBrick();
        } else if (e.keyCode == SWT.CR && e.stateMask == 0) {
            insertLineBreak();
        } else if (e.keyCode == SWT.DEL && e.stateMask == 0) {
            deleteBrick();
        }
    }

    private void warning() {
        canvas.getDisplay().beep();
    }

    private void editBrick() {
        if (!(selection instanceof TupleBrick)) {
            warning();
            return;
        }
        final TupleBrick tupleBrick = (TupleBrick) selection;
        final InputDialog dialog =
                new InputDialog(mainWindow.getShell(), "Edit",
                        "Brick text:", tupleBrick.getText(), null);
        if (dialog.open() == Window.CANCEL) {
            return;
        }
        tupleBrick.setText(dialog.getValue());
        revalidate(tupleBrick);
    }

    private void insertBrick() {
        if (selection == null) {
            warning();
            return;
        }
        final int index = selection.index;
        final ContainerBrick parent = selection.getParent();
        if (parent == null) {
            warning();
            return;
        }
        if (!parent.isValidInsertIndex(index)) {
            warning();
            return;
        }
        final InputDialog dialog =
                new InputDialog(mainWindow.getShell(), "Insert",
                        "Brick text:", "", null);
        if (dialog.open() == Window.CANCEL) {
            return;
        }
        final Brick newBrick = new TupleBrick(parent, dialog.getValue());
        parent.insertChild(index, newBrick);
        newBrick.attach(this);
        revalidate(newBrick);
        setSelection(newBrick);
    }

    private void insertLineBreak() {
        if (selection == null) {
            warning();
            return;
        }
        final int index = selection.index;
        final ContainerBrick parent = selection.getParent();
        if (parent == null) {
            warning();
            return;
        }
        if (!parent.isValidInsertIndex(index)) {
            warning();
            return;
        }
        final Brick newBrick = new LineBreak(parent);
        parent.insertChild(index, newBrick);
        newBrick.attach(this);
        revalidate(newBrick);
        setSelection(newBrick);
    }

    private void deleteBrick() {
        if (selection == null) {
            warning();
            return;
        }
        final int index = selection.index;
        final ContainerBrick parent = selection.getParent();
        if (parent == null) {
            warning();
            return;
        }
        if (!parent.isValidDeleteIndex(index)) {
            warning();
            return;
        }
        final Brick old = parent.removeChild(index);
        old.detach(this);
        revalidate(parent);
        setSelection(parent); // FIXME
    }

    void revalidate(Brick brick) {
        while (brick != null) {
            boolean changed = brick.doLayout(this, true);
            if (!changed) {
                paintOnly(brick);
                return;
            }
            brick = brick.getParent();
        }
        // here brick == null, so the whole document is changed
        canvasResized(); // ???
    }

    void paintOnly(Brick brick) {
//        canvas.update();
        final Rectangle rect = brick.toScreen();
        if (rect.intersects(clientArea)) {
            canvas.redraw(rect.x, rect.y, rect.width, rect.height, false);
        }
     }

    private void scrollPageUp() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getPageIncrement();
        bar.setSelection(bar.getSelection() - increment);
        scrollCanvasVertically();
    }

    private void scrollPageDown() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getPageIncrement();
        bar.setSelection(bar.getSelection() + increment);
        scrollCanvasVertically();
    }

    private void scrollLineUp() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getIncrement();
        bar.setSelection(bar.getSelection() - increment);
        scrollCanvasVertically();
    }

    private void scrollLineDown() {
        final ScrollBar bar = canvas.getVerticalBar();
        final int increment = bar.getIncrement();
        bar.setSelection(bar.getSelection() + increment);
        scrollCanvasVertically();
    }

    void navigateLevelUp() {
        if (selection == null) {
            return;
        }
        final Brick parent = selection.getParent();
        if (parent != null) {
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
        if (brick != null) {
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
        } while (brick != null);
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
        if (previous != null) {
            setSelection(previous, true);
        }
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
        if (next != null) {
            setSelection(next, true);
        }
    }

    void navigateNextOrUp() {
        Brick brick = selection;
        while (brick != null) {
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
            scrollTo(selection);
        }
    }

    public void setSelection(Brick newSel, boolean scroll) {
        setSelection(newSel);
        if (scroll && (newSel != null)) {
            scrollTo(newSel);
        }
    }

    public void setSelection(Brick newSel) {
        final Brick oldSel = selection;
        selection = newSel;
        if (oldSel != null) {
            paintOnly(oldSel);
        }
        if (newSel != null) {
            paintOnly(newSel);
        }
        displayCaretFor(newSel);
//        mainWindow.setStatus("Selected: " + newSel); // DEBUG
        mainWindow.setStatus("Path = " + getPath(newSel)); // DEBUG
    }

    public void scrollTo(Brick brick) {
        final Rectangle rect = brick.toScreen();
        final int dy = verticalScrollNeeded(rect);
        final int dx = horizontalScrollNeeded(rect);
        horizontalBar.setSelection(horizontalBar.getSelection() - dx);
        verticalBar.setSelection(verticalBar.getSelection() - dy);
        scrollCanvasVertically();
        scrollCanvasHorizontally();
    }

    private int verticalScrollNeeded(Rectangle rect) {
        final int frameTop = clientArea.y + scrollMargin.getTop();
        if (rect.y < frameTop) {
            return frameTop - rect.y;
        }
        final int frameHeight = clientArea.height - scrollMargin.getVerticalSum();
        if (rect.height >= frameHeight) {
            return frameTop - rect.y;
        }
        final int brickBottom = rect.y + rect.height;
        final int frameBottom =
                clientArea.y + clientArea.height - scrollMargin.getBottom();
        if (brickBottom > frameBottom) {
            return frameBottom - brickBottom;
        }
        return 0;
    }

    private int horizontalScrollNeeded(Rectangle rect) {
        final int frameLeft = clientArea.x + scrollMargin.getLeft();
        if (rect.x < frameLeft) {
            return frameLeft - rect.x;
        }
        final int frameWidth = clientArea.width - scrollMargin.getHorizontalSum();
        if (rect.width >= frameWidth) {
            return frameLeft - rect.x;
        }
        final int brickRight = rect.x + rect.width;
        final int frameRight =
                clientArea.x + clientArea.width - scrollMargin.getRight();
        if (brickRight > frameRight) {
            return frameRight - brickRight;
        }
        return 0;
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

    private static String getPath(Brick brick) {
        final LinkedList<String> list = new LinkedList<String>();
        while (brick != null) {
            if (brick instanceof TupleBrick) {
                final TupleBrick tuple = (TupleBrick) brick;
                list.addFirst(brick.index + ":" +
                        Strings.removeChar(tuple.getText(), '\n'));
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
