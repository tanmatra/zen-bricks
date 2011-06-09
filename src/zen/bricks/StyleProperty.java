package zen.bricks;

import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<V> extends Property<TupleStyle, V>
{
    // ============================================================ Constructors

    public StyleProperty(String title, String key) {
        super(key, title);
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
}
