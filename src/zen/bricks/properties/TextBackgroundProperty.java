package zen.bricks.properties;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.RGB;

import zen.bricks.ColorUtil;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.ColorEditorPart;

public class TextBackgroundProperty extends ColorProperty
{
    public TextBackgroundProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    public boolean isDefined(TupleStyle style) {
        return style.getTextBackground() != null;
    }

    protected StyleEditorPart<RGB> createEditorPart(TupleStyle style) {
        return new ColorEditorPart(this, style, style.getTextBackground());
    }

    @Override
    public void apply(StyleEditorPart<RGB> editorPart, TupleStyle style) {
        final ColorEditorPart colorEditorPart = (ColorEditorPart) editorPart;
        style.setTextBackgroundRGB(colorEditorPart.getBackground(),
                colorEditorPart.getValue());
    }

    public RGB get(TupleStyle style) {
        return style.getTextBackgroundRGB();
    }

    @Override
    public void set(TupleStyle style, RGB value) {
        // do nothing, as it never called
    }

    @Override
    public void load(UI ui, TupleStyle style, Preferences preferences) {
        final String string = preferences.get(key, null);
        final Boolean background;
        final RGB rgb;
        if (string == null) {
            background = null;
            rgb = null;
        } else if ("transparent".equals(string)) {
            background = false;
            rgb = null;
        } else {
            background = true;
            rgb = ColorUtil.parse(ui.getDevice(), string);
        }
        style.setTextBackgroundRGB(background, rgb);
    }
}
