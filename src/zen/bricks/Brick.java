package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public abstract class Brick
{
    // ================================================================== Fields

    ContainerBrick parent;
    int index;
    int x;
    int y;
    private int width;
    private int height;
    int ascent;

    // ============================================================ Constructors

    protected Brick(ContainerBrick parent) {
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    // ================================================================= Methods

    public ContainerBrick getParent() {
        return parent;
    }

    /**
     * @param editor
     */
    public void attach(Editor editor) {
    }

    /**
     * @param editor
     */
    public void detach(Editor editor) {
    }

    /**
     * Note: this.x and this.y is not handled by brick itself.
     *
     * @param x coordintate relative to this brick
     * @param y coordintate relative to this brick
     */
    public boolean contains(int x, int y) {
        return (x >= 0) && (y >= 0) && (x < width) && (y < height);
    }

    public boolean intersects(Rectangle rect, int baseX, int baseY) {
        return rect.intersects(x + baseX, y + baseY, width, height);
    }

    /**
     * Paints only if intersects with clipping.
     *
     * @param parentX absolute canvas-based
     * @param parentY absolute canvas-based
     * @param clipping absolute canvas-based
     */
    public void repaint(GC gc, int parentX, int parentY, Rectangle clipping,
                        Editor editor)
    {
        final int brickX = parentX + x;
        final int brickY = parentY + y;
        if (clipping.intersects(brickX, brickY, width, height)) {
            paint(gc, brickX, brickY, clipping, editor);
        }
    }

    /**
     * Paint this brick in canvas.
     * <p>
     * {@link #x} and {@link #y} should not be taken into account in this method
     *
     * @param gc graphic context
     * @param baseX absolute canvas-based X coordinate
     * @param baseY absolute canvas-based Y coordinate
     * @param clipping clipping area relative to canvas
     * @param editor editor
     */
    protected abstract void paint(GC gc, int baseX, int baseY,
            Rectangle clipping, Editor editor);

    /**
     * @return <code>true</code> if brick has really changed its size
     */
    protected abstract boolean doLayout(Editor editor);

    /**
     * @param width
     * @param height
     * @return <code>true</code> if brick has really changed its size
     */
    public boolean resize(int width, int height) {
        if ((this.width != width) || (this.height != height)) {
            this.width  = width;
            this.height = height;
            return true;
        } else {
            return false;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRight() {
        return x + width;
    }

    public int getBottom() {
        return y + height;
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param event
     * @param editor
     *
     * @return another brick to what event must be propagated
     *         or <code>null</code> to stop propagation
     */
    public Brick handleMouseEvent(int mouseX, int mouseY, Event event,
                                  Editor editor)
    {
        // debugMouseEvent(event);
        if (!contains(mouseX, mouseY)) {
            return null;
        }
        if (event.type == SWT.MouseDown) {
            event.doit = false;
            editor.setSelection(this);
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void debugMouseEvent(Event event) {
        final String type;
        switch (event.type) {
            case SWT.MouseDoubleClick: type = "MouseDoubleClick"; break;
            case SWT.MouseDown: type = "MouseDown"; break;
            case SWT.MouseHover: type = "MouseHover"; break;
            case SWT.MouseMove: type = "MouseMove"; break;
            case SWT.MouseUp: type = "MouseUp"; break;
            default: type = "???"; break;
        }
        System.out.println("\nmouse event: " + event
                + "\ntype: " + type
                + "\non: " + this);
    }

    public Rectangle toScreen() {
        int px = 0;
        int py = 0;
        Brick brick = this;
        while (brick != null) {
            px += brick.x;
            py += brick.y;
            brick = brick.getParent();
        }
        return new Rectangle(px, py, width, height);
    }

    public Brick getFirstChild() {
        return null;
    }

    public Brick getLastDescendantOrSelf() {
        return this;
    }

    public Brick getPreviousSibling() {
        if (parent == null) {
            return null;
        }
        final int prevIndex = index - 1;
        if (!parent.isValidIndex(prevIndex)) {
            return null;
        }
        return parent.getChild(prevIndex);
    }

    public Brick getNextSibling() {
        if (parent == null) {
            return null;
        }
        final int nextIndex = index + 1;
        if (!parent.isValidIndex(nextIndex)) {
            return null;
        }
        return parent.getChild(nextIndex);
    }
}
