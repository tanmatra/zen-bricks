package zen.bricks;

import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.properties.BorderProperty;
import zen.bricks.properties.ColorProperty;
import zen.bricks.properties.FontProperty;
import zen.bricks.properties.IntegerProperty;
import zen.bricks.properties.LayoutProperty;
import zen.bricks.properties.MarginProperty;
import zen.bricks.properties.TextBackgroundProperty;
import zen.bricks.properties.TransparentColor;
import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;

public class TupleStyle extends Style
{
    // ============================================================ Class Fields

    public static final ColorProperty<TupleStyle> FOREGROUND =
            new ColorProperty<TupleStyle>("color", "Foreground color")
    {
        public RGB get(TupleStyle style) {
            return style.getForegroundRGB(); }
        public void set(TupleStyle style, RGB value) {
            style.setForegroundRGB(value); }
    };

    public static final ColorProperty<TupleStyle> BACKGROUND =
            new ColorProperty<TupleStyle>("background", "Background color")
    {
        public RGB get(TupleStyle style) {
            return style.getBackgroundRGB(); }
        public void set(TupleStyle style, RGB value) {
            style.setBackgroundRGB(value); }
    };

    public static final Property<TupleStyle, TransparentColor> TEXT_BACKGROUND =
            new TextBackgroundProperty("textBackground",
                    "Text background color");

    public static final FontProperty<TupleStyle> FONT =
            new FontProperty<TupleStyle>("font", "Font")
    {
        public FontData[] get(TupleStyle style) {
            return style.getFontList(); }
        public void set(TupleStyle style, FontData[] value) {
            style.setFont(value); }
    };

    public static final MarginProperty<TupleStyle> PADDING =
            new MarginProperty<TupleStyle>("padding", "Brick padding")
    {
        public Margin get(TupleStyle style) {
            return style.getPadding(); }
        public void set(TupleStyle style, Margin value) {
            style.setPadding(value); }
    };

    public static final MarginProperty<TupleStyle> TEXT_MARGIN =
            new MarginProperty<TupleStyle>("textMargin", "Text margin")
    {
        public Margin get(TupleStyle style) {
            return style.getTextMargin(); }
        public void set(TupleStyle style, Margin value) {
            style.setTextMargin(value); }
    };

    public static final IntegerProperty<TupleStyle> LINE_SPACING =
            new IntegerProperty<TupleStyle>("lineSpacing", "Line spacing")
    {
        { setMinimum(-1); }
        public Integer get(TupleStyle style) {
            return style.getLineSpacing(); }
        public void set(TupleStyle style, Integer value) {
            style.setLineSpacing(value); }
    };

    public static final IntegerProperty<TupleStyle> CHILDREN_SPACING =
            new IntegerProperty<TupleStyle>("spacing", "Children spacing")
    {
        public Integer get(TupleStyle style) {
            return style.getSpacing(); }
        public void set(TupleStyle style, Integer value) {
            style.setSpacing(value); }
    };

    public static final Property<TupleStyle, TupleLayout> LAYOUT =
            new LayoutProperty("layout", "Layout");

    public static final Property<TupleStyle, Border> BORDER =
            new BorderProperty("border", "Border");

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static final List<? extends Property<TupleStyle, ?>> ALL_PROPERTIES =
            Arrays.asList(
                    FONT, FOREGROUND, BACKGROUND, TEXT_BACKGROUND,
                    PADDING, TEXT_MARGIN, LINE_SPACING, CHILDREN_SPACING,
                    LAYOUT, BORDER);

    // ================================================================== Fields

    private boolean topLevel;

    private Font font;

    /**
     * Not null only if {@link #font} specified.
     */
    private FontMetrics fontMetrics;

    /**
     * Not null only if {@link #font} specified.
     */
    private GC savedGC;

    private Color backgroundColor;

    private Color foregroundColor;

    private TransparentColor textBackground;

    private Margin padding;

    private Margin textMargin;

    private Integer lineSpacing;

    private Integer spacing;

    private TupleLayout layout;

    private Border border;

    // ============================================================ Constructors

    public TupleStyle(UI ui, String key, String name) {
        super(ui, key, name);
    }

    // ================================================================= Methods

    protected void loadImpl(Preferences node) {
        for (final Property<TupleStyle, ?> property : ALL_PROPERTIES) {
            property.load(this, node);
        }
    }

    protected void saveImpl(Preferences node) {
        for (final Property<TupleStyle, ?> property : ALL_PROPERTIES) {
            property.save(this, node);
        }
    }

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
        if (textBackground != null) {
            textBackground.dispose();
            textBackground = null;
        }
        if (savedGC != null) {
            savedGC.dispose();
            savedGC = null;
        }
        if (border != null) {
            border.dispose();
            border = null;
        }
    }

    public Device getDevice() {
        return ui.getDevice();
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
        font = new Font(getDevice(), fontList);
        savedGC = new GC(getDevice());
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
    }

    public FontData[] getFontList() {
        return (font != null) ? font.getFontData() : null;
    }

    public Font getFont() {
        return font;
    }

    public Point getTextExtent(String text) {
        return savedGC.textExtent(text, TupleBrick.TEXT_FLAGS);
    }

    public int getFontAscent() {
        return fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    public int getFontHeight() {
        return fontMetrics.getHeight();
    }

    public IStyleEditor createEditor() {
        final PropertiesListEditor<TupleStyle> editor =
                new PropertiesListEditor<TupleStyle>(this, ALL_PROPERTIES);
        if (isTopLevel()) {
            editor.setAllPropertiesMandatory(true);
        }
        return editor;
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
            backgroundColor = new Color(getDevice(), rgb);
        } else {
            backgroundColor = null;
        }
    }

    public void setForegroundRGB(RGB rgb) {
        if (foregroundColor != null) {
            foregroundColor.dispose();
        }
        if (rgb != null) {
            foregroundColor = new Color(getDevice(), rgb);
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

    public TransparentColor getTextBackground() {
        return textBackground;
    }

    public void setTextBackground(TransparentColor textBackgroundColor) {
        if (this.textBackground != null) {
            this.textBackground.dispose();
        }
        this.textBackground = textBackgroundColor;
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

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        if (this.border != null) {
            this.border.dispose();
        }
        this.border = border;
    }
}
