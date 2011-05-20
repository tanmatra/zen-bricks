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
    boolean valid;

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
    void realize(Editor editor) {
    }

    void dispose() {
    }

    public boolean contains(int x, int y) {
        return (x >= this.x) && (y >= this.y)
                && (x < (this.x + width)) && (y < (this.y + height));
    }

    public boolean intersects(Rectangle rect, int baseX, int baseY) {
        return rect.intersects(x + baseX, y + baseY, width, height);
    }

    /**
     * Paints only if intersects with clipping.
     */
    public void repaint(GC gc, int baseX, int baseY, Rectangle clipping,
                        Editor editor)
    {
        final int brickX = baseX + x;
        final int brickY = baseY + y;
        if (clipping.intersects(brickX, brickY, width, height)) {
            paint(gc, brickX, brickY, clipping, editor);
        }
    }

    public abstract void paint(GC gc, int baseX, int baseY, Rectangle clipping,
                               Editor editor);

    public void invalidate() {
        Brick brick = this;
        do {
            brick.valid = false;
            brick = brick.parent;
        } while (brick != null);
    }

    void validate(Editor editor) {
        if (valid) {
            return;
        }
        doLayout(editor);
        valid = true;
    }

    protected abstract void doLayout(Editor editor);

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
     */
    public Brick mouseEvent(int mouseX, int mouseY, Event event, Editor editor) {
        // debugMouseEvent(event);
        if (event.type == SWT.MouseDown) {
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
        final int prevIndex = index - 1;
        if (!parent.isValidIndex(prevIndex)) {
            return null;
        }
        return parent.getChild(prevIndex);
    }

    public Brick getNextSibling() {
        final int nextIndex = index + 1;
        if (!parent.isValidIndex(nextIndex)) {
            return null;
        }
        return parent.getChild(nextIndex);
    }
}
