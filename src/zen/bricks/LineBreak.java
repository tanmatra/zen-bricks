package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class LineBreak extends Brick
{
    public LineBreak(ContainerBrick parent) {
        super(parent);
        resize(2, 4);
    }

    protected void paint(GC gc, int baseX, int baseY, Rectangle clipping,
            Editor editor)
    {
        final Device device = gc.getDevice();
        gc.setBackground(device.getSystemColor(SWT.COLOR_CYAN));
        gc.fillRectangle(baseX, baseY, getWidth(), getHeight());
//        gc.setForeground(device.getSystemColor(SWT.COLOR_GRAY));
//        gc.drawRectangle(baseX, baseY + 1, getWidth() - 1, getHeight() - 3);
//        gc.setBackground(device.getSystemColor(SWT.COLOR_WHITE));
//        gc.fillRectangle(baseX + 1, baseY + 2, getWidth() - 2, getHeight() - 4);
    }
}
