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
    final Canvas canvas;
    TextBrick rootBrick; // fix type
    Rectangle clientArea;
    int xSelection;
    int ySelection;
    UI ui;
    Font font;

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

        rootBrick = new TextBrick(null,
                "Quick brown fox\njumps over the lazy dog");

        rootBrick.addChild(new ColorBrick(rootBrick, 200, 50,
                new RGB(192, 64, 64),
                new RGB(255, 128, 128)));
        final TextBrick b1 = new TextBrick("Jumps over");
        rootBrick.addChild(b1);
        b1.addChild(new TextBrick("the lazy dog."));
        rootBrick.addChild(new ColorBrick(rootBrick, 50, 100,
                new RGB(64, 192, 64),
                new RGB(128, 255, 128)));
        rootBrick.addChild(new ColorBrick(rootBrick, 100, 100,
                new RGB(64, 64, 192),
                new RGB(128, 128, 255)));

        rootBrick.realize(ui);
        rootBrick.calculateSize(ui);
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
        rootBrick.dispose();
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
        verticalBar.setMaximum(rootBrick.height);
        verticalBar.setThumb(Math.min(rootBrick.height, newArea.height));
        ySelection = verticalBar.getSelection(); // is it ok?

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setMaximum(rootBrick.width);
        horizontalBar.setThumb(Math.min(rootBrick.width, newArea.width));
        xSelection = horizontalBar.getSelection(); // is it ok?

        clientArea = newArea;
    }

    void vertScroll() {
        final int newYSelection = canvas.getVerticalBar().getSelection();
        final int delta = newYSelection - ySelection;
        ySelection = newYSelection;
        canvas.scroll(0, -delta, 0, 0, clientArea.width, clientArea.height, false);
    }

    void horizScroll() {
        final int newXSelection = canvas.getHorizontalBar().getSelection();
        final int delta = newXSelection - xSelection;
        xSelection = newXSelection;
        canvas.scroll(-delta, 0, 0, 0, clientArea.width, clientArea.height, false);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    void paint(GC gc) {
//        System.out.println("Clipping (x, y, w, h): " + gc.getClipping() +
//                ", selection(x, y): " + xSelection + ", " + ySelection);
        gc.setAntialias(SWT.ON);
        rootBrick.paint(gc, -xSelection, -ySelection, ui);
    }
}
