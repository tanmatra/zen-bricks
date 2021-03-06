package zen.bricks;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;

public class ColorUtil
{
    private static final int MULTIPLIER = 0x11;

    private static final Map<String, Integer> systemColors = new HashMap<>(35);

    static {
        systemColors.put("BLACK", SWT.COLOR_BLACK);
        systemColors.put("BLUE", SWT.COLOR_BLUE);
        systemColors.put("CYAN", SWT.COLOR_CYAN);
        systemColors.put("DARK_BLUE", SWT.COLOR_DARK_BLUE);
        systemColors.put("DARK_CYAN", SWT.COLOR_DARK_CYAN);
        systemColors.put("DARK_GRAY", SWT.COLOR_DARK_GRAY);
        systemColors.put("DARK_GREEN", SWT.COLOR_DARK_GREEN);
        systemColors.put("DARK_MAGENTA", SWT.COLOR_DARK_MAGENTA);
        systemColors.put("DARK_RED", SWT.COLOR_DARK_RED);
        systemColors.put("DARK_YELLOW", SWT.COLOR_DARK_YELLOW);
        systemColors.put("GRAY", SWT.COLOR_GRAY);
        systemColors.put("GREEN", SWT.COLOR_GREEN);
        systemColors.put("INFO_BACKGROUND", SWT.COLOR_INFO_BACKGROUND);
        systemColors.put("INFO_FOREGROUND", SWT.COLOR_INFO_FOREGROUND);
        systemColors.put("LIST_BACKGROUND", SWT.COLOR_LIST_BACKGROUND);
        systemColors.put("LIST_FOREGROUND", SWT.COLOR_LIST_FOREGROUND);
        systemColors.put("LIST_SELECTION", SWT.COLOR_LIST_SELECTION);
        systemColors.put("LIST_SELECTION_TEXT", SWT.COLOR_LIST_SELECTION_TEXT);
        systemColors.put("MAGENTA", SWT.COLOR_MAGENTA);
        systemColors.put("RED", SWT.COLOR_RED);
        systemColors.put("TITLE_BACKGROUND", SWT.COLOR_TITLE_BACKGROUND);
        systemColors.put("TITLE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
        systemColors.put("TITLE_FOREGROUND", SWT.COLOR_TITLE_FOREGROUND);
        systemColors.put("TITLE_INACTIVE_BACKGROUND", SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
        systemColors.put("TITLE_INACTIVE_BACKGROUND_GRADIENT", SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT);
        systemColors.put("TITLE_INACTIVE_FOREGROUND", SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
        systemColors.put("WHITE", SWT.COLOR_WHITE);
        systemColors.put("WIDGET_BACKGROUND", SWT.COLOR_WIDGET_BACKGROUND);
        systemColors.put("WIDGET_BORDER", SWT.COLOR_WIDGET_BORDER);
        systemColors.put("WIDGET_DARK_SHADOW", SWT.COLOR_WIDGET_DARK_SHADOW);
        systemColors.put("WIDGET_FOREGROUND", SWT.COLOR_WIDGET_FOREGROUND);
        systemColors.put("WIDGET_HIGHLIGHT_SHADOW", SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
        systemColors.put("WIDGET_LIGHT_SHADOW", SWT.COLOR_WIDGET_LIGHT_SHADOW);
        systemColors.put("WIDGET_NORMAL_SHADOW", SWT.COLOR_WIDGET_NORMAL_SHADOW);
        systemColors.put("YELLOW", SWT.COLOR_YELLOW);
    }

    private ColorUtil() {
    }

    public static Color parseColor(Device device, String str) {
        final RGB rgb = parse(device, str);
        return (rgb != null) ? new Color(device, rgb) : null;
    }

    public static RGB parse(Device device, String str) {
        if ((str == null) || str.isEmpty()) {
            return null;
        }
        str = str.trim();
        final Integer sysId = systemColors.get(str.toUpperCase());
        if (sysId != null) {
            return device.getSystemColor(sysId).getRGB();
        } else {
            return parse(str);
        }
    }

    public static RGB parse(String str) {
        if (str == null) {
            return null;
        }
        if (str.charAt(0) == '#') {
            final int r;
            final int g;
            final int b;
            if (str.length() == 7) {
                r = Integer.parseInt(str.substring(1, 3), 16);
                g = Integer.parseInt(str.substring(3, 5), 16);
                b = Integer.parseInt(str.substring(5, 7), 16);
            } else if (str.length() == 4) {
                r = Integer.parseInt(str.substring(1, 2), 16) * MULTIPLIER;
                g = Integer.parseInt(str.substring(2, 3), 16) * MULTIPLIER;
                b = Integer.parseInt(str.substring(3, 4), 16) * MULTIPLIER;
            } else {
                throw new IllegalArgumentException("Misformed color: " + str);
            }
            return new RGB(r, g, b);
        }
        throw new IllegalArgumentException("Misformed color: " + str);
    }

    public static String format(Color color) {
        return format(color.getRGB());
    }

    public static String format(RGB rgb) {
        return String.format("#%02X%02X%02X", rgb.red, rgb.green, rgb.blue);
    }
}
