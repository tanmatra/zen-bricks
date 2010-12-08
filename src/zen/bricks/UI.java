package zen.bricks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

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
    private int textAntialias;

    private TupleStyle basicStyle;
    private TupleStyle listStyle;
    private TupleStyle selectedStyle;

    private StyleChain basicChain;
    private StyleChain listChain;
    private ArrayList<TupleLayout> tupleLayouts;
    private ArrayList<BorderFactory> borderFactories;
    public final Properties properties; // FIXME

    // ============================================================ Constructors

    public UI(Device device, Properties properties) throws Exception {
        this.device = device;
        this.properties = properties;
        savedGC = new GC(device);
        try {
            init(properties);
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

        canvasBackgroundColor = parseColor(props, "canvas.background.color");

        final ServiceLoader<TupleLayout> layoutsLoader =
                ServiceLoader.load(TupleLayout.class);
        tupleLayouts = new ArrayList<TupleLayout>();
        for (final TupleLayout layout : layoutsLoader) {
            tupleLayouts.add(layout);
        }

        final ServiceLoader<BorderFactory> bordersLoader =
                ServiceLoader.load(BorderFactory.class);
        borderFactories = new ArrayList<BorderFactory>();
        for (final BorderFactory borderFactory : bordersLoader) {
            borderFactories.add(borderFactory);
        }
        initBorder(props);

        textAntialias = parseState(props, "text.antialias");

        basicStyle = new TupleStyle(this, "Basic", props, "basic_style");
        basicStyle.setBorder(border); // FIXME
        basicStyle.setTopLevel(true);
        listStyle = new TupleStyle(this, "List", props, "list_style");

        selectedStyle = new TupleStyle(
                this, "Selected", props, "selected_style");

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

    private void initBorder(Properties props) throws Exception {
//        final String borderClassName = props.getProperty("border.class");
//        final Class<Border> borderClass =
//                (Class<Border>) Class.forName(borderClassName);
//        final Constructor<Border> constr =
//                borderClass.getConstructor(UI.class, Properties.class);
//        if (!Border.class.isAssignableFrom(borderClass)) {
//            throw new ClassNotFoundException("Not a subclass of Border");
//        }
//        border = constr.newInstance(this, props);

        for (final BorderFactory factory : borderFactories) {
            if (factory.getName().equals("simple")) {
                border = factory.createBorder(this);
                border.init(props);
                return;
            }
        }
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
        final StyleChain styleChain = getStyleChain(brick, editor);
        final TupleStyle style = TupleStyle.LAYOUT.find(styleChain);
        final TupleLayout layout = style.getLayout();
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

    public List<TupleLayout> getTupleLayouts() {
        return tupleLayouts;
    }

    public List<BorderFactory> getBorderFactories() {
        return borderFactories;
    }
}
