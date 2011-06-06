package zen.bricks.properties;

import zen.bricks.TupleStyle;

public class LineSpacingProperty extends IntegerProperty
{
    public LineSpacingProperty(String title, String keySuffix) {
        super(title, keySuffix);
        setMinimum(-1);
    }

    public Integer get(TupleStyle style) {
        return style.getLineSpacing();
    }

    public void set(TupleStyle style, Integer value) {
        style.setLineSpacing(value);
    }
}
