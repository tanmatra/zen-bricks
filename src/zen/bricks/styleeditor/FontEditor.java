package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Text;

class FontEditor extends StyleEditorPart
{
    Button fontCheck;
    Button fontSelectButton;
    FontData[] fontList;
    private final String title;
    private Text previewText;
    private Font previewFont;

    FontEditor(FontData[] fontList, String title) {
        this.fontList = fontList;
        this.title = title;
    }

    int getNumColumns() {
        return 3;
    }

    void createWidgets(final Composite parent, int numColumns) {
        fontCheck = new Button(parent, SWT.CHECK);
        fontCheck.setText(title);
        fontCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final boolean selected = fontCheck.getSelection();
                fontSelectButton.setEnabled(selected);
                if (!selected) {
                    fontList = null;
                    showPreview();
                }
            }
        });
        gridData(numColumns - 2).applyTo(fontCheck);

        previewText = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
        previewText.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                disposePreviewFont();
            }
        });
        showPreview();
        gridData().hint(150, SWT.DEFAULT).applyTo(previewText);

        fontSelectButton = new Button(parent, SWT.PUSH);
        fontSelectButton.setText("Select...");
        fontSelectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final FontDialog fontDialog = new FontDialog(parent.getShell());
                fontDialog.setFontList(fontList);
                if (fontDialog.open() != null) {
                    fontList = fontDialog.getFontList();
                    showPreview();
                }
            }
        });
        gridData().applyTo(fontSelectButton);

        if (fontList != null) {
            fontCheck.setSelection(true);
        } else {
            fontSelectButton.setEnabled(false);
        }
    }

    void disposePreviewFont() {
        if (previewFont != null) {
            previewFont.dispose();
            previewFont = null;
        }
    }

    void showPreview() {
        disposePreviewFont();
        if (fontList != null) {
            previewFont = new Font(previewText.getDisplay(), fontList);
            previewText.setFont(previewFont);
            final FontData fd = fontList[0];
            previewText.setText(fd.getName() + " " + fd.getHeight() + "pt");
        } else {
            previewText.setFont(null);
            previewText.setText("");
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
