package zen.bricks;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

public class TextStyle
{
    // ============================================================ Class Fields

    static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    // ================================================================== Fields

    private final Device device;

    Font font;

    /**
     * Not null only if {@link #font} specified.
     */
    FontMetrics fontMetrics;

    /**
     * Not null only if {@link #font} specified.
     */
    GC savedGC;

    Color foregroundColor;

    /**
     * Not null if and only if is specified transparency or background color.
     */
    Boolean transparent;

    Color backgroundColor;

    private final String name;

    // ============================================================ Constructors

    public TextStyle(String name, Device device,
        Properties properties, String keyPrefix)
    {
        this.name = name;
        this.device = device;
        try {
            final String fontVal = properties.getProperty(keyPrefix + ".font");
            System.out.println("fontVal: " + fontVal);
            if (!"inherit".equals(fontVal)) {
                FontData fontData = parseFontData(fontVal);
                createFont(fontData);
            }

            foregroundColor = ColorUtil.parse(
                    device, properties, keyPrefix, ".color");

            final String backVal =
                    properties.getProperty(keyPrefix + ".background");
            if ("none".equals(backVal) || "transparent".equals(backVal)) {
                transparent = true;
            } else {
                backgroundColor = ColorUtil.parse(device, backVal);
                transparent = false;
            }
        } catch (RuntimeException e) {
            dispose();
            throw e;
        }
    }

    // ================================================================= Methods

    public void dispose() {
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (foregroundColor != null) {
            foregroundColor.dispose();
            foregroundColor = null;
        }
        if (backgroundColor != null) {
            backgroundColor.dispose();
            backgroundColor = null;
        }
        if (savedGC != null) {
            savedGC.dispose();
            savedGC = null;
        }
    }

    public String getName() {
        return name;
    }

    private void createFont(FontData fontData) {
        if (fontData == null) {
            return;
        }
        font = new Font(device, fontData);
        savedGC = new GC(device);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
    }

    private static FontData parseFontData(Properties properties, String key) {
        final String value = properties.getProperty(key);
        return parseFontData(value);
    }

    private static FontData parseFontData(String str) {
        if (str == null) {
            return null;
        }
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
        String heightStr = tokenizer.nextToken();
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

    void changeFont(FontData fontData) {
        if (font != null) {
            font.dispose();
            fontMetrics = null;
            savedGC.dispose();
            savedGC = null;
        }
        createFont(fontData);
    }
}
