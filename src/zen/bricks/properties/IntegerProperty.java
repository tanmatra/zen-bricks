package zen.bricks.properties;

import java.util.prefs.Preferences;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.IntegerEditorPart;

public abstract class IntegerProperty extends StyleProperty<Integer>
{
    public IntegerProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<Integer> newEditorPart(
            TupleStyle style)
    {
        return new IntegerEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String string = preferences.get(key, null);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(style, value);
    }

    public void save(TupleStyle object, Preferences preferences) {
        final Integer value = get(object);
        if (value == null) {
            preferences.remove(key);
        } else {
            preferences.putInt(key, value);
        }
    }
}
