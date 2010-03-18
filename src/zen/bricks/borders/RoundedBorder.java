package zen.bricks.borders;

import java.util.Properties;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import zen.bricks.Border;
import zen.bricks.Brick;
import zen.bricks.UI;

public class RoundedBorder extends Border
{
    private int arcSize;

    public void init(UI ui, Properties properties) {
        super.init(ui, properties);
        arcSize = ui.parseInt(properties, "border.arc.size");
    }

    protected void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping)
    {
        gc.drawRoundRectangle(x, y,
                brick.getWidth() - 1, brick.getHeight() - 1,
                arcSize, arcSize);
    }
}
