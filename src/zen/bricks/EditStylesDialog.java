package zen.bricks;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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
        final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setSashWidth(10);
        
        //================================
        final Composite listPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(5, 0, 5, 5)
            .applyTo(listPanel);

        final Label stylesLabel = new Label(listPanel, SWT.NONE);
        stylesLabel.setText("Styles:");

        final TableViewer tableViewer = new TableViewer(listPanel, 
            SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        GridDataFactory.fillDefaults().grab(true, true)
            .applyTo(tableViewer.getTable());
        tableViewer.setLabelProvider(new StyleLabelProvider());
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setInput(ui.getTextStyles());

        //================================
        final Composite propertiesPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 5, 5, 5)
            .applyTo(propertiesPanel);
        
        final Label propsLabel = new Label(propertiesPanel, SWT.NONE);
        propsLabel.setText("Properties:");
        GridDataFactory.fillDefaults().applyTo(propsLabel);
        
        final Composite scrollPanel = new Composite(propertiesPanel, 
            SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(600, 400).grab(true, true)
            .applyTo(scrollPanel);
        
        //================================
        sashForm.setWeights(new int[] { 20, 80 });

        return sashForm;
    }
}
