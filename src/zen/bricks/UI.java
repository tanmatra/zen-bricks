package zen.bricks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.widgets.Spinner;

import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.utils.RadioPanel;

public class UI
{
    // ================================================================== Fields

    final Device device;

    private Boolean advanced;
    int antialias;
    Color canvasBackgroundColor;
    int textAntialias;

    private GlobalStyle globalStyle;

    private TupleStyle basicStyle;
    private TupleStyle atomStyle;
    private TupleStyle listStyle;
    private TupleStyle selectedStyle;

    private StyleChain basicChain;
    private StyleChain atomChain;
    private StyleChain listChain;

    private ArrayList<TupleLayout> tupleLayouts;
    private ArrayList<BorderFactory<?>> borderFactories;

    int caretOffset;
    int caretWidth = 2;

    private final List<Editor> editors = new ArrayList<Editor>();

    // ============================================================ Constructors

    public UI(Device device) {
        this.device = device;
    }

    public UI(Device device, Preferences preferences) throws Exception {
        this.device = device;
        try {
            load(preferences);
        } catch (Exception e) {
            dispose();
            throw e;
        }
    }

    // ================================================================= Methods

    void load(Preferences prefs) throws Exception {
        globalStyle = new GlobalStyle();
        globalStyle.load(prefs);

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

        basicStyle = new TupleStyle(this, "Basic");
        basicStyle.setTopLevel(true);
        atomStyle = new TupleStyle(this, "Atom");
        listStyle = new TupleStyle(this, "List");
        selectedStyle = new TupleStyle(this, "Selected");

        final Preferences stylesNode = prefs.node("styles");
        basicStyle.load(stylesNode.node("basic"));
        atomStyle.load(stylesNode.node("atom"));
        listStyle.load(stylesNode.node("list"));
        selectedStyle.load(stylesNode.node("selected"));

        basicChain = basicStyle.createChain(null);
        atomChain = atomStyle.createChain(basicChain);
        listChain = listStyle.createChain(basicChain);

        fireChangedEvent();
    }

    void dispose() {
        if (canvasBackgroundColor != null) {
            canvasBackgroundColor.dispose();
            canvasBackgroundColor = null;
        }
        if (basicStyle != null) {
            basicStyle.dispose();
            basicStyle = null;
        }
        if (atomStyle != null) {
            atomStyle.dispose();
            atomStyle = null;
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

    public void save(Preferences preferences) {
        globalStyle.save(preferences);
        final Preferences stylesNode = preferences.node("styles");
        basicStyle.save(stylesNode.node("basic"));
        atomStyle.save(stylesNode.node("atom"));
        listStyle.save(stylesNode.node("list"));
        selectedStyle.save(stylesNode.node("selected"));
    }

    public void addEditor(Editor editor) {
        editors.add(editor);
    }

    public void removeEditor(Editor editor) {
        editors.remove(editor);
    }

    public void fireChangedEvent() {
        for (final Editor editor : editors) {
            editor.uiChanged();
        }
    }

    /**
     * @param editor
     */
    public void applyTo(Editor editor) {
        // what here ???
    }

    static Boolean parseBoolean(Preferences preferences, String key) {
        final String value = preferences.get(key, null);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    static int parseInt(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return Integer.parseInt(value);
    }

    static int parseState(Preferences preferences, String key) {
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

    static String stateToString(int state) {
        switch (state) {
            case SWT.ON: return "on";
            case SWT.OFF: return "off";
            default: return "default";
        }
    }

    void changeBasicFont(FontData[] fontList) {
        basicStyle.setFont(fontList);
    }

    public Device getDevice() {
        return device;
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
            chain = atomChain;
        }
        if (brick == editor.getSelection()) {
            chain = selectedStyle.createChain(chain);
        }
        return chain;
    }

    public List<? extends Style> getStyles() {
        return Arrays.asList(
                globalStyle, basicStyle, atomStyle, listStyle, selectedStyle);
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
        private static final String CARET_WIDTH_KEY = "caretWidth";
        private static final String CARET_OFFSET_KEY = "caretOffset";
        private static final String ANTIALIAS_KEY = "antialias";
        private static final String CANVAS_BACKGROUND_KEY = "canvasBackgroundColor";
        private static final String TEXT_ANTIALIAS_KEY = "textAntialias";

        public GlobalStyle() {
            super(UI.this, "Global");
        }

        public IStyleEditor createEditor() {
            return new GlobalStyleEditor();
        }

        public void load(Preferences preferences) {
            antialias = parseState(preferences, ANTIALIAS_KEY);
            textAntialias = parseState(preferences, TEXT_ANTIALIAS_KEY);
            canvasBackgroundColor = ColorUtil.parseColor(device,
                    preferences.get(CANVAS_BACKGROUND_KEY, null));
            caretOffset = preferences.getInt(CARET_OFFSET_KEY, 0);
            caretWidth = preferences.getInt(CARET_WIDTH_KEY, 2);
        }

        public void save(Preferences preferences) {
            preferences.put(ANTIALIAS_KEY, stateToString(antialias));
            preferences.put(TEXT_ANTIALIAS_KEY, stateToString(textAntialias));
            preferences.put(CANVAS_BACKGROUND_KEY,
                    ColorUtil.format(canvasBackgroundColor));
            preferences.putInt(CARET_OFFSET_KEY, caretOffset);
            preferences.putInt(CARET_WIDTH_KEY, caretWidth);
        }

        public void dispose() {
            // do nothing
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
        private Spinner caretOffsetSpinner;
        private Spinner caretWidthSpinner;

        public void createControl(Composite parent) {
            panel = new Composite(parent, SWT.NONE);
            GridLayoutFactory.swtDefaults().numColumns(2).applyTo(panel);

            new Label(panel, SWT.NONE).setText("Antialias");

            antialiasPanel = new RadioPanel(panel);
            antialiasPanel.setLabels(ANTIALIAS_LABELS);
            antialiasPanel.setValues(ANTIALIAS_VALUES);
            antialiasPanel.setValueSelected(antialias);

            new Label(panel, SWT.NONE).setText("Text antialias");

            textAntialiasPanel = new RadioPanel(panel);
            textAntialiasPanel.setLabels(ANTIALIAS_LABELS);
            textAntialiasPanel.setValues(ANTIALIAS_VALUES);
            textAntialiasPanel.setValueSelected(textAntialias);

            new Label(panel, SWT.NONE).setText("Canvas background");

            canvasColorSelector = new ColorSelector(panel);
            canvasColorSelector.setColorValue(canvasBackgroundColor.getRGB());

            new Label(panel, SWT.NONE).setText("Caret");

            final Composite caretPanel = new Composite(panel, SWT.NONE);
            GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(4)
                    .applyTo(caretPanel);
            new Label(caretPanel, SWT.NONE).setText("offset:");
            caretOffsetSpinner = new Spinner(caretPanel, SWT.BORDER);
            caretOffsetSpinner.setMinimum(-10);
            caretOffsetSpinner.setMaximum(10);
            caretOffsetSpinner.setSelection(caretOffset);
            final Label label = new Label(caretPanel, SWT.NONE);
            label.setText("width:");
            GridDataFactory.swtDefaults().indent(10, 0).applyTo(label);
            caretWidthSpinner = new Spinner(caretPanel, SWT.BORDER);
            caretWidthSpinner.setMinimum(-10);
            caretWidthSpinner.setMaximum(10);
            caretWidthSpinner.setSelection(caretWidth);
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

            caretOffset = caretOffsetSpinner.getSelection();
            caretWidth = caretWidthSpinner.getSelection();
        }

        public void cancel() {
        }
    }
}
