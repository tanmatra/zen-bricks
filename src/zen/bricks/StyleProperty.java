package zen.bricks;

import java.util.prefs.Preferences;

import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<T>
{
    // ================================================================== Fields

    protected final String title;

    protected final String key;

    // ============================================================ Constructors

    public StyleProperty(String title, String key) {
        this.title = title;
        this.key = key;
    }

    // ================================================================= Methods

    public String getTitle() {
        return title;
    }

    public abstract T get(TupleStyle style);

    public abstract void set(TupleStyle style, T value);

    public StyleEditorPart<T> makeEditorPart(TupleStyle style) {
        final StyleEditorPart<T> part = createEditorPart(style);
        if (style.isTopLevel()) {
            part.setMandatory(true);
        }
        return part;
    }

    protected abstract StyleEditorPart<T> createEditorPart(
            TupleStyle style);

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
        throw new Error("Style property \"" + title + "\" (" + key +
                ") not found in chain");
    }

    public abstract void load(UI ui, TupleStyle style, Preferences preferences);
}
