package zen.bricks;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ColorBrick extends Brick
{
    private final RGB fore;
    private final RGB back;
    private Color foreColor;
    private Color backColor;

    public ColorBrick(TextBrick parent, int width, int height,
            RGB fore, RGB back)
    {
        super(parent);
        this.width = width;
        this.height = height;
        this.fore = fore;
        this.back = back;
        ascent = height;
    }

    void realize(Editor editor) {
        final Display device = editor.getCanvas().getDisplay();
        foreColor = new Color(device, fore);
        backColor = new Color(device, back);
    }

    void dispose() {
        foreColor.dispose();
        backColor.dispose();
        super.dispose();
    }

    void paint(GC gc, int baseX, int baseY, UI ui, Rectangle clipping) {
//        System.out.println("base x, y: " + baseX + ", " + baseY);
        gc.setForeground(foreColor);
        gc.setBackground(backColor);
        gc.fillRoundRectangle(baseX, baseY, width, height, 6, 6);
        gc.drawRoundRectangle(baseX, baseY, width - 1, height - 1, 6, 6);
//        gc.fillRectangle(baseX, baseY, width, height);
    }
}
