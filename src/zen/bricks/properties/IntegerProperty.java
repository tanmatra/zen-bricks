package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.Property;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.EditorPart;
import zen.bricks.swt.LabelSpinnerPair;

public abstract class IntegerProperty<T> extends Property<T, Integer>
{
    // ================================================================== Fields

    private Integer minimum;

    // ============================================================ Constructors

    public IntegerProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public EditorPart<T, Integer> createEditorPart(T object) {
        final IntegerEditorPart<T> part = new IntegerEditorPart<T>(object, this);
        if (minimum != null) {
            part.setMinimum(minimum);
        }
        return part;
    }

    public void load(T object, Preferences preferences) {
        final String string = read(preferences);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(object, value);
    }

    public void save(T object, Preferences preferences) {
        final Integer value = get(object);
        if (value == null) {
            write(preferences, null);
        } else {
            write(preferences, value);
        }
    }

    // ========================================================== Nested Classes

    private static class IntegerEditorPart<T> extends CheckedEditorPart<T, Integer>
    {
        private LabelSpinnerPair pair;
        private Integer minimum;

        IntegerEditorPart(T object, Property<T, Integer> property) {
            super(object, property);
        }

        public void setMinimum(Integer minimum) {
            this.minimum = minimum;
        }

        protected void definedCheckChanged(boolean selected) {
            pair.setEnabled(selected);
        }

        public void createWidgets(Composite parent, int numColumns) {
            createDefinedCheck(parent);

            final Composite panel = createValuesPanel(parent, numColumns - 1);

            pair = new LabelSpinnerPair(panel, "Value:");
            if (minimum != null) {
                pair.setMinimum(minimum);
            }
            final Integer value = getEditedValue();
            if (value != null) {
                pair.setSelection(value);
                setDefined(true);
            } else {
                pair.setEnabled(false);
            }
        }

        public Integer getValue() {
            return isDefined() ? pair.getSelection() : null;
        }
    }
}
