package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class SpacingEditorPart extends CheckedEditorPart<Integer>
{
    private final Integer spacing;
    private LabelSpinnerPair pair;

    public SpacingEditorPart(StyleProperty<Integer> property, TupleStyle style) {
        super(property, style);
        spacing = property.get(style);
    }

    protected void enabledCheckSelected(boolean selected) {
        pair.setEnabled(selected);
    }

    void createWidgets(Composite parent, int numColumns) {
        createEnabledCheck(parent);

        final Composite panel = createValuesPanel(parent, numColumns - 1);

        pair = new LabelSpinnerPair(panel, "Value:");
        if (spacing != null) {
            pair.setSelection(spacing);
            setEnabled(true);
        } else {
            pair.setEnabled(false);
        }
    }

    protected Integer getValue() {
        return isEnabled() ? pair.getSelection() : null;
    }
}
