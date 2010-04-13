package zen.bricks.borders;

import java.util.Properties;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Border;
import zen.bricks.Brick;
import zen.bricks.UI;

public class SimpleBorder extends Border
{
    public SimpleBorder(UI ui, Properties properties) {
        super(ui, properties);
    }

    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping)
    {
        gc.drawRectangle(x, y, brick.getWidth() - 1, brick.getHeight() - 1);
    }
}
