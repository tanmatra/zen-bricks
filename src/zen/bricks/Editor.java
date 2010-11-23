package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

public class Editor
{
    // ============================================================ Class Fields

    private static final int VERT_SCROLL_INCREMENT = 5;
    private static final int HORIZ_SCROLL_INCREMENT = 5;

    // ================================================================== Fields

    final Canvas canvas;

    Brick root;

    Rectangle clientArea;

    UI ui;

    // ============================================================ Constructors

    public Editor(MainWindow mainWindow, Composite parent) {
        canvas = new Canvas(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER
                | SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND
                | SWT.NO_REDRAW_RESIZE);
        createListeners();
        initScrollbars();
    }

    // ================================================================= Methods

    public void setUI(UI ui) {
        this.ui = ui;
        ui.applyTo(this);
        refresh();
    }

    public void setDocument(TupleBrick documentBrick) {
        if (root != null) {
            root.dispose();
        }
        root = documentBrick;
        root.x = 0;
        root.y = 0;
        canvas.getVerticalBar().setSelection(0);
        canvas.getHorizontalBar().setSelection(0);
        refresh();
    }

    public void refresh() {
        if (root != null) {
            root.realize(this);
            root.calculateSize(ui);
            resized();
        }
        canvas.redraw();
    }

    static TupleBrick makeSample() {
        TupleBrick rootBrick = new TupleBrick(null,
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
        canvas.addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event e) {
                paint(e.gc);
            }
        });
        canvas.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                resized();
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

    private void initScrollbars() {
        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setIncrement(VERT_SCROLL_INCREMENT);
        verticalBar.setPageIncrement(100);
        verticalBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                vertScroll();
            }
        });
        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setIncrement(HORIZ_SCROLL_INCREMENT);
        horizontalBar.setPageIncrement(100);
        horizontalBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                horizScroll();
            }
        });
    }

    void disposed() {
        if (root != null) {
            root.dispose();
            root = null;
        }
        if (ui != null) {
            ui.dispose();
            ui = null;
        }
    }

    void resized() {
        clientArea = canvas.getClientArea();
        if (root == null) {
            return;
        }
        boolean needRepaint = false;

        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setMaximum(root.height);
        verticalBar.setThumb(Math.min(root.height, clientArea.height));
        verticalBar.setPageIncrement(clientArea.height); // TODO
        int vertSelection = verticalBar.getSelection();
        final int vertGap = root.height - clientArea.height;
        if (vertSelection >= vertGap) {
            if (vertGap <= 0) {
                vertSelection = 0;
            }
            needRepaint = true;
            root.y = - vertSelection;
        }

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setMaximum(root.width);
        horizontalBar.setThumb(Math.min(root.width, clientArea.width));
        horizontalBar.setPageIncrement(clientArea.width); // TODO
        int horizSelection = horizontalBar.getSelection();
        final int horizGap = root.width - clientArea.width;
        if (horizSelection >= horizGap) {
            if (horizGap <= 0) {
                horizSelection = 0;
            }
            needRepaint = true;
            root.x = - horizSelection;
        }

        if (needRepaint) {
            canvas.redraw();
        }
    }

    void vertScroll() {
        final int newY = - canvas.getVerticalBar().getSelection();
        final int yDelta = newY - root.y;
        canvas.scroll(0, yDelta, 0, 0, clientArea.width, clientArea.height,
                false);
        root.y = newY;
    }

    void horizScroll() {
        final int newX = - canvas.getHorizontalBar().getSelection();
        final int xDelta = newX - root.x;
        canvas.scroll(xDelta, 0, 0, 0, clientArea.width, clientArea.height,
                false);
        root.x = newX;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    void paint(GC gc) {
        ui.preparePaint(gc);
        final Rectangle clipping = gc.getClipping();

        gc.setBackground(ui.getCanvasBackgroundColor());

        // draw background on the right
        final int rightMarginWidth = clientArea.width - root.width;
        final int rightMarginX = root.x + root.width;
        if (rightMarginWidth > 0) {
            gc.fillRectangle(rightMarginX, 0, rightMarginWidth, clientArea.height);
        }

        // draw background on the bottom
        final int bottomMarginHeight = clientArea.height - root.height;
        final int bottomMarginY = root.y + root.height;
        if (bottomMarginHeight > 0) {
            gc.fillRectangle(0, bottomMarginY, rightMarginX, bottomMarginHeight);
        }

        root.paint(gc, root.x, root.y, ui, clipping);
    }

    void handleMouseEvent(Event event) {
        Brick target = root;
        int x = event.x;
        int y = event.y;
        while (target != null) {
            x -= target.x;
            y -= target.y;
            target = target.mouseDown(x, y, event);
        }
    }

    void keyDown(Event e) {
        if (e.keyCode == SWT.PAGE_DOWN && e.stateMask == 0) {
            final ScrollBar bar = canvas.getVerticalBar();
            final int increment = bar.getPageIncrement();
            bar.setSelection(bar.getSelection() + increment);
            vertScroll();
        } else if (e.keyCode == SWT.PAGE_UP && e.stateMask == 0) {
            final ScrollBar bar = canvas.getVerticalBar();
            final int increment = bar.getPageIncrement();
            bar.setSelection(bar.getSelection() - increment);
            vertScroll();
        } else if (e.keyCode == SWT.ARROW_UP && e.stateMask == 0) {
            final ScrollBar bar = canvas.getVerticalBar();
            final int increment = bar.getIncrement();
            bar.setSelection(bar.getSelection() - increment);
            vertScroll();
        } else if (e.keyCode == SWT.ARROW_DOWN && e.stateMask == 0) {
            final ScrollBar bar = canvas.getVerticalBar();
            final int increment = bar.getIncrement();
            bar.setSelection(bar.getSelection() + increment);
            vertScroll();
        }
    }
}
