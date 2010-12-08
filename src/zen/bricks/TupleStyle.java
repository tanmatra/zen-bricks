package zen.bricks;

import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.properties.BackgroundProperty;
import zen.bricks.properties.BorderProperty;
import zen.bricks.properties.ChildrenSpacingProperty;
import zen.bricks.properties.ColorProperty;
import zen.bricks.properties.FontProperty;
import zen.bricks.properties.ForegroundProperty;
import zen.bricks.properties.IntegerProperty;
import zen.bricks.properties.LayoutProperty;
import zen.bricks.properties.LineSpacingProperty;
import zen.bricks.properties.MarginProperty;
import zen.bricks.properties.PaddingProperty;
import zen.bricks.properties.TextBackgroundProperty;
import zen.bricks.properties.TextFontProperty;
import zen.bricks.properties.TextMarginProperty;
import zen.bricks.styleeditor.IBrickStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;

public class TupleStyle extends BrickStyle
{
    // ============================================================ Class Fields

    public static final ColorProperty FOREGROUND =
            new ForegroundProperty("Foreground color", "color");

    public static final ColorProperty BACKGROUND =
            new BackgroundProperty("Background color", "background");

    public static final ColorProperty TEXT_BACKGROUND =
            new TextBackgroundProperty("Text background color",
                    "textBackground");

    public static final FontProperty FONT =
            new TextFontProperty("Font", "font");

    public static final MarginProperty PADDING =
            new PaddingProperty("Brick padding", "padding");

    public static final MarginProperty TEXT_MARGIN =
            new TextMarginProperty("Text margin", "textMargin");

    public static final IntegerProperty LINE_SPACING =
            new LineSpacingProperty("Line spacing", "lineSpacing");

    public static final IntegerProperty CHILDREN_SPACING =
            new ChildrenSpacingProperty("Children spacing", "spacing");

    public static final StyleProperty<TupleLayout> LAYOUT =
            new LayoutProperty("Layout", "layout");

    public static final StyleProperty<Border> BORDER =
            new BorderProperty("Border", "border");

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static final StyleProperty<?>[] ALL_PROPERTIES = {
        FONT,
        FOREGROUND,
        BACKGROUND,
        TEXT_BACKGROUND,
        PADDING,
        TEXT_MARGIN,
        LINE_SPACING,
        CHILDREN_SPACING,
        LAYOUT,
        BORDER,
    };

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

    private Border border;

    // ============================================================ Constructors

    public TupleStyle(UI ui, String name, Preferences preferences) {
        super(name);
        this.device = ui.getDevice();
        try {
            for (final StyleProperty<?> styleProperty : ALL_PROPERTIES) {
                styleProperty.load(ui, this, preferences);
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
