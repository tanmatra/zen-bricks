package zen.bricks;

import zen.bricks.styleeditor.StyleEditorPart;

public abstract class StyleProperty<V> extends Property<TupleStyle, V>
{
    // ============================================================ Constructors

    public StyleProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public abstract StyleEditorPart<V> createEditorPart(TupleStyle style);
}
