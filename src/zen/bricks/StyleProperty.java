package zen.bricks;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.ColorEditorPart;
import zen.bricks.styleeditor.parts.FontEditorPart;
import zen.bricks.styleeditor.parts.IntegerEditorPart;
import zen.bricks.styleeditor.parts.MarginEditorPart;

public abstract class StyleProperty<T>
{
    // ================================================================== Fields

    protected final String title;

    protected final String keySuffix;

    // ============================================================ Constructors

    public StyleProperty(String title, String keySuffix) {
        this.title = title;
        this.keySuffix = keySuffix;
    }

    // ================================================================= Methods

    public String getTitle() {
        return title;
    }

    public abstract T get(TupleStyle style);

    public abstract void set(TupleStyle style, T value);

    public StyleEditorPart<T> makeEditorPart(TupleStyle style, UI ui) {
        final StyleEditorPart<T> part = createEditorPart(style, ui);
        if (style.isTopLevel()) {
            part.setMandatory(true);
        }
        return part;
    }

    protected abstract StyleEditorPart<T> createEditorPart(
            TupleStyle style, UI ui);

    public void apply(StyleEditorPart<T> editorPart, TupleStyle style) {
        set(style, editorPart.getValue());
    }

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
        throw new Error("Style property \"" + title +
                "\" (" + keySuffix +
                ") not found in chain");
    }

    public abstract void parse(UI ui, TupleStyle style,
                               Properties properties, String keyPrefix);

    // ========================================================== Nested Classes

    public static abstract class ColorProperty extends StyleProperty<RGB>
    {
        public ColorProperty(String title, String keySuffix) {
            super(title, keySuffix);
        }

        protected StyleEditorPart<RGB> createEditorPart(
                TupleStyle style, UI ui)
        {
            return new ColorEditorPart(this, style);
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            final String value = properties.getProperty(keyPrefix + keySuffix);
            set(style, ColorUtil.parse(style.getDevice(), value));
        }
    }

    public static abstract class FontProperty extends StyleProperty<FontData[]>
    {
        public FontProperty(String title, String keySuffix) {
            super(title, keySuffix);
        }

        protected StyleEditorPart<FontData[]> createEditorPart(
                TupleStyle style, UI ui)
        {
            return new FontEditorPart(this, style);
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            final String value = properties.getProperty(keyPrefix + ".font");
            final FontData[] list;
            if ((value == null) || "inherit".equals(value)) {
                list = null;
            } else {
                list = new FontData[] { FontProperty.parseFontData(value) };
            }
            set(style, list);
        }

        private static FontData parseFontData(String str) {
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
            final String heightStr = tokenizer.nextToken();
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
    }

    public static abstract class MarginProperty extends StyleProperty<Margin>
    {
        public MarginProperty(String title, String keySuffix) {
            super(title, keySuffix);
        }

        protected StyleEditorPart<Margin> createEditorPart(
                TupleStyle style, UI ui)
        {
            return new MarginEditorPart(this, style);
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            set(style, Margin.parseMargin(properties, keyPrefix + keySuffix));
        }
    }

    public static abstract class IntegerProperty extends StyleProperty<Integer>
    {
        public IntegerProperty(String title, String keySuffix) {
            super(title, keySuffix);
        }

        protected StyleEditorPart<Integer> createEditorPart(
                TupleStyle style, UI ui)
        {
            return new IntegerEditorPart(this, style);
        }

        public void parse(UI ui, TupleStyle style,
                          Properties properties, String keyPrefix)
        {
            final String string = properties.getProperty(keyPrefix + keySuffix);
            final Integer value = (string == null) ?
                    null : Integer.parseInt(string);
            set(style, value);
        }
    }
}
