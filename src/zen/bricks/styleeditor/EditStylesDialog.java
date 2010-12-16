package zen.bricks.styleeditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import zen.bricks.BrickStyle;
import zen.bricks.Editor;
import zen.bricks.UI;

public class EditStylesDialog extends Dialog
{
    // ========================================================== Nested Classes

    static class StyleLabelProvider extends LabelProvider
    {
        public String getText(Object element) {
            return ((BrickStyle) element).getName();
        }
    }

    // ============================================================ Class Fields

    private static final int APPLY_ID = IDialogConstants.CLIENT_ID + 1;

    // ================================================================== Fields

    private final UI ui;

    private Composite stackPanel;

    private StackLayout stackLayout;

    private final Map<BrickStyle, IStyleEditor> styleEditors =
        new HashMap<BrickStyle, IStyleEditor>();

    private final Editor editor;

    private BrickStyle selectedStyle;

    // ============================================================ Constructors

    public EditStylesDialog(Shell shell, Editor editor) {
        super(shell);
        this.ui = editor.getUI();
        this.editor = editor;
    }

    // ================================================================= Methods

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit styles");
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                cancelEditors();
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final List<? extends BrickStyle> brickStyles = ui.getBrickStyles();

        final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
        sashForm.setSashWidth(10);

        //------------------------------------------------- styles table viewer
        final Composite listPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(5, 0, 5, 5)
                .applyTo(listPanel);

        final Label stylesLabel = new Label(listPanel, SWT.NONE);
        stylesLabel.setText("Styles:");

        final TableViewer stylesViewer = new TableViewer(listPanel,
                SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        GridDataFactory.fillDefaults().grab(true, true)
                .applyTo(stylesViewer.getTable());
        stylesViewer.setLabelProvider(new StyleLabelProvider());
        stylesViewer.setContentProvider(ArrayContentProvider.getInstance());
        stylesViewer.setInput(brickStyles);
        stylesViewer.addPostSelectionChangedListener(
                new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event) {
                final IStructuredSelection selection =
                        (IStructuredSelection) event.getSelection();
                styleSelected((BrickStyle) selection.getFirstElement());
            }
        });

        //----------------------------------------------------- properties panel
        final Composite propertiesPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().extendedMargins(0, 5, 5, 5)
                .applyTo(propertiesPanel);

        final Label propsLabel = new Label(propertiesPanel, SWT.NONE);
        propsLabel.setText("Properties:");
        GridDataFactory.fillDefaults().applyTo(propsLabel);

        stackPanel = new Composite(propertiesPanel, SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(600, 400).grab(true, true)
                .applyTo(stackPanel);
        stackLayout = new StackLayout();
        stackPanel.setLayout(stackLayout);

        //----------------------------------------------------------------------
        sashForm.setWeights(new int[] { 20, 80 });

        if ((selectedStyle == null) && (brickStyles.size() > 0)) {
            selectedStyle = brickStyles.get(0);
        }
        stylesViewer.setSelection(
                new StructuredSelection(selectedStyle), true);

        return sashForm;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, APPLY_ID, "Apply", false);
        super.createButtonsForButtonBar(parent);
    }

    void styleSelected(BrickStyle style) {
        selectedStyle = style;
        IStyleEditor styleEditor = styleEditors.get(style);
        if (styleEditor == null) {
            styleEditor = style.createEditor();
            styleEditors.put(style, styleEditor);
            styleEditor.createControl(stackPanel);
        }
        stackLayout.topControl = styleEditor.getControl();
        stackPanel.layout();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == APPLY_ID) {
            apply();
            return;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void okPressed() {
        apply();
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        cancelEditors();
        super.cancelPressed();
    }

    private void apply() {
        for (IStyleEditor styleEditor : styleEditors.values()) {
            styleEditor.apply();
        }
        editor.refresh();
    }

    void cancelEditors() {
        for (IStyleEditor styleEditor : styleEditors.values()) {
            styleEditor.cancel();
        }
    }

    public BrickStyle getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(BrickStyle selectedStyle) {
        this.selectedStyle = selectedStyle;
    }
}
