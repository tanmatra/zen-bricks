package zen.bricks.borders;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.Brick;
import zen.bricks.UI;

public class SimpleBorder extends Border
{
    public static class Factory extends BorderFactory
    {
        public Border createBorder(UI ui) {
            return new SimpleBorder(this, ui);
        }

        public String getName() {
            return "simple";
        }

        public String getTitle() {
            return "Simple";
        }
    }

    protected SimpleBorder(BorderFactory factory, UI ui) {
        super(factory, ui);
    }

    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping)
    {
        gc.drawRectangle(x, y, brick.getWidth() - 1, brick.getHeight() - 1);
    }
}
