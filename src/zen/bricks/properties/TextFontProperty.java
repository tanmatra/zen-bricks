package zen.bricks.properties;

import org.eclipse.swt.graphics.FontData;

import zen.bricks.TupleStyle;

public class TextFontProperty extends FontProperty
{
    public TextFontProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public FontData[] get(TupleStyle style) {
        return style.getFontList();
    }

    public void set(TupleStyle style, FontData[] value) {
        style.setFont(value);
    }
}
