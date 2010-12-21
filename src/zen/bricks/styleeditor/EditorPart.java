package zen.bricks.styleeditor;

import zen.bricks.Property;

public abstract class EditorPart<T, V>
{
    protected final T editedObject;

    protected final Property<T, V> property;

    protected EditorPart(T object, Property<T, V> property) {
        this.editedObject = object;
        this.property = property;
    }

    protected abstract V getValue();

    public void apply() {
        property.set(editedObject, getValue());
    }

    public void cancel() {
        // do nothing by default
    }

    protected Property<T, V> getProperty() {
        return property;
    }

    protected boolean isPropertyDefined() {
        return property.isDefined(editedObject);
    }

    protected T getEditedObject() {
        return editedObject;
    }

    protected void setEditedValue(V value) {
        property.set(editedObject, value);
    }

    protected V getEditedValue() {
        return property.get(editedObject);
    }
}
