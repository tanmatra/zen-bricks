package zen.bricks.styleeditor;

import org.eclipse.swt.widgets.Composite;

import zen.bricks.Margin;

public class MarginEditorPart extends CheckedEditorPart
{
    private final Margin margin;
    private LabelSpinnerPair leftValue;
    private LabelSpinnerPair topValue;
    private LabelSpinnerPair rightValue;
    private LabelSpinnerPair bottomValue;

    public MarginEditorPart(String title, Margin margin) {
        super(title);
        this.margin = margin;
    }

    protected void enabledCheckSelected(boolean selected) {
        leftValue.setEnabled(selected);
        topValue.setEnabled(selected);
        rightValue.setEnabled(selected);
        bottomValue.setEnabled(selected);
    }

    void createWidgets(Composite parent, int numColumns) {
        createEnabledCheck(parent);

        final Composite panel = createValuesPanel(parent, numColumns);

        leftValue = new LabelSpinnerPair(panel, "Left:");
        topValue = new LabelSpinnerPair(panel, "Top:");
        rightValue = new LabelSpinnerPair(panel, "Right:");
        bottomValue = new LabelSpinnerPair(panel, "Bottom:");
        if (margin != null) {
            leftValue.setSelection(margin.getLeft());
            topValue.setSelection(margin.getTop());
            rightValue.setSelection(margin.getRight());
            bottomValue.setSelection(margin.getBottom());
        }

        setEnabled(margin != null);
        enabledCheckSelected(margin != null);
    }

    Margin getMargin() {
        return new Margin(
            leftValue.getSelection(),
            topValue.getSelection(),
            rightValue.getSelection(),
            bottomValue.getSelection());
    }

    void apply() {
    }

    void cancel() {
    }
}
