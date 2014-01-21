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

    private FontData[] fontList;

    Font exampleFont;

    Spinner spinner;

    public AdjustFontDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Adjust font size");
        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (exampleFont != null) {
                    exampleFont.dispose();
                }
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite area = (Composite) super.createDialogArea(parent);
        final GridLayout layout = (GridLayout) area.getLayout();
        layout.numColumns = 2;
        GridData gd;

        Group group = new Group(area, SWT.NONE);
        final FillLayout groupLayout = new FillLayout();
        groupLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        groupLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        group.setLayout(groupLayout);
        group.setText("Text sample");
        gd = new GridData(400, 100);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        final Text exampleText = new Text(group, SWT.MULTI | SWT.BORDER);
        exampleText.setText(SAMPLE_TEXT);
        exampleFont = new Font(exampleText.getDisplay(), fontList);
        exampleText.setFont(exampleFont);

        final Label label = new Label(area, SWT.NONE);
        label.setText("Font size");

        spinner = new Spinner(area, SWT.BORDER);
        spinner.setDigits(1);
        spinner.setMinimum(70);
        spinner.setMaximum(360);
        spinner.setSelection((int) (fontList[0].height * SCALE));
        spinner.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                exampleFont = exampleText.getFont();
                final FontData[] fontList = exampleFont.getFontData();
                for (int i = 0; i < fontList.length; i++) {
                    final FontData data = fontList[i];
                    data.height = spinner.getSelection() / SCALE;
                }
                exampleFont.dispose();
                exampleFont = new Font(exampleText.getDisplay(), fontList);
                exampleText.setFont(exampleFont);
            }
        });
        spinner.setFocus();

        return area;
    }

    @Override
    protected void okPressed() {
        fontList = exampleFont.getFontData();
        super.okPressed();
    }

    public FontData[] getFontList() {
        return fontList;
    }

    public void setFontList(FontData[] fontList) {
        this.fontList = fontList;
    }
}
