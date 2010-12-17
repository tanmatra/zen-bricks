package zen.bricks.properties;

import org.eclipse.swt.graphics.Color;

public class TransparentColor
{
    private Color color;

    public TransparentColor() {
    }

    public TransparentColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void dispose() {
        if (color != null) {
            color.dispose();
            color = null;
        }
    }
}
