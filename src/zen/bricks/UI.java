package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

public class UI
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================== Fields

    private GC gc;
    private Color borderColor;
    private Color textColor;
    private Color backgroundColor;
    private Color textBackgroundColor;
    private Color canvasBackgroundColor;
    private FontMetrics fontMetrics;

    // ============================================================ Constructors

    public UI(Canvas canvas, Properties properties) {
        gc = new GC(canvas);
        init(properties);
        gc.setAntialias(SWT.ON);
        fontMetrics = gc.getFontMetrics();
    }

    public UI(Canvas canvas, String styleFileName) throws IOException {
        this(canvas, loadProperties(styleFileName));
    }

    // ================================================================= Methods

    void init(Properties props) {
        borderColor = parseColor(props, "border.color");
        textColor = parseColor(props, "text.color");
        backgroundColor = parseColor(props, "background.color");
        textBackgroundColor = parseColor(props, "text.background.color");
        canvasBackgroundColor = parseColor(props, "canvas.background.color");
    }

    void dispose() {
        borderColor.dispose();
        textColor.dispose();
        backgroundColor.dispose();
        textBackgroundColor.dispose();
        gc.dispose();
    }

    private Color parseColor(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return ColorUtil.parse(getDevice(), value);
    }

    private Device getDevice() {
        return gc.getDevice();
    }

    public void load(InputStream inputStream) throws IOException {
        final Properties props = new Properties();
        props.load(inputStream);
        init(props);
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

    Color getTextBackgroundColor() {
        return textBackgroundColor;
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
        screenGC.setBackground(getTextBackgroundColor());
        screenGC.drawText(text, x, y, TEXT_FLAGS);
    }

    public Color getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    static Properties loadProperties(String filePath)
            throws IOException
    {
        final Properties properties;
        final InputStream inputStream = new FileInputStream(filePath);
        try {
            properties = new Properties();
            properties.load(inputStream);
            return properties;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
