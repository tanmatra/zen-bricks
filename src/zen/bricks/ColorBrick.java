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

    public ColorBrick(TupleBrick parent, int width, int height, RGB fore, RGB back) {
        super(parent);
        resize(width, height);
        this.fore = fore;
        this.back = back;
        setAscent(height);
    }

    @Override
    public void attach(Editor editor) {
        final Display device = editor.getCanvas().getDisplay();
        foreColor = new Color(device, fore);
        backColor = new Color(device, back);
    }

    @Override
    public void detach(Editor editor) {
        foreColor.dispose();
        backColor.dispose();
        super.detach(editor);
    }

    @Override
    public void paint(GC gc, int baseX, int baseY, Rectangle clipping, Editor editor) {
        gc.setForeground(foreColor);
        gc.setBackground(backColor);
        gc.fillRoundRectangle(baseX, baseY, getWidth(), getHeight(), 6, 6);
        gc.drawRoundRectangle(baseX, baseY, getWidth() - 1, getHeight() - 1, 6, 6);
    }
}
