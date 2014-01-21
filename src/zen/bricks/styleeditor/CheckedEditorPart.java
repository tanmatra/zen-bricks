package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import zen.bricks.Property;

public abstract class CheckedEditorPart<T, V> extends EditorPart<T, V>
{
    protected Button definedCheckbox;

    public CheckedEditorPart(T object, Property<T, V> property) {
        super(object, property);
    }

    @Override
    protected int getNumColumns() {
        return 2;
    }

    protected void createDefinedCheck(Composite parent) {
        definedCheckbox = new Button(parent, SWT.CHECK);
        definedCheckbox.setText(property.getTitle());
        if (isMandatory()) {
            definedCheckbox.setSelection(true);
            definedCheckbox.setGrayed(true);
            definedCheckbox.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    event.doit = false;
                    definedCheckbox.setSelection(true);
                }
            });
        } else {
            definedCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    definedCheckChanged(isDefined());
                }
            });
        }
    }

    protected Composite createValuesPanel(Composite parent, int span) {
        final Composite panel = new Composite(parent, SWT.NONE);
        final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = rowLayout.marginTop = rowLayout.marginRight = rowLayout.marginBottom = 0;
        rowLayout.center = true;
        panel.setLayout(rowLayout);
        gridData(span - 1).applyTo(panel);
        return panel;
    }

    protected Composite createValuesPanel(Composite parent, int span, int columns) {
        final Composite panel = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(columns, false);
        layout.marginWidth = layout.marginHeight = 0;
        panel.setLayout(layout);
        gridData(span - 1).applyTo(panel);
        return panel;
    }

    protected abstract void definedCheckChanged(boolean defined);

    public boolean isDefined() {
        return isMandatory() || definedCheckbox.getSelection();
    }

    protected void setDefined(boolean defined) {
        if (!isMandatory()) {
            definedCheckbox.setSelection(defined);
        }
    }
}
