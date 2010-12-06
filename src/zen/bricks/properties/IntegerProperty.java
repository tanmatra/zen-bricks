package zen.bricks.properties;

import java.util.Properties;

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
            TupleStyle style, UI ui)
    {
        return new IntegerEditorPart(this, style);
    }

    public void parse(UI ui, TupleStyle style,
                      Properties properties, String keyPrefix)
    {
        final String string = properties.getProperty(keyPrefix + keySuffix);
        final Integer value = (string == null) ?
                null : Integer.parseInt(string);
        set(style, value);
    }
}
