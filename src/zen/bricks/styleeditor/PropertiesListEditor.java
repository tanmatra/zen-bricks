package zen.bricks.styleeditor;

import java.util.List;

import zen.bricks.Property;

public class PropertiesListEditor<T> extends MultipartEditor
{
    protected final T object;

    protected final List<? extends Property<T, ?>> properties;

    private boolean mandatory;

    public PropertiesListEditor(T object,
            List<? extends Property<T, ?>> properties)
    {
        this.object = object;
        this.properties = properties;
    }

    public void setAllPropertiesMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    protected EditorPart<?, ?>[] createParts() {
        final int count = properties.size();
        final EditorPart<?, ?>[] parts = new EditorPart<?, ?>[count];
        for (int i = 0; i < count; i++) {
            final EditorPart<T, ?> part =
                    properties.get(i).createEditorPart(object);
            part.setMandatory(mandatory);
            parts[i] = part;
        }
        return parts;
    }
}
