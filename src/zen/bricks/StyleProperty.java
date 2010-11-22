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

    public abstract StyleEditorPart<T> createEditorPart(TupleStyle style);

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
        throw new Error("Style property not found in chain");
    }

    public static abstract class ColorProperty extends StyleProperty<RGB>
    {
        public ColorProperty(String title) {
            super(title);
        }

        public StyleEditorPart<RGB> createEditorPart(TupleStyle style) {
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
}
