package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import zen.bricks.ColorUtil;
import zen.bricks.Property;
import zen.bricks.styleeditor.CheckedEditorPart;
import zen.bricks.styleeditor.EditorPart;

public abstract class ColorProperty<T> extends Property<T, RGB>
{
    // ============================================================ Constructors

    public ColorProperty(String key, String title) {
        super(key, title);
    }

    // ================================================================= Methods

    public EditorPart<T, RGB> createEditorPart(T style) {
        return new ColorEditorPart<T>(style, this);
    }

    public void load(T style, Preferences preferences) {
        final String value = read(preferences);
        set(style, ColorUtil.parse(value));
    }

    public void save(T object, Preferences preferences) {
        final RGB rgb = get(object);
        write(preferences, (rgb == null) ? null : ColorUtil.format(rgb));
    }

    // ========================================================== Nested Classes

    private static class ColorEditorPart<T> extends CheckedEditorPart<T, RGB>
    {
        private ColorSelector colorSelector;

        public ColorEditorPart(T object, Property<T, RGB> property) {
            super(object, property);
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
