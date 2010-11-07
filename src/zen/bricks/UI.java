package zen.bricks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class UI
{
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
    private TupleLayout layout;
    private int lineSpacing;
    private int spacing;
    private int textAntialias;
    private final Margin textMargin = new Margin();

    private TextStyle basicStyle;
    private TextStyle listStyle;

    private StyleChain basicChain;
    private StyleChain listChain;

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
            // TODO: pass antialias to all text styles
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
        initLayout(props);
        lineSpacing = parseInt(props, "line.spacing");
        spacing = parseInt(props, "spacing");
        textAntialias = parseState(props, "text.antialias");
        textMargin.parse(props, "text.margin");

        basicStyle = new TextStyle("Basic", device, props, "text");
        listStyle = new TextStyle("List", device, props, "list.text");

        basicChain = new StyleChain(basicStyle, null);
        listChain = new StyleChain(listStyle, basicChain);
    }

    void dispose() {
        if (border != null) {
            border.dispose();
            border = null;
        }
        if (backgroundColor != null) {
            backgroundColor.dispose();
            backgroundColor = null;
        }
        if (savedGC != null) {
            savedGC.dispose();
            savedGC = null;
        }
        if (basicStyle != null) {
            basicStyle.dispose();
            basicStyle = null;
        }
        if (listStyle != null) {
            listStyle.dispose();
            listStyle = null;
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
        // what here ???
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

    void changeBasicFont(FontData[] fontList) {
        basicStyle.setFont(fontList);
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

    public void layout(TextBrick brick) {
        layout.doLayout(brick);
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

    public TextStyle getBasicStyle() {
        return basicStyle;
    }

    public void preparePaint(GC gc) {
        if (advanced != null) {
            gc.setAdvanced(advanced);
        }
        gc.setAntialias(antialias);
        gc.setTextAntialias(textAntialias);
    }

    public StyleChain getStyleChain(TextBrick brick) {
        if (!brick.isAtom()) {
            return listChain;
        } else {
            return basicChain;
        }
    }

    public List<TextStyle> getTextStyles() {
        return Arrays.asList(basicStyle, listStyle);
    }
}
