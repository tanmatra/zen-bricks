package zen.bricks;

import java.util.Properties;

public class Margin
{
    int left;
    int top;
    int right;
    int bottom;

    private static int parseInt(Properties props, String key) {
        final String value = props.getProperty(key);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public void parse(Properties props, String keyPrefix) {
        left   = parseInt(props, keyPrefix + ".left");
        top    = parseInt(props, keyPrefix + ".top");
        right  = parseInt(props, keyPrefix + ".right");
        bottom = parseInt(props, keyPrefix + ".bottom");
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }
}
