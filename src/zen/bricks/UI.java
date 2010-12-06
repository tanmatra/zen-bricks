package zen.bricks;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;

public class UI
{
    // ================================================================== Fields

    private final Device device;
    private GC savedGC;

    private Boolean advanced;
    private int antialias;
    private Border border;
    private Color canvasBackgroundColor;
    private TupleLayout layout;
    private int textAntialias;

    private TupleStyle basicStyle;
    private TupleStyle listStyle;
    private TupleStyle selectedStyle;

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
        } catch (final Exception e) {
            dispose();
            throw e;
        }
    }

    void init(Properties props) throws Exception {
        advanced = parseBoolean(props, "advanced");
        antialias = parseState(props, "antialias");
        initBorder(props);
        canvasBackgroundColor = parseColor(props, "canvas.background.color");
        initLayout(props);
        textAntialias = parseState(props, "text.antialias");

        basicStyle = new TupleStyle("Basic", device, props, "basic_style");
        basicStyle.setTopLevel(true);
        listStyle = new TupleStyle("List", device, props, "list_style");

        selectedStyle = new TupleStyle(
                "Selected", device, props, "selected_style");

        basicChain = basicStyle.createChain(null);
        listChain = listStyle.createChain(basicChain);
    }

    void dispose() {
        if (border != null) {
            border.dispose();
            border = null;
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

    /**
     * @param editor
     */
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

    private Color parseColor(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return new Color(device, ColorUtil.parse(device, value));
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

    Device getDevice() {
        return device;
    }

    GC getGC() {
        return savedGC;
    }

    public void layout(TupleBrick brick, Editor editor) {
        layout.doLayout(brick, editor);
    }

    public Color getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    public TupleStyle getBasicStyle() {
        return basicStyle;
    }

    public void preparePaint(GC gc) {
        if (advanced != null) {
            gc.setAdvanced(advanced);
        }
        gc.setAntialias(antialias);
        gc.setTextAntialias(textAntialias);
    }

    public StyleChain getStyleChain(TupleBrick brick, Editor editor) {
        StyleChain chain;
        if (brick.isList()) {
            chain = listChain;
        } else {
            chain = basicChain;
        }
        if (brick == editor.getSelection()) {
            chain = selectedStyle.createChain(chain);
        }
        return chain;
    }

    public List<? extends BrickStyle> getBrickStyles() {
        return Arrays.asList(basicStyle, listStyle, selectedStyle);
    }

    public Border getBorder() {
        return border;
    }
}
