package zen.bricks.styleeditor;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class PropertiesListEditor extends MultipartEditor
{
    protected final StyleProperty<?>[] properties;

    protected final TupleStyle style;

    public PropertiesListEditor(TupleStyle style, StyleProperty<?>[] properties)
    {
        this.properties = properties;
        this.style = style;
    }

    protected StyleEditorPart<?>[] createParts() {
        final int count = properties.length;
        final StyleEditorPart<?>[] parts = new StyleEditorPart<?>[count];
        for (int i = 0; i < count; i++) {
            parts[i] = properties[i].createEditorPart(style);
        }
        return parts;
    }
}
