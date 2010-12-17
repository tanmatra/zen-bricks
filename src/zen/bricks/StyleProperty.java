package zen.bricks;

import java.util.prefs.Preferences;

import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<T> extends Property<TupleStyle, T>
{
    // ================================================================== Fields

    protected final String key;

    // ============================================================ Constructors

    public StyleProperty(String title, String key) {
        super(title);
        this.key = key;
    }

    // ================================================================= Methods

    public StyleEditorPart<T> makeEditorPart(TupleStyle style) {
        final StyleEditorPart<T> part = createEditorPart(style);
        if (style.isTopLevel()) {
            part.setMandatory(true);
        }
        return part;
    }

    protected abstract StyleEditorPart<T> createEditorPart(
            TupleStyle style);

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
