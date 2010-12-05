package zen.bricks;

import java.util.List;

public class Strings
{
    private Strings() { }

    public static String removeChar(String str, char c) {
        int i = str.indexOf(c);
        if (i < 0) {
            return str;
        }
        final StringBuilder res = new StringBuilder(str.length() - 1);
        res.append(str, 0, i);
        i++;
        while (true) {
            final int p = str.indexOf(c, i);
            if (p < 0) {
                res.append(str, i, str.length());
                break;
            } else {
                res.append(str, i, p);
                i = p + 1;
            }
        }
        return res.toString();
    }

    public static String join(List<String> list, String sep) {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (String str : list) {
            if (!first) {
                buf.append(sep);
            } else {
                first = false;
            }
            buf.append(str);
        }
        return buf.toString();
    }
}
