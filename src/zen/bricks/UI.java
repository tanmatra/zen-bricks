package zen.bricks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

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

    // ============================================================ Constructors

    public UI(Device device, Preferences preferences) throws Exception {
        this.device = device;
        savedGC = new GC(device);
        try {
            init(preferences);
            savedGC.setAntialias(antialias);
            // TODO: pass antialias to all text styles
        } catch (final Exception e) {
            dispose();
            throw e;
        }
    }

    // ================================================================= Methods

    void init(Preferences prefs) throws Exception {
        advanced = parseBoolean(prefs, "advanced");
        antialias = parseState(prefs, "antialias");

        canvasBackgroundColor = parseColor(prefs, "canvasBackgroundColor");

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

        textAntialias = parseState(prefs, "textAntialias");

        final Preferences stylesNode = prefs.node("styles");
        basicStyle = new TupleStyle(this, "Basic", stylesNode.node("basic"));
        basicStyle.setTopLevel(true);
        listStyle = new TupleStyle(this, "List", stylesNode.node("list"));
        selectedStyle =
                new TupleStyle(this, "Selected", stylesNode.node("selected"));

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

    /**
     * @param editor
     */
    public void applyTo(Editor editor) {
        // what here ???
    }

    public Boolean parseBoolean(Preferences preferences, String key) {
        final String value = preferences.get(key, null);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    private Color parseColor(Preferences preferences, String key) {
        final String value = preferences.get(key, null);
        return new Color(device, ColorUtil.parse(device, value));
    }

    public int parseInt(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return Integer.parseInt(value);
    }

    private int parseState(Preferences preferences, String key) {
        final String value = preferences.get(key, null);
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

    public Device getDevice() {
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
