package zen.bricks.styleeditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.Margin;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class MarginEditorPart extends CheckedEditorPart<Margin>
{
    private final Margin margin;
    private LabelSpinnerPair leftValue;
    private LabelSpinnerPair topValue;
    private LabelSpinnerPair rightValue;
    private LabelSpinnerPair bottomValue;

    public MarginEditorPart(StyleProperty<Margin> property, TupleStyle style) {
        super(property, style);
        this.margin = property.get(style);
    }

    protected void enabledCheckSelected(boolean selected) {
        leftValue.setEnabled(selected);
        topValue.setEnabled(selected);
        rightValue.setEnabled(selected);
        bottomValue.setEnabled(selected);
    }

    void createWidgets(Composite parent, int columns) {
        createEnabledCheck(parent);

        final Composite panel = createValuesPanel(parent, columns - 1, 8);

        leftValue = new LabelSpinnerPair(panel, "Left:");
        topValue = new LabelSpinnerPair(panel, "Top:");
        rightValue = new LabelSpinnerPair(panel, "Right:");
        bottomValue = new LabelSpinnerPair(panel, "Bottom:");

        final GridDataFactory indent =
            GridDataFactory.swtDefaults().indent(10, 0);
        indent.applyTo(topValue.getLabel());
        indent.applyTo(rightValue.getLabel());
        indent.applyTo(bottomValue.getLabel());

        if (margin != null) {
            leftValue.setSelection(margin.getLeft());
            topValue.setSelection(margin.getTop());
            rightValue.setSelection(margin.getRight());
            bottomValue.setSelection(margin.getBottom());
        }

        setEnabled(margin != null);
        enabledCheckSelected(margin != null);
    }

    public Margin getValue() {
        if (!isEnabled()) {
            return null;
        }
        return new Margin(
            leftValue.getSelection(),
            topValue.getSelection(),
            rightValue.getSelection(),
            bottomValue.getSelection());
    }
}
