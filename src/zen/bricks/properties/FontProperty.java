package zen.bricks.properties;

import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.UI;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.FontEditorPart;

public abstract class FontProperty extends StyleProperty<FontData[]>
{
    public FontProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<FontData[]> createEditorPart(
            TupleStyle style)
    {
        return new FontEditorPart(this, style);
    }

    public void load(UI ui, TupleStyle style, Preferences preferences) {
        final String value = preferences.get(key, null);
        final FontData[] list;
        if ((value == null) || "inherit".equals(value)) {
            list = null;
        } else {
            list = new FontData[] { parseFontData(value) };
        }
        set(style, list);
    }

    private static FontData parseFontData(String str) {
        String name;
        float height = 8.0f;
        int style = SWT.NORMAL;
        StringTokenizer tokenizer;
        if (str.charAt(0) == '"') {
            final int p = str.indexOf('"', 1);
            name = str.substring(1, p);
            tokenizer = new StringTokenizer(str.substring(p + 1));
        } else {
            tokenizer = new StringTokenizer(str);
            name = tokenizer.nextToken();
        }
        final String heightStr = tokenizer.nextToken();
        height = Float.parseFloat(heightStr);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if ("bold".equals(token)) {
                style |= SWT.BOLD;
            } else if ("italic".equals(token)) {
                style |= SWT.ITALIC;
            }
        }
        final FontData data = new FontData(name, (int) height, style);
        if (Math.floor(height) != height) {
            data.height = height;
        }
        return data;
    }
}
