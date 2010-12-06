package zen.bricks.properties;

import org.eclipse.swt.graphics.RGB;

import zen.bricks.TupleStyle;

public class BackgroundProperty extends ColorProperty
{
    public BackgroundProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public RGB get(TupleStyle style) {
        return style.getBackgroundRGB();
    }

    public void set(TupleStyle style, RGB value) {
        style.setBackgroundRGB(value);
    }
}
