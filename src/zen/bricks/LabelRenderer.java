package zen.bricks;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract class LabelRenderer
{
    // ================================================================== Fields

    protected final Point textPosition = new Point(0, 0);

    protected Point textExtent;

    // ================================================================= Methods
    /**
     * @param editor
     */
    public void init(Editor editor) {
    }

    public void dispose() {
    }

    public abstract Point calculateSize(TupleBrick tuple, StyleChain chain);

    public abstract void paint(TupleBrick tuple, GC gc, int baseX, int baseY,
            Rectangle clipping, StyleChain chain);

    public Point getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(int textX, int textY) {
        textPosition.x = textX;
        textPosition.y = textY;
    }

    public Point getTextExtent() {
        return textExtent;
    }
}
