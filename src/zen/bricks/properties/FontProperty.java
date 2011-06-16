package zen.bricks.properties;

import java.util.StringTokenizer;
import java.util.prefs.Preferences;

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
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.StyleEditorPart;

public abstract class FontProperty extends StyleProperty<FontData[]>
{
    // ============================================================ Class Fields

    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";

    // ============================================================ Constructors

    public FontProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public StyleEditorPart<FontData[]> createEditorPart(TupleStyle style) {
        return new FontEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String value = read(preferences);
        final FontData[] list;
        if ((value == null) || "inherit".equals(value)) {
            list = null;
        } else {
            list = new FontData[] { parseFontData(value) };
        }
        set(style, list);
    }

    private static FontData parseFontData(String str) {
        String name;
        float height = 8.0f;
        int style = SWT.NORMAL;
        StringTokenizer tokenizer;
        if (str.charAt(0) == '"') {
            final int p = str.indexOf('"', 1);
            name = str.substring(1, p);
            tokenizer = new StringTokenizer(str.substring(p + 1));
        } else {
            tokenizer = new StringTokenizer(str);
            name = tokenizer.nextToken();
        }
        final String heightStr = tokenizer.nextToken();
        height = Float.parseFloat(heightStr);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (BOLD.equals(token)) {
                style |= SWT.BOLD;
            } else if (ITALIC.equals(token)) {
                style |= SWT.ITALIC;
            }
        }
        final FontData data = new FontData(name, (int) height, style);
        if (Math.floor(height) != height) {
            data.height = height;
        }
        return data;
    }

    public void save(TupleStyle object, Preferences preferences) {
        final FontData[] fontList = get(object);
        if (fontList == null) {
            write(preferences, null);
            return;
        }
        final StringBuilder buf = new StringBuilder(40);
        final FontData fontData = fontList[0];
        buf.append('"').append(fontData.getName()).append('"').append(' ');
        buf.append(fontData.height);
        final int style = fontData.getStyle();
        if ((style & SWT.BOLD) != 0) {
            buf.append(' ').append(BOLD);
        }
        if ((style & SWT.ITALIC) != 0) {
            buf.append(' ').append(ITALIC);
        }
        write(preferences, buf.toString());
    }

    // ========================================================== Nested Classes

    private static class FontEditorPart extends CheckedEditorPart<FontData[]>
    {
        Button fontSelectButton;
        FontData[] fontList;
        private Text previewText;
        private Font previewFont;
        private Button adjustButton;

        public FontEditorPart(StyleProperty<FontData[]> property,
                TupleStyle style)
        {
            super(property, style);
            fontList = getEditedValue(); // inline?
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
}
