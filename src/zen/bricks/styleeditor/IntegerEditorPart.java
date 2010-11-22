package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class IntegerEditorPart extends CheckedEditorPart<Integer>
{
    private final Integer value;
    private LabelSpinnerPair pair;

    public IntegerEditorPart(StyleProperty<Integer> property, TupleStyle style) {
        super(property, style);
        value = property.get(style);
    }

    protected void definedCheckChanged(boolean selected) {
        pair.setEnabled(selected);
    }

    void createWidgets(Composite parent, int numColumns) {
        createDefinedCheck(parent);

        final Composite panel = createValuesPanel(parent, numColumns - 1);

        pair = new LabelSpinnerPair(panel, "Value:");
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
