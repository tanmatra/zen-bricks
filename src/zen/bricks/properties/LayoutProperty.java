package zen.bricks.properties;

import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleLayout;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.CheckedEditorPart;

public class LayoutProperty extends StyleProperty<TupleLayout>
{
    public LayoutProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public TupleLayout get(TupleStyle style) {
        return style.getLayout();
    }

    public void set(TupleStyle style, TupleLayout value) {
        style.setLayout(value);
    }

    protected StyleEditorPart<TupleLayout> createEditorPart(
            final TupleStyle style, final UI ui)
    {
        return new CheckedEditorPart<TupleLayout>(this, style)
        {
            private Combo combo;

            public void createWidgets(Composite parent, int columns) {
                createDefinedCheck(parent);
                final Composite panel =
                        createValuesPanel(parent, columns - 1);

                combo = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
                final List<TupleLayout> layouts = ui.getTupleLayouts();
                for (TupleLayout tupleLayout : layouts) {
                    combo.add(tupleLayout.getTitle());
                }

                final TupleLayout styleLayout = style.getLayout();
                if (styleLayout != null) {
                    final int idx =
                            ui.getTupleLayouts().indexOf(styleLayout);
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
                    return ui.getTupleLayouts().get(idx);
                }
            }
        };
    }

    public void load(UI ui, TupleStyle style, Preferences preferences) {
        final String value = preferences.get(key, null);
        if (value == null) {
            set(style, null);
        } else {
            final List<TupleLayout> layouts = ui.getTupleLayouts();
            for (TupleLayout layout : layouts) {
                if (layout.getName().equals(value)) {
                    set(style, layout);
                    return;
                }
            }
        }
    }
}
