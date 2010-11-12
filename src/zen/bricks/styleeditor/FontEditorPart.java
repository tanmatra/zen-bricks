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

class FontEditorPart extends CheckedEditorPart
{
    Button fontSelectButton;
    FontData[] fontList;
    private Text previewText;
    private Font previewFont;

    FontEditorPart(FontData[] fontList, String title) {
        super(title);
        this.fontList = fontList;
    }

    int getNumColumns() {
        return 3;
    }

    protected void createWidgets(final Composite parent, int numColumns) {
        createEnabledCheck(parent, numColumns - 2);

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
            setEnabled(true);
        } else {
            fontSelectButton.setEnabled(false);
        }
    }

    protected void enabledCheckSelected(boolean selected) {
        fontSelectButton.setEnabled(selected);
        if (!selected) {
            fontList = null;
            showPreview();
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
