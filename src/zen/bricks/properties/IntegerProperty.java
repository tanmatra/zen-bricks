package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.swt.LabelSpinnerPair;

public abstract class IntegerProperty extends StyleProperty<Integer>
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

    public StyleEditorPart<Integer> createEditorPart(TupleStyle style) {
        final IntegerEditorPart part = new IntegerEditorPart(this, style);
        if (minimum != null) {
            part.setMinimum(minimum);
        }
        return part;
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String string = read(preferences);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(style, value);
    }

    public void save(TupleStyle object, Preferences preferences) {
        final Integer value = get(object);
        if (value == null) {
            write(preferences, null);
        } else {
            write(preferences, value);
        }
    }

    // ========================================================== Nested Classes

    private static class IntegerEditorPart extends CheckedEditorPart<Integer>
    {
        private LabelSpinnerPair pair;
        private Integer minimum;

        IntegerEditorPart(StyleProperty<Integer> property, TupleStyle style) {
            super(property, style);
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
