package zen.bricks.properties;

import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleLayout;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.StyleEditorPart;

public class LayoutProperty extends StyleProperty<TupleLayout>
{
    // ============================================================ Constructors

    public LayoutProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public TupleLayout get(TupleStyle style) {
        return style.getLayout();
    }

    public void set(TupleStyle style, TupleLayout value) {
        style.setLayout(value);
    }

    public StyleEditorPart<TupleLayout> createEditorPart(TupleStyle style) {
        return new LayoutEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String value = read(preferences);
        if (value == null) {
            set(style, null);
        } else {
            final List<TupleLayout> layouts = style.getUI().getTupleLayouts();
            for (TupleLayout layout : layouts) {
                if (layout.getName().equals(value)) {
                    set(style, layout);
                    return;
                }
            }
        }
    }

    public void save(TupleStyle object, Preferences preferences) {
        final TupleLayout tupleLayout = get(object);
        write(preferences,
                (tupleLayout == null) ? null : tupleLayout.getName());
    }

    // ========================================================== Nested Classes

    private static class LayoutEditorPart extends CheckedEditorPart<TupleLayout>
    {
        private Combo combo;

        LayoutEditorPart(StyleProperty<TupleLayout> property, TupleStyle style)
        {
            super(property, style);
        }

        private List<TupleLayout> getLayouts() {
            return getEditedObject().getUI().getTupleLayouts();
        }

        public void createWidgets(Composite parent, int columns) {
            createDefinedCheck(parent);
            final Composite panel = createValuesPanel(parent, columns - 1);

            combo = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
            final List<TupleLayout> layouts = getLayouts();
            for (TupleLayout tupleLayout : layouts) {
                combo.add(tupleLayout.getTitle());
            }

            final TupleLayout styleLayout = getEditedValue();
            if (styleLayout != null) {
                final int idx = layouts.indexOf(styleLayout);
                if (idx >= 0) {
                    combo.select(idx);
                }
            }

            combo.setEnabled(isDefined());
        }

        protected void definedCheckChanged(boolean defined) {
            combo.setEnabled(defined);
        }

        public TupleLayout getValue() {
            final int idx = combo.getSelectionIndex();
            if (idx < 0) {
                return null;
            } else {
                return getLayouts().get(idx);
            }
        }
    }
}
