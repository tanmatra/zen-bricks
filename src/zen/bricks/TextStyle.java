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

    private final Device device;
    private Font font;
    private FontData fontData;
    private FontMetrics fontMetrics;
    private int textAscent;
    private GC savedGC;
    private Color backgroundColor;
    private Color foregroundColor;

    public TextStyle(Device device, Properties properties, String keyPrefix) {
        this.device = device;
        savedGC = new GC(device);
        try {
            fontData = parseFontData(properties, keyPrefix + ".font");
            createFont(fontData);
            backgroundColor = ColorUtil.parse(
                    device, properties, keyPrefix, ".background");
            foregroundColor = ColorUtil.parse(
                    device, properties, keyPrefix, ".color");
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

    private void createFont(FontData data) {
        font = new Font(device, data);
        savedGC.setFont(font);
        fontMetrics = savedGC.getFontMetrics();
        textAscent = fontMetrics.getAscent() + fontMetrics.getLeading();
    }

    public void paintText(GC gc, int x, int y, String text) {
        gc.setFont(font);
        gc.setForeground(foregroundColor);
        int flags = TEXT_FLAGS;
        if (backgroundColor != null) {
            gc.setBackground(backgroundColor);
        } else {
            flags |= SWT.DRAW_TRANSPARENT;
        }
        gc.drawText(text, x, y, flags);
    }

    private static FontData parseFontData(Properties properties, String key) {
        final String value = properties.getProperty(key);
        String name;
        float height = 8.0f;
        int style = SWT.NORMAL;
        StringTokenizer tokenizer;
        if (value.charAt(0) == '"') {
            final int p = value.indexOf('"', 1);
            name = value.substring(1, p);
            tokenizer = new StringTokenizer(value.substring(p + 1));
        } else {
            tokenizer = new StringTokenizer(value);
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
        return savedGC.textExtent(text, TEXT_FLAGS);
    }

    public int getTextAscent() {
        return textAscent;
    }

    void changeFont(FontData data) {
        this.fontData = data;
        if (font != null) {
            font.dispose();
        }
        createFont(data);
    }
}
