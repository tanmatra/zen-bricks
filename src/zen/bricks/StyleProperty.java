package zen.bricks;

import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<V> extends Property<TupleStyle, V>
{
    // ================================================================== Fields

    protected final String key;

    // ============================================================ Constructors

    public StyleProperty(String title, String key) {
        super(title);
        this.key = key;
    }

    // ================================================================= Methods

    public StyleEditorPart<V> createEditorPart(TupleStyle style) {
        final StyleEditorPart<V> part = newEditorPart(style);
        if (style.isTopLevel()) {
            part.setMandatory(true);
        }
        return part;
    }

    protected abstract StyleEditorPart<V> newEditorPart(TupleStyle style);

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
}
