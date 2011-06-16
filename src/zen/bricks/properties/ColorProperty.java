package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.ColorUtil;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.StyleEditorPart;

public abstract class ColorProperty extends StyleProperty<RGB>
{
    // ============================================================ Constructors

    public ColorProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public StyleEditorPart<RGB> createEditorPart(TupleStyle style) {
        return new ColorEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String value = read(preferences);
        set(style, ColorUtil.parse(style.getDevice(), value));
    }

    public void save(TupleStyle object, Preferences preferences) {
        final RGB rgb = get(object);
        write(preferences, (rgb == null) ? null : ColorUtil.format(rgb));
    }

    // ========================================================== Nested Classes

    private static class ColorEditorPart extends CheckedEditorPart<RGB>
    {
        private ColorSelector colorSelector;

        public ColorEditorPart(StyleProperty<RGB> property, TupleStyle style) {
            super(property, style);
        }

        public void createWidgets(Composite parent, int columns) {
            createDefinedCheck(parent);

            final Composite panel = createValuesPanel(parent, columns - 1);

            colorSelector = new ColorSelector(panel);

            final boolean defined = isPropertyDefined();
            setDefined(defined);
            if (defined) {
                colorSelector.setColorValue(getEditedValue());
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
}
