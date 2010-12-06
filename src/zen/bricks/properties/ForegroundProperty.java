package zen.bricks.properties;

import org.eclipse.swt.graphics.RGB;

import zen.bricks.TupleStyle;

public class ForegroundProperty extends ColorProperty
{
    public ForegroundProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public RGB get(TupleStyle style) {
        return style.getForegroundRGB();
    }

    public void set(TupleStyle style, RGB value) {
        style.setForegroundRGB(value);
    }
}
