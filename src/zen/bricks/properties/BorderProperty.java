package zen.bricks.properties;

import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.Property;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.EditorPart;
import zen.bricks.styleeditor.IStyleEditor;

public class BorderProperty extends Property<TupleStyle, Border>
{
    // ============================================================ Constructors

    public BorderProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    @Override
    public Border get(TupleStyle style) {
        return style.getBorder();
    }

    @Override
    public void set(TupleStyle style, Border value) {
        style.setBorder(value);
    }

    @Override
    public void load(TupleStyle style, Preferences preferences) {
        final String type = read(preferences);
        if (type == null) {
            set(style, null);
        } else {
            final UI ui = style.getUI();
            for (final BorderFactory<?> factory : ui.getBorderFactories()) {
                if (factory.getName().equals(type)) {
                    final Preferences borderPrefs = subNode(preferences);
                    final Border border = factory.createBorder(ui, borderPrefs);
                    set(style, border);
                    return;
                }
            }
            throw new RuntimeException("Border type '" + type + "' not found");
        }
    }

    @Override
    public void save(TupleStyle style, Preferences preferences) {
        final Border border = get(style);
        final Preferences borderPrefs = subNode(preferences);
        if (border == null) {
            try {
                write(preferences, null);
                borderPrefs.removeNode();
            } catch (BackingStoreException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            write(preferences, border.getFactory().getName());
            border.save(borderPrefs);
        }
    }

    @Override
    public EditorPart<TupleStyle, Border> createEditorPart(TupleStyle style) {
        return new BorderEditorPart(style, this);
    }

    // ========================================================== Nested Classes

    private static class BorderEditorPart extends CheckedEditorPart<TupleStyle, Border>
    {
        private Label label;
        private Combo combo;
        private IStyleEditor borderStyleEditor;
        private BorderFactory<? extends Border> selectedFactory;
        private final List<BorderFactory<?>> factories;
        private Composite editorPanel;
        private Control editorControl;

        BorderEditorPart(TupleStyle style, BorderProperty property) {
            super(style, property);
            factories = style.getUI().getBorderFactories();
        }

        @Override
        public void apply() {
            if (borderStyleEditor != null) {
                borderStyleEditor.apply();
            } else {
                setEditedValue(null);
            }
        }

        @Override
        public Border getValue() {
            return null; // will be never called
        }

        @Override
        public void createWidgets(Composite parent, int columns) {
            // ---------------------------------------------------------- row 1
            createDefinedCheck(parent);

            final Composite typePanel = createValuesPanel(parent, columns - 1, 2);

            label = new Label(typePanel, SWT.NONE);
            label.setText("Style:");

            combo = new Combo(typePanel, SWT.DROP_DOWN | SWT.READ_ONLY);
            for (final BorderFactory<?> factory : factories) {
                combo.add(factory.getTitle());
            }
            combo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    comboSelected();
                }
            });

            // ---------------------------------------------------------- row 2
            new Label(parent, SWT.NONE).setVisible(false);

            editorPanel = new Composite(parent, SWT.NONE);
            final FillLayout layout = new FillLayout(SWT.HORIZONTAL);
            editorPanel.setLayout(layout);
            GridDataFactory.fillDefaults().applyTo(editorPanel);

            // ----------------------------------------------------------- init
            final Border sourceBorder = getEditedValue();
            setDefined(sourceBorder != null);

            selectedFactory = (sourceBorder != null) ? sourceBorder.getFactory() : null;
            if (selectedFactory != null) {
                final int factoryIndex = factories.indexOf(selectedFactory);
                if (factoryIndex >= 0) {
                    combo.select(factoryIndex);
                }
            }

            definedCheckChanged(isDefined());
        }

        @Override
        protected void definedCheckChanged(boolean defined) {
            label.setEnabled(defined);
            combo.setEnabled(defined);

            if (defined) {
                makeEditor();
            } else {
                destroyEditor();
            }
            layoutEditorPanel();
        }

        void comboSelected() {
            final int index = combo.getSelectionIndex();
            final BorderFactory<?> newFactory = (index < 0) ? null : factories.get(index);
            if (newFactory == selectedFactory) {
                return;
            }
            destroyEditor();
            selectedFactory = newFactory;
            makeEditor();
            layoutEditorPanel();
        }

        private void layoutEditorPanel() {
            final Composite parent = editorPanel.getParent();
            // parent.layout(true, true);
            parent.setSize(editorPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }

        private void makeEditor() {
            if (selectedFactory != null) {
                final BorderFactory<Border> bf = (BorderFactory<Border>) selectedFactory;
                borderStyleEditor = bf.createStyleEditor(getEditedObject(), getProperty());
                editorControl = borderStyleEditor.createControl(editorPanel);
            } else {
                borderStyleEditor = null;
            }
        }

        private void destroyEditor() {
            if (borderStyleEditor != null) {
                borderStyleEditor.cancel();
                editorControl.dispose();
                editorControl = null;
                borderStyleEditor = null;
            }
        }
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
