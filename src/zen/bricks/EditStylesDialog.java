package zen.bricks;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class EditStylesDialog extends Dialog
{
    static class StyleLabelProvider extends LabelProvider
    {
        public String getText(Object element) {
            return ((TextStyle) element).getName();
        }
    }

    private static final String SAMPLE_TEXT =
            "Quick Brown Fox Jumps Over The Lazy Dog.";

    private final UI ui;

    protected EditStylesDialog(Shell shell, UI ui) {
        super(shell);
        this.ui = ui;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit styles");
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                // todo
            }
        });
    }

    protected Control createDialogArea(Composite parent) {
        final Composite area = (Composite) super.createDialogArea(parent);
        final GridLayout layout = (GridLayout) area.getLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 15;
        GridData gd;
        Group group;

//        final Label label = new Label(area, SWT.NONE);
//        label.setText("Styles:");

        group = new Group(area, SWT.NONE);
        group.setText("Styles");
        final RowLayout rowLayout = new RowLayout();
        group.setLayout(rowLayout);

        final TableViewer tableViewer =
                new TableViewer(group, SWT.BORDER | SWT.SINGLE);
        tableViewer.getTable().setLayoutData(new RowData(150, 100));
        tableViewer.setLabelProvider(new StyleLabelProvider());
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setInput(ui.getTextStyles());

        group = new Group(area, SWT.NONE);
        group.setText("Style properties");
        gd = new GridData(300, 200);
        gd.verticalAlignment = SWT.TOP;
        gd.verticalSpan = 2;
        group.setLayoutData(gd);

        return area;
    }
}
