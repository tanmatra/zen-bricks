package zen.bricks;

import java.util.Properties;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

public abstract class Border
{
    protected Color color;

    protected Border(UI ui, Properties properties) {
        final String string = properties.getProperty("border.color");
        final RGB rgb = ColorUtil.parse(ui.getDevice(), string);
        color = new Color(ui.getDevice(), rgb);
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
