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
    private static final String TRANSPARENT = "transparent";

    public TextBackgroundProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public StyleEditorPart<TransparentColor> createEditorPart(TupleStyle style)
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
        final String string = read(preferences);
        final TransparentColor transparentColor;
        if (string == null) {
            transparentColor = null;
        } else if (TRANSPARENT.equals(string)) {
            transparentColor = new TransparentColor();
        } else {
            final Device device = style.getUI().getDevice();
            final RGB rgb = ColorUtil.parse(device, string);
            transparentColor = new TransparentColor(new Color(device, rgb));
        }
        set(style, transparentColor);
    }

    public void save(TupleStyle object, Preferences preferences) {
        final TransparentColor transparentColor = get(object);
        if (transparentColor == null) {
            write(preferences, null);
            return;
        }
        final Color color = transparentColor.getColor();
        if (color == null) {
            write(preferences, TRANSPARENT);
        } else {
            write(preferences, ColorUtil.format(color));
        }
    }
}
