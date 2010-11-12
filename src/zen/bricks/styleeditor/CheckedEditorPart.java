package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class CheckedEditorPart extends StyleEditorPart
{
    private Button enabledCheck;

    private final String title;

    public CheckedEditorPart(String title) {
        this.title = title;
    }

    protected int getNumColumns() {
        return 2;
    }

    protected void createEnabledCheck(Composite parent) {
        enabledCheck = new Button(parent, SWT.CHECK);
        enabledCheck.setText(title);
        enabledCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                enabledCheckSelected(isEnabled());
            }
        });
    }

    protected Composite createValuesPanel(Composite parent, int numColumns) {
        final Composite panel = new Composite(parent, SWT.NONE);
        final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = rowLayout.marginTop
            = rowLayout.marginRight = rowLayout.marginBottom = 0;
        rowLayout.center = true;
        panel.setLayout(rowLayout);
        gridData(numColumns - 1).applyTo(panel);
        return panel;
    }

    protected abstract void enabledCheckSelected(boolean selected);

    protected boolean isEnabled() {
        return enabledCheck.getSelection();
    }

    protected void setEnabled(boolean enabled) {
        enabledCheck.setSelection(enabled);
    }
}
