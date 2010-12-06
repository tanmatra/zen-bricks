package zen.bricks.properties;

import java.util.Properties;

import zen.bricks.Margin;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.MarginEditorPart;

public abstract class MarginProperty extends StyleProperty<Margin>
{
    public MarginProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<Margin> createEditorPart(
            TupleStyle style, UI ui)
    {
        return new MarginEditorPart(this, style);
    }

    public void parse(UI ui, TupleStyle style,
                      Properties properties, String keyPrefix)
    {
        set(style, Margin.parseMargin(properties, keyPrefix + keySuffix));
    }
}
