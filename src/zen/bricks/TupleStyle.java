package zen.bricks;

import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty.ColorProperty;
import zen.bricks.StyleProperty.FontProperty;
import zen.bricks.StyleProperty.IntegerProperty;
import zen.bricks.StyleProperty.MarginProperty;
import zen.bricks.styleeditor.IBrickStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.CheckedEditorPart;
import zen.bricks.styleeditor.parts.ColorEditorPart;

public class TupleStyle extends BrickStyle
{
    // ============================================================ Class Fields

    public static final ColorProperty FOREGROUND =
            new ColorProperty("Foreground color", ".color")
    {
        public RGB get(TupleStyle style) {
            return style.getForegroundRGB();
        }

        public void set(TupleStyle style, RGB value) {
            style.setForegroundRGB(value);
        }
    };

    public static final ColorProperty BACKGROUND =
        new ColorProperty("Background color", ".background")
    {
        public RGB get(TupleStyle style) {
            return style.getBackgroundRGB();
        }

        public void set(TupleStyle style, RGB value) {
            style.setBackgroundRGB(value);
        }
    };

    public static final ColorProperty TEXT_BACKGROUND =
            new ColorProperty("Text background color", ".textBackground")
    {
        public boolean isDefined(TupleStyle style) {
            return style.textBackground != null;
        }

        protected StyleEditorPart<RGB> createEditorPart(TupleStyle style, UI ui) {
            return new ColorEditorPart(this, style, style.textBackground);
        }

        @Override
        public void apply(StyleEditorPart<RGB> editorPart, TupleStyle style) {
            final ColorEditorPart colorEditorPart = (ColorEditorPart) editorPart;
            style.setTextBackgroundRGB(colorEditorPart.getBackground(),
                    colorEditorPart.getValue());
        }

        public RGB get(TupleStyle style) {
            return style.getTextBackgroundRGB();
        }

        @Override
        public void set(TupleStyle style, RGB value) {
            // do nothing, as it never called
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            final String string =
                    properties.getProperty(keyPrefix + keySuffix);
            final Boolean background;
            final RGB rgb;
            if (string == null) {
                background = null;
                rgb = null;
            } else if ("transparent".equals(string)) {
                background = false;
                rgb = null;
            } else {
                background = true;
                rgb = ColorUtil.parse(style.getDevice(), string);
            }
            style.setTextBackgroundRGB(background, rgb);
        }
    };

    public static final FontProperty FONT =
            new FontProperty("Font", ".font")
    {
        public FontData[] get(TupleStyle style) {
            return style.getFontList();
        }

        public void set(TupleStyle style, FontData[] value) {
            style.setFont(value);
        }
    };

    public static final MarginProperty PADDING =
            new MarginProperty("Brick padding", ".padding")
    {
        public Margin get(TupleStyle style) {
            return style.getPadding();
        }

        public void set(TupleStyle style, Margin value) {
            style.setPadding(value);
        }
    };

    public static final MarginProperty TEXT_MARGIN =
            new MarginProperty("Text margin", ".textMargin")
    {
        public Margin get(TupleStyle style) {
            return style.getTextMargin();
        }

        public void set(TupleStyle style, Margin value) {
            style.setTextMargin(value);
        }
    };

    public static final IntegerProperty LINE_SPACING =
            new IntegerProperty("Line spacing", ".lineSpacing")
    {
        public Integer get(TupleStyle style) {
            return style.getLineSpacing();
        }

        public void set(TupleStyle style, Integer value) {
            style.setLineSpacing(value);
        }
    };

    public static final IntegerProperty CHILD_SPACING =
            new IntegerProperty("Children spacing", ".spacing")
    {
        public Integer get(TupleStyle style) {
            return style.getSpacing();
        }

        public void set(TupleStyle style, Integer value) {
            style.setSpacing(value);
        }
    };

    // =========================================================================
    public static final StyleProperty<TupleLayout> LAYOUT =
            new StyleProperty<TupleLayout>("Layout", ".layout")
    {
        public TupleLayout get(TupleStyle style) {
            return style.getLayout();
        }

        public void set(TupleStyle style, TupleLayout value) {
            style.setLayout(value);
        }

        protected StyleEditorPart<TupleLayout> createEditorPart(
                final TupleStyle style, final UI ui)
        {
            return new CheckedEditorPart<TupleLayout>(this, style)
            {
                private Combo combo;

                public void createWidgets(Composite parent, int columns) {
                    createDefinedCheck(parent);
                    final Composite panel =
                            createValuesPanel(parent, columns - 1);

                    combo = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
                    final List<TupleLayout> layouts = ui.getTupleLayouts();
                    for (TupleLayout tupleLayout : layouts) {
                        combo.add(tupleLayout.getTitle());
                    }

                    final TupleLayout styleLayout = style.getLayout();
                    if (styleLayout != null) {
                        final int idx =
                                ui.getTupleLayouts().indexOf(styleLayout);
                        if (idx >= 0) {
                            combo.select(idx);
                        }
                    }

                    combo.setEnabled(isDefined());
                }

                protected void definedCheckChanged(boolean defined) {
                    combo.setEnabled(defined);
                }

                public TupleLayout getValue() {
                    final int idx = combo.getSelectionIndex();
                    if (idx < 0) {
                        return null;
                    } else {
                        return ui.getTupleLayouts().get(idx);
                    }
                }
            };
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            TupleLayout resultLayout = null;
            final String value = properties.getProperty(keyPrefix + keySuffix);
            if (value != null) {
                final List<TupleLayout> layouts = ui.getTupleLayouts();
                for (TupleLayout layout : layouts) {
                    if (layout.getName().equals(value)) {
                        resultLayout = layout;
                        break;
                    }
                }
            }
            set(style, resultLayout);
        }
    };

    // =========================================================================
    public static final StyleProperty<?>[] ALL_PROPERTIES = {
        FONT,
        FOREGROUND,
        BACKGROUND,
        TEXT_BACKGROUND,
        PADDING,
        TEXT_MARGIN,
        LINE_SPACING,
        CHILD_SPACING,
        LAYOUT,
    };

    static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================== Fields

    private boolean topLevel;

    private final Device device;

    private Font font;

    /**
     * Not null only if {@link #font} specified.
     */
    FontMetrics fontMetrics;

    /**
     * Not null only if {@link #font} specified.
     */
    GC savedGC;

    private Color backgroundColor;

    private Color foregroundColor;

    /**
     * null  - not defined<br/>
     * false - transparent<br/>
     * true  - opaque
     */
    Boolean textBackground;

    /**
     * Valid (not null) only if textBackground == true
     */
    private Color textBackgroundColor;

    private Margin padding;

    private Margin textMargin;

    private Integer lineSpacing;

    private Integer spacing;

    private TupleLayout layout;

    // ============================================================ Constructors

    public TupleStyle(UI ui, String name,
                      Properties properties, String keyPrefix)
    {
        super(name);
        this.device = ui.getDevice();
        try {
            for (final StyleProperty<?> styleProperty : ALL_PROPERTIES) {
                styleProperty.parse(ui, this, properties, keyPrefix);
            }
        } catch (RuntimeException e) {
            dispose();
            throw e;
        }
    }

    // ================================================================= Methods

    public boolean isTopLevel() {
        return topLevel;
    }

    public void setTopLevel(boolean topLevel) {
        this.topLevel = topLevel;
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (foregroundColor != null) {
            foregroundColor.dispose();
            foregroundColor = null;
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

    public Device getDevice() {
        return device;
    }

    public void setFont(FontData[] fontList) {
        if (font != null) {
            font.dispose();
            font = null;
            fontMetrics = null;
            savedGC.dispose();
            savedGC = null;
        }
        if (fontList == null) {
            return;
        }
        font = new Font(device, fontList);
        savedGC = new GC(device);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
    }

    public FontData[] getFontList() {
        return (font != null) ? font.getFontData() : null;
    }

    public Font getFont() {
        return font;
    }

    public IBrickStyleEditor createEditor(UI ui) {
//        return new TupleStyleEditor(this);
        return new PropertiesListEditor(TupleStyle.ALL_PROPERTIES, this, ui);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public RGB getBackgroundRGB() {
        return (backgroundColor != null) ? backgroundColor.getRGB() : null;
    }

    public void setBackgroundRGB(RGB rgb) {
        if (backgroundColor != null) {
            backgroundColor.dispose();
        }
        if (rgb != null) {
            backgroundColor = new Color(device, rgb);
        } else {
            backgroundColor = null;
        }
    }

    public void setForegroundRGB(RGB rgb) {
        if (foregroundColor != null) {
            foregroundColor.dispose();
        }
        if (rgb != null) {
            foregroundColor = new Color(device, rgb);
        } else {
            foregroundColor = null;
        }
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public RGB getForegroundRGB() {
        return foregroundColor != null ? foregroundColor.getRGB() : null;
    }

    public Boolean getTextBackground() {
        return textBackground;
    }

    public Color getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public RGB getTextBackgroundRGB() {
        return textBackgroundColor != null ?
                textBackgroundColor.getRGB() : null;
    }

    public void setTextBackgroundRGB(Boolean textBackground, RGB rgb) {
        if (textBackgroundColor != null) {
            textBackgroundColor.dispose();
        }
        this.textBackground = textBackground;
        if (Boolean.TRUE.equals(textBackground)) { // if opaque
            textBackgroundColor = new Color(device, rgb);
        } else {
            textBackgroundColor = null;
        }
    }

    public Margin getPadding() {
        return padding;
    }

    public void setPadding(Margin padding) {
        this.padding = padding;
    }

    public Margin getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(Margin textMargin) {
        this.textMargin = textMargin;
    }

    public StyleChain createChain(StyleChain parent) {
        return new StyleChain(this, parent);
    }

    public Integer getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(Integer value) {
        lineSpacing = value;
    }

    public Integer getSpacing() {
        return spacing;
    }

    public void setSpacing(Integer spacing) {
        this.spacing = spacing;
    }

    public TupleLayout getLayout() {
        return layout;
    }

    public void setLayout(TupleLayout layout) {
        this.layout = layout;
    }
}
