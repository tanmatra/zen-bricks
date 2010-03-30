package zen.bricks;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class AdjustFontDialog extends Dialog
{
    private static final String SAMPLE_TEXT =
            "Quick Brown Fox Jumps Over The Lazy Dog.";
    private static final float SCALE = 10.0f;

    FontData fontData;
    Font font;
    private Spinner spinner;

    protected AdjustFontDialog(Shell shell) {
        super(shell);
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Adjust font size");
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (font != null) {
                    font.dispose();
                }
            }
        });
    }

    protected Control createDialogArea(Composite parent) {
        final Composite area = (Composite) super.createDialogArea(parent);
        final GridLayout layout = (GridLayout) area.getLayout();
        layout.numColumns = 2;
        GridData gd;

        Group group = new Group(area, SWT.NONE);
        final FillLayout groupLayout = new FillLayout();
        groupLayout.marginHeight =
            convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        groupLayout.marginWidth =
            convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        group.setLayout(groupLayout);
        group.setText("Text sample");
        gd = new GridData(400, 100);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        final Text exampleText = new Text(group, SWT.MULTI | SWT.BORDER);
        exampleText.setText(SAMPLE_TEXT);
        font = new Font(exampleText.getDisplay(), fontData);
        exampleText.setFont(font);

        final Label label = new Label(area, SWT.NONE);
        label.setText("Font size");

        spinner = new Spinner(area, SWT.BORDER);
        spinner.setDigits(1);
        spinner.setMinimum(70);
        spinner.setMaximum(360);
        spinner.setSelection((int) (fontData.height * SCALE));
        spinner.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                font = exampleText.getFont();
                final FontData[] fontDatas = font.getFontData();
                for (int i = 0; i < fontDatas.length; i++) {
                    final FontData data = fontDatas[i];
                    data.height = spinner.getSelection() / SCALE;
                }
                font.dispose();
                font = new Font(exampleText.getDisplay(), fontDatas);
                exampleText.setFont(font);
            }
        });
        spinner.setFocus();

        return area;
    }

    protected void okPressed() {
        fontData.height = spinner.getSelection() / SCALE;
        super.okPressed();
    }
}
