package zen.bricks.properties;

import zen.bricks.Margin;
import zen.bricks.TupleStyle;

public class PaddingProperty extends MarginProperty
{
    public PaddingProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public Margin get(TupleStyle style) {
        return style.getPadding();
    }

    public void set(TupleStyle style, Margin value) {
        style.setPadding(value);
    }
}
