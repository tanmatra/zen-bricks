package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract class LabelRenderer
{
    // ================================================================== Fields

    private int x;

    private int y;

    private int width;

    private int height;

    private int textX;

    private int textY;

    protected final TupleBrick tupleBrick;

    private int ascent;

    // ============================================================ Constructors

    protected LabelRenderer(TupleBrick tupleBrick) {
        this.tupleBrick = tupleBrick;
    }

    // ================================================================= Methods
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

    protected TupleBrick getTupleBrick() {
        return tupleBrick;
    }

    protected String getText() {
        return getTupleBrick().getText();
    }

    public abstract void doLayout(Editor editor);

    public void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final int selfX = baseX + getX();
        final int selfY = baseY + getY();
        if (!clipping.intersects(selfX, selfY, getWidth(), getHeight())) {
            return;
        }
        doPaint(gc, selfX, selfY, editor);
    }

    protected abstract void doPaint(GC gc, int selfX, int selfY, Editor editor);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    public int getAscent() {
        return ascent;
    }

    protected void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public int getTextX() {
        return textX;
    }

    public void setTextX(int textX) {
        this.textX = textX;
    }

    public int getTextY() {
        return textY;
    }

    public void setTextY(int textY) {
        this.textY = textY;
    }

    public abstract void invalidate();

    public Rectangle toScreen() {
        final Rectangle rect = getTupleBrick().toScreen();
        rect.x += x;
        rect.y += y;
        rect.width = width;
        rect.height = height;
        return rect;
    }
}
