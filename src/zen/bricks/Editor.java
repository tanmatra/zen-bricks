package zen.bricks;

import java.util.LinkedList;
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
import zen.bricks.Position.Side;
import zen.bricks.io.ZenFileType;

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

    private Brick document;

    private UI ui;

    private Position position;

    private Rectangle clientArea;

    private ScrollBar verticalBar;

    private ScrollBar horizontalBar;

    private final Margin frameMargin = new Margin(10, 10, 10, 10);

    private final Margin scrollMargin = new Margin(10, 10, 10, 10);

    private ZenFileType fileType;

    private String fileName;

    // ============================================================ Constructors

    public Editor(UI ui, MainWindow mainWindow, Composite parent) {
        this.mainWindow = mainWindow;
        this.ui = ui;
        canvas = new Canvas(parent, CANVAS_STYLE);

        verticalBar = canvas.getVerticalBar();
        horizontalBar = canvas.getHorizontalBar();
        initScrollBars();

        canvas.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                canvasResized(false);
            }
        });
        canvas.addListener(SWT.Paint, new Listener() {
            @Override
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
            @Override
            public void handleEvent(Event event) {
                scrollCanvasVertically();
            }
        });
        final ScrollBar horizontalBar = canvas.getHorizontalBar();
        horizontalBar.setIncrement(HORIZ_SCROLL_INCREMENT);
        horizontalBar.setPageIncrement(100);
        horizontalBar.addListener(SWT.Selection, new Listener() {
            @Override
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

    void canvasResized(boolean needRepaint) {
        clientArea = canvas.getClientArea();

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
            document.setY(frameMargin.getTop() - vertSelection); // ???
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
            document.setX(frameMargin.getLeft() - horizSelection); // ???
        }

        if (needRepaint) {
            canvas.redraw();
        }
    }

    void scrollCanvasVertically() {
        final int newY = frameMargin.getTop() - verticalBar.getSelection();
        final int yDelta = newY - document.getY();
        canvas.scroll(
                0, yDelta, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x, p.y + yDelta);

        document.setY(newY);
    }

    void scrollCanvasHorizontally() {
        final int newX = frameMargin.getLeft() - horizontalBar.getSelection();
        final int xDelta = newX - document.getX();
        canvas.scroll(
                xDelta, 0, /* destX, destY */
                0, 0, /* sourceX, sourceY */
                clientArea.width, clientArea.height,
                false);

        final Caret caret = canvas.getCaret();
        final Point p = caret.getLocation();
        caret.setLocation(p.x + xDelta, p.y);

        document.setX(newX);
    }

    public UI getUI() {
        return ui;
    }

    public void setUI(UI ui) {
        if (this.ui != null) {
            this.ui.removeEditor(this);
        }
        this.ui = ui;
        ui.addEditor(this);
    }

    public void uiChanged() {
        canvas.setBackground(ui.getCanvasBackgroundColor());
        refresh();
    }

    public Brick getDocument() {
        return document;
    }

    public void setDocument(Brick document) {
        internalSetPosition(null);
        canvas.getCaret().setVisible(false);
        if (this.document != null) {
            this.document.detach(this);
        }
        this.document = document;
        document.setX(frameMargin.getTop());
        document.setY(frameMargin.getLeft());
        document.attach(this);
        refresh();
        setPosition(document.enter(Side.LEFT));
    }

    public void refresh() {
        if (document != null) {
            document.invalidate(true);
            document.doLayout(this, false);
            canvasResized(false); // ??
        }
        canvas.redraw();
        updateCaret();
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
            @Override
            public void handleEvent(Event e) {
                disposed();
            }
        });
        final Listener mouseListener = new Listener() {
            @Override
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
            @Override
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
            ui.removeEditor(this);
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
            x -= target.getX();
            y -= target.getY();
            target = target.handleMouseEvent(x, y, event, this);
        }
        if (event.doit && (event.type == SWT.MouseDown)) {
            internalSetPosition(null);
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
        } else if (e.keyCode == SWT.BS && e.stateMask == 0) {
            backspaceBrick();
//        } else if (e.keyCode == SWT.END && e.stateMask == 0) {
//            navigateLast();
        } else if (e.keyCode == 'd' && e.stateMask == SWT.CTRL) {
            if (position != null) {
                position.getBrick().printDebugInfo();
            }
        }
    }

    private void warning() {
        canvas.getDisplay().beep();
    }

    private void editBrick() {
        if ((position == null) || !position.canEdit()) {
            warning();
            return;
        }
        position.edit(this);
    }

    private void insertBrick() {
        if ((position == null) || !position.canInsert()) {
            warning();
            return;
        }

        final Brick parent = position.getBrick();
        if (!(parent instanceof ContainerBrick)) {
            warning();
            return;
        }
        final TupleBrick tuple = new TupleBrick((ContainerBrick) parent, "");
        position.insert(tuple);
        tuple.attach(this);
        if (tuple.edit(this)) {
            setPosition(tuple.enter(Side.LEFT));
        } else {
            tuple.detach(this);
            position.delete(this);
        }
    }

    private void insertLineBreak() {
        if ((position == null) | !position.canInsert()) {
            warning();
            return;
        }

        final Brick parent = position.getBrick();
        if (!(parent instanceof ContainerBrick)) {
            warning();
            return;
        }
        final LineBreak lineBreak = new LineBreak((ContainerBrick) parent);
        position.insert(lineBreak);
        lineBreak.attach(this);
        revalidate(lineBreak);
        position.next();
        updatePosition();
    }

    private void deleteBrick() {
        if ((position == null) || !position.canDelete()) {
            warning();
            return;
        }
        position.delete(this);
        updateCaret();
    }

    private void backspaceBrick() {
        if ((position == null) || !position.canBackDelete()) {
            warning();
            return;
        }
        position.backDelete(this);
        updateCaret();
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
        canvasResized(true);
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

    void navigatePreceding() {
        if (position != null) {
            setPosition(position.preceding());
        }
    }

    void navigateFollowing() {
        if (position != null) {
            setPosition(position.following());
        }
    }

    void navigatePrevious() {
        if (position != null) {
            setPosition(position.previous());
        }
    }

    void navigateNext() {
        if (position != null) {
            setPosition(position.next());
        }
    }

    void navigateFirst() {
        if (position != null) {
            setPosition(position.first());
        }
    }

    void navigateLast() {
        if (position != null) {
            setPosition(position.last());
        }
    }

    void navigateUp() {
        if (position == null) {
            warning();
            return;
        }
        setPosition(position.up(Side.LEFT));
    }

    public void navigateDown() {
        if (position == null) {
            warning();
            return;
        }
        setPosition(position.up(Side.RIGHT));
    }

    void scrollToSelected() {
        if (position != null) {
            scrollTo(position.getBrick()); // TODO
        }
    }

    public void setPosition(Position position) {
        if (position == null) {
            warning();
            return;
        }
        internalSetPosition(position);
    }

    private void internalSetPosition(Position position) {
        final Position oldPosition = this.position;
        this.position = position;
        if (oldPosition != null) {
            paintOnly(oldPosition.getBrick());
        }
        if (position != null) {
            paintOnly(position.getBrick());
            updateCaret();
            mainWindow.setStatus("Path = " + getPath(position.getBrick())); // DEBUG
        }
    }

    public Position getPosition() {
        return position;
    }

    private void updatePosition() {
        setPosition(position);
    }

    public StyleChain getStyleChain(TupleBrick brick) {
        StyleChain chain = ui.getStyleChain(brick);
        if ((position != null) && (position.getBrick() == brick)) {
            chain = ui.getSelectedStyle().createChain(chain);
        }
        // if (selection instanceof LineBreak) ..?
        return chain;
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

    void updateCaret() {
        if (position != null) {
            final Rectangle rect = position.toScreen();
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
                list.addFirst(brick.getIndex() + ":" +
                        Strings.removeChar(tuple.getText(), '\n'));
            } else {
                list.addFirst(brick.getIndex() + ":" +
                        brick.getClass().getName());
            }
            brick = brick.getParent();
        }
        return Strings.join(list, " / ");
    }

    public ZenFileType getFileType() {
        return fileType;
    }

    public void setFileType(ZenFileType fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        mainWindow.setEditorFileName(fileName);
    }
}
