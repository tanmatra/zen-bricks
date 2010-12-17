package zen.bricks.styleeditor;

import zen.bricks.Property;

public abstract class EditorPart<T, V>
{
    protected final T object;

    protected final Property<T, V> property;

    protected EditorPart(T object, Property<T, V> property) {
        this.object = object;
        this.property = property;
    }

    protected abstract V getValue();

    public void apply() {
        property.set(object, getValue());
    }

    public void cancel() {
        // do nothing by default
    }

    protected Property<T, V> getProperty() {
        return property;
    }

    protected boolean isPropertyDefined() {
        return property.isDefined(object);
    }

    protected T getObject() {
        return object;
    }

    protected void setObjectProperty(V value) {
        property.set(object, value);
    }

    protected V getObjectProperty() {
        return property.get(object);
    }
}
