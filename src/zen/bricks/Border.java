package zen.bricks;

import java.util.Properties;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract class Border
{
    protected Color color;

    protected Border(UI ui, Properties properties) {
        color = ui.parseColor(properties, "border.color");
    }

    public void dispose() {
        if (color != null) {
            color.dispose();
        }
    }

    public void paint(GC gc, int x, int y, Brick brick, Rectangle clipping) {
        gc.setForeground(color);
        paintBorder(gc, x, y, brick, clipping);
    }

    protected abstract void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping);

}
