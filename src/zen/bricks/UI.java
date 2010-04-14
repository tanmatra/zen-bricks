package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
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

    private Boolean advanced;
    private int antialias;
    private Color backgroundColor;
    private Border border;
    private final Margin brickPadding = new Margin();
    private Color canvasBackgroundColor;
    private Font font;
    FontData fontData;
    private FontMetrics fontMetrics;
    private TupleLayout layout;
    private int lineSpacing;
    private Color textBackgroundColor;
    private Color textColor;
    private int spacing;
    private int textAntialias;
    private int textAscent;
    private final Margin textMargin = new Margin();

    // ============================================================ Constructors

    public UI(Device device, Properties properties) throws Exception {
        this.device = device;
        savedGC = new GC(device);
        try {
            init(properties);
            if (layout == null) {
                layout = new SimpleLayout(this);
            }
            savedGC.setAntialias(antialias);
            savedGC.setFont(font);
            fontMetrics = savedGC.getFontMetrics();
            textAscent = fontMetrics.getAscent() + fontMetrics.getLeading();
        } catch (Exception e) {
            dispose();
            throw e;
        }
    }

    public UI(Device device, String themeFileName) throws Exception {
        this(device, loadProperties(themeFileName));
    }

    // ================================================================= Methods

    void init(Properties props) throws Exception {
        advanced = parseBoolean(props, "advanced");
        antialias = parseState(props, "antialias");
        initBorder(props);
        backgroundColor = parseColor(props, "background.color");
        brickPadding.parse(props, "brick.padding");
        canvasBackgroundColor = parseColor(props, "canvas.background.color");
        fontData = parseFont(props, "font");
        font = new Font(device, fontData);
        initLayout(props);
        lineSpacing = parseInt(props, "line.spacing");
        spacing = parseInt(props, "spacing");
        textAntialias = parseState(props, "text.antialias");
        textBackgroundColor = parseColor(props, "text.background.color");
        textColor = parseColor(props, "text.color");
        textMargin.parse(props, "text.margin");
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

    private void initLayout(Properties props) throws Exception {
        layout = null;
        final String layoutClassName = props.getProperty("layout.class");
        if (layoutClassName == null) {
            return;
        }
        final Class<TupleLayout> layoutClass =
                (Class<TupleLayout>) Class.forName(layoutClassName);
        if (!TupleLayout.class.isAssignableFrom(layoutClass)) {
            throw new ClassNotFoundException("Not a subclass of TupleLayout");
        }
        final Constructor<TupleLayout> constr =
                layoutClass.getConstructor(UI.class);
        layout = constr.newInstance(this);
    }

    private void initBorder(Properties props) throws Exception {
        final String borderClassName = props.getProperty("border.class");
        final Class<Border> borderClass =
                (Class<Border>) Class.forName(borderClassName);
        final Constructor<Border> constr =
                borderClass.getConstructor(UI.class, Properties.class);
        if (!Border.class.isAssignableFrom(borderClass)) {
            throw new ClassNotFoundException("Not a subclass of Border");
        }
        border = constr.newInstance(this, props);
    }

    public void applyTo(Editor editor) {
        editor.canvas.setFont(font);
        // what else?
    }

    public Boolean parseBoolean(Properties properties, String key) {
        final String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
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
        if ((value == null) || "default".equals(value)) {
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
        return textAscent;
    }

    public Point getTextExtent(String text) {
        return savedGC.textExtent(text, TEXT_FLAGS);
    }

    public void layout(TextBrick brick) {
        layout.doLayout(brick);
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

    public Margin getBrickPadding() {
        return brickPadding;
    }

    public Margin getTextMargin() {
        return textMargin;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void preparePaint(GC gc) {
        if (advanced != null) {
            gc.setAdvanced(advanced);
        }
        gc.setAntialias(antialias);
        gc.setTextAntialias(textAntialias);
    }
}
