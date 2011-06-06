package zen.bricks.properties;

import java.util.prefs.Preferences;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.IntegerEditorPart;

public abstract class IntegerProperty extends StyleProperty<Integer>
{
    private Integer minimum;

    public IntegerProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    protected StyleEditorPart<Integer> newEditorPart(
            TupleStyle style)
    {
        final IntegerEditorPart part = new IntegerEditorPart(this, style);
        if (minimum != null) {
            part.setMinimum(minimum);
        }
        return part;
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
