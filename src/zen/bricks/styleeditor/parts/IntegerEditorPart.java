package zen.bricks.styleeditor.parts;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.swt.LabelSpinnerPair;

public class IntegerEditorPart extends CheckedEditorPart<Integer>
{
    private final Integer value;
    private LabelSpinnerPair pair;
    private Integer minimum;

    public IntegerEditorPart(StyleProperty<Integer> property, TupleStyle style) {
        super(property, style);
        value = property.get(style);
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
