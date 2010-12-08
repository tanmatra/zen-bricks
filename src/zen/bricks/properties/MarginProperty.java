package zen.bricks.properties;

import java.util.prefs.Preferences;

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

    public void load(UI ui, TupleStyle style, Preferences preferences) {
        set(style, Margin.parseMargin(preferences.get(key, null)));
    }
}