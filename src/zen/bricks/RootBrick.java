package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

public class RootBrick extends ContainerBrick
{
    // ============================================================ Class Fields

    private static final int VERT_SCROLL_INCREMENT = 5;
    private static final int HORIZ_SCROLL_INCREMENT = 5;

    // ================================================================== Fields

    private final Editor editor;

    private Brick document;

    private final Canvas canvas;

    private final ScrollBar verticalBar;

    private final ScrollBar horizontalBar;

    Rectangle clientArea;

    private Margin padding = new Margin(5, 5, 5, 5);

    // ============================================================ Constructors

    public RootBrick(Editor editor) {
        super(null);
        this.editor = editor;

        canvas = editor.getCanvas();
        verticalBar = canvas.getVerticalBar();
        horizontalBar = canvas.getHorizontalBar();

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

        initScrollbars();
    }

    // ================================================================= Methods

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

    void vertScroll() {
        final int newY = - verticalBar.getSelection();
        final int yDelta = newY - y;
        canvas.scroll(
                0, yDelta, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);
        y = newY;
    }

    void horizScroll() {
        final int newX = - horizontalBar.getSelection();
        final int xDelta = newX - x;
        canvas.scroll(
                xDelta, 0, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);
        x = newX;
    }

    void calculateSize(UI ui, Editor editor) {
        if (document != null) {
            document.calculateSize(ui, editor);
            width = document.width + padding.getHorizontalSum();
            height = document.height + padding.getVerticalSum();
        }
    }

    void canvasResized() {
        clientArea = canvas.getClientArea();
        boolean needRepaint = false;

        final ScrollBar verticalBar = canvas.getVerticalBar();
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
            y = - vertSelection;
        }

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
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
            x = - horizSelection;
        }

        if (needRepaint) {
            canvas.redraw();
        }
    }

    void realize(Editor editor) {
        canvas.setBackground(editor.getUI().getCanvasBackgroundColor());
//        if (document != null) {
//            document.realize(editor);
//        }
        super.realize(editor);
    }

    void paint(GC gc) {
        final UI ui = editor.getUI();
        ui.preparePaint(gc);
        final Rectangle clipping = gc.getClipping();
        paint(gc, x, y, clipping, editor);
    }

    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
                      Editor editor)
    {
        if (document != null) {
            document.repaint(gc, baseX, baseY, clipping, editor);
        }
    }

    protected void addChild(Brick child) {
        document = child;
        child.index = 0;
        child.x = padding.getTop();
        child.y = padding.getLeft();
        x = 0;
        y = 0;
        verticalBar.setSelection(0);
        horizontalBar.setSelection(0);
    }

    protected Brick getChild(int i) {
        if ((i < 0) || (i >= childrenCount())) {
            throw new IndexOutOfBoundsException();
        }
        return document;
    }

    protected int childrenCount() {
        return (document != null) ? 1 : 0;
    }

    protected void childResized(Brick child) {
        // TODO Auto-generated method stub
    }

    public Brick mouseEvent(int mouseX, int mouseY, Event event, Editor editor)
    {
        if ((document != null) && document.contains(mouseX, mouseY)) {
            return document;
        } else {
            editor.setSelection(null);
            return null;
        }
    }
}
