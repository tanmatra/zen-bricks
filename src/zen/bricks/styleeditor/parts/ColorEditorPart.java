package zen.bricks.styleeditor.parts;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;

public class ColorEditorPart extends CheckedEditorPart<RGB>
{
    private ColorSelector colorSelector;

    public ColorEditorPart(StyleProperty<RGB> property, TupleStyle style) {
        super(property, style);
    }

    public void createWidgets(Composite parent, int columns) {
        createDefinedCheck(parent);

        final Composite panel = createValuesPanel(parent, columns - 1);

        colorSelector = new ColorSelector(panel);

        final boolean defined = property.isDefined(style);
        setDefined(defined);
        if (defined) {
            colorSelector.setColorValue(property.get(style));
        }
        definedCheckChanged(isDefined());
    }

    @Override
    protected void definedCheckChanged(boolean defined) {
        colorSelector.setEnabled(defined);
    }

    public RGB getValue() {
        return isDefined() ? colorSelector.getColorValue() : null;
    }
}
