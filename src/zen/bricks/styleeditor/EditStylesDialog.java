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
import org.eclipse.swt.custom.StackLayout;
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

public class EditStylesDialog extends Dialog
{
    // ========================================================== Nested Classes

    static class StyleLabelProvider extends LabelProvider
    {
        public String getText(Object element) {
            return ((Style) element).getName();
        }
    }

    static class StylesContentProvider implements ITreeContentProvider
    {
        public Object[] getElements(Object inputElement) {
            return (Object[]) inputElement;
        }

        public Object[] getChildren(Object parentElement) {
            final Style style = ((Style) parentElement);
            return style.getChildren().toArray();
        }

        public Object getParent(Object element) {
            final Style style = ((Style) element);
            return style.getParent();
        }

        public boolean hasChildren(Object element) {
            final Style style = ((Style) element);
            return style.getChildren().size() > 0;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
    }

    // ============================================================ Class Fields

    private static final int APPLY_ID = IDialogConstants.CLIENT_ID + 1;

    // ================================================================== Fields

    private final UI ui;

    private Composite stackPanel;

    private StackLayout stackLayout;

    private final Map<Style, IStyleEditor> styleEditors =
        new HashMap<Style, IStyleEditor>();

    private Style selectedStyle;

    private final MainWindow mainWindow;

    // ============================================================ Constructors

    public EditStylesDialog(MainWindow mainWindow) {
        super(mainWindow.getShell());
        this.mainWindow = mainWindow;
        this.ui = mainWindow.getUI();
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
        final Composite area = new Composite(parent, SWT.NONE);
        GridLayoutFactory.swtDefaults().applyTo(area);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final List<? extends Style> styles = ui.getAllStyles();

        final SashForm sashForm = new SashForm(area, SWT.HORIZONTAL);
        sashForm.setSashWidth(10);

        // ------------------------------------------------- styles table viewer
        final Composite listPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(listPanel);

        final Label stylesLabel = new Label(listPanel, SWT.NONE);
        stylesLabel.setText("Styles:");

        final TreeViewer stylesViewer = new TreeViewer(listPanel,
                SWT.BORDER | SWT.SINGLE);
        final Tree tree = stylesViewer.getTree();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tree);

        stylesViewer.setLabelProvider(new StyleLabelProvider());
        stylesViewer.setContentProvider(new StylesContentProvider());
        stylesViewer.setInput(styles.toArray());
        stylesViewer.expandAll();
        stylesViewer.addPostSelectionChangedListener(
                new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event) {
                final IStructuredSelection selection =
                        (IStructuredSelection) event.getSelection();
                styleSelected((Style) selection.getFirstElement());
            }
        });

        // ---------------------------------------------------- properties panel
        final Composite propertiesPanel = new Composite(sashForm, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(propertiesPanel);

        final Label propsLabel = new Label(propertiesPanel, SWT.NONE);
        propsLabel.setText("Properties:");
        GridDataFactory.fillDefaults().applyTo(propsLabel);

        stackPanel = new Composite(propertiesPanel, SWT.BORDER | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(600, 400).grab(true, true)
                .applyTo(stackPanel);
        stackLayout = new StackLayout();
        stackPanel.setLayout(stackLayout);

        // ---------------------------------------------------------------------
        sashForm.setWeights(new int[] { 20, 80 });

        if ((selectedStyle == null) && (!styles.isEmpty())) {
            selectedStyle = styles.get(0);
        }
        stylesViewer.setSelection(
                new StructuredSelection(selectedStyle), true);

        new Label(area, SWT.NONE)
                .setText("Theme: " + mainWindow.getThemeFileName());

        return area;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, APPLY_ID, "Apply", false);
        super.createButtonsForButtonBar(parent);
    }

    void styleSelected(Style style) {
        selectedStyle = style;
        if (style != null) {
            IStyleEditor styleEditor = styleEditors.get(style);
            if (styleEditor == null) {
                styleEditor = style.createEditor();
                styleEditors.put(style, styleEditor);
                styleEditor.createControl(stackPanel);
            }
            stackLayout.topControl = styleEditor.getControl();
        } else {
            stackLayout.topControl = null;
        }
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
        ui.fireChangedEvent();
    }

    void cancelEditors() {
        for (IStyleEditor styleEditor : styleEditors.values()) {
            styleEditor.cancel();
        }
    }

    public Style getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(Style selectedStyle) {
        this.selectedStyle = selectedStyle;
    }
}
