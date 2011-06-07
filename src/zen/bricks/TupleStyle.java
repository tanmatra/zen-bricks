package zen.bricks;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
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
import zen.bricks.properties.TransparentColor;
import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;

public class TupleStyle extends Style
{
    // ============================================================ Class Fields

    public static final ColorProperty FOREGROUND =
            new ForegroundProperty("Foreground color", "color");

    public static final ColorProperty BACKGROUND =
            new BackgroundProperty("Background color", "background");

    public static final StyleProperty<TransparentColor> TEXT_BACKGROUND =
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

    // ================================================================== Fields

    private boolean topLevel;

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

    private TransparentColor textBackground;

    private Margin padding;

    private Margin textMargin;

    private Integer lineSpacing;

    private Integer spacing;

    private TupleLayout layout;

    private Border border;

    // ============================================================ Constructors

    public TupleStyle(UI ui, String name) {
        super(ui, name);
    }

    // ================================================================= Methods

    public void load(Preferences preferences) {
        for (final StyleProperty<?> styleProperty : ALL_PROPERTIES) {
            styleProperty.load(this, preferences);
        }
    }

    public void save(Preferences preferences) {
        for (final StyleProperty<?> styleProperty : ALL_PROPERTIES) {
            styleProperty.save(this, preferences);
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

    public int getTextAscent() {
        return fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    public IStyleEditor createEditor() {
        return new PropertiesListEditor(ALL_PROPERTIES, this);
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
