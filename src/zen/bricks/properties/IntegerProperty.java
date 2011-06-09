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

    public StyleEditorPart<Integer> createEditorPart(TupleStyle style) {
        final IntegerEditorPart part = new IntegerEditorPart(this, style);
        if (minimum != null) {
            part.setMinimum(minimum);
        }
        return part;
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String string = read(preferences);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(style, value);
    }

    public void save(TupleStyle object, Preferences preferences) {
        final Integer value = get(object);
        if (value == null) {
            write(preferences, null);
        } else {
            write(preferences, value);
        }
    }
}
