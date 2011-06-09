package zen.bricks.properties;

import java.util.prefs.Preferences;

import zen.bricks.Margin;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.MarginEditorPart;

public abstract class MarginProperty extends StyleProperty<Margin>
{
    public MarginProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public StyleEditorPart<Margin> createEditorPart(TupleStyle style) {
        return new MarginEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        set(style, Margin.parseMargin(read(preferences)));
    }

    public void save(TupleStyle object, Preferences preferences) {
        final Margin margin = get(object);
        write(preferences, (margin == null) ? null : margin.format());
    }
}
