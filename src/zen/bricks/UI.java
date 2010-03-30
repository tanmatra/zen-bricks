package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class UI
{
    // ============================================================ Class Fields

    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // =========================================================== Class Methods

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

    static Properties loadProperties(String filePath) throws IOException {
        return loadProperties(new Properties(), filePath);
    }

    static Properties loadProperties(String filePath, String defaultsFilePath)
            throws IOException
    {
        final Properties defaults = loadProperties(defaultsFilePath);
        final Properties properties = new Properties(defaults);
        return loadProperties(properties, filePath);
    }

// ================================================================== Fields

    private final Device device;
    private GC savedGC;

    private int antialias;
    private Color backgroundColor;
    private Border border;
    private int brickPaddingLeft;
    private int brickPaddingTop;
    private int brickPaddingRight;
    private int brickPaddingBottom;
    private Color canvasBackgroundColor;
    private Font font;
    FontData fontData;
    private FontMetrics fontMetrics;
    private int lineSpacing;
    private Color textBackgroundColor;
    private Color textColor;
    private int spacing;
    private int textMarginLeft;
    private int textMarginTop;

    // ============================================================ Constructors

    public UI(Device device, Properties properties) {
        this.device = device;
        savedGC = new GC(device);
        init(properties);
        savedGC.setAntialias(antialias);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
    }

    public UI(Device device, String styleFileName) throws IOException {
        this(device, loadProperties(styleFileName));
    }

    // ================================================================= Methods

    /**
     * @throws IllegalArgumentException
     */
    void init(Properties props) {
        antialias = parseState(props, "antialias");
        initBorder(props);
        backgroundColor = parseColor(props, "background.color");
        brickPaddingLeft = parseInt(props, "brick.padding.left");
        brickPaddingTop = parseInt(props, "brick.padding.top");
        brickPaddingRight = parseInt(props, "brick.padding.right");
        brickPaddingBottom = parseInt(props, "brick.padding.bottom");
        canvasBackgroundColor = parseColor(props, "canvas.background.color");
        fontData = parseFont(props, "font");
        font = new Font(device, fontData);
        lineSpacing = parseInt(props, "line.spacing");
        spacing = parseInt(props, "spacing");
        textBackgroundColor = parseColor(props, "text.background.color");
        textColor = parseColor(props, "text.color");
        textMarginLeft = parseInt(props, "text.margin.left");
        textMarginTop = parseInt(props, "text.margin.top");
    }

    void dispose() {
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (border != null) {
            border.dispose();
            border = null;
        }
        if (textColor != null) {
            textColor.dispose();
            textColor = null;
        }
        if (backgroundColor != null) {
            backgroundColor.dispose();
            backgroundColor = null;
        }
        if (textBackgroundColor != null) {
            textBackgroundColor.dispose();
            textBackgroundColor = null;
        }
        if (savedGC != null) {
            savedGC.dispose();
            savedGC = null;
        }
    }

    private void initBorder(Properties props) {
        final String borderClassName = props.getProperty("border.class");
        final Class<? extends Border> borderClass;
        try {
            borderClass =
                    (Class<? extends Border>) Class.forName(borderClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        try {
            border = borderClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        border.init(this, props);
    }

    public void applyTo(Editor editor) {
        editor.canvas.setFont(font);
        // what else?
    }

    public Color parseColor(Properties properties, String key) {
        final String value = properties.getProperty(key);
        if ("none".equals(value) || "transparent".equals(value)) {
            return null;
        }
        return ColorUtil.parse(getDevice(), value);
    }

    public int parseInt(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return Integer.parseInt(value);
    }

    private int parseState(Properties properties, String key) {
        final String value = properties.getProperty(key);
        if ("default".equals(value)) {
            return SWT.DEFAULT;
        } else if ("on".equals(value)) {
            return SWT.ON;
        } else if ("off".equals(value)) {
            return SWT.OFF;
        } else {
            throw new IllegalArgumentException("Wrong state: " + value);
        }
    }

    private FontData parseFont(Properties properties, String key) {
        final String value = properties.getProperty(key);
        String name;
        float height = 8.0f;
        int style = SWT.NORMAL;
        StringTokenizer tokenizer;
        if (value.charAt(0) == '"') {
            final int p = value.indexOf('"', 1);
            name = value.substring(1, p);
            tokenizer = new StringTokenizer(value.substring(p + 1));
        } else {
            tokenizer = new StringTokenizer(value);
            name = tokenizer.nextToken();
        }
        String heightStr = tokenizer.nextToken();
        height = Float.parseFloat(heightStr);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if ("bold".equals(token)) {
                style |= SWT.BOLD;
            } else if ("italic".equals(token)) {
                style |= SWT.ITALIC;
            }
        }
        final FontData fontData = new FontData(name, (int) height, style);
        if (Math.floor(height) != height) {
            fontData.height = height;
        }
        return fontData;
    }

    void changeFont(FontData data) {
        this.fontData = data;
        if (font != null) {
            font.dispose();
        }
        font = new Font(device, data);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
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
        int flags = TEXT_FLAGS;
        if (textBackgroundColor != null) {
            screenGC.setBackground(textBackgroundColor);
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }
        screenGC.drawText(text, x, y, flags);
    }

    public Color getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    /**
     * Paint brick background and border.
     */
    public void paintBackground(GC gc, Brick brick, int baseX, int baseY,
            Rectangle clipping)
    {
        gc.setBackground(getBackgroundColor());
        gc.fillRectangle(baseX, baseY, brick.getWidth(), brick.getHeight());

        border.paint(gc, baseX, baseY, brick, clipping);
    }

    public int getBrickPaddingTop() {
        return brickPaddingTop;
    }

    public int getBrickPaddingLeft() {
        return brickPaddingLeft;
    }

    public int getBrickPaddingRight() {
        return brickPaddingRight;
    }

    public int getBrickPaddingBottom() {
        return brickPaddingBottom;
    }

    public int getTextMarginLeft() {
        return textMarginLeft;
    }

    public int getTextMarginTop() {
        return textMarginTop;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void preparePaint(GC gc) {
        gc.setAntialias(antialias);
    }
}
