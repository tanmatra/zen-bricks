package zen.bricks.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class LabelSpinnerPair
{
    private final Label label;
    private final Spinner spinner;

    public LabelSpinnerPair(Composite parent, String title) {
        label = new Label(parent, SWT.NONE);
        label.setText(title);
        spinner = new Spinner(parent, SWT.BORDER);
    }

    public void setMinimum(int minimum) {
        spinner.setMinimum(minimum);
    }

    public int getSelection() {
        return spinner.getSelection();
    }

    public void setSelection(int value) {
        spinner.setSelection(value);
    }

    public void setEnabled(boolean enabled) {
        label.setEnabled(enabled);
        spinner.setEnabled(enabled);
    }

    public Label getLabel() {
        return label;
    }

    public Spinner getSpinner() {
        return spinner;
    }
}
