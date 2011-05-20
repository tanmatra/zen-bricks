package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
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

    private Rectangle clientArea;

    private final Margin padding = new Margin(5, 5, 5, 5);

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

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x, p.y + yDelta);

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

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x + xDelta, p.y);

        x = newX;
    }

    protected boolean doLayout(Editor editor) {
        if (document != null) {
            document.validate(editor);
            final int width = document.getWidth() + padding.getHorizontalSum();
            final int height = document.getHeight() + padding.getVerticalSum();
            return resize(width, height);
        } else {
            return false;
        }
    }

    void canvasResized() {
        clientArea = canvas.getClientArea();
        boolean needRepaint = false;

        final ScrollBar verticalBar = canvas.getVerticalBar();
        verticalBar.setMaximum(getHeight());
        verticalBar.setThumb(Math.min(getHeight(), clientArea.height));
        verticalBar.setPageIncrement(clientArea.height); // TODO
        int vertSelection = verticalBar.getSelection();
        final int vertGap = getHeight() - clientArea.height;
        if (vertSelection >= vertGap) {
            if (vertGap <= 0) {
                vertSelection = 0;
            }
            needRepaint = true;
            y = - vertSelection;
        }

        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setMaximum(getWidth());
        horizontalBar.setThumb(Math.min(getWidth(), clientArea.width));
        horizontalBar.setPageIncrement(clientArea.width); // TODO
        int horizSelection = horizontalBar.getSelection();
        final int horizGap = getWidth() - clientArea.width;
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
        final Caret caret = canvas.getCaret();
        final boolean caretVisible = caret.getVisible();
        if (caretVisible) {
            caret.setVisible(false);
        }
        editor.getUI().preparePaint(gc);
        paint(gc, x, y, gc.getClipping(), editor);
        if (caretVisible) {
            caret.setVisible(true);
        }
    }

    void paintOnly(Brick brick) {
//        canvas.update();
        final Rectangle rect = brick.toScreen();
        if (rect.intersects(clientArea)) {
            canvas.redraw(rect.x, rect.y, rect.width, rect.height, false);
        }
    }

    void paintAll() {
        canvas.redraw();
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
        valid = false;
        child.parent = this;
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

    public Brick getFirstChild() {
        return document;
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

    public void scrollTo(Brick brick) {
        final Rectangle rect = brick.toScreen();
        final int dy = verticalScrollNeeded(rect);
        final int dx = horizontalScrollNeeded(rect);
        horizontalBar.setSelection(horizontalBar.getSelection() - dx);
        verticalBar.setSelection(verticalBar.getSelection() - dy);
        vertScroll();
        horizScroll();
    }

    private int verticalScrollNeeded(Rectangle rect) {
        final int frameTop = clientArea.y + padding.getTop();
        if (rect.y < frameTop) {
            return frameTop - rect.y;
        }
        final int frameHeight = clientArea.height - padding.getVerticalSum();
        if (rect.height >= frameHeight) {
            return frameTop - rect.y;
        }
        final int brickBottom = rect.y + rect.height;
        final int frameBottom =
                clientArea.y + clientArea.height - padding.getBottom();
        if (brickBottom > frameBottom) {
            return frameBottom - brickBottom;
        }
        return 0;
    }

    private int horizontalScrollNeeded(Rectangle rect) {
        final int frameLeft = clientArea.x + padding.getLeft();
        if (rect.x < frameLeft) {
            return frameLeft - rect.x;
        }
        final int frameWidth = clientArea.width - padding.getHorizontalSum();
        if (rect.width >= frameWidth) {
            return frameLeft - rect.x;
        }
        final int brickRight = rect.x + rect.width;
        final int frameRight =
                clientArea.x + clientArea.width - padding.getRight();
        if (brickRight > frameRight) {
            return frameRight - brickRight;
        }
        return 0;
    }
}
