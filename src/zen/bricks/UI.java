package zen.bricks;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class UI
{
    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    private GC gc;
    private Color borderColor;
    private Color textColor;
    private Color backgroundColor;
    private Color textBackColor;
    private FontMetrics fontMetrics;

    public UI(Canvas canvas) {
        gc = new GC(canvas);
        gc.setAntialias(SWT.ON);
        fontMetrics = gc.getFontMetrics();

        final Display display = canvas.getDisplay();
        borderColor = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        textColor = display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
        backgroundColor = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        textBackColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    }

    GC getGC() {
        return gc;
    }

    Color getBackgroundColor() {
        return backgroundColor;
    }

    Color getBorderColor() {
        return borderColor;
    }

    Color getTextColor() {
        return textColor;
    }

    Color getTextBackColor() {
        return textBackColor;
    }

    void dispose() {
        gc.dispose();
    }

    FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    int getTextAscent() {
        return fontMetrics.getAscent();
    }

    public Point getTextExtent(String text) {
        return gc.textExtent(text, TEXT_FLAGS);
    }

    public void paintText(GC screenGC, int x, int y, String text) {
        screenGC.setForeground(getTextColor());
        screenGC.setBackground(getTextBackColor());
        screenGC.drawText(text, x, y, TEXT_FLAGS);
    }
}
