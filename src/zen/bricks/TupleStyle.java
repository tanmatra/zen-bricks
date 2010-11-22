package zen.bricks;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.StyleProperty.ColorProperty;
import zen.bricks.StyleProperty.FontProperty;
import zen.bricks.StyleProperty.IntegerProperty;
import zen.bricks.StyleProperty.MarginProperty;
import zen.bricks.styleeditor.ColorEditorPart;
import zen.bricks.styleeditor.IBrickStyleEditor;
import zen.bricks.styleeditor.PropertiesListEditor;
import zen.bricks.styleeditor.StyleEditorPart;

public class TupleStyle
{
    // ============================================================ Class Fields

    public static final ColorProperty FOREGROUND =
            new ColorProperty("Foreground color")
    {
        public RGB get(TupleStyle style) {
            return style.getForegroundRGB();
        }

        public void set(TupleStyle style, RGB value) {
            style.setForegroundRGB(value);
        }
    };

    public static final ColorProperty BACKGROUND =
        new ColorProperty("Background color")
    {
        public RGB get(TupleStyle style) {
            return style.getBackgroundRGB();
        }

        public void set(TupleStyle style, RGB value) {
            style.setBackgroundRGB(value);
        }
    };

    public static final ColorProperty TEXT_BACKGROUND =
            new ColorProperty("Text background color")
    {
        public boolean isDefined(TupleStyle style) {
            return style.textBackground != null;
        }

        public StyleEditorPart createEditorPart(TupleStyle style) {
            final ColorEditorPart editorPart =
                    new ColorEditorPart(this, style, style.textBackground);
            return editorPart;
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

        public void set(TupleStyle style, RGB value) {
            // do nothing, as it never called
        }
    };

    public static final FontProperty FONT =
            new FontProperty("Font")
    {
        public FontData[] get(TupleStyle style) {
            return style.getFontList();
        }

        public void set(TupleStyle style, FontData[] value) {
            style.setFont(value);
        }
    };

    public static final MarginProperty PADDING =
            new MarginProperty("Brick padding")
    {
        public Margin get(TupleStyle style) {
            return style.getPadding();
        }

        public void set(TupleStyle style, Margin value) {
            style.setPadding(value);
        }
    };

    public static final MarginProperty TEXT_MARGIN =
            new MarginProperty("Text margin")
    {
        public Margin get(TupleStyle style) {
            return style.getTextMargin();
        }

        public void set(TupleStyle style, Margin value) {
            style.setTextMargin(value);
        }
    };

    public static final IntegerProperty LINE_SPACING =
            new IntegerProperty("Line spacing")
    {
        public Integer get(TupleStyle style) {
            return style.getLineSpacing();
        }

        public void set(TupleStyle style, Integer value) {
            style.setLineSpacing(value);
        }
    };

    public static final IntegerProperty CHILD_SPACING =
            new IntegerProperty("Children spacing")
    {
        public Integer get(TupleStyle style) {
            return style.getSpacing();
        }

        public void set(TupleStyle style, Integer value) {
            style.setSpacing(value);
        }
    };

    public static final StyleProperty<?>[] ALL_PROPERTIES = {
        FONT,
        FOREGROUND,
        BACKGROUND,
        TEXT_BACKGROUND,
        PADDING,
        TEXT_MARGIN,
        LINE_SPACING,
        CHILD_SPACING
    };

    static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================== Fields

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

    private final String name;

    // ============================================================ Constructors

    public TupleStyle(String name, Device device,
        Properties properties, String keyPrefix)
    {
        this.name = name;
        this.device = device;
        try {
            final String fontVal = properties.getProperty(keyPrefix + ".font");
            if (!"inherit".equals(fontVal)) {
                FontData fontData = parseFontData(fontVal);
                setFont(fontData != null ? new FontData[] { fontData } : null);
            }

            backgroundColor =
                    ColorUtil.parse(device, properties, keyPrefix, ".background");
            foregroundColor =
                    ColorUtil.parse(device, properties, keyPrefix, ".color");

            final String textBackStr =
                    properties.getProperty(keyPrefix + ".textBackground");
            if ("transparent".equals(textBackStr)) {
                textBackground = false; // transparent
            } else {
                textBackgroundColor = ColorUtil.parse(device, textBackStr);
                if (textBackgroundColor == null) {
                    textBackground = null; // not defined
                } else {
                    textBackground = true; // opaque
                }
            }

            padding = Margin.parseMargin(properties, keyPrefix + ".padding");
            textMargin = Margin.parseMargin(properties, keyPrefix + ".textMargin");
            lineSpacing = parseInt(properties, keyPrefix + ".lineSpacing");
            spacing = parseInt(properties, keyPrefix + ".spacing");
        } catch (RuntimeException e) {
            dispose();
            throw e;
        }
    }

    // ================================================================= Methods

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

    public String getName() {
        return name;
    }

    private static FontData parseFontData(String str) {
        if (str == null) {
            return null;
        }
        String name;
        float height = 8.0f;
        int style = SWT.NORMAL;
        StringTokenizer tokenizer;
        if (str.charAt(0) == '"') {
            final int p = str.indexOf('"', 1);
            name = str.substring(1, p);
            tokenizer = new StringTokenizer(str.substring(p + 1));
        } else {
            tokenizer = new StringTokenizer(str);
            name = tokenizer.nextToken();
        }
        String heightStr = tokenizer.nextToken();
        height = Float.parseFloat(heightStr);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if ("bold".equals(token)) {
                style |= SWT.BOLD;
            } else if ("italic".equals(token)) {
                style |= SWT.ITALIC;
            }
        }
        final FontData data = new FontData(name, (int) height, style);
        if (Math.floor(height) != height) {
            data.height = height;
        }
        return data;
    }

    private static Integer parseInt(Properties properties, String key) {
        final String string = properties.getProperty(key);
        if (string == null) {
            return null;
        } else {
            return Integer.parseInt(string);
        }
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

    public IBrickStyleEditor getEditor() {
//        return new TupleStyleEditor(this);
        return new PropertiesListEditor(TupleStyle.ALL_PROPERTIES, this);
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
        return textBackgroundColor != null ? textBackgroundColor.getRGB() : null;
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
}
