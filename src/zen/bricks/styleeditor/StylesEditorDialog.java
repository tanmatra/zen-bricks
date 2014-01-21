package zen.bricks.styleeditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import zen.bricks.MainWindow;
import zen.bricks.Style;
import zen.bricks.UI;

public class StylesEditorDialog extends Dialog
{
    // ========================================================== Nested Classes

    static class StyleLabelProvider extends LabelProvider
    {
        @Override
        public String getText(Object element) {
            return ((Style) element).getName();
        }
    }

    static class StylesContentProvider implements ITreeContentProvider
    {
        @Override
        public Object[] getElements(Object inputElement) {
            return (Object[]) inputElement;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            final Style style = ((Style) parentElement);
            return style.getChildren().toArray();
        }

        @Override
        public Object getParent(Object element) {
            final Style style = ((Style) element);
            return style.getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            final Style style = ((Style) element);
            return style.getChildren().size() > 0;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    // ============================================================ Class Fields

    private static final int APPLY_ID = IDialogConstants.CLIENT_ID + 1;

    // ================================================================== Fields

    private final UI ui;

    private final Map<Style, Control> styleEditorControls = new HashMap<>();

    private Style selectedStyle;

    private final MainWindow mainWindow;

    private ScrolledComposite scrolledComposite;

    private Style initialStyle;

    private TreeViewer stylesTreeViewer;

    private final ControlAdapter controlResizeListener = new ControlAdapter() {
        @Override
        public void controlResized(ControlEvent ev) {
            final Control control = (Control) ev.widget;
            scrolledComposite.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    };

    // ============================================================ Constructors

    public StylesEditorDialog(MainWindow mainWindow) {
        super(mainWindow.getShell());
        this.mainWindow = mainWindow;
        this.ui = mainWindow.getUI();
    }

    // ================================================================= Methods

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit styles");
        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                cancelEditors();
            }
        });
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite area = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().applyTo(area);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final SashForm sashForm = new SashForm(area, SWT.HORIZONTAL);
        createStylesListPanel(sashForm);
        createPropertiesPanel(sashForm);
        sashForm.setSashWidth(10);
        sashForm.setWeights(new int[] { 20, 80 });
        GridDataFactory.fillDefaults().grab(true, true).applyTo(sashForm);

        if (initialStyle == null) {
            final List<? extends Style> styles = ui.getAllStyles();
            if (!styles.isEmpty()) {
                initialStyle = styles.get(0);
            }
        }
        if (initialStyle != null) {
            stylesTreeViewer.setSelection(new StructuredSelection(initialStyle), true);
        }

        new Label(area, SWT.NONE)
                .setText("Theme: " + mainWindow.getThemeFileName());

        return area;
    }

    private Composite createStylesListPanel(Composite parent) {
        final Composite listPanel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(listPanel);

        final Label stylesLabel = new Label(listPanel, SWT.NONE);
        stylesLabel.setText("Styles:");

        stylesTreeViewer = new TreeViewer(listPanel, SWT.BORDER | SWT.SINGLE);
        final Tree tree = stylesTreeViewer.getTree();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);

        stylesTreeViewer.setLabelProvider(new StyleLabelProvider());
        stylesTreeViewer.setContentProvider(new StylesContentProvider());
        stylesTreeViewer.setInput(ui.getAllStyles().toArray());
        stylesTreeViewer.expandAll();
        stylesTreeViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                onStyleSelected((Style) selection.getFirstElement());
            }
        });

        return listPanel;
    }

    private Composite createPropertiesPanel(Composite parent) {
        final Composite propertiesPanel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(propertiesPanel);

        final Label propsLabel = new Label(propertiesPanel, SWT.NONE);
        propsLabel.setText("Properties:");
        GridDataFactory.fillDefaults().applyTo(propsLabel);

        scrolledComposite = new ScrolledComposite(propertiesPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        GridDataFactory.fillDefaults().hint(600, 400).grab(true, true).applyTo(scrolledComposite);

        return propertiesPanel;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, APPLY_ID, "Apply", false);
        super.createButtonsForButtonBar(parent);
    }

    private void onStyleSelected(Style style) {
        if (style == selectedStyle) {
            return;
        }
        if (selectedStyle != null) {
            final Control control = styleEditorControls.get(style);
            if (control != null) {
                control.removeControlListener(controlResizeListener);
                control.setVisible(false);
            }
        }

        selectedStyle = style;
        if (style != null) {
            Control control = styleEditorControls.get(style);
            if (control == null) {
                final IStyleEditor editor = style.createEditor();
                control = editor.createControl(scrolledComposite);
                control.setData(editor);
                styleEditorControls.put(style, control);
            }
            scrolledComposite.setContent(control);
            scrolledComposite.setMinSize(control.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            control.setVisible(true);
            control.addControlListener(controlResizeListener);
        }
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

    private static IStyleEditor getEditor(Control editorControl) {
        return (IStyleEditor) editorControl.getData();
    }

    private void apply() {
        for (Control control : styleEditorControls.values()) {
            final IStyleEditor editor = getEditor(control);
            editor.apply();
        }
        ui.fireChangedEvent();
    }

    void cancelEditors() {
        for (Control control : styleEditorControls.values()) {
            final IStyleEditor editor = getEditor(control);
            editor.cancel();
        }
    }

    public Style getSelectedStyle() {
        return selectedStyle;
    }

    public void setInitialStyle(Style selectedStyle) {
        initialStyle = selectedStyle;
    }
}
