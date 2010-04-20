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
import org.eclipse.swt.graphics.Point;

public class TextStyle
{
    private static final int TEXT_FLAGS = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;

    private TextStyle parent;
    private final Device device;
    private Font font;
    private FontMetrics fontMetrics;
    private GC savedGC;
    private Color foregroundColor;
    private boolean transparent;
    private Color backgroundColor;

    public TextStyle(TextStyle parent, Device device,
            Properties properties, String keyPrefix)
    {
        this.parent = parent;
        this.device = device;
        savedGC = new GC(device);
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
            if (backVal == null) {
                transparent = parent.transparent;
            } else if ("none".equals(backVal) || "transparent".equals(backVal)) {
                transparent = true;
            } else {
                backgroundColor = ColorUtil.parse(device, backVal);
            }
        } catch (RuntimeException e) {
            dispose();
            throw e;
        }
    }

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

    private void createFont(FontData fontData) {
        if (fontData == null) {
            return;
        }
        font = new Font(device, fontData);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
    }

    private Font getFont() {
        if (font != null) {
            return font;
        } else {
            return parent.getFont();
        }
    }

    private FontMetrics getFontMetrics() {
        if (font != null) {
            return fontMetrics;
        } else {
            return parent.getFontMetrics();
        }
    }

    private Color getForegroundColor() {
        if (foregroundColor != null) {
            return foregroundColor;
        } else {
            return parent.getForegroundColor();
        }
    }

    private Color getBackgroundColor() {
        if (backgroundColor != null) {
            return backgroundColor;
        } else {
            return parent.getBackgroundColor();
        }
    }

    public int getTextAscent() {
        final FontMetrics fm = getFontMetrics();
        return fm.getAscent() + fm.getLeading();
    }

    public void paintText(GC gc, int x, int y, String text) {
        gc.setFont(getFont());
        gc.setForeground(getForegroundColor());
        int flags = TEXT_FLAGS;
        if (transparent) {
            flags |= SWT.DRAW_TRANSPARENT;
        } else {
            gc.setBackground(getBackgroundColor());
        }
        gc.drawText(text, x, y, flags);
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

    public Point getTextExtent(String text) {
        if (font != null) {
            return savedGC.textExtent(text, TEXT_FLAGS);
        } else {
            return parent.getTextExtent(text);
        }
    }

    void changeFont(FontData fontData) {
        if (font != null) {
            font.dispose();
            fontMetrics = null;
        }
        createFont(fontData);
    }
}
