package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

public class Editor
{
    // ================================================================== Fields

    final Canvas canvas;
    Brick root;
    Rectangle clientArea;
    UI ui;
    Font font;

    // ============================================================ Constructors

    public Editor(Composite parent) {
        canvas = new Canvas(parent, SWT.V_SCROLL | SWT.H_SCROLL
                        | SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND
                        | SWT.NO_REDRAW_RESIZE);
        final Display display = parent.getDisplay();
        font = new Font(display, "Georgia", 9, SWT.NORMAL);
        canvas.setFont(font);
        ui = new UI(canvas);

        createListeners();
        initScrollbars();

        setRoot(makeSample());
    }

    // ================================================================= Methods

    public void setRoot(TextBrick rootBrick) {
        if (root != null) {
            root.dispose();
        }
        root = rootBrick;
        root.realize(ui);
        root.calculateSize(ui);
        resized();
        canvas.redraw();
    }

    TextBrick makeSample() {
        TextBrick rootBrick = new TextBrick(null,
                "Quick brown fox\njumps over the lazy dog");

        final ColorBrick color1 = new ColorBrick(rootBrick, 200, 50,
                new RGB(192, 64, 64),
                new RGB(255, 128, 128));
        color1.lineBreak = false;
        final TextBrick b1 = new TextBrick(rootBrick, "Jumps over");
        final Brick text2 = new TextBrick(b1, "the lazy dog.");
        text2.lineBreak = false;
        new ColorBrick(rootBrick, 50, 100,
                new RGB(64, 192, 64),
                new RGB(128, 255, 128));
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
    }

    private void initScrollbars() {
        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setIncrement(5);
        verticalBar.setPageIncrement(100);
        verticalBar.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                vertScroll();
            }
        });
        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setIncrement(5);
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
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (ui != null) {
            ui.dispose();
            ui = null;
        }
    }

    void resized() {
        clientArea = canvas.getClientArea();
        boolean needRepaint = false;

        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setMaximum(root.height);
        verticalBar.setThumb(Math.min(root.height, clientArea.height));
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
        gc.setAntialias(SWT.ON);
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
}
