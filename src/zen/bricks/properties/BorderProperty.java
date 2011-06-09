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
import org.eclipse.swt.widgets.Label;

import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.IStyleEditor;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.CheckedEditorPart;

public class BorderProperty extends StyleProperty<Border>
{
    // ============================================================ Constructors

    public BorderProperty(String title, String key) {
        super(title, key);
    }

    // ================================================================= Methods

    public Border get(TupleStyle style) {
        return style.getBorder();
    }

    public void set(TupleStyle style, Border value) {
        style.setBorder(value);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String type = read(preferences);
        if (type == null) {
            set(style, null);
        } else {
            final UI ui = style.getUI();
            for (final BorderFactory<?> factory : ui.getBorderFactories()) {
                if (factory.getName().equals(type)) {
                    final Preferences borderPrefs = node(preferences);
                    final Border border = factory.createBorder(ui, borderPrefs);
                    set(style, border);
                    return;
                }
            }
            throw new RuntimeException("Border type '" + type + "' not found");
        }
    }

    public void save(TupleStyle object, Preferences preferences) {
        final Border border = get(object);
        final Preferences borderPrefs = node(preferences);
        if (border == null) {
            try {
                write(preferences, null);
                borderPrefs.removeNode();
            } catch (BackingStoreException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        write(preferences, border.getFactory().getName());
        border.save(borderPrefs);
    }

    protected StyleEditorPart<Border> newEditorPart(
            final TupleStyle style)
    {
        return new BorderEditorPart(this, style);
    }

    // ========================================================== Nested Classes

    static class BorderEditorPart extends CheckedEditorPart<Border>
    {
        private Label label;

        private Combo combo;

        private IStyleEditor borderStyleEditor;

        private BorderFactory<?> selectedFactory;

        private final List<BorderFactory<?>> factories;

        private Composite editorPanel;

        BorderEditorPart(BorderProperty property, TupleStyle style) {
            super(property, style);
            factories = style.getUI().getBorderFactories();
        }

        public void apply() {
            if (borderStyleEditor != null) {
                borderStyleEditor.apply();
            } else {
                setEditedValue(null);
            }
        }

        public Border getValue() {
            return null; // will be never called
        }

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
                public void widgetSelected(SelectionEvent e) {
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

            selectedFactory =
                    (sourceBorder != null) ? sourceBorder.getFactory() : null;
            if (selectedFactory != null) {
                final int factoryIndex = factories.indexOf(selectedFactory);
                if (factoryIndex >= 0) {
                    combo.select(factoryIndex);
                }
            }

            definedCheckChanged(isDefined());
        }

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
            final BorderFactory<?> newFactory =
                    (index < 0) ? null : factories.get(index);
            if (newFactory == selectedFactory) {
                return;
            }
            destroyEditor();
            selectedFactory = newFactory;
            makeEditor();
            layoutEditorPanel();
        }

        private void layoutEditorPanel() {
            editorPanel.getParent().layout(true, true);
        }

        private void makeEditor() {
            if (selectedFactory != null) {
                final BorderFactory<Border> bf =
                        (BorderFactory<Border>) selectedFactory;
                borderStyleEditor =
                        bf.createStyleEditor(getEditedObject(), getProperty());
                borderStyleEditor.createControl(editorPanel);
            } else {
                borderStyleEditor = null;
            }
        }

        private void destroyEditor() {
            if (borderStyleEditor != null) {
                borderStyleEditor.cancel();
                borderStyleEditor.getControl().dispose();
                borderStyleEditor = null;
            }
        }
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
