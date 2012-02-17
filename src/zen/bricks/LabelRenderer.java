package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class LabelRenderer
{
    // ================================================================== Fields

    protected final Point textPosition = new Point(0, 0);

    protected Point textExtent;

    protected final TupleBrick tupleBrick;

    // ============================================================ Constructors

    protected LabelRenderer(TupleBrick tupleBrick) {
        this.tupleBrick = tupleBrick;
    }

    // ================================================================= Methods
    /**
     * @param editor
     */
    public void init(Editor editor) {
    }

    public void dispose() {
    }

    protected TupleBrick getTupleBrick() {
        return tupleBrick;
    }

    protected String getText() {
        return getTupleBrick().getText();
    }

    public abstract void doLayout(Editor editor);

    public abstract void paint(GC gc, int baseX, int baseY,
            Rectangle clipping, Editor editor);

    public int getX() {
        return textPosition.x;
    }

    public int getY() {
        return textPosition.y;
    }

    public void setX(int x) {
        textPosition.x = x;
    }

    public void setY(int y) {
        textPosition.y = y;
    }

    public int getWidth() {
        return textExtent.x;
    }

    public int getHeight() {
        return textExtent.y;
    }

    public abstract void invalidate();
}
