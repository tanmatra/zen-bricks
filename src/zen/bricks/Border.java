package zen.bricks;

import java.util.Properties;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

public abstract class Border
{
    protected final UI ui;
    protected Color color;
    private BorderFactory factory;

    protected Border(BorderFactory factory, UI ui) {
        this.factory = factory;
        this.ui = ui;
    }

    protected Border(UI ui, Properties properties) {
        this.ui = ui;
        init(properties);
    }

    public void init(Properties properties) {
        final String string = properties.getProperty("border.color");
        final RGB rgb = ColorUtil.parse(ui.getDevice(), string);
        color = new Color(ui.getDevice(), rgb);
    }

    public void dispose() {
        if (color != null) {
            color.dispose();
            color = null;
        }
    }

    public void paint(GC gc, int x, int y, Brick brick, Rectangle clipping) {
        gc.setForeground(color);
        paintBorder(gc, x, y, brick, clipping);
    }

    protected abstract void paintBorder(GC gc, int x, int y, Brick brick,
            Rectangle clipping);

    public RGB getColor() {
        return color.getRGB();
    }

    public void setColor(RGB rgb) {
        this.color = new Color(ui.getDevice(), rgb);
    }

    public BorderFactory getFactory() {
        return factory;
    }
}
