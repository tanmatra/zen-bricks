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
        canvas = new Canvas(parent,
                SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);
//                /*SWT.NO_BACKGROUND |*/ SWT.NO_REDRAW_RESIZE /*| SWT.BORDER*/);

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
        final Rectangle newArea = canvas.getClientArea();

        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setMaximum(root.height);
        verticalBar.setThumb(Math.min(root.height, newArea.height));
        root.y = verticalBar.getSelection(); // is it ok?

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setMaximum(root.width);
        horizontalBar.setThumb(Math.min(root.width, newArea.width));
        root.x = horizontalBar.getSelection(); // is it ok?

        clientArea = newArea;
    }

    void vertScroll() {
        final int newYSelection = canvas.getVerticalBar().getSelection();
        final int delta = newYSelection - root.y;
        canvas.scroll(0, -delta, 0, 0, clientArea.width, clientArea.height,
                false);
        root.y = newYSelection;
    }

    void horizScroll() {
        final int newXSelection = canvas.getHorizontalBar().getSelection();
        final int delta = newXSelection - root.x;
        canvas.scroll(-delta, 0, 0, 0, clientArea.width, clientArea.height,
                false);
        root.x = newXSelection;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    void paint(GC gc) {
        gc.setAntialias(SWT.ON);
        final Rectangle clipping = gc.getClipping();
        root.paint(gc, -root.x, -root.y, ui, clipping);
    }
}
