package zen.bricks.properties;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import zen.bricks.Border;
import zen.bricks.BorderFactory;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.CheckedEditorPart;

public class BorderProperty extends StyleProperty<Border>
{
    public BorderProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public Border get(TupleStyle style) {
        return style.getBorder();
    }

    public void set(TupleStyle style, Border value) {
        style.setBorder(value);
    }

    protected StyleEditorPart<Border> createEditorPart(
            final TupleStyle style, final UI ui)
    {
        return new CheckedEditorPart<Border>(this, style)
        {
            private Label label1;
            private Combo combo;
            private Label label2;
            private ColorSelector colorSelector;

            public Border getValue() {
                if (!isDefined()) {
                    return null;
                }
                final int index = combo.getSelectionIndex();
                if (index < 0) {
                    return null;
                }
                final Border border = ui.getBorderFactories().get(index)
                        .createBorder(ui);
                border.init(ui.properties);
                border.setColor(colorSelector.getColorValue());
                return border;
            }

            public void createWidgets(Composite parent, int columns) {
                final Border border = property.get(style);

                createDefinedCheck(parent);

                final Composite panel =
                        createValuesPanel(parent, columns - 1, 4);

                label1 = new Label(panel, SWT.NONE);
                label1.setText("Style:");

                combo = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
                final List<BorderFactory> factories = ui.getBorderFactories();
                for (final BorderFactory factory : factories) {
                    combo.add(factory.getTitle());
                }
                if (border != null) {
                    final int factoryIndex =
                            factories.indexOf(border.getFactory());
                    if (factoryIndex >= 0) {
                        combo.select(factoryIndex);
                    }
                }

                label2 = new Label(panel, SWT.NONE);
                label2.setText("Color:");
                GridDataFactory.swtDefaults().indent(10, 0).applyTo(label2);

                colorSelector = new ColorSelector(panel);
                if (border != null) {
                    colorSelector.setColorValue(border.getColor());
                }

                setDefined(border != null);
                definedCheckChanged(isDefined());
            }

            protected void definedCheckChanged(boolean defined) {
                label1.setEnabled(defined);
                combo.setEnabled(defined);
                label2.setEnabled(defined);
                colorSelector.setEnabled(defined);
            }
        };
    }

    public void parse(UI ui, TupleStyle style, Properties properties,
                        String keyPrefix)
    {
        // TODO Auto-generated method stub
    }
}
