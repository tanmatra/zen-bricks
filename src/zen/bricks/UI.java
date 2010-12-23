package zen.bricks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.utils.RadioPanel;

public class UI
{
    // ================================================================== Fields

    final Device device;
    private GC savedGC;

    private Boolean advanced;
    int antialias;
    private Border border;
    Color canvasBackgroundColor;
    int textAntialias;

    private TupleStyle basicStyle;
    private TupleStyle listStyle;
    private TupleStyle selectedStyle;

    private GlobalStyle globalStyle;

    private StyleChain basicChain;
    private StyleChain listChain;
    private ArrayList<TupleLayout> tupleLayouts;
    private ArrayList<BorderFactory<?>> borderFactories;

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

        @SuppressWarnings("rawtypes")
        final ServiceLoader<BorderFactory> bordersLoader =
                ServiceLoader.load(BorderFactory.class);
        borderFactories = new ArrayList<BorderFactory<?>>();
        for (final BorderFactory<?> borderFactory : bordersLoader) {
            borderFactories.add(borderFactory);
        }

        textAntialias = parseState(prefs, "textAntialias");

        final Preferences stylesNode = prefs.node("styles");
        basicStyle = new TupleStyle(this, "Basic", stylesNode.node("basic"));
        basicStyle.setTopLevel(true);
        listStyle = new TupleStyle(this, "List", stylesNode.node("list"));
        selectedStyle =
                new TupleStyle(this, "Selected", stylesNode.node("selected"));

        globalStyle = new GlobalStyle();

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
        if (selectedStyle != null) {
            selectedStyle.dispose();
            selectedStyle = null;
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
        } else if ("on".equals(value) || "true".equals(value)) {
            return SWT.ON;
        } else if ("off".equals(value) || "false".equals(value)) {
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

    public List<? extends Style> getStyles() {
        return Arrays.asList(globalStyle, basicStyle, listStyle, selectedStyle);
    }

    public Border getBorder() {
        return border;
    }

    public List<TupleLayout> getTupleLayouts() {
        return tupleLayouts;
    }

    public List<BorderFactory<?>> getBorderFactories() {
        return borderFactories;
    }

    // ========================================================== Nested Classes

    class GlobalStyle extends Style
    {
        public GlobalStyle() {
            super(UI.this, "Global");
        }

        public IStyleEditor createEditor() {
            return new GlobalStyleEditor();
        }

        public void dispose() {
        }
    }

    static final String[] ANTIALIAS_LABELS = { "On", "Off", "Default" };
    static final Integer[] ANTIALIAS_VALUES = { SWT.ON, SWT.OFF, SWT.DEFAULT };

    class GlobalStyleEditor implements IStyleEditor
    {
        private Composite panel;
        private RadioPanel antialiasPanel;
        private RadioPanel textAntialiasPanel;
        private ColorSelector canvasColorSelector;

        public void createControl(Composite parent) {
            panel = new Composite(parent, SWT.NONE);
            GridLayoutFactory.swtDefaults().numColumns(2).applyTo(panel);

            new Label(panel, SWT.NONE).setText("Antialias:");

            antialiasPanel = new RadioPanel(panel);
            antialiasPanel.setLabels(ANTIALIAS_LABELS);
            antialiasPanel.setValues(ANTIALIAS_VALUES);
            antialiasPanel.setValueSelected(antialias);

            new Label(panel, SWT.NONE).setText("Text antialias:");

            textAntialiasPanel = new RadioPanel(panel);
            textAntialiasPanel.setLabels(ANTIALIAS_LABELS);
            textAntialiasPanel.setValues(ANTIALIAS_VALUES);
            textAntialiasPanel.setValueSelected(textAntialias);

            new Label(panel, SWT.NONE).setText("Canvas background:");

            canvasColorSelector = new ColorSelector(panel);
            canvasColorSelector.setColorValue(canvasBackgroundColor.getRGB());
        }

        public Control getControl() {
            return panel;
        }

        public void apply() {
            antialias = (Integer) antialiasPanel.getSelectionValue();
            textAntialias = (Integer) textAntialiasPanel.getSelectionValue();

            final RGB rgb = canvasColorSelector.getColorValue();
            if (!rgb.equals(canvasBackgroundColor.getRGB())) {
                canvasBackgroundColor.dispose();
                canvasBackgroundColor = new Color(device, rgb);
            }
        }

        public void cancel() {
        }
    }
}
