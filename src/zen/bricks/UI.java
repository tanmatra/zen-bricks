package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class UI
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================== Fields

    private final Device device;
    private final GC savedGC;
    private Font font;
    private FontMetrics fontMetrics;
    private Color borderColor;
    private Color textColor;
    private Color backgroundColor;
    private Color textBackgroundColor;
    private Color canvasBackgroundColor;

    // ============================================================ Constructors

    public UI(Device device, Properties properties) {
        this.device = device;
        savedGC = new GC(device);
        init(properties);
        font = new Font(getDevice(), "Georgia", 9, SWT.NORMAL);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
        savedGC.setAntialias(SWT.ON);
    }

    public UI(Device device, String styleFileName) throws IOException {
        this(device, loadProperties(styleFileName));
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
        font.dispose();
        borderColor.dispose();
        textColor.dispose();
        backgroundColor.dispose();
        textBackgroundColor.dispose();
        savedGC.dispose();
    }

    public void applyTo(Editor editor) {
        editor.canvas.setFont(font);
        // what else?
    }

    private Color parseColor(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return ColorUtil.parse(getDevice(), value);
    }

    private Device getDevice() {
        return device;
    }

    GC getGC() {
        return savedGC;
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
        return savedGC.textExtent(text, TEXT_FLAGS);
    }

    public void paintText(GC screenGC, int x, int y, String text) {
        screenGC.setForeground(getTextColor());
        screenGC.setBackground(getTextBackgroundColor());
        screenGC.drawText(text, x, y, TEXT_FLAGS);
    }

    public Color getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    static Properties loadProperties(Properties properties, String filePath)
            throws IOException
    {
        final InputStream inputStream = new FileInputStream(filePath);
        try {
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

    static Properties loadProperties(String filePath)
            throws IOException
    {
        return loadProperties(new Properties(), filePath);
    }

    static Properties loadProperties(String filePath, String defaultsFilePath)
            throws IOException
    {
        final Properties defaults = loadProperties(defaultsFilePath);
        final Properties properties = new Properties(defaults);
        return loadProperties(properties, filePath);
    }
}
