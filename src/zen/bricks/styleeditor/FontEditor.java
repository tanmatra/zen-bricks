package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;

class FontEditor extends StyleEditorPart
{
    private Button fontCheck;
    private Button fontSelectButton;
    FontData[] fontList;
    private final String title;
    private final Font font;

    FontEditor(final Font font, String title) {
        this.font = font;
        this.title = title;
        if (font != null) {
            fontList = font.getFontData();
        }
    }

    int getNumColumns() {
        return 2;
    }

    void createWidgets(final Composite parent, int numColumns) {
        fontCheck = new Button(parent, SWT.CHECK);
        fontCheck.setText(title);
        fontCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                fontSelectButton.setEnabled(fontCheck.getSelection());
            }
        });

        fontSelectButton = new Button(parent, SWT.PUSH);
        fontSelectButton.setText("Select...");
        fontSelectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final FontDialog fontDialog = new FontDialog(parent.getShell());
                fontDialog.setFontList(fontList);
                FontData newFontList = fontDialog.open();
                if (newFontList != null) {
                    fontList = fontDialog.getFontList();
                }
            }
        });

        if (font != null) {
            fontCheck.setSelection(true);
        } else {
            fontSelectButton.setEnabled(false);
        }
    }

    protected FontData[] getFontList() {
        return fontList;
    }

    void apply() {
    }

    void cancel() {
    }
}
