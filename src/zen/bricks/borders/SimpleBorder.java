package zen.bricks.borders;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Border;
import zen.bricks.Brick;

public class SimpleBorder extends Border
{
    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping)
    {
        gc.drawRectangle(x, y, brick.getWidth() - 1, brick.getHeight() - 1);
    }
}
