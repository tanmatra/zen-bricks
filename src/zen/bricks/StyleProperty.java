package zen.bricks;

import java.util.Properties;

import zen.bricks.styleeditor.StyleEditorPart;

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
        throw new Error("Style property \"" + title + "\" (" + keySuffix +
                ") not found in chain");
    }

    public abstract void parse(UI ui, TupleStyle style,
                               Properties properties, String keyPrefix);
}
