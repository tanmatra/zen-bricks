package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.RGB;

import zen.bricks.ColorUtil;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.ColorEditorPart;

public abstract class ColorProperty extends StyleProperty<RGB>
{
    public ColorProperty(String title, String key) {
        super(title, key);
    }

    protected StyleEditorPart<RGB> newEditorPart(
            TupleStyle style)
    {
        return new ColorEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String value = preferences.get(key, null);
        set(style, ColorUtil.parse(style.getDevice(), value));
    }

    public void save(TupleStyle object, Preferences preferences) {
        final RGB rgb = get(object);
        if (rgb == null) {
            preferences.remove(key);
        } else {
            preferences.put(key, ColorUtil.format(rgb));
        }
    }
}
