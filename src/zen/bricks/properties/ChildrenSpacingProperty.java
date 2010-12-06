package zen.bricks.properties;

import zen.bricks.TupleStyle;

public class ChildrenSpacingProperty extends IntegerProperty
{
    public ChildrenSpacingProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public Integer get(TupleStyle style) {
        return style.getSpacing();
    }

    public void set(TupleStyle style, Integer value) {
        style.setSpacing(value);
    }
}
