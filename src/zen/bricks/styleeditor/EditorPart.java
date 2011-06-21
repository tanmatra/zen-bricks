package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.Property;

/**
 * @param <T> type of edited object
 * @param <V> type of edited property value
 */
public abstract class EditorPart<T, V>
{
    // ================================================================== Fields

    protected final T editedObject;

    protected final Property<T, V> property;

    private boolean mandatory;

    // ============================================================ Constructors

    protected EditorPart(T object, Property<T, V> property) {
        this.editedObject = object;
        this.property = property;
    }

    // ================================================================= Methods

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

    protected abstract int getNumColumns();

    public abstract void createWidgets(Composite parent, int numColumns);

    protected static GridDataFactory gridData() {
        return GridDataFactory.swtDefaults();
    }

    protected static GridDataFactory gridData(int span) {
        return gridData().span(span, 1);
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
