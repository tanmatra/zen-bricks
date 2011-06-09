package zen.bricks.properties;

import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import zen.bricks.StyleProperty;
import zen.bricks.TupleStyle;
import zen.bricks.styleeditor.StyleEditorPart;
import zen.bricks.styleeditor.parts.FontEditorPart;

public abstract class FontProperty extends StyleProperty<FontData[]>
{
    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";

    public FontProperty(String title, String keySuffix) {
        super(title, keySuffix);
    }

    protected StyleEditorPart<FontData[]> newEditorPart(
            TupleStyle style)
    {
        return new FontEditorPart(this, style);
    }

    public void load(TupleStyle style, Preferences preferences) {
        final String value = read(preferences);
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
            if (BOLD.equals(token)) {
                style |= SWT.BOLD;
            } else if (ITALIC.equals(token)) {
                style |= SWT.ITALIC;
            }
        }
        final FontData data = new FontData(name, (int) height, style);
        if (Math.floor(height) != height) {
            data.height = height;
        }
        return data;
    }

    public void save(TupleStyle object, Preferences preferences) {
        final FontData[] fontList = get(object);
        if (fontList == null) {
            write(preferences, null);
            return;
        }
        final StringBuilder buf = new StringBuilder(40);
        final FontData fontData = fontList[0];
        buf.append('"').append(fontData.getName()).append('"').append(' ');
        buf.append(fontData.height);
        final int style = fontData.getStyle();
        if ((style & SWT.BOLD) != 0) {
            buf.append(' ').append(BOLD);
        }
        if ((style & SWT.ITALIC) != 0) {
            buf.append(' ').append(ITALIC);
        }
        write(preferences, buf.toString());
    }
}
