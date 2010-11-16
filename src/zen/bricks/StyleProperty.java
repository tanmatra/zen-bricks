package zen.bricks;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.styleeditor.ColorEditorPart;
import zen.bricks.styleeditor.FontEditorPart;
import zen.bricks.styleeditor.IntegerEditorPart;
import zen.bricks.styleeditor.MarginEditorPart;
import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<T>
{
    protected final String title;

    public StyleProperty(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract T get(TupleStyle style);

    public abstract void set(TupleStyle style, T value);

    public boolean isDefined(TupleStyle style) {
        return get(style) != null;
    }

    public TupleStyle find(StyleChain chain) {
        do {
            final TupleStyle style = chain.style;
            if (isDefined(style)) {
                return style;
            }
            chain = chain.parent;
        } while (chain != null);
        throw new Error("Style property not found in chain");
    }

    public abstract StyleEditorPart createEditorPart(TupleStyle style);

    // =========================================================================

    public static abstract class ColorProperty extends StyleProperty<RGB>
    {
        public ColorProperty(String title) {
            super(title);
        }

        public StyleEditorPart createEditorPart(TupleStyle style) {
            return new ColorEditorPart(this, style);
        }
    }

    public static abstract class FontProperty extends StyleProperty<FontData[]>
    {
        public FontProperty(String title) {
            super(title);
        }

        public StyleEditorPart createEditorPart(TupleStyle style) {
            return new FontEditorPart(this, style);
        }
    }

    public static abstract class MarginProperty extends StyleProperty<Margin>
    {
        public MarginProperty(String title) {
            super(title);
        }

        public StyleEditorPart createEditorPart(TupleStyle style) {
            return new MarginEditorPart(this, style);
        }
    }

    public static abstract class IntegerProperty extends StyleProperty<Integer>
    {
        public IntegerProperty(String title) {
            super(title);
        }

        public StyleEditorPart createEditorPart(TupleStyle style) {
            return new IntegerEditorPart(this, style);
        }
    }

    // =========================================================================

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
            style.setBackgroundRGB(value); // ??? transparency
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

    // =========================================================================

    public static final StyleProperty<?>[] TUPLE_PROPERTIES = {
        FOREGROUND,
        BACKGROUND,
        FONT,
        PADDING,
        TEXT_MARGIN,
        LINE_SPACING,
        CHILD_SPACING
    };
}
