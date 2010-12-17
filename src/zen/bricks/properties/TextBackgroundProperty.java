package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;

import zen.bricks.ColorUtil;
import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.TransparentColorEditorPart;

public class TextBackgroundProperty extends StyleProperty<TransparentColor>
{
    public TextBackgroundProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<TransparentColor> newEditorPart(
            TupleStyle style)
    {
        return new TransparentColorEditorPart(this, style);
    }

    public TransparentColor get(TupleStyle style) {
        return style.getTextBackground();
    }

    public void set(TupleStyle style, TransparentColor value) {
        style.setTextBackground(value);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String string = preferences.get(key, null);
        final TransparentColor transparentColor;
        if (string == null) {
            transparentColor = null;
        } else if ("transparent".equals(string)) {
            transparentColor = new TransparentColor();
        } else {
            final Device device = style.getUI().getDevice();
            final RGB rgb = ColorUtil.parse(device, string);
            transparentColor = new TransparentColor(new Color(device, rgb));
        }
        set(style, transparentColor);
    }
}
