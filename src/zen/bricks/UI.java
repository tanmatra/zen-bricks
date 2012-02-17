package zen.bricks;

import java.util.ArrayList;
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

import zen.bricks.LineBreak.LineBreakStyle;
import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.swt.RadioPanel;

public class UI
{
    // ================================================================== Fields

    private final Device device;

    private Boolean advanced;
    int antialias;
    Color canvasBackgroundColor;
    int textAntialias;

    private TupleStyle basicStyle;
    private TupleStyle atomStyle;
    private TupleStyle listStyle;
    private TupleStyle selectedStyle;
    private TupleStyle selectedParentStyle;

    private LineBreakStyle lineBreakStyle;

    private List<Style> allStyles = new ArrayList<Style>();

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
        createStyles();
    }

    // ================================================================= Methods

    private void createStyles() {
        allStyles.add(new GlobalStyle("global", "Global"));

        basicStyle = new TupleStyle(this, "basic", "Basic");
        allStyles.add(basicStyle);

        atomStyle = new TupleStyle(basicStyle, "atom", "Atom");
        listStyle = new TupleStyle(basicStyle, "list", "List");
        selectedStyle = new TupleStyle(basicStyle, "selected", "Selected");
        selectedParentStyle =
                new TupleStyle(basicStyle, "selectedParent", "Selected parent");

        lineBreakStyle =
                new LineBreak.LineBreakStyle(this, "linebreak", "Line break");
        allStyles.add(lineBreakStyle);
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

    public void load(Preferences preferences) throws Exception {
        loadLayoutFactories();
        loadBorderFactories();

        for (final Style style : allStyles) {
            loadStyle(style, preferences);
        }

        basicChain = basicStyle.createChain(null);
        atomChain = atomStyle.createChain(basicChain);
        listChain = listStyle.createChain(basicChain);

        fireChangedEvent();
    }

    private static void loadStyle(Style style, Preferences preferences) {
        style.load(preferences);
        for (final Style child : style.getChildren()) {
            loadStyle(child, preferences);
        }
    }

    public void save(Preferences preferences) {
        for (final Style style : allStyles) {
            saveStyle(style, preferences);
        }
    }

    private static void saveStyle(Style style, Preferences preferences) {
        style.save(preferences);
        for (final Style child : style.getChildren()) {
            saveStyle(child, preferences);
        }
    }

    private void loadLayoutFactories() {
        final ServiceLoader<TupleLayout> layoutsLoader =
                ServiceLoader.load(TupleLayout.class);
        tupleLayouts = new ArrayList<TupleLayout>();
        for (final TupleLayout layout : layoutsLoader) {
            tupleLayouts.add(layout);
        }
    }

    private void loadBorderFactories() {
        @SuppressWarnings("rawtypes")
        final ServiceLoader<BorderFactory> bordersLoader =
                ServiceLoader.load(BorderFactory.class);
        borderFactories = new ArrayList<BorderFactory<?>>();
        for (final BorderFactory<?> borderFactory : bordersLoader) {
            borderFactories.add(borderFactory);
        }
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

    public Color getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    public TupleStyle getBasicStyle() {
        return basicStyle;
    }

    public TupleStyle getSelectedStyle() {
        return selectedStyle;
    }

    public TupleStyle getSelectedParentStyle() {
        return selectedParentStyle;
    }

    public void preparePaint(GC gc) {
        if (advanced != null) {
            gc.setAdvanced(advanced);
        }
    }

    public void prepareGraphicsPaint(GC gc) {
        gc.setAntialias(antialias);
    }

    public void prepareTextPaint(GC gc) {
        gc.setAdvanced(false); // TODO
        gc.setTextAntialias(textAntialias);
    }

    /*
     * You should ask Editor for additional styles for chain.
     */
    public StyleChain getStyleChain(TupleBrick brick) {
        if (brick.isList()) {
            return listChain;
        } else {
            return atomChain;
        }
    }

    public LineBreakStyle getLineBreakStyle() {
        return lineBreakStyle;
    }

    public List<? extends Style> getAllStyles() {
        return allStyles;
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

        public GlobalStyle(String key, String title) {
            super(UI.this, key, title);
        }

        public IStyleEditor createEditor() {
            return new GlobalStyleEditor();
        }

        protected void loadImpl(Preferences node) {
            antialias = parseState(node, ANTIALIAS_KEY);
            textAntialias = parseState(node, TEXT_ANTIALIAS_KEY);
            canvasBackgroundColor = ColorUtil.parseColor(getDevice(),
                    node.get(CANVAS_BACKGROUND_KEY, null));
            caretOffset = node.getInt(CARET_OFFSET_KEY, 0);
            caretWidth = node.getInt(CARET_WIDTH_KEY, 2);
        }

        protected void saveImpl(Preferences node) {
            node.put(ANTIALIAS_KEY, stateToString(antialias));
            node.put(TEXT_ANTIALIAS_KEY, stateToString(textAntialias));
            node.put(CANVAS_BACKGROUND_KEY,
                    ColorUtil.format(canvasBackgroundColor));
            node.putInt(CARET_OFFSET_KEY, caretOffset);
            node.putInt(CARET_WIDTH_KEY, caretWidth);
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
                canvasBackgroundColor = new Color(getDevice(), rgb);
            }

            caretOffset = caretOffsetSpinner.getSelection();
            caretWidth = caretWidthSpinner.getSelection();
        }

        public void cancel() {
        }
    }
}
