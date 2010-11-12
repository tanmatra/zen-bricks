package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class CheckedEditorPart extends StyleEditorPart
{
    private Button enabledCheck;

    private final String title;

    public CheckedEditorPart(String title) {
        this.title = title;
    }

    protected void createEnabledCheck(final Composite parent, int span) {
        enabledCheck = new Button(parent, SWT.CHECK);
        enabledCheck.setText(title);
        enabledCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                enabledCheckSelected(isEnabled());
            }
        });
        gridData().span(span, 1).applyTo(enabledCheck);
    }

    protected abstract void enabledCheckSelected(boolean selected);

    protected boolean isEnabled() {
        return enabledCheck.getSelection();
    }

    protected void setEnabled(boolean enabled) {
        enabledCheck.setSelection(enabled);
    }
}
