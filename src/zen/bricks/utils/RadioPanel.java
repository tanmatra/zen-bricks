package zen.bricks.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class RadioPanel extends Composite
{
    private Button[] buttons;

    public RadioPanel(Composite parent) {
        super(parent, SWT.NONE);
        setLayout(new RowLayout());
    }

    public RadioPanel(Composite parent, String... labels) {
        this(parent);
        setLabels(labels);
    }

    public void setLabels(String[] labels) {
        buttons = new Button[labels.length];
        for (int i = 0; i < labels.length; i++) {
            final Button button = new Button(this, SWT.RADIO);
            button.setText(labels[i]);
            buttons[i] = button;
        }
    }

    public void setSelection(int selection) {
        if ((selection < 0) || (selection >= buttons.length)) {
            throw new IllegalArgumentException();
        }
        buttons[selection].setSelection(true);
    }

    public int getSelection() {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getSelection()) {
                return i;
            }
        }
        return -1;
    }

    public void setValues(Object[] values) {
        if (values.length != buttons.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setData(values[i]);
        }
    }

    public Object getSelectionValue() {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getSelection()) {
                return buttons[i].getData();
            }
        }
        return null;
    }

    public void setValueSelected(Object value) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getData() == value) {
                buttons[i].setSelection(true);
                return;
            }
        }
    }
}
