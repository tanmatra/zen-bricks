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

    public String getKey() {
        return key;
    }
}
