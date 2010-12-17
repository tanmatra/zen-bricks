package zen.bricks.styleeditor.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;

public abstract class CheckedEditorPart<T> extends StyleEditorPart<T>
{
    Button definedCheck;

    public CheckedEditorPart(StyleProperty<T> property, TupleStyle style) {
        super(style, property);
    }

    protected int getNumColumns() {
        return 2;
    }

    protected void createDefinedCheck(Composite parent) {
        definedCheck = new Button(parent, SWT.CHECK);
        definedCheck.setText(property.getTitle());
        if (isMandatory()) {
            definedCheck.setSelection(true);
            definedCheck.setGrayed(true);
            definedCheck.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    event.doit = false;
                    definedCheck.setSelection(true);
                }
            });
        } else {
            definedCheck.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    definedCheckChanged(isDefined());
                }
            });
        }
    }

    protected Composite createValuesPanel(Composite parent, int span) {
        final Composite panel = new Composite(parent, SWT.NONE);
        final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = rowLayout.marginTop =
                rowLayout.marginRight = rowLayout.marginBottom = 0;
        rowLayout.center = true;
        panel.setLayout(rowLayout);
        gridData(span - 1).applyTo(panel);
        return panel;
    }

    protected Composite createValuesPanel(Composite parent, int span,
            int columns)
    {
        final Composite panel = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(columns, false);
        layout.marginWidth = layout.marginHeight = 0;
        panel.setLayout(layout);
        gridData(span - 1).applyTo(panel);
        return panel;
    }

    protected abstract void definedCheckChanged(boolean defined);

    public boolean isDefined() {
        return isMandatory() || definedCheck.getSelection();
    }

    protected void setDefined(boolean defined) {
        if (!isMandatory()) {
            definedCheck.setSelection(defined);
        }
    }
}
