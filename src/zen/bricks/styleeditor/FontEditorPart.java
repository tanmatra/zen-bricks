package zen.bricks.styleeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
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

    protected void createWidgets(final Composite parent, int columns) {
        createEnabledCheck(parent);

        final Composite panel = createValuesPanel(parent, columns - 1);

        previewText = new Text(panel, SWT.BORDER | SWT.READ_ONLY);
        previewText.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                disposePreviewFont();
            }
        });
        previewText.setLayoutData(new RowData(150, 20));
        showPreview();

        fontSelectButton = new Button(panel, SWT.PUSH);
        fontSelectButton.setText("Select...");
        fontSelectButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final FontDialog fontDialog = new FontDialog(panel.getShell());
                fontDialog.setFontList(fontList);
                if (fontDialog.open() != null) {
                    fontList = fontDialog.getFontList();
                    showPreview();
                }
            }
        });

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
