package zen.bricks.properties;

import zen.bricks.Margin;
import zen.bricks.TupleStyle;

public class TextMarginProperty extends MarginProperty
{
    public TextMarginProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public Margin get(TupleStyle style) {
        return style.getTextMargin();
    }

    public void set(TupleStyle style, Margin value) {
        style.setTextMargin(value);
    }
}
