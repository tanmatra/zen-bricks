package zen.bricks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class EditStylesDialog extends Dialog
{
    static class StyleLabelProvider extends LabelProvider
    {
        public String getText(Object element) {
            return ((TupleStyle) element).getName();
        }
    }

    private static final String SAMPLE_TEXT =
            "Quick Brown Fox Jumps Over The Lazy Dog.";

    // ================================================================== Fields

    private final UI ui;

    private Composite stackPanel;

    private StackLayout stackLayout;

    private final Map<TupleStyle, ITextStyleEditor> editors =
        new HashMap<TupleStyle, ITextStyleEditor>();

    // ============================================================ Constructors

    protected EditStylesDialog(Shell shell, UI ui) {
        super(shell);
        this.ui = ui;
    }

    // ================================================================= Methods

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit styles");
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                cancelEditors();
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
        tableViewer.setInput(ui.getTupleStyles());
        tableViewer.addPostSelectionChangedListener(
            new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event) {
                final IStructuredSelection selection =
                    (IStructuredSelection) event.getSelection();
                styleSelected((TupleStyle) selection.getFirstElement());
            }
        });

        //================================
        final Composite propertiesPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 5, 5, 5)
            .applyTo(propertiesPanel);

        final Label propsLabel = new Label(propertiesPanel, SWT.NONE);
        propsLabel.setText("Properties:");
        GridDataFactory.fillDefaults().applyTo(propsLabel);

        stackPanel = new Composite(propertiesPanel,
            SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(600, 400).grab(true, true)
            .applyTo(stackPanel);
        stackLayout = new StackLayout();
        stackPanel.setLayout(stackLayout);

        //================================
        sashForm.setWeights(new int[] { 20, 80 });

        tableViewer.setSelection(new StructuredSelection(ui.getBasicStyle()));

        return sashForm;
    }

    void styleSelected(TupleStyle style) {
        ITextStyleEditor editor = editors.get(style);
        if (editor == null) {
            editor = style.getEditor();
            editors.put(style, editor);
            editor.createControl(stackPanel);
        }
        stackLayout.topControl = editor.getControl();
        stackPanel.layout();
    }

    @Override
    protected void okPressed() {
        for (ITextStyleEditor editor : editors.values()) {
            editor.apply();
        }
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        cancelEditors();
        super.cancelPressed();
    }

    void cancelEditors() {
        for (ITextStyleEditor editor : editors.values()) {
            editor.cancel();
        }
    }
}
