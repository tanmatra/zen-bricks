package zen.bricks.styleeditor.parts;

import org.eclipse.jface.window.Window;
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

import zen.bricks.AdjustFontDialog;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class FontEditorPart extends CheckedEditorPart<FontData[]>
{
    Button fontSelectButton;
    FontData[] fontList;
    private Text previewText;
    private Font previewFont;
    private Button adjustButton;

    public FontEditorPart(StyleProperty<FontData[]> property, TupleStyle style) {
        super(property, style);
        fontList = property.get(style);
    }

    public void createWidgets(Composite parent, int columns) {
        createDefinedCheck(parent);

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

        adjustButton = new Button(panel, SWT.PUSH);
        adjustButton.setText("Adjust...");
        adjustButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                final AdjustFontDialog dialog =
                        new AdjustFontDialog(panel.getShell());
                dialog.setFontList(fontList);
                if (dialog.open() != Window.OK) {
                    return;
                }
                fontList = dialog.getFontList();
                showPreview();
            }
        });

        if (fontList != null) {
            setDefined(true);
        } else {
            fontSelectButton.setEnabled(false);
            adjustButton.setEnabled(false);
        }
    }

    protected void definedCheckChanged(boolean selected) {
        fontSelectButton.setEnabled(selected);
        adjustButton.setEnabled(selected);
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

    public FontData[] getValue() {
        return fontList;
    }
}
