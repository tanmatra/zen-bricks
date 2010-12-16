package zen.bricks.properties;

import java.util.prefs.Preferences;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.IntegerEditorPart;

public abstract class IntegerProperty extends StyleProperty<Integer>
{
    public IntegerProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<Integer> createEditorPart(
            TupleStyle style)
    {
        return new IntegerEditorPart(this, style);
    }

    public void load(UI ui, TupleStyle style, Preferences preferences) {
        final String string = preferences.get(key, null);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(style, value);
    }
}
